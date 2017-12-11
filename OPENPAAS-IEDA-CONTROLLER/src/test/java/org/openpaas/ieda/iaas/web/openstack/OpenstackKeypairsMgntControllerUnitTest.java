package org.openpaas.ieda.iaas.web.openstack;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.openpaas.ieda.controller.iaasMgnt.openstackMgnt.web.keypairs.OpenstackKeypairsMgntController;
import org.openpaas.ieda.openstackMgnt.web.keypairs.service.OpenstackKeypairsMgntService;
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
@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class OpenstackKeypairsMgntControllerUnitTest {

    private MockMvc mockMvc;
    
    @InjectMocks OpenstackKeypairsMgntController mockOpenstackKeypairsMgntController;
    @Mock OpenstackKeypairsMgntService mockOpenstackKeypairsMgntService;
    
    final static String VIEW_URL = "/openstackMgnt/keypairs";
    final static String KEYPAIRS_LIST_URL = "/openstackMgnt/keypairs/list/{accountId}";
    final static String KEYPAIRS_SAVE_URL = "/openstackMgnt/keypairs/save/{keyPair}/{1}";

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockOpenstackKeypairsMgntController).build();
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
     * @description : OPENSTACK Keypairs 화면 이동 TEST
     * @title : testGoOpenstackNetworkMgnt
     * @return : void
     ***************************************************/
     @Test
     public void testGoOpenstackKeypairsMgnt() throws Exception{
         mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
         .andExpect(status().isOk())
         .andExpect(view().name("iaas/openstack/keypairs/openstackKeypairsMgnt"));
     }
     
     /***************************************************
      * @project : OPENSTACK 인프라 관리 대시보드
      * @description : OPENSTACK Keypairs 목록 조회 TEST
      * @title : testGetOpenstackKeypairsInfoList
      * @return : void
      ***************************************************/
      @Test
      public void testGetOpenstackKeypairsInfoList() throws Exception{
          List<HashMap<String,Object>> expectList = setOpenstackKeypairsListInfo();
          when(mockOpenstackKeypairsMgntService.getOpenstackKeypairsInfoList(any(), anyInt())).thenReturn(expectList);
          mockMvc.perform(get(KEYPAIRS_LIST_URL, 1).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.records[0].accountId").value(1))
          .andExpect(jsonPath("$.records[0].keypairsName").value("keypairsName"))
          .andExpect(jsonPath("$.records[0].fingerprint").value("fingerprint"))
          .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
      }

      /***************************************************
       * @project : OPENSTACK 인프라 관리 대시보드
       * @description : OPENSTACK Keypairs 생성 TEST 
       * @title : testSaveOpenstackKeypairsInfo
       * @return : void
       ***************************************************/
       @Test
       public void testSaveOpenstackKeypairsInfo() throws Exception{
           mockMvc.perform(get(KEYPAIRS_SAVE_URL, "1", 1))
           .andDo(MockMvcResultHandlers.print())
           .andExpect(MockMvcResultMatchers.status().isOk());
       }

    /***************************************************
      * @project : OPENSTACK 인프라 관리 대시보드
      * @description : OPENSTACK Keypairs 정보 목록 값 설정
      * @title : setOpenstackKeypairsListInfo
      * @return : List<HashMap<String,Object>>
      ***************************************************/
    private List<HashMap<String,Object>> setOpenstackKeypairsListInfo() {
        List<HashMap<String,Object>> expectList = new ArrayList<HashMap<String,Object>>();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("accountId", 1);
        map.put("keypairsName",  "keypairsName");
        map.put("fingerprint",  "fingerprint");
        expectList.add(map);
        return expectList;
    }
    
}