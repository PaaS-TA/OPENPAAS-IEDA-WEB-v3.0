package org.openpaas.ieda.deploy.web.information;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.Application;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.common.BaseControllerUnitTest;
import org.openpaas.ieda.controller.deploy.web.information.iaasConfig.IaasConfigMgntController;
import org.openpaas.ieda.deploy.web.information.iassConfig.dao.IaasConfigMgntVO;
import org.openpaas.ieda.deploy.web.information.iassConfig.dto.IaasConfigMgntDTO;
import org.openpaas.ieda.deploy.web.information.iassConfig.service.IaasConfigMgntService;
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
public class IaasConfigMgntControllerUnitTest extends BaseControllerUnitTest {
    @InjectMocks IaasConfigMgntController mockIaasConfigInfoController;
    @Mock IaasConfigMgntService mockIaasConfigInfoService;
     
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
     
     
    /*************************************** URL *******************************************/
    final static String VIEW_URL = "/info/iaasConfig";
    final static String AWS_IAAS_CONFIG_VIEW_URL = "/info/iaasConfig/aws";
    final static String OPENSTACK_IAAS_CONFIG_VIEW_URL= "/info/iaasConfig/openstack";
    final static String GOOGLE_IAAS_CONFIG_VIEW_URL = "/info/iaasConfig/google";
    final static String VSPHERE_IAAS_CONFIG_VIEW_URL = "/info/iaasConfig/vSphere";
    final static String ALL_IAAS_CONFIG_LIST_URL = "/info/iaasConfig/all/list";
    final static String ALL_IAAS_CONFIG_CNT_URL = "/info/iaasConfig/all/cnt";
    final static String AWS_IAAS_CONFIG_LIST_URL = "/info/iaasConfig/{iaasType}/list";
    final static String IAAS_CONIFG_SAVE_URL =  "/info/iaasConfig/{iaasType}/save";
    final static String IAAS_CONIFG_DETAIL_INFO_URL = "/info/iaasConfig/{iaasType}/save/detail/{id}";
    final static String IAAS_CONFIG_DELETE_URL = "/info/iaasConfig/{iaasType}/delete";
    /*************************************** URL *******************************************/
     
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockIaasConfigInfoController).build();
        getLoggined();
    }
     
     
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 환경 설정관리 화면 이동 테스트
    * @title : testGoIaasConfigInfo
    * @return : void
    ***************************************************/
    @Test
    public void testGoIaasConfigInfo() throws Exception{
        mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/deploy/information/iaasConfig/iaasConfigMgnt"));
    }
     
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : AWS 환경 설정 관리 화면 이동 테스트
    * @title : testGoAwsConfigInfo
    * @return : void
    ***************************************************/
    @Test
    public void testGoAwsConfigInfo() throws Exception{
        mockMvc.perform(get(AWS_IAAS_CONFIG_VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/deploy/information/iaasConfig/awsConfigMgnt"));
    }
     
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : OPENSTACK 환경 설정 관리 화면 이동 테스트
    * @title : testGoOpenstackConfigInfo
    * @return : void
    ***************************************************/
    @Test
    public void testGoOpenstackConfigInfo() throws Exception{
        mockMvc.perform(get(OPENSTACK_IAAS_CONFIG_VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/deploy/information/iaasConfig/openstackConfigMgnt"));
    }
     
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description :  GOOGLE 환경 설정 관리 화면 이동 테스트
    * @title : testGoGoogleConfigInfo
    * @return : void
    ***************************************************/
    @Test
    public void testGoGoogleConfigInfo() throws Exception{
        mockMvc.perform(get(GOOGLE_IAAS_CONFIG_VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/deploy/information/iaasConfig/googleConfigMgnt"));
    }
     
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : VSPHERE 환경 설정 관리 화면 이동 테스트
    * @title : testGoVsphereConfigInfo
    * @return : void
    ***************************************************/
    @Test
    public void testGoVsphereConfigInfo() throws Exception{
        mockMvc.perform(get(VSPHERE_IAAS_CONFIG_VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/deploy/information/iaasConfig/vSphereConfigMgnt"));
    }
     
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 전체 환경 설정 목록 정보 요청 테스트
    * @title : testGetAllIaasConfigInfoList
    * @return : void
    ***************************************************/
    @Test
    public void testGetAllIaasConfigInfoList() throws Exception{
        List<IaasConfigMgntVO> expectList =  setAllIaasConfigInfoList();
        when(mockIaasConfigInfoService.getAllIaasConfigInfoList(any())).thenReturn(expectList);
        mockMvc.perform(get(ALL_IAAS_CONFIG_LIST_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].accountId").value(1))
        .andExpect(jsonPath("$.records[0].accountName").value("test"))
        .andExpect(jsonPath("$.records[0].deployStatus").value("사용중"))
        .andExpect(jsonPath("$.records[0].iaasType").value("VSPHERE"))
        .andExpect(jsonPath("$.records[0].iaasConfigAlias").value("test"))
        .andExpect(jsonPath("$.records[0].commonSecurityGroup").value("test"))
        .andExpect(jsonPath("$.records[0].vsphereVcentDataCenterName").value("test"))
        .andExpect(jsonPath("$.records[0].vsphereVcenterVmFolder").value("test"));
    }
     
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 인프라 별 환경 설정 개수 조회 테스트
    * @title : testGetAllIaasConfigCountInfo
    * @return : void
    ***************************************************/
    @Test
    public void testGetAllIaasConfigCountInfo() throws Exception{
        HashMap<String, Integer> expectMap = setIaasConfigCount(); 
        when(mockIaasConfigInfoService.getIaasConfigCount(any())).thenReturn(expectMap);
        mockMvc.perform(get(ALL_IAAS_CONFIG_CNT_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.google").value(1))
        .andExpect(jsonPath("$.vsphere").value(3))
        .andExpect(jsonPath("$.openstack").value(4))
        .andExpect(jsonPath("$.aws").value(2));
         
    }
     
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Aws 환경 설정 목록 조회 테스트
    * @title : testGetAwsConfigInfoList
    * @return : void
    ***************************************************/
    @Test
    public void testGetAwsConfigInfoList() throws Exception{
        List<IaasConfigMgntVO> expectList =  setAllIaasConfigInfoList();
        when(mockIaasConfigInfoService.getIaasConfigInfoList(anyString(),any())).thenReturn(expectList);
        mockMvc.perform(get(AWS_IAAS_CONFIG_LIST_URL,"AWS").
         contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].accountId").value(1))
        .andExpect(jsonPath("$.records[0].accountName").value("test"))
        .andExpect(jsonPath("$.records[0].deployStatus").value("사용중"))
        .andExpect(jsonPath("$.records[0].iaasConfigAlias").value("test"))
        .andExpect(jsonPath("$.records[0].commonSecurityGroup").value("test"))
        .andExpect(jsonPath("$.records[0].vsphereVcentDataCenterName").value("test"))
        .andExpect(jsonPath("$.records[0].vsphereVcenterVmFolder").value("test"));
    }
     
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 환경 설정 정보 상세 조회 테스트
    * @title : testGetIaasConfigInfo
    * @return : void
    ***************************************************/
    @Test
    public void testGetIaasConfigInfo() throws Exception{
        IaasConfigMgntVO expect = setUpdateIaasConfigInfo();
        when(mockIaasConfigInfoService.getIaasConfigInfo(anyString(),anyInt(),any())).thenReturn(expect);
        mockMvc.perform(get(IAAS_CONIFG_DETAIL_INFO_URL,"AWS","1").
         contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accountId").value(1))
        .andExpect(jsonPath("$.accountName").value("test"))
        .andExpect(jsonPath("$.commonAvailabilityZone").value("test"))
        .andExpect(jsonPath("$.commonKeypairName").value("test"))
        .andExpect(jsonPath("$.commonSecurityGroup").value("test"))
        .andExpect(jsonPath("$.createUserId").value("admin"))
        .andExpect(jsonPath("$.updateUserId").value("admin"))
        .andExpect(jsonPath("$.iaasConfigAlias").value("test"))
        .andExpect(jsonPath("$.id").value(1));
    }
     
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 환경 설정 정보 삭제 테스트
    * @title : testDeleteIaasConfigInfo
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteIaasConfigInfo() throws Exception{
        IaasConfigMgntDTO dto = setSaveIaasConfig();
        mockMvc.perform(delete(IAAS_CONFIG_DELETE_URL,"aws")
                .content(mapper.writeValueAsBytes(dto))
                .contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNoContent());
    }
     
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 환경 설정 정보 저장/수정 테스트
    * @title : testSaveIaasConfigInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveIaasConfigInfo() throws Exception{
        IaasConfigMgntDTO dto = setSaveIaasConfig();
        mockMvc.perform(put(IAAS_CONIFG_SAVE_URL,"aws")
                .content(mapper.writeValueAsBytes(dto))
                .contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
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
        config.setCommonRegion("asia");
        config.setTestFlag("Y");
        config.setFormat(new SimpleDateFormat());
        config.setCommonProject("");
        config.getCommonProject();
        config.setOpenstackKeystoneVersion("");
        config.getOpenstackKeystoneVersion();
        config.setOpenstackDomain("");
        
    }
     
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
    * @description : 인프라 환경 정보 삽입 데이터 설정
    * @title : setSaveIaasConfig
    * @return : IaasConfigMgntDTO
    ***************************************************/
    private IaasConfigMgntDTO setSaveIaasConfig(){
        IaasConfigMgntDTO dto = new IaasConfigMgntDTO();
        dto.setId("1");
        dto.setAccountId("1");
        dto.setCommonAvailabilityZone("test");
        dto.setCommonKeypairName("test");
        dto.setCommonKeypairPath("test");
        dto.setCommonSecurityGroup("test");
        dto.setIaasConfigAlias("test");
        dto.setIaasType("AWS");
        dto.setVsphereVcenterCluster("");
        dto.setVsphereVcenterDataCenterName("");
        dto.setVsphereVcenterDatastore("");
        dto.setVsphereVcenterDiskPath("");
        dto.setVsphereVcenterPersistentDatastore("");
        dto.setVsphereVcenterTemplateFolder("");
        dto.setVsphereVcenterVmFolder("");
        dto.getId();
        dto.getAccountId();
        dto.getCommonAvailabilityZone();
        dto.getCommonKeypairName();
        dto.getCommonKeypairPath();
        dto.getCommonSecurityGroup();
        dto.getIaasConfigAlias();
        dto.getIaasType();
        dto.getVsphereVcenterCluster();
        dto.getVsphereVcenterDataCenterName();
        dto.getVsphereVcenterDatastore();
        dto.getVsphereVcenterDiskPath();
        dto.getVsphereVcenterPersistentDatastore();
        dto.getVsphereVcenterTemplateFolder();
        dto.getVsphereVcenterVmFolder();
        return dto;
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
    * @description : 환경 설정 개수 설정
    * @title : setIaasConfigCount
    * @return : HashMap<String, Integer>
    ***************************************************/
    private HashMap<String, Integer> setIaasConfigCount() {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put("google", 1);
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
        vo.getAccountId();
        vo.getAccountName();
        vo.getCommonAvailabilityZone();
        vo.getCommonKeypairName();
        vo.getCommonKeypairPath();
        vo.getCommonSecurityGroup();
        vo.getCreateUserId();
        vo.getUpdateUserId();
        vo.getDeployStatus();
        vo.getCreateDate();
        vo.getUpdateDate();
        vo.getIaasConfigAlias();
        vo.getVsphereVcenterVmFolder();
        vo.getVsphereVcenterTemplateFolder();
        vo.getVsphereVcenterPersistentDatastore();
        vo.getVsphereVcenterDiskPath();
        vo.getVsphereVcenterDatastore();
        vo.getVsphereVcenterCluster();
        vo.getVsphereVcentDataCenterName();
        vo.getRecid();
        vo.getId();
        vo.getIaasType();
        list.add(vo);
        return list;
    }
     
}