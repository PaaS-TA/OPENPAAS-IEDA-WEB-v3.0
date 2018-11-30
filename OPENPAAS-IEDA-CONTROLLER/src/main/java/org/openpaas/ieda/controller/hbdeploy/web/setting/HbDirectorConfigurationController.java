package org.openpaas.ieda.controller.hbdeploy.web.setting;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.openpaas.ieda.controller.common.BaseController;
import org.openpaas.ieda.hbdeploy.web.config.setting.dao.HbDirectorConfigVO;
import org.openpaas.ieda.hbdeploy.web.config.setting.dto.HbDirectorConfigDTO;
import org.openpaas.ieda.hbdeploy.web.config.setting.service.HbDirectorConfigService;
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
import org.springframework.web.multipart.MultipartHttpServletRequest;


@Controller
public class HbDirectorConfigurationController extends BaseController { 

    @Autowired private HbDirectorConfigService service;
    private final static Logger LOGGER = LoggerFactory.getLogger(HbDirectorConfigurationController.class);
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 디렉터 설정 화면 이동
     * @title : goListDirector
     * @return : String
    ***************************************************/
    @RequestMapping(value="/config/hbDirector", method=RequestMethod.GET)
    public String goHbDirector() {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> HB 디렉터 설정 화면 요청"); }
        return "/hbdeploy/config/hbListDirector";
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 설치 관리자 정보 목록 조회(전체)
     * @title : getHbDirectorListByType
     * @return : ResponseEntity<HashMap<String,Object>>
    ***************************************************/
    @RequestMapping(value="/config/hbDirector/list/{directorType}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getHbDirectorListByType(@PathVariable String directorType) {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=============================> HB 설치 관리자  정보 목록 조회 요청"); }
        HashMap<String, Object> listResult = new HashMap<String, Object>();
        List<HbDirectorConfigVO> contents = service.getDirectorList(directorType);
        int size = 0;
        if( contents != null ) {
            size = contents.size();
        }
        listResult.put("total", size);
        listResult.put("records", contents);
        return new ResponseEntity<HashMap<String, Object> >(listResult, HttpStatus.OK);
    }
    
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 디렉터 설정 추가
     * @title : createDirector
     * @return : ResponseEntity
    ***************************************************/
    @RequestMapping(value="/config/hbDirector/add", method=RequestMethod.POST)
    public ResponseEntity<Object> createHbDirector(@RequestBody @Valid HbDirectorConfigDTO directorDto, Principal principal) {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> HB 설치 관리자 설정 추가 요청"); }
        String boshConfigFileName = "config";
        service.existCheckCreateDirectorInfo(directorDto,principal,boshConfigFileName);
        
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 디렉터 설정 삭제
     * @title : deleteDirector
     * @return : ResponseEntity<Object>
    ***************************************************/
    @RequestMapping(value="/config/hbDirector/delete/{seq}", method=RequestMethod.DELETE)
    public ResponseEntity<Object> deleteHbDirector(@PathVariable int seq) {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================> HB 기본 설치 관리자 삭제 요청!!"); }
        String boshConfigFileName = "config";
        service.deleteDirectorConfig(seq, boshConfigFileName);
        return new ResponseEntity<> (HttpStatus.NO_CONTENT); 
    }

    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  Credential Key File 업로드
     * @title : doDirectorCredentialKeyFileUpload
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value="/config/hbDirector/credskey/upload", method=RequestMethod.POST)
    public ResponseEntity<?> doHbDirectorCredentialKeyFileUpload(MultipartHttpServletRequest request){
        if(LOGGER.isInfoEnabled()){ LOGGER.debug("====================================> HB credential 파일 업로드 조회 요청"); }
        service.uploadCredentialKeyFile(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
}