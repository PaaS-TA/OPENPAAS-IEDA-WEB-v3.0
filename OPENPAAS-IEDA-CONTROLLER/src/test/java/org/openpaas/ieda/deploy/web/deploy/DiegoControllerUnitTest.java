package org.openpaas.ieda.deploy.web.deploy;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.security.Principal;
import java.util.ArrayList;
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
import org.openpaas.ieda.common.BaseControllerUnitTest;
import org.openpaas.ieda.controller.deploy.web.deploy.diego.DiegoController;
import org.openpaas.ieda.deploy.web.deploy.cf.dto.CfListDTO;
import org.openpaas.ieda.deploy.web.deploy.cf.service.CfService;
import org.openpaas.ieda.deploy.web.deploy.common.dao.network.NetworkVO;
import org.openpaas.ieda.deploy.web.deploy.common.dto.network.NetworkDTO;
import org.openpaas.ieda.deploy.web.deploy.common.dto.resource.ResourceDTO;
import org.openpaas.ieda.deploy.web.deploy.diego.dao.DiegoVO;
import org.openpaas.ieda.deploy.web.deploy.diego.dto.DiegoListDTO;
import org.openpaas.ieda.deploy.web.deploy.diego.dto.DiegoParamDTO;
import org.openpaas.ieda.deploy.web.deploy.diego.service.DiegoDeleteDeployAsyncService;
import org.openpaas.ieda.deploy.web.deploy.diego.service.DiegoDeployAsyncService;
import org.openpaas.ieda.deploy.web.deploy.diego.service.DiegoSaveService;
import org.openpaas.ieda.deploy.web.deploy.diego.service.DiegoService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class DiegoControllerUnitTest extends BaseControllerUnitTest{
    
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    private Principal principal = null;
    
    @InjectMocks DiegoController mockDiegoController;
    @Mock DiegoService mockDiegoService;
    @Mock DiegoSaveService mockDiegoSaveService;
    @Mock DiegoDeployAsyncService mockDiegoDeployAsyncService;
    @Mock DiegoDeleteDeployAsyncService mockDiegoDeleteDeployAsyncService;
    @Mock CfService mockCfService;
    
    
    final static String VIEW_URL = "/deploy/diego";
    final static String VIEW_POPUP_URL = "/deploy/diego/install/diegoPopup";
    final static String DIEGO_LIST_INFO_URL = "/deploy/diego/list/{iaasType}";
    final static String DIEGO_DETAIL_INFO_URL = "/deploy/diego/install/detail/{id}";
    final static String CF_LIST_INFO_URL = "/deploy/diego/list/cf/{iaas}";
    final static String DIEGO_SAVE_DEFAULT_INFO_URL = "/deploy/diego/install/saveDefaultInfo";
    final static String DIEGO_SAVE_NETWORK_INFO_URL = "/deploy/diego/install/saveNetworkInfo";
    final static String DEIGO_SAVE_RESOURCE_INFO_URL = "/deploy/diego/install/saveResourceInfo";
    final static String DIEGO_CREATE_SETTING_FILE_URL = "/deploy/diego/install/createSettingFile";
    final static String DIEGO_RECOURD_DELETE_URL = "/deploy/diego/delete/data";
    final static String DIEGO_JOB_LIST_INFO_URL = "/deploy/diego/install/save/job/list/{version}/{iaasType}";
    final static String DIEGO_SAVE_JOB_INFO_URL = "/deploy/diego/install/save/jobsInfo";
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockDiegoController).build();
        principal = getLoggined();
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : DIEGO 설치 화면 이동 TEST
    * @title : testGoDiego
    * @return : void
    ***************************************************/
    @Test
    public void testGoDiego() throws Exception{
        mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/deploy/deploy/diego/diego"));
    }
    
    
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : DIEGO 설치 화면 이동 TEST
    * @title : testGoDiego
    * @return : void
    ***************************************************/
    @Test
    public void testGoDiegoPopup() throws Exception{
        mockMvc.perform(get(VIEW_POPUP_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/deploy/deploy/diego/diegoPopup"));
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : DIEGO 목록 정보 조회
    * @title : testGetDiegoInfoList
    * @return : void
    ***************************************************/
    @Test
    public void testGetDiegoInfoList() throws Exception{
        List<DiegoListDTO> resultList = setDiegoInfoList();
        when(mockDiegoService.getDiegoInfoList(anyString())).thenReturn(resultList);
        mockMvc.perform(get(DIEGO_LIST_INFO_URL, "aws").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].recid").value(1))
        .andExpect(jsonPath("$.records[0].iaas").value("aws"))
        .andExpect(jsonPath("$.records[0].deploymentName").value("diego"))
        .andExpect(jsonPath("$.records[0].directorUuid").value("uuid"))
        .andExpect(jsonPath("$.records[0].diegoReleaseName").value("diego"))
        .andExpect(jsonPath("$.records[0].diegoReleaseVersion").value("1.25.1"))
        .andExpect(jsonPath("$.records[0].cfDeployment").value("cf.yml"))
        .andExpect(jsonPath("$.records[0].gardenReleaseName").value("garden"))
        .andExpect(jsonPath("$.records[0].gardenReleaseVersion").value("222"))
        .andExpect(jsonPath("$.records[0].etcdReleaseName").value("Etcd"))
        .andExpect(jsonPath("$.records[0].etcdReleaseVersion").value("104"))
        .andExpect(jsonPath("$.records[0].cflinuxfs2rootfsreleaseName").value("cflinux"))
        .andExpect(jsonPath("$.records[0].cflinuxfs2rootfsreleaseVersion").value("142"))
        .andExpect(jsonPath("$.records[0].keyFile").value("key.yml"))
        .andExpect(jsonPath("$.records[0].publicStaticIp").value("172.16.100.1"))
        .andExpect(jsonPath("$.records[0].subnetRange").value("192.168.1.0/24"))
        .andExpect(jsonPath("$.records[0].subnetGateway").value("192.168.1.1"))
        .andExpect(jsonPath("$.records[0].subnetDns").value("8.8.8.8"))
        .andExpect(jsonPath("$.records[0].subnetReservedIp").value("192.168.1.1"))
        .andExpect(jsonPath("$.records[0].subnetStaticIp").value("192.168.1.255"));
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 목록 정보 조회
    * @title : testGetCfList
    * @return : void
    ***************************************************/
    @Test
    public void testGetCfList() throws Exception{
        List<CfListDTO> resultList = setResultCfList();
        when(mockCfService.getCfLIst(anyString(), anyString())).thenReturn(resultList);
        mockMvc.perform(get(CF_LIST_INFO_URL, "aws").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].iaas").value("openstack"))
        .andExpect(jsonPath("$.records[0].deploymentName").value("cf"))
        .andExpect(jsonPath("$.records[0].releaseName").value("cf"))
        .andExpect(jsonPath("$.records[0].releaseVersion").value("222"))
        .andExpect(jsonPath("$.records[0].appSshFingerprint").value("fingerprint"))
        .andExpect(jsonPath("$.records[0].diegoYn").value("N"))
        .andExpect(jsonPath("$.records[0].deploymentFile").value("cf.yml"))
        .andExpect(jsonPath("$.records[0].deployStatus").value("deploying"))
        .andExpect(jsonPath("$.records[0].boshPassword").value("bosh"))
        .andExpect(jsonPath("$.records[0].stemcellName").value("stemcell"))
        .andExpect(jsonPath("$.records[0].stemcellVersion").value("3333"));
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : DIEGO 상세 정보 조회
    * @title : testGetDiegoDetailInfo
    * @return : void
    ***************************************************/
    @Test
    public void testGetDiegoDetailInfo() throws Exception{
        DiegoVO expectVO = setResultDiegoInfo();
        when(mockDiegoService.getDiegoDetailInfo(anyInt())).thenReturn(expectVO);
        mockMvc.perform(get(DIEGO_DETAIL_INFO_URL, 1).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.iaasType").value("aws"))
        .andExpect(jsonPath("$.content.createUserId").value("admin"))
        .andExpect(jsonPath("$.content.updateUserId").value("admin"))
        .andExpect(jsonPath("$.content.deploymentName").value("aws-diego"))
        .andExpect(jsonPath("$.content.directorUuid").value("uuid"))
        .andExpect(jsonPath("$.content.diegoReleaseName").value("diego"))
        .andExpect(jsonPath("$.content.diegoReleaseVersion").value("1.25.3"))
        .andExpect(jsonPath("$.content.cfReleaseName").value("cf"))
        .andExpect(jsonPath("$.content.cfReleaseVersion").value("272"))
        .andExpect(jsonPath("$.content.etcdReleaseVersion").value("104"))
        .andExpect(jsonPath("$.content.cflinuxfs2rootfsreleaseVersion").value("1.150.1"))
        .andExpect(jsonPath("$.content.paastaMonitoringUse").value("true"))
        .andExpect(jsonPath("$.content.cadvisorDriverPort").value("9033"))
        .andExpect(jsonPath("$.content.keyFile").value("key.yml"));
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : DIEGO 기본 정보 저장
    * @title : testSaveDefaultInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveDefaultInfo() throws Exception{
        DiegoVO vo = setResultDiegoInfo();
        when(mockDiegoSaveService.saveDefaultInfo(any(), any())).thenReturn(vo);
        DiegoParamDTO.Default dto = setDiegoDefaultInfo();
        mockMvc.perform(put(DIEGO_SAVE_DEFAULT_INFO_URL).contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.content.iaasType").value("aws"))
        .andExpect(jsonPath("$.content.createUserId").value("admin"))
        .andExpect(jsonPath("$.content.updateUserId").value("admin"))
        .andExpect(jsonPath("$.content.deploymentName").value("aws-diego"))
        .andExpect(jsonPath("$.content.directorUuid").value("uuid"))
        .andExpect(jsonPath("$.content.diegoReleaseName").value("diego"))
        .andExpect(jsonPath("$.content.diegoReleaseVersion").value("1.25.3"))
        .andExpect(jsonPath("$.content.cfId").value(1))
        .andExpect(jsonPath("$.content.cfName").value("cf-aws"))
        .andExpect(jsonPath("$.content.cfDeployment").value("cf.yml"))
        .andExpect(jsonPath("$.content.cfReleaseName").value("cf"))
        .andExpect(jsonPath("$.content.cfReleaseVersion").value("272"))
        .andExpect(jsonPath("$.content.gardenReleaseName").value("garden"))
        .andExpect(jsonPath("$.content.gardenReleaseVersion").value("153"))
        .andExpect(jsonPath("$.content.etcdReleaseName").value("etcd"))
        .andExpect(jsonPath("$.content.etcdReleaseVersion").value("104"));
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : DIEGO 네트워크 정보 저장
    * @title : testSaveNetworkInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveNetworkInfo() throws Exception{
        DiegoVO vo = setResultDiegoInfo();
        when(mockDiegoSaveService.saveNetworkInfo(any(), any())).thenReturn(vo);
        List<NetworkDTO> dto = resultNetworkListInfo();
        mockMvc.perform(put(DIEGO_SAVE_NETWORK_INFO_URL).contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$networks[0].id").value(1))
        .andExpect(jsonPath("$networks[0].createUserId").value("admin"))
        .andExpect(jsonPath("$networks[0].updateUserId").value("admin"))
        .andExpect(jsonPath("$networks[0].subnetStaticTo").value("192.168.0.255"))
        .andExpect(jsonPath("$networks[0].subnetDns").value("8.8.8.8"))
        .andExpect(jsonPath("$networks[0].availabilityZone").value("west-1"))
        .andExpect(jsonPath("$networks[0].networkName").value("netName"))
        .andExpect(jsonPath("$networks[0].cloudSecurityGroups").value("seg"))
        .andExpect(jsonPath("$networks[0].publicStaticIP").value("113.123.123.123"))
        .andExpect(jsonPath("$networks[0].deployType").value("DEIGO"))
        .andExpect(jsonPath("$networks[0].net").value("netId"))
        .andExpect(jsonPath("$networks[0].seq").value(0))
        .andExpect(jsonPath("$networks[0].subnetReservedFrom").value("192.168.0.1"))
        .andExpect(jsonPath("$networks[0].subnetReservedTo").value("192.168.0.155"));
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : DIEGO 리소스 정보 저장
    * @title : testSaveResourceInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveResourceInfo() throws Exception{
        Map<String, Object> expectMap = resultResourceMap();
        when(mockDiegoSaveService.saveResourceInfo(any(), any())).thenReturn(expectMap);
        ResourceDTO dto = setResourceInfo();
        mockMvc.perform(put(DEIGO_SAVE_RESOURCE_INFO_URL).contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsBytes(dto)))
        .andExpect(status().isCreated())
        .andDo(MockMvcResultHandlers.print())
        .andExpect(jsonPath("$deploymentFile").value("aws-diego"))
        .andExpect(jsonPath("$id").value(1));
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : DIEGO 배포 파일 생성
    * @title : testMakeDeploymentFile
    * @return : void
    ***************************************************/
    @Test
    public void testMakeDeploymentFile() throws Exception{
        DiegoParamDTO.Install dto = expectMakeFile();
        mockMvc.perform(post(DIEGO_CREATE_SETTING_FILE_URL).contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsBytes(dto)))
        .andExpect(status().isNoContent())
        .andDo(MockMvcResultHandlers.print());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : DIEGO 설치
    * @title : testDiegoInstall
    * @return : void
    ***************************************************/
    @Test
    public void testDiegoInstall(){
        DiegoParamDTO.Install dto = expectMakeFile();
        mockDiegoController.diegoInstall(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : DIEGO 단순 레코드 삭제
    * @title : testDeleteJustOnlyDiegoRecord
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteJustOnlyDiegoRecord() throws Exception{
        DiegoParamDTO.Delete dto = setDiegoDeleteInfo();
        mockMvc.perform(delete(DIEGO_RECOURD_DELETE_URL).contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsBytes(dto)))
        .andExpect(status().isOk())
        .andDo(MockMvcResultHandlers.print());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : DIEGO 삭제
    * @title : testDeleteDiego
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteDiego(){
        DiegoParamDTO.Delete dto = setDiegoDeleteInfo();
        mockDiegoController.deleteDiego(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : DIEGO 릴리즈 버전 및 인프라 별 JOb 목록 조회 
    * @title : testGetDiegoJobList
    * @return : void
    ***************************************************/
    @Test
    public void testGetDiegoJobList() throws Exception{
        List<HashMap<String, String>> expectMap = resultJobListInfo();
        when(mockDiegoService.getJobTemplateList(anyString(), anyString())).thenReturn(expectMap);
        mockMvc.perform(get(DIEGO_JOB_LIST_INFO_URL, "version", "aws").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(MockMvcResultHandlers.print())
        .andExpect(jsonPath("$[0].jobName").value("databases"))
        .andExpect(jsonPath("$[0].zone").value("z1"));
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : DIEGO 고급설정 정보 저장
    * @title : testSaveDiegoJobsInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveDiegoJobsInfo() throws Exception{
        List<HashMap<String, String>> expectMap = resultJobListInfo();
        when(mockDiegoService.getJobTemplateList(anyString(), anyString())).thenReturn(expectMap);
        mockMvc.perform(put(DIEGO_SAVE_JOB_INFO_URL).contentType(MediaType.APPLICATION_JSON)
         .content(mapper.writeValueAsString(expectMap)))
        .andExpect(status().isOk())
        .andDo(MockMvcResultHandlers.print());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Job 정보 설정
    * @title : resultJobListInfo
    * @return : List<HashMap<String, String>>
    ***************************************************/
    public List<HashMap<String, String>> resultJobListInfo() {
        List<HashMap<String, String>> mapList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("jobName", "databases");
        map.put("zone", "z1");
        mapList.add(map);
        return mapList;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : DIEGO 삭제 정보 설정
    * @title : setDiegoDeleteInfo
    * @return : DiegoParamDTO.Delete
    ***************************************************/
    public DiegoParamDTO.Delete setDiegoDeleteInfo() {
        DiegoParamDTO.Delete dto = new DiegoParamDTO.Delete();
        dto.setIaas("aws");
        dto.setId("1");
        dto.setPlatform("diego");
        List<String> list = new ArrayList<String>();
        String seq = "0";
        list.add(seq);
        dto.setSeq(list);
        return dto;
    }

    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 네트워크 저장 정보 설정
    * @title : expectMakeFile
    * @return : DiegoParamDTO.Install
    ***************************************************/
    public DiegoParamDTO.Install expectMakeFile() {
        DiegoParamDTO.Install dto = new DiegoParamDTO.Install();
        dto.setIaas("aws");
        dto.setId("1");
        dto.setPlatform("DIEGO");
        return dto;
    }

    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 네트워크 저장 정보 설정
    * @title : setResourceInfo
    * @return : ResourceDTO
    ***************************************************/
    public ResourceDTO setResourceInfo() {
        ResourceDTO dto = new ResourceDTO();
        dto.setBoshPassword("bosh");
        dto.setCfId("1");
        dto.setIaas("openstack");
        dto.setKeyFile("key.yml");
        dto.setPlatform("cf");
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
    * @description : 네트워크 저장 정보 설정
    * @title : resultResourceMap
    * @return : List<NetworkDTO>
    ***************************************************/
    public Map<String, Object> resultResourceMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("deploymentFile", "aws-diego");
        map.put("id", 1);
        return map;
    }

    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 네트워크 저장 정보 설정
    * @title : resultNetworkListInfo
    * @return : List<NetworkDTO>
    ***************************************************/
    public List<NetworkDTO> resultNetworkListInfo() {
        List<NetworkDTO> list = new ArrayList<NetworkDTO>();
        NetworkDTO dto = new NetworkDTO();
        dto.setAvailabilityZone("west-1");
        dto.setCloudSecurityGroups("seg");
        dto.setDeployType("DEIGO");
        dto.setId("1");
        dto.setNet("netId");
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
        return list;
    }

    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : DIEGO 기본 정보 저정 파라미터 값 설정
    * @title : setDiegoDefaultInfo
    * @return : DiegoParamDTO.Default
    ***************************************************/
    public DiegoParamDTO.Default setDiegoDefaultInfo() {
        DiegoParamDTO.Default dto = new DiegoParamDTO.Default();
        dto.setCadvisorDriverIp("10.0.0.6");
        dto.setCfDeploymentName("cf-aws");
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
        dto.setPaastaMonitoringUse("true");
        dto.setId("1");
        return dto;
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
    * @description : CF 목록 조회 결과 값 설정
    * @title : setResultCfList
    * @return : List<CfListDTO>
    ***************************************************/
    public List<CfListDTO> setResultCfList() {
        List<CfListDTO> list = new ArrayList<CfListDTO>();
        CfListDTO dto = new CfListDTO();
        dto.setBoshPassword("bosh");
        dto.setDeployStatus("deploying");
        dto.setDeploymentName("cf");
        dto.setDeploymentFile("cf.yml");
        dto.setDiegoYn("N");
        dto.setStemcellName("stemcell");
        dto.setStemcellVersion("3333");
        dto.setCfDiegoInstall("cf");
        dto.setAppSshFingerprint("fingerprint");
        dto.setIaas("openstack");
        dto.setReleaseName("cf");
        dto.setReleaseVersion("222");
        dto.setRecid(1);
        list.add(dto);
        return list;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Diego 목록 조회 값 설정
    * @title : setDiegoInfoList
    * @return : List<DiegoListDTO>
    ***************************************************/
    public List<DiegoListDTO> setDiegoInfoList() {
        List<DiegoListDTO> list = new ArrayList<DiegoListDTO>();
        DiegoListDTO dto =  new DiegoListDTO();
        dto.setAvailabilityZone("west-u1");
        dto.setBoshPassword("password");
        dto.setCfDeployment("cf.yml");
        dto.setCfId(1);
        dto.setCflinuxfs2rootfsreleaseName("cflinux");
        dto.setCflinuxfs2rootfsreleaseVersion("142");
        dto.setCloudSecurityGroups("seg");
        dto.setDeploymentFile("diego.yml");
        dto.setDeploymentName("diego");
        dto.setDeployStatus("deploy");
        dto.setDiegoReleaseName("diego");
        dto.setDiegoReleaseVersion("1.25.1");
        dto.setDirectorUuid("uuid");
        dto.setEtcdReleaseName("Etcd");
        dto.setEtcdReleaseVersion("104");
        dto.setGardenReleaseName("garden");
        dto.setGardenReleaseVersion("222");
        dto.setIaas("aws");
        dto.setId(1);
        dto.setKeyFile("key.yml");
        dto.setPublicStaticIp("172.16.100.1");
        dto.setRecid(1);
        dto.setStemcellName("aws-stemcell");
        dto.setStemcellVersion("3445.1");
        dto.setSubnetDns("8.8.8.8");
        dto.setSubnetId("sub1");
        dto.setSubnetGateway("192.168.1.1");
        dto.setSubnetRange("192.168.1.0/24");
        dto.setSubnetReservedIp("192.168.1.1");
        dto.setSubnetStaticIp("192.168.1.255");
        dto.getAvailabilityZone();
        dto.getBoshPassword();
        dto.getCfDeployment();
        dto.getCfId();
        dto.getCflinuxfs2rootfsreleaseName();
        dto.getCflinuxfs2rootfsreleaseVersion();
        dto.getCloudSecurityGroups();
        dto.getDeploymentFile();
        dto.getDeploymentName();
        dto.getDeployStatus();
        dto.getDiegoReleaseName();
        dto.getDiegoReleaseVersion();
        dto.getDirectorUuid();
        dto.getEtcdReleaseName();
        dto.getEtcdReleaseVersion();
        dto.getGardenReleaseName();
        dto.getGardenReleaseVersion();
        dto.getIaas();
        dto.getId();
        dto.getKeyFile();
        dto.getPublicStaticIp();
        dto.getRecid();
        dto.getStemcellName();
        dto.getStemcellVersion();
        dto.getSubnetDns();
        dto.getSubnetId();
        dto.getSubnetGateway();
        dto.getSubnetRange();
        dto.getSubnetReservedIp();
        dto.getSubnetStaticIp();
        list.add(dto);
        return list;
    }
}
