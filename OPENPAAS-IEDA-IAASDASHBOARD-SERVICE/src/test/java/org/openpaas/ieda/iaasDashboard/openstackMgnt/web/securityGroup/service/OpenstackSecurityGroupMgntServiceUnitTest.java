package org.openpaas.ieda.iaasDashboard.openstackMgnt.web.securityGroup.service;

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
import org.openpaas.ieda.iaasDashboard.openstackMgnt.api.securityGroup.OpenstackSecurityGroupMgntApiService;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.web.securityGroup.dao.OpenstackSecurityGroupMgntVO;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.web.securityGroup.dto.OpenstackSecurityGroupMgntDTO;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.web.securityGroup.service.OpenstackSecurityGroupMgntService;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.openstack4j.model.common.Link;
import org.openstack4j.model.compute.SecGroupExtension;
import org.openstack4j.model.network.SecurityGroupRule;
import org.openstack4j.openstack.networking.domain.NeutronSecurityGroupRule.SecurityGroupRuleConcreteBuilder;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class OpenstackSecurityGroupMgntServiceUnitTest {
    
    private Principal principal = null;
    @InjectMocks OpenstackSecurityGroupMgntService mockOpenstackSecurityGroupMgntService;
    @Mock OpenstackSecurityGroupMgntApiService mockOpenstackSecurityGroupMgntApiService;
    @Mock CommonIaasService mockCommonIaasService;
    @Mock MessageSource mockMessageSource;
    
    /***************************************************
     * @project : OPENSTACK 인프라 관리 대시보드
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
    * @project : OPENSTACK 관리 대시보드
    * @description : OPENSTACK 보안 그룹 목록 조회  TEST
    * @title : testGetOpenstackSecrityGroupInfoList
    * @return : void
    ***************************************************/
    @Test
    public void testGetOpenstackSecrityGroupInfoList(){
        getOpenstackAccountInfo();
        List<? extends SecGroupExtension> expectList = setSecGroupList();
        doReturn(expectList).when(mockOpenstackSecurityGroupMgntApiService).getOpenstackSecrityGroupInfoListFromOpenstack(any());
        List<OpenstackSecurityGroupMgntVO> resultList = mockOpenstackSecurityGroupMgntService.getOpenstackSecrityGroupInfoList(principal, 1);
        assertEquals(resultList.get(0).getSecurityGroupName(), expectList.get(0).getName());
        assertEquals(resultList.get(0).getDescription(), expectList.get(0).getDescription());
    }
    
    /***************************************************
    * @project : OPENSTACK 관리 대시보드
    * @description : OPENSTACK 보안 그룹 목록 조회  TEST
    * @title : testGetOpenstackSecrityGroupIngressInfo
    * @return : void
    ***************************************************/
    @Test
    public void testGetOpenstackSecrityGroupIngressInfo(){
        getOpenstackAccountInfo();
        List<? extends SecurityGroupRule> expectRules = setInboundRule("default");
        doReturn(expectRules).when(mockOpenstackSecurityGroupMgntApiService).getOpenstackSecrityGroupIngressInfoFromOpenstack(any(), anyString());
        List<HashMap<String, Object>> resultuRules = mockOpenstackSecurityGroupMgntService.getOpenstackSecrityGroupIngressInfo(1, "secId", principal);
        assertEquals(resultuRules.get(0).get("IpProtocol"), expectRules.get(0).getProtocol());
        assertEquals(resultuRules.get(0).get("direction"), expectRules.get(0).getDirection());
        assertEquals(resultuRules.get(0).get("etherType"), expectRules.get(0).getEtherType());
        assertEquals(resultuRules.get(0).get("remote"), expectRules.get(0).getRemoteIpPrefix());
    }
    
    /***************************************************
    * @project : OPENSTACK 관리 대시보드
    * @description : OPENSTACK 보안 그룹 Inbound Rule 설정 TEST
    * @title : testSetOpenstackRulesList
    * @return : void
    ***************************************************/
    @Test
    public void testSetOpenstackRulesList(){
        getOpenstackAccountInfo();
        IaasAccountMgntVO vo = new IaasAccountMgntVO();
        List<? extends SecurityGroupRule> expectRules = setInboundRule("default");
        List<HashMap<String, Object>> resultuRules = mockOpenstackSecurityGroupMgntService.setOpenstackRulesList(expectRules, vo);
        assertEquals(resultuRules.get(0).get("IpProtocol"), expectRules.get(0).getProtocol());
        assertEquals(resultuRules.get(0).get("direction"), expectRules.get(0).getDirection());
        assertEquals(resultuRules.get(0).get("etherType"), expectRules.get(0).getEtherType());
        assertEquals(resultuRules.get(0).get("remote"), expectRules.get(0).getRemoteIpPrefix());
    }
    
    /***************************************************
    * @project : OPENSTACK 관리 대시보드
    * @description : port Range 가 -1 인 경우 TEST
    * @title : testSetPortRangeMinAndMaxValueMinus1
    * @return : void
    ***************************************************/
    @Test
    public void testSetPortRangeMinAndMaxValueMinus1(){
        getOpenstackAccountInfo();
        IaasAccountMgntVO vo = new IaasAccountMgntVO();
        List<? extends SecurityGroupRule> expectRules = setInboundRule("-1");
        List<HashMap<String, Object>> resultuRules = mockOpenstackSecurityGroupMgntService.setOpenstackRulesList(expectRules, vo);
        assertEquals(resultuRules.get(0).get("IpProtocol"), expectRules.get(0).getProtocol());
        assertEquals(resultuRules.get(0).get("direction"), expectRules.get(0).getDirection());
        assertEquals(resultuRules.get(0).get("etherType"), expectRules.get(0).getEtherType());
        assertEquals(resultuRules.get(0).get("remote"), expectRules.get(0).getRemoteIpPrefix());
    }
    
    /***************************************************
    * @project : OPENSTACK 관리 대시보드
    * @description : port Range 가 0 인 경우 TEST
    * @title : testSetPortRangeMinAndMaxValue0
    * @return : void
    ***************************************************/
    @Test
    public void testSetPortRangeMinAndMaxValue0(){
        getOpenstackAccountInfo();
        IaasAccountMgntVO vo = new IaasAccountMgntVO();
        List<? extends SecurityGroupRule> expectRules = setInboundRule("0");
        List<HashMap<String, Object>> resultuRules = mockOpenstackSecurityGroupMgntService.setOpenstackRulesList(expectRules, vo);
        assertEquals(resultuRules.get(0).get("IpProtocol"), expectRules.get(0).getProtocol());
        assertEquals(resultuRules.get(0).get("direction"), expectRules.get(0).getDirection());
        assertEquals(resultuRules.get(0).get("etherType"), expectRules.get(0).getEtherType());
        assertEquals(resultuRules.get(0).get("remote"), expectRules.get(0).getRemoteIpPrefix());
    }
    
    /***************************************************
    * @project : OPENSTACK 관리 대시보드
    * @description : port Range 가 서로 다를 경우 TEST
    * @title : testSetPortRangeOtherValue
    * @return : void
    ***************************************************/
    @Test
    public void testSetPortRangeOtherValue(){
        getOpenstackAccountInfo();
        IaasAccountMgntVO vo = new IaasAccountMgntVO();
        List<? extends SecurityGroupRule> expectRules = setInboundRule("Other");
        List<HashMap<String, Object>> resultuRules = mockOpenstackSecurityGroupMgntService.setOpenstackRulesList(expectRules, vo);
        assertEquals(resultuRules.get(0).get("IpProtocol"), expectRules.get(0).getProtocol());
        assertEquals(resultuRules.get(0).get("direction"), expectRules.get(0).getDirection());
        assertEquals(resultuRules.get(0).get("etherType"), expectRules.get(0).getEtherType());
        assertEquals(resultuRules.get(0).get("remote"), expectRules.get(0).getRemoteIpPrefix());
    }
    
    /***************************************************
    * @project : OPENSTACK 관리 대시보드
    * @description : Procotol 정보가 NUll 값을 경우 TEST
    * @title : testSetProtocolValueNull
    * @return : void
    ***************************************************/
    @Test
    public void testSetProtocolValueNull(){
        getOpenstackAccountInfo();
        IaasAccountMgntVO vo = new IaasAccountMgntVO();
        List<? extends SecurityGroupRule> expectRules = setInboundRule("protocolNull");
        List<HashMap<String, Object>> resultuRules = mockOpenstackSecurityGroupMgntService.setOpenstackRulesList(expectRules, vo);
        assertEquals(null , expectRules.get(0).getProtocol());
        assertEquals(resultuRules.get(0).get("direction"), expectRules.get(0).getDirection());
        assertEquals(resultuRules.get(0).get("etherType"), expectRules.get(0).getEtherType());
        assertEquals(resultuRules.get(0).get("remote"), expectRules.get(0).getRemoteIpPrefix());
    }
    
    /***************************************************
    * @project : OPENSTACK 관리 대시보드
    * @description : Security Group가 remote 되어 있을 경우 TEST
    * @title : testSetRemoteBySecurityGroupId
    * @return : void
    ***************************************************/
    @Test
    public void testSetRemoteBySecurityGroupId(){
        getOpenstackAccountInfo();
        IaasAccountMgntVO vo = new IaasAccountMgntVO();
        List<? extends SecGroupExtension> expectList = setSecGroupList();
        doReturn(expectList).when(mockOpenstackSecurityGroupMgntApiService).getOpenstackSecrityGroupInfoListFromOpenstack(any());
        List<? extends SecurityGroupRule> expectRules = setInboundRule("remoteId");
        List<HashMap<String, Object>> resultuRules = mockOpenstackSecurityGroupMgntService.setOpenstackRulesList(expectRules, vo);
        assertEquals(resultuRules.get(0).get("direction"), expectRules.get(0).getDirection());
        assertEquals(resultuRules.get(0).get("etherType"), expectRules.get(0).getEtherType());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : OPENSTACK 보안 그룹 생성 TEST
    * @title : testSaveOpenstackSecurityGroupInfoe
    * @return : void
    ***************************************************/
    @Test
    public void testSaveOpenstackSecurityGroupInfo(){
        getOpenstackAccountInfo();
        OpenstackSecurityGroupMgntDTO dto = setSecurityGroupInfo("defaule");
        mockOpenstackSecurityGroupMgntService.saveOpenstackSecurityGroupInfo(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : OPENSTACK 보안 그룹 생성 TEST
    * @title : testSaveOpenstackSecurityGroupInfoe
    * @return : void
    ***************************************************/
    @Test
    public void testSaveOpenstackSecurityGroupInboundRuleInfo(){
        getOpenstackAccountInfo();
        OpenstackSecurityGroupMgntDTO dto = setSecurityGroupInfo("ingressRule");
        when(mockOpenstackSecurityGroupMgntApiService.saveOpenstackSecurityGroupInfoFromOpenstack(any(), any())).thenReturn("secId");
        mockOpenstackSecurityGroupMgntService.saveOpenstackSecurityGroupInfo(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : OPENSTACK 보안 그룹 삭제 TEST
    * @title : testDeleteOpenstackSecurityGroupInfo
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteOpenstackSecurityGroupInfo(){
        getOpenstackAccountInfo();
        when(mockOpenstackSecurityGroupMgntApiService.deleteOpenstackSecurityGroupInfoFromOpenstack(any(), anyString())).thenReturn(200);
        OpenstackSecurityGroupMgntDTO dto = setSecurityGroupInfo("default");
        mockOpenstackSecurityGroupMgntService.deleteOpenstackSecurityGroupInfo(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : OPENSTACK 보안 그룹 삭제 Exception TEST
    * @title : testDeleteOpenstackSecurityGroupInfo
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testDeleteOpenstackSecurityGroupInfoExcepiton(){
        getOpenstackAccountInfo();
        when(mockOpenstackSecurityGroupMgntApiService.deleteOpenstackSecurityGroupInfoFromOpenstack(any(), anyString())).thenReturn(404);
        OpenstackSecurityGroupMgntDTO dto = setSecurityGroupInfo("default");
        mockOpenstackSecurityGroupMgntService.deleteOpenstackSecurityGroupInfo(dto, principal);
    }
    /***************************************************
    * @param string 
     * @project : Paas 플랫폼 설치 자동화
    * @description : OPENSTACK 보안 그룹 생성 관련 정보 설정
    * @title : setSecurityGroupInfo
    * @return : OpenstackSecurityGroupMgntDTO
    ***************************************************/
    public OpenstackSecurityGroupMgntDTO setSecurityGroupInfo(String type) {
        OpenstackSecurityGroupMgntDTO dto = new OpenstackSecurityGroupMgntDTO();
        dto.setAccountId(1);
        dto.setDescription("test");
        dto.setSecurityGroupName("test");
        if(type.equalsIgnoreCase("ingressRule")){
            dto.setSecurityGroupId("secId");
            List<HashMap<String, String>> maps = new ArrayList<HashMap<String, String>> ();
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("protocol", "tcp");
            map.put("portRange", "25555");
            maps.add(map);
            dto.setIngressRules(maps);
            dto.setIngressRuleType("bosh");
            dto.getIngressRules().size();
        }
        dto.getAccountId();
        dto.getSecurityGroupName();
        return dto;
    }

    /***************************************************
    * @param string 
     * @project : OPENSTACK 관리 대시보드
    * @description : OPENSTACK 보안 그룹 Inbound Rules 값 설정
    * @title : List<? extends SecGroupExtension>
    * @return : void
    ***************************************************/
    public List<? extends SecurityGroupRule> setInboundRule(String type) {
        List<SecurityGroupRule> list = new ArrayList<SecurityGroupRule>();
        SecurityGroupRuleConcreteBuilder builder = new SecurityGroupRuleConcreteBuilder();
        builder.direction("ingress");
        if(type.equals("-1")){
            builder.portRangeMin(-1);
            builder.portRangeMax(-1);
        } else if(type.equals("0")){
            builder.portRangeMin(0);
            builder.portRangeMax(0);
        } else if(type.equalsIgnoreCase("Other")){
            builder.portRangeMin(1);
            builder.portRangeMax(3306);
        } else{
            builder.portRangeMin(25555);
            builder.portRangeMax(25555);
        }
        if(type.equalsIgnoreCase("protocolNull")){
            builder.protocol(null);
        } else{
            builder.protocol("tcp");
        }
        if(type.equalsIgnoreCase("remoteId")){
            builder.remoteGroupId("secId");
        }else{
            builder.remoteIpPrefix("0.0.0.0/0");
        }
        builder.ethertype("IPv4");
        list.add(builder.build());
        return list;
    }

    /***************************************************
    * @project : OPENSTACK 관리 대시보드
    * @description : OPENSTACK 보안 그룹 조회 값 설정
    * @title : List<? extends SecGroupExtension>
    * @return : void
    ***************************************************/
    public List<? extends SecGroupExtension> setSecGroupList() {
        List<SecGroupExtension> list = new ArrayList<SecGroupExtension>();
        SecGroupExtension sec = new SecGroupExtension() {
            private static final long serialVersionUID = 1L;
            @Override
            public String getTenantId() {
                return null;
            }
            
            @Override
            public List<? extends Rule> getRules() {
                return null;
            }
            
            @Override
            public String getName() {
                return "test";
            }
            
            @Override
            public List<? extends Link> getLinks() {
                return null;
            }
            
            @Override
            public String getId() {
                return "secId";
            }
            
            @Override
            public String getDescription() {
                return "test";
            }
        };
        list.add(sec);
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
