package org.openpaas.ieda.iaas.web.aws;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
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
import org.openpaas.ieda.controller.iaasMgnt.awsMgnt.web.elasticIp.AwsElasticIpMgntController;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.elasticIp.dao.AwsElasticIpMgntVO;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.elasticIp.service.AwsElasticIpMgntService;
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

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class AwsElasticIpMgntControllerUnitTest{
    @InjectMocks AwsElasticIpMgntController mockAwsElasticIpMgntController;
    @Mock AwsElasticIpMgntService mockAwsElasticIpMgntService;
    
    private MockMvc mockMvc;
    
    /*************************************** URL *******************************************/
    final static String VIEW_URL = "/awsMgnt/elasticIp";
    final static String ELASTIC_IP_LIST_INFO_URL = "/awsMgnt/elasticIp/list/{accountId}/{region}";
    final static String ELASTIC_IP_DETAIL_INFO_URL = "/awsMgnt/elasticIp/save/detail/{accountId}/{publicIp:.*}/{region}";
    final static String ELASTIC_IP_SAVE_URL = "/awsMgnt/elasticIp/save/{accountId}/{region}";
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockAwsElasticIpMgntController).build();
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
      * @description : AWS ElasticIp 관리 화면 이동 TEST
      * @title : testGoAwsElasticIpMgnt
      * @return : void
      ***************************************************/
      @Test
      public void testGoAwsElasticIpMgnt() throws Exception{
          mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
          .andExpect(status().isOk())
          .andExpect(view().name("iaas/aws/elasticIp/awsElasticIpMgnt"));
      }
      
      /***************************************************
       * @project : AWS 관리 대시보드
       * @description : AWS ElasticIp 목록 조회 TEST
       * @title : testGetAwsElasticIpInfoList
       * @return : void
       ***************************************************/
       @Test
       public void testGetAwsElasticIpInfoList() throws Exception{
           List<AwsElasticIpMgntVO> list = getElasticIpResultListInfo();
           when(mockAwsElasticIpMgntService.getAwsElasticIpInfoList(any(), anyInt(), anyString())).thenReturn(list);
           mockMvc.perform(get(ELASTIC_IP_LIST_INFO_URL, 1, "region").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.records[0].accountId").value(1))
           .andExpect(jsonPath("$.records[0].recid").value(1))
           .andExpect(jsonPath("$.records[0].publicIp").value("52.34.133.252"))
           .andExpect(jsonPath("$.records[0].allocationId").value("eipalloc-d8110ee2"))
           .andExpect(jsonPath("$.records[0].domain").value("vpc"));
           
       }
      
       /***************************************************
        * @project : AWS 관리 대시보드
        * @description : AWS ElasticIp 상세 조회 TEST
        * @title : testGetAwsElasticIpDetailInfo
        * @return : void
        ***************************************************/
        @Test
        public void testGetAwsElasticIpDetailInfo() throws Exception{
            HashMap<String, Object> map =  getElasticIpResultInfo();
            when(mockAwsElasticIpMgntService.getAwsElasticIpDetailInfo(anyInt(), anyString(), any(), anyString())).thenReturn(map);
            mockMvc.perform(get(ELASTIC_IP_DETAIL_INFO_URL, 1, "1580", "region").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accountId").value(map.get("accountId")))
            .andExpect(jsonPath("$.recid").value(map.get("recid")))
            .andExpect(jsonPath("$.publicIp").value(map.get("publicIp")))
            .andExpect(jsonPath("$.allocationId").value(map.get("allocationId")))
            .andExpect(jsonPath("$.domain").value(map.get("domain")))
            .andExpect(jsonPath("$.requestId").value(map.get("requestId")))
            .andExpect(jsonPath("$.associationId").value(map.get("associationId")))
            .andExpect(jsonPath("$.networkInterfaceId").value(map.get("networkInterfaceId")))
            .andExpect(jsonPath("$.networkInterfaceOwner").value(map.get("networkInterfaceOwner")))
            .andExpect(jsonPath("$.instanceId").value(map.get("instanceId")))
            .andExpect(jsonPath("$.publicDns").value(map.get("publicDns")))
            .andExpect(jsonPath("$.privateIpAddress").value(map.get("privateIpAddress")));
        }
        
        /***************************************************
         * @project : AWS 관리 대시보드
         * @description : AWS ElasticIP 할당 TEST
         * @title : testSaveAwsElasticIpInfo
         * @return : void
         ***************************************************/
         @Test
         public void testSaveAwsElasticIpInfo() throws Exception{
             mockMvc.perform(post(ELASTIC_IP_SAVE_URL, 1, "region")).andDo(MockMvcResultHandlers.print())
             .andExpect(status().isCreated());
         }
        
        
       /***************************************************
        * @project : AWS 관리 대시보드
        * @description : AWS ElasticIp 목록 조회 결과 값 설정
        * @title : getVpcResultInfo
        * @return : List<AwsVpcMgntVO> 
        ***************************************************/
        private List<AwsElasticIpMgntVO> getElasticIpResultListInfo() {
            List<AwsElasticIpMgntVO> list = new ArrayList<AwsElasticIpMgntVO>();
            AwsElasticIpMgntVO vo = new AwsElasticIpMgntVO();
            vo.setAccountId(1);
            vo.setPublicIp("52.34.133.252");
            vo.setAllocationId("eipalloc-d8110ee2");
            vo.setDomain("vpc");
            vo.setRecid(1);
            list.add(vo);
            return list;
        }
        

        /***************************************************
        * @project : AWS 관리 대시보드
        * @description : AWS VPC 상세 조회 결과 값 설정
        * @title : getElasticIpResultInfo
        * @return : AwsVpcMgntVO
        ***************************************************/
        private HashMap<String, Object> getElasticIpResultInfo() {
            AwsElasticIpMgntVO vo =new AwsElasticIpMgntVO();
            vo.setAccountId(1);
            vo.setPublicIp("52.34.133.252");
            vo.setAllocationId("eipalloc-d8110ee2");
            vo.setDomain("vpc");
            vo.setRequestId("-");
            vo.setAssociationId("-");
            vo.setNetworkInterfaceId("-");
            vo.setNetworkInterfaceOwner("-");
            vo.setInstanceId("-");
            vo.setPublicDns("-");
            vo.setPrivateIpAddress("-");
            vo.setRecid(1);
            
            HashMap<String, Object> map = new  HashMap<String, Object>();
            map.put("publicIp", vo.getPublicIp());
            map.put("allocationId", vo.getAllocationId());
            map.put("domain", vo.getDomain());
            map.put("requestId", vo.getRequestId());
            map.put("associationId", vo.getAssociationId());
            map.put("networkInterfaceId", vo.getNetworkInterfaceId());
            map.put("networkInterfaceOwner", vo.getNetworkInterfaceOwner());
            map.put("instanceId", vo.getInstanceId());
            map.put("publicDns", vo.getPublicDns());
            map.put("privateIpAddress", vo.getPrivateIpAddress());
            map.put("recid", vo.getRecid());
            
           
            return map;
        }
}