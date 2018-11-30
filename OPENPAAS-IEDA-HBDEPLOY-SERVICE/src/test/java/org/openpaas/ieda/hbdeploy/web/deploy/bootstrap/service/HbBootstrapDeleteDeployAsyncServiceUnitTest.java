package org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.service;

import static org.mockito.Matchers.anyInt;
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
import org.openpaas.ieda.hbdeploy.web.common.base.BaseHbDeployControllerUnitTest;
import org.openpaas.ieda.hbdeploy.web.config.setting.dao.HbDirectorConfigDAO;
import org.openpaas.ieda.hbdeploy.web.config.setting.dao.HbDirectorConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapVO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class HbBootstrapDeleteDeployAsyncServiceUnitTest extends BaseHbDeployControllerUnitTest{
    
    @InjectMocks HbBootstrapDeleteDeployAsyncService mockHbBootstrapDeleteDeployAsyncService;
    @Mock HbDirectorConfigDAO mockHbDirectorConfigDAO;
    @Mock HbBootstrapDAO mockHbBootstrapDAO;
    
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
     * @description : Bootstrap 설치 중 상태 정보 저장
     * @title : testSaveDeployStatus
     * @return : void
    ***************************************************/
/*    @Test
    public void testSaveDeployStatus(){
        HbBootstrapVO vo = new HbBootstrapVO();
        vo.setDeployStatus("DONE");
        vo.setId(1);
        vo.setDeployLog("deploy Log...");
        when(mockHbBootstrapDAO.updateBootStrapInfo(any())).thenReturn(1);
        mockHbBootstrapDeleteDeployAsyncService.saveDeployStatus(vo, principal);
    }*/
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Bootstrap 수정 할 값이 값이 Null일 경우
     * @title : testSaveDeployStatusBootStrapInfoNull
     * @return : void
    ***************************************************/
    @Test
    public void testSaveDeployStatusBootStrapInfoNull(){
        HbBootstrapVO vo = null;
        mockHbBootstrapDeleteDeployAsyncService.saveDeployStatus(vo, principal);
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Bootstrap 삭제 후 디렉터 정보 삭제
     * @title : testDeleteDirectorConfigInfo
     * @return : void
    ***************************************************/
    @Test
    public void testDeleteDirectorConfigInfo(){
        HbDirectorConfigVO vo = new HbDirectorConfigVO();
        vo.setCredentialFile("opesntack-cred.yml");
        vo.setDirectorName("bosh");
        vo.setDirectorCpi("openstack_cpi");
        vo.setDirectorPort(25555);
        vo.setIaasType("openstack");
        vo.setDirectorUuid("55uaud12u9-=da");
        when(mockHbDirectorConfigDAO.selectHbDirectorConfigInfoByDirectorNameAndCPI(anyString(), anyString())).thenReturn(vo);
        when(mockHbDirectorConfigDAO.deleteHbDirector(anyInt())).thenReturn(1);
        mockHbBootstrapDeleteDeployAsyncService.deleteDirectorConfigInfo("openstack", "mysql");
    }
    
}
