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
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapNetworkConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto.HbBootstrapNetworkConfigDTO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.service.HbBootstrapNetworkConfigService;
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
public class HbBootstrapNetworkConfigControllerUnitTest extends BaseControllerUnitTest {
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    
    @InjectMocks private HbBootstrapNetworkConfigController mockHbBootstrapNetworkConfigController;
    @Mock private HbBootstrapNetworkConfigService mockHbBootstrapNetworkConfigService;
    
    
    final static String BOOTSTRAP_NETWORK_CONFIG_VIEW_URL = "/deploy/hbBootstrap/networkConfig";
    final static String BOOTSTRAP_NETWORK_CONFIG_INFO_LIST_URL = "/deploy/hbBootstrap/network/list";
    final static String BOOTSTRAP_NETWORK_CONFIG_INFO_SAVE_URL = "/deploy/hbBootstrap/network/save";
    final static String BOOTSTRAP_NETWORK_CONFIG_INFO_DELETE_URL = "/deploy/hbBootstrap/network/delete";
    
    /****************************************************************
     * @project : Paas 이종 클라우드 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockHbBootstrapNetworkConfigController).build();
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid Bootstrap 네트워크 정보 관리 이동 Unit Test
     * @title : testGoNetworkConfig
     * @return : void
    *****************************************************************/
    @Test
    public void testGoNetworkConfig() throws Exception{
        mockMvc.perform(get(BOOTSTRAP_NETWORK_CONFIG_VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/hbdeploy/deploy/bootstrap/hbBootstrapNetworkConfig"));
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid Bootstrap 네트워크 정보 목록 조회 Unit Test
     * @title : HbBootstrapNetworkConfigVO
     * @return : void
    *****************************************************************/
    @Test
    public void testGetNetworkConfigInfoList() throws Exception{
        List<HbBootstrapNetworkConfigVO> expectList = expectNetworkConfigList();
        when(mockHbBootstrapNetworkConfigService.getNetworkConfigInfoList()).thenReturn(expectList);
        mockMvc.perform(get(BOOTSTRAP_NETWORK_CONFIG_INFO_LIST_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].recid").value(expectList.get(0).getRecid()))
        .andExpect(jsonPath("$.records[0].subnetId").value(expectList.get(0).getSubnetId()))
        .andExpect(jsonPath("$.records[0].privateStaticIp").value(expectList.get(0).getPrivateStaticIp()))
        .andExpect(jsonPath("$.records[0].subnetRange").value(expectList.get(0).getSubnetRange()))
        .andExpect(jsonPath("$.records[0].subnetGateway").value(expectList.get(0).getSubnetGateway()))
        .andExpect(jsonPath("$.records[0].subnetDns").value(expectList.get(0).getSubnetDns()))
        .andExpect(jsonPath("$.records[0].publicStaticIp").value(expectList.get(0).getPublicStaticIp()))
        .andExpect(jsonPath("$.records[0].createUserId").value(expectList.get(0).getCreateUserId()))
        .andExpect(jsonPath("$.records[0].updateUserId").value(expectList.get(0).getUpdateUserId()));
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap 네트워크 정보 저장 Unit Test
     * @title : testSaveNetworkConfigInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testSaveNetworkConfigInfo() throws Exception {
        String requestJson = mapper.writeValueAsString(setNetworkConfigInfo());
        mockMvc.perform(put(BOOTSTRAP_NETWORK_CONFIG_INFO_SAVE_URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap 네트워크 정보 삭제 Unit Test
     * @title : testDeleteNetworkConfigInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testDeleteNetworkConfigInfo() throws Exception{
        String requestJson = mapper.writeValueAsString(setNetworkConfigInfo());
        mockMvc.perform(delete(BOOTSTRAP_NETWORK_CONFIG_INFO_DELETE_URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNoContent());
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap 네트워크 정보 입력 값 설정
     * @title : setCpiConfigInfo
     * @return : HbBootstrapCpiConfigDTO
    *****************************************************************/
    private HbBootstrapNetworkConfigDTO setNetworkConfigInfo() {
    	HbBootstrapNetworkConfigDTO dto = new HbBootstrapNetworkConfigDTO();
        dto.setIaasType("AWS");
        dto.setId("1");
        dto.setNetworkConfigName("NetworkConfigName");
        dto.setSubnetId("bosh-snet");
        dto.setPrivateStaticIp("192.168.10.12");
        dto.setSubnetRange("192.168.10.0/24");
        dto.setSubnetGateway("172.16.100.1");
        dto.setSubnetDns("8.8.8.8");
        dto.setPublicStaticIp("172.16.100.23");
        
        dto.getPublicStaticIp();
        dto.getSubnetDns();
        dto.getSubnetGateway();
        dto.getSubnetRange();
        dto.getPrivateStaticIp();
        dto.getSubnetId();
        dto.getIaasType();
        dto.getId();
        dto.getNetworkConfigName();
        return dto;
    }

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap 네트워크 정보 목록 결과 값 설정
     * @title : expectNetworkConfigList
     * @return : List<HbBootstrapNetworkConfigVO>
    *****************************************************************/
    private List<HbBootstrapNetworkConfigVO> expectNetworkConfigList() {
        List<HbBootstrapNetworkConfigVO> list = new ArrayList<HbBootstrapNetworkConfigVO>();
        HbBootstrapNetworkConfigVO vo = new HbBootstrapNetworkConfigVO();
        vo.setIaasType("Openstack");
        vo.setCreateUserId("admin");
        vo.setNetworkConfigName("bosh");
        vo.setSubnetId("bosh-snet");
        vo.setPrivateStaticIp("192.168.10.12");
        vo.setSubnetRange("192.168.10.0/24");
        vo.setSubnetGateway("172.16.100.1");
        vo.setSubnetDns("8.8.8.8");
        vo.setPublicStaticIp("172.16.100.23");
        
        vo.getPublicStaticIp();
        vo.getSubnetDns();
        vo.getSubnetGateway();
        vo.getSubnetRange();
        vo.getPrivateStaticIp();
        vo.getPublicStaticIp();
        vo.getSubnetId();
        vo.getIaasType();
        vo.getCreateUserId();
        vo.getNetworkConfigName();
        list.add(vo);
        return list;
    }
}
