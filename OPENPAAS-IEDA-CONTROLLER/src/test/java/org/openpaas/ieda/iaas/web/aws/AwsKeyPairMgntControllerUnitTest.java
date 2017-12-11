package org.openpaas.ieda.iaas.web.aws;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.openpaas.ieda.awsMgnt.web.keypair.service.AwsKeypairMgntService;
import org.openpaas.ieda.common.BaseControllerUnitTest;
import org.openpaas.ieda.controller.iaasMgnt.awsMgnt.web.keypair.AwsKeyPairMgntController;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.amazonaws.services.ec2.model.KeyPairInfo;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class AwsKeyPairMgntControllerUnitTest  extends BaseControllerUnitTest {
    
    @InjectMocks AwsKeyPairMgntController mockAwsKeyPairMgntController;
    @Mock AwsKeypairMgntService mockAwsKeypairMgntService;
    
    private MockMvc mockMvc;
    /*************************************** URL *******************************************/
    final static String VIEW_URL = "/awsMgnt/keypair";
    final static String KEY_PAIR_LIST_INFO_URL = "/awsMgnt/keypair/list/{accountId}/{region}";
    final static String KEY_PAIR_CREATE_URL = "/awsMgnt/keypair/create/{accountId}/{name}/{region}";

    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
     ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockAwsKeyPairMgntController).build();
        getLoggined();
        
    }
    
    /***************************************************
        * @project : AWS 인프라 관리 대시보드
        * @description : Key Pair 목록 화면 이동 TEST
        * @title : testGoAwsKeyPairList
        * @return : String
    ***************************************************/
    @Test
    public void testGoAwsKeyPairList() throws Exception{
        mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(view().name("iaas/aws/keypair/awsKeypairMgnt"));
        
    }
    
    /***************************************************
        * @project : AWS 인프라 관리 대시보드
        * @description : Key Pair 목록 조회 TEST
        * @title : testGetAwsKeyPairInfoList
        * @return : void
    ***************************************************/
    @Test
    public void testGetAwsKeyPairInfoList() throws Exception{
        List<KeyPairInfo> list = getKeyPairInfoList();
        when(mockAwsKeypairMgntService.getAwsKeyPairInfoList(any(), anyInt(), anyString())).thenReturn(list);
        mockMvc.perform(get(KEY_PAIR_LIST_INFO_URL,1, "us-west-1").contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.records[0].keyName").value("testKeyPair"))
        .andExpect(jsonPath("$.records[0].keyFingerprint").value("1f:1f:1f:2f:3g:4g:5g:6h"));
    }
    
    /***************************************************
        * @project : AWS 인프라 관리 대시보드
        * @description : Key Pair 생성 TEST
        * @title : testCreateAwsKeyPair
        * @return : ResponseEntity<?>
    ***************************************************/
    @Test
    public void testCreateAwsKeyPair() throws Exception{
        mockMvc.perform(get(KEY_PAIR_CREATE_URL,1,"testKeyPair","us-west-1")
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk());
        
    }
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : AWS Key Pair 목록 조회 결과 값 설정
     * @title : getKeyPairInfoList
     * @return : List<AwsKeypairMgntVO>
     ***************************************************/
    private List<KeyPairInfo> getKeyPairInfoList(){
        List<KeyPairInfo> list = new ArrayList<KeyPairInfo>();
        KeyPairInfo keyPairInfo = new KeyPairInfo();
        keyPairInfo.setKeyName("testKeyPair");
        keyPairInfo.setKeyFingerprint("1f:1f:1f:2f:3g:4g:5g:6h");
        list.add(keyPairInfo);
        return list;
    }
    
}
