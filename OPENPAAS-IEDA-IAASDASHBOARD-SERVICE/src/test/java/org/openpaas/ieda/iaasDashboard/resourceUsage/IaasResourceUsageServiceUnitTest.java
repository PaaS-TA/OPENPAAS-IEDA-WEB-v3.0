/*package org.openpaas.ieda.iaasDashboard.resourceUsage;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.common.web.common.dao.CommonDAO;
import org.openpaas.ieda.iaasDashboard.api.resourceUsage.IaasResourceUsageApiService;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.openpaas.ieda.iaasDashboard.web.resourceUsage.dao.IaasResourceUsageVO;
import org.openpaas.ieda.iaasDashboard.web.resourceUsage.service.IaasResourceUsageService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class IaasResourceUsageServiceUnitTest {

private Principal principal = null;
    
    @InjectMocks
    private IaasResourceUsageService mockIaasResourceUsageService;
    @Mock
    private CommonDAO mockCommonDao;
    @Mock
    private MessageSource mockMessageSource;
    @Mock
    private IaasResourceUsageApiService mockIaasResourceUsageApiService;
    @Mock
    private CommonIaasService mockCommonIaasService;
    
    
    *//***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 유닛 테스트가 실행되기 전 호출
     * @title : setUp
     * @return : void
    ***************************************************//*
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        principal = getLoggined();
    }
    
    *//***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Security token 생성
     * @title : getLoggined
     * @return : Principal
    ***************************************************//*
    public Principal getLoggined() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken("admin", "admin");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(auth);
        securityContext.getAuthentication().getPrincipal();
        return auth;
    }
    
    *//***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 인프라 전체 리소스 사용량 정보 조회
     * @title : testGetIaasResourceUsageTotalInfoCaseFromAWS
     * @return : void
    ***************************************************//*
    @Test
    public void testGetIaasResourceUsageTotal(){
        List<IaasResourceUsageVO> expectedVo = setAllIaasResourceUsageListInfo();
        HashMap<String, Object> awsInfo  = setAwsResourceInfo();
        HashMap<String, Object> openstackInfo = setOpenstackResourceInfo();
        
        when( mockCommonDao.selectAccountInfoList( anyString(), anyString() )).thenReturn(setAllAccountInfoList());
        when( mockCommonIaasService.getAwsRegionInfo(anyString()) ).thenReturn(Region.getRegion(Regions.US_WEST_2));
        when( mockMessageSource.getMessage(anyString(), any(), any()) ).thenReturn("US_WEST_2");
        when( mockIaasResourceUsageApiService.getResourceInfoFromAWS(anyString(), anyString(), any()) ).thenReturn(awsInfo);
        when( mockIaasResourceUsageApiService.getResourceInfoFromOpenstackV2(anyString(), anyString(), anyString(), anyString()) ).thenReturn(openstackInfo);
        List<IaasResourceUsageVO> resultList = mockIaasResourceUsageService.getIaasResourceUsageTotalInfo(principal);
        for( int i=0; i<resultList.size(); i++ ){
            assertEquals( expectedVo.get(i).getInstance(), resultList.get(i).getInstance() );
            assertEquals( expectedVo.get(i).getNetwork(), resultList.get(i).getNetwork() );
            assertEquals( expectedVo.get(i).getVolume(), resultList.get(i).getVolume() );
        }
        
        verify(mockCommonDao, times(2)).selectAccountInfoList(anyString(), anyString());
        verifyNoMoreInteractions(mockCommonDao);
    }
    
    *//***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 인프라 전체 리소스 사용량 정보 조회(AWS Exception)
     * @title : testGetIaasResourceUsageTotalCaseFromOpenstack
     * @return : void
    ***************************************************//*
    @Test(expected=CommonException.class)
    public void testGetIaasResourceUsageTotalAwsException(){
        HashMap<String, Object> openstackInfo = setOpenstackResourceInfo();
        
        when( mockCommonDao.selectAccountInfoList( anyString(), anyString() )).thenReturn(setAllAccountInfoList());
        when( mockCommonIaasService.getAwsRegionInfo(anyString()) ).thenReturn(Region.getRegion(Regions.US_WEST_2));
        when( mockIaasResourceUsageApiService.getResourceInfoFromAWS(anyString(), anyString(), any()) ).thenReturn(null);
        when( mockIaasResourceUsageApiService.getResourceInfoFromOpenstackV2(anyString(), anyString(), anyString(), anyString()) ).thenReturn(openstackInfo);
        mockIaasResourceUsageService.getIaasResourceUsageTotalInfo(principal);
        verify(mockCommonDao, times(2)).selectAccountInfoList(anyString(), anyString());
        verifyNoMoreInteractions(mockCommonDao);
    }
    
    *//***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 인프라 전체 리소스 사용량 정보 조회(Openstack Exception)
     * @title : testGetIaasResourceUsageTotalOpenstackException
     * @return : void
    ***************************************************//*
    @Test(expected=CommonException.class)
    public void testGetIaasResourceUsageTotalOpenstackException(){
        HashMap<String, Object> awsInfo  = setAwsResourceInfo();
        
        when( mockCommonDao.selectAccountInfoList( anyString(), anyString() )).thenReturn(setAllAccountInfoList());
        when( mockCommonIaasService.getAwsRegionInfo(anyString()) ).thenReturn(Region.getRegion(Regions.US_WEST_2));
        when( mockIaasResourceUsageApiService.getResourceInfoFromAWS(anyString(), anyString(), any()) ).thenReturn(awsInfo);
        when( mockIaasResourceUsageApiService.getResourceInfoFromOpenstackV2(anyString(), anyString(), anyString(), anyString()) ).thenReturn(null);
        when( mockMessageSource.getMessage(any(),any(),any()) ).thenReturn("us-west-2");
        mockIaasResourceUsageService.getIaasResourceUsageTotalInfo(principal);
        verify(mockCommonDao, times(2)).selectAccountInfoList(anyString(), anyString());
        verifyNoMoreInteractions(mockCommonDao);
    }
    
    
    *//***************************************************
     * @project : 인프라 관리 대시보드
     * @description : AWS 리소스 사용량 정보 조회 테스트 
     * @title : testGetAwsResourceUsageInfoList
     * @return : void
    ***************************************************//*
    @Test
    public void testGetAwsResourceUsageInfoList(){
        List<IaasResourceUsageVO> expectedVo = setAwsResourceUsageList();
        HashMap<String, Object> awsInfo  = setAwsResourceInfo();
        
        when( mockCommonDao.selectAccountInfoList( anyString(), anyString() )).thenReturn(setAwsAccountInfoList());
        when( mockCommonIaasService.getAwsRegionInfo(anyString()) ).thenReturn(Region.getRegion(Regions.US_WEST_2));
        when( mockIaasResourceUsageApiService.getResourceInfoFromAWS(anyString(), anyString(), any()) ).thenReturn(awsInfo);
        
        List<IaasResourceUsageVO> resultList = mockIaasResourceUsageService.getAwsResourceUsageInfoList("us-west-2", principal);
        for( int i=0; i<resultList.size(); i++ ){
            assertEquals( expectedVo.get(i).getIaasType(), resultList.get(i).getIaasType() );
            assertEquals( expectedVo.get(i).getInstance(), resultList.get(i).getInstance() );
            assertEquals( expectedVo.get(i).getNetwork(), resultList.get(i).getNetwork() );
            assertEquals( expectedVo.get(i).getNetwork(), resultList.get(i).getNetwork() );
            assertEquals( expectedVo.get(0).getVolume(), resultList.get(0).getVolume() );
        }
        
        verify(mockCommonDao, times(1)).selectAccountInfoList(anyString(), anyString());
        verifyNoMoreInteractions(mockCommonDao);
    }
    
    *//***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 오픈스택 리소스 사용량 정보 조회 테스트
     * @title : testGetOpenstackResourceUsageInfoList
     * @return : void
    ***************************************************//*
    @Test
    public void testGetOpenstackResourceUsageInfoList(){
        List<IaasResourceUsageVO> expectedVo = setOpenstackResourceUsageList();
        HashMap<String, Object> openstackInfo  = setOpenstackResourceInfo();
        
        when( mockCommonDao.selectAccountInfoList( anyString(), anyString() )).thenReturn(setAwsAccountInfoList());
        when( mockIaasResourceUsageApiService.getResourceInfoFromOpenstackV2(any(), any(), any(), any()) ).thenReturn(openstackInfo);
        when( mockMessageSource.getMessage(anyString(), any(), any()) ).thenReturn("Openstack");
        List<IaasResourceUsageVO> resultList = mockIaasResourceUsageService.getOpenstackResourceUsageInfoList(principal);
        for( int i=0; i<resultList.size(); i++ ){
            assertEquals( expectedVo.get(i).getIaasType(), resultList.get(i).getIaasType() );
            assertEquals( expectedVo.get(i).getInstance(), resultList.get(i).getInstance() );
            assertEquals( expectedVo.get(i).getNetwork(), resultList.get(i).getNetwork() );
            assertEquals( expectedVo.get(0).getVolume(), resultList.get(0).getVolume() );
        }
        
        verify(mockCommonDao, times(1)).selectAccountInfoList(anyString(), anyString());
        verifyNoMoreInteractions(mockCommonDao);
    }
    
    
    *//***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 인프라 전체 리소스 사용량 정보 설정
     * @title : setAllIaasResourceUsageListInfo
     * @return : List<IaasResourceUsageVO>
    ***************************************************//*
    public List<IaasResourceUsageVO> setAllIaasResourceUsageListInfo(){
        List<IaasResourceUsageVO> list = new ArrayList<IaasResourceUsageVO>();
        //AWS
        List<IaasResourceUsageVO> awsList = setAwsResourceUsageList();
        list.add( awsList.get(0) );
        //OPENSTACK
        List<IaasResourceUsageVO> openstackList = setOpenstackResourceUsageList();
        list.add( openstackList.get(0) );
        return list;
    }
    
    *//***************************************************
     * @project : 인프라 관리 대시보드
     * @description : AWS 리소스 사용량 목록 데이터 설정
     * @title : setAwsResourceUsageListInfo
     * @return : List<IaasResourceUsageVO>
    ***************************************************//*
    public List<IaasResourceUsageVO> setAwsResourceUsageList(){
        List<IaasResourceUsageVO> list = new ArrayList<IaasResourceUsageVO>();
        IaasResourceUsageVO awsVo = new IaasResourceUsageVO();
       //AWS
        awsVo.setAccountName("aws-test1");
        awsVo.setIaasType("AWS");
        awsVo.setInstance(10);
        awsVo.setVolume(300);
        awsVo.setNetwork(4);
        list.add(awsVo);
        
        return list;
    }
    
    *//***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Openstack 리소스 사용량 목록 데이터 설정
     * @title : setOpenstackResourceUsageList
     * @return : List<IaasResourceUsageVO>
    ***************************************************//*
    public List<IaasResourceUsageVO> setOpenstackResourceUsageList(){
        List<IaasResourceUsageVO> list = new ArrayList<IaasResourceUsageVO>();
        IaasResourceUsageVO openstackVo = new IaasResourceUsageVO();
        
        openstackVo.setAccountName("openstack-test2");
        openstackVo.setIaasType("Openstack");
        openstackVo.setInstance(10);
        openstackVo.setNetwork(20);
        openstackVo.setVolume(120);
        list.add(openstackVo);
        
        return list;
    }
    
    
    *//***************************************************
     * @project : 인프라 관리 대시보드
     * @description : AWS 계정 정보 설정
     * @title : setAccountInfoList
     * @return : List<HashMap<String,Object>>
    ***************************************************//*
    public List<HashMap<String, Object>> setAllAccountInfoList(){
        List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> awsAccount = new HashMap<String, Object>();
        awsAccount.put("id", 1);
        awsAccount.put("iaasType", "AllIaas");
        awsAccount.put("accountName", "all-test1");
        awsAccount.put("commonAccessEndpoint", "http://172.10.10.1:5000/v2.0");
        awsAccount.put("commonAccessUser", "AKIAIGL5JRHJATEST");
        awsAccount.put("commonAccessSecret","oDSAm1znlUFU62DHxV7Aa232EWEEWE" );
        awsAccount.put("openstackKeystoneVersion", "v2");
        awsAccount.put("commonTenant", "v2Tenant");
        awsAccount.put("openstackDomain",null);
        awsAccount.put("createUserId", "admin");
        awsAccount.put("updateUserId", "admin");
        list.add(awsAccount);
        
        return list;
    }
    
    *//***************************************************
     * @project : 인프라 관리 대시보드
     * @description : AWS 계정 정보 설정
     * @title : setAccountInfoList
     * @return : List<HashMap<String,Object>>
    ***************************************************//*
    public List<HashMap<String, Object>> setAwsAccountInfoList(){
        List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> awsAccount = new HashMap<String, Object>();
        awsAccount.put("id", 1);
        awsAccount.put("iaasType", "AllIaas");
        awsAccount.put("accountName", "iaas-test1");
        awsAccount.put("commonAccessEndpoint", "http://endPoint");
        awsAccount.put("commonAccessUser", "AKIAIGL5JRHJATEST");
        awsAccount.put("commonAccessSecret","oDSAm1znlUFU62DHxV7Aa232EWEEWE" );
        awsAccount.put("openstackKeystoneVersion", "v2");
        awsAccount.put("commonTenant", "tenant");
        awsAccount.put("openstackDomain",null);
        awsAccount.put("createUserId", "admin");
        awsAccount.put("updateUserId", "admin");
        list.add(awsAccount);
        
        return list;
    }
    
    *//***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Openstack 계정 정보 목록 데이터 설정
     * @title : setOpenstackAccountInfoList
     * @return : List<HashMap<String,Object>>
    ***************************************************//*
    public List<HashMap<String, Object>> setOpenstackAccountInfoList(){
        List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        
        HashMap<String, Object> opAccount = new HashMap<String, Object>();
        opAccount.put("id", 2);
        opAccount.put("iaasType", "Openstack");
        opAccount.put("accountName", "openstack-test2");
        opAccount.put("commonAccessEndpoint", "http://172.10.10.1:5000/v2.0");
        opAccount.put("commonAccessUser", "bosh-v2");
        opAccount.put("commonAccessSecret","secret-v2" );
        opAccount.put("openstackKeystoneVersion", "v2");
        opAccount.put("commonTenant", "v2Tenant");
        opAccount.put("openstackDomain",null);
        opAccount.put("createUserId", "admin");
        opAccount.put("updateUserId", "admin");
        list.add(opAccount);
        return list;
    }
    
    
    *//***************************************************
     * @project : 인프라 관리 대시보드
     * @description : AWS 리소스 정보 설정
     * @title : setResourceInfo
     * @return : HashMap<String,Object>
    ***************************************************//*
    public HashMap<String, Object> setAwsResourceInfo(){
        HashMap<String, Object> resource = new HashMap<String, Object>();
        resource.put("iaasType", "AWS");
        resource.put("instance", 10);
        resource.put("network", 4);
        resource.put("volume", 300);
        resource.put("billing", 300);
        
        return resource;
    }
    
    *//***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Openstack 리소스 정보 설정
     * @title : setOpenstackResourceInfo
     * @return : HashMap<String,Object>
    ***************************************************//*
    public HashMap<String, Object> setOpenstackResourceInfo(){
        HashMap<String, Object> resource = new HashMap<String, Object>();
        resource.put("iaasType","OPENSTACK");
        resource.put("instance",10);
        resource.put("network",20);
        resource.put("volume",120);
        
        return resource;
    }

}
*/