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
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentResourceConfigDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentResourceConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dto.HbCfDeploymentResourceConfigDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class HbCfDeploymentResourceConfigServiceUnitTest extends BaseHbDeployControllerUnitTest{
    
    @InjectMocks private HbCfDeploymentResourceConfigService mockHbCfDeploymentResourceConfigService;
    @Mock private MessageSource mockMessageSource;
    @Mock private HbCfDeploymentResourceConfigDAO mockHbCfDeploymentResourceConfigDAO;
    
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
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Resource 목록 정보 조회 Unit Test
     * @title : testGetResourceConfigInfoList
     * @return : void
    *****************************************************************/
    @Test
    public void testGetResourceConfigInfoList(){
        List<HbCfDeploymentResourceConfigVO> expectList = setCfCrednetialConfigList();
        when(mockHbCfDeploymentResourceConfigDAO.selectCfDeploymentResourceConfigInfoList()).thenReturn(expectList);
        List< HbCfDeploymentResourceConfigVO> resultList = mockHbCfDeploymentResourceConfigService.getResourceConfigInfoList();
        assertEquals(expectList.size(), resultList.size());
        assertEquals(expectList.get(0).getDirectorInfo(), resultList.get(0).getDirectorInfo());
        assertEquals(expectList.get(0).getIaasType(), resultList.get(0).getIaasType());
        assertEquals(expectList.get(0).getInstanceTypeL(), resultList.get(0).getInstanceTypeL());
        assertEquals(expectList.get(0).getId(), resultList.get(0).getId());
        assertEquals(expectList.get(0).getInstanceTypeS(), resultList.get(0).getInstanceTypeS());
        assertEquals(expectList.get(0).getInstanceTypeM(), resultList.get(0).getInstanceTypeM());
        assertEquals(expectList.get(0).getStemcellName(), resultList.get(0).getStemcellName());
        assertEquals(expectList.get(0).getStemcellVersion(), resultList.get(0).getStemcellVersion());
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Resource 목록 정보 저장 Unit Test
     * @title : testInsertResourceConfigInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testInsertResourceConfigInfo(){
        HbCfDeploymentResourceConfigDTO dto = setCfResourceConfigInfo("insert");
        when(mockHbCfDeploymentResourceConfigDAO.selectCfDeploymentResourceConfigByName(anyString())).thenReturn(0);
        mockHbCfDeploymentResourceConfigService.saveResourceConfigInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Resource 목록 정보 저장 Unit Test
     * @title : testUpdateResourceConfigInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testUpdateResourceConfigInfo(){
        HbCfDeploymentResourceConfigDTO dto = setCfResourceConfigInfo("update");
        HbCfDeploymentResourceConfigVO vo = setCfCrednetialConfig();
        when(mockHbCfDeploymentResourceConfigDAO.selectCfDeploymentResourceConfigInfo(anyInt(), anyString())).thenReturn(vo);
        when(mockHbCfDeploymentResourceConfigDAO.selectCfDeploymentResourceConfigByName(anyString())).thenReturn(0);
        mockHbCfDeploymentResourceConfigService.saveResourceConfigInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Resource 목록 정보 저장 Exception Unit Test
     * @title : testSaveResourceConfigInfoConflict
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testSaveResourceConfigInfoConflict(){
        HbCfDeploymentResourceConfigDTO dto = setCfResourceConfigInfo("insert");
        when(mockMessageSource.getMessage(anyString(), anyObject(), anyObject())).thenReturn("conflic_exception");
        when(mockHbCfDeploymentResourceConfigDAO.selectCfDeploymentResourceConfigByName(anyString())).thenReturn(1);
        mockHbCfDeploymentResourceConfigService.saveResourceConfigInfo(dto, principal);
    }
    
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Resource 목록 정보 삭제 Unit Test
     * @title : testDeleteResourceConfigInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testDeleteResourceConfigInfo(){
        HbCfDeploymentResourceConfigDTO dto = setCfResourceConfigInfo("update");
        mockHbCfDeploymentResourceConfigService.deleteResourceConfigInfo(dto, principal);
    }
    
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Resource 목록 정보 삭제 Exception Unit Test
     * @title : testDeleteResourceConfigInfo
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testDeleteResourceConfigInfoNull(){
        HbCfDeploymentResourceConfigDTO dto = setCfResourceConfigInfo("insert");
        when(mockMessageSource.getMessage(anyString(), anyObject(), anyObject())).thenReturn("null");
        mockHbCfDeploymentResourceConfigService.deleteResourceConfigInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 리소스 정보 목록 조회 결과 값 설정
     * @title : setCfCrednetialConfigList
     * @return : List<HbCfDeploymentResourceConfigVO>
    *****************************************************************/
    private HbCfDeploymentResourceConfigVO setCfCrednetialConfig() {
        HbCfDeploymentResourceConfigVO vo = new HbCfDeploymentResourceConfigVO();
        vo.setDirectorInfo("1");
        vo.setIaasType("Openstack");
        vo.setId(1);
        vo.setInstanceTypeS("m1.small");
        vo.setInstanceTypeM("m1.medium");
        vo.setInstanceTypeL("m1.large");
        vo.setResourceConfigName("resource-config");
        vo.setStemcellName("openstakc-ubuntu");
        vo.setStemcellVersion("3621.48");
        return vo;
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 리소스 정보 저장/삭제 요청 값 설정
     * @title : setCfResourceConfigInfo
     * @return : HbCfDeploymentResourceConfigDTO
    *****************************************************************/
    private HbCfDeploymentResourceConfigDTO setCfResourceConfigInfo(String type) {
        HbCfDeploymentResourceConfigDTO dto = new HbCfDeploymentResourceConfigDTO();
        dto.setDirectorInfo("1");
        dto.setIaasType("Openstack");
        if("update".equalsIgnoreCase(type)){
            dto.setId(1);
        }
        dto.setInstanceTypeL("m1.large");
        dto.setInstanceTypeM("m1.medium");
        dto.setInstanceTypeS("m1.small");
        dto.setResourceConfigName("resource-config");
        dto.setStemcellName("ubuntu-trust");
        dto.setStemcellVersion("3568.21");
        return dto;
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 리소스 정보 목록 조회 결과 값 설정
     * @title : setCfCrednetialConfigList
     * @return : List<HbCfDeploymentResourceConfigVO>
    *****************************************************************/
    private List<HbCfDeploymentResourceConfigVO> setCfCrednetialConfigList() {
        List<HbCfDeploymentResourceConfigVO> list = new ArrayList<HbCfDeploymentResourceConfigVO>();
        HbCfDeploymentResourceConfigVO vo = new HbCfDeploymentResourceConfigVO();
        vo.setDirectorInfo("1");
        vo.setIaasType("Openstack");
        vo.setId(1);
        vo.setInstanceTypeS("m1.small");
        vo.setInstanceTypeM("m1.medium");
        vo.setInstanceTypeL("m1.large");
        vo.setResourceConfigName("resource-config");
        vo.setStemcellName("openstakc-ubuntu");
        vo.setStemcellVersion("3621.48");
        list.add(vo);
        return list;
    }
}
