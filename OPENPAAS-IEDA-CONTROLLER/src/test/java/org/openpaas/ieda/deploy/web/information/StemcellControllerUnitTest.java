package org.openpaas.ieda.deploy.web.information;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.common.BaseControllerUnitTest;
import org.openpaas.ieda.controller.deploy.web.information.stemcell.StemcellController;
import org.openpaas.ieda.deploy.web.config.stemcell.dao.StemcellManagementVO;
import org.openpaas.ieda.deploy.web.config.stemcell.service.StemcellManagementService;
import org.openpaas.ieda.deploy.web.information.stemcell.dto.StemcellDTO;
import org.openpaas.ieda.deploy.web.information.stemcell.service.StemcellDeleteAsyncService;
import org.openpaas.ieda.deploy.web.information.stemcell.service.StemcellService;
import org.openpaas.ieda.deploy.web.information.stemcell.service.StemcellUploadAsyncService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class StemcellControllerUnitTest extends BaseControllerUnitTest {
    
    private MockMvc mockMvc;
    private Principal principal = null;
    
    @InjectMocks
    private StemcellController mockStemcellController;
    @Mock
    private StemcellService mockStemcellService;
    @Mock
    private StemcellUploadAsyncService mockStemcellUploadAsyncService;
    @Mock
    private StemcellDeleteAsyncService mockStemcellDeleteAsyncService;
    @Mock
    private StemcellManagementService mockStemcellManagementService;
    
    /*************************************** URL *******************************************/
    final static String VIEW_URL="/info/stemcell";
    final static String UPLOADED_STEMCELL_LIST_URL="/info/stemcell/list/upload";
    final static String DOWNLOADED_STEMCELL_LIST_URL="/info/stemcell/list/local/{iaas}";
    final static String STEMCELL_UPLOAD_URL="/info/stemcell/upload/stemcellUploading";
    final static String UPLOADED_STEMCELL_DELETE_URL="/info/stemcell/delete/stemcellDelete";
    
    
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
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 업로드 화면을 호출하여 이동
     * @title : testGoListStemcell
     * @return : void
    *****************************************************************/
    @Test
    public void testGoListStemcell() throws Exception{
        mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/deploy/information/listStemcell"));
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드된 스템셀 목록 조회
     * @title : testGetUploadStemcellLIst
     * @return : void
    *****************************************************************/
    @Test
    public void testGetUploadStemcellList() throws Exception{
        List<StemcellManagementVO> stemcellList = setUploadStemcellList();
        when(mockStemcellService.getStemcellList()).thenReturn(stemcellList);
        mockMvc.perform(get(UPLOADED_STEMCELL_LIST_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].recid").value(stemcellList.get(0).getRecid()))
        .andExpect(jsonPath("$.records[0].stemcellFileName").value(stemcellList.get(0).getStemcellFileName()))
        .andExpect(jsonPath("$.records[0].os").value(stemcellList.get(0).getOs()))
        .andExpect(jsonPath("$.records[0].stemcellVersion").value(stemcellList.get(0).getStemcellVersion()));
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 다운로드된 스템셀 목록 조회
     * @title : testGetLocalStemcellList
     * @return : void
    *****************************************************************/
    @Test
    public void testGetLocalStemcellList() throws Exception{
        List<StemcellManagementVO> stemcellList = setUploadStemcellList();
        when(mockStemcellManagementService.getLocalStemcellList(anyString())).thenReturn(stemcellList);
        mockMvc.perform(get(DOWNLOADED_STEMCELL_LIST_URL, "aws").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].id").value(stemcellList.get(0).getRecid()))
        .andExpect(jsonPath("$.records[0].stemcellFileName").value(stemcellList.get(0).getStemcellFileName()));
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 업로드
     * @title : testUploadStemcell
     * @return : void
    *****************************************************************/
    @Test
    public void testUploadStemcell() throws Exception{
        StemcellDTO.Upload dto = setUploadStemcellInfo();
        doNothing().when(mockStemcellUploadAsyncService).uploadStemcellAsync(anyString(), anyString(), anyString());
        mockStemcellController.uploadStemcell(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드된 스템셀 삭제
     * @title : testDeleteStemcell
     * @return : void
    *****************************************************************/
    @Test
    public void testDeleteStemcell() throws Exception{
        StemcellDTO.Delete dto = setDeleteUploadedStemcellInfo();
        doNothing().when(mockStemcellDeleteAsyncService).deleteStemcellAsync(anyString(), anyString(), any());
        mockStemcellController.deleteStemcell(dto, principal);
    }
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드된 스템셀 목록 설정
     * @title : setUploadStemcellList
     * @return : List<StemcellManagementVO>
    *****************************************************************/
    public List<StemcellManagementVO> setUploadStemcellList(){
        List<StemcellManagementVO> list = new ArrayList<StemcellManagementVO>();
        StemcellManagementVO vo = new StemcellManagementVO();
        vo.setRecid(1);
        vo.setId(1);
        vo.setStemcellFileName("bosh-aws-xen-ubuntu-trusty-go_agent");
        vo.setOs("ubuntu-trusty");
        vo.setStemcellVersion("3262");
        vo.getRecid();
        vo.getId();
        vo.getStemcellFileName();
        vo.getOs();
        vo.getStemcellVersion();
        vo.setCreateDate(new Date());
        vo.setUpdateDate(new Date());
        vo.setCreateUserId("");
        vo.getCreateUserId();
        vo.setUpdateUserId("");
        vo.getUpdateUserId();
        vo.setIaas("");
        vo.getIaas();
        vo.setIsDose("");
        vo.getIsDose();
        vo.setIsExisted("");
        vo.getIsExisted();
        vo.setStemcellUrl("");
        vo.getStemcellUrl();
        vo.setStemcellName("");
        vo.getStemcellFileName();
        vo.setSize("");
        vo.getSize();
        vo.setOsVersion("");
        vo.getOsVersion();
        vo.setDownloadStatus("");
        vo.getDownloadStatus();
        
        list.add(vo);
        
        return list;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드할 스템셀 정보 설정
     * @title : setUploadStemcellInfo
     * @return : StemcellDTO.Upload
    *****************************************************************/
    public StemcellDTO.Upload setUploadStemcellInfo(){
        StemcellDTO.Upload upload = new StemcellDTO.Upload();
        upload.setFileName("bosh-stemcell-3263-aws-xen-ubuntu-trusty-go_agent.tgz");
        upload.getFileName();
        return upload;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드된 스템셀 삭제
     * @title : setDeleteUploadedStemcellInfo
     * @return : StemcellDTO.Delete
    *****************************************************************/
    public StemcellDTO.Delete setDeleteUploadedStemcellInfo(){
        StemcellDTO.Delete stemcell = new StemcellDTO.Delete();
        stemcell.setStemcellName("bosh-aws-xen-ubuntu-trusty-go_agent");
        stemcell.setVersion("3263");
        stemcell.getStemcellName();
        stemcell.getVersion();
        
        return stemcell;
    }

}
