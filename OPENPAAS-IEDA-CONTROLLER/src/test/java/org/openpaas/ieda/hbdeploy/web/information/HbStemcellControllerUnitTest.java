package org.openpaas.ieda.hbdeploy.web.information;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
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
import org.openpaas.ieda.controller.hbdeploy.web.information.stemcell.HbStemcellController;
import org.openpaas.ieda.hbdeploy.web.config.stemcell.dao.HbStemcellManagementVO;
import org.openpaas.ieda.hbdeploy.web.config.stemcell.service.HbStemcellManagementService;
import org.openpaas.ieda.hbdeploy.web.information.stemcell.dto.HbStemcellDTO;
import org.openpaas.ieda.hbdeploy.web.information.stemcell.service.HbStemcellDeleteAsyncService;
import org.openpaas.ieda.hbdeploy.web.information.stemcell.service.HbStemcellService;
import org.openpaas.ieda.hbdeploy.web.information.stemcell.service.HbStemcellUploadAsyncService;
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
public class HbStemcellControllerUnitTest extends BaseControllerUnitTest{
	
    private MockMvc mockMvc;
    private Principal principal = null;
    
    @InjectMocks
    private HbStemcellController mockHbStemcellController;
    @Mock
    private HbStemcellService mockHbStemcellService;
    @Mock
    private HbStemcellUploadAsyncService mockHbStemcellUploadAsyncService;
    @Mock
    private HbStemcellDeleteAsyncService mockHbStemcellDeleteAsyncService;
    @Mock
    private HbStemcellManagementService mockStemcellManagementService;
    
    /*************************************** URL *******************************************/
    final static String VIEW_URL="/info/hbStemcell";
    final static String UPLOADED_STEMCELL_LIST_URL="/info/hbstemcell/list/upload/{directorId}";
    final static String DOWNLOADED_STEMCELL_LIST_URL="/info/hbstemcell/list/local/{iaas}";
    final static String STEMCELL_UPLOAD_URL="/info/hbstemcell/upload/stemcellUploading";
    final static String UPLOADED_STEMCELL_DELETE_URL="/info/hbstemcell/delete/stemcellDelete";
    /**************************************************************************************/
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기 전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockHbStemcellController).build();
        principal = getLoggined();
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 스템셀 업로드 화면을 호출하여 이동
     * @title : testGoListStemcell
     * @return : void
    *****************************************************************/
    @Test
    public void testGoListStemcell() throws Exception{
        mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/hbdeploy/information/listHbStemcell"));
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드된 스템셀 목록 조회
     * @title : testGetUploadStemcellList
     * @return : void
    *****************************************************************/
    @Test
    public void testGetUploadStemcellList() throws Exception{
        List<HbStemcellManagementVO> stemcellList = setUploadStemcellList();
        when(mockHbStemcellService.getStemcellList(anyInt())).thenReturn(stemcellList);
        mockMvc.perform(get(UPLOADED_STEMCELL_LIST_URL, 1).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].recid").value(stemcellList.get(0).getRecid()))
        .andExpect(jsonPath("$.records[0].stemcellFileName").value(stemcellList.get(0).getStemcellFileName()))
        .andExpect(jsonPath("$.records[0].os").value(stemcellList.get(0).getOs()))
        .andExpect(jsonPath("$.records[0].stemcellVersion").value(stemcellList.get(0).getStemcellVersion()));
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description :  로컬에 다운로드된 스템셀 목록 조회
     * @title : testGetLocalStemcellList
     * @return : void
    *****************************************************************/
    @Test
    public void testGetLocalStemcellList() throws Exception{
        List<HbStemcellManagementVO> stemcellList = setUploadStemcellList();
        when(mockStemcellManagementService.getLocalStemcellList(anyString())).thenReturn(stemcellList);
        mockMvc.perform(get(DOWNLOADED_STEMCELL_LIST_URL, "aws").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].id").value(stemcellList.get(0).getRecid()))
        .andExpect(jsonPath("$.records[0].stemcellFileName").value(stemcellList.get(0).getStemcellFileName()));
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 스템셀 업로드
     * @title : testUploadStemcell
     * @return : void
    *****************************************************************/
    @Test
    public void testUploadStemcell() throws Exception{
        HbStemcellDTO.Upload dto = setUploadStemcellInfo();
        doNothing().when(mockHbStemcellUploadAsyncService).uploadStemcellAsync(anyString(), anyString(), anyString(), anyString());
        mockHbStemcellController.uploadStemcell(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 업로드된 스템셀 삭제
     * @title : testDeleteStemcell
     * @return : void
    *****************************************************************/
    @Test
    public void testDeleteStemcell() throws Exception{
        HbStemcellDTO.Delete dto = setDeleteUploadedStemcellInfo();
        doNothing().when(mockHbStemcellDeleteAsyncService).deleteStemcellAsync(anyString(), anyString(), anyString(), any());
        mockHbStemcellController.deleteStemcell(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 업로드된 스템셀 목록 설정
     * @title : setUploadStemcellList
     * @return : List<StemcellManagementVO>
    *****************************************************************/
    public List<HbStemcellManagementVO> setUploadStemcellList(){
        List<HbStemcellManagementVO> list = new ArrayList<HbStemcellManagementVO>();
        HbStemcellManagementVO vo = new HbStemcellManagementVO();
        vo.setRecid(1);
        vo.setId(1);
        vo.setStemcellFileName("bosh-aws-xen-ubuntu-trusty-go_agent");
        vo.setOs("ubuntu-trusty");
        vo.setStemcellVersion("3468");
        vo.setCreateDate(new Date());
        vo.setUpdateDate(new Date());
        vo.setCreateUserId("");
        vo.setUpdateUserId("");
        vo.setIaas("");
        vo.setIsDose("");
        vo.setIsExisted("");
        vo.setStemcellUrl("");
        vo.setStemcellName("");
        vo.setSize("");
        vo.setOsVersion("");
        vo.setDownloadStatus("");
        list.add(vo);
        return list;
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 업로드할 스템셀 정보 설정
     * @title : setUploadStemcellInfo
     * @return : StemcellDTO.Upload
    *****************************************************************/
    public HbStemcellDTO.Upload setUploadStemcellInfo(){
        HbStemcellDTO.Upload upload = new HbStemcellDTO.Upload();
        upload.setFileName("bosh-stemcell-3536-aws-xen-ubuntu-trusty-go_agent.tgz");
        upload.getFileName();
        return upload;
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 업로드된 스템셀 삭제
     * @title : setDeleteUploadedStemcellInfo
     * @return : StemcellDTO.Delete
    *****************************************************************/
    public HbStemcellDTO.Delete setDeleteUploadedStemcellInfo(){
        HbStemcellDTO.Delete stemcell = new HbStemcellDTO.Delete();
        stemcell.setStemcellName("bosh-aws-xen-ubuntu-trusty-go_agent");
        stemcell.setVersion("3468");
        stemcell.getStemcellName();
        stemcell.getVersion();
        
        return stemcell;
    }
}
