package org.openpaas.ieda.deploy.web.deploy;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import org.openpaas.ieda.controller.deploy.web.deploy.bootstrap.BootstrapController;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.dao.BootstrapVO;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.dto.BootStrapDeployDTO;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.dto.BootstrapListDTO;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.service.BootstrapDeleteDeployAsyncService;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.service.BootstrapDeployAsyncService;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.service.BootstrapSaveService;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.service.BootstrapService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class BootstrapControllerUnitTest extends BaseControllerUnitTest {
    
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    private Principal principal = null;
    
    @InjectMocks
    private BootstrapController mockBootstrapController;
    @Mock
    private BootstrapService mockBootstrapService;
    @Mock
    private BootstrapSaveService mockBootstrapSaveService;
    @Mock
    private BootstrapDeployAsyncService mockBootstrapDeployAsyncService;
    @Mock
    private BootstrapDeleteDeployAsyncService mockBootstrapDeleteDeployAsyncService;
    
    /*************************************** URL *******************************************/
    final static String BOOTSTRAP_VIEW_URL="/deploy/bootstrap";
    final static String BOOTSTRAP_POP_VIEW_URL="/deploy/bootstrap/install/bootstrapPopup";
    final static String BOOTSTRAP_LIST_URL="/deploy/bootstrap/list";
    final static String BOOTSTRAP_INFO_URL="/deploy/bootstrap/install/detail/{id}";
    final static String SAVE_IAASCONFIG_INFO_URL="/deploy/bootstrap/install/setIaasConfigInfo";
    final static String SAVE_DEFAULT_INFO_URL="/deploy/bootstrap/install/setDefaultInfo";
    final static String SAVE_NETWORK_INFO_URL="/deploy/bootstrap/install/setNetworkInfo";
    final static String SAVE_RESOURCE_INFO_URL="/deploy/bootstrap/install/setResourceInfo";
    final static String CREATE_SETTING_FILE_URL="/deploy/bootstrap/install/createSettingFile/{id}";
    final static String BOOTSTRAP_INSTALL_URL="/deploy/bootstrap/install/bootstrapInstall";
    final static String BOOTSTRAP_DELETE_URL="/deploy/bootstrap/delete/instance";
    final static String BOOTSTRAP_DELETE_INFO_URL="/deploy/bootstrap/delete/data";
    final static String BOOTSTRAP_LOG_INFO_URL="/deploy/bootstrap/list/{id}";
    


    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockBootstrapController).build();
        principal = getLoggined();
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 설치 화면 이동
     * @title : testGoBootstrap
     * @return : void
    *****************************************************************/
    @Test
    public void testGoBootstrap() throws Exception{
        mockMvc.perform(get(BOOTSTRAP_VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/deploy/deploy/bootstrap/bootstrap"));
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 설치 팝업 화면 이동
     * @title : testGoBootstrapPopup
     * @return : void
    *****************************************************************/
    @Test
    public void testGoBootstrapPopup() throws Exception{
        mockMvc.perform(get(BOOTSTRAP_POP_VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/deploy/deploy/bootstrap/bootstrapPopup"));
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 정보 목록 조회
     * @title : testGetBootstrapList
     * @return : void
    *****************************************************************/
    @Test
    public void testGetBootstrapList() throws Exception{
        List<BootstrapListDTO> bootstrapList = setBootstrapList();
        when(mockBootstrapService.getBootstrapList()).thenReturn(bootstrapList);
        mockMvc.perform(get(BOOTSTRAP_LIST_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].recid").value(bootstrapList.get(0).getRecid()))
        .andExpect(jsonPath("$.records[0].id").value(bootstrapList.get(0).getId()))
        .andExpect(jsonPath("$.records[0].iaasConfigAlias").value(bootstrapList.get(0).getIaasConfigAlias()))
        .andExpect(jsonPath("$.records[0].deployStatus").value(bootstrapList.get(0).getDeployStatus()))
        .andExpect(jsonPath("$.records[0].deploymentName").value(bootstrapList.get(0).getDeploymentName()))
        .andExpect(jsonPath("$.records[0].directorName").value(bootstrapList.get(0).getDirectorName()))
        .andExpect(jsonPath("$.records[0].iaas").value(bootstrapList.get(0).getIaas()))
        .andExpect(jsonPath("$.records[0].boshRelease").value(bootstrapList.get(0).getBoshRelease()))
        .andExpect(jsonPath("$.records[0].boshCpiRelease").value(bootstrapList.get(0).getBoshCpiRelease()))
        .andExpect(jsonPath("$.records[0].subnetId").value(bootstrapList.get(0).getSubnetId()))
        .andExpect(jsonPath("$.records[0].subnetRange").value(bootstrapList.get(0).getSubnetRange()))
        .andExpect(jsonPath("$.records[0].publicStaticIp").value(bootstrapList.get(0).getPublicStaticIp()))
        .andExpect(jsonPath("$.records[0].privateStaticIp").value(bootstrapList.get(0).getPrivateStaticIp()))
        .andExpect(jsonPath("$.records[0].subnetGateway").value(bootstrapList.get(0).getSubnetGateway()))
        .andExpect(jsonPath("$.records[0].subnetDns").value(bootstrapList.get(0).getSubnetDns()))
        .andExpect(jsonPath("$.records[0].ntp").value(bootstrapList.get(0).getNtp()))
        .andExpect(jsonPath("$.records[0].stemcell").value(bootstrapList.get(0).getStemcell()))
        .andExpect(jsonPath("$.records[0].instanceType").value(bootstrapList.get(0).getInstanceType()))
        .andExpect(jsonPath("$.records[0].boshPassword").value(bootstrapList.get(0).getBoshPassword()))
        .andExpect(jsonPath("$.records[0].deploymentFile").value(bootstrapList.get(0).getDeploymentFile()))
        .andExpect(jsonPath("$.records[0].deployLog").value(bootstrapList.get(0).getDeployLog()))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 상세 조회
     * @title : testGetBootstrapInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testGetBootstrapInfo() throws Exception{
        BootstrapVO bootstrapInfo = setBootstrapInfo();
        when(mockBootstrapService.getBootstrapInfo(anyInt())).thenReturn(bootstrapInfo);
        mockMvc.perform(get(BOOTSTRAP_INFO_URL, "1").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(bootstrapInfo.getId()))
        .andExpect(jsonPath("$.iaasType").value(bootstrapInfo.getIaasType()))
        .andExpect(jsonPath("$.iaasConfigId").value(bootstrapInfo.getIaasConfigId()))
        .andExpect(jsonPath("$.iaasConfig.accountId").value(bootstrapInfo.getIaasConfig().getAccountId()))
        .andExpect(jsonPath("$.iaasConfig.accountName").value(bootstrapInfo.getIaasConfig().getAccountName()))
        .andExpect(jsonPath("$.iaasConfig.commonKeypairName").value(bootstrapInfo.getIaasConfig().getCommonKeypairName()))
        .andExpect(jsonPath("$.iaasConfig.commonKeypairPath").value(bootstrapInfo.getIaasConfig().getCommonKeypairPath()))
        .andExpect(jsonPath("$.iaasConfig.commonSecurityGroup").value(bootstrapInfo.getIaasConfig().getCommonSecurityGroup()))
        .andExpect(jsonPath("$.iaasConfig.iaasConfigAlias").value(bootstrapInfo.getIaasConfig().getIaasConfigAlias()))
        
        .andExpect(jsonPath("$.deploymentName").value(bootstrapInfo.getDeploymentName()))
        .andExpect(jsonPath("$.directorName").value(bootstrapInfo.getDirectorName()))
        .andExpect(jsonPath("$.boshRelease").value(bootstrapInfo.getBoshRelease()))
        .andExpect(jsonPath("$.boshCpiRelease").value(bootstrapInfo.getBoshCpiRelease()))
        .andExpect(jsonPath("$.enableSnapshots").value(bootstrapInfo.getEnableSnapshots()))
        .andExpect(jsonPath("$.snapshotSchedule").value(bootstrapInfo.getSnapshotSchedule()))
        
        .andExpect(jsonPath("$.subnetId").value(bootstrapInfo.getSubnetId()))
        .andExpect(jsonPath("$.privateStaticIp").value(bootstrapInfo.getPrivateStaticIp()))
        .andExpect(jsonPath("$.publicStaticIp").value(bootstrapInfo.getPublicStaticIp()))
        .andExpect(jsonPath("$.subnetRange").value(bootstrapInfo.getSubnetRange()))
        .andExpect(jsonPath("$.subnetGateway").value(bootstrapInfo.getSubnetGateway()))
        .andExpect(jsonPath("$.subnetDns").value(bootstrapInfo.getSubnetDns()))
        .andExpect(jsonPath("$.ntp").value(bootstrapInfo.getNtp()))
        
        .andExpect(jsonPath("$.stemcell").value(bootstrapInfo.getStemcell()))
        .andExpect(jsonPath("$.cloudInstanceType").value(bootstrapInfo.getCloudInstanceType()))
        .andExpect(jsonPath("$.boshPassword").value(bootstrapInfo.getBoshPassword()))
        .andExpect(jsonPath("$.deploymentFile").value(bootstrapInfo.getDeploymentFile()))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인프라 환경 설정 정보 등록/수정
     * @title : testSaveIaasConfigInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testSaveIaasConfigInfo() throws Exception{
        String requestJson = mapper.writeValueAsString(setIaasConfigInfo());
        when(mockBootstrapSaveService.saveIaasConfigInfo(any(), any())).thenReturn(setBootstrapInfo());
        mockMvc.perform(put(SAVE_IAASCONFIG_INFO_URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(jsonPath("$.iaasConfigId").value(1))
            .andExpect(jsonPath("$.iaasType").value("Openstack"))
            .andExpect(jsonPath("$.id").value(1));
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 기본 정보 저장
     * @title : testSaveDefaultInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testSaveDefaultInfo() throws Exception{
        String requestJson = mapper.writeValueAsString(setDefaultInfo());
        when(mockBootstrapSaveService.saveDefaultInfo(any(), any())).thenReturn(setBootstrapInfo());
        mockMvc.perform(put(SAVE_DEFAULT_INFO_URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.deploymentName").value("bosh"))
            .andExpect(jsonPath("$.directorName").value("test-bosh"))
            .andExpect(jsonPath("$.ntp").value("1.kr.pool.ntp.org, 0.asia.pool.ntp.org"))
            .andExpect(jsonPath("$.boshRelease").value("bosh-257.tgz"))
            .andExpect(jsonPath("$.boshCpiRelease").value("bosh-openstack-cpi-release-14.tgz"))
            .andExpect(jsonPath("$.enableSnapshots").value("true"))
            .andExpect(jsonPath("$.snapshotSchedule").value("0 0 7 * * * schedule"));
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 네트워크 정보 저장
     * @title : testSaveNetworkInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testSaveNetworkInfo() throws Exception{
        String requestJson = mapper.writeValueAsString(setNetworkInfo());
        when(mockBootstrapSaveService.saveNetworkInfo(any(), any())).thenReturn(setBootstrapInfo());
        mockMvc.perform(put(SAVE_NETWORK_INFO_URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.subnetId").value("subnet-12345"))
            .andExpect(jsonPath("$.privateStaticIp").value("10.0.100.11"))
            .andExpect(jsonPath("$.publicStaticIp").value("10.0.20.6"))
            .andExpect(jsonPath("$.subnetRange").value("10.0.20.0/24"))
            .andExpect(jsonPath("$.subnetGateway").value("10.0.20.1"))
            .andExpect(jsonPath("$.subnetDns").value("8.8.8.8"));
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 리소스 정보 저장
     * @title : testSaveResourcesInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testSaveResourcesInfo() throws Exception{
        String requestJson = mapper.writeValueAsString(setResourceInfo());
        when(mockBootstrapSaveService.saveResourceInfo(any(), any())).thenReturn(setBootstrapInfo());
        mockMvc.perform(put(SAVE_RESOURCE_INFO_URL).contentType(MediaType.APPLICATION_JSON).content(requestJson))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.stemcell").value("bosh-stemcell-3421-openstack-kvm-ubuntu-trusty-go_agent.tgz"))
            .andExpect(jsonPath("$.boshPassword").value("1234"))
            .andExpect(jsonPath("$.cloudInstanceType").value("m1.large"));
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 배포 파일 생성
     * @title : testMakeDeploymentFile
     * @return : void
    *****************************************************************/
    @Test
    public void testMakeDeploymentFile() throws Exception{
        doNothing().when(mockBootstrapService).createSettingFile(1);
        mockMvc.perform(post(CREATE_SETTING_FILE_URL, "1").contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 설치
     * @title : testInstallBootstrap
     * @return : void
    *****************************************************************/
    @Test
    public void testInstallBootstrap() throws Exception{
        BootStrapDeployDTO.Install dto = setInstallInfo();
        doNothing().when(mockBootstrapDeployAsyncService).deployAsync(dto, principal);
        mockBootstrapController.installBootstrap(dto, principal);
        verify(mockBootstrapDeployAsyncService, times(1)).deployAsync(dto, principal);
        verifyNoMoreInteractions(mockBootstrapDeployAsyncService);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 배포 삭제
     * @title : testDeleteBootstrap
     * @return : void
    *****************************************************************/
    @Test
    public void testDeleteBootstrap() throws Exception{
        BootStrapDeployDTO.Delete dto = setDeleteInfo();
        doNothing().when(mockBootstrapDeleteDeployAsyncService).deleteDeployAsync(dto, principal);
        mockBootstrapController.deleteBootstrap(dto, principal);
        verify(mockBootstrapDeleteDeployAsyncService, times(1)).deleteDeployAsync(dto, principal);
        verifyNoMoreInteractions(mockBootstrapDeleteDeployAsyncService);
        
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 정보 삭제
     * @title : testDeleteBootstrapInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testDeleteBootstrapInfo() throws Exception{
        BootStrapDeployDTO.Delete dto = setDeleteInfo();
        doNothing().when(mockBootstrapService).deleteBootstrapInfo(any());
        mockMvc.perform(MockMvcRequestBuilders.delete(BOOTSTRAP_DELETE_INFO_URL).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 배포 정보
     * @title : testGetDeployLogMsg
     * @return : void
    *****************************************************************/
    @Test
    public void testGetDeployLogMsg() throws Exception{
       BootstrapVO info = setBootstrapInfo();
        when(mockBootstrapService.getBootstrapInfo(anyInt())).thenReturn(info);
        mockMvc.perform(get(BOOTSTRAP_LOG_INFO_URL, "1").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(content().string("log..."))
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN));
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 목록 정보 설정
     * @title : setBootstrapList
     * @return : List<BootstrapListDTO>
    *****************************************************************/
    public List<BootstrapListDTO> setBootstrapList(){
        List<BootstrapListDTO> listDtos = new ArrayList<BootstrapListDTO>();
        BootstrapListDTO dto = new BootstrapListDTO();
        dto.setRecid(1);
        dto.setId(1);
        dto.setIaasConfigAlias("aws-config1");
        dto.setDeployStatus("사용안함");
        dto.setDeploymentName("bosh");
        dto.setDirectorName("bosh");
        dto.setIaas("AWS");
        dto.setBoshRelease("bosh-256.tgz");
        dto.setBoshCpiRelease("bosh-aws-cpi-release-14.tgz");
        dto.setSubnetId("subnet-test");
        dto.setSubnetRange("10.0.0.0/24");
        dto.setPublicStaticIp("52.16.23.10");
        dto.setPrivateStaticIp("10.0.20.10");
        dto.setSubnetGateway("10.0.0.1");
        dto.setSubnetDns("8.8.8.8");
        dto.setNtp("1.kr.pool.ntp.org, 0.asia.pool.ntp.org");
        dto.setStemcell("light-bosh-stemcell-3147-aws-xen-ubuntu-trusty-go_agent.tgz");
        dto.setInstanceType("m1.large");
        dto.setBoshPassword("testCloud");
        dto.setDeploymentFile("aws-microbosh-1.yml");
        dto.setDeployLog("test..");
        
        listDtos.add(dto);
        return listDtos;
    }
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 상세 정보 설정
     * @title : setBootstrapInfo
     * @return : BootstrapVO
    *****************************************************************/
    public BootstrapVO setBootstrapInfo(){
        BootstrapVO vo = new BootstrapVO();
        vo.setId(1);
        vo.setIaasType("Openstack");
        vo.setIaasConfigId(1);
        vo.getIaasConfig().setAccountId(1);
        vo.getIaasConfig().setAccountName("openstack_v2");
        vo.getIaasConfig().setCommonKeypairName("bosh-key");
        vo.getIaasConfig().setCommonKeypairPath("bosh-key.pem");
        vo.getIaasConfig().setCommonSecurityGroup("bosh-security");
        vo.getIaasConfig().setIaasConfigAlias("openstack-config1");
        
        //기본정보
        vo.setDeploymentName("bosh");
        vo.setDirectorName("test-bosh");
        vo.setBoshRelease("bosh-257.tgz");
        vo.setBoshCpiRelease("bosh-openstack-cpi-release-14.tgz");
        vo.setEnableSnapshots("true");
        vo.setSnapshotSchedule("0 0 7 * * * schedule");
        vo.setNtp("1.kr.pool.ntp.org, 0.asia.pool.ntp.org");
        
        //네트워크
        vo.setSubnetId("subnet-12345");
        vo.setPrivateStaticIp("10.0.100.11");
        vo.setPublicStaticIp("10.0.20.6");
        vo.setSubnetRange("10.0.20.0/24");
        vo.setSubnetGateway("10.0.20.1");
        vo.setSubnetDns("8.8.8.8");
        
        //리소스
        vo.setStemcell("bosh-stemcell-3421-openstack-kvm-ubuntu-trusty-go_agent.tgz");
        vo.setCloudInstanceType("m1.large");
        vo.setBoshPassword("1234");
        vo.setDeploymentFile("openstack-microbosh-test-1.yml");
        
        vo.setDeployLog("log...");
        
        return vo;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인프라 환경 설정 정보 설정
     * @title : setIaasConfigInfo
     * @return : BootStrapDeployDTO.IaasConfig
    *****************************************************************/
    public BootStrapDeployDTO.IaasConfig setIaasConfigInfo(){
        BootStrapDeployDTO.IaasConfig configInfo = new BootStrapDeployDTO.IaasConfig();
        configInfo.setIaasConfigId("1");
        configInfo.setIaasType("Openstack");
        
        return configInfo; 
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 기본 정보 설정
     * @title : setDefaultInfo
     * @return : BootStrapDeployDTO.Default
    *****************************************************************/
    public BootStrapDeployDTO.Default setDefaultInfo(){
        BootStrapDeployDTO.Default defaultInfo = new BootStrapDeployDTO.Default();
        defaultInfo.setId("1");
        defaultInfo.setDeploymentName("bosh");
        defaultInfo.setDirectorName("test-bosh");
        defaultInfo.setBoshRelease("bosh-257.tgz");
        defaultInfo.setBoshCpiRelease("bosh-openstack-cpi-release-14.tgz");
        defaultInfo.setEnableSnapshots("true");
        defaultInfo.setSnapshotSchedule("0 0 7 * * * schedule");
        defaultInfo.setNtp("1.kr.pool.ntp.org, 0.asia.pool.ntp.org");
        
        return defaultInfo;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 네트워크 정보 설정
     * @title : setNetworkInfo
     * @return : BootStrapDeployDTO.Network
    *****************************************************************/
    public BootStrapDeployDTO.Network setNetworkInfo(){
        BootStrapDeployDTO.Network network = new BootStrapDeployDTO.Network();
        network.setId("1");
        network.setSubnetId("text-subnetId-12345");
        network.setPrivateStaticIp("10.0.100.11");
        network.setPublicStaticIp("10.0.20.6");
        network.setSubnetRange("10.0.20.0/24");
        network.setSubnetGateway("10.0.20.1");
        network.setSubnetDns("8.8.8.8");
        
        return network;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 리소스 정보 설정
     * @title : setResourceInfo
     * @return : BootStrapDeployDTO.Resource
    *****************************************************************/
    public BootStrapDeployDTO.Resource setResourceInfo(){
        BootStrapDeployDTO.Resource resource = new BootStrapDeployDTO.Resource();
        resource.setId("1");
        resource.setStemcell("bosh-stemcell-3421-openstack-kvm-ubuntu-trusty-go_agent.tgz");
        resource.setCloudInstanceType("m1.large");
        resource.setBoshPassword("1234");
        
        return resource;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 설치 정보 설정
     * @title : setInstallInfo
     * @return : BootStrapDeployDTO.Install
    *****************************************************************/
    public BootStrapDeployDTO.Install setInstallInfo(){
        BootStrapDeployDTO.Install install = new BootStrapDeployDTO.Install();
        install.setId("1");
        install.setIaasType("Openstack");
        return install;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 삭제 정보 설정
     * @title : setDeleteInfo
     * @return : BootStrapDeployDTO.Delete
    *****************************************************************/
    public BootStrapDeployDTO.Delete setDeleteInfo(){
        BootStrapDeployDTO.Delete delete = new BootStrapDeployDTO.Delete();
        delete.setId("1");
        delete.setIaasType("Openstack");
        return delete;
    }
    
}
