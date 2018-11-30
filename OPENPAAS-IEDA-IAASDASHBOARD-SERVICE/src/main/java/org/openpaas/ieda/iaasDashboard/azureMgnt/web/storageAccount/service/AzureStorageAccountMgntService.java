package org.openpaas.ieda.iaasDashboard.azureMgnt.web.storageAccount.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.storageAccount.dao.AzureStorageAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.storageAccount.dto.AzureStorageAccountMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.microsoft.azure.management.storage.StorageAccount;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageCredentials;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.BlobContainerProperties;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.LeaseState;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;


@Service
public class AzureStorageAccountMgntService {
    @Autowired
    private AzureStorageAccountMgntApiService azureStorageAccountMgntApiService;
    @Autowired
    private CommonIaasService commonIaasService;
    @Autowired
    private MessageSource message;
    
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE 계정 정보가 실제 존재 하는지 확인 및 상세 조회
     * @title : getAzureAccountInfo
     * @return : IaasAccountMgntVO
     ***************************************************/
    public IaasAccountMgntVO getAzureAccountInfo(Principal principal, int accountId) {
        return commonIaasService.getIaaSAccountInfo(principal, accountId, "azure");
    }
    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : Storage Account 목록 조회
     * @title : getAzureStorageAccountInfoList
     * @return : List<AzureStorageAccountMgntVO>
     ***************************************************/
    public List<AzureStorageAccountMgntVO> getAzureStorageAccountInfoList(Principal principal, int accountId) {
         IaasAccountMgntVO vo = getAzureAccountInfo(principal, accountId);
         List<StorageAccount> results = azureStorageAccountMgntApiService.getAzureStorageAccountInfoListFromAzure(vo);
         List<AzureStorageAccountMgntVO> list = new ArrayList<AzureStorageAccountMgntVO>();
         for (int i=0; i< results.size(); i++){
             StorageAccount result = results.get(i);
             AzureStorageAccountMgntVO azureVo = new AzureStorageAccountMgntVO();
             azureVo.setStorageAccountName(result.name());
             azureVo.setStorageAccountId(result.id());
             azureVo.setLocation(result.regionName());
             azureVo.setAccountType(result.type());
             azureVo.setResourceGroupName(result.resourceGroupName());
             azureVo.setSubscriptionName(getAzureSubscriptionName( principal, accountId, vo.getAzureSubscriptionId()));
             azureVo.setAzureSubscriptionId(vo.getAzureSubscriptionId());
             azureVo.setAccountId(accountId);
             azureVo.setRecid(i);
             list.add(azureVo);
         }
         return list;
    }
    
    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : Blobs 목록 조회
     * @title : geAzureBlobInfoList
     * @return : List<AzureStorageAccountMgntVO>
     * @throws URISyntaxException 
     * @throws SecurityException 
     * @throws IOException 
     ***************************************************/
    public List<AzureStorageAccountMgntVO> getAzureBlobInfoList(Principal principal, int accountId, String storageAccountName) throws URISyntaxException, NoSuchFieldException, SecurityException, IOException {
         String accountKey = getStorageAccountKey(principal,accountId,storageAccountName);
         String storageConnectionString = "DefaultEndpointsProtocol=https;" + 
                 "AccountName="+storageAccountName+";" + 
                 "AccountKey="+accountKey+";EndpointSuffix=core.windows.net";
         List<AzureStorageAccountMgntVO> list = new ArrayList<AzureStorageAccountMgntVO>();
         String containerName ="";
         StorageCredentials storageCredentials;
         try {
            storageCredentials = StorageCredentials.tryParseCredentials(storageConnectionString);
            CloudStorageAccount stoarageAccount = new CloudStorageAccount(storageCredentials);
            CloudBlobClient serviceClient = stoarageAccount.createCloudBlobClient();
            
            Iterator<CloudBlobContainer> containers = serviceClient.listContainers().iterator();
            if(containers.hasNext()){
              containerName += containers.next().getName();
            }
            List<String> namelist = new ArrayList<String>();
            for (Iterator<CloudBlobContainer> iterateBlob = serviceClient.listContainers().iterator(); iterateBlob.hasNext(); ) {
                namelist.add(iterateBlob.next().getName());
            }
            for(int k=0; k< namelist.size(); k++){
              containerName = namelist.get(k);
              CloudBlobContainer container = serviceClient.getContainerReference(containerName);
              AzureStorageAccountMgntVO azureVo = new AzureStorageAccountMgntVO();
              if(container.exists()){
                azureVo.setStorageAccountName(storageAccountName);
                azureVo.setBlobName(containerName);
                List<String> leasestatelist = new ArrayList <String>();
                for (Iterator<CloudBlobContainer> iterateBlob = serviceClient.listContainers().iterator(); iterateBlob.hasNext(); ) {
                    LeaseState leaseState = iterateBlob.next().getProperties().getLeaseState();
                    leasestatelist.add(leaseState.toString());
                }
                if(leasestatelist.size() == namelist.size()){
                    
                    String blobLeaseState = leasestatelist.get(k);
                    azureVo.setLeaseState(blobLeaseState);
                }
                
                BlobContainerProperties con = container.getProperties();
                String etag = con.getEtag();
                if(etag != null ){
                    azureVo.setEtag(etag);
                }
                    BlobContainerPermissions permissions = container.downloadPermissions();
                    if(permissions!=null){
                      String publicAccess = permissions.getPublicAccess().name();
                      switch(publicAccess){
                          case "OFF": 
                              publicAccess = "Private (no anonymous access)";
                              break;
                          case "BLOB": 
                              publicAccess = "Blob (anonymous access for blobs only)";
                              break;
                          case "CONTAINER": 
                              publicAccess = "Container (anonymous read access for containers and blobs)";
                              break;
                          default:
                              publicAccess = publicAccess+"";
                      }
                      azureVo.setPublicAccessLevel(publicAccess);
                    }
                    azureVo.setAccountId(accountId);
                    azureVo.setRecid(k);
                    list.add(azureVo);
              }
            }
        } catch (InvalidKeyException | StorageException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //common exception 
        }
        return list;
        
    }
    
    
    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : Tables 목록 조회
     * @title : getAzureTableInfoList
     * @return : List<AzureStorageAccountMgntVO>
     * @throws URISyntaxException 
     * @throws SecurityException 
     * @throws IOException 
     ***************************************************/   
    public List<AzureStorageAccountMgntVO> getAzureTableInfoList(Principal principal, int accountId, String storageAccountName) throws URISyntaxException, NoSuchFieldException, SecurityException, IOException {
        String accountKey = getStorageAccountKey(principal,accountId,storageAccountName);
        String storageConnectionString = "DefaultEndpointsProtocol=https;" + 
                "AccountName="+storageAccountName+";" + 
                "AccountKey="+accountKey+";EndpointSuffix=core.windows.net";
        List<AzureStorageAccountMgntVO> list = new ArrayList<AzureStorageAccountMgntVO>();
        StorageCredentials storageCredentials = null;
        String tableName ="";
        try {
            storageCredentials = StorageCredentials.tryParseCredentials(storageConnectionString);
            CloudStorageAccount stoarageAccount = new CloudStorageAccount(storageCredentials);
            CloudTableClient serviceClient = stoarageAccount.createCloudTableClient();
            List<String> namelist = new ArrayList<String>();
            for (Iterator<String> iterateTable = (Iterator<String>)serviceClient.listTables().iterator(); iterateTable.hasNext(); ) {
                namelist.add(iterateTable.next());
            }
            for(int k=0; k< namelist.size(); k++){
                tableName = namelist.get(k);
                CloudTable table = serviceClient.getTableReference(tableName);
                AzureStorageAccountMgntVO azureVo = new AzureStorageAccountMgntVO();
                if(table.exists()){
                    azureVo.setStorageAccountName(storageAccountName);
                    azureVo.setTableName(tableName);
                    azureVo.setTableUrl(table.getUri().toURL().toString());
                    azureVo.setAccountId(accountId);
                    azureVo.setRecid(k);
                    list.add(azureVo);
                }
            }
            
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (StorageException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
        
    }
    
        
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure Storage Account Key 조회
     * @title : getStorageAccountKey
     * @return : String
     ***************************************************/    
    public String getStorageAccountKey(Principal principal, int accountId, String storageAccountName){
        IaasAccountMgntVO vo = getAzureAccountInfo(principal, accountId);
        try{
            String key =  azureStorageAccountMgntApiService.getAzureStorageAccountKeyFromAzure(vo, storageAccountName);
            return key;
        }catch (Exception e) {
            String detailMessage = e.getMessage();
            if(!detailMessage.equals("") && detailMessage != null){
                throw new CommonException(
                  detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
            }else{
                throw new CommonException(
                  message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
            }
        }
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure Storage Account 생성
     * @title : saveStorageAccountInfo
     * @return : void
     ***************************************************/
    public void saveStorageAccountInfo(AzureStorageAccountMgntDTO dto, Principal principal) {
        IaasAccountMgntVO vo = getAzureAccountInfo(principal, dto.getAccountId());
        try{
            azureStorageAccountMgntApiService.createAzureStorageAccountFromAzure(vo, dto);
        }catch (Exception e) {
            String detailMessage = e.getMessage();
            if(!detailMessage.equals("") && detailMessage != null){
                throw new CommonException(
                  detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
            }else{
                throw new CommonException(
                  message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
            }
            
        }
    }    
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure Storage Account 삭제
     * @title : deleteStorageAccountInfo
     * @return : void
     ***************************************************/
    public void deleteStorageAccountInfo(AzureStorageAccountMgntDTO dto, Principal principal) {
        IaasAccountMgntVO vo = getAzureAccountInfo(principal, dto.getAccountId());
        try{
            azureStorageAccountMgntApiService.deleteAzureStorageAccountFromAzure(vo, dto.getStorageAccountId());
        }catch (Exception e) {
            String detailMessage = e.getMessage();
            if(!detailMessage.equals("") && detailMessage != null){
                throw new CommonException(
                  detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
            }else{
                throw new CommonException(
                  message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
            }
            
        }
    }

    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure Storage Account Blob 생성
     * @title : createAzureBlob
     * @return : void
     ***************************************************/
    public void createAzureBlob(AzureStorageAccountMgntDTO dto, Principal principal){
        try{
            //azureStorageAccountMgntApiService.createAzureBlobFromAzure(vo, dto);
            String storageAccountName = dto.getStorageAccountName();
            String accountKey = getStorageAccountKey(principal,dto.getAccountId(),storageAccountName);
            String storageConnectionString = "DefaultEndpointsProtocol=https;" + 
                    "AccountName="+storageAccountName+";" + 
                    "AccountKey="+accountKey+";EndpointSuffix=core.windows.net";
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            CloudBlobContainer container = blobClient.getContainerReference(dto.getBlobName()); //생성 팝업창에 입력한 blob name을     대입한다.

            if(container.exists()){throw new CommonException(
                    message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.already.exists", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
                
            }else{
            // Create the container  with public access option.
            BlobContainerPublicAccessType accessType;
            String publicAccessType = dto.getPublicAccessType();
            switch(publicAccessType){
              case "private":
                 accessType = BlobContainerPublicAccessType.OFF;
                 break;
              case "blob":
                 accessType = BlobContainerPublicAccessType.BLOB;
                 break;
              case "container":
                 accessType = BlobContainerPublicAccessType.CONTAINER;
                 break;
              default:
                 accessType = BlobContainerPublicAccessType.UNKNOWN; 
            }
            
            container.createIfNotExists(); 
            BlobContainerPermissions containerPermissions = new BlobContainerPermissions();
            // Include public access in the permissions object
            containerPermissions.setPublicAccess(accessType);
            // Set the permissions on the container
            container.uploadPermissions(containerPermissions);
            }
        }catch (Exception e) {
            String detailMessage = e.getMessage();
            if(!detailMessage.equals("") && detailMessage != null){
                throw new CommonException(
                  detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
            }else{
                throw new CommonException(
                  message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
            }
            
        }
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure Storage Account Blob 삭제
     * @title : deleteAzureBlob
     * @return : void
     ***************************************************/
    public void deleteAzureBlob(AzureStorageAccountMgntDTO dto, Principal principal){
        try{
            String storageAccountName = dto.getStorageAccountName();
            String accountKey = getStorageAccountKey(principal,dto.getAccountId(),storageAccountName);
            String storageConnectionString = "DefaultEndpointsProtocol=https;" + 
                    "AccountName="+storageAccountName+";" + 
                    "AccountKey="+accountKey+";EndpointSuffix=core.windows.net";
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            CloudBlobContainer container = blobClient.getContainerReference(dto.getBlobName()); //생성 팝업창에 입력한 blob name을     대입한다.
            container.deleteIfExists();
        }catch (Exception e) {
            String detailMessage = e.getMessage();
            if(!detailMessage.equals("") && detailMessage != null){
                throw new CommonException(
                  detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
            }else{
                throw new CommonException(
                  message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
            }
            
        }
    }

    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure Storage Account Table 생성
     * @title : createAzureTable
     * @return : void
     * @throws StorageException 
     ***************************************************/
    public void createAzureTable(AzureStorageAccountMgntDTO dto, Principal principal) throws StorageException{
        String storageAccountName = dto.getStorageAccountName();
        String accountKey = getStorageAccountKey(principal,dto.getAccountId(),storageAccountName);
        String storageConnectionString = "DefaultEndpointsProtocol=https;" + 
                "AccountName="+storageAccountName+";" + 
                "AccountKey="+accountKey+";EndpointSuffix=core.windows.net";
        CloudStorageAccount storageAccount;
        String tableName = dto.getTableName();
        try {
            storageAccount = CloudStorageAccount.parse(storageConnectionString);
            CloudTableClient tableClient = storageAccount.createCloudTableClient();
            CloudTable cloudTable = tableClient.getTableReference(tableName);
            cloudTable.createIfNotExists();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            String detailMessage = e.getMessage();
            if(!detailMessage.equals("") && detailMessage != null){
                throw new CommonException(
                  detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
            }else{
                throw new CommonException(
                        message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
                  }
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            String detailMessage = e.getMessage();
            if(!detailMessage.equals("") && detailMessage != null){
                throw new CommonException(
                  detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
            }else{
                throw new CommonException(
                        message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
                  }
        }
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure Storage Account Table 삭제 
     * @title : deleteAzureTable
     * @return : void
     * @throws StorageException 
     ***************************************************/
    public void deleteAzureTable(AzureStorageAccountMgntDTO dto, Principal principal) throws StorageException{
        String storageAccountName = dto.getStorageAccountName();
        String accountKey = getStorageAccountKey(principal,dto.getAccountId(),storageAccountName);
        String storageConnectionString = "DefaultEndpointsProtocol=https;" + 
                "AccountName="+storageAccountName+";" + 
                "AccountKey="+accountKey+";EndpointSuffix=core.windows.net";
        CloudStorageAccount storageAccount;
        String tableName = dto.getTableName();
        try {
            storageAccount = CloudStorageAccount.parse(storageConnectionString);
            CloudTableClient tableClient = storageAccount.createCloudTableClient();
            CloudTable cloudTable = tableClient.getTableReference(tableName);
            cloudTable.deleteIfExists();
        }catch (Exception e) {
            String detailMessage = e.getMessage();
            if(!detailMessage.equals("") && detailMessage != null){
                throw new CommonException(
                  detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
            }else{
                throw new CommonException(
                  message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
            }
            
        }
    }    
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure 구독 명 조회
     * @title : getAzureSubscriptionName
     * @return : String
     ***************************************************/
    public String getAzureSubscriptionName(Principal principal, int accountId, String subscriptionId) {
        IaasAccountMgntVO vo = getAzureAccountInfo(principal, accountId);
        String subscriptionName = commonIaasService.getSubscriptionNameFromAzure(vo, subscriptionId);
        return subscriptionName;
    }

}
