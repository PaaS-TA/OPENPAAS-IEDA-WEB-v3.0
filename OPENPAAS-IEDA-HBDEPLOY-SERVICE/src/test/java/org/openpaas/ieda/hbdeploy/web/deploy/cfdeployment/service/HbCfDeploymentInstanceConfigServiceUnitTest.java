package org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.hbdeploy.web.common.base.BaseHbDeployControllerUnitTest;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentInstanceConfigDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentInstanceConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dto.HbCfDeploymentInstanceConfigDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class HbCfDeploymentInstanceConfigServiceUnitTest extends BaseHbDeployControllerUnitTest{
    
    @InjectMocks private HbCfDeploymentInstanceConfigService mockHbCfDeploymentInstanceConfigService;
    @Mock private MessageSource mockMessageSource;
    @Mock private HbCfDeploymentInstanceConfigDAO mockHbCfDeploymentInstanceConfigDAO;
    
    private Principal principal = null;
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 유닛 테스트가 실행되기 전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        principal = getLoggined();
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CF DEPLOYMENT Instance 목록 정보 조회 Unit Test
     * @title : testGetHbCfInstanceConfigInfoList
     * @return : void
    ***************************************************/
    @Test
    public void testGetHbCfInstanceConfigInfoList(){
        List<HbCfDeploymentInstanceConfigVO> expectList = setCfInstanceConfigList();
        when(mockHbCfDeploymentInstanceConfigDAO.selectHbCfDeploymentInstanceConfigInfoList()).thenReturn(expectList);
        List<HbCfDeploymentInstanceConfigVO>  resultList = mockHbCfDeploymentInstanceConfigService.getHbCfInstanceConfigInfoList();
        assertEquals(expectList.size(), resultList.size());
        assertEquals(expectList.get(0).getAdapter(), resultList.get(0).getAdapter());
        assertEquals(expectList.get(0).getApi(), resultList.get(0).getApi());
        assertEquals(expectList.get(0).getCcWorker(), resultList.get(0).getCcWorker());
        assertEquals(expectList.get(0).getCfDeploymentName(), resultList.get(0).getCfDeploymentName());
        assertEquals(expectList.get(0).getCfDeploymentVersion(), resultList.get(0).getCfDeploymentVersion());
        assertEquals(expectList.get(0).getConsul(), resultList.get(0).getConsul());
        assertEquals(expectList.get(0).getDiegoApi(), resultList.get(0).getDiegoApi());
        assertEquals(expectList.get(0).getDiegoCell(), resultList.get(0).getDiegoCell());
        assertEquals(expectList.get(0).getIaasType(), resultList.get(0).getIaasType());
        assertEquals(expectList.get(0).getHaproxy(), resultList.get(0).getHaproxy());
        assertEquals(expectList.get(0).getTcpRouter(), resultList.get(0).getTcpRouter());
        assertEquals(expectList.get(0).getNats(), resultList.get(0).getNats());
        assertEquals(expectList.get(0).getId(), resultList.get(0).getId());
        assertEquals(expectList.get(0).getInstanceConfigName(), resultList.get(0).getInstanceConfigName());
        assertEquals(expectList.get(0).getUaa(), resultList.get(0).getUaa());
        assertEquals(expectList.get(0).getSingletonBlobstore(), resultList.get(0).getSingletonBlobstore());
        assertEquals(expectList.get(0).getTheDatabase(), resultList.get(0).getTheDatabase());
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CF DEPLOYMENT Instance 정보 저장 Unit Test
     * @title : testInsertHbCfInstanceConfigInfo
     * @return : void
    ***************************************************/
    @Test
    public void testInsertHbCfInstanceConfigInfo(){
        HbCfDeploymentInstanceConfigDTO dto = setCfInstanceConfigInfo("insert");
        when(mockHbCfDeploymentInstanceConfigDAO.selectHbCfDeploymentInstanceConfigByName(anyString())).thenReturn(0);
        mockHbCfDeploymentInstanceConfigService.saveHbCfInstanceConfigInfo(dto, principal);
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CF DEPLOYMENT Instance 정보 저장 Unit Test
     * @title : testUpdateHbCfInstanceConfigInfo
     * @return : void
    ***************************************************/
    @Test
    public void testUpdateHbCfInstanceConfigInfo(){
        HbCfDeploymentInstanceConfigDTO dto = setCfInstanceConfigInfo("update");
        HbCfDeploymentInstanceConfigVO vo = setCfInstanceConfig();
        when(mockHbCfDeploymentInstanceConfigDAO.selectHbCfDeploymentInstanceConfigInfo(anyInt())).thenReturn(vo);
        when(mockHbCfDeploymentInstanceConfigDAO.selectHbCfDeploymentInstanceConfigByName(anyString())).thenReturn(0);
        mockHbCfDeploymentInstanceConfigService.saveHbCfInstanceConfigInfo(dto, principal);
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CF DEPLOYMENT Instance 정보 저장 Exception Unit Test
     * @title : testUpdateHbCfInstanceConfigInfo
     * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testSaveHbCfInstanceConfigInfoConflict(){
        HbCfDeploymentInstanceConfigDTO dto = setCfInstanceConfigInfo("insert");
        when(mockHbCfDeploymentInstanceConfigDAO.selectHbCfDeploymentInstanceConfigByName(anyString())).thenReturn(1);
        when(mockMessageSource.getMessage(anyString(), anyObject(), anyObject())).thenReturn("conflict");
        mockHbCfDeploymentInstanceConfigService.saveHbCfInstanceConfigInfo(dto, principal);
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CF DEPLOYMENT Instance 정보 삭제 Unit Test
     * @title : testUpdateHbCfInstanceConfigInfo
     * @return : void
    ***************************************************/
    @Test
    public void testDeleteHbCfInstanceConfigInfo(){
        HbCfDeploymentInstanceConfigDTO dto = new HbCfDeploymentInstanceConfigDTO();
        dto.setId(1);
        dto.setIaasType("Openstack");
        mockHbCfDeploymentInstanceConfigService.deleteHbCfInstanceConfigInfo(dto, principal);
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CF DEPLOYMENT Instance 정보 삭제 Exception Unit Test
     * @title : testUpdateHbCfInstanceConfigInfo
     * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testDeleteHbCfInstanceConfigInfoNull(){
        HbCfDeploymentInstanceConfigDTO dto = new HbCfDeploymentInstanceConfigDTO();
        when(mockMessageSource.getMessage(anyString(), anyObject(), anyObject())).thenReturn("null");
        mockHbCfDeploymentInstanceConfigService.deleteHbCfInstanceConfigInfo(dto, principal);
    }
    
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 인스턴스 목록 정보 조회 결과 값 설정
     * @title : setCfInstanceConfigList
     * @return : List<HbCfDeploymentCredentialConfigVO>
    *****************************************************************/
    private HbCfDeploymentInstanceConfigVO setCfInstanceConfig() {
        HbCfDeploymentInstanceConfigVO vo = new HbCfDeploymentInstanceConfigVO();
        vo.setAdapter("1");
        vo.setApi("1");
        vo.setIaasType("Openstack");
        vo.setCfDeploymentVersion("2.7.0");
        vo.setCfDeploymentName("cf-deployment");
        vo.setCcWorker("1");
        vo.setConsul("1");
        vo.setDiegoApi("1");
        vo.setDiegoCell("1");
        vo.setDoppler("1");
        vo.setHaproxy("1");
        vo.setHaproxy("1");
        vo.setLogApi("1");
        vo.setInstanceConfigName("instance-config");
        vo.setRouter("1");
        vo.setScheduler("1");
        vo.setId(1);
        vo.setSingletonBlobstore("1");
        vo.setTcpRouter("1");
        vo.setTcpRouter("1");
        vo.setTheDatabase("1");
        vo.setUaa("1");
        vo.setNats("1");
        return vo;
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT Job 정보 저장/삭제 값 설정
     * @title : setCfInstanceConfigInfo
     * @return : HbCfDeploymentInstanceConfigDTO
    *****************************************************************/
    private HbCfDeploymentInstanceConfigDTO setCfInstanceConfigInfo(String type) {
        HbCfDeploymentInstanceConfigDTO dto = new HbCfDeploymentInstanceConfigDTO();
        dto.setAdapter("1");
        dto.setApi("1");
        dto.setIaasType("Openstack");
        dto.setCcWorker("1");
        dto.setCfDeploymentName("cf-deployment");
        dto.setCfDeploymentVersion("2.7.0");
        dto.setInstanceConfigName("instance-config");
        dto.setConsul("1");
        dto.setDiegoApi("1");
        dto.setDiegoCell("1");
        dto.setDoppler("1");
        dto.setHaproxy("1");
        if("update".equalsIgnoreCase(type)){
            dto.setId(1);
        }
        
        dto.setUaa("1");
        dto.setNats("1");
        dto.setSingletonBlobstore("1");
        dto.setTcpRouter("1");
        dto.setTheDatabase("1");
        dto.setRouter("1");
        dto.setScheduler("1");
        dto.setLogApi("1");
        return dto;
    }
    
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 인스턴스 목록 정보 조회 결과 값 설정
     * @title : setCfInstanceConfigList
     * @return : List<HbCfDeploymentCredentialConfigVO>
    *****************************************************************/
    private List<HbCfDeploymentInstanceConfigVO> setCfInstanceConfigList() {
        List<HbCfDeploymentInstanceConfigVO> list = new ArrayList<HbCfDeploymentInstanceConfigVO>();
        HbCfDeploymentInstanceConfigVO vo = new HbCfDeploymentInstanceConfigVO();
        vo.setAdapter("1");
        vo.setApi("1");
        vo.setIaasType("Openstack");
        vo.setCfDeploymentVersion("2.7.0");
        vo.setCfDeploymentName("cf-deployment");
        vo.setCcWorker("1");
        vo.setConsul("1");
        vo.setDiegoApi("1");
        vo.setDiegoCell("1");
        vo.setDoppler("1");
        vo.setHaproxy("1");
        vo.setHaproxy("1");
        vo.setLogApi("1");
        vo.setInstanceConfigName("instance-config");
        vo.setRouter("1");
        vo.setScheduler("1");
        vo.setId(1);
        vo.setSingletonBlobstore("1");
        vo.setTcpRouter("1");
        vo.setTcpRouter("1");
        vo.setTheDatabase("1");
        vo.setUaa("1");
        vo.setNats("1");
        list.add(vo);
        return list;
    }
    
}
