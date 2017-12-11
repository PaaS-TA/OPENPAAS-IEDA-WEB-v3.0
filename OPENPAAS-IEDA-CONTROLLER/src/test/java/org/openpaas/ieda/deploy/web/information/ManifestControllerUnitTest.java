package org.openpaas.ieda.deploy.web.information;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import org.openpaas.ieda.controller.deploy.web.information.manifest.ManifestController;
import org.openpaas.ieda.deploy.web.information.manifest.dao.ManifestVO;
import org.openpaas.ieda.deploy.web.information.manifest.dto.ManifestParamDTO;
import org.openpaas.ieda.deploy.web.information.manifest.service.ManifestService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class ManifestControllerUnitTest extends BaseControllerUnitTest {
    
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    
    @InjectMocks
    private ManifestController mockManifestController;
    @Mock
    private ManifestService mockManifestService;
    private Principal principal = null;
    
    /*************************************** URL *******************************************/
    final static String VIEW_URL= "/info/manifest";
    final static String MANIFEST_LIST_URL= "/info/manifest/list";
    final static String MANIFEST_INFO_URL= "/info/manifest/update/{id}";
    final static String MANIFEST_UPLOAD_URL= "/info/manifest/upload/{test}";
    final static String MANIFEST_DOWNLOAD_URL= "/info/manifest/download/{id}";
    final static String MANIFEST_UPDATE_URL= "/info/manifest/update";
    final static String MANIFEST_DELETE_URL= "/info/manifest/delete/{id}";
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockManifestController).build();
        principal = getLoggined();
    }

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Manifest 정보 화면 호출
     * @title : testGoListManifest
     * @return : void
    ***************************************************/
    @Test
    public void testGoListManifest() throws Exception{
       mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
       .andExpect(status().isOk())
       .andExpect(view().name("/deploy/information/listManifest"));    
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Manifest 정보 목록 조회
     * @title : testGetManifestList
     * @return : void
    ***************************************************/
    @Test
    public void testGetManifestList() throws Exception{
        List<ManifestVO> list = setManifestListVO();
        when(mockManifestService.getManifestList()).thenReturn(list);
        mockMvc.perform(get(MANIFEST_LIST_URL, "deployment").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].fileName").value(list.get(0).getFileName()))
        .andExpect(jsonPath("$.records[0].iaas").value(list.get(0).getIaas()))
        .andExpect(jsonPath("$.records[0].deploymentName").value(list.get(0).getDeploymentName()))
        .andExpect(jsonPath("$.records[0].description").value(list.get(0).getDescription()))
        .andExpect(jsonPath("$.records[0].deployStatus").value(list.get(0).getDeployStatus()));
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 해당 Manifest 파일 정보 조회
     * @title : testGetManifestInfo
     * @return : void
    ***************************************************/
    @Test
    public void testGetManifestInfo() throws Exception{
        when(mockManifestService.getManifestInfo(1)).thenReturn("content");
        mockMvc.perform(get(MANIFEST_INFO_URL, "1").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Manifest 업로드
     * @title : testUploadManifest
     * @return : void
    ***************************************************/
    @Test
    public void testUploadManifest() throws Exception{
        doNothing().when(mockManifestService).uploadManifestFile(any());
        MultipartHttpServletRequest req = new MockMultipartHttpServletRequest();
        mockManifestController.uploadManifest(req);
    }

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Manifest 파일 다운로드
     * @title : testDownloadManifestFile
     * @return : void
    ***************************************************/
    @Test
    public void testDownloadManifestFile() throws Exception{
        MockHttpServletResponse response = new MockHttpServletResponse();
        doNothing().when(mockManifestService).downloadManifestFile(1, response);
        mockManifestController.downloadManifestFile(1, response);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Manifest 내용 수정
     * @title : testUpdateManifest
     * @return : void
    ***************************************************/
    @Test
    public void testUpdateManifest() throws Exception{
        ManifestParamDTO dto = setManifestParamDTO();
        doNothing().when(mockManifestService).updateManifestContent(dto, principal);
        mockMvc.perform(put(MANIFEST_UPDATE_URL).content(mapper.writeValueAsBytes(dto)).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Manifest 삭제
     * @title : testDeleteManifest
     * @return : void
    ***************************************************/
    @Test
    public void testDeleteManifest() throws Exception{
        doNothing().when(mockManifestService).deleteManifest(1);
        mockMvc.perform(delete(MANIFEST_DELETE_URL, 1).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNoContent());
    }
    

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  ManifestVO 목록 정보 설정
     * @title : setManifestListVO
     * @return : List<ManifestVO>
    *****************************************************************/
    private List<ManifestVO> setManifestListVO() {
        List<ManifestVO> list = new ArrayList<ManifestVO>();
        ManifestVO vo = new ManifestVO();
        vo.setFileName("fileName");
        vo.setIaas("iaas");
        vo.setDeploymentName("deploymentName");
        vo.setDescription("description");
        vo.setDeployStatus("deployStatus");
        vo.getFileName();
        vo.getIaas();
        vo.getDeploymentName();
        vo.getDescription();
        vo.getDeployStatus();
        list.add(vo);
        
        return list;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Manifest 입력 값 설정
     * @title : setManifestParamDTO
     * @return : ManifestParamDTO
    ***************************************************/
    private ManifestParamDTO setManifestParamDTO() {
        ManifestParamDTO dto = new ManifestParamDTO();
        dto.setContent("content");
        dto.setFileName("mysql-manifest-1.yml");
        dto.setId("1");
        dto.getContent();
        dto.getFileName();
        dto.getId();
        dto.getContent();
        dto.getFileName();
        dto.getId();
        return dto;
    }
    
}
