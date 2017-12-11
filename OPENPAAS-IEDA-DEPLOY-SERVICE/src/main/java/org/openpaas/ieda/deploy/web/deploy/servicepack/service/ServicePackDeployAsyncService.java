package org.openpaas.ieda.deploy.web.deploy.servicepack.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.Arrays;
import java.util.Locale;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.deploy.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.deploy.web.config.setting.service.DirectorConfigService;
import org.openpaas.ieda.deploy.web.deploy.servicepack.dao.ServicePackDAO;
import org.openpaas.ieda.deploy.web.deploy.servicepack.dao.ServicePackVO;
import org.openpaas.ieda.deploy.web.deploy.servicepack.dto.ServicePackParamDTO;
import org.openpaas.ieda.deploy.web.information.manifest.dao.ManifestDAO;
import org.openpaas.ieda.deploy.web.information.manifest.dao.ManifestVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ServicePackDeployAsyncService {
    @Autowired private ServicePackDAO dao;
    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private DirectorConfigService directorConfigService;
    @Autowired private ManifestDAO manifestDao;
    @Autowired private MessageSource message;
    
    final private static String SEPARATOR = System.getProperty("file.separator");
    final private static String DEPLOYMENT_DIR = LocalDirectoryConfiguration.getDeploymentDir() + SEPARATOR;
    final private static String MESSAGE_ENDPOINT = "/deploy/servicePack/install/logs"; 
    private final static Logger LOGGER = LoggerFactory.getLogger(ServicePackDeployAsyncService.class);
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스팩 설치
     * @title : deploy
     * @return : void
    ***************************************************/
    private void deploy(ServicePackParamDTO dto, Principal principal) {
        ServicePackVO vo = null;
        ManifestVO manifestVo = null;
        String deploymentFileName = null;
        vo = dao.selectServicePackDetailInfo(dto.getId());

        if ( vo != null){
            manifestVo = manifestDao.selectManifestInfoByDeployName(vo.getDeploymentName());
            if(manifestVo == null){
                throw new CommonException("notfound.diegodelete.exception",
                        "배포파일 정보가 존재하지 않습니다..", HttpStatus.NOT_FOUND);
            }else {
                deploymentFileName = vo.getDeploymentFile();
            }
        }
        if (  StringUtils.isEmpty(deploymentFileName) ) {
            throw new CommonException("notfound.diegodelete.exception",
                    "배포파일 정보가 존재하지 않습니다..", HttpStatus.NOT_FOUND);
        }
        
        if ( vo != null ) {
            String deployStatus = message.getMessage("common.deploy.status.processing", null, Locale.KOREA);
            vo.setDeployStatus(deployStatus);
            vo.setUpdateUserId(principal.getName());
            dao.updateServicePackInfo(vo);
        }
        
        String status = "";
        StringBuffer content = new StringBuffer(); 
        String temp = "";
        String taskId = "";
        DirectorConfigVO defaultDirector = directorConfigService.getDefaultDirector();
        
        BufferedReader br = null;
        InputStreamReader isr = null;
        FileInputStream fis = null;
        try {
            HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
            PostMethod postMethod = new PostMethod(DirectorRestHelper.getDeployURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort()));
            postMethod = (PostMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)postMethod);
            postMethod.setRequestHeader("Content-Type", "text/yaml"); //header 정의
            
            String deployFile = DEPLOYMENT_DIR + deploymentFileName;
            
            fis = new FileInputStream(deployFile);
            isr = new InputStreamReader(fis, "UTF-8");
            br = new BufferedReader(isr);
            
            while ( (temp=br.readLine()) != null) {
                content.append(temp).append("\n");
            }
            
            postMethod.setRequestEntity(new StringRequestEntity(content.toString(), "text/yaml", "UTF-8"));
            //HTTP 요청 및 요청 결과
            int statusCode = httpClient.executeMethod(postMethod);
            if ( statusCode == HttpStatus.MOVED_PERMANENTLY.value() || statusCode == HttpStatus.MOVED_TEMPORARILY.value()    ) {
                Header location = postMethod.getResponseHeader("Location");
                taskId = DirectorRestHelper.getTaskId(location.getValue());
                status = DirectorRestHelper.trackToTask(defaultDirector, messagingTemplate, MESSAGE_ENDPOINT, httpClient, taskId, "event", principal.getName());
            } else {
                DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("배포 중 오류가 발생하였습니다.[" + statusCode + "]"));
            }
        } catch ( IOException e) {
            status = "error";
            DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("배포 중 Exception이 발생하였습니다."));
        } catch (RuntimeException e) {
            status = "error";
            DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("배포 중 Exception이 발생하였습니다."));
        }catch ( Exception e) {
            status = "error";
            DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("배포 중 Exception이 발생하였습니다."));
        } finally {
            try {
                if ( br != null ) {
                    br.close();
                }
                if ( fis != null ) {
                    fis.close();
                }
                if ( isr != null ) {
                    isr.close();
                }
            } catch ( Exception e ) {
                if( LOGGER.isErrorEnabled() ){
                    LOGGER.error( e.getMessage() );
                }
            }
            
            String deployStatus = "";
            if( status.equalsIgnoreCase("done") ){
                deployStatus = message.getMessage("common.deploy.status.done", null, Locale.KOREA);
            } else if( status.equalsIgnoreCase("error") ){
                deployStatus = message.getMessage("common.deploy.status.failed", null, Locale.KOREA);
            } else if( status.equalsIgnoreCase("cancelled") ){
                deployStatus = message.getMessage("common.deploy.status.cancelled", null, Locale.KOREA);
            }
            
            if ( vo != null ) {
                vo.setDeployStatus(deployStatus);
                vo.setUpdateUserId(principal.getName());
                dao.updateServicePackInfo(vo);
                manifestVo.setDeployStatus(deployStatus);
                manifestDao.updateManifestInfo(manifestVo);
            }
        }
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : deploy 메소드를 호출
     * @title : deployAsync
     * @return : void
    ***************************************************/
    @Async
    public void deployAsync(ServicePackParamDTO dto, Principal principal) {
        deploy(dto, principal);
    }
}
