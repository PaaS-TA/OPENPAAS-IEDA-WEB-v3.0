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
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.awsMgnt.web.subnet.dao.AwsSubnetMgntVO;
import org.openpaas.ieda.awsMgnt.web.subnet.dto.AwsSubnetMgntDTO;
import org.openpaas.ieda.awsMgnt.web.subnet.service.AwsSubnetMgntService;
import org.openpaas.ieda.awsMgnt.web.vpc.dao.AwsVpcMgntVO;
import org.openpaas.ieda.awsMgnt.web.vpc.service.AwsVpcMgntService;
import org.openpaas.ieda.common.BaseControllerUnitTest;
import org.openpaas.ieda.controller.iaasMgnt.awsMgnt.web.subnet.AwsSubnetMgntController;
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
public class AwsSubnetMgntControllerUnitTest extends BaseControllerUnitTest{
    
    @InjectMocks AwsSubnetMgntController mockAwsSubnetMgntController;
    @Mock AwsSubnetMgntService mockAwsSubnetMgntService;
    @Mock AwsVpcMgntService mockAwsVpcMgntService;
    private MockMvc mockMvc;
    private Principal principal = null;
    private static final ObjectMapper mapper = new ObjectMapper();
    
    
    /*************************************** URL *******************************************/
    final static String VIEW_URL = "/awsMgnt/subnet";
    final static String SUBNET_LIST_INFO_URL = "/awsMgnt/subnet/list/{accountId}/{region}";
    final static String SUBNET_DETAIL_INFO_URL = "/awsMgnt/subnet/save/detail/{accountId}/{subnetId:.*}/{region}";
    final static String SUBNET_SAVE_URL = "/awsMgnt/subnet/save";
    final static String SUBNET_DELETE_URL = "/awsMgnt/subnet/delete";
    final static String AWS_VPC_INFO_URL = "/awsMgnt/subnet/save/vpcs/{accountId}/{region}";
    
    /****************************************************************
     * @project : AWS 관리 대시보드
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockAwsSubnetMgntController).build();
        principal = getLoggined();
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
      * @description : AWS Subnet 관리 화면 이동 TEST
      * @title : testGoAwsSubnetMgnt
      * @return : void
      ***************************************************/
      @Test
      public void testGoAwsSubnetMgnt() throws Exception{
          mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
          .andExpect(status().isOk())
          .andExpect(view().name("iaas/aws/subnet/awsSubnetMgnt"));
      }

      /***************************************************
       * @project : AWS 관리 대시보드
       * @description : AWS Subnet 목록 조회 TEST
       * @title : testGetAwsSubnetInfoList
       * @return : void
       ***************************************************/
       @Test
       public void testGetAwsSubnetInfoList() throws Exception{
           List<AwsSubnetMgntVO> list = getSubnetResultListInfo();
           when(mockAwsSubnetMgntService.getAwsSubnetInfoList(any(), anyInt(), anyString())).thenReturn(list);
           mockMvc.perform(get(SUBNET_LIST_INFO_URL, 1, "region").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.records[0].accountId").value(1))
           .andExpect(jsonPath("$.records[0].recid").value(1))
           .andExpect(jsonPath("$.records[0].subnetId").value("subnet-33626875"))
           .andExpect(jsonPath("$.records[0].vpcId").value("vpcId"))
           .andExpect(jsonPath("$.records[0].cidrBlock").value("172.31.0.0/20"))
           .andExpect(jsonPath("$.records[0].state").value("available"))
           .andExpect(jsonPath("$.records[0].availabilityZone").value("us-west-2c"));
       }
       
       /***************************************************
        * @project : AWS 관리 대시보드
        * @description : AWS SUBNET 상세 조회 TEST
        * @title : testGetAwsSubnetDetailInfo
        * @return : void
        ***************************************************/
        @Test
        public void testGetAwsVpcDetailInfo() throws Exception{
            HashMap<String, Object> map = getSubnetResultInfo();
            when(mockAwsSubnetMgntService.getAwsSubnetDetailInfo(anyInt(), anyString(), any(), anyString())).thenReturn(map);
            mockMvc.perform(get(SUBNET_DETAIL_INFO_URL, 1, "2231", "region").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accountId").value(map.get("accountId")))
            .andExpect(jsonPath("$.recid").value(map.get("recid")))
            .andExpect(jsonPath("$.state").value(map.get("state")))
            .andExpect(jsonPath("$.vpcId").value(map.get("vpcId")))
            .andExpect(jsonPath("$.ipv4CidrBlock").value(map.get("ipv4CidrBlock")))
            .andExpect(jsonPath("$.subnetId").value(map.get("subnetId")))
            .andExpect(jsonPath("$.availabilityZone").value(map.get("availabilityZone")))
            .andExpect(jsonPath("$.ipv6CidrBlock").value(map.get("ipv6CidrBlock")))
            .andExpect(jsonPath("$.defaultForAz").value(map.get("defaultForAz")))
            .andExpect(jsonPath("$.assignIpv6AddressOnCreation").value(map.get("assignIpv6AddressOnCreation")))
            .andExpect(jsonPath("$.availableIpAddressCount").value(map.get("availableIpAddressCount")))
            .andExpect(jsonPath("$.mapPublicIpOnLaunch").value(map.get("mapPublicIpOnLaunch")))
            .andExpect(jsonPath("$.nameTag").value(map.get("nameTag")))
            .andExpect(jsonPath("$.defaultForAz").value(map.get("defaultForAz")));
            
        }
        
        /***************************************************
         * @project : AWS 관리 대시보드
         * @description : AWS VPC 생성 TEST
         * @title : testSaveAwsSubnetInfo
         * @return : void
         ***************************************************/
         @Test
         public void testSaveAwsSubnetInfo() throws Exception{
             AwsSubnetMgntDTO dto = setSubnetInfo();
             mockMvc.perform(post(SUBNET_SAVE_URL).contentType(MediaType.APPLICATION_JSON)
                     .content(mapper.writeValueAsBytes(dto))).andDo(MockMvcResultHandlers.print())
             .andExpect(status().isCreated());
         }
         
        /***************************************************
         * @project : AWS 관리 대시보드
         * @description : AWS Subnet 삭제 TEST
         * @title : testDeleteAwsSubnetInfo
         * @return : void
         ***************************************************/
         @Test
         public void testDeleteAwsSubnetInfo() throws Exception{
             AwsSubnetMgntDTO dto = setSubnetInfo();
             mockMvc.perform(delete(SUBNET_DELETE_URL).contentType(MediaType.APPLICATION_JSON)
                     .content(mapper.writeValueAsBytes(dto))).andDo(MockMvcResultHandlers.print())
             .andExpect(status().isNoContent());
         }
        
         /***************************************************
         * @project : Paas 플랫폼 설치 자동화
         * @description : AWS Subnet 생성을 위한 VPC 조회
         * @title : testDeleteAwsSubnetInfo
         * @return : void
        ***************************************************/
        @Test
         public void testGetAwsVpc() throws Exception{
             int accountId = 1;
             String region = "US_WEST_1";
             when(mockAwsVpcMgntService.getAwsVpcInfoList(accountId, region, principal)).thenReturn(new ArrayList<AwsVpcMgntVO>());
             mockMvc.perform(get(AWS_VPC_INFO_URL, accountId, region).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
                     .andExpect(status().isCreated());
         }
        
       /***************************************************
        * @project : AWS 관리 대시보드
        * @description : AWS Subnet 목록 조회 결과 값 설정
        * @title : getSubnetResultInfo
        * @return : List<AwsSubnetMgntVO> 
        ***************************************************/
        private List<AwsSubnetMgntVO> getSubnetResultListInfo() {
            List<AwsSubnetMgntVO> list = new ArrayList<AwsSubnetMgntVO>();
            AwsSubnetMgntVO vo = new AwsSubnetMgntVO();
            vo.setAccountId(1);
            vo.setSubnetId("subnet-33626875");
            vo.setState("available");
            vo.setVpcId("vpcId");
            vo.setCidrBlock("172.31.0.0/20");
            vo.setAvailabilityZone("us-west-2c");
            vo.setRecid(1);
            list.add(vo);
            return list;
        }
        
        /***************************************************
         * @project : AWS 관리 대시보드
         * @description : AWS Subnet 상세 조회 결과 값 설정
         * @title : setSubnetResultInfo
         * @return : AwsSubnetMgntVO
         ***************************************************/
         private HashMap<String, Object>  getSubnetResultInfo() {
             AwsSubnetMgntVO vo = new AwsSubnetMgntVO();
             vo.setAccountId(1);
             vo.setSubnetId("subnet-33626875");
             vo.setState("available");
             vo.setVpcId("vpcId");
             vo.setCidrBlock("172.31.0.0/20");
             vo.setAvailabilityZone("us-west-2c");
             vo.setIpv6CidrBlock("-");
             vo.setDefaultForAz(true);
             vo.setMapPublicIpOnLaunch(true);
             vo.setAssignIpv6AddressOnCreation(true);
             vo.setAvailableIpAddressCount(4091);
             vo.setMapPublicIpOnLaunch(true);
             vo.setNameTag("aaa");
             vo.setRecid(1);
             
             HashMap<String, Object> map = new  HashMap<String, Object>();
             map.put("accountId", vo.getAccountId());
             map.put("subnetId", vo.getSubnetId());
             map.put("state", vo.getState());
             map.put("vpcId", vo.getVpcId());
             map.put("cidrBlock", vo.getCidrBlock());
             map.put("availabilityZone", vo.getAvailabilityZone());
             map.put("ipv6CidrBlock", vo.getIpv6CidrBlock());
             map.put("defaultForAz", vo.isDefaultForAz());
             map.put("publicIpOnLaunch", vo.isMapPublicIpOnLaunch());
             map.put("assignIpv6AddressOnCreation", vo.isAssignIpv6AddressOnCreation());
             map.put("availableIpAddressCount", vo.getAvailableIpAddressCount());
             map.put("mapPublicIpOnLaunch", vo.isMapPublicIpOnLaunch());
             map.put("nameTag", vo.getNameTag());
             map.put("recid", vo.getRecid());
             
             return map;
         }

         /***************************************************
          * @project : AWS 관리 대시보드
          * @description : AWS Subnet 생성 값 설정
          * @title : setSaveVpcInfo
          * @return : AwsVpcMgntDTO
          ***************************************************/
          private AwsSubnetMgntDTO setSubnetInfo() {
              AwsSubnetMgntDTO dto = new AwsSubnetMgntDTO();
              dto.setAccountId(1);
              dto.setVpcId("vpcId");
              dto.setAvailabilityZone("us-west-2c");
              dto.setCidrBlock("172.31.0.0/20");
              dto.setRegion("region");
              dto.getAccountId();
              dto.getVpcId();
              dto.getAvailabilityZone();
              dto.getCidrBlock();
              dto.getRecid();
              dto.getRegion();
              return null;
          }
      
}