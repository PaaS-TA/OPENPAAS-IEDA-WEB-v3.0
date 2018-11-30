package org.openpaas.ieda.deploy.web.deploy.cf.service;

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
import org.openpaas.ieda.deploy.web.common.base.BaseDeployControllerUnitTest;
import org.openpaas.ieda.deploy.web.common.dao.ManifestTemplateVO;
import org.openpaas.ieda.deploy.web.config.setting.service.DirectorConfigService;
import org.openpaas.ieda.deploy.web.deploy.cf.dao.CfDAO;
import org.openpaas.ieda.deploy.web.deploy.cf.dao.CfVO;
import org.openpaas.ieda.deploy.web.deploy.cf.dto.CfParamDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = { Application.class })
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class CfDeployAsyncServiceUnitTest extends BaseDeployControllerUnitTest {
    
    @InjectMocks CfDeployAsyncService mockCfDeployAsyncService;
    @Mock SimpMessagingTemplate mockSimpMessagingTemplate;
    @Mock DirectorConfigService mockDirectorConfigService;
    @Mock CfDAO mockCfDAO;
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
    * @description : CF 설치 상태 저장 TEST
    * @title : testDeleteDeploy
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteDeploy(){
        CfVO vo = setCfInfo("default");
        mockCfDeployAsyncService.saveDeployStatus(vo);
    }
    
    @Test
    public void testSetDefualtInfo(){
        List<String> cmd = new ArrayList<String>();
        ManifestTemplateVO result = manifestInfo();
        CfVO vo = setCfInfo("default");
        mockCfDeployAsyncService.setDefualtInfo(cmd, vo, result);
    }
    
    @Test
    public void testPostgresDbUse(){
        List<String> cmd = new ArrayList<String>();
        ManifestTemplateVO result = manifestInfo();
        mockCfDeployAsyncService.postgresDbUse(cmd, result);
    }
    
    @Test
    public void testSetPublicNetworkIpUse(){
        List<String> cmd = new ArrayList<String>();
        ManifestTemplateVO result = manifestInfo();
        CfVO vo = setCfInfo("default");
        mockCfDeployAsyncService.setPublicNetworkIpUse(cmd, vo, result);
    }
    
    
    /****************************************************************
     * @project : 이종 플랫폼 설치 자동화
     * @description : CF DEPLOYMENT Manifest Template 값 설정
     * @title : manifestInfo
     * @return : ManifestTemplateVO
    *****************************************************************/
    private ManifestTemplateVO manifestInfo() {
        ManifestTemplateVO vo = new ManifestTemplateVO();
        vo.setCfTempleate("cf.yml");
        vo.setCommonBaseTemplate("cf-deployment.yml");
        vo.setCommonJobTemplate("instance.yml");
        vo.setCommonOptionTemplate("option.yml");
        vo.setDeployType("cf-deployment");
        vo.setIaasPropertyTemplate("iaas.yml");
        vo.setReleaseType("cf");
        vo.setIaasType("Openstack");
        vo.setId(1);
        vo.setInputTemplate("input.yml");
        vo.setOptionEtc("etc.yml");
        vo.setTemplateVersion("2.7.0");
        vo.setOptionNetworkTemplate("network2.yml");
        vo.setOptionResourceTemplate("resource.yml");
        vo.setMetaTemplate("meta.yml");
        vo.setMinReleaseVersion("2.7.0");
        vo.setInputTemplate("addOption.yml");
        return vo;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : testCfDeployInfoNull
    * @title : CF 설치 중 CF 정보가 존재 하지 않을 경우 TEST
    * @return : testCfDeployInfoNull
    ***************************************************/
    public void testCfDeployInfoNull(){
        CfParamDTO.Install dto = new CfParamDTO.Install();
        dto.setIaas("openstack");
        dto.setId("1");
        dto.setPlatform("cf");
        dto.getId();
        dto.getIaas();
        dto.getPlatform();
        mockCfDeployAsyncService.deploy(dto, principal, "cf");
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
        vo.setCfAdminPassword("admin");
        vo.setInceptionOsUserName("ubuntu");
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
        vo.setCfAdminPassword("admin");
        if(type.equalsIgnoreCase("paasta")) vo.setInceptionOsUserName("ubuntu");
        return vo;
    }
}
