package org.openpaas.ieda.controller.hbdeploy.web.deploy.cfdeployment;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
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
import org.openpaas.ieda.hbdeploy.web.config.setting.dao.HbDirectorConfigVO;
import org.openpaas.ieda.hbdeploy.web.config.setting.service.HbDirectorConfigService;
import org.openpaas.ieda.hbdeploy.web.config.stemcell.dao.HbStemcellManagementVO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentResourceConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dto.HbCfDeploymentResourceConfigDTO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service.HbCfDeploymentResourceConfigService;
import org.openpaas.ieda.hbdeploy.web.information.stemcell.service.HbStemcellService;
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
public class HbCfDeploymentResourceConfigControllerUnitTest {
    
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    
    @InjectMocks private HbCfDeploymentResourceConfigController mockHbCfDeploymentResourceConfigController;
    @Mock private HbCfDeploymentResourceConfigService mockHbCfDeploymentResourceConfigService;
    @Mock private HbDirectorConfigService mockHbDirectorConfigService;
    @Mock private HbStemcellService mockHbStemcellService;
    
    private final static String CF_RESOURCE_VIEW_URL = "/deploy/hbCfDeployment/resourceConfig";
    private final static String CF_RESOURCE_LIST_URL = "/deploy/hbCfDeployment/resourceConfig/list";
    private final static String CF_RESOURCE_SAVE_URL = "/deploy/hbCfDeployment/resourceConfig/save";
    private final static String CF_RESOURCE_DELETE_URL = "/deploy/hbCfDeployment/resourceConfig/delete";
    private final static String CF_RESOURCE_DIRECTOR_LIST_URL = "/config/hbCfDeployment/resourceConfig/list/director/{iaasType}";
    private final static String CF_RESOURCE_UPLOAD_STEMCELL_LIST_URL = "/config/hbCfDeployment/resourceConfig/list/stemcells/{directorId}";
    
    
    /****************************************************************
     * @project : Paas 이종 클라우드 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockHbCfDeploymentResourceConfigController).build();
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 리소스 정보 관리 화면 이동 Unit Test
     * @title : testGoResourceConfig
     * @return : void
    *****************************************************************/
    @Test
    public void testGoResourceConfig() throws Exception{
        mockMvc.perform(get(CF_RESOURCE_VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/hbdeploy/deploy/cfDeployment/hbCfDeploymentResourceConfig"));
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 리소스 정보 목록 조회  Unit Test
     * @title : testGetRecourceConfigInfoList
     * @return : void
    *****************************************************************/
    @Test
    public void testGetRecourceConfigInfoList() throws Exception{
        List<HbCfDeploymentResourceConfigVO> expectList = setCfCrednetialConfigList();
        when(mockHbCfDeploymentResourceConfigService.getResourceConfigInfoList()).thenReturn(expectList);
        mockMvc.perform(get(CF_RESOURCE_LIST_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].recid").value(expectList.get(0).getRecid()))
        .andExpect(jsonPath("$.records[0].id").value(expectList.get(0).getId()))
        .andExpect(jsonPath("$.records[0].resourceConfigName").value(expectList.get(0).getResourceConfigName()))
        .andExpect(jsonPath("$.records[0].iaasType").value(expectList.get(0).getIaasType()))
        .andExpect(jsonPath("$.records[0].stemcellName").value(expectList.get(0).getStemcellName()))
        .andExpect(jsonPath("$.records[0].stemcellVersion").value(expectList.get(0).getStemcellVersion()))
        .andExpect(jsonPath("$.records[0].instanceTypeS").value(expectList.get(0).getInstanceTypeS()))
        .andExpect(jsonPath("$.records[0].instanceTypeM").value(expectList.get(0).getInstanceTypeM()))
        .andExpect(jsonPath("$.records[0].instanceTypeL").value(expectList.get(0).getInstanceTypeL()))
        .andExpect(jsonPath("$.records[0].directorInfo").value(expectList.get(0).getDirectorInfo()));
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 리소스 정보 저장 Unit Test
     * @title : testGetRecourceConfigInfoList
     * @return : void
    *****************************************************************/
    @Test
    public void testSaveResourceConfigInfo() throws Exception{
        String requestJson = mapper.writeValueAsString(setCfResourceConfigInfo());
        mockMvc.perform(put(CF_RESOURCE_SAVE_URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
    }
    
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 리소스 정보 삭제 Unit Test
     * @title : testDeleteResourceConfigInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testDeleteResourceConfigInfo() throws Exception{
        String requestJson = mapper.writeValueAsString(setCfResourceConfigInfo());
        mockMvc.perform(delete(CF_RESOURCE_DELETE_URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNoContent());
    }
    
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 디렉터 조회 Unit Test
     * @title : testGetHbDirectorListByIaas
     * @return : voidList<HbDirectorConfigVO>
    *****************************************************************/
    @Test
    public void testGetHbDirectorListByIaas() throws Exception{
        List<HbDirectorConfigVO> expectList = setCfDriectorInfoList();
        when(mockHbDirectorConfigService.getDirectorListByIaas(anyString())).thenReturn(expectList);
        mockMvc.perform(get(CF_RESOURCE_DIRECTOR_LIST_URL,"Openstack").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].recid").value(expectList.get(0).getRecid()))
        .andExpect(jsonPath("$.records[0].iedaDirectorConfigSeq").value(expectList.get(0).getIedaDirectorConfigSeq()))
        .andExpect(jsonPath("$.records[0].iaasType").value(expectList.get(0).getIaasType()))
        .andExpect(jsonPath("$.records[0].directorName").value(expectList.get(0).getDirectorName()))
        .andExpect(jsonPath("$.records[0].directorUrl").value(expectList.get(0).getDirectorUrl()))
        .andExpect(jsonPath("$.records[0].directorPort").value(expectList.get(0).getDirectorPort()))
        .andExpect(jsonPath("$.records[0].directorUuid").value(expectList.get(0).getDirectorUuid()))
        .andExpect(jsonPath("$.records[0].directorCpi").value(expectList.get(0).getDirectorCpi()));
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 스템셀 조회 Unit Test
     * @title : testGetHbDirectorListByIaas
     * @return : voidList<HbDirectorConfigVO>
    *****************************************************************/
    @Test
    public void testGetHbUploadedStemcellList() throws Exception{
        List<HbStemcellManagementVO>  expectList = setCfStecmellInfoList();
        when(mockHbStemcellService.getStemcellList(anyInt())).thenReturn(expectList);
        mockMvc.perform(get(CF_RESOURCE_UPLOAD_STEMCELL_LIST_URL,1).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].recid").value(expectList.get(0).getRecid()))
        .andExpect(jsonPath("$.records[0].id").value(expectList.get(0).getId()))
        .andExpect(jsonPath("$.records[0].stemcellName").value(expectList.get(0).getStemcellName()))
        .andExpect(jsonPath("$.records[0].stemcellUrl").value(expectList.get(0).getStemcellUrl()))
        .andExpect(jsonPath("$.records[0].os").value(expectList.get(0).getOs()))
        .andExpect(jsonPath("$.records[0].osVersion").value(expectList.get(0).getOsVersion()))
        .andExpect(jsonPath("$.records[0].iaas").value(expectList.get(0).getIaas()))
        .andExpect(jsonPath("$.records[0].stemcellVersion").value(expectList.get(0).getStemcellVersion()))
        .andExpect(jsonPath("$.records[0].isExisted").value(expectList.get(0).getIsExisted()))
        .andExpect(jsonPath("$.records[0].downloadStatus").value(expectList.get(0).getDownloadStatus()))
        ;
        
    }

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 스템셀 목록 조회 결과 값 설정
     * @title : setCfStecmellInfoList
     * @return : List<HbStemcellManagementVO>
    *****************************************************************/
    private List<HbStemcellManagementVO> setCfStecmellInfoList() {
        List<HbStemcellManagementVO> list = new ArrayList<HbStemcellManagementVO> ();
        HbStemcellManagementVO vo = new HbStemcellManagementVO();
        vo.setDownloadStatus("done");
        vo.setIaas("Openstack");
        vo.setId(1);
        vo.setOs("ubuntu");
        vo.setOsVersion("14.04");
        vo.setIsExisted("exist");
        vo.setStemcellName("ubuntu-trust");
        vo.setStemcellUrl("https://aaa.com");
        vo.setStemcellVersion("3666.21");
        list.add(vo);
        return list;
    }

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 디렉터 정보 조회 결과 값 설정
     * @title : setCfDriectorInfoList
     * @return : List<HbDirectorConfigVO>
    *****************************************************************/
    private List<HbDirectorConfigVO> setCfDriectorInfoList() {
        List<HbDirectorConfigVO> list= new ArrayList<HbDirectorConfigVO>();
        HbDirectorConfigVO vo = new HbDirectorConfigVO();
        vo.setDirectorCpi("openstack_cpi");
        vo.setDirectorName("obosh");
        vo.setDirectorPort(25555);
        vo.setDirectorType("private");
        vo.setDirectorUrl("172.16.100.1");
        vo.setIedaDirectorConfigSeq(1);
        vo.setDirectorUuid("3123-ddfAS123sdfs313-dfv");
        vo.setIaasType("Openstack");
        list.add(vo);
        return list;
    }

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 리소스 정보 저장/삭제 요청 값 설정
     * @title : setCfResourceConfigInfo
     * @return : HbCfDeploymentResourceConfigDTO
    *****************************************************************/
    private HbCfDeploymentResourceConfigDTO setCfResourceConfigInfo() {
        HbCfDeploymentResourceConfigDTO dto = new HbCfDeploymentResourceConfigDTO();
        dto.setDirectorInfo("1");
        dto.setIaasType("Openstack");
        dto.setId(1);
        dto.setInstanceTypeL("m1.large");
        dto.setInstanceTypeM("m1.medium");
        dto.setInstanceTypeS("m1.small");
        dto.setResourceConfigName("resource-config");
        dto.setStemcellName("ubuntu-trust");
        dto.setStemcellVersion("3568.21");
        return dto;
    }

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 리소스 정보 목록 조회 결과 값 설정
     * @title : setCfCrednetialConfigList
     * @return : List<HbCfDeploymentResourceConfigVO>
    *****************************************************************/
    private List<HbCfDeploymentResourceConfigVO> setCfCrednetialConfigList() {
        List<HbCfDeploymentResourceConfigVO> list = new ArrayList<HbCfDeploymentResourceConfigVO>();
        HbCfDeploymentResourceConfigVO vo = new HbCfDeploymentResourceConfigVO();
        vo.setDirectorInfo("1");
        vo.setIaasType("Openstack");
        vo.setId(1);
        vo.setInstanceTypeS("m1.small");
        vo.setInstanceTypeM("m1.medium");
        vo.setInstanceTypeL("m1.large");
        vo.setResourceConfigName("resource-config");
        vo.setStemcellName("openstakc-ubuntu");
        vo.setStemcellVersion("3621.48");
        list.add(vo);
        return list;
    }
}
