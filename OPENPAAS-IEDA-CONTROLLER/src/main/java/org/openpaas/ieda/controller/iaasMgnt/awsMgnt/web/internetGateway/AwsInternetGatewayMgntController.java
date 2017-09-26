package org.openpaas.ieda.controller.iaasMgnt.awsMgnt.web.internetGateway;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.awsMgnt.web.internetGateway.dao.AwsInternetGatewayMgntVO;
import org.openpaas.ieda.awsMgnt.web.internetGateway.dto.AwsInternetGatewayMgntDTO;
import org.openpaas.ieda.awsMgnt.web.internetGateway.service.AwsInternetGatewayMgntService;
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
public class AwsInternetGatewayMgntController {
    
    @Autowired
    AwsInternetGatewayMgntService awsInternetGatewayMgntService;
    private final static Logger LOG = LoggerFactory.getLogger(AwsInternetGatewayMgntController.class);
    
    /***************************************************
    * @project : AWS 인프라 관리 대시보드
    * @description : AWS internetGateway 관리 화면 이동
    * @title : goAwsInternetGateWay
    * @return : String
    ***************************************************/
    @RequestMapping(value="/awsMgnt/internetGateway", method=RequestMethod.GET)
    public String goAwsInternetGateWay(){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> AWS 인터넷 게이트웨이 화면 이동");
        }
        return "iaas/aws/internetGateway/awsInternetgateWayMgnt";
    }
    
    /***************************************************
    * @project : AWS 인프라 관리 대시보드
    * @description : AWS 인터넷 게이트웨이 목록 조회
    * @title : getAwsInternetGatewayInfoList
    * @return : ResponseEntity<HashMap<String, Object>>
    ***************************************************/
    @RequestMapping(value="/awsMgnt/internetGateWay/list/{accountId}/{region}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getAwsInternetGatewayInfoList(Principal principal, @PathVariable int accountId, @PathVariable String region){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> AWS 인터넷 게이트웨이 목록 조회");
        }
        HashMap<String, Object> map = new HashMap<String, Object>();
        List<AwsInternetGatewayMgntVO> list = awsInternetGatewayMgntService.getAwsInternetGatewayInfoList(principal,accountId,region);
        if(list != null){
            map.put("total", list.size());
            map.put("records", list);
        }
        return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
    }
    
    /***************************************************
    * @project : AWS 인프라 관리 대시보드
    * @description : AWS 인터넷 게이트웨이 생성
    * @title : saveAwsInternetGatewayInfo
    * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value="/awsMgnt/internetGateWay/save", method=RequestMethod.POST)
    public ResponseEntity<?> saveAwsInternetGatewayInfo(@RequestBody AwsInternetGatewayMgntDTO dto, Principal principal){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> AWS 인터넷게이트웨이 생성");
        }
        awsInternetGatewayMgntService.saveAwsInternetGatewayInfo(dto, principal);
    return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    /***************************************************
    * @project : AWS 인프라 관리 대시보드
    * @description : AWS 인터넷 게이트웨이 삭제
    * @title : deleteAwsInternetGatewayInfo
    * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value="/awsMgnt/internetGateWay/delete", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteAwsInternetGatewayInfo(@RequestBody AwsInternetGatewayMgntDTO dto, Principal principal){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> AWS 인터넷게이트웨이 삭제");
        }
        awsInternetGatewayMgntService.deleteAwsInternetGatewayInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    /***************************************************
    * @project : AWS 인프라 관리 대시보드
    * @description : AWS 인터넷 게이트웨이에 연결 할 VPC 목록 조회
    * @title : getAwsVpcInfoList
    * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value="/awsMgnt/internetGateWay/attach/vpc/{accountId}/{region}", method=RequestMethod.GET)
    public ResponseEntity<List<AwsInternetGatewayMgntVO>> getAwsVpcInfoList(@PathVariable int accountId, Principal principal, @PathVariable String region){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> AWS 인터넷게이트웨이에 연결 할 VPC 조회");
        }
        List<AwsInternetGatewayMgntVO> list = awsInternetGatewayMgntService.getAwsVpcInfoList(principal, accountId, region);
        return new ResponseEntity<List<AwsInternetGatewayMgntVO>>(list, HttpStatus.OK);
    }
    
    /***************************************************
    * @project : AWS 인프라 관리 대시보드
    * @description : AWS VPC 연결
    * @title : internetGatewayAttachVpc
    * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value="/awsMgnt/internetGateWay/attach", method=RequestMethod.PUT)
    public ResponseEntity<?> internetGatewayAttachVpc(@RequestBody AwsInternetGatewayMgntDTO dto, Principal principal){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> AWS VCP 연결");
        }
        awsInternetGatewayMgntService.internetGatewayAttachVpc(dto, principal);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    /***************************************************
    * @project : AWS 인프라 관리 대시보드
    * @description : AWS VPC 연결 해제
    * @title : internetGatewayDetachVpc
    * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value="/awsMgnt/internetGateWay/detach", method=RequestMethod.PUT)
    public ResponseEntity<?> internetGatewayDetachVpc(@RequestBody AwsInternetGatewayMgntDTO dto, Principal principal){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> AWS VCP 연결 해제");
        }
        awsInternetGatewayMgntService.internetGatewayDetachVpc(dto, principal);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
