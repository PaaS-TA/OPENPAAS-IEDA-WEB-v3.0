package org.openpaas.ieda.controller.hbdeploy.web.deploy.bootstrap;

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
import org.openpaas.ieda.common.BaseControllerUnitTest;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapDefaultConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto.HbBootstrapDefaultConfigDTO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.service.HbBootstrapDefaultConfigService;
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
public class HbBootstrapDefaultConfigControllerUnitTest extends BaseControllerUnitTest {
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    
    @InjectMocks private HbBootstrapDefaultConfigController mockHbBootstrapDefaultConfigController;
    @Mock private HbBootstrapDefaultConfigService mockHbBootstrapDefaultConfigService;
    
    
    final static String BOOTSTRAP_DEFAULT_CONFIG_VIEW_URL = "/deploy/hbBootstrap/defaultConfig";
    final static String BOOTSTRAP_DEFAULT_CONFIG_INFO_LIST_URL = "/deploy/hbBootstrap/default/list";
    final static String BOOTSTRAP_DEFAULT_CONFIG_INFO_SAVE_URL = "/deploy/hbBootstrap/default/save";
    final static String BOOTSTRAP_DEFAULT_CONFIG_INFO_DELETE_URL = "/deploy/hbBootstrap/default/delete";
    
    /****************************************************************
     * @project : Paas 이종 클라우드 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockHbBootstrapDefaultConfigController).build();
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid Bootstrap 기본 정보 관리 이동 Unit Test
     * @title : testGoDefaultConfig
     * @return : void
    *****************************************************************/
    @Test
    public void testGoDefaultConfig() throws Exception{
        mockMvc.perform(get(BOOTSTRAP_DEFAULT_CONFIG_VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/hbdeploy/deploy/bootstrap/hbBootstrapDefaultConfig"));
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid Bootstrap 기본 정보 목록 조회 Unit Test
     * @title : HbBootstrapDefaultConfigVO
     * @return : void
    *****************************************************************/
    @Test
    public void testGetDefaultConfigInfoList() throws Exception{
        List<HbBootstrapDefaultConfigVO> expectList = expectDefaultConfigList();
        when(mockHbBootstrapDefaultConfigService.getDefaultConfigInfoList()).thenReturn(expectList);
        mockMvc.perform(get(BOOTSTRAP_DEFAULT_CONFIG_INFO_LIST_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].recid").value(expectList.get(0).getRecid()))
        .andExpect(jsonPath("$.records[0].boshCpiRelease").value(expectList.get(0).getBoshCpiRelease()))
        .andExpect(jsonPath("$.records[0].boshRelease").value(expectList.get(0).getBoshRelease()))
        .andExpect(jsonPath("$.records[0].credentialKeyName").value(expectList.get(0).getCredentialKeyName()))
        .andExpect(jsonPath("$.records[0].defaultConfigName").value(expectList.get(0).getDefaultConfigName()))
        .andExpect(jsonPath("$.records[0].deploymentName").value(expectList.get(0).getDeploymentName()))
        .andExpect(jsonPath("$.records[0].directorName").value(expectList.get(0).getDirectorName()))
        .andExpect(jsonPath("$.records[0].enableSnapshots").value(expectList.get(0).getEnableSnapshots()))
        .andExpect(jsonPath("$.records[0].iaasType").value(expectList.get(0).getIaasType()))
        .andExpect(jsonPath("$.records[0].snapshotSchedule").value(expectList.get(0).getSnapshotSchedule()))
        .andExpect(jsonPath("$.records[0].paastaMonitoringUse").value(expectList.get(0).getPaastaMonitoringUse()))
        .andExpect(jsonPath("$.records[0].paastaMonitoringRelease").value(expectList.get(0).getPaastaMonitoringRelease()))
        .andExpect(jsonPath("$.records[0].ntp").value(expectList.get(0).getNtp()))
        .andExpect(jsonPath("$.records[0].createUserId").value(expectList.get(0).getCreateUserId()))
        .andExpect(jsonPath("$.records[0].updateUserId").value(expectList.get(0).getUpdateUserId()));
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap 기본 정보 저장 Unit Test
     * @title : testSaveDefaultConfigInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testSaveDefaultConfigInfo() throws Exception {
        String requestJson = mapper.writeValueAsString(setDefaultConfigInfo());
        mockMvc.perform(put(BOOTSTRAP_DEFAULT_CONFIG_INFO_SAVE_URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap 기본 정보 삭제 Unit Test
     * @title : testDeleteDefaultConfigInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testDeleteDefaultConfigInfo() throws Exception{
        String requestJson = mapper.writeValueAsString(setDefaultConfigInfo());
        mockMvc.perform(delete(BOOTSTRAP_DEFAULT_CONFIG_INFO_DELETE_URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNoContent());
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap CPI 정보 입력 값 설정
     * @title : setCpiConfigInfo
     * @return : HbBootstrapCpiConfigDTO
    *****************************************************************/
    private HbBootstrapDefaultConfigDTO setDefaultConfigInfo() {
    	HbBootstrapDefaultConfigDTO dto = new HbBootstrapDefaultConfigDTO();
        dto.setIaasType("AWS");
        dto.setId("1");
        dto.setDeploymentName("bosh");
        dto.setDirectorName("bosh");
        dto.setCredentialKeyName("bosh-cres.yml");
        dto.setNtp("007***UFC");
        dto.setBoshCpiRelease("bosh-cpi-release");
        dto.setBoshRelease("bosh-relesae");
        dto.setEnableSnapshots("true");
        dto.setSnapshotSchedule("snapshot");
        dto.setPaastaMonitoringUse("true");
        dto.setPaastaMonitoringRelease("monitering-release");
        dto.setDefaultConfigName("defaultConfigName");
        
        dto.getIaasType();
        dto.getId();
        dto.getDeploymentName();
        dto.getDirectorName();
        dto.getCredentialKeyName();
        dto.getNtp();
        dto.getBoshCpiRelease();
        dto.getBoshRelease();
        dto.getEnableSnapshots();
        dto.getSnapshotSchedule();
        dto.getPaastaMonitoringUse();
        dto.getPaastaMonitoringRelease();
        dto.getDefaultConfigName();
        return dto;
    }

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap 기본 정보 목록 결과 값 설정
     * @title : expectDefaultConfigList
     * @return : List<HbBootstrapDefaultConfigVO>
    *****************************************************************/
    private List<HbBootstrapDefaultConfigVO> expectDefaultConfigList() {
        List<HbBootstrapDefaultConfigVO> list = new ArrayList<HbBootstrapDefaultConfigVO>();
        HbBootstrapDefaultConfigVO vo = new HbBootstrapDefaultConfigVO();
        vo.setIaasType("Openstack");
        vo.setCreateUserId("admin");
        vo.setDefaultConfigName("bosh");
        vo.setDeploymentName("bosh");
        vo.setDirectorName("bosh");
        vo.setBoshRelease("bosh-release");
        vo.setCredentialKeyName("bosh-cres.yml");
        vo.setNtp("ntp");
        vo.setBoshCpiRelease("cpi-release");
        vo.setEnableSnapshots("true");
        vo.setSnapshotSchedule("007***UFC");
        vo.setUpdateUserId("admin");
        vo.setPaastaMonitoringUse("true");
        vo.setPaastaMonitoringRelease("monitering-release");
        
        vo.getIaasType();
        vo.getCreateUserId();
        vo.getDefaultConfigName();
        vo.getDeploymentName();
        vo.getDirectorName();
        vo.getBoshRelease();
        vo.getCredentialKeyName();
        vo.getNtp();
        vo.getBoshCpiRelease();
        vo.getEnableSnapshots();
        vo.getSnapshotSchedule();
        vo.getUpdateUserId();
        vo.getPaastaMonitoringUse();
        vo.getPaastaMonitoringRelease();
        list.add(vo);
        return list;
    }
}
