package org.openpaas.ieda.controller.iaasMgnt.awsMgnt.web.elasticIp;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.iaasDashboard.awsMgnt.web.elasticIp.dao.AwsElasticIpMgntVO;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.elasticIp.service.AwsElasticIpMgntService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AwsElasticIpMgntController {
    
    @Autowired AwsElasticIpMgntService awsElasticIpMgntService;
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Elastic Ip 화면 이동
     * @title : goAwsElasticIpMgnt
     * @return : String
    *****************************************************************/
    @RequestMapping(value="/awsMgnt/elasticIp", method=RequestMethod.GET)
    public String goAwsElasticIpMgnt(){
        return "iaas/aws/elasticIp/awsElasticIpMgnt";
    }
    
    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS Elastic IP 목록 조회
     * @title : getAwsElasticIpInfoList
     * @return : ResponseEntity<HashMap<String, Object>>
     ***************************************************/
    @RequestMapping(value="/awsMgnt/elasticIp/list/{accountId}/{region}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getAwsElasticIpInfoList(Principal principal, @PathVariable int accountId, @PathVariable String region){
        HashMap<String, Object> map = new HashMap<String, Object>();
        List<AwsElasticIpMgntVO> list = awsElasticIpMgntService.getAwsElasticIpInfoList(principal,accountId, region);
        if(list != null){
            map.put("total", list.size());
            map.put("records", list);
        }
        return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
    }
    
    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS Elastic IP 상세 조회
     * @title : getAwsElasticIpDetailInfo
     * @return : ResponseEntity<HashMap<String, Object>>
     ***************************************************/
     @RequestMapping(value="/awsMgnt/elasticIp/save/detail/{accountId}/{publicIp:.*}/{region}", method=RequestMethod.GET)
     public ResponseEntity<HashMap<String, Object>> getAwsElasticIpDetailInfo(@PathVariable int accountId, @PathVariable String publicIp, Principal principal, @PathVariable String region){
         HashMap<String, Object> map = awsElasticIpMgntService.getAwsElasticIpDetailInfo(accountId, publicIp, principal, region);
         return new ResponseEntity<HashMap<String, Object>>(map,HttpStatus.OK);
     }
     
     
     /***************************************************
      * @project : AWS 인프라 관리 대시보드
      * @description : AWS Elastic IP 할당
      * @title : saveElasticIpInfo
      * @return : ResponseEntity<?>
      ***************************************************/
     @RequestMapping(value="/awsMgnt/elasticIp/save/{accountId}/{region}", method=RequestMethod.POST)
     public ResponseEntity<?> saveElasticIpInfo(Principal principal,@PathVariable int accountId, @PathVariable String region){
         awsElasticIpMgntService.allocateElasticIp(principal, accountId, region);
         return new ResponseEntity<>(HttpStatus.CREATED);
     }
    
}
