package org.openpaas.ieda.iaasDashboard.awsMgnt.web.keypair.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.common.base.BaseAwsMgntControllerUnitTest;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.keypair.service.AwsKeypairMgntApiService;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.keypair.service.AwsKeypairMgntService;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.appstream.model.Application;
import com.amazonaws.services.ec2.model.KeyPairInfo;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class AwsKeyPairMgntServiceUnitTest extends BaseAwsMgntControllerUnitTest{

    private Principal principal = null;
    
    
    @InjectMocks AwsKeypairMgntService mockAwsKeypairMgntService;
    @Mock AwsKeypairMgntApiService mockAwsKeypairMgntApiService;
    @Mock CommonIaasService mockCommonIaasService;
    @Mock MessageSource mockMessageSource;
    
    /**********************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
     **********************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        principal = getLoggined();
    }
    
    /**********************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS KeyPair 목록 조회 TEST
     * @title : testGetAwsKeyPairInfoList
     * @return : void
     **********************************************/
    @Test
    public void testGetAwsKeyPairInfoList(){
        List<KeyPairInfo> awsKeyList = getKeyPairInfoList();
        when(mockAwsKeypairMgntApiService.getAwsKeypairInfoListApiFromAws(any(), anyString())).thenReturn(awsKeyList);
        when(mockCommonIaasService.getAwsRegionInfo(any())).thenReturn(Region.getRegion(Regions.fromName("us-west-2")));
        List<KeyPairInfo> resultList = mockAwsKeypairMgntService.getAwsKeyPairInfoList(principal, 1, "us-west-2");
        assertEquals(resultList.size(), 1);
        assertEquals(resultList.get(0).getKeyName(), awsKeyList.get(0).getKeyName());
        assertEquals(resultList.get(0).getKeyFingerprint(), awsKeyList.get(0).getKeyFingerprint());
    }
    

     
     /***************************************************
     * @project : AWS 인프라 관리 대생성시보드
     * @description : AWS KeyPair 생성 TEST
     * @title : testCreateAwsKeyPair
     * @return : void
     ***************************************************/
     @Test
     public void testCreateAwsKeyPair(){
         HttpServletResponse req = new MockHttpServletResponse();
         List<KeyPairInfo> awsKeyList = getKeyPairInfoList();
         when(mockAwsKeypairMgntApiService.getAwsKeypairInfoListApiFromAws(any(), anyString())).thenReturn(awsKeyList);
         when(mockCommonIaasService.getAwsRegionInfo(any())).thenReturn(Region.getRegion(Regions.fromName("us-west-2")));
         when(mockAwsKeypairMgntApiService.createAwsKeypairApiFromAws(any(), any(), any())).thenReturn("abc");
         mockAwsKeypairMgntService.createAwsKeyPair(principal, "1", "2", "us-west-2", req);
     }
     
     @Test(expected=CommonException.class)
     public void testCreateAwsKeyPairFromException(){
         HttpServletResponse response = new MockHttpServletResponse();
         List<KeyPairInfo> awsKeyList = getKeyPairInfoList();
         when(mockAwsKeypairMgntApiService.getAwsKeypairInfoListApiFromAws(any(), anyString())).thenReturn(awsKeyList);
         when(mockCommonIaasService.getAwsRegionInfo(any())).thenReturn(Region.getRegion(Regions.fromName("us-west-2")));
         when(mockAwsKeypairMgntApiService.createAwsKeypairApiFromAws(any(), any(), any())).thenReturn("");
         mockAwsKeypairMgntService.createAwsKeyPair(principal, "1", "2", "us-west-2", response);
     }
     
     @Test(expected=CommonException.class)
     public void testCreateAwsKeyPairFromDuplication(){
         HttpServletResponse response = new MockHttpServletResponse();
         List<KeyPairInfo> awsKeyList = getKeyPairInfoList();
         when(mockAwsKeypairMgntApiService.getAwsKeypairInfoListApiFromAws(any(), anyString())).thenReturn(awsKeyList);
         when(mockCommonIaasService.getAwsRegionInfo(any())).thenReturn(Region.getRegion(Regions.fromName("us-west-2")));
         when(mockAwsKeypairMgntApiService.createAwsKeypairApiFromAws(any(), any(), any())).thenReturn("");
         mockAwsKeypairMgntService.createAwsKeyPair(principal, "1", awsKeyList.get(0).getKeyName(), "us-west-2", response);
     }
     /***************************************************
    * @project : AWS 인프라 관리 대시보드
    * @description : AWS KeyPair 조회 목설정값
    * @title : getAwsKeyPairInfoList
    * @return : List<KeyPairInfo>
      ***************************************************/
    public List<KeyPairInfo> getKeyPairInfoList(){
        List<KeyPairInfo> testKeyPairInfoList = new ArrayList<KeyPairInfo>();
        KeyPairInfo keyPairInfo = new KeyPairInfo();
        keyPairInfo.setKeyName("testKeyPairName");
        keyPairInfo.setKeyFingerprint("45:41:74:ed:86:d8:63:e1:1c:2e:04:57:6e:92:60:b5:4e:e5:4d:ec");
        testKeyPairInfoList.add(keyPairInfo);
        return testKeyPairInfoList;
    }
    
}
