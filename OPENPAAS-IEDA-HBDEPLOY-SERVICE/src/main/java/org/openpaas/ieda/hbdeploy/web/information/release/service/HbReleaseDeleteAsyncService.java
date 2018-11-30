package org.openpaas.ieda.hbdeploy.web.information.release.service;

import java.security.Principal;
import java.util.Arrays;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.hbdeploy.api.director.utility.HbDirectorRestHelper;
import org.openpaas.ieda.hbdeploy.web.config.setting.dao.HbDirectorConfigDAO;
import org.openpaas.ieda.hbdeploy.web.config.setting.dao.HbDirectorConfigVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class HbReleaseDeleteAsyncService {
    
    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private HbDirectorConfigDAO dao;
    final private static String MESSAGEENDPOINT = "/info/hbRelease/delete/socket/logs"; 

    /***************************************************
     * @param directorId 
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드된 릴리즈 삭제 요청
     * @title : deleteRelease
     * @return : void
    ***************************************************/
    public void deleteRelease(String releaseName, String releaseVersion, String directorId, Principal principal) {
        
        HbDirectorConfigVO directorInfo = dao.selectHbDirectorConfigBySeq(Integer.parseInt(directorId));
        if ( directorInfo == null ) {
            throw new CommonException("notfound.director.exception", "디렉터가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
        try {
            HttpClient httpClient = HbDirectorRestHelper.getHttpClient(directorInfo.getDirectorPort());
            DeleteMethod deleteMethod = new DeleteMethod(HbDirectorRestHelper.getDeleteReleaseURI( directorInfo.getDirectorUrl(), directorInfo.getDirectorPort(), releaseName, releaseVersion));
            deleteMethod = (DeleteMethod)HbDirectorRestHelper.setAuthorization(directorInfo.getUserId(), directorInfo.getUserPassword(), (HttpMethodBase)deleteMethod);
            //실행
            int statusCode = httpClient.executeMethod(deleteMethod);
            
            if ( statusCode == HttpStatus.MOVED_PERMANENTLY.value()  || statusCode == HttpStatus.MOVED_TEMPORARILY.value()    ) {
                Header location = deleteMethod.getResponseHeader("Location");
                String taskId = HbDirectorRestHelper.getTaskId(location.getValue());
                HbDirectorRestHelper.trackToTask(directorInfo, messagingTemplate, MESSAGEENDPOINT, httpClient, taskId, "event", principal.getName());
            } else {
                HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGEENDPOINT, "error", Arrays.asList("릴리즈 삭제 중 오류가 발생하였습니다."));
            }
        }catch(RuntimeException e){
            HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGEENDPOINT, "error", Arrays.asList("릴리즈 삭제 중 Exception이 발생하였습니다."));
        }catch ( Exception e) {
            HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGEENDPOINT, "error", Arrays.asList("릴리즈 삭제 중 Exception이 발생하였습니다."));
        }
    }

    /***************************************************
     * @param string 
     * @project : Paas 플랫폼 설치 자동화
     * @description : deleteRelease 메소드 호출
     * @title : deleteReleaseAsync
     * @return : void
    ***************************************************/
    @Async
    public void deleteReleaseAsync(String releaseName, String releaseVersion, String directorId, Principal principal) {
        deleteRelease(releaseName, releaseVersion, directorId, principal);
    }
}
