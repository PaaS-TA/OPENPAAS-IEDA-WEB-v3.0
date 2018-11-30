package org.openpaas.ieda.controller.hbdeploy.web.deploy.cfdeployment;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentVO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dto.HbCfDeploymentDTO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service.HbCfDeploymentDeleteAsyncService;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service.HbCfDeploymentDeployAsyncService;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service.HbCfDeploymentSaveService;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service.HbCfDeploymentService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class HbCfDeploymentControllerUnitTest {
    
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    
    @InjectMocks private HbCfDeploymentController mockHbCfDeploymentController;
    @Mock private HbCfDeploymentService mockHbCfDeploymentService;
    @Mock private HbCfDeploymentSaveService mockHbCfDeploymentSaveService;
    @Mock private HbCfDeploymentDeployAsyncService mockHbCfDeploymentDeployAsyncService;
    @Mock private HbCfDeploymentDeleteAsyncService mockHbCfDeploymentDeleteAsyncService;
    
    private static final String CF_VIEW_URL = "/deploy/hbCfDeployment/hbCfDeployment";
    private static final String CF_LIST_URL = "/deploy/hbCfDeployment/list/{installStatus}";
    private static final String CF_SAVE_URL = "/deploy/hbCfDeployment/install/save";
    private static final String CF_DELETE_URL = "/deploy/hbCfDeployment/install/delete";
    
    /****************************************************************
     * @project : Paas 이종 클라우드 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockHbCfDeploymentController).build();
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 설치 화면 이동 Unit Test
     * @title : testGoCfDeployment
     * @return : void
    *****************************************************************/
    @Test
    public void testGoCfDeployment() throws Exception{
        mockMvc.perform(get(CF_VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/hbdeploy/deploy/cfDeployment/hbCfDeployment"));
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 설치 목록 조회 Unit Test
     * @title : testGoCfDeployment
     * @return : void
    *****************************************************************/
    @Test
    public void testGetHbCfDeploymenList() throws Exception{
        List<HbCfDeploymentVO> expectList = setCfConfigList();
        when(mockHbCfDeploymentService.getHbCfDeploymentList(anyString())).thenReturn(expectList);
        mockMvc.perform(get(CF_LIST_URL,"installAble").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].recid").value(expectList.get(0).getRecid()))
        .andExpect(jsonPath("$.records[0].cfDeploymentConfigName").value(expectList.get(0).getCfDeploymentConfigName()))
        .andExpect(jsonPath("$.records[0].id").value(expectList.get(0).getId()))
        .andExpect(jsonPath("$.records[0].iaasType").value(expectList.get(0).getIaasType()))
        .andExpect(jsonPath("$.records[0].networkConfigInfo").value(expectList.get(0).getNetworkConfigInfo()))
        .andExpect(jsonPath("$.records[0].credentialConfigInfo").value(expectList.get(0).getCredentialConfigInfo()))
        .andExpect(jsonPath("$.records[0].defaultConfigInfo").value(expectList.get(0).getDefaultConfigInfo()))
        .andExpect(jsonPath("$.records[0].taskId").value(expectList.get(0).getTaskId()))
        .andExpect(jsonPath("$.records[0].cloudConfigFile").value(expectList.get(0).getCloudConfigFile()))
        .andExpect(jsonPath("$.records[0].instanceConfigInfo").value(expectList.get(0).getInstanceConfigInfo()))
        .andExpect(jsonPath("$.records[0].resourceConfigInfo").value(expectList.get(0).getResourceConfigInfo()));
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 설치 정보 저장 Unit Test
     * @title : testGoCfDeployment
     * @return : void
    *****************************************************************/
    @Test
    public void testSaveCfDeploymentInfo() throws Exception{
        String requestJson = mapper.writeValueAsString(setCfConfigInfo());
        mockMvc.perform(put(CF_SAVE_URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 설치 정보 삭제 Unit Test
     * @title : testGoCfDeployment
     * @return : void
    *****************************************************************/
    @Test
    public void testDeleteCfDeploymentInfo() throws Exception{
        String requestJson = mapper.writeValueAsString(setCfConfigInfo());
        mockMvc.perform(delete(CF_DELETE_URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNoContent());
    }

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 설치 목록 조회 결과 값 설정
     * @title : setCfConfigInfo
     * @return : List<HbCfDeploymentVO>
    *****************************************************************/
    private HbCfDeploymentDTO setCfConfigInfo() {
        HbCfDeploymentDTO dto = new HbCfDeploymentDTO();
        dto.setId(1);
        dto.setCfDeploymentConfigName("cf-config");
        dto.setCloudConfigFile("cloud-config.yml");
        dto.setCredentialConfigInfo("crenential-config");
        dto.setDefaultConfigInfo("default-config");
        dto.setDeployStatus("done");
        dto.setIaasType("Openstack");
        dto.setInstanceConfigInfo("instance-config");
        dto.setIaasType("Openstack");
        dto.setNetworkConfigInfo("network-config");
        dto.setTaskId("1");
        dto.setResourceConfigInfo("resource-config");
        return dto;
    }

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 설치 목록 조회 결과 값 설정
     * @title : setCfConfigList
     * @return : List<HbCfDeploymentVO>
    *****************************************************************/
    private List<HbCfDeploymentVO> setCfConfigList() {
        List<HbCfDeploymentVO> list = new ArrayList<HbCfDeploymentVO>();
        HbCfDeploymentVO vo = new HbCfDeploymentVO();
        vo.setCfDeploymentConfigName("cf-config");
        vo.setCloudConfigFile("cloud-config.yml");
        vo.setCredentialConfigInfo("crendential-config");
        vo.setDefaultConfigInfo("default-config");
        vo.setIaasType("Openstack");
        vo.setResourceConfigInfo("resource-config");
        vo.setDeployStatus("done");
        vo.setNetworkConfigInfo("network-config");
        vo.setId(1);
        vo.setInstanceConfigInfo("instance-config");
        vo.setTaskId("1");
        list.add(vo);
        return list;
    }
}
