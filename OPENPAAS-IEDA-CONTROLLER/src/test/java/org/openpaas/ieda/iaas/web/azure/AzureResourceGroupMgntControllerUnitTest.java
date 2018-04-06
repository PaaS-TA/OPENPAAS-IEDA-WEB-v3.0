/*package org.openpaas.ieda.iaas.web.azure;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.azureMgnt.web.resourceGroup.dao.AzureResourceGroupMgntVO;
import org.openpaas.ieda.azureMgnt.web.resourceGroup.service.AzureResourceGroupMgntService;
import org.openpaas.ieda.controller.iaasMgnt.azureMgnt.web.resourceGroup.AzureResourceGroupMgntController;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AzureResourceGroupMgntControllerUnitTest {

	@InjectMocks AzureResourceGroupMgntController mockAzureResourceGroupMgntController;
	@Mock AzureResourceGroupMgntService mockAzureResourceGroupMgntService;
	
	private MockMvc mockMvc;
	private static final ObjectMapper mapper = new ObjectMapper();
	
    *//*************************************** URL *******************************************//*
    final static String VIEW_URL = "/azureMgnt/resourceGroup";
    final static String RESOURCE_GROUP_LIST_INFO_URL = "/azureMgnt/resourceGroup/list/{accountId}";
    final static String RESOURCE_GROUP_DETAIL_INFO_URL = "/azureMgnt/resourceGroup/save/detail/{accountId}/{resourceGroupName}";
    final static String RESOURCE_LIST_INFO_URL = "/azureMgnt/resourceGroup/save/detail/resource/{accountId}/{resourceGroupName}";
    final static String RESOURCE_GROUP_SAVE_URL = "/azureMgnt/resourceGroup/save";
    final static String RESOURCE_GROUP_DELETE_URL = "/azureMgnt/resourceGroup/delete";
	 
	*//***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    ***************************************************//*
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockAzureResourceGroupMgntController).build();
    }
    
    *//***************************************************
     * @project : Azure 관리 대시보드
     * @description : 시큐리티 토큰 생성
     * @title : getLoggined
     * @return : Principal
     ***************************************************//*
     public Principal getLoggined() throws Exception {
         Authentication auth = new UsernamePasswordAuthenticationToken("admin", "admin");
         SecurityContext securityContext = SecurityContextHolder.getContext();
         securityContext.setAuthentication(auth);
         securityContext.getAuthentication().getPrincipal();
         return auth;
     }
     
     *//***************************************************
      * @project : Azure 관리 대시보드
      * @description : Azure Resource Group 관리 화면 이동 TEST
      * @title : testGoAzureResourceGroupMgnt
      * @return : void
      ***************************************************//*
      @Test
      public void testGoAzureResourceGroupMgnt() throws Exception{
          mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
          .andExpect(status().isOk())
          .andExpect(view().name("iaas/azure/resourceGroup/azureResourceGroupMgnt"));
      }
      
      *//***************************************************
       * @project : Azure 관리 대시보드
       * @description : Azure Resource Group 목록 조회 TEST
       * @title : testGetAzureResourceGroupInfoList
       * @return : void
       ***************************************************//*
      @Test
       public void testGetAzureResourceGroupInfoList() throws Exception{
           List<AzureResourceGroupMgntVO> list = getAzureResourceGroupResultListInfo();
           when(mockAzureResourceGroupMgntService.getAzureResourceGroupInfoList(any(), anyInt())).thenReturn(list);
           mockMvc.perform(get(RESOURCE_GROUP_LIST_INFO_URL, 1).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.records[0].accountId").value(1))
           .andExpect(jsonPath("$.records[0].recid").value(1))
           .andExpect(jsonPath("$.records[0].name").value("rg-test-name"))
           .andExpect(jsonPath("$.records[0].azureSubscriptionId").value("aaa"))
           .andExpect(jsonPath("$.records[0].location").value("centralus"))
           .andExpect(jsonPath("$.records[0].resourceGroupId").value("bbb"))
           .andExpect(jsonPath("$.records[0].status").value("Succeded"));
       }
       
      *//***************************************************
       * @project : Azure 관리 대시보드
       * @description : Azure ResourceGroup 상세 조회 TEST
       * @title : testGetAzureResourceGroupDetailInfo
       * @return : void
       ***************************************************//*
       @Test
       public void testGetAzureResourceGroupDetailInfo() throws Exception{
    	   HashMap<String, Object> vo = getAzureResourceGroupResultInfo();
           when(mockAzureResourceGroupMgntService.getAzureResourceGroupDetailInfo(any(), anyInt(), anyString())).thenReturn(vo);
           mockMvc.perform(get(RESOURCE_GROUP_DETAIL_INFO_URL, 1, "resourceGroupName").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.accountId").value(1))
           .andExpect(jsonPath("$.recid").value(1))
           .andExpect(jsonPath("$.name").value("name"))
           .andExpect(jsonPath("$.subscriptionName").value("subname"))
           .andExpect(jsonPath("$.depolyments").value("No"))
           .andExpect(jsonPath("$.resourceName").value("resourceName"))
           .andExpect(jsonPath("$.resourceType").value("resourceType"))
           .andExpect(jsonPath("$.resourceLocation").value("resourceLocation"));
           
       }      
      
       private HashMap<String, Object> getAzureResourceGroupResultInfo() {
    	   AzureResourceGroupMgntVO vo = new AzureResourceGroupMgntVO();
           vo.setAccountId(1);
           vo.setRecid(1);
           vo.setName("name");
           vo.setSubscriptionName("subname");
           vo.setDepolyments("No");
           vo.setResourceName("resourceName");
           HashMap<String, Object> map = new HashMap<String, Object>();
           //map.put(key, value);
           return map;
	}

	*//***************************************************
        * @project : Azure 관리 대시보드
        * @description : Azure Resource Group 목록 조회 결과 값 설정
        * @title : getAzureResourceGroupResultListInfo
        * @return : List<AzureResourceGroupMgntVO> 
        ***************************************************//*
		private List<AzureResourceGroupMgntVO> getAzureResourceGroupResultListInfo() {
			 List<AzureResourceGroupMgntVO> list = new ArrayList<AzureResourceGroupMgntVO>();
			 AzureResourceGroupMgntVO vo = new AzureResourceGroupMgntVO();
		        vo.setAccountId(1);
		        vo.setRecid(1);
		        vo.setName("rg-test-name");
		        vo.setAzureSubscriptionId("aaa");
		        vo.setLocation("centralus");
		        vo.setResourceGroupId("bbb");
		        vo.setStatus("Succeded");
		        list.add(vo);
		        return list;
		}
      
	
}
*/