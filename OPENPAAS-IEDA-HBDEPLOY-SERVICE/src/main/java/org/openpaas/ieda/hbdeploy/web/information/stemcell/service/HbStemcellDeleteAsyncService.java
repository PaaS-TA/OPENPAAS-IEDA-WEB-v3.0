package org.openpaas.ieda.hbdeploy.web.information.stemcell.service;

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
public class HbStemcellDeleteAsyncService {
    
    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private HbDirectorConfigDAO dao;
    
    final private static String MESSAGE_ENDPOINT  = "/info/hbstemcell/delete/logs"; 
    
    /***************************************************
     * @param directorId 
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드된 스템셀 삭제 요청
     * @title : deleteStemcell
     * @return : void
    ***************************************************/
    public void deleteStemcell(String stemcellName, String stemcellVersion, String directorId, Principal principal ) {
        //선택된 디렉터 정보 조회
        HbDirectorConfigVO directorInfo = dao.selectHbDirectorConfigBySeq(Integer.parseInt(directorId));
        if ( directorInfo == null ) {
            throw new CommonException("notfound.director.exception", "디렉터가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
        try {
            HttpClient httpClient = HbDirectorRestHelper.getHttpClient(directorInfo.getDirectorPort());
            DeleteMethod deleteMethod = new DeleteMethod(HbDirectorRestHelper.getDeleteStemcellURI(directorInfo.getDirectorUrl(), directorInfo.getDirectorPort(), stemcellName, stemcellVersion));
            deleteMethod = (DeleteMethod)HbDirectorRestHelper.setAuthorization(directorInfo.getUserId(), directorInfo.getUserPassword(), (HttpMethodBase)deleteMethod);
            
            //Request에 대한 응답
            int statusCode = httpClient.executeMethod(deleteMethod);
            if ( statusCode == HttpStatus.MOVED_PERMANENTLY.value() || statusCode == HttpStatus.MOVED_TEMPORARILY.value()    ) {
                
                Header location = deleteMethod.getResponseHeader("Location");
                String taskId = HbDirectorRestHelper.getTaskId(location.getValue());
                HbDirectorRestHelper.trackToTask(directorInfo, messagingTemplate, MESSAGE_ENDPOINT, httpClient, taskId, "event", principal.getName());
                
            } else {
                HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("스템셀 삭제 중 오류가 발생하였습니다."));
            }
        } catch ( RuntimeException e) {
            HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("스템셀 삭제 중 Exception이 발생하였습니다."));
        } catch ( Exception e) {
            HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("스템셀 삭제 중 Exception이 발생하였습니다."));
        }
    }

    /***************************************************
     * @param string 
     * @project : Paas 플랫폼 설치 자동화
     * @description : 비동기 처리방식으로 deleteStemcell 메소드 호출
     * @title : deleteStemcellAsync
     * @return : void
    ***************************************************/
    @Async
    public void deleteStemcellAsync(String stemcellName, String stemcellVersion, String directorId, Principal principal) {
        deleteStemcell(stemcellName, stemcellVersion, directorId, principal);
    }    
}
