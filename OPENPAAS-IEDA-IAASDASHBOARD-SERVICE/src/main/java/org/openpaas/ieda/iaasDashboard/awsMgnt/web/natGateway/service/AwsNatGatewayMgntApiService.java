package org.openpaas.ieda.iaasDashboard.awsMgnt.web.natGateway.service;

import java.util.List;

import org.openpaas.ieda.common.web.common.service.CommonApiService;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.natGateway.dto.AwsNatGatewayMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.Address;
import com.amazonaws.services.ec2.model.AllocateAddressRequest;
import com.amazonaws.services.ec2.model.CreateNatGatewayRequest;
import com.amazonaws.services.ec2.model.DescribeNatGatewaysRequest;
import com.amazonaws.services.ec2.model.NatGateway;
import com.amazonaws.services.ec2.model.Subnet;
import com.amazonaws.services.ec2.model.Vpc;

@Service
public class AwsNatGatewayMgntApiService {
	@Autowired
    CommonApiService commonApiService;
	/***************************************************
	    * @project : Paas 플랫폼 설치 자동화
	    * @description : 공통 AmazonEC2Client 객체 생성 
	    * @title : getAmazonEC2Client
	    * @return : AmazonEC2Client
	    ***************************************************/
	    public AmazonEC2Client getAmazonEC2Client(IaasAccountMgntVO vo, String region){
	        AWSStaticCredentialsProvider provider = commonApiService.getAwsStaticCredentialsProvider(vo.getCommonAccessUser(), vo.getCommonAccessSecret());
	        AmazonEC2Client ec2 =  (AmazonEC2Client)AmazonEC2ClientBuilder.standard().withRegion(region).withCredentials(provider).build();
	        return ec2;
	    }
	    
	    /***************************************************
	    * @project : AWS 인프라 관리 대시보드
	    * @description : AWS VPC 목록 조회 실제 API 호출
	    * @title : getAwsVpcInfoListApiFromAws
	    * @return : List<Vpc>
	    ***************************************************/
	    public List<Vpc> getAwsVpcInfoListApiFromAws(IaasAccountMgntVO vo, String regionName) {
	        AmazonEC2Client ec2 =  getAmazonEC2Client(vo, regionName);
	        List<Vpc> vpcs = ec2.describeVpcs().getVpcs();
	        return vpcs;
	    }
	    /***************************************************
	    * @project : AWS 인프라 관리 대시보드
	    * @description : AWS NATGateway 목록 조회 실제 API 호출
	    * @title : getAwsNatGatewayInfoListApiFromAws
	    * @return : List<NATGateway>
	    ***************************************************/
	    public List<NatGateway> getAwsNatGatewayInfoListApiFromAws(IaasAccountMgntVO vo, String regionName) {
	        AmazonEC2Client ec2 =  getAmazonEC2Client(vo, regionName);
	        //List<Address> add = ec2.describeVpcs().getVpcs().get(0).get
	        DescribeNatGatewaysRequest request = new DescribeNatGatewaysRequest().withMaxResults(100);
	        List<NatGateway> natGateways = ec2.describeNatGateways(request).getNatGateways();
	        return natGateways;
	    }
	    
	    /***************************************************
	    * @project : AWS 인프라 관리 대시보드
	    * @description : AWS Subnet Info 목록 조회 실제 API 호출
	    * @title : getAwsSubnetInfoListApiFromAws
	    * @return : List<Subnet>
	    ***************************************************/
	    public List<Subnet> getAwsSubnetInfoListApiFromAws(IaasAccountMgntVO vo, String regionName) {
	        AmazonEC2Client ec2 =  getAmazonEC2Client(vo, regionName);
	        List<Subnet> subnets = ec2.describeSubnets().getSubnets();
	        return subnets;
	    }
	    
	    /***************************************************
	    * @project : AWS 인프라 관리 대시보드
	    * @description : AWS Address Info 목록 조회 실제 API 호출
	    * @title : getAwsAddressInfoListApiFromAws
	    * @return : List<Address>
	    ***************************************************/
	    public List<Address> getAwsEipAllocationIdListApiFromAws(IaasAccountMgntVO vo, String regionName) {
	        AmazonEC2Client ec2 =  getAmazonEC2Client(vo, regionName);
	        List<Address> addresses = ec2.describeAddresses().getAddresses();
	        return addresses;
	    }
	    
	    /***************************************************
	      * @param region 
	     * @project : AWS 인프라 관리 대시보드
	      * @description : AWS Elastic IP 할당 실제 API 호출
	      * @title : allocateElasticIpFromAws
	      * @return : String
	      ***************************************************/
	     public void allocateNewElasticIpFromAws(IaasAccountMgntVO vo, Region region) {
	         String regionName = region.getName();
	    	 AmazonEC2Client ec2 =  getAmazonEC2Client(vo, regionName);
	         AllocateAddressRequest allocateRequest = new AllocateAddressRequest();
	         ec2.allocateAddress(allocateRequest);
	     }
	    
	    /***************************************************
	    * @project : AWS 인프라 관리 대시보드
	    * @description : AWS NAT Gateway 생성 실제 API 호출
	    * @title : createAwsNatGatewayApiFromAws
	    * @return : void
	    ***************************************************/
	    public void createAwsNatGatewayApiFromAws(IaasAccountMgntVO vo, String regionName, AwsNatGatewayMgntDTO dto){
	    	AmazonEC2Client ec2 =  getAmazonEC2Client(vo, regionName);
	    	String allocationId = dto.getAllocationId().toString();
	    	String subnetId = dto.getSubnetId().toString();
			CreateNatGatewayRequest request = new CreateNatGatewayRequest().withAllocationId(allocationId).withSubnetId(subnetId);
			ec2.createNatGateway(request);
	   }
}
