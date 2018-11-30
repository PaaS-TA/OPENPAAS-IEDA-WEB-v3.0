package org.openpaas.ieda.iaasDashboard.azureMgnt.web.publicIp.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.ArrayList;
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
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.publicIp.dao.AzurePublicIpMgntVO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.publicIp.dto.AzurePublicIpMgntDTO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.publicIp.service.AzurePublicIpMgntApiService;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.publicIp.service.AzurePublicIpMgntService;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.microsoft.azure.management.network.IPAllocationMethod;
import com.microsoft.azure.management.network.IPVersion;
import com.microsoft.azure.management.network.LoadBalancerPublicFrontend;
import com.microsoft.azure.management.network.NicIPConfiguration;
import com.microsoft.azure.management.network.PublicIPAddress;
import com.microsoft.azure.management.network.PublicIPSkuType;
import com.microsoft.azure.management.network.implementation.NetworkManager;
import com.microsoft.azure.management.network.implementation.PublicIPAddressInner;
import com.microsoft.azure.management.resources.fluentcore.arm.AvailabilityZoneId;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;

import rx.Observable;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class AzurePublicIpMgntServiceUnitTest extends BaseAzureMgntControllerUnitTest {
private Principal principal = null;
    
    @InjectMocks AzurePublicIpMgntService mockAzurePublicIpMgntService;
    @Mock AzurePublicIpMgntApiService mockAzurePublicIpMgntApiService;
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
     * @description : Azure Public IP 목록 조회 TEST
     * @title : testGetAzurePublicIpInfoList
     * @return : void
     ***************************************************/
    @Test
    public void testGetAzurePublicIpInfoList(){
        getAzureAccountInfo();
        getAzureSubscriptionName();
        
        List<PublicIPAddress> publicIpList = getResultPublicIpListInfo();
        when(mockAzurePublicIpMgntApiService.getAzurePublicIpInfoListFromAzure(any())).thenReturn(publicIpList);
        List<AzurePublicIpMgntVO> resultList = mockAzurePublicIpMgntService.getAzurePublicIpInfoList(principal, 1);
        assertEquals(resultList.size(), 1);
        assertEquals(resultList.get(0).getPublicIpName(), publicIpList.get(0).name());
        assertEquals(resultList.get(0).getPublicIpAddress(), publicIpList.get(0).ipAddress());
        //asertEquals(resultList.get(0).getAzureSubscriptionId(), subscriptionId);
        //assertEquals(resultList.get(0).getSubscriptionName(), subscriptionName);
        assertEquals(resultList.get(0).getLocation(), publicIpList.get(0).regionName());
        assertEquals(resultList.get(0).getResourceGroupName(), publicIpList.get(0).resourceGroupName());
    }
    
    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : 해당 정보 목록 값 설정  
     * @title : getResultPublicIpListInfo
     * @return : List<PublicIPAddress>
     ***************************************************/
    public List<PublicIPAddress> getResultPublicIpListInfo(){
    	List<PublicIPAddress> list = new ArrayList<PublicIPAddress>();
    	PublicIPAddress ipaddress = new PublicIPAddress(){

			@Override
			public String type() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String regionName() {
				// TODO Auto-generated method stub
				return "us-west-2";
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
				return "test-publicIpName";
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
			public PublicIPAddressInner inner() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public PublicIPAddress refresh() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Observable<PublicIPAddress> refreshAsync() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Update update() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public IPVersion version() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String ipAddress() {
				// TODO Auto-generated method stub
				return "104.43.242.133";
			}

			@Override
			public String leafDomainLabel() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String fqdn() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String reverseFqdn() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public IPAllocationMethod ipAllocationMethod() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int idleTimeoutInMinutes() {
				// TODO Auto-generated method stub
				return 4;
			}

			@Override
			public LoadBalancerPublicFrontend getAssignedLoadBalancerFrontend() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean hasAssignedLoadBalancer() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public NicIPConfiguration getAssignedNetworkInterfaceIPConfiguration() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean hasAssignedNetworkInterface() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public Set<AvailabilityZoneId> availabilityZones() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public PublicIPSkuType sku() {
				// TODO Auto-generated method stub
				return PublicIPSkuType.BASIC;
			}};
    	list.add(ipaddress);
        return list;
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure Public IP 생성 TEST
     * @title : testSaveStorageAccountInfo
     * @return : void
     ***************************************************/
    @Test
    public void testSaveStorageAccountInfo(){
        getAzureAccountInfo();
        AzurePublicIpMgntDTO dto = setAzurePublicIpInfo();
        mockAzurePublicIpMgntService.createPublicIp(dto, principal);
    }
    
    /***************************************************
    * @project : 인프라 관리 대시보드
    * @description : Azure Public IP 정보 설정
    * @title : setAzurePublicIpInfo
    * @return : AzurePublicIpMgntDTO
    ***************************************************/
    public AzurePublicIpMgntDTO setAzurePublicIpInfo() {
    	AzurePublicIpMgntDTO dto = new AzurePublicIpMgntDTO();
        dto.setAccountId(1);
        dto.setPublicIpName("test-publicIpName");
        dto.setResourceGroupName("test-resourceGroupName");
        dto.setLocation("us-west-2");
        return dto;
    }
    
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure 구독 명 조회
     * @title : getAzureSubNameInfo
     * @return : String
     ***************************************************/
    public void getAzureSubscriptionName() {
        getAzureAccountInfo();
        String subscriptionName = "test-subscriptionName";
        when(mockCommonIaasService.getSubscriptionNameFromAzure(any(), anyString())).thenReturn(subscriptionName);
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

}
