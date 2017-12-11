package org.openpaas.ieda.deploy.web.deploy;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.common.BaseControllerUnitTest;
import org.openpaas.ieda.controller.deploy.web.deploy.servicepack.ServicePackController;
import org.openpaas.ieda.deploy.web.deploy.servicepack.dao.ServicePackVO;
import org.openpaas.ieda.deploy.web.deploy.servicepack.dto.ServicePackParamDTO;
import org.openpaas.ieda.deploy.web.deploy.servicepack.service.ServicePackDeleteDeployAsyncService;
import org.openpaas.ieda.deploy.web.deploy.servicepack.service.ServicePackDeployAsyncService;
import org.openpaas.ieda.deploy.web.deploy.servicepack.service.ServicePackService;
import org.openpaas.ieda.deploy.web.information.manifest.dao.ManifestVO;
import org.openpaas.ieda.deploy.web.information.manifest.service.ManifestService;
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
public class ServicePackControllerUnitTest extends BaseControllerUnitTest{

    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    private Principal principal = null;
    
    @InjectMocks ServicePackController mockServicePackController;
    @Mock ServicePackService mockServicePackService;
    @Mock ManifestService mockManifestService;
    @Mock ServicePackDeployAsyncService mockServicePackDeployAsyncService;
    @Mock ServicePackDeleteDeployAsyncService mockServicePackDeleteDeployAsyncService;
    
    final static String VIEW_URL = "/deploy/servicePack";
    final static String SERVICE_PACK_LIST_URL = "/deploy/servicePack/list/{iaas}";
    final static String MANIFEST_LIST_URL = "/deploy/servicePack/list/manifest";
    final static String SERVICE_PACK_SAVE_URL = "/deploy/servicePack/install/saveServicePackinfo";
    final static String MAKE_DEPLOYMENT_FILE_URL = "/deploy/servicePack/install/createSettingFile/{id}";
    final static String DELETE_SERVIE_PACK_RECORD_URL = "/deploy/servicePack/delete/data";
    final static String SEARCH_MANIFEST_LIST = "/deploy/servicePack/list/manifest/search/{searchVal}";
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockServicePackController).build();
        principal = getLoggined();
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스팩 화면 이동
     * @title : testGoServicePack
     * @return : void
    ***************************************************/
    @Test
    public void testGoServicePack() throws Exception{
        mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/deploy/deploy/servicepack/servicePack"));
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스팩 전체 목록 조회
     * @title : testGetServicePackList
     * @return : void
    ***************************************************/
    @Test
    public void testGetServicePackList() throws Exception{
        String iaasType = "google";
        List<ServicePackVO> servicePacks = setServicePackListInfo();
        when(mockServicePackService.getServicePackList(iaasType)).thenReturn(servicePacks);
        mockMvc.perform(get(SERVICE_PACK_LIST_URL, iaasType).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드된 Manifest 조회 
     * @title : testGetManifestList
     * @return : void
    ***************************************************/
    @Test
    public void testGetManifestList() throws Exception{
        List<ManifestVO> manifests = setManifestInfo();
        when(mockManifestService.getManifestList()).thenReturn(manifests);
        mockMvc.perform(get(MANIFEST_LIST_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스팩 정보 저장 
     * @title : testSaveServicePackInfo
     * @return : void
    ***************************************************/
    @Test
    public void testSaveServicePackInfo() throws Exception {
        ServicePackParamDTO dto =setServicePackInputInfo();
        ServicePackVO info = setServicePackInfo();
        when(mockServicePackService.saveServicePackInfo(dto, principal)).thenReturn(info);
        mockMvc.perform(post(SERVICE_PACK_SAVE_URL).content(mapper.writeValueAsBytes(dto))
        .contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스팩 배포 파일 생성
     * @title : testMakeDeploymentFile
     * @return : void
    ***************************************************/
    @Test
    public void testMakeDeploymentFile() throws Exception {
        doNothing().when(mockServicePackService).makeDeploymentFile(1);
        mockMvc.perform(post(MAKE_DEPLOYMENT_FILE_URL,1).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNoContent());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스 팩 설치
     * @title : testServicePackInstall
     * @return : void
    ***************************************************/
    @Test
    public void testServicePackInstall() {
        ServicePackParamDTO dto =setServicePackInputInfo();
        doNothing().when(mockServicePackDeployAsyncService).deployAsync(dto, principal);
        mockServicePackController.servicePackInstall(dto, principal);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스팩 단순 레코드 삭제
     * @title : testDeleteJustOnlyServicePackRecord
     * @return : void
    ***************************************************/
    @Test
    public void testDeleteJustOnlyServicePackRecord() throws Exception{
        ServicePackParamDTO dto =setServicePackInputInfo();
        doNothing().when(mockServicePackService).deleteServicePackInfoRecord(dto);
        mockMvc.perform(delete(DELETE_SERVIE_PACK_RECORD_URL).content(mapper.writeValueAsBytes(dto))
                .contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNoContent());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스팩 검색
     * @title : testSearchManifestList
     * @return : void
    ***************************************************/
    @Test
    public void testSearchManifestList() throws Exception{
        when(mockManifestService.searchManifestList("mysql")).thenReturn(new ArrayList<ManifestVO>());
        mockMvc.perform(get(SEARCH_MANIFEST_LIST,"mysql").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스팩 플랫폼 삭제
     * @title : testServicePackDelete
     * @return : void
    ***************************************************/
    @Test
    public void testServicePackDelete() {
        ServicePackParamDTO dto =setServicePackInputInfo();
        doNothing().when(mockServicePackDeleteDeployAsyncService).deleteDeployAsync(dto, principal);
        mockServicePackController.servicePackDelete(dto, principal);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스 팩 입력 정보 설정
     * @title : setServicePackInputInfo
     * @return : ServicePackParamDTO
    ***************************************************/
    public ServicePackParamDTO setServicePackInputInfo() {
        ServicePackParamDTO dto = new ServicePackParamDTO();
        dto.setDeploymentFile("mysql-sevicePack-google-1.yml");
        dto.setDeploymentName("mysql-service");
        dto.setDeployStatus(null);
        dto.setIaas("google");
        dto.setId(1);
        
        return dto;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스팩 정보 설정
     * @title : setServicePackInfo
     * @return : ServicePackVO
    ***************************************************/
    public ServicePackVO setServicePackInfo() {
        ServicePackVO vo = new ServicePackVO();
        vo.setCreateDate(null);
        vo.setCreateUserId(null);
        vo.setDeploymentFile("mysql-sevicePack-google-1.yml");
        vo.setDeploymentName("mysql-service");
        vo.setDeployStatus(null);
        vo.setIaas("google");
        vo.setId(0);
        vo.setRecid(null);
        vo.setUpdateDate(null);
        vo.setUpdateUserId(null);
        return vo;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드된 Manifest 정보 설정
     * @title : setManifestInfo
     * @return : List<ManifestVO>
    ***************************************************/
    public List<ManifestVO> setManifestInfo(){
        List<ManifestVO> list = new ArrayList<ManifestVO>();
        ManifestVO vo = new ManifestVO();
        vo.setCreateDate(new Date());
        vo.setCreateUserId(principal.getName());
        vo.setDeploymentName("mysql-service");
        vo.setDeployStatus(null);
        vo.setFileName("mysql-sevicePack-google-1.yml");
        vo.setIaas("google");
        vo.setDescription("desc");
        vo.setPath(".bosh_plugin/deployment/manifest/mysql-sevicePack-google-1.yml");
        vo.setRecid(1);
        vo.setUpdateDate(new Date());
        vo.setUpdateUserId(principal.getName());
        list.add(vo);
        return list;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스팩 전체 목록 정보 설정
     * @title : setServicePackListInfo
     * @return : List<ServicePackVO>
    ***************************************************/
    public List<ServicePackVO> setServicePackListInfo(){
        List<ServicePackVO> list = new ArrayList<ServicePackVO>();
        ServicePackVO vo = new ServicePackVO();
        vo.setCreateDate(new Date());
        vo.setCreateUserId(principal.getName());
        vo.setDeploymentFile("mysql-sevicePack-google-1.yml");
        vo.setDeploymentName("mysql-service");
        vo.setDeployStatus(null);
        vo.setIaas("google");
        vo.setId(1);
        vo.setRecid("1");
        vo.setUpdateDate(new Date());
        vo.setUpdateUserId(principal.getName());
        list.add(vo);
        return list;
    }

}
