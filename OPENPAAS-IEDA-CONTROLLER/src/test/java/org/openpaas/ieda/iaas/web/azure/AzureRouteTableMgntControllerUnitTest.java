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
import org.openpaas.ieda.controller.iaasMgnt.azureMgnt.web.routeTable.AzureRouteTableMgntController;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.routeTable.dao.AzureRouteTableMgntVO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.routeTable.dto.AzureRouteTableMgntDTO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.routeTable.service.AzureRouteTableMgntService;
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
public class AzureRouteTableMgntControllerUnitTest {
    
    @InjectMocks AzureRouteTableMgntController mockAzureRouteTableMgntController;
    @Mock AzureRouteTableMgntService mockAzureRouteTableMgntService; 
    
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    
    /*************************************** URL *******************************************/
    final static String VIEW_URL = "/azureMgnt/routeTable";
    final static String ROUTE_TABLE_LIST_INFO_URL = "/azureMgnt/routeTable/list/{accountId}";
    final static String ROUTE_TABLE_SUBNETS_INFO_URL = "/azureMgnt/routeTable/list/subnets/{accountId}/{routeTableName}";
    final static String ROUTE_TABLE_SAVE_URL = "/azureMgnt/routeTable/save";
    final static String ROUTE_TABLE_DELETE_URL = "/azureMgnt/routeTable/delete";
    final static String ROUTE_TABLE_SUBNETS_ASSOCIATE_URL = "/azureMgnt/routeTable/subnet/save";
    final static String ROUTE_TABLE_SUBNETS_DISASSOCIATE_URL = "/azureMgnt/routeTable/subnet/delete";
    final static String ROUTE_TABLE_NETWORK_NAME_LIST_URL = "/azureMgnt/routeTable/list/networkName/{accountId}/{resourceGroupName}";
    final static String ROUTE_TABLE_SUBNET_NAME_LIST_URL = "/azureMgnt/routeTable/list/subnetName/{accountId}/{resourceGroupName}/{networkName}";
    
    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockAzureRouteTableMgntController).build();
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
      * @description : Azure RouteTable 관리 화면 이동 TEST
      * @title : testGoAzureRouteTableMgnt
      * @return : void
      ***************************************************/
    @Test
    public void testGoAzureRouteTableMgnt() throws Exception{
        mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
       .andExpect(status().isOk())
       .andExpect(view().name("iaas/azure/routeTable/azureRouteTableMgnt"));
   }
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE RouteTable목록 조회 TEST
     * @title : testGetAzureRouteTableInfoList
     * @return : void
     ***************************************************/
    @Test
    public void testGetAzureRouteTableInfoList() throws Exception{
        List<AzureRouteTableMgntVO> list = getAzureRouteTableInfoList();
        when(mockAzureRouteTableMgntService.getAzureRouteTableInfoList(any(), anyInt())).thenReturn(list);
        mockMvc.perform(get(ROUTE_TABLE_LIST_INFO_URL, 1).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].accountId").value(1))
        .andExpect(jsonPath("$.records[0].recid").value(1))
        .andExpect(jsonPath("$.records[0].routeTableName").value("test-name"))
        .andExpect(jsonPath("$.records[0].resourceGroupName").value("test-rg-name"))
        .andExpect(jsonPath("$.records[0].location").value("koreasouth"))
        .andExpect(jsonPath("$.records[0].subscriptionName").value("test-subscriptionName"))
        .andExpect(jsonPath("$.records[0].azureSubscriptionId").value("test-subscriptionId"))
        .andExpect(jsonPath("$.records[0].associations").value(3));
    }
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE RouteTable목록 조회 값 설정 
     * @title : getAzureRouteTableInfoList
     * @return : List<AzureRouteTableMgntVO>
     ***************************************************/
    private List<AzureRouteTableMgntVO> getAzureRouteTableInfoList(){
        List<AzureRouteTableMgntVO> list = new ArrayList<AzureRouteTableMgntVO>();
        AzureRouteTableMgntVO vo = new AzureRouteTableMgntVO();
        vo.setAccountId(1);
        vo.setRecid(1);
        vo.setRouteTableName("test-name");
        vo.setResourceGroupName("test-rg-name");
        vo.setLocation("koreasouth");
        vo.setSubscriptionName("test-subscriptionName");
        vo.setAzureSubscriptionId("test-subscriptionId");
        vo.setAssociations(3);
        list.add(vo);
        return list;
    }
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE RouteTable Associated Subnets목록 조회 TEST
     * @title : testGetAzureRouteTableSubnetInfoList
     * @return : void
     ***************************************************/
    @Test
    public void testGetAzureRouteTableSubnetInfoList() throws Exception{
        List<AzureRouteTableMgntVO> list = getAzureAssoicatedSubnetInfoList();
        String routeTableName = "test-name";
        when(mockAzureRouteTableMgntService.getAzureRouteTableSubnetInfoList(any(), anyInt(), any())).thenReturn(list);
        mockMvc.perform(get(ROUTE_TABLE_SUBNETS_INFO_URL, 1, routeTableName).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].accountId").value(1))
        .andExpect(jsonPath("$.records[0].recid").value(1))
        .andExpect(jsonPath("$.records[0].routeTableName").value("test-name"))
        .andExpect(jsonPath("$.records[0].subnetName").value("test-subnetName"))
        .andExpect(jsonPath("$.records[0].subnetAddressRange").value("10.10.0.0/24"))
        .andExpect(jsonPath("$.records[0].networkName").value("test-networkName"))
        .andExpect(jsonPath("$.records[0].securityGroupName").value("-"));
    }
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE RouteTable Associated Subnets 목록 조회 값 설정 
     * @title : getAzureAssoicatedSubnetInfoList
     * @return : List<AzureRouteTableMgntVO>
     ***************************************************/
    private List<AzureRouteTableMgntVO> getAzureAssoicatedSubnetInfoList(){
        List<AzureRouteTableMgntVO> list = new ArrayList<AzureRouteTableMgntVO>();
        AzureRouteTableMgntVO vo = new AzureRouteTableMgntVO();
        vo.setAccountId(1);
        vo.setRecid(1);
        vo.setRouteTableName("test-name");
        vo.setSubnetName("test-subnetName");
        vo.setSubnetAddressRange("10.10.0.0/24");
        vo.setNetworkName("test-networkName");
        vo.setSecurityGroupName("-");
        list.add(vo);
        return list;
    }
    
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE RouteTable 생성 TEST
     * @title : testSaveRouteTableInfo
     * @return : void
     ***************************************************/
    @Test    
    public void testSaveRouteTableInfo() throws Exception{
        AzureRouteTableMgntDTO dto = setRouteTableInfo();
         mockMvc.perform(post(ROUTE_TABLE_SAVE_URL).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto))).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
    }
    
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE RouteTable 생성 DTO 값 설정  
     * @title : setRouteTableInfo
     * @return : AzureRouteTableMgntDTO
     ***************************************************/
    private AzureRouteTableMgntDTO setRouteTableInfo(){
        AzureRouteTableMgntDTO dto = new AzureRouteTableMgntDTO();
        dto.setAccountId(1);
        dto.setRouteTableName("test-routeTableName");
        dto.setResourceGroupName("test-resourceGroupName");
        dto.setLocation("koreasouth");
        dto.setSubscriptionName("test-subscriptionName");
        return null;
    }
    
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE RouteTable Info 삭제 TEST
     * @title : testDeleteRouteTableInfo
     * @return : void
     ***************************************************/
    @Test
    public void testDeleteNetworkInfo() throws Exception{
        AzureRouteTableMgntDTO dto = setRouteTableInfo();
        mockMvc.perform(delete(ROUTE_TABLE_DELETE_URL).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto))).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNoContent());
    }

    /***************************************************
     * @param resourceGroupName 
     * @project : AZURE 관리 대시보드
     * @description : AZURE Network Name 목록 조회 TEST
     * @title : testGetAzureNetworkNameList
     * @return : void
     ***************************************************/
    @Test
    public void testGetAzureNetworkNameList() throws Exception{
        List<String> list = getAzureNetworkNameList();
        String resourceGroupName ="paas-ta-resoureceGroup";
        when(mockAzureRouteTableMgntService.getAzureNetworkName(any(), anyInt(), any())).thenReturn(list);
        mockMvc.perform(get(ROUTE_TABLE_NETWORK_NAME_LIST_URL, 1, resourceGroupName).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0]").value("test-networkName"));
    }
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE Network Name 목록 조회 값 설정 
     * @title : getAzureNetworkNameList
     * @return : List<AzureRouteTableMgntVO>
     ***************************************************/
    private List<String> getAzureNetworkNameList(){
        List<String> list = new ArrayList<String>();
        String networkName = "test-networkName";
        list.add(0,networkName);
        return list;
    }
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE Network에 대한 SubnetName 목록 조회 TEST
     * @title : testGetAzureSubnetNameList
     * @return : void
     ***************************************************/
    @Test
    public void testGetAzureSubnetNameList() throws Exception{
        List<String> list = getAzureSubnetNameList();
        String rgName = "paas-ta-resoureceGroup";
        String networkName = "paas-ta-net";
        when(mockAzureRouteTableMgntService.getAzureSubnetName(any(), anyInt(), any(), any())).thenReturn(list);
        mockMvc.perform(get(ROUTE_TABLE_SUBNET_NAME_LIST_URL, 1 , rgName, networkName ).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0]").value("test-subnetName"));
    }
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE Subnets Name 목록 조회 값 설정 
     * @title : getAzureSubnetNameList
     * @return : List<String>
     ***************************************************/
    private List<String> getAzureSubnetNameList(){
        List<String> list = new ArrayList<String>();
        String subnetName = "test-subnetName";
        list.add(subnetName);
        return list;
    }
    
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE RouteTable Associate Subnet TEST
     * @title : testAssociateAzureSubnet
     * @return : void
     ***************************************************/
    @Test    
    public void testAssociateAzureSubnet() throws Exception{
        AzureRouteTableMgntDTO dto = setSubnetInfo();
         mockMvc.perform(post(ROUTE_TABLE_SUBNETS_ASSOCIATE_URL).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto))).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
    }
    
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE RouteTable Subnet Association DTO 값 설정  
     * @title : setSubnetInfo
     * @return : AzureRouteTableMgntDTO
     ***************************************************/
    private AzureRouteTableMgntDTO setSubnetInfo(){
        AzureRouteTableMgntDTO dto = new AzureRouteTableMgntDTO();
        dto.setAccountId(1);
        dto.setRouteTableName("test-routeTableName");
        dto.setResourceGroupName("test-resourceGroupName");
        dto.setSubnetName("test-subnetName");
        dto.setNetworkName("test-networkName");
        return null;
    }
    
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE RouteTable Disassociate Subnet TEST
     * @title : testDisassociateAzureSubnet
     * @return : void
     ***************************************************/
    @Test
    public void testDisassociateAzureSubnet() throws Exception{
        AzureRouteTableMgntDTO dto = setSubnetInfo();
        mockMvc.perform(delete(ROUTE_TABLE_SUBNETS_DISASSOCIATE_URL).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto))).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNoContent());
    }
    
}
