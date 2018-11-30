package org.openpaas.ieda.controller.iaasMgnt.awsMgnt.web.vpc;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.iaasDashboard.awsMgnt.web.vpc.dao.AwsVpcMgntVO;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.vpc.dto.AwsVpcMgntDTO;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.vpc.service.AwsVpcMgntService;
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
public class AwsVpcMgntController {
    
    @Autowired AwsVpcMgntService awsVpcMgntService;
    private final static Logger LOG = LoggerFactory.getLogger(AwsVpcMgntController.class);
    
    /***************************************************
    * @project : AWS 인프라 관리 대시보드
    * @description : AWS VPC 관리 화면 이동
    * @title : goAwsVpcMgnt
    * @return : String
    ***************************************************/
    @RequestMapping(value="/awsMgnt/vpc", method=RequestMethod.GET)
    public String goAwsVpcMgnt(){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> AWS VPC 화면 이동");
        }
        return "iaas/aws/vpc/awsVpcMgnt";
    }
    
    /***************************************************
    * @project : AWS 인프라 관리 대시보드
    * @description : AWS VPC 목록 조회
    * @title : getAwsVpcInfoList
    * @return : ResponseEntity<HashMap<String, Object>>
    ***************************************************/
    @RequestMapping(value="/awsMgnt/vpc/list/{accountId}/{region}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getAwsVpcInfoList(Principal principal, @PathVariable int accountId, @PathVariable String region){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> AWS VPC 목록 조회");
        }
        HashMap<String, Object> map = new HashMap<String, Object>();
        List<AwsVpcMgntVO> list = awsVpcMgntService.getAwsVpcInfoList(accountId ,region, principal);
        if(list != null){
            map.put("total", list.size());
            map.put("records", list);
        }
        return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
    }
    
    /***************************************************
    * @project : AWS 인프라 관리 대시보드
    * @description : AWS VPC 상세 조회
    * @title : getAwsVpcDetailInfo
    * @return : ResponseEntity<AwsVpcMgntVO> 
    ***************************************************/
    @RequestMapping(value="/awsMgnt/vpc/save/detail/{accountId}/{vpcId}/{region}", method=RequestMethod.GET)
    public ResponseEntity<AwsVpcMgntVO> getAwsVpcDetailInfo(@PathVariable int accountId, @PathVariable String vpcId, @PathVariable String region,  Principal principal){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> AWS VPC 상세 조회");
        }
        AwsVpcMgntVO vo = awsVpcMgntService.getAwsVpcDetailInfo(accountId, vpcId, principal, region);
        return new ResponseEntity<>(vo,HttpStatus.OK);
    }
    
    /***************************************************
    * @project : AWS 인프라 관리 대시보드
    * @description : AWS VPC 생성
    * @title : saveAwsVpcInfo
    * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value="/awsMgnt/vpc/save", method=RequestMethod.POST)
    public ResponseEntity<?> saveAwsVpcInfo(@RequestBody AwsVpcMgntDTO dto, Principal principal){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> AWS VPC 생성");
        }
        awsVpcMgntService.saveAwsVpcInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    /***************************************************
    * @project : AWS 인프라 관리 대시보드
    * @description : AWS VPC 삭제
    * @title : deleteAwsVpcInfo
    * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value="/awsMgnt/vpc/delete", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteAwsVpcInfo(@RequestBody AwsVpcMgntDTO dto, Principal principal){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> AWS VPC 삭제");
        }
        awsVpcMgntService.deleteAwsVpcInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
}
