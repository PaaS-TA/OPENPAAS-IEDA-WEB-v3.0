package org.openpaas.ieda.controller.iaasMgnt.azureMgnt.web.securityGroup;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.iaasDashboard.azureMgnt.web.securityGroup.dao.AzureSecurityGroupMgntVO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.securityGroup.dto.AzureSecurityGroupMgntDTO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.securityGroup.service.AzureSecurityGroupMgntService;
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
public class AzureSecurityGroupMgntController {
	@Autowired private AzureSecurityGroupMgntService azureSecurityGroupMgntService;
    private final static Logger LOG = LoggerFactory.getLogger(AzureSecurityGroupMgntController.class);
    /***************************************************
    * @project : AZURE 인프라 관리 대시보드
    * @description : AZURE SecurityGroup 관리 화면 이동
    * @title : goAzureSecurityGroupMgnt
    * @return : String
    ***************************************************/
    @RequestMapping(value="/azureMgnt/securityGroup", method=RequestMethod.GET)
    public String goAzureSecurityGroupMgnt(){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> AZURE Security Group 화면 이동");
        }
        return "iaas/azure/securityGroup/azureSecurityGroupMgnt";
    }

    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE SecurityGroup 목록 조회
     * @title : getAzureSecurityGroupInfoList
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/securityGroup/list/{accountId}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getAzureSecurityGroupnfoList(Principal principal, @PathVariable("accountId") int accountId){
        List<AzureSecurityGroupMgntVO> list = azureSecurityGroupMgntService.getAzureSecurityGroupInfoList(principal,accountId);
        HashMap<String, Object> map = new HashMap<String, Object>();
        if(list != null && list.size() != 0){
            map.put("total", list.size());
            map.put("records", list);
        }
        return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
    }    
    
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE Security Group Inbounds 목록 조회
     * @title : getAzureSecurityGroupInbounds
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/securityGroup/list/inbound/{accountId}/{securityGroupName}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getAzureSecurityGroupInbounds(Principal principal, @PathVariable("accountId") int accountId, @PathVariable("securityGroupName") String securityGroupName){
        List<AzureSecurityGroupMgntVO> list = azureSecurityGroupMgntService.getAzureSecurityGroupInboundRules(principal, accountId, securityGroupName);
        HashMap<String, Object> map = new HashMap<String, Object>();
        if(list != null && list.size() != 0){
            map.put("total", list.size());
            map.put("records", list);
        }
        return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
    }  
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE Security Group Outbounds 목록 조회
     * @title : getAzureSecurityGroupOutbounds
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/securityGroup/list/outbound/{accountId}/{securityGroupName}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getAzureSecurityGroupOutbounds(Principal principal, @PathVariable("accountId") int accountId, @PathVariable("securityGroupName")String securityGroupName){
        List<AzureSecurityGroupMgntVO> list = azureSecurityGroupMgntService.getAzureSecurityGroupOutboundRules(principal,accountId,securityGroupName);
        HashMap<String, Object> map = new HashMap<String, Object>();
        if(list != null && list.size() != 0){
            map.put("total", list.size());
            map.put("records", list);
        }
        return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
    }
    
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE SecurityGroup 생성
     * @title : createAzureSecurityGroup
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/securityGroup/save", method=RequestMethod.POST)
    public ResponseEntity<?> createAzureSecurityGroup(@RequestBody AzureSecurityGroupMgntDTO dto, Principal principal){
        if (LOG.isInfoEnabled()) {
               LOG.info("================================================> Azure Security Group 생성");
        }
        azureSecurityGroupMgntService.createAzureSecurityGroup(dto, principal);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE SecurityGroup 삭제 
     * @title : deleteAzureSecurityGroup
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/securityGroup/delete", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteAzureSecurityGroup(@RequestBody AzureSecurityGroupMgntDTO dto, Principal principal){
        if (LOG.isInfoEnabled()) {
               LOG.info("================================================> Azure Security Group 삭제");
        }
        azureSecurityGroupMgntService.deleteAzureSecurityGroup(dto, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE SecurityGroup Inbound Rules생성
     * @title : createAzureInboundRules
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/inbound/save", method=RequestMethod.POST)
    public ResponseEntity<?> createAzureInboundRules(@RequestBody AzureSecurityGroupMgntDTO dto, Principal principal){
        if (LOG.isInfoEnabled()) {
               LOG.info("================================================> Azure Security Group Inbound Rules 생성");
        }
        azureSecurityGroupMgntService.createAzureInboundRules(dto, principal);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    
}
