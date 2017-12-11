package org.openpaas.ieda.deploy.web.management;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.common.BaseControllerUnitTest;
import org.openpaas.ieda.controller.deploy.web.management.user.UserManagementController;
import org.openpaas.ieda.deploy.web.management.user.dao.UserManagementVO;
import org.openpaas.ieda.deploy.web.management.user.dto.UserManagementDTO;
import org.openpaas.ieda.deploy.web.management.user.service.UserManagementService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

public class UserManagementControllerUnitTest extends BaseControllerUnitTest {

    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    private Principal principal = null;
    
    @InjectMocks
    private UserManagementController mockUserManagementController;
    @Mock
    private UserManagementService mockUserManagementService;
    
    final static String VIEW_USERMNGT_URL = "/admin/user";
    final static String USER_LIST_URL = "/admin/user/list";
    final static String USER_CREATE_URL = "/admin/user/add";
    final static String USER_UPDATE_URL = "/admin/user/update/{userId}";
    final static String USER_DELETE_URL = "/admin/user/delete/{userId}";
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockUserManagementController).build();
        principal = getLoggined();
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 사용자 관리 화면 이동
     * @title : testGoUserManagement
     * @return : void
    ***************************************************/
    @Test
    public void testGoUserManagement() throws Exception{
        mockMvc.perform(get(VIEW_USERMNGT_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/deploy/management/user/userManagement"));
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 사용자 목록 정보 요청
     * @title : testGetUserInfoList
     * @return : void
    ***************************************************/
    @Test
    public void testGetUserInfoList() throws Exception{
        List<UserManagementVO> expectList = setUserManagementInfo();
        when(mockUserManagementService.getUserInfoList()).thenReturn(expectList);
        mockMvc.perform(get(USER_LIST_URL).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 사용자 정보 등록
     * @title : testSaveUserInfo
     * @return : void
    ***************************************************/
    @Test
    public void testSaveUserInfo() throws Exception{
        UserManagementDTO.Regist dto  =  setUserMnagementSaveInfo();
        doNothing().when(mockUserManagementService).savaUserInfo(dto, principal);
        mockMvc.perform(post(USER_CREATE_URL).content(mapper.writeValueAsBytes(dto))
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 사용자 정보 수정
     * @title : testUpdateUserInfo
     * @return : void
    ***************************************************/
    @Test
    public void testUpdateUserInfo() throws Exception{
        UserManagementDTO.Regist dto  =  setUserMnagementSaveInfo();
        doNothing().when(mockUserManagementService).updateUserInfo(dto, "admin");
        mockMvc.perform(put(USER_UPDATE_URL, "admin").content(mapper.writeValueAsBytes(dto))
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 사용자 정보 삭제
     * @title : testDeleteUserInfo
     * @return : void
    ***************************************************/
    @Test
    public void testDeleteUserInfo() throws Exception{
        doNothing().when(mockUserManagementService).deleteUserInfo("admin");
        mockMvc.perform(delete(USER_DELETE_URL, "admin").contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNoContent());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 사용자 목록 정보 설정
     * @title : setUserManagementInfo
     * @return : List<UserManagementVO>
    ***************************************************/
    private List<UserManagementVO> setUserManagementInfo() {
        List<UserManagementVO> list = new ArrayList<UserManagementVO>();
        UserManagementVO vo = new UserManagementVO();
        vo.setCreateDate(new Date());
        vo.setCreateUserId(principal.getName());
        vo.setEmail("test@test.com");
        vo.setInitPassYn("Y");
        vo.setRecid(1);
        vo.setRoleId("1");
        vo.setRoleName("ADMIN");
        vo.setUpdateDate(new Date());
        vo.setUserId("admin");
        vo.setUserName("ADMIN");
        vo.setUserPassword("1234");
        vo.getCreateDate();
        vo.getCreateUserId();
        vo.getEmail();
        vo.getInitPassYn();
        vo.getRecid();
        vo.getRoleId();
        vo.getRoleName();
        vo.getUpdateDate();
        vo.getUserId();
        vo.getUserName();
        vo.getUserPassword();
        return list;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 입력할 사용자 정보 설정
     * @title : setUserMnagementSaveInfo
     * @return : UserManagementDTO.Regist
    ***************************************************/
    private UserManagementDTO.Regist setUserMnagementSaveInfo(){
        UserManagementDTO.Regist dto = new UserManagementDTO.Regist();
        dto.setEmail("admin@test.com");
        dto.setInitPassYn("Y");
        dto.setRoleId("1");
        dto.setUserId("admin");
        dto.setUserName("admin");
        dto.setUserPassword("1234");
        dto.getEmail();
        dto.getInitPassYn();
        dto.getRoleId();
        dto.getUserId();
        dto.getUserName();
        dto.getUserPassword();
        return dto;
    }

}
