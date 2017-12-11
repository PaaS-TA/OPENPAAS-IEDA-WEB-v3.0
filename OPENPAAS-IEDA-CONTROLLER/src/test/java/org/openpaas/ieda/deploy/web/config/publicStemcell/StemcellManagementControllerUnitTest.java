package org.openpaas.ieda.deploy.web.config.publicStemcell;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
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
import org.openpaas.ieda.controller.deploy.web.config.stemcell.StemcellManagementController;
import org.openpaas.ieda.deploy.web.config.stemcell.dao.StemcellManagementVO;
import org.openpaas.ieda.deploy.web.config.stemcell.dto.StemcellManagementDTO;
import org.openpaas.ieda.deploy.web.config.stemcell.service.StemcellManagementDownloadAsyncService;
import org.openpaas.ieda.deploy.web.config.stemcell.service.StemcellManagementService;
import org.openpaas.ieda.deploy.web.config.stemcell.service.StemcellManagementUploadService;
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
public class StemcellManagementControllerUnitTest extends BaseControllerUnitTest{
    
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    private Principal principal = null;
    
    @InjectMocks
    StemcellManagementController mockStemcellController;
    @Mock 
    StemcellManagementService mockStemcellService;
    @Mock 
    StemcellManagementUploadService mockStemcellUploadService;
    @Mock 
    StemcellManagementDownloadAsyncService mockStemcellDonwonloadService;
    
    /*************************************** URL *******************************************/
    final static String VIEW_URL = "/config/stemcell";
    final static String STEMCELL_LIST_URL = "/config/stemcell/list";
    final static String STEMCELL_REGIST_URL = "/config/stemcell/regist/info/{testFlag}";
    final static String STEMCELL_UPLOAD_URL = "/config/stemcell/regist/upload";
    final static String STEMCELL_DELETE_URL = "/config/stemcell/delete";
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockStemcellController).build();
        principal = getLoggined();
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 스템셀 관리 화면 이동 
    * @title : testGoStemcellManagement
    * @return : void
    ***************************************************/
    @Test
    public void testGoStemcellManagement() throws Exception{
        mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/deploy/config/stemcellManagement"));
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description :스템셀 목록 조회 
    * @title : testGetPublicStemcells
    * @return : void
    ***************************************************/
    @Test
    public void testGetPublicStemcells() throws Exception{
        List<StemcellManagementVO> stemcellList = getStemcellListInfo();
        when(mockStemcellService.getPublicStemcellList()).thenReturn(stemcellList);
        mockMvc.perform(get(STEMCELL_LIST_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].stemcellVersion").value("2820"))
        .andExpect(jsonPath("$.records[0].id").value(1))
        .andExpect(jsonPath("$.records[0].stemcellFileName").value("light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz"))
        .andExpect(jsonPath("$.records[0].stemcellName").value("testStemcellFile"))
        .andExpect(jsonPath("$.records[0].osVersion").value("TRUSTY"))
        .andExpect(jsonPath("$.records[0].stemcellUrl").value("https://bosh-jenkins-artifacts.s3.amazonaws.com/bosh-stemcell/aws/light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz"))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
    
    @Test
    public void testGetPublicStemcellsNotFoundError() throws Exception{
        List<StemcellManagementVO> stemcellList = getStemcellListInfo();
        when(mockStemcellService.getPublicStemcellList()).thenReturn(stemcellList);
        mockMvc.perform(get(STEMCELL_LIST_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : URL, VERSION 유형의 스템셀 정보 저장
    * @title : testSavePublicStemcellInfoByUrlAndVersionType
    * @return : void
    ***************************************************/
    @Test
    public void testSavePublicStemcellInfoByUrlAndVersionType() throws Exception{
        StemcellManagementDTO.Regist dto = setStemcellRegistInfo("url");
        StemcellManagementVO vo = getStemcellRegistInfo();
        when(mockStemcellService.saveStemcellInfoByURL(any(), anyString(), any())).thenReturn(vo);
        mockMvc.perform(post(STEMCELL_REGIST_URL,"Y").contentType(MediaType.APPLICATION_JSON).principal(principal).content(mapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.stemcellName").value("testStemcellFile"))
        .andExpect(jsonPath("$.stemcellUrl").value("https://bosh-jenkins-artifacts.s3.amazonaws.com/bosh-stemcell/aws/light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz"))
        .andExpect(jsonPath("$.stemcellVersion").value("2820"))
        .andExpect(jsonPath("$.osVersion").value("TRUSTY"))
        .andExpect(jsonPath("$.stemcellFileName").value("light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz"));
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : File 유형의 스템셀 정보 저장
    * @title : testUrlAndVersionTypeSystemStemcellRegist
    * @return : void
    ***************************************************/
    @Test
    public void testFileTypeSystemStemcellRegist() throws JsonProcessingException, Exception{
        StemcellManagementDTO.Regist dto = setStemcellRegistInfo("file");
        StemcellManagementVO vo = getStemcellRegistInfo();
        when(mockStemcellService.saveStemcellInfoByFilePath(any(), anyString(), any())).thenReturn(vo);
        mockMvc.perform(post(STEMCELL_REGIST_URL,"Y").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.stemcellName").value("testStemcellFile"))
        .andExpect(jsonPath("$.stemcellUrl").value("https://bosh-jenkins-artifacts.s3.amazonaws.com/bosh-stemcell/aws/light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz"))
        .andExpect(jsonPath("$.stemcellVersion").value("2820"))
        .andExpect(jsonPath("$.osVersion").value("TRUSTY"))
        .andExpect(jsonPath("$.stemcellFileName").value("light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz"));
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 스템셀 파일 업로드 요청
    * @title : testDoPublicStemcellUpload   
    * @return : void
    ***************************************************/
    @Test
    public void testDoPublicStemcellUpload() throws Exception{
        MultipartHttpServletRequest mockMultipartHttpReqeust = mock(MultipartHttpServletRequest.class);
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "orig", null, "bar".getBytes());
        doNothing().when(mockStemcellUploadService).uploadStemcellFile(mockMultipartHttpReqeust, principal);
        mockMvc.perform(MockMvcRequestBuilders.fileUpload(STEMCELL_UPLOAD_URL)
                .file(mockMultipartFile)
                .param("overlay", "true")
                .param("id", "1"))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 스템셀 파일 다운로드 요청
    * @title : testDoPublicStemcellDonwload
    * @return : void
    ***************************************************/
    @Test
    public void testDoPublicStemcellDonwload(){
        StemcellManagementDTO.Regist dto = setStemcellRegistInfo("url");
        doNothing().when(mockStemcellDonwonloadService).stemcellDownloadAsync(dto,principal);
        mockStemcellController.doPublicStemcellDonwload(dto, principal);
        verify(mockStemcellDonwonloadService, times(1)).stemcellDownloadAsync(dto,principal);
        verifyNoMoreInteractions(mockStemcellDonwonloadService);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 스템셀 삭제
    * @title : testPublicStemcellDelete
    * @return : void
    ***************************************************/
    @Test
    public void testPublicStemcellDelete() throws  Exception{
        StemcellManagementDTO.Delete dto = null;
        //when(mockStemcellService.deletePublicStemcell(any())).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.delete(STEMCELL_DELETE_URL).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
    }
    

    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 스템셀 목록 조회 리턴 값 설정
    * @title : getStemcellListInfo
    * @return : List<StemcellManagementVO>
    ***************************************************/
    private List<StemcellManagementVO> getStemcellListInfo() {
        List<StemcellManagementVO> list = new ArrayList<StemcellManagementVO>();
        StemcellManagementVO vo = new StemcellManagementVO();
        vo.setId(1);
        vo.setStemcellUrl("https://bosh-jenkins-artifacts.s3.amazonaws.com/bosh-stemcell/aws/light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
        vo.setStemcellName("testStemcellFile");
        vo.setStemcellFileName("light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
        vo.setStemcellVersion("2820");
        vo.setOsVersion("TRUSTY");
        vo.setCreateUserId("tester");
        vo.setUpdateUserId("tester");
        vo.getCreateDate();
        vo.getCreateUserId();
        vo.getUpdateUserId();
        vo.getUpdateDate();
        vo.getRecid();
        vo.getId();
        vo.getStemcellUrl();
        vo.getStemcellName();
        vo.getStemcellFileName();
        vo.getStemcellVersion();
        vo.getOsVersion();
        vo.getOs();
        vo.getDownloadLink();
        vo.getDownloadStatus();
        vo.getIsDose();
        vo.getIsExisted();
        vo.getIaas();
        vo.getSize();
        list.add(vo);
        return list;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 스템셀 상세 조회 리턴 값 설정
    * @title : getStemcellRegistInfo
    * @return : StemcellManagementVO
    ***************************************************/
    private StemcellManagementVO getStemcellRegistInfo(){
        StemcellManagementVO vo = new StemcellManagementVO();
        vo.setId(1);
        vo.setStemcellUrl("https://bosh-jenkins-artifacts.s3.amazonaws.com/bosh-stemcell/aws/light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
        vo.setStemcellName("testStemcellFile");
        vo.setStemcellFileName("light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
        vo.setStemcellVersion("2820");
        vo.setOsVersion("TRUSTY");
        vo.setCreateUserId("tester");
        vo.setUpdateUserId("tester");
        return vo;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 스템셀 정보 저장 값 설정
    * @title : setStemcellRegistInfo
    * @return : StemcellManagementDTO.Regist
    ***************************************************/
    private StemcellManagementDTO.Regist setStemcellRegistInfo(String fileType){
        StemcellManagementDTO.Regist dto = new StemcellManagementDTO.Regist();
        dto.setId(1);
        dto.setStemcellUrl("https://bosh-jenkins-artifacts.s3.amazonaws.com/bosh-stemcell/aws/light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
        dto.setStemcellName("testStemcellFile");
        dto.setStemcellFileName("light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
        dto.setStemcellVersion("2820");
        dto.setStemcellSize("123456789");
        dto.setOsName("UBUNTU");
        dto.setIaasType("AWS");
        dto.setOsVersion("TRUSTY");
        dto.setLight("true");
        if("url".equalsIgnoreCase(fileType)){
            dto.setFileType("url");
        }else{
            dto.setFileType("file");
        }
        dto.setOverlayCheck("true");
        dto.setCreateUserId("tester");
        dto.setUpdateUserId("tester");
        return dto;
    }
}
