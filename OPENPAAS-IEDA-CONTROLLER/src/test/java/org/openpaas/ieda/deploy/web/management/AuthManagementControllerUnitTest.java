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
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.common.BaseControllerUnitTest;
import org.openpaas.ieda.controller.deploy.web.management.auth.AuthManagementController;
import org.openpaas.ieda.deploy.web.management.auth.dao.AuthManagementVO;
import org.openpaas.ieda.deploy.web.management.auth.dto.AuthManagementDTO;
import org.openpaas.ieda.deploy.web.management.auth.service.AuthManagementService;
import org.openpaas.ieda.deploy.web.management.code.dao.CommonCodeVO;
import org.openpaas.ieda.deploy.web.management.code.service.CommonCodeService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthManagementControllerUnitTest extends BaseControllerUnitTest {

    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    private Principal principal = null;
    
    @InjectMocks
    AuthManagementController mockAuthManagementController;
    @Mock
    AuthManagementService mockAuthManagementService;
    @Mock
    CommonCodeService mockCommonCodeService;
    
    final static String VIEW_AUTHMNGT_URL = "/admin/role";
    final static String AUTH_GROUP_LIST_URL = "/admin/role/group/list";
    final static String AUTH_GROUP_SUB_LIST_URL = "/admin/role/group/{roleId}";
    final static String AUTH_GROUP_CREATE_CODE_LIST_URL = "/admin/role/commonCodeList";
    final static String AUTH_GROUP_CREATE_URL = "/admin/role/group/add";
    final static String AUTH_GROUP_DELETE_URL = "/admin/role/group/delete/{roleId}";
    final static String AUTH_GROUP_UPDATE_URL = "/admin/role/group/update/{roleId}";
    final static String AUTH_CREATE_URL = "/admin/role/detail/update/{roleId}";
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockAuthManagementController).build();
        principal = getLoggined();
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 권한 관리 화면 이동
     * @title : testGoAuthManagement
     * @return : void
    ***************************************************/
    @Test
    public void testGoAuthManagement() throws Exception{
        mockMvc.perform(get(VIEW_AUTHMNGT_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/deploy/management/auth/authManagement"));
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 권한 그룹 리스트 조회
     * @title : testGetRoleGroupList
     * @return : void
    ***************************************************/
    @Test
    public void testGetRoleGroupList() throws Exception{
        List<AuthManagementVO> expectList = setAuthManagementInfo();
        when(mockAuthManagementService.getRoleGroupList()).thenReturn(expectList);
        mockMvc.perform(get(AUTH_GROUP_LIST_URL).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 권한 그룹 하위 코드 목록 조회
     * @title : testGetRoleDetailList
     * @return : void
    ***************************************************/
    @Test
    public void testGetRoleDetailList() throws Exception{
        List<HashMap<String,Object>> expectList = setRoleDetailListInfo();
        when(mockAuthManagementService.getRoleDetailList(1)).thenReturn(expectList);
        mockMvc.perform(get(AUTH_GROUP_SUB_LIST_URL, 1).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 권한 그룹 등록 요청 시 공통 코드 요청
     * @title : testGetCommonCodeList
     * @return : void
    ***************************************************/
    @Test
    public void testGetCommonCodeList() throws Exception{
        List<CommonCodeVO> list  = setCommonCodeList();
        when(mockCommonCodeService.getCommonCodeList()).thenReturn(list);
        mockMvc.perform(get(AUTH_GROUP_CREATE_CODE_LIST_URL).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 권한 그룹 저장
     * @title : testSaveRoleInfo
     * @return : void
    ***************************************************/
    @Test
    public void testSaveRoleInfo() throws Exception{
        AuthManagementDTO.Regist dto  =  setRoleGroupInfo();
        when(mockAuthManagementService.saveRoleInfo(dto)).thenReturn(true);
        mockMvc.perform(post(AUTH_GROUP_CREATE_URL).content(mapper.writeValueAsBytes(dto))
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 권한 그룹 삭제 요청
     * @title : testDeleteRole
     * @return : void
    ***************************************************/
    @Test
    public void testDeleteRole() throws Exception{
        when(mockAuthManagementService.deleteRole(1)).thenReturn(true);
        mockMvc.perform(delete(AUTH_GROUP_DELETE_URL, 1).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNoContent());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 권한 그룹 수정 요청
     * @title : testUpdateRole
     * @return : void
    ***************************************************/
    @Test
    public void testUpdateRole() throws Exception{
        AuthManagementDTO.Regist dto  =  setRoleGroupInfo();
        when(mockAuthManagementService.updateRole(1, dto)).thenReturn(true);
        mockMvc.perform(put(AUTH_GROUP_UPDATE_URL, 1).content(mapper.writeValueAsBytes(dto))
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk());
    }
    
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 권한 상세 수정(등록) 요청
     * @title : testSaveRoleDetail
     * @return : void
    ***************************************************/
    @Test
    public void testSaveRoleDetail() throws Exception{
        AuthManagementDTO.Regist dto  =  setRoleGroupInfo();
        doNothing().when(mockAuthManagementService).saveRoleDetail(1, dto);
        mockMvc.perform(post(AUTH_CREATE_URL, 1).content(mapper.writeValueAsBytes(dto))
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 권한 정보 설정
     * @title : setAuthManagementInfo
     * @return : List<AuthManagementVO>
    ***************************************************/
    private List<AuthManagementVO> setAuthManagementInfo(){
        List<AuthManagementVO> list = new ArrayList<AuthManagementVO>();
        AuthManagementVO vo = new AuthManagementVO();
        vo.setActiveYn(new ArrayList<String>());
        vo.setAuthCode("100000");
        vo.setCreateDate(new Date());
        vo.setCreateUserId(principal.getName());
        vo.setUpdateUserId(principal.getName());
        vo.setRecid(1);
        vo.setRoleId(1);
        vo.setRoleName("role_test");
        vo.setSeq("1");
        vo.setUpdateDate(new Date());
        vo.setUsubGroupCode("");
        vo.setRoleDescription("desc");
        vo.getCreateDate();
        vo.getCreateUserId();
        vo.getUpdateUserId();
        vo.getUpdateDate();
        vo.getRecid();
        vo.getRoleId();
        vo.getRoleDescription();
        vo.getUsubGroupCode();
        vo.getSeq();
        vo.getRoleName();
        list.add(vo);
        return list;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 권한 그룹 하위 코드 목록 정보 설정
     * @title : setRoleDetailListInfo
     * @return : List<HashMap<String,Object>>
    ***************************************************/
    private List<HashMap<String,Object>> setRoleDetailListInfo(){
        List<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
        HashMap<String,Object> map =  new HashMap<String,Object>();
        map.put("code_idx", 1);
        map.put("code_name_kr", "kr");
        map.put("auth_code", 10000);
        map.put("usub_group_code", 110000);
        list.add(map);
        return list;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 권한 그룹 등록 요청 시 공통 코드 정보 설정
     * @title : setCommonCodeList
     * @return : List<CommonCodeVO>
    ***************************************************/
    private List<CommonCodeVO> setCommonCodeList(){
        List<CommonCodeVO> list = new ArrayList<CommonCodeVO>();
        CommonCodeVO vo = new CommonCodeVO();
        vo.setCodeDescription("auth_test");
        vo.setCodeIdx(1);
        vo.setCodeName("auth_code");
        vo.setCodeNameKR("권한 테스트");
        vo.setCodeValue("auth_value");
        vo.setCreateUserId(principal.getName());
        vo.setParentCode("100000");
        vo.setRecid(1);
        vo.setSortOrder(1);
        vo.setSubGroupCode("110000");
        vo.setUpdateUserId(principal.getName());
        vo.getCodeDescription();
        vo.getCodeIdx();
        vo.getCodeName();
        vo.getCodeNameKR();
        vo.getCodeValue();
        vo.getCreateUserId();
        vo.getUpdateUserId();
        vo.getParentCode();
        vo.getRecid();
        vo.getSortOrder();
        vo.getSubGroupCode();
        list.add(vo);
        return list;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 권한 그룹 정보 설정
     * @title : setRoleGroupInfo
     * @return : AuthManagementDTO.Regist
    ***************************************************/
    private AuthManagementDTO.Regist setRoleGroupInfo(){
        AuthManagementDTO.Regist dto = new AuthManagementDTO.Regist();
        dto.setActiveYn(new ArrayList<String>());
        dto.setRoleDescription("role_desc");
        dto.setRoleId("1");
        dto.setRoleName("DEPLOY_TYPE");
        dto.getRoleDescription();
        dto.getRoleId();
        dto.getRoleName();
        return dto;
    }

}
