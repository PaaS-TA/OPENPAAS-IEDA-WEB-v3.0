package org.openpaas.ieda.controller.iaasMgnt.openstackMgnt.web.securityGroup;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.openstackMgnt.web.securityGroup.dao.OpenstackSecurityGroupMgntVO;
import org.openpaas.ieda.openstackMgnt.web.securityGroup.dto.OpenstackSecurityGroupMgntDTO;
import org.openpaas.ieda.openstackMgnt.web.securityGroup.service.OpenstackSecurityGroupMgntService;
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
public class OpenstackSecurityGroupMgntController {
    
	private @Autowired OpenstackSecurityGroupMgntService openstackSecurityGroupMgntService;
    
    private final static Logger LOG = LoggerFactory.getLogger(OpenstackSecurityGroupMgntController.class);
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 보안 그룹 관리 화면 이동
    * @title : goOpenstackSecurityGroupMgnt
    * @return : String
    ***************************************************/
    @RequestMapping(value="/openstackMgnt/securityGroup", method=RequestMethod.GET)
    public String goOpenstackSecurityGroupMgnt(){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> OPENSTACK 보안 그룹 화면 이동");
        }
        return "iaas/openstack/securityGroup/openstackSecurityGroupMgnt";
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 보안 그룹 목록 정보 조회
    * @title : getOpenstackSecurityGroupInfoList
    * @return : ResponseEntity<HashMap<String, Object>>
    ***************************************************/
    @RequestMapping(value="/openstackMgnt/securityGroup/list/{accountId}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getOpenstackSecurityGroupInfoList(Principal principal, @PathVariable int accountId){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> OPENSTACK 보안 그룹 목록 정보 조회");
        }
        List<OpenstackSecurityGroupMgntVO> list = openstackSecurityGroupMgntService.getOpenstackSecrityGroupInfoList(principal, accountId);
        HashMap<String, Object> map = new HashMap<String, Object>();
        if(list.size() != 0){
            map.put("records", list);
            map.put("size", list.size());
        }
        return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 보안 그룹 목록 Inbound Rules 조회
    * @title : getOpenstackSecrityGroupIngressInfo
    * @return : ResponseEntity<List<HashMap<String, Object>>>
    ***************************************************/
    @RequestMapping(value="/openstackMgnt/securityGroup/ingress/list/{accountId}/{groupId}", method=RequestMethod.GET)
    public ResponseEntity<List<HashMap<String, Object>>> getOpenstackSecrityGroupIngressInfo(Principal principal, @PathVariable int accountId, @PathVariable String groupId){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> OPENSTACK 보안 그룹 Ingress Rule 목록 정보 조회");
        }
        List<HashMap<String, Object>> info = openstackSecurityGroupMgntService.getOpenstackSecrityGroupIngressInfo(accountId, groupId, principal);
        return new ResponseEntity<List<HashMap<String, Object>>>(info, HttpStatus.OK);
    }
    
    /***************************************************
     * @project : OPENSTACK 인프라 관리 대시보드
     * @description : OPENSTACK 보안 그룹 생성
     * @title : saveOpenstackSecurityGroupInfo
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value = "/openstackMgnt/securityGroup/save", method = RequestMethod.POST)
    public ResponseEntity<?> saveOpenstackSecurityGroupInfo(@RequestBody OpenstackSecurityGroupMgntDTO dto, Principal principal){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> OPENSTACK 보안 그룹 생성");
        }
        openstackSecurityGroupMgntService.saveOpenstackSecurityGroupInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    /****************************************************************
     * @project : OPENSTACK 인프라 관리 대시보드
     * @description : OPENSTACK 보안 그룹 삭제
     * @title : deleteOpenstackSecurityGroupInfo
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value="/openstackMgnt/securityGroup/delete", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteOpenstackSecurityGroupInfo(@RequestBody OpenstackSecurityGroupMgntDTO dto, Principal principal){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> OPENSTACK 보안 그룹 삭제");
        }
        openstackSecurityGroupMgntService.deleteOpenstackSecurityGroupInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
