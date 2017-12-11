package org.openpaas.ieda.controller.deploy.web.information.stemcell;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.controller.common.BaseController;
import org.openpaas.ieda.deploy.web.config.stemcell.dao.StemcellManagementVO;
import org.openpaas.ieda.deploy.web.config.stemcell.service.StemcellManagementService;
import org.openpaas.ieda.deploy.web.information.stemcell.dto.StemcellDTO;
import org.openpaas.ieda.deploy.web.information.stemcell.service.StemcellDeleteAsyncService;
import org.openpaas.ieda.deploy.web.information.stemcell.service.StemcellService;
import org.openpaas.ieda.deploy.web.information.stemcell.service.StemcellUploadAsyncService;
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

@Controller
public class StemcellController extends BaseController {

    @Autowired private StemcellService service;
    @Autowired private StemcellManagementService stemcellManagementService;
    @Autowired private StemcellDeleteAsyncService stemcellDeleteService;
    @Autowired private StemcellUploadAsyncService stemcellUploadService;
    
    final private static String STEMCELL_DIR = LocalDirectoryConfiguration.getStemcellDir();
    final private static Logger LOGGER = LoggerFactory.getLogger(StemcellController.class);

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 업로드 화면을 호출하여 이동
     * @title : goListStemcell
     * @return : String
    *****************************************************************/
    @RequestMapping(value = "/info/stemcell", method = RequestMethod.GET)
    public String goListStemcell() {
        return "/deploy/information/listStemcell";
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드된 스템셀 목록 조회 
     * @title : getUploadStemcellLIst
     * @return : ResponseEntity<HashMap<String,Object>>
    *****************************************************************/
    @RequestMapping(value = "/info/stemcell/list/upload", method = RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getUploadStemcellList() {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=======================> /info/stemcell/list/upload"); }
        List<StemcellManagementVO> contents = service.getStemcellList();
        HashMap<String, Object> result = new HashMap<String, Object>();
        if ( contents != null ) {
            result.put("total", contents.size());
            result.put("records", contents);
        }
        return new ResponseEntity<HashMap<String, Object>>(result, HttpStatus.OK);
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 로컬에 다운로드된 스템셀 목록 조회
     * @title : getLocalStemcellList
     * @return : ResponseEntity<HashMap<String,Object>>
    *****************************************************************/
    @RequestMapping(value = "/info/stemcell/list/local/{iaas}", method = RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getLocalStemcellList(@PathVariable String iaas) {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=======================> /info/stemcell/list/local/"+iaas); }
        List<StemcellManagementVO> contents = stemcellManagementService.getLocalStemcellList(iaas);
        
        HashMap<String, Object> result = new HashMap<String, Object>();
        int size = 0;
        if( contents != null ) {
            size = contents.size();
        }
        result.put("total", size);
        result.put("records", contents);
        return new ResponseEntity<HashMap<String, Object>>(result, HttpStatus.OK);
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 업로드
     * @title : doUploadStemcell
     * @return : ResponseEntity<Object>
    *****************************************************************/
    @MessageMapping("/info/stemcell/upload/stemcellUploading")
    @SendTo("/info/stemcell/upload/logs")
    public ResponseEntity<Object> uploadStemcell(@RequestBody @Valid StemcellDTO.Upload dto, Principal principal) {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=======================> /info/stemcell/upload/stemcellUploading"); }
        stemcellUploadService.uploadStemcellAsync(STEMCELL_DIR, dto.getFileName(), principal.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드 된 스템셀 삭제
     * @title : doDeleteStemcell
     * @return : ResponseEntity<Object>
    *****************************************************************/
    @MessageMapping("/info/stemcell/delete/stemcellDelete")
    @SendTo("/info/stemcell/delete/logs")
    public ResponseEntity<Object> deleteStemcell(@RequestBody @Valid StemcellDTO.Delete dto, Principal principal) {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=======================> /info/stemcell/delete/stemcellDelete"); }
        stemcellDeleteService.deleteStemcellAsync(dto.getStemcellName(), dto.getVersion(), principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
