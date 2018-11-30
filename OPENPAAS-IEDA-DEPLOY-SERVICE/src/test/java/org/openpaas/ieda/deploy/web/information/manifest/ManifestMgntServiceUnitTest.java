package org.openpaas.ieda.deploy.web.information.manifest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileWriter;
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
import org.openpaas.ieda.deploy.web.common.base.BaseDeployControllerUnitTest;
import org.openpaas.ieda.deploy.web.config.setting.service.DirectorConfigService;
import org.openpaas.ieda.deploy.web.information.manifest.dao.ManifestDAO;
import org.openpaas.ieda.deploy.web.information.manifest.dao.ManifestVO;
import org.openpaas.ieda.deploy.web.information.manifest.dto.ManifestListDTO;
import org.openpaas.ieda.deploy.web.information.manifest.dto.ManifestParamDTO;
import org.openpaas.ieda.deploy.web.information.manifest.service.ManifestService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration

public class ManifestMgntServiceUnitTest extends BaseDeployControllerUnitTest{
    
    final private static String SEPARATOR = System.getProperty("file.separator");
    final private static String MANIFEST_REAL_PATH = LocalDirectoryConfiguration.getManifastDir() + SEPARATOR + "aws-microbosh-7.yml";
    final private static String MANIFEST_TMP_PATH = LocalDirectoryConfiguration.getTmpDir() + SEPARATOR + "aws-microbosh-7.yml";

    @InjectMocks 
    private ManifestService mockManifestService;
    @Mock 
    private DirectorConfigService mockDirectorConfigService;
    @Mock
    private ManifestDAO mockManifestDao;
    @Mock
    private MessageSource mockMessageSource;
    
    private Principal principal = null;
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception{
        MockitoAnnotations.initMocks(this);
        principal = getLoggined();
        File file = new File(MANIFEST_TMP_PATH);
        FileWriter writer = new FileWriter(file);
        writer.write("test"); 
        writer.close();
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  Manifest 목록 조회 테스트
     * @title : testGetManifestList
     * @return : void
    *****************************************************************/
    @Test
    public void testGetManifestList() {
        List<ManifestVO> expectList = setDefualtManifestVOList();
        when(mockManifestDao.selectManifestList()).thenReturn(expectList);
        List<ManifestVO> resultList = mockManifestService.getManifestList();
        assertEquals(expectList.get(0).getId(), resultList.get(0).getId());
        assertEquals(expectList.get(0).getRecid(), resultList.get(0).getRecid());
        assertEquals(expectList.get(0).getIaas(), resultList.get(0).getIaas());
        assertEquals(expectList.get(0).getFileName(), resultList.get(0).getFileName());
        assertEquals(expectList.get(0).getDescription(), resultList.get(0).getDescription());
        assertEquals(expectList.get(0).getDeploymentName(), resultList.get(0).getDeploymentName());
        assertEquals(expectList.get(0).getPath(), resultList.get(0).getPath());
        assertEquals(expectList.get(0).getDeployStatus(), resultList.get(0).getDeployStatus());
        assertEquals(expectList.get(0).getCreateUserId(), resultList.get(0).getCreateUserId());
        assertEquals(expectList.get(0).getUpdateUserId(), resultList.get(0).getUpdateUserId());
    }
    

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  파일 업로드 테스트
     * @title : testuploadManifestFile
     * @return : void
    *****************************************************************/
    //@Test
    public void testuploadManifestFile() {
        MultipartHttpServletRequest request = setDefaultManifestList();
        when(mockManifestDao.selectManifestInfoByDeployName(anyString())).thenReturn(null);
        mockManifestService.uploadManifestFile(request);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  Manifest 정보 저장 테스트
     * @title : testSaveManifestInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testSaveManifestInfo(){
        ManifestListDTO manifestDto = setDefaultManifestListDTO();
        mockManifestDao.insertManifestInfo(manifestDto);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  Manifest 업데이트 테스트
     * @title : testUpdateManifestContent
     * @return : void
    *****************************************************************/
    @Test
    public void testUpdateManifestContent(){
        ManifestVO manifestVO = setDefaultManifestVO();
        ManifestParamDTO manifestParamDTO = setDefaultManifestParamDTO("update");
        when(mockManifestDao.selectManifestInfo(Integer.parseInt(manifestParamDTO.getId()))).thenReturn(manifestVO);
        String updateDeploymentName = "renewName";
        when(mockManifestDao.selectManifestInfoByDeployNameANDId(updateDeploymentName, Integer.parseInt(manifestParamDTO.getId()))).thenReturn(null);
        mockManifestService.updateManifestContent(manifestParamDTO, principal);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  Manifest 삭제 테스트
     * @title : testDeleteManifest
     * @return : void
    *****************************************************************/
    @Test
    public void testDeleteManifest(){
        ManifestVO manifestVO = setDefaultManifestVO();
        when(mockManifestDao.selectManifestInfo(manifestVO.getId())).thenReturn(manifestVO);
        mockManifestService.deleteManifest(manifestVO.getId());
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  기본 Manifest List 정보 설정
     * @title : setDefualtManifestVO
     * @return : ManifestVO
    *****************************************************************/
    public List<ManifestVO> setDefualtManifestVOList(){
        List<ManifestVO> list = new ArrayList<ManifestVO>();
        ManifestVO vo = new ManifestVO();
        vo.setId(1);
        vo.setRecid(1);
        vo.setIaas("openstack");
        vo.setFileName("openstack_manifest");
        vo.setDescription("opstck_manifest_Description");
        vo.setDeploymentName("opstk_deployment_name");
        vo.setPath("/home/local/manifest");
        vo.setDeployStatus("Y");
        vo.setCreateUserId("admin");
        vo.setUpdateUserId("admin");
        list.add(vo);
        return list;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  기본 Manifest 정보 설정 
     * @title : setDefaultManifestVO
     * @return : ManifestVO
    *****************************************************************/
    public ManifestVO setDefaultManifestVO(){
        ManifestVO vo = new ManifestVO();
        vo.setId(1);
        vo.setRecid(1);
        vo.setIaas("openstack");
        vo.setFileName("openstack_manifest");
        vo.setDescription("opstk_manifest_description");
        vo.setDeploymentName("opstk_deployment_name");
        vo.setPath("/home/local/manifest");
        vo.setDeployStatus("Y");
        vo.setCreateUserId("admin");
        vo.setUpdateUserId("admin");
        return vo;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  Manifest 업로드 데이터 세팅
     * @title : setDefaultManifestListDTO
     * @return : ManifestListDTO
    *****************************************************************/
    @SuppressWarnings("null")
    public MultipartHttpServletRequest setDefaultManifestList(){
        MultipartHttpServletRequest request = null;
        ManifestListDTO dto = new ManifestListDTO();
        dto.setIaas("openstack");
        dto.setFileName("opstk_manifest_file");
        dto.setDescription("opstk_manifest_description");
        dto.setPath("/home/local/manifest");
        dto.setDeploymentName("opstk_deployment_name");
        request.setAttribute("iaas", dto.getIaas());
        request.setAttribute("description", dto.getDescription());
        return request;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  ManifestListDTO 데이터 세팅
     * @title : setDefaultManifestListDTO
     * @return : ManifestListDTO
    *****************************************************************/
    public ManifestListDTO setDefaultManifestListDTO(){
        ManifestListDTO dto = new ManifestListDTO();
        dto.setIaas("openstack");
        dto.setFileName("opstk_manifest_file");
        dto.setDescription("opstk_manifest_description");
        dto.setPath("/home/local/manifest");
        dto.setCreateUserId("admin");
        dto.setUpdateUserId("admin");
        return dto;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  param DTO 데이터 세팅
     * @title : setDefaultManifestParamDTO
     * @return : ManifestParamDTO
    *****************************************************************/
    public ManifestParamDTO setDefaultManifestParamDTO(String save){
        ManifestParamDTO dto = new ManifestParamDTO();
        if(save.equals("update")){
            dto.setId("1");
        }
        dto.setFileName("aws-microbosh-7.yml");
        dto.setIaas("aws");
        dto.setContent("name: aws-manifest");
        return dto;
    }
}
