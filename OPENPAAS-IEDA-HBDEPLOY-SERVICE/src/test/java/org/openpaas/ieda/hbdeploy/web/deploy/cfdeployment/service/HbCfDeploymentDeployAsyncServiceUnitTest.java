package org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.deploy.web.common.dao.ManifestTemplateVO;
import org.openpaas.ieda.hbdeploy.web.common.base.BaseHbDeployControllerUnitTest;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentCredentialConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentDefaultConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentInstanceConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentNetworkConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentResourceConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentVO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class HbCfDeploymentDeployAsyncServiceUnitTest extends BaseHbDeployControllerUnitTest {
    
    @InjectMocks HbCfDeploymentDeployAsyncService mockHbCfDeploymentDeployAsyncService;
    @Mock HbCfDeploymentDAO mockHbCfDeploymentDAO;
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 유닛 테스트가 실행되기 전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }
    
    /****************************************************************
     * @project : 이종 Paas 플랫폼 설치 자동화
     * @description : CF DEPLOYMNET 정보 수정 Unit Test
     * @title : testSaveDeployStatus
     * @return : void
    *****************************************************************/
    @Test
    public void testSaveDeployStatus(){
        HbCfDeploymentVO vo = setCfConfig();
        mockHbCfDeploymentDeployAsyncService.saveDeployStatus(vo);
    }
    
    /****************************************************************
     * @project : 이종 Paas 플랫폼 설치 자동화
     * @description : CF DEPLOYMNET 기본 정보 관련 BOSH CLI 추가 Unit Test
     * @title : testSaveDeployStatus
     * @return : void
    *****************************************************************/
    public void testSetDefualtInfo(){
        List<String> cmd = new ArrayList<String>();
        ManifestTemplateVO result = manifestInfo();
        HbCfDeploymentVO vo = setCfConfig();
        mockHbCfDeploymentDeployAsyncService.setDefualtInfo(cmd, vo, result);
    }
    
    /****************************************************************
     * @project : 이종 Paas 플랫폼 설치 자동화
     * @description : CF DEPLOYMNET 인스턴스 정보 관련 BOSH CLI 추가 Unit Test
     * @title : testSaveDeployStatus
     * @return : void
    *****************************************************************/
    @Test
    public void testSetJobSetting(){
        List<String> cmd = new ArrayList<String>();
        ManifestTemplateVO result = manifestInfo();
        HbCfDeploymentVO vo = setCfConfig();
        mockHbCfDeploymentDeployAsyncService.setJobSetting(cmd, vo, result);
    }
    
    /****************************************************************
     * @project : 이종 Paas 플랫폼 설치 자동화
     * @description : CF DEPLOYMNET DB Type 관련 BOSH CLI 추가 Unit Test
     * @title : testSaveDeployStatus
     * @return : void
    *****************************************************************/
    @Test
    public void testPostgresDbUse(){
        List<String> cmd = new ArrayList<String>();
        ManifestTemplateVO result = manifestInfo();
        mockHbCfDeploymentDeployAsyncService.postgresDbUse(cmd, result);
    }
    
    /****************************************************************
     * @project : 이종 Paas 플랫폼 설치 자동화
     * @description : CF DEPLOYMNET Public Network 사용 유무 관련 BOSH CLI 추가 Unit Test
     * @title : testSaveDeployStatus
     * @return : void
    *****************************************************************/
    @Test 
    public void testSetPublicNetworkIpUse(){
        List<String> cmd = new ArrayList<String>();
        ManifestTemplateVO result = manifestInfo();
        HbCfDeploymentVO vo = setCfConfig();
        mockHbCfDeploymentDeployAsyncService.setPublicNetworkIpUse(cmd, vo, result);
    }
    
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT Manifest Template 값 설정
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
        vo.setReleaseType("paasta");
        vo.setIaasPropertyTemplate("iaas.yml");
        vo.setIaasType("Openstack");
        vo.setId(1);
        vo.setInputTemplate("input.yml");
        vo.setOptionEtc("etc.yml");
        vo.setTemplateVersion("2.7.0");
        vo.setOptionNetworkTemplate("network2.yml");
        vo.setOptionResourceTemplate("resource.yml");
        vo.setMetaTemplate("meta.yml");
        vo.setMinReleaseVersion("2.7.0");
        return vo;
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
        
        //기본 정보
        HbCfDeploymentDefaultConfigVO defaultVo = new HbCfDeploymentDefaultConfigVO();
        defaultVo.setCfDbType("postgres");
        defaultVo.setCfDeploymentVersion("cf-deployment/2.7.0");
        defaultVo.setDefaultConfigName("defalut-config");
        defaultVo.setDeploymentName("cf-deployment");
        defaultVo.setDomain("cf.com");
        defaultVo.setDomainOrganization("paas");
        defaultVo.setIaasType("Openstack");
        defaultVo.setId(1);
        vo.setHbCfDeploymentDefaultConfigVO(defaultVo);
        //인스턴스 정보
        HbCfDeploymentInstanceConfigVO instanceVo = new HbCfDeploymentInstanceConfigVO();
        instanceVo.setAdapter("1");
        instanceVo.setApi("1");
        instanceVo.setIaasType("Openstack");
        instanceVo.setCfDeploymentVersion("2.7.0");
        instanceVo.setCfDeploymentName("cf-deployment");
        instanceVo.setCcWorker("1");
        instanceVo.setConsul("1");
        instanceVo.setDiegoApi("1");
        instanceVo.setDiegoCell("1");
        instanceVo.setDoppler("1");
        instanceVo.setHaproxy("1");
        instanceVo.setHaproxy("1");
        instanceVo.setLogApi("1");
        instanceVo.setInstanceConfigName("instance-config");
        instanceVo.setRouter("1");
        instanceVo.setScheduler("1");
        instanceVo.setId(1);
        instanceVo.setSingletonBlobstore("1");
        instanceVo.setTcpRouter("1");
        instanceVo.setTcpRouter("1");
        instanceVo.setTheDatabase("1");
        instanceVo.setUaa("1");
        instanceVo.setNats("1");
        vo.setHbCfDeploymentInstanceConfigVO(instanceVo);
        
        //network 정보
        HbCfDeploymentNetworkConfigVO networkVo = new HbCfDeploymentNetworkConfigVO();
        networkVo.setAvailabilityZone("us-west-1a");
        networkVo.setId(1);
        networkVo.setIaasType("Openstack");
        networkVo.setNetworkName("network-config");
        networkVo.setPublicStaticIp("172.16.xxx.xxx");
        networkVo.setSecurityGroup("seg");
        networkVo.setSeq(1);
        networkVo.setSubnetGateway("172.16.100.1");
        networkVo.setSubnetDns("8.8.8.8");
        networkVo.setSubnetRange("10.0.0.0/24");
        networkVo.setSubnetReservedFrom1("10.0.0.1");
        networkVo.setSubnetReservedTo1("10.0.0.10");
        vo.setHbCfDeploymentNetworkConfigVO(networkVo);
        
        //인증서 정보
        HbCfDeploymentCredentialConfigVO credsVo = new HbCfDeploymentCredentialConfigVO();
        credsVo.setCity("seoul");
        credsVo.setCompany("paas-ta");
        credsVo.setCredentialConfigKeyFileName("creds.yml");
        credsVo.setCountryCode("seoul");
        credsVo.setDomain("cf.com");
        credsVo.setEmail("leedh@cloud4u.co.kr");
        credsVo.setReleaseVersion("2.7.0");
        credsVo.setReleaseName("cfdeployment");
        credsVo.setCredentialConfigName("credential-config");
        credsVo.setIaasType("Openstack");
        credsVo.setId(1);
        vo.setHbCfDeploymentCredentialConfigVO(credsVo);
        
        //리소스 정보
        HbCfDeploymentResourceConfigVO resourceVo = new HbCfDeploymentResourceConfigVO();
        resourceVo.setDirectorInfo("1");
        resourceVo.setIaasType("Openstack");
        resourceVo.setId(1);
        resourceVo.setInstanceTypeS("m1.small");
        resourceVo.setInstanceTypeM("m1.medium");
        resourceVo.setInstanceTypeL("m1.large");
        resourceVo.setResourceConfigName("resource-config");
        resourceVo.setStemcellName("openstakc-ubuntu");
        resourceVo.setStemcellVersion("3621.48");
        vo.setHbCfDeploymentResourceConfigVO(resourceVo);
        return vo;
    }
    
}
