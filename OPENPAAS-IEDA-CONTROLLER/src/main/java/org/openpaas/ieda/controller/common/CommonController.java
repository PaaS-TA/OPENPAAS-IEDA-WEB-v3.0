package org.openpaas.ieda.controller.common;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.common.web.common.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.amazonaws.regions.Region;

@Controller
public class CommonController extends BaseController{
    
    @Autowired CommonService commonservice;
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 공통 인프라 계정 정보 조회
    * @title : getIaasAccountInfoList
    * @return : ResponseEntity<HashMap<String, Object>>
    ***************************************************/
    @RequestMapping(value="/common/deploy/accountList/{iaasType}", method=RequestMethod.GET)
    public ResponseEntity<List<HashMap<String, Object>>> getIaasAccountInfoList(@PathVariable String iaasType, Principal principal){
        List<HashMap<String, Object>> iaasAccountInfoList = commonservice.getIaasAccountInfoList(iaasType, principal);
        return new ResponseEntity<List<HashMap<String, Object>>>(iaasAccountInfoList, HttpStatus.OK);
    }
 
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : AWS 리전 목록 조회
     * @title : getAwsRegionInfoLIst
     * @return : ResponseEntity<List<Region>>
    ***************************************************/
    @RequestMapping(value="/common/aws/region/list", method=RequestMethod.GET)
    public ResponseEntity<List<Region>> getAwsRegionInfoList(){
        List<Region> awsRegionList= commonservice.getAWSRegionList();
        return new ResponseEntity<List<Region>>(awsRegionList, HttpStatus.OK);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : AWS 가용영역 목록 조회
     * @title : getAwsAvaliabilityZoneInfoList
     * @return : ResponseEntity<List<Region>>
    ***************************************************/
    @RequestMapping(value="/common/aws/avaliabilityzone/list/{accountId}/{region}", method=RequestMethod.GET)
    public ResponseEntity<List<String>> getAwsAvailabilityZoneInfoList(Principal principal, @PathVariable int accountId, @PathVariable String region ){
        List<String> avaliabilityzones= commonservice.getAWSAvailabilityZoneByRegion(principal, accountId, region);
        return new ResponseEntity<List<String>>(avaliabilityzones, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Google 클라우드 영역 목록 조회
     * @title : getGoogleZoneInfoList
     * @return : ResponseEntity<List<String>>
    *****************************************************************/
    @RequestMapping(value="/common/google/zone/list/{accountId}", method=RequestMethod.GET)
    public ResponseEntity<List<String>> getGoogleZoneInfoList(Principal principal, @PathVariable int accountId){
        List<String> zones = commonservice.getGoogleZoneList(principal, accountId);
        return new ResponseEntity<List<String>>(zones, HttpStatus.OK);
    }
}
