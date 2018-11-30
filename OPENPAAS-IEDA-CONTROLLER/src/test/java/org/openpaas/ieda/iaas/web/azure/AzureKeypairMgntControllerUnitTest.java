package org.openpaas.ieda.iaas.web.azure;

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

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.controller.iaasMgnt.azureMgnt.web.keypair.AzureKeypairMgntController;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.keypair.dao.AzureKeypairMgntVO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.keypair.dto.AzureKeypairMgntDTO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.keypair.service.AzureKeypairMgntService;
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
public class AzureKeypairMgntControllerUnitTest {
    @InjectMocks AzureKeypairMgntController mockAzureKeypairMgntController;
    @Mock AzureKeypairMgntService mockAzureKeypairMgntService; 
    
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    
    /*************************************** URL *******************************************/
    final static String VIEW_URL = "/azureMgnt/storageAccessKey";
    final static String KEYPAIR_INFO_LIST_URL = "/azureMgnt/storageAccessKey/list/{accountId}";
    final static String KEYPAIR_SAVE_URL = "/azureMgnt/storageAccessKey/save";
   
    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockAzureKeypairMgntController).build();
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
     * @description : Azure Keypair 관리 화면 이동 TEST
     * @title : testGoAzureKeypairMgnt
     * @return : void
     ***************************************************/
    @Test
    public void testGoAzureKeypairMgnt() throws Exception{
       mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
      .andExpect(status().isOk())
      .andExpect(view().name("iaas/azure/keypair/azureKeypairMgnt"));
    }
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE Network목록 조회 TEST
     * @title : testGetAzureNetworkInfoList
     * @return : void
     ***************************************************/
    @Test
    public void testGetAzureKeypairnfoList() throws Exception{
        List<AzureKeypairMgntVO> list = getAzureKeypairInfoList();
        when(mockAzureKeypairMgntService.getAzureKeypairList(anyInt())).thenReturn(list);
        mockMvc.perform(get(KEYPAIR_INFO_LIST_URL, 1).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].accountId").value(1))
        .andExpect(jsonPath("$.records[0].keypairName").value("test-keypairName"));
    }
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE Keypair목록 조회 값 설정 
     * @title : getAzureKeypairInfoList
     * @return : List<AzureKeypairMgntVO>
     ***************************************************/
    private List<AzureKeypairMgntVO> getAzureKeypairInfoList(){
        List<AzureKeypairMgntVO> list = new ArrayList<AzureKeypairMgntVO>();
        AzureKeypairMgntVO vo = new AzureKeypairMgntVO();
        vo.setRecid(1);
        vo.setAccountId(1);
        vo.setKeypairName("test-keypairName");
        list.add(vo);
        return list;
    }

    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE Keypair 생성 TEST
     * @title : testSaveKeypairInfo
     * @return : void
     ***************************************************/
    @Test    
    public void testSaveKeypairInfo() throws Exception{
        AzureKeypairMgntDTO dto = setKeypairInfo();
         mockMvc.perform(post(KEYPAIR_SAVE_URL).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto))).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
    }
    
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE Keypair 생성 DTO 값 설정  
     * @title : setKeypairInfo
     * @return : AzureKeypairMgntDTO
     ***************************************************/
    private AzureKeypairMgntDTO setKeypairInfo(){
        AzureKeypairMgntDTO dto = new AzureKeypairMgntDTO();
        dto.setAccountId(1);
        dto.setKeypairName("test-keypairName");
        return null;
    }
}
