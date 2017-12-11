package org.openpaas.ieda.deploy.web.config.setting;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
import org.openpaas.ieda.controller.deploy.web.config.setting.DirectorConfigurationController;
import org.openpaas.ieda.deploy.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.deploy.web.config.setting.dto.DirectorConfigDTO;
import org.openpaas.ieda.deploy.web.config.setting.service.DirectorConfigService;
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
public class DirectorConfigurationControllerUnitTest{
    
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    
    @InjectMocks
    DirectorConfigurationController mockDirectorConfigurationController;
    @Mock
    DirectorConfigService mockDirectorConfigService;
    
    final static String VIEW_URL= "/config/director";
    final static String DIRECTOR_LIST_URL = "/config/director/list";
    final static String DIRECTOR_ADD_URL = "/config/director/add";
    final static String DIRECTOR_UPDATE_URL = "/config/director/update";
    final static String DIRECTOR_DELETE_URL = "/config/director/delete/{seq}";
    final static String SET_DEFAULT_DIRECTOR_URL = "/config/director/setDefault/{seq}";
    
    
    /***************************************************
     * @project : 인프라 계정 관리 대시보드
     * @description   : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockDirectorConfigurationController).build();
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 설치 관리자 설정 화면 이동 테스트 
    * @title : testGoListDirector
    * @return : void
    ***************************************************/
    @Test
    public void testGoListDirector() throws Exception{
        mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/deploy/config/listDirector"));
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description :  설치 관리자 정보 목록 조회(전체) 테스트
    * @title : testListDirector
    * @return : void
    ***************************************************/
    @Test
    public void testListDirector() throws Exception{
        List<DirectorConfigVO> resultList = setListDirector();
        when( mockDirectorConfigService.getDirectorList()).thenReturn(resultList);
        mockMvc.perform(get( DIRECTOR_LIST_URL ).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.records[0].iedaDirectorConfigSeq").value(1))
        .andExpect(jsonPath("$.records[0].recid").value(1))
        .andExpect(jsonPath("$.records[0].userId").value("admin"))
        .andExpect(jsonPath("$.records[0].defaultYn").value("Y"))
        .andExpect(jsonPath("$.records[0].directorCpi").value("openstack-cpi"))
        .andExpect(jsonPath("$.records[0].directorName").value("my-bosh"))
        .andExpect(jsonPath("$.records[0].directorPort").value(25555))
        .andExpect(jsonPath("$.records[0].directorUuid").value("sdasxcv234253234-13"))
        .andExpect(jsonPath("$.records[0].directorUrl").value("https:172.16.100.100"))
        .andExpect(jsonPath("$.records[0].createUserId").value("admin"))
        .andExpect(jsonPath("$.records[0].updateUserId").value("admin"))
        .andExpect(jsonPath("$.records[0].userPassword").value("admin"))
        .andExpect(jsonPath("$.records[0].connect").value(true));
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description :  설치 관리자 정보 목록 조회(전체) Null 테스트
    * @title : testListDirector
    * @return : void
    ***************************************************/
    @Test
    public void testListDirectorValueNull() throws Exception{
        when(mockDirectorConfigService.getDirectorList()).thenReturn(null);
        mockMvc.perform(get(DIRECTOR_LIST_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 설치 관리자 추가 테스트
    * @title : testCreateDirector
    * @return : void
    ***************************************************/
    @Test
    public void testCreateDirector() throws Exception{
        DirectorConfigDTO.Create dto = setDirectorConfigInfoList();
        mockMvc.perform(post(DIRECTOR_ADD_URL).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 설치 관리자 수정 테스트 
    * @title : testUpdateDirector
    * @return : void
    ***************************************************/
    @Test 
    public void testUpdateDirector() throws Exception{
        DirectorConfigDTO.Update dto = updateDirectorConfigInfo();
        mockMvc.perform(put(DIRECTOR_UPDATE_URL,1).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 설치 관리자 삭제 테스트 
    * @title : testDeleteDirector
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteDirector() throws Exception{
        mockMvc.perform(delete(DIRECTOR_DELETE_URL,1).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNoContent());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 기본 설치 관리자 설정 테스트
    * @title : testSetDefaultDirector
    * @return : void
    ***************************************************/
    @Test
    public void testSetDefaultDirector() throws Exception{
        DirectorConfigDTO.Update dto = updateDirectorConfigInfo();
        when( mockDirectorConfigService.existCheckSetDefaultDirectorInfo(anyInt(),any(), anyString())).thenReturn(setDirector());
        mockMvc.perform(put(SET_DEFAULT_DIRECTOR_URL,1).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.iedaDirectorConfigSeq").value(1))
        .andExpect(jsonPath("$.userId").value("admin"))
        .andExpect(jsonPath("$.directorName").value("my-bosh"))
        .andExpect(jsonPath("$.directorUrl").value("https:172.16.100.100"))
        .andExpect(jsonPath("$.directorPort").value(25555))
        .andExpect(jsonPath("$.directorCpi").value("openstack-cpi"))
        .andExpect(jsonPath("$.defaultYn").value("Y"));
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 설치 관리자 수정 정보 설정
    * @title : updateDirectorConfigInfo
    * @return : DirectorConfigDTO.Update
    ***************************************************/
    private DirectorConfigDTO.Update updateDirectorConfigInfo() {
        DirectorConfigDTO.Update dto = new DirectorConfigDTO.Update();
        Date date = new Date();
        dto.setIedaDirectorConfigSeq(1);
        dto.setUserId("admin");
        dto.setUpdateDate(new Date(date.getTime()));
        dto.setUserPassword("12345");
        return dto;
    }

    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 설치 관리자 정보 설정
    * @title : setDirectorConfigInfoList
    * @return : DirectorConfigDTO.Create
    ***************************************************/
    private DirectorConfigDTO.Create setDirectorConfigInfoList() {
        DirectorConfigDTO.Create dto = new DirectorConfigDTO.Create();
        dto.setDirectorPort(25555);
        dto.setUserId("admin");
        dto.setUserPassword("admin");
        dto.setDirectorUrl("123125-asdasb31123");
        return dto;
    }
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 설치 관리자 목록 조회 리턴 값 설정
    * @title : setListDirector
    * @return : List<DirectorConfigVO>
    ***************************************************/
    private List<DirectorConfigVO> setListDirector(){
        List<DirectorConfigVO> list = new ArrayList<DirectorConfigVO>();
        DirectorConfigVO vo = new DirectorConfigVO();
        vo.setIedaDirectorConfigSeq(1);
        vo.setRecid(1);
        vo.setUserId("admin");
        vo.setDefaultYn("Y");
        vo.setDirectorCpi("openstack-cpi");
        vo.setDirectorName("my-bosh");
        vo.setDirectorPort(25555);
        vo.setDirectorUuid("sdasxcv234253234-13");
        vo.setDirectorUrl("https:172.16.100.100");
        vo.setCreateUserId("admin");
        vo.setUpdateUserId("admin");
        vo.setUserPassword("admin");
        vo.setConnect(true);
        list.add(vo);
        return list;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 설치 관리자 조회 리턴 값 설정
    * @title : setListDirector
    * @return : List<DirectorConfigVO>
    ***************************************************/
    private DirectorConfigVO setDirector(){
        DirectorConfigVO vo = new DirectorConfigVO();
        vo.setIedaDirectorConfigSeq(1);
        vo.setRecid(1);
        vo.setUserId("admin");
        vo.setDefaultYn("Y");
        vo.setDirectorCpi("openstack-cpi");
        vo.setDirectorName("my-bosh");
        vo.setDirectorPort(25555);
        vo.setDirectorUuid("sdasxcv234253234-13");
        vo.setDirectorUrl("https:172.16.100.100");
        vo.setCreateUserId("admin");
        vo.setUpdateUserId("admin");
        vo.setUserPassword("admin");
        vo.setConnect(true);
        return vo;
    }
}
