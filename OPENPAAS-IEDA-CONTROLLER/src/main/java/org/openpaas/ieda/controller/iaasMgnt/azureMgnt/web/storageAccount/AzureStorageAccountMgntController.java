package org.openpaas.ieda.controller.iaasMgnt.azureMgnt.web.storageAccount;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.storageAccount.dao.AzureStorageAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.storageAccount.dto.AzureStorageAccountMgntDTO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.storageAccount.service.AzureStorageAccountMgntService;
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

import com.microsoft.azure.storage.StorageException;

@Controller
public class AzureStorageAccountMgntController {
    @Autowired private AzureStorageAccountMgntService azureStorageAccountMgntService;
    private final static Logger LOG = LoggerFactory.getLogger(AzureStorageAccountMgntController.class);
    /***************************************************
    * @project : AZURE 인프라 관리 대시보드
    * @description : AZURE Storage Account 관리 화면 이동
    * @title : goAzureStorageAccountMgnt
    * @return : String
    ***************************************************/
    @RequestMapping(value="/azureMgnt/storageAccount", method=RequestMethod.GET)
    public String goAzureStorageAccountMgnt(){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> AZURE Storage Account 화면 이동");
        }
        return "iaas/azure/storageAccount/azureStorageAccountMgnt";
    }

    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE Storage Account 목록 조회
     * @title : getAzureStorageAccountInfoList
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/storageAccount/list/{accountId}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getAzureStorageAccountInfoList(Principal principal, @PathVariable("accountId") int accountId){
        List<AzureStorageAccountMgntVO> list = azureStorageAccountMgntService.getAzureStorageAccountInfoList(principal,accountId);
        HashMap<String, Object> map = new HashMap<String, Object>();
        if(list != null && list.size() != 0){
            map.put("total", list.size());
            map.put("records", list);
        }
        return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
    }
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE Storage Account Blobs 목록 조회
     * @title : getAzureBlobInfoList
     * @return : ResponseEntity<?>
     * @throws URISyntaxException 
     * @throws IOException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     ***************************************************/
    @RequestMapping(value="/azureMgnt/storageAccount/list/blobs/{accountId}/{storageAccountName}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getAzureBlobInfoList(Principal principal, @PathVariable("accountId") int accountId, @PathVariable("storageAccountName") String storageAccountName) throws URISyntaxException, NoSuchFieldException, SecurityException, IOException{
        List<AzureStorageAccountMgntVO> list = azureStorageAccountMgntService.getAzureBlobInfoList(principal,accountId,storageAccountName);
        HashMap<String, Object> map = new HashMap<String, Object>();
        if(list != null && list.size() != 0){
            map.put("total", list.size());
            map.put("records", list);
        }
        return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
    }
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE Storage Account Table 목록 조회
     * @title : getAzureTableInfoList
     * @return : ResponseEntity<?>
     * @throws URISyntaxException 
     * @throws IOException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     ***************************************************/
    @RequestMapping(value="/azureMgnt/storageAccount/list/tables/{accountId}/{storageAccountName}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getAzureTableInfoList(Principal principal, @PathVariable("accountId") int accountId, @PathVariable("storageAccountName") String storageAccountName) throws URISyntaxException, NoSuchFieldException, SecurityException, IOException{
        List<AzureStorageAccountMgntVO> list = azureStorageAccountMgntService.getAzureTableInfoList(principal,accountId,storageAccountName);
        HashMap<String, Object> map = new HashMap<String, Object>();
        if(list != null && list.size() != 0){
            map.put("total", list.size());
            map.put("records", list);
        }
        return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
    }
    
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE Storage Account 생성
     * @title : saveStorageAccountInfo
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/storageAccount/save", method=RequestMethod.POST)
    public ResponseEntity<?> saveStorageAccountInfo(@RequestBody AzureStorageAccountMgntDTO dto, Principal principal){
        if (LOG.isInfoEnabled()) {
               LOG.info("================================================> Azure Storage Account 생성");
        }
        azureStorageAccountMgntService.saveStorageAccountInfo(dto, principal);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE Storage Account 삭제
     * @title : deleteStorageAccountInfo
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/storageAccount/delete", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteStorageAccountInfo(@RequestBody AzureStorageAccountMgntDTO dto, Principal principal){
       if (LOG.isInfoEnabled()) {
           LOG.info("================================================> Azure Storage Account 삭제");
       }
       azureStorageAccountMgntService.deleteStorageAccountInfo(dto, principal);
       return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
        
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE Storage Account Blob 생성
     * @title : saveAzureBlobInfo
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="azureMgnt/blob/save", method=RequestMethod.POST)        
    public ResponseEntity<?> saveAzureBlobInfo(@RequestBody AzureStorageAccountMgntDTO dto, Principal principal){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> Azure Storage Account Blob 생성");
         }
         azureStorageAccountMgntService.createAzureBlob(dto, principal);
         return new ResponseEntity<>(HttpStatus.CREATED);
     }
        
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE Storage Account Blob 삭제
     * @title : deleteAzureBlobInfo
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/blob/delete", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteAzureBlobInfo(@RequestBody AzureStorageAccountMgntDTO dto, Principal principal){
       if (LOG.isInfoEnabled()) {
           LOG.info("================================================> Azure Storage Account Blob 삭제");
       }
       azureStorageAccountMgntService.deleteAzureBlob(dto, principal);
       return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
        
        
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE Storage Account Table 생성
     * @title : saveAzureTableInfo
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/table/save", method=RequestMethod.POST)
    public ResponseEntity<?> saveAzureTableInfo(@RequestBody AzureStorageAccountMgntDTO dto, Principal principal){
        if (LOG.isInfoEnabled()) {
            LOG.info("================================================> Azure Storage Account Table생성");
     }
     try {
        azureStorageAccountMgntService.createAzureTable(dto, principal);
    } catch (StorageException e) {
        String detailMessage = e.getMessage();
        if(!detailMessage.equals("") && detailMessage != null){
            throw new CommonException(
              detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
        }else{
        }
        
    }
     return new ResponseEntity<>(HttpStatus.CREATED);
     } 
        
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE Storage Account Table 삭제
     * @title : deleteAzureTableInfo
     * @return : ResponseEntity<?>
     ***************************************************/
    @RequestMapping(value="/azureMgnt/table/delete", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteAzureTableInfo(@RequestBody AzureStorageAccountMgntDTO dto, Principal principal){
       if (LOG.isInfoEnabled()) {
           LOG.info("================================================> Azure Storage Account Table 삭제");
       }
       try {
          azureStorageAccountMgntService.deleteAzureTable(dto, principal);
       } catch (StorageException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
       }
       return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
