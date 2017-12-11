package org.openpaas.ieda.controller.deploy.web.deploy.servicepack;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.openpaas.ieda.controller.common.BaseController;
import org.openpaas.ieda.deploy.web.deploy.servicepack.dao.ServicePackVO;
import org.openpaas.ieda.deploy.web.deploy.servicepack.dto.ServicePackParamDTO;
import org.openpaas.ieda.deploy.web.deploy.servicepack.service.ServicePackDeleteDeployAsyncService;
import org.openpaas.ieda.deploy.web.deploy.servicepack.service.ServicePackDeployAsyncService;
import org.openpaas.ieda.deploy.web.deploy.servicepack.service.ServicePackService;
import org.openpaas.ieda.deploy.web.information.manifest.dao.ManifestVO;
import org.openpaas.ieda.deploy.web.information.manifest.service.ManifestService;
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
public class ServicePackController extends BaseController{
    @Autowired ServicePackService service;
    @Autowired ManifestService manifestService;
    @Autowired ServicePackDeployAsyncService servicePackDeployAsyncService;
    @Autowired ServicePackDeleteDeployAsyncService servicePackDeleteDeployAsyncService;
    
    private final static Logger LOGGER = LoggerFactory.getLogger(ServicePackController.class);
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스팩 화면 이동
     * @title : goServicePack
     * @return : String
    ***************************************************/
    @RequestMapping(value="/deploy/servicePack", method=RequestMethod.GET)
    public String goServicePack(){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /deploy/servicePack"); }
        return "/deploy/deploy/servicepack/servicePack";
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스팩 전체 목록 조회
     * @title : getServicePackList
     * @return : ResponseEntity<HashMap<String,Object>>
    ***************************************************/
    @RequestMapping(value="/deploy/servicePack/list/{iaas}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getServicePackList(@PathVariable String iaas) {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> /deploy/servicePack/list/"+iaas); }
        List<ServicePackVO> content = service.getServicePackList(iaas);
        
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("total", (content == null) ? 0:content.size());
        result.put("records", content);
        
        return new ResponseEntity<HashMap<String, Object>>(result, HttpStatus.OK);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드된 Manifest 조회 
     * @title : getManifestList
     * @return : ResponseEntity<HashMap<String,Object>>
    ***************************************************/
    @RequestMapping(value="/deploy/servicePack/list/manifest", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getManifestList() {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> /deploy/servicePack/list/manifest"); }
        List<ManifestVO> content = manifestService.getManifestList();
        
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("total", (content == null) ? 0:content.size());
        result.put("records", content);
        
        return new ResponseEntity<HashMap<String, Object>>(result, HttpStatus.OK);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스팩 정보 저장
     * @title : saveServicePackInfo
     * @return : ResponseEntity<ServicePackVO>
    ***************************************************/
    @RequestMapping(value="/deploy/servicePack/install/saveServicePackinfo", method=RequestMethod.POST)
    public ResponseEntity<ServicePackVO> saveServicePackInfo(@RequestBody @Valid ServicePackParamDTO dto, Principal principal){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> /deploy/servicePack/install/saveServicePackinfo"); }
        ServicePackVO vo = service.saveServicePackInfo(dto,principal);
        return new ResponseEntity<ServicePackVO>(vo,HttpStatus.CREATED);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스팩 배포 파일 생성
     * @title : makeDeploymentFile
     * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value="/deploy/servicePack/install/createSettingFile/{id}", method=RequestMethod.POST)
    public ResponseEntity<?> makeDeploymentFile(@PathVariable int id){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> /deploy/servicePack/install/createSettingFile/"+id); }
        //Manifest file Create
        service.makeDeploymentFile(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스 팩 설치
     * @title : servicePackInstall
     * @return : ResponseEntity<?>
    ***************************************************/
    @MessageMapping("/deploy/servicePack/install/servicepackInstall")
    @SendTo("/deploy/servicePack/install/logs")
    public ResponseEntity<?> servicePackInstall(@RequestBody @Valid ServicePackParamDTO dto, Principal principal ){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> /deploy/servicePack/install/servicepackInstall"); }
        servicePackDeployAsyncService.deployAsync(dto, principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스팩 단순 레코드 삭제
     * @title : deleteJustOnlyServicePackRecord
     * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping( value="/deploy/servicePack/delete/data", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteJustOnlyServicePackRecord(@RequestBody @Valid ServicePackParamDTO dto){
        if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> /deploy/servicePack/delete/data"); }
        service.deleteServicePackInfoRecord(dto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스팩 플랫폼 삭제 요청
     * @title : servicePackDelete
     * @return : ResponseEntity<?>
    ***************************************************/
    @MessageMapping("/deploy/servicePack/delete/instance")
    @SendTo("/deploy/servicePack/delete/logs")
    public ResponseEntity<?> servicePackDelete(@RequestBody @Valid ServicePackParamDTO dto, Principal principal){
        if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> /deploy/servicePack/delete/instance"); }
        servicePackDeleteDeployAsyncService.deleteDeployAsync(dto,principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스팩 검색
     * @title : searchManifestList
     * @return : ResponseEntity<HashMap<String,Object>>
    ***************************************************/
    @RequestMapping(value="/deploy/servicePack/list/manifest/search/{searchVal}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> searchManifestList(@PathVariable String searchVal) {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> /deploy/servicePack/list/manifest/search/"+searchVal); }
        List<ManifestVO> content = manifestService.searchManifestList(searchVal);
        
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("total", (content == null) ? 0:content.size());
        result.put("records", content);
        
        return new ResponseEntity<HashMap<String, Object>>(result, HttpStatus.OK);
    }
}
