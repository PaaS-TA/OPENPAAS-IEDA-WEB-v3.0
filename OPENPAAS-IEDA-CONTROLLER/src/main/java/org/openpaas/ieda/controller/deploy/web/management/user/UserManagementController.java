package org.openpaas.ieda.controller.deploy.web.management.user;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.openpaas.ieda.controller.common.BaseController;
import org.openpaas.ieda.deploy.web.management.user.dao.UserManagementVO;
import org.openpaas.ieda.deploy.web.management.user.dto.UserManagementDTO;
import org.openpaas.ieda.deploy.web.management.user.service.UserManagementService;
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
public class UserManagementController extends BaseController{
    
    @Autowired private UserManagementService service;
    private final static Logger LOGGER = LoggerFactory.getLogger(UserManagementController.class);
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 사용자 관리 화면 이동
     * @title : goUserManagement
     * @return : String
    ***************************************************/
    @RequestMapping(value="/admin/user", method=RequestMethod.GET)
    public String goUserManagement(){
        if (LOGGER.isInfoEnabled()) { LOGGER.info("================================================> /admin/user"); }
        return "/deploy/management/user/userManagement";
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 사용자 목록 정보 요청
     * @title : getUserInfoList
     * @return : ResponseEntity<HashMap<String,Object>>
    ***************************************************/
    @RequestMapping(value="/admin/user/list", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getUserInfoList(){
        if (LOGGER.isInfoEnabled()) { LOGGER.info("================================================>/admin/user/list"); }
        List<UserManagementVO> userList = service.getUserInfoList();        
        HashMap<String, Object> list = new HashMap<String, Object>();
        int size = 0;
        if( userList != null  ) {
            size = userList.size();
        }
        list.put("total", size);
        list.put("records", userList);
        return new ResponseEntity<HashMap<String, Object>>(list, HttpStatus.OK);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 사용자 정보 등록
     * @title : saveUserInfo
     * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value="/admin/user/add", method=RequestMethod.POST)
    public  ResponseEntity<?> saveUserInfo(@RequestBody @Valid UserManagementDTO.Regist dto, Principal principal){
        if (LOGGER.isInfoEnabled()) { LOGGER.info("================================================>/admin/user/add"); }
        service.savaUserInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 사용자 정보 수정
     * @title : updateUserInfo
     * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value="/admin/user/update/{userId}", method=RequestMethod.PUT)
    public ResponseEntity<?> updateUserInfo(@RequestBody @Valid UserManagementDTO.Regist dto, @PathVariable String userId){
        if (LOGGER.isInfoEnabled()) { LOGGER.info("================================================>/admin/user/update/"+userId); }
         service.updateUserInfo(dto,userId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 사용자 정보 삭제
     * @title : deleteUserInfo
     * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value="/admin/user/delete/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUserInfo(@PathVariable String userId){
        if (LOGGER.isInfoEnabled()) { LOGGER.info("================================================> /admin/user/delete/"+userId); }
        service.deleteUserInfo(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
