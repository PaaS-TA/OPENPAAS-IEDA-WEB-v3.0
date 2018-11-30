package org.openpaas.ieda.hbdeploy.web.config.setting.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.hbdeploy.web.common.base.BaseHbDeployControllerUnitTest;
import org.openpaas.ieda.hbdeploy.web.config.setting.dao.HbDirectorConfigDAO;
import org.openpaas.ieda.hbdeploy.web.config.setting.dao.HbDirectorConfigVO;
import org.openpaas.ieda.hbdeploy.web.config.setting.dto.HbDirectorConfigDTO;

public class HbDirectorConfigServiceUnitTest extends BaseHbDeployControllerUnitTest{
    
    private Principal principal = null;
    final private static String BOSHCONFIGTESTFILE = ".bosh_config_test";
    
    @InjectMocks HbDirectorConfigService mockHbDirectorConfigService;
    
    @Mock HbDirectorConfigDAO mockHbDirectorConfigDAO;
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        principal = getLoggined();
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 디렉터 목록 조회 테스트
    * @title : testGetDirectorList
    * @return : void
    ***************************************************/
    @Test
    public void testGetDirectorList(){
        List<HbDirectorConfigVO> expectList = setListDirector();
        when(mockHbDirectorConfigDAO.selectHbDirectorConfig(anyString())).thenReturn(expectList);
        List<HbDirectorConfigVO> resultList = mockHbDirectorConfigService.getDirectorList("public");
        for(int i=0; i<resultList.size();i++){
            assertEquals(expectList.get(i).getDirectorCpi(), resultList.get(i).getDirectorCpi());
            assertEquals(expectList.get(i).getDirectorUuid(), resultList.get(i).getDirectorUuid());
            assertEquals(expectList.get(i).getDirectorPort(), resultList.get(i).getDirectorPort());
            assertEquals(expectList.get(i).getDirectorName(), resultList.get(i).getDirectorName());
            assertEquals(expectList.get(i).getUserPassword(), resultList.get(i).getUserPassword());
        }
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 디렉터 생성 중 디렉터 값 중복 Exception 테스트
    * @title : testConfilctCreateDirectorInfo
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testConfilctCreateDirectorInfo(){
        List<HbDirectorConfigVO> expectList = setListDirector();
        HbDirectorConfigDTO dto = setDirectorConfigInfo();
        when(mockHbDirectorConfigDAO.selectHbDirectorConfigByDirectorUrl(any())).thenReturn(expectList);
        mockHbDirectorConfigService.existCheckCreateDirectorInfo(dto, principal, BOSHCONFIGTESTFILE);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 정상 디렉터 삭제 테스트
    * @title : testDeleteDirectorConfig
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteDirectorConfig(){
        HbDirectorConfigVO vo = setDirectorInfo();
        when(mockHbDirectorConfigDAO.selectHbDirectorConfigBySeq(anyInt())).thenReturn(vo);
        mockHbDirectorConfigService.deleteDirectorConfig(0, BOSHCONFIGTESTFILE);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 정상 디렉터 삭제 NullPoint 테스트
    * @title : testDeleteDirectorConfig
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testDeleteDirectorConfigNullPoint(){
        when(mockHbDirectorConfigDAO.selectHbDirectorConfigBySeq(anyInt())).thenReturn(null);
        mockHbDirectorConfigService.deleteDirectorConfig(0, BOSHCONFIGTESTFILE);
    }
    
    
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 디렉터 설정 정보 조회 리턴 값 설정
    * @title : setListDirector
    * @return : HbDirectorConfigVO
    ***************************************************/
    private HbDirectorConfigVO setDirectorInfo(){
        HbDirectorConfigVO vo = new HbDirectorConfigVO();
        vo.setIedaDirectorConfigSeq(1);
        vo.setRecid(1);
        vo.setUserId("admin");
        vo.setDirectorCpi("openstack-cpi");
        vo.setDirectorName("my-bosh");
        vo.setDirectorPort(25555);
        vo.setDirectorUuid("klasdha213sdd-sdfsd");
        vo.setDirectorUrl("123125-asdasb31123");
        vo.setCreateUserId("admin");
        vo.setUpdateUserId("admin");
        vo.setUserPassword("admidddddddddn");
        vo.setCredentialFile("openstack-microbosh-1-creds.yml");
        vo.setConnect(true);
        return vo;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 디렉터 목록 조회 리턴 값 설정
    * @title : setListDirector
    * @return : List<HbDirectorConfigVO> 
    ***************************************************/
    private List<HbDirectorConfigVO> setListDirector(){
        List<HbDirectorConfigVO> list = new ArrayList<HbDirectorConfigVO>();
        HbDirectorConfigVO vo = new HbDirectorConfigVO();
        vo.setIedaDirectorConfigSeq(1);
        vo.setRecid(1);
        vo.setUserId("admin");
        vo.setDirectorCpi("openstack-cpi");
        vo.setDirectorName("my-bosh");
        vo.setDirectorPort(25555);
        vo.setDirectorUuid("klasdha213sdd-sdfsd");
        vo.setDirectorUrl("123125-asdasb31123");
        vo.setCreateUserId("admin");
        vo.setUpdateUserId("admin");
        vo.setUserPassword("admidddddddddn");
        vo.setCredentialFile("openstack-microbosh-1-creds.yml");
        vo.setConnect(true);
        list.add(vo);
        return list;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 디렉터 정보 설정
    * @title : setDirectorConfigInfoList
    * @return : HbDirectorConfigDTO
    ***************************************************/
    private HbDirectorConfigDTO setDirectorConfigInfo() {
        HbDirectorConfigDTO dto = new HbDirectorConfigDTO();
        dto.setDirectorPort(25555);
        dto.setUserId("admin");
        dto.setUserPassword("admin");
        dto.setDirectorUrl("123125-asdasb31123");
        return dto;
    }
    
}
