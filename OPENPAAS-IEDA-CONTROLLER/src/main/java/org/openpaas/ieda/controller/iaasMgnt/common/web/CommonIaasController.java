package org.openpaas.ieda.controller.iaasMgnt.common.web;

import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.openpaas.ieda.controller.deploy.web.management.code.CommonCodeController;
import org.openpaas.ieda.iaasDashboard.web.account.dto.IaasAccountMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.common.dao.CommonCodeVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
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
public class CommonIaasController {
    
    @Autowired private CommonIaasService commonIaasService;
   
    private final static Logger LOGGER = LoggerFactory.getLogger(CommonCodeController.class);
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 기본 IaaS 계정 정보 설정
    * @title : setDefaultIaasAccount
    * @return : ResponseEntity<?> 
    ***************************************************/
    @RequestMapping(value="/common/iaasMgnt/setDefaultIaasAccount" ,method=RequestMethod.POST)
    public ResponseEntity<?> setDefaultIaasAccount(@RequestBody @Valid IaasAccountMgntDTO dto, Principal principal){
        commonIaasService.setDefaultIaasAccountInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 공통 코드 조회 (하위 코드 목록)
     * @title : getSubCode
     * @return : ResponseEntity<List<CommonIaasVO>>
    *****************************************************************/
    @RequestMapping(value="/common/iaas/codes/parent/{parentCode}", method=RequestMethod.GET)
    public ResponseEntity<List<CommonCodeVO>> getSubCode(@PathVariable String parentCode) {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================================> 서브 그룹 조회 요청");  }
        List<CommonCodeVO> content = commonIaasService.getSubGroupCodeList(parentCode);
        return new ResponseEntity<List<CommonCodeVO>>(content, HttpStatus.OK);
    }
}
