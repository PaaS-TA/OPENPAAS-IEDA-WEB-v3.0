package org.openpaas.ieda.deploy.web.information;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.openpaas.ieda.common.BaseControllerUnitTest;
import org.openpaas.ieda.controller.deploy.web.information.snapshot.SnapshotController;
import org.openpaas.ieda.deploy.web.information.snapshot.dto.SnapshotListDTO;
import org.openpaas.ieda.deploy.web.information.snapshot.service.SnapshotService;
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
public class SnapshotControllerUnitTest extends BaseControllerUnitTest {
    
    private MockMvc mockMvc;
    
    @InjectMocks
    private SnapshotController mockSnapshotController;
    @Mock
    private SnapshotService mockSnapshotService;
    
    /*************************************** URL *******************************************/
    final static String VIEW_URL= "/info/snapshot";
    final static String SNAPSHOT_LIST_URL= "/info/snapshot/list/{deploymentName}";
    final static String SNAPSHOT_DELETE_URL= "/info/snapshot/delete/{type}";
    
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출 테스트
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockSnapshotController).build();
        getLoggined();
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스냅샷 화면 호출 테스트
     * @title : testGoListSnapshot
     * @return : void
    ***************************************************/
    @Test
    public void testGoListSnapshot() throws Exception {
        mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/deploy/information/listSnapshot"));
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  스냅샷 정보 목록 조회 테스트
     * @title : testGetSnapshotList
     * @return : void
    *****************************************************************/
    @Test
    public void testGetSnapshotList() throws Exception {
        List<SnapshotListDTO> snapshotList = setSnapshotInfo();
        when(mockSnapshotService.getSnapshotList("deploymentName")).thenReturn(snapshotList);
        mockMvc.perform(get(SNAPSHOT_LIST_URL, "deploymentName").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].id").value(snapshotList.get(0).getRecid()))
        .andExpect(jsonPath("$.records[0].job").value(snapshotList.get(0).getJob()));
        
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  스냅샷 삭제 테스트
     * @title : testDeleteSnapshot
     * @return : void
    *****************************************************************/
    @Test
    public void testDeleteSnapshot() throws Exception {
        SnapshotListDTO dto = setDeleteSnapshotInfo();
        when(mockSnapshotService.deleteSnapshots(anyString(), any())).thenReturn("done");
        mockSnapshotController.deleteSnapshot("iaasType", dto);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드된 스템셀  정보 설정 
     * @title : setSnapshotListInfo
     * @return : List<SnapshotListDTO>
    *****************************************************************/
    public List<SnapshotListDTO> setSnapshotInfo(){
        List<SnapshotListDTO> list = new ArrayList<SnapshotListDTO>();
        SnapshotListDTO snapshot = new SnapshotListDTO();
        snapshot.setJob("JobName");
        snapshot.setUuid("uuid");
        snapshot.setSnapshotCid("SnapshotCid");
        snapshot.setCreatedAt("createdAt");
        snapshot.setClean("clean");
        snapshot.getJob();
        snapshot.getUuid();
        snapshot.getSnapshotCid();
        snapshot.getCreatedAt();
        snapshot.getClean();
        list.add(snapshot);
        return list;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드된 스템셀 삭제 정보 설정 
     * @title : setDeleteSnapshotInfo
     * @return : SnapshotListDTO
    *****************************************************************/
    public SnapshotListDTO setDeleteSnapshotInfo(){
        SnapshotListDTO snapshot = new SnapshotListDTO();
        snapshot.setJob("JobName");
        snapshot.setUuid("uuid");
        snapshot.setSnapshotCid("SnapshotCid");
        snapshot.setCreatedAt("createdAt");
        snapshot.setClean("clean");
        snapshot.getJob();
        snapshot.getUuid();
        snapshot.getSnapshotCid();
        snapshot.getCreatedAt();
        snapshot.getClean();
        return snapshot;
    }
}
