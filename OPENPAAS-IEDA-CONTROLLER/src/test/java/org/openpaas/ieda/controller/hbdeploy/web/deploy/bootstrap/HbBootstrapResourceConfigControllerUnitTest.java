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
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapResourceConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto.HbBootstrapResourceConfigDTO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.service.HbBootstrapResourceConfigService;
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
public class HbBootstrapResourceConfigControllerUnitTest extends BaseControllerUnitTest{
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    
    @InjectMocks private HbBootstrapResourceConfigController mockHbBootstrapResourceConfigController;
    @Mock private HbBootstrapResourceConfigService mockHbBootstrapResourceConfigService;
    
    final static String BOOTSTRAP_RESOURCE_CONFIG_VIEW_URL = "/deploy/hbBootstrap/resourceConfig";
    final static String BOOTSTRAP_RESOURCE_CONFIG_INFO_LIST_URL = "/deploy/hbBootstrap/resourceConfig/list";
    final static String BOOTSTRAP_RESOURCE_CONFIG_INFO_SAVE_URL = "/deploy/hbBootstrap/resourceConfig/save";
    final static String BOOTSTRAP_RESOURCE_CONFIG_INFO_DELETE_URL = "/deploy/hbBootstrap/resourceConfig/delete";
    
    /****************************************************************
     * @project : Paas 이종 클라우드 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockHbBootstrapResourceConfigController).build();
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid Bootstrap 리소스 정보 관리 이동 Unit Test
     * @title : testGoResourceConfig
     * @return : void
    *****************************************************************/
    @Test
    public void testGoResourceConfig() throws Exception{
        mockMvc.perform(get(BOOTSTRAP_RESOURCE_CONFIG_VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/hbdeploy/deploy/bootstrap/hbBootstrapResourceConfig"));
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid Bootstrap 네트워크 정보 목록 조회 Unit Test
     * @title : HbBootstrapNetworkConfigVO
     * @return : void
    *****************************************************************/
    @Test
    public void testGetResourceConfigInfoList() throws Exception{
        List<HbBootstrapResourceConfigVO> expectList = expectResourceConfigList();
        when(mockHbBootstrapResourceConfigService.getResourceConfigInfoList()).thenReturn(expectList);
        mockMvc.perform(get(BOOTSTRAP_RESOURCE_CONFIG_INFO_LIST_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].recid").value(expectList.get(0).getRecid()))
        .andExpect(jsonPath("$.records[0].resourceConfigName").value(expectList.get(0).getResourceConfigName()))
        .andExpect(jsonPath("$.records[0].iaasType").value(expectList.get(0).getIaasType()))
        .andExpect(jsonPath("$.records[0].stemcellName").value(expectList.get(0).getStemcellName()))
        .andExpect(jsonPath("$.records[0].instanceType").value(expectList.get(0).getInstanceType()))
        .andExpect(jsonPath("$.records[0].vmPassword").value(expectList.get(0).getVmPassword()))
        .andExpect(jsonPath("$.records[0].createUserId").value(expectList.get(0).getCreateUserId()))
        .andExpect(jsonPath("$.records[0].updateUserId").value(expectList.get(0).getUpdateUserId()));
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap 리소스 정보 목록 결과 값 설정
     * @title : expectResourceConfigList
     * @return : List<HbBootstrapResourceConfigVO>
    *****************************************************************/
    private List<HbBootstrapResourceConfigVO> expectResourceConfigList() {
        List<HbBootstrapResourceConfigVO> list = new ArrayList<HbBootstrapResourceConfigVO>();
        HbBootstrapResourceConfigVO vo = new HbBootstrapResourceConfigVO();
        vo.setIaasType("Openstack");
        vo.setResourceConfigName("test-name");
        vo.setStemcellName("light-bosh-stemcell-3468-21-aws-xen-hvm.tgz");
        vo.setInstanceType("t2.medium");
        vo.setVmPassword("admin");
        vo.setCreateUserId("admin");
        
        vo.getIaasType();
        vo.getResourceConfigName();
        vo.getStemcellName();
        vo.getInstanceType();
        vo.getVmPassword();
        vo.getCreateUserId();
        list.add(vo);
        return list;
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap 리소스 정보 저장 Unit Test
     * @title : testSaveResourceConfigInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testSaveResourceConfigInfo() throws Exception {
        String requestJson = mapper.writeValueAsString(setResourceConfigInfo());
        mockMvc.perform(put(BOOTSTRAP_RESOURCE_CONFIG_INFO_SAVE_URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap 리소스 정보 입력 값 설정
     * @title : setResourceConfigInfo
     * @return : HbBootstrapResourceConfigDTO
    *****************************************************************/
    private HbBootstrapResourceConfigDTO setResourceConfigInfo() {
        HbBootstrapResourceConfigDTO dto  = new HbBootstrapResourceConfigDTO();
        dto.setIaasType("Openstack");
        dto.setId(2);
        dto.setResourceConfigName("test-name");
        dto.setStemcellName("light-bosh-stemcell-3468-21-aws-xen-hvm.tgz");
        dto.setInstanceType("t2.medium");
        dto.setVmPassword("admin");
        
        dto.getIaasType();
        dto.getId();
        dto.getResourceConfigName();
        dto.getStemcellName();
        dto.getInstanceType();
        dto.getVmPassword();
        
        return dto;
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap 리소스 정보 삭제 Unit Test
     * @title : testDeleteResourceConfigInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testDeleteResourceConfigInfo() throws Exception{
        String requestJson = mapper.writeValueAsString(setResourceConfigInfo());
        mockMvc.perform(delete(BOOTSTRAP_RESOURCE_CONFIG_INFO_DELETE_URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNoContent());
    }
}
