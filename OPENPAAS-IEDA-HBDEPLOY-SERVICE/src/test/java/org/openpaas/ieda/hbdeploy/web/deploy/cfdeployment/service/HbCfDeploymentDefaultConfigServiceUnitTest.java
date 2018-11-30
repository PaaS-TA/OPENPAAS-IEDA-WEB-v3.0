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
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentDefaultConfigDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentDefaultConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dto.HbCfDeploymentDefaultConfigDTO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service.HbCfDeploymentDefaultConfigService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class HbCfDeploymentDefaultConfigServiceUnitTest extends BaseHbDeployControllerUnitTest{
    
    @InjectMocks private HbCfDeploymentDefaultConfigService mockHbCfDeploymentDefaultConfigService;
    @Mock private MessageSource mockMessageSource;
    @Mock private HbCfDeploymentDefaultConfigDAO mockHbCfDeploymentDefaultConfigDAO;
    
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
     * @description : 이종 CF DEPLOYMENT 기본 정보 목록 조회 Unit Test
     * @title : testGetDefaultConfigInfoList
     * @return : void
    *****************************************************************/
    @Test
    public void testGetDefaultConfigInfoList(){
        List<HbCfDeploymentDefaultConfigVO> expectList = setCfDefaultConfigList();
        when(mockHbCfDeploymentDefaultConfigDAO.selectHbCfDeploymentDefaultConfigInfoList()).thenReturn(expectList);
        List<HbCfDeploymentDefaultConfigVO> resultList = mockHbCfDeploymentDefaultConfigService.getDefaultConfigInfoList();
        assertEquals(expectList.size(), resultList.size());
        assertEquals(expectList.get(0).getCfDbType(), resultList.get(0).getCfDbType());
        assertEquals(expectList.get(0).getCfDeploymentVersion(), resultList.get(0).getCfDeploymentVersion());
        assertEquals(expectList.get(0).getDefaultConfigName(), resultList.get(0).getDefaultConfigName());
        assertEquals(expectList.get(0).getIaasType(), resultList.get(0).getIaasType());
        assertEquals(expectList.get(0).getDomain(), resultList.get(0).getDomain());
        assertEquals(expectList.get(0).getDomainOrganization(), resultList.get(0).getDomainOrganization());
        assertEquals(expectList.get(0).getId(), resultList.get(0).getId());
    }
    
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 기본 정보 저장 Unit Test
     * @title : testInsertDefaultConfigInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testInsertDefaultConfigInfo(){
        HbCfDeploymentDefaultConfigDTO dto = setDefaultConfigInfo("insert");
        when(mockHbCfDeploymentDefaultConfigDAO.selectHbCfDeploymentDefaultConfigByName(anyString())).thenReturn(0);
        mockHbCfDeploymentDefaultConfigService.saveDefaultConfigInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 기본 정보 저장 Unit Test
     * @title : testUpdateDefaultConfigInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testUpdateDefaultConfigInfo(){
        HbCfDeploymentDefaultConfigDTO dto = setDefaultConfigInfo("update");
        HbCfDeploymentDefaultConfigVO vo = setCfDefaultConfig();
        when(mockHbCfDeploymentDefaultConfigDAO.selectHbCfDeploymentDefaultConfigByName(anyString())).thenReturn(0);
        when(mockHbCfDeploymentDefaultConfigDAO.selectHbCfDeploymentDefaultConfigInfo(anyInt(),anyString())).thenReturn(vo);
        mockHbCfDeploymentDefaultConfigService.saveDefaultConfigInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 기본 정보 저장 Exception Unit Test
     * @title : testSaveDefaultConfigInfoConfilct
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testSaveDefaultConfigInfoConfilct(){
        HbCfDeploymentDefaultConfigDTO dto = setDefaultConfigInfo("insert");
        when(mockMessageSource.getMessage(anyString(), anyObject(), anyObject())).thenReturn("conflic_exception");
        when(mockHbCfDeploymentDefaultConfigDAO.selectHbCfDeploymentDefaultConfigByName(anyString())).thenReturn(1);
        mockHbCfDeploymentDefaultConfigService.saveDefaultConfigInfo(dto, principal);
    }
    
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 기본 정보 삭제 Unit Test
     * @title : testDeleteDefaultConfigInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testDeleteDefaultConfigInfo(){
        HbCfDeploymentDefaultConfigDTO dto = new HbCfDeploymentDefaultConfigDTO();
        dto.setId(1);
        dto.setIaasType("Openstack");
        mockHbCfDeploymentDefaultConfigService.deleteDefaultConfigInfo(dto, principal);
    }
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 기본 정보 삭제 Exception Unit Test
     * @title : testDeleteDefaultConfigInfoEmpty
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testDeleteDefaultConfigInfoEmpty(){
        HbCfDeploymentDefaultConfigDTO dto = new HbCfDeploymentDefaultConfigDTO();
        dto.setIaasType("Openstack");
        when(mockMessageSource.getMessage(anyString(), anyObject(), anyObject())).thenReturn("empty");
        mockHbCfDeploymentDefaultConfigService.deleteDefaultConfigInfo(dto, principal);
    }
    
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 기본 정보 저장 값 설정
     * @title : testSveDefaultConfigInfo
     * @return : void
    *****************************************************************/
    private HbCfDeploymentDefaultConfigDTO setDefaultConfigInfo(String type) {
        HbCfDeploymentDefaultConfigDTO dto = new HbCfDeploymentDefaultConfigDTO();
        dto.setCfDbType("postgres");
        dto.setCfDeploymentVersion("cf-deployment/2.7.0");
        dto.setDefaultConfigName("default-config");
        dto.setDomain("cf.com");
        dto.setDomainOrganization("paas");
        dto.setIaasType("Openstack");
        if("update".equalsIgnoreCase(type)){
            dto.setId(1);
        }
        
        return dto;
    }
    
    private HbCfDeploymentDefaultConfigVO setCfDefaultConfig(){
        HbCfDeploymentDefaultConfigVO vo = new HbCfDeploymentDefaultConfigVO();
        vo.setCfDbType("postgres");
        vo.setCfDeploymentVersion("cf-deployment/2.7.0");
        vo.setDefaultConfigName("defalut-config");
        vo.setDeploymentName("cf-deployment");
        vo.setDomain("cf.com");
        vo.setDomainOrganization("paas");
        vo.setIaasType("Openstack");
        vo.setId(1);
        return vo;
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 기본 정보 목록 조회 값 설정
     * @title : setCfDefaultConfigList
     * @return : void
    *****************************************************************/
    private List<HbCfDeploymentDefaultConfigVO> setCfDefaultConfigList() {
        List<HbCfDeploymentDefaultConfigVO> list = new ArrayList<HbCfDeploymentDefaultConfigVO>();
        HbCfDeploymentDefaultConfigVO vo = new HbCfDeploymentDefaultConfigVO();
        vo.setCfDbType("postgres");
        vo.setCfDeploymentVersion("cf-deployment/2.7.0");
        vo.setDefaultConfigName("defalut-config");
        vo.setDeploymentName("cf-deployment");
        vo.setDomain("cf.com");
        vo.setDomainOrganization("paas");
        vo.setIaasType("Openstack");
        vo.setId(1);
        list.add(vo);
        return list;
    }
    
}
