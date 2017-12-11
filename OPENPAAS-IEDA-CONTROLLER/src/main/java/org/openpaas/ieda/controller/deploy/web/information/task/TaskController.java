package org.openpaas.ieda.controller.deploy.web.information.task;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.websocket.server.ServerEndpoint;

import org.openpaas.ieda.controller.common.BaseController;
import org.openpaas.ieda.deploy.api.task.TaskListDTO;
import org.openpaas.ieda.deploy.web.information.task.dto.TaskDTO;
import org.openpaas.ieda.deploy.web.information.task.service.TaskAsyncService;
import org.openpaas.ieda.deploy.web.information.task.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@ServerEndpoint("/task")
public class TaskController extends BaseController {

    @Autowired private TaskService taskService;
    @Autowired private TaskAsyncService taskAsyncservice;

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Task 정보 화면을 호출하여 이동
     * @title : goListTaskHistory
     * @return : String
     *****************************************************************/
    @RequestMapping(value = "/info/task", method = RequestMethod.GET)
    public String goListTaskHistory() {
        return "/deploy/information/listTaskHistory";
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Task 실행 이력 정보 목록을 조회
     * @title : listTaskHistory
     * @return : ResponseEntity<HashMap<String,Object>>
     *****************************************************************/
    @RequestMapping(value = "/info/task/list", method = RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> listTaskHistory() {
        List<TaskListDTO> contents = taskService.listTask();
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("total", contents.size());
        result.put("records", contents);

        return new ResponseEntity<HashMap<String, Object>>(result, HttpStatus.OK);
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Task 디버그 로그 파일 다운로드
     * @title : doDownloadTaskLog
     * @return : void
     *****************************************************************/
    @RequestMapping(value = "/info/task/list/debugLog/{id}", method = RequestMethod.GET)
    public void doDownloadTaskLog(@PathVariable("id") String taskId, HttpServletResponse response) {
        taskService.getDownloadDebugLogFile(taskId, response);
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 이벤트 로그 조회
     * @title : doGetTaskLog
     * @return : ResponseEntity<Object>
     *****************************************************************/
    @MessageMapping("/info/task/list/eventLog/task")
    @SendTo("/info/task/list/eventLog/socket")
    public ResponseEntity<Object> doGetTaskLog(@RequestBody @Valid TaskDTO.GetLog dto, Principal principal) {
        taskAsyncservice.doGetTaskLogAsync(dto.getLogType(), dto.getTaskId(), dto.getLineOneYn(), principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
