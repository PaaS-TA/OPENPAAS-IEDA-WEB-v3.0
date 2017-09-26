package org.openpaas.ieda.controller.iaasMgnt.openstackMgnt.web.network;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.openstackMgnt.web.network.dao.OpenstackNetworkMgntVO;
import org.openpaas.ieda.openstackMgnt.web.network.dto.OpenstackNetworkMgntDTO;
import org.openpaas.ieda.openstackMgnt.web.network.service.OpenstackNetworkMgntService;
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
public class OpenstackNetworkMgntController {
    @Autowired
    OpenstackNetworkMgntService openstackNetworkMgntService;
    
    private final static Logger LOG = LoggerFactory.getLogger(OpenstackNetworkMgntController.class);
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 네트워크 관리 화면 이동
    * @title : goOpenstackNetwork
    * @return : String
    ***************************************************/
    @RequestMapping(value="/openstackMgnt/network", method=RequestMethod.GET)
    public String goOpenstackNetwork(){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> OPENSTACK 네트워크 화면 이동");
        }
        return "iaas/openstack/network/openstackNetworkMgnt";
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 네트워크 목록 정보 조회
    * @title : getOpenstackNetworkListInfo
    * @return : ResponseEntity<HashMap<String, Object>>
    ***************************************************/
    @RequestMapping(value="/openstackMgnt/network/list/{accountId}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getOpenstackNetworkInfoList(Principal principal, @PathVariable int accountId){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> OPENSTACK 네트워크 목록 정보 조회");
        }
        List<OpenstackNetworkMgntVO> list = openstackNetworkMgntService.getOpenstackNetworkInfoList(principal, accountId);
        HashMap<String, Object> map = new HashMap<String, Object>();
        if(list.size() != 0){
            map.put("records", list);
            map.put("size", list.size());
        }
        return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 네트워크 상세 정보 조회
    * @title : getOpenstackNetworkInfo
    * @return : ResponseEntity<OpenstackNetworkMgntVO>
    ***************************************************/
    @RequestMapping(value="/openstackMgnt/network/save/detail/{accountId}/{networkId}", method=RequestMethod.GET)
    public ResponseEntity<OpenstackNetworkMgntVO> getOpenstackNetworkDetailInfo(Principal principal, @PathVariable int accountId, @PathVariable String networkId){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> OPENSTACK 네트워크 상세 정보 조회");
        }
        OpenstackNetworkMgntVO vo = openstackNetworkMgntService.getOpenstackNetworkDetailInfo(principal, accountId, networkId);
        return new ResponseEntity<OpenstackNetworkMgntVO>(vo, HttpStatus.OK);
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 네트워크 생성
    * @title : saveOpenstackNetworkInfo
    * @return : ResponseEntity<OpenstackNetworkMgntVO>
    ***************************************************/
    @RequestMapping(value="/openstackMgnt/network/save", method=RequestMethod.POST)
    public ResponseEntity<?> saveOpenstackNetworkInfo(@RequestBody OpenstackNetworkMgntDTO dto, Principal principal){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> OPENSTACK 네트워크 생성");
        }
        openstackNetworkMgntService.saveOpenstackNetworkInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 네트워크 삭제
    * @title : deleteOpenstackNetworkInfo
    * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value="/openstackMgnt/network/delete", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteOpenstackNetworkInfo(@RequestBody OpenstackNetworkMgntDTO dto, Principal principal){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> OPENSTACK 네트워크 삭제");
        }
        openstackNetworkMgntService.deleteOpenstackNetworkInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 서브넷 목록 조회
    * @title : getOpenstackSubnetInfoList
    * @return : ResponseEntity<List<OpenstackNetworkMgntVO>> 
    ***************************************************/
    @RequestMapping(value="/openstackMgnt/subnet/list/{accountId}/{networkId}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getOpenstackSubnetInfoList(Principal principal, @PathVariable int accountId, @PathVariable String networkId){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> OPENSTACK 서브넷 목록 정보 조회");
        }
        List<OpenstackNetworkMgntVO> list = openstackNetworkMgntService.getOpenstackSubnetInfoList(principal, accountId, networkId);
        HashMap<String, Object> map = new HashMap<String, Object>();
        if(list.size() != 0){
            map.put("size", list.size());
            map.put("records", list);
        }
        return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 서브넷 생성
    * @title : saveOpenstackNetworkInfo
    * @return : ResponseEntity<OpenstackNetworkMgntVO>
    ***************************************************/
    @RequestMapping(value="/openstackMgnt/subnet/save", method=RequestMethod.POST)
    public ResponseEntity<?> saveOpenstackSubnetkInfo(@RequestBody OpenstackNetworkMgntDTO dto, Principal principal){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> OPENSTACK 서브넷 생성");
        }
        openstackNetworkMgntService.saveOpenstackSubnetkInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 서브넷 삭제
    * @title : deleteOpenstackNetworkInfo
    * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value="/openstackMgnt/subnet/delete", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteOpenstackSubnetInfo(@RequestBody OpenstackNetworkMgntDTO dto, Principal principal){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> OPENSTACK 서브넷 삭제");
        }
        openstackNetworkMgntService.deleteOpenstackSubnetInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
}
