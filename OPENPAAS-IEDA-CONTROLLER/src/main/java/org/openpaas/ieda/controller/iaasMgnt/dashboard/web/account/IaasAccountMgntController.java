package org.openpaas.ieda.controller.iaasMgnt.dashboard.web.account;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.account.dto.IaasAccountMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.account.service.IaasAccountMgntService;
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
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Controller
public class IaasAccountMgntController{

    @Autowired
    IaasAccountMgntService service;
    private final static Logger LOGGER = LoggerFactory.getLogger(IaasAccountMgntController.class);

    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Iaas Account 관리 화면 이동
     * @title : goIaasAccountMgnt
     * @return : String
     ***************************************************/
    @RequestMapping(value = "/iaasMgnt/account", method = RequestMethod.GET)
    public String goIaasAccountMgnt() {
        return "/iaas/account/iaasAccount";
    }

    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : AWS 계정 관리 화면 이동
     * @title : goAwsAccountMgnt
     * @return : String
     ***************************************************/
    @RequestMapping(value = "/iaasMgnt/account/aws", method = RequestMethod.GET)
    public String goAwsAccountMgnt() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("=====================> /iaasMgnt/account/aws ");
        }
        return "/iaas/account/awsAccount";
    }

    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 오픈스택 계정 관리 화면 이동
     * @title : goOpenstackAccountMgnt
     * @return : String
     ***************************************************/
    @RequestMapping(value = "/iaasMgnt/account/openstack", method = RequestMethod.GET)
    public String goOpenstackAccountMgnt() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("=====================> /iaasMgnt/account/openstack ");
        }
        return "/iaas/account/openstackAccount";
    }

    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Google 계정 관리 화면 이동
     * @title : goGoogleAccountMgnt
     * @return : String
     ***************************************************/
    @RequestMapping(value = "/iaasMgnt/account/google", method = RequestMethod.GET)
    public String goGoogleAccountMgnt() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("=====================> /iaasMgnt/account/google ");
        }
        return "/iaas/account/googleAccount";
    }

    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : vSphere 계정 관리 화면 이동
     * @title : goVsphereAccountMgnt
     * @return : String
     ***************************************************/
    @RequestMapping(value = "/iaasMgnt/account/vSphere", method = RequestMethod.GET)
    public String goVsphereAccountMgnt() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("=====================> /iaasMgnt/account/vSphere ");
        }
        return "/iaas/account/vSphereAccount";
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure 계정 관리 화면 이동
     * @title : goAzureAccountMgnt
     * @return : String
     ***************************************************/
    @RequestMapping(value = "/iaasMgnt/account/azure", method = RequestMethod.GET)
    public String goAzureAccountMgnt() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("=====================> /iaasMgnt/account/azure ");
        }
        return "/iaas/account/azureAccount";
    }

    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 전체 Iaas Account 목록 정보 요청
     * @title : getAllIaasAccountInfoList
     * @return : ResponseEntity<HashMap<String,Object>>
     ***************************************************/
    @RequestMapping(value = "/iaasMgnt/account/all/list", method = RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getAllIaasAccountInfoList(Principal principal) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("=====================> /iaasMgnt/account/all/list");
        }
        // 전체 인프라 계정 목록 조회
        List<IaasAccountMgntVO> allIaasAccountInfoList = service.getAllIaasAccountInfoList(principal);
        HashMap<String, Object> list = new HashMap<String, Object>();
        int size = 0;
        if (allIaasAccountInfoList != null) {
            size = allIaasAccountInfoList.size();
        }
        list.put("total", size);
        list.put("records", allIaasAccountInfoList);

        return new ResponseEntity<HashMap<String, Object>>(list, HttpStatus.OK);
    }

    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 인프라 별 계정 개수 조회
     * @title : getAllIaasAccountCountInfo
     * @return : ResponseEntity<HashMap<String,Object>>
     ***************************************************/
    @RequestMapping(value = "/iaasMgnt/account/all/cnt", method = RequestMethod.GET)
    public ResponseEntity<HashMap<String, Integer>> getAllIaasAccountCountInfo(Principal principal) {
        if (LOGGER.isInfoEnabled()) {  LOGGER.info("=====================> /iaasMgnt/account/all/cnt"); }
        HashMap<String, Integer> cnt = service.getIaasAccountCount(principal);
        return new ResponseEntity<HashMap<String, Integer>>(cnt, HttpStatus.OK);
    }

    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 인프라 계정 목록 조회(AWS/Openstack/google/vSphere/Azure)
     * @title : getIaasAccountInfoList
     * @return : ResponseEntity<HashMap<String,Object>>
     ***************************************************/
    @RequestMapping(value = "/iaasMgnt/account/{iaasType}/list", method = RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getIaasAccountInfoList(HttpServletRequest request,
            @PathVariable("iaasType") String iaasType, Principal principal) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("=====================> /iaasMgnt/account/" + iaasType + "/list ");
           
        }
        List<IaasAccountMgntVO> list = service.getIaasAccountInfoList(iaasType, principal);
        CommonIaasService common = new CommonIaasService();
        LOGGER.info("=====================> test" + iaasType + "");
        HashMap<String, Object> map = common.getPublicKey(request);

        HashMap<String, Object> result = new HashMap<String, Object>();
        int size = 0;
        if (list != null) {
            size = list.size();
            LOGGER.info("=====================> test" + size + "");
        }
       
        result.put("total", size);
        result.put("records", list);
        result.put("publicKeyModulus", map.get("publicKeyModulus"));
        result.put("publicKeyExponent", map.get("publicKeyExponent"));
        return new ResponseEntity<HashMap<String, Object>>(result, HttpStatus.OK);

    }

    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 인프라 별 계정 상세 정보 조회
     * @title : getIaasAccountInfo
     * @return : ResponseEntity<IaasAccountMgntVO>
     ***************************************************/
    @RequestMapping(value = "/iaasMgnt/account/{iaasType}/save/detail/{id}", method = RequestMethod.GET)
    public ResponseEntity<IaasAccountMgntVO> getIaasAccountInfo(@PathVariable("iaasType") String iaasType,
            @PathVariable("id") int id, Principal principal) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("=====================> /iaasMgnt/account/" + iaasType + "/save/detail/" + id);
        }
        IaasAccountMgntVO vo = service.getIaasAccountInfo(iaasType, id, principal);
        return new ResponseEntity<IaasAccountMgntVO>(vo, HttpStatus.OK);
    }

    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 인프라 계정 정보 저장/수정
     * @title : saveIaasAccountInfo
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value = "/iaasMgnt/account/{iaasType}/save", method = RequestMethod.PUT)
    public ResponseEntity<?> saveIaasAccountInfo(HttpServletRequest request, Principal principal,
            @PathVariable("iaasType") String iaasType, @RequestBody @Valid IaasAccountMgntDTO dto) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("================================================> /iaasMgnt/account/"+iaasType+"/save");
        }
        service.saveIaasAccountInfo(iaasType, dto, request, principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Iaas Account 정보 삭제
     * @title : deleteIaasAccountInfo
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value = "/iaasMgnt/account/{iaasType}/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteIaasAccountInfo(@PathVariable("iaasType") String iaasType, Principal principal,
            @RequestBody @Valid IaasAccountMgntDTO dto) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("================================================>/iaasMgnt/account/"+iaasType+"/delete");
        }
        service.deleteIaasAccountInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : google json 키 파일 목록 조회
     * @title : getJsonKeyPathFileList
     * @return : ResponseEntity<List<String>>
    *****************************************************************/
    @RequestMapping(value="/iaasMgnt/account/key/list", method=RequestMethod.GET)
    public ResponseEntity<List<String>> getJsonKeyPathFileList (){
        if (LOGGER.isInfoEnabled()) { LOGGER.info("================================================> /iaasMgnt/account/key/list"); }
        List<String> keyPathFileList = service.getJsonKeyFileList();
        return new ResponseEntity<List<String>>(keyPathFileList, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : google json 키 파일 업로드
     * @title : uploadJsonKeyPathFile
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value="/iaasMgnt/account/key/upload", method=RequestMethod.POST)
    public ResponseEntity<?> uploadJsonKeyPathFile( MultipartHttpServletRequest request){
        if(LOGGER.isInfoEnabled()){ LOGGER.debug("====================================>/iaasMgnt/account/key/upload"); }
        service.uploadJsonKeyFile(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
