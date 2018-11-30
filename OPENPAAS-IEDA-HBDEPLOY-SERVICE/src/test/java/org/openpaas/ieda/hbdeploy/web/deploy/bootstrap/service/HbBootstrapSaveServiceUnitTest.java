package org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.service;

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
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapVO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto.HbBootStrapDeployDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class HbBootstrapSaveServiceUnitTest extends BaseHbDeployControllerUnitTest{
    
    @InjectMocks HbBootstrapSaveService mockHbBootstrapSaveService;
    @Mock HbBootstrapDAO mockHbBootstrapDAO;
    @Mock MessageSource mockMessageSource;
    
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
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : BOOTSTRAP 설치 정보 저장 UNIT TEST
     * @title : testSaveBootstrapInfo
     * @return : void
    ***************************************************/
    @Test
    public void testUpdateBootstrapInfo(){
        HbBootStrapDeployDTO dto = setBootstrapInstallInfo("update");
        HbBootstrapVO vo = expectBootstrapConfig();
        when(mockHbBootstrapDAO.selectBootstrapConfigByName(anyString())).thenReturn(0);
        when(mockHbBootstrapDAO.selectBootstrapConfigInfo(anyInt(), anyString())).thenReturn(vo);
        mockHbBootstrapSaveService.saveBootstrapInfo(dto, principal);
    }
    
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : BOOTSTRAP 설치 정보 저장 UNIT TEST
     * @title : testSaveBootstrapInfo
     * @return : void
    ***************************************************/
    @Test
    public void testInsertBootstrapInfo(){
        HbBootStrapDeployDTO dto = setBootstrapInstallInfo("insert");
        HbBootstrapVO vo = expectBootstrapConfig();
        when(mockHbBootstrapDAO.selectBootstrapConfigByName(anyString())).thenReturn(0);
        when(mockHbBootstrapDAO.selectBootstrapConfigInfo(anyInt(), anyString())).thenReturn(vo);
        mockHbBootstrapSaveService.saveBootstrapInfo(dto, principal);
    }
    
    
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : BOOTSTRAP 설치 정보 저장 Exception UNIT TEST
     * @title : testSaveBootstrapInfoComplict
     * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testSaveBootstrapInfoComplict(){
        HbBootStrapDeployDTO dto = setBootstrapInstallInfo("insert");
        HbBootstrapVO vo = expectBootstrapConfig();
        when(mockHbBootstrapDAO.selectBootstrapConfigByName(anyString())).thenReturn(1);
        when(mockHbBootstrapDAO.selectBootstrapConfigInfo(anyInt(), anyString())).thenReturn(vo);
        when(mockMessageSource.getMessage(anyString(), anyObject(), anyObject())).thenReturn("conflic_exception");
        mockHbBootstrapSaveService.saveBootstrapInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : BOOTSTRAP 상세 조회 값 설정
     * @title : expectBootstrapConfig
     * @return : HbBootstrapVO
    *****************************************************************/
    private HbBootstrapVO expectBootstrapConfig(){
        HbBootstrapVO vo = new HbBootstrapVO();
        vo.setBootstrapConfigName("bootstrap-config");
        vo.setId(1);
        vo.setCpiConfigInfo("cpi-config");
        vo.setNetworkConfigInfo("network-config");
        vo.setDefaultConfigInfo("default-config");
        vo.setIaasType("Openstack");
        vo.setDeployLog("Done");
        vo.setResourceConfigInfo("resource-config");
        vo.setDeployLog("starting...");
        vo.setDeploymentFile("openstack-microbosh-1.yml");
        return vo;
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : BOOTSTRAP 정보 저장 값 설정
     * @title : setBootstrapInstallInfo
     * @return : void
    *****************************************************************/
    private HbBootStrapDeployDTO setBootstrapInstallInfo(String type){
        HbBootStrapDeployDTO dto = new HbBootStrapDeployDTO();
        dto.setDeploymentFile("openstack-micro-bosh.yml");
        dto.setCpiConfigInfo("cpi-config");
        dto.setDefaultConfigInfo("default-config");
        dto.setDeployLog("done");
        dto.setDeployStatus("processing");
        dto.setIaasType("Openstack");
        dto.setNetworkConfigInfo("network-config");
        dto.setResourceConfigInfo("resource-config");
        if("update".equalsIgnoreCase(type)){
            dto.setId("1");
        }
        dto.setBootstrapConfigName("bootstrap-config");
        return dto;
    }
}
