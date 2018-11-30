package org.openpaas.ieda.hbdeploy.web.information.release.service;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.hbdeploy.web.common.base.BaseHbDeployControllerUnitTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class HbReleaseServiceUnitService extends BaseHbDeployControllerUnitTest{
    
    @InjectMocks HbReleaseService mockHbReleaseService;
    
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
     * @description : 업로드된 릴리즈 정보 파싱 Unit Test
     * @title : setUploadReleaseList
     * @return : String
    *****************************************************************/
    @Test
    public void testSetUploadedReleaseList(){
        String responseBody = setUploadReleaseList();
        mockHbReleaseService.setUploadedReleaseList(responseBody);
    }
    
    /****************************************************************
     * @project : 이종 Paas 플랫폼 설치 자동화
     * @description : 업로드된 릴리즈 정보 설정
     * @title : setUploadReleaseList
     * @return : String
    *****************************************************************/
    private String setUploadReleaseList() {
        String info = "[{\"name\":\"paasta-controller\",";
        info += "\"release_versions\":[{\"version\":\"333\",";
        info += "\"commit_hash\":\"71adadbc\","; 
        info += "\"uncommitted_changes\":true,"; 
        info += "\"currently_deployed\":true,"; 
        info += "\"job_names\":[\"71adadbc\"]}]}]"; 
        return info;
    }
    
    
}
