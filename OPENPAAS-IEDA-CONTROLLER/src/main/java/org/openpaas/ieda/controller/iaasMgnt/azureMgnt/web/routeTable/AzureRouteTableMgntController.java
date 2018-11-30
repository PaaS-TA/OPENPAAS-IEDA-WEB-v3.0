package org.openpaas.ieda.controller.iaasMgnt.azureMgnt.web.routeTable;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.iaasDashboard.azureMgnt.web.routeTable.dao.AzureRouteTableMgntVO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.routeTable.dto.AzureRouteTableMgntDTO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.routeTable.service.AzureRouteTableMgntService;
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
public class AzureRouteTableMgntController {
	@Autowired 
	private AzureRouteTableMgntService azureRouteTableMgntService;
    private final static Logger LOG = LoggerFactory.getLogger(AzureRouteTableMgntController.class);
    /***************************************************
    * @project : AZURE 인프라 관리 대시보드
    * @description : AZURE Route Table 관리 화면 이동
    * @title : goAzureRouteTableMgnt
    * @return : String
    ***************************************************/
    @RequestMapping(value="/azureMgnt/routeTable", method=RequestMethod.GET)
    public String goAzureRouteTableMgnt(){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> AZURE Route Table 화면 이동");
        }
        return "iaas/azure/routeTable/azureRouteTableMgnt";
    }
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE Route Table 목록 조회
     * @title : getAzureRouteTablenfoList
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/routeTable/list/{accountId}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getAzureRouteTablenfoList(Principal principal, @PathVariable("accountId") int accountId){
        List<AzureRouteTableMgntVO> list = azureRouteTableMgntService.getAzureRouteTableInfoList(principal,accountId);
        HashMap<String, Object> map = new HashMap<String, Object>();
        if(list != null && list.size() != 0){
            map.put("total", list.size());
            map.put("records", list);
        }
        return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
    } 
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE Route Table의 Subnet 목록 조회
     * @title : getAzureRouteTableSubnetInfoList
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/routeTable/list/subnets/{accountId}/{routeTableName}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getAzureRouteTableSubnetInfoList(Principal principal, @PathVariable("accountId") int accountId, @PathVariable("routeTableName") String routeTableName){
        List<AzureRouteTableMgntVO> list = azureRouteTableMgntService.getAzureRouteTableSubnetInfoList(principal,accountId, routeTableName);
        HashMap<String, Object> map = new HashMap<String, Object>();
        if(list != null && list.size() != 0){
            map.put("total", list.size());
            map.put("records", list);
        }
        return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
    }
    
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE Route Table 생성
     * @title : createAzureRouteTable
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/routeTable/save", method=RequestMethod.POST)
    public ResponseEntity<?> createAzureRouteTable(@RequestBody AzureRouteTableMgntDTO dto, Principal principal){
        if (LOG.isInfoEnabled()) {
               LOG.info("================================================> Azure Route Table 생성");
        }
        azureRouteTableMgntService.createAzureRouteTable(dto, principal);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE Route Table 삭제
     * @title : deleteAzureRouteTable
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/routeTable/delete", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteAzureRouteTable(@RequestBody AzureRouteTableMgntDTO dto, Principal principal){
        if (LOG.isInfoEnabled()) {
               LOG.info("================================================> Azure Route Table 삭제 ");
        }
        azureRouteTableMgntService.deleteAzureRouteTable(dto, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE Subnet Associate
     * @title : associateAzureSubnet
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/routeTable/subnet/save", method=RequestMethod.POST)
    public ResponseEntity<?> associateAzureSubnet(@RequestBody AzureRouteTableMgntDTO dto, Principal principal){
        if (LOG.isInfoEnabled()) {
               LOG.info("================================================> Azure Subnet Associate");
        }
        azureRouteTableMgntService.associateAzureSubnet(dto, principal);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }  
    
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE Subnet disassociate
     * @title : disassociateAzureSubnet
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/routeTable/subnet/delete", method=RequestMethod.DELETE)
    public ResponseEntity<?> disassociateAzureSubnet(@RequestBody AzureRouteTableMgntDTO dto, Principal principal){
        if (LOG.isInfoEnabled()) {
               LOG.info("================================================> Azure Subnet Disassociate");
        }
        azureRouteTableMgntService.disassociateAzureSubnet(dto, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE Resoure Group에 대한 NetworkName 목록 조회
     * @title : getAzureNetworkNameList
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/routeTable/list/networkName/{accountId}/{resourceGroupName}", method=RequestMethod.GET)
    public ResponseEntity<List<String>> getAzureNetworkNameList(Principal principal, @PathVariable("accountId") int accountId, @PathVariable("resourceGroupName") String resourceGroupName){
    	List<String> list = new ArrayList<String>();
    	list = azureRouteTableMgntService.getAzureNetworkName(principal, accountId, resourceGroupName);
    	return new ResponseEntity<List<String>>(list, HttpStatus.OK);
    }
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE NetworkName에 대한 SubnetName 목록 조회
     * @title : getAzureSubnetNameList
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/routeTable/list/subnetName/{accountId}/{resourceGroupName}/{networkName}", method=RequestMethod.GET)
    public ResponseEntity<List<String>> getAzureSubnetNameList(Principal principal, @PathVariable("accountId") int accountId, @PathVariable("resourceGroupName") String resourceGroupName, @PathVariable("networkName") String networkName){
    	List<String> list = new ArrayList<String>();
    	list = azureRouteTableMgntService.getAzureSubnetName(principal, accountId, resourceGroupName, networkName);
    	return new ResponseEntity<List<String>>(list, HttpStatus.OK);
    }
    

}
