package org.openpaas.ieda.controller.iaasMgnt.openstackMgnt.web.floatingIp;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.controller.iaasMgnt.openstackMgnt.web.network.OpenstackNetworkMgntController;
import org.openpaas.ieda.openstackMgnt.web.floatingIp.dto.OpenstackFloatingIpMgntDTO;
import org.openpaas.ieda.openstackMgnt.web.floatingIp.service.OpenstackFloatingIpMgntService;
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
public class OpenstackFloatingIpMgntController{
    @Autowired
    OpenstackFloatingIpMgntService openstackFloatingIpMgntService;
    
    private final static Logger LOGGER = LoggerFactory.getLogger(OpenstackNetworkMgntController.class);
    
    /***************************************************
     * @project : OPENSTACK 인프라 관리 대시보드
     * @description : OPENSTACK FLOATING IP 관리 화면 이동
     * @title : goOpenstackFloatingIp
     * @return : String
     ***************************************************/
    @RequestMapping(value="/openstackMgnt/floatingIp", method=RequestMethod.GET)
    public String goOpenstackFloatingIp(){
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("================================================> OPENSTACK FLOATING IP 화면 이동");
        }
        return "iaas/openstack/floatingIp/openstackFloatingIpMgnt";
        
    }
     /****************************************************
     * @project : 인프라 관리 대시보드
     * @description : OPENSTACK FLOATING IP 목록 조회
     * @title : getFloatingIpList
     * @return : ResponseEntity<HashMap<String,Object>>
    ***************************************************/
    @RequestMapping(value="/openstackMgnt/floatingIp/list/{accountId}",method=RequestMethod.GET)
    public ResponseEntity<HashMap<String,Object>> getFloatingIpList(Principal principal, @PathVariable int accountId){
        List<HashMap<String,Object>> list = openstackFloatingIpMgntService.getOpenstackFloatingIpInfoList(principal, accountId);
       
        HashMap<String, Object> map = new HashMap<String, Object>();
        if(list.size() != 0){
            map.put("records", list);
            map.put("size", list.size());
        }       
        return new ResponseEntity<HashMap<String,Object>>(map, HttpStatus.OK);
    }

    /***************************************************
     * @project : Openstack 관리 대시보드
     * @description : Openstack Floating IP 할당
     * @title : saveFloatingIpInfo
     * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value="/openstackMgnt/floatingIp/save", method = RequestMethod.POST)
    public ResponseEntity<?> saveFloatingIpInfo(@RequestBody OpenstackFloatingIpMgntDTO dto, Principal principal){
        
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("================================================> OPENSTACK Floating IP 할당");
        }
        openstackFloatingIpMgntService.saveFloatingIpInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
     /***************************************************
     * @project : Openstack 관리 대시보드
     * @description : Openstack Floating IP 할당을 위한 Pool 목록 조회
     * @title : getPools
     * @return : ResponseEntity<List<? extends FloatingIP>>
    ***************************************************/
    @RequestMapping(value="/openstackMgnt/floatingIp/save/pool/list/{accountId}", method=RequestMethod.GET)
    public ResponseEntity< List<String>> getPoolList(Principal principal, @PathVariable int accountId){
        List<String> list = openstackFloatingIpMgntService.getOpenstackPoolInfoList(principal, accountId);
        return new ResponseEntity< List<String>>(list, HttpStatus.OK);
    }
}