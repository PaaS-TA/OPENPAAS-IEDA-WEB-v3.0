package org.openpaas.ieda.iaas.web.aws;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.controller.iaasMgnt.awsMgnt.web.internetGateway.AwsInternetGatewayMgntController;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.internetGateway.dao.AwsInternetGatewayMgntVO;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.internetGateway.dto.AwsInternetGatewayMgntDTO;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.internetGateway.service.AwsInternetGatewayMgntService;
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
public class AwsInternetGatewayMgntControllerUnitTest {
    @InjectMocks AwsInternetGatewayMgntController mockAwsInternetGatewayMgntController;
    @Mock AwsInternetGatewayMgntService mockAwsInternetGatewayMgntService;
    
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    
    
    /*************************************** URL *******************************************/
    final static String VIEW_URL = "/awsMgnt/internetGateway";
    final static String INTERNETGATEWAY_LIST_INFO_URL = "/awsMgnt/internetGateWay/list/{accountId}/{region}";
    final static String INTERNETGATEWAY_SAVE_INFO_URL = "/awsMgnt/internetGateWay/save";
    final static String INTERNETGATEWAY_DELETE_INFO_URL = "/awsMgnt/internetGateWay/delete";
    final static String VPC_LIST_INFO_URL = "/awsMgnt/internetGateWay/attach/vpc/{accountId}/{region}";
    final static String VPC_ATTACH_INFO_URL = "/awsMgnt/internetGateWay/attach";
    final static String VPC_DETACH_INFO_URL = "/awsMgnt/internetGateWay/detach";
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockAwsInternetGatewayMgntController).build();
    }
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : AWS 인터넷 게이트웨이 화면 이동 TEST
     * @title : testGoAwsInternetGateWay
     * @return : void
    ***************************************************/
    @Test
    public void testGoAwsInternetGateWay() throws Exception{
        mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("iaas/aws/internetGateway/awsInternetgateWayMgnt"));
    }
    
    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : AWS 인터넷 게이트웨이 목록 조회 TEST
    * @title : testGetAwsInternetGatewayInfoList
    * @return : void
    ***************************************************/
    @Test
    public void testGetAwsInternetGatewayInfoList() throws Exception{
        List<AwsInternetGatewayMgntVO> list = getResultInternetGatewayInfoList();
        when(mockAwsInternetGatewayMgntService.getAwsInternetGatewayInfoList(any(), anyInt(), anyString())).thenReturn(list);
        mockMvc.perform(get(INTERNETGATEWAY_LIST_INFO_URL, 1, "region").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].accountId").value(1))
        .andExpect(jsonPath("$.records[0].internetGatewayName").value("gate"))
        .andExpect(jsonPath("$.records[0].recid").value(1))
        .andExpect(jsonPath("$.records[0].status").value("avail"))
        .andExpect(jsonPath("$.records[0].internetGatewayId").value("itn-1111"))
        .andExpect(jsonPath("$.records[0].vpcId").value("vpcId"));
    }
    
    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : AWS 인터넷 게이트웨이 생성 TEST
    * @title : testSaveAwsInternetGatewayInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveAwsInternetGatewayInfo() throws Exception{
        AwsInternetGatewayMgntDTO dto = setInternetGatewayInfo();
        mockMvc.perform(post(INTERNETGATEWAY_SAVE_INFO_URL).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto))).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
    }
    
    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : AWS 인터넷 게이트 삭제 TEST
    * @title : testDeleteAwsInternetGatewayInfo
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteAwsInternetGatewayInfo() throws Exception{
        AwsInternetGatewayMgntDTO dto = setInternetGatewayInfo();
        mockMvc.perform(delete(INTERNETGATEWAY_DELETE_INFO_URL).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto))).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNoContent());
    }
    
    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : testGetAwsVpcInfoList
    * @title : AWS 인터넷게이트웨이에 연결 할 VPC 조회 TEST
    * @return : void
    ***************************************************/
    @Test
    public void testGetAwsVpcInfoList() throws Exception {
        List<AwsInternetGatewayMgntVO> list = getResultVpcIdInfoList();
        when(mockAwsInternetGatewayMgntService.getAwsVpcInfoList(any(), anyInt(), anyString())).thenReturn(list);
        mockMvc.perform(get(VPC_LIST_INFO_URL, 1, "region").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[0].vpcId").value("vpc1"))
        .andExpect(jsonPath("$.[1].vpcId").value("vpc2"));
        
    }
    
    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : testGetAwsVpcInfoList
    * @title : AWS 인터넷게이트웨이에 VPC 연결 TEST
    * @return : void
d     * @throws JsonProcessingException 
    ***************************************************/
    @Test
    public void testInternetGatewayAttachVpc() throws Exception{
        AwsInternetGatewayMgntDTO dto = setInternetGatewayInfo();
        mockMvc.perform(put(VPC_ATTACH_INFO_URL).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto))).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
        
    }
    
    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : AWS 인터넷게이트웨이에 VPC 연결 해제 TEST
    * @title : testInternetGatewayDetachVpc
    * @return : void
    ***************************************************/
    @Test
    public void testInternetGatewayDetachVpc() throws Exception{
        AwsInternetGatewayMgntDTO dto = setInternetGatewayInfo();
        mockMvc.perform(put(VPC_DETACH_INFO_URL).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto))).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : AWS 인터넷게이트웨이에 연결 할 VPC 조회 결과 값 설정
    * @title : getResultVpcIdInfoList
    * @return : List<AwsInternetGatewayMgntVO>
    ***************************************************/
    private List<AwsInternetGatewayMgntVO> getResultVpcIdInfoList() {
        List<AwsInternetGatewayMgntVO> list = new ArrayList<AwsInternetGatewayMgntVO>();
        AwsInternetGatewayMgntVO vo = new AwsInternetGatewayMgntVO();
        vo.setVpcId("vpc1");
        list.add(0, vo);
        vo = new AwsInternetGatewayMgntVO();
        vo.setVpcId("vpc2");
        list.add(1, vo);
        return list;
    }
    
    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : AWS 인터넷 게이트웨이 정보 설정
    * @title : setInternetGatewayInfo
    * @return : AwsInternetGatewayMgntDTO
    ***************************************************/
    private AwsInternetGatewayMgntDTO setInternetGatewayInfo() {
        AwsInternetGatewayMgntDTO dto = new AwsInternetGatewayMgntDTO();
        dto.setAccountId(1);
        dto.setInternetGatewayId("inId");
        dto.setInternetGatewayName("intName");
        dto.setVpcId("vpcId");
        dto.setRegion("ap-northeast-1");
        dto.getAccountId();
        dto.getInternetGatewayId();
        dto.getInternetGatewayName();
        dto.getRegion();
        dto.getVpcId();
        return dto;
    }

    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : AWS 인터넷 게이트웨이 목록 조회 결과 값 설정
    * @title : getResultInternetGatewayInfoList
    * @return : List<AwsInternetGatewayMgntVO>
    ***************************************************/
    private List<AwsInternetGatewayMgntVO> getResultInternetGatewayInfoList() {
        List<AwsInternetGatewayMgntVO> list = new ArrayList<AwsInternetGatewayMgntVO>();
        AwsInternetGatewayMgntVO vo = new AwsInternetGatewayMgntVO();
        vo.setAccountId(1);
        vo.setInternetGatewayId("itn-1111");
        vo.setInternetGatewayName("gate");
        vo.setRecid(1);
        vo.setStatus("avail");
        vo.setVpcId("vpcId");
        list.add(vo);
        return list;
    }
}
