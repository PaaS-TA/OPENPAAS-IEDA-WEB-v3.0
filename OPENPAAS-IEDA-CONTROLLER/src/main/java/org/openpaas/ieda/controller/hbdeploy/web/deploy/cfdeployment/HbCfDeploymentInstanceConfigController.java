package org.openpaas.ieda.controller.hbdeploy.web.deploy.cfdeployment;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.controller.common.BaseController;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentInstanceConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dto.HbCfDeploymentInstanceConfigDTO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service.HbCfDeploymentInstanceConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HbCfDeploymentInstanceConfigController extends BaseController{

    @Autowired
    private HbCfDeploymentInstanceConfigService service;
    
    private final static Logger LOGGER = LoggerFactory.getLogger(HbCfDeploymentInstanceConfigController.class);
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid CfDeployment 인스턴스 정보 화면 이동
     * @title : goInstanceConfig
     * @return : String 
    ***************************************************/
    @RequestMapping(value = "/deploy/hbCfDeployment/instanceConfig", method = RequestMethod.GET)
    public String goInstanceConfig() {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/hbCfDeployment/instanceConfig"); }
        return "/hbdeploy/deploy/cfDeployment/hbCfDeploymentInstanceConfig";
    }
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid CfDeployment 인스턴스 정보 목록 조회
     * @title : selectInstanceConfigInfoList
     * @return : ResponseEntity<HbCfDeploymentInstanceConfigVO>
    *****************************************************************/
    @RequestMapping(value = "/deploy/hbCfDeployment/instanceConfig/list", method = RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getHbCfInstanceConfigInfoList() {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/hbCfDeployment/instanceConfig/list"); }
        List<HbCfDeploymentInstanceConfigVO> InstanceConfigList = service.getHbCfInstanceConfigInfoList();
        HashMap<String, Object> list = new HashMap<String, Object>();
        int size =0;
        if( InstanceConfigList != null && InstanceConfigList.size() > 0  ) {
            size = InstanceConfigList.size();
        }
        list.put("total", size);
        list.put("records", InstanceConfigList);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 릴리즈 버전 및 인프라 별 JOb 목록 조회
     * @title : getHbCfJobList
     * @return : ResponseEntity<List<HashMap<String, String>>>
    *****************************************************************/
    @RequestMapping(value="/deploy/hbCfDeployment/instanceConfig/job/list/{version}/{deployType}", method=RequestMethod.GET)
    public ResponseEntity<List<HashMap<String, String>>> getHbCfJobList(@PathVariable String version, @PathVariable String deployType){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> /deploy/hbCfDeployment/instanceConfig/job/list/"+version+"/"+deployType); }
        List<HashMap<String, String>> list = service.getHbCfJobTemplateList(version,deployType);
        return new ResponseEntity<List<HashMap<String, String>>>(list, HttpStatus.OK);
    }
    
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid CfDeployment Instance 정보 등록/수정
     * @title : saveInstanceConfigInfo
     * @return : ResponseEntity<>
    *****************************************************************/
    @RequestMapping(value = "/deploy/hbCfDeployment/instanceConfig/save", method = RequestMethod.PUT)
    public ResponseEntity<?> saveInstanceConfigInfo(@RequestBody HbCfDeploymentInstanceConfigDTO dto, Principal principal) {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/hbCfDeployment/instanceConfig/save"); }
        service.saveHbCfInstanceConfigInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid CfDeployment Instance 정보 삭제
     * @title : deleteInstanceConfigInfo
     * @return : ResponseEntity<>
    *****************************************************************/
    @RequestMapping(value = "/deploy/hbCfDeployment/instanceConfig/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteInstanceConfigInfo(@RequestBody HbCfDeploymentInstanceConfigDTO dto, Principal principal) {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/hbCfDeployment/instanceConfig/delete"); }
        service.deleteHbCfInstanceConfigInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
