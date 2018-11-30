package org.openpaas.ieda.controller.iaasMgnt.openstackMgnt.web.router;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.iaasDashboard.openstackMgnt.web.network.dao.OpenstackNetworkMgntVO;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.web.router.dao.OpenstackRouterMgntVO;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.web.router.dto.OpenstackRouterMgntDTO;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.web.router.service.OpenstackRouterMgntService;
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
public class OpenstackRouterMgntController {

    private final static Logger LOG = LoggerFactory.getLogger(OpenstackRouterMgntController.class);
    @Autowired OpenstackRouterMgntService openstackRouterMgntService;
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK Router 관리 화면 이동
    * @title : goOpenstackRouter
    * @return : String
    ***************************************************/
    @RequestMapping(value="/openstackMgnt/router", method=RequestMethod.GET)
    public String goOpenstackRouter(){
        if (LOG.isInfoEnabled()){
            LOG.info("=========================================> OPENSTACK Router 화면 이동");
        }
        return "iaas/openstack/router/openstackRouterMgnt";
    }
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK Router 목록 조회
    * @title : OpenstackRouterList
    * @return : ResponseEntity<<HashMap><String, Object>>
    ***************************************************/
    @RequestMapping(value="/openstackMgnt/router/list/{accountId}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> openstackRouterList(Principal principal, @PathVariable int accountId){
        if (LOG.isInfoEnabled()){
            LOG.info("=========================================> OPENSTACK Router 목록 조회");
        }
        List<OpenstackRouterMgntVO> routerList = openstackRouterMgntService.getOpenstackRouterInfoList(principal, accountId);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("records", routerList);
        map.put("size", routerList != null ? routerList.size() : 0);
        return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
    }
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK Router 생성
    * @title : createOpenstackRouter
    * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value="/openstackMgnt/router/create", method=RequestMethod.POST)
    public ResponseEntity<?> createOpenstackRouter(Principal principal,
                                                       @RequestBody OpenstackRouterMgntVO rvo){
        if (LOG.isInfoEnabled()){
            LOG.info("=========================================> OPENSTACK Router 생성");
        }
        openstackRouterMgntService.createOpenstackRouter(rvo, principal);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    /**********삭*****************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK Router 삭제
    * @title : deleteOpenstackRouter
    * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value="/openstackMgnt/router/delete", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteOpenstackRouter(Principal principal,
                                                    @RequestBody OpenstackRouterMgntVO rvo){
        if (LOG.isInfoEnabled()) {
            LOG.info("========================================> OPENSTACK Router 삭제");
        }
        openstackRouterMgntService.deleteOpenstackRouter(rvo, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK Router Interface 목록 조회
    * @title : getOpenstackRouterInterfaceInfoList
    * @return : ResponseEntity<<HashMap><String, Object>>
    ***************************************************/
    @RequestMapping(value="/openstackMgnt/router/interface/list/{accountId}/{routeId}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getOpenstackRouterInterfaceInfoList(Principal principal,
                                                                                              @PathVariable int accountId,
                                                                                              @PathVariable String routeId){
        if (LOG.isInfoEnabled()){
            LOG.info("=========================================> OPENSTACK Router Interface 목록 조회");
        }
        List<OpenstackRouterMgntDTO> interfaces = openstackRouterMgntService.getOpenstackRouterInterfaceInfoList(principal, accountId, routeId);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("records", interfaces);
        map.put("size", interfaces != null? interfaces.size() : 0);
        return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
    }
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK Router Interface Subnet 목록 조회
    * @title : getOpenstackRouterInterfaceSubnetInfoList
    * @return : ResponseEntity<<HashMap><String, Object>>
    ***************************************************/
    @RequestMapping(value="/openstackMgnt/router/interface/list/{accountId}", method=RequestMethod.GET)
    public ResponseEntity<List<OpenstackNetworkMgntVO>> getOpenstackRouterInterfaceSubnetInfoList(Principal principal,
                                                                                              @PathVariable int accountId){
        if (LOG.isInfoEnabled()){
            LOG.info("========================================> OPENSTACK Router InterfaceSubnet 목록 조회");
        }
        List<OpenstackNetworkMgntVO> intnlist = openstackRouterMgntService.getOpenstackNetworkSubnetInfo(principal, accountId);
        return new ResponseEntity<List<OpenstackNetworkMgntVO>>(intnlist, HttpStatus.OK);
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK Router Interface Subnet 연결(생성)
    * @title : attachOpenstackRouterInterface
    * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value="/openstackMgnt/router/interface/attach", method=RequestMethod.POST)
    public ResponseEntity<?> attachOpenstackRouterInterface(Principal principal,
                                                                  @RequestBody OpenstackRouterMgntVO rvo){
        if (LOG.isInfoEnabled()) {
            LOG.info("========================================> OPENSTACK Router InterfaceSubnet 연결");
        }
        openstackRouterMgntService.attachOpenstackRouterInterface(rvo, principal);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK Router Interface Subnet 해제(삭제)
    * @title : detachOpenstackRouterInterface
    * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value="/openstackMgnt/router/interface/detach", method=RequestMethod.DELETE)
    public ResponseEntity<?> detachOpenstackRouterInterface(Principal principal,
                                                            @RequestBody OpenstackRouterMgntVO rvo){
        
        if(LOG.isInfoEnabled()) {
            LOG.info("========================================> OPENSTACK Router InterfaceSubnet 연결해제");
        }
        openstackRouterMgntService.detachOpenstackRouterInterface(rvo, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK Router Gateway 연결
    * @title : setOpenstackRouterGatewayAttach
    * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value="/openstackMgnt/router/gateway/attach", method=RequestMethod.POST)
    public ResponseEntity<?> setOpenstackRouterGatewayAttach(Principal principal,
                                               @RequestBody OpenstackRouterMgntVO rvo){
        if(LOG.isInfoEnabled()) {
            LOG.info("========================================> OPENSTACK Router Gateway 연결");
        }
        openstackRouterMgntService.setOpenstackRouterGatewayAttach(principal, rvo);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK Router Gateway 연결해제
    * @title : setOpenstackRouterGatewayDetach
    * @return : ResponseEntity<?>
    ***************************************************/
    @RequestMapping(value="/openstackMgnt/router/gateway/detach", method=RequestMethod.DELETE)
    public ResponseEntity<?> setOpenstackRouterGatewayDetach(Principal principal,
                                               @RequestBody OpenstackRouterMgntVO rvo){
        if(LOG.isInfoEnabled()) {
            LOG.info("========================================> OPENSTACK Router Gateway 연결해제");
        }
        openstackRouterMgntService.setOpenstackRouterGatewayDetach(principal, rvo);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK Router 외부 네트워크 목록 조회
    * @title : getOpenstackRouterExternalNetworkInfoList
    * @return : ResponseEntity<List<OpenstackNetworkMgntVO>>
    ***************************************************/
    @RequestMapping(value="/openstackMgnt/router/gateway/exnetlist/{accountId}", method=RequestMethod.GET)
    public ResponseEntity<List<HashMap<String, String>>> getOpenstackRouterExternalNetworkInfoList(Principal principal,
                                                                         @PathVariable int accountId){
        if (LOG.isInfoEnabled()){
            LOG.info("========================================> OPENSTACK Router External Network 조회");
        }
        List<HashMap<String, String>> exnetlist = openstackRouterMgntService.getOpenstackNetworkInfoList(principal, accountId);
        return new ResponseEntity<List<HashMap<String, String>>>(exnetlist, HttpStatus.OK);
    }
}
