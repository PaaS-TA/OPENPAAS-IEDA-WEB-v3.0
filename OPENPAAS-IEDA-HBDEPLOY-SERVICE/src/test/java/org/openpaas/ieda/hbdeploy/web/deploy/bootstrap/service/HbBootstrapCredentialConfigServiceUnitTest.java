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
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapCredentialConfigDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapCredentialConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto.HbBootstrapCredentialConfigDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class HbBootstrapCredentialConfigServiceUnitTest extends BaseHbDeployControllerUnitTest{
    
    @InjectMocks HbBootstrapCredentialConfigService mockHbBootstrapCredentialConfigService;
    @Mock HbBootstrapCredentialConfigDAO mockHbBootstrapCredentialConfigDAO;
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
     * @description : 디렉터 인증서 정보 목록 조회 Unit Test
     * @title : testGetCredentialConfigInfoList
     * @return : void
    ***************************************************/
    @Test
    public void testGetCredentialConfigInfoList(){
        List<HbBootstrapCredentialConfigVO> expectList = expectCredentialConfigList();
        when(mockHbBootstrapCredentialConfigDAO.selectBootstrapCredentialConfigList()).thenReturn(expectList);
        List<HbBootstrapCredentialConfigVO> resultList = mockHbBootstrapCredentialConfigService.getHbBootstrapCredentialConfigInfoList();
        assertEquals(expectList.size(), resultList.size());
        assertEquals(expectList.get(0).getIaasType(), resultList.get(0).getIaasType());
        assertEquals(expectList.get(0).getCredentialConfigName(), resultList.get(0).getCredentialConfigName());
    }
    
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 디렉터 인증서 목록 정보 삽입 Unit Test
     * @title : testInsertCredentialConfigInfo
     * @return : void
    ***************************************************/
    public void testInsertCredentialConfigInfo(){
        HbBootstrapCredentialConfigDTO dto = setCredentialConfigInfo("insert");
        when(mockHbBootstrapCredentialConfigDAO.selectBootstrapCredentialConfigByName(anyString())).thenReturn(0);
        mockHbBootstrapCredentialConfigService.saveHbBootstrapCredentialConfigInfo(dto, principal);
    }

    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 디렉터 인증서 목록 정보 삽입 중 이미 등록 한 기본 정보 중복이 존재 할 경우 Unit Test
     * @title : testInsertCpiConfigInfoConflictCpiConfigName
     * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testInsertCpiConfigInfoConflictCpiConfigName(){
        HbBootstrapCredentialConfigDTO dto = setCredentialConfigInfo("insert");
        when(mockHbBootstrapCredentialConfigDAO.selectBootstrapCredentialConfigByName(anyString())).thenReturn(1);
        mockHbBootstrapCredentialConfigService.saveHbBootstrapCredentialConfigInfo(dto, principal);
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 디렉터 인증서 목록 정보 수정 Unit Test
     * @title : testInsertCpiConfigInfo
     * @return : void
    ***************************************************/
    public void testUpdateCpiConfigInfo(){
        HbBootstrapCredentialConfigDTO dto = setCredentialConfigInfo("update");
        when(mockHbBootstrapCredentialConfigDAO.selectBootstrapCredentialConfigByName(anyString())).thenReturn(0);
        when(mockHbBootstrapCredentialConfigDAO.selectBootstrapCredentialConfigInfo(anyInt(), anyString())).thenReturn(expectDefailtConfigInfo(""));
        mockHbBootstrapCredentialConfigService.saveHbBootstrapCredentialConfigInfo(dto, principal);
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 디렉터 인증서 목록 정보 수정 Config Name이 존재 할 경우 Unit Test
     * @title : testUpdateCpiConfigInfoConflictCpiConfigName
     * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testUpdateCpiConfigInfoConflictCpiConfigName(){
        HbBootstrapCredentialConfigDTO dto = setCredentialConfigInfo("update");
        when(mockHbBootstrapCredentialConfigDAO.selectBootstrapCredentialConfigByName(anyString())).thenReturn(1);
        when(mockHbBootstrapCredentialConfigDAO.selectBootstrapCredentialConfigInfo(anyInt(), anyString())).thenReturn(expectDefailtConfigInfo("conflict"));
        mockHbBootstrapCredentialConfigService.saveHbBootstrapCredentialConfigInfo(dto, principal);
    }
    
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 디렉터 인증서 정보 상세 조회 설정 값
     * @title : expectDefailtConfigInfo
     * @return : HbBootstrapCredentialConfigVO
    ***************************************************/
    private HbBootstrapCredentialConfigVO expectDefailtConfigInfo(String type) {
        HbBootstrapCredentialConfigVO vo = new HbBootstrapCredentialConfigVO();
        vo.setIaasType("Openstack");
        vo.setCreateUserId("admin");
        vo.setCredentialConfigName("bosh");
        if("conflict".equals(type)){
            vo.setCredentialConfigName("bosh2");
        }
        vo.setNetworkConfigName("network");
        vo.setCredentialKeyName("keyname");
        vo.setDirectorPrivateIp("10.0.0.6");
        vo.setDirectorPublicIp("13.123.552.123");
        
        vo.getDirectorPublicIp();
        vo.getDirectorPrivateIp();
        vo.getCredentialKeyName();
        vo.getCredentialConfigName();
        vo.getIaasType();
        vo.getId();
        vo.getNetworkConfigName();
        return vo;
    }

    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 디렉터 인증서 목록 정보 삭제 Unit Test
     * @title : testDeleteCpiConfigInfo
     * @return : void
    ***************************************************/
    @Test
    public void testDeleteCpiConfigInfo(){
        HbBootstrapCredentialConfigDTO dto = setCredentialConfigInfo("update");
        mockHbBootstrapCredentialConfigService.deleteHbBootstrapCredentialConfigInfo(dto, principal);
    }
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 디렉터 인증서 목록 정보 삭제 아이디 값이 없을 경우 Unit Test
     * @title : testDeleteCpiConfigInfoIdEmpty
     * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testDeleteCpiConfigInfoIdEmpty(){
        HbBootstrapCredentialConfigDTO dto = setCredentialConfigInfo("insert");
        mockHbBootstrapCredentialConfigService.deleteHbBootstrapCredentialConfigInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap 디렉터 인증서 정보 목록 결과 값 설정
     * @title : expectCredentialConfigList
     * @return : List<HbBootstrapCredentialConfigVO>
    *****************************************************************/
    private List<HbBootstrapCredentialConfigVO> expectCredentialConfigList() {
        List<HbBootstrapCredentialConfigVO> list = new ArrayList<HbBootstrapCredentialConfigVO>();
        HbBootstrapCredentialConfigVO vo = new HbBootstrapCredentialConfigVO();
        vo.setIaasType("AWS");
        vo.setId(1);
        vo.setNetworkConfigName("network");
        vo.setCredentialConfigName("credential");
        vo.setCredentialKeyName("keyname");
        vo.setDirectorPrivateIp("10.0.0.6");
        vo.setDirectorPublicIp("13.123.552.123");
        
        vo.getDirectorPublicIp();
        vo.getDirectorPrivateIp();
        vo.getCredentialKeyName();
        vo.getCredentialConfigName();
        vo.getIaasType();
        vo.getId();
        vo.getNetworkConfigName();
        list.add(vo);
        return list;
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybrid_Bootstrap 디렉터 인증서 정보 저장 결과 값 설정
     * @title : setCredentialConfigInfo
     * @return : HbBootstrapCredentialConfigDTO
    *****************************************************************/
    private HbBootstrapCredentialConfigDTO setCredentialConfigInfo(String type) {
        HbBootstrapCredentialConfigDTO dto = new HbBootstrapCredentialConfigDTO();
        dto.setIaasType("AWS");
        if("update".equals(type)){
            dto.setId("1");
        }
        dto.setNetworkConfigName("network");
        dto.setCredentialConfigName("credential");
        dto.setCredentialKeyName("keyname");
        dto.setDirectorPrivateIp("10.0.0.6");
        dto.setDirectorPublicIp("13.123.552.123");
        
        dto.getDirectorPublicIp();
        dto.getDirectorPrivateIp();
        dto.getCredentialKeyName();
        dto.getCredentialConfigName();
        dto.getIaasType();
        dto.getId();
        dto.getNetworkConfigName();
        return dto;
    }
    
}
