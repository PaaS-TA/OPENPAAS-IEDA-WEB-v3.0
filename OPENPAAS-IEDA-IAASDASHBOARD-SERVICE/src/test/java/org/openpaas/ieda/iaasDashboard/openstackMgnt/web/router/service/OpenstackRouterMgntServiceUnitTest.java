package org.openpaas.ieda.iaasDashboard.openstackMgnt.web.router.service;
  
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
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.api.network.OpenstackNetworkMgntApiService;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.api.router.OpenstackRouterMgntApiService;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.web.network.dao.OpenstackNetworkMgntVO;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.web.router.dao.OpenstackRouterMgntVO;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.web.router.dto.OpenstackRouterMgntDTO;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.web.router.service.OpenstackRouterMgntService;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.Port;
import org.openstack4j.model.network.Router;
import org.openstack4j.model.network.State;
import org.openstack4j.model.network.Subnet;
import org.openstack4j.openstack.networking.domain.NeutronNetwork;
import org.openstack4j.openstack.networking.domain.NeutronPort;
import org.openstack4j.openstack.networking.domain.NeutronRouter;
import org.openstack4j.openstack.networking.domain.NeutronSubnet;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
 
public class OpenstackRouterMgntServiceUnitTest {
    private Principal principal = null;
      
    @InjectMocks OpenstackRouterMgntService mockOpenstackRouterMgntService;
    @Mock CommonIaasService mockCommonIaasService;
    @Mock OpenstackRouterMgntApiService mockOpenstackRouterMgntApiService;
    @Mock OpenstackNetworkMgntApiService mockOpenstackNetworkMgntApiService;
    @Mock MessageSource mockMessageSource;
      
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
    * @description : OPENSTACK Router 목록 조회 TEST
    * @title : testGetOpenstackRouterInfoList
    * @return : void
    ***************************************************/
    @Test
    public void testGetOpenstackRouterInfoList(){
        getOpenstackAccountInfo(); 
        List<? extends Router> expectList = setResultRouterList();
        doReturn(expectList).when(mockOpenstackRouterMgntApiService).getOpenstackRouterInfoListApiFromOpenstack(any());
        when(mockOpenstackRouterMgntApiService.getRouterExternalNetworkInfoApiFromOpenstack(any(), anyInt())).thenReturn("gateway");
        List<OpenstackRouterMgntVO> resultList = mockOpenstackRouterMgntService.getOpenstackRouterInfoList(principal, 1);
        assertEquals(resultList.get(0).getRouteId(), expectList.get(0).getId());
        assertEquals(resultList.get(0).getStatus(),expectList.get(0).getStatus());
    }
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : OPENSTACK Router 목록 조회 정보 결과 값 설정
     * @title : setResultRouterList
     * @return : List<OpenstackRouterMgntVO>
     ***************************************************/
    public List<? extends Router> setResultRouterList(){
        NeutronRouter router = new NeutronRouter();
        List<Router> builder = new ArrayList<Router>();
        router.setId("id");
        router.setName("name");
        router.setTenantId("tasdasd");
        router.toBuilder().adminStateUp(true);
        builder.add(router);
        return builder;
    }
    /***************************************************
     * @project : OPENSTACK 관리 대시보드
     * @description : OPENSTACK Router 생성 불능시 익셉션 TEST
     * @title : testSaveOpenstackRouterBadRequest
     * @return : void
     ***************************************************/
     @Test(expected=CommonException.class)
     public void testSaveOpenstackRouterBadRequest(){
         getOpenstackAccountInfo();
         OpenstackRouterMgntVO vo = setRouterInfo();
         when (mockMessageSource.getMessage(any(), any(), any())).thenReturn("BadRequest");
         mockOpenstackRouterMgntService.createOpenstackRouter(vo, principal);
     }
     /***************************************************
      * @project : OPENSTACK 관리 대시보드
      * @description : OPENSTACK Router 중복 익셉션 TEST
      * @title : testSaveOpenstackRouterIdMatch
      * @return : void
      ***************************************************/
      @Test(expected=CommonException.class)
      public void testSaveOpenstackRouterIdMatch(){
          getOpenstackAccountInfo();
          OpenstackRouterMgntVO rvo = setRouterInfo();
          List<? extends Router> router = setOpenstackRouterListInfo();
          doReturn(router).when(mockOpenstackRouterMgntApiService).getOpenstackRouterInfoListApiFromOpenstack(any());
          when (mockMessageSource.getMessage(any(), any(), any())).thenReturn("Conflict");
          mockOpenstackRouterMgntService.createOpenstackRouter(rvo, principal);
      }
     /***************************************************
      * @project : OPENSTACK 관리 대시보드
      * @description : OPENSTACK Router 삭제 TEST
      * @title : testDeleteOpenstackSubnetkInfo
      * @return : void
      ***************************************************/
      @Test(expected=CommonException.class)
      public void testDeleteOpenstackRouterInfo(){
          getOpenstackAccountInfo();
          OpenstackRouterMgntVO vo = setRouterInfo();
          mockOpenstackRouterMgntService.deleteOpenstackRouter(vo, principal);
      }
     /***************************************************
     * @project : OPENSTACK 관리 대시보드
      * @description : OPENSTACK Router 인터페이스 목록 조회 TEST
      * @title : testGetOpenstackRouterInterfaceInfoList
      * @return : void
      ***************************************************/
      @Test
      public void testGetOpenstackRouterInterfaceInfoList(){
          getOpenstackAccountInfo();
          List<? extends Port> plist = setRouterInterfacePortInfo();
          doReturn(plist).when(mockOpenstackRouterMgntApiService).getOpenstackNetworkPortApiFromOpenstack(any());
          Router router = setRouterInterfaceRouteInfo();
          doReturn(router).when(mockOpenstackRouterMgntApiService).getRouterApiFromOpenstack(any(), anyString());
          Network network = setRouterNetworkInterfaceInfo();
          doReturn(network).when(mockOpenstackNetworkMgntApiService).getOpenstackNetworkDetailInfoApiFromOpenstack(any(), anyString());
          List<OpenstackRouterMgntDTO> resultList = mockOpenstackRouterMgntService.getOpenstackRouterInterfaceInfoList(principal, 1, "routerId");
          assertEquals(resultList.get(0).getRouteId(), router.getId());
          assertEquals(resultList.get(0).getSubnetId(), plist.get(0).getFixedIps().iterator().next().getSubnetId());
          assertEquals(resultList.get(0).getSubnetName(), plist.get(0).getName());
          assertEquals(resultList.get(0).getSubnetFixedIps(), plist.get(0).getFixedIps().iterator().next().getIpAddress());
          assertEquals(resultList.get(0).getSubnetStatus(), plist.get(0).getState());
          assertEquals(resultList.get(0).getSubnetType(), network.isShared());
          assertEquals(resultList.get(0).getSubnetAdminStateUp(), plist.get(0).isAdminStateUp());
      }
      /***************************************************
      * @project : OPENSTACK 관리 대시보드
      * @description : OPENSTACK Router 인터페이스 연결 서브넷ID 동일일 경우 익셉션 발생 TEST
      * @title : testAttachOpenstackRouterInterfaceSubnetIdMatch
      * @return : void
      ***************************************************/
      @Test(expected=CommonException.class)
      public void testAttachOpenstackRouterInterfaceSubnetIdMatch(){
          getOpenstackAccountInfo();
          List<? extends Port> plist = setRouterInterfacePortInfo();
          doReturn(plist).when(mockOpenstackRouterMgntApiService).getOpenstackNetworkPortApiFromOpenstack(any());
          Router router = setRouterInterfaceRouteInfo();
          doReturn(router).when(mockOpenstackRouterMgntApiService).getRouterApiFromOpenstack(any(), anyString());
          Network network = setRouterNetworkInterfaceInfo();
          doReturn(network).when(mockOpenstackNetworkMgntApiService).getOpenstackNetworkDetailInfoApiFromOpenstack(any(), anyString());
          OpenstackRouterMgntVO rvo = setRouterInfo();
          when (mockMessageSource.getMessage(any(), any() , any())).thenReturn("conflict");
          mockOpenstackRouterMgntService.attachOpenstackRouterInterface(rvo, principal);
      }
      /***************************************************
      * @project : OPENSTACK 관리 대시보드
      * @description : OPENSTACK Router 인터페이스 연결 실행 불능시 익셉션 발생 TEST
      * @title : testAttachOpenstackRouterInterfaceBadrequest
      * @return : void
      ***************************************************/
      @Test(expected=CommonException.class)
      public void testAttachOpenstackRouterInterfaceBadrequest(){
          getOpenstackAccountInfo();
          List<? extends Port> plist = setRouterInterfacePortInfo();
          doReturn(plist).when(mockOpenstackRouterMgntApiService).getOpenstackNetworkPortApiFromOpenstack(any());
          Router router = setRouterInterfaceRouteInfo();
          doReturn(router).when(mockOpenstackRouterMgntApiService).getRouterApiFromOpenstack(any(), anyString());
          Network network = setRouterNetworkInterfaceInfo();
          doReturn(network).when(mockOpenstackNetworkMgntApiService).getOpenstackNetworkDetailInfoApiFromOpenstack(any(), anyString());
          OpenstackRouterMgntVO svo = setRouterInterfaceInfo();
          when(mockMessageSource.getMessage(any(), any(), any())).thenReturn("Badrequest");
          mockOpenstackRouterMgntService.attachOpenstackRouterInterface(svo, principal);
      }
      /***************************************************
      * @project : OPENSTACK 관리 대시보드
      * @description : OPENSTACK Router 인터페이스 연결해제 서브넷ID를 못 찾을 경우 익셉션 발생 TEST
      * @title : testDetachOpenstackRouterInterfaceSubnetIdMatch
      * @return : void
      ***************************************************/
      @Test(expected=CommonException.class)
      public void testDetachOpenstackRouterInterfaceSubnetIdMatch(){
          getOpenstackAccountInfo();
          List<? extends Port> plist = setRouterInterfacePortInfo();
          doReturn(plist).when(mockOpenstackRouterMgntApiService).getOpenstackNetworkPortApiFromOpenstack(any());
          Router router = setRouterInterfaceRouteInfo();
          doReturn(router).when(mockOpenstackRouterMgntApiService).getRouterApiFromOpenstack(any(), anyString());
          Network network = setRouterNetworkInterfaceInfo();
          doReturn(network).when(mockOpenstackNetworkMgntApiService).getOpenstackNetworkDetailInfoApiFromOpenstack(any(), anyString());
          OpenstackRouterMgntVO dvo = setRouterInfo();
          when(mockMessageSource.getMessage(any(), any(), any())).thenReturn("Badrequest");
          mockOpenstackRouterMgntService.detachOpenstackRouterInterface(dvo, principal);
      }
    /***************************************************
     * @project : OPENSTACK 인프라 관리 대시보드
     * @description : Openstack 네트워크 서브넷 정보 리스트 TEST
     * @title : TestGetOpenstackNetworkSubnetInfo
     * @return : void
     ***************************************************/
      @Test
      public void TestGetOpenstackNetworkSubnetInfo(){
          getOpenstackAccountInfo();
          List<? extends Network> nList = setOpenstackNetworkListInfo();
          doReturn(nList).when(mockOpenstackNetworkMgntApiService).getOpenstackNetworkInfoListApiFromOpenstack(any());
          List<? extends Subnet> sList = setOpenstackSubnetListInfo();
          doReturn(sList).when(mockOpenstackRouterMgntApiService).getNetworkSubnetInfoApiFromOpenstack(any(), anyString());
          List<OpenstackNetworkMgntVO> resultList = mockOpenstackRouterMgntService.getOpenstackNetworkSubnetInfo(principal, 1);
          assertEquals(sList.get(0).getId(), resultList.get(0).getSubnetId());
          assertEquals(sList.get(0).getName(), resultList.get(0).getSubnetName());
      }
    /***************************************************
     * @project : OPENSTACK 인프라 관리 대시보드
     * @description : Openstack 네트워크 게이트웨이 연결 TEST
     * @title : TestSetOpenstackRouterGatewayAttach
     * @return : void
     ***************************************************/
      @Test(expected=CommonException.class)
      public void TestSetOpenstackRouterGatewayAttach(){
          getOpenstackAccountInfo();
          Router router = setRouterInterfaceRouteInfo();
          doReturn(router).when(mockOpenstackRouterMgntApiService).getRouterApiFromOpenstack(any(), anyString());
          OpenstackRouterMgntVO rvo = setRouterInfo();
          when(mockMessageSource.getMessage(any(), any(), any())).thenReturn("Badrequest");
          mockOpenstackRouterMgntService.setOpenstackRouterGatewayAttach(principal, rvo);
      }
      /***************************************************
       * @project : OPENSTACK 인프라 관리 대시보드
       * @description : Openstack 네트워크 게이트웨이 연결해제 TEST
       * @title : TestSetOpenstackRouterGatewayDetach
       * @return : void
       ***************************************************/
        @Test(expected=CommonException.class)
        public void TestSetOpenstackRouterGatewayDetach(){
            getOpenstackAccountInfo();
            Router router = setRouterInterfaceRouteInfo();
            doReturn(router).when(mockOpenstackRouterMgntApiService).getRouterApiFromOpenstack(any(), anyString());
            OpenstackRouterMgntVO rvo = setRouterInfo();
            when(mockMessageSource.getMessage(any(), any(), any())).thenReturn("Badrequest");
            mockOpenstackRouterMgntService.setOpenstackRouterGatewayDetach(principal, rvo);
        }
      /***************************************************
       * @project : OPENSTACK 인프라 관리 대시보드
       * @description : Openstack 네트워크 정보 리스트 TEST
       * @title : testGetOpenstackNetworkInfoList
       * @return : void
       ***************************************************/
        @Test
        public void testGetOpenstackNetworkInfoList(){
            getOpenstackAccountInfo();
            List<HashMap<String, String>> resultList = setOpenstackExteranlNetwork();
            doReturn(resultList).when(mockOpenstackRouterMgntApiService).getNetworkInfoApiFromOpenstack(any());
            List<HashMap<String, String>> expectList = mockOpenstackRouterMgntService.getOpenstackNetworkInfoList(principal, 1);
            assertEquals(resultList.get(0).get("id"), expectList.get(0).get("id"));
        }

        /***************************************************
        * @project : OPENSTACK 인프라 관리 대시보드
        * @description : Openstack 네트워크 Port 리스트 TEST
        * @title : TestGetOpenstackNetworkPortInfoList
        * @return : void
        ***************************************************/
        @Test
        public void TestGetOpenstackNetworkPortInfoList(){
            getOpenstackAccountInfo();
            mockOpenstackRouterMgntService.getOpenstackNetworkPortInfoList(principal, 1);
        }

        /***************************************************
         * @project : Openstack 인프라 관리 대시보드
         * @description : External Network 정보 설정
         * @title : setOpenstackExteranlNetwork
         * @return : List<HashMap<String,String>>
        ***************************************************/
        private List<HashMap<String, String>> setOpenstackExteranlNetwork(){
            List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("id", "testSubnetId");
            map.put("name", "testSubnetName");
            list.add(map);
            return list;
        }
        
      /***************************************************
       * @project : OPENSTACK 인프라 관리 대시보드
       * @description : Openstack 라우터 리스트 정보 설정
       * @title : setOpenstackRouterListInfo
       * @return : List<? extends Router>
       ***************************************************/
        private List<? extends Router> setOpenstackRouterListInfo(){
            NeutronRouter router = new NeutronRouter();
            List<Router> rList = new ArrayList<Router>();
            router.setId("router1a");
            router.setName("routerName");
            rList.add(router);
            return rList;
        }
    /***************************************************
     * @project : OPENSTACK 인프라 관리 대시보드
     * @description : Openstack 네트워크 서브넷 리스트 네트워크 정보 설정
     * @title : setOpenstackNetworkListInfo
     * @return : List<? extends Network>
     ***************************************************/
      private List<? extends Network> setOpenstackNetworkListInfo(){
          NeutronNetwork network = new NeutronNetwork();
          List<Network> netList = new ArrayList<Network>();
          network.setId("tnetId");
          netList.add(network);
          return netList;
      }
    /***************************************************
     * @project : OPENSTACK 인프라 관리 대시보드
     * @description : Openstack 네트워크 서브넷 리스트 서브넷 정보 설정
     * @title : setOpenstackSubnetListInfo
     * @return : List<? extends Subnet>
     ***************************************************/
      private List<? extends Subnet> setOpenstackSubnetListInfo(){
          NeutronSubnet subnet = new NeutronSubnet();
          List<Subnet> subList = new ArrayList<Subnet>();
          subnet.setId("tnetId");
          subnet.setName("testSubName");
          subList.add(subnet);
          return subList;
      }

     /***************************************************
     * @project : OPENSTACK 관리 대시보드
     * @description : OPENSTACK 네트워크 정보 설정
     * @title : setRouterInfo
     * @return : OpenstackRouterMgntVO
     ***************************************************/
     private OpenstackRouterMgntVO setRouterInfo() {
         OpenstackRouterMgntVO vo = new OpenstackRouterMgntVO();
         vo.setRecid(0);
         vo.setAccountId(1);
         vo.setRouterName("routerName");
         vo.setRouteId("router1a");
         vo.setExternalNetwork("externalNetwork");
         vo.setSubnetId("subnet1a");
         vo.setNetworkId("networkId");
         vo.getRecid();
         vo.getAccountId();
         vo.getRouterName();
         vo.getRouteId();
         vo.getExternalNetwork();
         return vo;
     }
     /***************************************************
      * @project : OPENSTACK 관리 대시보드
     * @description : OPENSTACK 네트워크 인터페이스(DTO) 목록 정보 설정
     * @title : setRouterInterfaceInfo
     * @return : List<OpenstackRouterMgntDTO>
     ***************************************************/
     private OpenstackRouterMgntVO setRouterInterfaceInfo(){
         OpenstackRouterMgntVO svo = new OpenstackRouterMgntVO();
         svo.setAccountId(1);
         svo.setRouteId("router1a");
         svo.setSubnetId("subnet2a");
         svo.setSubnetName("testSubnet");
         svo.setSubnetFixedIps("192.168.1.10");
         svo.setSubnetStatus(State.ACTIVE);
         svo.setSubnetAdminStateUp(true);
         svo.setRecid(0);
         return svo;
     }
     /***************************************************
      * @project : OPENSTACK 관리 대시보드
     * @description : OPENSTACK 네트워크 인터페이스(port) 목록 정보 설정
     * @title : setRouterInterfacePortInfo
     * @return : List<? extends Port>
     ***************************************************/
     private List<? extends Port> setRouterInterfacePortInfo(){
         NeutronPort port = new NeutronPort();
         List<Port> pList = new ArrayList<Port>();
         port.setId("portId1");
         port.setName("portName1");
         port.toBuilder().fixedIp("192.168.1.10", "subnet1a");
         port.toBuilder().state(State.ACTIVE);
         port.toBuilder().adminState(true);
         port.toBuilder().deviceId("router1a");
         port.toBuilder().networkId("networkId");
         pList.add(port);
         return pList;
     }
     /***************************************************
      * @project : OPENSTACK 관리 대시보드
     * @description : OPENSTACK 네트워크 인터페이스(router) 목록 정보 설정
     * @title : setRouterInterfaceRouteInfo
     * @return : Router
     ***************************************************/
     private Router setRouterInterfaceRouteInfo(){
         NeutronRouter router = new NeutronRouter();
         router.setId("router1a");
         return router;
     }
     /***************************************************
      * @project : OPENSTACK 관리 대시보드
     * @description : OPENSTACK 네트워크 인터페이스(network) 목록 정보 설정
     * @title : setRouterNetworkInterfaceInfo
     * @return : Network
     ***************************************************/
     private Network setRouterNetworkInterfaceInfo(){
         NeutronNetwork network = new NeutronNetwork();
         network.setId("network1a");
         return network;
     }
    /***************************************************
    * @project : 인프라 관리 대시보드
    * @description : OPENSTACK Account 조회 정보 결과 값 설정
    * @title : getAwsAccountInfo
    * @return : IaasAccountMgntVO
    ***************************************************/
    private IaasAccountMgntVO getOpenstackAccountInfo() {
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