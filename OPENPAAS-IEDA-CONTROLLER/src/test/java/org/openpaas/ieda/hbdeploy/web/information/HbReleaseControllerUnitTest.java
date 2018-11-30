package org.openpaas.ieda.hbdeploy.web.information;

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
import org.openpaas.ieda.controller.hbdeploy.web.information.release.HbReleaseController;
import org.openpaas.ieda.deploy.api.release.ReleaseInfoDTO;
import org.openpaas.ieda.deploy.web.config.systemRelease.dao.ReleaseManagementVO;
import org.openpaas.ieda.deploy.web.config.systemRelease.service.ReleaseManagementService;
import org.openpaas.ieda.deploy.web.information.release.dto.ReleaseContentDTO;
import org.openpaas.ieda.hbdeploy.web.information.release.service.HbReleaseDeleteAsyncService;
import org.openpaas.ieda.hbdeploy.web.information.release.service.HbReleaseService;
import org.openpaas.ieda.hbdeploy.web.information.release.service.HbReleaseUploadAsyncService;
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
public class HbReleaseControllerUnitTest extends BaseControllerUnitTest{

	private MockMvc mockMvc;
	private Principal principal = null;
	
	@InjectMocks
	private HbReleaseController mockHbReleaseController;
	@Mock
	private HbReleaseService mockHbReleaseService;
	@Mock
	private HbReleaseUploadAsyncService mockHbReleaseUploadAsyncService;
	@Mock
	private HbReleaseDeleteAsyncService mockHbReleaseDeleteAsyncService;
	@Mock
	private ReleaseManagementService mockReleaseManagementService;
	
	/*************************************** URL *******************************************/
    final static String VIEW_URL="/info/hbRelease";
    final static String UPLOADED_RELEASE_LIST_URL="/info/hbRelease/list/upload/{directorId}";
    final static String DOWNLOADED_RELEASE_LIST_URL="/info/hbRelease/list/local";
    final static String RELEASE_UPLOAD_URL="/info/hbRelease/upload/releaseUploading";
    final static String UPLOADED_RELEASE_DELETE_URL="/info/hbRelease/delete/releaseDelete";
    /****************************************************************/
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기 전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception{
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockHbReleaseController).build();
        principal = getLoggined();
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 릴리즈 업로드 화면을 호출하여 이동 
     * @title : testGoListRelease
     * @return : void
    *****************************************************************/
    @Test
    public void testGoListRelease() throws Exception{
        mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/hbdeploy/information/listHbRelease"));
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드된 릴리즈 목록 조회
     * @title : testGetuploadReleaseList
     * @return : void
    *****************************************************************/
    @Test
    public void testGetuploadReleaseList() throws Exception{
    	List<ReleaseInfoDTO> releaseList = setUploadReleaseList();
        when(mockHbReleaseService.getUploadedReleaseList(anyString())).thenReturn(releaseList);
        mockMvc.perform(get(UPLOADED_RELEASE_LIST_URL, "directorId").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].recid").value(releaseList.get(0).getRecid()))
        .andExpect(jsonPath("$.records[0].name").value(releaseList.get(0).getName()))
        .andExpect(jsonPath("$.records[0].version").value(releaseList.get(0).getVersion()))
        .andExpect(jsonPath("$.records[0].currentDeployed").value(releaseList.get(0).getCurrentDeployed()))
        .andExpect(jsonPath("$.records[0].jobNames").value(releaseList.get(0).getJobNames()));
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 로컬에 다운로드된 릴리즈 목록 정보
     * @title : testGetLocalReleaseList
     * @return : void
    *****************************************************************/
    @Test
    public void testGetLocalReleaseList() throws Exception{
        List<ReleaseManagementVO> releaseList = setLocalReleaseList();
        when(mockReleaseManagementService.getSystemReleaseList()).thenReturn(releaseList);
        mockMvc.perform(get(DOWNLOADED_RELEASE_LIST_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].id").value(releaseList.get(0).getId()))
        .andExpect(jsonPath("$.records[0].releaseFileName").value(releaseList.get(0).getReleaseFileName()));
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 릴리즈 업로드
     * @title : testUploadRelease
     * @return : void
    *****************************************************************/
    @Test
    public void testUploadRelease() throws Exception{
        ReleaseContentDTO.Upload dto = setUploadReleaseInfo();
        doNothing().when(mockHbReleaseUploadAsyncService).uploadReleaseAsync(anyString(), anyString(), anyString());
        mockHbReleaseController.uploadRelease(principal, dto);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 업로드된 릴리즈 삭제
     * @title : testDeleteRelease
     * @return : void
    *****************************************************************/
    @Test
    public void testDeleteRelease() throws Exception{
        ReleaseContentDTO.Delete dto = setDeleteReleaseInfo();
        doNothing().when(mockHbReleaseDeleteAsyncService).deleteReleaseAsync(anyString(), anyString(), anyString(), any());
        mockHbReleaseController.deleteRelease(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description :  업로드된 릴리즈 목록 정보 설정
     * @title : setUploadReleaseList
     * @return : List<ReleaseInfoDTO>
    *****************************************************************/
    public List<ReleaseInfoDTO> setUploadReleaseList(){
        List<ReleaseInfoDTO> list = new ArrayList<ReleaseInfoDTO>();
        ReleaseInfoDTO releaseInfo = new ReleaseInfoDTO();
        releaseInfo.setRecid(1);
        releaseInfo.setName("bosh-openstack-cpi-release");
        releaseInfo.setVersion("35");
        releaseInfo.setCurrentDeployed("true");
        releaseInfo.setJobNames("3fcgG4");
        list.add(releaseInfo);
        return list;
        
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 플랫폼 설치 자동화에 다운로드된 릴리즈 목록 정보 설정
     * @title : setLocalReleaseList
     * @return : List<HbReleaseManagementVO>
    *****************************************************************/
    public List<ReleaseManagementVO> setLocalReleaseList(){
        List<ReleaseManagementVO> list = new ArrayList<ReleaseManagementVO>();
        ReleaseManagementVO vo = new ReleaseManagementVO();
        vo.setId(1);
        vo.setReleaseFileName("bosh-openstack-cpi-release-35");
        vo.setCreateDate(new Date());
        vo.setUpdateDate(new Date());
        vo.setDownloadLink("");
        vo.setDownloadStatus("");
        vo.setRecid(1);
        vo.setReleaseName("");
        vo.setReleaseSize("");
        vo.setReleaseType("");
        vo.setUpdateUserId("");
        vo.setCreateUserId("");
        list.add(vo);
        return list;
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 업로드할 릴리즈 정보 설정
     * @title : setUploadReleaseInfo
     * @return : ReleaseContentDTO.Upload
    *****************************************************************/
    public ReleaseContentDTO.Upload setUploadReleaseInfo(){
        ReleaseContentDTO.Upload upload = new ReleaseContentDTO.Upload();
        upload.setFileName("bosh-openstack-cpi-release-40");
        upload.getFileName();
        return upload;
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 업로드된 릴리즈 삭제 정보 설정
     * @title : setDeleteReleaseInfo
     * @return : ReleaseContentDTO.Delete
    *****************************************************************/
    public ReleaseContentDTO.Delete setDeleteReleaseInfo(){
        ReleaseContentDTO.Delete release = new ReleaseContentDTO.Delete();
        release.setFileName("bosh-openstack-cpi-release");
        release.setVersion("35");
        release.getFileName();
        release.getVersion();
        return release;
    }
}
