package org.openpaas.ieda.iaasDashboard.awsMgnt.web.elasticIp.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.elasticIp.dao.AwsElasticIpMgntVO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.amazonaws.regions.Region;
import com.amazonaws.services.ec2.model.Address;
import com.amazonaws.services.ec2.model.DescribeNetworkInterfacesRequest;
import com.amazonaws.services.ec2.model.NetworkInterface;

@Service
public class AwsElasticIpMgntService {
    @Autowired AwsElasticIpMgntApiService awsElasticIpMgntApiService;
    @Autowired CommonIaasService commonIaasService;
    @Autowired MessageSource message;
    
    /***************************************************
    * @project : AWS 인프라 관리 대시보드
    * @description : AWS ElasticIp 목록 조회
    * @title : getAwsElasticIpInfoList
    * @return : List<AwsElasticIpMgntVO>
    ***************************************************/
    public List<AwsElasticIpMgntVO> getAwsElasticIpInfoList(Principal principal, int accountId, String regionName) {
        IaasAccountMgntVO vo =  getAwsAccountInfo(principal, accountId);
        Region region = getAwsRegionInfo(regionName);
        List<Address> apiAwsAddressList = awsElasticIpMgntApiService.getAwsElasticIpInfoListApiFromAws(vo, region);
        List<AwsElasticIpMgntVO> awsAddressList = new ArrayList<AwsElasticIpMgntVO>();
        
        for ( int i=0; i<apiAwsAddressList.size(); i++ ){
          Address address = apiAwsAddressList.get(i);
          AwsElasticIpMgntVO awsElasticIpVo = new AwsElasticIpMgntVO();
          awsElasticIpVo.setPublicIp(address.getPublicIp());
          awsElasticIpVo.setAllocationId(address.getAllocationId());
          awsElasticIpVo.setDomain(address.getDomain());
          awsElasticIpVo.setRecid(i);
          awsElasticIpVo.setAccountId(accountId);
          awsAddressList.add(awsElasticIpVo);
        }
        return awsAddressList;
    }
    
    
    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS Elastic IP 정보 상세 조회 정보 설정 
     * @title : getAwsElasticIpDetailInfo
     * @return : HashMap<String, Object> 
     ***************************************************/
     @SuppressWarnings("unchecked")
    public HashMap<String, Object> getAwsElasticIpDetailInfo(int accountId, String publicIp, Principal principal, String regionName) {
         IaasAccountMgntVO vo =  getAwsAccountInfo(principal, accountId);
         Region region = getAwsRegionInfo(regionName);
         HashMap<String, Object> result = awsElasticIpMgntApiService.getAwsElasticIpDetailInfoFromAws(vo, region);
         
         List<Address> addressList = (List<Address>) result.get("addressList");
         HashMap<String, Object> apiAwsAddressInfo = new HashMap<String, Object>();
         for( int i=0; i<addressList.size(); i++ ){
             if( addressList.get(i).getPublicIp().equals( publicIp ) ){
                 Address address = addressList.get(i);
                 apiAwsAddressInfo.put("publicIp", address.getPublicIp());
                 apiAwsAddressInfo.put("allocationId", address.getAllocationId());
                 apiAwsAddressInfo.put("domain", address.getDomain());
                 apiAwsAddressInfo.put("instanceId", address.getInstanceId());
                 apiAwsAddressInfo.put("privateIpAddress", address.getPrivateIpAddress());
                 apiAwsAddressInfo.put("associationId", address.getAssociationId());
                 apiAwsAddressInfo.put("networkInterfaceId", address.getNetworkInterfaceId());
                 apiAwsAddressInfo.put("networkInterfaceOwner", address.getNetworkInterfaceOwnerId());
                 //publicDNS 불러오기
                 String networkInterfaceId = addressList.get(i).getNetworkInterfaceId();
                 if( ! StringUtils.isEmpty(networkInterfaceId) ){
                     DescribeNetworkInterfacesRequest request = new DescribeNetworkInterfacesRequest();
                     request.withNetworkInterfaceIds(networkInterfaceId);
                     List<NetworkInterface> interfaces = awsElasticIpMgntApiService.getNetworkInterfaces(vo, request, region);
                     String publicDns = "";
                     for( int j=0; j<interfaces.size(); j++ ){
                         if( interfaces.size() == j+1 ){
                             publicDns += interfaces.get(j).getAssociation().getPublicDnsName();
                         }else{
                             publicDns += interfaces.get(j).getAssociation().getPublicDnsName()+ ",";
                         }
                     }
                     apiAwsAddressInfo.put("publicDns", publicDns);
                 }else{
                     apiAwsAddressInfo.put("publicDns", "-");
                 }
             }
         }
         apiAwsAddressInfo.put("publicIp", publicIp);
           
         return apiAwsAddressInfo;
     }
    
     
     /***************************************************
      * @project : AWS 인프라 관리 대시보드
      * @description : AWS Elastic IP 할당 
      * @title : allocateElasticIp
      * @return : String
      ***************************************************/
     public String allocateElasticIp(Principal principal, int accountId, String regionName){
         
         IaasAccountMgntVO vo =  getAwsAccountInfo(principal, accountId);
         Region region = getAwsRegionInfo(regionName);
         try{
             String elasticIp = awsElasticIpMgntApiService.allocateElasticIpFromAws(vo, region);
             return elasticIp;
             
         }catch (Exception e) {
             String detailMessage = e.getMessage();
             if(!detailMessage.equals("") && detailMessage != null){
                 throw new CommonException(
                   detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
             }else{
                 throw new CommonException(
                         message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
             }
         }
        
     }
     
    
    /***************************************************
    * @project : AWS 인프라 관리 대시보드
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
