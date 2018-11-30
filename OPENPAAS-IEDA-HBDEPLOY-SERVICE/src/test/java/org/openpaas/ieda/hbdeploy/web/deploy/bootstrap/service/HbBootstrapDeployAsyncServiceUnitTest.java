package org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.service;

import java.security.Principal;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.hbdeploy.web.common.base.BaseHbDeployControllerUnitTest;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapVO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class HbBootstrapDeployAsyncServiceUnitTest extends BaseHbDeployControllerUnitTest{
    
    @InjectMocks private HbBootstrapDeployAsyncService mockHbBootstrapDeployAsyncService;
    @Mock private HbBootstrapDAO mockHbBootstrapDAO;
    
    Principal principal = null;
    
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
     * @description : Bootstrap 수정 할 값이 값이 Null일 경우
     * @title : testSaveDeployStatusBootStrapInfoNull
     * @return : void
    ***************************************************/
    @Test
    public void testSaveDeployStatusBootStrapInfoNull(){
        HbBootstrapVO vo = null;
        mockHbBootstrapDeployAsyncService.saveDeployStatus(vo, principal);
    }
    
}
