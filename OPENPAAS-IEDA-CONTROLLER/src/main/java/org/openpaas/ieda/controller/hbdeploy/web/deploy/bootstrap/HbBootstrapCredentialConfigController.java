package org.openpaas.ieda.controller.hbdeploy.web.deploy.bootstrap;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapCredentialConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapNetworkConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto.HbBootstrapCredentialConfigDTO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.service.HbBootstrapCredentialConfigService;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.service.HbBootstrapNetworkConfigService;
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
public class HbBootstrapCredentialConfigController {
    
    @Autowired private HbBootstrapCredentialConfigService service;
    @Autowired private HbBootstrapNetworkConfigService networkConfigservice;
    
    private final static Logger LOGGER = LoggerFactory.getLogger(HbBootstrapCredentialConfigController.class);
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 디렉터 인증서 관리 화면 이동
     * @title : goCredentialConfig
     * @return : String
    *****************************************************************/
    @RequestMapping(value="/deploy/hbBootstrap/credentialConfig", method=RequestMethod.GET)
    public String goCredentialConfig() {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /deploy/hbBootstrap/hbBootstrapDefaultConfig"); }
        return "/hbdeploy/deploy/bootstrap/hbBootstrapCredentialConfig";
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 디렉터 인증서 목록 조회
     * @title : getDirectorCredentialList
     * @return : ResponseEntity<HashMap<String,Object>>
    *****************************************************************/
    @RequestMapping(value="/deploy/hbBootstrap/credential/list", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getDirectorCredentialList() {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /deploy/hbBootstrap/defaultConfigList"); }
        HashMap<String, Object> map = new HashMap<String, Object>();
        List<HbBootstrapCredentialConfigVO> credentialList = service.getHbBootstrapCredentialConfigInfoList();
        int size = 0;
        if( credentialList.size() > 0  ) {
            size = credentialList.size();
        }
        map.put("records", credentialList);
        map.put("size", size);
        return new ResponseEntity<HashMap<String, Object> >(map, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 디렉터 인증서 정보 저장 및 인증서 파일 생성
     * @title : saveDirectorCredential
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value="/deploy/hbBootstrap/credential/save", method=RequestMethod.PUT)
    public ResponseEntity<?> saveHbbootstrapCredentialConfigInfo( @RequestBody HbBootstrapCredentialConfigDTO dto, Principal principal){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /deploy/hbBootstrap/credential/save"); }
        service.saveHbBootstrapCredentialConfigInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.CREATED); 
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 디렉터 인증서 정보 삭제 및 인증서 파일 삭제
     * @title : deleteDirectorCredential
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value="/deploy/hbBootstrap/credential/delete", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteDirectorCredentialConfigInfo( @RequestBody HbBootstrapCredentialConfigDTO dto, Principal principal){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /config/credentail/delete"); }
        service.deleteHbBootstrapCredentialConfigInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 디렉터 인증서 정보 저장 중 네트워크 목록 조회
     * @title : getNetworkConfigInfoList
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value="/deploy/hbBootstrap/credential/networkList/{iaasType}", method=RequestMethod.GET)
    public ResponseEntity<List<HbBootstrapNetworkConfigVO>> getNetworkConfigInfoList(@PathVariable String iaasType){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /deploy/hbBootstrap/credential/networkList"); }
        List<HbBootstrapNetworkConfigVO> list = networkConfigservice.getNetworkConfigInfoList(iaasType);
        return new ResponseEntity<List<HbBootstrapNetworkConfigVO>>(list, HttpStatus.OK); 
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 디렉터 인증서 정보 저장 중 네트워크 목록 조회
     * @title : getNetworkConfigInfoList
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value="/deploy/hbBootstrap/credential/networkInfo/{networkId}/{iaasType}", method=RequestMethod.GET)
    public ResponseEntity<HbBootstrapNetworkConfigVO> getNetworkConfigInfo(@PathVariable String networkId, @PathVariable String iaasType){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> /deploy/hbBootstrap/credential/networkInfo/{networkId}"); }
        HbBootstrapNetworkConfigVO networkInfo = networkConfigservice.getNetworkConfigInfo(networkId, iaasType);
        return new ResponseEntity<HbBootstrapNetworkConfigVO>(networkInfo, HttpStatus.OK); 
    }
}
    
