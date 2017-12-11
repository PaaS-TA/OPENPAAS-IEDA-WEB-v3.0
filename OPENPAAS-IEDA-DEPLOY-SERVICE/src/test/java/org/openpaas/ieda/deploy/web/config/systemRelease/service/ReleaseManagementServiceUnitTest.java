package org.openpaas.ieda.deploy.web.config.systemRelease.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
import org.openpaas.ieda.deploy.web.common.service.CommonDeployService;
import org.openpaas.ieda.deploy.web.common.service.CommonDeployUtils;
import org.openpaas.ieda.deploy.web.config.systemRelease.dao.ReleaseManagementDAO;
import org.openpaas.ieda.deploy.web.config.systemRelease.dao.ReleaseManagementVO;
import org.openpaas.ieda.deploy.web.config.systemRelease.dto.ReleaseManagementDTO;
import org.openpaas.ieda.deploy.web.management.code.dao.CommonCodeDAO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class ReleaseManagementServiceUnitTest extends BaseDeployControllerUnitTest{
    
    private Principal principal = null;
    final private static String SEPERATOR=System.getProperty("file.separator");
    final private static String RELEASE_REAL_PATH = LocalDirectoryConfiguration.getReleaseDir();
    final private static String RELEASE_DIR= RELEASE_REAL_PATH+SEPERATOR+"bosh-openstack-cpi-release-20.tgz";
    final private static String RELEASE_LOCK_PATH = LocalDirectoryConfiguration.getLockDir();
    final private static String RELEASE_LOCK_DIR= RELEASE_LOCK_PATH + SEPERATOR + "bosh-openstack-cpi-release-20-download.lock";
    
    @InjectMocks
    private ReleaseManagementService mockReleaseManagementService;
    @Mock
    private ReleaseManagementDAO mockReleaseManagemetDao;
    @Mock
    private CommonCodeDAO mockCommonCodeDao;
    @Mock
    private CommonDeployService mockCommonService;
    @Mock
    private MessageSource mockMessageSource;
    
    
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
    

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 시스템 릴리즈 정보 존재 시 목록 조회 테스트
     * @title : testGetSystemReleaseListFromInfoExistCase
     * @return : void
    *****************************************************************/
    @Test
    public void testGetSystemReleaseListFromInfoExistCase(){
        List<ReleaseManagementVO> releaseList = getReleaseListInfo("exist");
        when(mockReleaseManagemetDao.selectSystemReleaseList()).thenReturn(releaseList);
        List<ReleaseManagementVO> resultList = mockReleaseManagementService.getSystemReleaseList();
        assertEquals(1, resultList.size());
        assertEquals(releaseList.get(0).getDownloadLink(), resultList.get(0).getDownloadLink());
        assertEquals(releaseList.get(0).getReleaseFileName(), resultList.get(0).getReleaseFileName());
        assertEquals(releaseList.get(0).getReleaseSize(), resultList.get(0).getReleaseSize());
        assertEquals(releaseList.get(0).getReleaseName(), resultList.get(0).getReleaseName());
        assertEquals(releaseList.get(0).getReleaseType(), resultList.get(0).getReleaseType());
        assertEquals(releaseList.get(0).getDownloadStatus(), resultList.get(0).getDownloadStatus());
        verify(mockReleaseManagemetDao, times(2)).selectSystemReleaseList();
        verifyNoMoreInteractions(mockReleaseManagemetDao);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 시스템 릴리즈 정보가 존재 하고 다운로드 상태가 Null 값인 경우 시스템 릴리즈 목록 정보 조회 테스트
     * @title : testDownloadStatusNullGetSystemReleaseList
     * @return : void
    *****************************************************************/
    @Test
    public void testDownloadStatusNullGetSystemReleaseList(){
        List<ReleaseManagementVO> releaseList = getReleaseListInfo("null");
        when(mockReleaseManagemetDao.selectSystemReleaseList()).thenReturn(releaseList);
        List<ReleaseManagementVO> resultList = mockReleaseManagementService.getSystemReleaseList();
        assertEquals(1, resultList.size());
        assertEquals(releaseList.get(0).getDownloadLink(), resultList.get(0).getDownloadLink());
        assertEquals(releaseList.get(0).getReleaseFileName(), resultList.get(0).getReleaseFileName());
        assertEquals(releaseList.get(0).getReleaseSize(), resultList.get(0).getReleaseSize());
        assertEquals(releaseList.get(0).getReleaseName(), resultList.get(0).getReleaseName());
        assertEquals(releaseList.get(0).getReleaseType(), resultList.get(0).getReleaseType());
        assertEquals(releaseList.get(0).getDownloadStatus(), resultList.get(0).getDownloadStatus());
        verify(mockReleaseManagemetDao, times(2)).selectSystemReleaseList();
        verifyNoMoreInteractions(mockReleaseManagemetDao);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 시스템 릴리즈 정보가 존재 하고 다운로드 상태가 DOWNLOADED일 경우 시스템 릴리즈 목록 정보 조회
     * @title : testDownloadStatusDownLoadedGetSystemReleaseList
     * @return : void
    *****************************************************************/
    @Test
    public void testDownloadStatusDownLoadedGetSystemReleaseList(){
        List<ReleaseManagementVO> releaseList = getReleaseListInfo("downloaded");
        when(mockReleaseManagemetDao.selectSystemReleaseList()).thenReturn(releaseList);
        doNothing().when(mockReleaseManagemetDao).deleteSystemRelase(any());
        List<ReleaseManagementVO> resultList = mockReleaseManagementService.getSystemReleaseList();
        assertEquals(1, resultList.size());
        assertEquals(releaseList.get(0).getDownloadLink(), resultList.get(0).getDownloadLink());
        assertEquals(releaseList.get(0).getReleaseFileName(), resultList.get(0).getReleaseFileName());
        assertEquals(releaseList.get(0).getReleaseSize(), resultList.get(0).getReleaseSize());
        assertEquals(releaseList.get(0).getReleaseName(), resultList.get(0).getReleaseName());
        assertEquals(releaseList.get(0).getReleaseType(), resultList.get(0).getReleaseType());
        assertEquals(releaseList.get(0).getDownloadStatus(), resultList.get(0).getDownloadStatus());
        verify(mockReleaseManagemetDao, times(2)).selectSystemReleaseList();
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 공통 릴리즈 유형 콤보 조회 테스트
     * @title : testGetSystemReleaseTypeList
     * @return : void
    *****************************************************************/
    @Test
    public void testGetSystemReleaseTypeList(){
        List<String> releaseTypeList = setReleaseTypeListInfo();
        when(mockCommonCodeDao.selectReleaseTypeList("RELEASE_TYPE")).thenReturn(releaseTypeList);
        List<String> resultList = mockReleaseManagementService.getSystemReleaseTypeList();
        assertEquals(5, resultList.size());
        assertEquals(releaseTypeList.get(0), resultList.get(0));
        assertEquals(releaseTypeList.get(1), resultList.get(1));
        assertEquals(releaseTypeList.get(2), resultList.get(2));
        assertEquals(releaseTypeList.get(3), resultList.get(3));
        assertEquals(releaseTypeList.get(4), resultList.get(4));
        verify(mockCommonCodeDao, times(1)).selectReleaseTypeList("RELEASE_TYPE");
        verifyNoMoreInteractions(mockCommonCodeDao);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 로컬에 다운로드 된 릴리즈 정보 목록
     * @title : testGetLocalReleaseList
     * @return : void
    *****************************************************************/
    @Test
    public void testGetLocalReleaseList(){
        List<String> downLoadReleaseList = setReleaseTypeListInfo();
        when(mockReleaseManagemetDao.selectLocalReleaseList(anyString(), anyString())).thenReturn(downLoadReleaseList);
        List<String> resultList = mockReleaseManagementService.getLocalReleaseList(anyString(), anyString());
        assertEquals(5, resultList.size());
        assertEquals(downLoadReleaseList.get(0), resultList.get(0));
        assertEquals(downLoadReleaseList.get(1), resultList.get(1));
        assertEquals(downLoadReleaseList.get(2), resultList.get(2));
        assertEquals(downLoadReleaseList.get(3), resultList.get(3));
        assertEquals(downLoadReleaseList.get(4), resultList.get(4));
        verify(mockReleaseManagemetDao, times(1)).selectLocalReleaseList(anyString(), anyString());
        verifyNoMoreInteractions(mockReleaseManagemetDao);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 로컬에 저장된 릴리즈 목록 조회
     * @title : testGetLocalDownloadReleaseList
     * @return : void
    *****************************************************************/
    @Test
    public void testGetLocalDownloadReleaseList(){
        mockReleaseManagementService.getLocalReleaseFileList();
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Lock 파일 삭제 중 StringIndexOutOfBoundsException
     * @title : testDeleteLockFileFromStringIndexOutOfBoundsException
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testDeleteLockFileFromStringIndexOutOfBoundsException(){
        mockReleaseManagementService.deleteLockFile("error","error");
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 파일 형식 시스템 릴리즈 정보 저장 중 PreconditonFailError 발생
     * @title : testSaveSystemReleaseFileUploadInfoFromPreconditonFail
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testSaveSystemReleaseFileUploadInfoFromPreconditonFail(){
        ReleaseManagementDTO.Regist dto = setRegistSystemReleaseUploadInfo("precondition");
        when(mockMessageSource.getMessage(any(), any(), any())).thenReturn("message");
        mockReleaseManagementService.saveSystemReleaseFileUploadInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 파일 형식의 릴리즈 정보 저장 중 릴리즈 명 Not Found Error 발생
     * @title : testSaveSystemReleaseFileUploadInfoFromBadRequestCase
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testSaveSystemReleaseFileUploadInfoFromBadRequestCase() throws IOException{
        File file = new File(RELEASE_LOCK_DIR);
        FileWriter writer = new FileWriter(file);
        writer.write("test"); 
        writer.flush();
        writer.close();
        ReleaseManagementDTO.Regist dto = setRegistSystemReleaseUploadInfo("notfound");
        mockReleaseManagementService.saveSystemReleaseFileUploadInfo(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 시스템 릴리즈 정보 저장 테스트
    * @title : testInsertSystemReleaseInfo
    * @return : void
    ***************************************************/
    @Test
    public void testInsertSystemReleaseInfo(){
        ReleaseManagementDTO.Regist dto = setRegistSystemReleaseUploadInfo("nomal");
        when(mockReleaseManagemetDao.selectSystemRelease(anyString())).thenReturn(null);
        ReleaseManagementVO result = mockReleaseManagementService.saveSystemReleaseInfo(dto, principal);
        assertEquals(null, result);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 시스템 릴리즈 정보 수정
    * @title : testSaveSystemReleaseInfoFromUpdateCase
    * @return : void
    ***************************************************/
    @Test
    public void testSaveSystemReleaseInfoFromUpdateCase(){
        ReleaseManagementDTO.Regist dto = setRegistSystemReleaseUploadInfo("nomal");
        ReleaseManagementVO vo = getRegistSystemReleaseUploadInfo("nomal");
        when(mockReleaseManagemetDao.selectSystemRelease(anyString())).thenReturn(vo);
        when(mockReleaseManagemetDao.selectSystemReleaseById(anyInt())).thenReturn(vo);
        ReleaseManagementVO result = mockReleaseManagementService.saveSystemReleaseInfo(dto, principal);
        assertEquals(result.getId(), vo.getId());
        assertEquals(result.getReleaseFileName(), vo.getReleaseFileName());
        assertEquals(result.getReleaseName(), vo.getReleaseName());
        assertEquals(result.getReleaseSize(), vo.getReleaseSize());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 시스템 릴리즈 정보 저장 중 에러가 발생 한 경우
    * @title : testSaveSystemReleaseInfoFromExceptionCase
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testSaveSystemReleaseInfoFromExceptionCase(){
        ReleaseManagementDTO.Regist dto = null;
        when(mockReleaseManagemetDao.selectSystemRelease(anyString())).thenReturn(null);
        doNothing().when(mockReleaseManagemetDao).insertSystemRelease(null);
        mockReleaseManagementService.saveSystemReleaseInfo(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 파일 형식의 릴리즈 정보 저장 중 릴리즈 명 Conflict Error 테스트
    * @title : testSaveSystemReleaseFileUploadInfoFromConflictCase
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testSaveSystemReleaseFileUploadInfoFromConflictCase() throws IOException{
        ReleaseManagementDTO.Regist dto = setRegistSystemReleaseUploadInfo("conflict");
        File file = new File(RELEASE_DIR);
        FileWriter writer = null;
        try{
            writer = new FileWriter(file);
            writer.write("test"); 
        }catch (CommonException e) {
            e.printStackTrace();
        }finally {
            mockReleaseManagementService.saveSystemReleaseFileUploadInfo(dto,principal);
            if( writer != null ) {
                writer.close();
            }
        }
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 버전, Url 타입 릴리즈 정보 저장 시 릴리즈 명 Bad Request
    * @title : testCheckSystemReleaseDownloadedFileInfoByWgetFromBadRequestCase
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testCheckSystemReleaseDownloadedFileInfoByWgetFromBadRequestCase(){
        ReleaseManagementDTO.Regist dto = setReleaseDownload("notfound");
        dto.setReleaseFileName("");
        mockReleaseManagementService.checkSystemReleaseDownloadedFileInfoByWget(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 시스템 릴리즈 다운로드 중 다른 사용자가 동일 스템셀을 다운로드 하는 경우
    * @title : testCheckSystemReleaseDownloadedFileInfoByWgetFromConflictCase
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testCheckSystemReleaseDownloadedFileInfoByWgetFromConflictCase(){
        ReleaseManagementDTO.Regist dto = setReleaseDownload("nomal");
        when(mockCommonService.lockFileSet(anyString())).thenReturn(false);
        mockReleaseManagementService.checkSystemReleaseDownloadedFileInfoByWget(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 다운로드 링크 정보가 존재 하지 않을 경우
    * @title : testDownloadLinkNull
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testDownloadLinkNull(){
        ReleaseManagementDTO.Regist dto = new ReleaseManagementDTO.Regist();
        dto.setDownloadLink(null);
        mockReleaseManagementService.getSystemReleaseInfoByWget(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Url, Version 형식의 릴리즈 정보 저장 중 릴리즈 명 Conflict Error 테스트
    * @title : testRegistSystemReleaseDownloadInfoConflictError
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testRegistSystemReleaseDownloadInfoConflictError() throws IOException{
        ReleaseManagementDTO.Regist dto = setReleaseDownload("conflict");
        dto.setReleaseFileName("bosh-openstack-cpi-release-20.tgz");
        File file = new File(RELEASE_DIR);
        FileWriter writer = null;
        try{
            writer = new FileWriter(file);
            writer.write("test"); 
            when(mockCommonService.lockFileSet(anyString())).thenReturn(true);
        }catch (CommonException e) {
            e.printStackTrace();
        }finally {
            mockReleaseManagementService.checkSystemReleaseDownloadedFileInfoByWget(dto,principal);
            if( writer != null ) {
                writer.close();
            }
        }
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Version 타입의 시스템 릴리즈 다운로드 중 다른 사용자가 동일 릴리즈를 다운로드 할 경우
    * @title : testSetDownloadBaseURLByReleaseVersionFromConflicException
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testSetDownloadBaseURLByReleaseVersionFromConflicException(){
        ReleaseManagementDTO.Regist dto = setReleaseRegistInfoVersion("bosh-cpi");
        dto.setReleaseFileName("bosh-openstack-cpi-release-20.tgz");
        when(mockCommonService.lockFileSet(anyString())).thenReturn(false);
        mockReleaseManagementService.checkSystemReleaseDownloadedFileInfoByWget(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Version 타입의 시스템 릴리즈 다운로드 중 Ectd 유형 릴리즈 URL 생성 
    * @title : testSetDownloadBaseURLByReleaseVersionFromEtcd
    * @return : void
    ***************************************************/
    @Test
    public void testSetDownloadBaseURLByReleaseVersionFromEtcd(){
        ReleaseManagementDTO.Regist dto = setReleaseRegistInfoVersion("etcd");
        String etcdDownloadLink = "https://bosh.io/d/github.com/cloudfoundry-incubator/etcd-release?v=20";
        String result = mockReleaseManagementService.setDownloadBaseURLByReleaseVersion(dto);
        assertEquals(etcdDownloadLink, result);
    }
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : bosh 릴리즈 시스템 릴리즈 다운로드 중 URL 생성 
    * @title : testSetDownloadBaseURLByReleaseVersionFromBoshRelease
    * @return : void
    ***************************************************/
    @Test
    public void testSetDownloadBaseURLByReleaseVersionFromBoshRelease(){
        ReleaseManagementDTO.Regist dto = setReleaseRegistInfoVersion("bosh");
        String result = mockReleaseManagementService.setDownloadBaseURLByReleaseVersion(dto);
        String boshDownloadLink = "https://bosh.io/d/github.com/cloudfoundry/bosh?v=20";
        assertEquals(boshDownloadLink, result);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : cf 릴리즈 시스템 릴리즈 다운로드 중 URL 생성 
    * @title : testSetDownloadBaseURLByReleaseVersionFromCfRelease
    * @return : void
    ***************************************************/
    @Test
    public void testSetDownloadBaseURLByReleaseVersionFromCfRelease(){
        ReleaseManagementDTO.Regist dto = setReleaseRegistInfoVersion("cf");
        String result = mockReleaseManagementService.setDownloadBaseURLByReleaseVersion(dto);
        String cfDownloadLink = "https://bosh.io/d/github.com/cloudfoundry/cf-release?v=20";
        assertEquals(cfDownloadLink, result);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 시스템 릴리즈 삭제
     * @title : testDeleteSystemRelease
     * @return : void
    *****************************************************************/
    @Test
    public void testDeleteSystemRelease() throws IOException, SQLException{
        ReleaseManagementDTO.Delete dto = setReleaseDelete();
        FileWriter writer = null;
        try{
            writer = new FileWriter(RELEASE_DIR);
            writer.write("test"); 
        }catch (CommonException e) {
            e.printStackTrace();
        }finally {
            mockReleaseManagementService.deleteSystemRelease(dto);
            if( writer != null ) {
                writer.close();
            }
        }
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 시스템 릴리즈 업로드 정보 저장
     * @title : testSaveSystemReleaseFileUploadInfo
     * @return : void
    *****************************************************************/
    @Test 
    public void testSaveSystemReleaseFileUploadInfo() throws IOException{
        ReleaseManagementDTO.Regist dto = setRegistSystemReleaseUploadInfo("nomal");
        ReleaseManagementVO result = mockReleaseManagementService.saveSystemReleaseFileUploadInfo(dto, principal);
        assertEquals(result, null);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 릴리즈 정보 저장 요청 중 파일 정보가 존재 하지 않을 경우
    * @title : testSaveSystemReleaseUrlInfoFromEmptyFileInfo
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testSaveSystemReleaseUrlInfoFromEmptyFileInfo(){
        ReleaseManagementDTO.Regist dto = new ReleaseManagementDTO.Regist();
        dto.setReleasePathUrl("");
        dto.setReleasePathVersion("");
        mockReleaseManagementService.saveSystemReleaseUrlInfo(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : wget을 통한 릴리즈 다운로드 정보 검사
    * @title : testRegistSystemReleaseDownloadInfo
    * @return : void
    ***************************************************/
    @Test
    public void testCheckSystemReleaseDownloadedFileInfoByWget(){
        ReleaseManagementDTO.Regist dto = setReleaseDownload("nomal");
        ReleaseManagementVO vo = getRegistSystemReleaseUploadInfo("nomal");
        when(mockCommonService.lockFileSet(anyString())).thenReturn(true);
        when(mockReleaseManagemetDao.selectSystemReleaseById(anyInt())).thenReturn(vo);
        ReleaseManagementVO result = mockReleaseManagementService.checkSystemReleaseDownloadedFileInfoByWget(dto, principal);
        assertEquals(result.getId(), vo.getId());
        assertEquals(result.getReleaseFileName(), vo.getReleaseFileName());
        assertEquals(result.getReleaseName(), vo.getReleaseName());
        assertEquals(result.getReleaseSize(), vo.getReleaseSize());
        
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 릴리즈 Url 형식 다운로드 링크 정보 설정
    * @title : testSetReleaseDownloadLink
    * @return : void
    ***************************************************/
    @Test
    public void testSetReleaseDownloadLink(){
        ReleaseManagementDTO.Regist dto = setReleaseDownload("nomal");
        mockReleaseManagementService.setReleaseDownloadLink(dto);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 릴리즈 Version 형식 다운로드 링크 정보 설정
    * @title : testSetReleaseDownloadLinkFromBoshCpi
    * @return : void
    ***************************************************/
    @Test
    public void testSetReleaseDownloadLinkFromBoshCpi(){
        ReleaseManagementDTO.Regist dto = setReleaseRegistInfoVersion("bosh-cpi");
        mockReleaseManagementService.setReleaseDownloadLink(dto);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 릴리즈 정보 존재 시 목록 조회 테스트 값 설정
     * @title : getReleaseListInfo
     * @return : List<ReleaseManagementVO>
    *****************************************************************/
    private List<ReleaseManagementVO> getReleaseListInfo(String testType) {
        List<ReleaseManagementVO> releaseList = new ArrayList<ReleaseManagementVO>();
        ReleaseManagementVO vo = new ReleaseManagementVO();
        vo.setId(1);
        vo.setReleaseFileName("bosh-openstack-cpi-release-20.tgz");
        vo.setDownloadLink("downloadLink");
        if("exist".equals(testType)){
            vo.setDownloadStatus("downloading");
        }else if("null".equals(testType)){
            vo.setDownloadStatus(null);
        }else if("downloaded".equals(testType)){
            vo.setDownloadStatus("DOWNLOADED");
        }
        vo.setReleaseName("bosh-test-cpi-release");
        vo.setReleaseSize("1112 MB");
        vo.setReleaseType("cpi");
        vo.setCreateUserId("admin");
        vo.setUpdateUserId("admin");
        releaseList.add(vo);
        return releaseList;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 릴리즈 유형 정보 목록 설정
     * @title : setReleaseTypeListInfo
     * @return : List<String>
    *****************************************************************/
    public List<String> setReleaseTypeListInfo(){
        List<String> list = new ArrayList<String>();
        list.add(0, "bosh");
        list.add(1, "bosh_cpi");
        list.add(2, "cf");
        list.add(3, "diego");
        list.add(4, "etcd");
        return list;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 릴리즈 업로드 정보 설정
    * @title : setRegistSystemReleaseUploadInfo
    * @return : ReleaseManagementDTO.Regist
    ***************************************************/
    private ReleaseManagementDTO.Regist setRegistSystemReleaseUploadInfo(String errorType) {
        ReleaseManagementDTO.Regist dto = new ReleaseManagementDTO.Regist();
        dto.setId(1);
        if("precondition".equals(errorType)){
            dto.setReleaseFileName("precondition.yml");
        }else{
            dto.setReleaseFileName("bosh-openstack-cpi-release-20.tgz");
        }
        dto.setReleaseName("bosh-test-cpi-release");
        if("notfound".equals(errorType)){
            dto.setReleaseSize("0");
        }else{
            dto.setReleaseSize("123456789");
        }
        dto.setFileType("file");
        if("conflict".equals(errorType)){
            dto.setOverlayCheck("false");
        }else{
            dto.setOverlayCheck("true");
        }
        dto.setReleaseType("cpi");
        dto.setCreateUserId("admin");
        dto.setUpdateUserId("admin");
        return dto;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 시스템 릴리즈 다운로드 설정
     * @title : setReleaseDownload
     * @return : ReleaseManagementDTO.Regist
    ***************************************************/
    public ReleaseManagementDTO.Regist setReleaseDownload(String errorType) {
        ReleaseManagementDTO.Regist dto = new ReleaseManagementDTO.Regist();
        dto.setId(1);
        dto.setReleaseName("bosh-test-cpi-release/20");
        dto.setReleaseSize("123456789");
        dto.setReleaseFileName("bosh-openstack-cpi-release-20.tgz");
        dto.setFileType("url");
        if("notfound".equals(errorType)){
            dto.setReleasePathUrl("");
            dto.setReleasePathVersion("");
        }else if("badrequest".equals(errorType)){
            dto.setReleasePathUrl("https://bosh.io/d/github.com/cloudfoundry-incubator/bosh-openstack-cpi-release?v=20zz");
        }else if("urlnotfound".equals(errorType)){
            dto.setReleasePathUrl("zxczxcqwui");
        }else{
            dto.setReleasePathUrl("https://bosh.io/d/github.com/cloudfoundry-incubator/bosh-openstack-cpi-release?v=20");
        }
        if("conflict".equals(errorType)){
            dto.setOverlayCheck("false");
        }else{
            dto.setOverlayCheck("true");
        }
        dto.setReleaseType("bosh_cpi");
        dto.setCreateUserId("admin");
        dto.setUpdateUserId("admin");
        return dto;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 버전을 통한 릴리즈 다운로드 정보 설정 
     * @title : setReleaseRegistInfoVersion
     * @return : ReleaseManagementDTO.Regist
    *****************************************************************/
    public ReleaseManagementDTO.Regist setReleaseRegistInfoVersion(String releaseType) {
        ReleaseManagementDTO.Regist dto = new ReleaseManagementDTO.Regist();
        dto.setId(1);
        dto.setReleasePathUrl("https://bosh.io/d/github.com/cloudfoundry-incubator/bosh-openstack-cpi-release?v=20");
        dto.setReleaseName("bosh-test-cpi-release/20");
        dto.setDownloadLink("https://bosh.io/d/github.com/cloudfoundry-incubator/bosh-openstack-cpi-release?v=20");
        dto.setReleasePathVersion("20");
        dto.setReleaseSize("123456789");
        dto.setIaasType("openstack");
        dto.setFileType("version");
        dto.setOverlayCheck("true");
        if("bosh-cpi".equals(releaseType)){
            dto.setReleaseType("bosh_cpi");
        }else if("etcd".equals(releaseType)){
            dto.setReleaseType("etcd");
        }else if("bosh".equals(releaseType)){
            dto.setReleaseType("bosh");
        }else if("cf".equals(releaseType)){
            dto.setReleaseType("cf");
        }else if("os_conf".equalsIgnoreCase(releaseType)){
            dto.setReleaseType(releaseType);
        }
        dto.setDownloadLink("https://bosh.io/d/github.com/cloudfoundry-incubator/etcd-release?v=20");
        dto.setIaasType("openstack");
        return dto;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 파일 타입 릴리즈 정보 저장 시 리턴 값 설정
    * @title : getRegistSystemReleaseUploadInfo
    * @return : ReleaseManagementVO
    ***************************************************/
    public ReleaseManagementVO getRegistSystemReleaseUploadInfo(String errorType) {
        ReleaseManagementVO vo = new ReleaseManagementVO();
        vo.setId(1);
        vo.setReleaseFileName("bosh-openstack-cpi-release-20.tgz");
        vo.setReleaseName("bosh-test-cpi-release");
        vo.setReleaseSize("123456789");
        vo.setReleaseType("cpi");
        vo.setCreateUserId("admin");
        vo.setUpdateUserId("admin");
        return vo;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 시스템 릴리즈 삭제 입력 설정
     * @title : setReleaseDelete
     * @return : String
    ***************************************************/
    public ReleaseManagementDTO.Delete  setReleaseDelete() {
        ReleaseManagementDTO.Delete dto = new ReleaseManagementDTO.Delete();
        dto.setId("1");
        dto.setReleaseFileName("bosh-openstack-cpi-release-20.tgz");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        return dto;
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
        CommonDeployUtils.deleteFile(RELEASE_REAL_PATH, "bosh-openstack-cpi-release-20.tgz");
        
        //delte Release lock File
        CommonDeployUtils.deleteFile(RELEASE_LOCK_PATH, "bosh-openstack-cpi-release-20-download.lock");
    }
    
}
