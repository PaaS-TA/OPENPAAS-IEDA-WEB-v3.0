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
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.controller.iaasMgnt.openstackMgnt.web.router.OpenstackRouterMgntController;
import org.openpaas.ieda.openstackMgnt.web.network.dao.OpenstackNetworkMgntVO;
import org.openpaas.ieda.openstackMgnt.web.router.dao.OpenstackRouterMgntVO;
import org.openpaas.ieda.openstackMgnt.web.router.dto.OpenstackRouterMgntDTO;
import org.openpaas.ieda.openstackMgnt.web.router.service.OpenstackRouterMgntService;
import org.openstack4j.model.network.State;
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
public class OpenstackRouterMgntControllerUnitTest {

    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    
    @InjectMocks OpenstackRouterMgntController mockOpenstackRouterMgntController;
    @Mock OpenstackRouterMgntService mockOpenstackRouterMgntService;
    
    final static String VIEW_URL = "/openstackMgnt/router";
    final static String ROUTER_LIST_URL = "/openstackMgnt/router/list/{accountId}";
    //final static String ROUTER_DETAIL_VIEW_URL = "/openstackMgnt/router/save/detail/{accountId}/{routerId}";
    final static String ROUTER_SAVE_URL = "/openstackMgnt/router/create";
    final static String ROUTER_DELETE_URL = "/openstackMgnt/router/delete";
    final static String ROUTER_INTERFACE_LIST = "/openstackMgnt/router/interface/list/{accountId}/{routeId}";
    final static String ROUTER_INTERFACE_SUBNET_LIST = "/openstackMgnt/router/interface/list/{accountId}";
    final static String ROUTER_INTERFACE_SUBNET_ATTACH = "/openstackMgnt/router/interface/attach";
    final static String ROUTER_INTERFACE_SUBNET_DETACH = "/openstackMgnt/router/interface/detach";
    final static String ROUTER_GATEWAY_ATTACH = "/openstackMgnt/router/gateway/attach";
    final static String ROUTER_GATEWAY_DETACH = "/openstackMgnt/router/gateway/detach";
    final static String ROUTER_EXNETWORK_LIST = "/openstackMgnt/router/gateway/exnetlist/{accountId}";
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockOpenstackRouterMgntController).build();
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
    * @description : OPENSTACK Router 관리 화면 이동 TEST
    * @title : testGoOpenstackRouterMgnt
    * @return : void
    ***************************************************/
    @Test
    public void testGoOpenstackRouterMgnt() throws Exception{
        mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("iaas/openstack/router/openstackRouterMgnt"));
    }
    /***************************************************
     * @project : OPENSTACK 인프라 관리 대시보드
     * @description : OPENSTACK Router 목록 조회 TEST
     * @title : testGetOpenstackRouterInfoList
     * @return : void
     ***************************************************/
     @Test
     public void testGetOpenstackRouterInfoList() throws Exception{
         List<OpenstackRouterMgntVO> expectList = setOpenstackRouterListInfo();
         when(mockOpenstackRouterMgntService.getOpenstackRouterInfoList(any(), anyInt())).thenReturn(expectList);
         mockMvc.perform(get(ROUTER_LIST_URL, 1).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.records[0].accountId").value(1))
         .andExpect(jsonPath("$.records[0].routerName").value("routerName"))
         .andExpect(jsonPath("$.records[0].routeId").value("routeId"))
         .andExpect(jsonPath("$.records[0].externalNetwork").value("externalNetwork"))
         .andExpect(jsonPath("$.records[0].status").value(expectList.get(0).getStatus()))
         .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
     }
    /***************************************************
     * @project : OPENSTACK 인프라 관리 대시보드
     * @description : OPENSTACK Router 생성 TEST
     * @title : testCreateOpenstackRouter
     * @return : void
     ***************************************************/
     @Test
     public void testCreateOpenstackRouter() throws Exception{
         OpenstackRouterMgntDTO dto = setOpenstackSaveDto();
         mockMvc.perform(post(ROUTER_SAVE_URL).contentType(MediaType.APPLICATION_JSON)
                 .content(mapper.writeValueAsBytes(dto)))
         .andDo(MockMvcResultHandlers.print())
         .andExpect(MockMvcResultMatchers.status().isCreated());
     }
     /***************************************************
      * @project : OPENSTACK 인프라 관리 대시보드
      * @description : OPENSTACK Router 삭제 TEST
      * @title : testDeleteOpenstackRouter
      * @return : void
      ***************************************************/
      @Test
      public void testDeleteOpenstackRouter() throws Exception{
          OpenstackRouterMgntDTO dto = setOpenstackSaveDto();
          mockMvc.perform(delete(ROUTER_DELETE_URL).contentType(MediaType.APPLICATION_JSON)
                  .content(mapper.writeValueAsBytes(dto)))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isNoContent());
      }
      /***************************************************
      * @project : OPENSTACK 인프라 관리 대시보드
      * @description : OPENSTACK Router Interface 목록 조회 TEST
      * @title : testOpenstackRotuerInterfaceInfoList
      * @return : void
      ***************************************************/
      @Test
      public void testOpenstackRouterInterfaceInfoList() throws Exception{
          List<OpenstackRouterMgntDTO> expectIntList = setOpenstackRouterInterfaceInfoList();
          when(mockOpenstackRouterMgntService.getOpenstackRouterInterfaceInfoList(any(), anyInt(), anyString())).thenReturn(expectIntList);
          mockMvc.perform(get(ROUTER_INTERFACE_LIST, 1, "testRouter").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.records[0].accountId").value(1))
          .andExpect(jsonPath("$.records[0].routeId").value("testRouter"))
          .andExpect(jsonPath("$.records[0].subnetId").value("testSubnet"))
          .andExpect(jsonPath("$.records[0].subnetName").value("testSubnetName"))
          .andExpect(jsonPath("$.records[0].subnetFixedIps").value("192.168.1.10"))
          .andExpect(jsonPath("$.records[0].subnetStatus").value("ACTIVE"))
          .andExpect(jsonPath("$.records[0].subnetType").value(expectIntList.get(0).getSubnetType()))
          .andExpect(jsonPath("$.records[0].subnetAdminStateUp").value(expectIntList.get(0).getSubnetAdminStateUp()))
          .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
      }
      /***************************************************
       * @project : OPENSTACK 인프라 관리 대시보드
       * @description : OPENSTACK Router Interface Subnet 목록 조회 TEST
       * @title : testOpenstackRouterInterfaceSubnetInfoList
       * @return : void
       ***************************************************/
      @Test
      public void testOpenstackRouterInterfaceSubnetInfoList() throws Exception{
          List<OpenstackNetworkMgntVO> expectIntSubList = setOpenstackRouterInterfaceSubnetInfoList();
          when(mockOpenstackRouterMgntService.getOpenstackNetworkSubnetInfo(any(), anyInt())).thenReturn(expectIntSubList);
          mockMvc.perform(get(ROUTER_INTERFACE_SUBNET_LIST, 1).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.[0].subnetId").value(expectIntSubList.get(0).getSubnetId()))
          .andExpect(jsonPath("$.[0].subnetName").value(expectIntSubList.get(0).getSubnetName()))
          .andExpect(jsonPath("$.[0].cidrIpv4").value(expectIntSubList.get(0).getCidrIpv4()))
          .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
      }
     /***************************************************
      * @project : OPENSTACK 인프라 관리 대시보드
      * @description : OPENSTACK Router Interface Subnet 연결(생성) TEST
      * @title : testAttachOpenstackRouterInterface
      * @return : void
      ***************************************************/
      @Test
      public void testAttachOpenstackRouterInterface() throws Exception{
          OpenstackRouterMgntVO vo = setAttachOpenstackRouterInterfaceInfo();
          mockMvc.perform(post(ROUTER_INTERFACE_SUBNET_ATTACH).contentType(MediaType.APPLICATION_JSON)
          .content(mapper.writeValueAsBytes(vo)))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isCreated());
      }
      /***************************************************
       * @project : OPENSTACK 인프라 관리 대시보드
       * @description : OPENSTACK Router Interface Subnet 해제(삭제) TEST
       * @title : testDetachOpenstackRouterInterface
       * @return : void
       ***************************************************/
       @Test
       public void testDetachOpenstackRouterInterface() throws Exception{
           OpenstackRouterMgntVO vo = setAttachOpenstackRouterInterfaceInfo();
           mockMvc.perform(delete(ROUTER_INTERFACE_SUBNET_DETACH).contentType(MediaType.APPLICATION_JSON)
           .content(mapper.writeValueAsBytes(vo)))
           .andDo(MockMvcResultHandlers.print())
           .andExpect(MockMvcResultMatchers.status().isNoContent());
       }
      /***************************************************
       * @project : OPENSTACK 인프라 관리 대시보드
       * @description : OPENSTACK Router Gateway 연결 TEST
       * @title : testOpenstackRouterGatewayAttach
       * @return : void
       ***************************************************/
       @Test
       public void testOpenstackRouterGatewayAttach() throws Exception{
           OpenstackRouterMgntVO vo = setAttachOpenstackRouterInterfaceInfo();
           mockMvc.perform(post(ROUTER_GATEWAY_ATTACH).contentType(MediaType.APPLICATION_JSON)
           .content(mapper.writeValueAsBytes(vo)))
           .andDo(MockMvcResultHandlers.print())
           .andExpect(MockMvcResultMatchers.status().isCreated());
       }
       /***************************************************
        * @project : OPENSTACK 인프라 관리 대시보드
        * @description : OPENSTACK Router Gateway 연결해제 TEST
        * @title : testOpenstackRouterGatewayDetach
        * @return : void
        ***************************************************/
       @Test
       public void testOpenstackRouterGatewayDetach() throws Exception{
           OpenstackRouterMgntVO vo = setAttachOpenstackRouterInterfaceInfo();
           mockMvc.perform(delete(ROUTER_GATEWAY_DETACH).contentType(MediaType.APPLICATION_JSON)
           .content(mapper.writeValueAsBytes(vo)))
           .andDo(MockMvcResultHandlers.print())
           .andExpect(MockMvcResultMatchers.status().isNoContent());
       }
       /***************************************************
        * @project : OPENSTACK 인프라 관리 대시보드
        * @description : OPENSTACK Router Interface Subnet 목록 조회 TEST
        * @title : testOpenstackRouterExternalNetworkInfoList
        * @return : void
        ***************************************************/
       @Test
       public void testOpenstackRouterExternalNetworkInfoList() throws Exception{
           List<HashMap<String, String>> expectIntSubList = setOpenstackExteranlNetwork();
           when(mockOpenstackRouterMgntService.getOpenstackNetworkInfoList(any(), anyInt())).thenReturn(expectIntSubList);
           mockMvc.perform(get(ROUTER_EXNETWORK_LIST, 1).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.[0].id").value(expectIntSubList.get(0).get("id")))
           .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
       }
      /***************************************************
      * @project : OPENSTACK 인프라 관리 대시보드
      * @description : OPENSTACK Router 생성 JSON 값 설정
      * @title : setOpenstackSaveDto
      * @return : OpenstackRouterMgntDTO
      ***************************************************/
    private OpenstackRouterMgntDTO setOpenstackSaveDto() {
        OpenstackRouterMgntDTO dto = new OpenstackRouterMgntDTO();
        dto.setAccountId(1);
        dto.setRouterName("routerName");
        dto.setRouteId("testRouter");
        dto.setSubnetId("testSubnet");
        dto.setSubnetName("testSubnetName");
        dto.setSubnetFixedIps("192.168.1.10");
        dto.setSubnetStatus(State.ACTIVE);
        dto.setSubnetType(true);
        dto.setSubnetAdminStateUp(true);
        dto.setRecid(0);
        dto.getAccountId();
        dto.getRouterName();
        dto.getRouteId();
        dto.getSubnetId();
        dto.getSubnetName();
        dto.getSubnetFixedIps();
        dto.getSubnetStatus();
        dto.getSubnetType();
        dto.getSubnetAdminStateUp();
        dto.getRecid();
        return dto;
    }
    
      /***************************************************
       * @project : OPENSTACK 인프라 관리 대시보드
       * @description : OPENSTACK Router 목록 조회 기대 값 설정
       * @title : setOpenstackRouterListInfo
       * @return : OpenstackRouterMgntVO
       ***************************************************/
    private List<OpenstackRouterMgntVO> setOpenstackRouterListInfo() {
       List<OpenstackRouterMgntVO> expectList = new ArrayList<OpenstackRouterMgntVO>();
       OpenstackRouterMgntVO vo = new OpenstackRouterMgntVO();
       vo.setRecid(0);
       vo.setAccountId(1);
       vo.setRouterName("routerName");
       vo.setRouteId("routeId");
       vo.setExternalNetwork("externalNetwork");
       vo.setSubnetId("testSubnet");
       vo.setSubnetName("testSubnetName");
       vo.setSubnetFixedIps("192.168.1.10");
       vo.setSubnetStatus(State.ACTIVE);
       vo.setSubnetType("true");
       vo.setSubnetAdminStateUp(true);
       vo.setRecid(0);
       vo.getAccountId();
       vo.getRouterName();
       vo.getRouteId();
       vo.getSubnetId();
       vo.getSubnetName();
       vo.getSubnetFixedIps();
       vo.getSubnetStatus();
       vo.getSubnetType();
       vo.getSubnetAdminStateUp();
       vo.getRecid();
       expectList.add(vo);
       return expectList;
    }
    /***************************************************
     * @project : OPENSTACK 인프라 관리 대시보드
     * @description : OPENSTACK Router Interface JSON 값 설정
     * @title : setOpenstackRouterInterfaceInfoList
     * @return : List<OpenstackRouterMgntDTO>
     ***************************************************/
    private List<OpenstackRouterMgntDTO> setOpenstackRouterInterfaceInfoList() {
      List<OpenstackRouterMgntDTO> expectIntList = new ArrayList<OpenstackRouterMgntDTO>();
      OpenstackRouterMgntDTO dto = new OpenstackRouterMgntDTO();
      dto.setAccountId(1);
      dto.setRouterName("routerName");
      dto.setRouteId("testRouter");
      dto.setSubnetId("testSubnet");
      dto.setSubnetName("testSubnetName");
      dto.setSubnetFixedIps("192.168.1.10");
      dto.setSubnetStatus(State.ACTIVE);
      dto.setSubnetType(true);
      dto.setSubnetAdminStateUp(true);
      dto.setRecid(0);
      dto.getAccountId();
      dto.getRouterName();
      dto.getRouteId();
      dto.getSubnetId();
      dto.getSubnetName();
      dto.getSubnetFixedIps();
      dto.getSubnetStatus();
      dto.getSubnetType();
      dto.getSubnetAdminStateUp();
      dto.getRecid();
      expectIntList.add(dto);
      return expectIntList;
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
     * @description : OPENSTACK Router Interface Subnet 목록 값 설정
     * @title : setOpenstackRouterInterfaceSubnetInfoList
     * @return : List<OpenstackNetworkMgntVO>
     ***************************************************/
    private List<OpenstackNetworkMgntVO> setOpenstackRouterInterfaceSubnetInfoList(){
        List<OpenstackNetworkMgntVO> expectIntSubList = new ArrayList<OpenstackNetworkMgntVO>();
        OpenstackNetworkMgntVO vo = new OpenstackNetworkMgntVO();
        vo.setSubnetId("testSubnetId");
        vo.setSubnetName("testSubnetName");
        vo.setCidrIpv4("192.168.1.10");
        vo.setNetworkId("testNetworkId");
        expectIntSubList.add(vo);
        return expectIntSubList;
    }
    /***************************************************
     * @project : OPENSTACK 인프라 관리 대시보드
     * @description : OPENSTACK Router Interface 연결 값 설정
     * @title : setAttachOpenstackRouterInterfaceInfo
     * @return : OpenstackRouterMgntVO
     ***************************************************/
    private OpenstackRouterMgntVO setAttachOpenstackRouterInterfaceInfo(){
        OpenstackRouterMgntVO vo = new OpenstackRouterMgntVO();
        vo.setRecid(0);
        vo.setAccountId(1);
        vo.setRouterName("routerName");
        vo.setRouteId("routeId");
        vo.setExternalNetwork("externalNetwork");
        vo.setSubnetId("testSubnet");
        vo.setSubnetName("testSubnetName");
        vo.setSubnetFixedIps("192.168.1.10");
        vo.setSubnetStatus(State.ACTIVE);
        vo.setSubnetType("true");
        vo.setSubnetAdminStateUp(true);
        vo.setRecid(0);
        vo.getAccountId();
        vo.getRouterName();
        vo.getRouteId();
        vo.getSubnetId();
        vo.getSubnetName();
        vo.getSubnetFixedIps();
        vo.getSubnetStatus();
        vo.getSubnetType();
        vo.getSubnetAdminStateUp();
        vo.getRecid();
        return vo;
    }
}