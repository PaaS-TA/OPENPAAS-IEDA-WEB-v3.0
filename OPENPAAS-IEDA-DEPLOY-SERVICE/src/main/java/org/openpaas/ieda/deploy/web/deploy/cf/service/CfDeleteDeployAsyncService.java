package org.openpaas.ieda.deploy.web.deploy.cf.service;

import java.io.File;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.deploy.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.deploy.web.config.setting.service.DirectorConfigService;
import org.openpaas.ieda.deploy.web.deploy.cf.dao.CfDAO;
import org.openpaas.ieda.deploy.web.deploy.cf.dao.CfVO;
import org.openpaas.ieda.deploy.web.deploy.cf.dto.CfParamDTO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.network.NetworkDAO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.resource.ResourceDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class CfDeleteDeployAsyncService {
    
    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private DirectorConfigService directorConfigService;
    @Autowired private CfDAO cfDao;
    @Autowired private NetworkDAO networkDao;
    @Autowired private ResourceDAO resourceDao;
    @Autowired private MessageSource message;
    
    private final static String SEPARATOR = System.getProperty("file.separator");
    private final static String DEPLOYMENT_DIR = LocalDirectoryConfiguration.getDeploymentDir();
    final private static String CF_CREDENTIAL_DIR = LocalDirectoryConfiguration.getGenerateCfDeploymentCredentialDir();
    final static private String CF_MESSAGE_ENDPOINT =  "/deploy/cf/delete/logs";
    final static private String CF_DIEGO_MESSAGE_ENDPOINT =  "/deploy/cfDiego/delete/logs";
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 플랫폼 삭제 요청
     * @title : deleteDeploy
     * @return : void
    *****************************************************************/
    public void deleteDeploy(CfParamDTO.Delete dto, String platform, Principal principal) {
        String errorMsg = message.getMessage("common.internalServerError.message", null, Locale.KOREA);
        String messageEndpoint = platform.equalsIgnoreCase("cf") ? CF_MESSAGE_ENDPOINT : CF_DIEGO_MESSAGE_ENDPOINT;
        String deploymentName = "";
        String deploymentFileName = "";
        CfVO vo  = cfDao.selectCfInfoById(Integer.parseInt(dto.getId()));
        deploymentName = vo != null ?vo.getDeploymentName() : "";
        deploymentFileName = vo != null ? vo.getDeploymentFile() : "";
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
            DirectorConfigVO defaultDirector = directorConfigService.getDefaultDirector();
            
            List<String> cmd = new ArrayList<String>(); //bosh cloud config 명령어 실행 줄 Cloud Config 관련 Rest API를 아직 지원 안하는 것 같음 2018.08.01
            cmd.add("bosh");
            cmd.add("-e");
            cmd.add(defaultDirector.getDirectorName());
            cmd.add("update-cloud-config");
            cmd.add(cloudConfigFile);
            cmd.add("-n");
            ProcessBuilder builder = new ProcessBuilder(cmd);
            builder.redirectErrorStream(true);
            builder.start();
            
            HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
            
            DeleteMethod deleteMethod = new DeleteMethod(DirectorRestHelper.getDeleteDeploymentURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), deploymentName));
            deleteMethod = (DeleteMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)deleteMethod);
            
            int statusCode = httpClient.executeMethod(deleteMethod);
            if( statusCode == HttpStatus.MOVED_PERMANENTLY.value() || statusCode == HttpStatus.MOVED_TEMPORARILY.value() ) {
                Header location = deleteMethod.getResponseHeader("Location");
                String taskId = DirectorRestHelper.getTaskId(location.getValue());
                DirectorRestHelper.trackToTask(defaultDirector, messagingTemplate, messageEndpoint, httpClient, taskId, "event", principal.getName());
            }else {
                DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "done", Arrays.asList("CF 삭제에 실패 하였습니다."));
            }
            deleteCfInfo(vo);
        } catch(RuntimeException e){
            DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList(errorMsg));
        } catch ( Exception e) {
            DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList(errorMsg));
        }
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 정보 삭제
     * @title : deleteCfInfo
     * @return : void
    *****************************************************************/
    @Transactional
    public void deleteCfInfo( CfVO vo ){
        if ( vo != null ) {
            String cfDeployType = message.getMessage("common.deploy.type.cf.name", null, Locale.KOREA);
            cfDao.deleteCfInfoRecord(vo.getId());
            networkDao.deleteNetworkInfoRecord( vo.getId(), cfDeployType );
            resourceDao.deleteResourceInfo( vo.getId(), cfDeployType);
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("id", vo.getId().toString());
            map.put("deploy_type", cfDeployType);
            cfDao.deleteCfJobSettingListById(map);
            
            String cloudConfigFileName = DEPLOYMENT_DIR + SEPARATOR + vo.getDeploymentFile();
            File file = new File(cloudConfigFileName);
            if(file.exists()){
                file.delete();
            }
            String runtimeConfigFileName = CF_CREDENTIAL_DIR + SEPARATOR + vo.getDeploymentName()+"runtime-cred.yml";
            file = new File(runtimeConfigFileName);
            if(file.exists()){
                file.delete();
            }
        }
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 배포 삭제 상태 저장
     * @title : saveDeployStatus
     * @return : CfVO
    *****************************************************************/
    public CfVO saveDeployStatus(CfVO cfVo) {
        cfDao.updateCfInfo(cfVo);
        return cfVo;
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 비동기식으로 deleteDeploy 호출
     * @title : deleteDeployAsync
     * @return : void
    *****************************************************************/
    @Async
    public void deleteDeployAsync(CfParamDTO.Delete dto, String platform, Principal principal) {
        deleteDeploy(dto, platform, principal);
    }    
}
