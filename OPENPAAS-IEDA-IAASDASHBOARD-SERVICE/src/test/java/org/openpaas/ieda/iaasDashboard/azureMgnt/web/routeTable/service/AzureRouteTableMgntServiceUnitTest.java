package org.openpaas.ieda.iaasDashboard.azureMgnt.web.routeTable.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.common.base.BaseAzureMgntControllerUnitTest;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.routeTable.dao.AzureRouteTableMgntVO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.routeTable.dto.AzureRouteTableMgntDTO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.routeTable.service.AzureRouteTableMgntApiService;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.routeTable.service.AzureRouteTableMgntService;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.microsoft.azure.management.network.Network;
import com.microsoft.azure.management.network.NetworkPeerings;
import com.microsoft.azure.management.network.NetworkSecurityGroup;
import com.microsoft.azure.management.network.NetworkSecurityRule;
import com.microsoft.azure.management.network.NicIPConfiguration;
import com.microsoft.azure.management.network.Route;
import com.microsoft.azure.management.network.RouteTable;
import com.microsoft.azure.management.network.Subnet;
import com.microsoft.azure.management.network.implementation.NetworkManager;
import com.microsoft.azure.management.network.implementation.NetworkSecurityGroupInner;
import com.microsoft.azure.management.network.implementation.RouteTableInner;
import com.microsoft.azure.management.network.implementation.SubnetInner;
import com.microsoft.azure.management.network.implementation.VirtualNetworkInner;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;

import rx.Observable;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class AzureRouteTableMgntServiceUnitTest extends BaseAzureMgntControllerUnitTest{
     
    
    private Principal principal = null;
    
    @InjectMocks AzureRouteTableMgntService mockAzureRouteTableMgntService;
    @Mock AzureRouteTableMgntApiService mockAzureRouteTableMgntApiService;
    @Mock CommonIaasService mockCommonIaasService;
    @Mock MessageSource mockMessageSource;
    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        principal = getLoggined();
    }
    
    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : RouteTable 목록 조회
     * @title : testGetAzureRouteTableInfoList TEST
     * @return : void
     ***************************************************/
    @Test
    public void testGetAzureRouteTableInfoList(){
        IaasAccountMgntVO vo = getAzureAccountInfo();
        String subscriptionId = vo.getAzureSubscriptionId();
        String subscriptionName = getAzureSubscriptionName();
        List<RouteTable> routeTableList = getResultRouteTableListInfo();
        when(mockAzureRouteTableMgntApiService.getAzureRouteTableInfoListFromAzure(any())).thenReturn(routeTableList);
        List<AzureRouteTableMgntVO> resultList = mockAzureRouteTableMgntService.getAzureRouteTableInfoList(principal, 1);
        assertEquals(resultList.size(), 1);
        assertEquals(resultList.get(0).getRouteTableName(), "test-routeTableName");
        assertEquals(resultList.get(0).getResourceGroupName(), "test-resourceGroupName");
        assertEquals(resultList.get(0).getLocation(), "koreaSouth");
        assertEquals(resultList.get(0).getSubscriptionName(), subscriptionName);
        assertEquals(resultList.get(0).getAzureSubscriptionId(), subscriptionId);
        assertEquals(resultList.get(0).getAssociations().toString(), "1");
    }

    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : Azure RouteTable 정보 목록 값 설정  
     * @title : getResultRouteTableListInfo
     * @return : List<RouteTable>
     ***************************************************/
    private List<RouteTable> getResultRouteTableListInfo() {
        List<RouteTable> rtList = new ArrayList<RouteTable>();
        RouteTable routeTable = new RouteTable(){

            @Override
            public String type() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String regionName() {
                // TODO Auto-generated method stub
                return "koreaSouth";
            }

            @Override
            public Region region() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Map<String, String> tags() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String key() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String id() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String name() {
                // TODO Auto-generated method stub
                return "test-routeTableName";
            }

            @Override
            public String resourceGroupName() {
                // TODO Auto-generated method stub
                return "test-resourceGroupName";
            }

            @Override
            public NetworkManager manager() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public RouteTableInner inner() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public RouteTable refresh() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Observable<RouteTable> refreshAsync() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Update update() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public List<Subnet> listAssociatedSubnets() {
                List<Subnet> subnetList = new ArrayList<Subnet>();
                Subnet subnet = new Subnet(){

                    @Override
                    public SubnetInner inner() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public String key() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public String name() {
                        // TODO Auto-generated method stub
                        return "test-subnetName";
                    }

                    @Override
                    public Network parent() {
                        // TODO Auto-generated method stub
                        Network network = new Network(){

                            @Override
                            public String type() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public String regionName() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public Region region() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public Map<String, String> tags() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public String key() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public String id() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public String name() {
                                // TODO Auto-generated method stub
                                return "test-networkName";
                            }

                            @Override
                            public String resourceGroupName() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public NetworkManager manager() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public VirtualNetworkInner inner() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public Network refresh() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public Observable<Network> refreshAsync() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public Update update() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public boolean isPrivateIPAddressAvailable(String ipAddress) {
                                // TODO Auto-generated method stub
                                return false;
                            }

                            @Override
                            public boolean isPrivateIPAddressInNetwork(String ipAddress) {
                                // TODO Auto-generated method stub
                                return false;
                            }

                            @Override
                            public List<String> addressSpaces() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public List<String> dnsServerIPs() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public Map<String, Subnet> subnets() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public NetworkPeerings peerings() {
                                // TODO Auto-generated method stub
                                return null;
                            }};
                        return network;
                    }

                    @Override
                    public Set<NicIPConfiguration> getNetworkInterfaceIPConfigurations() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public Collection<NicIPConfiguration> listNetworkInterfaceIPConfigurations() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public Set<String> listAvailablePrivateIPAddresses() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public int networkInterfaceIPConfigurationCount() {
                        // TODO Auto-generated method stub
                        return 0;
                    }

                    @Override
                    public String addressPrefix() {
                        // TODO Auto-generated method stub
                        return "10.0.0.1/24";
                    }

                    @Override
                    public NetworkSecurityGroup getNetworkSecurityGroup() {
                        // TODO Auto-generated method stub
                        NetworkSecurityGroup securityGroup = new NetworkSecurityGroup(){

                            @Override
                            public String type() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public String regionName() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public Region region() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public Map<String, String> tags() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public String key() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public String id() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public String name() {
                                // TODO Auto-generated method stub
                                return "test-securityGroupName";
                            }

                            @Override
                            public String resourceGroupName() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public NetworkManager manager() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public NetworkSecurityGroupInner inner() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public NetworkSecurityGroup refresh() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public Observable<NetworkSecurityGroup> refreshAsync() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public Update update() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public List<Subnet> listAssociatedSubnets() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public Map<String, NetworkSecurityRule> securityRules() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public Map<String, NetworkSecurityRule> defaultSecurityRules() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public Set<String> networkInterfaceIds() {
                                // TODO Auto-generated method stub
                                return null;
                            }};
                        return securityGroup;
                    }

                    @Override
                    public String networkSecurityGroupId() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public RouteTable getRouteTable() {
                        RouteTable routeTable = new RouteTable(){

                            @Override
                            public String type() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public String regionName() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public Region region() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public Map<String, String> tags() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public String key() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public String id() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public String name() {
                                // TODO Auto-generated method stub
                                return "test-routeTableName";
                            }

                            @Override
                            public String resourceGroupName() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public NetworkManager manager() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public RouteTableInner inner() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public RouteTable refresh() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public Observable<RouteTable> refreshAsync() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public Update update() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public List<Subnet> listAssociatedSubnets() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public Map<String, Route> routes() {
                                // TODO Auto-generated method stub
                                return null;
                            }};

                        return routeTable;
                    }

                    @Override
                    public String routeTableId() {
                        // TODO Auto-generated method stub
                        return null;
                    }};
                    
                    subnetList.add(subnet);
                return subnetList;
            }

            @Override
            public Map<String, Route> routes() {
                // TODO Auto-generated method stub
                return null;
            }
            
        };
        rtList.add(routeTable);
        return rtList;
    }
    
    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : RouteTable의 Associated Subnet 목록 조회 TEST
     * @title : testGetAzureRouteTableSubnetInfoList
     * @return : void
     ***************************************************/
    @Test
    public void testGetAzureRouteTableSubnetInfoList(){
        List<RouteTable> routeTableList = getResultRouteTableListInfo();
        String routeTableName = "test-routeTableName";
        when(mockAzureRouteTableMgntApiService.getAzureRouteTableInfoListFromAzure(any())).thenReturn(routeTableList);
        List<AzureRouteTableMgntVO> resultList = mockAzureRouteTableMgntService.getAzureRouteTableSubnetInfoList(principal,1, routeTableName);
        assertEquals(resultList.size(), 1);
        assertEquals(resultList.get(0).getRouteTableName(), "test-routeTableName");
        assertEquals(resultList.get(0).getSubnetName(), "test-subnetName");
        assertEquals(resultList.get(0).getSubnetAddressRange(), "10.0.0.1/24");
        assertEquals(resultList.get(0).getNetworkName(), "test-networkName");
        assertEquals(resultList.get(0).getSecurityGroupName(), "test-securityGroupName");
    }

    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure RouteTable 생성 TEST
     * @title : testCreateAzureRouteTable
     * @return : void
     ***************************************************/
    @Test
    public void testCreateAzureRouteTable(){
        getAzureAccountInfo();
        AzureRouteTableMgntDTO dto = setAzureRouteTableInfo();
        mockAzureRouteTableMgntService.createAzureRouteTable(dto, principal);
    }
    
    /***************************************************
    * @project : 인프라 관리 대시보드
    * @description : Azure RouteTable 정보 설정
    * @title : setAzureRouteTableInfo
    * @return : AzureRouteTableMgntDTO
    ***************************************************/
    private AzureRouteTableMgntDTO setAzureRouteTableInfo() {
        AzureRouteTableMgntDTO dto = new AzureRouteTableMgntDTO();
        dto.setAccountId(1);
        dto.setRouteTableName("test-routeTableName");
        dto.setNetworkName("test-networkName");
        dto.setLocation("koreaSouth");
        dto.setResourceGroupName("test-resourceGroupName");
        dto.setSubnetName("test-subnetName");
        return dto;
    }
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure RouteTable 삭제 TEST
     * @title : testDeleteAzureRouteTable
     * @return : void
     ***************************************************/
    @Test
    public void testDeleteAzureRouteTable(){
        getAzureAccountInfo();
        AzureRouteTableMgntDTO dto = setAzureRouteTableInfo();
        mockAzureRouteTableMgntService.deleteAzureRouteTable(dto, principal);
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure Subnet associate TEST
     * @title : testAssociateAzureSubnet
     * @return : void
     ***************************************************/
    @Test
    public void testAssociateAzureSubnet(){
        getAzureAccountInfo();
        AzureRouteTableMgntDTO dto = setAzureRouteTableInfo();
        mockAzureRouteTableMgntService.associateAzureSubnet(dto, principal);
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드 
     * @description : Azure Subnet disassociate TEST
     * @title : testDisassociateAzureSubnet
     * @return : void
     ***************************************************/
    @Test
    public void testDisassociateAzureSubnet(){
        getAzureAccountInfo();
        AzureRouteTableMgntDTO dto = setAzureRouteTableInfo();
        mockAzureRouteTableMgntService.disassociateAzureSubnet(dto, principal);
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure Network 명 조회 TEST
     * @title : testGetAzureNetworkName
     * @return : void
     ***************************************************/
    @Test
    public void testGetAzureNetworkName(){
        List<String> networkNameList = getResultNetworkNameListInfo();
        String resourceGroupName = "test-resourceGroupName";
        when(mockAzureRouteTableMgntApiService.getAzureNetworkNameListFromAzure(any(),any())).thenReturn(networkNameList);
        List<String> resultList = mockAzureRouteTableMgntService.getAzureNetworkName(principal, 1, resourceGroupName);
        assertEquals(resultList.size(), 1);
        assertEquals(resultList.get(0), "test-networkName");
    }
    
    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : Azure NetworkName 목록 값 설정  
     * @title : getResultNetworkNameListInfo
     * @return : List<String>
     ***************************************************/
    private List<String> getResultNetworkNameListInfo() {
        List<String> list = new ArrayList<String>();
        String networkName = "test-networkName";
        list.add(networkName);
        return list;
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure Subnet 명 조회 TEST
     * @title : testGetAzureSubnetName
     * @return : void
     ***************************************************/
    @Test
    public void testGetAzureSubnetName(){
        List<String> subnetNameList = getResultSubnetNameListInfo();
        String resourceGroupName = "test-resourceGroupName";
        String networkName = "test-networkName";
        when(mockAzureRouteTableMgntApiService.getAzureSubnetNameListFromAzure(any(),any(), any())).thenReturn(subnetNameList);
        List<String> resultList = mockAzureRouteTableMgntService.getAzureSubnetName(principal, 1, resourceGroupName, networkName);
        assertEquals(resultList.size(), 1);
        assertEquals(resultList.get(0), "test-subnetName");
    }
    
    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : Azure Subnet Name 목록 값 설정  
     * @title : getResultSubnetNameListInfo
     * @return : List<String>
     ***************************************************/
    private List<String> getResultSubnetNameListInfo() {
        List<String> list = new ArrayList<String>();
        String subnetName = "test-subnetName";
        list.add(subnetName);
        return list;
    }

    /***************************************************
    * @project : 인프라 관리 대시보드
    * @description : Azure Account 조회 정보 결과 값 설정
    * @title : getAzureAccountInfo
    * @return : IaasAccountMgntVO
    ***************************************************/
    public IaasAccountMgntVO getAzureAccountInfo() {
        IaasAccountMgntVO vo = new IaasAccountMgntVO();
        vo.setAccountName("testAccountName");
        vo.setCreateUserId("admin");
        vo.setIaasType("azure");
        vo.setCommonTenant("commonUser");
        vo.setCommonAccessSecret("commonSecret");
        vo.setAzureSubscriptionId("azureSubscriptionId");
        when(mockCommonIaasService.getIaaSAccountInfo(any(), anyInt(), anyString())).thenReturn(vo);
        return vo;
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure 구독 명 조회
     * @title : getAzureSubNameInfo
     * @return : String
     ***************************************************/
    public String getAzureSubscriptionName() {
        getAzureAccountInfo();
        String subscriptionName = "aswdsad";
        when(mockCommonIaasService.getSubscriptionNameFromAzure(any(), anyString())).thenReturn(subscriptionName);
        return subscriptionName;
    }
    
}
