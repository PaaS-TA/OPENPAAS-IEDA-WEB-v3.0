package org.openpaas.ieda.awsMgnt.web.subnet.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.awsMgnt.web.subnet.dao.AwsSubnetMgntVO;
import org.openpaas.ieda.awsMgnt.web.subnet.dto.AwsSubnetMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.regions.Region;
import com.amazonaws.services.ec2.model.NetworkAcl;
import com.amazonaws.services.ec2.model.RouteTable;
import com.amazonaws.services.ec2.model.Subnet;

@Service
public class AwsSubnetMgntService {

    @Autowired AwsSubnetMgntApiService awsSubnetMgntApiService;
    @Autowired CommonIaasService commonIaasService;
    
    /***************************************************
    * @param region 
     * @project : 인프라 관리 대시보드
    * @description : Subnet 목록 조회
    * @title : getAwsSubnetInfoList
    * @return : List<AwsSubnetMgntVO>
    ***************************************************/
    public List<AwsSubnetMgntVO> getAwsSubnetInfoList(Principal principal, int accountId, String regionName) {
        IaasAccountMgntVO vo =  getAwsAccountInfo(principal, accountId);
        Region region = getAwsRegionInfo(regionName);
        List<Subnet> awsSubnetList = awsSubnetMgntApiService.getAwsSubnetInfoListApiFromAws(vo, region);
        
        List<AwsSubnetMgntVO> list = new ArrayList<AwsSubnetMgntVO>();
        for (int i=0; i<awsSubnetList.size(); i++ ){
            Subnet subnet = awsSubnetList.get(i);
            AwsSubnetMgntVO awsSubnetVo = new AwsSubnetMgntVO();
            awsSubnetVo.setSubnetId(subnet.getSubnetId());
            awsSubnetVo.setState(subnet.getState());
            awsSubnetVo.setVpcId(subnet.getVpcId());
            awsSubnetVo.setCidrBlock(subnet.getCidrBlock());
            awsSubnetVo.setAvailabilityZone(subnet.getAvailabilityZone());
            awsSubnetVo.setDefaultForAz(subnet.isDefaultForAz());
            awsSubnetVo.setRecid(i);
            awsSubnetVo.setAccountId(vo.getId());
            String ipv6CidrBlock = "";
            if( subnet.getIpv6CidrBlockAssociationSet().size() != 0 ){
                for( int j=0; j<subnet.getIpv6CidrBlockAssociationSet().size(); j++ ){
                    ipv6CidrBlock  += subnet.getIpv6CidrBlockAssociationSet().get(j).getIpv6CidrBlock();
                    if( subnet.getIpv6CidrBlockAssociationSet().size()-1 > j ){
                        ipv6CidrBlock  += ",";
                    }
                }
            }
            awsSubnetVo.setIpv6CidrBlock(ipv6CidrBlock );
            list.add(awsSubnetVo);
        }
        return list;
    }
    
    /***************************************************
     * @param region 
     * @project : 인프라 관리 대시보드
     * @description : Subnet 상세 정보 조회
     * @title : getAwsSubnetDetailInfo
     * @return : AwsSubnetMgntVO
     ***************************************************/
    @SuppressWarnings("unchecked")
    public HashMap<String, Object> getAwsSubnetDetailInfo(int accountId, String subnetId, Principal principal, String regionName) {
        IaasAccountMgntVO vo =  getAwsAccountInfo(principal, accountId);
        Region region = getAwsRegionInfo(regionName);
        HashMap<String, Object> detailMap = awsSubnetMgntApiService.getAwsSubnetDetailInfoFromAws(vo, subnetId, region);
        HashMap<String, Object> map = new HashMap<String, Object>();
        
        List<Subnet> subnetList = (List<Subnet>) detailMap.get("subnets");
        for(int i=0; i<subnetList.size(); i++){
            Subnet subnet = subnetList.get(i);
            if(subnet.getSubnetId().equals(subnetId)){
                map.put("subnetId", subnetId);
                map.put("state", subnet.getState());
                map.put("cidrBlock", subnet.getCidrBlock());
                map.put("availabilityZone", subnet.getAvailabilityZone());
                map.put("vpcId", subnet.getVpcId());
                map.put("defaultSubnet", subnet.getDefaultForAz());
                map.put("autoAssignPublicIp", subnet.getMapPublicIpOnLaunch());
                map.put("assignIpv6AddressOnCreation", subnet.getAssignIpv6AddressOnCreation());
                map.put("availableIpAddressCount", subnet.getAvailableIpAddressCount());
                map.put("ipv6CidrBlock", getIpv6CidrBlock(subnet) );
                String vpcId = subnet.getVpcId().toString();
            
                List<RouteTable> routeTables = (List<RouteTable>) detailMap.get("routeTables");
                List<NetworkAcl> networkAcls = (List<NetworkAcl>) detailMap.get("networkAcls");
                for( int j=0; j < routeTables.size(); j++ ){
                    if(routeTables.get(j).getVpcId().toString().equals(vpcId)){
                        map.put("routeTable", routeTables.get(i).getRouteTableId());
                   }
                }
                for( int j=0; j < networkAcls.size(); j++ ){
                    if(networkAcls.get(j).getVpcId().toString().equals(vpcId)){
                        map.put("networkAcl", networkAcls.get(i).getNetworkAclId());
                   }
                }
            }
        }
       return map;
    }
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : ipv6CidrBlock 정보 설정
     * @title : getIpv6CidrBlock
     * @return : String
    *****************************************************************/
    public String getIpv6CidrBlock(Subnet subnet){
        String ipv6CidrBlock = "-";
        if( subnet.getIpv6CidrBlockAssociationSet().size() != 0 ){
            for( int j=0; j<subnet.getIpv6CidrBlockAssociationSet().size(); j++ ){
                ipv6CidrBlock  += subnet.getIpv6CidrBlockAssociationSet().get(j).getIpv6CidrBlock();
                if( subnet.getIpv6CidrBlockAssociationSet().size()-1 > j ){
                    ipv6CidrBlock  += ",";
                }
            }
           
        }
        return ipv6CidrBlock;
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Subnet 생성
     * @title : saveAwsSubnetInfo
     * @return : void
     ***************************************************/    
    public void saveAwsSubnetInfo(AwsSubnetMgntDTO dto, Principal principal){
        IaasAccountMgntVO vo =  getAwsAccountInfo( principal, dto.getAccountId());
        Region region = getAwsRegionInfo(dto.getRegion());
        awsSubnetMgntApiService.saveSubnetFromAws(vo, dto, region);
    }
   
    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS Subnet 삭제
     * @title :  deleteAwsSubnetInfo
     * @return : void
     ***************************************************/
     public void deleteAwsSubnetInfo(AwsSubnetMgntDTO dto, Principal principal) {
         IaasAccountMgntVO vo =  getAwsAccountInfo(principal, dto.getAccountId());
         Region region = getAwsRegionInfo(dto.getRegion());
         awsSubnetMgntApiService.deleteSubnetInfoFromAws(vo, dto, region);
     }
     
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : AWS 계정 정보가 실제 존재 하는지 확인 및 상세 조회
     * @title : getAwsAccountInfo
     * @return : IaasAccountMgntVO
     ***************************************************/
     public IaasAccountMgntVO getAwsAccountInfo(Principal principal, int accountId){
         return commonIaasService.getIaaSAccountInfo(principal, accountId, "AWS");
     }
     
     /***************************************************
      * @project : AWS 인프라 관리 대시보드
      * @description : AWS 리전 조회
      * @title : getAwsRegionInfo
      * @return : Region
      ***************************************************/
      public Region getAwsRegionInfo(String regionName) {
          return commonIaasService.getAwsRegionInfo(regionName);
      }
}