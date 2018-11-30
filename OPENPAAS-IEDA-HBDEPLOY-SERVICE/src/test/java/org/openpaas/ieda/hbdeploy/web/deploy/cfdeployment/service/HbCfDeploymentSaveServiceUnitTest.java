package org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service;
import static org.mockito.Matchers.anyInt;
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
import org.openpaas.ieda.hbdeploy.web.common.base.BaseHbDeployControllerUnitTest;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentVO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dto.HbCfDeploymentDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class HbCfDeploymentSaveServiceUnitTest extends BaseHbDeployControllerUnitTest {
    
    @InjectMocks HbCfDeploymentSaveService mockHbCfDeploymentSaveService;
    @Mock MessageSource mockMessageSource;
    @Mock HbCfDeploymentDAO mockHbCfDeploymentDAO;
    
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
     * @description : 이종 CF DEPLOYMENT 정보 저장 Unit Test
     * @title : testInsertCfdeploymentConfigInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testInsertCfdeploymentConfigInfo(){
        HbCfDeploymentDTO dto = setCfConfigInfo("insert");
        when(mockHbCfDeploymentDAO.selectCfDeploymentConfigByName(anyString())).thenReturn(0);
        mockHbCfDeploymentSaveService.saveCfdeploymentConfigInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 정보 저장 Unit Test
     * @title : testUpdateCfdeploymentConfigInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testUpdateCfdeploymentConfigInfo(){
        HbCfDeploymentDTO dto = setCfConfigInfo("update");
        HbCfDeploymentVO vo = setCfConfig();
        when(mockHbCfDeploymentDAO.selectCfDeploymentConfigByName(anyString())).thenReturn(0);
        when(mockHbCfDeploymentDAO.selectCfDeploymentConfigInfo(anyInt())).thenReturn(vo);
        mockHbCfDeploymentSaveService.saveCfdeploymentConfigInfo(dto, principal);
    }
    
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 정보 저장 Exception Unit Test
     * @title : testSavetCfdeploymentConfigInfoConflict
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testSavetCfdeploymentConfigInfoConflict(){
        HbCfDeploymentDTO dto = setCfConfigInfo("insert");
        when(mockHbCfDeploymentDAO.selectCfDeploymentConfigByName(anyString())).thenReturn(1);
        when(mockMessageSource.getMessage(anyString(), anyObject(), anyObject())).thenReturn("conflic_exception");
        mockHbCfDeploymentSaveService.saveCfdeploymentConfigInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 정보 삭제 Unit Test
     * @title : testDleteCfdeploymentConfigInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testDleteCfdeploymentConfigInfo(){
    	HbCfDeploymentDTO dto = setCfConfigInfo("update");
    	mockHbCfDeploymentSaveService.deleteCfdeploymentConfigInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 정보 삭제 Exception Unit Test
     * @title : testDleteCfdeploymentConfigInfoNull
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testDleteCfdeploymentConfigInfoNull(){
    	HbCfDeploymentDTO dto = setCfConfigInfo("insert");
    	when(mockMessageSource.getMessage(anyString(), anyObject(), anyObject())).thenReturn("null");
    	mockHbCfDeploymentSaveService.deleteCfdeploymentConfigInfo(dto, principal);
    }
    
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 설치 목록 조회 결과 값 설정
     * @title : setCfConfigList
     * @return : List<HbCfDeploymentVO>
    *****************************************************************/
    private HbCfDeploymentVO setCfConfig() {
        HbCfDeploymentVO vo = new HbCfDeploymentVO();
        vo.setCfDeploymentConfigName("cf-config");
        vo.setCloudConfigFile("cloud-config.yml");
        vo.setCredentialConfigInfo("crendential-config");
        vo.setDefaultConfigInfo("default-config");
        vo.setIaasType("Openstack");
        vo.setResourceConfigInfo("resource-config");
        vo.setDeployStatus("done");
        vo.setNetworkConfigInfo("network-config");
        vo.setId(1);
        vo.setInstanceConfigInfo("instance-config");
        vo.setTaskId("1");
        return vo;
    }
    

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 설치 목록 조회 결과 값 설정
     * @title : setCfConfigInfo
     * @return : List<HbCfDeploymentVO>
    *****************************************************************/
    private HbCfDeploymentDTO setCfConfigInfo(String type) {
        HbCfDeploymentDTO dto = new HbCfDeploymentDTO();
        if("update".equalsIgnoreCase(type)){
            dto.setId(1);
        }
        dto.setCfDeploymentConfigName("cf-config");
        dto.setCloudConfigFile("cloud-config.yml");
        dto.setCredentialConfigInfo("crenential-config");
        dto.setDefaultConfigInfo("default-config");
        dto.setDeployStatus("done");
        dto.setIaasType("Openstack");
        dto.setInstanceConfigInfo("instance-config");
        dto.setIaasType("Openstack");
        dto.setNetworkConfigInfo("network-config");
        dto.setTaskId("1");
        dto.setResourceConfigInfo("resource-config");
        return dto;
    }
    
}
