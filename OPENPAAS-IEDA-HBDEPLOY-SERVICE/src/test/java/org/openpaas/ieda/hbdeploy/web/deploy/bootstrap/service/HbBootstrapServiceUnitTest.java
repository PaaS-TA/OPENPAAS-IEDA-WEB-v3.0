package org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.service;

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
import org.openpaas.ieda.deploy.web.common.dao.CommonDeployDAO;
import org.openpaas.ieda.deploy.web.common.service.CommonDeployService;
import org.openpaas.ieda.hbdeploy.web.common.base.BaseHbDeployControllerUnitTest;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapVO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto.HbBootStrapDeployDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class HbBootstrapServiceUnitTest extends BaseHbDeployControllerUnitTest{
    
    @InjectMocks HbBootstrapService mockHbBootstrapService;
    @Mock HbBootstrapDAO mockHbBootstrapDAO;
    @Mock CommonDeployDAO mockCommonDeployDAO;
    @Mock CommonDeployService mockCommonDeployService;
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
     * @description : 이종 BOOTSTRAP 목록 정보 조회 UNIT TEST
     * @title : testGetHbBootstrapList
     * @return : void
    ***************************************************/
    @Test
    public void testGetHbBootstrapList(){
        List<HbBootstrapVO> expectList = expectBootstrapConfigList();
        when(mockHbBootstrapDAO.selectBootstrapList(anyString())).thenReturn(expectList);
        List<HbBootstrapVO> resultList = mockHbBootstrapService.getHbBootstrapList("InstallAble");
        assertEquals(expectList.size(), resultList.size());
        assertEquals(expectList.get(0).getBootstrapConfigName(), resultList.get(0).getBootstrapConfigName());
        assertEquals(expectList.get(0).getCpiConfigInfo(), resultList.get(0).getCpiConfigInfo());
        assertEquals(expectList.get(0).getDeployLog(), resultList.get(0).getDeployLog());
        assertEquals(expectList.get(0).getResourceConfigInfo(), resultList.get(0).getResourceConfigInfo());
        assertEquals(expectList.get(0).getNetworkConfigInfo(), resultList.get(0).getNetworkConfigInfo());
        assertEquals(expectList.get(0).getDeploymentFile(), resultList.get(0).getDeploymentFile());
        assertEquals(expectList.get(0).getDeployStatus(), resultList.get(0).getDeployStatus());
        assertEquals(expectList.get(0).getIaasType(), resultList.get(0).getIaasType());
    }
    
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 BOOTSTRAP 상세 정보 조회 UNIT TEST
     * @title : testGetHbBootstrapInfo
     * @return : void
    ***************************************************/
    @Test
    public void testGetHbBootstrapInfo(){
        HbBootstrapVO expectVo = expectBootstrapConfig();
        when(mockHbBootstrapDAO.selectBootstrapConfigInfo(anyInt(), anyString())).thenReturn( expectVo);
        HbBootstrapVO resultVo = mockHbBootstrapService.getHbBootstrapInfo(1, "Openstack");
        assertEquals(expectVo.getBootstrapConfigName(), resultVo.getBootstrapConfigName());
        assertEquals(expectVo.getCpiConfigInfo(), resultVo.getCpiConfigInfo());
        assertEquals(expectVo.getDefaultConfigInfo(), resultVo.getDefaultConfigInfo());
        assertEquals(expectVo.getResourceConfigInfo(), resultVo.getResourceConfigInfo());
        assertEquals(expectVo.getDeployLog(), resultVo.getDeployLog());
        assertEquals(expectVo.getDeploymentFile(), resultVo.getDeploymentFile());
        assertEquals(expectVo.getIaasType(), resultVo.getIaasType());
        assertEquals(expectVo.getNetworkConfigInfo(), resultVo.getNetworkConfigInfo());
    }
    
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 BOOTSTRAP 상세 정보 조회 Exception UNIT TEST
     * @title : testGetHbBootstrapInfo
     * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testGetHbBootstrapInfoNullP(){
        when(mockMessageSource.getMessage(anyString(), anyObject(), anyObject())).thenReturn("conflic_exception");
        when(mockHbBootstrapDAO.selectBootstrapConfigInfo(anyInt(), anyString())).thenReturn(null);
        mockHbBootstrapService.getHbBootstrapInfo(1, "Openstack");
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 BOOTSTRAP 상세 정보 조회 UNIT TEST
     * @title : testDeleteBootstrapInfo
     * @return : void
    ***************************************************/
    @Test
    public void testDeleteBootstrapInfo(){
        HbBootStrapDeployDTO dto = new HbBootStrapDeployDTO();
        dto.setId("1");
        dto.setBootstrapConfigName("bootstrap-config");
        mockHbBootstrapService.deleteBootstrapInfo(dto);
    }
    
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 BOOTSTRAP 상세 정보 조회 Exception UNIT TEST
     * @title : testDeleteBootstrapInfo
     * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testDeleteBootstrapInfoIdEmpty(){
    	when(mockMessageSource.getMessage(anyString(), anyObject(), anyObject())).thenReturn("conflic_exception");
        HbBootStrapDeployDTO dto = new HbBootStrapDeployDTO();
        dto.setId("");
        mockHbBootstrapService.deleteBootstrapInfo(dto);
    }
    
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : BOOTSTRAP 상세 조회 값 설정
     * @title : expectBootstrapConfig
     * @return : HbBootstrapVO
    *****************************************************************/
    private HbBootstrapVO expectBootstrapConfig(){
        HbBootstrapVO vo = new HbBootstrapVO();
        vo.setBootstrapConfigName("bootstrap-config");
        vo.setId(1);
        vo.setCpiConfigInfo("cpi-config");
        vo.setNetworkConfigInfo("network-config");
        vo.setDefaultConfigInfo("default-config");
        vo.setIaasType("Openstack");
        vo.setDeployLog("Done");
        vo.setResourceConfigInfo("resource-config");
        vo.setDeployLog("starting...");
        vo.setDeploymentFile("openstack-microbosh-1.yml");
        return vo;
    }
    
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : BOOTSTRAP 조회 값 설정
     * @title : expectCpiConfigList
     * @return : List<HbBootstrapVO>
    *****************************************************************/
    private List<HbBootstrapVO> expectBootstrapConfigList() {
        List<HbBootstrapVO> list = new ArrayList<HbBootstrapVO>();
        HbBootstrapVO vo = new HbBootstrapVO();
        vo.setBootstrapConfigName("bootstrap-config");
        vo.setId(1);
        vo.setCpiConfigInfo("cpi-config");
        vo.setNetworkConfigInfo("network-config");
        vo.setDefaultConfigInfo("default-config");
        vo.setIaasType("Openstack");
        vo.setDeployLog("Done");
        vo.setResourceConfigInfo("resource-config");
        vo.setDeployLog("starting...");
        vo.setDeploymentFile("openstack-microbosh-1.yml");
        list.add(vo);
        return list;
    }
    
}
