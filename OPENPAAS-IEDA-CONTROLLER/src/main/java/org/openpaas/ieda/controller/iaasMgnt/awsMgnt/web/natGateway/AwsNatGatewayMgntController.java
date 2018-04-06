package org.openpaas.ieda.controller.iaasMgnt.awsMgnt.web.natGateway;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.awsMgnt.web.natGateway.dao.AwsNatGatewayMgntVO;
import org.openpaas.ieda.awsMgnt.web.natGateway.dto.AwsNatGatewayMgntDTO;
import org.openpaas.ieda.awsMgnt.web.natGateway.service.AwsNatGatewayMgntService;
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
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AwsNatGatewayMgntController {

	@Autowired AwsNatGatewayMgntService awsNatGatewayMgntService;
	private final static Logger LOG = LoggerFactory.getLogger(AwsNatGatewayMgntController.class);
	
	/****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : NAT Gateway 화면 이동
     * @title : goAwsNatGatewayMgnt
     * @return : String
    *****************************************************************/
    @RequestMapping(value="/awsMgnt/natGateway", method=RequestMethod.GET)
    public String goAwsNatGatewayMgnt(){
        return "iaas/aws/natGateway/awsNatGatewayMgnt";
    }
    
    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS NAT Gateway 목록 조회
     * @title : getAwsNatGatewayInfoList
     * @return : ResponseEntity<HashMap<String, Object>>
     ***************************************************/
     @RequestMapping(value="/awsMgnt/natGateway/list/{accountId}/{region}", method=RequestMethod.GET)
     @ResponseBody
     public ResponseEntity<HashMap<String, Object>> getAwsNatGatewayInfoList(@PathVariable("accountId") int accountId, @PathVariable("region") String region, Principal principal){
         if (LOG.isInfoEnabled()) {
             LOG.info("================================================> AWS NAT Gateway 목록 조회");
         }
         HashMap<String, Object> map = new HashMap<String, Object>();
          
         List<AwsNatGatewayMgntVO> list = awsNatGatewayMgntService.getAwsNatGatewayInfoList(accountId ,region, principal);
         if(list != null){
             map.put("total", list.size());
             map.put("records", list);
         }
         return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
     }
     
     /***************************************************
      * @project : AWS 인프라 관리 대시보드
      * @description : AWS Subnet List 조회
      * @title : getAwsSubnetList
      * @return : ResponseEntity<AwsNatGatewayMgntVO> 
      ***************************************************/
      @RequestMapping(value="/awsMgnt/natGateway/list/subnetIdList/{accountId}/{region}", method=RequestMethod.GET)
      @ResponseBody
      public ResponseEntity<List<AwsNatGatewayMgntVO>> getAwsSubnetList(@PathVariable("accountId") int accountId, @PathVariable("region") String region,  Principal principal){
          if (LOG.isInfoEnabled()) {
              LOG.info("================================================> AWS Subnet 목록 조회");
          }
          List<AwsNatGatewayMgntVO> list = awsNatGatewayMgntService.getAwsSubnetList(accountId, region, principal);
          return new ResponseEntity<List<AwsNatGatewayMgntVO>>(list,HttpStatus.OK);
      }
      
      /***************************************************
       * @project : AWS 인프라 관리 대시보드
       * @description : AWS Allocation ID / Elistic Ip List 조회
       * @title : getAwsEipAllocationIdList
       * @return : ResponseEntity<AwsNatGatewayMgntVO> 
       ***************************************************/
      @RequestMapping(value="/awsMgnt/natGateway/list/eipAllocationIdList/{accountId}/{region}", method=RequestMethod.GET)
      @ResponseBody
      public ResponseEntity<List<AwsNatGatewayMgntVO>> getAwsEipAllocationIdList(@PathVariable("accountId") int accountId, @PathVariable("region") String region,  Principal principal){
      if (LOG.isInfoEnabled()) {
          LOG.info("================================================> AWS eIP Allocation ID 목록 조회");
      }
	      List<AwsNatGatewayMgntVO> list = awsNatGatewayMgntService.getAwsEipAllocationIdList(accountId, region, principal);
	      return new ResponseEntity<List<AwsNatGatewayMgntVO>>(list,HttpStatus.OK);
	  }
      
      
      /***************************************************
       * @project : AWS 인프라 관리 대시보드
       * @description : AWS Elastic IP 할당
       * @title : saveElasticIpInfo
       * @return : ResponseEntity<?>
       ***************************************************/
      @RequestMapping(value="/awsMgnt/natGateway/list/elasictIp/save", method=RequestMethod.POST)
      @ResponseBody
      public ResponseEntity<?> allocateNewElasticIp(@RequestBody AwsNatGatewayMgntDTO dto,  Principal principal){
    	  if (LOG.isInfoEnabled()) {
              LOG.info("================================================> AWS NEW Elastic IP 할당");
          }
    	  awsNatGatewayMgntService.allocateNewElasticIp(dto, principal);
          return new ResponseEntity<>(HttpStatus.CREATED);
      }
      
      /***************************************************
       * @project : AWS 인프라 관리 대시보드
       * @description : AWS Nat Gateway생성
       * @title : saveAwsNatGatewayInfo
       * @return : ResponseEntity<?>
       ***************************************************/
       @RequestMapping(value="/awsMgnt/natGateway/save", method=RequestMethod.POST)
       @ResponseBody
       public ResponseEntity<?> saveAwsNatGatewayInfo(@RequestBody AwsNatGatewayMgntDTO dto, Principal principal){
           if (LOG.isInfoEnabled()) {
               LOG.info("================================================> AWS NAT Gateway 생성");
           }
           awsNatGatewayMgntService.saveAwsNatGatewayInfo(dto, principal);
           return new ResponseEntity<>(HttpStatus.CREATED);
       }
      
      
}
