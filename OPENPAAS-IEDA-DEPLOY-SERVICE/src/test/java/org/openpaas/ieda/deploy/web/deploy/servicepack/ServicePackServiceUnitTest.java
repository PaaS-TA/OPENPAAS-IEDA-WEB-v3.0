package org.openpaas.ieda.deploy.web.deploy.servicepack;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.web.common.base.BaseDeployControllerUnitTest;
import org.openpaas.ieda.deploy.web.deploy.servicepack.dao.ServicePackDAO;
import org.openpaas.ieda.deploy.web.deploy.servicepack.dao.ServicePackVO;
import org.openpaas.ieda.deploy.web.deploy.servicepack.dto.ServicePackParamDTO;
import org.openpaas.ieda.deploy.web.deploy.servicepack.service.ServicePackService;
import org.openpaas.ieda.deploy.web.information.manifest.dao.ManifestDAO;
import org.openpaas.ieda.deploy.web.information.manifest.dao.ManifestVO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class ServicePackServiceUnitTest extends BaseDeployControllerUnitTest{

    @InjectMocks
    private ServicePackService mockServicePackService;
    @Mock
    private ServicePackDAO mockServicePackDAO;
    @Mock
    private ManifestDAO mockManifestDAO;
    @Mock
    private MessageSource mockMessageSource;
    
    private Principal principal = null;
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 유닛 테스트가 실행되기 전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        principal=getLoggined();
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 
     * @title : testGetServicePackList
     * @return : void
    ***************************************************/
    @Test
    public void testGetServicePackList() {
        String iaasType = "google";
        List<ServicePackVO> servicePacks =  setServicePackListInfo();
        when(mockServicePackDAO.selectServicePackInfo(iaasType)).thenReturn(servicePacks);
        mockServicePackService.getServicePackList(iaasType);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스팩 정보 삽입
     * @title : testSaveServicePackInfoFromInsert
     * @return : void
    ***************************************************/
    @Test
    public void testSaveServicePackInfoFromInsert() {
        ServicePackParamDTO input = setServicePackInputInfo("insert");
        ServicePackVO vo = setServicePackVOInfo();
        ManifestVO manifest = setManifestInfo();
        when(mockManifestDAO.selectManifestInfoByDeployName("mysql-service")).thenReturn(manifest);
        doNothing().when(mockServicePackDAO).insertServicePackInfo(vo);
        when(mockMessageSource.getMessage(anyString(), any(), any())).thenReturn("DEPLOY_STATUS_PROCESSING");
        doNothing().when(mockManifestDAO).updateManifestInfo(manifest);
        mockServicePackService.saveServicePackInfo(input,  principal);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스팩 정보 수정
     * @title : testSaveServicePackInfoFromUpdate
     * @return : void
    ***************************************************/
    @Test
    public void testSaveServicePackInfoFromUpdate() {
        ServicePackParamDTO input = setServicePackInputInfo("insert");
        ServicePackVO vo = setServicePackVOInfo();
        ManifestVO manifest = setManifestInfo();
        when(mockManifestDAO.selectManifestInfoByDeployName("mysql-service")).thenReturn(manifest);
        doNothing().when(mockServicePackDAO).insertServicePackInfo(vo);
        when(mockMessageSource.getMessage(anyString(), any(), any())).thenReturn("DEPLOY_STATUS_PROCESSING");
        doNothing().when(mockManifestDAO).updateManifestInfo(manifest);
        mockServicePackService.saveServicePackInfo(input,  principal);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스팩 manifest 파일 생성 - Null 오류
     * @title : testMakeDeploymentFileFromNullException
     * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testMakeDeploymentFileFromNullException() {
        ServicePackVO vo = setServicePackVOInfo();
        when(mockServicePackDAO.selectServicePackDetailInfo(1)).thenReturn(vo);
        ManifestVO manifest = setManifestInfo();
        when(mockManifestDAO.selectManifestInfoByDeployName("mysql-service")).thenReturn(manifest);
        doNothing().when(mockServicePackDAO).updateServicePackInfo(vo);
        when( mockMessageSource.getMessage("common.notFound.template.message", null, Locale.KOREA) ).thenReturn("요청한 파일을 찾을 수 없습니다.");
        mockServicePackService.makeDeploymentFile(1);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스팩 manifest 파일 생성 - BadRequest 오류
     * @title : testMakeDeploymentFileFromBadRequestException
     * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testMakeDeploymentFileFromBadRequestException() {
        when(mockServicePackDAO.selectServicePackDetailInfo(1)).thenReturn(null);
        mockServicePackService.makeDeploymentFile(1);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스팩 단순 레코드 삭제
     * @title : testDeleteServicePackInfoRecord
     * @return : void
    ***************************************************/
    @Test
    public void testDeleteServicePackInfoRecord() {
        ServicePackParamDTO input = setServicePackInputInfo("update");
        ServicePackVO vo = setServicePackVOInfo();
        when(mockServicePackDAO.selectServicePackDetailInfo(1)).thenReturn(vo);
        ManifestVO manifest = setManifestInfo();
        when(mockManifestDAO.selectManifestInfoByDeployName("mysql-service")).thenReturn(manifest);
        doNothing().when(mockServicePackDAO).deleteServicePackInfoRecord(1);
        doNothing().when(mockManifestDAO).updateManifestInfo(manifest);
        mockServicePackService.deleteServicePackInfoRecord(input);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스팩 단순 레코드 삭제 오류
     * @title : testDeleteServicePackInfoRecordException
     * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testDeleteServicePackInfoRecordFromNullException() {
        ServicePackParamDTO input = setServicePackInputInfo("update");
        when(mockServicePackDAO.selectServicePackDetailInfo(1)).thenReturn(null);
        mockServicePackService.deleteServicePackInfoRecord(input);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 매니페스트 정보 설정
     * @title : setManifestInfo
     * @return : ManifestVO
    ***************************************************/
    public ManifestVO setManifestInfo() {
        ManifestVO vo = new ManifestVO();
        vo.setCreateDate(new Date());
        vo.setCreateUserId(principal.getName());
        vo.setDeploymentName("mysql-service");
        vo.setDeployStatus(null);
        vo.setDescription("mysql-service-desc");
        vo.setFileName("mysql-servicepack-1.yml");
        vo.setIaas("google");
        vo.setId(1);
        vo.setRecid(0);
        vo.setUpdateDate(new Date());
        vo.setUpdateUserId(principal.getName());
        vo.getCreateDate();
        vo.getCreateUserId();
        vo.getDeploymentName();
        vo.getDeployStatus();
        vo.getDescription();
        vo.getFileName();
        vo.getIaas();
        vo.getId();
        vo.getRecid();
        vo.getUpdateDate();
        vo.getUpdateUserId();
        return vo;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스 팩 저장 정보 설정
     * @title : setServicePackVOInfo
     * @return : ServicePackVO
    ***************************************************/
    public ServicePackVO setServicePackVOInfo() {
        ServicePackVO vo = new ServicePackVO();
        vo.setCreateDate(new Date());
        vo.setCreateUserId(principal.getName());
        vo.setDeploymentFile("mysql-servicepack-1.yml");
        vo.setDeploymentName("mysql-service");
        vo.setDeployStatus(null);
        vo.setIaas("google");
        vo.setId(0);
        vo.setRecid(null);
        vo.setUpdateDate(new Date());
        vo.setUpdateUserId(principal.getName());
        return vo;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스 팩 입력 정보 설정
     * @title : setServicePackInfo
     * @return : ServicePackParamDTO
    ***************************************************/
    public ServicePackParamDTO setServicePackInputInfo(String type) {
        ServicePackParamDTO dto = new ServicePackParamDTO();
        dto.setDeploymentFile("mysql-servicepack-1.yml");
        dto.setDeploymentName("mysql-service");
        dto.setDeployStatus("DEPLOY_STATUS_DONE");
        dto.setIaas("google");
        dto.getDeploymentFile();
        dto.getDeploymentName();
        dto.getDeployStatus();
        dto.getIaas();
        dto.getId();
        if( type.equalsIgnoreCase("update") ) {
            dto.setId(1);
        }else {
            dto.setId(0);
        }
        return dto;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스 팩 목록 정보 설정
     * @title : setServicePackListInfo
     * @return : List<ServicePackVO>
    ***************************************************/
    public List<ServicePackVO> setServicePackListInfo(){
        List<ServicePackVO> list = new ArrayList<ServicePackVO>();
        ServicePackVO vo = new ServicePackVO();
        vo.setCreateDate(new Date());
        vo.setCreateUserId(principal.getName());
        vo.setDeploymentFile("mysql-servicepack-1.yml");
        vo.setDeploymentName("mysql-service");
        vo.setDeployStatus("DEPLOY_STATUS_PROCESSING");
        vo.setIaas("google");
        vo.setId(1);
        vo.setRecid("1");
        vo.setUpdateDate(new Date());
        vo.setUpdateUserId(principal.getName());
        vo.getCreateDate();
        vo.getCreateUserId();
        vo.getDeploymentFile();
        vo.getDeploymentName();
        vo.getDeployStatus();
        vo.getIaas();
        vo.getId();
        vo.getRecid();
        list.add(vo);
        return list;
    }

}
