package org.openpaas.ieda.deploy.web.deploy.bootstrap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.security.Principal;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.ieda.OpenpaasIedaControllerApplication;
import org.openpaas.ieda.TestBeansConfiguration;
import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.deploy.web.common.BaseControllerTest;
import org.openpaas.ieda.deploy.web.common.service.CommonDeployUtils;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.dao.BootstrapVO;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.dto.BootStrapDeployDTO;
import org.openpaas.ieda.deploy.web.information.iassConfig.dao.IaasConfigMgntDAO;
import org.openpaas.ieda.deploy.web.information.iassConfig.dao.IaasConfigMgntVO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntDAO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {OpenpaasIedaControllerApplication.class, TestBeansConfiguration.class})
@WebAppConfiguration
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class BootstrapControllerTest extends BaseControllerTest {
    
    @Autowired WebApplicationContext wac;
    @Autowired BootstrapServiceTest bootstrapServiceTest;
    @Autowired IaasAccountMgntDAO accountDao;
    @Autowired IaasConfigMgntDAO configDao;
    
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    private Principal principal = null;
    
    private final static Logger LOGGER = LoggerFactory.getLogger(BootstrapControllerTest.class);
    final private static String LOCK_DIR=LocalDirectoryConfiguration.getLockDir();
    private final static String LOCK_FILE= "openstack-microbosh-test-1.yml";
    /*************************************** URL *******************************************/
    final static String VIEW_URL = "/deploy/bootstrap"; //Bootstrap 화면 이동
    final static String BOOTSTRAP_LIST_URL = "/deploy/bootstrap/list"; //목록 정보 조회
    final static String BOOTSTRAP_DETAIL_URL = "/deploy/bootstrap/install/detail/1"; //Bootstrap  상세 조회
    final static String IAAS_ACCOUNT_SAVE_URL =  "/iaasMgnt/account/Openstack/save";
    final static String IAAS_CONIFG_SAVE_URL =  "/info/iaasConfig/Openstack/save";
    final static String BOOTSTRAP_IAAS_CONFIG_URL = "/deploy/bootstrap/install/setIaasConfigInfo"; //인프라 환경 설정 정보 저장
    final static String BOOTSTRAP_DEFAULT_URL = "/deploy/bootstrap/install/setDefaultInfo"; //기본 정보 저장
    final static String BOOTSTRAP_NETWORK_URL = "/deploy/bootstrap/install/setNetworkInfo"; //네트워크 정보 저장
    final static String BOOTSTRAP_RESOURCE_URL = "/deploy/bootstrap/install/setResourceInfo"; //리소스 정보 저장
    final static String BOOTSTRAP_RECORD_DELETE_URL = "/deploy/bootstrap/delete/data"; //단순 Bootstrap 레코드 삭제
    final static String CREATE_SETTING_FILE_URL = "/deploy/bootstrap/install/createSettingFile/1"; //배포 파일 생성
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        principal = getLoggined();
    }
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 설치 화면 이동
     * @title : testGoBootstrap
     * @return : void
    *****************************************************************/
    @Test
    public void testGoBootstrap() throws Exception{
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=================  BOOTSTRAP 설치 화면 이동 TEST START  ================="); }
        ResultActions result = mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON));
        result.andDo(MockMvcResultHandlers.print())
        .andReturn();
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : bootstrap 목록 정보 조회
     * @title : testGetBootstrapList
     * @return : void
    *****************************************************************/
    @Test
    public void testGetBootstrapList() throws Exception{
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=================  BOOTSTRAP 목록 정보 조회 TEST START  ================="); }
        ResultActions result = 
                mockMvc.perform(MockMvcRequestBuilders.get(BOOTSTRAP_LIST_URL)
                        .contentType(MediaType.APPLICATION_JSON));
        
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 설치
     * @title : testBootstrapInstall
     * @return : void
    *****************************************************************/
    @Test
    public void testBootstrapInstall() throws Exception{
        testSaveIaasAccount();
        testSaveIaasConfig();
        testSaveIaasConfigInfo();
        testSaveDefaultInfo();
        testNetworkInfoSave();
        testResourcesInfoSave();
        testMakeDeploymentFile();
        testDoInstallBootstrap();
    }
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인프라 계정 정보 저장
     * @title : testSaveIaasAccount
     * @return : void
    *****************************************************************/
    public void testSaveIaasAccount() throws Exception{
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> testSaveIaasAccount"); }
        accountDao.insertIaasAccountInfo(setSaveIaasAccountInfo());
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 환경 설정 정보 저장
     * @title : testSaveIaasConfig
     * @return : void
    *****************************************************************/
    public void testSaveIaasConfig() throws Exception{
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> testSaveIaasConfig"); }
        configDao.insertIaasConfigInfo(setSaveIaasConfig());
    }
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 환경 설정 정보 저장
     * @title : testSaveIaasConfigInfo
     * @return : void
    *****************************************************************/
    public void testSaveIaasConfigInfo() throws Exception{
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=================  testSaveIaasConfigInfo  ================="); }
        String requestJson = setIaasConfigInfo();
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(BOOTSTRAP_IAAS_CONFIG_URL)
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson));
        
        result.andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 기본정보 저장
     * @title : testSaveDefaultInfo
     * @return : void
    *****************************************************************/
    public void testSaveDefaultInfo() throws Exception{
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=================  testSaveDefaultInfo  ================="); }
        String requestJson = mapper.writeValueAsString(setBootStrapDefault());
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(BOOTSTRAP_DEFAULT_URL)
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson));
        
        result.andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 네트워크 정보 저장 
     * @title : testNetworkInfoSave
     * @return : void
    *****************************************************************/
    public void testNetworkInfoSave() throws Exception{
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=================  testNetworkInfoSave  ================="); }
        String requestJson = setBootStrapNetwork();
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(BOOTSTRAP_NETWORK_URL)
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson));
        
        result.andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 리소스 정보 저장 
     * @title : testResourcesInfoSave
     * @return : void
    *****************************************************************/
    public void testResourcesInfoSave() throws Exception{
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=================  testResourcesInfoSave  ================="); }

        String requestJson = setBootStrapResource();
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(BOOTSTRAP_RESOURCE_URL)
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson));
        
        result.andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
        
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 배포 파일 생성 및 정보 저장
     * @title : testMakeDeploymentFile
     * @return : void
    *****************************************************************/
    public void testMakeDeploymentFile() throws Exception{
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=================  testMakeDeploymentFile  ================="); }
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(CREATE_SETTING_FILE_URL)
                .contentType(APPLICATION_JSON_UTF8)
                .param("id", "1"));
        
        result.andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andReturn();
        
        CommonDeployUtils.deleteFile(LOCK_DIR, LOCK_FILE);
    }
    

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 설치
     * @title : testDoInstallBootstrap
     * @return : void
    *****************************************************************/
    public void testDoInstallBootstrap() throws Exception{
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=================  testDoInstallBootstrap  ================="); }
        BootStrapDeployDTO.Install dto = setBootstrapInstall();
        bootstrapServiceTest.testDeployAsync(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 상세 조회 
     * @title : testGetBootstrapInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testGetBootstrapInfo() throws Exception{
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=================  testGetBoo2tstrapInfo  ================="); }
        testBootstrapInstall();
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(BOOTSTRAP_DETAIL_URL)
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON));
        result.andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 단순 Bootstrap 레코드 삭제
     * @title : testDeleteJustOnlyBootstrapRecord
     * @return : void
    *****************************************************************/
    @Test
    public void testDeleteBootstrapInfo()throws Exception{
        if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  testDeleteJustOnlyBootstrapRecord  =================");  }
        String requestJson = mapper.writeValueAsString(setBootstrapDelete());
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete(BOOTSTRAP_RECORD_DELETE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson));
        
        result.andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();
        
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인프라 환경 설정 정보 설정
     * @title : setIaasConfigInfo
     * @return : String
    *****************************************************************/
    public String setIaasConfigInfo() throws Exception{
        BootStrapDeployDTO.IaasConfig dto = new BootStrapDeployDTO.IaasConfig();
        dto.setIaasType("Openstack");
        dto.setIaasConfigId("1");
        dto.setTestFlag("true");
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(dto);
        
        return requestJson;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 기본정보 설정 및 수정
     * @title : setBootStrapDefault
     * @return : BootStrapDeployDTO.Default
    *****************************************************************/
    public BootStrapDeployDTO.Default setBootStrapDefault(){
        
        BootStrapDeployDTO.Default dto = new BootStrapDeployDTO.Default();
        dto.setId("1");
        dto.setDeploymentName("bosh");
        dto.setDirectorName("test-bosh");
        dto.setNtp("1.kr.pool.ntp.org, 0.asia.pool.ntp.org");
        dto.setBoshRelease("bosh-233.tgz");
        dto.setBoshCpiRelease("bosh-openstack-cpi-release-14.tgz");
        dto.setEnableSnapshots("true");
        dto.setSnapshotSchedule("0 0 7 * * * schedule");
        
        return dto;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 네트워크 정보 설정
     * @title : setBootStrapNetwork
     * @return : String
    *****************************************************************/
    public String setBootStrapNetwork() throws Exception{
        
        BootStrapDeployDTO.Network dto = new BootStrapDeployDTO.Network();
        
        dto.setId("1");
        dto.setSubnetId("text-subnetId-12345");
        dto.setPrivateStaticIp("10.0.100.11");
        dto.setPublicStaticIp("10.0.20.6");
        dto.setSubnetRange("10.0.20.0/24");
        dto.setSubnetGateway("10.0.20.1");
        dto.setSubnetDns("8.8.8.8");
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(dto);
        
        return requestJson;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 리소스 정보 설정
     * @title : setBootStrapResource
     * @return : String
    *****************************************************************/
    public String setBootStrapResource() throws Exception{
        
        BootStrapDeployDTO.Resource dto = new BootStrapDeployDTO.Resource();
        
        dto.setId("1");
        dto.setStemcell("light-bosh-stemcell-3147-aws-xen-ubuntu-trusty-go_agent.tgz");
        dto.setCloudInstanceType("m1.large");
        dto.setBoshPassword("1234");
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(dto);
        
        return requestJson;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 설치 설정
     * @title : setBootstrapInstall
     * @return : BootStrapDeployDTO.Install
    ***************************************************/
    public BootStrapDeployDTO.Install setBootstrapInstall(){
        
        BootStrapDeployDTO.Install dto = new BootStrapDeployDTO.Install();
        dto.setId("1");
        
        return dto;
    }
    
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 삭제 정보 설정
     * @title : setBootstrapDelete
     * @return : BootStrapDeployDTO.Delete
    ***************************************************/
    public BootStrapDeployDTO.Delete setBootstrapDelete(){
        BootStrapDeployDTO.Delete dto = new BootStrapDeployDTO.Delete();
        dto.setId("1");
        dto.setIaasType("Openstack");
        return dto;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 인프라 환경 설정 정보 설정(수정)
     * @title : setSavedIaasConfigInfo
     * @return : String
    ***************************************************/
    public BootstrapVO setSavedIaasConfigInfo(){
        BootstrapVO vo = new BootstrapVO();
        vo.setId(1);
        vo.setIaasType("openstack");
        vo.setIaasConfigId(1);
        
        return vo;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 설치 정보 설정
     * @title : setBootStrapInfoVO
     * @return : String
    ***************************************************/
    public BootstrapVO setBootStrapInfoVO(){
        BootstrapVO vo = new BootstrapVO();
        vo.setId(1);
        vo.setIaasType("openstack");
        vo.setIaasConfigId(1);
        
        return vo;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
    * @description : 인프라 환경 정보 삽입 데이터 설정
    * @title : setSaveIaasConfig
    * @return : IaasConfigMgntDTO
    ***************************************************/
    private IaasConfigMgntVO setSaveIaasConfig() throws Exception{
        IaasConfigMgntVO vo = new IaasConfigMgntVO();
        vo.setId(1);
        vo.setAccountId(1);
        vo.setCommonKeypairName("test");
        vo.setCommonKeypairPath("test");
        vo.setCommonSecurityGroup("test");
        vo.setIaasConfigAlias("test");
        vo.setIaasType("Openstack");
        vo.setTestFlag("true");
        vo.setCreateUserId(principal.getName());
        vo.setUpdateUserId(principal.getName());
        
        return vo;
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 인프라 계정 저장 정보 설정
     * @title : setSaveIaasAccountInfo
     * @return : IaasAccountMgntDTO
     ***************************************************/
    public IaasAccountMgntVO setSaveIaasAccountInfo(){
         IaasAccountMgntVO vo = new IaasAccountMgntVO();
         vo.setId(1);
         vo.setIaasType("Openstack");
         vo.setAccountName("openstack-test");
         vo.setCommonAccessUser("test");
         vo.setCommonAccessSecret("testPw");
         vo.setCreateUserId(principal.getName());
         vo.setUpdateUserId(principal.getName());
             
         return vo;
     }
    
}
