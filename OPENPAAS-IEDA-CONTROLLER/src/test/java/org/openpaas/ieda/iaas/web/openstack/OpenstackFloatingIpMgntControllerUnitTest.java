package org.openpaas.ieda.iaas.web.openstack;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;
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
import org.openpaas.ieda.controller.iaasMgnt.openstackMgnt.web.floatingIp.OpenstackFloatingIpMgntController;
import org.openpaas.ieda.openstackMgnt.web.floatingIp.dto.OpenstackFloatingIpMgntDTO;
import org.openpaas.ieda.openstackMgnt.web.floatingIp.service.OpenstackFloatingIpMgntService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.ObjectMapper;
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

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class OpenstackFloatingIpMgntControllerUnitTest {
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();

    @InjectMocks OpenstackFloatingIpMgntController mockOpenstackFloatingIpMgntController;
    @Mock OpenstackFloatingIpMgntService mockOpenstackFloatingIpMgntService;

    final static String VIEW_URL = "/openstackMgnt/floatingIp";
    final static String FLOATING_IP_LIST_URL = "/openstackMgnt/floatingIp/list/{accountId}";
    final static String FLOATING_IP_ALLOCATE_URL = "/openstackMgnt/floatingIp/save";
    final static String FLOATING_IP_POOL_LIST_URL = "/openstackMgnt/floatingIp/save/pool/list/{accountId}";

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockOpenstackFloatingIpMgntController).build();
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
    * @description : OPENSTACK Floating IP 관리 화면 이동 TEST
    * @title : testGoOpenstackFloatingIpMgnt
    * @return : void
    ***************************************************/
    @Test
    public void testGoOpenstackFloatingIpMgnt() throws Exception{
        mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("iaas/openstack/floatingIp/openstackFloatingIpMgnt"));
    }
    
    /***************************************************
     * @project : OPENSTACK 인프라 관리 대시보드
     * @description : OPENSTACK Floating IP 목록 조회 TEST
     * @title : testGetOpenstackFloatingIpInfoList
     * @return : void
     ***************************************************/
    @Test
    public void testGetOpenstackFloatingIpInfoList() throws Exception{
        List<HashMap<String,Object>> expectList = setOpenstackFloatingIpListInfo();
        when(mockOpenstackFloatingIpMgntService.getOpenstackFloatingIpInfoList(any(), anyInt())).thenReturn(expectList);
        mockMvc.perform(get(FLOATING_IP_LIST_URL,1).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].ipAddress").value("1.1.1.1"))
        .andExpect(jsonPath("$.records[0].instanceName").value("vm-aaa"))
        .andExpect(jsonPath("$.records[0].pool").value("ex-aaa"))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
    
    /***************************************************
     * @project : OPENSTACK 인프라 관리 대시보드
     * @description : OPENSTACK Floating IP 할당 TEST 
     * @title : testSaveOpenstackFloatingIpInfo
     * @return : void
     ***************************************************/
     @Test
     public void testSaveOpenstackFloatingIpInfo() throws Exception{
         OpenstackFloatingIpMgntDTO dto = setOpenstackSaveDto();
         mockMvc.perform(post(FLOATING_IP_ALLOCATE_URL).contentType(MediaType.APPLICATION_JSON)
                 .content(mapper.writeValueAsBytes(dto)))
         .andDo(MockMvcResultHandlers.print())
         .andExpect(MockMvcResultMatchers.status().isCreated());
     }
     
     /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : OPENSTACK Floating IP Pool 목록 조회 TEST 
     * @title : testGetPoolList
     * @return : void
     ***************************************************/
     @Test
     public void testGetPoolList() throws Exception{
        List<String> exportList = resultPoolList();
        when(mockOpenstackFloatingIpMgntService.getOpenstackPoolInfoList(any(), anyInt())).thenReturn(exportList);
        mockMvc.perform(get(FLOATING_IP_POOL_LIST_URL,1).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[0]").value("172.16.100.1"))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
     }
    
    
     /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : OPENSTACK Floating IP Pool 목록 조회 정보 설정 
     * @title : resultPoolList
     * @return : List<String>
     ***************************************************/
    public List<String> resultPoolList() {
        List<String> list = new ArrayList<String>();
        String pool = "172.16.100.1";
        list.add(pool);
        return list;
    }

    /***************************************************
      * @project : OPENSTACK 인프라 관리 대시보드
      * @description : OPENSTACK Floating IP 목록 조회 기대 값 설정
      * @title : setOpenstackFloatingIpListInfo
      * @return : List<HashMap<String,Object>>
     ***************************************************/
     public List<HashMap<String,Object>> setOpenstackFloatingIpListInfo(){
         List<HashMap<String,Object>> expectList = new ArrayList<HashMap<String,Object>>();
         HashMap<String,Object> map = new HashMap<String,Object>();
         map.put("ipAddress", "1.1.1.1");
         map.put("instanceName","vm-aaa");
         map.put("pool","ex-aaa");
         expectList.add(map);
         return expectList;
     }
     
     /***************************************************
     * @project : OPENSTACK 인프라 관리 대시보드
     * @description : OPENSTACK Floating IP 할당 시 입력 값 설정
     * @title : setOpenstackSaveDto
     * @return : OpenstackFloatingIpMgntDTO
    ***************************************************/
    public OpenstackFloatingIpMgntDTO setOpenstackSaveDto(){
         OpenstackFloatingIpMgntDTO dto = new OpenstackFloatingIpMgntDTO();
         dto.setAccountId(1);
         dto.setPool("ex-tex");
         return dto;
     }
    
}
