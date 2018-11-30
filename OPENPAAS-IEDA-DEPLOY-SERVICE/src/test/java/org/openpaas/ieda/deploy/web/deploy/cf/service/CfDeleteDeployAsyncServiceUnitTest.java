package org.openpaas.ieda.deploy.web.deploy.cf.service;

import static org.mockito.Matchers.anyInt;
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
import org.openpaas.ieda.deploy.web.config.setting.service.DirectorConfigService;
import org.openpaas.ieda.deploy.web.deploy.cf.dao.CfDAO;
import org.openpaas.ieda.deploy.web.deploy.cf.dao.CfVO;
import org.openpaas.ieda.deploy.web.deploy.cf.dto.CfParamDTO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.network.NetworkDAO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.resource.ResourceDAO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = { Application.class })
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class CfDeleteDeployAsyncServiceUnitTest extends BaseDeployControllerUnitTest {

    @InjectMocks CfDeleteDeployAsyncService mockCfDeleteDeployAsyncService;
    @Mock SimpMessagingTemplate mockSimpMessagingTemplate;
    @Mock DirectorConfigService mockDirectorConfigService;
    @Mock CfDAO mockCfDAO;
    @Mock NetworkDAO mockNetworkDAO;
    @Mock ResourceDAO mockResourceDAO;
    @Mock MessageSource mockMessageSource;
    
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
    * @description : CF 정보 삭제 TEST
    * @title : testDeleteDeploy
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteDeploy(){
        CfVO vo = setCfInfo("default");
        mockCfDeleteDeployAsyncService.deleteCfInfo(vo);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 배포 상태 변경 TEST
    * @title : testSaveDeployStatus
    * @return : void
    ***************************************************/
    @Test
    public void testSaveDeployStatus(){
        CfVO vo = setCfInfo("default");
        mockCfDeleteDeployAsyncService.saveDeployStatus(vo);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 비동기식으로 deleteDeploy 호출 메소드 TEST 
    * @title : testSaveDeployStatus
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testDeleteDeployAsync(){
        CfParamDTO.Delete dto = setCfDeleteInfo();
        when(mockCfDAO.selectCfInfoById(anyInt())).thenReturn(null);
        mockCfDeleteDeployAsyncService.deleteDeployAsync(dto, "cf", principal);
    }
    
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : CF 삭제 정보 설정
    * @title : setCfDeleteInfo
    * @return : CfParamDTO.Delete
    ***************************************************/
    public CfParamDTO.Delete setCfDeleteInfo() {
        CfParamDTO.Delete dto = new CfParamDTO.Delete();
        dto.setIaas("openstack");
        dto.setId("1");
        dto.setPlatform("cf");
        dto.getPlatform();
        dto.getId();
        dto.getIaas();
        return dto;
    }

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 상세 정보 조회 TEST
     * @title : testGetCfInfo
     * @return : void
     ***************************************************/
    public CfVO setCfInfo(String type) {
        CfVO vo = new CfVO();
        vo.setCountryCode("seoul");
        vo.setCreateUserId("admin");
        vo.setDeploymentFile("cf-yml");
        vo.setDeploymentName("cf");
        if(type.equalsIgnoreCase("null")) vo.setDeploymentFile("");
        vo.setDeployStatus("deploy");
        vo.setDescription("cf");
        vo.setDirectorUuid("uuid");
        vo.setDomain("domain");
        vo.setDomainOrganization("paas-ta");
        vo.setEmail("paas@com");
        if(!type.equalsIgnoreCase("iaasTypeNull")){
            vo.setIaasType("openstack");
            vo.setId(1);
        }
        vo.setUnitName("testunit");
        vo.setUpdateUserId("admin");
        vo.setTaskId(900);
        vo.setStateName("test");
        vo.setReleaseVersion("248");
        vo.setReleaseName("cf");
        vo.setPaastaMonitoringUse("yes");
        vo.setOrganizationName("pass-ta");
        vo.setKeyFile("cf-key.yml");
        vo.setLocalityName("mapo");
        vo.setLoginSecret("test");
        if(type.equalsIgnoreCase("null")) vo.setIaasType("");
        if(type.equalsIgnoreCase("vsphere")) vo.setIaasType("vsphere");
        return vo;
    }
    
}
