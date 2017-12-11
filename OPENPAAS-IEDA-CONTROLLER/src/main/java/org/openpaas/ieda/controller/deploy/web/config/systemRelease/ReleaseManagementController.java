package org.openpaas.ieda.controller.deploy.web.config.systemRelease;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.openpaas.ieda.controller.common.BaseController;
import org.openpaas.ieda.deploy.web.config.systemRelease.dao.ReleaseManagementVO;
import org.openpaas.ieda.deploy.web.config.systemRelease.dto.ReleaseManagementDTO;
import org.openpaas.ieda.deploy.web.config.systemRelease.service.ReleaseManagementDownloadService;
import org.openpaas.ieda.deploy.web.config.systemRelease.service.ReleaseManagementService;
import org.openpaas.ieda.deploy.web.config.systemRelease.service.ReleaseManagementUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Controller
public class ReleaseManagementController extends BaseController{

    @Autowired private ReleaseManagementService service;
    @Autowired private ReleaseManagementDownloadService downloadService;
    @Autowired private ReleaseManagementUploadService uploadService;
    
    private final static Logger LOGGER = LoggerFactory.getLogger(ReleaseManagementController.class);

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 시스템 릴리즈 관리 화면 요청
     * @title : goReleaseManagement
     * @return : String
    *****************************************************************/
    @RequestMapping(value="/config/systemRelease", method=RequestMethod.GET)
    public String goReleaseManagement() {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /config/systemRelease"); }
        return "/deploy/config/releaseManagement";
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 시스템 릴리즈 정보 목록 조회
     * @title : getSystemReleaseList
     * @return : ResponseEntity<HashMap<String,Object>>
    *****************************************************************/
    @RequestMapping(value="/config/systemRelease/list",  method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getSystemReleaseList(){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /config/systemRelease/list"); }
        List<ReleaseManagementVO> systemReleases = service.getSystemReleaseList();
        HashMap<String, Object> list = new HashMap<String, Object>();
        list.put("total", systemReleases.size());
        list.put("records", systemReleases);
        return new ResponseEntity<HashMap<String, Object>>(list, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 릴리즈 유형 조회
     * @title : getSystemReleaseTypeList
     * @return : ResponseEntity<List<String>>
    *****************************************************************/
    @RequestMapping(value="/config/systemRelease/list/releaseType",  method=RequestMethod.GET)
    public ResponseEntity<List<String>> getSystemReleaseTypeList(){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /config/systemRelease/list/releaseType"); }
        List<String> releaseTypes = service.getSystemReleaseTypeList();
        return new ResponseEntity<List<String>>(releaseTypes, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 시스템 릴리즈 입력 정보 저장
     * @title : saveSystemReleaseInfo
     * @return : ResponseEntity<ReleaseManagementVO>
    *****************************************************************/
    @RequestMapping(value="/config/systemRelease/regist",  method=RequestMethod.POST)
    public ResponseEntity<ReleaseManagementVO> saveSystemReleaseInfo(@RequestBody ReleaseManagementDTO.Regist dto, Principal principal){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /config/systemRelease/regist"); }
        ReleaseManagementVO result = null;
        if("file".equalsIgnoreCase(dto.getFileType())){
            result = service.saveSystemReleaseFileUploadInfo(dto, principal);
        }else {
            result = service.saveSystemReleaseUrlInfo(dto, principal);
        }
        return new ResponseEntity<ReleaseManagementVO>(result, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 원격지에 있는 릴리즈 다운로드
     * @title : doSystemReleaseDonwload
     * @return : ResponseEntity<?>
    *****************************************************************/
    @MessageMapping("/config/systemRelease/regist/download/releaseDownloading")
    @SendTo("/config/systemRelease/regist/socket/logs")
    public ResponseEntity<?> doSystemReleaseDownload(@RequestBody @Valid ReleaseManagementDTO.Regist dto, Principal principal){
    
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /config/systemRelease/regist/download/releaseDownloading"); }
            downloadService.releaseDownloadAsync(dto, principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 릴리즈 파일 업로드
     * @title : doSystemReleaseUpload
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value="/config/systemRelease/regist/upload",  method=RequestMethod.POST)
    public ResponseEntity<?> doSystemReleaseUpload( MultipartHttpServletRequest request, Principal principal){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /config/systemRelease/regist/upload"); }
        uploadService.uploadReleaseFile(request, principal);
        
        return new ResponseEntity<>(HttpStatus.OK);
        
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 시스템 릴리즈 삭제
     * @title : systemRelaseDelete
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value="/config/systemRelease/delete",  method=RequestMethod.DELETE)
    public ResponseEntity<?> systemRelaseDelete(@RequestBody ReleaseManagementDTO.Delete dto ){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /config/systemRelease/delete"); }
        service.deleteSystemRelease(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
}
