package org.openpaas.ieda.controller.hbdeploy.web.deploy.cfdeployment;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentCredentialConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dto.HbCfDeploymentCredentialConfigDTO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service.HbCfDeploymentCredentialService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class HbCfDeploymentCredentialConfigControllerUnitTest {
    
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    
    @InjectMocks private HbCfDeploymentCredentialConfigController mockHbCfDeploymentCredentialConfigController;
    @Mock private HbCfDeploymentCredentialService mockHbCfDeploymentCredentialService;
    
    private static final String CF_CREDS_VIEW_URL = "/deploy/hbCfDeployment/credentialConfig";
    private static final String CF_CREDS_LIST_URL = "/deploy/hbCfDeployment/credentialConfig/list";
    private static final String CF_CREDS_SAVE_URL = "/deploy/hbCfDeployment/credentialConfig/save";
    private static final String CF_CREDS_DELETE_URL = "/deploy/hbCfDeployment/credentialConfig/delete";
    
    /****************************************************************
     * @project : Paas 이종 클라우드 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockHbCfDeploymentCredentialConfigController).build();
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 인증서 정보 관리 화면 이동 Unit Test
     * @title : testgoDefaulConfig
     * @return : void
    *****************************************************************/
    @Test
    public void testgoCredentialConfig() throws Exception{
        mockMvc.perform(get(CF_CREDS_VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/hbdeploy/deploy/cfDeployment/hbCfDeploymentCredentialConfig"));
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 인증서 정보 조회 Unit Test
     * @title : testGetDefaultConfigInfoList
     * @return : void
    *****************************************************************/
    @Test
    public void testGetCredetialConfigInfoList() throws Exception{
        List<HbCfDeploymentCredentialConfigVO> expectList = setCfCrednetialConfigList();
        when(mockHbCfDeploymentCredentialService.getCredentialConfigInfoList()).thenReturn(expectList);
        mockMvc.perform(get(CF_CREDS_LIST_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].recid").value(expectList.get(0).getRecid()))
        .andExpect(jsonPath("$.records[0].credentialConfigName").value(expectList.get(0).getCredentialConfigName()))
        .andExpect(jsonPath("$.records[0].credentialConfigKeyFileName").value(expectList.get(0).getCredentialConfigKeyFileName()))
        .andExpect(jsonPath("$.records[0].releaseName").value(expectList.get(0).getReleaseName()))
        .andExpect(jsonPath("$.records[0].releaseVersion").value(expectList.get(0).getReleaseVersion()))
        .andExpect(jsonPath("$.records[0].iaasType").value(expectList.get(0).getIaasType()))
        .andExpect(jsonPath("$.records[0].domain").value(expectList.get(0).getDomain()))
        .andExpect(jsonPath("$.records[0].countryCode").value(expectList.get(0).getCountryCode()))
        .andExpect(jsonPath("$.records[0].city").value(expectList.get(0).getCity()))
        .andExpect(jsonPath("$.records[0].company").value(expectList.get(0).getCompany()));
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 인증서 정보 저장 Unit Test
     * @title : testSveDefaultConfigInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testSaveDefaultConfigInfo() throws Exception{
        String requestJson = mapper.writeValueAsString(setCfCredentialConfigInfo());
        mockMvc.perform(put(CF_CREDS_SAVE_URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 인증서 정보 삭제 Unit Test
     * @title : testSveDefaultConfigInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testDeleteDefaultConfigInfo() throws Exception{
        String requestJson = mapper.writeValueAsString(setCfCredentialConfigInfo());
        mockMvc.perform(delete(CF_CREDS_DELETE_URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNoContent());
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 정보 저장 정보 값 설정
     * @title : setCfCrednetialConfigList
     * @return : void
    *****************************************************************/
    private HbCfDeploymentCredentialConfigDTO setCfCredentialConfigInfo() {
        HbCfDeploymentCredentialConfigDTO dto = new HbCfDeploymentCredentialConfigDTO();
        dto.setCfDeploymentVersion("2.7.0");
        dto.setCity("seoul");
        dto.setCompany("paasta");
        dto.setCountryCode("korea");
        dto.setCredentialConfigKeyFileName("creds.yml");
        dto.setCredentialConfigName("credential-config");
        dto.setDomain("cf.com");
        dto.setId(1);
        dto.setIaasType("Openstack");
        dto.setReleaseName("cf-deployment");
        dto.setReleaseVersion("2.7.0");
        return dto;
    }

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 인증서 목록 정보 값 설정
     * @title : setCfCrednetialConfigList
     * @return : void
    *****************************************************************/
    private List<HbCfDeploymentCredentialConfigVO> setCfCrednetialConfigList() {
        List<HbCfDeploymentCredentialConfigVO> list = new ArrayList<HbCfDeploymentCredentialConfigVO>();
        HbCfDeploymentCredentialConfigVO vo = new HbCfDeploymentCredentialConfigVO();
        vo.setCity("seoul");
        vo.setCompany("paas-ta");
        vo.setCredentialConfigKeyFileName("creds.yml");
        vo.setCountryCode("seoul");
        vo.setDomain("cf.com");
        vo.setEmail("leedh@cloud4u.co.kr");
        vo.setReleaseVersion("2.7.0");
        vo.setReleaseName("cfdeployment");
        vo.setCredentialConfigName("credential-config");
        vo.setIaasType("Openstack");
        vo.setId(1);
        list.add(vo);
        return list;
    }

}
