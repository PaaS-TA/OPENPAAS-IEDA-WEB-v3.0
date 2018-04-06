package org.openpaas.ieda.awsMgnt.web.natGateway.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.openpaas.ieda.awsMgnt.web.natGateway.dao.AwsNatGatewayMgntVO;
import org.openpaas.ieda.awsMgnt.web.natGateway.dto.AwsNatGatewayMgntDTO;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.amazonaws.regions.Region;
import com.amazonaws.services.ec2.model.Address;
import com.amazonaws.services.ec2.model.NatGateway;
import com.amazonaws.services.ec2.model.Subnet;
@Service
public class AwsNatGatewayMgntService {
    @Autowired AwsNatGatewayMgntApiService awsNatGatewayMgntApiService;
    @Autowired CommonIaasService commonIaasService;
    @Autowired MessageSource message;
    
    /***************************************************
    * @project : AWS 인프라 관리 대시보드
    * @description : AWS NAT Gateway 목록 조회
    * @title : getAwsNatGatewayInfoList
    * @return : List<AwsNatGatewayMgntVO>
    ***************************************************/
    public List<AwsNatGatewayMgntVO> getAwsNatGatewayInfoList( int accountId, String regionName, Principal principal ) {
        IaasAccountMgntVO vo =  getAwsAccountInfo(principal, accountId);
        Region region = getAwsRegionInfo(regionName);
        List<NatGateway> apiAwsNatGwList = awsNatGatewayMgntApiService.getAwsNatGatewayInfoListApiFromAws(vo, region.getName());
        List<AwsNatGatewayMgntVO> awsNatGatewayList = new ArrayList<AwsNatGatewayMgntVO>();
        if(apiAwsNatGwList !=null && apiAwsNatGwList.size()!= 0){
            for ( int i=0; i<apiAwsNatGwList.size(); i++ ){
                NatGateway natGateway = apiAwsNatGwList.get(i);
                AwsNatGatewayMgntVO awsNatGatewayVO = new AwsNatGatewayMgntVO();
                awsNatGatewayVO.setNatGatewayId(natGateway.getNatGatewayId());
                awsNatGatewayVO.setState(natGateway.getState());
                if(natGateway.getNatGatewayAddresses().get(0).getPublicIp() !=null){
                awsNatGatewayVO.setPublicIp(natGateway.getNatGatewayAddresses().get(0).getPublicIp());
                }else{
                    awsNatGatewayVO.setPublicIp(" - ");
                }
                awsNatGatewayVO.setPrivateIp(natGateway.getNatGatewayAddresses().get(0).getPrivateIp());
                awsNatGatewayVO.setNetworkInterfaceId(natGateway.getNatGatewayAddresses().get(0).getNetworkInterfaceId());
                awsNatGatewayVO.setAllocationId(natGateway.getNatGatewayAddresses().get(0).getAllocationId());
                awsNatGatewayVO.setCreatedTime(natGateway.getCreateTime().toString());
                awsNatGatewayVO.setSubnetId(natGateway.getSubnetId().toString());
                awsNatGatewayVO.setVpcId(natGateway.getVpcId().toString());
                awsNatGatewayVO.setRecid(i);
                awsNatGatewayVO.setAccountId(accountId);
                awsNatGatewayList.add(awsNatGatewayVO);
            }
        }
        return awsNatGatewayList;
    }
    
    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS Subnet 목록 조회
     * @title : getAwsSubnetList
     * @return : List<AwsNatGatewayMgntVO>
     ***************************************************/
    public List<AwsNatGatewayMgntVO> getAwsSubnetList ( int accountId, String regionName, Principal principal ) {
    IaasAccountMgntVO vo =  getAwsAccountInfo(principal, accountId);
    Region region = getAwsRegionInfo(regionName);
    List<Subnet> apiAwsSubnetList = awsNatGatewayMgntApiService.getAwsSubnetInfoListApiFromAws(vo, region.getName());
    
    List<AwsNatGatewayMgntVO> awsSubnetList = new ArrayList<AwsNatGatewayMgntVO>();
    if(apiAwsSubnetList !=null && apiAwsSubnetList.size()!= 0){
        for ( int i=0; i<apiAwsSubnetList.size(); i++ ){
            Subnet subnet = apiAwsSubnetList.get(i);
            AwsNatGatewayMgntVO awsNatGatewayVO = new AwsNatGatewayMgntVO();
            awsNatGatewayVO.setSubnetId(subnet.getSubnetId().toString());
            awsNatGatewayVO.setVpcId(subnet.getVpcId().toString());
            if(subnet.getTags().size() != 0){
                String result = "";
                for(int j=0; j<subnet.getTags().size(); j++){
                result += subnet.getTags().get(j).getValue().toString();
                }
                awsNatGatewayVO.setNameTag(result);
            }else{
                awsNatGatewayVO.setNameTag("");
            }
            awsNatGatewayVO.setRecid(i);
            awsNatGatewayVO.setAccountId(accountId);
            awsSubnetList.add(awsNatGatewayVO);
        }
    }
    return awsSubnetList;
}

    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS EIP AllocationId 목록 조회
     * @title : getAwsEipAllocationIdList
     * @return : List<AwsNatGatewayMgntVO>
     ***************************************************/
    public List<AwsNatGatewayMgntVO> getAwsEipAllocationIdList( int accountId, String regionName, Principal principal ) {
        IaasAccountMgntVO vo =  getAwsAccountInfo(principal, accountId);
        Region region = getAwsRegionInfo(regionName);
        List<Address> apiAddressList = awsNatGatewayMgntApiService.getAwsEipAllocationIdListApiFromAws(vo, region.getName());
        List<AwsNatGatewayMgntVO> awsEipAllocationIdList = new ArrayList<AwsNatGatewayMgntVO>();
        if(apiAddressList !=null && apiAddressList.size()!= 0){
            for ( int i=0; i<apiAddressList.size(); i++ ){
                Address address = apiAddressList.get(i);
                AwsNatGatewayMgntVO awsNatGatewayVO = new AwsNatGatewayMgntVO();
               //elastic IP가 아무데도 associate되어있지 않을 경우
                if(address.getAssociationId() == null ){ 
                   awsNatGatewayVO.setPublicIp(address.getPublicIp());
                     awsNatGatewayVO.setAllocationId(address.getAllocationId());
                     awsNatGatewayVO.setRecid(i); 
                   awsNatGatewayVO.setAccountId(accountId);
                   awsEipAllocationIdList.add(awsNatGatewayVO);
                }
            }
        }
        return awsEipAllocationIdList;
    }
    
    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS Elastic IP 할당 
     * @title : allocateNewElasticIp
     * @return : String
     ***************************************************/
    public void allocateNewElasticIp(AwsNatGatewayMgntDTO dto, Principal principal){
    
        IaasAccountMgntVO vo =  getAwsAccountInfo(principal,dto.getAccountId());
        Region region = getAwsRegionInfo(dto.getRegion());
        awsNatGatewayMgntApiService.allocateNewElasticIpFromAws(vo, region);
   
    
    }
    
    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS VPC 생성
     * @title : saveAwsVpcInfo
     * @return : void
     ***************************************************/
     public void saveAwsNatGatewayInfo(AwsNatGatewayMgntDTO dto, Principal principal) {
         IaasAccountMgntVO vo =  getAwsAccountInfo(principal, dto.getAccountId());
         Region region = getAwsRegionInfo(dto.getRegion());
         try{
             awsNatGatewayMgntApiService.createAwsNatGatewayApiFromAws(vo, region.getName(),dto);
         }catch (Exception e) {
             throw new CommonException(
                     message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
         }
     }
    
    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS 계정 정보가 실제 존재 하는지 확인 및 상세 조회
     * @title : getAwsAccountInfo
     * @return : IaasAccountMgntVO
     ***************************************************/
     public IaasAccountMgntVO getAwsAccountInfo(Principal principal, int accountId){
         return commonIaasService.getIaaSAccountInfo(principal, accountId, "aws");
     }
     
     /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS 리전 명 조회
     * @title : getAwsRegionInfo
     * @return : Region
     ***************************************************/
     public Region getAwsRegionInfo(String regionName) {
         return commonIaasService.getAwsRegionInfo(regionName);
     }
     
    
}
