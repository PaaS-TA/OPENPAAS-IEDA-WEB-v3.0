package org.openpaas.ieda.iaasDashboard.awsMgnt.web.elasticIp.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.common.base.BaseAwsMgntControllerUnitTest;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.elasticIp.dao.AwsElasticIpMgntVO;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.elasticIp.service.AwsElasticIpMgntApiService;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.elasticIp.service.AwsElasticIpMgntService;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.springframework.context.MessageSource;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.model.Address;
import com.amazonaws.services.ec2.model.NetworkInterface;
import com.amazonaws.services.ec2.model.NetworkInterfaceAssociation;
import com.amazonaws.services.opsworks.model.ElasticIp;

public class AwsElasticIpMgntServiceUnitTest extends BaseAwsMgntControllerUnitTest{
    
    private Principal principal = null;
    
    @InjectMocks AwsElasticIpMgntService mockAwsElasticIpMgntService;
    @Mock AwsElasticIpMgntApiService mockAwsElasticIpMgntApiService;
    @Mock CommonIaasService mockCommonIaasService;
    @Mock MessageSource mockMessageSource;
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception{
        MockitoAnnotations.initMocks(this);
        principal = getLoggined();
    }
    

    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : AWS Elastic IP 목록 조회 TEST
    * @title : testGetAwsElasticIpInfoList
    * @return : void
    ***************************************************/
    @Test
    public void testGetAwsElasticIpInfoList(){
        getAwsAccountInfo();
        getAwsRegionInfo();
        List<Address> elasticIpList = getResultElasticIpListInfo(null);
        elasticIpList.get(0).setNetworkInterfaceId(null);
        List<ElasticIp> elasticIpDnsList = getResultElasticIpDnsListInfo();
        
        when(mockAwsElasticIpMgntApiService.getAwsElasticIpInfoListApiFromAws(any(), any())).thenReturn(elasticIpList);
        List<AwsElasticIpMgntVO> resultList = mockAwsElasticIpMgntService.getAwsElasticIpInfoList(principal, 1, "region");
        
        assertEquals(resultList.size(), 1);
        assertEquals(resultList.get(0).getPublicIp(), elasticIpList.get(0).getPublicIp());
        assertEquals(resultList.get(0).getDomain(), elasticIpList.get(0).getDomain());
        assertEquals(resultList.get(0).getAllocationId(), elasticIpList.get(0).getAllocationId());
        assertEquals(resultList.get(0).getInstanceId(), elasticIpList.get(0).getInstanceId());
        assertEquals(resultList.get(0).getPrivateIpAddress(), elasticIpList.get(0).getPrivateIpAddress());
        assertEquals(resultList.get(0).getNetworkInterfaceId(), elasticIpList.get(0).getNetworkInterfaceId());
        assertEquals(resultList.get(0).getNetworkInterfaceOwner(), elasticIpList.get(0).getNetworkInterfaceOwnerId());
        assertEquals(resultList.get(0).getPublicDns(), elasticIpDnsList.get(0).getName());
        
    }
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : AWS Elastic IP 상세 정보 조회
     * @title : testGetAwsElasticIpDeatilInfo
     * @return : void
     ***************************************************/
     @SuppressWarnings("unchecked")
     @Test
     public void testGetAwsElasticIpDeatilInfo(){
         getAwsAccountInfo();
         getAwsRegionInfo();
         HashMap<String, Object> elastic = setElasticDetailInfo("networkInterfaceId");
         List<NetworkInterface> networks = getNetworkInterfaces();
         
         when(mockAwsElasticIpMgntApiService.getAwsElasticIpDetailInfoFromAws(any(), any())).thenReturn( elastic );
         when(mockAwsElasticIpMgntApiService.getNetworkInterfaces(any(), any(), any())).thenReturn(networks);
         
         HashMap<String, Object> resultMap = mockAwsElasticIpMgntService.getAwsElasticIpDetailInfo(1,"publicIp", principal, "region");
         List<Address> elasticIps = (List<Address>) elastic.get("addressList");
         Address elasticIpInfo = elasticIps.get(0);
         
         assertEquals(resultMap.get("publicIp"),"publicIp");
         assertEquals(resultMap.get("domain"), elasticIpInfo.getDomain());
         assertEquals(resultMap.get("allocationId"), elasticIpInfo.getAllocationId());
         assertEquals(resultMap.get("instanceId"), elasticIpInfo.getInstanceId());
         assertEquals(resultMap.get("privateIpAddress"), elasticIpInfo.getPrivateIpAddress());
         assertEquals(resultMap.get("associationId"), elasticIpInfo.getAssociationId());
         assertEquals(resultMap.get("networkInterfaceId"), elasticIpInfo.getNetworkInterfaceId());
         assertEquals(resultMap.get("networkInterfaceOwner"), elasticIpInfo.getNetworkInterfaceOwnerId());
         assertEquals(resultMap.get("publicDns"), networks.get(0).getAssociation().getPublicDnsName()+","+ networks.get(1).getAssociation().getPublicDnsName());
     }
     
     /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : AWS Elastic IP 상세 정보 조회 (DNS 케이스)
     * @title : testGetAwsElasticIpDeatilInfoPublicIpNullCase
     * @return : void
    *****************************************************************/
     @SuppressWarnings("unchecked")
     @Test
     public void testGetAwsElasticIpDeatilInfoPublicDnsNullCase(){
         getAwsAccountInfo();
         getAwsRegionInfo();
         HashMap<String, Object> elastic = setElasticDetailInfo(null);
         when(mockAwsElasticIpMgntApiService.getAwsElasticIpDetailInfoFromAws(any(), any())).thenReturn( elastic );
         HashMap<String, Object> resultMap = mockAwsElasticIpMgntService.getAwsElasticIpDetailInfo(1,"publicIp", principal, "region");
         List<Address> elasticIps = (List<Address>) elastic.get("addressList");
         Address elasticIpInfo = elasticIps.get(0);
         
         assertEquals(resultMap.get("publicIp"),"publicIp");
         assertEquals(resultMap.get("domain"), elasticIpInfo.getDomain());
         assertEquals(resultMap.get("allocationId"), elasticIpInfo.getAllocationId());
         assertEquals(resultMap.get("instanceId"), elasticIpInfo.getInstanceId());
         assertEquals(resultMap.get("privateIpAddress"), elasticIpInfo.getPrivateIpAddress());
         assertEquals(resultMap.get("associationId"), elasticIpInfo.getAssociationId());
         assertEquals(resultMap.get("networkInterfaceId"), elasticIpInfo.getNetworkInterfaceId());
         assertEquals(resultMap.get("networkInterfaceOwner"), elasticIpInfo.getNetworkInterfaceOwnerId());
         assertEquals(resultMap.get("publicDns"), "-");
     }
    
     /***************************************************
      * @project : Paas 플랫폼 설치 자동화
      * @description : AWS Elastic IP 생성 TEST
      * @title : testSaveAwsElastiIpInfo
      * @return : void
      ***************************************************/
      @Test
      public void testSaveAwsElasticIpInfo(){
          getAwsAccountInfo();
          getAwsRegionInfo();
          mockAwsElasticIpMgntService.allocateElasticIp(principal, 1, "region");
      }
     
     
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : AWS Account 조회 정보 결과 값 설정
     * @title : getAwsAccountInfo
     * @return : IaasAccountMgntVO
     ***************************************************/
     private IaasAccountMgntVO getAwsAccountInfo() {
         IaasAccountMgntVO vo = new IaasAccountMgntVO();
         vo.setAccountName("testAccountName");
         vo.setCreateUserId("admin");
         vo.setIaasType("aws");
         vo.setCommonAccessSecret("commonSecret");
         vo.setCommonAccessUser("commonUser");
         when(mockCommonIaasService.getIaaSAccountInfo(any(), anyInt(), anyString())).thenReturn(vo);
         return vo;
     }
     
     /***************************************************
      * @project : AWS 관리 대시보드
      * @description : AWS API Elastic IP 목록 조회 결과 값 설정
      * @title : getResultElasticIpListInfo
      * @return : List<Address>
      ***************************************************/
     public List<Address> getResultElasticIpListInfo(String interfaceId) {
          List<Address> elasticIpList = new ArrayList<Address>();
          Address elasticIp = new Address();
          elasticIp.setPublicIp("publicIp");
          elasticIp.setAllocationId("allocate_id");
          elasticIp.setDomain("vpc");
          elasticIp.setInstanceId(null);
          elasticIp.setPrivateIpAddress(null);
          elasticIp.setAssociationId(null);
          elasticIp.setNetworkInterfaceId(interfaceId);
          elasticIp.setNetworkInterfaceOwnerId(null);
          elasticIpList.add(elasticIp);
          
          return elasticIpList;
      }
      /***************************************************
       * @project : AWS 관리 대시보드
       * @description : AWS API Elastic IP 목록 조회 Public DNS결과 값 설정
       * @title : getResultElasticIpDnsListInfo
       * @return : List<ElasticIp>
       ***************************************************/
      public List<ElasticIp> getResultElasticIpDnsListInfo() {
          List<ElasticIp> elasticIpDnsList = new ArrayList<ElasticIp>();
          ElasticIp elasticIpDns = new ElasticIp();
          elasticIpDns.setName(null);
          elasticIpDnsList.add(elasticIpDns);
          
          return elasticIpDnsList;
      }
       
       
       /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 
     * @title : setElasticDetailInfo
     * @return : HashMap<String, Object>
    *****************************************************************/
    public HashMap<String, Object> setElasticDetailInfo(String interfaceId){
           List<Address> elasticIpList = getResultElasticIpListInfo(interfaceId);
           HashMap<String, Object> map = new HashMap<String, Object>();
           map.put("addressList", elasticIpList);
           
           return map;
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 
     * @title : getNetworkInterfaces
     * @return : List<NetworkInterface>
    *****************************************************************/
    public List<NetworkInterface> getNetworkInterfaces(){
        List<NetworkInterface> list = new ArrayList<NetworkInterface>();
        NetworkInterface interfaceInfo = new NetworkInterface();
        
        NetworkInterfaceAssociation association = new NetworkInterfaceAssociation();
        association.setPublicDnsName("publicDns1");
        interfaceInfo.setAssociation(association);
        list.add(0, interfaceInfo);
        
        NetworkInterface interfaceInfo2 = new NetworkInterface();
        NetworkInterfaceAssociation association2 = new NetworkInterfaceAssociation();
        association2.setPublicDnsName("publicDns2");
        interfaceInfo2.setAssociation(association2);
        list.add(1, interfaceInfo2);
       
        return list;
    }
    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : AWS Region 조회 정보 결과 값 설정
    * @title : getAwsAccountInfo
    * @return : IaasAccountMgntVO
    ***************************************************/
    public void getAwsRegionInfo(){
        when(mockCommonIaasService.getAwsRegionInfo(any())).thenReturn(Region.getRegion(Regions.fromName("us-west-2")));
    }
    
}