package org.openpaas.ieda.controller.iaasMgnt.azureMgnt.web.resourceGroup;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.iaasDashboard.azureMgnt.web.resourceGroup.dao.AzureResourceGroupMgntVO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.resourceGroup.dto.AzureResourceGroupMgntDTO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.resourceGroup.service.AzureResourceGroupMgntService;
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
public class AzureResourceGroupMgntController{
    @Autowired AzureResourceGroupMgntService azureResourceGroupMgntService;
    private final static Logger LOG = LoggerFactory.getLogger(AzureResourceGroupMgntController.class);
    /***************************************************
    * @project : AZURE 인프라 관리 대시보드
    * @description : AZURE 리소스 그룹 관리 화면 이동
    * @title : goAzureResourceGroupMgnt
    * @return : String
    ***************************************************/
    @RequestMapping(value="/azureMgnt/resourceGroup", method=RequestMethod.GET)
    public String goAzureResourceGroupMgnt(){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> AZURE 리소스 그룹 화면 이동");
        }
        return "iaas/azure/resourceGroup/azureResourceGroupMgnt";
    }
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE Resource Group 목록 조회
     * @title : getAzureResourceGroupInfoList
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/resourceGroup/list/{accountId}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getAzureResourceGroupInfoList(Principal principal, @PathVariable int accountId){
        List<AzureResourceGroupMgntVO> list = azureResourceGroupMgntService.getAzureResourceGroupInfoList(principal,accountId);
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
    @RequestMapping(value="/azureMgnt/resourceGroup/save/detail/{accountId}/{resourceGroupName:.*}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getAzureResourceGroupDetailInfo(Principal principal, @PathVariable int accountId, @PathVariable String resourceGroupName){
        
        HashMap<String, Object> map = azureResourceGroupMgntService.getAzureResourceGroupDetailInfo(principal, accountId, resourceGroupName);
        return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
    }
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE Resource 목록 조회
     * @title : getAzureResourceListInfo
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/resourceGroup/save/detail/resource/{accountId}/{resourceGroupName:.*}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getAzureResourceListInfo(Principal principal, @PathVariable int accountId, @PathVariable String resourceGroupName){
        List<AzureResourceGroupMgntVO> list  = azureResourceGroupMgntService.getAzureResourceList(principal, accountId, resourceGroupName);
        HashMap<String, Object> map = new HashMap<String, Object>();
        if(list != null){
            map.put("total", list.size());
            map.put("records", list);
        }
        return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
    }
    
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE ResourceGroupInfo 생성
     * @title : saveResourceGroupInfo
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/resourceGroup/save", method=RequestMethod.POST)
    public ResponseEntity<?> saveResourceGroupInfo(@RequestBody AzureResourceGroupMgntDTO dto, Principal principal){
        azureResourceGroupMgntService.saveResourceGroupInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Azure 리전 목록 조회
     * @title : getAzureRegionInfoList
     * @return : ResponseEntity<List<Region>>
    ***************************************************/
    @RequestMapping(value="/azureMgnt/resourceGroup/save/azure/region/list", method=RequestMethod.GET)
    public ResponseEntity<ArrayList<String>> getAzureRegionInfoList(){
        ArrayList<String> azureRegionList= azureResourceGroupMgntService.getAzureRegionList();
        return new ResponseEntity<ArrayList<String>>(azureRegionList, HttpStatus.OK);
    }
    
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE Resource  목록 조회
     * @title : getAzureResourceListInfo
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/resourceGroup/save/azure/subscription/list/{accountId:.*}", method=RequestMethod.GET)
    public ResponseEntity<AzureResourceGroupMgntVO>  getAzureResourceListInfo(Principal principal,@PathVariable("accountId") int accountId){
        AzureResourceGroupMgntVO rgVO  = azureResourceGroupMgntService.getAzureSubscription(principal, accountId);
        return new ResponseEntity<AzureResourceGroupMgntVO>(rgVO, HttpStatus.OK);
    }
    
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE ResourceGroupInfo 삭제
     * @title : deleteAzureResourceGroupInfo
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/resouceGroup/delete", method=RequestMethod.DELETE)
       public ResponseEntity<?> deleteAzureResourceGroupInfo(Principal principal, @RequestBody AzureResourceGroupMgntDTO dto){
           if (LOG.isInfoEnabled()) {
               LOG.info("================================================> Azure ResourceGroup 삭제");
           }
           azureResourceGroupMgntService.deleteAzureResourceGroupInfo(principal, dto);
           return new ResponseEntity<>(HttpStatus.NO_CONTENT);
       }
}
