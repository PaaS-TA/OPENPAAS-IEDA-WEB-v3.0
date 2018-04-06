package org.openpaas.ieda.deploy.web.deploy.bootstrap.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.Arrays;
import java.util.Locale;

import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.deploy.api.director.dto.DirectorInfoDTO;
import org.openpaas.ieda.deploy.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.deploy.web.config.setting.service.DirectorConfigService;
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
public class BootstrapDeployAsyncService {

    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private DirectorConfigService directorConfigService;
    @Autowired private BootstrapDAO bootstrapDao;
    @Autowired private MessageSource message;
    
    final private static String SEPARATOR = System.getProperty("file.separator");
    final private static String DEPLOYMENT_DIR = LocalDirectoryConfiguration.getDeploymentDir() + SEPARATOR;
    final private static String KEY_DIR = LocalDirectoryConfiguration.getLockDir()+SEPARATOR;
    final private static String CREDENTIAL_DIR = LocalDirectoryConfiguration.getGenerateCredentialDir() + SEPARATOR;
    final private static String MESSAGE_ENDPOINT = "/deploy/bootstrap/install/logs"; 
    private final static Logger LOGGER = LoggerFactory.getLogger(BootstrapDeployAsyncService.class);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : bosh를 실행하여 Bootstrap 설치 실행
     * @title : deployBootstrap
     * @return : void
    *****************************************************************/
    public void deployBootstrap(BootStrapDeployDTO.Install dto, Principal principal) {
        
        String status = "";
        String accumulatedLog= null;
        BufferedReader bufferedReader = null;
        BootstrapVO bootstrapInfo = null;
        try {
            bootstrapInfo = bootstrapDao.selectBootstrapInfo(Integer.parseInt(dto.getId()));
            String deployFile = "";
            if( bootstrapInfo != null ) {
                deployFile = DEPLOYMENT_DIR + bootstrapInfo.getDeploymentFile();
            }else {
                bootstrapInfo = new BootstrapVO(); 
            }
            File deploymentFile = new File(deployFile);
            
            if( deploymentFile.exists() ) {
                //1. 배포상태 설정
                bootstrapInfo.setUpdateUserId( principal.getName());
                String deployStatus = message.getMessage("common.deploy.status.processing", null, Locale.KOREA);
                bootstrapInfo.setDeployStatus( deployStatus );
                saveDeployStatus(bootstrapInfo);
                //2. bosh 실행
                ProcessBuilder builder = new ProcessBuilder("bosh", "create-env", deployFile, 
                        "--state="+deployFile.replace(".yml", "")+"-state.json", 
                        "--vars-store="+CREDENTIAL_DIR+bootstrapInfo.getDeploymentFile().replace(".yml", "-creds.yml"), "--tty");
                builder.redirectErrorStream(true);
                Process process = builder.start();

                //실행 출력하는 로그를 읽어온다.
                InputStream inputStream = process.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                String info = null;
                StringBuffer accumulatedBuffer = new StringBuffer();
                while ((info = bufferedReader.readLine()) != null){
                    accumulatedBuffer.append(info).append("\n");
                    DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "started", Arrays.asList(info));
                }
                if( accumulatedBuffer != null ) {
                    accumulatedLog = accumulatedBuffer.toString();
                }
            } else {
                status = "error";
            }
            if( accumulatedLog != null ) {
                bootstrapInfo.setDeployLog(accumulatedLog);
            }
            if ( status.equalsIgnoreCase("error") ) {
                bootstrapInfo.setDeployStatus( message.getMessage("common.deploy.status.failed", null, Locale.KOREA) );
                saveDeployStatus(bootstrapInfo);
                DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList( "설치할 배포 파일(" + deployFile + ")이 존재하지 않습니다."));
            } else {
                if ( accumulatedLog.contains("Failed deploying") || accumulatedLog.contains("Failed")) {
                    status = "error";
                    bootstrapInfo.setDeployStatus(message.getMessage("common.deploy.status.failed", null, Locale.KOREA) );
                    saveDeployStatus(bootstrapInfo);
                    DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("", "MICRO BOSH 설치 중 오류가 발생하였습니다.<br> 배포 정보를 확인 하세요."));
                }    else {
                    // 타겟 테스트
                    DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "started", Arrays.asList("","MICRO BOSH 디렉터 정보 : https://" + bootstrapInfo.getPublicStaticIp() + ":25555"));
                    DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "started", Arrays.asList("MICRO BOSH 디렉터 타겟 접속 테스트..."));
                    DirectorInfoDTO directorInfo = directorConfigService.getDirectorInfo(bootstrapInfo.getPublicStaticIp(), 25555, "admin", "admin");
                    
                    if ( directorInfo == null ) {
                        status = "error";
                        bootstrapInfo.setDeployStatus(message.getMessage("common.deploy.status.failed", null, Locale.KOREA));
                        saveDeployStatus(bootstrapInfo);
                        DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("MICRO BOSH 디렉터 타겟 접속 테스트 실패 <br> 인프라 정보를 확인 하세요."));
                    } else {
                        DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "started", Arrays.asList("MICRO BOSH 디렉터 타겟 접속 테스트 성공"));
                        status = "done";
                        bootstrapInfo.setDeployStatus( message.getMessage("common.deploy.status.done", null,  Locale.KOREA ) );
                        saveDeployStatus(bootstrapInfo);
                        DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "done", Arrays.asList("", "MICRO BOSH 설치가 완료되었습니다."));
                    }
                }
            }
        }catch(RuntimeException e){
        
            status = "error";
            DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("배포 중 Exception이 발생하였습니다."));
            if ( bootstrapInfo != null ) {
                bootstrapInfo.setDeployLog(accumulatedLog);
            }
            String deployStatus = message.getMessage("common.deploy.status.failed", null, Locale.KOREA);
            if( deployStatus != null ) {
                bootstrapInfo.setDeployStatus( deployStatus );
            }
            saveDeployStatus(bootstrapInfo);
        }catch ( Exception e) {    
            status = "error";
            e.printStackTrace();
            DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("배포 중 Exception이 발생하였습니다."));
            if ( bootstrapInfo != null ) {
                bootstrapInfo.setDeployLog(accumulatedLog);
            }
            String deployStatus =  message.getMessage("common.deploy.status.failed", null, Locale.KOREA) ;
            if( deployStatus != null ) {
                bootstrapInfo.setDeployStatus(message.getMessage("common.deploy.status.failed", null, Locale.KOREA) );
            }
            saveDeployStatus(bootstrapInfo);
        }finally {
            try {
                if(bufferedReader!=null) {
                    bufferedReader.close();
                    
                }
            } catch (Exception e) {
                if( LOGGER.isErrorEnabled() ) {
                    LOGGER.error( e.getMessage() );
                }
            }
            //동시 설치 방지 lock 파일 삭제
            File lockFile = new File(KEY_DIR + "bootstrap.lock");
            if(lockFile.exists()){
                Boolean check = lockFile.delete();
                if( LOGGER.isDebugEnabled() ) {
                    LOGGER.debug("check delete lock File  : "  + check); 
                }
            }
        }
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 설치 상태를 설정하여 저장
     * @title : saveDeployStatus
     * @return : BootstrapVO
    *****************************************************************/
    public BootstrapVO saveDeployStatus(BootstrapVO bootstrapVo) {
        if ( bootstrapVo == null ) {
            return null;
        }
        bootstrapDao.updateBootStrapInfo(bootstrapVo);
        
        return bootstrapVo;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 비동기로 deployAsync 메소드 호출
     * @title : deployAsync
     * @return : void
    *****************************************************************/
    @Async
    public void deployAsync(BootStrapDeployDTO.Install dto, Principal principal) {
            deployBootstrap(dto, principal);
    }
    
}
