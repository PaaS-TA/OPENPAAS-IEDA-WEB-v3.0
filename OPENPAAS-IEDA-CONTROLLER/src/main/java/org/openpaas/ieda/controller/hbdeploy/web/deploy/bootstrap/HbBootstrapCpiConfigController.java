package org.openpaas.ieda.controller.hbdeploy.web.deploy.bootstrap;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.controller.common.BaseController;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapCpiConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto.HbBootstrapCpiConfigDTO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.service.HbBootstrapCpiConfigService;
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
public class HbBootstrapCpiConfigController extends BaseController {

    private final static Logger LOGGER = LoggerFactory.getLogger(HbBootstrapCpiConfigController.class);
    @Autowired private HbBootstrapCpiConfigService service;
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid Bootstrap CPI 정보 화면 이동
     * @title : goBootstrap
     * @return : String
    ***************************************************/
    @RequestMapping(value = "/deploy/hbBootstrap/cpiConfig", method = RequestMethod.GET)
    public String goCpiConfig() {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/hbBootstrap/cpiConfig"); }
        return "/hbdeploy/deploy/bootstrap/hbBootstrapCpiConfig";
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CPI 목록 정보 조회
     * @title : selectCpiConfigInfoList
     * @return : ResponseEntity<BootstrapVO>
    *****************************************************************/
    @RequestMapping(value = "/deploy/hbBootstrap/cpi/list", method = RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getCpiConfigInfoList() {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/hbBootstrap/cpiConfigList"); }
        List<HbBootstrapCpiConfigVO> cpiConfigList = service.getCpiConfigInfoList();
        HashMap<String, Object> list = new HashMap<String, Object>();
        int size =0;
        if( cpiConfigList.size() > 0  ) {
            size = cpiConfigList.size();
        }
        list.put("total", size);
        list.put("records", cpiConfigList);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CPI 정보 등록/수정
     * @title : saveCpiConfigInfo
     * @return : ResponseEntity<BootstrapVO>
    *****************************************************************/
    @RequestMapping(value = "/deploy/hbBootstrap/cpi/save", method = RequestMethod.PUT)
    public ResponseEntity<HbBootstrapCpiConfigVO> saveCpiConfigInfo(@RequestBody HbBootstrapCpiConfigDTO dto, Principal principal) {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/hbBootstrap/saveCpiConfigInfo"); }
        HbBootstrapCpiConfigVO config = service.saveCpiConfigInfo(dto, principal);
        return new ResponseEntity<>(config, HttpStatus.CREATED);
    }
    
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CPI 정보 삭제
     * @title : deleteCpiConfigInfo
     * @return : ResponseEntity<BootstrapVO>
    *****************************************************************/
    @RequestMapping(value = "/deploy/hbBootstrap/cpi/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCpiConfigInfo(@RequestBody HbBootstrapCpiConfigDTO dto, Principal principal) {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/hbBootstrap/deleteConfigInfo"); }
        service.deleteCpiConfigInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}