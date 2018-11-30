package org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service;
import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.hbdeploy.web.common.base.BaseHbDeployControllerUnitTest;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentDefaultConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentVO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class HbCfDeploymentDeleteAsyncServiceUnitTest extends BaseHbDeployControllerUnitTest {
	
    @InjectMocks HbCfDeploymentDeleteAsyncService mockHbCfDeploymentDeleteAsyncService;
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
     * @description : CF DEPLOYMNET 정보 삭제 Unit Test
     * @title : testDeleteCfInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testDeleteCfInfo(){
    	HbCfDeploymentVO vo = setCfConfig();
    	mockHbCfDeploymentDeleteAsyncService.deleteCfInfo(vo);
    }
    
    /****************************************************************
     * @project : 이종 Paas 플랫폼 설치 자동화
     * @description : CF DEPLOYMNET 배포 상태 수정 Unit Test
     * @title : testDeleteCfInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testSaveDeployStatus(){
    	HbCfDeploymentVO vo = setCfConfig();
    	mockHbCfDeploymentDeleteAsyncService.saveDeployStatus(vo);
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
        vo.setCloudConfigFile("1.yml");
        HbCfDeploymentDefaultConfigVO hbVo = new HbCfDeploymentDefaultConfigVO();
        hbVo.setDeploymentName("paasta");
        vo.setHbCfDeploymentDefaultConfigVO(hbVo);
        vo.setIaasType("Openstack");
        vo.setResourceConfigInfo("resource-config");
        vo.setDeployStatus("done");
        vo.setNetworkConfigInfo("network-config");
        vo.setId(1);
        vo.setInstanceConfigInfo("instance-config");
        vo.setTaskId("1");
        return vo;
    }
    
    
    
}
