package org.openpaas.ieda.iaasDashboard.azureMgnt.web.keypair.service;

import java.security.Principal;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.common.base.BaseAzureMgntControllerUnitTest;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.keypair.dto.AzureKeypairMgntDTO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.keypair.service.AzureKeypairMgntService;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class AzureKeypairMgntServiceUnitTest extends BaseAzureMgntControllerUnitTest{
@SuppressWarnings("unused")
private Principal principal = null;
    
    @InjectMocks AzureKeypairMgntService mockAzureKeypairMgntService;
    @Mock CommonIaasService mockCommonIaasService;
    @Mock MessageSource mockMessageSource;
    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        principal = getLoggined();
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure Keypair 생성 TEST
     * @title : testCreateKeypair
     * @return : void
     ***************************************************/
    @Test
    public void testCreateKeypair(){
        AzureKeypairMgntDTO dto = setAzureKeypairInfo();
        mockAzureKeypairMgntService.createKeypair(dto);
    }
    
    /***************************************************
    * @project : 인프라 관리 대시보드
    * @description : Azure Keypair 정보 설정
    * @title : setAzureKeypairInfo
    * @return : AzurePublicIpMgntDTO
    ***************************************************/
    public AzureKeypairMgntDTO setAzureKeypairInfo() {
    	AzureKeypairMgntDTO dto = new AzureKeypairMgntDTO();
        dto.setAccountId(1);
        dto.setKeypairName("test-key-a");
        return dto;
    }
    
}
