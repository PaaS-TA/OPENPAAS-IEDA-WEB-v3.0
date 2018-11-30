package org.openpaas.ieda.deploy.web.config.stemcell.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
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
import org.openpaas.ieda.deploy.web.config.stemcell.dao.StemcellManagementDAO;
import org.openpaas.ieda.deploy.web.config.stemcell.dao.StemcellManagementVO;
import org.openpaas.ieda.deploy.web.config.stemcell.dto.StemcellManagementDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class StemcellManagementServiceUnitTest extends BaseDeployControllerUnitTest{
    
    @InjectMocks 
    StemcellManagementService mockStemcellService;
    @Mock
    CommonDeployService mockCommonService;
    @Mock
    StemcellManagementDAO mockStemcelldao;
    @Mock
    MessageSource mockMessageSource;
    
    private Principal principal = null;
    final private static String SEPARATOR = System.getProperty("file.separator");
    final private static String STEMCELL_PATH = LocalDirectoryConfiguration.getStemcellDir();
    final private static String LOCK_PATH = LocalDirectoryConfiguration.getLockDir();

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
    * @description : 스템셀 정보가 존재 하지 않을 경우 스템셀 목록 조회 테스트
    * @title : testGetStemcellListValueNull
    * @return : void
    ***************************************************/
    @Test
    public void testGetStemcellListFromValueNullCase(){
        when(mockStemcelldao.selectPublicStemcellList()).thenReturn(null);
        List<StemcellManagementVO> result = mockStemcellService.getPublicStemcellList();
        assertEquals(result, null);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 조회 된 스템셀 목록 정보 중 다운로드 상태 값이 NULL인 경우
    * @title : testGetPublicStemcellListFromNullStatusCase
    * @return : void
    ***************************************************/
    @Test
    public void testGetPublicStemcellListFromNullStatusCase(){
        List<StemcellManagementVO> stemcellList = getStemcellReturnListInfo("null");
        when(mockStemcelldao.selectPublicStemcellList()).thenReturn(stemcellList);
        List<StemcellManagementVO> result = mockStemcellService.getPublicStemcellList();
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getStemcellUrl(), stemcellList.get(0).getStemcellUrl());
        assertEquals(result.get(0).getStemcellFileName(), stemcellList.get(0).getStemcellFileName());
        assertEquals(result.get(0).getStemcellName(), stemcellList.get(0).getStemcellName());
        assertEquals(result.get(0).getStemcellVersion(), stemcellList.get(0).getStemcellVersion());
        assertEquals(result.get(0).getOsVersion(), stemcellList.get(0).getOsVersion());
        assertEquals(result.get(0).getId(), stemcellList.get(0).getId());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 조회 된 스템셀 목록 정보 중 다운로드 상태 값이 Downloaded인 경우
    * @title : testGetPublicStemcellListFromDonwloadedStatusCase
    * @return : void
    ***************************************************/
    @Test
    public void testGetPublicStemcellListFromDonwloadedStatusCase(){
        List<StemcellManagementVO> stemcellList = getStemcellReturnListInfo("downloaded");
        when(mockStemcelldao.selectPublicStemcellList()).thenReturn(stemcellList);
        List<StemcellManagementVO> result = mockStemcellService.getPublicStemcellList();
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getStemcellUrl(), stemcellList.get(0).getStemcellUrl());
        assertEquals(result.get(0).getStemcellFileName(), stemcellList.get(0).getStemcellFileName());
        assertEquals(result.get(0).getStemcellName(), stemcellList.get(0).getStemcellName());
        assertEquals(result.get(0).getStemcellVersion(), stemcellList.get(0).getStemcellVersion());
        assertEquals(result.get(0).getOsVersion(), stemcellList.get(0).getOsVersion());
        assertEquals(result.get(0).getId(), stemcellList.get(0).getId());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 동일 한 사용자가 스템셀을 업로드 할 경우 
    * @title : testSaveStemcellInfoByFilePathFromLockFileConflictCase
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testSaveStemcellInfoByFilePathFromLockFileConflictCase(){
        StemcellManagementDTO.Regist dto = setStemcellUploadInfo("nomal");
        when(mockCommonService.lockFileSet(anyString())).thenReturn(false);
        mockStemcellService.saveStemcellInfoByFilePath(dto, "Y", principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 잘못 된 확장자의 스템셀을 업로드 할 경우 테스트
    * @title : testSaveStemcellInfoByFilePathFromExtensionErrorCase
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testSaveStemcellInfoByFilePathFromExtensionErrorCase(){
        StemcellManagementDTO.Regist dto = setStemcellUploadInfo("extension");
        when(mockCommonService.lockFileSet(anyString())).thenReturn(true);
        mockStemcellService.saveStemcellInfoByFilePath(dto, "Y", principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 사이즈가 0인 스템셀 파일 업로드 테스트
    * @title : testSaveStemcellInfoByFilePathFromSizeZeroCase
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testSaveStemcellInfoByFilePathFromSizeZeroCase(){
        StemcellManagementDTO.Regist dto = setStemcellUploadInfo("zero");
        when(mockCommonService.lockFileSet(anyString())).thenReturn(true);
        mockStemcellService.saveStemcellInfoByFilePath(dto, "Y", principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 스템셀 파일이 존재하고 덮어쓰기 불가 설정 후 스템셀 업로드
    * @title : testSaveStemcellInfoByFilePathFromOverlayFalseException
    * @return :  void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testSaveStemcellInfoByFilePathFromOverlayFalseException() throws IOException{
        File file = new File(STEMCELL_PATH + SEPARATOR + "light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
        FileWriter writer = null;
        writer = new FileWriter(file);
        writer.write("test"); 
        writer.flush();
        writer.close();
        StemcellManagementDTO.Regist dto = setStemcellUploadInfo("overlay");
        when(mockCommonService.lockFileSet(anyString())).thenReturn(true);
        mockStemcellService.saveStemcellInfoByFilePath(dto, "Y", principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : File 업로드에 의해 스템셀 다운로드 정보 저장 테스트
    * @title : testSaveStemcellInfoByFilePath
    * @return : void
    ***************************************************/
    @Test
    public void testSaveStemcellInfoByFilePath(){
        StemcellManagementDTO.Regist dto = setStemcellUploadInfo("nomal");
        StemcellManagementVO vo = getStemcellRegistInfo("nomal");
        when(mockCommonService.lockFileSet(anyString())).thenReturn(true);
        when(mockStemcelldao.selectPublicStemcellById(anyInt())).thenReturn(vo);
        StemcellManagementVO result = mockStemcellService.saveStemcellInfoByFilePath(dto, "Y", principal);
        assertEquals(result.getStemcellName(), vo.getStemcellName());
        assertEquals(result.getStemcellFileName(), vo.getStemcellFileName());
        assertEquals(result.getStemcellVersion(), vo.getStemcellVersion());
        assertEquals(result.getId(), vo.getId());
        assertEquals(result.getStemcellUrl(), vo.getStemcellUrl());
        assertEquals(result.getSize(), vo.getSize());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : URL로 부터 스템셀의 다운로드 정보 확인(lock 파일 생성/덮어쓰기 체크)
    * @title : testInsertStemcellInfo
    * @return : void
    ***************************************************/
    @Test
    public void testCheckDownloadInfoOfStemcellByURL(){
        StemcellManagementDTO.Regist dto = setStemcellUploadInfo("nomal");
        StemcellManagementVO vo = getStemcellRegistInfo("nomal");
        when(mockCommonService.lockFileSet(anyString())).thenReturn(true);
        when(mockStemcelldao.selectPublicStemcellInfoByFileName(anyString())).thenReturn(null);
        when(mockStemcelldao.selectPublicStemcellById(anyInt())).thenReturn(vo);
        StemcellManagementVO result =  mockStemcellService.checkDownloadInfoOfStemcellByURL(dto, "Y", principal);
        assertEquals(result.getStemcellName(), vo.getStemcellName());
        assertEquals(result.getStemcellFileName(), vo.getStemcellFileName());
        assertEquals(result.getStemcellVersion(), vo.getStemcellVersion());
        assertEquals(result.getId(), vo.getId());
        assertEquals(result.getStemcellUrl(), vo.getStemcellUrl());
        assertEquals(result.getSize(), vo.getSize());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : URL에 의한 스템셀 정보 수정 테스트
    * @title : testCheckDownloadInfoOfStemcellByURLFromStemcellInfoUpdateCase
    * @return : void
    ***************************************************/
    @Test
    public void testCheckDownloadInfoOfStemcellByURLFromStemcellInfoUpdateCase(){
        StemcellManagementDTO.Regist dto = setStemcellUploadInfo("update");
        StemcellManagementVO vo = getStemcellRegistInfo("nomal");
        when(mockCommonService.lockFileSet(anyString())).thenReturn(true);
        when(mockStemcelldao.selectPublicStemcellInfoByFileName(anyString())).thenReturn(vo);
        when(mockStemcelldao.selectPublicStemcellById(anyInt())).thenReturn(vo);
        StemcellManagementVO result =  mockStemcellService.checkDownloadInfoOfStemcellByURL(dto, "N", principal);
        assertEquals(result.getStemcellName(), vo.getStemcellName());
        assertEquals(result.getStemcellFileName(), vo.getStemcellFileName());
        assertEquals(result.getStemcellVersion(), vo.getStemcellVersion());
        assertEquals(result.getId(), vo.getId());
        assertEquals(result.getStemcellUrl(), vo.getStemcellUrl());
        assertEquals(result.getSize(), vo.getSize());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 스템셀 정보 저장 중 에러 발생 
    * @title : testCheckDownloadInfoOfStemcellByURLFromExceptionCase
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testCheckDownloadInfoOfStemcellByURLFromExceptionCase(){
        StemcellManagementDTO.Regist dto = null;
        StemcellManagementVO vo = getStemcellRegistInfo("error");
        when(mockMessageSource.getMessage(anyString(), anyObject(), anyObject())).thenReturn("conflic_exception");
        when(mockCommonService.lockFileSet(anyString())).thenReturn(true);
        when(mockStemcelldao.selectPublicStemcellInfoByFileName(anyString())).thenReturn(vo);
        doNothing().when(mockStemcelldao).insertPublicStemcell(null);
        mockStemcellService.checkDownloadInfoOfStemcellByURL(dto, "Y", principal);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :스템셀 정보 추출을 위한 wget 실행 테스트 
     * @title : doWgetToGetPublicStemcellInfo
     * @return : void
    *****************************************************************/
    @Test
    public void doWgetToGetPublicStemcellInfo(){
        StemcellManagementDTO.Regist dto = setStemcellUploadInfo("nomal");
        dto.setFileType("URL");
        dto.setStemcellUrl("bosh.io");
        when(mockCommonService.lockFileSet(anyString())).thenReturn(true);
        mockStemcellService.doWgetToGetPublicStemcellInfo(dto, "Y", principal);
    }
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : wget을 통해 스템셀 다운로드 정보 조회 중 다운로드 링크 정보가 빈 값일 경우
     * @title : testDoWgetToGetPublicStemcellInfoFromBadRequestCase
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testDoWgetToGetPublicStemcellInfoFromBadRequestCase(){
        StemcellManagementDTO.Regist dto = setStemcellUploadInfo("nomal");
        dto.setDownloadLink(null);
        mockStemcellService.doWgetToGetPublicStemcellInfo(dto, "Y", principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : centos 유형의 스템셀 다운로드 Url 설정 테스트
    * @title : testSetStemcellUrlForWgetByCentOsType
    * @return : void
    ***************************************************/
    public void testSetStemcellUrlForWgetByCentOsType(){
        StemcellManagementDTO.Regist dto = setStemcellVersionDownloadInfo("centos");
        String result = mockStemcellService.setStemcellUrlForWget(dto);
        assertEquals(result, "https://bosh-jenkins-artifacts.s3.amazonaws.com/bosh-stemcell/aws/light-bosh-stemcell-3232.3-aws-xen-hvm-centos-7-go_agent.tgz");
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : ubuntu 유형의 스템셀 다운로드 Url 설정 테스트
    * @title : testSetStemcellUrlForWgetByUbuntuType
    * @return : 
    ***************************************************/
    public void testSetStemcellUrlForWgetByUbuntuType(){
        StemcellManagementDTO.Regist dto = setStemcellVersionDownloadInfo("ubuntu");
        String result = mockStemcellService.setStemcellUrlForWget(dto);
        assertEquals(result, "https://bosh-jenkins-artifacts.s3.amazonaws.com/bosh-stemcell/aws/light-bosh-stemcell-3232.3-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : windows 유형의 스템셀 다운로드 Url 설정 테스트
     * @title : testSetStemcellUrlForWgetByWindowsType
     * @return : void
    *****************************************************************/
    @Test
    public void testSetStemcellUrlForWgetByWindowsType(){
        StemcellManagementDTO.Regist dto = setStemcellVersionDownloadInfo("windows");
        String result = mockStemcellService.setStemcellUrlForWget(dto);
        assertEquals(result, "https://bosh-windows-stemcells-production.s3.amazonaws.com/light-bosh-stemcell-1200.15-aws-xen-hvm-windows2012R2-go_agent.tgz");
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : url 유형의 스템셀 다운로드 링크 설정 테스트
    * @title : testSetStemcellUrlForWget
    * @return : void
    ***************************************************/
    @Test
    public void testSetStemcellUrlForWget(){
        StemcellManagementDTO.Regist dto = setStemcellUrlDownloadInfo("nomal");
        String result = mockStemcellService.setStemcellUrlForWget(dto);
        assertEquals(result, "https://bosh-jenkins-artifacts.s3.amazonaws.com/bosh-stemcell/aws/light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
    }
    
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 사이즈가 0인 스템셀 파일 업로드 테스트
    * @title : testCheckDownloadInfoOfStemcellByURLFromFileSizeZeroCase
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testCheckDownloadInfoOfStemcellByURLFromFileSizeZeroCase(){
        StemcellManagementDTO.Regist dto = setStemcellUploadInfo("empty");
        when(mockCommonService.lockFileSet(anyString())).thenReturn(true);
        mockStemcellService.checkDownloadInfoOfStemcellByURL(dto, "Y", principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 버전 형식 Centos 스템셀 다운로드 
    * @title : testSaveStemcellInfoByURLFromCentosVersionTypeCase
    * @return : void
    ***************************************************/
    @Test
    public void testSaveStemcellInfoByURLFromCentosVersionTypeCase(){
        StemcellManagementDTO.Regist dto = setStemcellVersionDownloadInfo("centos");
        StemcellManagementVO vo = getStemcellRegistInfo("nomal");
        when(mockCommonService.lockFileSet(anyString())).thenReturn(true);
        when(mockStemcelldao.selectPublicStemcellById(anyInt())).thenReturn(vo);
        StemcellManagementVO result =  mockStemcellService.saveStemcellInfoByURL(dto, "Y", principal);
        assertEquals(result.getStemcellName(), vo.getStemcellName());
        assertEquals(result.getStemcellFileName(), vo.getStemcellFileName());
        assertEquals(result.getStemcellVersion(), vo.getStemcellVersion());
        assertEquals(result.getId(), vo.getId());
        assertEquals(result.getStemcellUrl(), vo.getStemcellUrl());
        assertEquals(result.getSize(), vo.getSize());
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 
     * @title : testSaveStemcellInfoByURLFromBadRequestCase
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testSaveStemcellInfoByURLFromBadRequestCase(){
        StemcellManagementDTO.Regist dto = new StemcellManagementDTO.Regist();
        mockStemcellService.saveStemcellInfoByURL(dto, "Y", principal);
    }
    
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Aws Lite 유형 스템셀 subUrl 생성 테스트
    * @title : testSetStemcellDownLoadSubUrlByVersionTypeFromAWSLightCase
    * @return : void
    ***************************************************/
    @Test
    public void testSetStemcellDownLoadSubUrlByVersionTypeFromAWSLightCase(){
        StemcellManagementDTO.Regist dto = new StemcellManagementDTO.Regist();
        dto.setLight("true");
        String resultSubUrl = mockStemcellService.setStemcellDownLoadSubUrlByVersionType(dto);
        assertEquals(resultSubUrl, "light-bosh-stemcell");
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : AWS Lite 유형을 제외 한 스템셀 subUrl 생성 테스트
    * @title : testSetStemcellDownLoadSubUrlByVersionTypeFromAWSLightExceptCase
    * @return : void
    ***************************************************/
    @Test
    public void testSetStemcellDownLoadSubUrlByVersionTypeFromAWSLightExceptCase(){
        StemcellManagementDTO.Regist dto = new StemcellManagementDTO.Regist();
        dto.setLight("false");
        String resultSubUrl = mockStemcellService.setStemcellDownLoadSubUrlByVersionType(dto);
        assertEquals(resultSubUrl, "bosh-stemcell");
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Openstack Hypervisor 조합
    * @title : testSetIaasHypervisorFromOpenstack
    * @return : void
    ***************************************************/
    @Test
    public void testSetIaasHypervisorFromOpenstack(){
        StemcellManagementDTO.Regist dto = new StemcellManagementDTO.Regist();
        dto.setIaasType("OPENSTACK");
        String resultHypervisor = mockStemcellService.setIaasHypervisor(dto);
        assertEquals(resultHypervisor, "openstack-kvm");
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : vShpere Hypervisor 조합
    * @title : testSetIaasHypervisorFromVsphere
    * @return : void
    ***************************************************/
    @Test
    public void testSetIaasHypervisorFromVsphere(){
        StemcellManagementDTO.Regist dto = new StemcellManagementDTO.Regist();
        dto.setIaasType("VSPHERE");
        String resultHypervisor = mockStemcellService.setIaasHypervisor(dto);
        assertEquals(resultHypervisor, "vsphere-esxi");
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Google Hypervisor 조합
    * @title : testSetIaasHypervisorFromGoogle
    * @return : void
    ***************************************************/
    @Test
    public void testSetIaasHypervisorFromGoogle(){
        StemcellManagementDTO.Regist dto = new StemcellManagementDTO.Regist();
        dto.setIaasType("GOOGLE");
        String resultHypervisor = mockStemcellService.setIaasHypervisor(dto);
        assertEquals(resultHypervisor, "google-kvm");
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Azure Hypervisor 조합
     * @title : testSetIaasHypervisorFromAzure
     * @return : void
    *****************************************************************/
    @Test
    public void testSetIaasHypervisorFromAzure(){
        StemcellManagementDTO.Regist dto = new StemcellManagementDTO.Regist();
        dto.setIaasType("AZURE");
        String resultHypervisor = mockStemcellService.setIaasHypervisor(dto);
        assertEquals(resultHypervisor, "azure-hyperv");
    }
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : AWS LITE 유형 Hypervisor 조합
    * @title : testSetIaasHypervisorFromAWSLight
    * @return : void
    ***************************************************/
    @Test
    public void testSetIaasHypervisorFromAWSLight(){
        StemcellManagementDTO.Regist dto = new StemcellManagementDTO.Regist();
        dto.setIaasType("AWS");
        dto.setLight("true");
        String resultHypervisor = mockStemcellService.setIaasHypervisor(dto);
        assertEquals(resultHypervisor, "aws-xen-hvm");
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : AWS Hypervisor 설정
    * @title : testSetIaasHypervisorFromAWS
    * @return : void
    ***************************************************/
    @Test
    public void testSetIaasHypervisorFromAWS(){
        StemcellManagementDTO.Regist dto = new StemcellManagementDTO.Regist();
        dto.setIaasType("AWS");
        dto.setLight("false");
        dto.setStemcellVersion("3363.09");
        String resultHypervisor = mockStemcellService.setIaasHypervisor(dto);
        assertEquals(resultHypervisor, "aws-xen-hvm");
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Aws Lite 유형 3264 버전 이상 스템셀 Base Url 생성 
    * @title : testSetStemcellDownLoadBaseUrlByVersionTypeFromLight3264MoreCase
    * @return : void
    ***************************************************/
    public void testSetStemcellDownLoadBaseUrlByVersionTypeFromLight3264MoreCase(){
        StemcellManagementDTO.Regist dto = new StemcellManagementDTO.Regist();
        dto.setStemcellVersion("3266");
        dto.setLight("true");
        dto.setIaasType("AWS");
        String resulBaseUrl = mockStemcellService.setStemcellDownLoadBaseUrlByVersionType(dto);
        assertEquals(resulBaseUrl, "https://s3.amazonaws.com/bosh-aws-light-stemcells");
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 3264 버전 이상 스템셀 Base Url 생성 
    * @title : testSetStemcellDownLoadBaseUrlByVersionTypeFrom3264MoreCase
    * @return : void
    ***************************************************/
    public void testSetStemcellDownLoadBaseUrlByVersionTypeFrom3264MoreCase(){
        StemcellManagementDTO.Regist dto = new StemcellManagementDTO.Regist();
        dto.setStemcellVersion("3266");
        dto.setLight("false");
        dto.setIaasType("OPENSTACK");
        String resulBaseUrl = mockStemcellService.setStemcellDownLoadBaseUrlByVersionType(dto);
        assertEquals(resulBaseUrl, "https://s3.amazonaws.com/bosh-core-stemcells/openstack");
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 3264 버전 이하 스템셀 Base Url 생성 
    * @title : testSetStemcellDownLoadBaseUrlByVersionTypeFromUnder3264Case
    * @return : void
    ***************************************************/
    public void testSetStemcellDownLoadBaseUrlByVersionTypeFromUnder3264Case(){
        StemcellManagementDTO.Regist dto = new StemcellManagementDTO.Regist();
        dto.setStemcellVersion("3261");
        dto.setIaasType("OPENSTACK");
        dto.setOsName("UBUNTU");
        String resulBaseUrl = mockStemcellService.setStemcellDownLoadBaseUrlByVersionType(dto);
        assertEquals(resulBaseUrl, "https://bosh-jenkins-artifacts.s3.amazonaws.com/bosh-stemcell/openstack");
        
    }
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : wget 실행 중 다운로드 조건에 맞는 Lite 유형의 스템셀 버전 추출
    * @title : testSetStemcellVersionWithWgetFrom2820LightCase
    * @return : void
    ***************************************************/
    @Test
    public void testSetStemcellVersionWithWgetFrom2820LightCase(){
        StemcellManagementDTO.Regist dto = setStemcellUrlDownloadInfo("nomal");
        String resultVersion = mockStemcellService.setStemcellVersionWithWget(dto);
        assertEquals(resultVersion, "2820");
    }
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : wget 실행 중 다운로드 조건에 맞는 스템셀 버전 추출
    * @title : testSetStemcellVersionWithWgetFromWithoutBoshIo2820Case
    * @return : void
    ***************************************************/
    @Test
    public void testSetStemcellVersionWithWgetFromWithoutBoshIo2820Case(){
        StemcellManagementDTO.Regist dto = setStemcellUrlDownloadInfo("notBoshIo");
        dto.setStemcellFileName("bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
        String resultVersion = mockStemcellService.setStemcellVersionWithWget(dto);
        assertEquals(resultVersion, "2820");
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : wget 실행 중 bosh io 에서 다운로드 조건에 맞는 스템셀 버전 추출
    * @title : testSetStemcellVersionWithWgetFromBoshIo2820Case
    * @return : void
    ***************************************************/
    @Test
    public void testSetStemcellVersionWithWgetFromBoshIo2820Case(){
        StemcellManagementDTO.Regist dto = setStemcellUrlDownloadInfo("boshio");
        dto.setStemcellFileName("bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
        String resultVersion = mockStemcellService.setStemcellVersionWithWget(dto);
        assertEquals(resultVersion, "2820");
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : wget 실행 중 bosh io 에서 다운로드 조건에 맞는 스템셀 버전 추출
    * @title : testSetStemcellVersionWithWgetFromBoshIo2820LightCase
    * @return : void
    ***************************************************/
    @Test
    public void testSetStemcellVersionWithWgetFromBoshIo2820LightCase(){
        StemcellManagementDTO.Regist dto = setStemcellUrlDownloadInfo("boshiolite");
        String resultVersion = mockStemcellService.setStemcellVersionWithWget(dto);
        assertEquals(resultVersion, "2820");
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 스템셀 Base Url 생성 중 NumberFormatException 발생
    * @title : testStemcellDownloadBaseUrlNumberFormatException
    * @return : void
    ***************************************************/
    public void testSetStemcellDownLoadBaseUrlByVersionTypeFromNumberFormatException(){
        StemcellManagementDTO.Regist dto = new StemcellManagementDTO.Regist();
        dto.setStemcellVersion("32dasdas61");
        mockStemcellService.setStemcellDownLoadBaseUrlByVersionType(dto);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Stemcell lock 파일 삭제 테스트
    * @title : testDeleteStemcellLockFile
    * @return : void
     * @throws IOException 
    ***************************************************/
    @Test
    public void testDeleteStemcellLockFile() throws IOException{
        String lock_file = "light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent-download.lock";
        File file = new File(LOCK_PATH + SEPARATOR + lock_file);
        FileWriter writer = null;
        writer = new FileWriter(file);
        writer.write("test"); 
        writer.flush();
        writer.close();
        CommonDeployUtils.deleteFile(LOCK_PATH,lock_file);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 스템셀 삭제
    * @title : testDeletePublicStemcell
    * @return : void
    ***************************************************/
    @Test
    public void testDeletePublicStemcell() throws IOException{
        StemcellManagementDTO.Delete  dto = setStemcellDeleteData();
        File file = new File(STEMCELL_PATH + SEPARATOR + "light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
        FileWriter writer = null;
        writer = new FileWriter(file);
        writer.write("test"); 
        writer.flush();
        writer.close();
        mockStemcellService.deletePublicStemcell(dto);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 스템셀 삭제 정보 설정
    * @title : setStemcellDeleteData
    * @return : StemcellManagementDTO.Delete
    ***************************************************/
    private StemcellManagementDTO.Delete setStemcellDeleteData(){
        StemcellManagementDTO.Delete dto = new StemcellManagementDTO.Delete();
        dto.setId(1);
        dto.setStemcellFileName("light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
        return dto;
    }
    
    
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 공통 로컬 스템셀 콤보
     * @title : testGetListLocalStemcellsValueNull
     * @return : void
     ***************************************************/
     @Test
     public void testGetListLocalStemcellsValueNull(){
         when(mockStemcelldao.selectLocalStemcellListByIaas("openstack")).thenReturn(null);
         List<StemcellManagementVO> result = mockStemcellService.getLocalStemcellList("openstack");
         assertEquals(result, null);
     }
     
     /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 조회 된 스템셀 목록 정보 중 다운로드 상태 값이 NULL인 경우
     * @title : testGetListLocalStemcellsDownLoadStatusValueNull
     * @return : void
     ***************************************************/
     @Test
     public void testGetListLocalStemcellsDownLoadStatusValueNull(){
         List<StemcellManagementVO> stemcellList = getStemcellReturnListInfo("null");
         when(mockStemcelldao.selectLocalStemcellListByIaas(anyString())).thenReturn(stemcellList);
         List<StemcellManagementVO> result = mockStemcellService.getLocalStemcellList("openstack");
         assertEquals(result.size(), 1);
         assertEquals(result.get(0).getStemcellUrl(), stemcellList.get(0).getStemcellUrl());
         assertEquals(result.get(0).getStemcellFileName(), stemcellList.get(0).getStemcellFileName());
         assertEquals(result.get(0).getStemcellName(), stemcellList.get(0).getStemcellName());
         assertEquals(result.get(0).getStemcellVersion(), stemcellList.get(0).getStemcellVersion());
         assertEquals(result.get(0).getOsVersion(), stemcellList.get(0).getOsVersion());
         assertEquals(result.get(0).getId(), stemcellList.get(0).getId());
     }
     
     /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 조회 된 스템셀 목록 정보 중 다운로드 상태 값이 Downloaded인 경우
     * @title : testGetListLocalStemcellsDownLoadStatusValueDownloaded
     * @return : void
     ***************************************************/
     @Test
     public void testGetListLocalStemcellsDownLoadStatusValueDownloaded(){
         List<StemcellManagementVO> stemcellList = getStemcellReturnListInfo("downloaded");
         when(mockStemcelldao.selectLocalStemcellListByIaas(anyString())).thenReturn(stemcellList);
         List<StemcellManagementVO> result = mockStemcellService.getLocalStemcellList("openstack");
         assertEquals(result.size(), 1);
         assertEquals(result.get(0).getStemcellUrl(), stemcellList.get(0).getStemcellUrl());
         assertEquals(result.get(0).getStemcellFileName(), stemcellList.get(0).getStemcellFileName());
         assertEquals(result.get(0).getStemcellName(), stemcellList.get(0).getStemcellName());
         assertEquals(result.get(0).getStemcellVersion(), stemcellList.get(0).getStemcellVersion());
         assertEquals(result.get(0).getOsVersion(), stemcellList.get(0).getOsVersion());
         assertEquals(result.get(0).getId(), stemcellList.get(0).getId());
     }
     
     /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : bosh.io 스템셀 다운로드 정보 등록 요청
    * @title : testRegistPublicStemcellDownLoadBoshIoUrl
    * @return : void
    ***************************************************/
    @Test
     public void testRegistPublicStemcellDownLoadBoshIoUrl(){
        StemcellManagementDTO.Regist dto = setStemcellUrlDownloadInfo("boshio");
        when(mockCommonService.lockFileSet(anyString())).thenReturn(true);
        mockStemcellService.checkDownloadInfoOfStemcellByURL(dto, "Y", principal);
     }
    
    /***************************************************
   * @project : Paas 플랫폼 설치 자동화
   * @description : bosh.io Lite 스템셀 다운로드 정보 등록 요청
   * @title : testRegistPublicStemcellDownLoadBoshIoLiteUrl
   * @return : void
   ***************************************************/
   @Test
    public void testRegistPublicStemcellDownLoadBoshIoLiteUrl(){
       StemcellManagementDTO.Regist dto = setStemcellUrlDownloadInfo("boshiolite");
       when(mockCommonService.lockFileSet(anyString())).thenReturn(true);
       mockStemcellService.checkDownloadInfoOfStemcellByURL(dto, "Y", principal);
    }
   
   /***************************************************
  * @project : Paas 플랫폼 설치 자동화
  * @description : 버전 유형 Lite가 아닌 스템셀 다운로드 정보 등록 요청
  * @title : testRegistPublicStemcellDownLoadIfNotLite
  * @return : void
  ***************************************************/
   @Test 
   public void testRegistPublicStemcellDownLoadIfNotLite(){
       StemcellManagementDTO.Regist dto = setStemcellVersionDownloadInfo("ifNotLite");
       when(mockCommonService.lockFileSet(anyString())).thenReturn(true);
       mockStemcellService.checkDownloadInfoOfStemcellByURL(dto, "Y", principal);
   }
   
   /***************************************************
  * @project : Paas 플랫폼 설치 자동화
  * @description : 스템셀 다운로드 요청 중 스템셀 파일이 존재 하고 덮어 쓰기 체크가 안되어 있을 경우
  * @title : testRegistPublicStemcellDownladFileExistAndOverlayCheckFlase
  * @return : void
  ***************************************************/
   @Test(expected=CommonException.class)
   public void testRegistPublicStemcellDownladFileExistAndOverlayCheckFlase() throws IOException{
       StemcellManagementDTO.Regist dto = setStemcellUrlDownloadInfo("overlay");
       File file = new File(STEMCELL_PATH + SEPARATOR + "light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
       FileWriter writer = null;
       writer = new FileWriter(file);
       writer.write("test"); 
       writer.flush();
       writer.close();
       mockStemcellService.checkDownloadInfoOfStemcellByURL(dto, "Y", principal);
   }
   
   /***************************************************
  * @project : Paas 플랫폼 설치 자동화
  * @description : 스템셀 다운로드 Url이 잘못되었을 경우
  * @title : testRegistPublicStemcellDownladUrlBadRequest
  * @return : void
  ***************************************************/
   @Test(expected=CommonException.class)
   public void testRegistPublicStemcellDownladUrlBadRequest() throws IOException{
       StemcellManagementDTO.Regist dto = setStemcellUrlDownloadInfo("badrequest");
       mockStemcellService.checkDownloadInfoOfStemcellByURL(dto, "Y", principal);
   }
   
   /***************************************************
  * @project : Paas 플랫폼 설치 자동화
  * @description : 다른 사용자가 동일한 스템셀을 다운로드 중일 경우
  * @title : testRegistPublicStemcellDownladConflict
  * @return : void
  ***************************************************/
   @Test(expected=CommonException.class)
   public void testRegistPublicStemcellDownladConflict() throws IOException{
       StemcellManagementDTO.Regist dto = setStemcellUrlDownloadInfo("nomal");
       when(mockCommonService.lockFileSet(anyString())).thenReturn(false);
       mockStemcellService.checkDownloadInfoOfStemcellByURL(dto, "Y", principal);
   }
   
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 스템셀 Url 형식 다운로드 정보 설정
    * @title : setStemcellUrlDownloadInfo
    * @return : StemcellManagementDTO.Regist
    ***************************************************/
    private StemcellManagementDTO.Regist setStemcellVersionDownloadInfo(String type) {
        StemcellManagementDTO.Regist dto = new StemcellManagementDTO.Regist();
        dto.setId(1);
        dto.setStemcellUrl("testurl");
        dto.setDownloadLink("link");
        dto.setStemcellName("testStemcellName");
        if("windows".equals(type)){
            dto.setStemcellFileName("light-bosh-stemcell-1200.15-aws-xen-hvm-windows2012R2-go_agent.tgz");
        }else{
            dto.setStemcellFileName("light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
        }
        if("windows".equals(type)){
            dto.setStemcellVersion("1200.15");
        }else{
            dto.setStemcellVersion("3232.3");
        }
        dto.setStemcellSize("123456789");
        if("ubuntu".equals(type)){
            dto.setOsName("UBUNTU");
            dto.setOsVersion("TRUSTY");
        }else if("centos".equals(type)){
            dto.setOsName("CENTOS");
            dto.setOsVersion("7.X");
        }else{
            dto.setOsName("WINDOWS");
            dto.setOsVersion("2012R2");
        }
        dto.setIaasType("AWS");
        if("ifNotLite".equals(type)){
            dto.setLight("false");
        }else{
            dto.setLight("true");
        }
        dto.setFileType("version");
        dto.setOverlayCheck("true");
        dto.setCreateUserId("tester");
        dto.setUpdateUserId("tester");
        return dto;
    }

    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 스템셀 Url 형식 다운로드 정보 설정
    * @title : setStemcellUrlDownloadInfo
    * @return : StemcellManagementDTO.Regist
    ***************************************************/
    public StemcellManagementDTO.Regist setStemcellUrlDownloadInfo(String type) {
        StemcellManagementDTO.Regist dto = new StemcellManagementDTO.Regist();
        dto.setRecid(1);
        dto.setId(1);
        if("boshio".equals(type)){
            dto.setStemcellUrl("https://bosh.io/d/stemcells/bosh-aws-xen-centos-7-go_agent?v=3312.7");
            dto.setLight("false");
        }else if("boshiolite".equals(type)){
            dto.setStemcellUrl("https://bosh.io/d/stemcells/bosh-aws-xen-hvm-centos-7-go_agent?v=3363.22");
            dto.setLight("true");
        }else if("badrequest".equals(type)){
            dto.setStemcellUrl("badRequest");
            dto.setLight("true");
        }else if("notBoshIo".equals(type)){
            dto.setStemcellUrl("https://s3.amazonaws.com/bosh-core-stemcells/openstack/bosh-stemcell-3363.10-openstack-kvm-ubuntu-trusty-go_agent.tgz");
            dto.setLight("false");
        }else{
            dto.setStemcellUrl("https://bosh-jenkins-artifacts.s3.amazonaws.com/bosh-stemcell/aws/light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
            dto.setLight("true");
        }
        dto.getStemcellName();
        dto.setStemcellName("testStemcellFile");
        dto.setStemcellFileName("light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
        dto.setStemcellVersion("2820");
        dto.setStemcellSize("123456789");
        dto.setOsName("UBUNTU");
        dto.setIaasType("AWS");
        dto.setOsVersion("TRUSTY");
        dto.setFileType("url");
        if("overlay".equals(type)){
            dto.setOverlayCheck("false");
        }else{
            dto.setOverlayCheck("true");
        }
        dto.getDownloadLink();
        dto.getUpdateUserId();
        dto.getCreateUserId();
        dto.getCreateDate();
        dto.getUpdateDate();
        dto.setCreateUserId("tester");
        dto.setUpdateUserId("tester");
        return dto;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
    * @description : 스템셀 목록 조회 리턴 값 설정
    * @title : getStemcellReturnListInfo
    * @return : List<StemcellManagementVO>
    ***************************************************/
    private List<StemcellManagementVO> getStemcellReturnListInfo(String type) {
        List<StemcellManagementVO> list = new ArrayList<StemcellManagementVO>();
        StemcellManagementVO vo = new StemcellManagementVO();
        vo.setId(1);
        vo.setStemcellUrl("https://bosh-jenkins-artifacts.s3.amazonaws.com/bosh-stemcell/aws/light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
        vo.setStemcellName("testStemcellFile");
        vo.setStemcellFileName("light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
        vo.setStemcellVersion("2820");
        vo.setOsVersion("TRUSTY");
        vo.setCreateUserId("tester");
        vo.setUpdateUserId("tester");
        if("null".equals(type)){
            vo.setDownloadStatus("");
        }else if("downloaded".equals(type)){
            vo.setDownloadStatus("DOWNLOADED");
        }
        list.add(vo);
        return list;
    }
    
    /***************************************************
    * @param string 
     * @project : Paas 플랫폼 설치 자동화
    * @description : 스템셀 상세 조회 리턴 값 설정
    * @title : getStemcellRegistInfo
    * @return : StemcellManagementVO
    ***************************************************/
    private StemcellManagementVO getStemcellRegistInfo(String type){
        StemcellManagementVO vo = new StemcellManagementVO();
        if("error".equals(type)){
            vo.setId(null);
        }else{
            vo.setId(1);
        }
        vo.setStemcellUrl("https://bosh-jenkins-artifacts.s3.amazonaws.com/bosh-stemcell/aws/light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
        vo.setStemcellName("testStemcellFile");
        vo.setStemcellFileName("light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
        vo.setStemcellVersion("2820");
        vo.setOsVersion("TRUSTY");
        vo.setCreateUserId("tester");
        vo.setUpdateUserId("tester");
        return vo;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 스템셀 업로드 정보 설정
    * @title : setStemcellUploadInfo
    * @return : StemcellManagementDTO.Regist
    ***************************************************/
    private StemcellManagementDTO.Regist setStemcellUploadInfo(String type) {
        StemcellManagementDTO.Regist dto = new StemcellManagementDTO.Regist();
        dto.setId(1);
        dto.setStemcellName("testStemcellFile");
        if("extension".equals(type)){
            dto.setStemcellFileName("error.error");
        }else if("update".equals(type)){
            dto.setStemcellFileName("bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
        }else{
            dto.setStemcellFileName("light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
        }
        if("empty".equals(type)){
            dto.setStemcellUrl("");
            dto.setStemcellVersion("");
        }else{
            dto.setStemcellUrl("testurl");
            dto.setStemcellVersion("1111");
        }
        if("zero".equals(type)){
            dto.setStemcellSize("0");
        }else if("empty".equals(type)){
            dto.setStemcellSize("");
        }else{
            dto.setStemcellSize("123456789");
        }
        dto.setOsName("UBUNTU");
        dto.setIaasType("OPENSTACK");
        dto.setOsVersion("TRUSTY");
        if("update".equals(type)){
            dto.setLight("false");
        }else{
            dto.setLight("true");
        }
        
        if("overlay".equals(type)){
            dto.setOverlayCheck("false");
        }else{
            dto.setOverlayCheck("true");
        }
        dto.setFileType("file");
        dto.setCreateUserId("tester");
        dto.setUpdateUserId("tester");
        dto.setDownloadLink("https://bosh-jenkins-artifacts.s3.amazonaws.com/bosh-stemcell/aws/light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
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
        //delete deployment stemcell File
        CommonDeployUtils.deleteFile(STEMCELL_PATH, "light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
        //delete stemcell lock file
        CommonDeployUtils.deleteFile(LOCK_PATH, "light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent-download.lock");
        //vo gettest/setter coverage
        StemcellManagementVO vo = new StemcellManagementVO();
        vo.getRecid();
        vo.setRecid(1);

        vo.setSize("");
        vo.getOs();
        vo.setOs("");
        
        vo.getIaas();
        vo.setIaas("");
        
        vo.getIsExisted();
        vo.setIsExisted("");
        
        vo.getIsDose();
        vo.setIsDose("");
        
        vo.getDownloadLink();
        vo.setDownloadLink("");
        
        vo.getCreateUserId();
        vo.setCreateDate(null);
        vo.getCreateDate();
        vo.setCreateDate(new Date());
        vo.getCreateDate();
        
        vo.getUpdateUserId();
        vo.setUpdateDate(null);
        vo.getUpdateDate();
        vo.setUpdateDate(new Date());
        vo.getUpdateDate();
    }
}
