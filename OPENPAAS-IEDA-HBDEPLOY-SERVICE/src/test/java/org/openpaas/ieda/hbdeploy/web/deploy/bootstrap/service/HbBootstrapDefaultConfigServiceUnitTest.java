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
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapDefaultConfigDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapDefaultConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto.HbBootstrapDefaultConfigDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class HbBootstrapDefaultConfigServiceUnitTest extends BaseHbDeployControllerUnitTest{
    
    @InjectMocks HbBootstrapDefaultConfigService mockHbBootstrapDefaultConfigService;
    @Mock HbBootstrapDefaultConfigDAO mockHbBootstrapDefaultConfigDAO;
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
     * @description : 기본 정보 목록 조회 Unit Test
     * @title : testGetDefaultConfigInfoList
     * @return : void
    ***************************************************/
    @Test
    public void testGetDefaultConfigInfoList(){
        List<HbBootstrapDefaultConfigVO> expectList = expectDefaultConfigList();
        when(mockHbBootstrapDefaultConfigDAO.selectBootstrapDefaultConfigInfoList()).thenReturn(expectList);
        List<HbBootstrapDefaultConfigVO> resultList = mockHbBootstrapDefaultConfigService.getDefaultConfigInfoList();
        assertEquals(expectList.size(), resultList.size());
        assertEquals(expectList.get(0).getBoshCpiRelease(), resultList.get(0).getBoshCpiRelease());
        assertEquals(expectList.get(0).getBoshRelease(), resultList.get(0).getBoshRelease());
        assertEquals(expectList.get(0).getCreateUserId(), resultList.get(0).getCreateUserId());
        assertEquals(expectList.get(0).getCredentialKeyName(), resultList.get(0).getCredentialKeyName());
        assertEquals(expectList.get(0).getDefaultConfigName(), resultList.get(0).getDefaultConfigName());
        assertEquals(expectList.get(0).getDeploymentName(), resultList.get(0).getDeploymentName());
        assertEquals(expectList.get(0).getDirectorName(), resultList.get(0).getDirectorName());
        assertEquals(expectList.get(0).getEnableSnapshots(), resultList.get(0).getEnableSnapshots());
        assertEquals(expectList.get(0).getIaasType(), resultList.get(0).getIaasType());
        assertEquals(expectList.get(0).getNtp(), resultList.get(0).getNtp());
        assertEquals(expectList.get(0).getPaastaMonitoringRelease(), resultList.get(0).getPaastaMonitoringRelease());
        assertEquals(expectList.get(0).getPaastaMonitoringUse(), resultList.get(0).getPaastaMonitoringUse());
    }
    
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 기본 목록 정보 삽입 Unit Test
     * @title : testInsertDefaultConfigInfo
     * @return : void
    ***************************************************/
    @Test
    public void testInsertDefaultConfigInfo(){
        HbBootstrapDefaultConfigDTO dto = setDefaultConfigInfo("insert");
        when(mockHbBootstrapDefaultConfigDAO.selectBootstrapDefaultConfigByName(anyString())).thenReturn(0);
        mockHbBootstrapDefaultConfigService.saveDefaultConfigInfo(dto, principal);
    }

    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 기본 목록 정보 삽입 중 이미 등록 한 기본 정보 중복이 존재 할 경우 Unit Test
     * @title : testInsertDefaultConfigInfoConflictCpiConfigName
     * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testInsertDefaultConfigInfoConflictCpiConfigName(){
        HbBootstrapDefaultConfigDTO dto = setDefaultConfigInfo("insert");
        when(mockHbBootstrapDefaultConfigDAO.selectBootstrapDefaultConfigByName(anyString())).thenReturn(1);
        mockHbBootstrapDefaultConfigService.saveDefaultConfigInfo(dto, principal);
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 기본 목록 정보 수정 Unit Test
     * @title : testUpdateDefaultConfigInfo
     * @return : void
    ***************************************************/
    @Test
    public void testUpdateDefaultConfigInfo(){
        HbBootstrapDefaultConfigDTO dto = setDefaultConfigInfo("update");
        when(mockHbBootstrapDefaultConfigDAO.selectBootstrapDefaultConfigByName(anyString())).thenReturn(0);
        when(mockHbBootstrapDefaultConfigDAO.selectBootstrapDefaultConfigInfo(anyInt(), anyString())).thenReturn(expectDefailtConfigInfo(""));
        mockHbBootstrapDefaultConfigService.saveDefaultConfigInfo(dto, principal);
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 기본 목록 정보 수정 Config Name이 존재 할 경우 Unit Test
     * @title : testUpdateDefaultConfigInfoConflictCpiConfigName
     * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testUpdateDefaultConfigInfoConflictCpiConfigName(){
        HbBootstrapDefaultConfigDTO dto = setDefaultConfigInfo("update");
        when(mockHbBootstrapDefaultConfigDAO.selectBootstrapDefaultConfigByName(anyString())).thenReturn(1);
        when(mockHbBootstrapDefaultConfigDAO.selectBootstrapDefaultConfigInfo(anyInt(), anyString())).thenReturn(expectDefailtConfigInfo("conflict"));
        mockHbBootstrapDefaultConfigService.saveDefaultConfigInfo(dto, principal);
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Default 정보 설정 값
     * @title : expectDefailtConfigInfo
     * @return : void
    ***************************************************/
    private HbBootstrapDefaultConfigVO expectDefailtConfigInfo(String type) {
        HbBootstrapDefaultConfigVO vo = new HbBootstrapDefaultConfigVO();
        vo.setIaasType("Openstack");
        vo.setCreateUserId("admin");
        vo.setDefaultConfigName("bosh");
        if("conflict".equals(type)){
            vo.setDefaultConfigName("bosh2");
        }
        vo.setDefaultConfigName("bosh");
        vo.setDeploymentName("bosh");
        vo.setDirectorName("bosh");
        vo.setBoshRelease("bosh-release");
        vo.setCredentialKeyName("bosh-cres.yml");
        vo.setNtp("ntp");
        vo.setBoshCpiRelease("cpi-release");
        vo.setEnableSnapshots("true");
        vo.setSnapshotSchedule("007***UFC");
        vo.setUpdateUserId("admin");
        vo.setPaastaMonitoringUse("true");
        vo.setPaastaMonitoringRelease("monitering-release");
        
        vo.getIaasType();
        vo.getCreateUserId();
        vo.getDefaultConfigName();
        vo.getDeploymentName();
        vo.getDirectorName();
        vo.getBoshRelease();
        vo.getCredentialKeyName();
        vo.getNtp();
        vo.getBoshCpiRelease();
        vo.getEnableSnapshots();
        vo.getSnapshotSchedule();
        vo.getUpdateUserId();
        vo.getPaastaMonitoringUse();
        vo.getPaastaMonitoringRelease();
        return vo;
    }

    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 기본 목록 정보 삭제 Unit Test
     * @title : testDeleteDefaultConfigInfo
     * @return : void
    ***************************************************/
    @Test
    public void testDeleteDefaultConfigInfo(){
        HbBootstrapDefaultConfigDTO dto = setDefaultConfigInfo("update");
        mockHbBootstrapDefaultConfigService.deleteDefaultConfigInfo(dto, principal);
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 기본 목록 정보 삭제 아이디 값이 없을 경우 Unit Test
     * @title : testDeleteDefaultConfigInfoIdEmpty
     * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testDeleteDefaultConfigInfoIdEmpty(){
        HbBootstrapDefaultConfigDTO dto = setDefaultConfigInfo("insert");
        mockHbBootstrapDefaultConfigService.deleteDefaultConfigInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap 기본 정보 목록 결과 값 설정
     * @title : expectDefaultConfigList
     * @return : List<HbBootstrapDefaultConfigVO>
    *****************************************************************/
    private List<HbBootstrapDefaultConfigVO> expectDefaultConfigList() {
        List<HbBootstrapDefaultConfigVO> list = new ArrayList<HbBootstrapDefaultConfigVO>();
        HbBootstrapDefaultConfigVO vo = new HbBootstrapDefaultConfigVO();
        vo.setIaasType("Openstack");
        vo.setCreateUserId("admin");
        vo.setDefaultConfigName("bosh");
        vo.setDeploymentName("bosh");
        vo.setDirectorName("bosh");
        vo.setBoshRelease("bosh-release");
        vo.setCredentialKeyName("bosh-cres.yml");
        vo.setNtp("ntp");
        vo.setBoshCpiRelease("cpi-release");
        vo.setEnableSnapshots("true");
        vo.setSnapshotSchedule("007***UFC");
        vo.setUpdateUserId("admin");
        vo.setPaastaMonitoringUse("true");
        vo.setPaastaMonitoringRelease("monitering-release");
        
        vo.getIaasType();
        vo.getCreateUserId();
        vo.getDefaultConfigName();
        vo.getDeploymentName();
        vo.getDirectorName();
        vo.getBoshRelease();
        vo.getCredentialKeyName();
        vo.getNtp();
        vo.getBoshCpiRelease();
        vo.getEnableSnapshots();
        vo.getSnapshotSchedule();
        vo.getUpdateUserId();
        vo.getPaastaMonitoringUse();
        vo.getPaastaMonitoringRelease();
        list.add(vo);
        return list;
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap 기본 정보 저장 결과 값 설정
     * @title : setDefaultConfigInfo
     * @return : HbBootstrapDefaultConfigDTO
    *****************************************************************/
    private HbBootstrapDefaultConfigDTO setDefaultConfigInfo(String type) {
        HbBootstrapDefaultConfigDTO dto = new HbBootstrapDefaultConfigDTO();
        dto.setIaasType("AWS");
        dto.setDeploymentName("bosh");
        dto.setDirectorName("bosh");
        dto.setCredentialKeyName("bosh-cres.yml");
        dto.setNtp("007***UFC");
        dto.setBoshCpiRelease("bosh-cpi-release");
        dto.setBoshRelease("bosh-relesae");
        dto.setEnableSnapshots("true");
        dto.setSnapshotSchedule("snapshot");
        dto.setPaastaMonitoringUse("true");
        dto.setPaastaMonitoringRelease("monitering-release");
        dto.setDefaultConfigName("defaultConfigName");
        if("update".equals(type)){
        	dto.setId("1");
        }
        dto.getIaasType();
        dto.getId();
        dto.getDeploymentName();
        dto.getDirectorName();
        dto.getCredentialKeyName();
        dto.getNtp();
        dto.getBoshCpiRelease();
        dto.getBoshRelease();
        dto.getEnableSnapshots();
        dto.getSnapshotSchedule();
        dto.getPaastaMonitoringUse();
        dto.getPaastaMonitoringRelease();
        dto.getDefaultConfigName();
        return dto;
    }
    
}
