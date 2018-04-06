package org.openpaas.ieda.iaasDashboard.account;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Application;

import org.apache.commons.codec.binary.Base64;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.iaasDashboard.api.account.IaasAccountMgntApiService;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntDAO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.account.dto.IaasAccountMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.account.service.IaasAccountMgntService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class IaasAccountMgntServiceUnitTest {
    private Principal principal = null;
    
    @InjectMocks
    private IaasAccountMgntService mockIaasAccountMgntService;
    @Mock
    private IaasAccountMgntDAO mockIaasAccountMgntDao;
    @Mock
    private MessageSource mockMessageSource;
    @Mock
    private IaasAccountMgntApiService mockIaasAccountMgntApiService;
    
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 유닛 테스트가 실행되기 전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        principal = getLoggined();
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Security token 생성
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
     * @project : 인프라 관리 대시보드
     * @description : 전체 Iaas Account 목록 정보 조회
     * @title : testGetAllIaasAccountInfoList
     * @return : void
    ***************************************************/
    @Test
    public void testGetAllIaasAccountInfoList(){
        
        List<IaasAccountMgntVO> list = setAllIaasAccountMgntListInfo();
        when(mockIaasAccountMgntDao.selectAllIaasAccountInfoList(principal.getName())).thenReturn(list);
        
        List<IaasAccountMgntVO> resultList = mockIaasAccountMgntService.getAllIaasAccountInfoList(principal);
        for( int i=0; i<resultList.size(); i++ ){
            assertEquals(list.size(), resultList.size());
            assertEquals(list.get(i).getIaasType(), list.get(i).getIaasType());
            assertEquals(list.get(i).getAccountName(), resultList.get(i).getAccountName());
            assertEquals(list.get(i).getCommonAccessEndpoint(), resultList.get(i).getCommonAccessEndpoint());
            assertEquals(list.get(i).getCommonAccessUser(), resultList.get(i).getCommonAccessUser());
            assertEquals(list.get(i).getCommonAccessSecret(), resultList.get(i).getCommonAccessSecret());
            assertEquals(list.get(i).getCommonTenant(), resultList.get(i).getCommonTenant());
            assertEquals(list.get(i).getOpenstackKeystoneVersion(), resultList.get(i).getOpenstackKeystoneVersion());
            assertEquals(list.get(i).getCommonProject(), resultList.get(i).getCommonProject());
            assertEquals(list.get(i).getOpenstackDomain(), resultList.get(i).getOpenstackDomain());
            assertEquals(list.get(i).getStatus(), resultList.get(i).getStatus());
            assertEquals(list.get(i).getCreateUserId(), resultList.get(i).getCreateUserId());
            assertEquals(list.get(i).getUpdateUserId(), resultList.get(i).getUpdateUserId());
        }
        
        verify(mockIaasAccountMgntDao, times(1)).selectAllIaasAccountInfoList(principal.getName());
        verifyNoMoreInteractions(mockIaasAccountMgntDao);
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 인프라 별 계정 개수 조회
     * @title : testGetIaasAccountCount
     * @return : void
    ***************************************************/
    @Test
    public void testGetIaasAccountCount(){
        HashMap<String, Integer>  list = setIaasAccountCountInfo();
        when(mockIaasAccountMgntDao.selectIaasAccountCount(principal.getName())).thenReturn(list);
        HashMap<String, Integer> result = mockIaasAccountMgntService.getIaasAccountCount(principal); //요청
        for( int i=0; i< list.size(); i++ ){
            assertEquals(list.size(), result.size());
            assertEquals(list.get("aws_cnt"), result.get("aws_cnt"));
            assertEquals(list.get("openstack_cnt"), result.get("openstack_cnt"));
            assertEquals(list.get("vsphere_cnt"), result.get("vsphere_cnt"));
        }
        verify(mockIaasAccountMgntDao, times(1)).selectIaasAccountCount(principal.getName());
        verifyNoMoreInteractions(mockIaasAccountMgntDao);
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 해당 인프라 계정 정보 목록 조회
     * @title : testGetIaasAccountMgntInfoList
     * @return : void
    ***************************************************/
    @Test
    public void testGetIaasAccountInfoList(){

        List<IaasAccountMgntVO> list = setIaasAccountInfoList();
        when(mockIaasAccountMgntDao.selectIaasAccountInfoList(principal.getName(), "openstack")).thenReturn(list);
        
        List<IaasAccountMgntVO> resultList = mockIaasAccountMgntService.getIaasAccountInfoList("openstack", principal);
        for( int i=0; i<list.size(); i++ ){
            assertEquals(list.size(), resultList.size());
            assertEquals(list.get(i).getIaasType(), list.get(i).getIaasType());
            assertEquals(list.get(i).getAccountName(), resultList.get(i).getAccountName());
            assertEquals(list.get(i).getCommonAccessEndpoint(), resultList.get(i).getCommonAccessEndpoint());
            assertEquals(list.get(i).getCommonAccessUser(), resultList.get(i).getCommonAccessUser());
            assertEquals(list.get(i).getCommonAccessSecret(), resultList.get(i).getCommonAccessSecret());
            assertEquals(list.get(i).getCommonTenant(), resultList.get(i).getCommonTenant());
            assertEquals(list.get(i).getOpenstackKeystoneVersion(), resultList.get(i).getOpenstackKeystoneVersion());
            assertEquals(list.get(i).getCommonProject(), resultList.get(i).getCommonProject());
            assertEquals(list.get(i).getOpenstackDomain(), resultList.get(i).getOpenstackDomain());
            assertEquals(list.get(i).getStatus(), resultList.get(i).getStatus());
            assertEquals(list.get(i).getCreateUserId(), resultList.get(i).getCreateUserId());
            assertEquals(list.get(i).getUpdateUserId(), resultList.get(i).getUpdateUserId());
        }
        
        verify(mockIaasAccountMgntDao, times(1)).selectIaasAccountInfoList(principal.getName(), "openstack");
        verifyNoMoreInteractions(mockIaasAccountMgntDao);
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 인프라 계정 상세 정보 조회
     * @title : testGetIaasAccountInfo
     * @return : void
    ***************************************************/
    @Test
    public void testGetIaasAccountInfo(){

        IaasAccountMgntVO expectedVo = setIaasAccountInfo();
        when(mockIaasAccountMgntDao.selectIaasAccountInfo(principal.getName(), "aws",1 )).thenReturn(expectedVo);
        IaasAccountMgntVO result = mockIaasAccountMgntService.getIaasAccountInfo("aws", 1, principal);
        assertEquals( expectedVo.getId(), result.getId());
        assertEquals( expectedVo.getAccountName(), result.getAccountName());
        assertEquals( expectedVo.getCommonAccessUser(), result.getCommonAccessUser());
        assertEquals( expectedVo.getCommonAccessSecret(), result.getCommonAccessSecret());
        assertEquals( expectedVo.getCreateUserId(), result.getCreateUserId());
        assertEquals( expectedVo.getUpdateUserId(), result.getUpdateUserId());
        
        verify(mockIaasAccountMgntDao, times(1)).selectIaasAccountInfo(principal.getName(), "aws", 1);
        verifyNoMoreInteractions(mockIaasAccountMgntDao);
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : AWS 계정 정보 등록
     * @title : testSaveIaasAccountInfo
     * @return : void
    ***************************************************/
    @Test
    public void testSaveIaasAccountInfoRegistCaseFromAWS(){
        
        HttpServletRequest req = new MockHttpServletRequest();
        PublicKey publicKey = setPublicKey(req, "success");
        
        IaasAccountMgntVO expectedVo = setIaasAccountInfo();
        IaasAccountMgntDTO dto = setSaveIaasAccountInfoDto(publicKey, "aws", "");
        
        when(mockIaasAccountMgntDao.selectIaasAccountDuplicationByInfraAccount(expectedVo)).thenReturn(0);
        when(mockIaasAccountMgntDao.selectIaasAccountDuplicationByAccountName(expectedVo)).thenReturn(0);
        when( mockIaasAccountMgntApiService.getAccountInfoFromAWS(anyString(), anyString()) ).thenReturn(true);
        when(mockIaasAccountMgntDao.insertIaasAccountInfo(expectedVo)).thenReturn(1);
        
        mockIaasAccountMgntService.saveIaasAccountInfo("aws", dto, req, principal);
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Openstack v2 계정 정보 등록
     * @title : testSaveIaasAccountInfoRegistCaseFromOpenstackV2
     * @return : void
    ***************************************************/
    @Test
    public void testSaveIaasAccountInfoRegistCaseFromOpenstackV2(){
        
        HttpServletRequest req = new MockHttpServletRequest();
        PublicKey publicKey = setPublicKey(req, "success");
        
        IaasAccountMgntVO expectedVo = setIaasAccountInfo();
        IaasAccountMgntDTO dto = setSaveIaasAccountInfoDto(publicKey, "openstack", "v2");
        
        when(mockIaasAccountMgntDao.selectIaasAccountDuplicationByInfraAccount(expectedVo)).thenReturn(0);
        when(mockIaasAccountMgntDao.selectIaasAccountDuplicationByAccountName(expectedVo)).thenReturn(0);
        when( mockIaasAccountMgntApiService.getAccountInfoFromOpenstackV2(anyString(), anyString(), anyString(), anyString()) ).thenReturn(true);
        when(mockIaasAccountMgntDao.insertIaasAccountInfo(expectedVo)).thenReturn(1);
        
        mockIaasAccountMgntService.saveIaasAccountInfo("openstack", dto, req, principal);
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Openstack v3 계정 정보 등록
     * @title : testSaveIaasAccountInfoRegistCaseFromOpenstackV2
     * @return : void
    ***************************************************/
    @Test
    public void testSaveIaasAccountInfoRegistCaseFromOpenstackV3(){
        
        HttpServletRequest req = new MockHttpServletRequest();
        PublicKey publicKey = setPublicKey(req, "success");
        
        IaasAccountMgntVO expectedVo = setIaasAccountInfo();
        IaasAccountMgntDTO dto = setSaveIaasAccountInfoDto(publicKey, "openstack", "v3");
        
        when(mockIaasAccountMgntDao.selectIaasAccountDuplicationByInfraAccount(expectedVo)).thenReturn(0);
        when(mockIaasAccountMgntDao.selectIaasAccountDuplicationByAccountName(expectedVo)).thenReturn(0);
        when( mockIaasAccountMgntApiService.getAccountInfoFromOpenstackV3(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(true);
        when(mockIaasAccountMgntDao.insertIaasAccountInfo(expectedVo)).thenReturn(1);
        
        mockIaasAccountMgntService.saveIaasAccountInfo("openstack", dto, req, principal);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Google 계정 정보 등록
     * @title : testSaveIaasAccountInfoRegistCaseFromGoogle
     * @return : void
    *****************************************************************/
    @Test
    public void testSaveIaasAccountInfoRegistCaseFromGoogle(){
        
        HttpServletRequest req = new MockHttpServletRequest();
        PublicKey publicKey = setPublicKey(req, "success");
        
        IaasAccountMgntVO expectedVo = setIaasAccountInfo();
        IaasAccountMgntDTO dto = setSaveIaasAccountInfoDto(publicKey, "google", "");
        
        when(mockIaasAccountMgntDao.selectIaasAccountDuplicationByInfraAccount(expectedVo)).thenReturn(0);
        when(mockIaasAccountMgntDao.selectIaasAccountDuplicationByAccountName(expectedVo)).thenReturn(0);
        when( mockIaasAccountMgntApiService.getAccountInfoFromGoogle(anyString(), anyString())).thenReturn(true);
        when(mockIaasAccountMgntDao.insertIaasAccountInfo(expectedVo)).thenReturn(1);
        
        mockIaasAccountMgntService.saveIaasAccountInfo("google", dto, req, principal);
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : vSphere 계정 정보 등록
     * @title : testSaveIaasAccountInfoRegistCaseFromVSphere
     * @return : void
    ***************************************************/
    @Test
    public void testSaveIaasAccountInfoRegistCaseFromVSphere(){
        
        HttpServletRequest req = new MockHttpServletRequest();
        PublicKey publicKey = setPublicKey(req, "success");
        
        IaasAccountMgntVO expectedVo = setIaasAccountInfo();
        IaasAccountMgntDTO dto = setSaveIaasAccountInfoDto(publicKey, "vsphere", "");
        
        when(mockIaasAccountMgntDao.selectIaasAccountDuplicationByInfraAccount(expectedVo)).thenReturn(0);
        when(mockIaasAccountMgntDao.selectIaasAccountDuplicationByAccountName(expectedVo)).thenReturn(0);
        when( mockIaasAccountMgntApiService.getAccountInfoFromVsphere(anyString(), anyString(), anyString())).thenReturn(true);
        when(mockIaasAccountMgntDao.insertIaasAccountInfo(expectedVo)).thenReturn(1);
        
        mockIaasAccountMgntService.saveIaasAccountInfo("vsphere", dto, req, principal);
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure 계정 정보 등록
     * @title : testSaveIaasAccountInfoRegistCaseFromAzure
     * @return : void
    ***************************************************/
    
    public void testSaveIaasAccountInfoRegistCaseFromAzure(){
        
        HttpServletRequest req = new MockHttpServletRequest();
        PublicKey publicKey = setPublicKey(req, "success");
        
        IaasAccountMgntVO expectedVo = setIaasAccountInfo();
        IaasAccountMgntDTO dto = setSaveIaasAccountInfoDto(publicKey, "azure", "");
        
        when(mockIaasAccountMgntDao.selectIaasAccountDuplicationByInfraAccount(expectedVo)).thenReturn(0);
        when(mockIaasAccountMgntDao.selectIaasAccountDuplicationByAccountName(expectedVo)).thenReturn(0);
        when(mockIaasAccountMgntDao.insertIaasAccountInfo(expectedVo)).thenReturn(1);
        
        mockIaasAccountMgntService.saveIaasAccountInfo("azure", dto, req, principal);
    }
    
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 인프라 계정 등록 중복 오류
     * @title : testSaveIaasAccountInfoRegistInfraAccountDuplicationError
     * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testSaveIaasAccountInfoRegistInfraAccountDuplicationError(){
        
        HttpServletRequest req = new MockHttpServletRequest();
        PublicKey publicKey = setPublicKey(req, "success");
        
        IaasAccountMgntDTO dto = setSaveIaasAccountInfoDto(publicKey, "aws", "");
        
        when(mockIaasAccountMgntDao.selectIaasAccountDuplicationByInfraAccount(any())).thenReturn(2);
        when(mockIaasAccountMgntDao.selectIaasAccountDuplicationByAccountName(any())).thenReturn(0);
        when( mockIaasAccountMgntApiService.getAccountInfoFromAWS(anyString(), anyString()) ).thenReturn(true);
        
        mockIaasAccountMgntService.saveIaasAccountInfo("aws", dto, req, principal);
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 인프라 계정 등록 별칭 중복 오류
     * @title : testSaveIaasAccountInfoRegistAccountNameDuplicationError
     * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testSaveIaasAccountInfoRegistAccountNameDuplicationError(){
        
        HttpServletRequest req = new MockHttpServletRequest();
        PublicKey publicKey = setPublicKey(req, "success");
        
        IaasAccountMgntDTO dto = setSaveIaasAccountInfoDto(publicKey, "aws", "");
        
        when(mockIaasAccountMgntDao.selectIaasAccountDuplicationByInfraAccount(any())).thenReturn(0);
        when(mockIaasAccountMgntDao.selectIaasAccountDuplicationByAccountName(any())).thenReturn(2);
        when( mockIaasAccountMgntApiService.getAccountInfoFromAWS(anyString(), anyString()) ).thenReturn(true);
        
        mockIaasAccountMgntService.saveIaasAccountInfo("aws", dto, req, principal);
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 인프라 계정 등록 RSA Session 오류
     * @title : testSaveIaasAccountInfoRegistSessionPrivateKeyNullError
     * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testSaveIaasAccountInfoRegistSessionPrivateKeyNullError(){
        HttpServletRequest req = new MockHttpServletRequest();
        PublicKey publicKey = setPublicKey(req, "fail");
        
        IaasAccountMgntDTO dto = setSaveIaasAccountInfoDto(publicKey, "aws", "");
        
        mockIaasAccountMgntService.saveIaasAccountInfo("aws", dto, req, principal);
        
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 인프라 계정 등록/수정 실패(계정이 존재하지 않을 경우)
     * @title : testSaveIaasAccountInfoRegistAccountNotFoundError
     * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testSaveIaasAccountInfoRegistAccountNotFoundError(){
        
        HttpServletRequest req = new MockHttpServletRequest();
        PublicKey publicKey = setPublicKey(req, "success");
        
        IaasAccountMgntDTO dto = setSaveIaasAccountInfoDto(publicKey, "aws", "");
        
        when(mockIaasAccountMgntDao.selectIaasAccountDuplicationByInfraAccount(any())).thenReturn(0);
        when(mockIaasAccountMgntDao.selectIaasAccountDuplicationByAccountName(any())).thenReturn(0);
        when( mockIaasAccountMgntApiService.getAccountInfoFromAWS(anyString(), anyString()) ).thenReturn(false);
        
        mockIaasAccountMgntService.saveIaasAccountInfo("aws", dto, req, principal);
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 인프라 계정 정보수정
     * @title : testSaveIaasAccountInfoUpdateCase
     * @return : void
    ***************************************************/
    @Test
    public void testSaveIaasAccountInfoUpdateCase(){
        
        HttpServletRequest req = new MockHttpServletRequest();
        PublicKey publicKey = setPublicKey(req, "success");
        
        IaasAccountMgntVO expectedVo = setIaasAccountInfo();
        IaasAccountMgntDTO dto = setSaveIaasAccountInfoDto(publicKey, "vsphere", "");
        dto.setId("1");
        
        when( mockIaasAccountMgntDao.selectIaasAccountInfo(anyString(), anyString(), anyInt()) ).thenReturn(expectedVo);
        when( mockIaasAccountMgntApiService.getAccountInfoFromVsphere(anyString(), anyString(), anyString())).thenReturn(true);
        when(mockIaasAccountMgntDao.updateIaasAccountInfo(expectedVo)).thenReturn(1);
        
        mockIaasAccountMgntService.saveIaasAccountInfo("vsphere", dto, req, principal);
        
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 인프라 계정 정보 삭제
     * @title : testDeleteIaasAccountInfo
     * @return : void
    ***************************************************/
    @Test
    public void testDeleteIaasAccountInfo(){
        IaasAccountMgntDTO dto = setIaasAccountInfoDto();
        when(mockIaasAccountMgntDao.deleteIaasAccountInfo(principal.getName(), 1)).thenReturn(1);
        mockIaasAccountMgntService.deleteIaasAccountInfo(dto, principal);
        
        verify(mockIaasAccountMgntDao, times(1)).deleteIaasAccountInfo(principal.getName(), 1);
        verifyNoMoreInteractions(mockIaasAccountMgntDao);
    }
    
  
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 인프라 계정 정보 삭제 Id 값 Null 오류
     * @title : testDeleteIaasAccountInfoInternalServerError
     * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testDeleteIaasAccountInfoBadRequestError(){
        IaasAccountMgntDTO dto = new IaasAccountMgntDTO();
        dto.setId("");
        dto.setIaasType("aws");
        when(mockIaasAccountMgntDao.deleteIaasAccountInfo(principal.getName(), 1)).thenReturn(1);
        when(mockMessageSource.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn("데이터 삭제 중 오류가  발생하였습니다.");
        mockIaasAccountMgntService.deleteIaasAccountInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Google Json 키 파일 목록 조회
     * @title : testGetJsonKeyFileList
     * @return : void
    *****************************************************************/
    @Test
    public void testGetJsonKeyFileList(){
        mockIaasAccountMgntService.getJsonKeyFileList();
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Json Key 업로드
     * @title : testUploadJsonKeyFile
     * @return : void
    *****************************************************************/
    @Test
    public void testUploadJsonKeyFile(){
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        mockIaasAccountMgntService.uploadJsonKeyFile(request);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : VO 객체 커버리지
     * @title : tearDown
     * @return : void
    *****************************************************************/
    @After
    public void tearDown() throws Exception {
        //coverage test
        IaasAccountMgntVO accountVo = new IaasAccountMgntVO();
        accountVo.getDefaultYn();
        accountVo.setDefaultYn("N");
          
        accountVo.getCommonAccessEndpoint();
        accountVo.setCommonAccessEndpoint("");
        
        accountVo.getOpenstackKeystoneVersion();
        accountVo.setOpenstackKeystoneVersion("v2");
        
        accountVo.getCommonTenant();
        accountVo.setCommonTenant("bosh");
        
        accountVo.getOpenstackDomain();
        accountVo.setOpenstackDomain("");
        
        
        accountVo.getUpdateUserId();
        accountVo.setUpdateUserId(principal.getName());
        
        accountVo.getCreateDate();
        accountVo.setCreateDate(new Date());
        accountVo.getCreateDate();
        
        accountVo.getUpdateDate();
        accountVo.setUpdateDate(new Date());
        accountVo.getUpdateDate();
        
        accountVo.getRecid();
        accountVo.setRecid(1);
        
        accountVo.getTestFlag();
        accountVo.setTestFlag("Y");
        
        accountVo.setStatus("사용안함");
        accountVo.setCommonProject("bosh");
        accountVo.setGoogleJsonKeyPath("google-key1.json");
        
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 전체 인프라 계정 존재 시 목록 조회 유닛 테스트 값 설정
     * @title : getIaasAccountMgntListInfo
     * @return : List<IaasAccountMgntVO>
    ***************************************************/
    private List<IaasAccountMgntVO> setAllIaasAccountMgntListInfo(){
        List<IaasAccountMgntVO> list = new ArrayList<IaasAccountMgntVO>();
        IaasAccountMgntVO vo = new IaasAccountMgntVO();
        //when aws
        vo.setId(1);
        vo.setAccountName("aws-test1");
        vo.setCommonAccessUser("AKIAIGL5JRHJATEST");
        vo.setCommonAccessSecret("oDSAm1znlUFU62DHxV7Aa232EWEEWE");
        vo.setCreateUserId("admin");
        vo.setUpdateUserId("admin");
        list.add(vo);
        
        //when openstack
        vo.setId(2);
        vo.setAccountName("openstack-test1");
        vo.setCommonAccessEndpoint("http://10.10.10.1:5000/v2.0");
        vo.setCommonAccessUser("bosh-test");
        vo.setCommonAccessSecret("secret");
        vo.setOpenstackKeystoneVersion("v2");
        vo.setCommonTenant("tenant");
        vo.setCreateUserId("admin");
        vo.setUpdateUserId("admin");
        list.add(vo);
        
        //when google
        vo.setId(3);
        vo.setAccountName("google-test");
        vo.setCommonProject("paas-ta");
        vo.setGoogleJsonKeyPath("google-key.json");
        vo.setCreateUserId("admin");
        vo.setUpdateUserId("admin");
        list.add(vo);
        
        //when vSphere
        vo.setId(4);
        vo.setAccountName("vsphere-test1");
        vo.setCommonAccessEndpoint("10.0.0.1");
        vo.setCommonAccessUser("test");//Application Id
        vo.setCommonAccessSecret("testPw");//Application secret
        vo.setCreateUserId("admin");
        vo.setUpdateUserId("admin");
        list.add(vo);
        
      //when azure
        vo.setId(1);
        vo.setAccountName("azure-test1");
        vo.setCommonAccessUser("779daebc-febe-4901-bb05-464d3b877b7b"); //application id
        vo.setCommonAccessSecret("lNYSO2Bm4j/FwsW+HbefnNc1V7742N5jiA8vNIC4rXs="); // application key
        vo.setCommonTenant("aeacdca2-4f9e-4bc5-8c8b-b0403fbdcfd1"); //tenant id
        vo.setCreateUserId("admin");
        vo.setUpdateUserId("admin");
        list.add(vo);
        
        return list;
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 인프라 별 계정 개수 정보 설정
     * @title : getIaasAccountCountInfo
     * @return : HashMap<String,Object>
    ***************************************************/
    private HashMap<String, Integer> setIaasAccountCountInfo(){
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put("aws_cnt",3);
        map.put("openstack_cnt", 5);
        map.put("vsphere_cnt", 2);
        map.put("google_cnt", 1);
        map.put("azure_cnt", 1);
        
        return map;
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 인프라 계정 정보 목록 정보 설정
     * @title : setIaasAccountMgntInfoList
     * @return : List<IaasAccountMgntVO>
    ***************************************************/
    private List<IaasAccountMgntVO> setIaasAccountInfoList(){
        List<IaasAccountMgntVO> list = new ArrayList<IaasAccountMgntVO>();
        IaasAccountMgntVO vo = new IaasAccountMgntVO();
        //when openstack v2
        vo.setId(1);
        vo.setAccountName("openstack-v2-test");
        vo.setCommonAccessEndpoint("http://10.10.10.1:5000/v2.0");
        vo.setCommonAccessUser("bosh-v2");
        vo.setCommonAccessSecret("secret-v2");
        vo.setOpenstackKeystoneVersion("v2");
        vo.setCommonTenant("v2Tenant");
        vo.setCreateUserId("admin");
        vo.setUpdateUserId("admin");
        list.add(vo);
        
        //when openstack v3
        vo.setId(2);
        vo.setAccountName("openstack-v3-test");
        vo.setCommonAccessEndpoint("http://172.10.10.1:5000/v2.0");
        vo.setCommonAccessUser("bosh-v3");
        vo.setCommonAccessSecret("secret-v3");
        vo.setOpenstackKeystoneVersion("v3");
        vo.setCommonProject("v3Tenant");
        vo.setOpenstackDomain("v3domain");
        vo.setCreateUserId("admin");
        vo.setUpdateUserId("admin");
        list.add(vo);
        
        return list;
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 해당 인프라 계정 상세 정보 조회
     * @title : setIaasAccountInfo
     * @return : IaasAccountMgntVO
    ***************************************************/
    private IaasAccountMgntVO setIaasAccountInfo(){
        IaasAccountMgntVO vo = new IaasAccountMgntVO();
        vo.setId(1);
        vo.setAccountName("aws-test1");
        vo.setCommonAccessUser("AKIAIGL5JRHJATEST");
        vo.setCommonAccessSecret("oDSAm1znlUFU62DHxV7Aa232EWEEWE");
        vo.setCreateUserId("admin");
        vo.setUpdateUserId("admin");
        vo.setDefaultYn(null);
        
        return vo;
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description :  인프라 계정 삭제를 위한 계정 정보 설정
     * @title : setIaasAccountInfoDto
     * @return : IaasAccountMgntDTO
    ***************************************************/
    private IaasAccountMgntDTO setIaasAccountInfoDto(){
        IaasAccountMgntDTO dto = new IaasAccountMgntDTO();
        dto.setId("1");
        dto.setIaasType("aws");
        dto.setAccountName("aws-test1");
        dto.setCommonAccessUser("AKIAIGL5JRHJATEST");
        dto.setCommonAccessSecret("oDSAm1znlUFU62DHxV7Aa232EWEEWE");
        dto.setTestFlag("Y");
        dto.setDefaultYn(null);
        return dto;
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : RSA 암호화를 통해 인프라 계정 등록/수정 정보 설정
     * @title : setSaveIaasAccountInfoDto
     * @return : IaasAccountMgntDTO
    ***************************************************/
    private IaasAccountMgntDTO setSaveIaasAccountInfoDto(PublicKey publicKey, String iaasType, String  openstackKeystoneVersion){
        IaasAccountMgntDTO dto = new IaasAccountMgntDTO();
        dto.getDefaultYn();
        dto.setTestFlag("Y");
        if( iaasType.equalsIgnoreCase("aws") ){
            dto.setIaasType("aws");
            dto.setAccountName("aws-test1");
            dto.setCommonAccessEndpoint("");
            dto.setCommonTenant("");
            dto.setOpenstackKeystoneVersion("");
            dto.setOpenstackDomain("");
            dto.setCommonProject("");
        }else if(iaasType.equalsIgnoreCase("openstack")) {
            dto.setIaasType("openstack");
            if( openstackKeystoneVersion.equalsIgnoreCase("v2")){
                dto.setAccountName("openstack-v2-test");
                dto.setCommonAccessEndpoint("http://10.10.10.1:5000/v2.0");
                dto.setCommonAccessUser("bosh-v2");
                dto.setCommonAccessSecret("secret-v2");
                dto.setOpenstackKeystoneVersion("v2");
                dto.setCommonTenant("v2Tenant");
                dto.setOpenstackDomain("");
                dto.setCommonProject("");
            }else{
               
                dto.setAccountName("openstack-v3-test");
                dto.setCommonAccessEndpoint("http://172.10.10.1:5000/v2.0");
                dto.setCommonAccessUser("bosh-v3");
                dto.setCommonAccessSecret("secret-v3");
                dto.setOpenstackKeystoneVersion("v3");
                dto.setCommonProject("v3Tenant");
                dto.setOpenstackDomain("v3domain");
                dto.setCommonTenant("");
            }
        }else if(iaasType.equalsIgnoreCase("vsphere")){
            dto.setIaasType("vsphere");
            dto.setAccountName("vsphere-test1");
            dto.setCommonAccessEndpoint("10.0.0.1");
            dto.setCommonAccessUser("test");//Application Id
            dto.setCommonAccessSecret("testPw");//Application secret
            dto.setCommonTenant("");
            dto.setOpenstackKeystoneVersion("");
            dto.setOpenstackDomain("");
            dto.setCommonProject("");
        }else if( iaasType.equalsIgnoreCase("google") ){
            dto.setIaasType("google");
            dto.setCommonProject("paas-ta");
            dto.setGoogleJsonKeyPath("google-key.json");
        }else if( iaasType.equalsIgnoreCase("azure") ){
            dto.setIaasType("azure");
            dto.setAccountName("azure-test1");
            dto.setCommonAccessUser("779daebc-febe-4901-bb05-464d3b877b7b");//Application Id
            dto.setCommonAccessSecret("lNYSO2Bm4j/FwsW+HbefnNc1V7742N5jiA8vNIC4rXs=");//Application key
            dto.setCommonTenant("aeacdca2-4f9e-4bc5-8c8b-b0403fbdcfd1"); //tenant id
        }
        
        //RSA(commonAccessUser & commonAccessSecret)
        try {
            byte[] commonAccessUserCipherData = null;
            byte[] commonAccessSecretCipherData = null;
            Cipher cipher = null;
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            commonAccessUserCipherData = cipher.doFinal("AKIAIGL5JRHJATEST".getBytes());
            dto.setCommonAccessUser(Base64.encodeBase64URLSafeString(commonAccessUserCipherData));
            commonAccessSecretCipherData = cipher.doFinal("oDSAm1znlUFU62DHxV7Aa232EWEEWE".getBytes());
            dto.setCommonAccessSecret(Base64.encodeBase64URLSafeString(commonAccessSecretCipherData));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : RSA를 생성하여  privateKey 세션 생성
     * @title : setPublicKey
     * @return : PublicKey
    ***************************************************/
    private PublicKey setPublicKey(HttpServletRequest req, String valid){
        PublicKey pubKey= null;
        KeyPairGenerator generator;
        try {
            generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);
            KeyPair keyPair = generator.genKeyPair();

            pubKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            HttpSession session = req.getSession();
            
            if( valid.equalsIgnoreCase("success") ){
             // 세션에 공개키의 문자열을 키로하여 개인키를 저장한다.
                session.setAttribute("__rsaPrivateKey__", privateKey);
            }
            
            
        }catch (NoSuchAlgorithmException e1) {
           e1.printStackTrace();
        }
        return pubKey;
    }
    
}
