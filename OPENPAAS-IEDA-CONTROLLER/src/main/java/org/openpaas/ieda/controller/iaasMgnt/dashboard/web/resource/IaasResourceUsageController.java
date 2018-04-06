package org.openpaas.ieda.controller.iaasMgnt.dashboard.web.resource;

import java.security.Principal;
import java.util.List;

import org.openpaas.ieda.iaasDashboard.web.resourceUsage.dao.IaasResourceUsageVO;
import org.openpaas.ieda.iaasDashboard.web.resourceUsage.service.IaasResourceUsageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IaasResourceUsageController {
    
    @Autowired IaasResourceUsageService service; 
    private final static Logger LOGGER = LoggerFactory.getLogger(IaasResourceUsageController.class);

/***************************************************
 * @project : 인프라 관리 대시보드
 * @description : 대시보드 화면 호출
 * @title : goIaasDashboard
 * @return : String
***************************************************/
@RequestMapping(value="/iaasMgnt/main/dashboard", method=RequestMethod.GET)
public String goIaasResourceUsage() {
return "/iaas/resourceUsage/dashboard";
}

/***************************************************
 * @project : 인프라 관리 대시보드
 * @description : AWS 리소스 사용량 조회 화면 호출
 * @title : goAwsResourceUsage
 * @return : String
***************************************************/
@RequestMapping(value="/iaasMgnt/resourceUsage/aws", method=RequestMethod.GET)
public String goAwsResourceUsage(){
    return "/iaas/resourceUsage/awsResourceUsage";
}

/***************************************************
 * @project : 인프라 관리 대시보드
 * @description : Openstack 리소스 사용량 조회 화면 호출
 * @title : goOpenstackResourceUsage
 * @return : String
***************************************************/
@RequestMapping(value="/iaasMgnt/resourceUsage/openstack", method=RequestMethod.GET)
    public String goOpenstackResourceUsage(){
        return "/iaas/resourceUsage/openstackResourceUsage";
    }

/***************************************************
 * @project : 인프라 관리 대시보드
 * @description : Azure 리소스 사용량 조회 화면 호출
 * @title : goAzureResourceUsage
 * @return : String
***************************************************/
@RequestMapping(value="/iaasMgnt/resourceUsage/azure", method=RequestMethod.GET)
    public String goAzureResourceUsage(){
        return "/iaas/resourceUsage/azureResourceUsage";
    }

/***************************************************
 * @project : 인프라 관리 대시보드
 * @description : 인프라 전체 리소스 사용량 조회
 * @title : getIaasResourceUsageTotalInfo
 * @return : ResponseEntity<List<IaasResourceUsageVO>>
***************************************************/
@RequestMapping(value="/iaasMgnt/resourceUsage/all/list", method=RequestMethod.GET)
    public ResponseEntity<List<IaasResourceUsageVO>>  getIaasResourceUsageTotalInfo(Principal principal){
    if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> /iaasMgnt/resource/all/list"); }
    List<IaasResourceUsageVO> list = service.getIaasResourceUsageTotalInfo(principal);
    return new ResponseEntity<List<IaasResourceUsageVO>>(list, HttpStatus.OK);
    }

/***************************************************
 * @project : 인프라 관리 대시보드
 * @description : AWS 리소스 사용량 조회
 * @title : getAwsResourceUsageInfoList
 * @return : ResponseEntity<List<IaasResourceUsageVO>>
***************************************************/
@RequestMapping(value="/iaasMgnt/resourceUsage/aws/list/{region}", method=RequestMethod.GET)
public ResponseEntity<List<IaasResourceUsageVO>>  getAwsResourceUsageInfoList(@PathVariable String region, Principal principal){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> /iaasMgnt/resourceUsage/aws/list"); }
        List<IaasResourceUsageVO> list =  service.getAwsResourceUsageInfoList(region, principal);
        return new ResponseEntity<List<IaasResourceUsageVO>>(list, HttpStatus.OK);
    }

/***************************************************
 * @project : 인프라 관리 대시보드
 * @description : Openstack 리소스 사용량 조회
 * @title : getOpenstackResourceUsageInfoList
 * @return : ResponseEntity<List<IaasResourceUsageVO>>
***************************************************/
@RequestMapping(value="/iaasMgnt/resourceUsage/openstack/list", method=RequestMethod.GET)
    public ResponseEntity<List<IaasResourceUsageVO>>  getOpenstackResourceUsageInfoList(Principal principal){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> /iaasMgnt/resourceUsage/openstack/list"); }
        List<IaasResourceUsageVO> list =  service.getOpenstackResourceUsageInfoList(principal);
        return new ResponseEntity<List<IaasResourceUsageVO>>(list, HttpStatus.OK);
    }

/***************************************************
 * @project : 인프라 관리 대시보드
 * @description : Azure 리소스 사용량 조회
 * @title : getAzureResourceUsageInfoList
 * @return : ResponseEntity<List<IaasResourceUsageVO>>
***************************************************/
@RequestMapping(value="/iaasMgnt/resourceUsage/azure/list", method=RequestMethod.GET)
public ResponseEntity<List<IaasResourceUsageVO>>  getAzureResourceUsageInfoList(Principal principal){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> /iaasMgnt/resourceUsage/azure/list"); }
        List<IaasResourceUsageVO> list =  service.getAzureResourceUsageInfoList(principal);
        return new ResponseEntity<List<IaasResourceUsageVO>>(list, HttpStatus.OK);
    }
    
}
