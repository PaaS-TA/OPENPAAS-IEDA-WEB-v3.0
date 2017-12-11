package org.openpaas.ieda.controller.deploy.web.deploy.cfDiego;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.web.common.dto.KeyInfoDTO;
import org.openpaas.ieda.deploy.web.deploy.cf.dto.CfListDTO;
import org.openpaas.ieda.deploy.web.deploy.cf.dto.CfParamDTO;
import org.openpaas.ieda.deploy.web.deploy.cf.service.CfDeployAsyncService;
import org.openpaas.ieda.deploy.web.deploy.cf.service.CfSaveService;
import org.openpaas.ieda.deploy.web.deploy.cf.service.CfService;
import org.openpaas.ieda.deploy.web.deploy.cfDiego.dao.CfDiegoVO;
import org.openpaas.ieda.deploy.web.deploy.cfDiego.dto.CfDiegoParamDTO;
import org.openpaas.ieda.deploy.web.deploy.cfDiego.service.CfDiegoSaveService;
import org.openpaas.ieda.deploy.web.deploy.cfDiego.service.CfDiegoService;
import org.openpaas.ieda.deploy.web.deploy.common.dto.network.NetworkDTO;
import org.openpaas.ieda.deploy.web.deploy.common.dto.resource.ResourceDTO;
import org.openpaas.ieda.deploy.web.deploy.diego.dto.DiegoParamDTO;
import org.openpaas.ieda.deploy.web.deploy.diego.service.DiegoDeployAsyncService;
import org.openpaas.ieda.deploy.web.deploy.diego.service.DiegoSaveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.Gson;

@Controller
public class CfDiegoController {
    
    @Autowired  CfDiegoService cfDiegoService;
    @Autowired  CfDiegoSaveService cfDiegoSaveService;
    @Autowired  CfSaveService cfSaveService;
    @Autowired  DiegoSaveService diegoSaveService;
    @Autowired  CfDeployAsyncService cfDeployAsyncService;
    @Autowired  DiegoDeployAsyncService diegoDeployAsyncService;
    @Autowired  CfService cfService;
    
    final private static Logger LOGGER= LoggerFactory.getLogger(CfDiegoController.class);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF & Diego 통합 설치 화면 이동
     * @title : goCfDiego
     * @return : String
    *****************************************************************/
    @RequestMapping(value = "/deploy/cfDiego", method=RequestMethod.GET)
    public String goCfDiego() {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF & Diego 통합 설치 화면 요청"); }
        return "/deploy/deploy/cfDiego";
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 설치 팝업 화면 호출
     * @title : goCfPopup
     * @return : String
    *****************************************************************/
    @RequestMapping(value = "/deploy/cfDiego/install/cfPopup", method=RequestMethod.GET)
    public String goCfPopup() {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF 설치 팝업 화면 요청"); }
        return "/deploy/deploy/cf/cfPopup";
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Diego 설치 팝업 화면 호출
     * @title : goDiegoPopup
     * @return : String
    *****************************************************************/
    @RequestMapping(value = "/deploy/cfDiego/install/diegoPopup", method=RequestMethod.GET)
    public String goDiegoPopup() {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> Diego  설치 팝업 화면 요청"); }
        return "/deploy/deploy/diego/diegoPopup";
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF & Dieg 목록 조회
     * @title : getCfDiegoLIst
     * @return : ResponseEntity<Map<String,Object>>
    *****************************************************************/
    @RequestMapping(value="/deploy/cfDiego/list/{iaas}", method=RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getCfDiegoLIst(@PathVariable String iaas) {
        
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF & Diego 정보 목록 조회 요청"); }
        List<CfDiegoVO> content = cfDiegoService.getCfDiegoList(iaas.toLowerCase());
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("records", content);
        
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  CF & Diego 정보 상세 조회
     * @title : getCfDiegoInfo
     * @return : ResponseEntity<Map<String,Object>>
    *****************************************************************/
    @RequestMapping(value="/deploy/cfDiego/install/detail/{id}", method=RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getCfDiegoInfo(@PathVariable int id) {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF & Diego  정보 상세 조회 요청"); }
        CfDiegoVO vo = cfDiegoService.getCfDiegoInfo(id);
        Map<String, Object> result =  new HashMap<>();
        result.put("content", vo);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : IaaS에 따른 CF 정보 목록 조회 
     * @title : getCfDeploymentLst
     * @return : ResponseEntity<Map<String,Object>>
    *****************************************************************/
    @RequestMapping(value="/deploy/cfDiego/list/cf/{iaas}", method=RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getCfDeploymentList(@PathVariable String iaas) {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF 정보 목록 조회 요청"); }
        List<CfListDTO> content = cfService.getCfLIst(iaas.toLowerCase(), "cfDiego");
        Map<String, Object> result = new HashMap<>();
        
        result.put("total", (content == null) ? 0:content.size());
        result.put("records", content);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 기본 정보 저장
     * @title : saveDefaultInfo
     * @return : ResponseEntity<Map<String,Object>>
    *****************************************************************/
    @RequestMapping(value="/deploy/cfDiego/install/saveDefaultInfo", method=RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> saveDefaultInfo(@RequestBody @Valid CfDiegoParamDTO.Default dto, Principal principal){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF & Diego 기본 정보 저장 요청"); }
        CfDiegoVO vo =  cfDiegoSaveService.saveDefaultInfo( dto, principal );
        Map<String, Object> result  = new HashMap<String, Object>();
        result.put("content", vo);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 키 생성 정보 저장
     * @title : saveKeyInfo
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value="/deploy/cfDiego/install/saveKeyInfo", method=RequestMethod.PUT)
    public ResponseEntity<?> saveKeyInfo(@RequestBody @Valid KeyInfoDTO dto, Principal principal){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF 키 생성 정보 저장 요청"); }
        cfSaveService.saveKeyInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 네트워크 정보 저장 
     * @title : saveNetworkCfInfo
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value="/deploy/cfDiego/install/saveNetworkInfo", method=RequestMethod.PUT)
    public ResponseEntity<?> saveNetworkCfInfo(@RequestBody @Valid List<NetworkDTO> dto, Principal principal){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF & Diego 네트워크 정보 저장 요청"); }
        cfDiegoSaveService.saveNetworkInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 리소스 정보 저장 
     * @title : saveResourceInfo
     * @return : ResponseEntity<Map<String,Object>>
    *****************************************************************/
    @RequestMapping(value="/deploy/cfDiego/install/saveResourceInfo", method=RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> saveResourceInfo(@RequestBody @Valid ResourceDTO dto, Principal principal){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF & Diego 리소스 정보 저장 요청"); }
        Map<String, Object> result = cfDiegoSaveService.saveResourceInfo(dto, principal);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 고급 설정 정보 저장
    * @title : saveCfJobsInfo
    * @return : ResponseEntity<List<HashMap<String, String>>>
    ***************************************************/
    @RequestMapping(value="/deploy/cfDiego/install/save/jobsInfo", method=RequestMethod.PUT)
    public ResponseEntity<List<HashMap<String, String>>> saveCfJobsInfo(@RequestBody List<HashMap<String, String>> maps, Principal principal){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> /deploy/cfDiego/install/save/jobsInfo"); }
        cfSaveService.saveCfJobsInfo(maps, principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 배포 파일 생성
     * @title : makeDeploymentFile
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value="/deploy/cfDiego/install/createSettingFile", method=RequestMethod.POST)
    public ResponseEntity<?> makeDeploymentFile(@RequestBody CfDiegoParamDTO.Install dto){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> CF & Diego 배포 파일 생성 및 정보 저장 요청"); }
        //Manifest file Create
        cfDiegoService.createSettingFile(dto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 설치
     * @title : installCf
     * @return : ResponseEntity<?>
    *****************************************************************/
    @MessageMapping("/deploy/cfDiego/install/cfInstall")
    @SendTo("/deploy/cfDiego/install/cfLogs")
    public ResponseEntity<?> installCf(@RequestBody @Valid CfDiegoParamDTO.Install dto, Principal principal){
        
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF & Diego  플랫폼 설치 요청"); }
        ObjectMapper mapper = new ObjectMapper();
        String json =  new Gson().toJson(dto);
        try{
            CfParamDTO.Install cfDto = mapper.readValue(json, CfParamDTO.Install.class);
            cfDeployAsyncService.deployAsync(cfDto, principal, "cfDiego");
        }catch (IOException e){
            throw new CommonException("notfound.cfDiego.exception",
                    "CF & DIEGO 설치 정보를 읽어올 수 없습니다. ", HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Diego 설치
     * @title : installDiego
     * @return : ResponseEntity<?>
    *****************************************************************/
    @MessageMapping("/deploy/cfDiego/install/diegoInstall")
    @SendTo("/deploy/cfDiego/install/diegoLogs")
    public ResponseEntity<?> installDiego(@RequestBody @Valid CfDiegoParamDTO.Install dto, Principal principal){
        
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF & Diego  플랫폼 설치 요청"); }
        ObjectMapper mapper = new ObjectMapper();
        String json =  new Gson().toJson(dto);
        try{
            DiegoParamDTO.Install diegoDto = mapper.readValue(json, DiegoParamDTO.Install.class);
            diegoDeployAsyncService.deployAsync(diegoDto, principal, "cfDiego");
        }catch (IOException e){
            throw new CommonException("notfound.cfDiego.exception",
                    "CF & DIEGO 설치 정보를 읽어올 수 없습니다. ", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF & Diego  단순 레코드 삭제 
     * @title : deleteJustOnlyCfDiegoRecord
     * @return : ResponseEntity<Object>
    *****************************************************************/
    @RequestMapping( value="/deploy/cfDiego/delete/data", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteJustOnlyCfDiegoRecord(@RequestBody @Valid CfDiegoParamDTO.Delete dto){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF & Diego 단순 레코드 삭제 요청"); }
        cfDiegoService.deleteCfDiegoInfoRecord(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF & Diego 플랫폼 삭제
     * @title : deleteCfDiego
     * @return : ResponseEntity<?>
    *****************************************************************/
    @MessageMapping("/deploy/cfDiego/delete/instance")
    @SendTo("/deploy/cfDiego/delete/logs")
    public ResponseEntity<?> deleteCfDiego(@RequestBody @Valid CfDiegoParamDTO.Delete dto, Principal principal){
        
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> CF & Diego 플랫폼 삭제 요청 요청"); }
        cfDiegoService.deleteCfDiego(dto, principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
