package org.openpaas.ieda.controller.hbdeploy.web.deploy.cfdeployment;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.controller.common.BaseController;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentCredentialConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dto.HbCfDeploymentCredentialConfigDTO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service.HbCfDeploymentCredentialService;
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
public class HbCfDeploymentCredentialConfigController extends BaseController {
    
    @Autowired private HbCfDeploymentCredentialService service;
    private final static Logger LOGGER = LoggerFactory.getLogger(HbCfDeploymentDefaultConfigController.class);
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid CfDeployment 인증서 관리 화면 이동
     * @title : goCredentialConfig
     * @return : String
    ***************************************************/
    @RequestMapping(value = "/deploy/hbCfDeployment/credentialConfig", method = RequestMethod.GET)
    public String goCredentialConfig() {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/hbCfDeployment/credentialConfig"); }
        return "/hbdeploy/deploy/cfDeployment/hbCfDeploymentCredentialConfig";
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 인증서 목록 정보 조회
     * @title : getCredentialConfigInfoList
     * @return : ResponseEntity<CfDeploymentVO>
    *****************************************************************/
    @RequestMapping(value = "/deploy/hbCfDeployment/credentialConfig/list", method = RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getCredentialConfigInfoList() {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/hbCfDeployment/credentialConfig/list"); }
        List<HbCfDeploymentCredentialConfigVO> CredentialConfigList = service.getCredentialConfigInfoList();
        HashMap<String, Object> list = new HashMap<String, Object>();
        int size =0;
        if( CredentialConfigList.size() > 0  ) {
            size = CredentialConfigList.size();
        }
        list.put("total", size);
        list.put("records",CredentialConfigList);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 인증서 정보 등록/수정
     * @title : saveCredentialConfigInfo
     * @return : ResponseEntity<>
    *****************************************************************/
    @RequestMapping(value = "/deploy/hbCfDeployment/credentialConfig/save", method = RequestMethod.PUT)
    public ResponseEntity<?> saveCredentialConfigInfo(@RequestBody HbCfDeploymentCredentialConfigDTO dto, Principal principal) {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/hbCfDeployment/credentialConfig/save"); }
        service.saveCredentialConfigInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 인증서 정보 삭제
     * @title : deleteCredentialConfigInfo
     * @return : ResponseEntity<>
    *****************************************************************/
    @RequestMapping(value = "/deploy/hbCfDeployment/credentialConfig/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCredentialConfigInfo(@RequestBody HbCfDeploymentCredentialConfigDTO dto, Principal principal) {
        if (LOGGER.isInfoEnabled()) { LOGGER.info("====================================> /deploy/hbCfDeployment/credentialConfig/save"); }
        service.deleteCredentialConfigInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
