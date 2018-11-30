package org.openpass.ieda.hbdeploy.web.config.release.service;

import java.security.Principal;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.hbdeploy.web.config.release.dao.HbReleaseManagementDAO;
import org.openpaas.ieda.hbdeploy.web.config.release.dto.HbReleaseManagementDTO;
import org.openpaas.ieda.hbdeploy.web.config.release.service.HbReleaseManagementUploadService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class HbReleaseManagementUploadServiceUnitTest {

    @InjectMocks
    HbReleaseManagementUploadService mockHbReleaseManagementUploadService;
    @Mock
    HbReleaseManagementDAO mockHbReleasemanagementDao;
    
    private Principal principal = null;
    
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
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 시큐리티 토큰 생성
    * @title : getLoggined
    * @return : Principal
    ***************************************************/
    public Principal getLoggined() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken("admin", "admin");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(auth);
        securityContext.getAuthentication().getPrincipal();
        return auth;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 릴리즈 정보 저장
     * @title : testSaveHybridRelese
     * @return : void
    *****************************************************************/
    @Test
    public void testSaveHybridRelese(){
        HbReleaseManagementDTO.Regist dto = setReleaseRegistInfoUrl();
        mockHbReleaseManagementUploadService.saveHybridRelease(dto, principal);
    }
    
    /***************************************************
     * @param string 
     * @project : Paas 플랫폼 설치 자동화
     * @description : URL을 통한 릴리즈 정보 설정 
     * @title : setReleaseRegistInfoUrl
     * @return : String
    ***************************************************/
    public HbReleaseManagementDTO.Regist setReleaseRegistInfoUrl(){
        HbReleaseManagementDTO.Regist dto = new HbReleaseManagementDTO.Regist();
        dto.setId(1);
        dto.setReleasePathUrl("https://bosh.io/d/github.com/cloudfoundry-incubator/bosh-openstack-cpi-release?v=38");
        dto.setDownloadLink("https://bosh.io/d/github.com/cloudfoundry-incubator/bosh-openstack-cpi-release?v=38");
        dto.setReleaseName("bosh-test-cpi-release/38");
        dto.setReleaseFileName("bosh-openstack-cpi-release-38.tgz");
        dto.setReleasePathVersion("38");
        dto.setReleaseSize("123456789");
        dto.setFileType("url");
        dto.setOverlayCheck("true");
        dto.setReleaseType("bosh_cpi");
        dto.setIaasType("openstack");
        dto.setCreateUserId("tester");
        dto.setUpdateUserId("tester");
        return dto;
    }
    
}
