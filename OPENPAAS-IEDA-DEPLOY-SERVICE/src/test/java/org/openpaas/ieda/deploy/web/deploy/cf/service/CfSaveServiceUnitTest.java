package org.openpaas.ieda.deploy.web.deploy.cf.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
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
import org.openpaas.ieda.deploy.web.common.base.BaseDeployControllerUnitTest;
import org.openpaas.ieda.deploy.web.common.dto.KeyInfoDTO;
import org.openpaas.ieda.deploy.web.deploy.cf.dao.CfDAO;
import org.openpaas.ieda.deploy.web.deploy.cf.dao.CfVO;
import org.openpaas.ieda.deploy.web.deploy.cf.dto.CfParamDTO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.network.NetworkDAO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.network.NetworkVO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.resource.ResourceDAO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.resource.ResourceVO;
import org.openpaas.ieda.deploy.web.deploy.common.dto.network.NetworkDTO;
import org.openpaas.ieda.deploy.web.deploy.common.dto.resource.ResourceDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class CfSaveServiceUnitTest extends BaseDeployControllerUnitTest {
    
    @InjectMocks CfSaveService mockCfSaveService;
    @Mock CfDAO mockCfDAO;
    @Mock NetworkDAO mockNetworkDAO;
    @Mock ResourceDAO mockResourceDAO;
    @Mock MessageSource mockMessageSource;
    
    
    private Principal principal = null;
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
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
    * @description : CF 기본 정보 삽입 테스트
    * @title : testSaveDefaultInfoInsert
    * @return : void
    ***************************************************/
    @Test
    public void testSaveDefaultInfoInsert(){
        CfParamDTO.Default dto = setCfDefaultParamInfo("insert");
        when(mockCfDAO.selectCfDeploymentNameDuplication(anyString(), anyString(), anyInt())).thenReturn(0);
        CfVO resultVo = mockCfSaveService.saveDefaultInfo(dto, principal);
        assertEquals(resultVo.getDirectorUuid(), dto.getDirectorUuid());
        assertEquals(resultVo.getPaastaMonitoringUse(), dto.getPaastaMonitoringUse());
        assertEquals(resultVo.getReleaseName(), dto.getReleaseName());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 기본 정보 수정 테스트
    * @title : testSaveDefaultInfoUpdate
    * @return : void
    ***************************************************/
    @Test
    public void testSaveDefaultInfoUpdate(){
        CfParamDTO.Default dto = setCfDefaultParamInfo("update");
        CfVO expectVo = setCfInfo("default");
        when(mockCfDAO.selectCfInfoById(anyInt())).thenReturn(expectVo);
        when(mockCfDAO.selectCfDeploymentNameDuplication(anyString(), anyString(), anyInt())).thenReturn(0);
        CfVO resultVo = mockCfSaveService.saveDefaultInfo(dto, principal);
        assertEquals(resultVo.getDirectorUuid(), dto.getDirectorUuid());
        assertEquals(resultVo.getPaastaMonitoringUse(), dto.getPaastaMonitoringUse());
        assertEquals(resultVo.getReleaseName(), dto.getReleaseName());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 기본 정보 삽입 시 배포 파일 명이 존재 할 경우 테스트
    * @title : testSaveDefaultInfoDepmoymentFileNameConflict
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testSaveDefaultInfoDepmoymentFileNameConflict(){
        CfParamDTO.Default dto = setCfDefaultParamInfo("update");
        CfVO expectVo = setCfInfo("default");
        when(mockCfDAO.selectCfInfoById(anyInt())).thenReturn(expectVo);
        when(mockCfDAO.selectCfDeploymentNameDuplication(anyString(), anyString(), anyInt())).thenReturn(1);
        mockCfSaveService.saveDefaultInfo(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 배포 파일명 설정 테스트
    * @title : testMakeDeploymentName
    * @return : void
    ***************************************************/
    @Test
    public void testMakeDeploymentName(){
        CfVO expectVo = setCfInfo("default");
        mockCfSaveService.makeDeploymentName(expectVo);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 배포 파일명 설정 중 인프라 환경 타입 값이 존재 하지 않을 경우  테스트
    * @title : testMakeDeploymentNameIaasNull
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testMakeDeploymentNameIaasNull(){
        CfVO expectVo = setCfInfo("iaasTypeNull");
        mockCfSaveService.makeDeploymentName(expectVo);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 네트워크 정보 삽입 테스트
    * @title : testSaveNetworkInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveNetworkInfo(){
        List<NetworkDTO> dto = expectNetworkList("default");
        mockCfSaveService.saveNetworkInfo(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 네트워크 정보 삽입 시 네투워크 사이즈가 2개 이상일 경우 테스트
    * @title : testSaveNetworkInfoSize2
    * @return : void
    ***************************************************/
    @Test
    public void testSaveNetworkInfoSize2(){
        List<NetworkDTO> dto = expectNetworkList("size");
        List<NetworkVO> vo = setNetworkInfoList("size");
        when(mockNetworkDAO.selectNetworkList(anyInt(), anyString())).thenReturn(vo);
        List<HashMap<String, Object>> jobs =  setJobSettingInfoList();
        when(mockCfDAO.selectCfJobSettingInfoListBycfId(anyString(), anyInt())).thenReturn(jobs);
        doNothing().when(mockCfDAO).deleteCfJobSettingRecordsByIdAndZone(anyInt(), anyString());
        mockCfSaveService.saveNetworkInfo(dto, principal);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : cf jobs 목록 설정
     * @title : setJobSettingInfoList
     * @return : List<HashMap<String,Object>>
    ***************************************************/
    public List<HashMap<String, Object>> setJobSettingInfoList(){
        List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("id", 1);
        map.put("seq", 1);
        map.put("deploy_type", "DEPLOY_TYPE_CF");
        map.put("job_id", 1);
        map.put("instances", 3);
        map.put("zone", "z2");
        map.put("create_user_id", "admin");
        map.put("update_user_id", "admin");
        map.put("job_name", "consul");
        list.add(map);
        return list;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Key 생성 정보 저장 테스트
    * @title : testSaveKeyInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveKeyInfo(){
        CfVO expectVo = setCfInfo("default");
        when(mockCfDAO.selectCfInfoById(anyInt())).thenReturn(expectVo);
        KeyInfoDTO dto = cfKeyInfo();
        mockCfSaveService.saveKeyInfo(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Key 생성 정보 저장 시 해당 KEY에 관련 한 정보가 존재 하지 않을 경우 테스트
    * @title : testSaveKeyInfoCfInfoNull
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testSaveKeyInfoCfInfoNull(){
        when(mockCfDAO.selectCfInfoById(anyInt())).thenReturn(null);
        KeyInfoDTO dto = cfKeyInfo();
        mockCfSaveService.saveKeyInfo(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 리소스 정보 저장 및 배포 파일명 설정 테스트
    * @title : testSaveResourceInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveResourceInfo(){
        ResourceDTO dto = cfResourceInfo();
        CfVO expectVo = setCfInfo("default");
        when(mockMessageSource.getMessage(anyString(), anyObject(), anyObject())).thenReturn("DEPLOY_TYPE_CF");
        when(mockCfDAO.selectCfResourceInfoById(anyInt(), anyString())).thenReturn(expectVo);
        mockCfSaveService.saveResourceInfo(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 리소스 정보 저장 및 배포 파일 명이 존해하지 않을 경우 테스트
    * @title : testSaveResourceInfoDeploymentFileNull
    * @return : void
    ***************************************************/
    @Test
    public void testSaveResourceInfoDeploymentFileNameNull(){
        ResourceDTO dto = cfResourceInfo();
        CfVO expectVo = setCfInfo("null");
        when(mockMessageSource.getMessage(anyString(), anyObject(), anyObject())).thenReturn("DEPLOY_TYPE_CF");
        when(mockCfDAO.selectCfResourceInfoById(anyInt(), anyString())).thenReturn(expectVo);
        mockCfSaveService.saveResourceInfo(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 리소스 정보가 존재 할 경우 테스트
    * @title : testSaveResourceInfoDeploymentFileNull
    * @return : void
    ***************************************************/
    @Test
    public void testSaveResourceInfoUpdate(){
        ResourceDTO dto = cfResourceInfo();
        CfVO expectVo = setCfInfo("resource");
        when(mockMessageSource.getMessage(anyString(), anyObject(), anyObject())).thenReturn("DEPLOY_TYPE_CF");
        when(mockCfDAO.selectCfResourceInfoById(anyInt(), anyString())).thenReturn(expectVo);
        mockCfSaveService.saveResourceInfo(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF VSPHERE 환경의 리소스 정보 저장 테스트
    * @title : testSaveResourceInfoVsphere
    * @return : void
    ***************************************************/
    @Test
    public void testSaveResourceInfoVsphere(){
        ResourceDTO dto = cfResourceInfo();
        CfVO expectVo = setCfInfo("vsphere");
        when(mockMessageSource.getMessage(anyString(), anyObject(), anyObject())).thenReturn("DEPLOY_TYPE_CF");
        when(mockCfDAO.selectCfResourceInfoById(anyInt(), anyString())).thenReturn(expectVo);
        mockCfSaveService.saveResourceInfo(dto, principal);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  cf jobs 정보 저장
     * @title : testSaveCfJobsInfo
     * @return : void
    ***************************************************/
    public void testSaveCfJobsInfo() {
        List<HashMap<String, Object>> jobs =  setJobSettingInfoList();
        when(mockMessageSource.getMessage(any(), any(), any())).thenReturn("DEPLOY_TYPE_CF");
        when(mockCfDAO.selectCfJobSettingInfoListBycfId(anyString(), anyInt())).thenReturn(jobs);
        doNothing().when(mockCfDAO).insertCfJobSettingInfo(any());
        mockCfSaveService.saveCfJobsInfo(setCfJObsInfo(), principal);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF Job 정보 저장 데이터 설정
     * @title : setCfJObsInfo
     * @return : List<HashMap<String,String>>
    ***************************************************/
    public List<HashMap<String, String>> setCfJObsInfo(){
        List<HashMap<String, String>> maps = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("id", "1");
        map.put("seq", "1");
        map.put("deploy_type", "DEPLOY_TYPE_CF");
        map.put("job_name", "consul");
        map.put("job_id", "1000");
        map.put("instances", "3");
        map.put("zone", "z1");
        maps.add(map);
        return maps;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 리소스 정보 저장 파라미터 값 설정
    * @title : cfResourceInfo
    * @return : void
    ***************************************************/
    public ResourceDTO cfResourceInfo() {
        ResourceDTO dto = new ResourceDTO();
        dto.setBoshPassword("bosh");
        dto.setCfId("1");
        dto.setId("1");
        dto.setIaas("openstack");
        dto.setKeyFile("key.yml");
        dto.setPlatform("cf");
        dto.setLargeCpu("10");
        dto.setLargeDisk("12312");
        dto.setLargeFlavor("m1.large");
        dto.setLargeRam("1323");
        dto.setMediumCpu("1");
        dto.setMediumDisk("8192");
        dto.setMediumFlavor("m1.medium");
        dto.setMediumRam("111");
        dto.setSmallCpu("1");
        dto.setSmallDisk("123");
        dto.setSmallRam("8192");
        dto.setStemcellName("os");
        dto.setStemcellVersion("3127");
        dto.setRunnerFlavor("m1.runner");
        dto.setSmallFlavor("m1.small");
        dto.getKeyFile();
        dto.getPlatform();
        dto.getIaas();
        dto.getCfId();
        return dto;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 키 정보 저장 파라미터 값 설정
    * @title : cfKeyInfo
    * @return : KeyInfoDTO
    ***************************************************/
    public KeyInfoDTO cfKeyInfo() {
        KeyInfoDTO dto = new KeyInfoDTO();
        dto.setCountryCode("kr");
        dto.setDomain("172.16.100.1.xio.io");
        dto.setEmail("paas-ta@cloud.com");
        dto.setIaas("openstack");
        dto.setId("1");
        dto.setLocalityName("mapo");
        dto.setOrganizationName("paas-ta");
        dto.setPlatform("cf");
        dto.setStateName("seoul");
        dto.setUnitName("seoul");
        return dto;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 네트워크 정보 설정
     * @title : setNetworkInfoList
     * @return : List<NetworkVO>
     ***************************************************/
    public List<NetworkVO> setNetworkInfoList(String type) {
        List<NetworkVO> list = new ArrayList<NetworkVO>();
        NetworkVO vo = new NetworkVO();
        vo.setAvailabilityZone("testZone");
        vo.setCloudSecurityGroups("seg");
        vo.setCreateUserId("admin");
        vo.setDeployType("cf-deploy");
        vo.setId(1);
        vo.setNet("internal");
        vo.setNetworkName("netName");
        vo.setSeq(1);
        vo.setSubnetDns("8.8.8.8");
        vo.setSubnetGateway("192.168.1.1");
        vo.setSubnetId("subId");
        vo.setSubnetRange("192.168.1.0/24");
        vo.setSubnetReservedFrom("192.168.1.1");
        vo.setSubnetReservedTo("192.168.1.20");
        vo.setSubnetStaticFrom("192.168.1.11");
        vo.setSubnetStaticTo("192.168.1.40");
        vo.setUpdateUserId("admin");
        vo.getAvailabilityZone();
        vo.getCloudSecurityGroups();
        vo.getCreateUserId();
        vo.getDeployType();
        vo.getId();
        vo.getNet();
        vo.getNetworkName();
        vo.getSeq();
        vo.getSubnetDns();
        vo.getSubnetGateway();
        vo.getSubnetId();
        vo.getSubnetRange();
        vo.getSubnetReservedFrom();
        vo.getSubnetReservedTo();
        vo.getSubnetStaticFrom();
        vo.getSubnetStaticTo();
        vo.getUpdateUserId();
        list.add(vo);
        if (type.equals("size")) {
            vo = new NetworkVO();
            vo.setNet("internal");
            vo.setNetworkName("netName");
            vo.setSeq(1);
            vo.setSubnetDns("8.8.8.8");
            vo.setSubnetGateway("192.168.2.1");
            vo.setSubnetId("subId");
            vo.setSubnetRange("192.168.1.2/24");
            vo.setSubnetReservedFrom("192.168.2.1");
            vo.setSubnetReservedTo("192.168.2.20");
            vo.setSubnetStaticFrom("192.168.2.11");
            vo.setSubnetStaticTo("192.168.2.40");
            list.add(vo);
            vo = new NetworkVO();
            vo.setNet("internal");
            vo.setNetworkName("netName2");
            vo.setSeq(1);
            vo.setSubnetDns("8.8.8.8");
            vo.setSubnetGateway("192.168.3.1");
            vo.setSubnetId("subId");
            vo.setSubnetRange("192.168.1.3/24");
            vo.setSubnetReservedFrom("192.168.3.1");
            vo.setSubnetReservedTo("192.168.3.20");
            vo.setSubnetStaticFrom("192.168.3.11");
            vo.setSubnetStaticTo("192.168.3.40");
            list.add(vo);
        }
        return list;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 네트워크 정보 설정
     * @title : expectNetworkList
     * @return : List<NetworkVO>
     ***************************************************/
    public List<NetworkDTO> expectNetworkList(String type) {
        List<NetworkDTO> list = new ArrayList<NetworkDTO>();
        NetworkDTO dto = new NetworkDTO();
        dto.setId("1");
        dto.setCfId("1");
        dto.setDeployType("cf");
        dto.setNet("cf-net");
        dto.setSeq("1");
        dto.setSubnetRange("/24");
        dto.setSubnetGateway("1");
        dto.setSubnetDns("8.8.8.8");
        dto.setSubnetReservedFrom("1");
        dto.setSubnetReservedTo("255");
        dto.setSubnetId("1");
        dto.setCloudSecurityGroups("seg");
        dto.setNetworkName("cf-net");
        dto.setAvailabilityZone("us-west-1");
        list.add(dto);
        if(type.equals("size")){
            dto = new NetworkDTO();
            dto.setId("1");
            dto.setCfId("1");
            dto.setDeployType("cf");
            dto.setNet("cf-net");
            dto.setSeq("1");
            dto.setSubnetRange("/24");
            dto.setSubnetGateway("1");
            dto.setSubnetDns("8.8.8.8");
            dto.setSubnetReservedFrom("1");
            dto.setSubnetReservedTo("255");
            dto.setSubnetId("1");
            dto.setCloudSecurityGroups("seg");
            dto.setNetworkName("cf-net");
            dto.setAvailabilityZone("us-west-1");
            list.add(dto);
        }
        dto.getSeq();
        dto.getBoshId();
        dto.getIaas();
        dto.getDiegoId();
        return list;
    }

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 상세 정보 조회 테스트
     * @title : testGetCfInfo
     * @return : void
     ***************************************************/
    public CfVO setCfInfo(String type) {
        CfVO vo = new CfVO();
        vo.setCountryCode("seoul");
        vo.setCreateUserId("admin");
        vo.setDeploymentFile("cf-yml");
        vo.setDeploymentName("cf");
        if(type.equalsIgnoreCase("null")) vo.setDeploymentFile("");
        vo.setDeployStatus("deploy");
        vo.setDescription("cf");
        vo.setDirectorUuid("uuid");
        vo.setDomain("domain");
        vo.setDomainOrganization("paas-ta");
        vo.setEmail("paas@com");
        if(!type.equalsIgnoreCase("iaasTypeNull")){
            vo.setIaasType("openstack");
            vo.setId(1);
        }
        vo.setUnitName("testunit");
        vo.setUpdateUserId("admin");
        vo.setTaskId(900);
        vo.setStateName("test");
        vo.setReleaseVersion("248");
        vo.setReleaseName("cf");
        vo.setPaastaMonitoringUse("yes");
        vo.setOrganizationName("pass-ta");
        vo.setKeyFile("cf-key.yml");
        vo.setLocalityName("mapo");
        vo.setLoginSecret("test");
        if(type.equalsIgnoreCase("null")) vo.setIaasType("");
        if(type.equalsIgnoreCase("resource")) vo.setResource(setResourceInfo());
        if(type.equalsIgnoreCase("vsphere")) vo.setIaasType("vsphere");
        return vo;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 리소스 정보 설정
     * @title : setResourceInfo
     * @return : ResourceVO
     ***************************************************/
    public ResourceVO setResourceInfo() {
        ResourceVO vo = new ResourceVO();
        vo.setBoshPassword("bosh");
        vo.setCreateUserId("admin");
        vo.setDeployType("openstack");
        vo.setId(1);
        vo.setLargeCpu(10);
        vo.setLargeDisk(1000);
        vo.setLargeFlavor("m1.large");
        vo.setLargeRam(1000);
        vo.setMediumCpu(5);
        vo.setMediumDisk(500);
        vo.setMediumFlavor("m1.medium");
        vo.setMediumRam(500);
        vo.setRunnerFlavor("m1.xlarge");
        vo.setSmallFlavor("m1.small");
        vo.setSmallCpu(1);
        vo.setSmallRam(1000);
        vo.setSmallDisk(100);
        vo.setStemcellName("stemcell");
        vo.setStemcellVersion("3417");
        vo.setUpdateUserId("adimn");
        return vo;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 상세 조회 결과 값 설정
    * @title : setCfDefaultParamInfo
    * @return : CfVO
    ***************************************************/
    public CfParamDTO.Default setCfDefaultParamInfo(String type) {
        CfParamDTO.Default dto = new CfParamDTO.Default();
        dto.setDeploymentName("cf");
        dto.setDirectorUuid("uuid");
        dto.setDomain("domain");
        dto.setIaas("openstack");
        dto.setDomainOrganization("paas-ta");
        dto.setPaastaMonitoringUse("yes");
        dto.setReleaseName("cf");
        dto.setReleaseVersion("222");
        if(type.equalsIgnoreCase("update")) dto.setId("1");
        return dto;
    }
    
}
