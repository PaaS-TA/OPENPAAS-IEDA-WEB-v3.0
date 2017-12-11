package org.openpaas.ieda.deploy.web.common;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.common.BaseControllerUnitTest;
import org.openpaas.ieda.controller.deploy.web.common.CommonDeployController;
import org.openpaas.ieda.deploy.api.deployment.DeploymentInfoDTO;
import org.openpaas.ieda.deploy.api.release.ReleaseInfoDTO;
import org.openpaas.ieda.deploy.web.common.dao.ManifestTemplateVO;
import org.openpaas.ieda.deploy.web.common.dto.KeyInfoDTO;
import org.openpaas.ieda.deploy.web.common.service.CommonDeployService;
import org.openpaas.ieda.deploy.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.deploy.web.config.setting.service.DirectorConfigService;
import org.openpaas.ieda.deploy.web.config.stemcell.dao.StemcellManagementVO;
import org.openpaas.ieda.deploy.web.config.stemcell.service.StemcellManagementService;
import org.openpaas.ieda.deploy.web.config.systemRelease.service.ReleaseManagementService;
import org.openpaas.ieda.deploy.web.information.deploy.service.DeploymentService;
import org.openpaas.ieda.deploy.web.information.iassConfig.dao.IaasConfigMgntVO;
import org.openpaas.ieda.deploy.web.information.iassConfig.service.IaasConfigMgntService;
import org.openpaas.ieda.deploy.web.information.release.service.ReleaseService;
import org.openpaas.ieda.deploy.web.information.stemcell.service.StemcellService;
import org.openpaas.ieda.deploy.web.management.code.dao.CommonCodeVO;
import org.openpaas.ieda.deploy.web.management.code.service.CommonCodeService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class CommonDeployControllerUnitTest extends BaseControllerUnitTest {

    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    private Principal principal = null;
    
    @InjectMocks private CommonDeployController mockCommonDeployController;
    @Mock private CommonDeployService mockCommonDeployService;
    @Mock private ReleaseManagementService mockReleaseManagementService;
    @Mock private ReleaseService mockReleaseService;
    @Mock private StemcellService mockStemcellService;
    @Mock private StemcellManagementService mockStemcellManagementService;
    @Mock private CommonCodeService mockCommonCodeService;
    @Mock private DirectorConfigService mockDirectorConfigService;
    @Mock private DeploymentService mockDeploymentService;
    @Mock private IaasConfigMgntService mockIaasConfigMgntService;
    
    //URL 정보
    /**********************************************************************************************************/
    final static String DEFAULT_DIRECTOR_LIST_URL = "/common/use/director";
    final static String DEPLOYMENT_LIST_URL = "/common/use/deployments";
    final static String DEPLOYMENT_LIST_BY_PLATFORM_LIST_URL = "/common/deploy/deployments/{platform}/{iaas}";
    final static String PRIVATE_KEY_PATH_FILE_LIST_URL = "/common/deploy/key/list/{iaasType}";
    final static String BOSH_AWS_DEPLOY_INFO_URL = "/common/use/deployment/{deploymentFile:.+}";
    final static String LOCAL_RELEASE_LIST_URL = "/common/deploy/systemRelease/list/{type}/{iaas}";
    final static String LOCAL_FILE_RELEASE_LIST_URL = "/common/deploy/release/list/{type}";
    final static String STEMCELL_LIST_URL = "/common/deploy/stemcell/list/{type}/{iaas}";
    final static String SUB_CODE_INFO_URL = "/common/deploy/codes/parent/{parentCode}";
    final static String COMPLEX_CODE_INFO_URL = "/common/deploy/codes/parent/{parentCode}/subcode/{subGroupCode}";
    final static String SET_LOCAL_FILE_URL = "/common/deploy/lockFile/{FileName:.*}";
    final static String COUNTRY_CODE_LIST_URL = "/common/deploy/codes/countryCode/{parentCode}";
    final static String CREATE_KEY_INFO_URL = "/common/deploy/key/createKey";
    final static String RELEASE_INFO_BY_PLATFORM_URL = "/common/deploy/list/releaseInfo/{deployType}/{iaas}";
    final static String IAAS_CONFIG_LIST_URL = "/common/deploy/list/iaasConfig/{iaasType}";
    final static String IAAS_CONFIG_INFO_URL = "/common/deploy/list/iaasConfig/{iaasType}/{id}";
    /**********************************************************************************************************/
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockCommonDeployController).build();
        principal = getLoggined();
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 기본 설치 관리자 정보 조회
     * @title : testGetDefaultDirector
     * @return : void
    ***************************************************/
    @Test
    public void testGetDefaultDirector() throws Exception{
        when(mockDirectorConfigService.getDefaultDirector()).thenReturn(new DirectorConfigVO());
        mockMvc.perform(get(DEFAULT_DIRECTOR_LIST_URL).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 기본 설치관리자에 배포명 조회 요청
     * @title : testGetDeploymentList
     * @return : void
    ***************************************************/
    @Test
    public void testGetDeploymentList() throws Exception{
        List<DeploymentInfoDTO> deployments = setDeploymentListInfo();
        when(mockDeploymentService.listDeployment()).thenReturn(deployments);
        mockMvc.perform(get(DEPLOYMENT_LIST_URL).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 
     * @title : setDeploymentListInfo
     * @return : List<DeploymentInfoDTO>
    ***************************************************/
    public List<DeploymentInfoDTO> setDeploymentListInfo(){
        List<DeploymentInfoDTO> list = new ArrayList<DeploymentInfoDTO>();
        DeploymentInfoDTO dto = new DeploymentInfoDTO();
        dto.setName("cf");
        dto.setRecid(1);
        dto.setReleaseInfo("cf-273");
        dto.setStemcellInfo("stemcell-google-light-3445.7.tgz");
        list.add(dto);
        return list;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 플랫폼 별 배포 목록 조회
     * @title : testGetDeploymentListByPlatform
     * @return : void
    ***************************************************/
    @Test
    public void testGetDeploymentListByPlatform() throws Exception{
        String platform = "cf";
        String iaasType = "openstack";
        when(mockCommonDeployService.listDeployment(platform, iaasType)).thenReturn(new ArrayList<String>());
        mockMvc.perform(get(DEPLOYMENT_LIST_BY_PLATFORM_LIST_URL, platform, iaasType).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Private Key 파일업로드
     * @title : testDoBootstrapKeyPathFileUpload
     * @return : void
    ***************************************************/
    @Test
    public void testDoBootstrapKeyPathFileUpload() throws Exception{
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        doNothing().when(mockCommonDeployService).uploadKeyFile(request);
        mockCommonDeployController.doBootstrapKeyPathFileUpload(request);
    }
    
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Private Key 파일 정보 목록  조회(AWS/Openstack/google)
     * @title : testGetKeyPathFileList
     * @return : void
    ***************************************************/
    @Test
    public void testGetKeyPathFileList() throws Exception{
        String iaasType = "google";
        when(mockCommonDeployService.getKeyFileList(iaasType)).thenReturn(new ArrayList<String>());
        mockMvc.perform(get(PRIVATE_KEY_PATH_FILE_LIST_URL, iaasType).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 배포파일 정보 조회
     * @title : testGetBoshAwsDeployInfo
     * @return : void
    ***************************************************/
    @Test
    public void testGetBoshAwsDeployInfo() throws Exception{
        String deploymentFile = "google-cf-1.yml";
        when(mockCommonDeployService.getDeploymentInfo(deploymentFile)).thenReturn("contents");
        mockMvc.perform(get(BOSH_AWS_DEPLOY_INFO_URL, deploymentFile).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 배포파일 브라우저 다운로드
     * @title : testDownloadDeploymentFile
     * @return : void
    ***************************************************/
    @Test
    public void testDownloadDeploymentFile() throws Exception{
        String deploymentFile = "google-cf-1.yml";
        MockHttpServletResponse response = new MockHttpServletResponse();
        doNothing().when(mockCommonDeployService).downloadDeploymentFile(deploymentFile, response);
        mockCommonDeployController.downloadDeploymentFile(deploymentFile, response);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 공통 시스템 릴리즈 콤보
     * @title : testGetlocalReleaseList
     * @return : void
    ***************************************************/
    @Test
    public void testGetlocalReleaseList() throws Exception{
        String type = "BOSH_CPI";
        String iaasType = "google";
        when(mockReleaseManagementService.getLocalReleaseList(type, iaasType)).thenReturn(new ArrayList<String>());
        mockMvc.perform(get(LOCAL_RELEASE_LIST_URL, type, iaasType).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 공통 릴리즈 콤보(cf/diego/garden/etcd)
     * @title : testGetLocalFilterReleaseList
     * @return : void
    ***************************************************/
    @Test
    public void testGetLocalFilterReleaseList() throws Exception{
        String type = "CF";
        when(mockReleaseService.getFilteredReleseList(type)).thenReturn(new ArrayList<ReleaseInfoDTO>());
        mockMvc.perform(get(LOCAL_FILE_RELEASE_LIST_URL, type).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 공통 릴리즈 콤보(cf/diego/garden/etcd) 정보 설정
     * @title : setLocalFilterReleaseListInfo
     * @return : List<ReleaseInfoDTO>
    ***************************************************/
    public List<ReleaseInfoDTO> setLocalFilterReleaseListInfo(){
        List<ReleaseInfoDTO> list = new ArrayList<ReleaseInfoDTO>();
        ReleaseInfoDTO dto = new ReleaseInfoDTO();
        dto.setCurrentDeployed("y");
        dto.setJobNames("consul");
        dto.setName("cf");
        dto.setRecid(1);
        dto.setVersion("1");
        list.add(dto);
        return list;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 공통 스템셀 콤보(Bootstrap)
     * @title : testGetListStemcellFromBootstrap
     * @return : void
    ***************************************************/
    @Test
    public void testGetListStemcellFromBootstrap() throws Exception{
        String type = "BOOTSTRAP";
        String iaasType = "AWS";
        List<StemcellManagementVO> stemcells =  setStemellListFromCFInfo();
        when(mockStemcellManagementService.getLocalStemcellList(iaasType.toLowerCase())).thenReturn(stemcells);
        mockMvc.perform(get(STEMCELL_LIST_URL, type, iaasType).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 공통 스템셀 콤보(Bootstrap) 정보 설정
     * @title : setStemellListFromBootstrapInfo
     * @return : List<StemcellManagementVO>
    ***************************************************/
    public List<StemcellManagementVO> setStemellListFromBootstrapInfo(){
        List<StemcellManagementVO> list = new ArrayList<StemcellManagementVO>();
        StemcellManagementVO vo = new StemcellManagementVO();
        vo.setCreateDate(new Date());
        vo.setCreateUserId(principal.getName());
        vo.setUpdateUserId(principal.getName());
        vo.setUpdateDate(new Date());
        vo.setRecid(1);
        vo.setId(1);
        vo.setStemcellUrl("https://bosh-jenkins-artifacts.s3.amazonaws.com/bosh-stemcell/aws/light-bosh-stemcell-2820-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
        vo.setStemcellName("aws-stemcell/3445.7");
        vo.setStemcellFileName("light-bosh-stemcell-3445.7-aws-xen-hvm-ubuntu-trusty-go_agent.tgz");
        vo.setStemcellVersion("3445.7");
        vo.setOsVersion("TRUSTY");
        vo.setOs("UUBNTU");
        vo.setDownloadStatus("");
        vo.setIsDose("");
        vo.setIsExisted("");
        vo.setIaas("aws");
        vo.setSize("412878694");
        list.add(vo);
        return list;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 공통 스템셀 콤보(CF)
     * @title : testGetListStemcellFromCF
     * @return : void
    ***************************************************/
    @Test
    public void testGetListStemcellFromCF() throws Exception{
        String type = "CF";
        String iaasType = "GOOGLE";
        List<StemcellManagementVO> stemcells =  setStemellListFromCFInfo();
        when(mockStemcellService.getStemcellList()).thenReturn(stemcells);
        mockMvc.perform(get(STEMCELL_LIST_URL, type, iaasType).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 공통 스템셀 콤보(CF) 정보 설정
     * @title : setStemellListFromCFInfo
     * @return : List<StemcellManagementVO>
    ***************************************************/
    public List<StemcellManagementVO> setStemellListFromCFInfo(){
        List<StemcellManagementVO> list = new ArrayList<StemcellManagementVO>();
        StemcellManagementVO vo = new StemcellManagementVO();
        vo.setCreateDate(new Date());
        vo.setCreateUserId(principal.getName());
        vo.setUpdateUserId(principal.getName());
        vo.setUpdateDate(new Date());
        vo.setRecid(1);
        vo.setId(1);
        vo.setStemcellUrl("https://s3.amazonaws.com/bosh-gce-light-stemcells/light-bosh-stemcell-3445.7-google-kvm-ubuntu-trusty-go_agent.tgz");
        vo.setStemcellName("google-stemcell/3445.7");
        vo.setStemcellFileName("bosh-stemcell-3445.7-google-kvm-ubuntu-trusty-go_agent.tgz");
        vo.setStemcellVersion("3445.7");
        vo.setOsVersion("TRUSTY");
        vo.setOs("UUBNTU");
        vo.setDownloadStatus("");
        vo.setIsDose("");
        vo.setIsExisted("");
        vo.setIaas("google");
        vo.setSize("412878694");
        list.add(vo);
        return list;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 공통 코드 조회 (하위 코드 목록)
     * @title : testGetSubCode
     * @return : void
    ***************************************************/
    @Test
    public void testGetSubCode() throws Exception{
        String parentCode = "100000";
        List<CommonCodeVO> codes = setSubCodeInfo();
        when(mockCommonCodeService.getSubGroupCodeList()).thenReturn(codes);
        mockMvc.perform(get(SUB_CODE_INFO_URL, parentCode).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 공통 하위 코드 정보 설정
     * @title : setSubCodeInfo
     * @return : List<CommonCodeVO>
    ***************************************************/
    public List<CommonCodeVO> setSubCodeInfo(){
        List<CommonCodeVO> list = new ArrayList<CommonCodeVO>();
        CommonCodeVO vo = new CommonCodeVO();
        vo.setCodeDescription("메뉴 별 권한");
        vo.setCodeIdx(1);
        vo.setCodeName("ROLE");
        vo.setCodeNameKR("메뉴 별 권한");
        vo.setCodeValue("100000");
        vo.setCreateDate(new Date());
        vo.setUpdateDate(new Date());
        vo.setCreateUserId("SYSTEM");
        vo.setUpdateUserId("SYSTEM");
        vo.setSortOrder(0);
        vo.setRecid(1);
        vo.setParentCode(null);
        vo.setSubGroupCode(null);
        vo.setUsubGroupCode(null);
        list.add(vo);
        return list;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 공통 코드 조회 (하위 그룹의 코드 목록)
     * @title : testGetComplexCode
     * @return : void
    ***************************************************/
    @Test
    public void testGetComplexCode() throws Exception{
        String parentCode = "100000";
        String subGroupCode = "110000";
        when(mockCommonCodeService.getCodeListByParentAndSubGroup(parentCode, subGroupCode)).thenReturn(new ArrayList<CommonCodeVO>());
        mockMvc.perform(get(COMPLEX_CODE_INFO_URL, parentCode, subGroupCode).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : lock 파일 생성
     * @title : testSetLockFile
     * @return : void
    ***************************************************/
    @Test
    public void testSetLockFile() throws Exception{
        String fileName = "aws-stemcell-1111.tgz";
        when(mockCommonDeployService.lockFileSet(fileName)).thenReturn(true);
        mockMvc.perform(get(SET_LOCAL_FILE_URL, fileName).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 국가 코드 조회(KR 우선 정렬 조건)
     * @title : testGetCountryCodeList
     * @return : void
    ***************************************************/
    @Test
    public void testGetCountryCodeList() throws Exception{
        String parentCode = "20000";
        List<CommonCodeVO> codes = setCountryCodeList();
        when(mockCommonCodeService.getCountryCodeList(parentCode)).thenReturn(codes);
        mockMvc.perform(get(COUNTRY_CODE_LIST_URL, parentCode).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 국가코드 목록 정보 설정
     * @title : setCountryCodeList
     * @return : List<CommonCodeVO>
    ***************************************************/
    public List<CommonCodeVO> setCountryCodeList(){
        List<CommonCodeVO> list = new ArrayList<CommonCodeVO>();
        CommonCodeVO vo = new CommonCodeVO();
        vo.setCodeDescription("desc");
        vo.setCodeIdx(1);
        vo.setCodeName("COUNTRY_CODE");
        vo.setCodeNameKR("국가코드");
        vo.setCodeValue("20000");
        vo.setCreateDate(new Date());
        vo.setUpdateDate(new Date());
        vo.setCreateUserId("SYSTEM");
        vo.setUpdateUserId("SYSTEM");
        vo.setSortOrder(0);
        vo.setRecid(1);
        vo.setParentCode(null);
        vo.setSubGroupCode(null);
        vo.setUsubGroupCode(null);
        list.add(vo);
        return list;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Key 생성
     * @title : testCreateKeyInfo
     * @return : void
    ***************************************************/
    @Test
    public void testCreateKeyInfo() throws Exception{
        KeyInfoDTO dto = setCreateKeyInfo();
        when(mockCommonDeployService.createKeyInfo(dto, principal)).thenReturn("cf-key-google-1.yml");
        when(mockCommonDeployService.getFingerprint("cf-key-google-1.yml")).thenReturn("fingerprint");
        mockMvc.perform(post(CREATE_KEY_INFO_URL).content(mapper.writeValueAsBytes(dto)).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 플랫폼 별 릴리즈 설치 지원 버전 목록 조회
     * @title : testGetReleaseInfoByPlatform
     * @return : void
    ***************************************************/
    @Test
    public void testGetReleaseInfoByPlatform() throws Exception{
        String deployType = "CF";
        String iaasType = "openstack";
        List<ManifestTemplateVO> templates = setReleaseInfoByPlatform();
        when(mockCommonDeployService.getReleaseInfoByPlatform(deployType, iaasType)).thenReturn(templates);
        mockMvc.perform(get(RELEASE_INFO_BY_PLATFORM_URL,deployType, iaasType).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 배포 유형에 따른 릴리즈 정보 설정
     * @title : setReleaseInfoByPlatform
     * @return : List<ManifestTemplateVO>
    ***************************************************/
    public List<ManifestTemplateVO> setReleaseInfoByPlatform(){
        List<ManifestTemplateVO> list = new ArrayList<ManifestTemplateVO>();
        ManifestTemplateVO vo = new ManifestTemplateVO();
        vo.setCfTempleate("cf-google-1.yml");
        vo.setCommonBaseTemplate("generic_manifest_mask.yml");
        vo.setCommonJobTemplate("cf.yml");
        vo.setCommonOptionTemplate(null);
        vo.setCreateDate(new Date());
        vo.setCreateUserId(principal.getName());
        vo.setDeployType("cf");
        vo.setIaasPropertyTemplate("cf_google_settings.yml");
        vo.setIaasType("google");
        vo.setId(1);
        vo.setInputTemplate("cf_google_inputs.yml");
        vo.setMetaTemplate("cf_google_stub_273.yml");
        vo.setMinReleaseVersion("273");
        vo.setOptionEtc("cf_diego_options.yml");
        vo.setOptionNetworkTemplate("cf_google_network_options.yml");
        vo.setOptionResourceTemplate("cf_google_resouce_options.yml");
        vo.setReleaseType("cf");
        vo.setShellScript(null);
        vo.setTemplateVersion("273");
        vo.setUpdateDate(new Date());
        vo.setUpdateUserId(principal.getName());
        vo.getCfTempleate();
        vo.getCommonBaseTemplate();
        vo.getCommonJobTemplate();
        vo.getCommonOptionTemplate();
        vo.getCreateDate();
        vo.getCreateUserId();
        vo.getDeployType();
        vo.getIaasPropertyTemplate();
        vo.getIaasType();
        vo.getId();
        vo.getInputTemplate();
        vo.getMetaTemplate();
        vo.getMinReleaseVersion();
        vo.getOptionEtc();
        vo.getOptionNetworkTemplate();
        vo.getOptionResourceTemplate();
        vo.getReleaseType();
        vo.getShellScript();
        vo.getTemplateVersion();
        vo.getUpdateDate();
        vo.getUpdateUserId();
        list.add(vo);
        return list;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인프라 환경 설정 목록 정보 조회 
     * @title : testGetIaasConfigList
     * @return : void
    ***************************************************/
    @Test
    public void testGetIaasConfigList() throws Exception{
        String iaasType = "openstack";
        List<IaasConfigMgntVO> list = setIaasConfigListInfo();
        when(mockIaasConfigMgntService.getIaasConfigInfoList(iaasType, principal)).thenReturn(list);
        mockMvc.perform(get(IAAS_CONFIG_LIST_URL,iaasType).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인프라 환경 설정 목록 정보 설정
     * @title : setIaasConfigListInfo
     * @return : List<IaasConfigMgntVO>
    ***************************************************/
    public List<IaasConfigMgntVO> setIaasConfigListInfo(){
        List<IaasConfigMgntVO> list = new ArrayList<IaasConfigMgntVO>();
        IaasConfigMgntVO vo = new IaasConfigMgntVO();
        vo.setAccountId(1);
        vo.setAccountName("bosh");
        vo.setCommonAvailabilityZone(null);
        vo.setCommonKeypairName("bosh");
        vo.setCommonKeypairPath("bosh.pem");
        vo.setCommonSecurityGroup("bosh-security");
        vo.setCreateUserId(principal.getName());
        vo.setUpdateUserId(principal.getName());
        vo.setDeployStatus("사용중");
        vo.setIaasConfigAlias("openstack-config");
        vo.setRecid(1);
        vo.setId(1);
        vo.setIaasType("openstack");
        list.add(vo);
        return list;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인프라 환경 설정 정보 상세 조회 
     * @title : testGetIaasConfigInfo
     * @return : void
    ***************************************************/
    @Test
    public void testGetIaasConfigInfo() throws Exception{
        String iaasType = "openstack";
        int id = 1;
        when(mockCommonDeployService.getIaasConfigInfo(iaasType, id, principal)).thenReturn(new HashMap<String, Object>());
        mockMvc.perform(get(IAAS_CONFIG_INFO_URL,iaasType, id).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Key 생성 정보 설정
     * @title : setCreateKeyInfo
     * @return : KeyInfoDTO
    ***************************************************/
    public KeyInfoDTO setCreateKeyInfo() {
        KeyInfoDTO dto = new KeyInfoDTO();
        dto.setCountryCode("KR");
        dto.setDomain("10.10.10.10.xip.io");
        dto.setEmail("admin@test.com");
        dto.setIaas("google");
        dto.setId("1");
        dto.setLocalityName("seoul");
        dto.setOrganizationName("cloud");
        dto.setPlatform("diego");
        dto.setUnitName("seoul");
        dto.setVersion("273");
        dto.getCountryCode();
        dto.getDomain();
        dto.getEmail();
        dto.getIaas();
        dto.getId();
        dto.getLocalityName();
        dto.getOrganizationName();
        dto.getPlatform();
        dto.getUnitName();
        dto.getVersion();
        return dto;
    }


}
