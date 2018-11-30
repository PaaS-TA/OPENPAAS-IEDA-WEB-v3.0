package org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

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
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentVO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class HbCfDeploymentServiceUnitTest extends BaseHbDeployControllerUnitTest {
    
    @InjectMocks private HbCfDeploymentService mockHbCfDeploymentService;
    @Mock private HbCfDeploymentDAO mockHbCfDeploymentDAO;
    @Mock private MessageSource mockMessageSource;
    
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
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 목록 정보 조회 Unit Test
     * @title : testGetHbCfDeploymentList
     * @return : void
    *****************************************************************/
    @Test
    public void testGetHbCfDeploymentList(){
        List<HbCfDeploymentVO> expectList = setCfConfigList();
        when(mockHbCfDeploymentDAO.selectCfDeploymentList(anyString())).thenReturn(expectList);
        List<HbCfDeploymentVO> resultList = mockHbCfDeploymentService.getHbCfDeploymentList("installAble");
        assertEquals(expectList.size(), resultList.size());
        assertEquals(expectList.get(0).getCfDeploymentConfigName(), resultList.get(0).getCfDeploymentConfigName());
        assertEquals(expectList.get(0).getCloudConfigFile(), resultList.get(0).getCloudConfigFile());
        assertEquals(expectList.get(0).getIaasType(), resultList.get(0).getIaasType());
        assertEquals(expectList.get(0).getCredentialConfigInfo(), resultList.get(0).getCredentialConfigInfo());
        assertEquals(expectList.get(0).getDefaultConfigInfo(), resultList.get(0).getDefaultConfigInfo());
        assertEquals(expectList.get(0).getNetworkConfigInfo(), resultList.get(0).getNetworkConfigInfo());
        assertEquals(expectList.get(0).getInstanceConfigInfo(), resultList.get(0).getInstanceConfigInfo());
        assertEquals(expectList.get(0).getResourceConfigInfo(), resultList.get(0).getResourceConfigInfo());
        assertEquals(expectList.get(0).getId(), resultList.get(0).getId());
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 상세 정보 조회 Unit Test
     * @title : testGetHbCfDeploymentList
     * @return : void
    *****************************************************************/
    @Test
    public void testGetHbCfDeploymentInfo(){
        HbCfDeploymentVO expectVo = setCfConfig();
        when(mockHbCfDeploymentDAO.selectCfDeploymentConfigInfo(anyInt())).thenReturn(expectVo);
        HbCfDeploymentVO resultVo = mockHbCfDeploymentService.getHbCfDeploymentInfo(0);
        assertEquals(expectVo.getCfDeploymentConfigName(), resultVo.getCfDeploymentConfigName());
        assertEquals(expectVo.getCloudConfigFile(), resultVo.getCloudConfigFile());
        assertEquals(expectVo.getCredentialConfigInfo(), resultVo.getCredentialConfigInfo());
        assertEquals(expectVo.getIaasType(), resultVo.getIaasType());
        assertEquals(expectVo.getDefaultConfigInfo(), resultVo.getDefaultConfigInfo());
        assertEquals(expectVo.getCfDeploymentConfigName(), resultVo.getCfDeploymentConfigName());
        assertEquals(expectVo.getNetworkConfigInfo(), resultVo.getNetworkConfigInfo());
        assertEquals(expectVo.getInstanceConfigInfo(), resultVo.getInstanceConfigInfo());
        assertEquals(expectVo.getResourceConfigInfo(), resultVo.getResourceConfigInfo());
        assertEquals(expectVo.getId(), resultVo.getId());
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 상세 정보 조회 Excepton Unit Test
     * @title : testGetHbCfDeploymentList
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testGetHbCfDeploymentInfoNull(){
        when(mockHbCfDeploymentDAO.selectCfDeploymentConfigInfo(anyInt())).thenReturn(null);
        when(mockMessageSource.getMessage(anyString(), anyObject(), anyObject())).thenReturn("null");
        mockHbCfDeploymentService.getHbCfDeploymentInfo(0);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 설치 정보 결과 값 설정
     * @title : setCfConfigList
     * @return : List<HbCfDeploymentVO>
    *****************************************************************/
    private HbCfDeploymentVO setCfConfig() {
        HbCfDeploymentVO vo = new HbCfDeploymentVO();
        vo.setCfDeploymentConfigName("cf-config");
        vo.setCloudConfigFile("cloud-config.yml");
        vo.setCredentialConfigInfo("crendential-config");
        vo.setDefaultConfigInfo("default-config");
        vo.setIaasType("Openstack");
        vo.setResourceConfigInfo("resource-config");
        vo.setDeployStatus("done");
        vo.setNetworkConfigInfo("network-config");
        vo.setId(1);
        vo.setInstanceConfigInfo("instance-config");
        vo.setTaskId("1");
        return vo;
    }
    
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 설치 목록 조회 결과 값 설정
     * @title : setCfConfigList
     * @return : List<HbCfDeploymentVO>
    *****************************************************************/
    private List<HbCfDeploymentVO> setCfConfigList() {
        List<HbCfDeploymentVO> list = new ArrayList<HbCfDeploymentVO>();
        HbCfDeploymentVO vo = new HbCfDeploymentVO();
        vo.setCfDeploymentConfigName("cf-config");
        vo.setCloudConfigFile("cloud-config.yml");
        vo.setCredentialConfigInfo("crendential-config");
        vo.setDefaultConfigInfo("default-config");
        vo.setIaasType("Openstack");
        vo.setResourceConfigInfo("resource-config");
        vo.setDeployStatus("done");
        vo.setNetworkConfigInfo("network-config");
        vo.setId(1);
        vo.setInstanceConfigInfo("instance-config");
        vo.setTaskId("1");
        list.add(vo);
        return list;
    }
    
}
