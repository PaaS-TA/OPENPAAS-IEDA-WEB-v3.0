package org.openpaas.ieda.deploy.web.config.credential.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.web.common.base.BaseDeployControllerUnitTest;
import org.openpaas.ieda.deploy.web.config.credential.dao.CredentialManagementDAO;
import org.openpaas.ieda.deploy.web.config.credential.dao.CredentialManagementVO;
import org.openpaas.ieda.deploy.web.config.credential.dto.CredentialManagementDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class CredentialManagementServiceUnitTest extends BaseDeployControllerUnitTest{
    
    @InjectMocks CredentialManagementService mockCredentialManagementService;
    @Mock CredentialManagementDAO mockCredentialManagementDAO;
    @Mock MessageSource mockMessageSource;
    private Principal principal = null;
    
    final private static String SEPARATOR = System.getProperty("file.separator");
    final private static String CREDENTIAL_DIR = LocalDirectoryConfiguration.getGenerateCredentialDir();
    
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
    
    @Test
    public void test(){
    	String boshRelease = "bosh-266.10.0.tgz";
        String releaseVersion = boshRelease.replaceAll("[^0-9]", "");
        String releaseName = boshRelease.replaceAll("[^A-Za-z]", "");
        
        System.out.println(releaseVersion);
        System.out.println(releaseName);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 디렉터 인증서 목록 조회 UNIT TEST
     * @title : testGetDirectorCredentialList
     * @return : void
    *****************************************************************/
    @Test
    public void testGetDirectorCredentialList(){
        List<CredentialManagementVO> expectList = setDirectorCredentialList();
        when(mockCredentialManagementDAO.selectDirectorCredentialList()).thenReturn(expectList);
        List<CredentialManagementVO> resultList = mockCredentialManagementService.getDirectorCredentialList();
        assertEquals(resultList.size(), 1);
        assertEquals(resultList.get(0).getCreateUserId(), expectList.get(0).getCreateUserId());
        assertEquals(resultList.get(0).getCredentialKeyName(), expectList.get(0).getCredentialKeyName());
        assertEquals(resultList.get(0).getCredentialName(), expectList.get(0).getCredentialName());
        assertEquals(resultList.get(0).getUpdateUserId(), expectList.get(0).getUpdateUserId());
        assertEquals(resultList.get(0).getDirectorPublicIp(), expectList.get(0).getDirectorPublicIp());
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 디렉터 인증서 파일 명과 디렉터 공인 아이피를 통한 Key File 생성 UNIT TEST
     * @title : testMakeCredentialFile
     * @return : void
    *****************************************************************/
    public void testMakeCredentialFile(){
        CredentialManagementDTO dto = setDirectornfo();
        mockCredentialManagementService.makeCredentialFile(dto, dto.getCredentialName());
        File file = new File(CREDENTIAL_DIR + SEPARATOR + dto.getCredentialKeyName());
        if(file.exists()) file.delete(); // TEST 종료 후 해당 파일 삭제
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 디렉터 인증서 정보 저장 UNIT 테스트
     * @title : testSaveDirectorCredential
     * @return : void
    *****************************************************************/
    public void testSaveDirectorCredential(){
        CredentialManagementDTO dto = setDirectornfo();
        when(mockCredentialManagementDAO.selectdirectorCredentialInfoByName(dto.getCredentialName())).thenReturn(0);
        mockCredentialManagementService.saveDirectorCredential(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 디렉터 인증서 정보 저장 중 중복 데이터 검사 UNIT 테스트
     * @title : testSaveDirectorCredentialDuplicateInfo
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testSaveDirectorCredentialDuplicateInfo(){
        CredentialManagementDTO dto = setDirectornfo();
        when(mockCredentialManagementDAO.selectdirectorCredentialInfoByName(dto.getCredentialName())).thenReturn(1);
        mockCredentialManagementService.saveDirectorCredential(dto, principal);
    }
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 디렉터 인증서 정보 삭제 UNIT TEST
     * @title : testDeleteDirectorCredentialInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testDeleteDirectorCredentialInfo() throws IOException{
        CredentialManagementDTO dto = setDirectornfo();
        File file = new File(CREDENTIAL_DIR + SEPARATOR + dto.getCredentialKeyName());
        @SuppressWarnings("resource")
        FileWriter write = new FileWriter(file);
        write.write("1");
        write.flush();
        mockCredentialManagementService.deleteDirectorCredentialInfo(dto);
        file.delete();
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 디렉터 인증서 정보 삭제 중 ID 값이 없을 경우 UNIT TEST
     * @title : testDeleteDirectorCredentialInfoIdEmpty
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testDeleteDirectorCredentialInfoIdEmpty(){
        CredentialManagementDTO dto = setDirectornfo();
        dto.setId("");
        mockCredentialManagementService.deleteDirectorCredentialInfo(dto);;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 디렉터 인증서 정보 저장 및 삭제 데이터 설정 값
     * @title : testSaveDirectorCredential
     * @return : void
    *****************************************************************/
    private CredentialManagementDTO setDirectornfo(){
       CredentialManagementDTO dto = new CredentialManagementDTO();
        dto.setCreateUserId("admin");
        dto.setUpdateUserId("admin");
        dto.setDirectorPublicIp("172.16.100.1");
        dto.setCredentialKeyName("my-credential1-cred.yml");
        dto.setCredentialName("my-credential1");
        dto.setId("1");
        dto.getCreateUserId();
        dto.getUpdateUserId();
        dto.getDirectorPublicIp();
        dto.getCredentialKeyName();
        dto.getCredentialName();
        dto.getId();
        return dto;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 디렉터 인증서 목록 정보 조회 데이터 결과 값 설정
     * @title : testSaveDirectorCredential
     * @return : void
    *****************************************************************/
    private List<CredentialManagementVO> setDirectorCredentialList() {
        List<CredentialManagementVO> list = new ArrayList<CredentialManagementVO>();
        CredentialManagementVO vo = new CredentialManagementVO();
        vo.setCreateUserId("admin");
        vo.setUpdateUserId("admin");
        vo.setDirectorPublicIp("172.16.100.1");
        vo.setCredentialKeyName("my-credential1-cred.yml");
        vo.setCredentialName("my-credential1");
        vo.setRecid(1);
        vo.setId(1);
        vo.getCreateUserId();
        vo.getUpdateUserId();
        vo.getDirectorPublicIp();
        vo.getCredentialKeyName();
        vo.getCredentialName();
        vo.getRecid();
        vo.getId();
        list.add(vo);
        return list;
    }
}
