package org.openpaas.ieda.iaasDashboard.awsMgnt.web.routeTable.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openpaas.ieda.common.web.common.service.CommonApiService;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.routeTable.dto.AwsRouteTableMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.Address;
import com.amazonaws.services.ec2.model.AssociateRouteTableRequest;
import com.amazonaws.services.ec2.model.CreateRouteRequest;
import com.amazonaws.services.ec2.model.CreateRouteTableRequest;
import com.amazonaws.services.ec2.model.CreateRouteTableResult;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DeleteRouteRequest;
import com.amazonaws.services.ec2.model.DeleteRouteTableRequest;
import com.amazonaws.services.ec2.model.DescribeNatGatewaysRequest;
import com.amazonaws.services.ec2.model.DisassociateRouteTableRequest;
import com.amazonaws.services.ec2.model.InternetGateway;
import com.amazonaws.services.ec2.model.NatGateway;
//import com.amazonaws.services.ec2.model.ReplaceRouteRequest;
//import com.amazonaws.services.ec2.model.ReplaceRouteTableAssociationRequest;
import com.amazonaws.services.ec2.model.RouteTable;
import com.amazonaws.services.ec2.model.Subnet;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.Vpc;

@Service
public class AwsRouteTableMgntApiService {

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
     * @description : AWS RouteTable 목록 조회 실제 API 호출 (Route info / subnet association info nested)
     * @title : getAwsRouteTableListApiFromAws
     * @return : List<RouteTable>
     ***************************************************/
    public List<RouteTable> getAwsRouteTableListApiFromAws(IaasAccountMgntVO vo, String regionName) {
    	AmazonEC2Client ec2 =  getAmazonEC2Client(vo, regionName);
    	List<RouteTable> routetables = ec2.describeRouteTables().getRouteTables();
    	return routetables;
    }
    
    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS Route Table 생성 실제 API 호출
     * @title : createAwsRouteTableFromAws
     * @return : void
     ***************************************************/
    public void createAwsRouteTableFromAws(IaasAccountMgntVO vo, String regionName, AwsRouteTableMgntDTO dto){
    	AmazonEC2Client ec2 =  getAmazonEC2Client(vo, regionName);
    	String vpcId = dto.getVpcId();
    	CreateRouteTableRequest request = new CreateRouteTableRequest().withVpcId(vpcId);
    	CreateRouteTableResult result = ec2.createRouteTable(request);
    	 if( !StringUtils.isEmpty(dto.getNameTag()) ){
             List<Tag> tags = new ArrayList<Tag>();
             Tag newTag = new Tag();
             newTag.setKey("Name");
             newTag.setValue(dto.getNameTag());
             tags.add(newTag);
             CreateTagsRequest createTagsRequest = new CreateTagsRequest();
             createTagsRequest.setTags(tags);
             createTagsRequest.withResources(result.getRouteTable().getRouteTableId());
             ec2.createTags(createTagsRequest);
         }
    }
    
    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS Route 생성 실제 API 호출
     * @title : createAwsRouteFromAws
     * @return : void
     ***************************************************/
    public void createAwsRouteFromAws(IaasAccountMgntVO vo, String regionName, AwsRouteTableMgntDTO dto){
    	AmazonEC2Client ec2 =  getAmazonEC2Client(vo, regionName);
    	String routeTableId = dto.getRouteTableId();
    	String destinationCidrBlock = dto.getDestinationIpv4CidrBlock();
    	if(dto.getTargetId().toString().contains("nat-")){
    		String natGatewayId = dto.getTargetId();
    		CreateRouteRequest request = new CreateRouteRequest().withRouteTableId(routeTableId).withDestinationCidrBlock(destinationCidrBlock).withNatGatewayId(natGatewayId);
     	     ec2.createRoute(request);
    	}else if(dto.getTargetId().toString().contains("igw-")){
    	   String gatewayId = dto.getTargetId();
    	   CreateRouteRequest request = new CreateRouteRequest().withRouteTableId(routeTableId).withDestinationCidrBlock(destinationCidrBlock).withGatewayId(gatewayId);
    	   ec2.createRoute(request);
    	}
    }
    
    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS Delete Route Table 실제 API 호출
     * @title : deleteAwsRouteTableFromAws
     * @return : void
     ***************************************************/
    public void deleteAwsRouteTableFromAws (IaasAccountMgntVO vo, String regionName, AwsRouteTableMgntDTO dto){
    	AmazonEC2Client ec2 =  getAmazonEC2Client(vo, regionName);
    	String routeTableId = dto.getRouteTableId();
    	DeleteRouteTableRequest request = new DeleteRouteTableRequest().withRouteTableId(routeTableId);
    	ec2.deleteRouteTable(request);
    }
    
    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS Replace Existing Route form Route Table 실제 API 호출
     * @title : replaceAwsExistingRouteFromRouteTableWithRouteTableFromAws
     * @return : void
     ***************************************************/
    /*public void replaceAwsExistingRouteFromRouteTableWithRouteTableFromAws(IaasAccountMgntVO vo, String regionName, AwsRouteTableMgntDTO dto){
    	AmazonEC2Client ec2 =  getAmazonEC2Client(vo, regionName);
    	String routeTableId = dto.getRouteTableId();
    	String destinationCidrBlock = dto.getDestinationIpv4CidrBlock();
    	ReplaceRouteRequest request = new ReplaceRouteRequest().withRouteTableId(routeTableId).withDestinationCidrBlock(destinationCidrBlock);
    	ec2.replaceRoute(request);
    }*/
    
    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS Delete Route in Route Table 실제 API 호출
     * @title : deleteAwsRouteInRouteTableFromAws
     * @return : void
     ***************************************************/
    public void deleteAwsRouteInRouteTableFromAws(IaasAccountMgntVO vo, String regionName, AwsRouteTableMgntDTO dto){
    	AmazonEC2Client ec2 =  getAmazonEC2Client(vo, regionName);
    	String routeTableId = dto.getRouteTableId();
    	String destinationCidrBlock = dto.getDestinationIpv4CidrBlock();
    	DeleteRouteRequest request = new DeleteRouteRequest().withRouteTableId(routeTableId).withDestinationCidrBlock(destinationCidrBlock);
    	ec2.deleteRoute(request);
    }
    
    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS Associate Subnet with Route Table 실제 API 호출
     * @title : associateAwsSubnetWithRouteTableFromAws
     * @return : void
     ***************************************************/
    public void associateAwsSubnetWithRouteTableFromAws(IaasAccountMgntVO vo, String regionName, AwsRouteTableMgntDTO dto){
    	AmazonEC2Client ec2 =  getAmazonEC2Client(vo, regionName);
    	String routeTableId = dto.getRouteTableId();
    	String subnetId = dto.getSubnetId();
    	AssociateRouteTableRequest request = new AssociateRouteTableRequest().withRouteTableId(routeTableId).withSubnetId(subnetId);
    	ec2.associateRouteTable(request);
    }
    
    
    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS Disassociate Subnet with Route Table 실제 API 호출
     * @title : disassociateAwsSubnetFromRouteTableFromAws
     * @return : void
     ***************************************************/
    public void disassociateAwsSubnetFromRouteTableFromAws(IaasAccountMgntVO vo, String regionName, AwsRouteTableMgntDTO dto){
    	AmazonEC2Client ec2 =  getAmazonEC2Client(vo, regionName);
    	String associationId = dto.getAssociationId();
    	DisassociateRouteTableRequest request = new DisassociateRouteTableRequest().withAssociationId(associationId);
    	ec2.disassociateRouteTable(request);
    }
    
    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS Replace Route Table Associated with Subnet 실제 API 호출
     * @title : replaceAwsRouteTableAssociatedWithSubnetFromAws
     * @return : String
     ***************************************************/
    /*public String replaceAwsRouteTableAssociatedWithSubnetFromAws(IaasAccountMgntVO vo, String regionName, AwsRouteTableMgntDTO dto){
    	AmazonEC2Client ec2 =  getAmazonEC2Client(vo, regionName);
    	String associationId = dto.getAssociationId();
    	ReplaceRouteTableAssociationRequest request = new ReplaceRouteTableAssociationRequest().withAssociationId(associationId);
    	String newAssociationId = ec2.replaceRouteTableAssociation(request).getNewAssociationId().toString();
    	return newAssociationId;
    }*/
    
    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS Address 목록 조회 실제 API 호출
     * @title : getAwsAddressListApiFromAws
     * @return : List<Address>
     ***************************************************/
     public List<InternetGateway> getAwsIGWListApiFromAws(IaasAccountMgntVO vo, String regionName) {
         AmazonEC2Client ec2 =  getAmazonEC2Client(vo, regionName);
         List<InternetGateway> igws = ec2.describeInternetGateways().getInternetGateways();
         return igws;
     }
    
     /***************************************************
      * @project : AWS 인프라 관리 대시보드
      * @description : AWS NAT Gateway 목록 조회 실제 API 호출
      * @title : getAwsNatGatewayListApiFromAws
      * @return : List<NatGateway>
      ***************************************************/
      public List<NatGateway> getAwsNatGatewayListApiFromAws(IaasAccountMgntVO vo, String regionName) {
          AmazonEC2Client ec2 =  getAmazonEC2Client(vo, regionName);
          DescribeNatGatewaysRequest request = new DescribeNatGatewaysRequest();
          List<NatGateway> natgws = ec2.describeNatGateways(request).getNatGateways();
          return natgws;
      }
     
    /***************************************************
    * @project : AWS 인프라 관리 대시보드
    * @description : AWS Address 목록 조회 실제 API 호출
    * @title : getAwsAddressListApiFromAws
    * @return : List<Address>
    ***************************************************/
    public List<Address> getAwsAddressListApiFromAws(IaasAccountMgntVO vo, String regionName) {
        AmazonEC2Client ec2 =  getAmazonEC2Client(vo, regionName);
        List<Address> addresses = ec2.describeAddresses().getAddresses();
        return addresses;
    }
    
    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS Subnet 목록 조회 실제 API 호출
     * @title : getAwsSubnetInfoListApiFromAws
     * @return : List<Subnet>
     ***************************************************/
    public List<Subnet> getAwsSubnetInfoListApiFromAws (IaasAccountMgntVO vo, String regionName) {
    	AmazonEC2Client ec2 =  getAmazonEC2Client(vo, regionName);
    	List<Subnet> subnets = ec2.describeSubnets().getSubnets();
    	return subnets;
    }
    
    /***************************************************
    * @project : AWS 인프라 관리 대시보드
    * @description : AWS VPC 목록 조회 실제 API 호출
    * @title : getAwsVpcIdListApiFromAws
    * @return : List<Vpc>
    ***************************************************/
    public List<Vpc> getAwsVpcIdListApiFromAws(IaasAccountMgntVO vo, String regionName) {
        AmazonEC2Client ec2 =  getAmazonEC2Client(vo, regionName);
        List<Vpc> vpcs = ec2.describeVpcs().getVpcs();
        return vpcs;
    }
}