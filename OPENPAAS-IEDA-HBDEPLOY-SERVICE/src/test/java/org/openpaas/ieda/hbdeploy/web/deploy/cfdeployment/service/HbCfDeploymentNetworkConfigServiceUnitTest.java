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
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentNetworkConfigDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentNetworkConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dto.HbCfDeploymentNetworkConfigDTO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service.HbCfDeploymentNetworkConfigService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class HbCfDeploymentNetworkConfigServiceUnitTest extends BaseHbDeployControllerUnitTest{
    
    @InjectMocks HbCfDeploymentNetworkConfigService mockHbCfDeploymentNetworkConfigService;
    @Mock MessageSource mockMessageSource;
    @Mock HbCfDeploymentNetworkConfigDAO mockHbCfDeploymentNetworkConfigDAO;
    
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
     * @description : 이종 CF DEPLOYMENT 네트워크  정보 목록 조회 Unit Test
     * @title : testGetNetworkConfigInfoList
     * @return : void
    *****************************************************************/
    @Test
    public void testGetNetworkConfigInfoList(){
        List<HbCfDeploymentNetworkConfigVO> expectList = setCfNetworkConfigList();
        when(mockHbCfDeploymentNetworkConfigDAO.selectHbCfDeploymentNetworkConfigInfoList()).thenReturn(expectList);
        List<HbCfDeploymentNetworkConfigVO> resultList = mockHbCfDeploymentNetworkConfigService.getNetworkConfigInfoList();
        assertEquals(expectList.size(), resultList.size());
        assertEquals(expectList.get(0).getAvailabilityZone(), resultList.get(0).getAvailabilityZone());
        assertEquals(expectList.get(0).getIaasType(), resultList.get(0).getIaasType());
        assertEquals(expectList.get(0).getId(), resultList.get(0).getId());
        assertEquals(expectList.get(0).getNetworkName(), resultList.get(0).getNetworkName());
        assertEquals(expectList.get(0).getPublicStaticIp(), resultList.get(0).getPublicStaticIp());
        assertEquals(expectList.get(0).getSubnetReservedTo1(), resultList.get(0).getSubnetReservedTo1());
        assertEquals(expectList.get(0).getSubnetReservedFrom1(), resultList.get(0).getSubnetReservedFrom1());
        assertEquals(expectList.get(0).getSecurityGroup(), resultList.get(0).getSecurityGroup());
        assertEquals(expectList.get(0).getSeq(), resultList.get(0).getSeq());
        assertEquals(expectList.get(0).getSubnetGateway(), resultList.get(0).getSubnetGateway());
        assertEquals(expectList.get(0).getSubnetDns(), resultList.get(0).getSubnetDns());
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 네트워크 정보 저장 Unit Test
     * @title : testInsertNetworkConfigInfo
     * @return : void
    *****************************************************************/
    public void testInsertNetworkConfigInfo(){
        HbCfDeploymentNetworkConfigDTO dto = setCfNetworkConfigInfo("insert");
        when(mockHbCfDeploymentNetworkConfigDAO.selectHbCfDeploymentNetworkConfigByName(anyString())).thenReturn(0);
        mockHbCfDeploymentNetworkConfigService.saveNetworkConfigInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 네트워크 정보 저장 Unit Test
     * @title : testUpdateNetworkConfigInfo
     * @return : void
    *****************************************************************/
    public void testUpdateNetworkConfigInfo(){
        HbCfDeploymentNetworkConfigDTO dto = setCfNetworkConfigInfo("update");
        when(mockHbCfDeploymentNetworkConfigDAO.selectHbCfDeploymentNetworkConfigByName(anyString())).thenReturn(0);
        HbCfDeploymentNetworkConfigVO vo = setCfNetworkConfig();
        when(mockHbCfDeploymentNetworkConfigDAO.selectHbCfDeploymentNetworkConfigInfo(anyInt())).thenReturn(vo);
        mockHbCfDeploymentNetworkConfigService.saveNetworkConfigInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 네트워크 정보 저장 Excepiton Unit Test
     * @title : testUpdateNetworkConfigInfo
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testSaveNetworkConfigInfoConflict(){
        HbCfDeploymentNetworkConfigDTO dto = setCfNetworkConfigInfo("insert");
        when(mockHbCfDeploymentNetworkConfigDAO.selectHbCfDeploymentNetworkConfigByName(anyString())).thenReturn(1);
        when(mockMessageSource.getMessage(anyString(), anyObject(), anyObject())).thenReturn("conflic_exception");
        mockHbCfDeploymentNetworkConfigService.saveNetworkConfigInfo(dto, principal);
        
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 네트워크 정보 삭제 Unit Test
     * @title : testDeleteNetworkConfigInfo
     * @return : void
    *****************************************************************/
    @Test 
    public void testDeleteNetworkConfigInfo(){
    	HbCfDeploymentNetworkConfigDTO dto = setCfNetworkConfigInfo("update");
    	mockHbCfDeploymentNetworkConfigService.deleteNetworkConfigInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 네트워크 정보 삭제 Excepiton Unit Test
     * @title : testDeleteNetworkConfigInfoNull
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testDeleteNetworkConfigInfoNull(){
    	HbCfDeploymentNetworkConfigDTO dto = setCfNetworkConfigInfo("insert");
    	when(mockMessageSource.getMessage(anyString(), anyObject(), anyObject())).thenReturn("null");
    	mockHbCfDeploymentNetworkConfigService.deleteNetworkConfigInfo(dto, principal);
    }
    
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 네트워크 목록 정보 조회 값 설정
     * @title : setCfNetworkConfigList
     * @return : List<HbCfDeploymentNetworkConfigVO>
    *****************************************************************/
    private HbCfDeploymentNetworkConfigVO setCfNetworkConfig() {
        HbCfDeploymentNetworkConfigVO vo = new HbCfDeploymentNetworkConfigVO();
        vo.setAvailabilityZone("us-west-1a");
        vo.setId(1);
        vo.setIaasType("Openstack");
        vo.setNetworkName("network-config");
        vo.setPublicStaticIp("172.16.xxx.xxx");
        vo.setSecurityGroup("seg");
        vo.setSeq(1);
        vo.setSubnetGateway("172.16.100.1");
        vo.setSubnetDns("8.8.8.8");
        vo.setSubnetRange("10.0.0.0/24");
        vo.setSubnetReservedFrom1("10.0.0.1");
        vo.setSubnetReservedTo1("10.0.0.10");
        return vo;
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 저장/삭제 요청 값 설정
     * @title : setCfNetworkConfigInfo
     * @return : HbCfDeploymentNetworkConfigDTO
    *****************************************************************/
    private HbCfDeploymentNetworkConfigDTO setCfNetworkConfigInfo(String type) {
        HbCfDeploymentNetworkConfigDTO dto = new HbCfDeploymentNetworkConfigDTO();
        if("update".equalsIgnoreCase(type)){
            dto.setId(1);
        }
        dto.setIaasType("Openstack");
        dto.setNetworkName("network-config");
        return dto;
    }

    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 이종 CF DEPLOYMENT 네트워크 목록 정보 조회 값 설정
     * @title : setCfNetworkConfigList
     * @return : List<HbCfDeploymentNetworkConfigVO>
    *****************************************************************/
    private List<HbCfDeploymentNetworkConfigVO> setCfNetworkConfigList() {
        List<HbCfDeploymentNetworkConfigVO> list = new ArrayList<HbCfDeploymentNetworkConfigVO>();
        HbCfDeploymentNetworkConfigVO vo = new HbCfDeploymentNetworkConfigVO();
        vo.setAvailabilityZone("us-west-1a");
        vo.setId(1);
        vo.setIaasType("Openstack");
        vo.setNetworkName("network-config");
        vo.setPublicStaticIp("172.16.xxx.xxx");
        vo.setSecurityGroup("seg");
        vo.setSeq(1);
        vo.setSubnetGateway("172.16.100.1");
        vo.setSubnetDns("8.8.8.8");
        vo.setSubnetRange("10.0.0.0/24");
        vo.setSubnetReservedFrom1("10.0.0.1");
        vo.setSubnetReservedTo1("10.0.0.10");
        list.add(vo);
        return list;
    }
    
}
