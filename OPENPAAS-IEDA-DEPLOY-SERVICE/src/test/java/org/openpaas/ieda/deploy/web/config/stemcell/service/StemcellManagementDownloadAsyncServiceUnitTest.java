package org.openpaas.ieda.deploy.web.config.stemcell.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
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
import org.openpaas.ieda.deploy.web.config.stemcell.dao.StemcellManagementDAO;
import org.openpaas.ieda.deploy.web.config.stemcell.dao.StemcellManagementVO;
import org.openpaas.ieda.deploy.web.config.stemcell.dto.StemcellManagementDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class StemcellManagementDownloadAsyncServiceUnitTest extends BaseDeployControllerUnitTest{
    
    @InjectMocks
    StemcellManagementDownloadAsyncService mockStemcellDownloadService;
    @Mock
    SimpMessagingTemplate mockMessagingTemplate;
    @Mock
    StemcellManagementDAO mockStemcellDao;
    @Mock
    MessageSource mockMessageSource;
    
    final private static String STEMCELL_PATH = LocalDirectoryConfiguration.getStemcellDir() + "/light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz";
    final private static String LOCK_PATH = LocalDirectoryConfiguration.getLockDir()+System.getProperty("file.separator")+"light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent-download.lock";
    final private static String STEMCELL_TEMP_PATH = LocalDirectoryConfiguration.getTmpDir() + "/light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz";
    
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
        File file = new File(STEMCELL_TEMP_PATH);
        FileWriter writer = null;
        writer = new FileWriter(file);
        writer.write("test"); 
        writer.flush();
        writer.close();
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : wget을 통해 다운로드된 스템셀 상태 저장
    * @title : testRegistPublicStemcellDownLoad
    * @return : void
    ***************************************************/
    @Test
    public void testRegistPublicStemcellDownLoad() throws IOException{
        StemcellManagementDTO.Regist dto = setReleaseDownloadInfo("nomal");
        StemcellManagementVO vo = getStemcellRegistInfo();
        when(mockStemcellDao.selectPublicStemcellById(anyInt())).thenReturn(vo);
        mockStemcellDownloadService.saveStemcellDownLoadStatus(dto, vo, principal, true);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 스템셀 파일 다운로드 중 에러가 발생 한 경우
    * @title : testRegistPublicStemcellDownLoadError
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testSavePublicStemcellDownLoadStatusFromDownloadException() {
        StemcellManagementDTO.Regist dto = setReleaseDownloadInfo("nomal");
        StemcellManagementVO vo = getStemcellRegistInfo();
        mockStemcellDownloadService.saveStemcellDownLoadStatus(dto, vo, principal, false);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 스템셀 파일이 존재하고 덮어 쓰기가 체크 되어 있을 경우
    * @title : testsaveStemcellDownLoadStatusFromOverlayCheckCase
    * @return : void
    ***************************************************/
    @Test
    public void testsaveStemcellDownLoadStatusFromOverlayCheckCase() throws IOException{
        StemcellManagementDTO.Regist dto = setReleaseDownloadInfo("nomal");
        StemcellManagementVO vo = getStemcellRegistInfo();
        when(mockStemcellDao.selectPublicStemcellById(anyInt())).thenReturn(vo);
        File file = new File(STEMCELL_PATH);
        FileWriter writer = null;
        writer = new FileWriter(file);
        writer.write("test"); 
        writer.flush();
        writer.close();
        mockStemcellDownloadService.saveStemcellDownLoadStatus(dto, vo, principal, true);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 스템셀 파일이 존재하고 덮어 쓰기가 체크 되어 있지 않을 경우
    * @title : testSaveStemcellDownLoadStatusFromOverlayCheckFalse
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testSaveStemcellDownLoadStatusFromOverlayCheckFalse() throws IOException{
        StemcellManagementDTO.Regist dto = setReleaseDownloadInfo("overlay");
        StemcellManagementVO vo = getStemcellRegistInfo();
        File file = new File(STEMCELL_PATH);
        FileWriter writer = null;
        writer = new FileWriter(file);
        writer.write("test"); 
        writer.flush();
        writer.close();
        mockStemcellDownloadService.saveStemcellDownLoadStatus(dto, vo, principal, true);
    }
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 스템셀 파일 복사 중 에러가 발생 할 경우
    * @title : testTmpStemcellFileCopyError
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testSaveStemcellDownLoadStatusFromFileCopyError() throws IOException{
        File temp = new File(STEMCELL_TEMP_PATH);
        if(temp.exists()){
            temp.delete();
        }
        File file = new File(STEMCELL_PATH);
        FileWriter writer = null;
        writer = new FileWriter(file);
        writer.write("test"); 
        writer.flush();
        writer.close();
        StemcellManagementDTO.Regist dto = setReleaseDownloadInfo("nomal");
        StemcellManagementVO vo = getStemcellRegistInfo();
        mockStemcellDownloadService.saveStemcellDownLoadStatus(dto, vo, principal, true);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 스템셀 정보 조회
    * @title : testGetStemcellInfo
    * @return : StemcellManagementVO
    ***************************************************/
    @Test
    public void testGetStemcellInfo(){
        StemcellManagementDTO.Regist dto = setReleaseDownloadInfo("nomal");
        StemcellManagementVO vo = getStemcellRegistInfo();
        when(mockStemcellDao.selectPublicStemcellById(anyInt())).thenReturn(vo);
        StemcellManagementVO result = mockStemcellDownloadService.getStemcellInfo(dto);
        assertEquals(result.getDownloadLink(), vo.getDownloadLink());
        assertEquals(result.getDownloadStatus(), vo.getDownloadStatus());
        assertEquals(result.getStemcellFileName(), vo.getStemcellFileName());
        assertEquals(result.getStemcellUrl(), vo.getStemcellUrl());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 스템셀 정보 조회 결과가 존재 하지 않을 경우
    * @title : testGetStemcellInfoNullPointException
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testGetStemcellInfoNullPointException(){
        StemcellManagementDTO.Regist dto = setReleaseDownloadInfo("nomal");
        when(mockStemcellDao.selectPublicStemcellById(anyInt())).thenReturn(null);
        mockStemcellDownloadService.getStemcellInfo(dto);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 스템셀 다운로드 정보 저장 
    * @title : testSavePublicStemcell
    * @return : void
    ***************************************************/
    @Test
    public void testSavePublicStemcell(){
        StemcellManagementDTO.Regist dto = setReleaseDownloadInfo("nomal");
        StemcellManagementVO vo = getStemcellRegistInfo();
        when(mockStemcellDao.selectPublicStemcellById(anyInt())).thenReturn(vo);
        mockStemcellDownloadService.saveDownloadStemcellInfo(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 스템셀 다운로드 method 비동기 호출
    * @title : testStemcellDownloadAsync
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testStemcellDownloadAsync(){
        StemcellManagementDTO.Regist dto = setReleaseDownloadInfo("nomal");
        when(mockStemcellDao.selectPublicStemcellById(anyInt())).thenReturn(null);
        mockStemcellDownloadService.stemcellDownloadAsync(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Lock 파일 삭제
    * @title :  testDeleteLockFile
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteLockFile() throws IOException{
        File file = new File(LOCK_PATH);
        FileWriter writer = null;
        writer = new FileWriter(file);
        writer.write("test"); 
        writer.flush();
        writer.close();
        mockStemcellDownloadService.deleteLockFile("done", "light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
    }
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 릴리즈 다운로드 정보 설정
    * @title : setReleaseDownloadInfo
    * @return : StemcellManagementDTO.Regist
    ***************************************************/
    private StemcellManagementDTO.Regist setReleaseDownloadInfo(String type) {
        StemcellManagementDTO.Regist dto = new StemcellManagementDTO.Regist();
        dto.setId(1);
        dto.setStemcellUrl("https://bosh-jenkins-artifacts.s3.amazonaws.com/bosh-stemcell/aws/light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
        dto.setStemcellName("testStemcellFile");
        dto.setStemcellFileName("light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
        dto.setStemcellVersion("2820");
        dto.setStemcellSize("123456789");
        dto.setOsName("UBUNTU");
        dto.setIaasType("AWS");
        dto.setOsVersion("TRUSTY");
        dto.setLight("true");
        dto.setFileType("url");
        if("overlay".equals(type)){
            dto.setOverlayCheck("false");
        }else{
            dto.setOverlayCheck("true");
        }
        dto.setCreateUserId("tester");
        dto.setUpdateUserId("tester");
        return dto;
    }
    
    /***************************************************
    * @param string 
     * @project : Paas 플랫폼 설치 자동화
    * @description : 스템셀 상세 조회 리턴 값 설정
    * @title : getStemcellRegistInfo
    * @return : StemcellManagementVO
    ***************************************************/
    private StemcellManagementVO getStemcellRegistInfo(){
        StemcellManagementVO vo = new StemcellManagementVO();
        vo.setId(1);
        vo.setStemcellUrl("https://bosh-jenkins-artifacts.s3.amazonaws.com/bosh-stemcell/aws/light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
        vo.setStemcellName("testStemcellFile");
        vo.setStemcellFileName("light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
        vo.setDownloadLink("https://bosh-jenkins-artifacts.s3.amazonaws.com/bosh-stemcell/aws/light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
        vo.setStemcellVersion("2820");
        vo.setOsVersion("TRUSTY");
        vo.setCreateUserId("tester");
        vo.setUpdateUserId("tester");
        return vo;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 동작한 직후 실행
     * @title : tearDown
     * @return : void
    ***************************************************/
    @After
    public void tearDown(){
        //delete deployment stemcell File
        File file = new File(STEMCELL_PATH);
        if(file.exists()){
            file.delete();
        }
        
        File lockFile = new File(LOCK_PATH);
        if(lockFile.exists()){
            lockFile.delete();
        }
        
        File temp = new File(STEMCELL_TEMP_PATH);
        if(temp.exists()){
            temp.delete();
        }
    }
    
}
