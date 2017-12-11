package org.openpaas.ieda.deploy.web.deploy.diego.service;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.Application;

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
import org.openpaas.ieda.deploy.web.deploy.common.dao.network.NetworkDAO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.network.NetworkVO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.resource.ResourceDAO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.resource.ResourceVO;
import org.openpaas.ieda.deploy.web.deploy.diego.dao.DiegoDAO;
import org.openpaas.ieda.deploy.web.deploy.diego.dao.DiegoVO;
import org.openpaas.ieda.deploy.web.deploy.diego.dto.DiegoListDTO;
import org.openpaas.ieda.deploy.web.deploy.diego.dto.DiegoParamDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class DiegoServiceUnitTest extends BaseDeployControllerUnitTest {
    
    
    @InjectMocks DiegoService mockDiegoService;
    @Mock DiegoDAO mockDiegoDAO;
    @Mock NetworkDAO mockNetworkDAO;
    @Mock ResourceDAO mockResourceDAO;
    @Mock CommonDeployDAO mockCommonDeployDAO;
    @Mock MessageSource mockMessageSource;
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
     *****************************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Diego 목록 정보를 조회
    * @title : testGetDiegoInfoList
    * @return : void
    ***************************************************/
    @Test
    public void testGetDiegoInfoList(){
        List<DiegoVO> vo = setResultDiegoInfo();
        ResourceVO resourceVO = setResourceInfo();
        List<NetworkVO> resourceNetworkList = resultNetworkInfoList("default");
        when(mockDiegoDAO.selectDiegoListInfo(anyString())).thenReturn(vo);
        when(mockNetworkDAO.selectNetworkList(anyInt(), anyString())).thenReturn(resourceNetworkList);
        when(mockResourceDAO.selectResourceInfo(anyInt(), anyString())).thenReturn(resourceVO);
        mockDiegoService.getDiegoInfoList("aws");
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Diego 목록 정보를 조회 네트워크 사이즈가 2개 이상 일 경우
    * @title : testGetDiegoInfoList
    * @return : void
    ***************************************************/
    @Test
    public void testGetDiegoInfoListNetworkSize(){
        List<DiegoVO> vo = setResultDiegoInfo();
        ResourceVO resourceVO = setResourceInfo();
        List<NetworkVO> resourceNetworkList = resultNetworkInfoList("size");
        when(mockDiegoDAO.selectDiegoListInfo(anyString())).thenReturn(vo);
        when(mockNetworkDAO.selectNetworkList(anyInt(), anyString())).thenReturn(resourceNetworkList);
        when(mockResourceDAO.selectResourceInfo(anyInt(), anyString())).thenReturn(resourceVO);
        mockDiegoService.getDiegoInfoList("aws");
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Diego 목록 정보를 조회 Extanl 일 경우
    * @title : testGetDiegoInfoListNetworkExternal
    * @return : void
    ***************************************************/
    @Test
    public void testGetDiegoInfoListNetworkExternal(){
        List<DiegoVO> vo = setResultDiegoInfo();
        ResourceVO resourceVO = setResourceInfo();
        List<NetworkVO> resourceNetworkList = resultNetworkInfoList("external");
        when(mockDiegoDAO.selectDiegoListInfo(anyString())).thenReturn(vo);
        when(mockNetworkDAO.selectNetworkList(anyInt(), anyString())).thenReturn(resourceNetworkList);
        when(mockResourceDAO.selectResourceInfo(anyInt(), anyString())).thenReturn(resourceVO);
        mockDiegoService.getDiegoInfoList("aws");
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Diego 정보 상세 조회
    * @title : testGetDiegoDetailInfo
    * @return : void
    ***************************************************/
    @Test
    public void testGetDiegoDetailInfo(){
        ResourceVO resourceVO = setResourceInfo();
        DiegoVO expectVo = setResultDiegoInfo("default");
        List<NetworkVO> resourceNetworkList = resultNetworkInfoList("external");
        List<HashMap<String, Object>> jobs = resultJobsMap();
        when(mockDiegoDAO.selectDiegoInfo(anyInt())).thenReturn(expectVo);
        when(mockNetworkDAO.selectNetworkList(anyInt(), anyString())).thenReturn(resourceNetworkList);
        when(mockResourceDAO.selectResourceInfo(anyInt(), anyString())).thenReturn(resourceVO);
        when(mockDiegoDAO.selectDiegoJobSettingInfoListBycfId(anyString(), anyInt())).thenReturn(jobs);
        mockDiegoService.getDiegoDetailInfo(1);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Diego Manifest 파일 생성 
    * @title : testCreateSettingFile
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testCreateSettingFile(){
        DiegoVO expectVo = setResultDiegoInfo("default");
        ManifestTemplateVO expectManifestVO = setManifestTemplate("default");
        when(mockCommonDeployDAO.selectManifetTemplate(anyString(), anyString(), anyString(), anyString())).thenReturn(expectManifestVO);
        mockDiegoService.createSettingFile(expectVo);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : option Manifest 템플릿 정보 설정
    * @title : testSetOptionManifestTemplateInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSetOptionManifestTemplateInfo(){
        DiegoVO expectVo = setResultDiegoInfo("size");
        ManifestTemplateVO expectManifestVO = setManifestTemplate("default");
        mockDiegoService.setOptionManifestTemplateInfo(expectManifestVO, expectManifestVO, expectVo);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Diego 단순 레코드 삭제
    * @title : testDeleteDiegoInfoRecord
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteDiegoInfoRecord(){
       DiegoParamDTO.Delete dto = setDiegoDeleteInfo();
       mockDiegoService.deleteDiegoInfoRecord(dto);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 인프라 및 릴리즈 버전 별 job 목록 조회
    * @title : testDeleteDiegoInfoRecord
    * @return : void
    ***************************************************/
    @Test
    public void testGetJobTemplateList(){
        mockDiegoService.getJobTemplateList("aws", "1.25.3");
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 입력한 정보를 ReplaceItemDTO 목록에 넣고 setReplaceItems 메소드에 응답
     * @title : setReplaceItems
     * @return : List<ReplaceItemDTO>
    *****************************************************************/
    @Test
    public void testSetReplaceItems(){
        DiegoVO expectVo = setResultDiegoInfo("size");
        mockDiegoService.setReplaceItems(expectVo);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 입력한 정보를 ReplaceItemDTO 목록에 넣고 setReplaceItems 메소드에 응답
     * @title : setReplaceItems
     * @return : List<ReplaceItemDTO>
    *****************************************************************/
    @Test
    public void testSetReplaceItemsCflinuxNull(){
        DiegoVO expectVo = setResultDiegoInfo("cflinux");
        mockDiegoService.setReplaceItems(expectVo);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 입력한 정보를 ReplaceItemDTO 목록에 넣고 setReplaceItems 메소드에 응답
     * @title : setReplaceItems
     * @return : List<ReplaceItemDTO>
    *****************************************************************/
    @Test
    public void testSetReplaceItemsIaasTypeGoogle(){
        DiegoVO expectVo = setResultDiegoInfo("google");
        mockDiegoService.setReplaceItems(expectVo);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 입력한 정보를 ReplaceItemDTO 목록에 넣고 setReplaceItems 메소드에 응답
     * @title : setReplaceItems
     * @return : List<ReplaceItemDTO>
    *****************************************************************/
    @Test
    public void testSetReplaceItemsIaasTypeGoogleNetworkSize(){
        DiegoVO expectVo = setResultDiegoInfo("googleSize");
        mockDiegoService.setReplaceItems(expectVo);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 입력한 정보를 ReplaceItemDTO 목록에 넣고 setReplaceItems 메소드에 응답
     * @title : setReplaceItems
     * @return : List<ReplaceItemDTO>
    *****************************************************************/
    @Test
    public void testSetReplaceItemsIaasTypeVspherework(){
        DiegoVO expectVo = setResultDiegoInfo("update");
        mockDiegoService.setReplaceItems(expectVo);
    }
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 입력한 정보를 ReplaceItemDTO 목록에 넣고 setReplaceItems 메소드에 응답
     * @title : setReplaceItems
     * @return : List<ReplaceItemDTO>
    *****************************************************************/
    @Test
    public void testSetReplaceItemsIaasTypeVsphereworkSize(){
        DiegoVO expectVo = setResultDiegoInfo("vsphereSize");
        mockDiegoService.setReplaceItems(expectVo);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 입력한 정보를 ReplaceItemDTO 목록에 넣고 setReplaceItems 메소드에 응답
     * @title : setReplaceItems
     * @return : List<ReplaceItemDTO>
    *****************************************************************/
    @Test
    public void testSetReplaceItemsIaasTypeJobSize0(){
        DiegoVO expectVo = setResultDiegoInfo("jobSize0");
        when(mockDiegoDAO.selectDiegoJobTemplatesByReleaseVersion(any())).thenReturn(resultJobListInfo());
        mockDiegoService.setReplaceItems(expectVo);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 입력한 정보를 ReplaceItemDTO 목록에 넣고 setReplaceItems 메소드에 응답
     * @title : setReplaceItems
     * @return : List<ReplaceItemDTO>
    *****************************************************************/
    @Test
    public void testSetReplaceItemsIaasTypeJobSize3(){
        DiegoVO expectVo = setResultDiegoInfo("jobSize3");
        when(mockDiegoDAO.selectDiegoJobTemplatesByReleaseVersion(any())).thenReturn(resultJobListInfo());
        mockDiegoService.setReplaceItems(expectVo);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 입력한 정보를 ReplaceItemDTO 목록에 넣고 setReplaceItems 메소드에 응답
     * @title : setReplaceItems
     * @return : List<ReplaceItemDTO>
    *****************************************************************/
    @Test
    public void testSetReplaceItemsIaasTypeJobSize2(){
        DiegoVO expectVo = setResultDiegoInfo("size");
        mockDiegoService.setReplaceItems(expectVo);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 입력한 정보를 ReplaceItemDTO 목록에 넣고 setReplaceItems 메소드에 응답
     * @title : setReplaceItems
     * @return : List<ReplaceItemDTO>
    *****************************************************************/
    @Test
    public void testSetReplaceItemsIaasTypeJobSize1(){
        DiegoVO expectVo = setResultDiegoInfo("jobSize1");
        mockDiegoService.setReplaceItems(expectVo);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : DIEGO 삭제 정보 설정
    * @title : setDiegoDeleteInfo
    * @return : DiegoParamDTO.Delete
    ***************************************************/
    public DiegoParamDTO.Delete setDiegoDeleteInfo() {
        DiegoParamDTO.Delete dto = new DiegoParamDTO.Delete();
        dto.setIaas("aws");
        dto.setId("1");
        dto.setPlatform("diego");
        List<String> list = new ArrayList<String>();
        String seq = "0";
        list.add(seq);
        dto.setSeq(list);
        return dto;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : option Manifest 템플릿 정보 설정 값이 없을 경우
    * @title : testSetOptionManifestTemplateInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSetOptionManifestTemplateInfoOption(){
        DiegoVO expectVo = setResultDiegoInfo("default");
        ManifestTemplateVO expectManifestVO = setManifestTemplate("null");
        mockDiegoService.setOptionManifestTemplateInfo(expectManifestVO, expectManifestVO, expectVo);
    }
    

    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Job 정보 설정
    * @title : resultJobListInfo
    * @return : List<HashMap<String, String>>
    ***************************************************/
    public List<HashMap<String, String>> resultJobListInfo() {
        List<HashMap<String, String>> mapList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("jobName", "databases");
        map.put("zone", "z3");
        mapList.add(map);
        map = new HashMap<String, String>();
        map.put("jobName", "databases");
        map.put("zone", "z1");
        mapList.add(map);
        map = new HashMap<String, String>();
        map.put("jobName", "databases");
        map.put("zone", "z2");
        mapList.add(map);
        return mapList;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Manifest 템플릿 설정
    * @title : setManifestTemplate
    * @return : ManifestTemplateVO
    ***************************************************/
    public ManifestTemplateVO setManifestTemplate(String type) {
        ManifestTemplateVO vo = new ManifestTemplateVO();
        if(type.equals("default")){
            vo.setCfTempleate("cf.yml");
            vo.setCommonJobTemplate("job.yml");
            vo.setCommonBaseTemplate("base.yml");
            vo.setDeployType("diego");
            vo.setIaasType("aws");
            vo.setMetaTemplate("meta.yml");
            vo.setCommonOptionTemplate("option.yml");
            vo.setInputTemplate("diego_aws_inputs.yml");
            vo.setOptionEtc("etc.yml");
            vo.setIaasPropertyTemplate("iaas.yml");
            vo.setOptionNetworkTemplate("network.yml");
            vo.setReleaseType("diego");
            vo.setMinReleaseVersion("1.25.3");
            vo.setTemplateVersion("1.25.3");
            vo.setOptionResourceTemplate("resource.yml");
        }
        return vo;
    }

    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : DIEGO 상세 정보 조회 결과 값 설정 
    * @title : setResultDiegoInfo
    * @return : DiegoVO
    ***************************************************/
    public DiegoVO setResultDiegoInfo(String type) {
        DiegoVO vo = new DiegoVO();
        vo.setCadvisorDriverIp("10.0.0.6");
        vo.setCadvisorDriverPort("9033");
        vo.setCfDeployment("cf.yml");
        vo.setCfId(1);
        vo.setCflinuxfs2rootfsreleaseName("cflinux");
        vo.setCflinuxfs2rootfsreleaseVersion("1.150.1");
        vo.setCfName("cf-aws");
        vo.setCfReleaseName("cf");
        vo.setCfReleaseVersion("272");
        vo.setCreateUserId("admin");
        if(type.equals("update")){
            vo.setDeploymentFile("aws-diego.yml");
        }

        vo.setDeploymentName("aws-diego");
        vo.setDeployStatus("deploy");
        vo.setDiegoReleaseName("diego");
        if(type.equals("cflinux")){
            vo.setDiegoReleaseVersion("0.1440.0");
            vo.setCflinuxfs2rootfsreleaseName("");
            vo.setCflinuxfs2rootfsreleaseVersion("");
        } else {
            vo.setDiegoReleaseVersion("1.25.3");
        }
        vo.setDirectorUuid("uuid");
        vo.setEtcdReleaseName("etcd");
        vo.setEtcdReleaseVersion("104");
        vo.setGardenReleaseName("garden");
        vo.setGardenReleaseVersion("153");
        if(type.equals("update")){
            vo.setId(1);
            vo.setIaasType("vsphere");
        } else if(!type.equals("error")){
            vo.setId(1);
            vo.setIaasType("aws");
        }
        
        if(type.equals("google")) vo.setIaasType("google");
        
        vo.setKeyFile("key.yml");
        vo.setPaastaMonitoringUse("true");
        vo.setTaskId(1);
        vo.setUpdateUserId("admin");
        List<NetworkVO> list = new ArrayList<NetworkVO>();
        NetworkVO networkVo = new NetworkVO();
        networkVo.setAvailabilityZone("west-1");
        networkVo.setCloudSecurityGroups("seg");
        networkVo.setCreateUserId("admin");
        networkVo.setDeployType("DEIGO");
        networkVo.setId(1);
        networkVo.setNet("Internal");
        networkVo.setNetworkName("netName");
        networkVo.setPublicStaticIP("113.123.123.123");
        networkVo.setSeq(0);
        networkVo.setSubnetDns("8.8.8.8");
        networkVo.setSubnetRange("192.168.0.0/24");
        networkVo.setSubnetReservedFrom("192.168.0.1");
        networkVo.setSubnetReservedTo("192.168.0.155");
        networkVo.setSubnetStaticFrom("192.168.155");
        networkVo.setSubnetStaticTo("192.168.0.255");
        networkVo.setSubnetGateway("192.168.0.1");
        networkVo.setUpdateUserId("admin");
        vo.setNetwork(networkVo);
        ResourceVO resourceVO = new ResourceVO();
        resourceVO.setBoshPassword("bosh");
        resourceVO.setCreateUserId("admin");
        resourceVO.setDeployType("openstack");
        if(type.equals("update")){
            resourceVO.setId(1);
        }
        resourceVO.setLargeCpu(10);
        resourceVO.setLargeDisk(1000);
        resourceVO.setLargeFlavor("m1.large");
        resourceVO.setLargeRam(1000);
        resourceVO.setMediumCpu(5);
        resourceVO.setMediumDisk(500);
        resourceVO.setMediumFlavor("m1.medium");
        resourceVO.setMediumRam(500);
        resourceVO.setRunnerCpu(1500);
        resourceVO.setRunnerDisk(1500);
        resourceVO.setRunnerFlavor("m1.xlarge");
        resourceVO.setRunnerRam(1500);
        resourceVO.setSmallFlavor("m1.small");
        resourceVO.setSmallCpu(1);
        resourceVO.setSmallRam(1000);
        resourceVO.setSmallDisk(100);
        resourceVO.setStemcellName("stemcell");
        resourceVO.setStemcellVersion("3417");
        resourceVO.setUpdateUserId("adimn");
        
        resourceVO.getLargeCpu();
        resourceVO.getLargeDisk();
        resourceVO.getLargeFlavor();
        resourceVO.getLargeRam();
        resourceVO.getMediumCpu();
        resourceVO.getMediumDisk();
        resourceVO.getMediumFlavor();
        resourceVO.getMediumRam();
        resourceVO.getRunnerCpu();
        resourceVO.getRunnerDisk();
        resourceVO.getRunnerFlavor();
        resourceVO.getRunnerRam();
        resourceVO.getSmallFlavor();
        resourceVO.getSmallCpu();
        resourceVO.getSmallRam();
        resourceVO.getSmallDisk();
        resourceVO.getStemcellName();
        resourceVO.getStemcellVersion();
        resourceVO.getUpdateUserId();
        
        vo.setResource(resourceVO);
        list.add(networkVo);
        if(type.equals("size") || type.equals("googleSize") || type.equals("vsphereSize")){
            networkVo = new NetworkVO();
            networkVo.setAvailabilityZone("west-1");
            networkVo.setCloudSecurityGroups("seg");
            networkVo.setCreateUserId("admin");
            networkVo.setDeployType("DEIGO");
            networkVo.setId(1);
            networkVo.setNet("Internal");
            networkVo.setNetworkName("netName");
            networkVo.setPublicStaticIP("113.123.123.123");
            networkVo.setSeq(0);
            networkVo.setSubnetDns("8.8.8.8");
            networkVo.setSubnetRange("192.168.0.0/24");
            networkVo.setSubnetReservedFrom("192.168.0.1");
            networkVo.setSubnetReservedTo("192.168.0.155");
            networkVo.setSubnetStaticFrom("192.168.155");
            networkVo.setSubnetStaticTo("192.168.0.255");
            networkVo.setSubnetGateway("192.168.0.1");
            networkVo.setUpdateUserId("admin");
            list.add(networkVo);
            networkVo = new NetworkVO();
            networkVo.setAvailabilityZone("west-1");
            networkVo.setCloudSecurityGroups("seg");
            networkVo.setCreateUserId("admin");
            networkVo.setDeployType("DEIGO");
            networkVo.setId(1);
            networkVo.setNet("Internal");
            networkVo.setNetworkName("netName");
            networkVo.setPublicStaticIP("113.123.123.123");
            networkVo.setSeq(0);
            networkVo.setSubnetDns("8.8.8.8");
            networkVo.setSubnetRange("192.168.0.0/24");
            networkVo.setSubnetReservedFrom("192.168.0.1");
            networkVo.setSubnetReservedTo("192.168.0.155");
            networkVo.setSubnetStaticFrom("192.168.155");
            networkVo.setSubnetStaticTo("192.168.0.255");
            networkVo.setSubnetGateway("192.168.0.1");
            networkVo.setUpdateUserId("admin");
            list.add(networkVo);
            if(type.equals("googleSize")) vo.setIaasType("google");
            if(type.equals("vsphereSize")) vo.setIaasType("vsphere");
            
        }
        vo.setNetworks(list);
        List<HashMap<String, Object>> mapList = new ArrayList<HashMap<String, Object>>();
        if(!type.equals("jobSize0") || type.equals("jobSize3") || type.equals("jobSize2") || type.equals("jobSize1")){
            HashMap<String, Object> map = new HashMap<String, Object>();
            if(!type.equals("jobSize1")){
                map.put("job_name", "databases");
                map.put("zone", "z1");
                map.put("instances", "1");
                mapList.add(map);
            }
            if(type.equals("jobSize3")){
                map = new HashMap<String, Object>();
                map.put("job_name", "databases");
                map.put("zone", "z2");
                map.put("instances", "1");
                mapList.add(map);
                map = new HashMap<String, Object>();
                map.put("job_name", "databases");
                map.put("zone", "z3");
                map.put("instances", "1");
                mapList.add(map);
            }
        }
        vo.setJobs(mapList);
        return vo;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Job 정보 설정
    * @title : resultJobsMap
    * @return : List<HashMap<String, Object>>
    ***************************************************/
    public List<HashMap<String, Object>> resultJobsMap() {
        List<HashMap<String, Object>> mapList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("jobName", "databases");
        map.put("zone", "z3");
        mapList.add(map);
        return mapList;
    }

    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 네트워크 정보 설정
    * @title : resultNetworkInfoList
    * @return : List<NetworkVO>
    ***************************************************/
    public List<NetworkVO> resultNetworkInfoList(String type){
        List<NetworkVO> list = new ArrayList<NetworkVO>();
        NetworkVO networkVo = new NetworkVO();
        networkVo.setAvailabilityZone("west-1");
        networkVo.setCloudSecurityGroups("seg");
        networkVo.setCreateUserId("admin");
        networkVo.setDeployType("DEIGO");
        networkVo.setId(1);
        if(!type.equals("external")){
            networkVo.setNet("internal");
        } else {
            networkVo.setNet("netid");
        }
        
        networkVo.setNetworkName("netName");
        networkVo.setPublicStaticIP("113.123.123.123");
        networkVo.setSeq(0);
        networkVo.setSubnetDns("8.8.8.8");
        networkVo.setSubnetRange("192.168.0.0/24");
        networkVo.setSubnetReservedFrom("192.168.0.1");
        networkVo.setSubnetReservedTo("192.168.0.155");
        networkVo.setSubnetStaticFrom("192.168.155");
        networkVo.setSubnetStaticTo("192.168.0.255");
        networkVo.setSubnetGateway("192.168.0.1");
        networkVo.setUpdateUserId("admin");
        list.add(networkVo);
        if(type.equals("size")) {
            networkVo = new NetworkVO();
            networkVo.setAvailabilityZone("west-1");
            networkVo.setCloudSecurityGroups("seg");
            networkVo.setCreateUserId("admin");
            networkVo.setDeployType("DEIGO");
            networkVo.setId(1);
            networkVo.setNet("internal");
            networkVo.setNetworkName("netName");
            networkVo.setPublicStaticIP("113.123.123.123");
            networkVo.setSeq(0);
            networkVo.setSubnetDns("8.8.8.8");
            networkVo.setSubnetRange("192.168.0.0/24");
            networkVo.setSubnetReservedFrom("192.168.0.1");
            networkVo.setSubnetReservedTo("192.168.0.155");
            networkVo.setSubnetStaticFrom("192.168.155");
            networkVo.setSubnetStaticTo("192.168.0.255");
            networkVo.setSubnetGateway("192.168.0.1");
            networkVo.setUpdateUserId("admin");
            list.add(networkVo);
            
            networkVo = new NetworkVO();
            networkVo.setAvailabilityZone("west-1");
            networkVo.setCloudSecurityGroups("seg");
            networkVo.setCreateUserId("admin");
            networkVo.setDeployType("DEIGO");
            networkVo.setId(1);
            networkVo.setNet("internal");
            networkVo.setNetworkName("netName");
            networkVo.setPublicStaticIP("113.123.123.123");
            networkVo.setSeq(0);
            networkVo.setSubnetDns("8.8.8.8");
            networkVo.setSubnetRange("192.168.0.0/24");
            networkVo.setSubnetReservedFrom("192.168.0.1");
            networkVo.setSubnetReservedTo("192.168.0.155");
            networkVo.setSubnetStaticFrom("192.168.155");
            networkVo.setSubnetStaticTo("192.168.0.255");
            networkVo.setSubnetGateway("192.168.0.1");
            networkVo.setUpdateUserId("admin");
            list.add(networkVo);
            
            networkVo = new NetworkVO();
            networkVo.setAvailabilityZone("west-1");
            networkVo.setCloudSecurityGroups("seg");
            networkVo.setCreateUserId("admin");
            networkVo.setDeployType("DEIGO");
            networkVo.setId(1);
            networkVo.setNet("internal");
            networkVo.setNetworkName("netName");
            networkVo.setPublicStaticIP("113.123.123.123");
            networkVo.setSeq(0);
            networkVo.setSubnetDns("8.8.8.8");
            networkVo.setSubnetRange("192.168.0.0/24");
            networkVo.setSubnetReservedFrom("192.168.0.1");
            networkVo.setSubnetReservedTo("192.168.0.155");
            networkVo.setSubnetStaticFrom("192.168.155");
            networkVo.setSubnetStaticTo("192.168.0.255");
            networkVo.setSubnetGateway("192.168.0.1");
            networkVo.setUpdateUserId("admin");
            list.add(networkVo);
        }
        return list;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 리소스 정보 설정
    * @title : setResourceInfo
    * @return : ResourceVO
    ***************************************************/
    public ResourceVO setResourceInfo(){
        ResourceVO resourceVO = new ResourceVO();
        resourceVO.setBoshPassword("bosh");
        resourceVO.setCreateUserId("admin");
        resourceVO.setDeployType("openstack");
        resourceVO.setId(1);
        resourceVO.setLargeCpu(10);
        resourceVO.setLargeDisk(1000);
        resourceVO.setLargeFlavor("m1.large");
        resourceVO.setLargeRam(1000);
        resourceVO.setMediumCpu(5);
        resourceVO.setMediumDisk(500);
        resourceVO.setMediumFlavor("m1.medium");
        resourceVO.setMediumRam(500);
        resourceVO.setRunnerCpu(1500);
        resourceVO.setRunnerDisk(1500);
        resourceVO.setRunnerFlavor("m1.xlarge");
        resourceVO.setRunnerRam(1500);
        resourceVO.setSmallFlavor("m1.small");
        resourceVO.setSmallCpu(1);
        resourceVO.setSmallRam(1000);
        resourceVO.setSmallDisk(100);
        resourceVO.setStemcellName("stemcell");
        resourceVO.setStemcellVersion("3417");
        resourceVO.setUpdateUserId("adimn");
        return resourceVO;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : DIEGO 상세 정보 조회 결과 값 설정 
    * @title : setResultDiegoInfo
    * @return : DiegoVO
    ***************************************************/
    public List<DiegoVO>  setResultDiegoInfo() {
        List<DiegoVO> diegoList = new ArrayList<DiegoVO>();
        DiegoVO vo = new DiegoVO();
        vo.setCadvisorDriverIp("10.0.0.6");
        vo.setCadvisorDriverPort("9033");
        vo.setCfDeployment("cf.yml");
        vo.setCfId(1);
        vo.setCflinuxfs2rootfsreleaseName("cflinux");
        vo.setCflinuxfs2rootfsreleaseVersion("1.150.1");
        vo.setCfName("cf-aws");
        vo.setCfReleaseName("cf");
        vo.setCfReleaseVersion("272");
        vo.setCreateUserId("admin");
        vo.setDeploymentFile("aws-diego.yml");
        vo.setDeploymentName("aws-diego");
        vo.setDeployStatus("deploy");
        vo.setDiegoReleaseName("diego");
        vo.setDiegoReleaseVersion("1.25.3");
        vo.setDirectorUuid("uuid");
        vo.setEtcdReleaseName("etcd");
        vo.setEtcdReleaseVersion("104");
        vo.setGardenReleaseName("garden");
        vo.setGardenReleaseVersion("153");
        vo.setIaasType("aws");
        vo.setId(1);
        vo.setKeyFile("key.yml");
        vo.setPaastaMonitoringUse("true");
        vo.setTaskId(1);
        vo.setUpdateUserId("admin");
        List<NetworkVO> list = new ArrayList<NetworkVO>();
        NetworkVO networkVo = new NetworkVO();
        networkVo.setAvailabilityZone("west-1");
        networkVo.setCloudSecurityGroups("seg");
        networkVo.setCreateUserId("admin");
        networkVo.setDeployType("DEIGO");
        networkVo.setId(1);
        networkVo.setNet("netId");
        networkVo.setNetworkName("netName");
        networkVo.setPublicStaticIP("113.123.123.123");
        networkVo.setSeq(0);
        networkVo.setSubnetDns("8.8.8.8");
        networkVo.setSubnetRange("192.168.0.0/24");
        networkVo.setSubnetReservedFrom("192.168.0.1");
        networkVo.setSubnetReservedTo("192.168.0.155");
        networkVo.setSubnetStaticFrom("192.168.155");
        networkVo.setSubnetStaticTo("192.168.0.255");
        networkVo.setSubnetGateway("192.168.0.1");
        networkVo.setUpdateUserId("admin");
        
        ResourceVO resourceVO = new ResourceVO();
        resourceVO.setBoshPassword("bosh");
        resourceVO.setCreateUserId("admin");
        resourceVO.setDeployType("openstack");
        resourceVO.setId(1);
        resourceVO.setLargeCpu(10);
        resourceVO.setLargeDisk(1000);
        resourceVO.setLargeFlavor("m1.large");
        resourceVO.setLargeRam(1000);
        resourceVO.setMediumCpu(5);
        resourceVO.setMediumDisk(500);
        resourceVO.setMediumFlavor("m1.medium");
        resourceVO.setMediumRam(500);
        resourceVO.setRunnerCpu(1500);
        resourceVO.setRunnerDisk(1500);
        resourceVO.setRunnerFlavor("m1.xlarge");
        resourceVO.setRunnerRam(1500);
        resourceVO.setSmallFlavor("m1.small");
        resourceVO.setSmallCpu(1);
        resourceVO.setSmallRam(1000);
        resourceVO.setSmallDisk(100);
        resourceVO.setStemcellName("stemcell");
        resourceVO.setStemcellVersion("3417");
        resourceVO.setUpdateUserId("adimn");
        
        resourceVO.getLargeCpu();
        resourceVO.getLargeDisk();
        resourceVO.getLargeFlavor();
        resourceVO.getLargeRam();
        resourceVO.getMediumCpu();
        resourceVO.getMediumDisk();
        resourceVO.getMediumFlavor();
        resourceVO.getMediumRam();
        resourceVO.getRunnerCpu();
        resourceVO.getRunnerDisk();
        resourceVO.getRunnerFlavor();
        resourceVO.getRunnerRam();
        resourceVO.getSmallFlavor();
        resourceVO.getSmallCpu();
        resourceVO.getSmallRam();
        resourceVO.getSmallDisk();
        resourceVO.getStemcellName();
        resourceVO.getStemcellVersion();
        resourceVO.getUpdateUserId();
        vo.setResource(resourceVO);
        
        vo.setNetwork(networkVo);
        list.add(networkVo);
        vo.setNetworks(list);
        diegoList.add(vo);
        return diegoList;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Diego 목록 조회 값 설정
    * @title : setDiegoInfoList
    * @return : List<DiegoListDTO>
    ***************************************************/
    public List<DiegoListDTO> setDiegoInfoList() {
        List<DiegoListDTO> list = new ArrayList<DiegoListDTO>();
        DiegoListDTO dto =  new DiegoListDTO();
        dto.setAvailabilityZone("west-u1");
        dto.setBoshPassword("password");
        dto.setCfDeployment("cf.yml");
        dto.setCfId(1);
        dto.setCflinuxfs2rootfsreleaseName("cflinux");
        dto.setCflinuxfs2rootfsreleaseVersion("142");
        dto.setCloudSecurityGroups("seg");
        dto.setDeploymentFile("diego.yml");
        dto.setDeploymentName("diego");
        dto.setDeployStatus("deploy");
        dto.setDiegoReleaseName("diego");
        dto.setDiegoReleaseVersion("1.25.1");
        dto.setDirectorUuid("uuid");
        dto.setEtcdReleaseName("Etcd");
        dto.setEtcdReleaseVersion("104");
        dto.setGardenReleaseName("garden");
        dto.setGardenReleaseVersion("222");
        dto.setIaas("aws");
        dto.setId(1);
        dto.setKeyFile("key.yml");
        dto.setPublicStaticIp("172.16.100.1");
        dto.setRecid(1);
        dto.setStemcellName("aws-stemcell");
        dto.setStemcellVersion("3445.1");
        dto.setSubnetDns("8.8.8.8");
        dto.setSubnetId("sub1");
        dto.setSubnetGateway("192.168.1.1");
        dto.setSubnetRange("192.168.1.0/24");
        dto.setSubnetReservedIp("192.168.1.1");
        dto.setSubnetStaticIp("192.168.1.255");
        dto.getAvailabilityZone();
        dto.getBoshPassword();
        dto.getCfDeployment();
        dto.getCfId();
        dto.getCflinuxfs2rootfsreleaseName();
        dto.getCflinuxfs2rootfsreleaseVersion();
        dto.getCloudSecurityGroups();
        dto.getDeploymentFile();
        dto.getDeploymentName();
        dto.getDeployStatus();
        dto.getDiegoReleaseName();
        dto.getDiegoReleaseVersion();
        dto.getDirectorUuid();
        dto.getEtcdReleaseName();
        dto.getEtcdReleaseVersion();
        dto.getGardenReleaseName();
        dto.getGardenReleaseVersion();
        dto.getIaas();
        dto.getId();
        dto.getKeyFile();
        dto.getPublicStaticIp();
        dto.getRecid();
        dto.getStemcellName();
        dto.getStemcellVersion();
        dto.getSubnetDns();
        dto.getSubnetId();
        dto.getSubnetGateway();
        dto.getSubnetRange();
        dto.getSubnetReservedIp();
        dto.getSubnetStaticIp();
        list.add(dto);
        return list;
    }
}
