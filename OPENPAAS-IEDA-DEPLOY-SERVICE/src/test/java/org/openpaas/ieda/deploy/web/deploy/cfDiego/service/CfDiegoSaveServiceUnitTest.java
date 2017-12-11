package org.openpaas.ieda.deploy.web.deploy.cfDiego.service;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
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
import org.openpaas.ieda.deploy.web.deploy.cf.dao.CfDAO;
import org.openpaas.ieda.deploy.web.deploy.cf.dao.CfVO;
import org.openpaas.ieda.deploy.web.deploy.cf.service.CfSaveService;
import org.openpaas.ieda.deploy.web.deploy.cfDiego.dao.CfDiegoDAO;
import org.openpaas.ieda.deploy.web.deploy.cfDiego.dao.CfDiegoVO;
import org.openpaas.ieda.deploy.web.deploy.cfDiego.dto.CfDiegoParamDTO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.network.NetworkVO;
import org.openpaas.ieda.deploy.web.deploy.common.dto.network.NetworkDTO;
import org.openpaas.ieda.deploy.web.deploy.common.dto.resource.ResourceDTO;
import org.openpaas.ieda.deploy.web.deploy.diego.dao.DiegoDAO;
import org.openpaas.ieda.deploy.web.deploy.diego.dao.DiegoVO;
import org.openpaas.ieda.deploy.web.deploy.diego.service.DiegoSaveService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class CfDiegoSaveServiceUnitTest extends BaseDeployControllerUnitTest {
    
    @InjectMocks CfDiegoSaveService mockCfDiegoSaveService;
    @Mock CfSaveService mockCfSaveService;
    @Mock DiegoSaveService mockDiegoSaveService;
    @Mock CfDAO mockCfDAO;
    @Mock DiegoDAO mockDiegoDAO;
    @Mock CfDiegoDAO mockCfDiegoDAO;
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
    * @description : CF 기본 정보 저장
    * @title : testSavCfDefaultInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSavCfDefaultInfo(){
        CfVO expectCfVo = setResultCfInfo();
        CfDiegoVO expectCfDiegoVo = expectCfDiegoVO("default");
        when(mockCfSaveService.saveDefaultInfo(any(), any())).thenReturn(expectCfVo);
        when(mockCfDiegoDAO.selectCfDiegoInfoByPlaform(anyString(), anyInt())).thenReturn(expectCfDiegoVo);
        CfDiegoParamDTO.Default dto = setCfDiegoParamInfo("cf");
        mockCfDiegoSaveService.saveDefaultInfo(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Diego 기본 정보 저장
    * @title : testSaveDiegoDefaultInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveDiegoDefaultInfo(){
        CfDiegoVO expectCfDiegoVo = expectCfDiegoVO("idNull");
        DiegoVO expectDiegoVo = setResultDiegoInfo("default");
        when(mockDiegoSaveService.saveDefaultInfo(any(), any())).thenReturn(expectDiegoVo);
        when(mockCfDiegoDAO.selectCfDiegoInfoByPlaform(anyString(), anyInt())).thenReturn(expectCfDiegoVo);
        CfDiegoParamDTO.Default dto = setCfDiegoParamInfo("diego");
        mockCfDiegoSaveService.saveDefaultInfo(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF & Diego 기본 정보 저장 ID Null
    * @title : testSaveCfDefaultInfo
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testSaveCfDefaultInfoIdNull(){
        CfDiegoVO expectCfDiegoVo = expectCfDiegoVO("idNull");
        DiegoVO expectDiegoVo = setResultDiegoInfo("idNull");
        when(mockDiegoSaveService.saveDefaultInfo(any(), any())).thenReturn(expectDiegoVo);
        when(mockCfDiegoDAO.selectCfDiegoInfoByPlaform(anyString(), anyInt())).thenReturn(expectCfDiegoVo);
        CfDiegoParamDTO.Default dto = null;
        mockCfDiegoSaveService.saveDefaultInfo(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 기본 정보 삽입
    * @title : testSaveDiegoDefaultInfo
    * @return : void
    ***************************************************/
    @Test
    public void testInsertDiegoDefaultInfo(){
        CfVO expectCfVo = setResultCfInfo();
        CfDiegoVO expectCfDiegoVo = expectCfDiegoVO("default");
        when(mockCfSaveService.saveDefaultInfo(any(), any())).thenReturn(expectCfVo);
        when(mockCfDiegoDAO.selectCfDiegoInfoByPlaform(anyString(), anyInt())).thenReturn(expectCfDiegoVo);
        CfDiegoParamDTO.Default dto = setCfDiegoParamInfo("insert");
        mockCfDiegoSaveService.saveDefaultInfo(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Cf 네트워크 정보 저장
    * @title : testSaveCfNetworkInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveCfNetworkInfo(){
        List<NetworkDTO> networkDto = cfDiegoNetworkInfo("cf");
        mockCfDiegoSaveService.saveNetworkInfo(networkDto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Diego 네트워크 정보 저장
    * @title : testSaveCfNetworkInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveDiegoNetworkInfo(){
        CfDiegoVO expectCfDiegoVo = expectCfDiegoVO("default");
        when(mockCfDiegoDAO.selectCfDiegoInfoByPlaform(anyString(), anyInt())).thenReturn(expectCfDiegoVo);
        List<NetworkDTO> networkDto = cfDiegoNetworkInfo("diego");
        mockCfDiegoSaveService.saveNetworkInfo(networkDto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Diego 네트워크 정보 저장
    * @title : testSaveCfNetworkInfo
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testSaveCfDiegoNetworkInfoEmpty(){
        List<NetworkDTO> networkDto = cfDiegoNetworkInfo("idNull");
        mockCfDiegoSaveService.saveNetworkInfo(networkDto, principal);
    }
    

    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Diego 네트워크 정보 저장 중 Null Point
    * @title : testSaveDiegoNetworkInfoNullPoint
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testSaveDiegoNetworkInfoNullPoint(){
        CfDiegoVO expectCfDiegoVo = expectCfDiegoVO("default");
        when(mockCfDiegoDAO.selectCfDiegoInfoByPlaform(anyString(), anyInt())).thenReturn(expectCfDiegoVo);
        List<NetworkDTO> networkDto = cfDiegoNetworkInfo("diego");
        mockCfDiegoSaveService.saveNetworkInfo(networkDto, null);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Cf 리소스 정보 저장 및 배포 파일명 설정 
    * @title : testSaveResourceInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveCfResourceInfo(){
        ResourceDTO dto = cfDiegoResourceInfo("cf");
        when(mockCfSaveService.saveResourceInfo(any(), any())).thenReturn(setExpectResourceInfo());
        CfDiegoVO expectCfDiegoVo = expectCfDiegoVO("default");
        when(mockCfDiegoDAO.selectCfDiegoInfoByPlaform(anyString(), anyInt())).thenReturn(expectCfDiegoVo);
        mockCfDiegoSaveService.saveResourceInfo(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Diego 리소스 정보 저장 및 배포 파일명 설정 
    * @title : testSaveResourceInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveDiegoResourceInfo(){
        ResourceDTO dto = cfDiegoResourceInfo("diego");
        when(mockDiegoSaveService.saveResourceInfo(any(), any())).thenReturn(setExpectResourceInfo());
        CfDiegoVO expectCfDiegoVo = expectCfDiegoVO("default");
        when(mockCfDiegoDAO.selectCfDiegoInfoByPlaform(anyString(), anyInt())).thenReturn(expectCfDiegoVo);
        mockCfDiegoSaveService.saveResourceInfo(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF & Diego 리소스 정보 저장 후 기대 값 설정
    * @title : setResultResourceInfo
    * @return : Map<String, Object>
    ***************************************************/
    public HashMap<String, Object> setExpectResourceInfo() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("deploymentFile", "cf.yml");
        map.put("id", 1);
        return map;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF & Diego 리소스 정보 저장 파라미터 값 설정
    * @title : cfDiegoResourceInfo
    * @return : void
    ***************************************************/
    public ResourceDTO cfDiegoResourceInfo(String type) {
        ResourceDTO dto = new ResourceDTO();
        dto.setBoshPassword("bosh");
        dto.setCfId("1");
        dto.setIaas("openstack");
        dto.setKeyFile("key.yml");
        if(type.equals("cf")) dto.setPlatform("cf");
        if(type.equals("diego")) dto.setPlatform("diego");
        dto.setLargeCpu("m1.large");
        dto.setLargeDisk("m1.disk");
        dto.setLargeFlavor("m1.large");
        dto.setLargeRam("m1.large");
        dto.setMediumCpu("1");
        dto.setMediumDisk("8192");
        dto.setMediumFlavor("m1.medium");
        dto.setMediumRam("111");
        dto.setRunnerCpu("4");
        dto.setRunnerDisk("1");
        dto.setRunnerRam("2313");
        dto.setSmallCpu("1");
        dto.setSmallDisk("123");
        dto.setSmallRam("8192");
        dto.setStemcellName("os");
        dto.setStemcellVersion("3127");
        return dto;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 네트워크 정보 저장 파라미터 값 설정
    * @title : cfNetworkInfo
    * @return : List<NetworkDTO>
    ***************************************************/
    public List<NetworkDTO> cfDiegoNetworkInfo(String type) {
        List<NetworkDTO> list = new ArrayList<NetworkDTO>();
        NetworkDTO dto = new NetworkDTO();
        dto.setId("1");
        if(!type.equals("idNull")) {
            dto.setDiegoId("1");
            dto.setCfId("1");
        }
        if(type.equals("cf")) {
            dto.setCfId("1");
            dto.setDeployType("cf");
        }
        if(type.equals("diego")) {
            dto.setCfId(null);
            dto.setDeployType("diego");
        }
        dto.setNet("cf-net");
        dto.setSeq("1");
        dto.setPublicStaticIP("172.16.100.1");
        dto.setSubnetRange("/24");
        dto.setSubnetGateway("1");
        dto.setSubnetDns("8.8.8.8");

        
        dto.setSubnetReservedFrom("1");
        dto.setSubnetReservedTo("255");
        dto.setSubnetStaticFrom("1");
        dto.setSubnetStaticTo("255");
        dto.setSubnetId("1");
        dto.setCloudSecurityGroups("seg");
        dto.setNetworkName("cf-net");
        dto.setAvailabilityZone("us-west-1");
        list.add(dto);
        return list;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF & DIEGO 상세 조회 값 설정
    * @title : setCfDiegoInfoList
    * @return : List<CfDiegoVO> 
    ***************************************************/
    public CfDiegoVO expectCfDiegoVO(String type) {
        CfDiegoVO vo = new CfDiegoVO();
        vo.setDeployStatus("deploy");
        vo.setIaasType("aws");
        vo.setId(1);
        vo.setRecid(1);
        vo.setUpdateUserId("admin");
        vo.setCreateUserId("admin");
        vo.setCfVo(setResultCfInfo());
        if(type.equals("idNull")){
            vo.setDiegoVo(setResultDiegoInfo("idNull"));
        }else {
            vo.setDiegoVo(setResultDiegoInfo("default"));
        }
            
        
        return vo;
    }
    
    /***************************************************
    * @param string 
     * @project : Paas 플랫폼 설치 자동화
    * @description : DIEGO 상세 정보 조회 결과 값 설정 
    * @title : setResultDiegoInfo
    * @return : DiegoVO
    ***************************************************/
    public DiegoVO setResultDiegoInfo(String type) {
        DiegoVO vo = new DiegoVO();
        vo.setCadvisorDriverIp("10.0.0.6");
        vo.setCadvisorDriverPort("9033");
        vo.setCfDeployment("cf.yml");
        vo.setCfId(1);
        vo.setCflinuxfs2rootfsreleaseName("cflinux");
        vo.setCflinuxfs2rootfsreleaseVersion("1.150.1");
        vo.setCfName("cf-aws");
        vo.setCfReleaseName("cf");
        vo.setCfReleaseVersion("272");
        vo.setCreateUserId("admin");
        vo.setDeploymentFile("aws-diego.yml");
        vo.setDeploymentName("aws-diego");
        vo.setDeployStatus("deploy");
        vo.setDiegoReleaseName("diego");
        vo.setDiegoReleaseVersion("1.25.3");
        vo.setDirectorUuid("uuid");
        vo.setEtcdReleaseName("etcd");
        vo.setEtcdReleaseVersion("104");
        vo.setGardenReleaseName("garden");
        vo.setGardenReleaseVersion("153");
        vo.setIaasType("aws");
        if(!type.equals("idNull")) vo.setId(5);
        
        vo.setKeyFile("key.yml");
        vo.setPaastaMonitoringUse("true");
        vo.setTaskId(1);
        vo.setUpdateUserId("admin");
        List<NetworkVO> list = new ArrayList<NetworkVO>();
        NetworkVO networkVo = new NetworkVO();
        networkVo.setAvailabilityZone("west-1");
        networkVo.setCloudSecurityGroups("seg");
        networkVo.setCreateUserId("admin");
        networkVo.setDeployType("DEIGO");
        networkVo.setId(1);
        networkVo.setNet("netId");
        networkVo.setNetworkName("netName");
        networkVo.setPublicStaticIP("113.123.123.123");
        networkVo.setSeq(0);
        networkVo.setSubnetDns("8.8.8.8");
        networkVo.setSubnetRange("192.168.0.0/24");
        networkVo.setSubnetReservedFrom("192.168.0.1");
        networkVo.setSubnetReservedTo("192.168.0.155");
        networkVo.setSubnetStaticFrom("192.168.155");
        networkVo.setSubnetStaticTo("192.168.0.255");
        networkVo.setSubnetGateway("192.168.0.1");
        networkVo.setUpdateUserId("admin");
        vo.setNetwork(networkVo);
        list.add(networkVo);
        vo.setNetworks(list);
        return vo;
    }

    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 상세 조회 결과 값 설정
    * @title : setResultInfo
    * @return : CfVO
    ***************************************************/
    public CfVO setResultCfInfo() {
        CfVO vo = new CfVO();
        vo.setId(1);
        vo.setIaasType("openstack");
        vo.setDeaMemoryMB(31728);
        vo.setDeaDiskMB(8192);
        vo.setReleaseName("cf");
        vo.setReleaseVersion("222");
        vo.setAppSshFingerprint("fingerprint");
        vo.setDiegoYn("N");
        vo.setDeploymentName("cf");
        vo.setDeploymentFile("cf-yml");
        vo.setDomain("test.domain");
        vo.setPaastaMonitoringUse("yes");
        vo.setIngestorIp("172.16.100.100");
        vo.setCountryCode("kor");
        vo.setStateName("seoul");
        vo.setLocalityName("mapo");
        vo.setUnitName("paas-ta");
        vo.setEmail("test@paasta.co.kr");
        vo.setKeyFile("keyFile");
        vo.setOrganizationName("paasta");
        vo.setDeployStatus("deploying");
        return vo;
    }
    
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 기본 정보 값 설정
    * @title : setCfDefaultParamInfo
    * @return : CfDiegoParamDTO.Default
    ***************************************************/
    public CfDiegoParamDTO.Default setCfDiegoParamInfo(String type) {
        CfDiegoParamDTO.Default dto = new CfDiegoParamDTO.Default();
        dto.setAppSshFingerprint("fingerPrint");
        dto.setDeaDiskMB("32718");
        dto.setDeaMemoryMB("8192");
        dto.setDeploymentName("cf");
        if(type.equals("cf") || type.equals("insert")) dto.setPlatform("cf");
        if(type.equals("diego")) dto.setPlatform("diego");
        dto.setDescription("cf");
        dto.setDiegoYn("N");
        dto.setDirectorUuid("uuid");
        dto.setDomain("domain");
        dto.setIaas("openstack");
        if(!type.equals("insert")) dto.setId("1");
        
        dto.setIngestorIp("172.16.100.1");
        dto.setLoginSecret("login");
        dto.setDomainOrganization("paas-ta");
        dto.setPaastaMonitoringUse("yes");
        dto.setReleaseName("cf");
        dto.setReleaseVersion("272");
        dto.setDiegoReleaseName("diego");
        dto.setDiegoReleaseVersion("1.25.3");
        dto.setCfId(1);
        dto.setGardenReleaseName("garden");
        dto.setGardenReleaseVersion("105");
        dto.setEtcdReleaseName("etcd");
        dto.setEtcdReleaseVersion("104");
        dto.setCfDeployment("cf.yml");
        dto.setCfDeploymentName("cf");
        dto.setCflinuxfs2rootfsreleaseName("cflinux");
        dto.setKeyFile("key.yml");
        
        dto.getAppSshFingerprint();
        dto.getDeaDiskMB();
        dto.getDeaMemoryMB();
        dto.getDeploymentName();
        dto.getPlatform();
        dto.getDescription();
        dto.getDiegoYn();
        dto.getDirectorUuid();
        dto.getDomain();
        dto.getIaas();
        dto.getId();
        dto.getIngestorIp();
        dto.getLoginSecret();
        dto.getDomainOrganization();
        dto.getPaastaMonitoringUse();
        dto.getReleaseName();
        dto.getReleaseVersion();
        dto.getDiegoReleaseName();
        dto.getDiegoReleaseVersion();
        dto.getCfId();
        dto.getGardenReleaseName();
        dto.getGardenReleaseVersion();
        dto.getEtcdReleaseName();
        dto.getEtcdReleaseVersion();
        dto.getCfDeployment();
        dto.getCfDeploymentName();
        dto.getCflinuxfs2rootfsreleaseName();
        dto.getKeyFile();
        return dto;
    }
    
}
