package org.openpaas.ieda.iaas.web.account;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.Application;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.common.BaseControllerUnitTest;
import org.openpaas.ieda.controller.iaasMgnt.dashboard.web.account.IaasAccountMgntController;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.account.dto.IaasAccountMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.account.service.IaasAccountMgntService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class IaasAccountMgntControllerUnitTest extends BaseControllerUnitTest {
    
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    private Principal principal = null;
    
    @InjectMocks
     IaasAccountMgntController mockIaasAccountMgntController;
    @Mock
     IaasAccountMgntService mockIaasAccountMgntService;
    
    /*************************************** URL *******************************************/
    final static String VIEW_URL = "/iaasMgnt/account"; //Iaas Account 관리 화면 요청
    final static String VIEW_AWS_URL = "/iaasMgnt/account/aws"; //AWS계정 관리 화면 요청
    final static String VIEW_OPENSTACK_URL = "/iaasMgnt/account/openstack"; //Openstack계정 관리 화면 요청
    final static String VIEW_GOOGLE_URL = "/iaasMgnt/account/google"; //GOOGLE 계정 관리 화면 요청
    final static String VIEW_VSPHERE_URL = "/iaasMgnt/account/vSphere"; //vSphere계정 관리 화면 요청
    final static String VIEW_AZURE_URL = "/iaasMgnt/account/azure"; //Azure 계정 관리 화면 요청
    final static String IAAS_ACCOUNT_LIST_URL = "/iaasMgnt/account/all/list"; //전체 Iaas Account 목록 정보 조회
    final static String IAAS_ACCOUNT_CNT_URL = "/iaasMgnt/account/all/cnt"; // 인프라 계정 갯수 정보 조회 
    final static String IAAS_TYPE_LIST_URL = "/iaasMgnt/account/{iaasType}/list"; //인프라 계정별 목록 정보 조회
    final static String IAAS_TYPE_DETAIL_URL = "/iaasMgnt/account/{iaasType}/save/detail/{id}"; //인프라 별 계정 상세 정보 조회
    final static String IAAS_ACCOUNT_SAVE_URL = "/iaasMgnt/account/{iaasType}/save"; //인프라 별 계정 상세 정보 저장/수정
    final static String IAAS_ACCOUNT_DELETE_URL = "/iaasMgnt/account/{iaasType}/delete"; //인프라 별 계정 상세 정보 조회
    final static String JSON_KEY_PATH_FILE_LIST_URL = "/iaasMgnt/account/key/list";//GOOGLE Json 키 파일 목록 조회
    final static String UPLOAD_JSON_KEY_URL = "/iaasMgnt/account/key/upload";//GOOGLE Json 키 파일 업로드
    
    
    /***************************************************
     * @project : 인프라 계정 관리 대시보드
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockIaasAccountMgntController).build();
        principal = getLoggined();
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Iaas Account 관리 화면 이동 테스트
     * @title : testGoIaasAccountMgnt
     * @return : void
    ***************************************************/
    @Test
    public void testGoIaasAccountMgnt() throws Exception{
        mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/iaas/account/iaasAccount"));
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : AWS계정 관리 화면 이동 테스트
     * @title : testAwsAccountMgnt
     * @return : void
    ***************************************************/
    @Test
    public void testGoAwsAccountMgnt() throws Exception{
        mockMvc.perform(get(VIEW_AWS_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/iaas/account/awsAccount"));
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Openstack계정 관리 화면 이동 테스트
     * @title : testGoOpenstackAccountMgnt
     * @return : void
    ***************************************************/
    @Test
    public void testGoOpenstackAccountMgnt() throws Exception{
        mockMvc.perform(get(VIEW_OPENSTACK_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/iaas/account/openstackAccount"));
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : MS Azure계정 관리 화면 이동 테스트
     * @title : testGoMsAzureAccountMgnt
     * @return : void
    ***************************************************/
    @Test
    public void testGoGoogleAccountMgnt() throws Exception{
        mockMvc.perform(get(VIEW_GOOGLE_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/iaas/account/googleAccount"));
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : vShpere계정 관리 화면 이동 테스트
     * @title : testGoVshpereAccountMgnt
     * @return : void
    ***************************************************/
    @Test
    public void testGoVshpereAccountMgnt() throws Exception{
        mockMvc.perform(get(VIEW_VSPHERE_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/iaas/account/vSphereAccount"));
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure계정 관리 화면 이동 테스트
     * @title : testGoAzureAccountMgnt
     * @return : void
    ***************************************************/
    
    public void testGoAzureAccountMgnt() throws Exception{
        mockMvc.perform(get(VIEW_AZURE_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/iaas/account/azureAccount"));
    }
    
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 전체 Iaas Account 목록 정보 조회 테스트
     * @title : testGetAllIaasAccountInfoList
     * @return : void
    ***************************************************/
    @Test
    public void testGetAllIaasAccountInfoList() throws Exception{
        List<IaasAccountMgntVO> list = setIaasAccountInfoList();
        
        when(mockIaasAccountMgntService.getAllIaasAccountInfoList(any())).thenReturn(list);
        mockMvc.perform(get(IAAS_ACCOUNT_LIST_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].createUserId").value("admin"))
        .andExpect(jsonPath("$.records[0].iaasType").value("AWS"))
        .andExpect(jsonPath("$.records[0].status").value("Active"))
        .andExpect(jsonPath("$.records[0].accountName").value("aws-aaa"))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
    
     /***************************************************
      * @project : 인프라 관리 대시보드
      * @description : 인프라 계정 갯수 정보 조회 테스트
      * @title : testGetAllIaasAccountCountInfo
      * @return : void
     ***************************************************/
     @Test
     public void testGetAllIaasAccountCountInfo() throws Exception{
         HashMap<String, Integer> map = setIaasAccountCount();
         
         when(mockIaasAccountMgntService.getIaasAccountCount(any())).thenReturn(map);
         mockMvc.perform(get(IAAS_ACCOUNT_CNT_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.aws_cnt").value(map.get("aws_cnt")))
         .andExpect(jsonPath("$.openstack_cnt").value(map.get("openstack_cnt")))
         .andExpect(jsonPath("$.azure_cnt").value(map.get("azure_cnt")))
         .andExpect(jsonPath("$.vsphere_cnt").value(map.get("vsphere_cnt")))
         .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
     }
     
     /***************************************************
      * @param iaasType 
      * @project : 인프라 관리 대시보드
      * @description : 인프라 유형 별 목록 조회 테스트 
      * @title : testGetIaasAccountMgntInfoList
      * @return : void
      ***************************************************/
      @Test
      public void testGetIaasAccountMgntInfoList() throws Exception{
          List<IaasAccountMgntVO> list = setIaasAccountMgntInfoList();
          when(mockIaasAccountMgntService.getIaasAccountInfoList(any(), any())).thenReturn(list);
          
          mockMvc.perform(get(IAAS_TYPE_LIST_URL,"aws").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.records[0].accountName").value("account_name"))
          .andExpect(jsonPath("$.records[0].commonAccessUser").value("common_access_user"))
          .andExpect(jsonPath("$.records[0].commonAccessSecret").value("common_access_secret"))
          .andExpect(jsonPath("$.records[0].iaasType").value("iaas_type"))
          .andExpect(jsonPath("$.records[0].id").value(1))
          .andExpect(jsonPath("$.records[0].status").value("status"))
          .andExpect(jsonPath("$.records[0].createUserId").value(principal.getName()))
          .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
      }
      /***************************************************
       * @project : 인프라 관리 대시보드
       * @description : 계정 별 상세 정보 조회 테스트
       * @title : testGetIaasAccountInfo
       * @return : Void
      ***************************************************/
      @Test
      public void testGetIaasAccountInfo() throws Exception{
          IaasAccountMgntVO vo = setIaasAccountMgntInfo();
          when(mockIaasAccountMgntService.getIaasAccountInfo(any(),anyInt(), any())).thenReturn(vo);
          
          mockMvc.perform(get(IAAS_TYPE_DETAIL_URL,"aws",1).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.accountName").value("account_name"))
          .andExpect(jsonPath("$.commonAccessUser").value("common_access_user"))
          .andExpect(jsonPath("$.commonAccessSecret").value("common_access_secret"))
          .andExpect(jsonPath("$.iaasType").value("iaas_type"))
          .andExpect(jsonPath("$.id").value(1))
          .andExpect(jsonPath("$.status").value("status"))
          .andExpect(jsonPath("$.createUserId").value(principal.getName()))
          .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
          
      }
       
      /***************************************************
       * @project : 인프라 관리 대시보드
       * @description : 인프라 계정 정보 등록 테스트 
       * @title : testSaveIaasAccountInfo
       * @return : void
      ***************************************************/
      @Test
      public void testSaveIaasAccountInfoRegistCase() throws Exception{
          IaasAccountMgntDTO dto = setSaveIaasAccountInfo();
          doNothing().when(mockIaasAccountMgntService).saveIaasAccountInfo(anyString(), any(), any(), any());
          
          mockMvc.perform(MockMvcRequestBuilders.put(IAAS_ACCOUNT_SAVE_URL, "aws").contentType(MediaType.APPLICATION_JSON)
                  .content(mapper.writeValueAsString(dto)))
                  .andDo(MockMvcResultHandlers.print())
                  .andExpect(MockMvcResultMatchers.status().isOk())
                  .andReturn();
      }
      
      /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : google json 키 파일 목록 조회
     * @title : testGetJsonKeyPathFileList
     * @return : void
    *****************************************************************/
    @Test
      public void testGetJsonKeyPathFileList() throws Exception{
          List<String> keyPathFileList = setKeyPathFileList();
          when(mockIaasAccountMgntService.getJsonKeyFileList()).thenReturn(keyPathFileList);
          
          mockMvc.perform(get(JSON_KEY_PATH_FILE_LIST_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.[0]").value(keyPathFileList.get(0)))
          .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
      }
        
      /***************************************************
       * @project : 인프라 관리 대시보드
       * @description : 인프라 계정 수정 테스트 
       * @title : testSaveIaasAccountInfoUpdateCase
       * @return : void
      ***************************************************/
      @Test
      public void testSaveIaasAccountInfoUpdateCase() throws Exception{
          IaasAccountMgntDTO dto = setSaveIaasAccountInfo();
          dto.setId("1");
          doNothing().when(mockIaasAccountMgntService).saveIaasAccountInfo(anyString(), any(), any(), any());
          mockMvc.perform(MockMvcRequestBuilders.put(IAAS_ACCOUNT_SAVE_URL, "aws").contentType(MediaType.APPLICATION_JSON)
                  .content(mapper.writeValueAsString(dto)))
                  .andDo(MockMvcResultHandlers.print())
                  .andExpect(MockMvcResultMatchers.status().isOk())
                  .andReturn();
      }
      
      /****************************************************************
       * @project : Paas 플랫폼 설치 자동화
       * @description : Google Json key 파일 업로드
       * @title : testUploadJsonKeyPathFile
       * @return : void
      *****************************************************************/
      @Test
      public void testUploadJsonKeyPathFile() throws Exception{
          MultipartHttpServletRequest mockMultipartHttpReqeust = mock(MultipartHttpServletRequest.class);
          MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "orig", null, "bar".getBytes());
          doNothing().when(mockIaasAccountMgntService).uploadJsonKeyFile(mockMultipartHttpReqeust);
          mockMvc.perform(MockMvcRequestBuilders.fileUpload(UPLOAD_JSON_KEY_URL)
                .file(mockMultipartFile))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
      }
      
      /***************************************************
       * @project : 인프라 관리 대시보드
       * @description : 인프라 정보 삭제 테스트
       * @title : testDeleteI트aasAccountInfo
       * @return : Void
       * @throws Exception 
      ***************************************************/
      @Test
      public void testDeleteIaasAccountInfo() throws Exception{
          IaasAccountMgntDTO dto = setIaasAccountDelete();
          doNothing().when(mockIaasAccountMgntService).deleteIaasAccountInfo(any(), any());
          mockMvc.perform(MockMvcRequestBuilders.delete(IAAS_ACCOUNT_DELETE_URL, "aws").contentType(MediaType.APPLICATION_JSON)
                  .principal(principal)
                  .content(mapper.writeValueAsString(dto)))
                  .andDo(MockMvcResultHandlers.print())
                  .andExpect(MockMvcResultMatchers.status().isNoContent())
                  .andReturn();
      }
      
      /****************************************************************
       * @project : Paas 플랫폼 설치 자동화
       * @description : VO 객체 커버리지
       * @title : tearDown
       * @return : void
      *****************************************************************/
      @After
      public void tearDown() throws Exception {
          //coverage test
          IaasAccountMgntVO account = new IaasAccountMgntVO();
          account.getDefaultYn();
          account.setDefaultYn("N");
            
          account.getCommonAccessEndpoint();
          account.setCommonAccessEndpoint("");
          
          account.getOpenstackKeystoneVersion();
          account.setOpenstackKeystoneVersion("v2");
          
          account.getCommonTenant();
          account.setCommonTenant("bosh");
          
          account.getOpenstackDomain();
          account.setOpenstackDomain("");
                    
          account.getUpdateUserId();
          account.setUpdateUserId(principal.getName());
          
          account.getCreateDate();
          account.setCreateDate(new Date());
          account.getCreateDate();
          
          account.getUpdateDate();
          account.setUpdateDate(new Date());
          account.getUpdateDate();
          
          account.setTestFlag("Y");
          account.setCommonProject("bosh");
          account.setGoogleJsonKeyPath("google-key1.json");
          account.setRecid(1);
      }

      /***************************************************
       * @project : 인프라 관리 대시보드
       * @description : Iaas Account 목록 정보 값 설정
       * @title : setIaasAccountInfoList
       * @return : List<IaasAccountMgntVO>
      ***************************************************/
      public List<IaasAccountMgntVO> setIaasAccountInfoList(){
          IaasAccountMgntVO vo = new IaasAccountMgntVO();
          List<IaasAccountMgntVO> list = new ArrayList<IaasAccountMgntVO>();
          //vo.setId(1);
          vo.setCreateUserId("admin");
          vo.setIaasType("AWS");
          vo.setStatus("Active");
          vo.setAccountName("aws-aaa");
          list.add(vo);
          return list;
      }
         
      /***************************************************
       * @project : 인프라 관리 대시보드
       * @description : 인프라 계정 정보 갯수 설정
       * @title : setIaasAccountCount
       * @return : HashMap<String,Integer>
      ***************************************************/
      public HashMap<String, Integer> setIaasAccountCount(){
          HashMap<String, Integer> map = new  HashMap<String, Integer>();
          map.put("aws_cnt",1);
          map.put("openstack_cnt", 3);
          map.put("azure_cnt", 5);
          map.put("vsphere_cnt", 4);
          return map;
      }

      /***************************************************
       * @param : iaasType 
       * @project : 인프라 관리 대시보드
       * @description : 인프라 유형 별 정보 목록 설정
       * @title : setIaasAccountMgntInfoList
       * @return : List<IaaSAccountMgntVO>
      ***************************************************/
      public  List<IaasAccountMgntVO> setIaasAccountMgntInfoList(){
          IaasAccountMgntVO vo = new IaasAccountMgntVO();
          List<IaasAccountMgntVO> list = new ArrayList<IaasAccountMgntVO>();
          vo.setAccountName("account_name");
          vo.setCommonAccessUser("common_access_user");
          vo.setCommonAccessSecret("common_access_secret");
          vo.setIaasType("iaas_type");
          vo.setId(1);
          vo.setStatus("status");
          vo.setCreateUserId(principal.getName());
          list.add(vo);
          return list;
      }
          
      /***************************************************
       * @param : iaasType , id
       * @project : 인프라 관리 대시보드
       * @description : 계정 별 상세 정보 설정
       * @title : setIaasAccountMgntInfo
       * @return : IaaSAccountMgntVO
      ***************************************************/
      public  IaasAccountMgntVO setIaasAccountMgntInfo(){
          IaasAccountMgntVO vo = new IaasAccountMgntVO();
          vo.setAccountName("account_name");
          vo.setCommonAccessUser("common_access_user");
          vo.setCommonAccessSecret("common_access_secret");
          vo.setIaasType("iaas_type");
          vo.setId(1);
          vo.setStatus("status");
          vo.setCreateUserId(principal.getName());
          return vo;
      }
           
      /***************************************************
       * @project : 인프라 관리 대시보드
       * @description : 인프라 계정 저장 정보 설정
       * @title : setSaveIaasAccountInfo
       * @return : IaasAccountMgntDTO
      ***************************************************/
      public IaasAccountMgntDTO setSaveIaasAccountInfo(){
           IaasAccountMgntDTO dto = new IaasAccountMgntDTO();
           dto.setIaasType("aws");
           dto.setAccountName("aws-test");
           dto.setCommonAccessUser("test");
           dto.setCommonAccessSecret("testPw");
           dto.getIaasType();
           dto.getCommonAccessEndpoint();
           dto.getCommonAccessSecret();
           return dto;
      }
      
      /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : google Json Key 파일 목록 설정
     * @title : setKeyPathFileList
     * @return : List<String>
    *****************************************************************/
    public List<String> setKeyPathFileList(){
          List<String> keyPathList = new ArrayList<String>();
          keyPathList.add("google-key1.json");
          keyPathList.add("google-key2.json");
          
          return keyPathList;
      }

        /***************************************************
         * @project : 인프라 관리 대시보드
         * @description : 인프라 계정 삭제 입력 설정
         * @title : setIaasAccountDelete
         * @return : String
        ***************************************************/
        public IaasAccountMgntDTO setIaasAccountDelete() {
            IaasAccountMgntDTO dto = new IaasAccountMgntDTO();
            dto.setId("1");
            dto.setIaasType("aws");
            dto.setAccountName("aws-test");
            dto.setCommonAccessUser("test");
            return dto;
        }
        
}
