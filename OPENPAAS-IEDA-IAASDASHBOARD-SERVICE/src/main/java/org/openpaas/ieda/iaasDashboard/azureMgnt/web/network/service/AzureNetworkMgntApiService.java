package org.openpaas.ieda.iaasDashboard.azureMgnt.web.network.service;

import java.util.List;

import org.openpaas.ieda.common.web.common.service.CommonApiService;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.network.dto.AzureNetworkMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microsoft.azure.credentials.AzureTokenCredentials;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.network.Network;
import com.microsoft.azure.management.resources.ResourceGroup;
import com.microsoft.azure.management.resources.ResourceGroups;
import com.microsoft.rest.LogLevel;

@Service
public class AzureNetworkMgntApiService {

    @Autowired
    CommonApiService commonApiService;

    /****************************************************************
     * @project : Azure 인프라 관리 대시보드
     * @description : Azure TokenCredentials 공통 빌드
     * @title : getAzureClient
     * @return : AzureTokenCredentials
     *****************************************************************/
    public AzureTokenCredentials getAzureClient(IaasAccountMgntVO vo) {
        AzureTokenCredentials azure = commonApiService.getAzureCredentialsFromAzure(vo.getCommonAccessUser(),
                vo.getCommonTenant(), vo.getCommonAccessSecret(), vo.getAzureSubscriptionId());
        return azure;
    }

    /****************************************************************
     * @project : Azure 인프라 관리 대시보드
     * @description : Azure API를 통해 Virtural Network 목록 정보 조회
     * @title : getAzureNetworkInfoListApiFromAzure
     * @return : List<Network>
     *****************************************************************/
    public List<Network> getAzureNetworkInfoListApiFromAzure(IaasAccountMgntVO vo) {
        AzureTokenCredentials azureClient = getAzureClient(vo);
        Azure azure = Azure.configure().withLogLevel(LogLevel.NONE).authenticate(azureClient)
                .withSubscription(vo.getAzureSubscriptionId());
        List<Network> list = azure.networks().list();
        return list;
    }

    /****************************************************************
     * @project : Azure 인프라 관리 대시보드
     * @description : Azure API를 통해 Virtural Network 생성
     * @title : createAzureNetworkFromAzure
     * @return : void
     *****************************************************************/
    public void createAzureNetworkFromAzure(IaasAccountMgntVO vo, AzureNetworkMgntDTO dto) {
        AzureTokenCredentials azureClient = getAzureClient(vo);
        Azure azure = Azure.configure().withLogLevel(LogLevel.BASIC).authenticate(azureClient)
                .withSubscription(vo.getAzureSubscriptionId());
        //Azure location name 으로 Azure region 가져오기
        com.microsoft.azure.management.resources.fluentcore.arm.Region region = com.microsoft.azure.management.resources.fluentcore.arm.Region.findByLabelOrName(dto.getLocation());
        
        // create a virtual network with one subnet
        azure.networks().define(dto.getNetworkName()).withRegion(region)
        .withExistingResourceGroup(dto.getResourceGroupName())
        .withAddressSpace(dto.getNetworkAddressSpaceCidr())
        .withSubnet(dto.getSubnetName(), dto.getSubnetAddressRangeCidr()).create();

        
        // create a virtual network with two subnets 인 경우 보안그룹 설정
        // this NSG definition block traffic to and from the public Internet
        /*
         * NetworkSecurityGroup backEndSubnetNsg = azure.networkSecurityGroups()
         * .define("vnet1BackEndSubnetNsgName") .withRegion(region)
         * .withExistingResourceGroup(dto.getResourceGroupName())
         * .defineRule("DenyInternetInComing") .denyInbound()
         * .fromAddress("INTERNET") .fromAnyPort() .toAnyAddress() .toAnyPort()
         * .withAnyProtocol() .attach() .defineRule("DenyInternetOutGoing")
         * .denyOutbound() .fromAnyAddress() .fromAnyPort()
         * .toAddress("INTERNET") .toAnyPort() .withAnyProtocol() .attach()
         * .create();
         */

        // create a virtual network with two subnets
        /*
         * Network network = azure.networks().define(dto.getNetworkName())
         * .withRegion(region)
         * .withExistingResourceGroup(dto.getResourceGroupName())
         * .withAddressSpace(dto.getNetworkAddressRangeCidr())
         * .withSubnet(dto.getSubnetName(), dto.getSubnetAddressRangeCidr())
         * .defineSubnet("vnet1BackEndSubnetName")
         * .withAddressPrefix("backEndSubnetAddressRangeCidr")
         * .withExistingNetworkSecurityGroup(backEndSubnetNsg) .attach()
         * .create();
         */
    }

    /****************************************************************
     * @project : Azure 인프라 관리 대시보드
     * @description : Azure API를 통해 Virtural Network 삭제
     * @title : deleteAzureNetworkFromAzure
     * @return : void
     *****************************************************************/
    public void deleteAzureNetworkFromAzure(IaasAccountMgntVO vo, AzureNetworkMgntDTO dto) {
        AzureTokenCredentials azureClient = getAzureClient(vo);
        Azure azure = Azure.configure().withLogLevel(LogLevel.BASIC).authenticate(azureClient)
                .withSubscription(vo.getAzureSubscriptionId());
        azure.networks().deleteById(dto.getNetworkId());
    }

    /****************************************************************
     * @project : Azure 인프라 관리 대시보드
     * @description : Azure API를 통해 Virtural Network Subnet 생성
     * @title : addSubnetFromAzure
     * @return : void
     *****************************************************************/
    public void addSubnetFromAzure(IaasAccountMgntVO vo, AzureNetworkMgntDTO dto) {
        AzureTokenCredentials azureClient = getAzureClient(vo);
        Azure azure = Azure.configure().withLogLevel(LogLevel.BASIC).authenticate(azureClient)
                .withSubscription(vo.getAzureSubscriptionId());
        azure.networks().getById(dto.getNetworkId()).update().defineSubnet(dto.getSubnetName()).withAddressPrefix(dto.getSubnetAddressRangeCidr()).attach().apply();
    }

    /****************************************************************
     * @project : Azure 인프라 관리 대시보드
     * @description : Azure API를 통해 Virtural Network의 Subnet 삭제
     * @title : deleteSubnetFromAzure
     * @return : void
     *****************************************************************/
    public void deleteSubnetFromAzure(IaasAccountMgntVO vo, AzureNetworkMgntDTO dto) {
        AzureTokenCredentials azureClient = getAzureClient(vo);
        Azure azure = Azure.configure().withLogLevel(LogLevel.BASIC).authenticate(azureClient)
                .withSubscription(vo.getAzureSubscriptionId());
        String resourceGroupName = dto.getResourceGroupName();
        String virtualNetworkName = dto.getNetworkName();
        String subnetName = dto.getSubnetName();
        if (resourceGroupName!= null && virtualNetworkName!= null && subnetName!= null ){
        	azure.networks().getById(dto.getNetworkId()).manager().inner().subnets().delete(resourceGroupName, virtualNetworkName, subnetName);
        }	
    }
    
    /****************************************************************
     * @project : Azure 인프라 관리 대시보드
     * @description : Azure API를 통해 리소스 그룹 목록 정보 조회
     * @title : getResourceGroupInfoListApiFromAzure
     * @return : List<ResourceGroup>
     *****************************************************************/
    public List<ResourceGroup> getResourceGroupInfoListApiFromAzure(IaasAccountMgntVO vo) {
        AzureTokenCredentials azureClient = getAzureClient(vo);
        Azure azure = Azure.configure().withLogLevel(LogLevel.NONE).authenticate(azureClient)
                .withSubscription(vo.getAzureSubscriptionId());
        ResourceGroups resource = azure.resourceGroups();
        List<ResourceGroup> list = resource.list();
        return list;
    }

}
