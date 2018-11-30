package org.openpaas.ieda.iaasDashboard.openstackMgnt.web.network.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.api.network.OpenstackNetworkMgntApiService;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.web.network.dao.OpenstackNetworkMgntVO;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.web.network.dto.OpenstackNetworkMgntDTO;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.web.network.service.OpenstackNetworkMgntService;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.network.IPVersionType;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.NetworkType;
import org.openstack4j.openstack.networking.domain.NeutronNetwork;
import org.openstack4j.openstack.networking.domain.NeutronNetwork.NetworkConcreteBuilder;
import org.openstack4j.openstack.networking.domain.NeutronSubnet;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class OpenstackNetworkMgntServiceUnitTest {
    private Principal principal = null;
    
    @InjectMocks OpenstackNetworkMgntService mockOpenstackNetworkMgntService;
    @Mock CommonIaasService mockCommonIaasService;
    @Mock MessageSource mockMessageSource;
    @Mock OpenstackNetworkMgntApiService mockOpenstackNetworkMgntApiService;
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        principal = getLoggined();
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
    * @project : OPENSTACK 관리 대시보드
    * @description : OPENSTACK 네트워크 목록 조회  TEST
    * @title : testGetOpenstackNetworkInfoList
    * @return : void
    ***************************************************/
    @Test
    public void testGetOpenstackNetworkInfoList(){
        getOpenstackAccountInfo();
        List<? extends Network> expectList = setResultNetworkList();
        doReturn(expectList).when(mockOpenstackNetworkMgntApiService).getOpenstackNetworkInfoListApiFromOpenstack(any());
        List<OpenstackNetworkMgntVO> resultList = mockOpenstackNetworkMgntService.getOpenstackNetworkInfoList(principal, 1);
        assertEquals(resultList.get(0).isAdminStateUp(), expectList.get(0).isAdminStateUp());
        assertEquals(resultList.get(0).isRouterExternal(), expectList.get(0).isRouterExternal());
        assertEquals(resultList.get(0).isShared(), expectList.get(0).isShared());
        assertEquals(resultList.get(0).getNetworkId(), expectList.get(0).getId());
    }
    
    /***************************************************
    * @project : OPENSTACK 관리 대시보드
    * @description : OPENSTACK 네트워크 상세 조회  TEST
    * @title : testGetOpenstackNetworkDetailInfo
    * @return : void
    ***************************************************/
    @Test
    public void testGetOpenstackNetworkDetailInfo(){
        getOpenstackAccountInfo();
        Network network = setResultNetworkInfo();
        when(mockOpenstackNetworkMgntApiService.getOpenstackNetworkDetailInfoApiFromOpenstack(any(), anyString())).thenReturn(network);
        OpenstackNetworkMgntVO resultVo =  mockOpenstackNetworkMgntService.getOpenstackNetworkDetailInfo(principal, 1, "networkId");
        assertEquals(resultVo.isAdminStateUp(), network.isAdminStateUp());
        assertEquals(resultVo.getNetworkName(), network.getName());
        assertEquals(resultVo.isShared(), network.isShared());
        assertEquals(resultVo.getTenantId(), network.getTenantId());
        assertEquals(resultVo.isRouterExternal(), network.isRouterExternal());
    }
    
    /***************************************************
    * @project : OPENSTACK 관리 대시보드
    * @description : OPENSTACK 네트워크 생성 IPv4 일 경우 TEST
    * @title : testSaveOpenstackNetworkIpv4Info
    * @return : void
    ***************************************************/
    @Test
    public void testSaveOpenstackNetworkIpv4Info(){
        getOpenstackAccountInfo();
        OpenstackNetworkMgntDTO dto = setNetworkInfo("ipv4");
        mockOpenstackNetworkMgntService.saveOpenstackNetworkInfo(dto, principal);
    }
    
    /***************************************************
    * @project : OPENSTACK 관리 대시보드
    * @description : OPENSTACK 네트워크 생성 IPv6 일 경우 TEST
    * @title : testSaveOpenstackNetworkIpv6Info
    * @return : void
    ***************************************************/
    @Test
    public void testSaveOpenstackNetworkIpv6Info(){
        getOpenstackAccountInfo();
        OpenstackNetworkMgntDTO dto = setNetworkInfo("ipv6");
        mockOpenstackNetworkMgntService.saveOpenstackNetworkInfo(dto, principal);
    }
    
    /***************************************************
    * @project : OPENSTACK 관리 대시보드
    * @description : OPENSTACK 네트워크 삭제 TEST
    * @title : testSaveOpenstackNetworkInfo
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteOpenstackNetworkInfo(){
        IaasAccountMgntVO vo = getOpenstackAccountInfo();
        OpenstackNetworkMgntDTO dto = setNetworkInfo("default");
        when(mockOpenstackNetworkMgntApiService.deleteOpenstackNetworkInfoApiFromOpenstack(vo, dto)).thenReturn(ActionResponse.actionSuccess());
        mockOpenstackNetworkMgntService.deleteOpenstackNetworkInfo(dto, principal);
    }
    
    /***************************************************
    * @project : OPENSTACK 관리 대시보드
    * @description : OPENSTACK 서브넷 목록 조회 TEST
    * @title : testGetOpenstackSubnetInfoList
    * @return : void
    ***************************************************/
    @Test
    public void testGetOpenstackSubnetInfoList(){
        List<NeutronSubnet> expectList = setResultSubnetList("default");
        doReturn(expectList).when(mockOpenstackNetworkMgntApiService).getOpenstackSubnetInfoListApiFromOpenstack(any(), anyString());
        List<OpenstackNetworkMgntVO> resultList = mockOpenstackNetworkMgntService.getOpenstackSubnetInfoList(principal, 1, "1");
        assertEquals(resultList.get(0).isDhcpEnabled(), expectList.get(0).isDHCPEnabled());
        assertEquals(resultList.get(0).getTenantId(), expectList.get(0).getTenantId());
        assertEquals(resultList.get(0).getNetworkId(), expectList.get(0).getNetworkId());
        assertEquals(resultList.get(0).getCidrIpv4(), expectList.get(0).getCidr());
        assertEquals(resultList.get(0).getGatewayIp(), expectList.get(0).getGateway());
        assertEquals(resultList.get(0).getSubnetName(), expectList.get(0).getName());
    }
    
    /***************************************************
    * @project : OPENSTACK 관리 대시보드
    * @description : OPENSTACK 서브넷 목록 조회 결과 값이 2개 이상 일 경우 TEST
    * @title : testGetOpenstackSubnetInfoListSize2
    * @return : void
    ***************************************************/
    @Test
    public void testGetOpenstackSubnetInfoListSize2(){
        List<NeutronSubnet> expectList = setResultSubnetList("size");
        doReturn(expectList).when(mockOpenstackNetworkMgntApiService).getOpenstackSubnetInfoListApiFromOpenstack(any(), anyString());
        List<OpenstackNetworkMgntVO> resultList = mockOpenstackNetworkMgntService.getOpenstackSubnetInfoList(principal, 1, "1");
        
        for(int i = 0; i < expectList.size(); i++){
            assertEquals(resultList.get(i).isDhcpEnabled(), expectList.get(i).isDHCPEnabled());
            assertEquals(resultList.get(i).getTenantId(), expectList.get(i).getTenantId());
            assertEquals(resultList.get(i).getNetworkId(), expectList.get(i).getNetworkId());
            assertEquals(resultList.get(i).getCidrIpv4(), expectList.get(i).getCidr());
            assertEquals(resultList.get(i).getGatewayIp(), expectList.get(i).getGateway());
            assertEquals(resultList.get(i).getSubnetName(), expectList.get(i).getName());
        }
    }
    
    /***************************************************
    * @project : OPENSTACK 관리 대시보드
    * @description : OPENSTACK 서브넷 생성 IPv4 일 경우 TEST
    * @title : testSaveOpenstackSubnetkIpv4Info
    * @return : void
    ***************************************************/
    @Test
    public void testSaveOpenstackSubnetkIpv4Info(){
        getOpenstackAccountInfo();
        OpenstackNetworkMgntDTO dto = setNetworkInfo("ipv4");
        mockOpenstackNetworkMgntService.saveOpenstackSubnetkInfo(dto, principal);
    }
    
    /***************************************************
    * @project : OPENSTACK 관리 대시보드
    * @description : OPENSTACK 서브넷 생성 IPv6 일 경우 TEST
    * @title : testSaveOpenstackSubnetkIpv6Info
    * @return : void
    ***************************************************/
    @Test
    public void testSaveOpenstackSubnetkIpv6Info(){
        getOpenstackAccountInfo();
        OpenstackNetworkMgntDTO dto = setNetworkInfo("ipv6");
        mockOpenstackNetworkMgntService.saveOpenstackSubnetkInfo(dto, principal);
    }
    
    /***************************************************
    * @project : OPENSTACK 관리 대시보드
    * @description : OPENSTACK 서브넷 삭제 TEST
    * @title : testSaveOpenstackSubnetkInfo
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteOpenstackSubnetInfo(){
        IaasAccountMgntVO vo = getOpenstackAccountInfo();
        OpenstackNetworkMgntDTO dto = setNetworkInfo("default");
        when(mockOpenstackNetworkMgntApiService.deleteOpenstackSubnetInfoApiFromOpenstack(vo, dto)).thenReturn(ActionResponse.actionSuccess());
        mockOpenstackNetworkMgntService.deleteOpenstackSubnetInfo(dto, principal);
    }
    
    /***************************************************
     * @project : OPENSTACK 관리 대시보드
    * @description : OPENSTACK 네트워크  정보 설정
    * @title : setSaveNetworkInfo
    * @return : List<NeutronSubnet>
    ***************************************************/
    public OpenstackNetworkMgntDTO setNetworkInfo(String type) {
        OpenstackNetworkMgntDTO dto = new OpenstackNetworkMgntDTO();
        dto.setAccountId(1);
        dto.setAdminState(true);
        dto.setDnsNameServers("8.8.8.8");
        dto.setEnableDHCP(true);
        dto.setGatewayIp("192.168.100.1");
        if("ipv4".equalsIgnoreCase(type)) dto.setIpVersion("IPv4");
        else if("ipv6".equalsIgnoreCase(type)) dto.setIpVersion("IPv6");
        dto.setNetworkAddress("192.168.100.0.24");
        dto.setNetworkId("networkId");
        dto.setNetworkName("networkName");
        dto.setSubnetId("subnetId");
        dto.setSubnetName("subnetName");
        dto.getAccountId();
        dto.isAdminState();
        dto.getDnsNameServers();
        dto.isEnableDHCP();
        dto.getGatewayIp();
        dto.getIpVersion();
        dto.getNetworkAddress();
        dto.getNetworkId();
        dto.getNetworkName();
        dto.getSubnetId();
        dto.getSubnetName();
        OpenstackNetworkMgntVO vo = new OpenstackNetworkMgntVO();
        vo.setStatus("ACTIVE");
        vo.setNetworkType("networkType");
        vo.getRecid();
        vo.getStatus();
        vo.getAccountId();
        vo.getProviderNetwork();
        vo.getSegId();
        vo.getAllocationPools();
        vo.getIpVersion();
        vo.getDnsName();
        vo.getRouteDestination();
        vo.getSubnetId();
        vo.getNetworkType();
        return dto;
    }

    /***************************************************
    * @project : OPENSTACK 관리 대시보드
    * @description : OPENSTACK 서브넷 목록 조회 결과 값 설정
    * @title : setResultNetworkInfo
    * @return : List<NeutronSubnet>
    ***************************************************/
    public Network setResultNetworkInfo() {
        Network network = new NeutronNetwork();
        network.toBuilder().adminStateUp(true);
        network.toBuilder().isRouterExternal(true);
        network.toBuilder().name("networkName");
        network.toBuilder().isShared(true);
        network.toBuilder().tenantId("tenantId");
        return network;
    }

    /***************************************************
    * @param string 
     * @project : OPENSTACK 관리 대시보드
    * @description : OPENSTACK 서브넷 목록 조회 결과 값 설정
    * @title : setResultSubnetList
    * @return : List<NeutronSubnet>
    ***************************************************/
    public List<NeutronSubnet> setResultSubnetList(String type){
        List<NeutronSubnet> neutronSubnets = new ArrayList<NeutronSubnet>();
        NeutronSubnet subnet = new NeutronSubnet();
        subnet.toBuilder().addDNSNameServer("8.8.8.8");
        subnet.toBuilder().addDNSNameServer("255.255.255.0");
        subnet.toBuilder().cidr("192.168.110.0/24");
        subnet.toBuilder().name("subnetName");
        subnet.toBuilder().enableDHCP(false);
        subnet.toBuilder().tenantId("tenantId");
        subnet.toBuilder().gateway("192.168.110.1");
        subnet.toBuilder().ipVersion(IPVersionType.V4);
        subnet.toBuilder().networkId("networkId");
        subnet.toBuilder().addHostRoute("192.168.110.0/24", "192.168.100.1");
        subnet.toBuilder().addHostRoute("192.168.120.0/24", "192.168.100.1");
        subnet.toBuilder().addPool("192.168.100.2", "192.168.100.254");
        subnet.toBuilder().addPool("192.168.120.2", "192.168.120.254");
        neutronSubnets.add(subnet);
        if("size".equalsIgnoreCase(type)){
            subnet = new NeutronSubnet();
            subnet.toBuilder().addDNSNameServer("8.8.8.8");
            subnet.toBuilder().cidr("192.168.120.0/24");
            subnet.toBuilder().name("subnetName");
            subnet.toBuilder().enableDHCP(false);
            subnet.toBuilder().tenantId("tenantId");
            subnet.toBuilder().gateway("192.168.120.1");
            subnet.toBuilder().ipVersion(IPVersionType.V4);
            subnet.toBuilder().networkId("networkId");
            subnet.toBuilder().addHostRoute("192.168.120.0/24", "192.168.120.1");
            subnet.toBuilder().addPool("192.168.120.2", "192.168.120.254");
            neutronSubnets.add(subnet);
        }
        return neutronSubnets;
    }
    
    /***************************************************
    * @project : OPENSTACK 관리 대시보드
    * @description : OPENSTACK 네트워크 목록 조회 결과 값 설정
    * @title : setResultNetworkList
    * @return : List<? extends Network>
    ***************************************************/
    public List<? extends Network> setResultNetworkList() {
        List<Network> list = new ArrayList<Network>();
        Network network = new NeutronNetwork();
        NetworkConcreteBuilder builder = new NetworkConcreteBuilder();
        builder.adminStateUp(true);
        builder.isRouterExternal(true);
        builder.networkType(NetworkType.VLAN);
        builder.networkType(NetworkType.FLAT).build().setTenantId("tenantId");
        builder.physicalNetwork("physicalNetwork").build().setName("networkName");
        builder.isShared(false).build().setId("networkId");
        network = builder.segmentId("segId").build();
        list.add(network);
        return list;
    }

     
    
    /***************************************************
    * @project : OPENSTACK 관리 대시보드
    * @description : OPENSTACK Account 조회 정보 결과 값 설정
    * @title : getAwsAccountInfo
    * @return : IaasAccountMgntVO
    ***************************************************/
    public IaasAccountMgntVO getOpenstackAccountInfo() {
        IaasAccountMgntVO vo = new IaasAccountMgntVO();
        vo.setAccountName("testAccountName");
        vo.setCreateUserId("admin");
        vo.setIaasType("openstack");
        vo.setCommonProject("bosh");
        vo.setCommonAccessSecret("commonSecret");
        vo.setCommonAccessUser("commonUser");
        when(mockCommonIaasService.getIaaSAccountInfo(any(), anyInt(), anyString())).thenReturn(vo);
        return vo;
    }
}
