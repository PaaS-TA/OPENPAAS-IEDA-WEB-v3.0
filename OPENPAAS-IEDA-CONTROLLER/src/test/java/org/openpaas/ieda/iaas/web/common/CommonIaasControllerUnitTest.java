package org.openpaas.ieda.iaas.web.common;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;
import java.util.ArrayList;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.common.BaseControllerUnitTest;
import org.openpaas.ieda.controller.iaasMgnt.common.web.CommonIaasController;
import org.openpaas.ieda.iaasDashboard.web.account.dto.IaasAccountMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.common.dao.CommonCodeVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
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
public class CommonIaasControllerUnitTest extends BaseControllerUnitTest{

    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    private Principal principal = null;
    
    @InjectMocks CommonIaasController mockCommonIaasController;
    @Mock CommonIaasService mockCommonIaasService;
    
    final static String DEFAULT_IAAS_ACCOUNT_URL = "/common/iaasMgnt/setDefaultIaasAccount";
    final static String SUB_CODE_URL = "/common/iaas/codes/parent/{parentCode}";
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockCommonIaasController).build();
        principal = getLoggined();
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 기본 IaaS 계정 정보 설정
     * @title : testSetDefaultIaasAccount
     * @return : void
    ***************************************************/
    @Test
    public void testSetDefaultIaasAccount() throws Exception{
        IaasAccountMgntDTO dto = setDefaultAccountInfo();
        doNothing().when(mockCommonIaasService).setDefaultIaasAccountInfo(dto, principal);
        mockMvc.perform(post(DEFAULT_IAAS_ACCOUNT_URL).content(mapper.writeValueAsBytes(dto))
                .contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 공통 코드 조회 (하위 코드 목록)
     * @title : testGetSubCode
     * @return : void
    ***************************************************/
    @Test
    public void testGetSubCode() throws Exception{
        String parentCode = "100000";
        when(mockCommonIaasService.getSubGroupCodeList(parentCode)).thenReturn(new ArrayList<CommonCodeVO>());
        mockMvc.perform(get(SUB_CODE_URL, parentCode).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 기본 IaaS 계정 정보 설정
     * @title : setDefaultAccountInfo
     * @return : IaasAccountMgntDTO
    ***************************************************/
    public IaasAccountMgntDTO setDefaultAccountInfo() {
        IaasAccountMgntDTO dto = new IaasAccountMgntDTO();
        dto.setIaasType("aws");
        dto.setAccountName("aws-test");
        dto.setCommonAccessUser("test");
        dto.setCommonAccessSecret("testPw");
        
        return dto;
    }

}
