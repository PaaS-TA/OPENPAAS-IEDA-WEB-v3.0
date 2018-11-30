package org.openpaas.ieda.iaas.web.azure;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doNothing;
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
import org.openpaas.ieda.controller.iaasMgnt.azureMgnt.web.storageAccount.AzureStorageAccountMgntController;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.storageAccount.dao.AzureStorageAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.storageAccount.dto.AzureStorageAccountMgntDTO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.storageAccount.service.AzureStorageAccountMgntService;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
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
public class AzureStorageAccountMgntControllerUnitTest {

    
    @InjectMocks AzureStorageAccountMgntController mockAzureStorageAccountMgntController;
    @Mock AzureStorageAccountMgntService mockAzureStorageAccountMgntService;
    @Mock CommonIaasService commonIaasService;
    Principal principal = null;
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    
    /*************************************** URL *******************************************/
    final static String VIEW_URL = "/azureMgnt/storageAccount";
    final static String STORAGE_ACCOUNT_INFO_LIST_URL = "/azureMgnt/storageAccount/list/{accountId}";
    final static String STORAGE_ACCOUNT_BLOB_INFO_LIST_URL = "/azureMgnt/storageAccount/list/blobs/{accountId}/{storageAccountName}";
    final static String STORAGE_ACCOUNT_TABLE_INFO_LIST_URL = "/azureMgnt/storageAccount/list/tables/{accountId}/{storageAccountName}";
    final static String STORAGE_ACCOUNT_SAVE_URL = "/azureMgnt/storageAccount/save";
    final static String STORAGE_ACCOUNT_DELETE_URL = "/azureMgnt/storageAccount/delete";
    final static String STORAGE_ACCOUNT_BLOB_SAVE_URL = "/azureMgnt/blob/save";
    final static String STORAGE_ACCOUNT_BLOB_DELETE_URL = "/azureMgnt/blob/delete";
    final static String STORAGE_ACCOUNT_TABLE_SAVE_URL = "/azureMgnt/table/save";
    final static String STORAGE_ACCOUNT_TABLE_DELETE_URL = "/azureMgnt/table/delete";
       
    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockAzureStorageAccountMgntController).build();
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
     * @description : Azure Storage Account 관리 화면 이동 TEST
     * @title : testGoAzureStorageAccountMgnt
     * @return : void
     ***************************************************/
    @Test
    public void testGoAzureStorageAccountMgnt() throws Exception{
        mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
       .andExpect(status().isOk())
       .andExpect(view().name("iaas/azure/storageAccount/azureStorageAccountMgnt"));
    }
    
    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : Azure Storage Account  목록 조회 TEST
     * @title : testGetAzureStorageAccountInfoList
     * @return : void
     ***************************************************/
    @Test
    public void testGetAzureStorageAccountInfoList() throws Exception{
        List<AzureStorageAccountMgntVO> list = getAzureStorageAccountResultInfoList();
        when(mockAzureStorageAccountMgntService.getAzureStorageAccountInfoList(any(), anyInt())).thenReturn(list);
        mockMvc.perform(get(STORAGE_ACCOUNT_INFO_LIST_URL, 1 ).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].accountId").value(1))
        .andExpect(jsonPath("$.records[0].recid").value(1))
        .andExpect(jsonPath("$.records[0].storageAccountName").value("test-storageAccountName"))
        .andExpect(jsonPath("$.records[0].subscriptionName").value("test-subscriptionName"))
        .andExpect(jsonPath("$.records[0].azureSubscriptionId").value("test-azureSubscriptionId"))
        .andExpect(jsonPath("$.records[0].accountType").value("test-storageAccount"))
        .andExpect(jsonPath("$.records[0].location").value("us-west-2"))
        .andExpect(jsonPath("$.records[0].resourceGroupName").value("test-resourceGroupName"));
    
    }
    
    /***************************************************
    * @project : Azure 관리 대시보드
    * @description : Azure Storage Account 목록 조회 결과 값 설정
    * @title : getAzureStorageAccountResultInfoList
    * @return : List<AzureStorageAccountMgntVO> 
    ***************************************************/
    private  List<AzureStorageAccountMgntVO> getAzureStorageAccountResultInfoList(){
        List<AzureStorageAccountMgntVO> list = new ArrayList<AzureStorageAccountMgntVO>();
        AzureStorageAccountMgntVO vo = new AzureStorageAccountMgntVO();
        vo.setAccountId(1);
        vo.setRecid(1);
        vo.setStorageAccountName("test-storageAccountName");
        vo.setSubscriptionName("test-subscriptionName");
        vo.setAzureSubscriptionId("test-azureSubscriptionId");
        vo.setAccountType("test-storageAccount");
        vo.setLocation("us-west-2");
        vo.setResourceGroupName("test-resourceGroupName");
        list.add(vo);
        return list;
    }
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE Storage Account Blobs 목록 조회 TEST
     * @title : testGetAzureBlobInfoList
     * @return : void
     ***************************************************/
    @Test
    public void testGetAzureBlobInfoList() throws Exception{
        List<AzureStorageAccountMgntVO> list = getAzureBlobResultInfoList();
        when(mockAzureStorageAccountMgntService.getAzureBlobInfoList(any(), anyInt(), any())).thenReturn(list);
        mockMvc.perform(get(STORAGE_ACCOUNT_BLOB_INFO_LIST_URL, 1, "storageAccountName").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].accountId").value(1))
        .andExpect(jsonPath("$.records[0].recid").value(1))
        .andExpect(jsonPath("$.records[0].storageAccountName").value("test-storageAccountName"))
        .andExpect(jsonPath("$.records[0].blobName").value("test-blobName"))
        .andExpect(jsonPath("$.records[0].publicAccessLevel").value("test-private"))
        .andExpect(jsonPath("$.records[0].leaseState").value("test-avaliable"))
        .andExpect(jsonPath("$.records[0].etag").value("test-etag"));
    }
    
    /***************************************************
    * @project : Azure 관리 대시보드
    * @description : Azure Storage Account Blobs 목록 조회 결과 값 설정
    * @title : getAzureBlobResultInfoList
    * @return : List<AzureStorageAccountMgntVO> 
    ***************************************************/
    private List<AzureStorageAccountMgntVO> getAzureBlobResultInfoList(){
    	 List<AzureStorageAccountMgntVO> list = new ArrayList<AzureStorageAccountMgntVO>();
         AzureStorageAccountMgntVO vo = new AzureStorageAccountMgntVO();
         vo.setAccountId(1);
         vo.setRecid(1);
         vo.setStorageAccountName("test-storageAccountName");
         vo.setBlobName("test-blobName");
         vo.setPublicAccessLevel("test-private");
         vo.setLeaseState("test-avaliable");
         vo.setLocation("us-west-2");
         vo.setEtag("test-etag");
         list.add(vo);
    	 return list;
    }
    
    /***************************************************
     * @project : AZURE 관리 대시보드
     * @description : AZURE Storage Account Table 목록 조회 TEST
     * @title : testGetAzureTableInfoList
     * @return : void
     ***************************************************/
    @Test
    public void testGetAzureTableInfoList() throws Exception{
        List<AzureStorageAccountMgntVO> list = getAzureTableResultInfoList();
        when(mockAzureStorageAccountMgntService.getAzureTableInfoList(any(), anyInt(), any())).thenReturn(list);
        mockMvc.perform(get(STORAGE_ACCOUNT_TABLE_INFO_LIST_URL, 1, "storageAccountName").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].accountId").value(1))
        .andExpect(jsonPath("$.records[0].recid").value(1))
        .andExpect(jsonPath("$.records[0].storageAccountName").value("test-storageAccountName"))
        .andExpect(jsonPath("$.records[0].tableName").value("test-tableName"))
        .andExpect(jsonPath("$.records[0].tableUrl").value("test-url"));
    }
    
    /***************************************************
    * @project : Azure 관리 대시보드
    * @description : Azure Storage Account Tables 목록 조회 결과 값 설정
    * @title : getAzureTableResultInfoList
    * @return : List<AzureStorageAccountMgntVO> 
    ***************************************************/
    private List<AzureStorageAccountMgntVO> getAzureTableResultInfoList(){
    	 List<AzureStorageAccountMgntVO> list = new ArrayList<AzureStorageAccountMgntVO>();
         AzureStorageAccountMgntVO vo = new AzureStorageAccountMgntVO();
         vo.setAccountId(1);
         vo.setRecid(1);
         vo.setStorageAccountName("test-storageAccountName");
         vo.setTableName("test-tableName");
         vo.setTableUrl("test-url");
         list.add(vo);
         return list;
    }   
    
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE Storage Account 생성 TEST
     * @title : testSaveStorageAccountInfo
     * @return : void
     ***************************************************/
    @Test    
    public void testSaveStorageAccountInfo() throws Exception{
        AzureStorageAccountMgntDTO dto = setStorageAccountInfo();
        mockMvc.perform(post(STORAGE_ACCOUNT_SAVE_URL).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto))).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
    }
    
    /***************************************************
    * @project : Azure 관리 대시보드
    * @description : Azure Storage Account 생성 값 설정
    * @title : setStorageAccountInfo
    * @return : AzureStorageAccountMgntDTO
    ***************************************************/
    private AzureStorageAccountMgntDTO setStorageAccountInfo(){
    	AzureStorageAccountMgntDTO dto = new AzureStorageAccountMgntDTO();
        dto.setAccountId(1);
        dto.setStorageAccountName("test-storageAccountName");
        dto.setLocation("us-west-2");
        dto.setResourceGroupName("test-resourceGroupName");
        return null;
    }
    
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE Storage Account 삭제 TEST
     * @title : testDeleteStorageAccountInfo
     * @return : void
     ***************************************************/
    @Test
    public void testDeleteStorageAccountInfo() throws Exception{
    	AzureStorageAccountMgntDTO dto = setStorageAccountInfo();
        doNothing().when(mockAzureStorageAccountMgntService).deleteStorageAccountInfo(any(), any());
        mockMvc.perform(delete(STORAGE_ACCOUNT_DELETE_URL).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto))).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNoContent());
    }
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE Storage Account Blob 생성 TEST
     * @title : testSaveAzureBlobInfo
     * @return : void
     ***************************************************/
    @Test    
    public void testSaveAzureBlobInfo() throws Exception{
        AzureStorageAccountMgntDTO dto = setBlobInfo();
        mockMvc.perform(post(STORAGE_ACCOUNT_BLOB_SAVE_URL).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto))).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
    } 
    
    /***************************************************
    * @project : Azure 관리 대시보드
    * @description : Azure Storage Account Blob 생성 값 설정
    * @title : setBlobInfo
    * @return : AzureStorageAccountMgntDTO
    ***************************************************/
    private AzureStorageAccountMgntDTO setBlobInfo(){
    	AzureStorageAccountMgntDTO dto = new AzureStorageAccountMgntDTO();
        dto.setAccountId(1);
        dto.setStorageAccountName("test-storageAccountName");
        dto.setBlobName("blobName");
        dto.setPublicAccessType("test-publicAccessType-private");
        return null;
    }
    
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE Storage Account Blob 삭제 TEST
     * @title : testDeleteAzureBlobInfo
     * @return : void
     ***************************************************/
    @Test
    public void testDeleteAzureBlobInfo() throws Exception{
    	AzureStorageAccountMgntDTO dto = setBlobInfo();
        doNothing().when(mockAzureStorageAccountMgntService).deleteAzureBlob(any(), any());
        mockMvc.perform(delete(STORAGE_ACCOUNT_BLOB_DELETE_URL).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto))).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNoContent());
    }
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE Storage Account Table 생성 TEST
     * @title : testSaveAzureTableInfo
     * @return : void
     ***************************************************/
    @Test    
    public void testSaveAzureTableInfo() throws Exception{
        AzureStorageAccountMgntDTO dto = setTableInfo();
        mockMvc.perform(post(STORAGE_ACCOUNT_TABLE_SAVE_URL).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto))).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
    }
    
    /***************************************************
    * @project : Azure 관리 대시보드
    * @description : Azure Storage Account Table 생성 값 설정
    * @title : setTableInfo
    * @return : AzureStorageAccountMgntDTO
    ***************************************************/
    private AzureStorageAccountMgntDTO setTableInfo(){
    	AzureStorageAccountMgntDTO dto = new AzureStorageAccountMgntDTO();
        dto.setAccountId(1);
        dto.setStorageAccountName("test-storageAccountName");
        dto.setTableName("test-tableName");
        return null;
    }
    
    
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE Storage Account Table 삭제 TEST
     * @title : testDeleteAzureTableInfo
     * @return : void
     ***************************************************/
    @Test
    public void testDeleteAzureTableInfo() throws Exception{
    	AzureStorageAccountMgntDTO dto = setTableInfo();
        doNothing().when(mockAzureStorageAccountMgntService).deleteAzureTable(any(), any());
        mockMvc.perform(delete(STORAGE_ACCOUNT_TABLE_DELETE_URL).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto))).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNoContent());
    }
    
    
    /*public static void main(String[] args) {
        // TODO Auto-generated method stub

    }*/

}
