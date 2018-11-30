package org.openpaas.ieda.iaasDashboard.awsMgnt.web.elasticIp.service;

import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.common.web.common.service.CommonApiService;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.Address;
import com.amazonaws.services.ec2.model.AllocateAddressRequest;
import com.amazonaws.services.ec2.model.AllocateAddressResult;
import com.amazonaws.services.ec2.model.DescribeAddressesResult;
import com.amazonaws.services.ec2.model.DescribeNetworkInterfacesRequest;
import com.amazonaws.services.ec2.model.DomainType;
import com.amazonaws.services.ec2.model.NetworkInterface;

@Service
public class AwsElasticIpMgntApiService {
    
    
    @Autowired
    CommonApiService commonApiService;
    
    /***************************************************
     * @param region 
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS Elastic IP 목록 조회 실제 API 호출
     * @title : getAwsElasticListInfoFromAws
     * @return : List<Address>
     ***************************************************/
    public List<Address> getAwsElasticIpInfoListApiFromAws(IaasAccountMgntVO vo, Region region) {
        AWSStaticCredentialsProvider provider = commonApiService.getAwsStaticCredentialsProvider(vo.getCommonAccessUser(), vo.getCommonAccessSecret());
        AmazonEC2Client ec2 =  (AmazonEC2Client)AmazonEC2ClientBuilder.standard().withRegion(region.getName()).withCredentials(provider).build();
        
        DescribeAddressesResult response = ec2.describeAddresses();
        List<Address> address = response.getAddresses();
        return address;
        
    }
    
    
    /***************************************************
     * @param region 
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS Elastic IP 상세 정보 조회 실제 API 호출
     * @title : getAwsElasticDetailInfoFromAws
     * @return : HashMap<String, Object>
     ***************************************************/
     public HashMap<String, Object> getAwsElasticIpDetailInfoFromAws(IaasAccountMgntVO vo, Region region) {
         AWSStaticCredentialsProvider provider = commonApiService.getAwsStaticCredentialsProvider(vo.getCommonAccessUser(), vo.getCommonAccessSecret());
         AmazonEC2Client ec2 =  (AmazonEC2Client)AmazonEC2ClientBuilder.standard().withRegion(region.getName()).withCredentials(provider).build();
         
         DescribeAddressesResult address = ec2.describeAddresses();
         List<Address> addressList = address.getAddresses();
         
         HashMap<String, Object> map = new HashMap<String, Object>();
         map.put("addressList", addressList);
         
         return map;
     }
     
     /****************************************************************
     * @param region 
     * @project : Paas 플랫폼 설치 자동화
     * @description : 네트워크 인터페이스 목록 조회
     * @title : describeNetworkInterfaces
     * @return : List<NetworkInterface>
    *****************************************************************/
    public List<NetworkInterface> getNetworkInterfaces(IaasAccountMgntVO vo, DescribeNetworkInterfacesRequest request, Region region){
         AWSStaticCredentialsProvider provider = commonApiService.getAwsStaticCredentialsProvider(vo.getCommonAccessUser(), vo.getCommonAccessSecret());
         AmazonEC2Client ec2 =  (AmazonEC2Client)AmazonEC2ClientBuilder.standard().withRegion(region.getName()).withCredentials(provider).build();
         return ec2.describeNetworkInterfaces(request).getNetworkInterfaces();
     }
    
     /***************************************************
      * @param region 
     * @project : AWS 인프라 관리 대시보드
      * @description : AWS Elastic IP 할당 실제 API 호출
      * @title : allocateElasticIpFromAws
      * @return : String
      ***************************************************/
     public String allocateElasticIpFromAws(IaasAccountMgntVO vo, Region region) {
         AWSStaticCredentialsProvider provider = commonApiService.getAwsStaticCredentialsProvider(vo.getCommonAccessUser(), vo.getCommonAccessSecret());
         AmazonEC2Client ec2 =  (AmazonEC2Client)AmazonEC2ClientBuilder.standard().withRegion(region.getName()).withCredentials(provider).build();
         
         AllocateAddressRequest allocateRequest = new AllocateAddressRequest().withDomain(DomainType.Vpc);
         AllocateAddressResult allocateResponse = ec2.allocateAddress(allocateRequest);
         String allocationId = allocateResponse.getAllocationId();
         return allocationId;
     }
   
}