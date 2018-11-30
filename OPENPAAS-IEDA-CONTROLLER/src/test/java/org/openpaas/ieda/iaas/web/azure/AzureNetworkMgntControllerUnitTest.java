package org.openpaas.ieda.iaas.web.azure;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.controller.iaasMgnt.azureMgnt.web.network.AzureNetworkMgntController;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.network.dao.AzureNetworkMgntVO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.network.dto.AzureNetworkMgntDTO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.network.service.AzureNetworkMgntService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class AzureNetworkMgntControllerUnitTest {
    
    @InjectMocks AzureNetworkMgntController mockAzureNetworkMgntController;
    @Mock AzureNetworkMgntService mockAzureNetworkMgntService; 
    
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    
    /*************************************** URL *******************************************/
    final static String VIEW_URL = "/azureMgnt/network";
    final static String NETWORK_LIST_INFO_URL = "/azureMgnt/network/list/{accountId}";
    final static String NETWORK_SUBNETS_INFO_URL = "/azureMgnt/network/list/subnets/{accountId}/{networkName}";
    final static String NETWORK_SAVE_URL = "/azureMgnt/network/save";
    final static String NETWORK_DELETE_URL = "/azureMgnt/network/delete";
    //final static String NETWORK_SUBNETS_ADD_URL = "/azureMgnt/subnet/save";
    final static String AZURE_RESOURCE_GROUP_LIST_URL = "/azureMgnt/resourceGroup/list/groupInfo/{accountId}";
    final static String AZURE_SUBSCRIPTION_LIST_URL = "/azureMgnt/network/list/subscriptionInfo/{accountId}";
    
    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockAzureNetworkMgntController).build();
    }
    
    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : 시큐리티 토큰 생성
     * @title : getLoggined
     * @return : Principal
     ***************************************************/
    public Principal getLoggined() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken("admin", "admin");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(auth);
        securityContext.getAuthentication().getPrincipal();
        return auth;
    }
    
    /***************************************************
      * @project : Azure 관리 대시보드
      * @description : Azure Network 관리 화면 이동 TEST
      * @title : testGoAzureNetworkMgnt
      * @return : void
      ***************************************************/
    @Test
    public void testGoAzureNetworkMgnt() throws Exception{
        mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
       .andExpect(status().isOk())
       .andExpect(view().name("iaas/azure/network/azureNetworkMgnt"));
   }
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE Network목록 조회 TEST
     * @title : testGetAzureNetworkInfoList
     * @return : void
     ***************************************************/
    @Test
    public void testGetAzureNetworkInfoList() throws Exception{
        List<AzureNetworkMgntVO> list = getAzureNetworkInfoList();
        when(mockAzureNetworkMgntService.getAzureNetworkInfoList(any(), anyInt())).thenReturn(list);
        mockMvc.perform(get(NETWORK_LIST_INFO_URL, 1).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].accountId").value(1))
        .andExpect(jsonPath("$.records[0].recid").value(1))
        .andExpect(jsonPath("$.records[0].networkName").value("test-name"))
        .andExpect(jsonPath("$.records[0].networkAddressSpaceCidr").value("10.0.0.0/16"))
        .andExpect(jsonPath("$.records[0].resourceGroupName").value("test-rg-name"))
        .andExpect(jsonPath("$.records[0].location").value("koreasouth"))
        .andExpect(jsonPath("$.records[0].subnetName").value("test-subnet-name"))
        .andExpect(jsonPath("$.records[0].subnetAddressRangeCidr").value("10.0.0.0/24"))
        .andExpect(jsonPath("$.records[0].subscriptionName").value("test-subscriptionName"));
    }
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE Network목록 조회 값 설정 
     * @title : getAzureNetworkInfoList
     * @return : List<AzureNetworkMgntVO>
     ***************************************************/
    private List<AzureNetworkMgntVO> getAzureNetworkInfoList(){
        List<AzureNetworkMgntVO> list = new ArrayList<AzureNetworkMgntVO>();
        AzureNetworkMgntVO vo = new AzureNetworkMgntVO();
        vo.setAccountId(1);
        vo.setRecid(1);
        vo.setNetworkName("test-name");
        vo.setNetworkAddressSpaceCidr("10.0.0.0/16");
        vo.setResourceGroupName("test-rg-name");
        vo.setLocation("koreasouth");
        vo.setSubnetName("test-subnet-name");
        vo.setSubnetAddressRangeCidr("10.0.0.0/24");
        vo.setSubscriptionName("test-subscriptionName");
        list.add(vo);
        return list;
    }

    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE network subnets 목록 조회 TEST
     * @title : testGetAzureNetworkSubnetsInfo
     * @return : void
     ***************************************************/
    @Test
    public void testGetAzureNetworkSubnetsInfo() throws Exception{
        List<AzureNetworkMgntVO> list = getAzureNetworkSubnetsInfo();
        when(mockAzureNetworkMgntService.getAzureNetworkSubnetsInfoList(any(), anyInt(), any())).thenReturn(list);
        mockMvc.perform(get(NETWORK_SUBNETS_INFO_URL, 1, "AzureNetwork").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].accountId").value(1))
        .andExpect(jsonPath("$.records[0].recid").value(1))
        .andExpect(jsonPath("$.records[0].subnetName").value("test-subnet-name"))
        .andExpect(jsonPath("$.records[0].subnetAddressRangeCidr").value("10.0.0.0/24"))
        .andExpect(jsonPath("$.records[0].subnetAddressesCnt").value(251))
        .andExpect(jsonPath("$.records[0].securityGroupName").value("test-securityGroupName"));
    }
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE network subnets 목록 조회 값 설정  
     * @title : getAzureNetworkSubnetsInfo
     * @return : List<AzureNetworkMgntVO>
     ***************************************************/
    private List<AzureNetworkMgntVO> getAzureNetworkSubnetsInfo(){
        List<AzureNetworkMgntVO> list = new ArrayList<AzureNetworkMgntVO>();
        AzureNetworkMgntVO vo = new AzureNetworkMgntVO();
        vo.setAccountId(1);
        vo.setRecid(1);
        vo.setSubnetName("test-subnet-name");
        vo.setSubnetAddressRangeCidr("10.0.0.0/24");
        vo.setSubnetAddressesCnt(251);
        vo.setSecurityGroupName("test-securityGroupName");
        list.add(vo);
        return list;
    }
    
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE Network 생성 TEST
     * @title : testSaveNetworkInfo
     * @return : void
     ***************************************************/
    @Test    
    public void testSaveNetworkInfo() throws Exception{
        AzureNetworkMgntDTO dto = setNetworkInfo();
         mockMvc.perform(post(NETWORK_SAVE_URL).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto))).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
    }
    
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE Network 생성 DTO 값 설정  
     * @title : setNetworkInfo
     * @return : AzureNetworkMgntDTO
     ***************************************************/
    private AzureNetworkMgntDTO setNetworkInfo(){
        AzureNetworkMgntDTO dto = new AzureNetworkMgntDTO();
        dto.setAccountId(1);
        dto.setNetworkName("test-networkName");
        dto.setNetworkAddressSpaceCidr("10.0.0.0/16");
        dto.setResourceGroupName("test-resourceGroupName");
        dto.setLocation("koreasouth");
        dto.setSubnetName("test-subnetName");
        dto.setSubnetAddressRangeCidr("10.0.0.0/24");
        return null;
    }
    
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE NetworkInfo 삭제 TEST
     * @title : testDeleteNetworkInfo
     * @return : void
     ***************************************************/
    @Test
    public void testDeleteNetworkInfo() throws Exception{
        AzureNetworkMgntDTO dto = setNetworkInfo();
        mockMvc.perform(delete(NETWORK_DELETE_URL).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto))).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNoContent());
    }
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE 리소스 그룹 목록 조회 TEST
     * @title : testGetResourceGroupInfoList
     * @return : void
     ***************************************************/
    @Test
    public void testGetResourceGroupInfoList() throws Exception{
    List<AzureNetworkMgntVO> list = getResourceGroupInfoList();
        when(mockAzureNetworkMgntService.getResourceGroupInfoList(any(), anyInt())).thenReturn(list);
        mockMvc.perform(get(AZURE_RESOURCE_GROUP_LIST_URL, 1).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].accountId").value(1))
        .andExpect(jsonPath("$.records[0].recid").value(1))
        .andExpect(jsonPath("$.records[0].resourceGroupName").value("test-resource-group-name"))
        .andExpect(jsonPath("$.records[0].location").value("koreaSouth"));
            
    }
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE 리소스 그룹 목록 조회 값 설정 
     * @title : getResourceGroupInfoList
     * @return : List<AzureNetworkMgntVO>
     ***************************************************/
    private List<AzureNetworkMgntVO> getResourceGroupInfoList(){
        List<AzureNetworkMgntVO> list = new ArrayList<AzureNetworkMgntVO>();
        AzureNetworkMgntVO vo = new AzureNetworkMgntVO();
        vo.setAccountId(1);
        vo.setRecid(1);
        vo.setResourceGroupName("test-resource-group-name");
        vo.setLocation("koreaSouth");
        list.add(vo);
        return list;
    }
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE 구독 목록 조회 TEST
     * @title : testGetAzureSubscription
     * @return : void
     ***************************************************/
    @Test
    public void testGetAzureSubscription() throws Exception{
		AzureNetworkMgntVO vo = getAzureSubscription();
	    when(mockAzureNetworkMgntService.getAzureSubscriptionInfo(any(), anyInt())).thenReturn(vo);
	    mockMvc.perform(get(AZURE_SUBSCRIPTION_LIST_URL, 1).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
	    .andExpect(status().isOk())
	    .andExpect(jsonPath("$.accountId").value(1))
	    .andExpect(jsonPath("$.recid").value(1))
	    .andExpect(jsonPath("$.azureSubscriptionId").value("test-subscription-id"))
	    .andExpect(jsonPath("$.subscriptionName").value("test-subscription-name"))
	    .andExpect(jsonPath("$.location").value("koreaSouth"));
            
    }
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE 구독 목록 조회 값 설정 
     * @title : getAzureSubscription
     * @return :  List<AzureNetworkMgntVO>
     ***************************************************/
    private AzureNetworkMgntVO getAzureSubscription(){
        AzureNetworkMgntVO vo = new AzureNetworkMgntVO();
        vo.setAccountId(1);
        vo.setRecid(1);
        vo.setSubscriptionName("test-subscription-name");
        vo.setAzureSubscriptionId("test-subscription-id");
        vo.setLocation("koreaSouth");
        return vo;
    }
    
}
