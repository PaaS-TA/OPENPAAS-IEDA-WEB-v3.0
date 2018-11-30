package org.openpaas.ieda.controller.hbdeploy.web.deploy.bootstrap;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.controller.common.BaseController;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapNetworkConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto.HbBootstrapNetworkConfigDTO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.service.HbBootstrapNetworkConfigService;
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
public class HbBootstrapNetworkConfigController extends BaseController {

    private final static Logger LOGGER = LoggerFactory.getLogger(HbBootstrapNetworkConfigController.class);
    @Autowired private HbBootstrapNetworkConfigService service;
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid Bootstrap 네트워크 정보 화면 이동
     * @title : goNetworkConfig
     * @return : String
    ***************************************************/
    @RequestMapping(value = "/deploy/hbBootstrap/networkConfig", method = RequestMethod.GET)
    public String goNetworkConfig() {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/hbBootstrap/NetworkConfig"); }
        return "/hbdeploy/deploy/bootstrap/hbBootstrapNetworkConfig";
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Network 목록 정보 조회
     * @title : selectNetworkConfigInfoList
     * @return : ResponseEntity<HashMap<String, Object>>
    *****************************************************************/
    @RequestMapping(value = "/deploy/hbBootstrap/network/list", method = RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getNetworkConfigInfoList() {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/hbBootstrap/NetworkConfigList"); }
        List<HbBootstrapNetworkConfigVO> NetworkConfigList = service.getNetworkConfigInfoList();
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
     * @description : Network 정보 등록/수정
     * @title : saveNetworkConfigInfo
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value = "/deploy/hbBootstrap/network/save", method = RequestMethod.PUT)
    public ResponseEntity<?> saveNetworkConfigInfo(@RequestBody HbBootstrapNetworkConfigDTO dto, Principal principal) {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/hbBootstrap/saveNetworkConfigInfo"); }
        service.saveNetworkConfigInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Network 정보 삭제
     * @title : deleteNetworkConfigInfo
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value = "/deploy/hbBootstrap/network/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteNetworkConfigInfo(@RequestBody HbBootstrapNetworkConfigDTO dto, Principal principal) {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/hbBootstrap/deleteConfigInfo"); }
        service.deleteNetworkConfigInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}