package org.openpaas.ieda.controller.deploy.web.deploy.cf;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.openpaas.ieda.controller.common.BaseController;
import org.openpaas.ieda.deploy.web.common.dto.KeyInfoDTO;
import org.openpaas.ieda.deploy.web.deploy.cf.dao.CfVO;
import org.openpaas.ieda.deploy.web.deploy.cf.dto.CfListDTO;
import org.openpaas.ieda.deploy.web.deploy.cf.dto.CfParamDTO;
import org.openpaas.ieda.deploy.web.deploy.cf.service.CfDeleteDeployAsyncService;
import org.openpaas.ieda.deploy.web.deploy.cf.service.CfDeployAsyncService;
import org.openpaas.ieda.deploy.web.deploy.cf.service.CfSaveService;
import org.openpaas.ieda.deploy.web.deploy.cf.service.CfService;
import org.openpaas.ieda.deploy.web.deploy.common.dao.network.NetworkVO;
import org.openpaas.ieda.deploy.web.deploy.common.dto.network.NetworkDTO;
import org.openpaas.ieda.deploy.web.deploy.common.dto.resource.ResourceDTO;
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
public class CfController extends BaseController{
    
    @Autowired private CfService cfService;
    @Autowired private CfSaveService cfSaveService;
    @Autowired private CfDeployAsyncService cfDeployAsyncService;
    @Autowired private CfDeleteDeployAsyncService cfDeleteDeployAsyncService;
    
    final private static Logger LOGGER = LoggerFactory.getLogger(CfController.class);
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 설치 화면 이동
     * @title : goCf
     * @return : String
    *****************************************************************/
    @RequestMapping(value = "/deploy/cf", method=RequestMethod.GET)
    public String goCf() {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> /deploy/cf"); }
        return "/deploy/deploy/cf/cf";
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 설치 팝업 화면 호출
     * @title : goCfPopup
     * @return : String
    *****************************************************************/
    @RequestMapping(value = "/deploy/cf/install/cfPopup", method=RequestMethod.GET)
    public String goCfPopup() {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> goCfPopup"); }
        return "/deploy/deploy/cf/cfPopup";
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 정보 목록 조회 
     * @title : getCfLIst
     * @return : ResponseEntity<Map<String,Object>>
    *****************************************************************/
    @RequestMapping(value="/deploy/cf/list/{iaas}", method=RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getCfLIst(@PathVariable String iaas) {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> /deploy/cf/list/" + iaas); }
        List<CfListDTO> content = cfService.getCfLIst(iaas.toLowerCase(),"cf");

        Map<String, Object> result = new HashMap<>();
        result.put("total", (content == null) ? 0:content.size());
        result.put("records", content);
        
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 정보 상세 조회
     * @title : getCfInfo
     * @return : ResponseEntity<Map<String,Object>>
    *****************************************************************/
    @RequestMapping(value="/deploy/cf/install/detail/{id}", method=RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getCfInfo(@PathVariable int id){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> /deploy/cf/install/detail/{id}"); }
        CfVO vo = cfService.getCfInfo(id);
        Map<String, Object> result =  new HashMap<>();
        result.put("content", vo);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 기본 정보 저장 
     * @title : saveDefaultInfo
     * @return : ResponseEntity<Map<String,Object>>
    *****************************************************************/
    @RequestMapping(value="/deploy/cf/install/saveDefaultInfo", method=RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> saveDefaultInfo(@RequestBody @Valid CfParamDTO.Default dto, Principal principal){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> /deploy/cf/install/saveDefaultInfo"); }
        CfVO vo = cfSaveService.saveDefaultInfo(dto, principal);
        Map<String, Object> result  = new HashMap<>();
        result.put("content", vo);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 네트워크 정보 조회
     * @title : saveNetworkCfInfo
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value="/deploy/cf/install/saveNetworkInfo/networks/list/{id}/{deployType}", method=RequestMethod.GET)
    public ResponseEntity<List<NetworkVO>> getNetowrkListInfo( @PathVariable int id, @PathVariable String deployType ){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> /deploy/cf/install/saveNetworkInfo"); }
        List<NetworkVO> networks = cfService.getNetowrkListInfo(id, deployType);
        
        return new ResponseEntity<List<NetworkVO>>(networks,HttpStatus.OK);
    }
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 네트워크 정보 저장 
     * @title : saveNetworkCfInfo
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value="/deploy/cf/install/saveNetworkInfo", method=RequestMethod.PUT)
    public ResponseEntity<?> saveNetworkCfInfo(@RequestBody @Valid List<NetworkDTO> dto, Principal principal){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> /deploy/cf/install/saveNetworkInfo"); }
        cfSaveService.saveNetworkInfo(dto, principal);
        
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : KEY 생성 정보 저장
     * @title : saveKeyInfo
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value="/deploy/cf/install/saveKeyInfo", method=RequestMethod.PUT)
    public ResponseEntity<?> saveKeyInfo(@RequestBody @Valid KeyInfoDTO dto, Principal principal){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> deploy/cf/install/saveKeyInfo"); }
        cfSaveService.saveKeyInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 리소스 정보 저장
     * @title : saveResourceCfInfo
     * @return : ResponseEntity<Map<String,Object>>
    *****************************************************************/
    @RequestMapping(value="/deploy/cf/install/saveResourceInfo", method=RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> saveResourceCfInfo(@RequestBody @Valid ResourceDTO dto,Principal principal){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> /deploy/cf/install/saveResourceInfo "); }
        HashMap<String, Object> map = cfSaveService.saveResourceInfo(dto,principal);
        return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 배포 파일 생성
     * @title : makeDeploymentFile
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value="/deploy/cf/install/createSettingFile", method=RequestMethod.POST)
    public ResponseEntity<?> makeDeploymentFile(@RequestBody CfParamDTO.Install dto){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> /deploy/cf/install/createSettingFile"); }
        CfVO vo = cfService.getCfInfo( Integer.parseInt(dto.getId()) );
        cfService.createSettingFile(vo);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 플랫폼 설치
     * @title : installCf
     * @return : ResponseEntity<?>
    *****************************************************************/
    @MessageMapping("/deploy/cf/install/cfInstall")
    @SendTo("/deploy/cf/install/logs")
    public ResponseEntity<?> installCf(@RequestBody @Valid CfParamDTO.Install dto, Principal principal){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> /deploy/cf/install/cfInstall"); }
        cfDeployAsyncService.deployAsync(dto, principal, "cf");
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 단순 레코드 삭제 
     * @title : deleteJustOnlyCfRecord
     * @return : ResponseEntity<Object>
    *****************************************************************/
    @RequestMapping( value="/deploy/cf/delete/data", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteJustOnlyCfRecord(@RequestBody @Valid  CfParamDTO.Delete dto) { 
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> /deploy/cf/delete/data"); }
        cfService.deleteCfInfoRecord(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 플랫폼 삭제 요청
     * @title : deleteCf
     * @return : ResponseEntity<?>
    *****************************************************************/
    @MessageMapping("/deploy/cf/delete/instance")
    @SendTo("/deploy/cf/delete/logs")
    public ResponseEntity<?> deleteCf(@RequestBody @Valid CfParamDTO.Delete dto, Principal principal){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> /deploy/cf/delete/instance"); }
        cfDeleteDeployAsyncService.deleteDeployAsync(dto, "cf", principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 릴리즈 버전 및 인프라 별 JOb 목록 조회
     * @title : getCfJobList
     * @return : ResponseEntity<List<HashMap<String, String>>>
    *****************************************************************/
    @RequestMapping(value="/deploy/cf/install/save/job/list/{version}/{deployType}", method=RequestMethod.GET)
    public ResponseEntity<List<HashMap<String, String>>> getCfJobList(@PathVariable String version, @PathVariable String deployType){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> /deploy/cf/install/save/job/list/"+version); }
        List<HashMap<String, String>> list = cfService.getJobTemplateList(deployType, version);
        return new ResponseEntity<List<HashMap<String, String>>>(list, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 고급설정 정보 저장
     * @title : saveCfJobsInfo
     * @return : ResponseEntity<List<HashMap<String,String>>>
    *****************************************************************/
    @RequestMapping(value="/deploy/cf/install/save/jobsInfo", method=RequestMethod.PUT)
    public ResponseEntity<List<HashMap<String, String>>> saveCfJobsInfo(@RequestBody List<HashMap<String, String>> maps, Principal principal){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> /deploy/cf/install/save/jobsInfo"); }
        cfSaveService.saveCfJobsInfo(maps, principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}