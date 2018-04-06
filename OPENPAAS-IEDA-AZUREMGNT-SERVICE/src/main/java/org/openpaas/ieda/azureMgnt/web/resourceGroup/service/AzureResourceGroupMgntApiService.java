package org.openpaas.ieda.azureMgnt.web.resourceGroup.service;

import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.common.web.common.service.CommonApiService;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microsoft.azure.PagedList;
import com.microsoft.azure.credentials.AzureTokenCredentials;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.resources.GenericResource;
import com.microsoft.azure.management.resources.ResourceGroup;
import com.microsoft.azure.management.resources.ResourceGroups;
import com.microsoft.rest.LogLevel;

@Service
public class AzureResourceGroupMgntApiService {
    @Autowired
    CommonApiService commonApiService;
    
    /****************************************************************
     * @project : Azure 인프라 관리 대시보드
     * @description : Azure TokenCredentials 공통 빌드
     * @title : getAzureClient
     * @return : AzureTokenCredentials
    *****************************************************************/
   public AzureTokenCredentials getAzureClient(IaasAccountMgntVO vo){
       AzureTokenCredentials azure = commonApiService.getAzureCredentialsFromAzure(vo.getCommonAccessUser(),vo.getCommonTenant(), vo.getCommonAccessSecret(), vo.getAzureSubscriptionId());
       
       return azure;
    }
    
    /****************************************************************
     * @project : Azure 인프라 관리 대시보드
     * @description : Azure API를 통해 리소스 그룹 목록 정보 조회
     * @title : getAzuerResourceGroupInfoListApiFromAzure
     * @return : List<ResourceGroup>
    *****************************************************************/
   public List<ResourceGroup> getAzureResourceGroupInfoListApiFromAzure(IaasAccountMgntVO vo){
       
       AzureTokenCredentials azureClient = getAzureClient(vo);
       Azure azure  = Azure.configure()
               .withLogLevel(LogLevel.NONE)
               .authenticate(azureClient)
               .withSubscription(vo.getAzureSubscriptionId());
       ResourceGroups resource = azure.resourceGroups();
       List<ResourceGroup> list = resource.list();
       return list;
   }

   
   /***************************************************
    * @project : Azure 인프라 관리 대시보드
    * @description : Azure ResourceGroup 상세 정보 조회 실제 API 호출
    * @title : getAzureResourceGroupDetailInfoFromAzure
    * @return : HashMap<String, Object>
    ***************************************************/
    public HashMap<String, Object> getAzureResourceGroupDetailInfoFromAzure(IaasAccountMgntVO vo) {
    	AzureTokenCredentials azureClient = getAzureClient(vo);
        Azure azure  = Azure.configure()
                .withLogLevel(LogLevel.NONE)
                .authenticate(azureClient)
                .withSubscription(vo.getAzureSubscriptionId());
        ResourceGroups resource = azure.resourceGroups();
        List<ResourceGroup> resourceGroupList = resource.list();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("resourceGroupList", resourceGroupList);
        
        return map;
    }
    
    
    /****************************************************************
     * @project : Azure 인프라 관리 대시보드
     * @description : Azure API를 통해 리소스 그룹 생성
     * @title : createResourceGroupromAzure
     * @return : ResourceGroup
    *****************************************************************/
   public ResourceGroup createResourceGroupromAzure(IaasAccountMgntVO vo, String regionName, String name, String azureSubscriptionId){
       
       AzureTokenCredentials azureClient = getAzureClient(vo);
       Azure azure  = Azure.configure()
               .withLogLevel(LogLevel.BASIC)
               .authenticate(azureClient)
               .withSubscription(azureSubscriptionId);
       com.microsoft.azure.management.resources.fluentcore.arm.Region region =  com.microsoft.azure.management.resources.fluentcore.arm.Region.findByLabelOrName(regionName);
       ResourceGroup resourceGroup = azure.resourceGroups().define(name).withRegion(region).create();
     
       return resourceGroup;
   }
   
 
   /***************************************************
    * @project : 인프라 관리 대시보드
    * @description :Azure API를 통해 MS Azure 계정 Subscription ID 가져오기 
    * @title : getSubscriptionInfoFromAzure
    * @return : Subscription
   ***************************************************/ 
   public String getSubscriptionInfoFromAzure (IaasAccountMgntVO vo, String subscriptionId ){
	   AzureTokenCredentials azureClient = getAzureClient(vo);
       Azure azure  = Azure.configure()
               .withLogLevel(LogLevel.BASIC)
               .authenticate(azureClient)
               .withSubscription(subscriptionId);
       String subscription = azure.subscriptions().getById(subscriptionId).displayName().toString();
   	return subscription;
   }
  
   /***************************************************
    * @project : 인프라 관리 대시보드
    * @description : Azure API를 통해 MS Azure 계정 deployments 정보 확인 
    * @title : getDepolymentInfoFromAzure
    * @return : String
   ***************************************************/ 
   public String getDepolymentInfoFromAzure (IaasAccountMgntVO vo, String resourceGroupName ){
	   AzureTokenCredentials azureClient = getAzureClient(vo);
	   String subscriptionId = vo.getAzureSubscriptionId();
       Azure azure  = Azure.configure()
               .withLogLevel(LogLevel.BASIC)
               .authenticate(azureClient)
               .withSubscription(subscriptionId);
       PagedList<GenericResource> resourcelist = azure.genericResources().listByResourceGroup(resourceGroupName);
       int resourcelistsize = resourcelist.size();
       String depolyments = Integer.toString(resourcelistsize);
       if(resourcelistsize == 0){
           depolyments  = "No Deployments";
       }else{
           depolyments = depolyments  +" Succeeded";
       }
   	return depolyments;
   }
   
   /***************************************************
    * @project : 인프라 관리 대시보드
    * @description : Azure API를 통해 Resource Group에 대한 Resource List 조회 
    * @title : getAzureResouceListFromAzure
    * @return : HashMap<String, Object>
   ***************************************************/ 
   public PagedList<GenericResource> getAzureResouceListFromAzure(IaasAccountMgntVO vo, String resourceGroupName){
	   AzureTokenCredentials azureClient = getAzureClient(vo);
	   String subscriptionId = vo.getAzureSubscriptionId();
       Azure azure  = Azure.configure()
               .withLogLevel(LogLevel.BASIC)
               .authenticate(azureClient)
               .withSubscription(subscriptionId);
       PagedList<GenericResource> resourceList = azure.genericResources().listByResourceGroup(resourceGroupName);
	   return resourceList;
   }
   
   /****************************************************************
    * @project : Azure 인프라 관리 대시보드
    * @description : Azure API를 통해 리소스 그룹 삭제
    * @title : deleteResourceGroupInfoApiFromAzure
    * @return : void
   *****************************************************************/
   public void deleteResourceGroupInfoApiFromAzure(IaasAccountMgntVO vo, String resourceGroupName){
	   AzureTokenCredentials azureClient = getAzureClient(vo);
	   String subscriptionId = vo.getAzureSubscriptionId();
       Azure azure  = Azure.configure()
               .withLogLevel(LogLevel.BASIC)
               .authenticate(azureClient)
               .withSubscription(subscriptionId);
       azure.resourceGroups().deleteByName(resourceGroupName);
   }
   
}
