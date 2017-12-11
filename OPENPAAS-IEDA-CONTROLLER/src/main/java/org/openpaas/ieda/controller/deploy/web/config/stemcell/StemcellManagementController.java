package org.openpaas.ieda.controller.deploy.web.config.stemcell;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.openpaas.ieda.controller.common.BaseController;
import org.openpaas.ieda.deploy.web.config.stemcell.dao.StemcellManagementVO;
import org.openpaas.ieda.deploy.web.config.stemcell.dto.StemcellManagementDTO;
import org.openpaas.ieda.deploy.web.config.stemcell.service.StemcellManagementDownloadAsyncService;
import org.openpaas.ieda.deploy.web.config.stemcell.service.StemcellManagementService;
import org.openpaas.ieda.deploy.web.config.stemcell.service.StemcellManagementUploadService;
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
public class StemcellManagementController extends BaseController {
    
    @Autowired private StemcellManagementService service;
    @Autowired private StemcellManagementUploadService uploadService;
    @Autowired private StemcellManagementDownloadAsyncService donwonloadService;
    
    private final static Logger LOGGER = LoggerFactory.getLogger(StemcellManagementController.class);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 관리 화면 이동
     * @title : goStemcellManagement
     * @return : String
    *****************************************************************/
    @RequestMapping(value="/config/stemcell", method=RequestMethod.GET)
    public String goStemcellManagement() {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /config/stemcell"); }
        return "/deploy/config/stemcellManagement";
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 목록 조회
     * @title : getPublicStemcells
     * @return : ResponseEntity<HashMap<String,Object>>
    *****************************************************************/
    @RequestMapping(value="/config/stemcell/list", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getStemcellList() {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /config/stemcell/lis"); }
        HashMap<String, Object> list = new HashMap<String, Object>();
        
        List<StemcellManagementVO> stemcellList = service.getPublicStemcellList();
        list.put("records", stemcellList);
        
        return new ResponseEntity<HashMap<String, Object> >(list, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 입력 정보 저장
     * @title : publicStemcellRegist
     * @return : ResponseEntity<StemcellManagementVO>
    *****************************************************************/
    @RequestMapping(value="/config/stemcell/regist/info/{testFlag}",  method=RequestMethod.POST)
    public ResponseEntity<StemcellManagementVO> savePublicStemcellInfo(@RequestBody StemcellManagementDTO.Regist dto, @PathVariable String testFlag, Principal principal ){
        
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /config/stemcell/regist/info/"+testFlag); }
        StemcellManagementVO result = null;
        if("file".equalsIgnoreCase(dto.getFileType())){
            result = service.saveStemcellInfoByFilePath(dto, testFlag, principal);
        }else{
            result = service.saveStemcellInfoByURL(dto, testFlag, principal);
        }
        
        return new ResponseEntity<StemcellManagementVO>(result, HttpStatus.CREATED);
    }
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 파일 업로드
     * @title : doPublicStemcellUpload
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value="/config/stemcell/regist/upload",  method=RequestMethod.POST)
    public ResponseEntity<?> doPublicStemcellUpload( MultipartHttpServletRequest request, Principal principal ){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /config/stemcell/regist/upload"); }
        uploadService.uploadStemcellFile(request, principal);
        return new ResponseEntity<>(HttpStatus.OK);
        
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 원격지에 있는 스템셀 다운로드
     * @title : doPublicStemcellDonwload
     * @return : ResponseEntity<?>
    *****************************************************************/
    @MessageMapping("/config/stemcell/regist/stemcellDownloading")
    @SendTo("/config/stemcell/regist/socket/logs")
    public ResponseEntity<?> doPublicStemcellDonwload(@RequestBody @Valid StemcellManagementDTO.Regist dto, Principal principal){
    
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /config/stemcell/regist/stemcellDownloading"); }
        donwonloadService.stemcellDownloadAsync(dto, principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 파일 및 정보 삭제
     * @title : publicStemcellDelete
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value="/config/stemcell/delete",  method=RequestMethod.DELETE)
    public ResponseEntity<?> publicStemcellDelete(@RequestBody StemcellManagementDTO.Delete dto ){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /config/stemcell/deletePublicStemcell"); }
        service.deletePublicStemcell(dto);
        
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
}
