package org.openpaas.ieda.iaas.web.aws;

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
import java.util.List;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.awsMgnt.web.vpc.dao.AwsVpcMgntVO;
import org.openpaas.ieda.awsMgnt.web.vpc.dto.AwsVpcMgntDTO;
import org.openpaas.ieda.awsMgnt.web.vpc.service.AwsVpcMgntService;
import org.openpaas.ieda.controller.iaasMgnt.awsMgnt.web.vpc.AwsVpcMgntController;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class AwsVpcMgntControllerUnitTest {
    
    @InjectMocks AwsVpcMgntController mockAwsVpcMgntController;
    @Mock AwsVpcMgntService mockAwsVpcMgntService;
    
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    
    
    /*************************************** URL *******************************************/
    final static String VIEW_URL = "/awsMgnt/vpc";
    final static String VPC_LIST_INFO_URL = "/awsMgnt/vpc/list/{accountId}/{region}";
    final static String VPC_DETAIL_INFO_URL = "/awsMgnt/vpc/save/detail/{accountId}/{vpcId}/{region}";
    final static String VPC_SAVE_URL = "/awsMgnt/vpc/save";
    final static String VPC_DELETE_URL = "/awsMgnt/vpc/delete";
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockAwsVpcMgntController).build();
    }
    
    /***************************************************
    * @project : AWS 관리 대시보드
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
    * @project : AWS 관리 대시보드
    * @description : AWS VPC 관리 화면 이동 TEST
    * @title : testGoAwsVpcMgnt
    * @return : void
    ***************************************************/
    @Test
    public void testGoAwsVpcMgnt() throws Exception{
        mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("iaas/aws/vpc/awsVpcMgnt"));
    }
    
    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : AWS VPC 목록 조회 TEST
    * @title : testGetAwsVpcInfoList
    * @return : void
    ***************************************************/
    @Test
    public void testGetAwsVpcInfoList() throws Exception{
        List<AwsVpcMgntVO> list = getVpcResultListInfo();
        when(mockAwsVpcMgntService.getAwsVpcInfoList(anyInt(), anyString(), any())).thenReturn(list);
        mockMvc.perform(get(VPC_LIST_INFO_URL, 1, "us-west-2").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].accountId").value(1))
        .andExpect(jsonPath("$.records[0].recid").value(1))
        .andExpect(jsonPath("$.records[0].vpcId").value("vpcId"))
        .andExpect(jsonPath("$.records[0].nameTag").value("vpc"))
        .andExpect(jsonPath("$.records[0].ipv4CidrBlock").value("172.16.100.0/24"))
        .andExpect(jsonPath("$.records[0].ipv6CidrBlock").value("casd-c"))
        .andExpect(jsonPath("$.records[0].tenancy").value("default"))
        .andExpect(jsonPath("$.records[0].status").value("available"))
        .andExpect(jsonPath("$.records[0].dhcpOptionSet").value("dopt-27bea345"))
        .andExpect(jsonPath("$.records[0].dnsHostNames").value(false))
        .andExpect(jsonPath("$.records[0].dnsResolution").value(false))
        .andExpect(jsonPath("$.records[0].classicLinkDns").value(false))
        .andExpect(jsonPath("$.records[0].routeTable").value("rtb-7112"))
        .andExpect(jsonPath("$.records[0].networkAcle").value("acl-111111"))
        .andExpect(jsonPath("$.records[0].defaultVpc").value(false));
    }
    
    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : AWS VPC 상세 조회 TEST
    * @title : testGetAwsVpcDetailInfo
    * @return : void
    ***************************************************/
    @Test
    public void testGetAwsVpcDetailInfo() throws Exception{
        AwsVpcMgntVO vo = getVpcResultInfo();
        when(mockAwsVpcMgntService.getAwsVpcDetailInfo(anyInt(), anyString(), any(), anyString())).thenReturn(vo);
        mockMvc.perform(get(VPC_DETAIL_INFO_URL, 1, "2231", "us-west-2").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accountId").value(1))
        .andExpect(jsonPath("$.recid").value(1))
        .andExpect(jsonPath("$.vpcId").value("vpcId"))
        .andExpect(jsonPath("$.nameTag").value("vpc"))
        .andExpect(jsonPath("$.ipv4CidrBlock").value("172.16.100.0/24"))
        .andExpect(jsonPath("$.ipv6CidrBlock").value("casd-c"))
        .andExpect(jsonPath("$.tenancy").value("default"))
        .andExpect(jsonPath("$.status").value("available"))
        .andExpect(jsonPath("$.dhcpOptionSet").value("dopt-27bea345"))
        .andExpect(jsonPath("$.dnsHostNames").value(false))
        .andExpect(jsonPath("$.dnsResolution").value(false))
        .andExpect(jsonPath("$.classicLinkDns").value(false))
        .andExpect(jsonPath("$.routeTable").value("rtb-7112"))
        .andExpect(jsonPath("$.networkAcle").value("acl-111111"))
        .andExpect(jsonPath("$.defaultVpc").value(false));
    }
    
    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : AWS VPC 생성 TEST
    * @title : testSaveAwsVpcInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveAwsVpcInfo() throws Exception{
        AwsVpcMgntDTO dto = setVpcInfo();
        mockMvc.perform(post(VPC_SAVE_URL).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto))).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
    }
    
    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : AWS VPC 삭제 TEST
    * @title : testDeleteAwsVpcInfo
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteAwsVpcInfo() throws Exception{
        AwsVpcMgntDTO dto = setVpcInfo();
        mockMvc.perform(delete(VPC_DELETE_URL).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto))).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNoContent());
    }
    
    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : AWS VPC 생성 값 설정
    * @title : setSaveVpcInfo
    * @return : AwsVpcMgntDTO
    ***************************************************/
    private AwsVpcMgntDTO setVpcInfo() {
        AwsVpcMgntDTO dto = new AwsVpcMgntDTO();
        dto.setAccountId(1);
        dto.setIpv4CirdBlock("172.16.100.0/24");
        dto.setNameTag("vpc");
        dto.setIpv6CirdBlock(false);
        dto.setTenancy("default");
        dto.setVpcId("vpcId");
        return null;
    }

    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : AWS VPC 상세 조회 결과 값 설정
    * @title : setVpcResultInfo
    * @return : AwsVpcMgntVO
    ***************************************************/
    private AwsVpcMgntVO getVpcResultInfo() {
        AwsVpcMgntVO vo = new AwsVpcMgntVO();
        vo.setAccountId(1);
        vo.setClassicLinkDns(false);
        vo.setDefaultVpc(false);
        vo.setDhcpOptionSet("dopt-27bea345");
        vo.setDnsHostNames(false);
        vo.setDnsResolution(false);
        vo.setIpv4CidrBlock("172.16.100.0/24");
        vo.setIpv6CidrBlock("casd-c");
        vo.setNameTag("vpc");
        vo.setNetworkAcle("acl-111111");
        vo.setRecid(1);
        vo.setRouteTable("rtb-7112");
        vo.setStatus("available");
        vo.setTenancy("default");
        vo.setVpcId("vpcId");
        return vo;
    }

    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : AWS VPC 목록 조회 결과 값 설정
    * @title : getVpcResultInfo
    * @return : List<AwsVpcMgntVO> 
    ***************************************************/
    private List<AwsVpcMgntVO> getVpcResultListInfo() {
        List<AwsVpcMgntVO> list = new ArrayList<AwsVpcMgntVO>();
        AwsVpcMgntVO vo = new AwsVpcMgntVO();
        vo.setAccountId(1);
        vo.setClassicLinkDns(false);
        vo.setDefaultVpc(false);
        vo.setDhcpOptionSet("dopt-27bea345");
        vo.setDnsHostNames(false);
        vo.setDnsResolution(false);
        vo.setIpv4CidrBlock("172.16.100.0/24");
        vo.setIpv6CidrBlock("casd-c");
        vo.setNameTag("vpc");
        vo.setNetworkAcle("acl-111111");
        vo.setRecid(1);
        vo.setRouteTable("rtb-7112");
        vo.setStatus("available");
        vo.setTenancy("default");
        vo.setVpcId("vpcId");
        list.add(vo);
        return list;
    }
    
    
    
}
