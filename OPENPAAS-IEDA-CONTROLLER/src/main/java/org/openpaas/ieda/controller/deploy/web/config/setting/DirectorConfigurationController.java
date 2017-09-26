package org.openpaas.ieda.controller.deploy.web.config.setting;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.openpaas.ieda.controller.common.BaseController;
import org.openpaas.ieda.deploy.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.deploy.web.config.setting.dto.DirectorConfigDTO;
import org.openpaas.ieda.deploy.web.config.setting.service.DirectorConfigService;
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
public class DirectorConfigurationController extends BaseController { 

    @Autowired private DirectorConfigService service;
    
    private final static Logger LOG = LoggerFactory.getLogger(DirectorConfigurationController.class);
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 설치관리자 설정 화면 이동
     * @title : goListDirector
     * @return : String
    ***************************************************/
    @RequestMapping(value="/config/director", method=RequestMethod.GET)
    public String goListDirector() {
        if(LOG.isInfoEnabled()){ LOG.info("=====================> 설치관리자 설정 화면 요청"); }
        return "/deploy/config/listDirector";
    }


    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 설치관리자 설정 추가
     * @title : createDirector
     * @return : ResponseEntity
    ***************************************************/
    @RequestMapping(value="/config/director/add", method=RequestMethod.POST)
    public ResponseEntity<Object> createDirector(@RequestBody @Valid DirectorConfigDTO.Create directorDto, Principal principal) {
        
        if(LOG.isInfoEnabled()){ LOG.info("=====================> 설치 관리자 설정 추가 요청"); }
        String boshConfigFileName = ".bosh_config";
        service.existCheckCreateDirectorInfo(directorDto,principal,boshConfigFileName);
        if(LOG.isInfoEnabled()){ LOG.info("=====================> 설치 관리자 설정 추가 성공!!"); }
        
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 설치 관리자 정보 목록 조회(전체)
     * @title : listDirector
     * @return : ResponseEntity<HashMap<String,Object>>
    ***************************************************/
    @RequestMapping(value="/config/director/list", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getDirectorList() {
        
        if(LOG.isInfoEnabled()){ LOG.info("=============================> 설치 관리자  정보 목록 조회 요청"); }
        HashMap<String, Object> listResult = new HashMap<String, Object>();
        //defalutYn을 기준으로 정렬하여 설치 관리자 정보를 가져옴
        List<DirectorConfigVO> contents = service.getDirectorList();
        int size = 0;
        if( contents != null ) size = contents.size();
        listResult.put("total", size);
        listResult.put("records", contents);
        if(LOG.isInfoEnabled()){ LOG.info("=============================> 설치 관리자 정보 목록 조회 성공!"); }
        
        return new ResponseEntity<HashMap<String, Object> >(listResult, HttpStatus.OK);
    }

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 설치관리자 설정 수정
     * @title : updateDirector
     * @return : ResponseEntity<Object>
    ***************************************************/
    @RequestMapping(value="/config/director/update/{seq}", method=RequestMethod.PUT)
    public ResponseEntity<Object> updateDirector(@PathVariable int seq, @RequestBody @Valid DirectorConfigDTO.Update updateDto, Principal principal) {
        
        if(LOG.isInfoEnabled()){ LOG.info("=============================> 설치 관리자 수정 요청"); }
        String boshConfigFileName = ".bosh_config";
        service.existCheckUpdateDirectorinfo(updateDto, principal, boshConfigFileName);
        if(LOG.isInfoEnabled()){ LOG.info("=============================> 설치 관리자 수정 성공"); }
        
        return new ResponseEntity<> (HttpStatus.OK); 
    }
    
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 설치관리자 설정 삭제
     * @title : deleteDirector
     * @return : ResponseEntity<Object>
    ***************************************************/
    @RequestMapping(value="/config/director/delete/{seq}", method=RequestMethod.DELETE)
    public ResponseEntity<Object> deleteDirector(@PathVariable int seq) {
        
        if(LOG.isInfoEnabled()){ LOG.info("================> 기본 설치 관리자 삭제 요청!!"); }
        String boshConfigFileName = ".bosh_config";
        service.deleteDirectorConfig(seq, boshConfigFileName);
        if(LOG.isInfoEnabled()){ LOG.info("================> 기본 설치 관리자 삭제 성공!!"); }
        
        return new ResponseEntity<> (HttpStatus.NO_CONTENT); 
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 기본 설치 관리자 설정
     * @title : setDefaultDirector
     * @return : ResponseEntity<Response>
    ***************************************************/
    @RequestMapping(value="/config/director/setDefault/{seq}", method=RequestMethod.PUT)
    public ResponseEntity<DirectorConfigVO> setDefaultDirector(@PathVariable int seq, Principal principal) {
        
        if(LOG.isInfoEnabled()){ LOG.info("================> 기본 설치 관리자 설정 요청!!"); }
        String boshConfigFileName = ".bosh_config";
        DirectorConfigVO directorConfig = service.existCheckSetDefaultDirectorInfo(seq,principal, boshConfigFileName);
        if(LOG.isInfoEnabled()){ LOG.info("================> 기본 설치 관리자 설정 성공!!"); }
        
        return new ResponseEntity<DirectorConfigVO>(directorConfig, HttpStatus.OK);
    }
    
}