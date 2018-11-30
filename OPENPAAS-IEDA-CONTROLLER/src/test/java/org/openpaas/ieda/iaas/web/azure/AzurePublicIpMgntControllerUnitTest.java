package org.openpaas.ieda.iaas.web.azure;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.controller.iaasMgnt.azureMgnt.web.publicIp.AzurePublicIpMgntController;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.publicIp.dao.AzurePublicIpMgntVO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.publicIp.dto.AzurePublicIpMgntDTO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.publicIp.service.AzurePublicIpMgntService;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AzurePublicIpMgntControllerUnitTest {
    @InjectMocks AzurePublicIpMgntController mockAzurePublicIpMgntController;
    @Mock AzurePublicIpMgntService mockAzurePublicIpMgntService;
    @Mock CommonIaasService commonIaasService;
    Principal principal = null;
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    
    /*************************************** URL *******************************************/
    final static String VIEW_URL = "/azureMgnt/publicIp";
    final static String PUBLIC_IP_INFO_LIST_URL = "/azureMgnt/publicIp/list/{accountId}";
    final static String PUBLIC_IP_SAVE_URL = "/azureMgnt/publicIp/save";
    
    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockAzurePublicIpMgntController).build();
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
     * @description : Azure Public IP  관리 화면 이동 TEST
     * @title : testGoAzurePublicIpMgnt
     * @return : void
     ***************************************************/
    @Test
    public void testGoAzurePublicIpMgnt() throws Exception{
        mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
       .andExpect(status().isOk())
       .andExpect(view().name("iaas/azure/publicIp/azurePublicIpMgnt"));
    }
    
    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : Azure Public IP 목록 조회 TEST
     * @title : testGetAzurePublicIpInfoList
     * @return : void
     ***************************************************/
    @Test
    public void testGetAzurePublicIpInfoList() throws Exception{
        List<AzurePublicIpMgntVO> list = getAzurePublicIpResultInfoList();
        when(mockAzurePublicIpMgntService.getAzurePublicIpInfoList(any(), anyInt())).thenReturn(list);
        mockMvc.perform(get(PUBLIC_IP_INFO_LIST_URL, 1 ).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].accountId").value(1))
        .andExpect(jsonPath("$.records[0].recid").value(1))
        .andExpect(jsonPath("$.records[0].publicIpName").value("test-publicIpName"))
        .andExpect(jsonPath("$.records[0].publicIpAddress").value("104.43.242.133"))
        .andExpect(jsonPath("$.records[0].subscriptionName").value("test-subscriptionName"))
        .andExpect(jsonPath("$.records[0].location").value("us-west-2"))
        .andExpect(jsonPath("$.records[0].resourceGroupName").value("test-resourceGroupName"));
    }
    
    /***************************************************
    * @project : Azure 관리 대시보드
    * @description : Azure Public IP 목록 조회 결과 값 설정
    * @title : getAzurePublicIpResultInfoList
    * @return : List<AzurePublicIpMgntVO> 
    ***************************************************/
    private  List<AzurePublicIpMgntVO> getAzurePublicIpResultInfoList(){
        List<AzurePublicIpMgntVO> list = new ArrayList<AzurePublicIpMgntVO>();
        AzurePublicIpMgntVO vo = new AzurePublicIpMgntVO();
        vo.setAccountId(1);
        vo.setRecid(1);
        vo.setPublicIpName("test-publicIpName");
        vo.setPublicIpAddress("104.43.242.133");
        vo.setSubscriptionName("test-subscriptionName");
        vo.setLocation("us-west-2");
        vo.setResourceGroupName("test-resourceGroupName");
        list.add(vo);
        return list;
    }
    
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE Public IP 할당  TEST
     * @title : testSavePublicIpInfo
     * @return : void
     ***************************************************/
    @Test    
    public void testSavePublicIpInfo() throws Exception{
        AzurePublicIpMgntDTO dto = setPublicIpInfo();
        mockMvc.perform(post(PUBLIC_IP_SAVE_URL).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto))).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
    }
    
    /***************************************************
    * @project : Azure 관리 대시보드
    * @description : Azure Public IP 할당 값 설정
    * @title : setPublicIpInfo
    * @return : AzurePublicIpMgntDTO
    ***************************************************/
    private AzurePublicIpMgntDTO setPublicIpInfo(){
        AzurePublicIpMgntDTO dto = new AzurePublicIpMgntDTO();
        dto.setAccountId(1);
        dto.setPublicIpName("test-publicIpName");
        dto.setSubscriptionName("test-subscriptionName");
        dto.setResourceGroupName("test-resourceGroupName");
        dto.setLocation("us-west-2");
        return null;
    }
}
