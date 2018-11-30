package org.openpaas.ieda.controller.deploy.web.config.credential;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.deploy.web.config.credential.dao.CredentialManagementVO;
import org.openpaas.ieda.deploy.web.config.credential.dto.CredentialManagementDTO;
import org.openpaas.ieda.deploy.web.config.credential.service.CredentialManagementService;
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
public class CredentialManagementController {
    
    @Autowired private CredentialManagementService service;
    
    private final static Logger LOGGER = LoggerFactory.getLogger(CredentialManagementController.class);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 디렉터 인증서 관리 화면 이동
     * @title : goCredentialManagement
     * @return : String
    *****************************************************************/
    @RequestMapping(value="/config/credential", method=RequestMethod.GET)
    public String goCredentialManagement() {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /config/credential"); }
        return "/deploy/config/credentialManagement";
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 디렉터 인증서 관리 화면 이동
     * @title : goCredentialManagement
     * @return : String
    *****************************************************************/
    @RequestMapping(value="/config/credential2", method=RequestMethod.GET)
    public String goCredentialManagement2() {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /config/credential"); }
        return "/deploy/config/credentialManagement2";
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 디렉터 인증서 목록 조회
     * @title : getDirectorCredentialList
     * @return : ResponseEntity<HashMap<String,Object>>
    *****************************************************************/
    @RequestMapping(value="/config/credentail/list", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getDirectorCredentialList() {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /config/credentail/list"); }
        HashMap<String, Object> map = new HashMap<String, Object>();
        List<CredentialManagementVO> credentialList = service.getDirectorCredentialList();
        map.put("records", credentialList);
        return new ResponseEntity<HashMap<String, Object> >(map, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 디렉터 인증서 정보 저장 및 인증서 파일 생성
     * @title : saveDirectorCredential
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value="/config/credentail/save", method=RequestMethod.POST)
    public ResponseEntity<?> saveDirectorCredential( @RequestBody CredentialManagementDTO dto, Principal principal){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /config/credentail/save"); }
        service.saveDirectorCredential(dto, principal);
        return new ResponseEntity<>(HttpStatus.CREATED); 
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 디렉터 인증서 정보 삭제 및 인증서 파일 삭제
     * @title : deleteDirectorCredential
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value="/config/credentail/delete", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteDirectorCredential( @RequestBody CredentialManagementDTO dto, Principal principal){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /config/credentail/delete"); }
        service.deleteDirectorCredentialInfo(dto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
    }
}
    
