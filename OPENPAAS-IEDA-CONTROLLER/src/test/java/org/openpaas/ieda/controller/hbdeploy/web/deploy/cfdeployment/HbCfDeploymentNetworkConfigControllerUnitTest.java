package org.openpaas.ieda.controller.hbdeploy.web.deploy.cfdeployment;
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
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentNetworkConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dto.HbCfDeploymentNetworkConfigDTO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service.HbCfDeploymentNetworkConfigService;
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

public class HbCfDeploymentNetworkConfigControllerUnitTest {
    
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    
    @InjectMocks private HbCfDeploymentNetworkConfigController mockHbCfDeploymentNetworkConfigController;
    @Mock private HbCfDeploymentNetworkConfigService mockHbCfDeploymentNetworkConfigService;

    private final static String CF_NETWORK_VIEW_URL = "/deploy/hbCfDeployment/networkConfig";
    private final static String CF_NETWORK_LIST_URL = "/deploy/hbCfDeployment/networkConfig/list";
    private final static String CF_NETWORK_SAVE_URL = "/deploy/hbCfDeployment/networkConfig/save";
    private final static String CF_NETWORK_DELETE_URL = "/deploy/hbCfDeployment/networkConfig/delete";
    
    
    /****************************************************************
     * @project : Paas 이종 클라우드 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockHbCfDeploymentNetworkConfigController).build();
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 네트워크 정보 관리 화면 이동 Unit Test
     * @title : testgoDefaulConfig
     * @return : void
    *****************************************************************/
    @Test
    public void testGoNetworkConfig() throws Exception{
        mockMvc.perform(get(CF_NETWORK_VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/hbdeploy/deploy/cfDeployment/hbCfDeploymentNetworkConfig"));
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 네트워크 목록 정보 조회 Unit Test
     * @title : testGetNetworkConfigInfoList
     * @return : void
    *****************************************************************/
    @Test
    public void testGetNetworkConfigInfoList() throws Exception{
        List<HbCfDeploymentNetworkConfigVO> expectList = setCfNetworkConfigList();
        when(mockHbCfDeploymentNetworkConfigService.getNetworkConfigInfoList()).thenReturn(expectList);
        mockMvc.perform(get(CF_NETWORK_LIST_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].recid").value(expectList.get(0).getRecid()))
        .andExpect(jsonPath("$.records[0].id").value(expectList.get(0).getId()))
        .andExpect(jsonPath("$.records[0].seq").value(expectList.get(0).getSeq()))
        .andExpect(jsonPath("$.records[0].iaasType").value(expectList.get(0).getIaasType()))
        .andExpect(jsonPath("$.records[0].networkName").value(expectList.get(0).getNetworkName()))
        .andExpect(jsonPath("$.records[0].publicStaticIp").value(expectList.get(0).getPublicStaticIp()))
        .andExpect(jsonPath("$.records[0].availabilityZone").value(expectList.get(0).getAvailabilityZone()))
        .andExpect(jsonPath("$.records[0].securityGroup").value(expectList.get(0).getSecurityGroup()))
        .andExpect(jsonPath("$.records[0].subnetRange").value(expectList.get(0).getSubnetRange()))
        .andExpect(jsonPath("$.records[0].subnetDns").value(expectList.get(0).getSubnetDns()))
        .andExpect(jsonPath("$.records[0].subnetGateway").value(expectList.get(0).getSubnetGateway()));
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 네트워크 정보 저장 Unit Test
     * @title : testSaveNetworkConfigInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testSaveNetworkConfigInfo() throws Exception{
        String requestJson = mapper.writeValueAsString(setCfNetworkConfigInfo());
        mockMvc.perform(put(CF_NETWORK_SAVE_URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 네트워크 정보 삭제 Unit Test
     * @title : testDeleteNetworkConfigInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testDeleteNetworkConfigInfo() throws Exception{
        String requestJson = mapper.writeValueAsString(setCfNetworkConfigInfo());
        mockMvc.perform(delete(CF_NETWORK_DELETE_URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNoContent());
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 저장/삭제 요청 값 설정
     * @title : setCfNetworkConfigInfo
     * @return : HbCfDeploymentNetworkConfigDTO
    *****************************************************************/
    private HbCfDeploymentNetworkConfigDTO setCfNetworkConfigInfo() {
        HbCfDeploymentNetworkConfigDTO dto = new HbCfDeploymentNetworkConfigDTO();
        dto.setId(1);
        dto.setIaasType("Openstack");
        dto.setNetworkName("network-config");
        return dto;
    }

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 네트워크 목록 정보 조회 값 설정
     * @title : setCfNetworkConfigList
     * @return : List<HbCfDeploymentNetworkConfigVO>
    *****************************************************************/
    private List<HbCfDeploymentNetworkConfigVO> setCfNetworkConfigList() {
        List<HbCfDeploymentNetworkConfigVO> list = new ArrayList<HbCfDeploymentNetworkConfigVO>();
        HbCfDeploymentNetworkConfigVO vo = new HbCfDeploymentNetworkConfigVO();
        vo.setAvailabilityZone("us-west-1a");
        vo.setId(1);
        vo.setIaasType("Openstack");
        vo.setNetworkName("network-config");
        vo.setPublicStaticIp("172.16.xxx.xxx");
        vo.setSecurityGroup("seg");
        vo.setSeq(1);
        vo.setSubnetGateway("172.16.100.1");
        vo.setSubnetDns("8.8.8.8");
        vo.setSubnetRange("10.0.0.0/24");
        vo.setSubnetReservedFrom1("10.0.0.1");
        vo.setSubnetReservedTo1("10.0.0.10");
        list.add(vo);
        return list;
    }
}
