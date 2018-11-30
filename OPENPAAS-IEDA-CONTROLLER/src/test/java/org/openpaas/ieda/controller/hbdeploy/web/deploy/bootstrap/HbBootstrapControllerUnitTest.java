package org.openpaas.ieda.controller.hbdeploy.web.deploy.bootstrap;

import static org.mockito.Matchers.anyInt;
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
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapVO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto.HbBootStrapDeployDTO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.service.HbBootstrapDeleteDeployAsyncService;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.service.HbBootstrapDeployAsyncService;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.service.HbBootstrapSaveService;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.service.HbBootstrapService;
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
public class HbBootstrapControllerUnitTest {
    
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    
    @InjectMocks private HbBootstrapController mockHbBootstrapController;
    @Mock private HbBootstrapSaveService mockHbBootstrapSaveService;
    @Mock private HbBootstrapService mockHbBootstrapService;
    @Mock private HbBootstrapDeployAsyncService mockHbBootstrapDeployAsyncService;
    @Mock private HbBootstrapDeleteDeployAsyncService mockHbBootstrapDeleteDeployAsyncService;
    
    final static String BOOTSTRAP_CONFIG_VIEW_URL = "/deploy/hbBootstrap";
    final static String BOOTSTRAP_CONFIG_INFO_LIST_URL = "/deploy/hbBootstrap/list/{installStatus}";
    final static String BOOTSTRAP_CONFIG_INFO_URL = "/deploy/hbBootstrap/install/detail/{id}/{iaas}";
    final static String BOOTSTRAP_CONFIG_INFO_SAVE_URL = "/deploy/hbBootstrap/install/save";
    final static String BOOTSTRAP_CONFIG_INFO_DELETE_URL = "/deploy/hbBootstrap/delete/data";
    
    
    /****************************************************************
     * @project : Paas 이종 클라우드 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockHbBootstrapController).build();
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap 설치 화면 이동 Unit Test
     * @title : testGoHbBootstrapCpiConfig
     * @return : void
    *****************************************************************/
    @Test
    public void testGoHbBootstrap() throws Exception{
        mockMvc.perform(get(BOOTSTRAP_CONFIG_VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/hbdeploy/deploy/bootstrap/hbBootstrap"));
    }
    
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap 목록 정보 조회 Unit Test
     * @title : testGetHbBootstrapInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testGetHbBootstrapList() throws Exception{
        List<HbBootstrapVO> expectList = expectBootstrapConfigList();
        when(mockHbBootstrapService.getHbBootstrapList("installAble")).thenReturn(expectList);
        mockMvc.perform(get(BOOTSTRAP_CONFIG_INFO_LIST_URL,"installAble").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].recid").value(expectList.get(0).getRecid()))
        .andExpect(jsonPath("$.records[0].bootstrapConfigName").value(expectList.get(0).getBootstrapConfigName()))
        .andExpect(jsonPath("$.records[0].id").value(expectList.get(0).getId()))
        .andExpect(jsonPath("$.records[0].networkConfigInfo").value(expectList.get(0).getNetworkConfigInfo()))
        .andExpect(jsonPath("$.records[0].cpiConfigInfo").value(expectList.get(0).getCpiConfigInfo()))
        .andExpect(jsonPath("$.records[0].defaultConfigInfo").value(expectList.get(0).getDefaultConfigInfo()))
        .andExpect(jsonPath("$.records[0].resourceConfigInfo").value(expectList.get(0).getResourceConfigInfo()))
        .andExpect(jsonPath("$.records[0].deploymentFile").value(expectList.get(0).getDeploymentFile()));
    }
    
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap 상세 정보 조회 Unit Test
     * @title : testGetHbBootstrapInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testGetHbBootstrapInfo() throws Exception{
        HbBootstrapVO expectInfo = expectBootstrapConfig();
        when(mockHbBootstrapService.getHbBootstrapInfo(anyInt(),anyString())).thenReturn(expectInfo);
        mockMvc.perform(get(BOOTSTRAP_CONFIG_INFO_URL,1,"Openstack").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(expectInfo.getId()))
        .andExpect(jsonPath("$.bootstrapConfigName").value(expectInfo.getBootstrapConfigName()))
        .andExpect(jsonPath("$.iaasType").value(expectInfo.getIaasType()))
        .andExpect(jsonPath("$.networkConfigInfo").value(expectInfo.getNetworkConfigInfo()))
        .andExpect(jsonPath("$.cpiConfigInfo").value(expectInfo.getCpiConfigInfo()))
        .andExpect(jsonPath("$.defaultConfigInfo").value(expectInfo.getDefaultConfigInfo()))
        .andExpect(jsonPath("$.resourceConfigInfo").value(expectInfo.getResourceConfigInfo()))
        .andExpect(jsonPath("$.deploymentFile").value(expectInfo.getDeploymentFile()));
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap 정보 저정 Unit Test
     * @title : testSaveBootstrapConfigInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testSaveBootstrapConfigInfo() throws Exception{
        String requestJson = mapper.writeValueAsString(setBootstrapInstallInfo());
        mockMvc.perform(put(BOOTSTRAP_CONFIG_INFO_SAVE_URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap 정보 삭제 Unit Test
     * @title : testDeleteBootstrap
     * @return : void
    *****************************************************************/
    @Test
    public void testDeleteBootstrap() throws Exception{
        String requestJson = mapper.writeValueAsString(setBootstrapInstallInfo());
        mockMvc.perform(delete(BOOTSTRAP_CONFIG_INFO_DELETE_URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : BOOTSTRAP 상세 조회 값 설정
     * @title : expectBootstrapConfig
     * @return : HbBootstrapVO
    *****************************************************************/
    private HbBootstrapVO expectBootstrapConfig(){
        HbBootstrapVO vo = new HbBootstrapVO();
        vo.setBootstrapConfigName("bootstrap-config");
        vo.setId(1);
        vo.setCpiConfigInfo("cpi-config");
        vo.setNetworkConfigInfo("network-config");
        vo.setDefaultConfigInfo("default-config");
        vo.setIaasType("Openstack");
        vo.setDeployLog("Done");
        vo.setResourceConfigInfo("resource-config");
        vo.setDeployLog("starting...");
        vo.setDeploymentFile("openstack-microbosh-1.yml");
        return vo;
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : BOOTSTRAP 조회 값 설정
     * @title : expectCpiConfigList
     * @return : List<HbBootstrapVO>
    *****************************************************************/
    private List<HbBootstrapVO> expectBootstrapConfigList() {
        List<HbBootstrapVO> list = new ArrayList<HbBootstrapVO>();
        HbBootstrapVO vo = new HbBootstrapVO();
        vo.setBootstrapConfigName("bootstrap-config");
        vo.setId(1);
        vo.setCpiConfigInfo("cpi-config");
        vo.setNetworkConfigInfo("network-config");
        vo.setDefaultConfigInfo("default-config");
        vo.setIaasType("Openstack");
        vo.setDeployLog("Done");
        vo.setResourceConfigInfo("resource-config");
        vo.setDeployLog("starting...");
        vo.setDeploymentFile("openstack-microbosh-1.yml");
        list.add(vo);
        return list;
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : BOOTSTRAP 정보 저장 값 설정
     * @title : setBootstrapInstallInfo
     * @return : void
    *****************************************************************/
    private HbBootStrapDeployDTO setBootstrapInstallInfo(){
        HbBootStrapDeployDTO dto = new HbBootStrapDeployDTO();
        dto.setDeploymentFile("openstack-micro-bosh.yml");
        dto.setCpiConfigInfo("cpi-config");
        dto.setDefaultConfigInfo("default-config");
        dto.setDeployLog("done");
        dto.setDeployStatus("processing");
        dto.setIaasType("Openstack");
        dto.setNetworkConfigInfo("network-config");
        dto.setResourceConfigInfo("resource-config");
        dto.setId("1");
        dto.setBootstrapConfigName("bootstrap-config");
        return dto;
    }
    
}
