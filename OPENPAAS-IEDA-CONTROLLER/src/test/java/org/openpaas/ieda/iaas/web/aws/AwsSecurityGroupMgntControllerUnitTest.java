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
import org.openpaas.ieda.awsMgnt.web.securityGroup.dao.AwsSecurityGroupMgntVO;
import org.openpaas.ieda.awsMgnt.web.securityGroup.dto.AwsSecurityGroupMgntDTO;
import org.openpaas.ieda.awsMgnt.web.securityGroup.service.AwsSecurityGroupMgntService;
import org.openpaas.ieda.awsMgnt.web.vpc.dao.AwsVpcMgntVO;
import org.openpaas.ieda.awsMgnt.web.vpc.service.AwsVpcMgntService;
import org.openpaas.ieda.common.BaseControllerUnitTest;
import org.openpaas.ieda.controller.iaasMgnt.awsMgnt.web.securityGroup.AwsSecurityGroupMgntController;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class AwsSecurityGroupMgntControllerUnitTest extends BaseControllerUnitTest{

    @InjectMocks AwsSecurityGroupMgntController mockawsSecurityGroupMgntController;
    @Mock AwsSecurityGroupMgntService mockawsSecurityGroupMgntService;
    @Mock AwsVpcMgntService mockvpcService;
    
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    
    
    /*************************************** URL *******************************************/
    final static String VIEW_URL = "/awsMgnt/securityGroup";
    final static String SECURITYGROUP_INFO_LIST_URL = "/awsMgnt/securityGroup/list/{accountId}/{region}";
    final static String SECURITYGROUP_RULES_LIST_URL = "/awsMgnt/securityGroup/ingress/list/{accountId}/{groupId:.*}/{region}";
    final static String SECURITYGROUP_SAVE_URL = "/awsMgnt/securityGroup/save";
    final static String SECURITYGROUP_DELETE_URL = "/awsMgnt/securityGroup/delete";
    final static String SECURITYGROUP_VPC_LIST_URL = "/awsMgnt/securityGroup/save/vpcs/{accountId}/{region}";
    
    /****************************************************************
     * @project : AWS 관리 대시보드
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockawsSecurityGroupMgntController).build();
    }
    
    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : AWS Security Group관리 화면 이동 TEST
    * @title : goAwsSecurityGroupMgnt
    * @return : void
    ***************************************************/
    @Test
    public void testGoAwsSecurityGroupMgnt() throws Exception {
        mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("iaas/aws/securityGroup/awsSecurityGroupMgnt"));
    }
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : AWS Security Group 목록 조회 TEST
     * @title : testGetAwsSecurityGroupInfoList
     * @return : void
     ***************************************************/
    @Test
    public void testGetAwsSecurityGroupInfoList() throws Exception{
        List<AwsSecurityGroupMgntVO> list = getAwsSecurityGroupResultInfo();
        when(mockawsSecurityGroupMgntService.getAwsSecurityGroupInfoList(any(), anyInt(), anyString())).thenReturn(list);
        mockMvc.perform(get(SECURITYGROUP_INFO_LIST_URL, 1, "region").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].accountId").value(1))
        .andExpect(jsonPath("$.records[0].recid").value(1))
        .andExpect(jsonPath("$.records[0].nameTag").value("testNameTag"))
        .andExpect(jsonPath("$.records[0].groupId").value("testGroupId"))
        .andExpect(jsonPath("$.records[0].groupName").value("testGroupName"))
        .andExpect(jsonPath("$.records[0].description").value("testDescription"))
        .andExpect(jsonPath("$.records[0].vpcId").value("testVpcId"));
    }
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : AWS Security Group Rules 조회
     * @title : testGetAwsSecurityGroupRules
     * @return : void
    ***************************************************/
    @Test
    public void testGetAwsSecurityGroupRules() throws Exception{
        List<HashMap<String, Object>> map = getAwsSecurityRulesResultInfo();
        when(mockawsSecurityGroupMgntService.getAwsSecurityGroupRules(anyInt(), anyString(), anyString(), any())).thenReturn(map);
        mockMvc.perform(get(SECURITYGROUP_RULES_LIST_URL,1,"sg1", "us-west-2").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : AWS Security Group 생성 TEST
     * @title : testSaveAwsSecurityGroupInfo
     * @return : void
     ***************************************************/
    @Test
    public void testSaveAwsSecurityGroupInfo() throws Exception{
        AwsSecurityGroupMgntDTO dto = getSaveAwsSecurityGroupResultInfo();
        mockMvc.perform(post(SECURITYGROUP_SAVE_URL).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto))).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
    }
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : AWS Security Group 삭제
     * @title : testDeleteAwsSecurityGroupInfo
     * @return : void
     ***************************************************/
    @Test
    public void testDeleteAwsSecurityGroupInfo() throws Exception{
        AwsSecurityGroupMgntDTO dto = getSaveAwsSecurityGroupResultInfo();
        mockMvc.perform(delete(SECURITYGROUP_DELETE_URL).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto))).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNoContent());
    }
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : AWS VPC 목록 조회
     * @title : testGetAwsVpc
     * @return : void
     ***************************************************/
    @Test
    public void testGetAwsVpc() throws Exception{
        List<AwsVpcMgntVO> list = GetAwsVpcResult();
        when(mockvpcService.getAwsVpcInfoList(anyInt(), anyString(), any())).thenReturn(list);
        mockMvc.perform(get(SECURITYGROUP_VPC_LIST_URL, 1, "us-west-2").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
    }
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : AWS Security Group 목록 조회 결과 값 설정
     * @title : getAwsSecurityGroupResultInfo
     * @return : List<AwsSecurityGroupMgntVo>
     ***************************************************/
    private List<AwsSecurityGroupMgntVO> getAwsSecurityGroupResultInfo(){
        List<AwsSecurityGroupMgntVO> list = new ArrayList<AwsSecurityGroupMgntVO>();
        AwsSecurityGroupMgntVO vo = new AwsSecurityGroupMgntVO();
        vo.setAccountId(1);
        vo.setRecid(1);
        vo.setNameTag("testNameTag");
        vo.setGroupId("testGroupId");
        vo.setGroupName("testGroupName");
        vo.setDescription("testDescription");
        vo.setVpcId("testVpcId");
        list.add(vo);
        return list;
    }
    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : AWS Security Group Rules 목록 조회 결과 값 설정
    * @title : getAwsSecurityGroupRulesResultInfo
    * @return : HashMap<String, Object>
    ***************************************************/
    private List<HashMap<String, Object>> getAwsSecurityRulesResultInfo(){
        List<HashMap<String, Object>> maps = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map = new HashMap<String, Object>();
        AwsSecurityGroupMgntVO vo = new AwsSecurityGroupMgntVO();
        vo.setGroupId("sg1");
        vo.setAccountId(1);
        vo.setRecid(1);
        vo.setNameTag("testNameTag1");
        vo.setGroupName("testGroupName1");
        vo.setDescription("testDescription1");
        vo.setVpcId("testVpcId1");
        map.put("groupId", vo);
        map.put("trafficType", "testTrafficType1");
        map.put("protocol", "testProtocol1");
        map.put("portRange", "testPortRange1");
        map.put("source","testSource1");
        maps.add(map);
        return maps;
    }
    
    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : AWS Security Group 생성 결과 값 설정
    * @title : getSaveAwsSecurityGroupResultInfo
    * @return : List<AwsSecurityGroupMgntDTO>
    ***************************************************/
    private AwsSecurityGroupMgntDTO getSaveAwsSecurityGroupResultInfo(){
        AwsSecurityGroupMgntDTO dto = new AwsSecurityGroupMgntDTO();
        dto.setAccountId(1);
        dto.setDescription("testDescription");
        dto.setGroupId("testGroupId");
        dto.setGroupName("testGroupName");
        dto.setVpcId("testVpcId");
        dto.setNameTag("testNameTag");
        return dto;
    }
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : AWS VPC 목록 조회 결과 값 설정
     * @title : GetAwsVpcResult
     * @return : List<AwsVpcMgntVO>
     ***************************************************/
    private List<AwsVpcMgntVO> GetAwsVpcResult(){
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
