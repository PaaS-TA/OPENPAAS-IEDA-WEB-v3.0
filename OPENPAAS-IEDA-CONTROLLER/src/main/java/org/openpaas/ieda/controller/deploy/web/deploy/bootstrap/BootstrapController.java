package org.openpaas.ieda.controller.deploy.web.deploy.bootstrap;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.openpaas.ieda.controller.common.BaseController;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.dao.BootstrapVO;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.dto.BootStrapDeployDTO;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.dto.BootstrapListDTO;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.service.BootstrapDeleteDeployAsyncService;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.service.BootstrapDeployAsyncService;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.service.BootstrapSaveService;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.service.BootstrapService;
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
public class BootstrapController extends BaseController {

    @Autowired private BootstrapSaveService saveService;
    @Autowired private BootstrapService bootstrapService;
    @Autowired private BootstrapDeployAsyncService deployAsyncService;
    @Autowired private BootstrapDeleteDeployAsyncService deleteDeployService;

    private final static Logger LOGGER = LoggerFactory.getLogger(BootstrapController.class);

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 설치 화면 이동
     * @title : goBootstrap
     * @return : String
    ***************************************************/
    @RequestMapping(value = "/deploy/bootstrap", method = RequestMethod.GET)
    public String goBootstrap() {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/bootstrap"); }
        return "/deploy/deploy/bootstrap/bootstrap";
    }

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 설치 화면 이동
     * @title : goBootstrapPopup
     * @return : String
    ***************************************************/
    @RequestMapping(value = "/deploy/bootstrap/install/bootstrapPopup", method = RequestMethod.GET)
    public String goBootstrapPopup() {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/bootstrap/install/bootstrapPopup"); }
        return "/deploy/deploy/bootstrap/bootstrapPopup";
    }

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 정보 목록 조회
     * @title : getBootstrapList
     * @return : ResponseEntity<HashMap<String,Object>>
    ***************************************************/
    @RequestMapping(value = "/deploy/bootstrap/list", method = RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getBootstrapList() {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/bootstrap/list"); }
        List<BootstrapListDTO> content = bootstrapService.getBootstrapList();
        HashMap<String, Object> result = new HashMap<String, Object>();
        int total = content != null ? content.size() : 0;
        result.put("records", content);
        result.put("total", total);
        return new ResponseEntity<HashMap<String, Object>>(result, HttpStatus.OK);
    }

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 상세 조회
     * @title : getBootstrapInfo
     * @return : ResponseEntity<BootstrapVO>
    ***************************************************/
    @RequestMapping(value = "/deploy/bootstrap/install/detail/{id}", method = RequestMethod.GET)
    public ResponseEntity<BootstrapVO> getBootstrapInfo(@PathVariable int id) {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/bootstrap/install/detail/"+id); }
        BootstrapVO vo = bootstrapService.getBootstrapInfo(id);
        return new ResponseEntity<BootstrapVO>(vo, HttpStatus.OK);
    }

    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인프라 환경 설정 정보 등록/수정
     * @title : saveIaasConfigInfo
     * @return : ResponseEntity<BootstrapVO>
    *****************************************************************/
    @RequestMapping(value = "/deploy/bootstrap/install/setIaasConfigInfo", method = RequestMethod.PUT)
    public ResponseEntity<BootstrapVO> saveIaasConfigInfo(@RequestBody BootStrapDeployDTO.IaasConfig dto, Principal principal) {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/bootstrap/install/setIaasConfigInfo"); }
        BootstrapVO config = saveService.saveIaasConfigInfo(dto, principal);
        return new ResponseEntity<>(config, HttpStatus.OK);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 기본 정보 저장
     * @title : saveDefaultInfo
     * @return : ResponseEntity<BootstrapVO>
    ***************************************************/
    @RequestMapping(value = "/deploy/bootstrap/install/setDefaultInfo", method = RequestMethod.PUT)
    public ResponseEntity<BootstrapVO> saveDefaultInfo(@RequestBody BootStrapDeployDTO.Default dto, Principal principal) {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/bootstrap/install/setDefaultInfo"); }
        BootstrapVO vo = saveService.saveDefaultInfo(dto, principal);
        return new ResponseEntity<>(vo, HttpStatus.OK);
    }

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 네트워크 정보 저장
     * @title : saveNetworkInfo
     * @return : ResponseEntity<BootstrapVO>
    ***************************************************/
    @RequestMapping(value = "/deploy/bootstrap/install/setNetworkInfo", method = RequestMethod.PUT)
    public ResponseEntity<BootstrapVO> saveNetworkInfo(@RequestBody @Valid BootStrapDeployDTO.Network dto, Principal principal) {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/bootstrap/install/setNetworkInfo"); }
        BootstrapVO vo = saveService.saveNetworkInfo(dto, principal);
        return new ResponseEntity<BootstrapVO>(vo, HttpStatus.OK);
    }

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 리소스 정보 저장
     * @title : saveResourcesInfo
     * @return : ResponseEntity<BootstrapVO>
    ***************************************************/
    @RequestMapping(value = "/deploy/bootstrap/install/setResourceInfo", method = RequestMethod.PUT)
    public ResponseEntity<BootstrapVO> saveResourcesInfo(@RequestBody @Valid BootStrapDeployDTO.Resource dto, Principal principal) {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/bootstrap/install/setResourceInfo"); }
        BootstrapVO vo = saveService.saveResourceInfo(dto, principal);
        return new ResponseEntity<BootstrapVO>(vo, HttpStatus.OK);
    }

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 배포 파일 생성
     * @title : makeDeploymentFile
     * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value = "/deploy/bootstrap/install/createSettingFile/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> makeDeploymentFile(@PathVariable int id) {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/bootstrap/install/createSettingFile/"+id); }
        bootstrapService.createSettingFile(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 설치
     * @title : installBootstrap
     * @return : ResponseEntity<BootstrapVO>
    ***************************************************/
    @MessageMapping("/deploy/bootstrap/install/bootstrapInstall")
    @SendTo("/deploy/bootstrap/install/logs")
    public ResponseEntity<BootstrapVO> installBootstrap(@RequestBody @Valid BootStrapDeployDTO.Install dto, Principal principal) {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/bootstrap/install/bootstrapInstall"); }
        deployAsyncService.deployAsync(dto, principal);
        return new ResponseEntity<BootstrapVO>(HttpStatus.OK);
    }

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 삭제 요청
     * @title : deleteBootstrap
     * @return : ResponseEntity<?>
    ***************************************************/
    @MessageMapping("/deploy/bootstrap/delete/instance")
    @SendTo("/deploy/bootstrap/delete/logs")
    public ResponseEntity<?> deleteBootstrap(@RequestBody @Valid BootStrapDeployDTO.Delete dto, Principal principal) {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/bootstrap/delete/instance"); }
        deleteDeployService.deleteDeployAsync(dto, principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 단순 Bootstrap 레코드 삭제
     * @title : deleteJustOnlyBootstrapRecord
     * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value = "/deploy/bootstrap/delete/data", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteBootstrapInfo(@RequestBody @Valid BootStrapDeployDTO.Delete dto) {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("===================== /deploy/bootstrap/delete/data"); }
            bootstrapService.deleteBootstrapInfo(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 배포 로그 정보 조회
     * @title : getDeployLogMsg
     * @return : ResponseEntity<String>
    ***************************************************/
    @RequestMapping(value = "/deploy/bootstrap/list/{id}", method = RequestMethod.GET)
    public ResponseEntity<String> getDeployLogMsg(@PathVariable int id) {
        if (LOGGER.isDebugEnabled()) { 
        LOGGER.debug("====================================> /deploy/bootstrap/list/"); 
    }
        BootstrapVO vo = bootstrapService.getBootstrapInfo(id);
        return new ResponseEntity<String>(vo.getDeployLog(), HttpStatus.OK);
    }

}