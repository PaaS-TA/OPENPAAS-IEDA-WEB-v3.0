package org.openpaas.ieda.iaas.web.azure;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.controller.iaasMgnt.azureMgnt.web.securityGroup.AzureSecurityGroupMgntController;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.securityGroup.dao.AzureSecurityGroupMgntVO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.securityGroup.dto.AzureSecurityGroupMgntDTO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.securityGroup.service.AzureSecurityGroupMgntService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class AzureSecurityGroupMgntControllerUnitTest {
    @InjectMocks AzureSecurityGroupMgntController mockAzureSecurityGroupMgntController;
    @Mock AzureSecurityGroupMgntService mockAzureSecurityGroupMgntService; 
    
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    
    /*************************************** URL *******************************************/
    final static String VIEW_URL = "/azureMgnt/securityGroup";
    final static String SECURITY_GROUP_LIST_INFO_URL = "/azureMgnt/securityGroup/list/{accountId}";
    final static String SECURITY_GROUP_INBOUND_RULES_LIST_URL = "/azureMgnt/securityGroup/list/inbound/{accountId}/{securityGroupName}";
    final static String SECURITY_GROUP_OUTBOUND_RULES_LIST_URL = "/azureMgnt/securityGroup/list/outbound/{accountId}/{securityGroupName}";
    final static String SECURITY_GROUP_SAVE_URL = "/azureMgnt/securityGroup/save";
    final static String SECURITY_GROUP_DELETE_URL = "/azureMgnt/securityGroup/delete";
    final static String SECURITY_GROUP_INBOUND_SAVE_URL = "/azureMgnt/inbound/save";
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
        mockMvc = MockMvcBuilders.standaloneSetup(mockAzureSecurityGroupMgntController).build();
    }
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE SecurityGroup목록 조회 TEST
     * @title : testGetAzureSecurityGroupInfoList
     * @return : void
     ***************************************************/
    @Test
    public void testGetAzureSecurityGroupInfoList() throws Exception{
        List<AzureSecurityGroupMgntVO> list = getAzureSecurityGroupInfoList();
        when(mockAzureSecurityGroupMgntService.getAzureSecurityGroupInfoList(any(), anyInt())).thenReturn(list);
        mockMvc.perform(get(SECURITY_GROUP_LIST_INFO_URL, 1).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].accountId").value(1))
        .andExpect(jsonPath("$.records[0].recid").value(1))
        .andExpect(jsonPath("$.records[0].securityGroupName").value("test-name"))
        .andExpect(jsonPath("$.records[0].subscriptionName").value("test-subscriptionName"))
        .andExpect(jsonPath("$.records[0].azureSubscriptionId").value("test-subscription-id"))
        .andExpect(jsonPath("$.records[0].location").value("koreasouth"))
        .andExpect(jsonPath("$.records[0].resourceGroupName").value("test-rg-name"));
    }
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE Security Group 목록 조회 값 설정 
     * @title : getAzureSecurityGroupInfoList
     * @return : List<AzureSecurityGroupMgntVO>
     ***************************************************/
    private List<AzureSecurityGroupMgntVO> getAzureSecurityGroupInfoList(){
        List<AzureSecurityGroupMgntVO> list = new ArrayList<AzureSecurityGroupMgntVO>();
        AzureSecurityGroupMgntVO vo = new AzureSecurityGroupMgntVO();
        vo.setAccountId(1);
        vo.setRecid(1);
        vo.setSecurityGroupName("test-name");
        vo.setSubscriptionName("test-subscriptionName");
        vo.setAzureSubscriptionId("test-subscription-id");
        vo.setLocation("koreasouth");
        vo.setResourceGroupName("test-rg-name");
        list.add(vo);
        return list;
    }
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE Security Group Inbounds 목록 조회 TEST
     * @title : testGetAzureSecurityGroupInbounds
     * @return : void
     ***************************************************/
    @Test
    public void testGetAzureSecurityGroupInbounds() throws Exception{
        List<AzureSecurityGroupMgntVO> list = getAzureSecurityGroupInboundsList();
        String securityGroupName = "test-name";
        when(mockAzureSecurityGroupMgntService.getAzureSecurityGroupInboundRules(any(), anyInt(), any())).thenReturn(list);
        mockMvc.perform(get(SECURITY_GROUP_INBOUND_RULES_LIST_URL, 1, securityGroupName).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].accountId").value(1))
        .andExpect(jsonPath("$.records[0].recid").value(1))
        .andExpect(jsonPath("$.records[0].securityGroupName").value("test-name"))
        .andExpect(jsonPath("$.records[0].priority").value(100))
        .andExpect(jsonPath("$.records[0].inboundName").value("test-inboundName"))
        .andExpect(jsonPath("$.records[0].port").value("22"))
        .andExpect(jsonPath("$.records[0].protocol").value("tcp"))
        .andExpect(jsonPath("$.records[0].source").value("test-VirtualNetWork"))
        .andExpect(jsonPath("$.records[0].destination").value("test-VirtualNetWork"))
        .andExpect(jsonPath("$.records[0].action").value("test-Allow"));
    }   
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE Security Group Inbounds 목록 조회 값 설정 
     * @title : getAzureSecurityGroupInboundsList
     * @return : List<AzureSecurityGroupMgntVO>
     ***************************************************/
    private List<AzureSecurityGroupMgntVO> getAzureSecurityGroupInboundsList(){
        List<AzureSecurityGroupMgntVO> list = new ArrayList<AzureSecurityGroupMgntVO>();
        AzureSecurityGroupMgntVO vo = new AzureSecurityGroupMgntVO();
        vo.setAccountId(1);
        vo.setRecid(1);
        vo.setSecurityGroupName("test-name");
        vo.setPriority(100);
        vo.setInboundName("test-inboundName");
        vo.setPort("22");
        vo.setProtocol("tcp");
        vo.setSource("test-VirtualNetWork");
        vo.setDestination("test-VirtualNetWork");
        vo.setAction("test-Allow");
        list.add(vo);
        return list;
    }
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE Security Group Outbounds 목록 조회 TEST
     * @title : testGetAzureSecurityGroupOutbounds
     * @return : void
     ***************************************************/
    @Test
    public void testGetAzureSecurityGroupOutbounds() throws Exception{
        List<AzureSecurityGroupMgntVO> list = getAzureSecurityGroupOutboundsList();
        String securityGroupName = "test-name";
        when(mockAzureSecurityGroupMgntService.getAzureSecurityGroupOutboundRules(any(), anyInt(), any())).thenReturn(list);
        mockMvc.perform(get(SECURITY_GROUP_OUTBOUND_RULES_LIST_URL, 1, securityGroupName).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].accountId").value(1))
        .andExpect(jsonPath("$.records[0].recid").value(1))
        .andExpect(jsonPath("$.records[0].securityGroupName").value("test-name"))
        .andExpect(jsonPath("$.records[0].priority").value(100))
        .andExpect(jsonPath("$.records[0].outboundName").value("test-outbound"))
        .andExpect(jsonPath("$.records[0].port").value("22"))
        .andExpect(jsonPath("$.records[0].protocol").value("tcp"))
        .andExpect(jsonPath("$.records[0].source").value("test-VirtualNetWork"))
        .andExpect(jsonPath("$.records[0].destination").value("test-VirtualNetWork"))
        .andExpect(jsonPath("$.records[0].action").value("test-Allow"));
    }
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE Security Group Outbounds 목록 조회 값 설정 
     * @title : getAzureSecurityGroupOutboundsList
     * @return : List<AzureSecurityGroupMgntVO>
     ***************************************************/
    private List<AzureSecurityGroupMgntVO> getAzureSecurityGroupOutboundsList(){
        List<AzureSecurityGroupMgntVO> list = new ArrayList<AzureSecurityGroupMgntVO>();
        AzureSecurityGroupMgntVO vo = new AzureSecurityGroupMgntVO();
        vo.setAccountId(1);
        vo.setRecid(1);
        vo.setSecurityGroupName("test-name");
        vo.setPriority(100);
        vo.setOutboundName("test-outbound");
        vo.setPort("22");
        vo.setProtocol("tcp");
        vo.setSource("test-VirtualNetWork");
        vo.setDestination("test-VirtualNetWork");
        vo.setAction("test-Allow");
        list.add(vo);
        return list;
    }   
    
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE SecurityGroup 생성 TEST
     * @title : testCreateAzureSecurityGroup
     * @return : void
     ***************************************************/
    @Test    
    public void testCreateAzureSecurityGroup() throws Exception{
        AzureSecurityGroupMgntDTO dto = setSecurityGroupInfo();
         mockMvc.perform(post(SECURITY_GROUP_SAVE_URL).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto))).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
    }
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE SecurityGroup 생성 DTO 값 설정  
     * @title : setSecurityGroupInfo
     * @return : AzureSecurityGroupMgntDTO
     ***************************************************/
    private AzureSecurityGroupMgntDTO setSecurityGroupInfo(){
        AzureSecurityGroupMgntDTO dto = new AzureSecurityGroupMgntDTO();
        dto.setAccountId(1);
        dto.setSecurityGroupName("test-securityGroupName");
        dto.setSubscriptionName("test-subscriptionName");
        dto.setAzureSubscriptionId("test-azureSubscriptionId");
        dto.setLocation("koreasouth");
        dto.setResourceGroupName("test-resourceGroupName");
        return null;
    }
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE SecurityGroup 삭제 TEST 
     * @title : testDeleteAzureSecurityGroup
     * @return : void
     ***************************************************/
    @Test
    public void testDeleteAzureSecurityGroup() throws Exception{
        AzureSecurityGroupMgntDTO dto = setSecurityGroupInfo();
        mockMvc.perform(delete(SECURITY_GROUP_DELETE_URL).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto))).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNoContent());
    }
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE SecurityGroup Inbound Rules생성
     * @title : testCreateAzureInboundRules
     * @return : void
     ***************************************************/
    @Test    
    public void testCreateAzureInboundRules() throws Exception{
        AzureSecurityGroupMgntDTO dto = setSecurityGroupInfo();
         mockMvc.perform(post(SECURITY_GROUP_INBOUND_SAVE_URL).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto))).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
    }    
}
