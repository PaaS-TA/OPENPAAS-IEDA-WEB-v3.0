package org.openpaas.ieda.deploy.web.information;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.common.BaseControllerUnitTest;
import org.openpaas.ieda.controller.deploy.web.information.vms.VmsController;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.service.BootstrapService;
import org.openpaas.ieda.deploy.web.information.vms.dto.VmsListDTO;
import org.openpaas.ieda.deploy.web.information.vms.service.VmsJobAsyncService;
import org.openpaas.ieda.deploy.web.information.vms.service.VmsLogDownloadService;
import org.openpaas.ieda.deploy.web.information.vms.service.VmsService;
import org.openpaas.ieda.deploy.web.information.vms.service.VmsSnapshotAsyncService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class VmsControllerUnitTest extends BaseControllerUnitTest {
    
    private MockMvc mockMvc;
    private Principal principal = null;
    
    @InjectMocks
    private VmsController mockVmsController;

    @Mock
    private VmsService mockVmsService;
    @Mock
    private VmsLogDownloadService mockVmsLogDownloadService;
    @Mock
    private VmsJobAsyncService mockVmsJobAsyncService;
    @Mock
    private BootstrapService mockBootstrapService;
    @Mock
    private VmsSnapshotAsyncService mockVmsSnapshotAsyncService;
    
    /*************************************** URL *******************************************/
    final static String VIEW_URL= "/info/vms";
    final static String VMS_LIST_URL= "/info/vms/list/{deploymentName}";
    final static String VMS_SNAPSHOT_INFO_URL= "/info/vms/list/snapshot";
    final static String VMS_DOWNLOAD_LOG_URL= "/info/vms/download/{jobName}/{index}/{deploymentName}/{type}";
    final static String VMS_CHANGE_JOB_STATE_URL= "/info/vms/vmLogs/job";
    final static String VMS_TAKE_SNAPSHOT_URL= "/info/vms/snapshotLog/snapshotTaking";
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockVmsController).build();
        principal = getLoggined();
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : VM 정보 화면 호출 테스트
     * @title : testGoListVm
     * @return : void
    ***************************************************/
    @Test
    public void testGoListVm() throws Exception {
        mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/deploy/information/listVm"));
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : VM 정보 목록 조회 테스트
     * @title : testGetVmList
     * @return : void
    ***************************************************/
    @Test
    public void testGetVmList() throws Exception {
        List<VmsListDTO> list = setVmsListDTO();
        when(mockVmsService.getVmList(anyString())).thenReturn(list);
        mockMvc.perform(get(VMS_LIST_URL,"deployment").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].recid").value(list.get(0).getRecid()))
        .andExpect(jsonPath("$.records[0].state").value(list.get(0).getState()))
        .andExpect(jsonPath("$.records[0].az").value(list.get(0).getAz()))
        .andExpect(jsonPath("$.records[0].load").value(list.get(0).getLoad()))
        .andExpect(jsonPath("$.records[0].cpuUser").value(list.get(0).getCpuUser()))
        .andExpect(jsonPath("$.records[0].cpuSys").value(list.get(0).getCpuSys()))
        .andExpect(jsonPath("$.records[0].cpuWait").value(list.get(0).getCpuWait()))
        .andExpect(jsonPath("$.records[0].memoryUsage").value(list.get(0).getMemoryUsage()))
        .andExpect(jsonPath("$.records[0].swapUsage").value(list.get(0).getSwapUsage()))
        .andExpect(jsonPath("$.records[0].diskSystem").value(list.get(0).getDiskSystem()))
        .andExpect(jsonPath("$.records[0].diskEphemeral").value(list.get(0).getDiskEphemeral()))
        .andExpect(jsonPath("$.records[0].diskPersistent").value(list.get(0).getDiskPersistent()));
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스냅샷 사용 여부 테스트
     * @title : testGetSnapshotInfo
     * @return : void
    ***************************************************/
    @Test
    public void testGetSnapshotInfo() throws Exception {
        Map<String, Object> result = setResult();
        when(mockBootstrapService.getSnapshotInfo()).thenReturn(1);
        mockMvc.perform(get(VMS_SNAPSHOT_INFO_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.contents").value(result.get("cnt").toString()));
    }
    
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Agent/Job 로그 다운로드 테스트
     * @title : testDoDoenwloadLog
     * @return : void
    ***************************************************/
    @Test
    public void testDoDoenwloadLog() throws Exception {
        mockVmsLogDownloadService.doDownloadLog(anyString(), anyString(), anyString(), anyString(), any());
        mockMvc.perform(get(VMS_DOWNLOAD_LOG_URL,"jobName","index","deploymentName","type").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Job 관리 테스트
     * @title : testChangeJobState
     * @return : void
    ***************************************************/
    @Test
    public void testChangeJobState() throws Exception {
        VmsListDTO dto = setVmsDTO();
        doNothing().when(mockVmsJobAsyncService).doGetJobLogAsync(any(), any());
        mockVmsController.changeJobState(dto, principal);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스냅샷 생성 테스트
     * @title : testTakeSnapshot
     * @return : void
    ***************************************************/
    @Test
    public void testTakeSnapshot() throws Exception {
        doNothing().when(mockVmsSnapshotAsyncService).doGetSnapshotLogAsync(setVmsDTO(), principal);
        mockVmsController.takeSnapshot(setVmsDTO(), principal);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  Vms목록 정보 설정
     * @title : setVmsListDTO
     * @return : List<VmsListDTO>
    *****************************************************************/
    private List<VmsListDTO> setVmsListDTO() {
        List<VmsListDTO> list = new ArrayList<VmsListDTO>();
        VmsListDTO dto = new VmsListDTO();
        dto.setRecid(1);
        dto.setState("state");
        dto.setAz("az");
        dto.setVmType("vmType");
        dto.setIps("ips");
        dto.setLoad("load");
        dto.setCpuUser("cpuUser");
        dto.setCpuSys("cpuSys");
        dto.setCpuWait("cpuWait");
        dto.setMemoryUsage("memoryUsage");
        dto.setSwapUsage("swapUsage");
        dto.setDiskSystem("diskSystem");
        dto.setDiskEphemeral("diskEphemeral");
        dto.setDiskPersistent("diskPersistent");
        dto.getRecid();
        dto.getState();
        dto.getAz();
        dto.getVmType();
        dto.getIps();
        dto.getLoad();
        dto.getCpuUser();
        dto.getCpuSys();
        dto.getCpuWait();
        dto.getMemoryUsage();
        dto.getSwapUsage();
        dto.getDiskSystem();
        dto.getDiskEphemeral();
        dto.getDiskPersistent();
        list.add(dto);
        
        return list;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  Vms목록 정보 설정
     * @title : setVmsListDTO
     * @return : List<VmsListDTO>
    *****************************************************************/
    private VmsListDTO setVmsDTO() {
        VmsListDTO dto = new VmsListDTO();
        dto.setRecid(1);
        dto.setState("state");
        dto.setAz("az");
        dto.setVmType("vmType");
        dto.setIps("ips");
        dto.setLoad("load");
        dto.setCpuUser("cpuUser");
        dto.setCpuSys("cpuSys");
        dto.setCpuWait("cpuWait");
        dto.setMemoryUsage("memoryUsage");
        dto.setSwapUsage("swapUsage");
        dto.setDiskSystem("diskSystem");
        dto.setDiskEphemeral("diskEphemeral");
        dto.setDiskPersistent("diskPersistent");
        
        return dto;
    }
     /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스냅샷 사용 여부 정보 설정
     * @title : setResult
     * @return : HashMap<String,Object>
    ***************************************************/
    public HashMap<String, Object> setResult() {
         HashMap<String, Object> map = new HashMap<String, Object>();
         map.put("cnt", 1);
         return map;
     }
}
