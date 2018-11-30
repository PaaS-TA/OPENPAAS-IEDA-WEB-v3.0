package org.openpaas.ieda.iaasDashboard.openstackMgnt.web.floatingIp.service;

import static org.junit.Assert.assertEquals;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.api.floatingIp.OpenstackFloatingIpMgntApiService;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.web.floatingIp.dto.OpenstackFloatingIpMgntDTO;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.web.floatingIp.service.OpenstackFloatingIpMgntService;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.openstack4j.model.compute.FloatingIP;
import org.openstack4j.model.network.Network;
import org.openstack4j.openstack.compute.domain.NovaFloatingIP.FloatingIPConcreteBuilder;
import org.openstack4j.openstack.networking.domain.NeutronNetwork;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class OpenstackFloatingIpMgntServiceUnitTest {
    private Principal principal = null;
    
    @InjectMocks OpenstackFloatingIpMgntService mockOpenstackFloatingIpMgntService;
    @Mock CommonIaasService mockCommonIaasService;
    @Mock MessageSource mockMessageSource;
    @Mock OpenstackFloatingIpMgntApiService mockOpenstackFloatingIpMgntApiService;
    
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
      * @description : OPENSTACK Floating IP 목록 조회 TEST
      * @title : testgetOpenstackFloatingIpInfoList
      * @return : void
      ***************************************************/
     @Test
     public void testgetOpenstackFloatingIpInfoList(){
         getOpenstackAccountInfo();
         List<? extends FloatingIP> expectList = setResultFloatingIpList();
         doReturn(expectList).when(mockOpenstackFloatingIpMgntApiService).getOpenstackFloatingIpInfoListApiFromOpenstack(any());
         when(mockOpenstackFloatingIpMgntApiService.getOpenstackInstanceName(any(), anyString())).thenReturn("instanceName");
         List<HashMap<String, Object>> resultList = mockOpenstackFloatingIpMgntService.getOpenstackFloatingIpInfoList(principal, 1);
         assertEquals(resultList.get(0).get("ipAddress"), expectList.get(0).getFloatingIpAddress());
         assertEquals(resultList.get(0).get("pool"), expectList.get(0).getPool());
         assertEquals(resultList.get(0).get("instanceName"), "instanceName");
     }
     
     /***************************************************
      * @project : OPENSTACK 관리 대시보드
      * @description : OPENSTACK Floating IP 할당 TEST
      * @title : testSaveOpenstackFloatingIpInfo
      * @return : void
      ***************************************************/
      @Test
      public void testSaveOpenstackFloatingIpInfo(){
          getOpenstackAccountInfo();
          OpenstackFloatingIpMgntDTO dto = setFlotingIpInfo();
          mockOpenstackFloatingIpMgntService.saveFloatingIpInfo(dto,principal);
      }
      
      /***************************************************
      * @project : OPENSTACK 관리 대시보드
      * @description : OPENSTACK Floating IP 할당 시 Pool 목록 조회 
      * @title : testGetOpenstackPoolInfoList
      * @return : List<String>
      ***************************************************/
      @Test
      public void testGetOpenstackPoolInfoList(){
          getOpenstackAccountInfo();
          List<? extends Network> pools = setPoolList();
          doReturn(pools).when(mockOpenstackFloatingIpMgntApiService).getOpenstackPoolInfoListApiFromOpenstack(any());
          List<? extends Network> poolList = mockOpenstackFloatingIpMgntApiService.getOpenstackPoolInfoListApiFromOpenstack(any());
          List<String> resultList = mockOpenstackFloatingIpMgntService.getOpenstackPoolInfoList(principal, 1);
          assertEquals(poolList.get(0).getName(), resultList.get(0));
      }
      
      /***************************************************
       * @project : OPENSTACK 관리 대시보드
       * @description : OPENSTACK Pool 목록 설정 
       * @title : setPoolList
       * @return : List<String>
       ***************************************************/
      private List<? extends Network> setPoolList() {
          List<Network> pools = new ArrayList<Network>();
          Network network = new NeutronNetwork();
          network.toBuilder().name("networkName");
          network.toBuilder().isRouterExternal(true);
          pools.add(network.toBuilder().build());
          return pools;
      }

     /***************************************************
      * @project : OPENSTACK 관리 대시보드
      * @description : OPENSTACK Floating IP  정보 설정 
      * @title : setFlotingIpInfo
      * @return : OpenstackFloatingIpMgntDTO 
      ***************************************************/
      public OpenstackFloatingIpMgntDTO setFlotingIpInfo() {
         OpenstackFloatingIpMgntDTO dto = new OpenstackFloatingIpMgntDTO();
         dto.setAccountId(1);
         dto.setPool("ex-aaa");
         dto.getAccountId();
         dto.getPool();
         return dto;
      }

    /***************************************************
      * @project : OPENSTACK 관리 대시보드
      * @description : OPENSTACK Floating IP 목록 조회 결과 값 설정
      * @title : setResultFloatingIpList
      * @return : List<? extends FloatingIP>
      ***************************************************/
      private List<? extends FloatingIP> setResultFloatingIpList() {
        List<FloatingIP> list = new ArrayList<FloatingIP>();
        FloatingIPConcreteBuilder builder = new FloatingIPConcreteBuilder();
        builder.floatingIpAddress("test");
        builder.pool("test");
        list.add(builder.build());
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