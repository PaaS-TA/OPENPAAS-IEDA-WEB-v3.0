package org.openpaas.ieda.controller.hbdeploy.web.deploy.cfdeployment;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentInstanceConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dto.HbCfDeploymentInstanceConfigDTO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service.HbCfDeploymentInstanceConfigService;
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
public class HbCfDeploymentInstanceConfigControllerUnitTest {
    
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    
    @InjectMocks private HbCfDeploymentInstanceConfigController mockHbCfDeploymentInstanceConfigController;
    @Mock private HbCfDeploymentInstanceConfigService mockHbCfDeploymentInstanceConfigService;
    
    private static final String CF_INSTANCE_VIEW_URL = "/deploy/hbCfDeployment/instanceConfig";
    private static final String CF_INSTANCE_LIST_URL = "/deploy/hbCfDeployment/instanceConfig/list";
    private static final String CF_INSTANCE_JOB_LIST_URL = "/deploy/hbCfDeployment/instanceConfig/job/list/{version}/{deployType}";
    private static final String CF_INSTANCE_SAVE_URL = "/deploy/hbCfDeployment/instanceConfig/save";
    private static final String CF_INSTANCE_DELETE_URL = "/deploy/hbCfDeployment/instanceConfig/delete";
    
    /****************************************************************
     * @project : Paas 이종 클라우드 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockHbCfDeploymentInstanceConfigController).build();
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 인스턴스 정보 관리 화면 이동 Unit Test
     * @title : testgoDefaulConfig
     * @return : void
    *****************************************************************/
    @Test
    public void testGoInstanceConfig() throws Exception{
        mockMvc.perform(get(CF_INSTANCE_VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/hbdeploy/deploy/cfDeployment/hbCfDeploymentInstanceConfig"));
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 인스턴스 목록 정보 조회 Unit Test
     * @title : testgoDefaulConfig
     * @return : void
    *****************************************************************/
    @Test
    public void testGetHbCfInstanceConfigInfoList() throws Exception{
        List<HbCfDeploymentInstanceConfigVO> expectList = setCfInstanceConfigList();
        when(mockHbCfDeploymentInstanceConfigService.getHbCfInstanceConfigInfoList()).thenReturn(expectList);
        mockMvc.perform(get(CF_INSTANCE_LIST_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].recid").value(expectList.get(0).getRecid()))
        .andExpect(jsonPath("$.records[0].instanceConfigName").value(expectList.get(0).getInstanceConfigName()))
        .andExpect(jsonPath("$.records[0].iaasType").value(expectList.get(0).getIaasType()))
        .andExpect(jsonPath("$.records[0].cfDeploymentName").value(expectList.get(0).getCfDeploymentName()))
        .andExpect(jsonPath("$.records[0].cfDeploymentVersion").value(expectList.get(0).getCfDeploymentVersion()))
        .andExpect(jsonPath("$.records[0].adapter").value(expectList.get(0).getAdapter()))
        .andExpect(jsonPath("$.records[0].api").value(expectList.get(0).getApi()))
        .andExpect(jsonPath("$.records[0].ccWorker").value(expectList.get(0).getCcWorker()))
        .andExpect(jsonPath("$.records[0].consul").value(expectList.get(0).getConsul()))
        .andExpect(jsonPath("$.records[0].theDatabase").value(expectList.get(0).getTheDatabase()))
        .andExpect(jsonPath("$.records[0].diegoApi").value(expectList.get(0).getDiegoApi()))
        .andExpect(jsonPath("$.records[0].diegoCell").value(expectList.get(0).getDiegoCell()))
        .andExpect(jsonPath("$.records[0].doppler").value(expectList.get(0).getDoppler()))
        .andExpect(jsonPath("$.records[0].logApi").value(expectList.get(0).getLogApi()))
        .andExpect(jsonPath("$.records[0].haproxy").value(expectList.get(0).getHaproxy()))
        .andExpect(jsonPath("$.records[0].router").value(expectList.get(0).getRouter()))
        .andExpect(jsonPath("$.records[0].nats").value(expectList.get(0).getNats()))
        .andExpect(jsonPath("$.records[0].singletonBlobstore").value(expectList.get(0).getSingletonBlobstore()))
        .andExpect(jsonPath("$.records[0].tcpRouter").value(expectList.get(0).getTcpRouter()))
        .andExpect(jsonPath("$.records[0].uaa").value(expectList.get(0).getUaa()))
        .andExpect(jsonPath("$.records[0].scheduler").value(expectList.get(0).getScheduler()));
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT Job 정보 조회 Unit Test
     * @title : testGetHbCfJobList
     * @return : void
    *****************************************************************/
    @Test
    public void testGetHbCfJobList() throws Exception{
        List<HashMap<String, String>> expectList = setCfJobList();
        when(mockHbCfDeploymentInstanceConfigService.getHbCfJobTemplateList(anyString(), anyString())).thenReturn(expectList);
        mockMvc.perform(get(CF_INSTANCE_JOB_LIST_URL,"2.7.0", "CF-DEPLOYMENT").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].zone").value(expectList.get(0).get("zone")))
        .andExpect(jsonPath("$[0].job").value(expectList.get(0).get("job")))
        .andExpect(jsonPath("$[0].instances").value(expectList.get(0).get("instances")));
    }

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT Job 정보 저장 Unit Test
     * @title : testSaveInstanceConfigInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testSaveInstanceConfigInfo() throws Exception{
        String requestJson = mapper.writeValueAsString(setCfInstanceConfigInfo());
        mockMvc.perform(put(CF_INSTANCE_SAVE_URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT Job 정보 삭제 Unit Test
     * @title : testDeleteInstanceConfigInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testDeleteInstanceConfigInfo() throws Exception{
        String requestJson = mapper.writeValueAsString(setCfInstanceConfigInfo());
        mockMvc.perform(delete(CF_INSTANCE_DELETE_URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNoContent());
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT Job 정보 저장/삭제 값 설정
     * @title : setCfInstanceConfigInfo
     * @return : HbCfDeploymentInstanceConfigDTO
    *****************************************************************/
    private HbCfDeploymentInstanceConfigDTO setCfInstanceConfigInfo() {
        HbCfDeploymentInstanceConfigDTO dto = new HbCfDeploymentInstanceConfigDTO();
        dto.setAdapter("1");
        dto.setApi("1");
        dto.setIaasType("Openstack");
        dto.setCcWorker("1");
        dto.setCfDeploymentName("cf-deployment");
        dto.setCfDeploymentVersion("2.7.0");
        dto.setConsul("1");
        dto.setDiegoApi("1");
        dto.setDiegoCell("1");
        dto.setDoppler("1");
        dto.setHaproxy("1");
        dto.setId(1);
        dto.setUaa("1");
        dto.setNats("1");
        dto.setSingletonBlobstore("1");
        dto.setTcpRouter("1");
        dto.setTheDatabase("1");
        dto.setRouter("1");
        dto.setScheduler("1");
        dto.setLogApi("1");
        return dto;
    }

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT JOB 목록 정보 조회 결과 값 설정
     * @title : setCfInstanceConfigList
     * @return : List<HashMap<String, String>>
    *****************************************************************/
    private List<HashMap<String, String>> setCfJobList() {
        List<HashMap<String, String>> maps = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("zone", "z1");
        map.put("job", "diego-cell");
        map.put("instances", "1");
        maps.add(map);
        return maps;
    }

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 인스턴스 목록 정보 조회 결과 값 설정
     * @title : setCfInstanceConfigList
     * @return : List<HbCfDeploymentCredentialConfigVO>
    *****************************************************************/
    private List<HbCfDeploymentInstanceConfigVO> setCfInstanceConfigList() {
        List<HbCfDeploymentInstanceConfigVO> list = new ArrayList<HbCfDeploymentInstanceConfigVO>();
        HbCfDeploymentInstanceConfigVO vo = new HbCfDeploymentInstanceConfigVO();
        vo.setAdapter("1");
        vo.setApi("1");
        vo.setIaasType("Openstack");
        vo.setCfDeploymentVersion("2.7.0");
        vo.setCfDeploymentName("cf-deployment");
        vo.setCcWorker("1");
        vo.setConsul("1");
        vo.setDiegoApi("1");
        vo.setDiegoCell("1");
        vo.setDoppler("1");
        vo.setHaproxy("1");
        vo.setHaproxy("1");
        vo.setLogApi("1");
        vo.setInstanceConfigName("instance-config");
        vo.setRouter("1");
        vo.setScheduler("1");
        vo.setId(1);
        vo.setSingletonBlobstore("1");
        vo.setTcpRouter("1");
        vo.setTcpRouter("1");
        vo.setTheDatabase("1");
        vo.setUaa("1");
        vo.setNats("1");
        list.add(vo);
        return list;
    }
    
}
