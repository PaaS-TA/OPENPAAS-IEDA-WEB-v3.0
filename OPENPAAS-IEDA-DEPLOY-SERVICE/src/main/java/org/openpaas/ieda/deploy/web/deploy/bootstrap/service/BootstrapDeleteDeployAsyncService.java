package org.openpaas.ieda.deploy.web.deploy.bootstrap.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.Arrays;
import java.util.Locale;

import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.deploy.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.deploy.web.common.service.CommonDeployUtils;
import org.openpaas.ieda.deploy.web.config.setting.dao.DirectorConfigDAO;
import org.openpaas.ieda.deploy.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.dao.BootstrapDAO;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.dao.BootstrapVO;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.dto.BootStrapDeployDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class BootstrapDeleteDeployAsyncService{

    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private DirectorConfigDAO directorDao;
    @Autowired private BootstrapDAO bootstrapDao;
    @Autowired MessageSource message;
    
    final private static String SEPARATOR = System.getProperty("file.separator");
    final private static String DEPLOYMENT_DIR = LocalDirectoryConfiguration.getDeploymentDir() + SEPARATOR;
    final private static String CREDENTIAL_FILE = LocalDirectoryConfiguration.getGenerateCredentialDir() + SEPARATOR;
    final private static String LOCK_DIR=LocalDirectoryConfiguration.getLockDir();
    final private static String MESSAGE_ENDPOINT = "/deploy/bootstrap/delete/logs"; 
    private final static Logger LOGGER = LoggerFactory.getLogger(BootstrapDeleteDeployAsyncService.class);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : bosh를 실행하여 해당 플랫폼 삭제 요청
     * @title : deleteBootstrapDeploy
     * @return : void
    *****************************************************************/
    public void deleteBootstrapDeploy(BootStrapDeployDTO.Delete dto, Principal principal) {
        
        String accumulatedLog = "";
        BootstrapVO vo = bootstrapDao.selectBootstrapInfo(Integer.parseInt(dto.getId()));
        if( vo == null ){
            bootstrapDao.deleteBootstrapInfo(Integer.parseInt(dto.getId()));
            vo = new BootstrapVO();
        }
        String status = "";
        String resultMessage = "";
        BufferedReader bufferedReader = null;

        try {
            String deployStateFile = DEPLOYMENT_DIR +vo.getDeploymentFile().split(".yml")[0] + "-state.json";
            File stateFile = new File(deployStateFile);
            if ( !stateFile.exists() ) {
                status = "done";
                resultMessage = "MICRO BOSH 삭제가 완료되었습니다.";
                bootstrapDao.deleteBootstrapInfo(vo.getId());
                DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, status, Arrays.asList("MICRO BOSH를 삭제했습니다."));
                
            }else{
                String deployFile = DEPLOYMENT_DIR + vo.getDeploymentFile();
                File file = new File(deployFile);
                if( file.exists() ){
                    ProcessBuilder builder = new ProcessBuilder("bosh", "delete-env", deployFile, 
                                                                "--state="+deployStateFile, 
                                                                "--vars-store="+CREDENTIAL_FILE+vo.getDeploymentFile().split(".yml")[0]+"-creds.yml", "--tty");
                    builder.redirectErrorStream(true);
                    Process process = builder.start();
                    
                       //배포 상태
                    vo.setDeployStatus( message.getMessage("common.deploy.status.deleting", null, Locale.KOREA) );
                    saveDeployStatus(vo, principal);
                    
                    //Delete log...
                    InputStream inputStream = process.getInputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                    StringBuffer accumulatedBuffer = new StringBuffer("");
                    String info = null;
                    while ((info = bufferedReader.readLine()) != null){
                        accumulatedBuffer.append(info).append("\n");
                        DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "started", Arrays.asList(info));
                    }
                    accumulatedLog = accumulatedBuffer.toString();
                } else {
                    status = "error";
                    resultMessage = "배포 파일(" + deployFile + ")이 존재하지 않습니다.";
                    vo.setDeployStatus( message.getMessage("common.deploy.status.failed", null, Locale.KOREA) );
                    saveDeployStatus(vo, principal);
                }
                
                if ( "error".equalsIgnoreCase(status) || accumulatedLog.contains("fail") || accumulatedLog.contains("error") || accumulatedLog.contains("No deployment")) {
                    status = "error";
                    vo.setDeployStatus(message.getMessage("common.deploy.status.failed", null, Locale.KOREA));
                    saveDeployStatus(vo, principal);
                    if ( resultMessage.isEmpty() ) {
                        resultMessage = "MICRO BOSH 삭제 중 오류가 발생하였습니다.";
                    }
                } else {
                    status = "done";
                    resultMessage = "MICRO BOSH 삭제가 완료되었습니다.";
                    bootstrapDao.deleteBootstrapInfo(vo.getId());
                    //설치 관리자 삭제
                    deleteDirectorConfigInfo(vo.getIaasType(), vo.getDirectorName());
                }
                DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, status, Arrays.asList(resultMessage));
            }
        }catch(RuntimeException e){
            status = "error";
            DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, status, Arrays.asList("MICRO BOSH 삭제 중 Exception이 발생하였습니다."));
        } catch ( Exception e) {
            status = "error";
            DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, status, Arrays.asList("MICRO BOSH 삭제 중 Exception이 발생하였습니다."));
        }finally {
            if(status.toLowerCase().equalsIgnoreCase("error")){
                vo.setDeployStatus(message.getMessage("common.deploy.status.failed", null, Locale.KOREA));
                saveDeployStatus(vo, principal);
            }
            if(bufferedReader!=null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    if( LOGGER.isErrorEnabled() ){
                        LOGGER.error( e.getMessage() );
                    }
                }
            }
            CommonDeployUtils.deleteFile(LOCK_DIR, "bootstrap.lock");
        }
    }

    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 설치 상태 저장
     * @title : saveDeployStatus
     * @return : BootstrapVO
    *****************************************************************/
    public BootstrapVO saveDeployStatus(BootstrapVO bootstrapVo, Principal principal) {
        if ( bootstrapVo == null ) {
            return null;
        }
        bootstrapVo.setUpdateUserId(principal.getName());
        bootstrapDao.updateBootStrapInfo(bootstrapVo);
        return bootstrapVo;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 설치관리자 정보가 존재 할 경우 삭제
     * @title : deleteDirectorConfigInfo
     * @return : void
    ***************************************************/
    public void deleteDirectorConfigInfo(String iaasType, String directorName) {
        String cpi = iaasType.toLowerCase()+"_cpi";
        DirectorConfigVO vo = directorDao.selectDirectorConfigInfoByDirectorNameAndCPI(cpi, directorName);
        if( vo != null ) {
            directorDao.deleteDirector(vo.getIedaDirectorConfigSeq());
        }
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 비동기로  deleteDeploy 호출
     * @title : deleteDeployAsync
     * @return : void
    *****************************************************************/
    @Async
    public void deleteDeployAsync(BootStrapDeployDTO.Delete dto, Principal principal) {
            deleteBootstrapDeploy(dto, principal);
    }    

}
