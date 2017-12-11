package org.openpaas.ieda.deploy.web.information.release.service;


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
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ReleaseUploadAsyncService {
    
    @Autowired 
    private SimpMessagingTemplate messagingTemplate;
    @Autowired 
    private DirectorConfigService directorService;
    @Autowired MessageSource message;
    
    final private static String LOCK_DIR=LocalDirectoryConfiguration.getLockDir();
    final private static String RELEASE_DIR=LocalDirectoryConfiguration.getReleaseDir();
    final private static String SEPARATOR= System.getProperty("file.separator");
    final private static String MESSAGE_ENDPOINT  = "/info/release/upload/socket/logs"; 
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 릴리즈 업로드 요청
     * @title : uploadRelease
     * @return : void
    ***************************************************/
    public void uploadRelease( String release, String user) {
        DirectorConfigVO director = directorService.getDefaultDirector();
        try {
            HttpClient httpClient = DirectorRestHelper.getHttpClient(director.getDirectorPort());
            PostMethod postMethod = new PostMethod(DirectorRestHelper.getUploadReleaseURI(director.getDirectorUrl(), director.getDirectorPort()));
            postMethod = (PostMethod)DirectorRestHelper.setAuthorization(director.getUserId(), director.getUserPassword(), (HttpMethodBase)postMethod);
            postMethod.setRequestHeader("Content-Type", "application/x-compressed");
            
            String uploadFile = RELEASE_DIR + SEPARATOR + release;
            postMethod.setRequestEntity(new FileUploadRequestDTO(new File(uploadFile), "application/x-compressed", messagingTemplate, MESSAGE_ENDPOINT, user));
            DirectorRestHelper.sendTaskOutputWithTag(user, messagingTemplate, MESSAGE_ENDPOINT, "Started", release, Arrays.asList("Uploading Release ...", ""));
            
            int statusCode = httpClient.executeMethod(postMethod);
            if ( statusCode == HttpStatus.MOVED_PERMANENTLY.value()|| statusCode == HttpStatus.MOVED_TEMPORARILY.value() ){
                Header location = postMethod.getResponseHeader("Location");
                String taskId = DirectorRestHelper.getTaskId(location.getValue());
                DirectorRestHelper.trackToTaskWithTag(director, messagingTemplate, MESSAGE_ENDPOINT, release, httpClient, taskId, "event", user);
            } else {
                DirectorRestHelper.sendTaskOutputWithTag(user, messagingTemplate, MESSAGE_ENDPOINT, "error", release, Arrays.asList("릴리즈 업로드 중 오류가 발생하였습니다."));
            }
            
        } catch (HttpException e) {
            DirectorRestHelper.sendTaskOutput(user, messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("릴리즈 업로드 중 오류가 발생하였습니다."));
        } catch (IOException e) {
            DirectorRestHelper.sendTaskOutput(user, messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("릴리즈 업로드 중 오류가 발생하였습니다."));
        } finally{
            CommonDeployUtils.deleteFile(LOCK_DIR, release.split(".tgz")[0]+"-upload.lock");
        }
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 비동기로 uploadRelease 메소드 호출
     * @title : uploadReleaseAsync
     * @return : void
    *****************************************************************/
    @Async
    public void uploadReleaseAsync(String releaseFileName, String userId) {
        uploadRelease(releaseFileName, userId);
    }
}
