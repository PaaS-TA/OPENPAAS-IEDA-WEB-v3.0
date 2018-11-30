package org.openpaas.ieda.hbdeploy.web.config.setting;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import org.openpaas.ieda.controller.hbdeploy.web.setting.HbDirectorConfigurationController;
import org.openpaas.ieda.hbdeploy.web.config.setting.dao.HbDirectorConfigVO;
import org.openpaas.ieda.hbdeploy.web.config.setting.dto.HbDirectorConfigDTO;
import org.openpaas.ieda.hbdeploy.web.config.setting.service.HbDirectorConfigService;
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
public class HbDirectorConfigurationControllerUnitTest {
    
    @InjectMocks
    HbDirectorConfigurationController mockHbDirectorConfigurationController;
    
    @Mock
    HbDirectorConfigService mockHbDirectorConfigService;
    
    private MockMvc mockMvc;
    
    private static final ObjectMapper mapper = new ObjectMapper();
    
    final static String VIEW_URL= "/config/hbDirector";
    final static String HB_DIRECTOR_LIST_URL = "/config/hbDirector/list/{directorType}";
    final static String HB_DIRECTOR_ADD_URL = "/config/hbDirector/add";
    final static String HB_DIRECTOR_DELETE_URL = "/config/hbDirector/delete/{seq}";
    
    /***************************************************
     * @project : 인프라 계정 관리 대시보드
     * @description   : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockHbDirectorConfigurationController).build();
    }
    
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 디렉터 설정 화면 이동 테스트 
    * @title : testGoListDirector
    * @return : void
    ***************************************************/
    @Test
    public void goHbDirector() throws Exception{
        mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("/hbdeploy/config/hbListDirector"));
    }
    
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 디렉터 설정 정보 추가 테스트
    * @title : testCreateHbDirector
    * @return : void
    ***************************************************/
    @Test
    public void testCreateHbDirector() throws  Exception{
        HbDirectorConfigDTO dto = setHbDirectorConfigInfo();
        mockMvc.perform(post(HB_DIRECTOR_ADD_URL, "public").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description :  디렉터 정보 목록 조회(전체) 테스트
    * @title : testListDirector
    * @return : void
    ***************************************************/
    @Test
    public void testListDirector() throws Exception{
        List<HbDirectorConfigVO> resultList = setListDirector();
        when( mockHbDirectorConfigService.getDirectorList(anyString())).thenReturn(resultList);
        mockMvc.perform(get( HB_DIRECTOR_LIST_URL, "private" ).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.records[0].iedaDirectorConfigSeq").value(1))
        .andExpect(jsonPath("$.records[0].recid").value(1))
        .andExpect(jsonPath("$.records[0].userId").value("admin"))
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
    * @description : 디렉터 삭제 테스트 
    * @title : testDeleteDirector
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteDirector() throws Exception{
        mockMvc.perform(delete(HB_DIRECTOR_DELETE_URL,1).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNoContent());
    }
    
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 디렉터 조회 리턴 값 설정
    * @title : setListDirector
    * @return : List<HbDirectorConfigVO>
    ***************************************************/
    private List<HbDirectorConfigVO> setListDirector(){
        List<HbDirectorConfigVO> list = new ArrayList<HbDirectorConfigVO>();
        HbDirectorConfigVO vo = new HbDirectorConfigVO();
        vo.setIedaDirectorConfigSeq(1);
        vo.setRecid(1);
        vo.setUserId("admin");
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
    * @description : 디렉터 설정 정보 설정
    * @title : setHbDirectorConfigInfo
    * @return : HbDirectorConfigDTO
    ***************************************************/
    private HbDirectorConfigDTO setHbDirectorConfigInfo() {
        HbDirectorConfigDTO dto = new HbDirectorConfigDTO();
        dto.setDirectorPort(25555);
        dto.setUserId("admin");
        dto.setUserPassword("admin");
        dto.setDirectorUrl("123125-asdasb31123");
        return dto;
    }
}
