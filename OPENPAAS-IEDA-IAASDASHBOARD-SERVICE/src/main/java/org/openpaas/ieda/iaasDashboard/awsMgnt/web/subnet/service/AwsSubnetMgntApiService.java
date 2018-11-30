package org.openpaas.ieda.iaasDashboard.awsMgnt.web.subnet.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openpaas.ieda.common.web.common.service.CommonApiService;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.subnet.dto.AwsSubnetMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.CreateSubnetRequest;
import com.amazonaws.services.ec2.model.CreateSubnetResult;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DeleteSubnetRequest;
import com.amazonaws.services.ec2.model.DescribeSubnetsRequest;
import com.amazonaws.services.ec2.model.NetworkAcl;
import com.amazonaws.services.ec2.model.RouteTable;
import com.amazonaws.services.ec2.model.Subnet;
import com.amazonaws.services.ec2.model.Tag;
@Service
public class AwsSubnetMgntApiService {
    
    @Autowired
    CommonApiService commonApiService;
    
    /***************************************************
     * @param region 
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS 서브넷 목록 조회 실제 API 호출
     * @title : getAwsSubnetInfoListApiFromAws
     * @return : List<Subnet>
     ***************************************************/
    public List<Subnet> getAwsSubnetInfoListApiFromAws(IaasAccountMgntVO vo, Region region) {
        AWSStaticCredentialsProvider provider = commonApiService.getAwsStaticCredentialsProvider(vo.getCommonAccessUser(), vo.getCommonAccessSecret());
        AmazonEC2Client ec2 =  (AmazonEC2Client)AmazonEC2ClientBuilder.standard().withRegion(region.getName()).withCredentials(provider).build();
        List<Subnet> subnets = ec2.describeSubnets().getSubnets();
        return subnets;
    }
    
    
    /***************************************************
     * @param region 
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS 서브넷 상세 정보 조회 실제 API 호출
     * @title : getAwsSubnetDetailInfoFromAws
     * @return : HashMap<String, Object> 
     ***************************************************/
    public HashMap<String, Object> getAwsSubnetDetailInfoFromAws(IaasAccountMgntVO vo, String subnetId, Region region) {
        AWSStaticCredentialsProvider provider = commonApiService.getAwsStaticCredentialsProvider(vo.getCommonAccessUser(), vo.getCommonAccessSecret());
        AmazonEC2Client ec2 =  (AmazonEC2Client)AmazonEC2ClientBuilder.standard().withRegion(region.getName()).withCredentials(provider).build();
        
        DescribeSubnetsRequest subnetsRequest = new DescribeSubnetsRequest();
        subnetsRequest.withSubnetIds(subnetId);
        List<Subnet> subnets = ec2.describeSubnets(subnetsRequest).getSubnets();
        
        HashMap<String, Object> detailInfo = new HashMap<String, Object>();
        detailInfo.put("subnets", subnets);
        
        List<RouteTable> routeTables = ec2.describeRouteTables().getRouteTables();
        List<NetworkAcl> networkAcls = ec2.describeNetworkAcls().getNetworkAcls();
        
        detailInfo.put("routeTables", routeTables);
        detailInfo.put("networkAcls", networkAcls);
        
        return detailInfo;
    }
    
    /***************************************************
     * @param region 
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS 서브넷 생성 실제 API 호출
     * @title : saveAwsSubnet
     * @return : void
     ***************************************************/
    public void saveSubnetFromAws(IaasAccountMgntVO vo, AwsSubnetMgntDTO dto, Region region ){
        AWSStaticCredentialsProvider provider = commonApiService.getAwsStaticCredentialsProvider(vo.getCommonAccessUser(), vo.getCommonAccessSecret());
        AmazonEC2Client ec2 =  (AmazonEC2Client)AmazonEC2ClientBuilder.standard().withRegion(region.getName()).withCredentials(provider).build();
       
        CreateSubnetRequest subnetRequest = new CreateSubnetRequest();
        subnetRequest.withVpcId(dto.getVpcId())
                      .withCidrBlock(dto.getCidrBlock())
                      .withAvailabilityZone(dto.getAvailabilityZone());
        
        subnetRequest.setVpcId(dto.getVpcId());
        subnetRequest.setAvailabilityZone(dto.getAvailabilityZone());
        
         //필수는 아니지만 입력값이 있으면 받아와서 입력해준다.
        if( !StringUtils.isEmpty(dto.getCidrBlock()) ){
            subnetRequest.setCidrBlock(dto.getCidrBlock());
        }
        //subnet_request.setIpv6CidrBlock(dto.getIpv6CidrBlock());
        CreateSubnetResult response  = ec2.createSubnet(subnetRequest);
        
        //tag 적용 확인 요망
        if( !StringUtils.isEmpty(dto.getNameTag()) ){
            List<Tag> tags = new ArrayList<Tag>();
            Tag newTag = new Tag();
            newTag.setKey("nameTag");
            newTag.setValue(dto.getNameTag());
            tags.add(newTag);
            CreateTagsRequest createTagsRequest = new CreateTagsRequest();
            createTagsRequest.setTags(tags);
            createTagsRequest.withResources(response.getSubnet().getSubnetId());
            ec2.createTags(createTagsRequest);
        }
    }
    
    /***************************************************
     * @param region 
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS Subnet 실제 메소드 호출
     * @title : deleteSubnetInfoFromAws
     * @return : void
     ***************************************************/
     public void deleteSubnetInfoFromAws(IaasAccountMgntVO vo, AwsSubnetMgntDTO dto, Region region) {
         AWSStaticCredentialsProvider provider = commonApiService.getAwsStaticCredentialsProvider(vo.getCommonAccessUser(), vo.getCommonAccessSecret());
         AmazonEC2Client ec2 =  (AmazonEC2Client)AmazonEC2ClientBuilder.standard().withRegion(region.getName()).withCredentials(provider).build();
         DeleteSubnetRequest request = new DeleteSubnetRequest();
         request.setSubnetId(dto.getSubnetId());
         ec2.deleteSubnet(request);
     }
}