package org.openpaas.ieda.deploy.web.information.release;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.web.common.base.BaseDeployControllerUnitTest;
import org.openpaas.ieda.deploy.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.deploy.web.config.setting.service.DirectorConfigService;
import org.openpaas.ieda.deploy.web.information.release.service.ReleaseService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class ReleaseServiceUnitTest extends BaseDeployControllerUnitTest{
    
    @InjectMocks 
    private ReleaseService mockReleaseService;
    @Mock 
    private DirectorConfigService mockDirectorConfigService;
    @Mock
    private MessageSource mockMessageSource;
    
    
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
     * @description : 업로드된 릴리즈 목록 조회 요청
     * @title : testGetUploadedReleaseListIOException
     * @return : void
    *****************************************************************/
//    @Test(expected=CommonException.class)
    public void testGetUploadedReleaseListIOException(){
        DirectorConfigVO defaultDirector = setDefaultDirectorInfo();
        when(mockDirectorConfigService.getDefaultDirector()).thenReturn(defaultDirector);
        mockReleaseService.getUploadedReleaseList();
      
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 조회한 릴리즈 목록 정보 설정
     * @title : testSetUploadedReleaseList
     * @return : void
    *****************************************************************/
    @Test
    public void testSetUploadedReleaseList(){
        mockReleaseService.setUploadedReleaseList(setUploadReleaseList());
      
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드된 릴리즈 목록 조회 요청 (JsonMappingException)
     * @title : testSetUploadedReleaseListJsonMappingException
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testSetUploadedReleaseListJsonMappingException(){
        DirectorConfigVO defaultDirector = setDefaultDirectorInfo();
        when(mockDirectorConfigService.getDefaultDirector()).thenReturn(defaultDirector);
        when(mockMessageSource.getMessage(any(), any(), any())).thenReturn("");
        mockReleaseService.setUploadedReleaseList(setUploadReleaseListJsonMappingException());
      
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 
     * @title : testGetFilteredReleseListIOException
     * @return : void
    *****************************************************************/
//    @Test(expected=CommonException.class)
    public void testGetFilteredReleseListIOException(){
        DirectorConfigVO defaultDirector = setDefaultDirectorInfo();
        when(mockDirectorConfigService.getDefaultDirector()).thenReturn(defaultDirector);
        mockReleaseService.getFilteredReleseList("cf");
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Paasta-controller 릴리즈 정보 조회
     * @title : testSetFilteredPaastaControllerReleseInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testSetFilteredPaastaControllerReleseInfo(){
        String responseBody = setUploadPaastaControllerReleaseList();
        mockReleaseService.setFilteredReleseInfo(responseBody, "cf");
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Paasta-container 릴리즈 정보 조회
     * @title : testSetFilteredPaastaContainerReleseInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testSetFilteredPaastaContainerReleseInfo(){
        String responseBody = setUploadPaastaContainerReleaseList();
        mockReleaseService.setFilteredReleseInfo(responseBody,"diego");
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : garden-linux 릴리즈 정보 조회
     * @title : testSetFilteredGardenRuncReleaseInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testSetFilteredGardenRuncReleaseInfo(){
        String responseBody = setUploadGardenRuncReleaseList();
        mockReleaseService.setFilteredReleseInfo(responseBody, "garden-linux");
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Paasta-garden-linux 릴리즈 정보 조회
     * @title : testSetFilteredPaastaGardenRuncReleaseInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testSetFilteredPaastaGardenRuncReleaseInfo(){
        String responseBody = setUploadPaastaGardenRuncReleaseList();
        mockReleaseService.setFilteredReleseInfo(responseBody, "garden-linux");
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 기본 설치 관리자 정보 설정
     * @title : setDefaultDirectorInfo
     * @return : DirectorConfigVO
    *****************************************************************/
    public DirectorConfigVO setDefaultDirectorInfo(){
        DirectorConfigVO vo = new DirectorConfigVO();
        vo.setIedaDirectorConfigSeq(1);
        vo.setRecid(1);
        vo.setUserId("admin");
        vo.setDefaultYn("Y");
        vo.setDirectorCpi("openstack-cpi");
        vo.setDirectorName("my-bosh");
        vo.setDirectorPort(25555);
        vo.setDirectorUuid("f5a3ff66-95a2-4ff2-test-728bd1c1c595");
        vo.setDirectorUrl("10.10.10.10");
        vo.setCreateUserId("admin");
        vo.setUpdateUserId("admin");
        vo.setUserPassword("admin");
        vo.setConnect(true);
        return vo;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드된 릴리즈 정보 설정
     * @title : setUploadReleaseList
     * @return : String
    *****************************************************************/
    private String setUploadReleaseList() {
        String info = "[{\"name\":\"paasta-controller\",";
        info += "\"release_versions\":[{\"version\":\"247\",";
        info += "\"commit_hash\":\"71adadbc\","; 
        info += "\"uncommitted_changes\":true,"; 
        info += "\"currently_deployed\":true,"; 
        info += "\"job_names\":[\"71adadbc\"]}]}]"; 
        return info;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드된 릴리즈 정보 JsonMappingException 설정
     * @title : setUploadReleaseListJsonMappingException
     * @return : String
    *****************************************************************/
    private String setUploadReleaseListJsonMappingException() {
        String info = "[{\"name\":\"cf\",";
        info += "\"release_versions\":[{\"version\":\"247\",";
        info += "\"commit_hash\":\"71adadbc\","; 
        info += "\"uncommitted_changes\":true,"; 
        info += "\"currently_deployed\":true,"; 
//        info += "\"job_names\":[\"71adadbc\"]}]}]"; 
        return info;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Paasta-Controller 릴리즈 정보 설정
     * @title : setUploadPaastaControllerReleaseList
     * @return : String
    *****************************************************************/
    private String setUploadPaastaControllerReleaseList() {
        String info = "[{\"name\":\"paasta-controller\",";
        info += "\"release_versions\":[{\"version\":\"2.0\",";
        info += "\"commit_hash\":\"71adadbc\","; 
        info += "\"uncommitted_changes\":true,"; 
        info += "\"currently_deployed\":true,"; 
        info += "\"job_names\":[\"71adadbc\"]}]}]"; 
        return info;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Paasta-Container 릴리즈 정보 설정
     * @title : setUploadPaastaContainerReleaseList
     * @return : String
    *****************************************************************/
    private String setUploadPaastaContainerReleaseList() {
        String info = "[{\"name\":\"paasta-container\",";
        info += "\"release_versions\":[{\"version\":\"2.0\",";
        info += "\"commit_hash\":\"71adadbc\","; 
        info += "\"uncommitted_changes\":true,"; 
        info += "\"currently_deployed\":true,"; 
        info += "\"job_names\":[\"71adadbc\"]}]}]"; 
        return info;
    }
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Paasta-Garden-runc 릴리즈 정보 설정
     * @title : setUploadPaastaGardenRuncReleaseList
     * @return : String
    *****************************************************************/
    private String setUploadPaastaGardenRuncReleaseList() {
        String info = "[{\"name\":\"paasta-garden-runc\",";
        info += "\"release_versions\":[{\"version\":\"2.0\",";
        info += "\"commit_hash\":\"71adadbc\","; 
        info += "\"uncommitted_changes\":true,"; 
        info += "\"currently_deployed\":true,"; 
        info += "\"job_names\":[\"71adadbc\"]}]}]"; 
        return info;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : garden-runc 릴리즈 정보 설정
     * @title : setUploadGardenRuncReleaseList
     * @return : String
    *****************************************************************/
    private String setUploadGardenRuncReleaseList() {
        String info = "[{\"name\":\"garden-runc\",";
        info += "\"release_versions\":[{\"version\":\"2.0\",";
        info += "\"commit_hash\":\"71adadbc\","; 
        info += "\"uncommitted_changes\":true,"; 
        info += "\"currently_deployed\":true,"; 
        info += "\"job_names\":[\"71adadbc\"]}]}]"; 
        return info;
    }
    

}
