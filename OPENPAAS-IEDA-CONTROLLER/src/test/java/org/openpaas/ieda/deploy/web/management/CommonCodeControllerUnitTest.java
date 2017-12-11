package org.openpaas.ieda.deploy.web.management;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.common.BaseControllerUnitTest;
import org.openpaas.ieda.controller.deploy.web.management.code.CommonCodeController;
import org.openpaas.ieda.deploy.web.management.code.dao.CommonCodeDAO;
import org.openpaas.ieda.deploy.web.management.code.dao.CommonCodeVO;
import org.openpaas.ieda.deploy.web.management.code.dto.CommonCodeDTO;
import org.openpaas.ieda.deploy.web.management.code.service.CommonCodeService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CommonCodeControllerUnitTest extends BaseControllerUnitTest {
    
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    
    @InjectMocks
    CommonCodeController mockCommonCodeController;
    @Mock
    CommonCodeService mockCommonCodeService;
    @Mock
    CommonCodeDAO mockCommonCodeDAO;
    
    final static String VIEW_CODEMNGT_URL = "/admin/code";
    final static String CODE_GROUP_LIST_URL = "/admin/code/groupList";
    final static String CODE_LIST_URL = "/admin/code/codeList/{parentCode}";
    final static String CODE_CREATE_URL = "/admin/code/add";
    final static String CODE_UPDATE_URL = "/admin/code/update/{codeIdx}";
    final static String CODE_DELETE_URL = "/admin/code/delete/{codeIdx}";
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockCommonCodeController).build();
        getLoggined();
    }
    
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 코드 관리 화면 이동
    * @title : testGoCodeManagement
    * @return : void
    ***************************************************/
    @Test
    public void testGoCodeManagement() throws Exception{
        mockMvc.perform(get(VIEW_CODEMNGT_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/deploy/management/code/codeManagement"));
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 코드 그룹 조회
    * @title : testGetCodeGroupListInfo
    * @return : void
    ***************************************************/
    @Test
    public void testGetCodeGroupListInfo() throws Exception{
        List<CommonCodeVO> expectList = setResultCodeInfo();
        when(mockCommonCodeDAO.selectParentCodeIsNull()).thenReturn(expectList);
        mockMvc.perform(get(CODE_GROUP_LIST_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].codeIdx").value(1))
        .andExpect(jsonPath("$.records[0].codeName").value("testCode"))
        .andExpect(jsonPath("$.records[0].codeValue").value("testCode"))
        .andExpect(jsonPath("$.records[0].updateUserId").value("admin"))
        .andExpect(jsonPath("$.records[0].createUserId").value("admin"))
        .andExpect(jsonPath("$.records[0].recid").value(1))
        .andExpect(jsonPath("$.records[0].sortOrder").value(1))
        .andExpect(jsonPath("$.records[0].codeDescription").value("testCode"));
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 코드 목록 조회
    * @title : testGetCodeListInfo
    * @return : void
    ***************************************************/
    @Test
    public void testGetCodeListInfo() throws Exception{
        List<CommonCodeVO> expectList = setResultCodeInfo();
        when(mockCommonCodeService.getCodeList(anyString())).thenReturn(expectList);
        mockMvc.perform(get(CODE_LIST_URL, "1").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].codeIdx").value(1))
        .andExpect(jsonPath("$.records[0].codeName").value("testCode"))
        .andExpect(jsonPath("$.records[0].codeValue").value("testCode"))
        .andExpect(jsonPath("$.records[0].updateUserId").value("admin"))
        .andExpect(jsonPath("$.records[0].createUserId").value("admin"))
        .andExpect(jsonPath("$.records[0].recid").value(1))
        .andExpect(jsonPath("$.records[0].sortOrder").value(1))
        .andExpect(jsonPath("$.records[0].codeDescription").value("testCode"));
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 코드 그룹 등록
    * @title : testCreateCodeParentCodeNotNull
    * @return : void
    ***************************************************/
    @Test
    public void testCreateCodeParentCodeNotNull() throws Exception{
        CommonCodeDTO.Regist dto = setCodeSaveInfo("null");
        mockMvc.perform(post(CODE_CREATE_URL).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isCreated());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 코드 등록
    * @title : testCreateCodeParentCodeNull
    * @return : void
    ***************************************************/
    @Test
    public void testCreateCodeParentCodeNull() throws Exception{
        CommonCodeDTO.Regist dto = setCodeSaveInfo("notNull");
        mockMvc.perform(post(CODE_CREATE_URL).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isCreated());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 코드 수정
    * @title : testCreateCodeParentCodeNull
    * @return : void
    ***************************************************/
    @Test
    public void testUpdateCode() throws Exception{
        CommonCodeDTO.Regist dto = setCodeSaveInfo("null");
        mockMvc.perform(put(CODE_UPDATE_URL, 1).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 코드 삭제
    * @title : testDeleteCode
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteCode() throws Exception{
        CommonCodeDTO.Regist dto = setCodeSaveInfo("null");
        mockMvc.perform(delete(CODE_DELETE_URL, 1).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 코드 등록 정보 설정
    * @title : setCodeSaveInfo
    * @return : void
    ***************************************************/
    public CommonCodeDTO.Regist setCodeSaveInfo(String type) {
        CommonCodeDTO.Regist dto = new CommonCodeDTO.Regist();
        dto.setCodeDescription("testCode");
        dto.setCodeIdx(1);
        dto.setCodeName("testCode");
        dto.setCodeNameKR("코드테스트");
        dto.setCodeValue("testCode");
        dto.setRecid(1);
        dto.setSubGroupCode("testCode");
        dto.getCodeDescription();
        dto.getCodeIdx();
        dto.getCodeName();
        dto.getCodeNameKR();
        dto.getRecid();
        dto.getSubGroupCode();
        if(!"null".equalsIgnoreCase(type)){
            dto.setParentCode("test");
            dto.getParentCode();
        }
        return dto;
    }

    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 코드 관련 Result 값 설정
    * @title : testGoCodeManagement
    * @return : void
    ***************************************************/
    public List<CommonCodeVO> setResultCodeInfo() {
        List<CommonCodeVO> list = new ArrayList<CommonCodeVO>();
        CommonCodeVO vo = new CommonCodeVO();
        vo.setCodeDescription("testCode");
        vo.setCodeIdx(1);
        vo.setCodeName("testCode");
        vo.setCodeNameKR("코드테스트");
        vo.setCodeValue("testCode");
        vo.setCreateUserId("admin");
        vo.setParentCode("100000");
        vo.setRecid(1);
        vo.setSortOrder(1);
        vo.setSubGroupCode("testCode");
        vo.setUpdateUserId("admin");
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

}
