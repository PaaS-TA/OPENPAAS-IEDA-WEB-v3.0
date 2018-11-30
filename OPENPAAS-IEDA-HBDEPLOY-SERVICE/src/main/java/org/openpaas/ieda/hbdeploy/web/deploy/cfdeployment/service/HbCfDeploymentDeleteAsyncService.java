package org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service;

import java.io.File;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.hbdeploy.api.director.utility.HbDirectorRestHelper;
import org.openpaas.ieda.hbdeploy.web.config.setting.dao.HbDirectorConfigDAO;
import org.openpaas.ieda.hbdeploy.web.config.setting.dao.HbDirectorConfigVO;
import org.openpaas.ieda.hbdeploy.web.config.setting.service.HbDirectorConfigService;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentVO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dto.HbCfDeploymentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class HbCfDeploymentDeleteAsyncService {
    
    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private HbDirectorConfigService directorConfigService;
    @Autowired private HbDirectorConfigDAO directorConfigDao;
    @Autowired private HbCfDeploymentDAO cfDeploymentDao;
    @Autowired private HbCfDeploymentService cfDeploymentService;
    @Autowired private MessageSource message;
    
    private final static String SEPARATOR = System.getProperty("file.separator");
    private final static String DEPLOYMENT_DIR = LocalDirectoryConfiguration.getDeploymentDir();
    final private static String HYBRID_CF_CREDENTIAL_DIR = LocalDirectoryConfiguration.getGenerateHybridCfCredentialDir();
    private final static String KEY_DIR = LocalDirectoryConfiguration.getLockDir()+SEPARATOR;
    final static private String CF_MESSAGE_ENDPOINT =  "/deploy/hbCfDeployment/delete/logs";
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 플랫폼 삭제 요청
     * @title : deleteDeploy
     * @return : void
    *****************************************************************/
    public void deleteDeploy(HbCfDeploymentDTO dto, String platform, Principal principal) {
        String errorMsg = message.getMessage("common.internalServerError.message", null, Locale.KOREA);
        String messageEndpoint = CF_MESSAGE_ENDPOINT;
        String deploymentName = "";
        String deploymentFileName = "";
        
        HbCfDeploymentVO vo = cfDeploymentService.getHbCfDeploymentInfo(dto.getId());
        
        deploymentName = vo != null ?vo.getDefaultConfigInfo() : "";
        deploymentFileName = vo != null ? vo.getCloudConfigFile() : "";
        if ( StringUtils.isEmpty(deploymentName) ) {
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), 
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        String deleteStatusName = message.getMessage("common.deploy.status.deleting", null, Locale.KOREA);
        if ( vo != null ) {
            vo.setDeployStatus(deleteStatusName);
            vo.setUpdateUserId(principal.getName());
            saveDeployStatus(vo);
        }
        String cloudConfigFile = DEPLOYMENT_DIR + SEPARATOR + deploymentFileName; 
        try {
            HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "started", Arrays.asList("CF Deployment Delete Starting...."));
            HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "started", Arrays.asList("Director Info Checking...."));
            HbDirectorConfigVO directorInfo = directorConfigDao.selectHbDirectorConfigBySeq(Integer.parseInt(vo.getHbCfDeploymentResourceConfigVO().getDirectorInfo()));
            String httpStatus = directorConfigService.isExistBoshEnvLogin(directorInfo.getDirectorUrl(), directorInfo.getDirectorPort(), directorInfo.getUserId(), directorInfo.getUserPassword());
            if(!"200".equals(httpStatus)){
                HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList("디렉터 정보를 확인 하세요."));
                if(vo != null){
                    String deployStatus = message.getMessage("common.deploy.status.error", null, Locale.KOREA);
                    vo.setDeployStatus(deployStatus);
                    vo.setUpdateUserId(principal.getName());
                    saveDeployStatus(vo);
                }
            }
            HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "started", Arrays.asList("Director Info Check Succeed...."));
            List<String> cmd = new ArrayList<String>(); //bosh cloud config 명령어 실행 줄 Cloud Config 관련 Rest API를 아직 지원 안하는 것 같음 2018.08.01
            cmd.add("bosh");
            cmd.add("-e");
            cmd.add(directorInfo.getDirectorName());
            cmd.add("update-cloud-config");
            cmd.add(cloudConfigFile);
            cmd.add("-n");
            ProcessBuilder builder = new ProcessBuilder(cmd);
            builder.redirectErrorStream(true);
            builder.start();
            
            HttpClient httpClient = HbDirectorRestHelper.getHttpClient(directorInfo.getDirectorPort());
            
            DeleteMethod deleteMethod = new DeleteMethod(HbDirectorRestHelper.getDeleteDeploymentURI(directorInfo.getDirectorUrl(), directorInfo.getDirectorPort(), deploymentName));
            deleteMethod = (DeleteMethod)HbDirectorRestHelper.setAuthorization(directorInfo.getUserId(), directorInfo.getUserPassword(), (HttpMethodBase)deleteMethod);
            int statusCode = httpClient.executeMethod(deleteMethod);
            
            if( statusCode == HttpStatus.MOVED_PERMANENTLY.value() || statusCode == HttpStatus.MOVED_TEMPORARILY.value() ) {
                Header location = deleteMethod.getResponseHeader("Location");
                String taskId = HbDirectorRestHelper.getTaskId(location.getValue());
                HbDirectorRestHelper.trackToTask(directorInfo, messagingTemplate, messageEndpoint, httpClient, taskId, "event", principal.getName());
            }else {
                HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "done", Arrays.asList("CF 삭제에 실패 했습니다.."));
            }
            deleteCfInfo(vo);
        } catch(RuntimeException e){
            HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList(errorMsg));
        } catch ( Exception e) {
            HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList(errorMsg));
        } finally {
            //동시 설치 방지 lock 파일 삭제
            File lockFile = new File(KEY_DIR + "hybird_cfDeployment.lock");
            if(lockFile.exists()){
                lockFile.delete();
            }
        }
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 정보 삭제
     * @title : deleteCfInfo
     * @return : void
    *****************************************************************/
    @Transactional
    public void deleteCfInfo( HbCfDeploymentVO vo ){
        HbCfDeploymentDTO dto = new HbCfDeploymentDTO();
        dto.setId(vo.getId());
        if ( vo != null ) {
            cfDeploymentDao.deleteCfDeploymentConfigInfo(dto);
        }
        String cloudConfigFileName = DEPLOYMENT_DIR + SEPARATOR + vo.getCloudConfigFile();
        File file = new File(cloudConfigFileName);
        if(file.exists()){
            file.delete();
        }
        String runtimeConfigFileName = HYBRID_CF_CREDENTIAL_DIR + SEPARATOR + vo.getHbCfDeploymentDefaultConfigVO().getDeploymentName()+"runtime-cred.yml";
        file = new File(runtimeConfigFileName);
        if(file.exists()){
            file.delete();
        }
    }
    
    public void saveDeployStatus(HbCfDeploymentVO vo) {
        cfDeploymentDao.updateCfDeploymentConfigInfo(vo);
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 비동기식으로 deleteDeploy 호출
     * @title : deleteDeployAsync
     * @return : void
    *****************************************************************/
    @Async
    public void deleteDeployAsync(HbCfDeploymentDTO dto, String platform, Principal principal) {
        deleteDeploy(dto, platform, principal);
    }    
}
