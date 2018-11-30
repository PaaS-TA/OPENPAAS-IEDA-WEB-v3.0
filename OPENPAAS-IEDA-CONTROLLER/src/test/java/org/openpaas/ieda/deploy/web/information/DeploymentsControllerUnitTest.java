package org.openpaas.ieda.deploy.web.information;

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
import org.openpaas.ieda.controller.deploy.web.information.deployment.DeploymentsController;
import org.openpaas.ieda.deploy.api.deployment.DeploymentInfoDTO;
import org.openpaas.ieda.deploy.web.information.deployment.service.DeploymentService;
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
public class DeploymentsControllerUnitTest extends BaseControllerUnitTest {

    private MockMvc mockMvc;
    @InjectMocks private DeploymentsController mockDeploymentsController;
    @Mock private DeploymentService mockDeploymentService;
    
    /*************************************** URL *******************************************/
    final static String VIEW_URL="/info/deployment";
    final static String DEPOLYMENT_LIST_URL="/info/deployment/list";
    /***************************************************************************************/
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockDeploymentsController).build();
        getLoggined();
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 배포 정보 화면 이동 test
     * @title : testGoListDeployment
     * @return : void
    *****************************************************************/
    @Test
    public void testGoListDeployment() throws Exception{
        mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/deploy/information/listDeployment"));
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 설치 정보 목록을 조회 
     * @title : testListDeployment
     * @return : void
    *****************************************************************/
    @Test
    public void testListDeployment() throws Exception{
        when(mockDeploymentService.listDeployment()).thenReturn(setupListDeployment());
        mockMvc.perform(get(DEPOLYMENT_LIST_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드된 스템셀 목록 설정
     * @title : setupListDeployment
     * @return :  HashMap<String, Object>
    *****************************************************************/
    public List<DeploymentInfoDTO> setupListDeployment(){
        List<DeploymentInfoDTO> list = new ArrayList<DeploymentInfoDTO>();
        DeploymentInfoDTO dto = new DeploymentInfoDTO();
        dto.setName("cf");
        dto.setReleaseInfo("cf-release");
        dto.setStemcellInfo("stemcell-3363");
        list.add(dto);
        dto.getName();
        dto.getReleaseInfo();
        dto.getStemcellInfo();
        return list;
    }
}