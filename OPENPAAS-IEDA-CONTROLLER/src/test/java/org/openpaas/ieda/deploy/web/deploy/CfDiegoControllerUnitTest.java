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
import org.openpaas.ieda.controller.deploy.web.deploy.cfDiego.CfDiegoController;
import org.openpaas.ieda.deploy.web.common.dto.KeyInfoDTO;
import org.openpaas.ieda.deploy.web.deploy.cf.dao.CfVO;
import org.openpaas.ieda.deploy.web.deploy.cf.dto.CfListDTO;
import org.openpaas.ieda.deploy.web.deploy.cf.service.CfDeployAsyncService;
import org.openpaas.ieda.deploy.web.deploy.cf.service.CfSaveService;
import org.openpaas.ieda.deploy.web.deploy.cf.service.CfService;
import org.openpaas.ieda.deploy.web.deploy.cfDiego.dao.CfDiegoVO;
import org.openpaas.ieda.deploy.web.deploy.cfDiego.dto.CfDiegoParamDTO;
import org.openpaas.ieda.deploy.web.deploy.cfDiego.service.CfDiegoSaveService;
import org.openpaas.ieda.deploy.web.deploy.cfDiego.service.CfDiegoService;
import org.openpaas.ieda.deploy.web.deploy.common.dao.network.NetworkVO;
import org.openpaas.ieda.deploy.web.deploy.common.dto.network.NetworkDTO;
import org.openpaas.ieda.deploy.web.deploy.common.dto.resource.ResourceDTO;
import org.openpaas.ieda.deploy.web.deploy.diego.dao.DiegoVO;
import org.openpaas.ieda.deploy.web.deploy.diego.service.DiegoDeployAsyncService;
import org.openpaas.ieda.deploy.web.deploy.diego.service.DiegoSaveService;
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
public class CfDiegoControllerUnitTest extends BaseControllerUnitTest{
    
    @InjectMocks CfDiegoController mockCfDiegoController;
    @Mock CfDiegoService mockCfDiegoService;
    @Mock CfDiegoSaveService mockCfDiegoSaveService;
    @Mock CfSaveService mockCfSaveService;
    @Mock DiegoSaveService mockDiegoSaveService;
    @Mock CfDeployAsyncService mockCfDeployAsyncService;
    @Mock DiegoDeployAsyncService mockDiegoDeployAsyncService;
    @Mock CfService mockCfService;
    
    
    final static String CF_DIEGO_VIEW_URL = "/deploy/cfDiego";
    final static String CF_VIEW_POPUP_URL = "/deploy/cfDiego/install/cfPopup";
    final static String DIEGO_VIEW_POPUP_URL = "/deploy/cfDiego/install/diegoPopup";
    final static String CF_DIEGO_INFO_LIST_URL = "/deploy/cfDiego/list/{iaas}";
    final static String CF_DIEGO_DETAIL_INFO_URL = "/deploy/cfDiego/install/detail/{id}";
    final static String CF_INFO_LIST_URL = "/deploy/cfDiego/list/cf/{iaas}";
    final static String CF_DIEGO_SAVE_DEFAULT_INFO_URL = "/deploy/cfDiego/install/saveDefaultInfo";
    final static String CF_DIEGO_SAVE_KEY_INFO_URL = "/deploy/cfDiego/install/saveKeyInfo";
    final static String CF_DIEGO_SAVE_NETWORK_INFO_URL = "/deploy/cfDiego/install/saveNetworkInfo";
    final static String CF_DIEGO_SAVE_RESOURCE_INFO_URL = "/deploy/cfDiego/install/saveResourceInfo";
    final static String CF_DIEGO_SAVE_JOB_INFO_URL = "/deploy/cfDiego/install/save/jobsInfo";
    final static String CF_DIEGO_CREATE_MANIFEST_URL = "/deploy/cfDiego/install/createSettingFile";
    final static String CF_DIEGO_DATA_DELETE_URL = "/deploy/cfDiego/delete/data";
    
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
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
        mockMvc = MockMvcBuilders.standaloneSetup(mockCfDiegoController).build();
        principal = getLoggined();
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF & Diego 통합 설치 화면 요청 TEST
    * @title : testGoDiego
    * @return : void
    ***************************************************/
    @Test
    public void testGoCfDiego() throws Exception{
        mockMvc.perform(get(CF_DIEGO_VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/deploy/deploy/cfDiego"));
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 설치 팝업 화면 이동 TEST
    * @title : testGoDiego
    * @return : void
    ***************************************************/
    @Test
    public void testGoCfPopup() throws Exception{
        mockMvc.perform(get(CF_VIEW_POPUP_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/deploy/deploy/cf/cfPopup"));
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : DIEGO 설치 팝업 화면 이동 TEST
    * @title : testGoDiego
    * @return : void
    ***************************************************/
    @Test
    public void testGoDiegoPopup() throws Exception{
        mockMvc.perform(get(DIEGO_VIEW_POPUP_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/deploy/deploy/diego/diegoPopup"));
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF & Dieg 목록 조회
    * @title : testGetCfDiegoLIst
    * @return : void
    ***************************************************/
    @Test
    public void testGetCfDiegoList() throws Exception{
        List<CfDiegoVO> expectList = setCfDiegoInfoList();
        when(mockCfDiegoService.getCfDiegoList(anyString())).thenReturn(expectList);
        mockMvc.perform(get(CF_DIEGO_INFO_LIST_URL, "aws").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].id").value(1))
        .andExpect(jsonPath("$.records[0].recid").value(1))
        .andExpect(jsonPath("$.records[0].cfVo.id").value(1))
        .andExpect(jsonPath("$.records[0].cfVo.iaasType").value("openstack"))
        .andExpect(jsonPath("$.records[0].cfVo.diegoYn").value("N"))
        .andExpect(jsonPath("$.records[0].cfVo.deploymentName").value("cf"))
        .andExpect(jsonPath("$.records[0].cfVo.releaseName").value("cf"))
        .andExpect(jsonPath("$.records[0].cfVo.releaseVersion").value("222"))
        .andExpect(jsonPath("$.records[0].cfVo.appSshFingerprint").value("fingerprint"))
        .andExpect(jsonPath("$.records[0].diegoVo.diegoReleaseName").value("diego"))
        .andExpect(jsonPath("$.records[0].diegoVo.diegoReleaseVersion").value("1.25.3"))
        .andExpect(jsonPath("$.records[0].diegoVo.cfId").value(1))
        .andExpect(jsonPath("$.records[0].diegoVo.cfName").value("cf-aws"))
        .andExpect(jsonPath("$.records[0].diegoVo.cfDeployment").value("cf.yml"))
        .andExpect(jsonPath("$.records[0].diegoVo.cfReleaseName").value("cf"))
        .andExpect(jsonPath("$.records[0].diegoVo.cfReleaseVersion").value("272"));
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF & Diego 정보 상세 조회
    * @title : testGetCfDiegoInfo
    * @return : void
    ***************************************************/
    @Test
    public void testGetCfDiegoInfo() throws Exception{
        CfDiegoVO expectVo = expectCfDiegoVO();
        when(mockCfDiegoService.getCfDiegoInfo(anyInt())).thenReturn(expectVo);
        mockMvc.perform(get(CF_DIEGO_DETAIL_INFO_URL, 1).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.id").value(1))
        .andExpect(jsonPath("$.content.recid").value(1))
        .andExpect(jsonPath("$.content.cfVo.iaasType").value("openstack"))
        .andExpect(jsonPath("$.content.cfVo.diegoYn").value("N"))
        .andExpect(jsonPath("$.content.cfVo.deploymentName").value("cf"))
        .andExpect(jsonPath("$.content.cfVo.releaseName").value("cf"))
        .andExpect(jsonPath("$.content.cfVo.releaseVersion").value("222"))
        .andExpect(jsonPath("$.content.cfVo.paastaMonitoringUse").value("yes"))
        .andExpect(jsonPath("$.content.cfVo.domain").value("test.domain"))
        .andExpect(jsonPath("$.content.cfVo.iaasType").value("openstack"));
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : IaaS에 따른 CF 정보 목록 조회 
    * @title : testGetCfDeploymentList
    * @return : void
    ***************************************************/
    @Test
    public void testGetCfDeploymentList() throws Exception{
        List<CfListDTO> setCfList = setResultCfList();
        when(mockCfService.getCfLIst(anyString(), anyString())).thenReturn(setCfList);
        mockMvc.perform(get(CF_INFO_LIST_URL, "aws").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andDo(MockMvcResultHandlers.print())
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
    * @description : 기본 정보 저장
    * @title : testSaveDefaultInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveDefaultInfo() throws Exception{
        CfDiegoVO resultSaveDefaultInfo = expectCfDiegoVO();
        CfDiegoParamDTO.Default dto = setCfDiegoParamInfo();
        when(mockCfDiegoSaveService.saveDefaultInfo(any(), any())).thenReturn(resultSaveDefaultInfo);
        mockMvc.perform(put(CF_DIEGO_SAVE_DEFAULT_INFO_URL).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.id").value(1))
        .andExpect(jsonPath("$.content.recid").value(1))
        .andExpect(jsonPath("$.content.cfVo.iaasType").value("openstack"))
        .andExpect(jsonPath("$.content.cfVo.diegoYn").value("N"))
        .andExpect(jsonPath("$.content.cfVo.deploymentName").value("cf"))
        .andExpect(jsonPath("$.content.cfVo.releaseName").value("cf"))
        .andExpect(jsonPath("$.content.cfVo.releaseVersion").value("222"))
        .andExpect(jsonPath("$.content.cfVo.paastaMonitoringUse").value("yes"))
        .andExpect(jsonPath("$.content.cfVo.domain").value("test.domain"))
        .andExpect(jsonPath("$.content.cfVo.iaasType").value("openstack"));
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description :  키 생성 정보 저장
    * @title : testSaveKeyInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveKeyInfo() throws Exception{
        KeyInfoDTO dto = cfKeyInfo();
        mockMvc.perform(put(CF_DIEGO_SAVE_KEY_INFO_URL).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsBytes(dto)))
        .andExpect(status().isNoContent())
        .andDo(MockMvcResultHandlers.print());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 네트워크 정보 저장 
    * @title : testSaveNetworkCfInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveNetworkCfInfo() throws Exception{
        List<NetworkDTO> networkListInfo = cfNetworkInfo();
        mockMvc.perform(put(CF_DIEGO_SAVE_NETWORK_INFO_URL).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsBytes(networkListInfo)))
        .andExpect(status().isNoContent())
        .andDo(MockMvcResultHandlers.print());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 리소스 정보 저장 
    * @title : testSaveResourceInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveResourceInfo() throws  Exception{
        Map<String, Object> expectMap = setExpectResourceInfo();
        when(mockCfDiegoSaveService.saveResourceInfo(any(), any())).thenReturn(expectMap);
        ResourceDTO setResourceInfo = cfDiegoResourceInfo();
        mockMvc.perform(put(CF_DIEGO_SAVE_RESOURCE_INFO_URL).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsBytes(setResourceInfo)))
        .andExpect(status().isOk())
        .andDo(MockMvcResultHandlers.print())
        .andExpect(jsonPath("$.deploymentFile").value("cf.yml"))
        .andExpect(jsonPath("$.id").value(1));
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 고급 설정 정보 저장
    * @title : testSaveCfJobsInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveCfJobsInfo() throws Exception{
        List<HashMap<String, String>> list = setSaveCfJobsInfo();
        mockMvc.perform(put(CF_DIEGO_SAVE_JOB_INFO_URL).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsBytes(list)))
        .andExpect(status().isOk())
        .andDo(MockMvcResultHandlers.print());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 배포 파일 생성배포 파일 생성
    * @title : testMakeDeploymentFile
    * @return : void
    ***************************************************/
    @Test
    public void testMakeDeploymentFile() throws  Exception{
        CfDiegoParamDTO.Install dto = setCfDiegoInstallDto();
        mockMvc.perform(post(CF_DIEGO_CREATE_MANIFEST_URL).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsBytes(dto)))
        .andExpect(status().isNoContent());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 설치
    * @title : testInstallCf
    * @return : void
    ***************************************************/
    @Test
    public void testInstallCf(){
        CfDiegoParamDTO.Install dto = setCfDiegoInstallDto();
        mockCfDiegoController.installCf(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Diego 설치
    * @title : testInstallDiego
    * @return : void
    ***************************************************/
    @Test
    public void testInstallDiego(){
        CfDiegoParamDTO.Install dto = setCfDiegoInstallDto();
        mockCfDiegoController.installDiego(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Diego 단순 데이터 삭제
    * @title : testDeleteJustOnlyCfDiegoRecord
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteJustOnlyCfDiegoRecord() throws Exception{
        CfDiegoParamDTO.Delete dto = setCfDiegoDeleteDto();
        mockMvc.perform(delete(CF_DIEGO_DATA_DELETE_URL).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsBytes(dto)))
        .andExpect(status().isOk());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF & Diego 플랫폼 삭제 
    * @title : testDeleteCfDiego
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteCfDiego(){
        CfDiegoParamDTO.Delete dto = setCfDiegoDeleteDto();
        mockCfDiegoController.deleteCfDiego(dto, principal);
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
    * @description : CF & Diego 삭제 값 설정
    * @title : setCfDiegoDeleteDto
    * @return : CfDiegoParamDTO.Delete
    ***************************************************/
    public CfDiegoParamDTO.Delete setCfDiegoDeleteDto() {
        CfDiegoParamDTO.Delete dto = new CfDiegoParamDTO.Delete();
        dto.setIaas("aws");
        dto.setId("1");
        dto.setPlatform("cfDiego");
        return dto;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF & Diego 리소스 정보 저장 후 기대 값 설정
    * @title : setCfDiegoInstallDto
    * @return : CfDiegoParamDTO.Install
    ***************************************************/
    public CfDiegoParamDTO.Install setCfDiegoInstallDto() {
        CfDiegoParamDTO.Install dto = new CfDiegoParamDTO.Install();
        dto.setIaas("aws");
        dto.setId("1");
        dto.setPlatform("cfDiego");
        return dto;
    }

    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF & Diego 리소스 정보 저장 후 기대 값 설정
    * @title : setResultResourceInfo
    * @return : Map<String, Object>
    ***************************************************/
    public Map<String, Object> setExpectResourceInfo() {
        Map<String, Object> map = new HashMap<String, Object>();
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
    public ResourceDTO cfDiegoResourceInfo() {
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
    * @description : CF & DIEGO 키 정보 저장 파라미터 값 설정
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
    * @description : CF 기본 정보 값 설정
    * @title : setCfDefaultParamInfo
    * @return : CfDiegoParamDTO.Default
    ***************************************************/
    public CfDiegoParamDTO.Default setCfDiegoParamInfo() {
        CfDiegoParamDTO.Default dto = new CfDiegoParamDTO.Default();
        dto.setAppSshFingerprint("fingerPrint");
        dto.setDeaDiskMB("32718");
        dto.setDeaMemoryMB("8192");
        dto.setDeploymentName("cf");
        dto.setPlatform("cfDiego");
        dto.setDescription("cf");
        dto.setDiegoYn("N");
        dto.setDirectorUuid("uuid");
        dto.setDomain("domain");
        dto.setIaas("openstack");
        dto.setId("1");
        dto.setIngestorIp("172.16.100.1");
        dto.setLoginSecret("login");
        dto.setDomainOrganization("paas-ta");
        dto.setPaastaMonitoringUse("yes");
        dto.setReleaseName("cf");
        dto.setReleaseVersion("222");
        return dto;
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
}
