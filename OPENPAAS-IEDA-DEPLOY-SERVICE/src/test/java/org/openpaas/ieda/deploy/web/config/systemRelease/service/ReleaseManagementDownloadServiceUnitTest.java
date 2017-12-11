package org.openpaas.ieda.deploy.web.config.systemRelease.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Principal;

import javax.ws.rs.core.Application;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.web.common.base.BaseDeployControllerUnitTest;
import org.openpaas.ieda.deploy.web.config.systemRelease.dao.ReleaseManagementDAO;
import org.openpaas.ieda.deploy.web.config.systemRelease.dao.ReleaseManagementVO;
import org.openpaas.ieda.deploy.web.config.systemRelease.dto.ReleaseManagementDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ReleaseManagementDownloadServiceUnitTest extends BaseDeployControllerUnitTest{
    
    final private static String RELEASE_REAL_PATH = LocalDirectoryConfiguration.getReleaseDir() +  "/bosh-openstack-cpi-release-20.tgz";
    final private static String RELEASE_LOCK_PATH = LocalDirectoryConfiguration.getLockDir()+System.getProperty("file.separator")+"bosh-openstack-cpi-release-20-download.lock";
    final private static String RELEASE_TMP_PATH = LocalDirectoryConfiguration.getTmpDir()+"/bosh-openstack-cpi-release-20.tgz";
    private Principal principal = null;
    
    @InjectMocks
    ReleaseManagementDownloadService mockReleaseManagementDownloadService;
    @Mock
    ReleaseManagementDAO mockReleaseManagementDao;
    @Mock
    SimpMessagingTemplate mockMessagingTemplate;
    @Mock
    MessageSource mockMessageSource;
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        principal = getLoggined();
        File file = new File(RELEASE_TMP_PATH);
        FileWriter writer = new FileWriter(file);
        writer.write("test"); 
        writer.close();
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 릴리즈 다운로드 중 릴리즈 명 값이 빈 값일 경우
    * @title : testCheckSystemReleaseDownloadFileFromEmptyReleaseNameCase
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testCheckSystemReleaseDownloadFileFromEmptyReleaseNameCase(){
        ReleaseManagementDTO.Regist dto = setReleaseRegistInfoUrl("nomal");
        ReleaseManagementVO vo =  getRegistSystemReleaseUploadInfo("empty");
        when(mockReleaseManagementDao.selectSystemReleaseById(dto.getId())).thenReturn(vo);
        mockReleaseManagementDownloadService.checkSystemReleaseDownloadFile(dto,vo, principal, false);
    }
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 시스템 릴리즈 다운로드 파일 검사
    * @title : testSystemReleaseDownload
    * @return : void
    ***************************************************/
    @Test
    public void testCheckSystemReleaseDownloadFile(){
        FileWriter writer = null;
        try {
            writer = new FileWriter(RELEASE_REAL_PATH);
            writer.write("test"); 
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        ReleaseManagementDTO.Regist dto = setReleaseRegistInfoUrl("nomal");
        ReleaseManagementVO vo =  getRegistSystemReleaseUploadInfo("nomal");
        when(mockReleaseManagementDao.selectSystemReleaseById(dto.getId())).thenReturn(vo);
        doNothing().when(mockMessagingTemplate).convertAndSendToUser(any(),anyString(),anyString());
        mockReleaseManagementDownloadService.checkSystemReleaseDownloadFile(dto,vo, principal, true);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 릴리즈 다운로드 중 릴리즈 파일이 존재하고 덮어쓰기 체크가 안되어 있을 경우
    * @title : testCheckSystemReleaseDownloadFileFromOverlayfalseCase
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testCheckSystemReleaseDownloadFileFromOverlayfalseCase(){
        File file = new File(RELEASE_REAL_PATH);
        FileWriter writer = null;
        try{
            writer = new FileWriter(file);
            writer.write("test"); 
        }catch (Exception e) {
            e.printStackTrace();
        }
        ReleaseManagementDTO.Regist dto = setReleaseRegistInfoUrl("conflict");
        ReleaseManagementVO vo =  getRegistSystemReleaseUploadInfo("nomal");
        when(mockReleaseManagementDao.selectSystemReleaseById(dto.getId())).thenReturn(vo);
        doNothing().when(mockMessagingTemplate).convertAndSendToUser(any(),anyString(),anyString());
        mockReleaseManagementDownloadService.checkSystemReleaseDownloadFile(dto,vo, principal, true);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 릴리즈 Lock 파일 삭제
    * @title : testDeleteLockFile
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteLockFile(){
        File file = new File(RELEASE_LOCK_PATH);
        FileWriter writer = null;
        try{
            writer = new FileWriter(file);
            writer.write("test"); 
        }catch (Exception e) {
            e.printStackTrace();
        }
        mockReleaseManagementDownloadService.deleteLockFile("bosh-openstack-cpi-release-20.tgz");
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 릴리즈 다운로드 정보 저장 
    * @title : testSaveSystemReleseInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveSystemReleseInfo(){
        ReleaseManagementDTO.Regist dto = setReleaseRegistInfoUrl("nomal");
        ReleaseManagementVO vo = getRegistSystemReleaseUploadInfo("nomal");
        when(mockReleaseManagementDao.selectSystemReleaseById(anyInt())).thenReturn(vo);
        mockReleaseManagementDownloadService.saveSystemReleseInfo(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 릴리즈 다운로드 정보 저장 중 Null Point 오류가 발생 한 경우
    * @title : testSaveSystemReleseNullPointError
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testSaveSystemReleseInfoFromExceptionCase(){
        ReleaseManagementDTO.Regist dto = setReleaseRegistInfoUrl("nomal");
        when(mockReleaseManagementDao.selectSystemReleaseById(anyInt())).thenReturn(null);
        mockReleaseManagementDownloadService.saveSystemReleseInfo(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 릴리즈 정보 조회
    * @title : testGetSystemReleaseInfo
    * @return : void
    ***************************************************/
    @Test
    public void testGetSystemReleaseInfo(){
        ReleaseManagementDTO.Regist dto = setReleaseRegistInfoUrl("nomal");
        ReleaseManagementVO vo = getRegistSystemReleaseUploadInfo("nomal");
        when(mockReleaseManagementDao.selectSystemReleaseById(anyInt())).thenReturn(vo);
        
        ReleaseManagementVO result = mockReleaseManagementDownloadService.getSystemReleaseInfo(dto);
        assertEquals(result.getDownloadLink(), vo.getDownloadLink());
        assertEquals(result.getId(), vo.getId());
        assertEquals(result.getReleaseFileName(), vo.getReleaseFileName());
        assertEquals(result.getReleaseSize(), vo.getReleaseSize());
        assertEquals(result.getReleaseName(), vo.getReleaseName());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 릴리즈 정보 조회가 존재 하지 않을 경우
    * @title : testGetSystemReleaseInfoFromBadRequestCase
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testGetSystemReleaseInfoFromBadRequestCase(){
        ReleaseManagementDTO.Regist dto = setReleaseRegistInfoUrl("nomal");
        when(mockReleaseManagementDao.selectSystemReleaseById(anyInt())).thenReturn(null);
        mockReleaseManagementDownloadService.getSystemReleaseInfo(dto);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 릴리즈 파일 복사 중 에러가 발생 한 경우
    * @title : testCheckSystemReleaseDownloadFileFromMoveException
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testCheckSystemReleaseDownloadFileFromMoveException(){
        ReleaseManagementDTO.Regist dto = setReleaseRegistInfoUrl("nomal");
        ReleaseManagementVO vo = getRegistSystemReleaseUploadInfo("nomal");
        File tmpFile = new File(RELEASE_TMP_PATH);
        tmpFile.delete();
        mockReleaseManagementDownloadService.checkSystemReleaseDownloadFile(dto, vo, principal, true);
    }
    
    /***************************************************
     * @param string 
     * @project : Paas 플랫폼 설치 자동화
     * @description : URL을 통한 릴리즈 정보 설정 
     * @title : setReleaseRegistInfoUrl
     * @return : String
    ***************************************************/
    public ReleaseManagementDTO.Regist setReleaseRegistInfoUrl(String type){
        ReleaseManagementDTO.Regist dto = new ReleaseManagementDTO.Regist();
        dto.setId(1);
        dto.setReleasePathUrl("https://bosh.io/d/github.com/cloudfoundry-incubator/bosh-openstack-cpi-release?v=20");
        dto.setDownloadLink("https://bosh.io/d/github.com/cloudfoundry-incubator/bosh-openstack-cpi-release?v=20");
        dto.setReleaseName("bosh-test-cpi-release/20");
        dto.setReleaseFileName("bosh-openstack-cpi-release-20.tgz");
        dto.setReleasePathVersion("20");
        dto.setReleaseSize("123456789");
        dto.setFileType("url");
        if("conflict".equals(type)){
            dto.setOverlayCheck("false");
        }else{
            dto.setOverlayCheck("true");
        }
        dto.setReleaseType("bosh_cpi");
        dto.setIaasType("openstack");
        dto.setCreateUserId("tester");
        dto.setUpdateUserId("tester");
        return dto;
    }
    
    /***************************************************
    * @param string 
     * @project : Paas 플랫폼 설치 자동화
    * @description : 파일 타입 릴리즈 정보 저장 시 리턴 값 설정
    * @title : getRegistSystemReleaseUploadInfo
    * @return : ReleaseManagementVO
    ***************************************************/
    public ReleaseManagementVO getRegistSystemReleaseUploadInfo(String type) {
        ReleaseManagementVO vo = new ReleaseManagementVO();
        vo.setId(1);
        vo.setReleaseFileName("bosh-openstack-cpi-release-20.tgz");
        vo.setReleaseName("bosh-test-cpi-release");
        vo.setReleaseSize("123456789");
        vo.setDownloadStatus("downlading");
        vo.setDownloadLink("https://bosh.io/d/github.com/cloudfoundry-incubator/bosh-openstack-cpi-release?v=20");
        vo.setReleaseType("cpi");
        vo.setCreateUserId("admin");
        vo.setUpdateUserId("admin");
        return vo;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 각 각의 메소드 처리 후 릴리즈 실제 파일, Lock 파일 삭제 
    * @title : tearDown
    * @return : void
    ***************************************************/
    @After
    public void tearDown(){
        //delete Release File
        File file = new File(RELEASE_REAL_PATH);
        if(file.exists()){
            file.delete();
        }
        
        File tmpFile = new File(RELEASE_TMP_PATH);
        if(tmpFile.exists()){
            tmpFile.delete();
        }
        //delte Release lock File
        File lockFile = new File(RELEASE_LOCK_PATH);
        if(lockFile.exists()){
            lockFile.delete();
        }
    }
}
