package org.openpaas.ieda.controller.iaasMgnt.azureMgnt.web.network;

import java.security.Principal;

import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.azureMgnt.web.network.dao.AzureNetworkMgntVO;
import org.openpaas.ieda.azureMgnt.web.network.service.AzureNetworkMgntService;
import org.openpaas.ieda.controller.iaasMgnt.azureMgnt.web.resourceGroup.AzureResourceGroupMgntController;
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
public class AzureNetworkMgntController {
	@Autowired AzureNetworkMgntService azureNetworkMgntService;
	private final static Logger LOG = LoggerFactory.getLogger(AzureResourceGroupMgntController.class);
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
	    @RequestMapping(value="/azureMgnt/resourceGroup/network/list/{accountId}", method=RequestMethod.GET)
	    public ResponseEntity<HashMap<String, Object>> getAzureNetworkInfoList(Principal principal, @PathVariable int accountId){
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
	     * @description : AZURE Resource Group 상세 목록 조회
	     * @title : getAzureResourceGroupDetailInfo
	     * @return : ResponseEntity<?>
	     ***************************************************/
	    /*@RequestMapping(value="/azureMgnt/network/save/detail/{accountId}/{networkName:.*}", method=RequestMethod.GET)
	    public ResponseEntity<HashMap<String, Object>> getAzureResourceGroupDetailInfo(Principal principal, @PathVariable int accountId, @PathVariable String networkName){
	        
	    	HashMap<String, Object> map = azureNetworkMgntService.getAzureNetworkDetailInfo(principal, accountId, networkName);
	        return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
	    }*/
	    
}
