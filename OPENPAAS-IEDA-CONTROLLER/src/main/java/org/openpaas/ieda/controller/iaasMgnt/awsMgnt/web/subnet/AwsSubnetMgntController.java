package org.openpaas.ieda.controller.iaasMgnt.awsMgnt.web.subnet;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.awsMgnt.web.subnet.dao.AwsSubnetMgntVO;
import org.openpaas.ieda.awsMgnt.web.subnet.dto.AwsSubnetMgntDTO;
import org.openpaas.ieda.awsMgnt.web.subnet.service.AwsSubnetMgntService;
import org.openpaas.ieda.awsMgnt.web.vpc.dao.AwsVpcMgntVO;
import org.openpaas.ieda.awsMgnt.web.vpc.service.AwsVpcMgntService;
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
public class AwsSubnetMgntController {
    
    @Autowired AwsSubnetMgntService awsSubnetMgntService;
    @Autowired AwsVpcMgntService vpsService;
    
    private final static Logger LOG = LoggerFactory.getLogger(AwsSubnetMgntController.class);
    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : AWS Subnet 관리 화면 이동
    * @title : goAwsSubnetMgnt
    * @return : String
    ***************************************************/
    @RequestMapping(value="/awsMgnt/subnet", method=RequestMethod.GET)
    public String goAwsSubnetMgnt(){
        return "iaas/aws/subnet/awsSubnetMgnt";
    }
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : AWS Subnet 목록 조회
     * @title : getAwsSubnetInfoList
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/awsMgnt/subnet/list/{accountId}/{region}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getAwsSubnetInfoList(Principal principal,@PathVariable int accountId, @PathVariable String region){
        HashMap<String, Object> map = new HashMap<String, Object>();
        List<AwsSubnetMgntVO> list = awsSubnetMgntService.getAwsSubnetInfoList(principal,accountId, region);
        if(list != null){
            map.put("total", list.size());
            map.put("records", list);
        }
        
        return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
    }
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : AWS Subnet 상세 조회
     * @title : getAwsSubnetDetailInfo
     * @return : ResponseEntity<HashMap<String,Object>>
    ***************************************************/
    @RequestMapping(value="/awsMgnt/subnet/save/detail/{accountId}/{subnetId:.*}/{region}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getAwsSubnetDetailInfo(@PathVariable int accountId, @PathVariable String subnetId,@PathVariable String region , Principal principal ){
        HashMap<String, Object> info = awsSubnetMgntService.getAwsSubnetDetailInfo(accountId, subnetId, principal, region);
        return new ResponseEntity<HashMap<String, Object>>(info, HttpStatus.OK);
    }
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : AWS Subnet 생성
     * @title : saveAwsSubnetInfo
     * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value = "/awsMgnt/subnet/save", method = RequestMethod.POST)
    public ResponseEntity<?> saveAwsSubnetInfo(@RequestBody AwsSubnetMgntDTO dto, Principal principal){
        
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> AWS SUBNET 생성");
        }
        awsSubnetMgntService.saveAwsSubnetInfo(dto, principal);
        
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : AWS Subnet 삭제
     * @title : deleteAwsSubnetInfo
     * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value="/awsMgnt/subnet/delete", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteAwsSubnetInfo(@RequestBody AwsSubnetMgntDTO dto, Principal principal){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> AWS SUBNET 삭제");
        }
        awsSubnetMgntService.deleteAwsSubnetInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : AWS Subnet 생성을 위한 VPC 조회
     * @title : getAwsVpc
     * @return : ResponseEntity<List<AwsVpcMgntVO> >
    ***************************************************/
    @RequestMapping(value="/awsMgnt/subnet/save/vpcs/{accountId}/{region}", method=RequestMethod.GET)
    public ResponseEntity<List<AwsVpcMgntVO>> getAwsVpc(Principal principal, @PathVariable int accountId, @PathVariable String region){
        List<AwsVpcMgntVO> vpcs= vpsService.getAwsVpcInfoList(accountId, region, principal);
        return new ResponseEntity<List<AwsVpcMgntVO>> (vpcs, HttpStatus.CREATED);
    }
    
}