package org.openpaas.ieda.deploy.web.management.code.service;

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
import org.openpaas.ieda.deploy.web.management.code.dao.CommonCodeDAO;
import org.openpaas.ieda.deploy.web.management.code.dao.CommonCodeVO;
import org.openpaas.ieda.deploy.web.management.code.dto.CommonCodeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class CommonCodeServiceUnitTest {
    
    private Principal principal = null;
    final static Logger LOGGER = LoggerFactory.getLogger(CommonCodeServiceUnitTest.class);
    
    @InjectMocks CommonCodeService mockCommonCodeService;
    @Mock CommonCodeDAO mockCommonCodeDAO;
    @Mock MessageSource mockMessageSource;
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        principal = getLoggined();
        if(LOGGER.isInfoEnabled()){  LOGGER.info("loging" + principal); }
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 시큐리티 토큰 생성
    * @title : getLoggined
    * @return : Principal
    ***************************************************/
    public Principal getLoggined() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken("admin", "admin");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(auth);
        securityContext.getAuthentication().getPrincipal();
        return auth;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 서브그룹 정보 목록을 조회 TEST
    * @title : testGetSubGroupCodeList
    * @return : void
    ***************************************************/
    @Test
    public void testGetSubGroupCodeList(){
        if(LOGGER.isInfoEnabled()){  LOGGER.info("================= 서브그룹 정보 목록 조회 TEST START  ================="); }
        List<CommonCodeVO> expectList = setResultCodeInfo("default");
        when(mockCommonCodeDAO.selectParentCodeAndSubGroupCode(anyString(), anyString(), anyString())).thenReturn(expectList);
        List<CommonCodeVO> resultList = mockCommonCodeService.getSubGroupCodeList("codeTest", "codeTest", "codeTest");
        assertEquals(expectList.get(0).getCodeDescription(), resultList.get(0).getCodeDescription());
        assertEquals(expectList.get(0).getCodeIdx(), resultList.get(0).getCodeIdx());
        assertEquals(expectList.get(0).getCodeName(), resultList.get(0).getCodeName());
        assertEquals(expectList.get(0).getCodeNameKR(), resultList.get(0).getCodeNameKR());
        assertEquals(expectList.get(0).getCodeValue(), resultList.get(0).getCodeValue());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 서브그룹 정보 목록 조회시 목록 정보가 존재 하지 않을 경우 TEST
    * @title : testGetSubGroupCodeListError
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testGetSubGroupCodeListError(){
        if(LOGGER.isInfoEnabled()){  LOGGER.info("================= 서브그룹 정보 목록 조회시 목록 정보가 존재 하지 않을 경우 TEST START  ================="); }
        List<CommonCodeVO> expectList = new ArrayList<CommonCodeVO>();
        when(mockCommonCodeDAO.selectParentCodeAndSubGroupCode(anyString(), anyString(), anyString())).thenReturn(expectList);
        mockCommonCodeService.getSubGroupCodeList("codeTest", "codeTest", "3");
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 코드 그룹 등록 중 이미 코드 정보가 존재 할 경우 TEST
    * @title : testCreateCodeConfilctError
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testCreateCodeConfilctError(){
        if(LOGGER.isInfoEnabled()){  LOGGER.info("================= 코드 그룹 등록 중 이미 코드 정보가 존재 할 경우 TEST START  ================="); }
        when(mockCommonCodeDAO.selectCodeValueCheck(any())).thenReturn(1);
        CommonCodeDTO.Regist dto = setCodeSaveInfo("default");
        mockCommonCodeService.createCode(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 코드 그룹 등록 TEST
    * @title : testCreateCode
    * @return : void
    ***************************************************/
    @Test
    public void testCreateCode(){
        if(LOGGER.isInfoEnabled()){  LOGGER.info("================= 코드 그룹 등록 TEST START  ================="); }
        when(mockCommonCodeDAO.selectCodeValueCheck(any())).thenReturn(0);
        CommonCodeDTO.Regist dto = setCodeSaveInfo("default");
        mockCommonCodeService.createCode(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 코드 등록 TEST
    * @title : testCreateSubCode
    * @return : void
    ***************************************************/
    @Test
    public void testCreateSubCode(){
        if(LOGGER.isInfoEnabled()){  LOGGER.info("================= 코드 등록 TEST START  ================="); }
        List<CommonCodeVO> expectList = new ArrayList<CommonCodeVO>();
        when(mockCommonCodeDAO.selectCodeName(anyString())).thenReturn(expectList);
        when(mockCommonCodeDAO.selectCodeValueCheck(any())).thenReturn(0);
        when(mockCommonCodeDAO.selectMaxSortOrder(anyString(), anyString())).thenReturn(0);
        CommonCodeDTO.Regist dto = setCodeSaveInfo("default");
        mockCommonCodeService.createSubCode(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 코드 등록 중 ParentCode 값이 Null일 경우  TEST
    * @title : testCreateSubCodeParentCodeNull
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testCreateSubCodeParentCodeNull(){
        if(LOGGER.isInfoEnabled()){  LOGGER.info("================= 코드 등록 TEST START  ================="); }
        List<CommonCodeVO> expectList = setResultCodeInfo("null");
        when(mockCommonCodeDAO.selectCodeName(anyString())).thenReturn(expectList);
        when(mockCommonCodeDAO.selectCodeValueCheck(any())).thenReturn(0);
        when(mockCommonCodeDAO.selectMaxSortOrder(anyString(), anyString())).thenReturn(0);
        CommonCodeDTO.Regist dto = setCodeSaveInfo("null");
        mockCommonCodeService.createSubCode(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 코드 등록 중 코드 명이 충돌 날 경우 TEST
    * @title : testCreateSubCodeConfilct
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testCreateSubCodeConfilct(){
        if(LOGGER.isInfoEnabled()){  LOGGER.info("================= 코드 등록 중 코드 명이 충돌 날 경우 TEST START  ================="); }
        List<CommonCodeVO> expectList = setResultCodeInfo("default");
        when(mockCommonCodeDAO.selectCodeName(anyString())).thenReturn(expectList);
        when(mockCommonCodeDAO.selectCodeValueCheck(any())).thenReturn(1);
        when(mockCommonCodeDAO.selectMaxSortOrder(anyString(), anyString())).thenReturn(0);
        CommonCodeDTO.Regist dto = setCodeSaveInfo("default");
        mockCommonCodeService.createSubCode(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 코드 수정 중 수정 데이터가 존재 하지 않을 경우 TEST
    * @title : testUpdateCodeValueNull
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testUpdateCodeValueNull(){
        if(LOGGER.isInfoEnabled()){  LOGGER.info("================= 코드 수정 중 수정 데이터가 존재 하지 않을 경우 TEST START  ================="); }
        when(mockCommonCodeDAO.selectCodeIdx(anyInt())).thenReturn(null);
        CommonCodeDTO.Regist dto = setCodeSaveInfo("default");
        mockCommonCodeService.updateCode(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 코드 수정 TEST
    * @title : testUpdateCode
    * @return : void
    ***************************************************/
    @Test
    public void testUpdateCode(){
        if(LOGGER.isInfoEnabled()){  LOGGER.info("================= 코드 수정 TEST START  ================="); }
        CommonCodeVO expectVO = setresultCodeVo();
        when(mockCommonCodeDAO.selectCodeIdx(anyInt())).thenReturn(expectVO);
        CommonCodeDTO.Regist dto = setCodeSaveInfo("default");
        mockCommonCodeService.updateCode(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 코드 삭제 중 삭제 데이터가 존재 하지 않을 경우 TEST
    * @title : testDeleteCodeValueNull
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testDeleteCodeValueNull(){
        if(LOGGER.isInfoEnabled()){  LOGGER.info("================= 코드 삭제 중 삭제 데이터가 존재 하지 않을 경우 TEST START  ================="); }
        when(mockCommonCodeDAO.selectCodeIdx(anyInt())).thenReturn(null);
        mockCommonCodeService.deleteCode(1);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 코드 삭제 TEST
    * @title : testDeleteCode
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteCode(){
        if(LOGGER.isInfoEnabled()){  LOGGER.info("================= 코드 삭제 중 삭제 데이터가 존재 하지 않을 경우 TEST START  ================="); }
        CommonCodeVO expectVO = setresultCodeVo();
        when(mockCommonCodeDAO.selectCodeIdx(anyInt())).thenReturn(expectVO);
        mockCommonCodeService.deleteCode(1);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 상세 권한 목록이 존재 하지 않을 경우 TEST
    * @title : testGetCommonCodeListListValueEmpty
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testGetCommonCodeListListValueEmpty(){
        if(LOGGER.isInfoEnabled()){  LOGGER.info("================= 상세 권한 목록이 존재 하지 않을 경우 TEST START  ================="); }
        List<CommonCodeVO> expectList = new ArrayList<CommonCodeVO>();
        when(mockCommonCodeDAO.selectCommonCodeList(anyString())).thenReturn(expectList);
        mockCommonCodeService.getCommonCodeList();
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 상세 권한 목록이 존재 하지 않을 경우 TEST
    * @title : testGetCommonCodeListList
    * @return : void
    ***************************************************/
    @Test
    public void testGetCommonCodeListList(){
        if(LOGGER.isInfoEnabled()){  LOGGER.info("================= 상세 권한 목록 조회 TEST START  ================="); }
        List<CommonCodeVO> expectList = setResultCodeInfo("default");
        when(mockCommonCodeDAO.selectCommonCodeList(anyString())).thenReturn(expectList);
        mockCommonCodeService.getCommonCodeList();
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 국가 코드 조회(KR 기준 정렬) TEST
    * @title : testGetCountryCodeList
    * @return : void
    ***************************************************/
    @Test
    public void testGetCountryCodeList(){
        if(LOGGER.isInfoEnabled()){  LOGGER.info("================= 국가 코드 조회(KR 기준 정렬) TEST START  ================="); }
        List<CommonCodeVO> expectList = setResultCodeInfo("default");
        when(mockCommonCodeDAO.selectCountryCodeList(anyString())).thenReturn(expectList);
        mockCommonCodeService.getCountryCodeList("1");
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 국가 코드 조회가 존재 하지 않을 경우  TEST
    * @title : testGetCountryCodeListValueNull
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testGetCountryCodeListValueNull(){
        if(LOGGER.isInfoEnabled()){  LOGGER.info("================= 국가 코드 조회가 존재 하지 않을 경우  TEST START  ================="); }
        List<CommonCodeVO> expectList = new ArrayList<CommonCodeVO>();
        when(mockCommonCodeDAO.selectCountryCodeList(anyString())).thenReturn(expectList);
        mockCommonCodeService.getCountryCodeList("1");
        
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 코드 수정 정보 조회 결과 값 설정
    * @title : setresultCodeVo
    * @return : CommonCodeVO
    ***************************************************/
    public CommonCodeVO setresultCodeVo() {
        CommonCodeVO vo = new CommonCodeVO();
        vo.setCodeDescription("testCode");
        vo.setCodeIdx(1);
        vo.setCodeName("testCode");
        vo.setCodeNameKR("코드테스트");
        vo.setCodeValue("testCode");
        vo.setCreateUserId("admin");
        vo.setParentCode("100000");
        vo.setRecid(1);
        vo.setSortOrder(1);
        vo.setSubGroupCode("testCode");
        vo.setUpdateUserId("admin");
        return vo;
    }

    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 코드 등록 정보 설정
    * @title : setCodeSaveInfo
    * @return : void
    ***************************************************/
    public CommonCodeDTO.Regist setCodeSaveInfo(String type) {
        CommonCodeDTO.Regist dto = new CommonCodeDTO.Regist();
        dto.setCodeDescription("testCode");
        dto.setCodeIdx(1);
        dto.setCodeName("testCode");
        dto.setCodeNameKR("코드테스트");
        dto.setCodeValue("testCode");
        dto.setRecid(1);
        dto.setSortOrder(1);
        dto.getSortOrder();
        dto.setCreateUserId("admin");
        dto.getCreateUserId();
        dto.setUpdateUserId("admin");
        dto.getUpdateUserId();
        dto.setSubGroupCode("testCode");
        if(!"null".equals(type)){
            dto.setParentCode("test");
            dto.getParentCode();
        }
        dto.getRecid();
        dto.getCodeValue();
        dto.getCodeName();
        dto.getCodeNameKR();
        dto.getCodeIdx();
        dto.getCodeDescription();
        return dto;
    }

    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 코드 관련 Result 값 설정
    * @title : testGoCodeManagement
    * @return : void
    ***************************************************/
    public List<CommonCodeVO> setResultCodeInfo(String type) {
        List<CommonCodeVO> list = new ArrayList<CommonCodeVO>();
        CommonCodeVO vo = new CommonCodeVO();
        vo.setCodeDescription("testCode");
        vo.getCodeDescription();
        vo.setCodeIdx(1);
        vo.getCodeIdx();
        vo.setCodeName("testCode");
        vo.getCodeName();
        vo.setCodeNameKR("코드테스트");
        vo.getCodeNameKR();
        vo.setCodeValue("testCode");
        vo.getCodeValue();
        vo.setCreateUserId("admin");
        vo.getCreateUserId();
        if(!type.equals("null")){
            vo.setParentCode("100000");
            vo.getParentCode();
        }
        vo.setRecid(1);
        vo.getRecid();
        vo.setSortOrder(1);
        vo.getSortOrder();
        vo.setSubGroupCode("testCode");
        vo.getSubGroupCode();
        vo.setUpdateUserId("admin");
        vo.getUpdateUserId();
        list.add(vo);
        return list;
    }
    
    
}
