package org.openpaas.ieda.controller.hbdeploy.web.config.hbstemcell;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.openpaas.ieda.controller.deploy.web.config.stemcell.StemcellManagementController;
import org.openpaas.ieda.deploy.web.config.stemcell.dao.StemcellManagementVO;
import org.openpaas.ieda.deploy.web.config.stemcell.dto.StemcellManagementDTO;
import org.openpaas.ieda.hbdeploy.web.config.stemcell.dao.HbStemcellManagementVO;
import org.openpaas.ieda.hbdeploy.web.config.stemcell.dto.HbStemcellManagementDTO;
import org.openpaas.ieda.hbdeploy.web.config.stemcell.service.HbStemcellManagementDownloadAsyncService;
import org.openpaas.ieda.hbdeploy.web.config.stemcell.service.HbStemcellManagementService;
import org.openpaas.ieda.hbdeploy.web.config.stemcell.service.HbStemcellManagementUploadService;
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
public class HbStemcellManagementController {

    @Autowired private HbStemcellManagementService service;
    @Autowired private HbStemcellManagementUploadService uploadService;
    @Autowired private HbStemcellManagementDownloadAsyncService downloadService;

    private final static Logger LOGGER = LoggerFactory.getLogger(StemcellManagementController.class);
    
	/****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 이종 스템셀 관리 화면 이동
     * @title : goHbStemcellManagement
     * @return : String
    *****************************************************************/
    @RequestMapping(value="/config/hbstemcell", method=RequestMethod.GET)
    public String goHbStemcellManagement() {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /config/hbstemcell"); }
        return "/hbdeploy/config/hbStemcellManagement";
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : IaaS 별 이종 스템셀 목록 조회
     * @title : getStemcellListDetail
     * @return : ResponseEntity<HashMap<String,Object>>
    *****************************************************************/
    @RequestMapping(value="/config/hbstemcell/list/{iaas}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getHybridStemcellListDetail(@PathVariable String iaas) {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /config/stemcell/list/"+iaas); }
        HashMap<String, Object> list = new HashMap<String, Object>();
        List<HbStemcellManagementVO> stemcellList = service.getHybridStemcellList(iaas);
        list.put("records", stemcellList);
        return new ResponseEntity<HashMap<String,Object>>(list, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 이종 스템셀 입력 정보 저장 및 다운로드
     * @title : savePublicStemcellInfo
     * @return : ResponseEntity<StemcellManagementVO>
    *****************************************************************/
    @RequestMapping(value="/config/hbstemcell/regist/info/{testFlag}",  method=RequestMethod.POST)
    public ResponseEntity<StemcellManagementVO> saveHybridStemcellInfo(@RequestBody StemcellManagementDTO.Regist dto, @PathVariable String testFlag, Principal principal ){
        
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
     * @description : Hybrid 스템셀 다중 삭제
     * @title : publicStemcellMultiDelete
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value="/config/hbstemcell/Muldelete",  method=RequestMethod.DELETE)
    public ResponseEntity<?> HybridStemcellMultiDelete(@RequestBody ArrayList<HbStemcellManagementDTO.Delete> list){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /config/stemcell/deletePublicStemcell"); }
        for(int i=0;i<list.size();i++){
            service.deleteHybridStemcell(list.get(i));
        }
        
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 원격지에 있는 스템셀 다운로드
     * @title : doHybridStemcellDonwload
     * @return : ResponseEntity<?>
    *****************************************************************/
    @MessageMapping("/config/hbstemcell/regist/stemcellDownloading")
    @SendTo("/config/hbstemcell/regist/socket/logs")
    public ResponseEntity<?> doHybridStemcellDownload(@RequestBody @Valid HbStemcellManagementDTO.Regist dto, Principal principal){
        
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /config/hbstemcell/regist/stemcellDownloading"); }
        downloadService.stemcellDownloadAsync(dto, principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 로컬 스템셀 파일 업로드
     * @title : doFileUploadHybridStemcell
     * @return : ResponseEntity<Object>
    *****************************************************************/
    @RequestMapping(value="/config/hbstemcell/regist/upload", method=RequestMethod.POST)
    public ResponseEntity<Object> doFileUploadHybridStemcell(MultipartHttpServletRequest request, Principal principal){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /config/hbstemcell/regist/upload"); }
        uploadService.uploadStemcellFile(request, principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
