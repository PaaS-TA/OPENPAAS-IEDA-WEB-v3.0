package org.openpaas.ieda.hbdeploy.web.information;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.common.BaseControllerUnitTest;
import org.openpaas.ieda.controller.hbdeploy.web.information.deployment.HbDeploymentsController;
import org.openpaas.ieda.deploy.api.deployment.DeploymentInfoDTO;
import org.openpaas.ieda.hbdeploy.web.information.deployment.service.HbDeploymentService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.amazonaws.services.appstream.model.Application;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class HbDeploymentsControllerUnitTest extends BaseControllerUnitTest {

    private MockMvc mockMvc;
    @InjectMocks private HbDeploymentsController mockHbDeploymentsController;
    @Mock private HbDeploymentService mockHbDeploymentService;
    
    /*************************************** URL *******************************************/
    final static String VIEW_URL = "/info/hbDeployment";
    final static String HYBRID_DEPLOYMENT_LIST_URL = "/info/hbDeployment/list/{directorId}";
    /*****************************************************************/
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 메소드 실행 전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockHbDeploymentsController).build();
        getLoggined();
    }
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 배포 정보 화면 이동 test
     * @title : testGoListDeployment
     * @return : void
    *****************************************************************/
    @Test
    public void testGoListDeployment() throws Exception{
        mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/hbdeploy/information/listHbDeployment"));
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 설치 정보 목록 조회
     * @title : testListDeployment
     * @return : void
    *****************************************************************/
    @Test
    public void testListDeployment() throws Exception {
        List<DeploymentInfoDTO> depList = setupListDeployment();
        when(mockHbDeploymentService.listDeployment(anyString())).thenReturn(depList);
        mockMvc.perform(get(HYBRID_DEPLOYMENT_LIST_URL, "director").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].name").value(depList.get(0).getName()))
        .andExpect(jsonPath("$.records[0].releaseInfo").value(depList.get(0).getReleaseInfo()))
        .andExpect(jsonPath("$.records[0].stemcellInfo").value(depList.get(0).getStemcellInfo()));
    }
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드된 릴리즈&스템셀 목록 설정
     * @title : setupListDeployment
     * @return : List<DeploymentInfoDTO>
    *****************************************************************/
    public List<DeploymentInfoDTO> setupListDeployment(){
        List<DeploymentInfoDTO> list = new ArrayList<DeploymentInfoDTO>();
        DeploymentInfoDTO dto = new DeploymentInfoDTO();
        dto.setName("cf");
        dto.setReleaseInfo("hb_cf_release");
        dto.setStemcellInfo("hb_stemcell_3486");
        list.add(dto);
        return list;
    }
}
