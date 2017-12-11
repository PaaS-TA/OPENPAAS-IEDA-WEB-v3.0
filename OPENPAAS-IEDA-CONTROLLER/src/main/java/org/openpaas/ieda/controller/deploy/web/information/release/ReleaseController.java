package org.openpaas.ieda.controller.deploy.web.information.release;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.openpaas.ieda.controller.common.BaseController;
import org.openpaas.ieda.deploy.api.release.ReleaseInfoDTO;
import org.openpaas.ieda.deploy.web.config.systemRelease.dao.ReleaseManagementVO;
import org.openpaas.ieda.deploy.web.config.systemRelease.service.ReleaseManagementService;
import org.openpaas.ieda.deploy.web.information.release.dto.ReleaseContentDTO;
import org.openpaas.ieda.deploy.web.information.release.service.ReleaseDeleteAsyncService;
import org.openpaas.ieda.deploy.web.information.release.service.ReleaseService;
import org.openpaas.ieda.deploy.web.information.release.service.ReleaseUploadAsyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ReleaseController extends BaseController {
    
    @Autowired private ReleaseService releaseService;
    @Autowired private ReleaseUploadAsyncService releaseUploadService;
    @Autowired private ReleaseDeleteAsyncService releaseDeleteService;
    @Autowired private ReleaseManagementService systemReleaseService;
    
    final private static Logger LOGGER = LoggerFactory.getLogger(ReleaseController.class);
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 릴리즈 업로드 화면을 호출하여 이동
     * @title : goListRelease
     * @return : String
    ***************************************************/
    @RequestMapping(value="/info/release", method=RequestMethod.GET)
    public String goListRelease() {
        return "/deploy/information/listRelease";
    }
    
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드된 릴리즈 정보 목록 조회
     * @title : getUploadReleaseList
     * @return : ResponseEntity<HashMap<String,Object>>
    ***************************************************/
    @RequestMapping( value="/info/release/list/upload", method =RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getUploadReleaseList(){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("====================> /info/release/list/upload"); }
        
        List<ReleaseInfoDTO> contents = releaseService.getUploadedReleaseList();
        HashMap<String, Object> result = new HashMap<String, Object>();
        int size = 0;
        if ( contents != null ) {
            size = contents.size();
        }
        result.put("total", size);
        result.put("records", contents);
        return new ResponseEntity<HashMap<String, Object>>( result, HttpStatus.OK);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 로컬에 다운로드된 릴리즈 정보 목록 조회
     * @title : getLocalReleaseList
     * @return : ResponseEntity<HashMap<String,Object>>
    ***************************************************/
    @RequestMapping( value="/info/release/list/local", method =RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getLocalReleaseList(){
        
        if(LOGGER.isInfoEnabled()){ LOGGER.info("====================> /info/release/list/local"); }
        List<ReleaseManagementVO> contents = systemReleaseService.getSystemReleaseList();
        HashMap<String, Object> result = new HashMap<String, Object>();
        int size=0;
        if ( contents != null ) {
            size= contents.size();
        }
        result.put("total", size);
        result.put("records", contents);
        return new ResponseEntity<HashMap<String, Object>>( result, HttpStatus.OK);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 릴리즈 업로드
     * @title : uploadRelease
     * @return : ResponseEntity<Object>
    ***************************************************/
    @MessageMapping("/info/release/upload/releaseUploading")
    @SendToUser("/info/release/upload/socket/logs")
    public ResponseEntity<Object> uploadRelease(Principal p, @RequestBody @Valid ReleaseContentDTO.Upload dto) {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("====================> /info/release/upload/releaseUploading"); }
        releaseUploadService.uploadReleaseAsync(dto.getFileName(), p.getName() );
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드된 릴리즈 삭제
     * @title : deleteRelease
     * @return : ResponseEntity<Object>
    ***************************************************/
    @MessageMapping("/info/release/delete/releaseDelete")
    @SendTo("/info/release/delete/socket/logs")
    public ResponseEntity<Object> deleteRelease(@RequestBody @Valid ReleaseContentDTO.Delete dto, Principal principal) {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("====================> /info/release/delete/releaseDelete"); }
        releaseDeleteService.deleteReleaseAsync(dto.getFileName(), dto.getVersion(), principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
}
