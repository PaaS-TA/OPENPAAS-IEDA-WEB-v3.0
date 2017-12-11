package org.openpaas.ieda.iaas.web.main;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
import org.openpaas.ieda.controller.deploy.web.dashboard.DashboardController;
import org.openpaas.ieda.deploy.api.deployment.DeploymentInfoDTO;
import org.openpaas.ieda.deploy.api.release.ReleaseInfoDTO;
import org.openpaas.ieda.deploy.web.config.stemcell.dao.StemcellManagementVO;
import org.openpaas.ieda.deploy.web.information.deploy.service.DeploymentService;
import org.openpaas.ieda.deploy.web.information.release.service.ReleaseService;
import org.openpaas.ieda.deploy.web.information.stemcell.service.StemcellService;
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
public class DashboardControllerUnitTest extends BaseControllerUnitTest{

    private MockMvc mockMvc;
    
    @InjectMocks DashboardController mockDashboardController;
    @Mock DeploymentService mockDeploymentService;
    @Mock ReleaseService mockReleaseService;
    @Mock StemcellService mockStemcellService;
    
    final static String DASHBOARD_VIEW_URL = "/main/dashboard";
    final static String DEPLOYMENT_LIST_URL = "/main/dashboard/deployments";
    final static String RELEASE_LIST_URL = "/main/dashboard/releases";
    final static String STEMCELL_LIST_URL = "/main/dashboard/stemcells";
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockDashboardController).build();
        getLoggined();
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : DASHBOARD 화면 호출
     * @title : testGoDashboard
     * @return : void
    ***************************************************/
    @Test
    public void testGoDashboard() throws Exception{
        mockMvc.perform(get(DASHBOARD_VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/deploy/dashboard/dashboard"));
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 설치 정보 목록 조회
     * @title : testGetDploymentList
     * @return : void
    ***************************************************/
    @Test
    public void testGetDploymentList() throws Exception{
        List<DeploymentInfoDTO> deployments = setDeploymentList();
        when(mockDeploymentService.listDeployment()).thenReturn(deployments);
        mockMvc.perform(get(DEPLOYMENT_LIST_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드 릴리즈 정보 목록 조회
     * @title : testGetReleaseList
     * @return : void
    ***************************************************/
    @Test
    public void testGetReleaseList() throws Exception{
        when(mockReleaseService.getUploadedReleaseList()).thenReturn(new ArrayList<ReleaseInfoDTO>());
        mockMvc.perform(get(RELEASE_LIST_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드 스템셀 정보 목록 조회
     * @title : testGetStemcellList
     * @return : void
    ***************************************************/
    @Test
    public void testGetStemcellList() throws Exception{
        when(mockStemcellService.getStemcellList()).thenReturn(new ArrayList<StemcellManagementVO>());
        mockMvc.perform(get(STEMCELL_LIST_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 설치 정보 목록 설정
     * @title : setDeploymentList
     * @return : List<DeploymentInfoDTO>
    ***************************************************/
    public List<DeploymentInfoDTO> setDeploymentList(){
        List<DeploymentInfoDTO> list = new ArrayList<DeploymentInfoDTO>();
        DeploymentInfoDTO dto = new DeploymentInfoDTO();
        dto.setName("cf");
        dto.setRecid(1);
        dto.setReleaseInfo("cf-release");
        dto.setStemcellInfo("google-stemcell-3363.tgz");
        list.add(dto);
        return list;
    }

}
