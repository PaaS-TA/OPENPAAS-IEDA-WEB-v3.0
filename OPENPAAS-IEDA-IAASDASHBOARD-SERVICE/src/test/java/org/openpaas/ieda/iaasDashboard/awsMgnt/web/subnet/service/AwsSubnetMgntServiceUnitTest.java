package org.openpaas.ieda.iaasDashboard.awsMgnt.web.subnet.service;

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
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.subnet.dao.AwsSubnetMgntVO;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.subnet.dto.AwsSubnetMgntDTO;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.subnet.service.AwsSubnetMgntApiService;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.subnet.service.AwsSubnetMgntService;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.springframework.context.MessageSource;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.model.NetworkAcl;
import com.amazonaws.services.ec2.model.RouteTable;
import com.amazonaws.services.ec2.model.Subnet;
import com.amazonaws.services.ec2.model.SubnetIpv6CidrBlockAssociation;

public class AwsSubnetMgntServiceUnitTest extends BaseAwsMgntControllerUnitTest{
    
    private Principal principal = null;
    
    @InjectMocks AwsSubnetMgntService mockAwsSubnetMgntService;
    @Mock AwsSubnetMgntApiService mockAwsSubnetMgntApiService;
    @Mock CommonIaasService mockCommonIaasService;
    @Mock MessageSource mockMessageSource;
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        principal = getLoggined();
    }
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : AWS Subnet 목록 조회 TEST
     * @title : testGetAwsSubnetcInfoList
     * @return : void
     ***************************************************/
     @Test
     public void testGetAwsSubnetcInfoList(){
         getAwsAccountInfo();
         getAwsRegionInfo();
         List<Subnet>  subnetList = getResultSubnetListInfo();
         
         when(mockAwsSubnetMgntApiService.getAwsSubnetInfoListApiFromAws(any(), any())).thenReturn(subnetList);
         List<AwsSubnetMgntVO> resultList = mockAwsSubnetMgntService.getAwsSubnetInfoList(principal, 1, "region");
         assertEquals(resultList.get(0).getState(), subnetList.get(0).getState());
         assertEquals(resultList.get(0).getVpcId(), subnetList.get(0).getVpcId());
         assertEquals(resultList.get(0).getCidrBlock(), subnetList.get(0).getCidrBlock());
         assertEquals(resultList.get(0).getIpv6CidrBlock(), "10.0.0.21,10.0.0.1");
         assertEquals(resultList.get(0).getAvailabilityZone(), subnetList.get(0).getAvailabilityZone());
         assertEquals(resultList.get(0).isDefaultForAz(), subnetList.get(0).isDefaultForAz());
         assertEquals(resultList.get(0).isMapPublicIpOnLaunch(), subnetList.get(0).isMapPublicIpOnLaunch());
         assertEquals(resultList.size(), 1);
     }
      
  /***************************************************
   * @project : AWS 관리 대시보드
   * @description : AWS Subnet 상세 정보 조회 TEST
   * @title : testDefaultGetAwsSubnetDetailInfo
   * @return : void
   ***************************************************/
   @SuppressWarnings("unchecked")
   @Test
   public void testDefaultGetAwsSubnetDetailInfo(){
       getAwsAccountInfo();
       getAwsRegionInfo();
       HashMap<String, Object> subnetMap = getResultSubnetDetailInfo();
       when(mockAwsSubnetMgntApiService.getAwsSubnetDetailInfoFromAws(any(),anyString(), any())).thenReturn(subnetMap);
       HashMap<String, Object> resultMap = mockAwsSubnetMgntService.getAwsSubnetDetailInfo(1, "subnet-33626875", principal, "region");
       
       List<Subnet> subnets = (List<Subnet>) subnetMap.get("subnets");
       assertEquals(resultMap.get("cidrBlock"), subnets.get(0).getCidrBlock());
       assertEquals(resultMap.get("state"), subnets.get(0).getState());
       assertEquals(resultMap.get("availabilityZone"), subnets.get(0).getAvailabilityZone());
       assertEquals(resultMap.get("availableIpAddressCount"), subnets.get(0).getAvailableIpAddressCount());
       assertEquals(resultMap.get("defaultSubnet"), subnets.get(0).getDefaultForAz());
       assertEquals(resultMap.get("autoAssignPublicIp"), subnets.get(0).getMapPublicIpOnLaunch());
       assertEquals(resultMap.get("assignIpv6AddressOnCreation"), subnets.get(0).getAssignIpv6AddressOnCreation());
       List<NetworkAcl> networkAcl = getSubnetNetworkAclListInfo();
       assertEquals(resultMap.get("networkAcl"), networkAcl.get(0).getNetworkAclId());
       List<RouteTable> routeTable = getSubnetRouteTableListInfo();
       assertEquals(resultMap.get("routeTable"), routeTable.get(0).getRouteTableId());
   }
       
   /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : AWS Subnet 생성 TEST
    * @title : testSaveAwsSubnetInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveAwsSubnetInfo(){
        getAwsAccountInfo();
        getAwsRegionInfo();
        AwsSubnetMgntDTO dto = setAwsSubnetInfo();
        mockAwsSubnetMgntService.saveAwsSubnetInfo(dto, principal);
    }

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : AWS Subnet 삭제 TEST
     * @title : AWS Subnet 삭제 TEST
     * @return : void
     ***************************************************/
     @Test
     public void testDeleteAwsSubnetInfo(){
         getAwsAccountInfo();
         getAwsRegionInfo();
         AwsSubnetMgntDTO dto = setAwsSubnetInfo();
         mockAwsSubnetMgntService.deleteAwsSubnetInfo(dto, principal);
     }
        
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : AWS Subnet 정보 설정
     * @title : setAwsSubnetInfo
     * @return : AwsSubnetMgntDTO
     ***************************************************/
       private AwsSubnetMgntDTO setAwsSubnetInfo() {
           AwsSubnetMgntDTO dto = new AwsSubnetMgntDTO();
           dto.setAccountId(1);
           dto.setVpcId("vpcId");
           dto.setCidrBlock("172.31.0.0/20");
           dto.setAvailabilityZone("us-west-2c");
           dto.getAccountId();
           dto.getVpcId();
           dto.getCidrBlock();
           dto.getAvailabilityZone();
           return dto;
    }

    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : AWS API Subnet 상세 정보 결과 값 설정
    * @title : getResultSubnetDetailInfo
    * @return : HashMap<String, Object>
    ***************************************************/
    private HashMap<String, Object> getResultSubnetDetailInfo() {
       HashMap<String, Object> detailInfo = new HashMap<String, Object>();
       detailInfo.put("subnets", getResultSubnetListInfo());
       
       List<RouteTable> routeTables = getSubnetRouteTableListInfo();
       List<NetworkAcl> networkAcls =getSubnetNetworkAclListInfo();
       
       detailInfo.put("routeTables", routeTables);
       detailInfo.put("networkAcls", networkAcls);
       
       return detailInfo;
    }

    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : AWS API Subnet 목록 조회 결과 값 설정
    * @title : getResultSubnetListInfo
    * @return : List<AwsSubnetMgntVO>
    ***************************************************/
    private List<Subnet>  getResultSubnetListInfo() {
        List<Subnet> subnetList = new ArrayList<Subnet>();
        Subnet subnet = new Subnet();
        subnet.setSubnetId("subnet-33626875");
        subnet.setState("available");
        subnet.setVpcId("vpc-132cd476");
        subnet.setCidrBlock("172.31.0.0/20");
        subnet.setAvailableIpAddressCount(4091);
        subnet.setAvailabilityZone("us-west-2c");
        subnet.setDefaultForAz(false);
        subnet.setMapPublicIpOnLaunch(false);
        subnet.setAssignIpv6AddressOnCreation(false);
        
        Subnet subnetgetIp6CidrBlockInfo = getIp6CidrBlockInfo(subnet);
        subnet.setIpv6CidrBlockAssociationSet(subnetgetIp6CidrBlockInfo.getIpv6CidrBlockAssociationSet());
        
        subnetList.add(subnet);
        return subnetList;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Ip6CidrBlockInfo 설정
     * @title : getIp6CidrBlockInfo
     * @return : Subnet
    *****************************************************************/
    public Subnet getIp6CidrBlockInfo(Subnet subnet){
        
        List<SubnetIpv6CidrBlockAssociation> ipv6CidrBlockAssociationList = new ArrayList<SubnetIpv6CidrBlockAssociation>();
        SubnetIpv6CidrBlockAssociation SubnetIpv6CidrBlockAssociation_01 = new SubnetIpv6CidrBlockAssociation();
        SubnetIpv6CidrBlockAssociation_01.setIpv6CidrBlock("10.0.0.1");
        ipv6CidrBlockAssociationList.add(0, SubnetIpv6CidrBlockAssociation_01);
        
        SubnetIpv6CidrBlockAssociation SubnetIpv6CidrBlockAssociation_02 = new SubnetIpv6CidrBlockAssociation();
        SubnetIpv6CidrBlockAssociation_02.setIpv6CidrBlock("10.0.0.21");
        ipv6CidrBlockAssociationList.add(0, SubnetIpv6CidrBlockAssociation_02);
        
        
        subnet.setIpv6CidrBlockAssociationSet(ipv6CidrBlockAssociationList);
        
        return subnet;
    }

      /***************************************************
       * @project : AWS 관리 대시보드
       * @description : AWS API Subnet Network ACL목록 조회 결과 값 설정
       * @title : getResultSubnetListInfo
       * @return : List<NetworkAcl>
       ***************************************************/
       private List<NetworkAcl> getSubnetNetworkAclListInfo() {
           List<NetworkAcl> networkAcls =new ArrayList<NetworkAcl>();
           NetworkAcl networkAcl = new NetworkAcl();
           networkAcl.setNetworkAclId("rtb-819753e4"); 
           networkAcl.setVpcId("vpc-132cd476");
           networkAcls.add(0, networkAcl);
           
           return networkAcls;
       }
       
   /***************************************************
    * @project : AWS 관리 대시보드
    * @description : AWS API Subnet RouteTable목록 조회 결과 값 설정
    * @title : getSubnetRouteTableListInfo
    * @return : List<RouteTable>
    ***************************************************/
    private List<RouteTable> getSubnetRouteTableListInfo() {
        List<RouteTable> routeTables = new ArrayList<RouteTable>();
        RouteTable routetable = new RouteTable();
        routetable.setRouteTableId("acl-19946e7c");
        routetable.setVpcId("vpc-132cd476");
        routeTables.add(0, routetable);
        return routeTables;
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
       * @description : AWS Region 조회 정보 결과 값 설정
       * @title : getAwsAccountInfo
       * @return : IaasAccountMgntVO
       ***************************************************/
       public void getAwsRegionInfo(){
           when(mockCommonIaasService.getAwsRegionInfo(any())).thenReturn(Region.getRegion(Regions.fromName("us-west-2")));
       }
    
}