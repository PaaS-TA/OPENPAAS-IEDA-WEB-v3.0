package org.openpaas.ieda.deploy.web.config.systemRelease;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import org.openpaas.ieda.common.BaseControllerUnitTest;
import org.openpaas.ieda.controller.deploy.web.config.systemRelease.ReleaseManagementController;
import org.openpaas.ieda.deploy.web.config.systemRelease.dao.ReleaseManagementVO;
import org.openpaas.ieda.deploy.web.config.systemRelease.dto.ReleaseManagementDTO;
import org.openpaas.ieda.deploy.web.config.systemRelease.dto.ReleaseManagementDTO.Regist;
import org.openpaas.ieda.deploy.web.config.systemRelease.service.ReleaseManagementDownloadService;
import org.openpaas.ieda.deploy.web.config.systemRelease.service.ReleaseManagementService;
import org.openpaas.ieda.deploy.web.config.systemRelease.service.ReleaseManagementUploadService;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class ReleaseManagementControllerUnitTest extends BaseControllerUnitTest {
    
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    private Principal principal = null;
    
    @InjectMocks
    private ReleaseManagementController mockReleaseController;
    @Mock 
    private ReleaseManagementDownloadService mockReleaseDownloadService;
    @Mock 
    private ReleaseManagementUploadService mockReleaseUploadService;
    @Mock
    private ReleaseManagementService mockReleaseManagementService;
    
    /*************************************** URL *******************************************/
    final static String VIEW_URL = "/config/systemRelease"; //시스템 릴리즈 관리 화면 요청
    final static String SYSTEM_RELEASE_LIST_URL = "/config/systemRelease/list"; //시스템 릴리즈 목록 정보 조회
    final static String RELEASE_TYPE_LIST_URL = "/config/systemRelease/list/releaseType";
    final static String SYSTEM_RELEASE_INFO_REGIST_URL = "/config/systemRelease/regist";
    final static String SYSTEM_RELEASE_UPLOAD_URL = "/config/systemRelease/regist/upload";
    final static String SYSTEM_RELEASE_DELETE_URL = "/config/systemRelease/delete";
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockReleaseController).build();
        principal = getLoggined();
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 릴리즈 관리 화면 이동 테스트
     * @title : testGoReleaseManagement
     * @return : void
    *****************************************************************/
    @Test
    public void testGoReleaseManagement() throws Exception{
        mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/deploy/config/releaseManagement"));
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 시스템 릴리즈 목록 정보 조회 테스트
     * @title : testGetSystemReleaseList
     * @return : void
    *****************************************************************/
    @Test
    public void testGetSystemReleaseList() throws Exception{
        List<ReleaseManagementVO> list = setReleaseListInfo();
        when(mockReleaseManagementService.getSystemReleaseList()).thenReturn(list);
        mockMvc.perform(get(SYSTEM_RELEASE_LIST_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].createUserId").value("admin"))
        .andExpect(jsonPath("$.records[0].releaseName").value("bosh-test-cpi-release"))
        .andExpect(jsonPath("$.records[0].releaseType").value("cpi"))
        .andExpect(jsonPath("$.records[0].releaseSize").value("2222.2 MB"))
        .andExpect(jsonPath("$.records[0].releaseFileName").value("bosh-openstack-cpi-release-20.tgz"))
        .andExpect(jsonPath("$.records[0].downloadLink").value("https://bosh.io/d/github.com/cloudfoundry-incubator/bosh-openstack-cpi-release?v=20"))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 릴리즈 유형 별 목록 조회 테스트 
     * @title : testGetSystemReleaseTypeList
     * @return : void
    *****************************************************************/
    @Test
    public void testGetSystemReleaseTypeList() throws Exception{
        List<String> list = setReleaseTypeListInfo();
        when(mockReleaseManagementService.getSystemReleaseTypeList()).thenReturn(list);
        
        mockMvc.perform(get(RELEASE_TYPE_LIST_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[0]").value("bosh"))
        .andExpect(jsonPath("$.[1]").value("bosh_cpi"))
        .andExpect(jsonPath("$.[2]").value("cf"))
        .andExpect(jsonPath("$.[3]").value("diego"))
        .andExpect(jsonPath("$.[4]").value("etcd"))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 시스템 릴리즈 File 유형 정보 저장
     * @title : testSystemReleaseFileInfoRegist
     * @return : void
    *****************************************************************/
    @Test
    public void testSystemReleaseFileInfoRegist() throws Exception{
        ReleaseManagementDTO.Regist dto = setReleaseRegistFileTypeInputInfo();
        ReleaseManagementVO vo = getRegistSystemReleaseUploadInfo();
        String requestJson = mapper.writeValueAsString(dto);
        when(mockReleaseManagementService.saveSystemReleaseFileUploadInfo(any(), any())).thenReturn(vo);
        mockMvc.perform(post(SYSTEM_RELEASE_INFO_REGIST_URL).contentType(MediaType.APPLICATION_JSON)
        .content(requestJson))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(jsonPath("$.releaseName").value("bosh-test-cpi-release"))
        .andExpect(jsonPath("$.releaseType").value("cpi"))
        .andExpect(jsonPath("$.releaseSize").value("1112 MB"))
        .andExpect(jsonPath("$.releaseFileName").value("bosh-openstack-cpi-release-20.tgz"))
        .andExpect(jsonPath("$.downloadLink").value("downloadLink"))
        .andExpect(jsonPath("$.downloadStatus").value("downloading"));
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 시스템 릴리즈 URL 형식 정보 저장
    * @title : testSystemReleaseUrlInfoRegist
    * @return : void
    ***************************************************/
    @Test
    public void testSystemReleaseUrlInfoRegist() throws Exception{
        ReleaseManagementDTO.Regist dto = setReleaseRegistUrlTypeInputInfo();
        ReleaseManagementVO vo = getRegistSystemReleaseUploadInfo();
        when(mockReleaseManagementService.saveSystemReleaseUrlInfo(any(),any())).thenReturn(vo);
        mockMvc.perform(post(SYSTEM_RELEASE_INFO_REGIST_URL).contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(jsonPath("$.releaseName").value("bosh-test-cpi-release"))
        .andExpect(jsonPath("$.releaseType").value("cpi"))
        .andExpect(jsonPath("$.releaseSize").value("1112 MB"))
        .andExpect(jsonPath("$.releaseFileName").value("bosh-openstack-cpi-release-20.tgz"))
        .andExpect(jsonPath("$.downloadLink").value("downloadLink"))
        .andExpect(jsonPath("$.downloadStatus").value("downloading"));
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 시스템 릴리즈 다운르도 테스트
     * @title : testDoSystemReleaseDonwload
     * @return : void
    *****************************************************************/
    @Test
    public void testDoSystemReleaseDonwload() throws JsonProcessingException, Exception{
        ReleaseManagementDTO.Regist dto = setReleaseRegistUrlTypeInputInfo();
        doNothing().when(mockReleaseDownloadService).releaseDownloadAsync(dto,principal);
        mockReleaseController.doSystemReleaseDownload(dto, principal);
        verify(mockReleaseDownloadService, times(1)).releaseDownloadAsync(dto,principal);
        verifyNoMoreInteractions(mockReleaseDownloadService);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 시스템 릴리즈 업로드 테스트
     * @title : testDoSystemReleaseUpload
     * @return : void
    *****************************************************************/
    @Test
    public void testDoSystemReleaseUpload() throws Exception{
        MultipartHttpServletRequest mockMultipartHttpReqeust = mock(MultipartHttpServletRequest.class);
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "orig", null, "bar".getBytes());
        doNothing().when(mockReleaseUploadService).uploadReleaseFile(mockMultipartHttpReqeust, principal);
        mockMvc.perform(MockMvcRequestBuilders.fileUpload(SYSTEM_RELEASE_UPLOAD_URL)
                .file(mockMultipartFile)
                .param("overlay", "true")
                .param("id", "1"))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 시스템 릴리즈 삭제 테스트
     * @title : testSystemRelaseDelete
     * @return : void
    *****************************************************************/
    @Test
    public void testSystemRelaseDelete() throws Exception{
        ReleaseManagementDTO.Delete dto = setReleaseDelete();
        doNothing().when(mockReleaseManagementService).deleteSystemRelease(any());
        mockMvc.perform(MockMvcRequestBuilders.delete(SYSTEM_RELEASE_DELETE_URL).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
    }
   
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 릴리즈 목록 정보 값 설정
     * @title : setReleaseListInfo
     * @return : List<ReleaseManagementVO>
    *****************************************************************/
    public List<ReleaseManagementVO> setReleaseListInfo(){
        ReleaseManagementVO vo = new ReleaseManagementVO();
        List<ReleaseManagementVO> list = new ArrayList<ReleaseManagementVO>();
        vo.setId(1);
        vo.setReleaseType("cpi");
        vo.setReleaseFileName("bosh-openstack-cpi-release-20.tgz");
        vo.setReleaseSize("2222.2 MB");
        vo.setReleaseName("bosh-test-cpi-release");
        vo.setDownloadStatus("DOWNLOADING");
        vo.setDownloadLink("https://bosh.io/d/github.com/cloudfoundry-incubator/bosh-openstack-cpi-release?v=20");
        vo.setCreateUserId("admin");
        list.add(vo);
        return list;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 릴리즈 유형 정보 목록 설정
     * @title : setReleaseTypeListInfo
     * @return : List<String>
    *****************************************************************/
    public List<String> setReleaseTypeListInfo(){
        List<String> list = new ArrayList<String>();
        list.add(0, "bosh");
        list.add(1, "bosh_cpi");
        list.add(2, "cf");
        list.add(3, "diego");
        list.add(4, "etcd");
        return list;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 로컬 File을 통한 릴리즈 등록 정보 설정
     * @title : setReleaseRegistFileTypeInputInfo
     * @return : ReleaseManagementDTO.Regist
    *****************************************************************/
    public ReleaseManagementDTO.Regist setReleaseRegistFileTypeInputInfo() throws JsonProcessingException {
        ReleaseManagementDTO.Regist dto = new ReleaseManagementDTO.Regist();
        dto.setId(1);
        dto.setReleaseFileName("bosh-openstack-cpi-release-20.tgz");
        dto.setReleaseName("bosh-test-cpi-release");
        dto.setReleaseSize("123456789");
        dto.setFileType("file");
        dto.setOverlayCheck("true");
        dto.setReleaseType("cpi");
        dto.setCreateUserId("admin");
        dto.setUpdateUserId("admin");
        return dto;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 릴리즈 저장 후 정보 조회 값 설정
     * @title : getRegistSystemReleaseUploadInfo
     * @return : ReleaseManagementVO
    *****************************************************************/
    public ReleaseManagementVO getRegistSystemReleaseUploadInfo(){
        ReleaseManagementVO vo = new ReleaseManagementVO();
        vo.setId(1);
        vo.setReleaseFileName("bosh-openstack-cpi-release-20.tgz");
        vo.setDownloadLink("downloadLink");
        vo.setDownloadStatus("downloading");
        vo.setReleaseName("bosh-test-cpi-release");
        vo.setReleaseSize("1112 MB");
        vo.setReleaseType("cpi");
        vo.setCreateUserId("admin");
        vo.setUpdateUserId("admin");
        return vo;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 원격지 url을 통한 시스템 릴리즈 정보 설정
     * @title : setReleaseRegistUrlTypeInputInfo
     * @return : Regist
    *****************************************************************/
    private Regist setReleaseRegistUrlTypeInputInfo() {
        ReleaseManagementDTO.Regist dto = new ReleaseManagementDTO.Regist();
        dto.setId(1);
        dto.setReleaseFileName("bosh-openstack-cpi-release-20.tgz");
        dto.setReleaseName("bosh-test-cpi-release");
        dto.setDownloadLink("download.com");
        dto.setDownloadStatus("downloading");
        dto.setFileType("url");
        dto.setReleaseSize("123456789");
        dto.setOverlayCheck("true");
        dto.setReleaseType("cpi");
        dto.setCreateUserId("admin");
        dto.setUpdateUserId("admin");
        return dto;
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 시스템 릴리즈 삭제 입력 설정
     * @title : setReleaseDelete
     * @return : ReleaseManagementDTO.Delete
    *****************************************************************/
    public ReleaseManagementDTO.Delete  setReleaseDelete() throws Exception {
        ReleaseManagementDTO.Delete dto = new ReleaseManagementDTO.Delete();
        dto.setId("1");
        dto.setReleaseFileName("bosh-openstack-cpi-release-20.tgz");
        return dto;
    }
}
