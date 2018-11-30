package org.openpaas.ieda.controller.iaasMgnt.azureMgnt.web.network;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.iaasDashboard.azureMgnt.web.network.dao.AzureNetworkMgntVO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.network.dto.AzureNetworkMgntDTO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.network.service.AzureNetworkMgntService;
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
public class AzureNetworkMgntController {
    @Autowired AzureNetworkMgntService azureNetworkMgntService;
    private final static Logger LOG = LoggerFactory.getLogger(AzureNetworkMgntController.class);
    /***************************************************
    * @project : AZURE 인프라 관리 대시보드
    * @description : AZURE Network 관리 화면 이동
    * @title : goAzureNetworkMgnt
    * @return : String
    ***************************************************/
    @RequestMapping(value="/azureMgnt/network", method=RequestMethod.GET)
    public String goAzureResourceGroupMgnt(){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> AZURE 리소스 그룹 화면 이동");
        }
        return "iaas/azure/network/azureNetworkMgnt";
    }

    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE Network목록 조회
     * @title : getAzureNetworkInfoList
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/network/list/{accountId}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getAzureNetworkInfoList(Principal principal, @PathVariable("accountId") int accountId){
        List<AzureNetworkMgntVO> list = azureNetworkMgntService.getAzureNetworkInfoList(principal,accountId);
        HashMap<String, Object> map = new HashMap<String, Object>();
        if(list != null){
            map.put("total", list.size());
            map.put("records", list);
        }
        return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
    }

    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE network subnets 목록 조회
     * @title : getAzureNetworkSubnetsInfo
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/network/list/subnets/{accountId}/{networkName}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getAzureNetworkSubnetsInfoList(Principal principal, @PathVariable("accountId") int accountId, @PathVariable("networkName") String networkName){
        List<AzureNetworkMgntVO> list  = azureNetworkMgntService.getAzureNetworkSubnetsInfoList(principal, accountId, networkName);
        HashMap<String, Object> map = new HashMap<String, Object>();
        if(list != null){
            map.put("total", list.size());
            map.put("records", list);
        }
        return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
    }

    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE 리소스 그룹 목록 조회
     * @title : getResourceGroupInfoList
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/resourceGroup/list/groupInfo/{accountId}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getResourceGroupInfoList(Principal principal, @PathVariable("accountId") int accountId){
        List<AzureNetworkMgntVO> list = azureNetworkMgntService.getResourceGroupInfoList(principal,accountId);
        HashMap<String, Object> map = new HashMap<String, Object>();
        if(list != null){
            map.put("total", list.size());
            map.put("records", list);
        }
        return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
    }

    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE Network 생성
     * @title : saveNetworkInfo
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/network/save", method=RequestMethod.POST)
    public ResponseEntity<?> saveNetworkInfo(@RequestBody AzureNetworkMgntDTO dto, Principal principal){
        azureNetworkMgntService.saveNetworkInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE NetworkInfo 삭제
     * @title : deleteNetworkInfo
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/network/delete", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteNetworkInfo(@RequestBody AzureNetworkMgntDTO dto, Principal principal){
       if (LOG.isInfoEnabled()) {
           LOG.info("================================================> Azure Virtual Network 삭제");
       }
       azureNetworkMgntService.deleteNetworkInfo(dto, principal);
       return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE Network Subnet 생성
     * @title : addSubnet
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/subnet/save", method=RequestMethod.POST)
    public ResponseEntity<?> addSubnet(@RequestBody AzureNetworkMgntDTO dto, Principal principal){
        azureNetworkMgntService.addSubnet(dto, principal);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }    
    
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE Network subnet Info 삭제
     * @title : deleteSubnet
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/subnet/delete", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteSubnet(@RequestBody AzureNetworkMgntDTO dto, Principal principal){
       if (LOG.isInfoEnabled()) {
           LOG.info("================================================> Azure Virtual Network Subnet 삭제");
       }
       azureNetworkMgntService.deleteSubnet(dto, principal);
       return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @title : getAzureSubscription
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/network/list/subscriptionInfo/{accountId}", method=RequestMethod.GET)
    public ResponseEntity<AzureNetworkMgntVO> getAzureSubscriptionInfo(Principal principal,@PathVariable("accountId") int accountId){
    	AzureNetworkMgntVO rgVO  = azureNetworkMgntService.getAzureSubscriptionInfo(principal, accountId);
        return new ResponseEntity<AzureNetworkMgntVO>(rgVO, HttpStatus.OK);
    }
}
