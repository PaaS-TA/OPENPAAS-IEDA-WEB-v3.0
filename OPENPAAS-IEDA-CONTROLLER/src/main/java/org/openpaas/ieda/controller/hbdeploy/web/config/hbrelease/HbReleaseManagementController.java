package org.openpaas.ieda.controller.hbdeploy.web.config.hbrelease;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.openpaas.ieda.controller.common.BaseController;
import org.openpaas.ieda.hbdeploy.web.config.release.dao.HbReleaseManagementVO;
import org.openpaas.ieda.hbdeploy.web.config.release.dto.HbReleaseManagementDTO;
import org.openpaas.ieda.hbdeploy.web.config.release.service.HbReleaseManagementDownloadAsyncService;
import org.openpaas.ieda.hbdeploy.web.config.release.service.HbReleaseManagementService;
import org.openpaas.ieda.hbdeploy.web.config.release.service.HbReleaseManagementUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Controller
public class HbReleaseManagementController extends BaseController{

    @Autowired HbReleaseManagementService service;
    @Autowired HbReleaseManagementDownloadAsyncService downloadService;
    @Autowired HbReleaseManagementUploadService uploadService;
    
    private final static Logger LOGGER = LoggerFactory.getLogger(HbReleaseManagementController.class);
    
    /****************************************************************
     * @project : 이종 플랫폼 설치 자동화
     * @description : Hybrid 릴리즈 관리 화면 요청
     * @title : goReleaseManagement
     * @return : String
    *****************************************************************/
    @RequestMapping(value="/config/hbRelease", method=RequestMethod.GET)
    public String goReleaseManagement() {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /config/hbReleaseManagement"); }
        return "/hbdeploy/config/hbReleaseManagement";
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : IaaS 별 이종 릴리즈 목록 조회
     * @title : getHybridReleaseList
     * @return : ResponseEntity<HashMap<String,Object>>
    *****************************************************************/
    @RequestMapping(value="/config/hbRelease/list/{iaasType}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getHybridReleaseList(@PathVariable String iaasType){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=================================> /hbconfig/hbRelease/list/"+iaasType); }
        HashMap<String, Object> list = new HashMap<String, Object>();
        List<HbReleaseManagementVO> releaseList = service.getHybridReleaseList(iaasType);
        list.put("records", releaseList);
        return new ResponseEntity<HashMap<String,Object>>(list, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : hybrid 릴리즈 입력 정보 저장
     * @title : saveHybridReleaseInfo
     * @return : ResponseEntity<HbReleaseManagementVO>
    *****************************************************************/
    @RequestMapping(value="/config/hbRelease/regist",  method=RequestMethod.POST)
    public ResponseEntity<HbReleaseManagementVO> saveHybridReleaseInfo(@RequestBody HbReleaseManagementDTO.Regist dto, Principal principal){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /config/systemRelease/regist"); }
        HbReleaseManagementVO result = null;
        if("file".equalsIgnoreCase(dto.getFileType())){
            result = service.saveHybridReleaseFileUploadInfo(dto, principal);
        }else {
            result = service.saveHybridReleaseUrlInfo(dto, principal);
        }
        return new ResponseEntity<HbReleaseManagementVO>(result, HttpStatus.OK);
    }
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 원격지에 있는 릴리즈 다운로드
     * @title : doHybridReleaseDownload
     * @return : ResponseEntity<?>
    *****************************************************************/
    @MessageMapping("/config/hbRelease/regist/download/hbReleaseDownloading")
    @SendTo("/config/hbRelease/regist/socket/logs")
    public ResponseEntity<?> doHybridReleaseDownload(@RequestBody @Valid HbReleaseManagementDTO.Regist dto, Principal principal){
    
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /config/systemRelease/regist/download/releaseDownloading"); }
            downloadService.releaseDownloadAsync(dto, principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 릴리즈 파일 업로드
     * @title : doHybridReleaseUpload
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value="/config/hbRelease/regist/upload",  method=RequestMethod.POST)
    public ResponseEntity<?> doSystemReleaseUpload( MultipartHttpServletRequest request, Principal principal){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /config/systemRelease/regist/upload"); }
        uploadService.uploadHybridReleaseFile(request, principal);
        
        return new ResponseEntity<>(HttpStatus.OK);
        
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Hybrid 릴리즈 다중 삭제
     * @title : HybridRelaseMultiDelete
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value="/config/hbRelease/multidelete",  method=RequestMethod.DELETE)
    public ResponseEntity<?> HybridReleaseMultiDelete(@RequestBody ArrayList<HbReleaseManagementDTO.Delete> list){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /config/hbRelease/multidelete"); }
        for(int i=0;i<list.size();i++){
            service.deleteHybridRelease(list.get(i));
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
}
