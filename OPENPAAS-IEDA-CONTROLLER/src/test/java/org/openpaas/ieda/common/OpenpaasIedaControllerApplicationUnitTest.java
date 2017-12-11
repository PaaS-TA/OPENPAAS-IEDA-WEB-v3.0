package org.openpaas.ieda.common;

import javax.ws.rs.core.Application;

import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.OpenpaasIedaControllerApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class OpenpaasIedaControllerApplicationUnitTest extends BaseControllerUnitTest{
    
    @InjectMocks 
    OpenpaasIedaControllerApplication mockOpenpaasIedaControllerApplication;
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    //@Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        MockMvcBuilders.standaloneSetup(mockOpenpaasIedaControllerApplication).build();
        getLoggined();
    }

    //@Test
    public void testMain() throws Exception{
        String args[] = {"1"};
        OpenpaasIedaControllerApplication.main(args);
    }
    
}
