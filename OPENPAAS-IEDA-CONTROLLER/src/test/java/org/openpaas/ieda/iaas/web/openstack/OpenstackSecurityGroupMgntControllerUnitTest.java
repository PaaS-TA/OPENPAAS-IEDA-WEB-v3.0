package org.openpaas.ieda.iaas.web.openstack;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.openpaas.ieda.controller.iaasMgnt.openstackMgnt.web.securityGroup.OpenstackSecurityGroupMgntController;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.web.securityGroup.dao.OpenstackSecurityGroupMgntVO;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.web.securityGroup.dto.OpenstackSecurityGroupMgntDTO;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.web.securityGroup.service.OpenstackSecurityGroupMgntService;
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
public class OpenstackSecurityGroupMgntControllerUnitTest {
    
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    
    @InjectMocks OpenstackSecurityGroupMgntController mockOpenstackSecurityGroupMgntController;
    @Mock OpenstackSecurityGroupMgntService mockOpenstackSecurityGroupMgntService;
    
    final static String VIEW_URL = "/openstackMgnt/securityGroup";
    final static String SECURITYGROUP_LIST_URL = "/openstackMgnt/securityGroup/list/{accountId}";
    final static String SECURITYGROUP_INBOUND_RULES_LIST_URL = "/openstackMgnt/securityGroup/ingress/list/{accountId}/{groupId}";
    final static String SECURITYGROUP_SAVE_URL = "/openstackMgnt/securityGroup/save";
    final static String SECURITYGROUP_DELETE_URL = "/openstackMgnt/securityGroup/delete";
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : 하나의 메소드가 실행되기전 호출
    * @title : setUp
    * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockOpenstackSecurityGroupMgntController).build();
        getLoggined();
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
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
    * @description : OPENSTACK 보안 그룹 관리 화면 이동 TEST
    * @title : testGoOpenstackSecurityGroupMgnt
    * @return : void
    ***************************************************/
    @Test
    public void testGoOpenstackSecurityGroupMgnt() throws Exception{
        mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("iaas/openstack/securityGroup/openstackSecurityGroupMgnt"));
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 보안 그룹 목록 조회 TEST
    * @title : testGetOpenstackSecrityGroupInfoList
    * @return : void
    ***************************************************/
    @Test
    public void testGetOpenstackSecrityGroupInfoList() throws Exception{
        List<OpenstackSecurityGroupMgntVO> expectList  = setOpenstackSecurityGroupListInfo();
        when(mockOpenstackSecurityGroupMgntService.getOpenstackSecrityGroupInfoList(any(), anyInt())).thenReturn(expectList);
        mockMvc.perform(get(SECURITYGROUP_LIST_URL, 1).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].accountId").value(1))
        .andExpect(jsonPath("$.records[0].recid").value(1))
        .andExpect(jsonPath("$.records[0].description").value("test"))
        .andExpect(jsonPath("$.records[0].securityGroupId").value("secId"))
        .andExpect(jsonPath("$.records[0].securityGroupName").value("test"));
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 보안 그룹 목록 Inbound Rules 조회 TEST
    * @title : testGetOpenstackSecrityGroupIngressInfo
    * @return : void
    ***************************************************/
    @Test
    public void testGetOpenstackSecrityGroupIngressInfo() throws Exception{
        List<HashMap<String, Object>> maps = setOpenstackSecurityGroupIngressListInfo();
        when(mockOpenstackSecurityGroupMgntService.getOpenstackSecrityGroupIngressInfo(anyInt(), anyString(), any())).thenReturn(maps);
        mockMvc.perform(get(SECURITYGROUP_INBOUND_RULES_LIST_URL, 1, "sg1").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[0].portRange").value("25555"))
        .andExpect(jsonPath("$.[0].IpProtocol").value("any"))
        .andExpect(jsonPath("$.[0].remote").value("10.0.0.0/0"))
        .andExpect(jsonPath("$.[0].etherType").value("Ipv4"))
        .andExpect(jsonPath("$.[0].direction").value("ingress"));
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 보안 그룹 생성 TEST
    * @title : testSaveOpenstackSecurityGroupInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveOpenstackSecurityGroupInfo() throws Exception{
        OpenstackSecurityGroupMgntDTO dto = setSaveOpenstackSecurityGroupInfo();
        
        mockMvc.perform(post(SECURITYGROUP_SAVE_URL).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isCreated());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : OPENSTACK 보안 그룹 삭제 TEST
    * @title : testDeleteOpenstackSecurityGroupInfo
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteOpenstackSecurityGroupInfo() throws Exception{
        OpenstackSecurityGroupMgntDTO dto = setSaveOpenstackSecurityGroupInfo();
        
        mockMvc.perform(delete(SECURITYGROUP_DELETE_URL).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 보안 그룹 생성 조회 TEST
    * @title : setSaveOpenstackSecurityGroupList
    * @return : OpenstackSecurityGroupMgntDTO
    ***************************************************/
    public OpenstackSecurityGroupMgntDTO setSaveOpenstackSecurityGroupInfo() {
        OpenstackSecurityGroupMgntDTO dto = new OpenstackSecurityGroupMgntDTO();
        dto.setAccountId(1);
        dto.setDescription("test");
        dto.setIngressRuleType("bosh");
        dto.setSecurityGroupId("secId");
        dto.setSecurityGroupName("test");
        List<HashMap<String, String>> maps = new ArrayList<HashMap<String, String>> ();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("protocol", "tcp");
        map.put("portRange", "25555");
        maps.add(map);
        dto.setIngressRules(maps);
        dto.getAccountId();
        dto.getDescription();
        dto.getIngressRules().size();
        dto.getIngressRuleType();
        dto.getSecurityGroupId();
        dto.getSecurityGroupName();
        return dto;
    }

    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 보안 그룹 Ingress Rules 조회 결과 값 설정
    * @title : setOpenstackSecurityGroupIngressListInfo
    * @return : List<HashMap<String, Object>>
    ***************************************************/
    public List<HashMap<String, Object>> setOpenstackSecurityGroupIngressListInfo() {
        List<HashMap<String, Object>> maps = new ArrayList<HashMap<String, Object>> ();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("portRange", "25555");
        map.put("IpProtocol", "any");
        map.put("remote", "10.0.0.0/0");
        map.put("etherType", "Ipv4");
        map.put("direction", "ingress");
        maps.add(map);
        return maps;
    }

    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 보안 그룹 목록 조회 결과 값 설정
    * @title : setOpenstackSecurityGroupListInfo
    * @return : List<OpenstackSecurityGroupMgntVO>
    ***************************************************/
    public List<OpenstackSecurityGroupMgntVO> setOpenstackSecurityGroupListInfo() {
        List<OpenstackSecurityGroupMgntVO> list = new ArrayList<OpenstackSecurityGroupMgntVO>();
        OpenstackSecurityGroupMgntVO vo = new OpenstackSecurityGroupMgntVO();
        vo.setAccountId(1);
        vo.setDescription("test");
        vo.setSecurityGroupName("test");
        vo.setRecid(1);
        vo.setSecurityGroupId("secId");
        vo.getRecid();
        vo.getAccountId();
        vo.getDescription();
        vo.getSecurityGroupId();
        vo.getSecurityGroupName();
        list.add(vo);
        return list;
    }
}
