package org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
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
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapCpiConfigDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapCpiConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto.HbBootstrapCpiConfigDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class HbBootstrapCpiConfigServiceUnitTest extends BaseHbDeployControllerUnitTest{
    
    @InjectMocks HbBootstrapCpiConfigService mockHbBootstrapCpiConfigService;
    @Mock HbBootstrapCpiConfigDAO mockHbBootstrapCpiConfigDAO;
    @Mock MessageSource mockMessageSource;
    
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
     * @description : CPI 목록 정보 조회 Unit Test
     * @title : testGetCpiConfigInfoList
     * @return : void
    ***************************************************/
    @Test
    public void testGetCpiConfigInfoList(){
        List<HbBootstrapCpiConfigVO> expectList = expectCpiConfigList();
        when(mockHbBootstrapCpiConfigDAO.selectBootstrapCpiConfigInfoList()).thenReturn(expectList);
        List<HbBootstrapCpiConfigVO> resultList = mockHbBootstrapCpiConfigService.getCpiConfigInfoList();
        assertEquals(expectList.size(), resultList.size());
        assertEquals(expectList.get(0).getCommonAccessUser(), resultList.get(0).getCommonAccessUser());
        assertEquals(expectList.get(0).getCommonAvailabilityZone(), resultList.get(0).getCommonAvailabilityZone());
        assertEquals(expectList.get(0).getCommonKeypairName(), resultList.get(0).getCommonKeypairName());
        assertEquals(expectList.get(0).getCommonKeypairPath(), resultList.get(0).getCommonKeypairPath());
        assertEquals(expectList.get(0).getCommonProject(), resultList.get(0).getCommonProject());
        assertEquals(expectList.get(0).getCommonSecurityGroup(), resultList.get(0).getCommonSecurityGroup());
        assertEquals(expectList.get(0).getCommonTenant(), resultList.get(0).getCommonTenant());
        assertEquals(expectList.get(0).getIaasConfigAlias(), resultList.get(0).getIaasConfigAlias());
        assertEquals(expectList.get(0).getIaasConfigId(), resultList.get(0).getIaasConfigId());
        assertEquals(expectList.get(0).getIaasType(), resultList.get(0).getIaasType());
        assertEquals(expectList.get(0).getCpiName(), resultList.get(0).getCpiName());
        assertEquals(expectList.get(0).getOpenstackVersion(), resultList.get(0).getOpenstackVersion());
        assertEquals(expectList.get(0).getCpiInfoId(), resultList.get(0).getCpiInfoId());
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CPI 목록 정보 삽입 Unit Test
     * @title : testInsertCpiConfigInfo
     * @return : void
    ***************************************************/
    @Test
    public void testInsertCpiConfigInfo(){
        HbBootstrapCpiConfigDTO dto = setCpiConfigInfo("insert");
        when(mockHbBootstrapCpiConfigDAO.selectBootstrapCpiConfigByName(anyString())).thenReturn(0);
        mockHbBootstrapCpiConfigService.saveCpiConfigInfo(dto, principal);
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CPI 목록 정보 삽입 중 이미 등록 한 CPI Config Name이 존재 할 경우 Unit Test
     * @title : testInsertCpiConfigInfoConflictCpiConfigName
     * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testInsertCpiConfigInfoConflictCpiConfigName(){
        HbBootstrapCpiConfigDTO dto = setCpiConfigInfo("insert");
        when(mockHbBootstrapCpiConfigDAO.selectBootstrapCpiConfigByName(anyString())).thenReturn(1);
        mockHbBootstrapCpiConfigService.saveCpiConfigInfo(dto, principal);
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CPI 목록 정보 수정 Unit Test
     * @title : testInsertCpiConfigInfo
     * @return : void
    ***************************************************/
    @Test
    public void testUpdateCpiConfigInfo(){
        HbBootstrapCpiConfigDTO dto = setCpiConfigInfo("update");
        when(mockHbBootstrapCpiConfigDAO.selectBootstrapCpiConfigByName(anyString())).thenReturn(0);
        when(mockHbBootstrapCpiConfigDAO.selectBootstrapCpiConfigInfo(anyInt(), anyString())).thenReturn(expectCpiConfigInfo(""));
        mockHbBootstrapCpiConfigService.saveCpiConfigInfo(dto, principal);
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CPI 목록 정보 수정 Config Name이 존재 할 경우 Unit Test
     * @title : testUpdateCpiConfigInfoConflictCpiConfigName
     * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testUpdateCpiConfigInfoConflictCpiConfigName(){
        HbBootstrapCpiConfigDTO dto = setCpiConfigInfo("update");
        when(mockHbBootstrapCpiConfigDAO.selectBootstrapCpiConfigByName(anyString())).thenReturn(1);
        when(mockHbBootstrapCpiConfigDAO.selectBootstrapCpiConfigInfo(anyInt(), anyString())).thenReturn(expectCpiConfigInfo("conflict"));
        mockHbBootstrapCpiConfigService.saveCpiConfigInfo(dto, principal);
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CPI 목록 정보 삭제 Unit Test
     * @title : testDeleteCpiConfigInfo
     * @return : void
    ***************************************************/
    @Test
    public void testDeleteCpiConfigInfo(){
    	HbBootstrapCpiConfigDTO dto = setCpiConfigInfo("update");
    	mockHbBootstrapCpiConfigService.deleteCpiConfigInfo(dto, principal);
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CPI 목록 정보 삭제 아이디 값이 없을 경우 Unit Test
     * @title : testDeleteCpiConfigInfoIdEmpty
     * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testDeleteCpiConfigInfoIdEmpty(){
    	HbBootstrapCpiConfigDTO dto = setCpiConfigInfo("insert");
    	mockHbBootstrapCpiConfigService.deleteCpiConfigInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap CPI 정보 결과 값 설정
     * @title : expectCpiConfigInfo
     * @return : HbBootstrapCpiConfigVO
    *****************************************************************/
    private HbBootstrapCpiConfigVO expectCpiConfigInfo(String type) {
        HbBootstrapCpiConfigVO vo = new HbBootstrapCpiConfigVO();
        vo.setCommonAccessUser("test");
        vo.setCommonAvailabilityZone("test");
        vo.setCommonKeypairName("test");
        vo.setCommonKeypairPath("test.pem");
        vo.setCommonProject("admin");
        vo.setCommonSecurityGroup("test");
        vo.setIaasType("AWS");
        vo.setCommonTenant("test");
        vo.setCpiInfoId(1);
        vo.setRecid(1);
        vo.setCpiName("cpi_test");
        if("conflict".equals(type)){
        	vo.setCpiName("cpi_test2");
        }
        vo.setOpenstackVersion("v2");
        vo.setIaasConfigAlias("openstack-config");
        vo.setIaasConfigId(1);
        vo.setCreateUserId("admin");
        vo.setUpdateUserId("admin");
        return vo;
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap CPI 정보 입력 값 설정
     * @title : setCpiConfigInfo
     * @return : HbBootstrapCpiConfigDTO
    *****************************************************************/
    private HbBootstrapCpiConfigDTO setCpiConfigInfo(String type) {
        HbBootstrapCpiConfigDTO dto = new HbBootstrapCpiConfigDTO();
        if(!"insert".equals(type)){
            dto.setCpiInfoId("1");
        }
        dto.setIaasType("AWS");
        dto.setIaasConfigId("1");
        dto.setCpiName("cpi_test");
        
        dto.getCpiInfoId();
        dto.getIaasConfigId();
        dto.getIaasType();
        dto.getCpiName();
        return dto;
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap CPI 목록 정보 결과 값 설정
     * @title : expectCpiConfigList
     * @return : List<HbBootstrapCpiConfigVO>
    *****************************************************************/
    private List<HbBootstrapCpiConfigVO> expectCpiConfigList() {
        List<HbBootstrapCpiConfigVO> list = new ArrayList<HbBootstrapCpiConfigVO>();
        HbBootstrapCpiConfigVO vo = new HbBootstrapCpiConfigVO();
        vo.setCommonAccessUser("test");
        vo.setCommonAvailabilityZone("test");
        vo.setCommonKeypairName("test");
        vo.setCommonKeypairPath("test.pem");
        vo.setCommonProject("admin");
        vo.setCommonSecurityGroup("test");
        vo.setIaasType("AWS");
        vo.setCommonTenant("test");
        vo.setCpiInfoId(1);
        vo.setRecid(1);
        vo.setCpiName("cpi_test");
        vo.setOpenstackVersion("v2");
        vo.setIaasConfigAlias("openstack-config");
        vo.setIaasConfigId(1);
        vo.setCreateUserId("admin");
        vo.setUpdateUserId("admin");
        
        vo.getIaasType();
        vo.getCommonAccessUser();
        vo.getCommonAvailabilityZone();
        vo.getCommonKeypairName();
        vo.getCommonKeypairPath();
        vo.getCommonProject();
        vo.getCommonSecurityGroup();
        vo.getCommonTenant();
        vo.getCpiInfoId();
        vo.getRecid();
        vo.getCpiName();
        vo.getCreateUserId();
        vo.getUpdateUserId();
        vo.getIaasConfigAlias();
        vo.getIaasConfigId();
        vo.getOpenstackVersion();
        list.add(vo);
        return list;
    }
    
}
