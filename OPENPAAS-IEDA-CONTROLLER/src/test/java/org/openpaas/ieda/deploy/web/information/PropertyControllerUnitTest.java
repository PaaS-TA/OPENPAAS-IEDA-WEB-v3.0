package org.openpaas.ieda.deploy.web.information;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
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

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.common.BaseControllerUnitTest;
import org.openpaas.ieda.controller.deploy.web.information.property.PropertyController;
import org.openpaas.ieda.deploy.web.information.property.PropertyVO;
import org.openpaas.ieda.deploy.web.information.property.dto.PropertyDTO;
import org.openpaas.ieda.deploy.web.information.property.service.PropertyService;
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
public class PropertyControllerUnitTest  extends BaseControllerUnitTest {
    
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    @InjectMocks
    private PropertyController mockPropertyController;
    @Mock
    private PropertyService mockPropertyService;

    /*************************************** URL *******************************************/
    final static String VIEW_URL= "/info/property";
    final static String PROPERTY_LIST_URL= "/info/property/list/{deployment}";
    final static String PROPERTY_DETAIL_LIST_URL= "/info/property/list/detailInfo/deploymentName/name";
    final static String CREATE_PROPERTY_URL= "/info/property/modify/createProperty";
    final static String UPDATE_PROPERTY_URL= "/info/property/modify/updateProperty";
    final static String DELETE_PROPERTY_URL= "/info/property/modify/deleteProperty";
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockPropertyController).build();
        getLoggined();
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Property 화면 이동 테스트
     * @title : testGoProperty
     * @return : void
    ***************************************************/
    @Test
    public void testGoProperty() throws Exception {
        mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/deploy/information/listProperty"));
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Property 목록 정보 조회 테스트
     * @title : testGetPropertyList
     * @return : void
    ***************************************************/
    @Test
    public void testGetPropertyList() throws Exception {
        List<PropertyDTO> list = setPropertyList();
        when(mockPropertyService.getPropertyList((anyString()))).thenReturn(list);
        mockMvc.perform(get(PROPERTY_LIST_URL, "deployment").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].recid").value(list.get(0).getRecid()))
        .andExpect(jsonPath("$.records[0].name").value(list.get(0).getName()))
        .andExpect(jsonPath("$.records[0].value").value(list.get(0).getValue()))
        .andExpect(jsonPath("$.records[0].deploymentName").value(list.get(0).getDeploymentName()));
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Property 상세 정보 확인 테스트
     * @title : testGetPropertyDetailInfo
     * @return : void
    ***************************************************/
    @Test
    public void testGetPropertyDetailInfo() throws Exception {
        PropertyVO propertyDetailInfo = setPropertyDetailInfo();
        when(mockPropertyService.getPropertyDetailInfo(anyString(), anyString())).thenReturn(propertyDetailInfo);
        mockMvc.perform(get(PROPERTY_DETAIL_LIST_URL, "deploymentName","name").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(propertyDetailInfo.getName()))
        .andExpect(jsonPath("$.value").value(propertyDetailInfo.getValue()))
        .andExpect(jsonPath("$.deploymentName").value(propertyDetailInfo.getDeploymentName()));
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Property 정보 생성 테스트
     * @title : testCreatePropertyInfo
     * @return : void
    ***************************************************/
    @Test
    public void testCreatePropertyInfo() throws Exception {
        doNothing().when(mockPropertyService).createProperyInfo(any());
        mockMvc.perform(post(CREATE_PROPERTY_URL).content(mapper.writeValueAsBytes(setPropertyDetailInfo())).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Property 정보 수정 테스트
     * @title : testUpdatePropertyInfo
     * @return : void
    ***************************************************/
    @Test
    public void testUpdatePropertyInfo() throws Exception {
        doNothing().when(mockPropertyService).updateProperyInfo(any());
        mockMvc.perform(put(UPDATE_PROPERTY_URL).content(mapper.writeValueAsBytes(setPropertyDetailInfo())).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Property 삭제 테스트
     * @title : testDeletePropertyInfo
     * @return : void
    ***************************************************/
    @Test
    public void testDeletePropertyInfo() throws Exception {
        doNothing().when(mockPropertyService).deleteProperyInfo(any());
        mockMvc.perform(delete(DELETE_PROPERTY_URL).content(mapper.writeValueAsBytes(setPropertyDetailInfo())).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNoContent());
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  Property 리스트 결과 값 설정
     * @title : setPropertyList
     * @return : List<PropertyDTO>
    *****************************************************************/
    private List<PropertyDTO> setPropertyList() {
        List<PropertyDTO> list = new ArrayList<PropertyDTO>();
        PropertyDTO dto = new PropertyDTO();
        dto.setRecid(1);
        dto.setDeploymentName("deploymentName");
        dto.setName("name");
        dto.setValue("value");
        dto.getRecid();
        dto.getDeploymentName();
        dto.getName();
        dto.getValue();
        list.add(dto);
        
        return list;
    }
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Property 상세 정보 결과 값 설정
     * @title : setPropertyDetailInfo
     * @return : PropertyVO
    *****************************************************************/
    private PropertyVO setPropertyDetailInfo(){
        PropertyVO vo = new PropertyVO();
        vo.setRecid(1);
        vo.setDeploymentName("deploymentName");
        vo.setName("name");
        vo.setValue("value");
        return vo;
    }
}
