package org.openpaas.ieda.deploy.web.deploy.diego.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.openpaas.ieda.deploy.web.deploy.common.dao.network.NetworkDAO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.network.NetworkVO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.resource.ResourceDAO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.resource.ResourceVO;
import org.openpaas.ieda.deploy.web.deploy.common.dto.network.NetworkDTO;
import org.openpaas.ieda.deploy.web.deploy.common.dto.resource.ResourceDTO;
import org.openpaas.ieda.deploy.web.deploy.diego.dao.DiegoDAO;
import org.openpaas.ieda.deploy.web.deploy.diego.dao.DiegoVO;
import org.openpaas.ieda.deploy.web.deploy.diego.dto.DiegoParamDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class DiegoSaveServiceUnitTest extends BaseDeployControllerUnitTest {
    
    @InjectMocks DiegoSaveService mockDiegoSaveService;
    @Mock DiegoDAO mockDiegoDAO;
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
    * @description : Diego 기본정보 삽입
    * @title : testInsertDefaultInfo
    * @return : void
    ***************************************************/
    @Test
    public void testInsertDefaultInfo(){
        DiegoParamDTO.Default dto = setDiegoDefaultInfo("insert");
        when(mockDiegoDAO.selectDiegoDeploymentNameDuplication(anyString(), anyString(), anyInt())).thenReturn(0);
        mockDiegoSaveService.saveDefaultInfo(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Diego 기본정보 수정
    * @title : testUpdateDefaultInfo
    * @return : void
    ***************************************************/
    @Test
    public void testUpdateDefaultInfo(){
        DiegoParamDTO.Default dto = setDiegoDefaultInfo("update");
        DiegoVO expectVo = setResultDiegoInfo("update");
        when(mockDiegoDAO.selectDiegoInfo(anyInt())).thenReturn(expectVo);
        when(mockDiegoDAO.selectDiegoDeploymentNameDuplication(anyString(), anyString(), anyInt())).thenReturn(0);
        DiegoVO resultVo = mockDiegoSaveService.saveDefaultInfo(dto, principal);
        assertEquals(resultVo.getCadvisorDriverIp(), expectVo.getCadvisorDriverIp());
        assertEquals(resultVo.getCadvisorDriverPort(), expectVo.getCadvisorDriverPort());
        assertEquals(resultVo.getCfDeployment(), expectVo.getCfDeployment());
        assertEquals(resultVo.getCflinuxfs2rootfsreleaseVersion(), expectVo.getCflinuxfs2rootfsreleaseVersion());
        assertEquals(resultVo.getCflinuxfs2rootfsreleaseName(), expectVo.getCflinuxfs2rootfsreleaseName());
        assertEquals(resultVo.getCfName(), expectVo.getCfName());
        assertEquals(resultVo.getCfReleaseName(), expectVo.getCfReleaseName());
        assertEquals(resultVo.getDeploymentFile(), expectVo.getDeploymentFile());
        assertEquals(resultVo.getDeployStatus(), expectVo.getDeployStatus());
        assertEquals(resultVo.getDiegoReleaseName(), expectVo.getDiegoReleaseName());
        assertEquals(resultVo.getEtcdReleaseVersion(), expectVo.getEtcdReleaseVersion());
        assertEquals(resultVo.getPaastaMonitoringUse(), expectVo.getPaastaMonitoringUse());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : DIEGO 기본 정보 저장 중 배포 명 중복
    * @title : testDeploymentFileConflictSaveDefaultInfo
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testDeploymentFileConflictSaveDefaultInfo(){
        DiegoParamDTO.Default dto = setDiegoDefaultInfo("insert");
        when(mockDiegoDAO.selectDiegoDeploymentNameDuplication(anyString(), anyString(), anyInt())).thenReturn(1);
        mockDiegoSaveService.saveDefaultInfo(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : DIEGO 네트워크 정보 저장
    * @title : testSaveNetworkInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveNetworkInfo(){
        List<NetworkDTO> dto = resultNetworkListInfo("default");
        List<NetworkVO> expectNetworkList = resultNetworkInfoList();
        when(mockNetworkDAO.selectNetworkList(anyInt(), anyString())).thenReturn(expectNetworkList);
        when(mockDiegoDAO.selectDiegoJobSettingInfoListBycfId(anyString(), anyInt())).thenReturn(resultJobListInfo());
        DiegoVO resultVo = mockDiegoSaveService.saveNetworkInfo(dto, principal);
        assertEquals(resultVo.getNetworks().get(0).getAvailabilityZone(), dto.get(0).getAvailabilityZone());
        assertEquals(resultVo.getNetworks().get(0).getCloudSecurityGroups(), dto.get(0).getCloudSecurityGroups());
        assertEquals(resultVo.getNetworks().get(0).getSubnetDns(), dto.get(0).getSubnetDns());
        assertEquals(resultVo.getNetworks().get(0).getSubnetReservedFrom(), dto.get(0).getSubnetReservedFrom());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : DIEGO 네트워크 정보 사이즈가 2개 일 경우
    * @title : testSaveNetworkInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveNetworkInfoSize(){
        List<NetworkDTO> dto = resultNetworkListInfo("size");
        List<NetworkVO> expectNetworkList = resultNetworkInfoList();
        when(mockNetworkDAO.selectNetworkList(anyInt(), anyString())).thenReturn(expectNetworkList);
        when(mockDiegoDAO.selectDiegoJobSettingInfoListBycfId(anyString(), anyInt())).thenReturn(resultJobListInfo());
        DiegoVO resultVo = mockDiegoSaveService.saveNetworkInfo(dto, principal);
        assertEquals(resultVo.getNetworks().get(0).getAvailabilityZone(), dto.get(0).getAvailabilityZone());
        assertEquals(resultVo.getNetworks().get(0).getCloudSecurityGroups(), dto.get(0).getCloudSecurityGroups());
        assertEquals(resultVo.getNetworks().get(0).getSubnetDns(), dto.get(0).getSubnetDns());
        assertEquals(resultVo.getNetworks().get(0).getSubnetReservedFrom(), dto.get(0).getSubnetReservedFrom());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Diego 리소스 삽입
    * @title : testInsertResourceInfo
    * @return : void
    ***************************************************/
    @Test
    public void testInsertResourceInfo(){
        ResourceDTO dto = setResourceInfo("defalut");
        DiegoVO expectVo = setResultDiegoInfo("default");
        CfVO cfVo = setResultCfInfo();
        when(mockDiegoDAO.selectResourceInfoById(anyInt(), anyString())).thenReturn(expectVo);
        when(mockCfDAO.selectCfInfoByDeploymentName(anyString(), anyString())).thenReturn(cfVo);
        mockDiegoSaveService.saveResourceInfo(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Diego 배포 파일 명 생성 중 에러
    * @title : testMakeDeploymentNameError
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testMakeDeploymentNameError(){
        DiegoVO expectVo = setResultDiegoInfo("error");
        mockDiegoSaveService.makeDeploymentName(expectVo);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Diego 리소스 수정
    * @title : testInsertResourceInfo
    * @return : void
    ***************************************************/
    @Test
    public void testUpdateResourceInfo(){
        ResourceDTO dto = setResourceInfo("update");
        DiegoVO expectVo = setResultDiegoInfo("update");
        CfVO cfVo = setResultCfInfo();
        when(mockDiegoDAO.selectResourceInfoById(anyInt(), anyString())).thenReturn(expectVo);
        when(mockCfDAO.selectCfInfoByDeploymentName(anyString(), anyString())).thenReturn(cfVo);
        Map<String, Object> map = mockDiegoSaveService.saveResourceInfo(dto, principal);
        assertEquals(map.get("deploymentFile"), "aws-diego.yml");
        assertEquals(map.get("id"), 1);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Diego jobs 정보 저장
    * @title : testSaveDiegoJobsInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveDiegoJobsInfo(){
        when(mockDiegoDAO.selectDiegoJobSettingInfoListBycfId(anyString(), anyInt())).thenReturn(resultJobListInfo());
        mockDiegoSaveService.saveDiegoJobsInfo(resultJobListInfo2(), principal);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 정보 설정
     * @title : setResultCfInfo
     * @return : CfVO
    ***************************************************/
    public CfVO setResultCfInfo() {
        CfVO cfVo = new CfVO();
        cfVo.setAppSshFingerprint("");
        cfVo.setCountryCode("Korea");
        cfVo.setCreateDate(new Date());
        cfVo.setCreateUserId(principal.getName());
        cfVo.setDeploymentFile("openstack-cf-1.yml");
        cfVo.setDeploymentName("cf");
        cfVo.setDescription("desc");
        cfVo.setDiegoYn("Y");
        cfVo.setDirectorUuid("");
        cfVo.setDomain("10.10.10.10.xip.io");
        cfVo.setDomainOrganization("domain desc");
        cfVo.setIaasType("openstack");
        cfVo.setId(1);
        cfVo.setKeyFile("openstack-cf-key-1.yml");
        cfVo.setReleaseName("cf");
        cfVo.setReleaseVersion("272");
        return cfVo;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Job 정보 설정
    * @title : resultJobListInfo
    * @return : List<HashMap<String, String>>
    ***************************************************/
    public List<HashMap<String, String>> resultJobListInfo2() {
        List<HashMap<String, String>> mapList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("jobName", "databases");
        map.put("zone", "z1");
        map.put("id", "1");
        mapList.add(map);
        return mapList;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 네트워크 저장 정보 설정
    * @title : resultNetworkListInfo
    * @return : List<NetworkDTO>
    ***************************************************/
    public ResourceDTO setResourceInfo(String type) {
        ResourceDTO dto = new ResourceDTO();
        dto.setBoshPassword("bosh");
        dto.setCfId("1");
        dto.setIaas("openstack");
        dto.setKeyFile("key.yml");
        dto.setPlatform("cf");
        dto.setId("1");
        dto.setLargeCpu("1");
        dto.setLargeDisk("1");
        dto.setLargeFlavor("m1.large");
        dto.setLargeRam("1");
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
        
        dto.getBoshPassword();
        dto.getCfId();
        dto.getId();
        dto.getIaas();
        dto.getKeyFile();
        dto.getPlatform();
        dto.getLargeCpu();
        dto.getLargeDisk();
        dto.getLargeFlavor();
        dto.getLargeRam();
        dto.getMediumCpu();
        dto.getMediumDisk();
        dto.getMediumFlavor();
        dto.getMediumRam();
        dto.getRunnerCpu();
        dto.getRunnerDisk();
        dto.getRunnerRam();
        dto.getSmallCpu();
        dto.getSmallDisk();
        dto.getSmallRam();
        dto.getStemcellName();
        dto.getStemcellVersion();
        
        return dto;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Job 정보 설정
    * @title : resultJobListInfo
    * @return : List<HashMap<String, String>>
    ***************************************************/
    public List<HashMap<String, Object>> resultJobListInfo() {
        List<HashMap<String, Object>> mapList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("jobName", "databases");
        map.put("zone", "z3");
        mapList.add(map);
        return mapList;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 네트워크 정보 설정
    * @title : resultNetworkInfoList
    * @return : List<NetworkVO>
    ***************************************************/
    public List<NetworkVO> resultNetworkInfoList(){
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
        
        networkVo.getAvailabilityZone();
        networkVo.getCloudSecurityGroups();
        networkVo.getCreateUserId();
        networkVo.getDeployType();
        networkVo.getId();
        networkVo.getNet();
        networkVo.getNetworkName();
        networkVo.getPublicStaticIP();
        networkVo.getSeq();
        networkVo.getSubnetDns();
        networkVo.getSubnetRange();
        networkVo.getSubnetReservedFrom();
        networkVo.getSubnetReservedTo();
        networkVo.getSubnetStaticFrom();
        networkVo.getSubnetStaticTo();
        networkVo.getSubnetGateway();
        networkVo.getUpdateUserId();
        list.add(networkVo);
        return list;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 네트워크 저장 정보 설정
    * @title : resultNetworkListInfo
    * @return : List<NetworkDTO>
    ***************************************************/
    public List<NetworkDTO> resultNetworkListInfo(String type) {
        List<NetworkDTO> list = new ArrayList<NetworkDTO>();
        NetworkDTO dto = new NetworkDTO();
        dto.setAvailabilityZone("west-1");
        dto.setCloudSecurityGroups("seg");
        dto.setDeployType("DEIGO");
        dto.setId("1");
        dto.setNet("netId");
        dto.setDiegoId("1");
        dto.setNetworkName("netName");
        dto.setPublicStaticIP("113.123.123.123");
        dto.setSeq("1");
        dto.setSubnetDns("8.8.8.8");
        dto.setSubnetRange("192.168.0.0/24");
        dto.setSubnetReservedFrom("192.168.0.1");
        dto.setSubnetReservedTo("192.168.0.155");
        dto.setSubnetStaticFrom("192.168.155");
        dto.setSubnetStaticTo("192.168.0.255");
        
        dto.getAvailabilityZone();
        dto.getCloudSecurityGroups();
        dto.getDeployType();
        dto.getId();
        dto.getNet();
        dto.getNetworkName();
        dto.getPublicStaticIP();
        dto.getDiegoId();
        dto.getSeq();
        dto.getSubnetDns();
        dto.getSubnetRange();
        dto.getSubnetReservedFrom();
        dto.getSubnetReservedTo();
        dto.getSubnetStaticFrom();
        dto.getSubnetStaticTo();
        list.add(dto);
        if(type.equals("size")){
            dto = new NetworkDTO();
            dto.setAvailabilityZone("west-1");
            dto.setCloudSecurityGroups("seg");
            dto.setDeployType("DEIGO");
            dto.setId("1");
            dto.setNet("netId");
            dto.setDiegoId("1");
            dto.setNetworkName("netName");
            dto.setPublicStaticIP("113.123.123.123");
            dto.setSeq("1");
            dto.setSubnetDns("8.8.8.8");
            dto.setSubnetRange("192.168.0.0/24");
            dto.setSubnetReservedFrom("192.168.0.1");
            dto.setSubnetReservedTo("192.168.0.155");
            dto.setSubnetStaticFrom("192.168.155");
            dto.setSubnetStaticTo("192.168.0.255");
            list.add(dto);
        }
        return list;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : DIEGO 기본 정보 저정 파라미터 값 설정
    * @title : setDiegoDefaultInfo
    * @return : DiegoParamDTO.Default
    ***************************************************/
    public DiegoParamDTO.Default setDiegoDefaultInfo(String type) {
        DiegoParamDTO.Default dto = new DiegoParamDTO.Default();
        dto.setCadvisorDriverIp("10.0.0.6");
        dto.setCfId(1);
        dto.setCflinuxfs2rootfsreleaseName("cflinux");
        dto.setCflinuxfs2rootfsreleaseVersion("1.154.0");
        dto.setDeploymentName("cf-aws-diego");
        dto.setDiegoReleaseName("diego");
        dto.setDiegoReleaseVersion("1.25.3");
        dto.setDirectorUuid("uuid");
        dto.setEtcdReleaseName("etcd");
        dto.setEtcdReleaseVersion("104");
        dto.setGardenReleaseName("garden");
        dto.setGardenReleaseVersion("172");
        dto.setIaas("aws");
        dto.setCfDeploymentName("cf-aws");
        dto.setCfDeploymentFile("");
        dto.setPaastaMonitoringUse("true");
        dto.setCfDeploymentName("");
        if(type.equals("update")){
            dto.setId("1");
            dto.setCfDeploymentFile("cf.yml");
        }
        
        dto.getCadvisorDriverIp();
        dto.getCfDeploymentName();
        dto.getCfId();
        dto.getCflinuxfs2rootfsreleaseName();
        dto.getCflinuxfs2rootfsreleaseVersion();
        dto.getCfDeploymentFile();
        dto.getDeploymentName();
        dto.getDiegoReleaseName();
        dto.getDiegoReleaseVersion();
        dto.getDirectorUuid();
        dto.getEtcdReleaseName();
        dto.getEtcdReleaseVersion();
        dto.getGardenReleaseName();
        dto.getGardenReleaseVersion();
        dto.getIaas();
        dto.getPaastaMonitoringUse();
        dto.getId();
        
        return dto;
    }
    
    /***************************************************
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
        if(type.equals("update")){
            vo.setDeploymentFile("aws-diego.yml");
        }

        vo.setDeploymentName("aws-diego");
        vo.setDeployStatus("deploy");
        vo.setDiegoReleaseName("diego");
        vo.setDiegoReleaseVersion("1.25.3");
        vo.setDirectorUuid("uuid");
        vo.setEtcdReleaseName("etcd");
        vo.setEtcdReleaseVersion("104");
        vo.setGardenReleaseName("garden");
        vo.setGardenReleaseVersion("153");
        if(type.equals("update")){
            vo.setId(1);
            vo.setIaasType("vsphere");
        } else if(!type.equals("error")){
            vo.setId(1);
            vo.setIaasType("aws");
        }
        
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
        ResourceVO resourceVO = new ResourceVO();
        resourceVO.setBoshPassword("bosh");
        resourceVO.setCreateUserId("admin");
        resourceVO.setDeployType("openstack");
        if(type.equals("update")){
            resourceVO.setId(1);
        }
        resourceVO.setLargeCpu(10);
        resourceVO.setLargeDisk(1000);
        resourceVO.setLargeFlavor("m1.large");
        resourceVO.setLargeRam(1000);
        resourceVO.setMediumCpu(5);
        resourceVO.setMediumDisk(500);
        resourceVO.setMediumFlavor("m1.medium");
        resourceVO.setMediumRam(500);
        resourceVO.setRunnerCpu(1500);
        resourceVO.setRunnerDisk(1500);
        resourceVO.setRunnerFlavor("m1.xlarge");
        resourceVO.setRunnerRam(1500);
        resourceVO.setSmallFlavor("m1.small");
        resourceVO.setSmallCpu(1);
        resourceVO.setSmallRam(1000);
        resourceVO.setSmallDisk(100);
        resourceVO.setStemcellName("stemcell");
        resourceVO.setStemcellVersion("3417");
        resourceVO.setUpdateUserId("adimn");
        
        resourceVO.getLargeCpu();
        resourceVO.getLargeDisk();
        resourceVO.getLargeFlavor();
        resourceVO.getLargeRam();
        resourceVO.getMediumCpu();
        resourceVO.getMediumDisk();
        resourceVO.getMediumFlavor();
        resourceVO.getMediumRam();
        resourceVO.getRunnerCpu();
        resourceVO.getRunnerDisk();
        resourceVO.getRunnerFlavor();
        resourceVO.getRunnerRam();
        resourceVO.getSmallFlavor();
        resourceVO.getSmallCpu();
        resourceVO.getSmallRam();
        resourceVO.getSmallDisk();
        resourceVO.getStemcellName();
        resourceVO.getStemcellVersion();
        resourceVO.getUpdateUserId();
        
        vo.setResource(resourceVO);
        list.add(networkVo);
        vo.setNetworks(list);
        return vo;
    }
    
}
