package org.openpaas.ieda.deploy.web.deploy.bootstrap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.Date;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.web.common.base.BaseDeployControllerUnitTest;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.dao.BootstrapDAO;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.dao.BootstrapVO;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.dto.BootStrapDeployDTO;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.service.BootstrapSaveService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class BootstrapSaveServiceUnitTest extends BaseDeployControllerUnitTest{
    
    @InjectMocks
    private BootstrapSaveService mockBootstrapSaveService;
    @Mock
    private BootstrapDAO mockBootstrapDAO;
    @Mock
    private MessageSource mockMessageSource;
    
    private Principal principal;
    
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
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Google 환경 설정 정보 저장
     * @title : testSaveIaasConfigInfoFromInsertCase
     * @return : void
    *****************************************************************/
    @Test
    public void testSaveIaasConfigInfoFromInsertCase(){
        BootStrapDeployDTO.IaasConfig dto = setIaasConfigInfo("insert");
        BootstrapVO vo = setBootstrapVOInfoFromGoogle("insert");
        when(mockBootstrapDAO.selectBootstrapInfo(1)).thenReturn(vo);
        when(mockBootstrapDAO.insertBootStrapInfo(vo)).thenReturn(1);
        BootstrapVO result = mockBootstrapSaveService.saveIaasConfigInfo(dto, principal);
        assertEquals(result.getIaasType(), vo.getIaasType());
        assertEquals(result.getCreateUserId(), result.getCreateUserId());
        assertEquals(result.getUpdateUserId(), result.getUpdateUserId());
        
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Google 환경 설정 정보 수정
     * @title : testSaveIaasConfigInfoFromUpdateCase
     * @return : void
    *****************************************************************/
    @Test
    public void testSaveIaasConfigInfoFromUpdateCase(){
        BootStrapDeployDTO.IaasConfig dto = setIaasConfigInfo("update");
        BootstrapVO vo = setBootstrapVOInfoFromGoogle("update");
        when(mockBootstrapDAO.selectBootstrapInfo(1)).thenReturn(vo);
        when(mockBootstrapDAO.updateBootStrapInfo(vo)).thenReturn(1);
        BootstrapVO result = mockBootstrapSaveService.saveIaasConfigInfo(dto, principal);
        assertEquals(result.getIaasType(), vo.getIaasType());
        assertEquals(result.getCreateUserId(), result.getCreateUserId());
        assertEquals(result.getUpdateUserId(), result.getUpdateUserId());
        
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Google 환경 설정 정보 수정(Exception)
     * @title : testSaveIaasConfigInfoFromUpdateExceptionCase
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testSaveIaasConfigInfoFromUpdateExceptionCase(){
        BootStrapDeployDTO.IaasConfig dto = setIaasConfigInfo("update");
        when(mockBootstrapDAO.selectBootstrapInfo(1)).thenReturn(null);
        mockBootstrapSaveService.saveIaasConfigInfo(dto, principal);
        
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 기본 정보 저장
     * @title : testSaveDefaultInfoFromUpdate
     * @return : void
    *****************************************************************/
    @Test
    public void testSaveDefaultInfoFromUpdate(){
        BootStrapDeployDTO.Default dto = setDefaultInfo();
        BootstrapVO vo = setBootstrapVOInfoFromGoogle("update");
        when(mockBootstrapDAO.selectBootstrapInfo(1)).thenReturn(vo);
        when(mockBootstrapDAO.updateBootStrapInfo(vo)).thenReturn(1);
        BootstrapVO result = mockBootstrapSaveService.saveDefaultInfo(dto, principal);
        assertEquals(result.getDeploymentName(), vo.getDeploymentName());
        assertEquals(result.getDirectorName(), vo.getDirectorName());
        assertEquals(result.getNtp(), vo.getNtp());
        assertEquals(result.getBoshRelease(), vo.getBoshRelease());
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 기본 정보 저장 id null 오류
     * @title : testSaveDefaultInfoFromIdEmptyException
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testSaveDefaultInfoFromIdEmptyException(){
        BootStrapDeployDTO.Default dto = setDefaultInfo();
        BootstrapVO vo = setBootstrapVOInfoFromGoogle("update");
        dto.setId(null);
        when(mockBootstrapDAO.selectBootstrapInfo(1)).thenReturn(vo);
        mockBootstrapSaveService.saveDefaultInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 기본 정보 저장 (Null 오류)
     * @title : testSaveDefaultInfoFromNullException
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testSaveDefaultInfoFromNullException(){
        BootStrapDeployDTO.Default dto = setDefaultInfo();
        when(mockBootstrapDAO.selectBootstrapInfo(1)).thenReturn(null);
        mockBootstrapSaveService.saveDefaultInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 네트워크 정보 저장
     * @title : testSaveNetworkInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testSaveNetworkInfo(){
        BootStrapDeployDTO.Network dto = setNetworkInfo();
        BootstrapVO vo = setBootstrapVOInfoFromGoogle("update");
        when(mockBootstrapDAO.selectBootstrapInfo(1)).thenReturn(vo);
        when(mockBootstrapDAO.updateBootStrapInfo(vo)).thenReturn(1);
        mockBootstrapSaveService.saveNetworkInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 네트워크 정보 저장(Id null 오류)
     * @title : testSaveNetworkInfoFromIdNull
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testSaveNetworkInfoFromIdNull(){
        BootStrapDeployDTO.Network dto = setNetworkInfo();
        dto.setId(null);
        mockBootstrapSaveService.saveNetworkInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 네트워크 정보 저장( 조회 null 오류) 
     * @title : testSaveNetworkInfoFromNullException
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testSaveNetworkInfoFromNullException(){
        BootStrapDeployDTO.Network dto = setNetworkInfo();
        when(mockBootstrapDAO.selectBootstrapInfo(1)).thenReturn(null);
        when(mockMessageSource.getMessage(any(), any(), any())).thenReturn("");
        mockBootstrapSaveService.saveNetworkInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 리소스 정보 저장
     * @title : testSaveResourcesInfo
     * @return : void
    *****************************************************************/
    @Test
    public void testSaveResourcesInfo(){
        BootStrapDeployDTO.Resource dto = setResourceInfo();
        BootstrapVO vo = setBootstrapVOInfoFromGoogle("update");
        vo.setDeploymentFile(null);
        when(mockBootstrapDAO.selectBootstrapInfo(1)).thenReturn(vo);
        when(mockBootstrapDAO.updateBootStrapInfo(vo)).thenReturn(1);
        BootstrapVO result =  mockBootstrapSaveService.saveResourceInfo(dto, principal);
        assertEquals(result.getStemcell(), vo.getStemcell());
        assertEquals(result.getCloudInstanceType(), vo.getCloudInstanceType());
        assertEquals(result.getBoshPassword(), vo.getBoshPassword());
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 리소스 정보 저장(Id null 오류) 
     * @title : testSaveResourcesInfoFromIdNull
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testSaveResourcesInfoFromIdNull(){
        BootStrapDeployDTO.Resource dto = setResourceInfo();
        dto.setId(null);
        mockBootstrapSaveService.saveResourceInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 리소스 정보 저장 (조회 null 오류)
     * @title : testSaveResourcesInfoFromNullException
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testSaveResourcesInfoFromNullException(){
        BootStrapDeployDTO.Resource dto = setResourceInfo();
        when(mockBootstrapDAO.selectBootstrapInfo(1)).thenReturn(null);
        mockBootstrapSaveService.saveResourceInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 배포 파일명 생성 오류
     * @title : testMakeDeploymentNameNullException
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testMakeDeploymentNameNullException(){
        BootstrapVO vo = setBootstrapVOInfoFromGoogle("update");
        vo.setIaasType(null);
        vo.setId(null);
        mockBootstrapSaveService.makeDeploymentName(vo);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 환경 설정 정보 설정
     * @title : setIaasConfigInfo
     * @return : BootStrapDeployDTO.IaasConfig
    *****************************************************************/
    public BootStrapDeployDTO.IaasConfig setIaasConfigInfo(String type){
        BootStrapDeployDTO.IaasConfig dto = new BootStrapDeployDTO.IaasConfig();
        if( type.equalsIgnoreCase("update") ){
            dto.setId("1");
        }
        dto.setIaasType("Google");
        dto.setIaasConfigId("1");
        dto.setTestFlag("Y");
        
        dto.getId();
        dto.getIaasType();
        dto.getIaasConfigId();
        dto.getTestFlag();
        
        return dto;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 기본 정보 설정 
     * @title : setDefaultInfo
     * @return : BootStrapDeployDTO.Default
    *****************************************************************/
    public BootStrapDeployDTO.Default setDefaultInfo(){
        BootStrapDeployDTO.Default dto = new BootStrapDeployDTO.Default();
        dto.setId("1");
        dto.setIaasConfigId("1");
        dto.setDeploymentName("bosh");
        dto.setDirectorName("bosh");
        dto.setBoshRelease("bosh-release");
        dto.setNtp("1.kr.pool.ntp.org, 0.asia.pool.ntp.org");
        dto.setBoshCpiRelease("bosh-cpi-release");
        dto.setEnableSnapshots("false");
        dto.setSnapshotSchedule("");
        
        dto.getId();
        dto.getIaasConfigId();
        dto.getDeploymentName();
        dto.getDirectorName();
        dto.getBoshRelease();
        dto.getBoshCpiRelease();
        dto.getNtp();
        dto.getEnableSnapshots();
        dto.getSnapshotSchedule();
        
        return dto;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 네트워크 정보 설정
     * @title : setNetworkInfo
     * @return : BootStrapDeployDTO.Network
    *****************************************************************/
    public BootStrapDeployDTO.Network setNetworkInfo(){
        BootStrapDeployDTO.Network dto = new BootStrapDeployDTO.Network ();
        dto.setId("1");
        dto.setSubnetId("sbosh");
        dto.setNetworkName("bosh");
        dto.setPrivateStaticIp("192.168.40.10");
        dto.setSubnetRange("192.168.40.0/24");
        dto.setSubnetGateway("192.168.40.1");
        dto.setSubnetDns("8.8.8.8");
        dto.setPublicStaticIp("35.200.83.25");
        dto.setPublicSubnetId(null);
        dto.setPublicSubnetRange(null);
        dto.setPublicSubnetGateway(null);
        dto.setPublicSubnetDns(null);
        
        dto.getId();
        dto.getSubnetId();
        dto.getNetworkName();
        dto.getPrivateStaticIp();
        dto.getSubnetRange();
        dto.getSubnetGateway();
        dto.getPublicSubnetDns();
        dto.getPublicStaticIp();
        dto.getPublicSubnetDns();
        dto.getPublicSubnetId();
        dto.getPublicSubnetGateway();
        
        
        return dto;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 리소스 정보 저장
     * @title : setResourceInfo
     * @return : BootStrapDeployDTO.Resource
    *****************************************************************/
    public BootStrapDeployDTO.Resource setResourceInfo(){
        BootStrapDeployDTO.Resource dto = new BootStrapDeployDTO.Resource();
        dto.setId("1");
        dto.setStemcell("light-bosh-stemcell-3363-google-kvm-ubuntu-trusty-go_agent.tgz");
        dto.setCloudInstanceType("n1-standard-2");
        dto.setBoshPassword("cloudc0w");
        dto.setResourcePoolCpu(null);
        dto.setResourcePoolRam(null);
        dto.setResourcePoolDisk(null);
        
        dto.getId();
        dto.getStemcell();
        dto.getCloudInstanceType();
        dto.getBoshPassword();
        dto.getResourcePoolCpu();
        dto.getResourcePoolRam();
        dto.getResourcePoolDisk();
        
        return dto;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : BootstrapVO 정보 설정 (GOOGLE)
     * @title : setBootstrapVOInfoFromGoogle
     * @return : BootstrapVO
    *****************************************************************/
    public BootstrapVO setBootstrapVOInfoFromGoogle(String type){
        BootstrapVO vo = new BootstrapVO();
        
        if( type.equalsIgnoreCase("update") ){
            vo.setId(1);
            vo.getIaasConfig().setDeployStatus("사용중");
            
        }
        vo.setIaasType("Google");
        vo.setIaasConfigId(1);
        vo.setCreateDate(new Date());
        vo.setUpdateDate(new Date());
        vo.setUpdateUserId(principal.getName());
        vo.setCreateUserId(principal.getName());
        vo.setTestFlag("Y");
        
        //Iaas Config Info
//        vo.setIaasConfig(new IaasConfigMgntVO());
//           vo.setIaasAccount(new HashMap<String, Object>());
        vo.getIaasConfig().setId(1);
        vo.getIaasConfig().setAccountId(1);
        vo.getIaasConfig().setAccountName("google-account");
        vo.getIaasConfig().setIaasType("Google");
        vo.getIaasConfig().setIaasConfigAlias("google-config");
        vo.getIaasConfig().setCommonSecurityGroup("internet");
        vo.getIaasConfig().setCommonKeypairPath("google-key");
        vo.getIaasConfig().setCommonAvailabilityZone("asia-northeast1-a");
        vo.getIaasConfig().setCommonRegion(null);
        vo.getIaasConfig().setCommonKeypairName(null);
        vo.getIaasConfig().setVsphereVcentDataCenterName(null);
        vo.getIaasConfig().setVsphereVcenterCluster(null);
        vo.getIaasConfig().setVsphereVcenterDatastore(null);
        vo.getIaasConfig().setVsphereVcenterDiskPath(null);
        vo.getIaasConfig().setVsphereVcenterPersistentDatastore(null);
        vo.getIaasConfig().setVsphereVcenterTemplateFolder(null);
        vo.getIaasConfig().setVsphereVcenterVmFolder(null);
        
        //기본 정보 설정
        vo.setDeploymentName("bosh");
        vo.setDirectorName("bosh");
        vo.setBoshRelease("bosh-release");
        vo.setBoshCpiRelease("bosh-cpi-release");
        vo.setSnapshotSchedule(null);
        vo.setEnableSnapshots("false");
        vo.setNtp("1.kr.pool.ntp.org, 0.asia.pool.ntp.org");
        
        //네트워크 정보 설정
        vo.setSubnetId("sbosh");
        vo.setNetworkName("bosh");
        vo.setPrivateStaticIp("192.168.40.10");
        vo.setSubnetRange("192.168.40.0/24");
        vo.setSubnetGateway("192.168.40.1");
        vo.setSubnetDns("8.8.8.8");
        vo.setPublicStaticIp("35.200.83.25");
        vo.setPublicSubnetId(null);
        vo.setPublicSubnetRange(null);
        vo.setPublicSubnetGateway(null);
        vo.setPublicSubnetDns(null);
        
        //리소스 정보 설정
        vo.setStemcell("light-bosh-stemcell-3363-google-kvm-ubuntu-trusty-go_agent.tgz");
        vo.setCloudInstanceType("n1-standard-2");
        vo.setBoshPassword("cloudc0w");
        vo.setResourcePoolCpu(null);
        vo.setResourcePoolRam(null);
        vo.setResourcePoolDisk(null);
        
        vo.setDeploymentFile("google-microbosh-1.yml");
        vo.setDeployStatus(null);
        vo.setDeployLog("test...");
        
        return vo;
    }
   

}
