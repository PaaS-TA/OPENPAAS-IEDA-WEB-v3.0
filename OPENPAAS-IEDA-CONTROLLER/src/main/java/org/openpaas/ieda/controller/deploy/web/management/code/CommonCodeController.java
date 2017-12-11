package org.openpaas.ieda.controller.deploy.web.management.code;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.openpaas.ieda.controller.common.BaseController;
import org.openpaas.ieda.deploy.web.management.code.dao.CommonCodeDAO;
import org.openpaas.ieda.deploy.web.management.code.dao.CommonCodeVO;
import org.openpaas.ieda.deploy.web.management.code.dto.CommonCodeDTO;
import org.openpaas.ieda.deploy.web.management.code.service.CommonCodeService;
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
public class CommonCodeController extends BaseController {
    @Autowired private CommonCodeService service;
    @Autowired private CommonCodeDAO dao;
    
    private final static Logger LOGGER= LoggerFactory.getLogger(CommonCodeController.class);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 코드 관리 화면을 호출하여 이동
     * @title : goCodeManagement
     * @return : String
    *****************************************************************/
    @RequestMapping(value="/admin/code", method=RequestMethod.GET)
    public String goCodeManagement() {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> goCodeManagement");  }
        return "/deploy/management/code/codeManagement";
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 코드 그룹 목록 조회
     * @title : getCodeGroups
     * @return : ResponseEntity<HashMap<String,Object>>
    *****************************************************************/
    @RequestMapping(value="/admin/code/groupList", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getCodeGroups() {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> getCodeGroups");  }
        List<CommonCodeVO> page = dao.selectParentCodeIsNull();
        HashMap<String, Object> list = new HashMap<String, Object>();
        list.put("total", page.size());
        list.put("records", page);
        
        return new ResponseEntity<HashMap<String, Object>>(list, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 코드 목록 조회
     * @title : getCodeList
     * @return : ResponseEntity<HashMap<String,Object>>
    *****************************************************************/
    @RequestMapping(value="/admin/code/codeList/{parentCode}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getCodeList(@PathVariable String parentCode) {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> getCodeList");  }
        List<CommonCodeVO> page = service.getCodeList(parentCode);
        HashMap<String, Object> list = new HashMap<String, Object>();
        int count = 0;
        if (page.size() >0) {
            count = page.size();
        }
        list.put("total", count);
        list.put("records", page);
        return new ResponseEntity<HashMap<String, Object>>(list, HttpStatus.OK);
    }
    

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 코드 그룹 추가 및 수정
     * @title : saveCodeGroup
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value="/admin/code/codeGroup/add", method=RequestMethod.POST)
    public ResponseEntity<?> saveCodeGroupInfo( @RequestBody @Valid CommonCodeDTO.Regist codeDto, Principal principal) {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> saveCodeGroup");  }
        service.saveCodeGroupInfo(codeDto, principal);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 수정에 필요한 코드 그룹 정보 조회
     * @title : getCodeGroupInfo
     * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value="/admin/code/list/{codeIdx}", method=RequestMethod.GET)
    public ResponseEntity<CommonCodeVO> getCodeGroupInfo(@PathVariable int codeIdx) {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> getCodeGroupInfo");  }
        CommonCodeVO vo = service.getCommonCodeList(codeIdx);
        return new ResponseEntity<CommonCodeVO>(vo, HttpStatus.OK);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하위 코드 등록
     * @title : saveCodeInfo
     * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value="/admin/code/add", method=RequestMethod.POST)
    public ResponseEntity<?> saveCodeInfo( @RequestBody @Valid CommonCodeDTO.Regist codeDto, Principal principal) {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> saveCodeInfo");  }
        service.createSubCode(codeDto, principal);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 해당 코드 수정
     * @title : updateCode
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value="/admin/code/update/{codeIdx}", method=RequestMethod.PUT)
    public ResponseEntity<?> updateCode(@PathVariable int codeIdx,
            @RequestBody @Valid CommonCodeDTO.Regist updateCodeDto, Principal principal) {
        
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> updateCode");  }
        updateCodeDto.setCodeIdx(codeIdx);
        service.updateCode(updateCodeDto, principal);
        return new ResponseEntity<> (HttpStatus.OK); 
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 코드 그룹 정보 삭제
     * @title : deleteCode
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value="/admin/code/codeGroup/delete/{codeIdx}", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteCodeGroupInfo(@PathVariable int codeIdx) {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> deleteCodeGroupInfo");  }
        service.deleteCodeGroupInfo(codeIdx);
        return new ResponseEntity<> (HttpStatus.OK); 
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 코드 정보 삭제
     * @title : deleteCodeInfo
     * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value="/admin/code/delete/{codeIdx}", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteCodeInfo(@PathVariable int codeIdx) {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> deleteCodeInfo");  }
        service.deleteCodeInfo(codeIdx);
        return new ResponseEntity<> (HttpStatus.OK); 
    }
}

