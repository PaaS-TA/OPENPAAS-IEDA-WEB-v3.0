package org.openpaas.ieda.controller.hbdeploy.web.deploy.bootstrap;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.controller.common.BaseController;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapDefaultConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto.HbBootstrapDefaultConfigDTO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.service.HbBootstrapDefaultConfigService;
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
public class HbBootstrapDefaultConfigController extends BaseController {

    private final static Logger LOGGER = LoggerFactory.getLogger(HbBootstrapDefaultConfigController.class);
    
    @Autowired private HbBootstrapDefaultConfigService service;
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid Bootstrap 기본 정보 화면 이동
     * @title : goDefaultConfig
     * @return : String
    ***************************************************/
    @RequestMapping(value = "/deploy/hbBootstrap/defaultConfig", method = RequestMethod.GET)
    public String goDefaultConfig() {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/hbBootstrap/defaultConfig"); }
        return "/hbdeploy/deploy/bootstrap/hbBootstrapDefaultConfig";
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 기본 정보 목록 전체 조회
     * @title : getDefaultConfigInfoList
     * @return : ResponseEntity
    *****************************************************************/
    @RequestMapping(value = "/deploy/hbBootstrap/default/list", method = RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getDefaultConfigInfoList() {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/hbBootstrap/defaultConfigList"); }
        List<HbBootstrapDefaultConfigVO> defaultConfigList = service.getDefaultConfigInfoList();
        HashMap<String, Object> list = new HashMap<String, Object>();
        int size =0;
        if( defaultConfigList.size() > 0  ) {
            size = defaultConfigList.size();
        }
        list.put("total", size);
        list.put("records", defaultConfigList);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 기본 정보 등록/수정
     * @title : saveDefaultConfigInfo
     * @return : ResponseEntity
    *****************************************************************/
    @RequestMapping(value = "/deploy/hbBootstrap/default/save", method = RequestMethod.PUT)
    public ResponseEntity<?> saveDefaultConfigInfo(@RequestBody HbBootstrapDefaultConfigDTO dto, Principal principal) {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/hbBootstrap/saveDefaultConfigInfo"); }
        service.saveDefaultConfigInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 기본 정보 삭제
     * @title : deleteDefaultConfigInfo
     * @return : ResponseEntity<BootstrapVO>
    *****************************************************************/
    @RequestMapping(value = "/deploy/hbBootstrap/default/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteDefaultConfigInfo(@RequestBody HbBootstrapDefaultConfigDTO dto, Principal principal) {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/hbBootstrap/deleteDefaultConfigInfo"); }
        service.deleteDefaultConfigInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}