package org.openpaas.ieda.controller.hbdeploy.web.deploy.cfdeployment;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.controller.common.BaseController;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentNetworkConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dto.HbCfDeploymentNetworkConfigDTO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service.HbCfDeploymentNetworkConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HbCfDeploymentNetworkConfigController  extends BaseController{
	
	@Autowired private HbCfDeploymentNetworkConfigService service;
	private final static Logger LOGGER = LoggerFactory.getLogger(HbCfDeploymentNetworkConfigController.class);
    
	/***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid CfDeployment 네트워크 정보 화면 이동
     * @title : goCfDeployment
     * @return : String
    ***************************************************/
    @RequestMapping(value = "/deploy/hbCfDeployment/networkConfig", method = RequestMethod.GET)
    public String goNetworkConfig() {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/hbCfDeployment/networkConfig"); }
        return "/hbdeploy/deploy/cfDeployment/hbCfDeploymentNetworkConfig";
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 네트워크 목록 정보 조회
     * @title : selectNetworkConfigInfoList
     * @return : ResponseEntity<CfDeploymentVO>
    *****************************************************************/
    @RequestMapping(value = "/deploy/hbCfDeployment/networkConfig/list", method = RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getNetworkConfigInfoList() {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/hbCfDeployment/networkConfig/list"); }
        List<HbCfDeploymentNetworkConfigVO> NetworkConfigList = service.getNetworkConfigInfoList();
        HashMap<String, Object> list = new HashMap<String, Object>();
        int size =0;
        if( NetworkConfigList.size() > 0  ) {
            size = NetworkConfigList.size();
        }
        list.put("total", size);
        list.put("records", NetworkConfigList);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 네트워크 정보 등록/수정
     * @title : saveNetworkConfigInfo
     * @return : ResponseEntity<>
    *****************************************************************/
    @RequestMapping(value = "/deploy/hbCfDeployment/networkConfig/save", method = RequestMethod.PUT)
    public ResponseEntity<?> saveNetworkConfigInfo(@RequestBody HbCfDeploymentNetworkConfigDTO dto, Principal principal) {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/hbCfDeployment/networkConfig/save"); }
        service.saveNetworkConfigInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 네트워크 정보 삭제
     * @title : deleteNetworkConfigInfo
     * @return : ResponseEntity<>
    *****************************************************************/
    @RequestMapping(value = "/deploy/hbCfDeployment/networkConfig/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteNetworkConfigInfo(@RequestBody HbCfDeploymentNetworkConfigDTO dto, Principal principal) {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/hbCfDeployment/networkConfig/delete"); }
        service.deleteNetworkConfigInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
