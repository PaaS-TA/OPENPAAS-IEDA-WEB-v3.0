package org.openpaas.ieda.controller.deploy.web.config.credential;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
import org.openpaas.ieda.common.BaseControllerUnitTest;
import org.openpaas.ieda.deploy.web.config.credential.dao.CredentialManagementVO;
import org.openpaas.ieda.deploy.web.config.credential.dto.CredentialManagementDTO;
import org.openpaas.ieda.deploy.web.config.credential.service.CredentialManagementService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class CredentialManagementControllerUnitTest extends BaseControllerUnitTest{
    
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    private Principal principal = null;
    
    @InjectMocks CredentialManagementController mockCredentialManagementController;
    @Mock CredentialManagementService mockCredentialManagementService;
    
    final static String VIEW_URL = "/config/credential";
    final static String DIRECTOR_CREDENCIAL_LIST = "/config/credentail/list";
    final static String DIRECTOR_CREDENCIAL_SAVE = "/config/credentail/save";
    final static String DIRECTOR_CREDENCIAL_DELETE = "/config/credentail/delete";
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockCredentialManagementController).build();
        principal = getLoggined();
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 디렉터 인증서 화면 이동 UNIT TEST
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Test
    public void testGoCredentialManagement() throws Exception{
        mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/deploy/config/credentialManagement"));
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 디렉터 인증서 목록 조회 UNIT TEST
     * @title : testGetDirectorCredentialList
     * @return : void
    *****************************************************************/
    @Test
    public void testGetDirectorCredentialList() throws Exception{
        List<CredentialManagementVO> expectList = setDirectorCredentialList();
        when(mockCredentialManagementService.getDirectorCredentialList()).thenReturn(expectList);
        mockMvc.perform(get(DIRECTOR_CREDENCIAL_LIST).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].directorPublicIp").value("172.16.100.1"))
        .andExpect(jsonPath("$.records[0].credentialName").value("my-credential"))
        .andExpect(jsonPath("$.records[0].credentialKeyName").value("my-credential-cred.yml"))
        .andExpect(jsonPath("$.records[0].createUserId").value("admin"))
        .andExpect(jsonPath("$.records[0].updateUserId").value("admin"));
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 디렉터 인증서 정보 저장 UNIT TEST
     * @title : testSaveDirectorCredential
     * @return : void
    *****************************************************************/
    @Test
    public void testSaveDirectorCredential() throws Exception{
       CredentialManagementDTO dto = setDirectornfo();
        mockMvc.perform(post(DIRECTOR_CREDENCIAL_SAVE).contentType(MediaType.APPLICATION_JSON).principal(principal).content(mapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isCreated());
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 디렉터 인증서 정보 삭제 UNIT TEST
     * @title : testDeleteDirectorCredential
     * @return : void
    *****************************************************************/
    @Test
    public void testDeleteDirectorCredential() throws Exception{
        CredentialManagementDTO dto = setDirectornfo();
        mockMvc.perform(delete(DIRECTOR_CREDENCIAL_DELETE).contentType(MediaType.APPLICATION_JSON).principal(principal).content(mapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 디렉터 인증서 정보 저장 및 삭제 데이터 설정 값
     * @title : setDirectornfo
     * @return : void
    *****************************************************************/
    private CredentialManagementDTO setDirectornfo(){
       CredentialManagementDTO dto = new CredentialManagementDTO();
        dto.setCreateUserId("admin");
        dto.setUpdateUserId("admin");
        dto.setDirectorPublicIp("172.16.100.1");
        dto.setDirectorPrivateIp("192.168.100.1");
        dto.setCredentialKeyName("my-credential-cred.yml");
        dto.setCredentialName("my-credential");
        dto.setId("1");
        dto.getCreateUserId();
        dto.getDirectorPrivateIp();
        dto.getUpdateUserId();
        dto.getDirectorPublicIp();
        dto.getCredentialKeyName();
        dto.getCredentialName();
        dto.getId();
        return dto;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 디렉터 인증서 목록 정보 조회 데이터 결과 값 설정
     * @title : testSaveDirectorCredential
     * @return : void
    *****************************************************************/
    private List<CredentialManagementVO> setDirectorCredentialList() {
        List<CredentialManagementVO> list = new ArrayList<CredentialManagementVO>();
        CredentialManagementVO vo = new CredentialManagementVO();
        vo.setCreateUserId("admin");
        vo.setUpdateUserId("admin");
        vo.setDirectorPublicIp("172.16.100.1");
        vo.setDirectorPrivateIp("192.168.100.1");
        vo.setCredentialKeyName("my-credential-cred.yml");
        vo.setCredentialName("my-credential");
        vo.setRecid(1);
        vo.setId(1);
        vo.getCreateUserId();
        vo.getUpdateUserId();
        vo.getDirectorPublicIp();
        vo.getDirectorPrivateIp();
        vo.getCredentialKeyName();
        vo.getCredentialName();
        vo.getRecid();
        vo.getId();
        list.add(vo);
        return list;
    }
}
