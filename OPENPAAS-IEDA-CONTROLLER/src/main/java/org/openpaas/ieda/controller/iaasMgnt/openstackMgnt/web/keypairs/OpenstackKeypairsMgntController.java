package org.openpaas.ieda.controller.iaasMgnt.openstackMgnt.web.keypairs;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.openpaas.ieda.controller.iaasMgnt.openstackMgnt.web.network.OpenstackNetworkMgntController;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.web.keypairs.service.OpenstackKeypairsMgntService;
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
public class OpenstackKeypairsMgntController {
    @Autowired
    OpenstackKeypairsMgntService openstackKeypairsMgntService;
    
    private final static Logger LOGGER = LoggerFactory.getLogger(OpenstackNetworkMgntController.class);
    
    /***************************************************
     * @project : OPENSTACK 인프라 관리 대시보드
     * @description : OPENSTACK KEYPAIRS 관리 화면 이동
     * @title : goOpenstackKeypiars
     * @return : String
     ***************************************************/
    @RequestMapping(value="/openstackMgnt/keypairs", method=RequestMethod.GET)
    public String goOpenstackKeypiars(){
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("================================================> OPENSTACK Keypairs 화면 이동");
        }
        return "iaas/openstack/keypairs/openstackKeypairsMgnt";
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : OPENSTACK KEYPAIRS  목록 조회
     * @title : getOpenstackKeypairInfoList
     * @return : ResponseEntity<HashMap<String,Object>>
    ***************************************************/
    @RequestMapping(value="/openstackMgnt/keypairs/list/{accountId}",method=RequestMethod.GET)
    public ResponseEntity<HashMap<String,Object>> getOpenstackKeypairsInfoList(Principal principal, @PathVariable int accountId){
        List<HashMap<String,Object>> list = openstackKeypairsMgntService.getOpenstackKeypairsInfoList(principal, accountId);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("records", list);
        map.put("size", list != null ? list.size() : 0);
        return new ResponseEntity<HashMap<String,Object>>(map, HttpStatus.OK);
    }
    
    /***************************************************
     * @project : Openstack 관리 대시보드
     * @description : Openstack Keypairs 생성
     * @title : saveOpenstackKeypairsInfo
     * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value="/openstackMgnt/keypairs/save/{accountId}/{keyFileName}", method = RequestMethod.GET)
    public ResponseEntity<?> saveOpenstackKeypairsInfo(@PathVariable String keyFileName, @PathVariable int accountId, HttpServletResponse response, Principal principal){
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("================================================> OPENSTACK Keyparits 할당");
        }
        openstackKeypairsMgntService.saveOpenstackKeypairsInfo(keyFileName, accountId, principal, response);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
}