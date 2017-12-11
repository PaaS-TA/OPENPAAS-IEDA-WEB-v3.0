package org.openpaas.ieda.deploy.web.deploy.diego.service;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

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
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.web.common.base.BaseDeployControllerUnitTest;
import org.openpaas.ieda.deploy.web.config.setting.service.DirectorConfigService;
import org.openpaas.ieda.deploy.web.deploy.common.dao.network.NetworkDAO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.network.NetworkVO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.resource.ResourceDAO;
import org.openpaas.ieda.deploy.web.deploy.diego.dao.DiegoDAO;
import org.openpaas.ieda.deploy.web.deploy.diego.dao.DiegoVO;
import org.openpaas.ieda.deploy.web.deploy.diego.dto.DiegoParamDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class DiegoDeleteDeployAsyncServiceUnitTest extends BaseDeployControllerUnitTest {
    
    @InjectMocks DiegoDeleteDeployAsyncService mockDiegoDeleteDeployAsyncService;
    @Mock DiegoDAO mockDiegoDAO;
    @Mock NetworkDAO mockNetworkDAO;
    @Mock ResourceDAO mockResourceDAO;
    @Mock SimpMessagingTemplate mockSimpMessagingTemplate;
    @Mock DirectorConfigService mockDirectorConfigService;
    @Mock MessageSource mockMessageSource;
    
    private Principal principal = null;
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
    * @description : Diego 플랫폼 삭제 요청 배포 파일이 존재 하지 않을 경우
    * @title : testDeleteDeployDeploymentFileNull
    * @return : void
    ***************************************************/
    @Test(expected=CommonException.class)
    public void testDeleteDeployDeploymentFileNull(){
        DiegoParamDTO.Delete dto = setDiegoDeleteInfo();
        DiegoVO vo = setResultDiegoInfo("null");
        when(mockDiegoDAO.selectDiegoInfo(anyInt())).thenReturn(vo);
        mockDiegoDeleteDeployAsyncService.deleteDeploy(dto, "diego", principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Diego 정보 삭제
    * @title : testDeleteDiegoInfo
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteDiegoInfo(){
        DiegoVO vo = setResultDiegoInfo("null");
        when(mockMessageSource.getMessage(any(), any(), any())).thenReturn("");
        mockDiegoDeleteDeployAsyncService.deleteDiegoInfo(vo);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Diego 배포 상태 저장
    * @title : testDeleteDiegoInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveDeployStatus(){
        DiegoVO vo = setResultDiegoInfo("null");
        mockDiegoDeleteDeployAsyncService.saveDeployStatus(vo);
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
        vo.setDeploymentFile("aws-diego.yml");
        if(!type.equals("null")) {
            vo.setDeploymentName("aws-diego");
        }else {
            vo.setDeploymentFile("");
        }
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
        vo.setNetwork(networkVo);
        list.add(networkVo);
        vo.setNetworks(list);
        return vo;
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
    
}
