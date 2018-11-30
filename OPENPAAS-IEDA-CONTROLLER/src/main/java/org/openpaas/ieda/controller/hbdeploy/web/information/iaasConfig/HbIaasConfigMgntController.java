package org.openpaas.ieda.controller.hbdeploy.web.information.iaasConfig;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.openpaas.ieda.controller.deploy.web.information.iaasConfig.IaasConfigMgntController;
import org.openpaas.ieda.hbdeploy.web.information.iaasConfig.dao.HbIaasConfigMgntVO;
import org.openpaas.ieda.hbdeploy.web.information.iaasConfig.dto.HbIaasConfigMgntDTO;
import org.openpaas.ieda.hbdeploy.web.information.iaasConfig.service.HbIaasConfigMgntService;
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
public class HbIaasConfigMgntController {

    @Autowired HbIaasConfigMgntService service;
    private final static Logger LOGGER = LoggerFactory.getLogger(IaasConfigMgntController.class);
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 환경 설정관리 화면 이동
     * @title : goIaasConfigMgnt
     * @return : String
    ***************************************************/
    @RequestMapping(value="/info/hbIaasConfig", method=RequestMethod.GET)
    public String goIaasConfigInfo(){       
        return "/hbdeploy/information/iaasConfig/iaasConfigMgnt";
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : AWS 환경 설정 관리 화면 이동
     * @title : goAwsConfigInfo
     * @return : String
    ***************************************************/
    @RequestMapping(value="/info/hbIaasConfig/aws", method=RequestMethod.GET)
    public String goAwsConfigInfo(){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> /info/hbIaasConfig/aws "); }
        return "/hbdeploy/information/iaasConfig/awsConfigMgnt";
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : openstack 환경 설정 관리 화면 이동
     * @title : goOpenstackConfigInfo
     * @return : String
    ***************************************************/
    @RequestMapping(value="/info/hbIaasConfig/openstack", method=RequestMethod.GET)
    public String goOpenstackConfigInfo(){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> /info/hbIaasConfig/openstack "); }
        return "/hbdeploy/information/iaasConfig/openstackConfigMgnt";
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 전체 환경 설정 목록 정보 요청
     * @title : getAllIaasConfigInfoList
     * @return : ResponseEntity<HashMap<String,Object>>
    ***************************************************/
    @RequestMapping(value="/info/hbIaasConfig/all/list", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getAllIaasConfigInfoList(Principal principal){
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("================================================> /info/hbIaasConfig/all/list");
        }
        //전체 환경 설정 목록 조회
        List<HbIaasConfigMgntVO> allIaasConfigInfoList = service.getAllIaasConfigInfoList(principal);
        HashMap<String, Object> list = new HashMap<String, Object>();
        int size = 0;
        if ( allIaasConfigInfoList != null ) {
            size = allIaasConfigInfoList.size();
        }
        list.put("total", size);
        list.put("records", allIaasConfigInfoList);
    
        return new ResponseEntity<HashMap<String, Object>>(list, HttpStatus.OK);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인프라 별 환경 설정 개수 조회
     * @title : getAllIaasConfigCountInfo
     * @return : ResponseEntity<HashMap<String,Object>>
    ***************************************************/
    @RequestMapping(value="/info/hbIaasConfig/all/cnt", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Integer>> getAllIaasConfigCountInfo(Principal principal){
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("================================================> /info/hbIaasConfig/all/cnt");
        }
        HashMap<String, Integer> cnt = service.getIaasConfigCount(principal);
        return new ResponseEntity<HashMap<String, Integer>>(cnt, HttpStatus.OK);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인프라 환경 설정 목록 조회(AWS/Openstack)
     * @title : getIaasConfigInfoList
     * @return : ResponseEntity<HashMap<String,Object>>
    ***************************************************/
    @RequestMapping(value="/info/hbIaasConfig/{iaasType}/list", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getIaasConfigInfoList(@PathVariable("iaasType") String iaasType, Principal principal){
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("================================================> /info/hbIaasConfig/"+iaasType+"/list");
        }
        
        List<HbIaasConfigMgntVO> list = service.getIaasConfigInfoList(iaasType, principal);
        HashMap<String, Object> result = new HashMap<String, Object>();
        int size = 0;
        if (list != null) {
            size = list.size();
        }
        result.put("total", size);
        result.put("records", list);
        return new ResponseEntity<HashMap<String, Object>>(result, HttpStatus.OK);
    }

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 환경 설정 정보 저장/수정
     * @title : saveIaasConfigInfo
     * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value="/info/hbIaasConfig/{iaasType}/save", method=RequestMethod.PUT)
    public  ResponseEntity<?> saveIaasConfigInfo(Principal principal, @PathVariable("iaasType") String iaasType, @RequestBody @Valid HbIaasConfigMgntDTO dto){
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("================================================> /info/hbIaasConfig/"+iaasType+"/save");
        }
        service.saveIaasConfigInfo(iaasType, dto, principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  환경 설정 정보 상세 조회
     * @title : getIaasConifgInfo
     * @return : ResponseEntity<HbIaasConfigMgntVO>
    ***************************************************/
    @RequestMapping(value="/info/hbIaasConfig/{iaasType}/save/detail/{id}", method=RequestMethod.GET)
    public ResponseEntity<HbIaasConfigMgntVO> getIaasConfigInfo(@PathVariable("iaasType") String iaasType, @PathVariable("id") int id, Principal principal){
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("================================================> /info/hbIaasConfig/"+iaasType+"/save/detail/"+id);
        }
        HbIaasConfigMgntVO iaasInfo = service.getIaasConfigInfo(iaasType, id, principal);
        return new ResponseEntity<>(iaasInfo,HttpStatus.OK);
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 환경 설정 정보 삭제
     * @title : deleteIaasConfigInfo
     * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value="/info/hbIaasConfig/{iaasType}/delete", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteIaasConfigInfo(@PathVariable("iaasType") String iaasType, @RequestBody @Valid HbIaasConfigMgntDTO dto, Principal principal){
        if (LOGGER.isInfoEnabled()){LOGGER.info("================================================> /info/hbIaasConfig/"+iaasType+"/delete");}
        service.deleteIaasConfigInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
