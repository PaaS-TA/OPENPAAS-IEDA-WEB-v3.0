package org.openpaas.ieda.deploy.web.information;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.openpaas.ieda.controller.deploy.web.information.task.TaskController;
import org.openpaas.ieda.deploy.api.task.TaskListDTO;
import org.openpaas.ieda.deploy.web.information.task.dto.TaskDTO;
import org.openpaas.ieda.deploy.web.information.task.service.TaskAsyncService;
import org.openpaas.ieda.deploy.web.information.task.service.TaskService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class TaskControllerUnitTest  extends BaseControllerUnitTest {
    
    private MockMvc mockMvc;
    
    @InjectMocks
    private TaskController mockTaskController;
    @Mock
    private TaskService mockTaskService;
    @Mock
    private TaskAsyncService mockTaskAsyncService;
    private Principal principal = null;
    
    /*************************************** URL *******************************************/
    final static String VIEW_URL= "/info/task";
    final static String TASK_LIST_URL= "/info/task/list";
    final static String TASK_DEBUGLOG_LIST_URL= "/info/task/list/debugLog/{id}";
    final static String TASK_EVENTLOG_LIST_URL= "/info/task/list/eventLog/task";
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockTaskController).build();
        principal = getLoggined();
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Task 정보 화면을 호출하여 이동 테스트
     * @title : goListTaskHistory
     * @return : void
    *****************************************************************/
    @Test
    public void testGoListTaskHistory() throws Exception {
         mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/deploy/information/listTaskHistory"));
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Task 실행 이력 정보 목록을 조회 테스트
     * @title : testListTaskHistory
     *  @return : void
    *****************************************************************/
    @Test
    public void testListTaskHistory() throws Exception {
        List<TaskListDTO> taskList = setTaskHistoryList();
        when(mockTaskService.listTask()).thenReturn(taskList);
        mockMvc.perform(get(TASK_LIST_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].recid").value(taskList.get(0).getRecid()))
        .andExpect(jsonPath("$.records[0].id").value(taskList.get(0).getId()))
        .andExpect(jsonPath("$.records[0].state").value(taskList.get(0).getState()))
        .andExpect(jsonPath("$.records[0].runTime").value(taskList.get(0).getRunTime()))
        .andExpect(jsonPath("$.records[0].user").value(taskList.get(0).getUser()))
        .andExpect(jsonPath("$.records[0].description").value(taskList.get(0).getDescription()))
        .andExpect(jsonPath("$.records[0].result").value(taskList.get(0).getResult()));
    }
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Task 디버그 로그 파일 다운로드 테스트
     * @title : testDoDownloadTaskLog
     * @return : void
    *****************************************************************/
    @Test
    public void testDoDownloadTaskLog() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        doNothing().when(mockTaskService).getDownloadDebugLogFile("1", response);
        mockMvc.perform(get(TASK_DEBUGLOG_LIST_URL, "1").contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print());
    }
    
    /****************************************************************
    * @project : Paas 플랫폼 설치 자동화 테스트
    * @description : 이벤트 로그 조회
    * @title : testDoGetTaskLog
    *  @return : void
   *****************************************************************/
    @Test
    public void testDoGetTaskLog() throws Exception {
        doNothing().when(mockTaskAsyncService).doGetTaskLogAsync("event", "1", "", principal);
        mockTaskController.doGetTaskLog(setTaskLogInfo(), principal);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  Task 실행 이력 목록 정보 설정
     * @title : setTaskHistoryList
     * @return : List<TaskListDTO>
    *****************************************************************/
    private List<TaskListDTO> setTaskHistoryList() {
        List<TaskListDTO> list = new ArrayList<TaskListDTO>();
        TaskListDTO dto = new TaskListDTO();
        dto.setRecid(1);
        dto.setId("taskId");
        dto.setState("state");
        dto.setRunTime("runTime");
        dto.setUser("user");
        dto.setDescription("description");
        dto.setResult("result");
        dto.getRecid();
        dto.getState();
        dto.getRunTime();
        dto.getUser();
        dto.getDescription();
        dto.getResult();
        dto.getDeployment();
        dto.getTimestamp();
        dto.getStartedAt();
        list.add(dto);
        
        return list;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 이벤트 로그 조회 정보 설정
     * @title : setTaskLogInfo
     * @return : TaskDTO.GetLog
    ***************************************************/
    private TaskDTO.GetLog setTaskLogInfo(){
        TaskDTO.GetLog dto = new TaskDTO.GetLog();
        dto.setTaskId("1");
        dto.setLogType("event");
        dto.setLineOneYn("");
        dto.getTaskId();
        dto.getLogType();
        dto.getLineOneYn();
        return dto;
    }
}
