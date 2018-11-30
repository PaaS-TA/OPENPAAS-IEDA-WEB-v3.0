package org.openpaas.ieda.deploy.web.deploy;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
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

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.common.BaseControllerUnitTest;
import org.openpaas.ieda.controller.deploy.web.deploy.cf.CfController;
import org.openpaas.ieda.deploy.web.common.dto.KeyInfoDTO;
import org.openpaas.ieda.deploy.web.deploy.cf.dao.CfVO;
import org.openpaas.ieda.deploy.web.deploy.cf.dto.CfListDTO;
import org.openpaas.ieda.deploy.web.deploy.cf.dto.CfParamDTO;
import org.openpaas.ieda.deploy.web.deploy.cf.service.CfDeleteDeployAsyncService;
import org.openpaas.ieda.deploy.web.deploy.cf.service.CfDeployAsyncService;
import org.openpaas.ieda.deploy.web.deploy.cf.service.CfSaveService;
import org.openpaas.ieda.deploy.web.deploy.cf.service.CfService;
import org.openpaas.ieda.deploy.web.deploy.common.dao.network.NetworkVO;
import org.openpaas.ieda.deploy.web.deploy.common.dto.network.NetworkDTO;
import org.openpaas.ieda.deploy.web.deploy.common.dto.resource.ResourceDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class CfControllerUnitTest extends BaseControllerUnitTest{
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    private Principal principal = null;
    
    @InjectMocks CfController mockCfController;
    @Mock CfService mockCfService;
    @Mock CfSaveService mockCfSaveService;
    @Mock CfDeployAsyncService mockCfDeployAsyncService;
    @Mock CfDeleteDeployAsyncService mockCfDeleteDeployAsyncService;
    
    
    final static String VIEW_URL = "/deploy/cf";
    final static String VIEW_POPUP_URL = "/deploy/cf/install/cfPopup";
    final static String CF_LIST_URL = "/deploy/cf/list/openstack";
    final static String CF_DETAIL_INFO_URL = "/deploy/cf/install/detail/1";
    final static String CF_SAVE_DEFAULT_INFO_URL = "/deploy/cf/install/saveDefaultInfo";
    final static String CF_NETWORK_INFO_URL = "/deploy/cf/install/saveNetworkInfo/networks/list/1/deploy_cf";
    final static String CF_SAVE_NETWORK_INFO_URL = "/deploy/cf/install/saveNetworkInfo";
    final static String CF_SAVE_KEY_INFO_URL = "/deploy/cf/install/saveKeyInfo";
    final static String CF_SAVE_RESOURCE_INFO_URL = "/deploy/cf/install/saveResourceInfo";
    final static String CF_MAKE_MANIFEST_FILE_URL = "/deploy/cf/install/createSettingFile";
    final static String CF_DATA_DELETE_INFO_URL = "/deploy/cf/delete/data";
    final static String CF_JOB_LIST_URL="/deploy/cf/install/save/job/list/273/aws";
    final static String CF_JOB_INFO_SAVE_URL="/deploy/cf/install/save/jobsInfo";
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockCfController).build();
        principal = getLoggined();
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 설치 화면 이동 TEST
    * @title : testGoCf
    * @return : void
    ***************************************************/
    @Test
    public void testGoCf() throws Exception{
        mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/deploy/deploy/cf/cf"));
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 설치 팝업 화면 출력 TEST
    * @title : testGoCfPopup
    * @return : void
    ***************************************************/
    @Test
    public void testGoCfPopup() throws Exception{
        mockMvc.perform(get(VIEW_POPUP_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/deploy/deploy/cf/cfPopup"));
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 정보 목록 조회 TEST
    * @title : testGetCfList
    * @return : void
    ***************************************************/
    @Test
    public void testGetCfList() throws Exception{
        List<CfListDTO> expectList = setResultCfList();
        when(mockCfService.getCfLIst(anyString(), anyString())).thenReturn(expectList);
        mockMvc.perform(get(CF_LIST_URL, "openstack").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
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
    * @description : CF 정보 상세 조회 TEST
    * @title : testGetCfInfo
    * @return : void
    ***************************************************/
    @Test
    public void testGetCfInfo() throws Exception{
        CfVO expectVo = setResultCfInfo();
        when(mockCfService.getCfInfo(anyInt())).thenReturn(expectVo);
        mockMvc.perform(get(CF_DETAIL_INFO_URL, 1).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.iaasType").value("openstack"))
        .andExpect(jsonPath("$.content.deploymentName").value("cf"))
        .andExpect(jsonPath("$.content.releaseName").value("cf"))
        .andExpect(jsonPath("$.content.releaseVersion").value("222"))
        .andExpect(jsonPath("$.content.domain").value("test.domain"))
        .andExpect(jsonPath("$.content.paastaMonitoringUse").value("yes"))
        .andExpect(jsonPath("$.content.countryCode").value("kor"))
        .andExpect(jsonPath("$.content.stateName").value("seoul"))
        .andExpect(jsonPath("$.content.cfAdminPassword").value("admin"))
        .andExpect(jsonPath("$.content.inceptionOsUserName").value("ubuntu"));
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 기본 정보 저장 TEST
    * @title : testSaveDefaultInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveDefaultInfo() throws Exception{
        CfVO expectVo = setResultCfInfo();
        CfParamDTO.Default dto = setCfDefaultParamInfo();
        when(mockCfSaveService.saveDefaultInfo(any(), any())).thenReturn(expectVo);
        mockMvc.perform(put(CF_SAVE_DEFAULT_INFO_URL).contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsBytes(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.iaasType").value("openstack"))
        .andExpect(jsonPath("$.content.deploymentName").value("cf"))
        .andExpect(jsonPath("$.content.releaseName").value("cf"))
        .andExpect(jsonPath("$.content.releaseVersion").value("222"))
        .andExpect(jsonPath("$.content.domain").value("test.domain"))
        .andExpect(jsonPath("$.content.paastaMonitoringUse").value("yes"))
        .andExpect(jsonPath("$.content.cfAdminPassword").value("admin"))
        .andExpect(jsonPath("$.content.inceptionOsUserName").value("ubuntu"))
        .andExpect(jsonPath("$.content.countryCode").value("kor"))
        .andExpect(jsonPath("$.content.stateName").value("seoul"));
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 네트워크 정보 저장 TEST
    * @title : testSaveNetworkCfInfo
    * @return : void
    ***************************************************/
    public void testGetNetowrkListInfo() throws Exception{
        List<NetworkVO> expectNetworkList = setResultNetworkInfo();
        when(mockCfService.getNetowrkListInfo(anyInt(), anyString())).thenReturn(expectNetworkList);
        mockMvc.perform(get(CF_NETWORK_INFO_URL, 1, "cf").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[0].createUserId").value("admin"))
        .andExpect(jsonPath("$.[0].publicStaticIP").value("1.1.1.1"))
        .andExpect(jsonPath("$.[0].deployType").value("cf"))
        .andExpect(jsonPath("$.[0].net").value("network"))
        .andExpect(jsonPath("$.[0].seq").value(1))
        .andExpect(jsonPath("$.[0].subnetStaticTo").value("255"))
        .andExpect(jsonPath("$.[0].subnetReservedFrom").value("1"))
        .andExpect(jsonPath("$.[0].subnetRange").value("/24"))
        .andExpect(jsonPath("$.[0].subnetGateway").value("1"))
        .andExpect(jsonPath("$.[0].updateUserId").value("admin"));
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 네트워크 정보 저장
    * @title : testSaveNetworkCfInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveNetworkCfInfo() throws Exception{
        List<NetworkDTO> dto = cfNetworkInfo();
        mockMvc.perform(put(CF_SAVE_NETWORK_INFO_URL).contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsBytes(dto)))
        .andExpect(status().isOk());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF KEY 정보 저장
    * @title : testSaveKeyInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveKeyInfo() throws Exception{
        KeyInfoDTO dto = cfKeyInfo();
        mockMvc.perform(put(CF_SAVE_KEY_INFO_URL).contentType(MediaType.APPLICATION_JSON)
         .content(mapper.writeValueAsBytes(dto)))
         .andExpect(status().isOk());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 리소스 정보 저장
    * @title : testSaveResourceCfInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveResourceCfInfo() throws Exception{
        ResourceDTO dto = cfResourceInfo();
        HashMap<String, Object> expectMap = setResultResourceInfo();
        when(mockCfSaveService.saveResourceInfo(any(), any())).thenReturn(expectMap);
        mockMvc.perform(put(CF_SAVE_RESOURCE_INFO_URL).contentType(MediaType.APPLICATION_JSON)
         .content(mapper.writeValueAsBytes(dto)))
         .andExpect(status().isOk());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 배포 파일 생성
    * @title : makeDeploymentFile
    * @return : void
    ***************************************************/
    public void testMakeDeploymentFile() throws JsonProcessingException, Exception{
        CfParamDTO.Install dto = setCfIntallParam();
        mockMvc.perform(post(CF_MAKE_MANIFEST_FILE_URL).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto)))
                .andExpect(status().isNoContent());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 플랫폼 설치
    * @title : testInstallCf
    * @return : void
    ***************************************************/
    @Test
    public void testInstallCf() throws Exception{
        CfParamDTO.Install dto = setCfIntallParam();
        mockCfController.installCf(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 플랫폼 삭제 TEST
    * @title : testDeleteCf
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteCf(){
        CfParamDTO.Delete dto = setCfDeleteInfo();
        mockCfController.deleteCf(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 플랫폼 단순 레코드 삭제 TEST
    * @title : testDeleteJustOnlyCfRecord
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteJustOnlyCfRecord() throws Exception{
        CfParamDTO.Delete dto = setCfDeleteInfo();
        mockMvc.perform(delete(CF_DATA_DELETE_INFO_URL).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto)))
                .andExpect(status().isOk());
    }

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 릴리즈 버전 및 인프라 별 JOb 목록 조회
     * @title : testGetCfJobList
     * @return : void
    ***************************************************/
    @Test
    public void testGetCfJobList() throws Exception{
        when(mockCfService.getJobTemplateList(anyString(), anyString())).thenReturn(setJobTemplateList());
        mockMvc.perform(get(CF_JOB_LIST_URL, "273", "aws").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 고급설정 정보 저장
     * @title : testSaveCfJobsInfo
     * @return : void
    ***************************************************/
    @Test
    public void testSaveCfJobsInfo() throws Exception{
        List<HashMap<String, String>> list = setSaveCfJobsInfo();
        doNothing().when(mockCfSaveService).saveCfJobsInfo(any(), any());
        mockMvc.perform(put(CF_JOB_INFO_SAVE_URL).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(list)))
                .andExpect(status().isOk());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : cf jobs 저장 정보 설정
     * @title : setSaveCfJobsInfo
     * @return : List<HashMap<String,String>>
    ***************************************************/
    public List<HashMap<String, String>> setSaveCfJobsInfo(){
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>> ();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("id", null);
        map.put("seq", "1");
        map.put("deploy_type", "DEPLOY_TYPE_CF");
        map.put("job_name", "consul");
        map.put("instances", "1");
        map.put("zone", "z1");
        list.add(map);
        return list;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 릴리즈  버전 및 인프라 별 JOb 목록 조회 설정
     * @title : setJobTemplateList
     * @return : List<HashMap<String,String>>
    ***************************************************/
    public List<HashMap<String, String>> setJobTemplateList(){
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>> ();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("id", "1");
        map.put("seq", "1");
        map.put("deploy_type", "DEPLOY_TYPE_CF");
        map.put("job_name", "api");
        map.put("zone_z1", "true");
        map.put("zone_z2", "true");
        map.put("zone_z3", "false");
        list.add(map);
        return list;
        
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 플랫폼 단순 레코드 삭제 파라미터 값 설정
    * @title : setCfDeleteInfo
    * @return : CfParamDTO.Delete
    ***************************************************/
    public CfParamDTO.Delete setCfDeleteInfo() {
        CfParamDTO.Delete dto = new CfParamDTO.Delete();
        dto.setIaas("openstack");
        dto.setId("1");
        dto.setPlatform("platform");
        return null;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 설치 관련 파리미터 값 설정
    * @title : setCfIntallParam
    * @return : CfParamDTO.Install
    ***************************************************/
    public CfParamDTO.Install setCfIntallParam() {
        CfParamDTO.Install dto = new CfParamDTO.Install();
        dto.setIaas("openstack");
        dto.setId("1");
        dto.setPlatform("cf");
        return dto;
    }
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 리소스 정보 저장 후 기대 값 설정
    * @title : setResultResourceInfo
    * @return : Map<String, Object>
    ***************************************************/
    public HashMap<String, Object> setResultResourceInfo() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("deploymentFile", "cf.yml");
        map.put("id", 1);
        return map;
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
        dto.setSmallCpu("1");
        dto.setSmallDisk("123");
        dto.setSmallRam("8192");
        dto.setStemcellName("os");
        dto.setStemcellVersion("3127");
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
    * @description : CF 네트워크 정보 저장 파라미터 값 설정
    * @title : cfNetworkInfo
    * @return : List<NetworkDTO>
    ***************************************************/
    public List<NetworkDTO> cfNetworkInfo() {
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
        return list;
    }

    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 네트워크 정보 결과 값 설정
    * @title : setResultNetworkInfo
    * @return : void
    ***************************************************/
    public List<NetworkVO> setResultNetworkInfo() {
        List<NetworkVO> list = new ArrayList<NetworkVO>();
        NetworkVO vo = new NetworkVO();
        vo.setId(1);
        vo.setCreateUserId("admin");
        vo.setUpdateUserId("admin");
        vo.setDeployType("cf");
        vo.setNet("network");
        vo.setSeq(1);
        vo.setSubnetStaticFrom("1");
        vo.setSubnetStaticTo("255");
        vo.setSubnetReservedFrom("1");
        vo.setSubnetReservedTo("255");
        vo.setSubnetDns("8.8.8.8");
        vo.setSubnetRange("/24");
        vo.setSubnetGateway("1");
        vo.setNetworkName("network");
        vo.setCloudSecurityGroups("seg");
        vo.setAvailabilityZone("us-west-1");
        vo.setSubnetId("124d-aaa");
        list.add(vo);
        return list;
    }

    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 상세 조회 결과 값 설정
    * @title : setCfDefaultParamInfo
    * @return : CfVO
    ***************************************************/
    public CfParamDTO.Default setCfDefaultParamInfo() {
        CfParamDTO.Default dto = new CfParamDTO.Default();
        dto.setDeploymentName("cf");
        dto.setDirectorUuid("uuid");
        dto.setDomain("domain");
        dto.setIaas("openstack");
        dto.setId("1");
        dto.setDomainOrganization("paas-ta");
        dto.setPaastaMonitoringUse("yes");
        dto.setReleaseName("cf");
        dto.setReleaseVersion("222");
        dto.setCfAdminPassword("admin");
        dto.setInceptionOsUserName("ubuntu");
        return dto;
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
        vo.setReleaseName("cf");
        vo.setReleaseVersion("222");
        vo.setDeploymentName("cf");
        vo.setDeploymentFile("cf-yml");
        vo.setDomain("test.domain");
        vo.setPaastaMonitoringUse("yes");
        vo.setCountryCode("kor");
        vo.setStateName("seoul");
        vo.setLocalityName("mapo");
        vo.setUnitName("paas-ta");
        vo.setEmail("test@paasta.co.kr");
        vo.setKeyFile("keyFile");
        vo.setOrganizationName("paasta");
        vo.setDeployStatus("deploying");
        vo.setCfAdminPassword("admin");
        vo.setInceptionOsUserName("ubuntu");
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
}
