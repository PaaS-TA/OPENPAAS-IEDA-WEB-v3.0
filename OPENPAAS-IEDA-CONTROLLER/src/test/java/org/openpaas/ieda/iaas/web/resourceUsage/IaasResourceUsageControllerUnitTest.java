package org.openpaas.ieda.iaas.web.resourceUsage;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import org.openpaas.ieda.controller.iaasMgnt.dashboard.web.resource.IaasResourceUsageController;
import org.openpaas.ieda.iaasDashboard.web.resourceUsage.dao.IaasResourceUsageVO;
import org.openpaas.ieda.iaasDashboard.web.resourceUsage.service.IaasResourceUsageService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class IaasResourceUsageControllerUnitTest {
    
    private MockMvc mockMvc;
    
    @InjectMocks
     IaasResourceUsageController mockIaasResourceUsageController;
    @Mock
     IaasResourceUsageService mockIaasResourceUsageService;

    /*************************************** URL *******************************************/
    final static String VIEW_DASHBOARD_URL = "/iaasMgnt/main/dashboard"; //Iaas 리소스 사용량 화면 요청
    final static String VIEW_AWS_URL = "/iaasMgnt/resourceUsage/aws"; //AWS 리소스 사용량  화면 요청
    final static String VIEW_OPENSTACK_URL = "/iaasMgnt/resourceUsage/openstack"; //Openstack 리소스 사용량 화면 요청
    final static String IAAS_RESOURCE_USAGE_LIST_URL = "/iaasMgnt/resourceUsage/all/list"; //전체 인프라 리소스 사용량 조회
    final static String AWS_RESOURCE_USAGE_LIST_URL = "/iaasMgnt/resourceUsage/aws/list/{region}"; // AWS 리소스 사용량 조회
    final static String OPENSTACK_RESOURCE_USAGE_LIST_URL = "/iaasMgnt/resourceUsage/openstack/list"; //Openstack 리소스 사용량 조회
    
    /***************************************************
     * @project : 인프라 계정 관리 대시보드
     * @description   : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockIaasResourceUsageController).build();
    }
    
    
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 대시보드 화면 호출
     * @title : testGoIaasResourceUsage
     * @return : void
    ***************************************************/
    @Test
    public void testGoIaasResourceUsage() throws Exception{
        mockMvc.perform(get(VIEW_DASHBOARD_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/iaas/resourceUsage/dashboard"));
    }
   
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : AWS 리소스 사용량 조회 화면 호출
     * @title : testGoAwsResourceUsagee
     * @return : void
    ***************************************************/
    @Test
    public void testGoAwsResourceUsagee() throws Exception{
        mockMvc.perform(get(VIEW_AWS_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/iaas/resourceUsage/awsResourceUsage"));
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : OPenstack 리소스 사용량 조회 화면 호출
     * @title : testOpenstackResourceUsage
     * @return : void
    ***************************************************/
    @Test
    public void testOpenstackResourceUsage() throws Exception{
        mockMvc.perform(get(VIEW_OPENSTACK_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/iaas/resourceUsage/openstackResourceUsage"));
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 인프라 전체 리소스 사용량 조회
     * @title : testGetIaasResourceUsageTotalInfo
     * @return : void
    ***************************************************/
    @Test
    public void testGetIaasResourceUsageTotalInfo() throws Exception{
        List<IaasResourceUsageVO> list = setIaasResourceUsageInfoList();
        when(mockIaasResourceUsageService.getIaasResourceUsageTotalInfo(any())).thenReturn(list);
        mockMvc.perform(get(IAAS_RESOURCE_USAGE_LIST_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[0].accountName").value(list.get(0).getAccountName()))
        .andExpect(jsonPath("$.[0].iaasType").value(list.get(0).getIaasType()))
        .andExpect(jsonPath("$.[0].instance").value((int)list.get(0).getInstance()))
        .andExpect(jsonPath("$.[0].network").value((int)list.get(0).getNetwork()))
        .andExpect(jsonPath("$.[0].volume").value((int)list.get(0).getVolume()))
        .andExpect(jsonPath("$.[1].accountName").value(list.get(1).getAccountName()))
        .andExpect(jsonPath("$.[1].iaasType").value(list.get(1).getIaasType()))
        .andExpect(jsonPath("$.[1].instance").value((int)list.get(1).getInstance()))
        .andExpect(jsonPath("$.[1].network").value((int)list.get(1).getNetwork()))
        .andExpect(jsonPath("$.[1].volume").value((int)list.get(1).getVolume()))
        .andExpect(jsonPath("$.[2].accountName").value(list.get(2).getAccountName()))
        .andExpect(jsonPath("$.[2].iaasType").value(list.get(2).getIaasType()))
        .andExpect(jsonPath("$.[2].instance").value((int)list.get(2).getInstance()))
        .andExpect(jsonPath("$.[2].network").value((int)list.get(2).getNetwork()))
        .andExpect(jsonPath("$.[2].volume").value((int)list.get(2).getVolume()))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : AWS 리소스 사용량 목록 조회
     * @title : TestgetAwsResourceUsageInfoList
     * @return : void
    ***************************************************/
    @Test
    public void testGetAwsResourceUsageInfoList() throws Exception{
        List<IaasResourceUsageVO> list = setAwsResourceUsageInfoList();
        when(mockIaasResourceUsageService.getAwsResourceUsageInfoList(any(), any())).thenReturn(list);
        mockMvc.perform(get(AWS_RESOURCE_USAGE_LIST_URL, "us-west-1").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[0].accountName").value(list.get(0).getAccountName()))
        .andExpect(jsonPath("$.[0].iaasType").value( list.get(0).getIaasType()))
        .andExpect(jsonPath("$.[0].instance").value( (int)list.get(0).getInstance()))
        .andExpect(jsonPath("$.[0].network").value( (int)list.get(0).getNetwork()))
        .andExpect(jsonPath("$.[0].volume").value( (int)list.get(0).getVolume()))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        
        
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Openstack 리소스 사용량 조회
     * @title : TestgetOpenstackResourceUsageInfoList
     * @return : void
    ***************************************************/
    @Test
    public void testGetOpenstackResourceUsageInfoList() throws Exception{
        List<IaasResourceUsageVO> list = setOpenstackResourceUsageInfoList();
        when(mockIaasResourceUsageService.getOpenstackResourceUsageInfoList(any())).thenReturn(list);
        mockMvc.perform(get(OPENSTACK_RESOURCE_USAGE_LIST_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[0].accountName").value(list.get(0).getAccountName()))
        .andExpect(jsonPath("$.[0].iaasType").value(list.get(0).getIaasType()))
        .andExpect(jsonPath("$.[0].instance").value((int)list.get(0).getInstance()))
        .andExpect(jsonPath("$.[0].network").value((int)list.get(0).getNetwork()))
        .andExpect(jsonPath("$.[0].volume").value((int)list.get(0).getVolume()))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 전체 인프라 리소스 사용량 목록 정보 설정
     * @title : setIaasResourceUsageInfoList
     * @return : List<IaasResourceUsageVO>
    ***************************************************/
    public List<IaasResourceUsageVO> setIaasResourceUsageInfoList(){
        List<IaasResourceUsageVO> list = new ArrayList<IaasResourceUsageVO>();
        IaasResourceUsageVO vo = new IaasResourceUsageVO();
        //AWS
        vo.setAccountName("aws-test1");
        vo.setIaasType("AWS");
        vo.setInstance(4);
        vo.setVolume(300);
        vo.setNetwork(15);
        list.add(vo);
        //OPENSTACK
        vo.setAccountName("openstack-test2");
        vo.setIaasType("Openstack");
        vo.setInstance(10);
        vo.setNetwork(20);
        vo.setVolume(120);
        list.add(vo);
        
        vo.setAccountName("azure-test3");
        vo.setIaasType("Azure");
        vo.setInstance(3);
        vo.setNetwork(10);
        vo.setVolume(150);
        list.add(vo);
        return list;
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : AWS 리소스 사용 목록 정보 설정
     * @title : setAWsResourceUsageInfoList
     * @return : List<IaasResourceUsageVO>
    ***************************************************/
    public List<IaasResourceUsageVO> setAwsResourceUsageInfoList(){
        List<IaasResourceUsageVO> list = new ArrayList<IaasResourceUsageVO>();
        IaasResourceUsageVO vo = new IaasResourceUsageVO();
        //AWS
        vo.setAccountName("aws-test1");
        vo.setIaasType("AWS");
        vo.setInstance(Long.parseLong("4"));
        vo.setVolume(Long.parseLong("300"));
        vo.setNetwork(Long.parseLong("15"));
        list.add(vo);
        return list;
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : OPENSTACK 리소스 사용 목록 정보 설정
     * @title : setOpenstackResourceUsageInfoList
     * @return : List<IaasResourceUsageVO>
    ***************************************************/
    public List<IaasResourceUsageVO> setOpenstackResourceUsageInfoList(){
        List<IaasResourceUsageVO> list = new ArrayList<IaasResourceUsageVO>();
        IaasResourceUsageVO vo = new IaasResourceUsageVO();
        vo.setAccountName("openstack-test2");
        vo.setIaasType("Openstack");
        vo.setInstance(10);
        vo.setNetwork(20);
        vo.setVolume(120);
        list.add(vo);
        
        return list;
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : AZURE 리소스 사용 목록 정보 설정
     * @title : setAzureResourceUsageInfoList
     * @return : List<IaasResourceUsageVO>
    ***************************************************/
    public List<IaasResourceUsageVO> setAzureResourceUsageInfoList(){
        List<IaasResourceUsageVO> list = new ArrayList<IaasResourceUsageVO>();
        IaasResourceUsageVO vo = new IaasResourceUsageVO();
        vo.setAccountName("azure-test3");
        vo.setIaasType("Azure");
        vo.setInstance(3);
        vo.setNetwork(10);
        vo.setVolume(150);
        list.add(vo);
        
        return list;
    }
}
