package org.openpaas.ieda.iaasDashboard.azureMgnt.web.publicIp.service;

import java.util.List;

import org.openpaas.ieda.common.web.common.service.CommonApiService;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.publicIp.dto.AzurePublicIpMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microsoft.azure.credentials.AzureTokenCredentials;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.network.PublicIPAddress;
import com.microsoft.azure.management.network.PublicIPSkuType;
import com.microsoft.rest.LogLevel;

@Service
public class AzurePublicIpMgntApiService {
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
     * @description : Azure API를 통해 Public IP 정보 목록 조회 실제 API 호출
     * @title : getAzurePublicIpInfoListFromAzure
     * @return : List<PublicIPAddress>
    *****************************************************************/
    public List<PublicIPAddress> getAzurePublicIpInfoListFromAzure(IaasAccountMgntVO vo){
    
        AzureTokenCredentials azureClient = getAzureClient(vo);
        Azure azure  = Azure.configure()
               .withLogLevel(LogLevel.NONE)
               .authenticate(azureClient)
               .withSubscription(vo.getAzureSubscriptionId());
        List<PublicIPAddress>  list = azure.publicIPAddresses().list();
       
        return list;
    }
    
   /****************************************************************
    * @project : Azure 인프라 관리 대시보드
    * @description : Azure API를 통해 실제 API 호출
    * @title : createAzurePublicIpFromAzure
    * @return : void
   *****************************************************************/
    public void createAzurePublicIpFromAzure(IaasAccountMgntVO vo, AzurePublicIpMgntDTO dto){
        AzureTokenCredentials azureClient = getAzureClient(vo);
        Azure azure  = Azure.configure()
               .withLogLevel(LogLevel.NONE)
               .authenticate(azureClient)
               .withSubscription(vo.getAzureSubscriptionId());
        
        PublicIPSkuType skuType = PublicIPSkuType.BASIC;
        PublicIPAddress publicIp =  azure.publicIPAddresses().define(dto.getPublicIpName()).withRegion(dto.getLocation()).withExistingResourceGroup(dto.getResourceGroupName()).withStaticIP().withIdleTimeoutInMinutes(4).withSku(skuType).create();
        //IPVersion ver = publicIp.version();
	   
    }
}
