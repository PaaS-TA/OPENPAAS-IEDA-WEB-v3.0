package org.openpaas.ieda.iaas.web.openstack;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.controller.iaasMgnt.openstackMgnt.web.network.OpenstackNetworkMgntController;
import org.openpaas.ieda.openstackMgnt.web.network.dao.OpenstackNetworkMgntVO;
import org.openpaas.ieda.openstackMgnt.web.network.dto.OpenstackNetworkMgntDTO;
import org.openpaas.ieda.openstackMgnt.web.network.service.OpenstackNetworkMgntService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class OpenstackNetworkMgntControllerUnitTest {
    
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    
    @InjectMocks OpenstackNetworkMgntController mockOpenstackNetworkMgntController;
    @Mock OpenstackNetworkMgntService mockOpenstackNetworkMgntService;
    
    final static String VIEW_URL = "/openstackMgnt/network";
    final static String NETWORK_LIST_URL = "/openstackMgnt/network/list/{accountId}";
    final static String NETWORK_DETAIL_VIEW_URL = "/openstackMgnt/network/save/detail/{accountId}/{networkId}";
    final static String NETWORK_SAVE_URL = "/openstackMgnt/network/save";
    final static String NETWORK_DELETE_URL = "/openstackMgnt/network/delete";
    final static String SUBNET_LIST_URL = "/openstackMgnt/subnet/list/{accountId}/{networkId}";
    final static String SUBNET_SAVE_URL = "/openstackMgnt/subnet/save";
    final static String SUBNET_DELETE_URL = "/openstackMgnt/subnet/delete";
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockOpenstackNetworkMgntController).build();
        getLoggined();
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 시큐리티 토큰 생성
    * @title : getLoggined
    * @return : Principal
    ***************************************************/
    public Principal getLoggined() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken("admin", "admin");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(auth);
        securityContext.getAuthentication().getPrincipal();
        return auth;
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 네트워크 관리 화면 이동 TEST
    * @title : testGoOpenstackNetworkMgnt
    * @return : void
    ***************************************************/
    @Test
    public void testGoOpenstackNetworkMgnt() throws Exception{
        mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("iaas/openstack/network/openstackNetworkMgnt"));
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 네트워크 목록 조회 TEST
    * @title : testGetOpenstackNetworkInfoList
    * @return : void
    ***************************************************/
    @Test
    public void testGetOpenstackNetworkInfoList() throws Exception{
        List<OpenstackNetworkMgntVO> expectList = setOpenstackNetworkListInfo();
        when(mockOpenstackNetworkMgntService.getOpenstackNetworkInfoList(any(), anyInt())).thenReturn(expectList);
        mockMvc.perform(get(NETWORK_LIST_URL, 1).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].accountId").value(1))
        .andExpect(jsonPath("$.records[0].adminStateUp").value(true))
        .andExpect(jsonPath("$.records[0].allocationPools").value("192.168.100.2 - 192.168.100.254"))
        .andExpect(jsonPath("$.records[0].cidrIpv4").value("192.168.100.0/24"))
        .andExpect(jsonPath("$.records[0].dhcpEnabled").value(true))
        .andExpect(jsonPath("$.records[0].dnsName").value("8.8.8.8"))
        .andExpect(jsonPath("$.records[0].ipVersion").value("IPv4"))
        .andExpect(jsonPath("$.records[0].networkId").value("networkdId"))
        .andExpect(jsonPath("$.records[0].networkName").value("bosh-net"))
        .andExpect(jsonPath("$.records[0].networkType").value("openstack"))
        .andExpect(jsonPath("$.records[0].providerNetwork").value("provider"))
        .andExpect(jsonPath("$.records[0].tenantId").value("tenanatId"))
        .andExpect(jsonPath("$.records[0].routeDestination").value("destination"))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 네트워크 상세 조회 TEST
    * @title : testGetOpenstackNetworkDetailInfo
    * @return : void
    ***************************************************/
    @Test
    public void testGetOpenstackNetworkDetailInfo() throws Exception{
        OpenstackNetworkMgntVO expectVo = setOpenstackNetworkDetailInfo();
        when(mockOpenstackNetworkMgntService.getOpenstackNetworkDetailInfo(any(), anyInt(), anyString())).thenReturn(expectVo);
        mockMvc.perform(get(NETWORK_DETAIL_VIEW_URL, 1, "networkdId").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accountId").value(1))
        .andExpect(jsonPath("$.adminStateUp").value(true))
        .andExpect(jsonPath("$.allocationPools").value("192.168.100.2 - 192.168.100.254"))
        .andExpect(jsonPath("$.cidrIpv4").value("192.168.100.0/24"))
        .andExpect(jsonPath("$.dhcpEnabled").value(true))
        .andExpect(jsonPath("$.dnsName").value("8.8.8.8"))
        .andExpect(jsonPath("$.ipVersion").value("IPv4"))
        .andExpect(jsonPath("$.networkId").value("networkdId"))
        .andExpect(jsonPath("$.networkName").value("bosh-net"))
        .andExpect(jsonPath("$.networkType").value("openstack"))
        .andExpect(jsonPath("$.providerNetwork").value("provider"))
        .andExpect(jsonPath("$.tenantId").value("tenanatId"))
        .andExpect(jsonPath("$.routeDestination").value("destination"))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 네트워크 생성 TEST 
    * @title : testSaveOpenstackNetworkInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveOpenstackNetworkInfo() throws Exception{
        OpenstackNetworkMgntDTO dto = setOpenstackSaveDto();
        mockMvc.perform(post(NETWORK_SAVE_URL).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isCreated());
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 네트워크 삭제 TEST
    * @title : testDeleteOpenstackNetworkInfo
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteOpenstackNetworkInfo() throws Exception{
        OpenstackNetworkMgntDTO dto = setOpenstackSaveDto();
        mockMvc.perform(delete(NETWORK_DELETE_URL).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 서브넷 목록 조회 TEST
    * @title : testGetOpenstackSubnetInfoList
    * @return : void
    ***************************************************/
    @Test
    public void testGetOpenstackSubnetInfoList() throws Exception{
        List<OpenstackNetworkMgntVO> expectList = setOpenstackNetworkListInfo();
        when(mockOpenstackNetworkMgntService.getOpenstackSubnetInfoList(any(), anyInt(), anyString())).thenReturn(expectList);
        mockMvc.perform(get(SUBNET_LIST_URL, 1, "networkId").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].accountId").value(1))
        .andExpect(jsonPath("$.records[0].adminStateUp").value(true))
        .andExpect(jsonPath("$.records[0].allocationPools").value("192.168.100.2 - 192.168.100.254"))
        .andExpect(jsonPath("$.records[0].cidrIpv4").value("192.168.100.0/24"))
        .andExpect(jsonPath("$.records[0].dhcpEnabled").value(true))
        .andExpect(jsonPath("$.records[0].dnsName").value("8.8.8.8"))
        .andExpect(jsonPath("$.records[0].ipVersion").value("IPv4"))
        .andExpect(jsonPath("$.records[0].networkId").value("networkdId"))
        .andExpect(jsonPath("$.records[0].networkName").value("bosh-net"))
        .andExpect(jsonPath("$.records[0].networkType").value("openstack"))
        .andExpect(jsonPath("$.records[0].providerNetwork").value("provider"))
        .andExpect(jsonPath("$.records[0].tenantId").value("tenanatId"))
        .andExpect(jsonPath("$.records[0].routeDestination").value("destination"))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 서브넷 생성 TEST
    * @title : testGetOpenstackSubnetInfoList
    * @return : void
    ***************************************************/
    @Test
    public void testSaveOpenstackSubnetkInfo() throws Exception{
        OpenstackNetworkMgntDTO dto = setOpenstackSaveDto();
        mockMvc.perform(post(SUBNET_SAVE_URL).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isCreated());
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 서브넷 삭제 TEST
    * @title : testDeleteOpenstackSubnetInfo
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteOpenstackSubnetInfo() throws Exception{
        OpenstackNetworkMgntDTO dto = setOpenstackSaveDto();
        mockMvc.perform(delete(SUBNET_DELETE_URL).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 네트워크 생성 JSON 값 설정
    * @title : setOpenstackSaveDto
    * @return : OpenstackNetworkMgntDTO
    ***************************************************/
    public OpenstackNetworkMgntDTO setOpenstackSaveDto() {
        OpenstackNetworkMgntDTO dto = new OpenstackNetworkMgntDTO();
        dto.setAccountId(1);
        dto.setAdminState(true);
        dto.setDnsNameServers("8.8.8.8");
        dto.setEnableDHCP(true);
        dto.setGatewayIp("192.168.100.1");
        dto.setIpVersion("IPv4");
        dto.setNetworkAddress("192.168.100.0/24");
        dto.setNetworkId("networkdId");
        dto.setNetworkName("networkName");
        dto.setSubnetId("subnetId");
        dto.setSubnetName("subnetName");
        dto.getAccountId();
        dto.getDnsNameServers();
        dto.getGatewayIp();
        dto.getNetworkAddress();
        dto.getIpVersion();
        dto.getNetworkId();
        dto.getNetworkName();
        dto.getSubnetId();
        dto.getSubnetName();
        return dto;
    }

    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 네트워크 상세 조회 기대 값 설정
    * @title : setOpenstackNetworkDetailInfo
    * @return : OpenstackNetworkMgntVO
    ***************************************************/
    public OpenstackNetworkMgntVO setOpenstackNetworkDetailInfo() {
        OpenstackNetworkMgntVO vo = new OpenstackNetworkMgntVO();
        vo.setAccountId(1);
        vo.setAdminStateUp(true);
        vo.setAllocationPools("192.168.100.2 - 192.168.100.254");
        vo.setCidrIpv4("192.168.100.0/24");
        vo.setDhcpEnabled(true);
        vo.setDnsName("8.8.8.8");
        vo.setGatewayIp("192.168.100.1");
        vo.setIpVersion("IPv4");
        vo.setNetworkId("networkdId");
        vo.setNetworkName("bosh-net");
        vo.setNetworkType("openstack");
        vo.setProviderNetwork("provider");
        vo.setRecid(1);
        vo.setTenantId("tenanatId");
        vo.setRouteDestination("destination");
        vo.setRouterExternal(true);
        vo.setSegId("segId");
        vo.setShared(true);
        vo.setSubnetId("subId");
        vo.setSubnetName("subentName");
        return vo;
    }

    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 네트워크 정보 목록 값 설정
    * @title : setOpenstackNetworkListInfo
    * @return : List<OpenstackNetworkMgntVO>
    ***************************************************/
    public List<OpenstackNetworkMgntVO> setOpenstackNetworkListInfo() {
        List<OpenstackNetworkMgntVO> expectList = new ArrayList<OpenstackNetworkMgntVO>();
        OpenstackNetworkMgntVO vo = new OpenstackNetworkMgntVO();
        vo.setAccountId(1);
        vo.setAdminStateUp(true);
        vo.setAllocationPools("192.168.100.2 - 192.168.100.254");
        vo.setCidrIpv4("192.168.100.0/24");
        vo.setDhcpEnabled(true);
        vo.setDnsName("8.8.8.8");
        vo.setGatewayIp("192.168.100.1");
        vo.setIpVersion("IPv4");
        vo.setNetworkId("networkdId");
        vo.setNetworkName("bosh-net");
        vo.setNetworkType("openstack");
        vo.setProviderNetwork("provider");
        vo.setRecid(1);
        vo.setTenantId("tenanatId");
        vo.setRouteDestination("destination");
        vo.setRouterExternal(true);
        vo.setSegId("segId");
        vo.setShared(true);
        vo.setSubnetId("subId");
        vo.setSubnetName("subentName");
        vo.getAccountId();
        vo.getAllocationPools();
        vo.getGatewayIp();
        vo.getNetworkId();
        vo.getDnsName();
        vo.getCidrIpv4();
        vo.getGatewayIp();
        vo.getIpVersion();
        vo.getNetworkType();
        vo.getNetworkId();
        vo.getNetworkName();
        vo.getNetworkType();
        vo.getProviderNetwork();
        vo.getRecid();
        vo.getRouteDestination();
        vo.getSegId();
        vo.getStatus();
        vo.getSubnetId();
        vo.getSubnetName();
        vo.getTenantId();
        vo.isAdminStateUp();
        vo.isDhcpEnabled();
        vo.isRouterExternal();
        vo.isShared();
        expectList.add(vo);
        return expectList;
    }
}
