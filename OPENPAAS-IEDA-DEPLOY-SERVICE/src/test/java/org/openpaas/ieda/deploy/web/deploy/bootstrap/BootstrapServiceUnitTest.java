package org.openpaas.ieda.deploy.web.deploy.bootstrap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Application;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.web.common.base.BaseDeployControllerUnitTest;
import org.openpaas.ieda.deploy.web.common.dao.CommonDeployDAO;
import org.openpaas.ieda.deploy.web.common.dao.ManifestTemplateVO;
import org.openpaas.ieda.deploy.web.config.setting.dao.DirectorConfigDAO;
import org.openpaas.ieda.deploy.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.dao.BootstrapDAO;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.dao.BootstrapVO;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.dto.BootStrapDeployDTO;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.dto.BootstrapListDTO;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.service.BootstrapService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class BootstrapServiceUnitTest extends BaseDeployControllerUnitTest{
    
    @InjectMocks
    private BootstrapService mockBootstrapService;
    @Mock
    private BootstrapDAO mockBootstrapDAO;
    @Mock
    private CommonDeployDAO mockCommonDeployDAO;
    @Mock
    private DirectorConfigDAO mockDirectorConfigDAO;
    @Mock
    private MessageSource mockMessageSource;
    
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 유닛 테스트가 실행되기 전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        getLoggined();
    }
    
    @Test
    public void testGetBootstrapList(){
        List<BootstrapVO> list = setBootstrapList("openstack");
        when(mockBootstrapDAO.selectBootstrapList()).thenReturn(list);
        List<BootstrapListDTO> result = mockBootstrapService.getBootstrapList();
        for( int i=0; i<result.size(); i++ ){
            assertEquals(list.size(), result.size());
            assertEquals(list.get(i).getId(), result.get(i).getId());
            assertEquals(list.get(i).getIaasType(), result.get(i).getIaas());
            assertEquals(list.get(i).getDeploymentName(), result.get(i).getDeploymentName());
            assertEquals(list.get(i).getDirectorName(), result.get(i).getDirectorName());
            assertEquals(list.get(i).getBoshRelease(), result.get(i).getBoshRelease());
            assertEquals(list.get(i).getBoshCpiRelease(), result.get(i).getBoshCpiRelease());
            assertEquals(list.get(i).getNtp(), result.get(i).getNtp());
            assertEquals(list.get(i).getDeployLog(), result.get(i).getDeployLog());
            assertEquals(list.get(i).getDeployStatus(), result.get(i).getDeployStatus());
            assertEquals(list.get(i).getDeploymentFile(), result.get(i).getDeploymentFile());
            assertEquals(list.get(i).getIaasConfig().getIaasConfigAlias(), result.get(i).getIaasConfigAlias());
            assertEquals(list.get(i).getSubnetId(), result.get(i).getSubnetId());
            assertEquals(list.get(i).getPublicStaticIp(), result.get(i).getPublicStaticIp());
            assertEquals(list.get(i).getPrivateStaticIp(), result.get(i).getPrivateStaticIp());
            assertEquals(list.get(i).getSubnetRange(), result.get(i).getSubnetRange());
            assertEquals(list.get(i).getSubnetGateway(), result.get(i).getSubnetGateway());
            assertEquals(list.get(i).getSubnetDns(), result.get(i).getSubnetDns());
            assertEquals(list.get(i).getStemcell(), result.get(i).getStemcell());
            assertEquals(list.get(i).getBoshPassword(), result.get(i).getBoshPassword());
            assertEquals(list.get(i).getCloudInstanceType(), result.get(i).getInstanceType());
        }
        verify(mockBootstrapDAO, times(1)).selectBootstrapList();
        verifyNoMoreInteractions(mockBootstrapDAO);
        
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 정보 상세 조회
     * @title : testGetBootstrapInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testGetBootstrapInfo(){
        BootstrapVO vo = setBootstrapInfo("Openstack");
        when(mockBootstrapDAO.selectBootstrapInfo(anyInt())).thenReturn(vo);
        
        BootstrapVO result = mockBootstrapService.getBootstrapInfo(1);
        assertEquals(vo.getId(), result.getId());
        assertEquals(vo.getIaasType(), result.getIaasType());
        assertEquals(vo.getDeploymentName(), result.getDeploymentName());
        assertEquals(vo.getDirectorName(), result.getDirectorName());
        assertEquals(vo.getBoshRelease(), result.getBoshRelease());
        assertEquals(vo.getBoshCpiRelease(), result.getBoshCpiRelease());
        assertEquals(vo.getNtp(), result.getNtp());
        assertEquals(vo.getDeployLog(), result.getDeployLog());
        assertEquals(vo.getDeployStatus(), result.getDeployStatus());
        assertEquals(vo.getDeploymentFile(), result.getDeploymentFile());
        assertEquals(vo.getIaasConfig().getIaasConfigAlias(), result.getIaasConfig().getIaasConfigAlias());
        assertEquals(vo.getSubnetId(), result.getSubnetId());
        assertEquals(vo.getPublicStaticIp(), result.getPublicStaticIp());
        assertEquals(vo.getPrivateStaticIp(), result.getPrivateStaticIp());
        assertEquals(vo.getSubnetRange(), result.getSubnetRange());
        assertEquals(vo.getSubnetGateway(), result.getSubnetGateway());
        assertEquals(vo.getSubnetDns(), result.getSubnetDns());
        assertEquals(vo.getStemcell(), result.getStemcell());
        assertEquals(vo.getBoshPassword(), result.getBoshPassword());
        assertEquals(vo.getCloudInstanceType(), result.getCloudInstanceType());
        
        verify(mockBootstrapDAO, times(1)).selectBootstrapInfo(1);
        verifyNoMoreInteractions(mockBootstrapDAO);
        
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 상세 조회 (잘못된 요청 Exception)
     * @title : testGetBootstrapInfoBadRequestCase
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testGetBootstrapInfoBadRequestCase(){
        when(mockBootstrapDAO.selectBootstrapInfo(anyInt())).thenReturn(null);
        when(mockMessageSource.getMessage(any(), any(), any())).thenReturn("");
        mockBootstrapService.getBootstrapInfo(1);
        
        verify(mockBootstrapDAO, times(1)).selectBootstrapInfo(1);
        verifyNoMoreInteractions(mockBootstrapDAO);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스냅샷 사용 여부
     * @title : testGetSnapshotInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testGetSnapshotInfo(){
        DirectorConfigVO director = setDirectorConfigInfo();
        when(mockDirectorConfigDAO.selectDirectorConfigByDefaultYn(anyString())).thenReturn(director);
        when(mockBootstrapDAO.selectSnapshotInfo(director)).thenReturn(1);
        int result = mockBootstrapService.getSnapshotInfo();
        assertEquals(result, 1);
        
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스냅샷 사용 여부 (잘못된 요청 Exception)
     * @title : testGetSnapshotInfoBadRequestCase
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testGetSnapshotInfoBadRequestCase(){
        DirectorConfigVO director = setDirectorConfigInfo();
        when(mockDirectorConfigDAO.selectDirectorConfigByDefaultYn(anyString())).thenReturn(null);
        when(mockBootstrapDAO.selectSnapshotInfo(director)).thenReturn(1);
        mockBootstrapService.getSnapshotInfo();
        
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 화면에 입력한 값을 Input Template 파일의 항목과 치환 하기위해 데이터 설정(Google)
     * @title : testMakeReplaceItemsFromGoogle
     * @return : void
    *****************************************************************/
    @Test
    public void testMakeReplaceItemsFromGoogle(){
        BootstrapVO vo = setBootstrapInfo("Openstack");//data 상세 조회 
        mockBootstrapService.makeReplaceItems(vo);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 배포 파일 생성 (Bootstrap 상세 정보 null 케이스 )
     * @title : testCreateSettingFileBootstrapNullCase
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testCreateSettingFileBootstrapNullCase(){
        when(mockBootstrapDAO.selectBootstrapInfo(anyInt())).thenReturn(null);
        mockBootstrapService.createSettingFile(1);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 배포 파일 생성 (Bootstrap Manifest Template null 케이스)
     * @title : testCreateSettingFileManifestTemplateNullCase
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testCreateSettingFileManifestTemplateNullCase(){
        BootstrapVO vo = setBootstrapInfo("Openstack");//data 상세 조회
        when(mockBootstrapDAO.selectBootstrapInfo(anyInt())).thenReturn(vo);
        when(mockCommonDeployDAO.selectManifetTemplate(anyString(), anyString(), anyString(), anyString())).thenReturn(null);
        mockBootstrapService.createSettingFile(1);
    }
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Manifest Template 디렉토리 정보 설정(BOSH RELEASE 256)
     * @title : testSetOptionManifestTemplateInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testSetOptionManifestTemplateInfoFrom256Release(){
        ManifestTemplateVO  manifestTemplate = setManifestTemplate("256", "Openstack");
        mockBootstrapService.setOptionManifestTemplateInfo(manifestTemplate, "openstack", "v2");
    }
   
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Manifest Template 디렉토리 정보 설정(BOSH RELEASE 233)
     * @title : testSetOptionManifestTemplateInfoFrom233Release
     * @return : void
    *****************************************************************/
    @Test
    public void testSetOptionManifestTemplateInfoFrom233Release(){
        ManifestTemplateVO  manifestTemplate = setManifestTemplate("233", "Openstack");
        mockBootstrapService.setOptionManifestTemplateInfo(manifestTemplate, "openstack", "v3");
    }
   
   
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Manifest Template 디렉토리 정보
     * @title : testSetOptionManifestTemplateInfo
     * @return : void
    *****************************************************************/
   @Test
    public void testSetOptionManifestTemplateInfoFromEmptyValue(){
        ManifestTemplateVO  manifestTemplate = new ManifestTemplateVO();
        ManifestTemplateVO result = mockBootstrapService.setOptionManifestTemplateInfo(manifestTemplate, "openstack", "v2");
        assertEquals(result.getCommonBaseTemplate(), "");
        assertEquals(result.getCommonJobTemplate(), "");
        assertEquals(result.getIaasPropertyTemplate(), "");
        assertEquals(result.getMetaTemplate(), "");
    }
   
   
   
   /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 단순 레코드 삭제
     * @title : testDeleteBootstrapInfo
     * @return : void
    *****************************************************************/
   @Test
   public void testDeleteBootstrapInfo(){
       BootStrapDeployDTO.Delete dto = setDeleteInfo();
       mockBootstrapService.deleteBootstrapInfo(dto);
       doNothing().when(mockBootstrapDAO).deleteBootstrapInfo(1);
       verify(mockBootstrapDAO, times(1)).deleteBootstrapInfo(1);
       verifyNoMoreInteractions(mockBootstrapDAO);
   }
   
   /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 정보 삭제(잘못된 요청 Exception)
     * @title : testDeleteBootstrapInfoBadRequestCase
     * @return : void
    *****************************************************************/
   @Test(expected=CommonException.class)
   public void testDeleteBootstrapInfoBadRequestCase(){
       BootStrapDeployDTO.Delete dto = new BootStrapDeployDTO.Delete();
       mockBootstrapService.deleteBootstrapInfo(dto);
       doNothing().when(mockBootstrapDAO).deleteBootstrapInfo(1);
       verify(mockBootstrapDAO, times(0)).deleteBootstrapInfo(1);
   }

    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 목록 조회
     * @title : setBootstrapList
     * @return : List<BootstrapVO>
    *****************************************************************/
    public List<BootstrapVO> setBootstrapList(String iaasType){
        List<BootstrapVO> list = new ArrayList<BootstrapVO>();
        BootstrapVO vo = new BootstrapVO();
        vo.setId(1);
        vo.setIaasType(iaasType);
        vo.setIaasConfigId(1);
        vo.getIaasConfig().setAccountId(1);
        vo.getIaasConfig().setAccountName("bosh");
        vo.getIaasConfig().setCommonKeypairName("bosh-key");
        vo.getIaasConfig().setCommonKeypairPath("bosh-key.pem");
        vo.getIaasConfig().setCommonSecurityGroup("bosh-security");
        vo.getIaasConfig().setIaasConfigAlias(iaasType+"-config1");
        
        //기본정보
        vo.setDeploymentName("bosh");
        vo.setDirectorName("test-bosh");
        vo.setBoshRelease("bosh-257.tgz");
        vo.setBoshCpiRelease("bosh-"+iaasType+"-cpi-release-14.tgz");
        vo.setEnableSnapshots("true");
        vo.setSnapshotSchedule("0 0 7 * * * schedule");
        vo.setNtp("1.kr.pool.ntp.org, 0.asia.pool.ntp.org");
        
        //네트워크
        if(  iaasType.equals("Google") ){
            vo.setNetworkName("bosh");
            vo.setSubnetId("subnet");
        }
        vo.setSubnetId("subnet-12345");
        vo.setPrivateStaticIp("10.0.100.11");
        vo.setPublicStaticIp("10.0.20.6");
        vo.setSubnetRange("10.0.20.0/24");
        vo.setSubnetGateway("10.0.20.1");
        vo.setSubnetDns("8.8.8.8");
        
        //리소스
        vo.setStemcell("bosh-stemcell-3421-"+iaasType+"-kvm-ubuntu-trusty-go_agent.tgz");
        vo.setCloudInstanceType("m1.large");
        vo.setBoshPassword("1234");
        vo.setDeploymentFile(iaasType+"-microbosh-test-1.yml");
        vo.setDeployLog("log...");
        
        list.add(vo);
        
        return list;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 상세 정보 설정
     * @title : setBootstrapInfo
     * @return : BootstrapVO
    *****************************************************************/
    public BootstrapVO setBootstrapInfo(String iaasType){
        BootstrapVO vo = new BootstrapVO();
        vo.setId(1);
        vo.setIaasType(iaasType);
        vo.setIaasConfigId(1);
        
        vo.getIaasAccount().put("commonAccessUser", "bosh-test");
        vo.getIaasAccount().put("commonAccessSecret", "secret-test");
        vo.getIaasAccount().put("commonTenant", "tenent");
        if( iaasType.equals("Openstack") ){
            vo.getIaasAccount().put("commonAccessEndpoint", "http://10.10.10.1:5000/v2.0");
            vo.getIaasConfig().setCommonKeypairPath("bosh-key.pem");
            vo.getIaasConfig().setIaasConfigAlias("openstack-config1");
            vo.getIaasConfig().setAccountName("openstack_v2");
        }else{
            vo.getIaasAccount().put("commonAccessEndpoint", "");
            vo.getIaasConfig().setCommonKeypairPath("bosh-key");
            vo.getIaasConfig().setIaasConfigAlias("google-config1");
            vo.getIaasConfig().setAccountName("google_c1");
            vo.getIaasAccount().put("googleJsonKey", "googleJsonKey");
        }
        
        
        vo.getIaasConfig().setAccountId(1);
        vo.getIaasConfig().setCommonKeypairName("bosh-key");
        vo.getIaasConfig().setCommonSecurityGroup("bosh-security");
        
        
        //기본정보
        vo.setDeploymentName("bosh");
        vo.setDirectorName("test-bosh");
        vo.setBoshRelease("bosh-257.tgz");
        vo.setBoshCpiRelease("bosh-"+iaasType+"-cpi-release-14.tgz");
        vo.setEnableSnapshots("true");
        vo.setSnapshotSchedule("0 0 7 * * * schedule");
        vo.setNtp("1.kr.pool.ntp.org, 0.asia.pool.ntp.org");
        vo.setPaastaMonitoringUse("false");
        
        //네트워크
        vo.setSubnetId("subnet-12345");
        vo.setPrivateStaticIp("10.0.100.11");
        vo.setPublicStaticIp("10.0.20.6");
        vo.setSubnetRange("10.0.20.0/24");
        vo.setSubnetGateway("10.0.20.1");
        vo.setSubnetDns("8.8.8.8");
        
        //리소스
        vo.setStemcell("bosh-stemcell-3421-"+iaasType+"-kvm-ubuntu-trusty-go_agent.tgz");
        vo.setCloudInstanceType("m1.large");
        vo.setBoshPassword("1234");
        vo.setDeploymentFile(iaasType+"-microbosh-test-1.yml");
        
        vo.setDeployLog("log...");
        
        return vo;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 설치 관리자 정보 설정
     * @title : setDirectorConfigInfo
     * @return : DirectorConfigVO
    *****************************************************************/
    public DirectorConfigVO setDirectorConfigInfo(){
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
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 배포 파일 정보 설정
     * @title : setManifestTemplate
     * @return : ManifestTemplateVO
    *****************************************************************/
    public ManifestTemplateVO setManifestTemplate(String version, String iaas){
        ManifestTemplateVO vo = new ManifestTemplateVO();
        
        vo.setId(1);
        vo.setDeployType("BOOTSTRAP");
        vo.setReleaseType("bosh");
        vo.setTemplateVersion(version);
        vo.setMinReleaseVersion(version);
        vo.setCommonBaseTemplate("generic_manifest_mask.yml");
        
        if( iaas.equalsIgnoreCase("openstack") ){
            vo.setIaasType("openstack");
            if( version.equals("256") ){
                vo.setCommonJobTemplate("bootstrap.yml");
                vo.setMetaTemplate("bootstrap_openstack_stub_256.yml");
                vo.setInputTemplate("bootstrap_openstack_inputs.yml");
            }else{
                vo.setIaasPropertyTemplate("openstack-microbosh-stub.yml");
                vo.setInputTemplate("openstack-microbosh-param.yml");
            }
        }else{
            vo.setIaasType("Google");
            vo.setCommonJobTemplate("bootstrap.yml");
            vo.setMetaTemplate("bootstrap_google_stub_256.yml");
            vo.setInputTemplate("bootstrap_google_inputs.yml");
        }
        return vo;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : bootstrap_openstack_inputs 파일 읽어옴
     * @title : setManifestInputTemplateStream
     * @return : String
    *****************************************************************/
    public String setManifestInputTemplateStream(){
        String content = "";
        try {
            InputStream inputs =  this.getClass().getClassLoader().getResourceAsStream("static/test/deploy_template/bootstrap_openstack_inputs.yml");
            content = IOUtils.toString(inputs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 삭제 정보 설정
     * @title : setDeleteInfo
     * @return : BootStrapDeployDTO.Delete
    *****************************************************************/
    public BootStrapDeployDTO.Delete setDeleteInfo(){
        BootStrapDeployDTO.Delete delete = new BootStrapDeployDTO.Delete();
        delete.setId("1");
        delete.setIaasType("Openstack");
        return delete;
    }

}
