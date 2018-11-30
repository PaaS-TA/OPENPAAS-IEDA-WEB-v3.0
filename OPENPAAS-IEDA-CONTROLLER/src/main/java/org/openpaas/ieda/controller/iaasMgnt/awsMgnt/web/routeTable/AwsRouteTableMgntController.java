package org.openpaas.ieda.controller.iaasMgnt.awsMgnt.web.routeTable;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.controller.iaasMgnt.awsMgnt.web.vpc.AwsVpcMgntController;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.routeTable.dao.AwsRouteTableMgntVO;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.routeTable.dto.AwsRouteTableMgntDTO;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.routeTable.service.AwsRouteTableMgntService;
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
public class AwsRouteTableMgntController {
    @Autowired AwsRouteTableMgntService awsRouteTableMgntService;
    private final static Logger LOG = LoggerFactory.getLogger(AwsVpcMgntController.class);
     
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Elastic Ip 화면 이동
     * @title : goAwsElasticIpMgnt
     * @return : String
    *****************************************************************/
    @RequestMapping(value="/awsMgnt/routeTable", method=RequestMethod.GET)
    public String goAwsElasticIpMgnt(){
         if (LOG.isInfoEnabled()) {
             LOG.info("================================================> AWS Route Table 관리 화면 이동");
         }
        return "iaas/aws/routeTable/awsRouteTableMgnt";
    }
    
    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS RouteTable 목록 조회
     * @title : getAwsRouteTableInfoList
     * @return : ResponseEntity<HashMap<String, Object>>
     ***************************************************/
     @RequestMapping(value="/awsMgnt/routeTable/list/{accountId}/{region}", method=RequestMethod.GET)
     @ResponseBody
     public ResponseEntity<HashMap<String, Object>> getAwsRouteTableInfoList(@PathVariable("accountId") int accountId, @PathVariable("region") String region, Principal principal){
         if (LOG.isInfoEnabled()) {
             LOG.info("================================================> AWS Route Table 목록 조회");
         }
         HashMap<String, Object> map = new HashMap<String, Object>();
          
         List<AwsRouteTableMgntVO> list = awsRouteTableMgntService.getAwsRouteTableInfoList(accountId ,region, principal);
         if(list != null){
             map.put("total", list.size());
             map.put("records", list);
         }
         return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
     }

     /***************************************************
      * @project : AWS 관리 대시보드
      * @description : AWS Route 목록 조회 (화면 하단)
      * @title : getAwsRouteList
      * @return : ResponseEntity<?>
      ***************************************************/
     @RequestMapping(value="/awsMgnt/routeTable/save/detail/route/{accountId}/{region}/{routeTableId}", method=RequestMethod.GET)
     @ResponseBody
     public ResponseEntity<HashMap<String, Object>> getAwsRouteList(@PathVariable("accountId") int accountId, @PathVariable("region") String region, Principal principal, @PathVariable("routeTableId") String routeTableId){
         
         List<AwsRouteTableMgntVO> list  = awsRouteTableMgntService.getAwsRouteList(accountId, region, principal, routeTableId);
         HashMap<String, Object> map = new HashMap<String, Object>();
         if(list != null){
             map.put("total", list.size());
             map.put("records", list);
         }
         return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
     }     
     
     /***************************************************
      * @project : AWS 관리 대시보드
      * @description : AWS RouteTable에 대한 associated Subnets 목록 조회 (화면 하단)
      * @title : getAwsAssociatedSubnetList
      * @return : ResponseEntity<?>
      ***************************************************/
     @RequestMapping(value="/awsMgnt/routeTable/list/detail/subnet/associated/{accountId}/{region}/{routeTableId}/{vpcId}", method=RequestMethod.GET)
     @ResponseBody
     public ResponseEntity<HashMap<String, Object>> getAwsAssociatedSubnetList(@PathVariable("accountId") int accountId, @PathVariable("region") String region, Principal principal, @PathVariable("routeTableId") String routeTableId, @PathVariable("vpcId") String vpcId){
         List<AwsRouteTableMgntVO> list  = awsRouteTableMgntService.getAwsAssociatedWithThisTableSubnetList(accountId, region, principal, routeTableId, vpcId);
         HashMap<String, Object> map = new HashMap<String, Object>();
          if(list != null){
              map.put("total", list.size());
              map.put("records", list);
          }
          return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
      }
     
     /***************************************************
      * @project : AWS 관리 대시보드
      * @description : AWS RouteTable의 해당 VPC에 대한 Association 가능한 Subnets 목록 조회 (화면 하단)
      * @title :getAwsAvailableSubnetList
      * @return : ResponseEntity<?>
      ***************************************************/
     @RequestMapping(value="/awsMgnt/routeTable/list/avaliable/subnets/{accountId}/{region}/{routeTableId}/{vpcId}", method=RequestMethod.GET)
     @ResponseBody
     public ResponseEntity<List<AwsRouteTableMgntVO>> getAwsAvailableSubnetList(@PathVariable("accountId") int accountId, @PathVariable("region") String region, Principal principal, @PathVariable("routeTableId") String routeTableId, @PathVariable("vpcId") String vpcId){
         List<AwsRouteTableMgntVO> list  = awsRouteTableMgntService.getAwsAvailableSubnetList(accountId, region, principal, routeTableId, vpcId);
         return new ResponseEntity<List<AwsRouteTableMgntVO>>(list, HttpStatus.OK);
     }
 
     /***************************************************
      * @project : AWS 인프라 관리 대시보드
      * @description : AWS VPC ID List 조회
      * @title : getAwsVpcIdList
      * @return : ResponseEntity<AwsRouteTableMgntVO> 
      ***************************************************/
     @RequestMapping(value="/awsMgnt/routeTable/vpcIdList/{accountId}/{region}", method=RequestMethod.GET)
     @ResponseBody
     public ResponseEntity<List<AwsRouteTableMgntVO>> getAwsVpcIdList (@PathVariable("accountId") int accountId, @PathVariable("region") String region,  Principal principal){
         if (LOG.isInfoEnabled()) {
             LOG.info("================================================> AWS VPC ID 목록 조회");
         }
         List<AwsRouteTableMgntVO> list = awsRouteTableMgntService.getAwsVpcIdList(accountId, region, principal);
         return new ResponseEntity<List<AwsRouteTableMgntVO>>(list,HttpStatus.OK);
     }
     
     /***************************************************
      * @project : AWS 인프라 관리 대시보드
      * @description : AWS Target List 조회
      * @title : getAwsTargetIdList
      * @return : ResponseEntity<AwsRouteTableMgntVO> 
      ***************************************************/
     @RequestMapping(value="/awsMgnt/routeTable/route/list/targetList/{accountId}/{region}/{vpcId}", method=RequestMethod.GET)
     @ResponseBody
     public ResponseEntity<ArrayList<String>> getAwsTargetInfoList(@PathVariable("accountId") int accountId, @PathVariable("region") String region,  Principal principal, @PathVariable("vpcId") String vpcId){
         if (LOG.isInfoEnabled()) {
             LOG.info("================================================> AWS Target 목록 조회");
         }
         ArrayList<String> list = awsRouteTableMgntService.getAwsTargetInfoList(accountId, region, principal, vpcId);
         return new ResponseEntity<ArrayList<String>>(list,HttpStatus.OK);
     }
     /***************************************************
      * @project : AWS 인프라 관리 대시보드
      * @description : AWS Route Table생성
      * @title : saveAwsRouteTableInfo
      * @return : ResponseEntity<?>
      ***************************************************/
     @RequestMapping(value="/awsMgnt/routeTable/save", method=RequestMethod.POST)
     @ResponseBody
     public ResponseEntity<?> saveAwsRouteTableInfo(@RequestBody AwsRouteTableMgntDTO dto, Principal principal){
         if (LOG.isInfoEnabled()) {
             LOG.info("================================================> AWS RouteTable 생성");
         }  
         awsRouteTableMgntService.saveAwsRouteTableInfo(dto, principal);
         return new ResponseEntity<>(HttpStatus.CREATED);
     }
     
     /***************************************************
      * @project : AWS 인프라 관리 대시보드
      * @description : AWS delete RouteTable
      * @title : deleteRouteTable
      * @return : ResponseEntity<?>
      ***************************************************/
     @RequestMapping(value="/awsMgnt/routeTable/delete", method=RequestMethod.DELETE)
     @ResponseBody
     public ResponseEntity<?> deleteRouteTable (@RequestBody AwsRouteTableMgntDTO dto, Principal principal){
         if(LOG.isInfoEnabled()) {
             LOG.info("========================================> AWS Subnet 연결해제");
         }
         awsRouteTableMgntService.deleteRouteTable(dto, principal);
         return new ResponseEntity<>(HttpStatus.NO_CONTENT);
     }
     
     /***************************************************
      * @project : AWS 인프라 관리 대시보드
      * @description : AWS Route 추가
      * @title : addAwsRouteInfo
      * @return : ResponseEntity<?>
      ***************************************************/
     @RequestMapping(value="/awsMgnt/routeTable/route/add", method=RequestMethod.POST)
     @ResponseBody
     public ResponseEntity<?> addAwsRouteInfo(@RequestBody AwsRouteTableMgntDTO dto, Principal principal){
         if (LOG.isInfoEnabled()) {
             LOG.info("================================================> AWS Route 추가");
         }  
         awsRouteTableMgntService.addAwsRouteInfo(dto, principal);
         return new ResponseEntity<>(HttpStatus.CREATED);
     }

     /***************************************************
      * @project : AWS 인프라 관리 대시보드
      * @description : AWS Route 삭제
      * @title : deleteAwsRouteInfo
      * @return : ResponseEntity<?>
      ***************************************************/
     @RequestMapping(value="/awsMgnt/routeTable/route/delete", method=RequestMethod.DELETE)
     @ResponseBody
     public ResponseEntity<?> deleteAwsRouteInfo(@RequestBody AwsRouteTableMgntDTO dto, Principal principal){
         if (LOG.isInfoEnabled()) {
             LOG.info("================================================> AWS Route 삭제");
         }  
         awsRouteTableMgntService.deleteAwsRouteInfo(dto, principal);
         return new ResponseEntity<>(HttpStatus.NO_CONTENT);
     }
     
     /***************************************************
      * @project : AWS 인프라 관리 대시보드
      * @description : AWS associate Aws Subnet With RouteTable
      * @title : associateAwsSubnetWithRouteTable
      * @return : ResponseEntity<?>
      ***************************************************/
     @RequestMapping(value="/awsMgnt/routeTable/list/subnet/associate", method=RequestMethod.POST)
     @ResponseBody
     public ResponseEntity<?> associateAwsSubnetWithRouteTable (@RequestBody AwsRouteTableMgntDTO dto, Principal principal){
         if (LOG.isInfoEnabled()) {
             LOG.info("================================================> AWS Subnet 연결");
         }  
         awsRouteTableMgntService.associateAwsSubnetWithRouteTable(dto, principal);
         return new ResponseEntity<>(HttpStatus.CREATED);
     } 
    
     /***************************************************
      * @project : AWS 인프라 관리 대시보드
      * @description : AWS disassociate Aws Subnet from RouteTable
      * @title : disassociateAwsSubnetFromRouteTable
      * @return : ResponseEntity<?>
      ***************************************************/
      @RequestMapping(value="/awsMgnt/routeTable/list/subnet/disassociate", method=RequestMethod.DELETE)
      @ResponseBody
      public ResponseEntity<?> disassociateAwsSubnetFromRouteTable (@RequestBody AwsRouteTableMgntDTO dto, Principal principal){
          if(LOG.isInfoEnabled()) {
              LOG.info("========================================> AWS Subnet 연결해제");
          }
          awsRouteTableMgntService.disassociateAwsSubnetFromRouteTable(dto, principal);
          return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }
}
