package org.openpaas.ieda.deploy.web.information.iassConfig;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.core.Application;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.web.common.base.BaseDeployControllerUnitTest;
import org.openpaas.ieda.deploy.web.information.iassConfig.dao.IaasConfigMgntDAO;
import org.openpaas.ieda.deploy.web.information.iassConfig.dao.IaasConfigMgntVO;
import org.openpaas.ieda.deploy.web.information.iassConfig.dto.IaasConfigMgntDTO;
import org.openpaas.ieda.deploy.web.information.iassConfig.service.IaasConfigMgntService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class IaasConfigMgntServiceUnitTest extends BaseDeployControllerUnitTest{
    
    @InjectMocks 
    private IaasConfigMgntService mockIaasConfigMgntService;
    @Mock 
    IaasConfigMgntDAO momckIaasConfigMgntDAO;
    @Mock 
    MessageSource mockMessageSource;
    
    private Principal principal = null;
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :하나의 메소드가 실행되기전 호출 
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        principal = getLoggined();
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 전체 환경 설정 목록 정보 조회 테스트
    * @title : testGetAllIaasConfigInfoList
    * @return : void
    ***************************************************/
    @Test
    public void testGetAllIaasConfigInfoList(){
        List<IaasConfigMgntVO> expectList = setAllIaasConfigInfoList();
        when(momckIaasConfigMgntDAO.selectAllIaasConfigInfoList(anyString())).thenReturn(expectList);
        List<IaasConfigMgntVO> resultList = mockIaasConfigMgntService.getAllIaasConfigInfoList(principal);
        assertEquals(resultList.get(0).getAccountId(), expectList.get(0).getAccountId());
        assertEquals(resultList.get(0).getAccountName(), expectList.get(0).getAccountName());
        assertEquals(resultList.get(0).getCommonAvailabilityZone(), expectList.get(0).getCommonAvailabilityZone());
        assertEquals(resultList.get(0).getCommonKeypairName(), expectList.get(0).getCommonKeypairName());
        assertEquals(resultList.get(0).getCommonKeypairPath(), expectList.get(0).getCommonKeypairPath());
        assertEquals(resultList.get(0).getCommonRegion(), expectList.get(0).getCommonRegion());
        assertEquals(resultList.get(0).getCommonSecurityGroup(), expectList.get(0).getCommonSecurityGroup());
        assertEquals(resultList.get(0).getVsphereVcenterDiskPath(), expectList.get(0).getVsphereVcenterDiskPath());
        assertEquals(resultList.get(0).getVsphereVcenterCluster(), expectList.get(0).getVsphereVcenterCluster());
        assertEquals(resultList.get(0).getCommonSecurityGroup(), expectList.get(0).getCommonSecurityGroup());
        assertEquals(resultList.get(0).getVsphereVcenterPersistentDatastore(), expectList.get(0).getVsphereVcenterPersistentDatastore());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 인프라 별 환경 설정 정보 개수 조회 테스트
    * @title : testGetIaasConfigCount
    * @return : void
    ***************************************************/
    @Test
    public void testGetIaasConfigCount(){
        HashMap<String, Integer> expectMap = setIaasConfigCount();
        when(momckIaasConfigMgntDAO.selectIaasConfigCount(anyString())).thenReturn(expectMap);
        HashMap<String, Integer> resultMap = mockIaasConfigMgntService.getIaasConfigCount(principal);
        assertEquals(expectMap.get("azure"), resultMap.get("azure"));
        assertEquals(expectMap.get("aws"), resultMap.get("aws"));
        assertEquals(expectMap.get("vsphere"), resultMap.get("vsphere"));
        assertEquals(expectMap.get("openstack"), resultMap.get("openstack"));
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 해당 인프라 계정 정보 목록 조회 테스트
    * @title : testGetAwsConfigInfoList
    * @return : void
    ***************************************************/
    @Test
    public void testgetIaasConfigInfoList(){
        List<IaasConfigMgntVO> expectAwsList = setAwsConfigInfoList();
        when(momckIaasConfigMgntDAO.selectIaasConfigInfoList(anyString(), anyString())).thenReturn(expectAwsList);
        List<IaasConfigMgntVO> resultList = mockIaasConfigMgntService.getIaasConfigInfoList("AWS", principal);
        assertEquals(resultList.get(0).getAccountId(), expectAwsList.get(0).getAccountId());
        assertEquals(resultList.get(0).getAccountName(), expectAwsList.get(0).getAccountName());
        assertEquals(resultList.get(0).getCommonAvailabilityZone(), expectAwsList.get(0).getCommonAvailabilityZone());
        assertEquals(resultList.get(0).getCommonKeypairName(), expectAwsList.get(0).getCommonKeypairName());
        assertEquals(resultList.get(0).getCommonKeypairPath(), expectAwsList.get(0).getCommonKeypairPath());
        assertEquals(resultList.get(0).getCommonRegion(), expectAwsList.get(0).getCommonRegion());
        assertEquals(resultList.get(0).getCommonSecurityGroup(), expectAwsList.get(0).getCommonSecurityGroup());
        assertEquals(resultList.get(0).getIaasConfigAlias(), expectAwsList.get(0).getIaasConfigAlias());
        assertEquals(resultList.get(0).getIaasType(), expectAwsList.get(0).getIaasType());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 인프라 환경 설정 정보 등록/수정 중 Conflict 에러 테스트
    * @title : testSaveIaasConfigInfoConflictError
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testSaveIaasConfigInfoConflictError(){
        IaasConfigMgntDTO dto = setSaveIaasConfig("insert");
        when(momckIaasConfigMgntDAO.selectIaasConfigDuplicationByConfigInfo(any())).thenReturn(1);
        when(mockMessageSource.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn("충돌");
        mockIaasConfigMgntService.saveIaasConfigInfo("AWS", dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 인프라 환경 설정 정보 등록/수정 중 Conflict 에러 테스트
    * @title : testSaveIaasConfigInfoConflictError
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testSaveIaasConfigNameConflictError(){
        IaasConfigMgntDTO dto = setSaveIaasConfig("insert");
        when(momckIaasConfigMgntDAO.selectIaasConfigDuplicationByConfigInfo(any())).thenReturn(0);
        when(momckIaasConfigMgntDAO.selectIaasConfigDuplicationByConfigName(any())).thenReturn(1);
        when(mockMessageSource.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn("충돌");
        mockIaasConfigMgntService.saveIaasConfigInfo("AWS", dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description :  인프라 환경 설정 정보 등록 테스트
    * @title : testInsertIaasConfigInfo
    * @return : void
    ***************************************************/
    @Test
    public void testInsertIaasConfigInfo(){
        IaasConfigMgntDTO dto = setSaveIaasConfig("insert");
        when(momckIaasConfigMgntDAO.selectIaasConfigDuplicationByConfigInfo(any())).thenReturn(0);
        when(momckIaasConfigMgntDAO.selectIaasConfigDuplicationByConfigName(any())).thenReturn(0);
        mockIaasConfigMgntService.saveIaasConfigInfo("AWS", dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description :  인프라 환경 설정 정보 수정 테스트
    * @title : testInsertIaasConfigInfo
    * @return : void
    ***************************************************/
    @Test
    public void testUpdateIaasConfigInfo(){
        IaasConfigMgntDTO dto = setSaveIaasConfig("update");
        IaasConfigMgntVO expect = setUpdateIaasConfigInfo();
        when(momckIaasConfigMgntDAO.selectIaasConfigDuplicationByConfigInfo(any())).thenReturn(0);
        when(momckIaasConfigMgntDAO.selectIaasConfigDuplicationByConfigName(any())).thenReturn(0);
        when(momckIaasConfigMgntDAO.selectIaasConfigInfo(anyString(), anyString(), anyInt())).thenReturn(expect);
        mockIaasConfigMgntService.saveIaasConfigInfo("AWS", dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 인프라 환경 설정 정보 상세 조회 테스트
    * @title : testGetIaasConfigInfo 
    * @return : void
    ***************************************************/
    @Test
    public void testGetIaasConfigInfo(){
        IaasConfigMgntVO expect = setUpdateIaasConfigInfo();
        when(momckIaasConfigMgntDAO.selectIaasConfigInfo(anyString(), anyString(), anyInt())).thenReturn(expect);
        IaasConfigMgntVO result = mockIaasConfigMgntService.getIaasConfigInfo("AWS", 1, principal);
        assertEquals(result.getAccountId(), expect.getAccountId());
        assertEquals(result.getAccountName(), expect.getAccountName());
        assertEquals(result.getCommonKeypairName(), expect.getCommonKeypairName());
        assertEquals(result.getCommonKeypairPath(), expect.getCommonKeypairPath());
        assertEquals(result.getCommonSecurityGroup(), expect.getCommonSecurityGroup());
        assertEquals(result.getCommonRegion(), expect.getCommonRegion());
        assertEquals(result.getDeployStatus(), expect.getDeployStatus());
        assertEquals(result.getId(), expect.getId());
        assertEquals(result.getCreateUserId(), expect.getCreateUserId());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 인프라 환경 설정 정보 상세 조회 값이 Null일 경우 테스트
    * @title : testGetIaasConfigInfoValueNull
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testGetIaasConfigInfoValueNull(){
        when(momckIaasConfigMgntDAO.selectIaasConfigInfo(anyString(), anyString(), anyInt())).thenReturn(null);
        when(mockMessageSource.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn("빈 값");
        mockIaasConfigMgntService.getIaasConfigInfo("AWS", 1, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 인프라 환경 설정 정보 삭제 테스트
    * @title : testDeleteIaasConfigInfo
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteIaasConfigInfo(){
        IaasConfigMgntDTO dto = setSaveIaasConfig("update");
        mockIaasConfigMgntService.deleteIaasConfigInfo(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Config ID가 비엇을 경우 테스트
    * @title : testDeleteIaasConfigInfoConfigIdEmpty
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testDeleteIaasConfigInfoConfigIdEmpty(){
        IaasConfigMgntDTO dto = setSaveIaasConfig("insert");
        when(mockMessageSource.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn("빈 값");
        mockIaasConfigMgntService.deleteIaasConfigInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : VO 객체 커버리지
     * @title : tearDown
     * @return : void
    *****************************************************************/
    @After
    public void tearDown() throws Exception {
        IaasConfigMgntVO config = new IaasConfigMgntVO();
        
        config.getRecid();
        config.getVsphereVcenterVmFolder();
        config.getVsphereVcenterTemplateFolder();
        config.getVsphereVcenterDatastore();
        config.getVsphereVcentDataCenterName();
        config.getUpdateUserId();
        config.getUpdateDate();
        config.setUpdateDate(new Date());
        config.getUpdateDate();
        config.getCreateDate();
        config.setCreateDate(new Date());
        config.getCreateDate();
        config.getTestFlag();
        config.setTestFlag("Y");
        
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 인프라 환경 정보 설정
    * @title : setUpdateIaasConfigInfo
    * @return : IaasConfigMgntVO
    ***************************************************/
    private IaasConfigMgntVO setUpdateIaasConfigInfo() {
        IaasConfigMgntVO vo = new IaasConfigMgntVO();
        vo.setAccountId(1);
        vo.setAccountName("test");
        vo.setCommonAvailabilityZone("test");
        vo.setCommonKeypairName("test");
        vo.setCommonKeypairPath("test");
        vo.setCommonSecurityGroup("test");
        vo.setCreateUserId("admin");
        vo.setUpdateUserId("admin");
        vo.setDeployStatus("사용중");
        vo.setIaasConfigAlias("test");
        vo.setRecid(1);
        vo.setId(1);
        vo.setIaasType("AWS");
        return vo;
    }

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
    * @description : 인프라 환경 정보 삽입 데이터 설정
    * @title : setSaveIaasConfig
    * @return : IaasConfigMgntDTO
    ***************************************************/
    private IaasConfigMgntDTO setSaveIaasConfig(String save){
        IaasConfigMgntDTO dto = new IaasConfigMgntDTO();
        if(save.equals("update")){
            dto.setId("1");
        }
        dto.setAccountId("1");
        dto.setCommonAvailabilityZone("test");
        dto.setCommonKeypairName("test");
        dto.setCommonKeypairPath("test");
        dto.setCommonSecurityGroup("test");
        dto.setIaasConfigAlias("test");
        dto.setIaasType("AWS");
        dto.setCommonRegion("asia");
        dto.setVsphereVcenterDataCenterName("");
        dto.setVsphereVcenterTemplateFolder("");
        dto.setVsphereVcenterVmFolder("");
        dto.setVsphereVcenterDatastore("");
        dto.setVsphereVcenterPersistentDatastore("");
        dto.setVsphereVcenterDiskPath("");
        dto.setVsphereVcenterCluster("");
        dto.setTestFlag("Y");
        
        
        return dto;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 해당 인프라 환경 정보 목록 값 설정
    * @title : setAwsConfigInfoList
    * @return : List<IaasConfigMgntVO> 
    ***************************************************/
    private List<IaasConfigMgntVO> setAwsConfigInfoList() {
        List<IaasConfigMgntVO> list = new ArrayList<IaasConfigMgntVO>();
        IaasConfigMgntVO vo = new IaasConfigMgntVO();
        vo.setAccountId(1);
        vo.setAccountName("test");
        vo.setCommonAvailabilityZone("test");
        vo.setCommonKeypairName("test");
        vo.setCommonKeypairPath("test");
        vo.setCommonSecurityGroup("test");
        vo.setCreateUserId("admin");
        vo.setUpdateUserId("admin");
        vo.setDeployStatus("사용중");
        vo.setIaasConfigAlias("test");
        vo.setRecid(1);
        vo.setId(1);
        vo.setIaasType("AWS");
        list.add(vo);
        return list;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 환경 설정 개수 설정
    * @title : setIaasConfigCount
    * @return : HashMap<String, Integer>
    ***************************************************/
    private HashMap<String, Integer> setIaasConfigCount() {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put("azure", 1);
        map.put("aws", 2);
        map.put("vsphere", 3);
        map.put("openstack", 4);
        return map;
    }
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 전체 환경 설정 목록 정보 조회 값 설정
    * @title : setAllIaasConfigInfoList
    * @return : List<IaasConfigMgntVO>
    ***************************************************/
    private List<IaasConfigMgntVO> setAllIaasConfigInfoList() {
        List<IaasConfigMgntVO> list = new ArrayList<IaasConfigMgntVO>();
        IaasConfigMgntVO vo = new IaasConfigMgntVO();
        vo.setAccountId(1);
        vo.setAccountName("test");
        vo.setCommonAvailabilityZone("test");
        vo.setCommonKeypairName("test");
        vo.setCommonKeypairPath("test");
        vo.setCommonSecurityGroup("test");
        vo.setCreateUserId("admin");
        vo.setUpdateUserId("admin");
        vo.setDeployStatus("사용중");
        vo.setIaasConfigAlias("test");
        vo.setVsphereVcenterVmFolder("test");
        vo.setVsphereVcenterTemplateFolder("test");
        vo.setVsphereVcenterPersistentDatastore("test");
        vo.setVsphereVcenterDiskPath("test");
        vo.setVsphereVcenterDatastore("test");
        vo.setVsphereVcenterCluster("test");
        vo.setVsphereVcentDataCenterName("test");
        vo.setRecid(1);
        vo.setId(1);
        vo.setIaasType("VSPHERE");
        list.add(vo);
        return list;
    }
}
