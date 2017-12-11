package org.openpaas.ieda.deploy.web.deploy.cfDiego.service;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.ArrayList;
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
import org.openpaas.ieda.deploy.web.deploy.cf.dao.CfVO;
import org.openpaas.ieda.deploy.web.deploy.cf.service.CfDeleteDeployAsyncService;
import org.openpaas.ieda.deploy.web.deploy.cf.service.CfService;
import org.openpaas.ieda.deploy.web.deploy.cfDiego.dao.CfDiegoDAO;
import org.openpaas.ieda.deploy.web.deploy.cfDiego.dao.CfDiegoVO;
import org.openpaas.ieda.deploy.web.deploy.cfDiego.dto.CfDiegoParamDTO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.network.NetworkDAO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.network.NetworkVO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.resource.ResourceDAO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.resource.ResourceVO;
import org.openpaas.ieda.deploy.web.deploy.diego.dao.DiegoVO;
import org.openpaas.ieda.deploy.web.deploy.diego.service.DiegoDeleteDeployAsyncService;
import org.openpaas.ieda.deploy.web.deploy.diego.service.DiegoService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class CfDiegoServiceUnitTest extends BaseDeployControllerUnitTest {
    
    
    @InjectMocks CfDiegoService mockCfDiegoService;
    @Mock CfService mockCfService;
    @Mock CfDiegoDAO mockCfDiegoDAO;
    @Mock DiegoService mockDiegoService;
    @Mock NetworkDAO mockNetworkDAO;
    @Mock ResourceDAO mockResourceDAO;
    @Mock CfDeleteDeployAsyncService mockCfDeleteDeployAsyncService;
    @Mock MessageSource mockMessageSource;
    @Mock DiegoDeleteDeployAsyncService mockDiegoDeleteDeployAsyncService;    
    
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
    * @description : CF & Dieg 목록 조회
    * @title : testGetCfDiegoList
    * @return : void
    ***************************************************/
    @Test
    public void testGetCfDiegoList(){
        List<CfDiegoVO> expectList = setCfDiegoInfoList();
        List<NetworkVO> networkListDto = setNetworkInfoList();
        ResourceVO expectResourceVo = setCfDiegoResourceInfo();
        when(mockCfDiegoDAO.selectCfDiegoList(anyString())).thenReturn(expectList);
        when(mockNetworkDAO.selectNetworkList(anyInt(), anyString())).thenReturn(networkListDto);
        when(mockResourceDAO.selectResourceInfo(anyInt(), anyString())).thenReturn(expectResourceVo);
        mockCfDiegoService.getCfDiegoList("aws");
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF & DIego 상세 정보 조회
    * @title : testGetCfDiegoInfo
    * @return : void
    ***************************************************/
    @Test
    public void testGetCfDiegoInfo(){
        CfDiegoVO expectCfDiegoVo = expectCfDiegoVO();
        when(mockCfDiegoDAO.selectCfDiegoInfoById(anyInt())).thenReturn(expectCfDiegoVo);
        when(mockCfService.getCfInfo(anyInt())).thenReturn(setResultCfInfo());
        when(mockDiegoService.getDiegoDetailInfo(anyInt())).thenReturn(setResultDiegoInfo());
        mockCfDiegoService.getCfDiegoInfo(1);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF & DIego 상세 정보 조회
    * @title : 
    * @return : 
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testGetCfDiegoInfoNull(){
        when(mockCfDiegoDAO.selectCfDiegoInfoById(anyInt())).thenReturn(null);
        mockCfDiegoService.getCfDiegoInfo(1);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 및 Diego 배포 파일 생성
    * @title : createSettingFile
    * @return : void
    ***************************************************/
    @Test
    public void testCfCreateSettingFile(){
        CfDiegoParamDTO.Install dto = setCfDiegoInstallDto("cf");
        when(mockCfService.getCfInfo(anyInt())).thenReturn(setResultCfInfo());
        mockCfDiegoService.createSettingFile(dto);
    }
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 및 Diego 배포 파일 생성
    * @title : createSettingFile
    * @return : void
    ***************************************************/
    @Test
    public void testDiegoCreateSettingFile(){
        CfDiegoParamDTO.Install dto = setCfDiegoInstallDto("diego");
        when(mockCfService.getCfInfo(anyInt())).thenReturn(setResultCfInfo());
        mockCfDiegoService.createSettingFile(dto);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 및 Diego 단순 레코드 삭제
    * @title : testDeleteCfDiegoInfoRecord
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteCfInfoRecord(){
        CfDiegoParamDTO.Delete dto = setCfDiegoDeleteDto("cf");
        CfDiegoVO expectCfDiegoVo = expectCfDiegoVO();
        when(mockCfDiegoDAO.selectCfDiegoInfoByPlaform(anyString(), anyInt())).thenReturn(expectCfDiegoVo);
        mockCfDiegoService.deleteCfDiegoInfoRecord(dto);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 및 Diego 단순 레코드 삭제
    * @title : testDeleteCfDiegoInfoRecord
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteDiegoInfoRecord(){
        CfDiegoParamDTO.Delete dto = setCfDiegoDeleteDto("diego");
        CfDiegoVO expectCfDiegoVo = expectCfDiegoVO();
        when(mockCfDiegoDAO.selectCfDiegoInfoByPlaform(anyString(), anyInt())).thenReturn(expectCfDiegoVo);
        mockCfDiegoService.deleteCfDiegoInfoRecord(dto);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 삭제
    * @title : testDeleteCfDiego
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteCf(){
        CfDiegoParamDTO.Delete dto = setCfDiegoDeleteDto("cf");
        CfDiegoVO expectCfDiegoVo = expectCfDiegoVO();
        when(mockCfDiegoDAO.selectCfDiegoInfoByPlaform(anyString(), anyInt())).thenReturn(expectCfDiegoVo);
        mockCfDiegoService.deleteCfDiego(dto, principal);
    }
    
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Diego 삭제
    * @title : testDeleteCfDiego
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteDiego(){
        CfDiegoParamDTO.Delete dto = setCfDiegoDeleteDto("diego");
        CfDiegoVO expectCfDiegoVo = expectCfDiegoVO();
        when(mockCfDiegoDAO.selectCfDiegoInfoByPlaform(anyString(), anyInt())).thenReturn(expectCfDiegoVo);
        mockCfDiegoService.deleteCfDiego(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF & Diego 삭제 값 설정
    * @title : setCfDiegoDeleteDto
    * @return : CfDiegoParamDTO.Delete
    ***************************************************/
    public CfDiegoParamDTO.Delete setCfDiegoDeleteDto(String type) {
        CfDiegoParamDTO.Delete dto = new CfDiegoParamDTO.Delete();
        dto.setIaas("aws");
        dto.setId("1");
        if(type.equals("cf")) dto.setPlatform("cf");
        if(type.equals("diego")) dto.setPlatform("diego");
        return dto;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF & Diego 리소스 정보 저장 후 기대 값 설정
    * @title : setCfDiegoInstallDto
    * @return : CfDiegoParamDTO.Install
    ***************************************************/
    public CfDiegoParamDTO.Install setCfDiegoInstallDto(String type) {
        CfDiegoParamDTO.Install dto = new CfDiegoParamDTO.Install();
        dto.setIaas("aws");
        dto.setId("1");
        if(type.equals("cf")) dto.setPlatform("cf");
        if(type.equals("diego")) dto.setPlatform("diego");
        return dto;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF & DIEGO 상세 조회 값 설정
    * @title : setCfDiegoInfoList
    * @return : List<CfDiegoVO> 
    ***************************************************/
    public CfDiegoVO expectCfDiegoVO() {
        CfDiegoVO vo = new CfDiegoVO();
        vo.setDeployStatus("deploy");
        vo.setIaasType("aws");
        vo.setId(1);
        vo.setRecid(1);
        vo.setUpdateUserId("admin");
        vo.setCreateUserId("admin");
        vo.setCfVo(setResultCfInfo());
        vo.setDiegoVo(setResultDiegoInfo());
        
        return vo;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF & DIEGO 리소스 정보 설정
    * @title : setCfDiegoResourceInfo
    * @return : ResourceVO
    ***************************************************/
    public ResourceVO setCfDiegoResourceInfo() {
        ResourceVO resourceVO = new ResourceVO();
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
        return resourceVO;
    }

    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF & DIEGO 네트워크 정보 설정
    * @title : setNetworkInfoList
    * @return : List<NetworkVO>
    ***************************************************/
    public List<NetworkVO> setNetworkInfoList(){
        List<NetworkVO> list = new ArrayList<NetworkVO>();
        NetworkVO networkVo = new NetworkVO();
        networkVo.setAvailabilityZone("west-1");
        networkVo.setCloudSecurityGroups("seg");
        networkVo.setCreateUserId("admin");
        networkVo.setDeployType("DEIGO");
        networkVo.setId(1);
        networkVo.setNet("internal");
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
        list.add(networkVo);
        networkVo = new NetworkVO();
        networkVo.setAvailabilityZone("west-1");
        networkVo.setCloudSecurityGroups("seg");
        networkVo.setCreateUserId("admin");
        networkVo.setDeployType("DEIGO");
        networkVo.setId(1);
        networkVo.setNet("internal");
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
        list.add(networkVo);
        networkVo = new NetworkVO();
        networkVo.setAvailabilityZone("west-1");
        networkVo.setCloudSecurityGroups("seg");
        networkVo.setCreateUserId("admin");
        networkVo.setDeployType("DEIGO");
        networkVo.setId(1);
        networkVo.setNet("internal");
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
        list.add(networkVo);
        return list;
    }
    
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF & DIEGO 목록 조회 값 설정
    * @title : setCfDiegoInfoList
    * @return : List<CfDiegoVO> 
    ***************************************************/
    public List<CfDiegoVO> setCfDiegoInfoList() {
        List<CfDiegoVO> list = new ArrayList<CfDiegoVO>();
        CfDiegoVO vo = new CfDiegoVO();
        vo.setDeployStatus("deploy");
        vo.setIaasType("aws");
        vo.setId(1);
        vo.setRecid(1);
        vo.setUpdateUserId("admin");
        vo.setCreateUserId("admin");
        vo.setCfVo(setResultCfInfo());
        vo.setDiegoVo(setResultDiegoInfo());
        
        vo.getDeployStatus();
        vo.getIaasType();
        vo.getId();
        vo.getRecid();
        vo.getUpdateUserId();
        vo.getCreateUserId();
        vo.getCfVo();
        vo.getDiegoVo();
        
        list.add(vo);
        return list;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : DIEGO 상세 정보 조회 결과 값 설정 
    * @title : setResultDiegoInfo
    * @return : DiegoVO
    ***************************************************/
    public DiegoVO setResultDiegoInfo() {
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
        vo.setId(1);
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
        networkVo.setNet("internal");
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
        networkVo = new NetworkVO();
        networkVo.setAvailabilityZone("west-1");
        networkVo.setCloudSecurityGroups("seg");
        networkVo.setCreateUserId("admin");
        networkVo.setDeployType("DEIGO");
        networkVo.setId(1);
        networkVo.setNet("internal");
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
}
