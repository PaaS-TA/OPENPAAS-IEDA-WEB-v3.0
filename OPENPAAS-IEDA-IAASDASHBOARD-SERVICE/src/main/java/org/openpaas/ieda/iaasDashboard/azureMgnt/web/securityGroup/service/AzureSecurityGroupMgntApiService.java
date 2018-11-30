package org.openpaas.ieda.iaasDashboard.azureMgnt.web.securityGroup.service;

import java.util.List;

import org.openpaas.ieda.common.web.common.service.CommonApiService;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.securityGroup.dto.AzureSecurityGroupMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microsoft.azure.credentials.AzureTokenCredentials;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.network.NetworkSecurityGroup;
import com.microsoft.azure.management.network.implementation.NetworkManagementClientImpl;
import com.microsoft.azure.management.network.implementation.SecurityRuleInner;
import com.microsoft.rest.LogLevel;

@Service
public class AzureSecurityGroupMgntApiService {
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
     * @description : Azure API를 통해  SecurityGroup 정보 목록 조회 실제 API 호출
     * @title : getAzureSecurityGroupInfoListFromAzure
     * @return : List<NetworkSecurityGroup>
    *****************************************************************/
   public List<NetworkSecurityGroup> getAzureSecurityGroupInfoListFromAzure(IaasAccountMgntVO vo){
       AzureTokenCredentials azureClient = getAzureClient(vo);
       Azure azure  = Azure.configure()
               .withLogLevel(LogLevel.NONE)
               .authenticate(azureClient)
               .withSubscription(vo.getAzureSubscriptionId());
       List<NetworkSecurityGroup>  list = azure.networkSecurityGroups().list();
       return list;
   }
   
   /****************************************************************
    * @project : Azure 인프라 관리 대시보드
    * @description : Azure API를 통해  SecurityGroup 생성 실제 API 호출
    * @title : createAzureSecurityGroup성FromAzure
    * @return : void
   *****************************************************************/   
   public void createAzureSecurityGroupFromAzure(IaasAccountMgntVO vo, AzureSecurityGroupMgntDTO dto){
       AzureTokenCredentials azureClient = getAzureClient(vo);
       Azure azure  = Azure.configure()
               .withLogLevel(LogLevel.NONE)
               .authenticate(azureClient)
               .withSubscription(vo.getAzureSubscriptionId());
       String sgname = dto.getSecurityGroupName();
       String regionName = dto.getLocation();
       String resourceGroupName = dto.getResourceGroupName();
       azure.networkSecurityGroups().define(sgname).withRegion(regionName).withExistingResourceGroup(resourceGroupName).create();
   }
   
   /****************************************************************
    * @project : Azure 인프라 관리 대시보드
    * @description : Azure API를 통해  SecurityGroup InboundRules생성 실제 API 호출
    * @title : createAzureSecurityGroupInboundRulesFromAzure
    * @return : void
   *****************************************************************/   
    public SecurityRuleInner createAzureSecurityGroupInboundRulesFromAzure(IaasAccountMgntVO vo, AzureSecurityGroupMgntDTO dto, SecurityRuleInner securityRuleInner ){
        AzureTokenCredentials azureClient = getAzureClient(vo);
        String resourceGroupName = dto.getResourceGroupName();
        String sgname = dto.getSecurityGroupName();
        String inboundName ="";
        NetworkManagementClientImpl mgntClient = new NetworkManagementClientImpl(azureClient){};
        mgntClient = mgntClient.withSubscriptionId(vo.getAzureSubscriptionId());
        inboundName = securityRuleInner.name();
        SecurityRuleInner securityRule = mgntClient.securityRules().createOrUpdate(resourceGroupName, sgname, inboundName, securityRuleInner);
        return securityRule;
    }
   
   /****************************************************************
    * @project : Azure 인프라 관리 대시보드
    * @description : Azure API를 통해  SecurityGroup 삭제  실제 API 호출
    * @title : deleteAzureSecurityGroupFromAzure
    * @return : void
   *****************************************************************/   
   public void deleteAzureSecurityGroupFromAzure(IaasAccountMgntVO vo, AzureSecurityGroupMgntDTO dto){
       AzureTokenCredentials azureClient = getAzureClient(vo);
       Azure azure  = Azure.configure()
               .withLogLevel(LogLevel.NONE)
               .authenticate(azureClient)
               .withSubscription(vo.getAzureSubscriptionId());
       String sgid = dto.getSecurityGroupId();
       azure.networkSecurityGroups().deleteById(sgid);
       }
   
}
