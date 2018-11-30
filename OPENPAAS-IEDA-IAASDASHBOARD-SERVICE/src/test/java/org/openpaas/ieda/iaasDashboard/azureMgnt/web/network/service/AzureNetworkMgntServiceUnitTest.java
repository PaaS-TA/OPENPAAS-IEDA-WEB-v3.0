package org.openpaas.ieda.iaasDashboard.azureMgnt.web.network.service;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.network.dao.AzureNetworkMgntVO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.network.dto.AzureNetworkMgntDTO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.network.service.AzureNetworkMgntApiService;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.network.service.AzureNetworkMgntService;
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
import com.microsoft.azure.management.network.RouteTable;
import com.microsoft.azure.management.network.Subnet;
import com.microsoft.azure.management.network.implementation.NetworkManager;
import com.microsoft.azure.management.network.implementation.NetworkSecurityGroupInner;
import com.microsoft.azure.management.network.implementation.SubnetInner;
import com.microsoft.azure.management.network.implementation.VirtualNetworkInner;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;

import rx.Observable;
@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class AzureNetworkMgntServiceUnitTest extends BaseAzureMgntControllerUnitTest {
    
    private Principal principal = null;
    
    @InjectMocks AzureNetworkMgntService mockAzureNetworkMgntService;
    @Mock AzureNetworkMgntApiService mockAzureNetworkMgntApiService;
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
     * @description : Azure Network 목록 조회 TEST
     * @title : testGetAzureNetworkInfoList
     * @return : void
     ***************************************************/
    @Test
    public void testGetAzureNetworkInfoList(){
        IaasAccountMgntVO vo = getAzureAccountInfo();
        String subscriptionId = vo.getAzureSubscriptionId();
        String subscriptionName = getAzureSubscriptionName();
        List<Network> networkList = getResultNetworkListInfo();
        when(mockAzureNetworkMgntApiService.getAzureNetworkInfoListApiFromAzure(any())).thenReturn(networkList);
        List<AzureNetworkMgntVO> resultList = mockAzureNetworkMgntService.getAzureNetworkInfoList(principal, 1);
        assertEquals(resultList.size(), 1);
        assertEquals(resultList.get(0).getNetworkName(), networkList.get(0).name());
        assertEquals(resultList.get(0).getNetworkId(), networkList.get(0).id());
        assertEquals(resultList.get(0).getResourceType(), networkList.get(0).type());
        assertEquals(resultList.get(0).getAzureSubscriptionId(), subscriptionId);
        assertEquals(resultList.get(0).getSubscriptionName(), subscriptionName);
        assertEquals(resultList.get(0).getLocation(), networkList.get(0).regionName());
        assertEquals(resultList.get(0).getResourceGroupName(), networkList.get(0).resourceGroupName());
        assertEquals(resultList.get(0).getNetworkAddressSpaceCidr(), networkList.get(0).addressSpaces().get(0).toString());
    }
    
    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : NETWORK 목록 조회 결과 값 설정 
     * @title : getResultNetworkListInfo
     * @return : List<AzureNetworkMgntVO>
     ***************************************************/
    public List<Network> getResultNetworkListInfo () {
        List<Network> networkList = new ArrayList<Network>();
        Network network = new Network() {
            
            @Override
            public Update update() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public Observable<Network> refreshAsync() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public Network refresh() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public VirtualNetworkInner inner() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public NetworkManager manager() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String resourceGroupName() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String name() {
                // TODO Auto-generated method stub
                return "test-networkName";
            }
            
            @Override
            public String id() {
                // TODO Auto-generated method stub
                return "test-networkId";
            }
            
            @Override
            public String key() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String type() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public Map<String, String> tags() {
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
            public Map<String, Subnet> subnets() {
                HashMap<String, Subnet> a = new HashMap<String, Subnet>();
                Subnet subnet = new Subnet() {
                    @Override
                    public Network parent() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public String name() {
                        // TODO Auto-generated method stub
                        return "test-subnetName";
                    }
                    
                    @Override
                    public String key() {
                        // TODO Auto-generated method stub
                        return "lee";
                    }
                    
                    @Override
                    public SubnetInner inner() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public String routeTableId() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public String networkSecurityGroupId() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public int networkInterfaceIPConfigurationCount() {
                        // TODO Auto-generated method stub
                        return 0;
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
                    public RouteTable getRouteTable() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public NetworkSecurityGroup getNetworkSecurityGroup() {
                        NetworkSecurityGroup sg = new NetworkSecurityGroup() {
                            
                            @Override
                            public List<Subnet> listAssociatedSubnets() {
                                // TODO Auto-generated method stub
                                return null;
                            }
                            
                            @Override
                            public com.microsoft.azure.management.network.NetworkSecurityGroup.Update update() {
                                // TODO Auto-generated method stub
                                return null;
                            }
                            
                            @Override
                            public Observable<NetworkSecurityGroup> refreshAsync() {
                                // TODO Auto-generated method stub
                                return null;
                            }
                            
                            @Override
                            public NetworkSecurityGroup refresh() {
                                // TODO Auto-generated method stub
                                return null;
                            }
                            
                            @Override
                            public NetworkSecurityGroupInner inner() {
                                // TODO Auto-generated method stub
                                return null;
                            }
                            
                            @Override
                            public NetworkManager manager() {
                                // TODO Auto-generated method stub
                                return null;
                            }
                            
                            @Override
                            public String resourceGroupName() {
                                // TODO Auto-generated method stub
                                return null;
                            }
                            
                            @Override
                            public String name() {
                                // TODO Auto-generated method stub
                                return "test-securityGroupName";
                            }
                            
                            @Override
                            public String id() {
                                // TODO Auto-generated method stub
                                return null;
                            }
                            
                            @Override
                            public String key() {
                                // TODO Auto-generated method stub
                                return null;
                            }
                            
                            @Override
                            public String type() {
                                // TODO Auto-generated method stub
                                return null;
                            }
                            
                            @Override
                            public Map<String, String> tags() {
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
                            public Map<String, NetworkSecurityRule> securityRules() {
                                // TODO Auto-generated method stub
                                return null;
                            }
                            
                            @Override
                            public Set<String> networkInterfaceIds() {
                                // TODO Auto-generated method stub
                                return null;
                            }
                            
                            @Override
                            public Map<String, NetworkSecurityRule> defaultSecurityRules() {
                                // TODO Auto-generated method stub
                                return null;
                            }
                        };
                        return sg;
                    }
                    
                    @Override
                    public Set<NicIPConfiguration> getNetworkInterfaceIPConfigurations() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public String addressPrefix() {
                        // TODO Auto-generated method stub
                        return "10.10.1.0/24";
                    }
                };
                a.put("test-subnetName", subnet);
                return a;
            }
            
            @Override
            public NetworkPeerings peerings() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public boolean isPrivateIPAddressInNetwork(String ipAddress) {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public boolean isPrivateIPAddressAvailable(String ipAddress) {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public List<String> dnsServerIPs() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public List<String> addressSpaces() {
                // TODO Auto-generated method stub
                List<String> space = new ArrayList<String>();
                
                space.add("10.10.0.0/16");
                return space;
            }
        };
        networkList.add(network);
        return networkList;
    }
    
    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : 해당 NETWORK 에 대한 Subnets 정보 목록 조회 TEST
     * @title : testGetAzureNetworkSubnetsInfoList
     * @return : void
     ***************************************************/
    @Test
    public void testGetAzureNetworkSubnetsInfoList(){
        List<Network> networkList = getResultNetworkListInfo();
        List<Subnet> subnetList = getResultSubnetListInfo();
        when(mockAzureNetworkMgntApiService.getAzureNetworkInfoListApiFromAzure(any())).thenReturn(networkList);
        String networkName = networkList.get(0).name();
        //String networkName = "test-networkName";
        List<AzureNetworkMgntVO> resultList = mockAzureNetworkMgntService.getAzureNetworkSubnetsInfoList(principal, 1 , networkName);
        assertEquals(resultList.size(), 1);
        assertEquals(resultList.get(0).getNetworkName(), networkName);
        assertEquals(resultList.get(0).getSubnetName(), subnetList.get(0).name());
        assertEquals(resultList.get(0).getSubnetAddressRangeCidr(), subnetList.get(0).addressPrefix());
        assertEquals(resultList.get(0).getSubnetAddressesCnt().toString(), "251");
        assertEquals(resultList.get(0).getSecurityGroupName(), subnetList.get(0).getNetworkSecurityGroup().name());
    }
    
    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : 해당 NETWORK 에 대한 Subnets 정보 목록 값 설정  
     * @title : setAzureNetworkSubnetsInfo
     * @return : List<AzureNetworkMgntVO>
     ***************************************************/
    public List<Subnet> getResultSubnetListInfo(){
        List<Subnet> subnetList = new ArrayList<Subnet>();
        Subnet subnet = new Subnet() {
            
            @Override
            public Network parent() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String name() {
                // TODO Auto-generated method stub
                return "test-subnetName";
            }
            
            @Override
            public String key() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public SubnetInner inner() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String routeTableId() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String networkSecurityGroupId() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public int networkInterfaceIPConfigurationCount() {
                // TODO Auto-generated method stub
                return 0;
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
            public RouteTable getRouteTable() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public NetworkSecurityGroup getNetworkSecurityGroup() {
                // TODO Auto-generated method stub
                NetworkSecurityGroup sg = new NetworkSecurityGroup() {
                    
                    @Override
                    public List<Subnet> listAssociatedSubnets() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public Update update() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public Observable<NetworkSecurityGroup> refreshAsync() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public NetworkSecurityGroup refresh() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public NetworkSecurityGroupInner inner() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public NetworkManager manager() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public String resourceGroupName() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public String name() {
                        // TODO Auto-generated method stub
                        return "test-securityGroupName";
                    }
                    
                    @Override
                    public String id() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public String key() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public String type() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public Map<String, String> tags() {
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
                    public Map<String, NetworkSecurityRule> securityRules() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public Set<String> networkInterfaceIds() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public Map<String, NetworkSecurityRule> defaultSecurityRules() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                };
                
                return sg;
            }
            
            @Override
            public Set<NicIPConfiguration> getNetworkInterfaceIPConfigurations() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String addressPrefix() {
                // TODO Auto-generated method stub
                return "10.10.1.0/24";
            }
        };
        subnetList.add(subnet);
        return subnetList;
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure Network 생성 TEST
     * @title : testSaveNetworkInfo
     * @return : void
     ***************************************************/
    @Test
    public void testSaveNetworkInfo(){
        getAzureAccountInfo();
        AzureNetworkMgntDTO dto = setAzureNetworkInfo();
        mockAzureNetworkMgntService.saveNetworkInfo(dto, principal);
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure Network 삭제 TEST
     * @title : testDeleteNetworkInfo
     * @return : void
     ***************************************************/
    @Test
    public void testDeleteNetworkInfo(){
        getAzureAccountInfo();
        AzureNetworkMgntDTO dto = setAzureNetworkInfo();
        mockAzureNetworkMgntService.deleteNetworkInfo(dto, principal);
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure Network Subnet 생성 TEST
     * @title : testAddSubnet
     * @return : void
     ***************************************************/
    @Test
    public void testAddSubnet(){
        getAzureAccountInfo();
        AzureNetworkMgntDTO dto = setAzureNetworkInfo();
        mockAzureNetworkMgntService.addSubnet(dto, principal);
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure Network Subnet 삭제 TEST
     * @title : testDeleteSubnet
     * @return : void
     ***************************************************/
    @Test
    public void testDeleteSubnet(){
        getAzureAccountInfo();
        AzureNetworkMgntDTO dto = setAzureNetworkInfo();
        mockAzureNetworkMgntService.deleteSubnet(dto, principal);
    }
    
    /***************************************************
    * @project : 인프라 관리 대시보드
    * @description : Azure Network 정보 설정
    * @title : setAzureNetworkInfo
    * @return : AzureNetworkDTO
    ***************************************************/
    public AzureNetworkMgntDTO setAzureNetworkInfo() {
        AzureNetworkMgntDTO dto = new AzureNetworkMgntDTO();
        dto.setAccountId(1);
        dto.setNetworkName("test-networkName");
        dto.setNetworkId("test-networkId");
        dto.setNetworkAddressSpaceCidr("10.10.0.0/16");
        dto.setSubnetName("test-subnetName");
        dto.setSubnetAddressRangeCidr("10.10.1.0/24");
        dto.setLocation("koreaSouth");
        dto.setResourceGroupName("test-resourceGroupName");
        dto.setResourceGroupId("test-resourceGroupId");
        return dto;
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
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure ResourceGroup 정보 조회 TEST
     * @title : testGetResourceGroupInfoList
     * @return : void
     ***************************************************/
    public void testGetResourceGroupInfoList(){
        List<AzureNetworkMgntVO> list = setResourceGroupInfoList();
        List<AzureNetworkMgntVO>  resultList =  mockAzureNetworkMgntService.getResourceGroupInfoList(principal, 1);
        assertEquals(resultList.size(), 1);
        assertEquals(resultList.get(0).getNetworkName(), list.get(0).getNetworkName());
        assertEquals(resultList.get(0).getResourceGroupName(), list.get(0).getResourceGroupName());
        assertEquals(resultList.get(0).getResourceGroupId(), list.get(0).getResourceGroupId());
        assertEquals(resultList.get(0).getLocation(), list.get(0).getLocation());
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure ResourceGroup 정보 조회 값 설정
     * @title : setResourceGroupInfoList
     * @return : List<AzureNetworkMgntVO>
     ***************************************************/
    public List<AzureNetworkMgntVO> setResourceGroupInfoList(){
        List<AzureNetworkMgntVO> list = new ArrayList<AzureNetworkMgntVO>();
        AzureNetworkMgntVO vo = new AzureNetworkMgntVO();
        vo.setAccountId(1);
        vo.setNetworkName("test-networkName");
        vo.setResourceGroupName("test-resourceGroupName");
        vo.setLocation("koreaSouth");
        list.add(vo);
        return list;
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure 구독 정보 조회 TEST
     * @title : testGetAzureSubscriptionInfo
     * @return : void
     ***************************************************/
    public void testGetAzureSubscriptionInfo(){
        IaasAccountMgntVO vo = getAzureAccountInfo();
        String subscriptionName = getAzureSubscriptionName();
        AzureNetworkMgntVO result = mockAzureNetworkMgntService.getAzureSubscriptionInfo(principal, 1);
        assertEquals(result.getAzureSubscriptionId(), vo.getAzureSubscriptionId());
        assertEquals(result.getSubscriptionName(), subscriptionName );
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure 구독 정보 조회 값 설정
     * @title : setAzureSubscriptionInfo
     * @return : AzureNetworkMgntVO
     ***************************************************/
    public AzureNetworkMgntVO setAzureSubscriptionInfo(){
        AzureNetworkMgntVO vo = new AzureNetworkMgntVO();
        vo.setAccountId(1);
        vo.setAzureSubscriptionId("test-azureSubscriptionId");
        String subscriptionName = "";
        when(mockCommonIaasService.getSubscriptionNameFromAzure(any(), anyString())).thenReturn(subscriptionName);
        vo.setSubscriptionName(subscriptionName);
        return vo;
    }
}
