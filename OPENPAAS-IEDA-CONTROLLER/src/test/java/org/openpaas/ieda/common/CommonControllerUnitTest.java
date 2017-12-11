package org.openpaas.ieda.common;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.common.web.common.service.CommonService;
import org.openpaas.ieda.controller.common.CommonController;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.amazonaws.regions.Region;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class CommonControllerUnitTest extends BaseControllerUnitTest{

    private MockMvc mockMvc;
    private Principal principal = null;
    
    @InjectMocks
    CommonController momckCommonController;
    @Mock
    CommonService mockCommonService;
    
    /*************************************** URL *******************************************/
    final static String IAAS_ACCOUNT_INFO_LIST_URL = "/common/deploy/accountList/{iaasType}";
    final static String AWS_REGION_INFO_LIST_URL = "/common/aws/region/list";
    final static String AWS_AVAILABILITY_ZONE_LIST_URL = "/common/aws/avaliabilityzone/list/{accountId}/{region}";
    final static String GOOGLE_ZONE_LIST_URL = "/common/google/zone/list/{accountId}";
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(momckCommonController).build();
        principal = getLoggined();
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 공통 인프라 계정 정보 조회
     * @title : testGetIaasAccountInfoList
     * @return : void
     ***************************************************/
     @Test
     public void testGetIaasAccountInfoList() throws Exception{
         String iaasType = "openstack";
         when(mockCommonService.getIaasAccountInfoList(iaasType, principal)).thenReturn(new ArrayList<HashMap<String, Object>>() );
         mockMvc.perform(get(IAAS_ACCOUNT_INFO_LIST_URL, iaasType).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
         .andExpect(status().isOk());
     }
     
     /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : AWS 리전 목록 조회
     * @title : testGetAwsRegionInfoList
     * @return : void
    ***************************************************/
    @Test
     public void testGetAwsRegionInfoList() throws Exception{
         when(mockCommonService.getAWSRegionList()).thenReturn(new ArrayList<Region>() );
         mockMvc.perform(get(AWS_REGION_INFO_LIST_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
         .andExpect(status().isOk());
     }
     
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : AWS 가용영역 목록 조회
     * @title : testGetAwsAvailabilityZoneInfoList
     * @return : void
    ***************************************************/
    @Test
    public void testGetAwsAvailabilityZoneInfoList() throws Exception{
        int accountId = 1;
        String region = "US_WEST_2";
        when(mockCommonService.getAWSAvailabilityZoneByRegion(principal, accountId, region)).thenReturn(new ArrayList<String>() );
        mockMvc.perform(get(AWS_AVAILABILITY_ZONE_LIST_URL, accountId, region).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Google 클라우드 영역 목록 조회
     * @title : testGetGoogleZoneInfoList
     * @return : void
    ***************************************************/
    @Test
    public void testGetGoogleZoneInfoList() throws Exception{
        int accountId = 1;
        when(mockCommonService.getGoogleZoneList(principal, accountId)).thenReturn(new ArrayList<String>() );
        mockMvc.perform(get(GOOGLE_ZONE_LIST_URL, accountId).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }

}
