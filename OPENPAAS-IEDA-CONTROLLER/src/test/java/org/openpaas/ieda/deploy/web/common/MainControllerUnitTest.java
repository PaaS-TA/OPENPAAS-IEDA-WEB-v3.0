package org.openpaas.ieda.deploy.web.common;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.security.Principal;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.common.BaseControllerUnitTest;
import org.openpaas.ieda.controller.deploy.web.main.MainController;
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
public class MainControllerUnitTest extends BaseControllerUnitTest{

private MockMvc mockMvc;
    
    @InjectMocks MainController mockMainController;
    
    final static String MAIN_VIEW_URL = "/platform";
    final static String TOP_VIEW_URL = "/top";
    final static String MENU_VIEW_URL = "/menu";
    
    Principal principal = null;
    /****************************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 하나의 메소드가 실행되기전 호출
    * @title : setUp
    * @return : void
   *****************************************************************/
   @Before
   public void setUp() throws Exception {
       MockitoAnnotations.initMocks(this);
       mockMvc = MockMvcBuilders.standaloneSetup(mockMainController).build();
       principal = getLoggined();
   }
   
   /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 메인 화면 이동
    * @title : testGoServicePack
    * @return : void
   ***************************************************/
   @Test
   public void testGoLayout() throws Exception{
       mockMvc.perform(get(MAIN_VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
       .andExpect(status().isOk())
       .andExpect(view().name("/deploy/main/layout"));
   }
   
   /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 메인의 top 화면 호출
     * @title : testGoTop
     * @return : void
    ***************************************************/
   @Test
   public void testGoTop() throws Exception{
	   mockMvc.perform(get(TOP_VIEW_URL).principal(principal).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
       .andExpect(status().isOk())
       .andExpect(view().name("/deploy/main/top"));
   }
   
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :
     * @title : testGoMenu
     * @return : void
     ***************************************************/
   @Test
   public void testGoMenu() throws Exception{
       mockMvc.perform(get(MENU_VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
       .andExpect(status().isOk())
       .andExpect(view().name("/deploy/main/menu"));
   }

}
