package org.openpaas.ieda.deploy.web.information.release;

import static org.junit.Assert.assertEquals;

import java.security.Principal;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.ieda.OpenpaasIedaControllerApplication;
import org.openpaas.ieda.TestBeansConfiguration;
import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.deploy.api.release.ReleaseInfoDTO;
import org.openpaas.ieda.deploy.web.common.BaseControllerTest;
import org.openpaas.ieda.deploy.web.common.service.CommonDeployUtils;
import org.openpaas.ieda.deploy.web.config.systemRelease.ReleaseManagementDownloadServiceTest;
import org.openpaas.ieda.deploy.web.config.systemRelease.dto.ReleaseManagementDTO;
import org.openpaas.ieda.deploy.web.information.stemcell.StemcellControllerTest;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {OpenpaasIedaControllerApplication.class, TestBeansConfiguration.class})
@WebAppConfiguration
@Transactional
@TransactionConfiguration(defaultRollback=true)
public class ReleaseControllerTest extends BaseControllerTest{
    
    @Autowired WebApplicationContext wac;
    @Autowired ReleaseServiceTest service;
    @Autowired ReleaseManagementDownloadServiceTest releaseManagementservice;
    
    private Principal principal = null;
    private MockMvc mockMvc;
    
    final private static String RELEASE_DIR=LocalDirectoryConfiguration.getReleaseDir();
    final private static String LOCK_DIR=LocalDirectoryConfiguration.getLockDir();
    final private static String RELEASE_REAL_PATH = "bosh-openstack-cpi-release-20.tgz";
    final private static String RELEASE_UPLOAD_LOCK_PATH = "bosh-openstack-cpi-release-20-upload.lock";
    final private static String RELEASE_DOWNLOAD_LOCK_PATH = "bosh-openstack-cpi-release-20-download.lock";
    
    private final static Logger LOGGER = LoggerFactory.getLogger(StemcellControllerTest.class);
    
    /*************************************** URL *******************************************/
    final static String VIEW_URL = "/info/release"; //릴리즈 업로드 화면 요청
    final static String DOWNLOAD_RELEASE_LIST_URL = "/info/release/list/local"; //로컬에 다운로드 된 스템셀 목록 정보 조회
    final static String DOWNLOAD_RELEASE_DELETE_URL = "/deleteLocalRelease"; //로컬 릴리즈 삭제
    final static String SYSTEM_RELEASE_REGIST_URL = "/config/systemRelease/regist"; //릴리즈 등록

    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 동작하기 직전 실행
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(SecurityMockMvcConfigurers.springSecurity()).build();
        principal = getLoggined();
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 릴리즈 업로드 화면 이동
     * @title : testGoListRelease
     * @return : void
    *****************************************************************/
    @Test
    public void testGoListRelease() throws Exception {
        if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  릴리즈 업로드 화면 이동 START  ================="); }
        if( mockMvc != null ){
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(VIEW_URL)
                    .contentType(MediaType.APPLICATION_JSON));
            
            result.andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();
        }
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드 된 릴리즈 목록 정보 조회 
     * @title : testGetListRelease
     * @return : void
    *****************************************************************/
    @Test
    public void testGetListRelease() throws Exception {
        if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  업로드 된 릴리즈 목록 정보 조회 START  ================="); }
        List<ReleaseInfoDTO> list = service.uploadedReleaseList();
        assertEquals(list.get(0).getName(), "bosh");
        assertEquals(list.get(0).getVersion(), "256");
        assertEquals(list.get(0).getJobNames(), "[71adadbc]");
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 로컬 릴리즈 목록 정보 조회
     * @title : testGetListLocalRelease
     * @return : void
    *****************************************************************/
    @Test
    public void testGetListLocalRelease() throws Exception {
        if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  다운로드 된 릴리즈 목록 정보 조회 START  ================="); }
        if(mockMvc != null){
            ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(DOWNLOAD_RELEASE_LIST_URL)
                    .contentType(MediaType.APPLICATION_JSON));
            
            result.andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();
        }
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 시스템 릴리즈 입력 정보 저장 
     * @title : testSystemReleaseRegist
     * @return : void
    *****************************************************************/
    @Test
    public void testSystemReleaseRegist() throws Exception {
        if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  시스템 릴리즈 입력 정보 저장 START  ================="); }
        String requestJson = setReleaseRegistInfoUrl();
        if( mockMvc != null ){
             ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(SYSTEM_RELEASE_REGIST_URL)
                     .contentType(APPLICATION_JSON_UTF8)
                     .content(requestJson));
                
             result.andDo(MockMvcResultHandlers.print())
             .andExpect(MockMvcResultMatchers.status().isOk())
             .andReturn();
        }
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 시스템 릴리즈 다운로드
     * @title : testSystemReleaseDownloading
     * @return : void
    *****************************************************************/
    @Test
    public void testSystemReleaseDownloading() throws Exception {
        if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  시스템 릴리즈 다운로드 START  ================="); }
        testSystemReleaseRegist();
        ReleaseManagementDTO.Regist dto = setReleaseDownload();
        Principal principalTest = principal; 
        releaseManagementservice.testReleaseDownloadAsync(dto, principalTest);
    }
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 릴리즈 업로드
     * @title : testDoUploadRelease
     * @return : void
    *****************************************************************/
    @Test
    public void testDoUploadRelease() throws Exception {
        if(LOGGER.isInfoEnabled()){  LOGGER.info("=================  릴리즈 업로드 START  ================="); }
        String userId = principal.getName();
        service.uploadReleaseAsync(userId);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드 된 릴리즈 삭제
     * @title : testDoDeleteRelease
     * @return : void
    *****************************************************************/
    @Test
    public void testDoDeleteRelease() throws Exception {
        if(LOGGER.isInfoEnabled()){  LOGGER.info("================= 업로드 된 릴리즈 삭제 START  ================="); }
         service.deleteReleaseAsync();
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 시스템 릴리즈 다운로드 설정
     * @title : setReleaseDownload
     * @return : ReleaseManagementDTO.Regist
    *****************************************************************/
    public ReleaseManagementDTO.Regist setReleaseDownload() {
        ReleaseManagementDTO.Regist dto = new ReleaseManagementDTO.Regist();
        dto.setId(1);
        dto.setReleaseName("bosh-openstack-cpi-release-20");
        dto.setReleaseSize("123456789");
        dto.setFileType("url");
        dto.setReleaseFileName("https://bosh.io/d/github.com/cloudfoundry-incubator/bosh-openstack-cpi-release?v=20");
        dto.setOverlayCheck("true");
        dto.setReleaseType("cpi");
        
        return dto;
    }
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 시스템 릴리즈 입력 설정 URL
     * @title : setReleaseRegistInfoUrl
     * @return : String
    *****************************************************************/
    public String setReleaseRegistInfoUrl() throws Exception{
        ReleaseManagementDTO.Regist dto = new ReleaseManagementDTO.Regist();
        dto.setId(1);
        dto.setReleasePathUrl("https://bosh.io/d/github.com/cloudfoundry-incubator/bosh-openstack-cpi-release?v=20");
        dto.setReleaseName("bosh-test-cpi-release/20");
        dto.setReleasePathVersion("20");
        dto.setReleaseSize("123456789");
        dto.setFileType("url");
        dto.setOverlayCheck("true");
        dto.setReleaseType("bosh-cpi");
        dto.setIaasType("openstack");
        dto.setCreateUserId("tester");
        dto.setUpdateUserId("tester");
        
        //JSON 형태로 변환
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson= ow.writeValueAsString(dto);
        
        return requestJson;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 동작한 직후 실행
     * @title : tearDown
     * @return : void
    *****************************************************************/
    @After
    public void tearDown(){
        //delete Release File
        CommonDeployUtils.deleteFile(RELEASE_DIR, RELEASE_REAL_PATH);
        //delete upload Lock File
        CommonDeployUtils.deleteFile(LOCK_DIR, RELEASE_UPLOAD_LOCK_PATH);
        //delete download Release Lock File
        CommonDeployUtils.deleteFile(LOCK_DIR, RELEASE_DOWNLOAD_LOCK_PATH);
    }
    
}
