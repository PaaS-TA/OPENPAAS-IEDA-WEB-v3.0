package org.openpaas.ieda.deploy.web.information.stemcell.service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.PostMethod;
import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.deploy.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.deploy.web.common.service.CommonDeployUtils;
import org.openpaas.ieda.deploy.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.deploy.web.config.setting.service.DirectorConfigService;
import org.openpaas.ieda.deploy.web.information.stemcell.dto.FileUploadRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class StemcellUploadAsyncService {
    
    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private DirectorConfigService directorConfigService;
    
    final private static String LOCK_DIR = LocalDirectoryConfiguration.getLockDir();
    final private static String MESSAGE_ENDPOINT  = "/info/stemcell/upload/logs"; 
    final private static String EXCEPTION_MESSAGE = "스템셀 업로드 중 오류가 발생하였습니다.";
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 업로드 요청
     * @title : uploadStemcell
     * @return : void
    ***************************************************/
    public void uploadStemcell(String stemcellDir, String stemcellFileName, String userId) {
        DirectorConfigVO defaultDirector = directorConfigService.getDefaultDirector();
        try {
            HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
            PostMethod postMethod  = new PostMethod(DirectorRestHelper.getUploadStemcellURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort()));
            postMethod = (PostMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)postMethod);
            postMethod.setRequestHeader("Content-Type", "application/x-compressed");
            
            String uploadFile = stemcellDir + System.getProperty("file.separator") + stemcellFileName;
            postMethod.setRequestEntity(new FileUploadRequestDTO(new File(uploadFile), "application/x-compressed", messagingTemplate, MESSAGE_ENDPOINT, userId));
            DirectorRestHelper.sendTaskOutputWithTag(userId, messagingTemplate, MESSAGE_ENDPOINT, "Started", stemcellFileName, Arrays.asList("Uploading Stemcell ...", ""));
            
            int statusCode = httpClient.executeMethod(postMethod);
            if ( statusCode == HttpStatus.MOVED_PERMANENTLY.value() || statusCode == HttpStatus.MOVED_TEMPORARILY.value() ) {
                Header location = postMethod.getResponseHeader("Location");
                String taskId = DirectorRestHelper.getTaskId(location.getValue());
                DirectorRestHelper.trackToTaskWithTag(defaultDirector, messagingTemplate, MESSAGE_ENDPOINT, stemcellFileName , httpClient, taskId, "event", userId);
            } else {
                DirectorRestHelper.sendTaskOutputWithTag(userId, messagingTemplate, MESSAGE_ENDPOINT, "error", stemcellFileName, Arrays.asList(EXCEPTION_MESSAGE));
            }
        } catch( HttpException e){
            DirectorRestHelper.sendTaskOutputWithTag(userId, messagingTemplate, MESSAGE_ENDPOINT, "error", stemcellFileName, Arrays.asList(EXCEPTION_MESSAGE));
        } catch ( IOException e) {
            DirectorRestHelper.sendTaskOutputWithTag(userId, messagingTemplate, MESSAGE_ENDPOINT, "error", stemcellFileName, Arrays.asList(EXCEPTION_MESSAGE));
        }finally{
            CommonDeployUtils.deleteFile(LOCK_DIR, stemcellFileName.split(".tgz")[0]+"-upload.lock");
        }
    }

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 비동기 처리방식으로 uploadStemcell 메소드를 호출
     * @title : uploadStemcellAsync
     * @return : void
    ***************************************************/
    @Async
    public void uploadStemcellAsync(String stemcellDir, String stemcellFileName, String userId) {
        uploadStemcell(stemcellDir, stemcellFileName, userId);
    }
}
