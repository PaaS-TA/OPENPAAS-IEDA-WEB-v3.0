package org.openpaas.ieda.deploy.web.deploy.diego.service;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.deploy.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.deploy.web.config.setting.service.DirectorConfigService;
import org.openpaas.ieda.deploy.web.deploy.common.dao.network.NetworkDAO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.resource.ResourceDAO;
import org.openpaas.ieda.deploy.web.deploy.diego.dao.DiegoDAO;
import org.openpaas.ieda.deploy.web.deploy.diego.dao.DiegoVO;
import org.openpaas.ieda.deploy.web.deploy.diego.dto.DiegoParamDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class DiegoDeleteDeployAsyncService {
    
    @Autowired private DiegoDAO diegoDao;
    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private DirectorConfigService directorConfigService;
    @Autowired private NetworkDAO networkDao;
    @Autowired private ResourceDAO resourceDao;
    @Autowired MessageSource message;
        
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Diego 플랫폼 삭제 요청
     * @title : deleteDeploy
     * @return : void
    ***************************************************/
    public void deleteDeploy(DiegoParamDTO.Delete dto, String platform, Principal principal) {
        String messageEndpoint = "/deploy/"+platform+"/delete/logs"; 
        String deploymentName = null;
        DiegoVO vo = diegoDao.selectDiegoInfo(Integer.parseInt(dto.getId()));
        
        if ( vo != null ) {
            deploymentName = vo.getDeploymentName();
        }
            
        if ( StringUtils.isEmpty(deploymentName) ) {
            throw new CommonException("notfound.diegodelete.exception", "배포정보가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
        
        DirectorConfigVO defaultDirector = directorConfigService.getDefaultDirector();
        if ( vo != null ) {
            vo.setDeployStatus(message.getMessage("common.deploy.status.deleting", null, Locale.KOREA));
            vo.setUpdateUserId(principal.getName());
            saveDeployStatus(vo);
        }
        
        try {
            HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
            
            DeleteMethod deleteMethod = new DeleteMethod(DirectorRestHelper.getDeleteDeploymentURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), deploymentName));
            deleteMethod = (DeleteMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)deleteMethod);
            int statusCode = httpClient.executeMethod(deleteMethod);
            if ( statusCode == HttpStatus.MOVED_PERMANENTLY.value() || statusCode == HttpStatus.MOVED_TEMPORARILY.value() ) {
                Header location = deleteMethod.getResponseHeader("Location");
                String taskId = DirectorRestHelper.getTaskId(location.getValue());
                
                DirectorRestHelper.trackToTask(defaultDirector, messagingTemplate, messageEndpoint, httpClient, taskId, "event", principal.getName());
                deleteDiegoInfo(vo);
                
            } else {
                deleteDiegoInfo(vo);
                DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "done", Arrays.asList("Diego 삭제가 완료되었습니다."));
            }
        }catch(RuntimeException e){
            vo.setDeployStatus(message.getMessage("common.deploy.status.error", null, Locale.KOREA));
            vo.setUpdateUserId(principal.getName());
            saveDeployStatus(vo);
            DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList("배포삭제 중 Exception이 발생하였습니다."));
        }catch ( Exception e) {
            vo.setDeployStatus(message.getMessage("common.deploy.status.error", null, Locale.KOREA));
            vo.setUpdateUserId(principal.getName());
            saveDeployStatus(vo);
            DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList("배포삭제 중 Exception이 발생하였습니다."));
        }

    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Diego 정보 삭제
     * @title : deleteDiegoInfo
     * @return : void
    ***************************************************/
    @Transactional
    public void deleteDiegoInfo( DiegoVO vo ){
        if ( vo != null ) {
            String deployType = message.getMessage("common.deploy.type.diego.name", null, Locale.KOREA);
            HashMap<String, String> map = new HashMap<String, String>();
            diegoDao.deleteDiegoInfoRecord(vo.getId());
            networkDao.deleteNetworkInfoRecord( vo.getId(), deployType );
            resourceDao.deleteResourceInfo( vo.getId(), deployType );
            map.put("id", vo.getId().toString());
            map.put("deploy_type", deployType);
            diegoDao.deleteDiegoJobSettingInfo(map);
        }
    }
    
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Diego 배포 상태 저장
     * @title : saveDeployStatus
     * @return : DiegoVO
    ***************************************************/
    public DiegoVO saveDeployStatus(DiegoVO diegoVo) {
        if ( diegoVo == null ) {
            return null;
        }
        diegoDao.updateDiegoDefaultInfo(diegoVo);
        return diegoVo;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : deleteDeploy 메소드 호출
     * @title : deleteDeployAsync
     * @return : void
    ***************************************************/
    @Async
    public void deleteDeployAsync(DiegoParamDTO.Delete dto, String platform, Principal principal) {
        deleteDeploy(dto, platform, principal);
    }
    

}
