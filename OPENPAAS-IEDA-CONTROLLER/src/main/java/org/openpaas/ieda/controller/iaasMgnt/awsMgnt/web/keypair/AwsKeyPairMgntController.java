package org.openpaas.ieda.controller.iaasMgnt.awsMgnt.web.keypair;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.openpaas.ieda.controller.common.BaseController;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.keypair.service.AwsKeypairMgntService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.amazonaws.services.ec2.model.KeyPairInfo;

@Controller
public class AwsKeyPairMgntController extends BaseController{
    
    @Autowired AwsKeypairMgntService awsKeypairMgntService;
    private final static Logger LOG = LoggerFactory.getLogger(AwsKeypairMgntService.class);
    
    /***************************************************
    * @project : AWS 인프라 관리 대시보드
    * @description : Key Pair 목록 화면 이동
    * @title : goAwsKeyPairList
    * @return : String
    ***************************************************/
    @RequestMapping(value="/awsMgnt/keypair",method=RequestMethod.GET)
    public String goAwsKeyPairMgnt(){
        if (LOG.isInfoEnabled()){
            LOG.info("================================================> AWS Key Pair 화면 이동");
        }
        return "iaas/aws/keypair/awsKeypairMgnt";
    }
    
    /***************************************************
    * @project : AWS 인프라 관리 대시보드
    * @description : Key Pair 목록 조회
    * @title : getAwsKeyPairInfoList
    * @return : ResponseEntity<HashMap><String, Object>
    ***************************************************/
    @RequestMapping(value="/awsMgnt/keypair/list/{accountId}/{region}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getAwsKeyPairInfoList(Principal principal,
                                                                            @PathVariable int accountId,
                                                                            @PathVariable String region){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> AWS Key Pair 목록 조회");
        }
        HashMap<String, Object> map = new HashMap<String, Object>();
        List<KeyPairInfo> list = awsKeypairMgntService.getAwsKeyPairInfoList(principal, accountId, region);
        if(list != null){
            map.put("total", list.size());
            map.put("records", list);
        }
        return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
    }
    
    /***************************************************
    * @project : AWS 인프라 관리 대시보드
    * @description : Key Pair 생성
    * @title : createAwsKeyPair
    * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value="/awsMgnt/keypair/create/{accountId}/{name}/{region}", method=RequestMethod.GET)
    public void createAwsKeyPair(Principal principal, HttpServletResponse response,
                                                 @PathVariable String accountId,
                                                 @PathVariable String name,
                                                 @PathVariable String region){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> AWS Key Pair 생성");
        }
        awsKeypairMgntService.createAwsKeyPair(principal, accountId, name, region, response);
    }
}
