package org.openpaas.ieda.deploy.web.config.stemcell.service;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.security.Principal;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.web.common.base.BaseDeployControllerUnitTest;
import org.openpaas.ieda.deploy.web.config.stemcell.dao.StemcellManagementDAO;
import org.openpaas.ieda.deploy.web.config.stemcell.dto.StemcellManagementDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class StemcellManagementUploadServiceUnitTest extends BaseDeployControllerUnitTest{
    @InjectMocks
    StemcellManagementUploadService StemcellManagementUploadService;
    @Mock
    StemcellManagementDAO mockStemcellDao;
    @Mock
    MessageSource mockMessageSource;
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
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 스템셀 업로드 후 정보 저장
    * @title : testSavePublicStemcell
    * @return : void
    ***************************************************/
    @Test
    public void testSavePublicStemcell(){
        StemcellManagementDTO.Regist dto = setReleaseDownloadInfo();
        StemcellManagementUploadService.saveStemcellInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 파일 업로드 후 정보 저장 중 에러가 발생 한 경우
     * @title : testSaveStemcellInfoFromBadRequestCase
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testSaveStemcellInfoFromBadRequestCase(){
        when(mockMessageSource.getMessage(anyString(), anyObject(), anyObject())).thenReturn("null");
        StemcellManagementDTO.Regist dto = null;
        StemcellManagementUploadService.saveStemcellInfo(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 릴리즈 다운로드 정보 설정
    * @title : setReleaseDownloadInfo
    * @return : StemcellManagementDTO.Regist
    ***************************************************/
    private StemcellManagementDTO.Regist setReleaseDownloadInfo() {
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
        dto.setOverlayCheck("true");
        dto.setCreateUserId("tester");
        dto.setUpdateUserId("tester");
        return dto;
    }
}
