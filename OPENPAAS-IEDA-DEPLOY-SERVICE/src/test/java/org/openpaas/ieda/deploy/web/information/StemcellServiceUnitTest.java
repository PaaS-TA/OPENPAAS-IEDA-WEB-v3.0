package org.openpaas.ieda.deploy.web.information;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.deploy.web.common.base.BaseDeployControllerUnitTest;
import org.openpaas.ieda.deploy.web.information.stemcell.service.StemcellService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class StemcellServiceUnitTest extends BaseDeployControllerUnitTest{
    
    @InjectMocks 
    private StemcellService mockStemcellService;
    

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :하나의 메소드가 실행되기전 호출 
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        getLoggined();
        
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 조회한 릴리즈 목록 정보 설정
     * @title : testSetUploadedReleaseList
     * @return : void
    *****************************************************************/
    @Test
    public void testSetUploadedReleaseList(){
        mockStemcellService.setUploadedStemcellList(setUploadedStecmcellInfo());
      
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드된 스템셀 정보 설정
     * @title : setUploadedStecmcellInfo
     * @return : String
    *****************************************************************/
    private String setUploadedStecmcellInfo() {
        String info = "[{\"name\":\"bosh-aws-xen-ubuntu-trusty-go_agent\",";
        info += "\"operating_system\":\"ubuntu-trusty\","; 
        info += "\"version\":\"3262\","; 
        info += "\"cid\":\"7115e1ba-84a7-4964-9b0d-213d2279f63b\","; 
        info += "\"deployments\":[]}]"; 
        return info;
    }

}
