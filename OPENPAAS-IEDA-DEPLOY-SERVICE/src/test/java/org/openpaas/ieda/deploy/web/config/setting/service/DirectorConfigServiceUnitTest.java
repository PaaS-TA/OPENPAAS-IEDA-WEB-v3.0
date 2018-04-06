package org.openpaas.ieda.deploy.web.config.setting.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.api.director.dto.DirectorInfoDTO;
import org.openpaas.ieda.deploy.web.common.base.BaseDeployControllerUnitTest;
import org.openpaas.ieda.deploy.web.config.setting.dao.DirectorConfigDAO;
import org.openpaas.ieda.deploy.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.deploy.web.config.setting.dto.DirectorConfigDTO;

public class DirectorConfigServiceUnitTest extends BaseDeployControllerUnitTest{
    
    private Principal principal = null;
    final private static String HOMEDIR = System.getProperty("user.home"); //User's home directory
    final private static String FILESEPARATOR = System.getProperty("file.separator");//File separator ("/" on UNIX)
    final private static String BOSHCONFIGTESTFILEPATH = HOMEDIR+FILESEPARATOR+".bosh_config_test";
    final private static String BOSHCONFIGTESTFILE = ".bosh_config_test";
    @InjectMocks DirectorConfigService mockDirectorConfigService;
    @Mock DirectorConfigDAO mockDirectorConfigDAO;

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
    * @description : 기본 설치 관리자 정보가 존재 하지 않을 경우 테스트
    * @title : testGetDefaultDirector
    * @return : void
    ***************************************************/
    @Test
    public void testGetDefaultDirectorResultNull(){
        when(mockDirectorConfigDAO.selectDirectorConfigByDefaultYn(anyString())).thenReturn(null);
        DirectorConfigVO resultVo = mockDirectorConfigService.getDefaultDirector();
        assertEquals(resultVo, null);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 기본 설치 관리자 정보 조회 테스트
    * @title : testGetDefaultDirector
    * @return : void
    ***************************************************/
    @Test
    public void testGetDefaultDirector(){
        DirectorConfigVO expectVo = setDirectorInfo();
        when(mockDirectorConfigDAO.selectDirectorConfigByDefaultYn(anyString())).thenReturn(expectVo);
        DirectorConfigVO resultVo = mockDirectorConfigService.getDefaultDirector();
        assertEquals(expectVo.getUserId(), resultVo.getUserId());
        assertEquals(expectVo.getDirectorCpi(), resultVo.getDirectorCpi());
        assertEquals(expectVo.getDirectorName(), resultVo.getDirectorName());
        assertEquals(expectVo.getDirectorUrl(), resultVo.getDirectorUrl());
        assertEquals(expectVo.getUserPassword(), resultVo.getUserPassword());
        assertEquals(expectVo.getDefaultYn(), resultVo.getDefaultYn());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 설치 관리자 정보 목록 조회 테스트
    * @title : testListDirector
    * @return : void
    ***************************************************/
    @Test
    public void testListDirector(){
        List<DirectorConfigVO> expectList = setListDirector();
        when(mockDirectorConfigDAO.selectDirectorConfig()).thenReturn(expectList);
        List<DirectorConfigVO> result = mockDirectorConfigService.getDirectorList();
        for(int i=0; i<result.size();i++){
            assertEquals(expectList.get(i).getDefaultYn(), result.get(i).getDefaultYn());
            assertEquals(expectList.get(i).getDirectorCpi(), result.get(i).getDirectorCpi());
            assertEquals(expectList.get(i).getDirectorUuid(), result.get(i).getDirectorUuid());
            assertEquals(expectList.get(i).getDirectorPort(), result.get(i).getDirectorPort());
            assertEquals(expectList.get(i).getDirectorName(), result.get(i).getDirectorName());
            assertEquals(expectList.get(i).getUserPassword(), result.get(i).getUserPassword());
        }
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 설치 관리자 추가 중 정보 조회 실패 테스트
    * @title : testCreateDirectorLoginFail
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testCreateDirectorSearchFail(){
        List<DirectorConfigVO> expectList = new ArrayList<DirectorConfigVO>();
        DirectorConfigDTO.Create dto = setDirectorConfigInfo();
        when(mockDirectorConfigDAO.selectDirectorConfigByDirectorUrl(anyString())).thenReturn(expectList);
        mockDirectorConfigService.existCheckCreateDirectorInfo(dto, principal, BOSHCONFIGTESTFILE);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  디렉터 로그인 테스트
     * @title : testBoshboshEnvLoginSequence
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testBoshEnvLoginSequence(){
        DirectorConfigVO dvo = setDirectorInfo();
        mockDirectorConfigService.boshEnvAliasSequence(dvo);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  디렉터 로그인 판별 테스트
     * @title : testisExistBoshEnvLogin
     * @return : void
    *****************************************************************/
    @Test
    public void testisExistBoshEnvLogin(){
        DirectorConfigVO dvo = setDirectorInfo();
        mockDirectorConfigService.isExistBoshEnvLogin(dvo.getDirectorUrl(),
                                                           dvo.getDirectorPort(),
                                                           dvo.getUserId(), 
                                                           dvo.getUserPassword());
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  디렉터 로그인 중 Credential File 이 없을 경우
     * @title : testBoshEnvLoginSequenceCredenstialFileIsNull
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testBoshEnvLoginSequenceCredenstialFileIsNull(){
        DirectorConfigVO dvo = setDirectorInfo();
        mockDirectorConfigService.boshEnvAliasSequence(dvo);
    }
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 설치 관리자 추가 테스트
     * @title : testInsertDirectorInfo
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testInsertDirectorInfo(){
        DirectorConfigDTO.Create dto = setDirectorConfigInfo();
        DirectorInfoDTO apiDto = setDirectorInfoDTO();
        DirectorConfigVO dvo = setDirectorInfo();
        when(mockDirectorConfigDAO.selectDirectorConfigBySeq(anyInt())).thenReturn(dvo);
        mockDirectorConfigService.insertDirectorInfo(dto, principal, apiDto, BOSHCONFIGTESTFILE);
    }

    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 설치관리자 설정 조회 테스트
    * @title : testGetDirectorConfig
    * @return : void
    ***************************************************/
    @Test
    public void testGetDirectorConfig(){
        DirectorConfigVO expectVo = setDirectorInfo();
        when(mockDirectorConfigDAO.selectDirectorConfigBySeq(anyInt())).thenReturn(expectVo);
        mockDirectorConfigService.getDirectorConfig(1);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 설치 관리자 설정 조회 중 설치 관리자 정보가 존재 하지 않을 경우 테스트
    * @title : testGetDirectorConfigResultNull
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testGetDirectorConfigResultNull(){
        when(mockDirectorConfigDAO.selectDirectorConfigBySeq(anyInt())).thenReturn(null);
        mockDirectorConfigService.getDirectorConfig(1);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 설치 관리자 삭제 정보가 존재 하지 않을 경우
    * @title : testDeleteDirectorConfigResultNull
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testDeleteDirectorConfigResultNull(){
        when(mockDirectorConfigDAO.selectDirectorConfigBySeq(anyInt())).thenReturn(null);
        mockDirectorConfigService.deleteDirectorConfig(1, BOSHCONFIGTESTFILE);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Config 파일을 찾을 수 없을 경우 테스트
    * @title : deleteDirectorConfigFileNotFound
    * @return : void
    ***************************************************/
    @Test
    public void deleteDirectorConfigFileNotFound(){
        DirectorConfigVO expectVo = setDirectorInfo();
        when(mockDirectorConfigDAO.selectDirectorConfigBySeq(anyInt())).thenReturn(expectVo);
        mockDirectorConfigService.deleteDirectorConfig(1, BOSHCONFIGTESTFILE);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : bosh_config 형식이 잘못 되었을 경우 테스트
    * @title : deleteDirectorConfigNullPoint
    * @return : void
    ***************************************************/
    @Test
    public void deleteDirectorConfigNullPoint() throws Exception{
        OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(BOSHCONFIGTESTFILEPATH),"UTF-8");
        fileWriter.write("1");
        fileWriter.close();
        DirectorConfigVO expectVo = setDirectorInfo();
        when(mockDirectorConfigDAO.selectDirectorConfigBySeq(anyInt())).thenReturn(expectVo);
        mockDirectorConfigService.deleteDirectorConfig(1, BOSHCONFIGTESTFILE);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 설치 관리자 정보 삭제 테스트
    * @title : testDeleteDirectorConfig
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testDeleteDirectorConfig(){
        testInsertDirectorInfo();
        DirectorConfigVO expectVo = setDirectorInfo();
        when(mockDirectorConfigDAO.selectDirectorConfigBySeq(anyInt())).thenReturn(expectVo);
        mockDirectorConfigService.deleteDirectorConfig(1, BOSHCONFIGTESTFILE);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 기본 설치 관리자 설정 중 해당 설치 관리자 정보가 존재 하지 않을 경우 테스트
    * @title : testExistCheckSetDefaultDirectorInfoValueNull
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testExistCheckSetDefaultDirectorInfoValueNull(){
        when(mockDirectorConfigDAO.selectDirectorConfigBySeq(anyInt())).thenReturn(null);
        mockDirectorConfigService.existCheckSetDefaultDirectorInfo(1, principal, BOSHCONFIGTESTFILE);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 기본 설치 관리자 설정 중 디렉터 로그인에 실패 했을 경우 테스트
    * @title : testExistCheckSetDefaultDirectorInfoLoginFail
    * @return : void
    ***************************************************/
    public void testExistCheckSetDefaultDirectorInfoLoginFail(){
        DirectorConfigVO expectVo = setDirectorInfo();
        when(mockDirectorConfigDAO.selectDirectorConfigBySeq(anyInt())).thenReturn(expectVo);
        mockDirectorConfigService.existCheckSetDefaultDirectorInfo(1, principal, BOSHCONFIGTESTFILE);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 기본 설치 관리자 설정 테스트
    * @title : testSetDefaultDirectorInfo
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testSetDefaultDirectorInfo(){
        DirectorConfigVO expectVo = setDirectorInfo();
        DirectorInfoDTO apiDto =  setDirectorInfoDTO();
        when(mockDirectorConfigDAO.selectDirectorConfigByDefaultYn(anyString())).thenReturn(expectVo);
        mockDirectorConfigService.setDefaultDirectorInfo(expectVo, apiDto, principal, BOSHCONFIGTESTFILE);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 설치 관리자 정보 설정
    * @title : setDirectorInfoDTO
    * @return : DirectorInfoDTO
    ***************************************************/
    private DirectorInfoDTO setDirectorInfoDTO(){
        DirectorInfoDTO dto = new DirectorInfoDTO();
        dto.setCpi("bosh-openstack");
        dto.setName("bosh");
        dto.setUser("admin");
        dto.setUuid("klasdha213sdd-sdfsd");
        dto.setVersion("33.123");
        return dto;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 설치 관리자 정보 설정
    * @title : setDirectorConfigInfoList
    * @return : DirectorConfigDTO.Create
    ***************************************************/
    private DirectorConfigDTO.Create setDirectorConfigInfo() {
        DirectorConfigDTO.Create dto = new DirectorConfigDTO.Create();
        dto.setDirectorPort(25555);
        dto.setUserId("admin");
        dto.setUserPassword("admin");
        dto.setDirectorUrl("123125-asdasb31123");
        return dto;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 설치 관리자 조회 리턴 값 설정
    * @title : setListDirector
    * @return : List<DirectorConfigVO>
    ***************************************************/
    private List<DirectorConfigVO> setListDirector(){
        List<DirectorConfigVO> list = new ArrayList<DirectorConfigVO>();
        DirectorConfigVO vo = new DirectorConfigVO();
        vo.setIedaDirectorConfigSeq(1);
        vo.setRecid(1);
        vo.setUserId("admin");
        vo.setDefaultYn("Y");
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
    * @description : 설치 관리자 조회 리턴 값 설정
    * @title : setListDirector
    * @return : List<DirectorConfigVO>
    ***************************************************/
    private DirectorConfigVO setDirectorInfo(){
        DirectorConfigVO vo = new DirectorConfigVO();
        vo.setIedaDirectorConfigSeq(1);
        vo.setRecid(1);
        vo.setUserId("admin");
        vo.setDefaultYn("Y");
        vo.setDirectorCpi("openstack-cpi");
        vo.setDirectorName("my-bosh");
        vo.setDirectorPort(25555);
        vo.setDirectorUuid("klasdha213sdd-sdfsd");
        vo.setDirectorUrl("123125-asdasb31123");
        vo.setCreateUserId("admin");
        vo.setUpdateUserId("admin");
        vo.setUserPassword("admin");
        vo.setCredentialFile("openstack-microbosh-1-creds.yml");
        vo.setConnect(true);
        return vo;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 설치 관리자 조회 리턴 값 설정
    * @title : setListDirector
    * @return : List<DirectorConfigVO>
    ***************************************************/
    private DirectorConfigVO setDirectorInfo2(){
        DirectorConfigVO vo = new DirectorConfigVO();
        vo.setIedaDirectorConfigSeq(1);
        vo.setRecid(1);
        vo.setUserId("admin");
        vo.setDefaultYn("N");
        vo.setDirectorCpi("openstack-cpi");
        vo.setDirectorName("my-bosh");
        vo.setDirectorPort(25555);
        vo.setDirectorUuid("klasdha213sdd-sdfsd");
        vo.setDirectorUrl("123125-asdasb31123");
        vo.setCreateUserId("admin");
        vo.setUpdateUserId("admin");
        vo.setUserPassword("admin");
        vo.setConnect(true);
        return vo;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 하나의 메소드가 동작한 직후 실행
    * @title : tearDown
    * @return : void
    ***************************************************/
    @After
    public void tearDown(){
        File file = new File(BOSHCONFIGTESTFILEPATH);
        if(file.exists()){
            file.delete();
        }
    }
    
    
}
