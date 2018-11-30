package org.openpaas.ieda.controller.hbdeploy.web.deploy.cfdeployment;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentVO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dto.HbCfDeploymentDTO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service.HbCfDeploymentDeleteAsyncService;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service.HbCfDeploymentDeployAsyncService;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service.HbCfDeploymentSaveService;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service.HbCfDeploymentService;
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
public class HbCfDeploymentController {
    
    @Autowired private HbCfDeploymentService hbCfDeploymentService;
    @Autowired private HbCfDeploymentSaveService hbCfDeploymentSaveService;
    @Autowired private HbCfDeploymentDeployAsyncService hbCfDeploymentDeployAsyncService;
    @Autowired private HbCfDeploymentDeleteAsyncService hbCfDeploymentDeleteAsyncService;
    
    final private static Logger LOGGER = LoggerFactory.getLogger(HbCfDeploymentController.class);
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CF 설치 화면 이동
     * @title : goCfDeployment
     * @return : String
    *****************************************************************/
    @RequestMapping(value = "/deploy/hbCfDeployment/hbCfDeployment", method=RequestMethod.GET)
    public String goCfDeployment() {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> /deploy/hbCfDeployment/hbCfDeployment"); }
        return "/hbdeploy/deploy/cfDeployment/hbCfDeployment";
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CF Deployment 정보 조회
     * @title : getHbCfDeploymenList
     * @return : ResponseEntity<HashMap<String,Object>>
    ***************************************************/
    @RequestMapping(value = "/deploy/hbCfDeployment/list/{installStatus}", method = RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getHbCfDeploymenList(@PathVariable String installStatus) {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/hbBootstrap/list/{installStatus}"); }
        List<HbCfDeploymentVO> content = hbCfDeploymentService.getHbCfDeploymentList(installStatus);
        HashMap<String, Object> result = new HashMap<String, Object>();
        int total = content != null ? content.size() : 0;
        result.put("records", content);
        result.put("total", total);
        return new ResponseEntity<HashMap<String, Object>>(result, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CF Deployment 정보 저장/수정
     * @title : saveCfDeploymentInfo
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value = "/deploy/hbCfDeployment/install/save", method = RequestMethod.PUT)
    public ResponseEntity<?> saveCfDeploymentInfo(@RequestBody HbCfDeploymentDTO dto, Principal principal){
        if(LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/hbCfDeployment/install/save");}
        hbCfDeploymentSaveService.saveCfdeploymentConfigInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CF Deployment 정보 삭제
     * @title : deleteCfDeploymentInfo
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value = "/deploy/hbCfDeployment/install/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCfDeploymentInfo(@RequestBody HbCfDeploymentDTO dto, Principal principal){
        if(LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/hbCfDeployment/install/delete");}
        hbCfDeploymentSaveService.deleteCfdeploymentConfigInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CF Deployment 플랫폼 설치
     * @title : installCf
     * @return : ResponseEntity<?>
    *****************************************************************/
    @MessageMapping("/deploy/hbCfDeployment/install/cfDeploymentInstall")
    @SendTo("/deploy/hbCfDeployment/install/logs")
    public ResponseEntity<?> installCfDeployment(@RequestBody @Valid HbCfDeploymentDTO dto, Principal principal){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> /deploy/hbCfDeployment/install/cfInstall"); }
        hbCfDeploymentDeployAsyncService.deployAsync(dto, principal, "cf");
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CF Deployment 플랫폼 삭제
     * @title : deleteCfDeployment
     * @return : ResponseEntity<?>
    *****************************************************************/
    @MessageMapping("/deploy/hbCfDeployment/delete/instance")
    @SendTo("/deploy/hbCfDeployment/delete/logs")
    public ResponseEntity<?> deleteCfDeployment(@RequestBody @Valid HbCfDeploymentDTO dto, Principal principal){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> /deploy/hbCfDeployment/install/cfInstall"); }
        hbCfDeploymentDeleteAsyncService.deleteDeployAsync(dto, "cf", principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
}
