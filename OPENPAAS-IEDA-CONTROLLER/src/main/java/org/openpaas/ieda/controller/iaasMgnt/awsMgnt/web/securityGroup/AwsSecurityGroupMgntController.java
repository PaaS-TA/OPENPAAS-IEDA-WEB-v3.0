package org.openpaas.ieda.controller.iaasMgnt.awsMgnt.web.securityGroup;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.awsMgnt.web.securityGroup.dao.AwsSecurityGroupMgntVO;
import org.openpaas.ieda.awsMgnt.web.securityGroup.dto.AwsSecurityGroupMgntDTO;
import org.openpaas.ieda.awsMgnt.web.securityGroup.service.AwsSecurityGroupMgntService;
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
public class AwsSecurityGroupMgntController {
    
    @Autowired AwsSecurityGroupMgntService awsSecurityGroupMgntService;
    @Autowired AwsVpcMgntService vpcService;
    
    private final static Logger LOG = LoggerFactory.getLogger(AwsSecurityGroupMgntController.class);
    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : AWS Security Group관리 화면 이동
    * @title : goAwsSecurityGroupMgnt
    * @return : String
    ***************************************************/
    @RequestMapping(value="/awsMgnt/securityGroup", method=RequestMethod.GET)
    public String goAwsSecurityGroupMgnt(){
        return "iaas/aws/securityGroup/awsSecurityGroupMgnt";
    }
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : AWS Security Group 목록 조회
     * @title : getAwsSecurityGroupInfoList
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/awsMgnt/securityGroup/list/{accountId}/{region}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getAwsSecurityGroupInfoList(Principal principal, @PathVariable int accountId, @PathVariable String region){
        List<AwsSecurityGroupMgntVO> list = awsSecurityGroupMgntService.getAwsSecurityGroupInfoList(principal,accountId, region);
        HashMap<String, Object> map = new HashMap<String, Object>();
        if(list != null){
            map.put("total", list.size());
            map.put("records", list);
        }
        
        return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
    }
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : AWS Security Group Rules 조회
     * @title : getAwsSecurityGroupRules
     * @return : ResponseEntity<HashMap<String,Object>>
    ***************************************************/
    @RequestMapping(value="/awsMgnt/securityGroup/ingress/list/{accountId}/{groupId:.*}/{region}", method=RequestMethod.GET)
    public ResponseEntity<List<HashMap<String, Object>>> getAwsSecurityGroupRules(@PathVariable int accountId, @PathVariable String groupId, @PathVariable String region, Principal principal){
        List<HashMap<String, Object>> info = awsSecurityGroupMgntService.getAwsSecurityGroupRules(accountId, groupId, region, principal);
        return new ResponseEntity<List<HashMap<String, Object>>>(info, HttpStatus.OK);
    }
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : AWS Security Group 생성
     * @title : saveAwsSecurityGroupInfo
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value = "/awsMgnt/securityGroup/save", method = RequestMethod.POST)
    public ResponseEntity<?> saveAwsSecurityGroupInfo(@RequestBody AwsSecurityGroupMgntDTO dto, Principal principal){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> AWS Security Group 생성");
        }
        awsSecurityGroupMgntService.saveAwsSecurityGroupInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    /****************************************************************
     * @project : AWS 관리 대시보드
     * @description : 보안 그룹 삭제
     * @title : deleteAwsSecurityGroupInfo
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value="/awsMgnt/securityGroup/delete", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteAwsSecurityGroupInfo(@RequestBody AwsSecurityGroupMgntDTO dto, Principal principal){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> AWS Security Group 삭제");
        }
        awsSecurityGroupMgntService.deleteAwsSecurityGroupInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : AWS VPC 목록 조회
     * @title : getAwsVpc
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/awsMgnt/securityGroup/save/vpcs/{accountId}/{region}", method=RequestMethod.GET)
    public ResponseEntity<List<AwsVpcMgntVO>> getAwsVpc(Principal principal, @PathVariable int accountId, @PathVariable String region){
        List<AwsVpcMgntVO> vpcs= vpcService.getAwsVpcInfoList(accountId, region, principal);
        return new ResponseEntity<List<AwsVpcMgntVO>> (vpcs, HttpStatus.CREATED);
    }
}