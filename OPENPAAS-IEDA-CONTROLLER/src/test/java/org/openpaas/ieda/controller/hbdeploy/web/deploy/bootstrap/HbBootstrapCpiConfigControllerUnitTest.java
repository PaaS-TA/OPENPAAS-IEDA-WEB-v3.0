package org.openpaas.ieda.controller.hbdeploy.web.deploy.bootstrap;

import static org.mockito.Matchers.any;
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
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapCpiConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto.HbBootstrapCpiConfigDTO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.service.HbBootstrapCpiConfigService;
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
public class HbBootstrapCpiConfigControllerUnitTest extends BaseControllerUnitTest {
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    
    @InjectMocks private HbBootstrapCpiConfigController mockHbBootstrapCpiConfigController;
    @Mock private HbBootstrapCpiConfigService mockHbBootstrapCpiConfigService;
    
    
    final static String BOOTSTRAP_CPI_CONFIG_VIEW_URL = "/deploy/hbBootstrap/cpiConfig";
    final static String BOOTSTRAP_CPI_CONFIG_INFO_LIST_URL = "/deploy/hbBootstrap/cpi/list";
    final static String BOOTSTRAP_CPI_CONFIG_INFO_SAVE_URL = "/deploy/hbBootstrap/cpi/save";
    final static String BOOTSTRAP_CPI_CONFIG_INFO_DELETE_URL = "/deploy/hbBootstrap/cpi/delete";
    
    /****************************************************************
     * @project : Paas 이종 클라우드 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockHbBootstrapCpiConfigController).build();
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap CPI 이동 Unit Test
     * @title : testGoHbBootstrapCpiConfig
     * @return : void
    *****************************************************************/
    @Test
    public void MockitoAnnotations() throws Exception{
        mockMvc.perform(get(BOOTSTRAP_CPI_CONFIG_VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/hbdeploy/deploy/bootstrap/hbBootstrapCpiConfig"));
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap CPI 목록 정보 조회 Unit Test
     * @title : testGetCpiConfigInfoList
     * @return : void
    *****************************************************************/
    @Test
    public void testGetCpiConfigInfoList() throws Exception{
        List<HbBootstrapCpiConfigVO> expectList = expectCpiConfigList();
        when(mockHbBootstrapCpiConfigService.getCpiConfigInfoList()).thenReturn(expectList);
        mockMvc.perform(get(BOOTSTRAP_CPI_CONFIG_INFO_LIST_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].recid").value(expectList.get(0).getRecid()))
        .andExpect(jsonPath("$.records[0].cpiInfoId").value(expectList.get(0).getCpiInfoId()))
        .andExpect(jsonPath("$.records[0].iaasType").value(expectList.get(0).getIaasType()))
        .andExpect(jsonPath("$.records[0].iaasConfigId").value(expectList.get(0).getIaasConfigId()))
        .andExpect(jsonPath("$.records[0].cpiName").value(expectList.get(0).getCpiName()))
        .andExpect(jsonPath("$.records[0].commonAccessUser").value(expectList.get(0).getCommonAccessUser()))
        .andExpect(jsonPath("$.records[0].commonTenant").value(expectList.get(0).getCommonTenant()))
        .andExpect(jsonPath("$.records[0].commonProject").value(expectList.get(0).getCommonProject()))
        .andExpect(jsonPath("$.records[0].openstackVersion").value(expectList.get(0).getOpenstackVersion()))
        .andExpect(jsonPath("$.records[0].commonSecurityGroup").value(expectList.get(0).getCommonSecurityGroup()))
        .andExpect(jsonPath("$.records[0].commonKeypairName").value(expectList.get(0).getCommonKeypairName()))
        .andExpect(jsonPath("$.records[0].commonKeypairPath").value(expectList.get(0).getCommonKeypairPath()))
        .andExpect(jsonPath("$.records[0].commonAvailabilityZone").value(expectList.get(0).getCommonAvailabilityZone()))
        .andExpect(jsonPath("$.records[0].iaasConfigAlias").value(expectList.get(0).getIaasConfigAlias()))
        .andExpect(jsonPath("$.records[0].createUserId").value(expectList.get(0).getCreateUserId()))
        .andExpect(jsonPath("$.records[0].updateUserId").value(expectList.get(0).getUpdateUserId()));
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap CPI 정보 저장 Unit Test
     * @title : testSaveCpiConfigInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testSaveCpiConfigInfo() throws Exception {
        String requestJson = mapper.writeValueAsString(setCpiConfigInfo());
        when(mockHbBootstrapCpiConfigService.saveCpiConfigInfo(any(), any())).thenReturn(expectCpiConfigInfo());
        mockMvc.perform(put(BOOTSTRAP_CPI_CONFIG_INFO_SAVE_URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.cpiInfoId").value(1))
        .andExpect(jsonPath("$.iaasType").value("AWS"))
        .andExpect(jsonPath("$.iaasConfigId").value(1))
        .andExpect(jsonPath("$.cpiName").value("cpi_test"));
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap CPI 정보 삭제 Unit Test
     * @title : testDeleteCpiConfigInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testDeleteCpiConfigInfo() throws Exception{
        String requestJson = mapper.writeValueAsString(setCpiConfigInfo());
        mockMvc.perform(delete(BOOTSTRAP_CPI_CONFIG_INFO_DELETE_URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNoContent());

    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap CPI 정보 입력 값 설정
     * @title : setCpiConfigInfo
     * @return : HbBootstrapCpiConfigDTO
    *****************************************************************/
    private HbBootstrapCpiConfigDTO setCpiConfigInfo() {
        HbBootstrapCpiConfigDTO dto = new HbBootstrapCpiConfigDTO();
        dto.setCpiInfoId("1");
        dto.setIaasType("AWS");
        dto.setIaasConfigId("1");
        dto.setCpiName("cpi_test");
        
        dto.getCpiInfoId();
        dto.getIaasConfigId();
        dto.getIaasType();
        dto.getCpiName();
        return dto;
    }

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap CPI 정보 결과 값 설정
     * @title : expectCpiConfigInfo
     * @return : HbBootstrapCpiConfigVO
    *****************************************************************/
    private HbBootstrapCpiConfigVO expectCpiConfigInfo() {
        HbBootstrapCpiConfigVO vo = new HbBootstrapCpiConfigVO();
        vo.setCommonAccessUser("test");
        vo.setCommonAvailabilityZone("test");
        vo.setCommonKeypairName("test");
        vo.setCommonKeypairPath("test.pem");
        vo.setCommonProject("admin");
        vo.setCommonSecurityGroup("test");
        vo.setIaasType("AWS");
        vo.setCommonTenant("test");
        vo.setCpiInfoId(1);
        vo.setRecid(1);
        vo.setCpiName("cpi_test");
        vo.setOpenstackVersion("v2");
        vo.setIaasConfigAlias("openstack-config");
        vo.setIaasConfigId(1);
        vo.setCreateUserId("admin");
        vo.setUpdateUserId("admin");
        return vo;
    }

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap CPI 목록 정보 결과 값 설정
     * @title : expectCpiConfigList
     * @return : List<HbBootstrapCpiConfigVO>
    *****************************************************************/
    private List<HbBootstrapCpiConfigVO> expectCpiConfigList() {
        List<HbBootstrapCpiConfigVO> list = new ArrayList<HbBootstrapCpiConfigVO>();
        HbBootstrapCpiConfigVO vo = new HbBootstrapCpiConfigVO();
        vo.setCommonAccessUser("test");
        vo.setCommonAvailabilityZone("test");
        vo.setCommonKeypairName("test");
        vo.setCommonKeypairPath("test.pem");
        vo.setCommonProject("admin");
        vo.setCommonSecurityGroup("test");
        vo.setIaasType("AWS");
        vo.setCommonTenant("test");
        vo.setCpiInfoId(1);
        vo.setRecid(1);
        vo.setCpiName("cpi_test");
        vo.setOpenstackVersion("v2");
        vo.setIaasConfigAlias("openstack-config");
        vo.setIaasConfigId(1);
        vo.setCreateUserId("admin");
        vo.setUpdateUserId("admin");
        
        vo.getIaasType();
        vo.getCommonAccessUser();
        vo.getCommonAvailabilityZone();
        vo.getCommonKeypairName();
        vo.getCommonKeypairPath();
        vo.getCommonProject();
        vo.getCommonSecurityGroup();
        vo.getCommonTenant();
        vo.getCpiInfoId();
        vo.getRecid();
        vo.getCpiName();
        vo.getCreateUserId();
        vo.getUpdateUserId();
        vo.getIaasConfigAlias();
        vo.getIaasConfigId();
        vo.getOpenstackVersion();
        list.add(vo);
        return list;
    }
    
    
}
