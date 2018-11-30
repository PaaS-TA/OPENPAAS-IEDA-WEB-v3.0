package org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.hbdeploy.web.common.base.BaseHbDeployControllerUnitTest;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentCredentialConfigDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentCredentialConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dto.HbCfDeploymentCredentialConfigDTO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service.HbCfDeploymentCredentialService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class HbCfDeploymentCredentialServiceUnitTest extends BaseHbDeployControllerUnitTest{
    
    @InjectMocks HbCfDeploymentCredentialService mockHbCfDeploymentCredentialService;
    @Mock MessageSource mockMessageSource;
    @Mock HbCfDeploymentCredentialConfigDAO mockHbCfDeploymentCredentialConfigDAO;
    
    private Principal principal = null;
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 유닛 테스트가 실행되기 전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        principal = getLoggined();
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 인증서 정보 목록 조회 Unit Test
     * @title : testGetDefaultConfigInfoList
     * @return : void
    *****************************************************************/
    @Test
    public void testGetDefaultConfigInfoList(){
        List<HbCfDeploymentCredentialConfigVO> expectList = setCfCrednetialConfigList();
        when(mockHbCfDeploymentCredentialConfigDAO.selectHbCfDeploymentCredentialConfigInfoList()).thenReturn(expectList);
        List<HbCfDeploymentCredentialConfigVO> resultList = mockHbCfDeploymentCredentialService.getCredentialConfigInfoList();
        assertEquals(expectList.size(), resultList.size());
        assertEquals(expectList.get(0).getId(), resultList.get(0).getId());
        assertEquals(expectList.get(0).getCredentialConfigName(), resultList.get(0).getCredentialConfigName());
        assertEquals(expectList.get(0).getCredentialConfigKeyFileName(), resultList.get(0).getCredentialConfigKeyFileName());
        assertEquals(expectList.get(0).getDomain(), resultList.get(0).getDomain());
        assertEquals(expectList.get(0).getIaasType(), resultList.get(0).getIaasType());
        assertEquals(expectList.get(0).getReleaseName(), resultList.get(0).getReleaseName());
        assertEquals(expectList.get(0).getReleaseVersion(), resultList.get(0).getReleaseVersion());
        assertEquals(expectList.get(0).getCity(), resultList.get(0).getCity());
        assertEquals(expectList.get(0).getCountryCode(), resultList.get(0).getCountryCode());
        assertEquals(expectList.get(0).getCompany(), resultList.get(0).getCompany());
        assertEquals(expectList.get(0).getEmail(), resultList.get(0).getEmail());
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 인증서 정보 저장 Unit Test
     * @title : testInsertCredentialConfigInfo
     * @return : void
    *****************************************************************/
    public void testInsertCredentialConfigInfo(){
        HbCfDeploymentCredentialConfigDTO dto = setCfCredentialConfigInfo("insert");
        when(mockHbCfDeploymentCredentialConfigDAO.selectHbCfDeploymentCredentialConfigByName(anyString())).thenReturn(0);
        mockHbCfDeploymentCredentialService.saveCredentialConfigInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 인증서 정보 저장 Unit Test
     * @title : testUpdateCredentialConfigInfo
     * @return : void
    *****************************************************************/
    public void testUpdateCredentialConfigInfo(){
        HbCfDeploymentCredentialConfigDTO dto = setCfCredentialConfigInfo("update");
        HbCfDeploymentCredentialConfigVO vo = setCfCrednetialConfig();
        when(mockHbCfDeploymentCredentialConfigDAO.selectHbCfDeploymentCredentialConfigByName(anyString())).thenReturn(0);
        when(mockHbCfDeploymentCredentialConfigDAO.selectHbCfDeploymentCredentialConfigInfo(anyInt())).thenReturn(vo);
        mockHbCfDeploymentCredentialService.saveCredentialConfigInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 인증서 정보 저장 Exception Unit Test
     * @title : testUpdateCredentialConfigInfoConflict
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testUpdateCredentialConfigInfoConflict(){
        HbCfDeploymentCredentialConfigDTO dto = setCfCredentialConfigInfo("insert");
        when(mockMessageSource.getMessage(anyString(), anyObject(), anyObject())).thenReturn("conflic_exception");
        when(mockHbCfDeploymentCredentialConfigDAO.selectHbCfDeploymentCredentialConfigByName(anyString())).thenReturn(1);
        mockHbCfDeploymentCredentialService.saveCredentialConfigInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 인증서 정보 삭제 Unit Test
     * @title : testDeleteCredentialConfigInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testDeleteCredentialConfigInfo(){
        HbCfDeploymentCredentialConfigDTO dto = new HbCfDeploymentCredentialConfigDTO();
        dto.setId(1);
        dto.setIaasType("Openstack");
        mockHbCfDeploymentCredentialService.deleteCredentialConfigInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 인증서 정보 삭제 Exception Unit Test
     * @title : testDeleteCredentialConfigInfoNull
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testDeleteCredentialConfigInfoNull(){
        HbCfDeploymentCredentialConfigDTO dto = new HbCfDeploymentCredentialConfigDTO();
        when(mockMessageSource.getMessage(anyString(), anyObject(), anyObject())).thenReturn("null");
        mockHbCfDeploymentCredentialService.deleteCredentialConfigInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 인증서 정보 값 설정
     * @title : setCfCrednetialConfig
     * @return : void
    *****************************************************************/
    private HbCfDeploymentCredentialConfigVO setCfCrednetialConfig() {
        HbCfDeploymentCredentialConfigVO vo = new HbCfDeploymentCredentialConfigVO();
        vo.setCity("seoul");
        vo.setCompany("paas-ta");
        vo.setCredentialConfigKeyFileName("creds.yml");
        vo.setCountryCode("seoul");
        vo.setDomain("cf.com");
        vo.setEmail("leedh@cloud4u.co.kr");
        vo.setReleaseVersion("2.7.0");
        vo.setReleaseName("cfdeployment");
        vo.setCredentialConfigName("credential-config");
        vo.setIaasType("Openstack");
        vo.setId(1);
        return vo;
    }
    
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 정보 저장 정보 값 설정
     * @title : setCfCrednetialConfigList
     * @return : void
    *****************************************************************/
    private HbCfDeploymentCredentialConfigDTO setCfCredentialConfigInfo(String type) {
        HbCfDeploymentCredentialConfigDTO dto = new HbCfDeploymentCredentialConfigDTO();
        dto.setCfDeploymentVersion("2.7.0");
        dto.setCity("seoul");
        dto.setCompany("paasta");
        dto.setCountryCode("korea");
        dto.setCredentialConfigKeyFileName("creds.yml");
        dto.setCredentialConfigName("credential-config");
        dto.setDomain("cf.com");
        if("update".equalsIgnoreCase(type)){
            dto.setId(1);
        }
        dto.setIaasType("Openstack");
        dto.setReleaseName("cf-deployment");
        dto.setReleaseVersion("2.7.0");
        return dto;
    }

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 인증서 목록 정보 값 설정
     * @title : setCfCrednetialConfigList
     * @return : void
    *****************************************************************/
    private List<HbCfDeploymentCredentialConfigVO> setCfCrednetialConfigList() {
        List<HbCfDeploymentCredentialConfigVO> list = new ArrayList<HbCfDeploymentCredentialConfigVO>();
        HbCfDeploymentCredentialConfigVO vo = new HbCfDeploymentCredentialConfigVO();
        vo.setCity("seoul");
        vo.setCompany("paas-ta");
        vo.setCredentialConfigKeyFileName("creds.yml");
        vo.setCountryCode("seoul");
        vo.setDomain("cf.com");
        vo.setEmail("leedh@cloud4u.co.kr");
        vo.setReleaseVersion("2.7.0");
        vo.setReleaseName("cfdeployment");
        vo.setCredentialConfigName("credential-config");
        vo.setIaasType("Openstack");
        vo.setId(1);
        list.add(vo);
        return list;
    }
}
