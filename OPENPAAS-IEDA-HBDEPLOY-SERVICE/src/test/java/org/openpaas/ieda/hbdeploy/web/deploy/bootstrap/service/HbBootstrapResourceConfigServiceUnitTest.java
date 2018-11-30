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
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapResourceConfigDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapResourceConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto.HbBootstrapResourceConfigDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class HbBootstrapResourceConfigServiceUnitTest extends BaseHbDeployControllerUnitTest {
    @InjectMocks HbBootstrapResourceConfigService mockHbBootstrapResourceConfigService;
    @Mock HbBootstrapResourceConfigDAO mockHbBootstrapResourceConfigDAO;
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
    public void testGetResourceConfigInfoList(){
        List<HbBootstrapResourceConfigVO> expectList = expectResourceConfigList();
        when(mockHbBootstrapResourceConfigDAO.selectBootstrapResourceConfigInfoList()).thenReturn(expectList);
        List<HbBootstrapResourceConfigVO> resultList = mockHbBootstrapResourceConfigService.getResourceConfigInfoList();
        assertEquals(expectList.size(), resultList.size());
        assertEquals(expectList.get(0).getIaasType(), resultList.get(0).getIaasType());
        assertEquals(expectList.get(0).getResourceConfigName(), resultList.get(0).getResourceConfigName());
        assertEquals(expectList.get(0).getStemcellName(), resultList.get(0).getStemcellName());
        assertEquals(expectList.get(0).getInstanceType(), resultList.get(0).getInstanceType());
        assertEquals(expectList.get(0).getVmPassword(), resultList.get(0).getVmPassword());
        assertEquals(expectList.get(0).getCreateUserId(), resultList.get(0).getCreateUserId());
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 리소스  정보 삽입 Unit Test
     * @title : testInsertResourceConfigInfo
     * @return : void
    ***************************************************/
    @Test
    public void testInsertResourceConfigInfo(){
        HbBootstrapResourceConfigDTO dto = setResourceConfigInfo("insert");
        when(mockHbBootstrapResourceConfigDAO.selectBootstrapResourceConfigByName(anyString())).thenReturn(0);
        mockHbBootstrapResourceConfigService.saveResourceConfigInfo(dto, principal);
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 리소스  정보 삽입 중 이미 등록 한 기본 정보 중복이 존재 할 경우 Unit Test
     * @title : testInsertResourceConfigInfoConflictResourceConfigName
     * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testInsertResourceConfigInfoConflictResourceConfigName(){
        HbBootstrapResourceConfigDTO dto = setResourceConfigInfo("nameConflict");
        when(mockHbBootstrapResourceConfigDAO.selectBootstrapResourceConfigByName(anyString())).thenReturn(1);
        mockHbBootstrapResourceConfigService.saveResourceConfigInfo(dto, principal);
    }
    
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 리소스  정보 수정 Unit Test
     * @title : testUpdateResourceConfigInfo
     * @return : void
    ***************************************************/
    @Test
    public void testUpdateResourceConfigInfo(){
        HbBootstrapResourceConfigDTO dto = setResourceConfigInfo("update");
        when(mockHbBootstrapResourceConfigDAO.selectBootstrapResourceConfigByName(anyString())).thenReturn(0);
        when(mockHbBootstrapResourceConfigDAO.selectBootstrapResourceConfigInfo(anyInt(), anyString())).thenReturn(expectDefailtConfigInfo(""));
        mockHbBootstrapResourceConfigService.saveResourceConfigInfo(dto, principal);
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 리소스 정보 수정 Config Name이 존재 할 경우 Unit Test
     * @title : testUpdateResourceConfigInfoConflictResourceConfigName
     * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testUpdateResourceConfigInfoConflictResourceConfigName(){
        HbBootstrapResourceConfigDTO dto = setResourceConfigInfo("update");
        when(mockHbBootstrapResourceConfigDAO.selectBootstrapResourceConfigByName(anyString())).thenReturn(1);
        when(mockHbBootstrapResourceConfigDAO.selectBootstrapResourceConfigInfo(anyInt(), anyString())).thenReturn(expectDefailtConfigInfo("conflict"));
        mockHbBootstrapResourceConfigService.saveResourceConfigInfo(dto, principal);
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 리소스 목록 정보 삭제 Unit Test
     * @title : testDeleteResouceConfigInfo
     * @return : void
    ***************************************************/
    @Test
    public void testDeleteResouceConfigInfo(){
    	HbBootstrapResourceConfigDTO dto = setResourceConfigInfo("delete");
        mockHbBootstrapResourceConfigService.deleteResourceConfigInfo(dto, principal);
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 리소스 정보 삭제 아이디 값이 없을 경우 Unit Test
     * @title : testDeleteResourceConfigInfoIdEmpty
     * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testDeleteResourceConfigInfoIdEmpty(){
    	HbBootstrapResourceConfigDTO dto = setResourceConfigInfo("idEmpty");
        mockHbBootstrapResourceConfigService.deleteResourceConfigInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap 리소 정보 결과 값 설정
     * @title : expectDefailtConfigInfo
     * @return : HbBootstrapResourceConfigVO
    *****************************************************************/    
    private HbBootstrapResourceConfigVO expectDefailtConfigInfo(String type) {
        HbBootstrapResourceConfigVO vo = new HbBootstrapResourceConfigVO();
        vo.setIaasType("Openstack");
        vo.setCreateUserId("admin");
        vo.setResourceConfigName("");
        if("conflict".equals(type)){
            vo.setResourceConfigName("bosh2");
        }
        vo.setStemcellName("light-bosh-stemcell-3468-21-aws-xen-hvm.tgz");
        vo.setInstanceType("t2.medium");
        vo.setVmPassword("admin");
        vo.setCreateUserId("admin");
        
        vo.getIaasType();
        vo.getResourceConfigName();
        vo.getStemcellName();
        vo.getInstanceType();
        vo.getVmPassword();
        vo.getCreateUserId();
        return vo;
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap 리소스 정보 저장 결과 값 설정
     * @title : setResourceConfigInfo
     * @return : HbBootstrapResourceConfigDTO
    *****************************************************************/    
    private HbBootstrapResourceConfigDTO setResourceConfigInfo(String type) {
        HbBootstrapResourceConfigDTO dto = new HbBootstrapResourceConfigDTO();
        dto.setIaasType("Openstack");
        if("insert".equals(type)){
            dto.setId(null);
        } else if("update".equals(type)){
            dto.setId(1);
        }else if("delete".equals(type)){
            dto.setId(2);
        }else {
            dto.setId(null);
        }
        dto.setResourceConfigName("test-name");
        dto.setStemcellName("light-bosh-stemcell-3468-21-aws-xen-hvm.tgz");
        dto.setInstanceType("t2.medium");
        dto.setVmPassword("admin");
        
        dto.getIaasType();
        dto.getId();
        dto.getResourceConfigName();
        dto.getStemcellName();
        dto.getInstanceType();
        dto.getVmPassword();
        
        return dto;
    }

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap 리소스 정보 목록 결과 값 설정
     * @title : expectResourceConfigList
     * @return : List<HbBootstrapResourceConfigVO>
    *****************************************************************/
    private List<HbBootstrapResourceConfigVO> expectResourceConfigList() {
        List<HbBootstrapResourceConfigVO> list = new ArrayList<HbBootstrapResourceConfigVO>();
        HbBootstrapResourceConfigVO vo = new HbBootstrapResourceConfigVO();
        vo.setIaasType("Openstack");
        vo.setResourceConfigName("test-name");
        vo.setStemcellName("light-bosh-stemcell-3468-21-aws-xen-hvm.tgz");
        vo.setInstanceType("t2.medium");
        vo.setVmPassword("admin");
        vo.setCreateUserId("admin");
        
        vo.getIaasType();
        vo.getResourceConfigName();
        vo.getStemcellName();
        vo.getInstanceType();
        vo.getVmPassword();
        vo.getCreateUserId();
        list.add(vo);
        return list;
    }
}