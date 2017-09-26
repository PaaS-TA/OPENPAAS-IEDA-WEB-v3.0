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
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.Application;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.api.director.dto.DirectorInfoDTO;
import org.openpaas.ieda.deploy.web.common.base.BaseDeployControllerUnitTest;
import org.openpaas.ieda.deploy.web.config.setting.dao.DirectorConfigDAO;
import org.openpaas.ieda.deploy.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.deploy.web.config.setting.dto.DirectorConfigDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DirectorConfigServiceUnitTest extends BaseDeployControllerUnitTest{
    
    private Principal principal = null;
    final private static String HOMEDIR = System.getProperty("user.home"); //User's home directory
    final private static String FILESEPARATOR = System.getProperty("file.separator");//File separator ("/" on UNIX)
    final private static String BOSHCONFIGTESTFILEPATH = HOMEDIR+FILESEPARATOR+".bosh_config_test";
    final private static String BOSHCONFIGTESTFILE = ".bosh_config_test";
    final static Logger LOGGER = LoggerFactory.getLogger(DirectorConfigServiceUnitTest.class);
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
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> 기본 설치 관리자 정보가 존재 하지 않을 경우 테스트"); }
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
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> 기본 설치 관리자 정보 조회 테스트"); }
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
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> 설치 관리자 정보 목록 조회 테스트"); }
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
    * @description : 설치 관리자 설정 추가 중 이미 해당 설치 관리자가 존재 할 경우 테스트
    * @title : testExistCreateDirectorInfo
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testExistCreateDirectorInfo(){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> 설치 관리자 설정 추가 중 이미 해당 설치 관리자가 존재 할 경우 테스트"); }
        List<DirectorConfigVO> expectList = setListDirector();
        DirectorConfigDTO.Create dto = setDirectorConfigInfo();
        when(mockDirectorConfigDAO.selectDirectorConfigByDirectorUrl(anyString())).thenReturn(expectList);
        mockDirectorConfigService.existCheckCreateDirectorInfo(dto, principal, BOSHCONFIGTESTFILE);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 설치 관리자 정보 추가 중 디렉터 로그인 실패 테스트
    * @title : testCreateDirectorLoginFail
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testCreateDirectorLoginFail(){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> 설치 관리자 정보 추가 중 디렉터 로그인 실패 테스트"); }
        List<DirectorConfigVO> expectList = new ArrayList<DirectorConfigVO>();
        DirectorConfigDTO.Create dto = setDirectorConfigInfo();
        when(mockDirectorConfigDAO.selectDirectorConfigByDirectorUrl(anyString())).thenReturn(expectList);
        mockDirectorConfigService.existCheckCreateDirectorInfo(dto, principal, BOSHCONFIGTESTFILE);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 설치 관리자 설정 추가 테스트
     * @title : testInsertDirectorInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testInsertDirectorInfo(){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> 설치 관리자 설정 추가 테스트"); }
        DirectorConfigDTO.Create dto = setDirectorConfigInfo();
        DirectorInfoDTO apiDto = setDirectorInfoDTO();
        when(mockDirectorConfigDAO.selectDirectorConfigByDefaultYn(anyString())).thenReturn(null);
        mockDirectorConfigService.createDirectorInfo(dto, principal, apiDto, BOSHCONFIGTESTFILE);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 설치관리자 설정 조회 테스트
    * @title : testGetDirectorConfig
    * @return : void
    ***************************************************/
    @Test
    public void testGetDirectorConfig(){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> 설치관리자 설정 조회 테스트"); }
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
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> 설치 관리자 설정 조회 중 설치 관리자 정보가 존재 하지 않을 경우 테스트"); }
        when(mockDirectorConfigDAO.selectDirectorConfigBySeq(anyInt())).thenReturn(null);
        mockDirectorConfigService.getDirectorConfig(1);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 설치 관리자 수정 시 해당 설치 관리자 정보가 존재 하지 않을 경우 테스트
    * @title : UpdateDirectorinfoResultNull
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void UpdateDirectorinfoResultNull(){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> 설치 관리자 수정 시 해당 설치 관리자 정보가 존재 하지 않을 경우 테스트"); }
        when(mockDirectorConfigDAO.selectDirectorConfigBySeq(anyInt())).thenReturn(null);
        DirectorConfigDTO.Update dto = updateDirectorConfigInfo();
        mockDirectorConfigService.existCheckUpdateDirectorinfo(dto, principal, BOSHCONFIGTESTFILE);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 설치 관리자 수정 정보 디렉터 로그인 실패 테스트
    * @title : existCheckUpdateDirectorinfoLoginFail
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void existCheckUpdateDirectorinfoLoginFail(){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> 설치 관리자 수정 정보 디렉터 로그인 실패 테스트"); }
        DirectorConfigVO expectVo = setDirectorInfo();
        when(mockDirectorConfigDAO.selectDirectorConfigBySeq(anyInt())).thenReturn(expectVo);
        DirectorConfigDTO.Update dto = updateDirectorConfigInfo();
        mockDirectorConfigService.existCheckUpdateDirectorinfo(dto, principal, BOSHCONFIGTESTFILE);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 설치 관리자 정보 수정 테스트
    * @title : testUpdateDirectorinfo
    * @return : void
    ***************************************************/
    @Test
    public void testUpdateDirectorinfo(){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> 설치 관리자 정보 수정 테스트"); }
        DirectorConfigDTO.Update dto = updateDirectorConfigInfo();
        DirectorConfigVO vo = setDirectorInfo();
        mockDirectorConfigService.updateDirectorinfo(dto,vo, principal, BOSHCONFIGTESTFILE);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 설치 관리자 삭제 정보가 존재 하지 않을 경우
    * @title : testDeleteDirectorConfigResultNull
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testDeleteDirectorConfigResultNull(){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> 설치 관리자 삭제 정보가 존재 하지 않을 경우"); }
        when(mockDirectorConfigDAO.selectDirectorConfigBySeq(anyInt())).thenReturn(null);
        mockDirectorConfigService.deleteDirectorConfig(1, BOSHCONFIGTESTFILE);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Config 파일을 찾을 수 없을 경우 테스트
    * @title : deleteDirectorConfigFileNotFound
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void deleteDirectorConfigFileNotFound(){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> Config 파일을 찾을 수 없을 경우 테스트"); }
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
    @Test(expected=CommonException.class)
    public void deleteDirectorConfigNullPoint() throws Exception{
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> bosh_config 형식이 잘못 되었을 경우 테스트"); }
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
    @Test
    public void testDeleteDirectorConfig() throws Exception{
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> 설치 관리자 정보 삭제 테스트"); }
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
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> 기본 설치 관리자 설정 중 해당 설치 관리자 정보가 존재 하지 않을 경우 테스트"); }
        when(mockDirectorConfigDAO.selectDirectorConfigBySeq(anyInt())).thenReturn(null);
        mockDirectorConfigService.existCheckSetDefaultDirectorInfo(1, principal, BOSHCONFIGTESTFILE);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 기본 설치 관리자 설정 중 디렉터 로그인에 실패 했을 경우 테스트
    * @title : testExistCheckSetDefaultDirectorInfoLoginFail
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testExistCheckSetDefaultDirectorInfoLoginFail(){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> 기본 설치 관리자 설정 중 디렉터 로그인에 실패 했을 경우 테스트"); }
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
    @Test
    public void testSetDefaultDirectorInfo(){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> 기본 설치 관리자 설정 테스트"); }
        DirectorConfigVO expectVo = setDirectorInfo();
        DirectorInfoDTO apiDto =  setDirectorInfoDTO();
        when(mockDirectorConfigDAO.selectDirectorConfigByDefaultYn(anyString())).thenReturn(expectVo);
        mockDirectorConfigService.setDefaultDirectorInfo(expectVo, apiDto, principal, BOSHCONFIGTESTFILE);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 설치 관리자 설정 파일이 존재 할 경우 테스트
    * @title : testFileExistSetBoshConfigFile
    * @return : void
    ***************************************************/
    @Test
    public void testFileExistSetBoshConfigFile(){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> 설치 관리자 설정 파일이 존재 할 경우 테스트"); }
        testInsertDirectorInfo();
        DirectorConfigVO vo = setDirectorInfo();
        mockDirectorConfigService.setBoshConfigFile(vo, BOSHCONFIGTESTFILE);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 설치 관리자 설정 파일 정보가 잘못 되었을 경우 테스트
    * @title : testSetBoshConfigFileNullPoint
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testSetBoshConfigFileNullPoint() throws Exception{
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> 설치 관리자 설정 파일 정보가 잘못 되었을 경우 테스트"); }
        OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(BOSHCONFIGTESTFILEPATH),"UTF-8");
        fileWriter.write("1");
        fileWriter.close();
        DirectorConfigVO vo = setDirectorInfo();
        mockDirectorConfigService.setBoshConfigFile(vo, BOSHCONFIGTESTFILE);
    }
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 설치 관리자 설정 파일이 존재 하지 않을 경우 테스트
    * @title : testFileNotExistSetBoshConfigFile
    * @return : void
    ***************************************************/
    @Test 
    public void testFileNotExistSetBoshConfigFile(){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> 설치 관리자 설정 파일이 존재 하지 않을 경우 테스트"); }
        DirectorConfigVO vo = setDirectorInfo();
        mockDirectorConfigService.setBoshConfigFile(vo, BOSHCONFIGTESTFILE);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 설치 관리자 설정 파일과 기본 설치 관리자가 존재 하지 않을 경우 테스트
    * @title : testFileNotExistSetBoshConfigFile
    * @return : void
    ***************************************************/
    @Test(expected=NullPointerException.class)
    public void testFileNotExistSetBoshConfigFileDefault(){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> 설치 관리자 설정 파일과 기본 설치 관리자가 존재 하지 않을 경우 테스트"); }
        DirectorConfigVO vo = setDirectorInfo2();
        mockDirectorConfigService.setBoshConfigFile(vo, BOSHCONFIGTESTFILE);
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
    * @description : 설치 관리자 수정 정보 설정
    * @title : updateDirectorConfigInfo
    * @return : DirectorConfigDTO.Update
    ***************************************************/
    private DirectorConfigDTO.Update updateDirectorConfigInfo() {
        DirectorConfigDTO.Update dto = new DirectorConfigDTO.Update();
        Date date = new Date();
        dto.setIedaDirectorConfigSeq(1);
        dto.setUserId("admin");
        dto.setUpdateDate(new Date(date.getTime()));
        dto.setUserPassword("12345");
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
