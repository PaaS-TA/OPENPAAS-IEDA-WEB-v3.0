package org.openpaas.ieda.iaasDashboard.azureMgnt.web.securityGroup.service;

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
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.securityGroup.dao.AzureSecurityGroupMgntVO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.securityGroup.dto.AzureSecurityGroupMgntDTO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.securityGroup.service.AzureSecurityGroupMgntApiService;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.securityGroup.service.AzureSecurityGroupMgntService;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.microsoft.azure.management.network.NetworkSecurityGroup;
import com.microsoft.azure.management.network.NetworkSecurityRule;
import com.microsoft.azure.management.network.Subnet;
import com.microsoft.azure.management.network.implementation.NetworkManager;
import com.microsoft.azure.management.network.implementation.NetworkSecurityGroupInner;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;

import rx.Observable;
@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class AzureSecurityGroupMgntServiceUnitTest extends BaseAzureMgntControllerUnitTest{
    
    private Principal principal = null;
    
    @InjectMocks AzureSecurityGroupMgntService mockAzureSecurityGroupMgntService;
    @Mock AzureSecurityGroupMgntApiService mockAzureSecurityGroupMgntApiService;
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
     * @description : Azure SecurityGroup 목록 조회 TEST
     * @title : testGetAzureSecurityGroupInfoList
     * @return : void
     ***************************************************/    
    @Test
    public void testGetAzureSecurityGroupInfoList(){
        IaasAccountMgntVO vo = getAzureAccountInfo();
        String subscriptionId = vo.getAzureSubscriptionId();
        String subscriptionName = getAzureSubscriptionName();
        List<NetworkSecurityGroup> securityGroupList = getResultSecurityGroupListInfo();
        when(mockAzureSecurityGroupMgntApiService.getAzureSecurityGroupInfoListFromAzure(any())).thenReturn(securityGroupList);
        List<AzureSecurityGroupMgntVO> resultList = mockAzureSecurityGroupMgntService.getAzureSecurityGroupInfoList(principal, 1);
        assertEquals(resultList.size(), 1);
        assertEquals(resultList.get(0).getSecurityGroupName(), securityGroupList.get(0).name());
        assertEquals(resultList.get(0).getAzureSubscriptionId(), subscriptionId);
        assertEquals(resultList.get(0).getSubscriptionName(), subscriptionName);
        assertEquals(resultList.get(0).getLocation(), securityGroupList.get(0).regionName());
        assertEquals(resultList.get(0).getResourceGroupName(), securityGroupList.get(0).resourceGroupName());
    }
    
    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : Azure Network SecurityGroup 정보 목록 값 설정  
     * @title : getResultSecurityGroupListInfo
     * @return : List<NetworkSecurityGroup>
     ***************************************************/    
    private List<NetworkSecurityGroup> getResultSecurityGroupListInfo() {
        List<NetworkSecurityGroup> sgList = new ArrayList<NetworkSecurityGroup>();
        NetworkSecurityGroup securityGroup = new NetworkSecurityGroup(){

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
                return "securityGroupName";
            }

            @Override
            public String resourceGroupName() {
                // TODO Auto-generated method stub
                return "resourceGorupName";
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
            sgList.add(securityGroup);
        return sgList;
    }

    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : Azure SecurityGroup Inbound Rules 목록 조회 TEST
     * @title : testGetAzureSecurityGroupInboundRules
     * @return : void
     ***************************************************/
    @Test
    public void testGetAzureSecurityGroupInboundRules(){
        String secruityGroupName ="test-secruityGroupName";
        List<NetworkSecurityGroup> securityGroupList = getResultSecurityGroupListInfo();
        when(mockAzureSecurityGroupMgntApiService.getAzureSecurityGroupInfoListFromAzure(any())).thenReturn(securityGroupList);
        List<AzureSecurityGroupMgntVO> resultList = mockAzureSecurityGroupMgntService.getAzureSecurityGroupInboundRules(principal, 1, secruityGroupName);
        assertEquals(resultList.size(), 0);
        /*assertEquals(resultList.get(0).getSecurityGroupName(), secruityGroupName);
        assertEquals(resultList.get(0).getInboundName(), "all");
        assertEquals(resultList.get(0).getPriority().toString(), "100");
        assertEquals(resultList.get(0).getPort(), "*");
        assertEquals(resultList.get(0).getProtocol(), "*");
        assertEquals(resultList.get(0).getSource(), "*");
        assertEquals(resultList.get(0).getDestination(), "*");
        assertEquals(resultList.get(0).getAction(), "Allow");*/
    }
    
    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : Azure SecurityGroup Outbound Rules 목록 조회 TEST
     * @title : testGetAzureSecurityGroupOutboundRules
     * @return : void
     ***************************************************/
    @Test
    public void testGetAzureSecurityGroupOutboundRules(){
        String secruityGroupName ="test-secruityGroupName";
        List<NetworkSecurityGroup> securityGroupList = getResultSecurityGroupListInfo();
        when(mockAzureSecurityGroupMgntApiService.getAzureSecurityGroupInfoListFromAzure(any())).thenReturn(securityGroupList);
        List<AzureSecurityGroupMgntVO> resultList = mockAzureSecurityGroupMgntService.getAzureSecurityGroupOutboundRules(principal, 1, secruityGroupName);
        assertEquals(resultList.size(), 0);
        /*assertEquals(resultList.get(0).getSecurityGroupName(), secruityGroupName);
        assertEquals(resultList.get(0).getOutboundName(), "Port_8080");
        assertEquals(resultList.get(0).getPriority().toString(), "100");
        assertEquals(resultList.get(0).getPort(), "0-65535");
        assertEquals(resultList.get(0).getProtocol(), "*");
        assertEquals(resultList.get(0).getSource(), "*");
        assertEquals(resultList.get(0).getDestination(), "*");
        assertEquals(resultList.get(0).getAction(), "Allow");*/
    }
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure SecurityGroup 생성 TEST
     * @title : testCreateAzureSecurityGroup
     * @return : void
     ***************************************************/
    @Test
    public void testCreateAzureSecurityGroup(){
    	getAzureAccountInfo();
    	AzureSecurityGroupMgntDTO dto = setAzureSecurityGroupInfo();
    	mockAzureSecurityGroupMgntService.createAzureSecurityGroup(dto, principal);
    }   
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure InboundRules 생성 TEST
     * @title : testCreateAzureInboundRules
     * @return : void
     ***************************************************/
    @Test
    public void testCreateAzureInboundRules(){
    	getAzureAccountInfo();
    	AzureSecurityGroupMgntDTO dto = setAzureInboundRules();
    	mockAzureSecurityGroupMgntService.createAzureInboundRules(dto, principal);
    } 
    
    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : Azure SecurityGroup Inbound rules정보 목록 값 설정  
     * @title : setAzureInboundRules
     * @return : AzureSecurityGroupMgntDTO
     ***************************************************/
    private AzureSecurityGroupMgntDTO setAzureInboundRules() {
    	AzureSecurityGroupMgntDTO dto = new AzureSecurityGroupMgntDTO();
        dto.setAccountId(1);
        dto.setSecurityGroupName("test-securityGroupName");
        dto.setPriority(100);
        dto.setInboundName("test-inboundName");
        dto.setPort("22");
        dto.setProtocol("tcp");
        dto.setSource("v-network");
        dto.setDestination("v-network");
        dto.setAction("Allow");
        return dto;
	}

	/***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure SecurityGroup 삭제 TEST
     * @title : testDeleteSecurityGroupInfo
     * @return : void
     ***************************************************/
    @Test
    public void testDeleteSecurityGroupInfo(){
    	getAzureAccountInfo();
    	AzureSecurityGroupMgntDTO dto = setAzureSecurityGroupInfo();
    	mockAzureSecurityGroupMgntService.deleteAzureSecurityGroup(dto, principal);
    }
    
    /***************************************************
    * @project : 인프라 관리 대시보드
    * @description : Azure SecurityGroup 정보 설정
    * @title : setAzureSecurityGroupInfo
    * @return : AzureSecurityGroupMgntDTO
    ***************************************************/
    public AzureSecurityGroupMgntDTO setAzureSecurityGroupInfo() {
    	AzureSecurityGroupMgntDTO dto = new AzureSecurityGroupMgntDTO();
        dto.setAccountId(1);
        dto.setSecurityGroupName("test-securityGroupName");
        dto.setSubscriptionName("test-subscriptionName");
        dto.setAzureSubscriptionId("test-azureSubscriptionId");
        dto.setLocation("koreaSouth");
        dto.setResourceGroupName("test-resourceGroupName");
        return dto;
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
     * @description : Azure 구독 정보 조회 값 설정
     * @title : setAzureSubscriptionInfo
     * @return : AzureNetworkMgntVO
     ***************************************************/
    public AzureSecurityGroupMgntVO setAzureSubscriptionInfo(){
        AzureSecurityGroupMgntVO vo = new AzureSecurityGroupMgntVO();
        vo.setAccountId(1);
        vo.setAzureSubscriptionId("test-azureSubscriptionId");
        String subscriptionName = "";
        when(mockCommonIaasService.getSubscriptionNameFromAzure(any(), anyString())).thenReturn(subscriptionName);
        vo.setSubscriptionName(subscriptionName);
        return vo;
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
