package org.openpaas.ieda.hbdeploy.web.information.stemcell.service;
import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class HbStemcellServiceUnitTest {
    
    @InjectMocks HbStemcellService mockHbStemcellService;
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 유닛 테스트가 실행되기 전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }
    
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Stemcell 정보 Json 파싱 Unit Test
     * @title : testSetUploadedStemcellList
     * @return : void
    ***************************************************/
    @Test
    public void testSetUploadedStemcellList(){
        String responseBody = stemcellJsonInput();
        mockHbStemcellService.setUploadedStemcellList(responseBody);
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Stemcell 정보 Json 설정
     * @title : stemcellJsonInput
     * @return : String
    ***************************************************/
    public String stemcellJsonInput(){
        JSONObject jObj = new JSONObject();
        JSONArray jArray = new JSONArray();
        jObj.put("name", "openstack-ubuntu-16.04.tgz");
        jObj.put("operatingSystem", "ubuntu-trust");
        jObj.put("version", "3562.21");
        jArray.add(jObj);
        return jArray.toJSONString();
    }
    
    
}
