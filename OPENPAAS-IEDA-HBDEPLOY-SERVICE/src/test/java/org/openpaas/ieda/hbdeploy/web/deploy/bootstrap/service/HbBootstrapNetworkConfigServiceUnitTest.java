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
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapNetworkConfigDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapNetworkConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto.HbBootstrapNetworkConfigDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class HbBootstrapNetworkConfigServiceUnitTest extends BaseHbDeployControllerUnitTest{
    
    @InjectMocks HbBootstrapNetworkConfigService mockHbBootstrapNetworkConfigService;
    @Mock HbBootstrapNetworkConfigDAO mockHbBootstrapNetworkConfigDAO;
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
     * @description : 네트워크 정보 목록 조회 Unit Test
     * @title : testGetNetworkConfigInfoList
     * @return : void
    ***************************************************/
    @Test
    public void testGetNetworkConfigInfoList(){
        List<HbBootstrapNetworkConfigVO> expectList = expectNetworkConfigList();
        when(mockHbBootstrapNetworkConfigDAO.selectBootstrapNetworkConfigInfoList()).thenReturn(expectList);
        List<HbBootstrapNetworkConfigVO> resultList = mockHbBootstrapNetworkConfigService.getNetworkConfigInfoList();
        assertEquals(expectList.size(), resultList.size());
        assertEquals(expectList.get(0).getIaasType(), resultList.get(0).getIaasType());
        assertEquals(expectList.get(0).getNetworkConfigName(), resultList.get(0).getNetworkConfigName());
        assertEquals(expectList.get(0).getPublicStaticIp(), resultList.get(0).getPublicStaticIp());
        assertEquals(expectList.get(0).getSubnetDns(), resultList.get(0).getSubnetDns());
        assertEquals(expectList.get(0).getSubnetGateway(), resultList.get(0).getSubnetGateway());
        assertEquals(expectList.get(0).getSubnetRange(), resultList.get(0).getSubnetRange());
        assertEquals(expectList.get(0).getPrivateStaticIp(), resultList.get(0).getPrivateStaticIp());
        assertEquals(expectList.get(0).getSubnetId(), resultList.get(0).getSubnetId());
    }
    
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 네트워크 목록 정보 삽입 Unit Test
     * @title : testInsertNetworkConfigInfo
     * @return : void
    ***************************************************/
    @Test
    public void testInsertNetworkConfigInfo(){
        HbBootstrapNetworkConfigDTO dto = setNetworkConfigInfo("insert");
        when(mockHbBootstrapNetworkConfigDAO.selectBootstrapNetworkConfigByName(anyString())).thenReturn(0);
        mockHbBootstrapNetworkConfigService.saveNetworkConfigInfo(dto, principal);
    }

    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 네트워크 목록 정보 삽입 중 이미 등록 한 기본 정보 중복이 존재 할 경우 Unit Test
     * @title : testInsertCpiConfigInfoConflictCpiConfigName
     * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testInsertCpiConfigInfoConflictCpiConfigName(){
        HbBootstrapNetworkConfigDTO dto = setNetworkConfigInfo("insert");
        when(mockHbBootstrapNetworkConfigDAO.selectBootstrapNetworkConfigByName(anyString())).thenReturn(1);
        mockHbBootstrapNetworkConfigService.saveNetworkConfigInfo(dto, principal);
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 네트워크 목록 정보 수정 Unit Test
     * @title : testInsertCpiConfigInfo
     * @return : void
    ***************************************************/
    @Test
    public void testUpdateCpiConfigInfo(){
        HbBootstrapNetworkConfigDTO dto = setNetworkConfigInfo("update");
        when(mockHbBootstrapNetworkConfigDAO.selectBootstrapNetworkConfigByName(anyString())).thenReturn(0);
        when(mockHbBootstrapNetworkConfigDAO.selectBootstrapNetworkConfigInfo(anyInt(), anyString())).thenReturn(expectDefailtConfigInfo(""));
        mockHbBootstrapNetworkConfigService.saveNetworkConfigInfo(dto, principal);
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 네트워크 목록 정보 수정 Config Name이 존재 할 경우 Unit Test
     * @title : testUpdateCpiConfigInfoConflictCpiConfigName
     * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testUpdateCpiConfigInfoConflictCpiConfigName(){
        HbBootstrapNetworkConfigDTO dto = setNetworkConfigInfo("update");
        when(mockHbBootstrapNetworkConfigDAO.selectBootstrapNetworkConfigByName(anyString())).thenReturn(1);
        when(mockHbBootstrapNetworkConfigDAO.selectBootstrapNetworkConfigInfo(anyInt(), anyString())).thenReturn(expectDefailtConfigInfo("conflict"));
        mockHbBootstrapNetworkConfigService.saveNetworkConfigInfo(dto, principal);
    }
    
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 네트워크 목록 정보 삭제 Unit Test
     * @title : testDeleteCpiConfigInfo
     * @return : void
    ***************************************************/
    private HbBootstrapNetworkConfigVO expectDefailtConfigInfo(String type) {
        HbBootstrapNetworkConfigVO vo = new HbBootstrapNetworkConfigVO();
        vo.setIaasType("Openstack");
        vo.setCreateUserId("admin");
        vo.setNetworkConfigName("bosh");
        if("conflict".equals(type)){
            vo.setNetworkConfigName("bosh2");
        }
        vo.setSubnetId("bosh-snet");
        vo.setPrivateStaticIp("192.168.10.12");
        vo.setSubnetRange("192.168.10.0/24");
        vo.setSubnetGateway("172.16.100.1");
        vo.setSubnetDns("8.8.8.8");
        vo.setPublicStaticIp("172.16.100.23");
        
        vo.getPublicStaticIp();
        vo.getSubnetDns();
        vo.getSubnetGateway();
        vo.getSubnetRange();
        vo.getPrivateStaticIp();
        vo.getSubnetId();
        vo.getIaasType();
        vo.getCreateUserId();
        vo.getNetworkConfigName();
        
        vo.getIaasType();
        vo.getCreateUserId();
        vo.getNetworkConfigName();
        return vo;
    }

    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 네트워크 목록 정보 삭제 Unit Test
     * @title : testDeleteCpiConfigInfo
     * @return : void
    ***************************************************/
    @Test
    public void testDeleteCpiConfigInfo(){
        HbBootstrapNetworkConfigDTO dto = setNetworkConfigInfo("update");
        mockHbBootstrapNetworkConfigService.deleteNetworkConfigInfo(dto, principal);
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 네트워크 목록 정보 삭제 아이디 값이 없을 경우 Unit Test
     * @title : testDeleteCpiConfigInfoIdEmpty
     * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testDeleteCpiConfigInfoIdEmpty(){
        HbBootstrapNetworkConfigDTO dto = setNetworkConfigInfo("insert");
        mockHbBootstrapNetworkConfigService.deleteNetworkConfigInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap 네트워크 정보 목록 결과 값 설정
     * @title : expectCpiConfigList
     * @return : List<HbBootstrapCpiConfigVO>
    *****************************************************************/
    private List<HbBootstrapNetworkConfigVO> expectNetworkConfigList() {
        List<HbBootstrapNetworkConfigVO> list = new ArrayList<HbBootstrapNetworkConfigVO>();
        HbBootstrapNetworkConfigVO vo = new HbBootstrapNetworkConfigVO();
        vo.setCreateUserId("admin");
        vo.setIaasType("Openstack");
        vo.setCreateUserId("admin");
        vo.setNetworkConfigName("bosh");
        vo.setSubnetId("bosh-snet");
        vo.setPrivateStaticIp("192.168.10.12");
        vo.setSubnetRange("192.168.10.0/24");
        vo.setSubnetGateway("172.16.100.1");
        vo.setSubnetDns("8.8.8.8");
        vo.setPublicStaticIp("172.16.100.23");
        
        vo.getPublicStaticIp();
        vo.getSubnetDns();
        vo.getSubnetGateway();
        vo.getSubnetRange();
        vo.getPrivateStaticIp();
        vo.getSubnetId();
        vo.getIaasType();
        vo.getCreateUserId();
        vo.getNetworkConfigName();
        vo.getIaasType();
        vo.getCreateUserId();
        vo.getNetworkConfigName();
        list.add(vo);
        return list;
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap 네트워크 정보 저장 결과 값 설정
     * @title : expectCpiConfigList
     * @return : List<HbBootstrapCpiConfigVO>
    *****************************************************************/
    private HbBootstrapNetworkConfigDTO setNetworkConfigInfo(String type) {
        HbBootstrapNetworkConfigDTO dto = new HbBootstrapNetworkConfigDTO();
        dto.setIaasType("AWS");
        if("update".equals(type)){
        	dto.setId("1");
        }
        dto.setIaasType("AWS");
        dto.setNetworkConfigName("NetworkConfigName");
        dto.setSubnetId("bosh-snet");
        dto.setPrivateStaticIp("192.168.10.12");
        dto.setSubnetRange("192.168.10.0/24");
        dto.setSubnetGateway("172.16.100.1");
        dto.setSubnetDns("8.8.8.8");
        dto.setPublicStaticIp("172.16.100.23");
        
        dto.getPublicStaticIp();
        dto.getSubnetDns();
        dto.getSubnetGateway();
        dto.getSubnetRange();
        dto.getPrivateStaticIp();
        dto.getSubnetId();
        dto.getIaasType();
        dto.getId();
        dto.getNetworkConfigName();
        
        
        dto.getIaasType();
        dto.getId();
        dto.getNetworkConfigName();
        return dto;
    }
    
}
