package org.openpaas.ieda.iaasDashboard.azureMgnt.web.routeTable.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openpaas.ieda.common.web.common.service.CommonApiService;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.routeTable.dto.AzureRouteTableMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microsoft.azure.credentials.AzureTokenCredentials;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.network.Network;
import com.microsoft.azure.management.network.RouteTable;
import com.microsoft.azure.management.network.Subnet;
import com.microsoft.rest.LogLevel;
@Service
public class AzureRouteTableMgntApiService {
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
     * @description : Azure API를 통해  RouteTable 정보 목록 조회 실제 API 호출
     * @title : getAzureRouteTableInfoListFromAzure
     * @return : List<RouteTable>
    *****************************************************************/
    public List<RouteTable> getAzureRouteTableInfoListFromAzure(IaasAccountMgntVO vo){
        AzureTokenCredentials azureClient = getAzureClient(vo);
        Azure azure  = Azure.configure()
              .withLogLevel(LogLevel.NONE)
              .authenticate(azureClient)
              .withSubscription(vo.getAzureSubscriptionId());
        List<RouteTable>  list = azure.routeTables().list();
        return list;
    }

    /****************************************************************
     * @project : Azure 인프라 관리 대시보드
     * @description : Azure API를 통해  RouteTable 생성 실제 API 호출
     * @title : createAzureRouteTableFromAzure
     * @return : void
    *****************************************************************/   
    public void createAzureRouteTableFromAzure(IaasAccountMgntVO vo, AzureRouteTableMgntDTO dto){
        AzureTokenCredentials azureClient = getAzureClient(vo);
        Azure azure  = Azure.configure()
              .withLogLevel(LogLevel.NONE)
              .authenticate(azureClient)
              .withSubscription(vo.getAzureSubscriptionId());
        String routeTablename = dto.getRouteTableName();
        String regionName = dto.getLocation();
        String resourceGroupName = dto.getResourceGroupName();
        azure.routeTables().define(routeTablename).withRegion(regionName).withExistingResourceGroup(resourceGroupName).create();
    }
    
    /****************************************************************
     * @project : Azure 인프라 관리 대시보드
     * @description : Azure API를 통해  RouteTable 삭제 실제 API 호출
     * @title : deleteAzureRouteTableFromAzure
     * @return : void
    *****************************************************************/   
    public void deleteAzureRouteTableFromAzure(IaasAccountMgntVO vo, AzureRouteTableMgntDTO dto){
        AzureTokenCredentials azureClient = getAzureClient(vo);
        Azure azure  = Azure.configure()
              .withLogLevel(LogLevel.NONE)
              .authenticate(azureClient)
              .withSubscription(vo.getAzureSubscriptionId());
        String routeTableName = dto.getRouteTableName();
        String resourceGroupName = dto.getResourceGroupName();
        azure.routeTables().deleteByResourceGroup(resourceGroupName, routeTableName);
    }
    
    /****************************************************************
     * @project : Azure 인프라 관리 대시보드
     * @description : Azure API를 통해  Subnet 연결 실제 API 호출
     * @title : associateAzureSubnetFromAzure
     * @return : void
    *****************************************************************/
    public void associateAzureSubnetFromAzure (IaasAccountMgntVO vo, AzureRouteTableMgntDTO dto){
        AzureTokenCredentials azureClient = getAzureClient(vo);
        Azure azure  = Azure.configure()
              .withLogLevel(LogLevel.NONE)
              .authenticate(azureClient)
              .withSubscription(vo.getAzureSubscriptionId());
        String resourceGroupName = dto.getResourceGroupName();
        String networkName = dto.getNetworkName();
        String subnetName = dto.getSubnetName();
        String routeTableName = dto.getRouteTableName();
        Iterator<Subnet> itr = azure.networks().getByResourceGroup(resourceGroupName, networkName).subnets().values().iterator();
        List<Subnet> subnetList = new ArrayList<Subnet>();
        while(itr.hasNext()){
            subnetList.add(itr.next());
        }
        Subnet subnet = null;
        for(int i=0; i< subnetList.size(); i++){
            if(subnetList.get(i).name().equals(subnetName)){
                subnet = subnetList.get(i);
                if(subnet != null){
                    RouteTable routeTable = azure.routeTables().getByResourceGroup(resourceGroupName, routeTableName);
                    String cidr = subnet.addressPrefix();
                    com.microsoft.azure.management.network.Network.Update network = azure.networks().getByResourceGroup(resourceGroupName, networkName).update().defineSubnet(subnetName).withAddressPrefix(cidr).withExistingRouteTable(routeTable).attach();
                    network.apply();
                }
            }
        }
        
    }

    /****************************************************************
     * @project : Azure 인프라 관리 대시보드
     * @description : Azure API를 통해  Subnet 연결 해제 실제 API 호출
     * @title : disassociateAzureSubnetFromAzure
     * @return : void
    *****************************************************************/
    public void disassociateAzureSubnetFromAzure (IaasAccountMgntVO vo, AzureRouteTableMgntDTO dto){
        AzureTokenCredentials azureClient = getAzureClient(vo);
        Azure azure  = Azure.configure()
              .withLogLevel(LogLevel.NONE)
              .authenticate(azureClient)
              .withSubscription(vo.getAzureSubscriptionId());
        String resourceGroupName = dto.getResourceGroupName();
        String networkName = dto.getNetworkName();
        String subnetName = dto.getSubnetName();
        String routeTableName = dto.getRouteTableName();
        Iterator<Subnet> itr = azure.routeTables().getByResourceGroup(resourceGroupName, routeTableName).listAssociatedSubnets().iterator();
        List<Subnet> subnetList = new ArrayList<Subnet>();
        while(itr.hasNext()){
            subnetList.add(itr.next());
        }
        Subnet subnet = null;
        for(int i=0; i< subnetList.size(); i++){
            if(subnetList.get(i).name().equals(subnetName)){
                subnet = subnetList.get(i);
                if(subnet != null){
                	azure.networks().getByResourceGroup(resourceGroupName, networkName).update().updateSubnet(subnetName).withoutRouteTable().withoutNetworkSecurityGroup().parent().apply();
                }
            }
        }    
    }
    
    /****************************************************************
     * @project : Azure 인프라 관리 대시보드
     * @description : Azure API를 통해  Network Name 정보 목록 조회 실제 API 호출
     * @title : getAzureNetworkNameListFromAzure
     * @return : List<String>
    *****************************************************************/
    public List<String> getAzureNetworkNameListFromAzure(IaasAccountMgntVO vo, String resourceGroupName){
        AzureTokenCredentials azureClient = getAzureClient(vo);
        Azure azure  = Azure.configure()
              .withLogLevel(LogLevel.NONE)
              .authenticate(azureClient)
              .withSubscription(vo.getAzureSubscriptionId());
        Iterator<Network> itr = azure.networks().listByResourceGroup(resourceGroupName).listIterator();
        List<Network> networkList = new ArrayList<Network>();
        while(itr.hasNext()){
            networkList.add(itr.next());
        }
        String  networkName = "";
        List<String> list = new ArrayList<String>();
        for(int i=0; i< networkList.size(); i++){
            networkName = networkList.get(i).name();
            list.add(networkName);
        }
        return list;
    }
    
    /****************************************************************
     * @project : Azure 인프라 관리 대시보드
     * @description : Azure API를 통해  Subnet Name 정보 목록 조회 실제 API 호출
     * @title : getAzureSubnetNameListFromAzure
     * @return : List<String>
    *****************************************************************/
    public List<String> getAzureSubnetNameListFromAzure(IaasAccountMgntVO vo, String resourceGroupName, String networkName){
        AzureTokenCredentials azureClient = getAzureClient(vo);
        Azure azure  = Azure.configure()
              .withLogLevel(LogLevel.NONE)
              .authenticate(azureClient)
              .withSubscription(vo.getAzureSubscriptionId());
        Iterator<Subnet> itr = azure.networks().getByResourceGroup(resourceGroupName, networkName).subnets().values().iterator();
        List<Subnet> subnetList = new ArrayList<Subnet>();
        while(itr.hasNext()){
            subnetList.add(itr.next());
        }
        String  subnetName = "";
        List<String> list = new ArrayList<String>();
        for(int i=0; i< subnetList.size(); i++){
            Subnet subnet = subnetList.get(i);
            if(subnet.routeTableId() == null || subnet.routeTableId().isEmpty() ){
            subnetName = subnet.name();
            list.add(subnetName);
            }
        }
        return list;
    }
}
