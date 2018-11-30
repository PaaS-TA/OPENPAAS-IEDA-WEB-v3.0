package org.openpaas.ieda.controller.hbdeploy.web.deploy.bootstrap;

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
import org.openpaas.ieda.common.BaseControllerUnitTest;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapCredentialConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapNetworkConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto.HbBootstrapCredentialConfigDTO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.service.HbBootstrapCredentialConfigService;
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
public class HbBootstrapCredentialConfigControllerUnitTest extends BaseControllerUnitTest {
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    
    @InjectMocks private HbBootstrapCredentialConfigController mockHbBootstrapCredentialConfigController;
    @Mock private HbBootstrapCredentialConfigService mockHbBootstrapCredentialConfigService;
    @Mock private HbBootstrapNetworkConfigService mockHbBootstrapNetworkConfigService;
    
    final static String BOOTSTRAP_CREDENTIAL_CONFIG_VIEW_URL = "/deploy/hbBootstrap/credentialConfig";
    final static String BOOTSTRAP_CREDENTIAL_CONFIG_INFO_LIST_URL = "/deploy/hbBootstrap/credential/list";
    final static String BOOTSTRAP_CREDENTIAL_CONFIG_INFO_SAVE_URL = "/deploy/hbBootstrap/credential/save";
    final static String BOOTSTRAP_CREDENTIAL_CONFIG_INFO_DELETE_URL = "/deploy/hbBootstrap/credential/delete";
    final static String BOOTSTRAP_CREDENTIAL_NETWORK_INFO_LIST_URL = "/deploy/hbBootstrap/credential/networkList/{iaasType}";
    final static String BOOTSTRAP_CREDENTIAL_NETWORK_INFO_DETAIL_URL = "/deploy/hbBootstrap/credential/networkInfo/{networkId}/{iaasType}";
    
    
    /****************************************************************
     * @project : Paas 이종 클라우드 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockHbBootstrapCredentialConfigController).build();
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid Bootstrap 인증서 정보 관리 이동 Unit Test
     * @title : testGoCredentialConfig
     * @return : void
    *****************************************************************/
    @Test
    public void testGoCredentialConfig() throws Exception{
        mockMvc.perform(get(BOOTSTRAP_CREDENTIAL_CONFIG_VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/hbdeploy/deploy/bootstrap/hbBootstrapCredentialConfig"));
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid Bootstrap 인증서 정보 목록 조회 Unit Test
     * @title : testGetCredentialConfigInfoList
     * @return : void
    *****************************************************************/
    @Test
    public void testGetCredentialConfigInfoList() throws Exception{
        List<HbBootstrapCredentialConfigVO> expectList = expectCredentialConfigList();
        when(mockHbBootstrapCredentialConfigService.getHbBootstrapCredentialConfigInfoList()).thenReturn(expectList);
        mockMvc.perform(get(BOOTSTRAP_CREDENTIAL_CONFIG_INFO_LIST_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].recid").value(expectList.get(0).getRecid()))
        .andExpect(jsonPath("$.records[0].credentialConfigName").value(expectList.get(0).getCredentialConfigName()))
        .andExpect(jsonPath("$.records[0].networkConfigName").value(expectList.get(0).getNetworkConfigName()))
        .andExpect(jsonPath("$.records[0].iaasType").value(expectList.get(0).getIaasType()))
        .andExpect(jsonPath("$.records[0].credentialKeyName").value(expectList.get(0).getCredentialKeyName()))
        .andExpect(jsonPath("$.records[0].directorPublicIp").value(expectList.get(0).getDirectorPublicIp()))
        .andExpect(jsonPath("$.records[0].directorPrivateIp").value(expectList.get(0).getDirectorPrivateIp()))
        .andExpect(jsonPath("$.records[0].createUserId").value(expectList.get(0).getCreateUserId()))
        .andExpect(jsonPath("$.records[0].updateUserId").value(expectList.get(0).getUpdateUserId()));
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap 인증서 정보 저장 Unit Test
     * @title : testSaveCredentialConfigInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testSaveCredentialConfigInfo() throws Exception {
        String requestJson = mapper.writeValueAsString(setCredentialConfigInfo());
        mockMvc.perform(put(BOOTSTRAP_CREDENTIAL_CONFIG_INFO_SAVE_URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap 인증서 정보 삭제 Unit Test
     * @title : testDeleteCredentialConfigInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testDeleteCredentialConfigInfo() throws Exception{
        String requestJson = mapper.writeValueAsString(setCredentialConfigInfo());
        mockMvc.perform(delete(BOOTSTRAP_CREDENTIAL_CONFIG_INFO_DELETE_URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNoContent());
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap 인증서 정보 저장 네트워크 목록 조회
     * @title : testGetNetworkConfigInfoList
     * @return : void
    *****************************************************************/
    @Test
    public void testGetNetworkConfigInfoList() throws Exception{
        List<HbBootstrapNetworkConfigVO> expectList = expectNetworkConfigList();
        when(mockHbBootstrapNetworkConfigService.getNetworkConfigInfoList(anyString())).thenReturn(expectList);
        mockMvc.perform(get(BOOTSTRAP_CREDENTIAL_NETWORK_INFO_LIST_URL, "openstack").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[0].recid").value(expectList.get(0).getRecid()))
        .andExpect(jsonPath("$.[0].subnetId").value(expectList.get(0).getSubnetId()))
        .andExpect(jsonPath("$.[0].privateStaticIp").value(expectList.get(0).getPrivateStaticIp()))
        .andExpect(jsonPath("$.[0].subnetRange").value(expectList.get(0).getSubnetRange()))
        .andExpect(jsonPath("$.[0].subnetGateway").value(expectList.get(0).getSubnetGateway()))
        .andExpect(jsonPath("$.[0].subnetDns").value(expectList.get(0).getSubnetDns()))
        .andExpect(jsonPath("$.[0].publicStaticIp").value(expectList.get(0).getPublicStaticIp()))
        .andExpect(jsonPath("$.[0].createUserId").value(expectList.get(0).getCreateUserId()))
        .andExpect(jsonPath("$.[0].updateUserId").value(expectList.get(0).getUpdateUserId()));
    }
    
    @Test
    public void testGetNetworkConfigInfo() throws Exception{
        HbBootstrapNetworkConfigVO expectVo = expectNetworkConfigInfo();
        when(mockHbBootstrapNetworkConfigService.getNetworkConfigInfo(anyString(), anyString())).thenReturn(expectVo);
        mockMvc.perform(get(BOOTSTRAP_CREDENTIAL_NETWORK_INFO_DETAIL_URL, "1", "openstack").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.recid").value(expectVo.getRecid()))
        .andExpect(jsonPath("$.subnetId").value(expectVo.getSubnetId()))
        .andExpect(jsonPath("$.privateStaticIp").value(expectVo.getPrivateStaticIp()))
        .andExpect(jsonPath("$.subnetRange").value(expectVo.getSubnetRange()))
        .andExpect(jsonPath("$.subnetGateway").value(expectVo.getSubnetGateway()))
        .andExpect(jsonPath("$.subnetDns").value(expectVo.getSubnetDns()))
        .andExpect(jsonPath("$.publicStaticIp").value(expectVo.getPublicStaticIp()))
        .andExpect(jsonPath("$.createUserId").value(expectVo.getCreateUserId()))
        .andExpect(jsonPath("$.updateUserId").value(expectVo.getUpdateUserId()));
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap 인증서 정보 입력 값 설정
     * @title : setCredentialConfigInfo
     * @return : HbBootstrapCredentialConfigDTO
    *****************************************************************/
    private HbBootstrapCredentialConfigDTO setCredentialConfigInfo() {
    	HbBootstrapCredentialConfigDTO dto = new HbBootstrapCredentialConfigDTO();
        dto.setIaasType("AWS");
        dto.setId("1");
        dto.setNetworkConfigName("network");
        dto.setCredentialConfigName("credential");
        dto.setCredentialKeyName("keyname");
        dto.setDirectorPrivateIp("10.0.0.6");
        dto.setDirectorPublicIp("13.123.552.123");
        
        dto.getDirectorPublicIp();
        dto.getDirectorPrivateIp();
        dto.getCredentialKeyName();
        dto.getCredentialConfigName();
        dto.getIaasType();
        dto.getId();
        dto.getNetworkConfigName();
        return dto;
    }

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap 인증서 정보 목록 결과 값 설정
     * @title : expectCredentialConfigList
     * @return : List<HbBootstrapCredentialConfigVO> 
    *****************************************************************/
    private List<HbBootstrapCredentialConfigVO> expectCredentialConfigList() {
        List<HbBootstrapCredentialConfigVO> list = new ArrayList<HbBootstrapCredentialConfigVO>();
        HbBootstrapCredentialConfigVO vo = new HbBootstrapCredentialConfigVO();
        vo.setIaasType("AWS");
        vo.setId(1);
        vo.setNetworkConfigName("network");
        vo.setCredentialConfigName("credential");
        vo.setCredentialKeyName("keyname");
        vo.setDirectorPrivateIp("10.0.0.6");
        vo.setDirectorPublicIp("13.123.552.123");
        
        vo.getDirectorPublicIp();
        vo.getDirectorPrivateIp();
        vo.getCredentialKeyName();
        vo.getCredentialConfigName();
        vo.getIaasType();
        vo.getId();
        vo.getNetworkConfigName();
        list.add(vo);
        return list;
    }
    
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap 네트워크 정보 목록 결과 값 설정
     * @title : expectCredentialConfigList
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
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap 네트워크 정보 목록 결과 값 설정
     * @title : expectCredentialConfigList
     * @return : List<HbBootstrapNetworkConfigVO>
    *****************************************************************/
    private HbBootstrapNetworkConfigVO expectNetworkConfigInfo() {
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
        return vo;
    }
    
}
