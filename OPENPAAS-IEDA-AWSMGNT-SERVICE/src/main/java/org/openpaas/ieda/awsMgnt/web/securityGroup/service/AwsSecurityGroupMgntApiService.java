package org.openpaas.ieda.awsMgnt.web.securityGroup.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openpaas.ieda.awsMgnt.web.securityGroup.dto.AwsSecurityGroupMgntDTO;
import org.openpaas.ieda.common.web.common.service.CommonApiService;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressResult;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DeleteSecurityGroupRequest;
import com.amazonaws.services.ec2.model.DeleteSecurityGroupResult;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.IpRange;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.UserIdGroupPair;

@Service
public class AwsSecurityGroupMgntApiService {
    
    @Autowired
    CommonApiService commonApiService;
    
    /****************************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : Amazon EC2 Client 공통 빌드
     * @title : getAmazonEC2Client
     * @return : AmazonEC2Client
    *****************************************************************/
    public AmazonEC2Client getAmazonEC2Client(IaasAccountMgntVO vo, Region region){
        AWSStaticCredentialsProvider provider = commonApiService.getAwsStaticCredentialsProvider(vo.getCommonAccessUser(), vo.getCommonAccessSecret());
        AmazonEC2Client ec2 =  (AmazonEC2Client)AmazonEC2ClientBuilder.standard().withRegion(region.getName()).withCredentials(provider).build();
        
        return ec2;
    }
    
    /****************************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS API를 통해 시큐리티 목록 정보 조회
     * @title : getAwsSecurityGroupInfoListApiFromAws
     * @return : List<SecurityGroup>
    *****************************************************************/
    public List<SecurityGroup> getAwsSecurityGroupInfoListApiFromAws(IaasAccountMgntVO vo, Region region) {
        AmazonEC2Client ec2 =  getAmazonEC2Client(vo, region);
        DescribeSecurityGroupsResult response = ec2.describeSecurityGroups();
        List<SecurityGroup> group = response.getSecurityGroups();  
        
        return group;
    }
    
    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS Security Group Ingress Rules 정보 조회 실제 API 호출
     * @title : getAwsSecurityGroupRulesInfoFromAws
     * @return : HashMap<String, Object> 
     ***************************************************/
    public List<SecurityGroup> getAwsSecurityGroupRulesInfoFromAws(IaasAccountMgntVO vo, String groupId, Region region) {
        AmazonEC2Client ec2 = getAmazonEC2Client(vo, region);
        DescribeSecurityGroupsRequest request = new DescribeSecurityGroupsRequest();
        request.withGroupIds(groupId);
        DescribeSecurityGroupsResult groups = ec2.describeSecurityGroups(request);  
        
        return groups.getSecurityGroups();
    }
    
    
    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS Security Group 생성 실제 API 호출
     * @title : saveSecurityGroupFromAws
     * @return : String
     ***************************************************/
    public String saveSecurityGroupFromAws(IaasAccountMgntVO vo, AwsSecurityGroupMgntDTO dto, Region region ){
        AmazonEC2Client ec2 =  getAmazonEC2Client(vo, region);
       
        CreateSecurityGroupRequest request = new CreateSecurityGroupRequest();
        request.setGroupName(dto.getGroupName());
        request.setDescription(dto.getDescription());
        request.setVpcId(dto.getVpcId());
        request.withGroupName(dto.getGroupName()).withDescription(dto.getDescription());
        
        CreateSecurityGroupResult result = ec2.createSecurityGroup(request);
        String groupId = result.getGroupId();
        
        //tag 적용 
        if( !StringUtils.isEmpty(dto.getNameTag()) ){
            List<Tag> tags = new ArrayList<Tag>();
            Tag newTag = new Tag();
            newTag.setKey("nameTag");
            newTag.setValue(dto.getNameTag());
            tags.add(newTag);
            CreateTagsRequest createTagsRequest = new CreateTagsRequest();
            createTagsRequest.setTags(tags);
            createTagsRequest.withResources(result.getGroupId());
            ec2.createTags(createTagsRequest);
        }
        
        return groupId;
    }
    
    
    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS Security Group Ingress Rule 생성 실제 API 호출
     * @title : saveSecurityGroupIngressRuleFromAws
     * @return : void
     ***************************************************/
    public Boolean saveSecurityGroupIngressRuleFromAws(IaasAccountMgntVO vo, AwsSecurityGroupMgntDTO dto, Region region ){
        Boolean flag= false;
        AmazonEC2Client ec2 =  getAmazonEC2Client(vo, region);
        
        for( int i=0; i<dto.getIngressRules().size();i++ ){
            AuthorizeSecurityGroupIngressRequest ingressRequest =new AuthorizeSecurityGroupIngressRequest();
            IpPermission ipPermission = new IpPermission();
            String[] ports = dto.getIngressRules().get(i).get("portRange").split("-");
            String portRanges = ports[0];
            int fromPort = Integer.valueOf(portRanges);
            int toPort = ports.length == 2 ? Integer.valueOf(portRanges) : fromPort;
            if(toPort == 0){
                toPort = 65535;
            }
            //if security id
            UserIdGroupPair userIdGroupPairs = new UserIdGroupPair();
            userIdGroupPairs.withGroupId(dto.getGroupId()).withVpcId(dto.getVpcId());
            IpRange ipRange = new IpRange();
            ipRange.setCidrIp("0.0.0.0/0");
            ipPermission.withIpv4Ranges(ipRange)
                        .withIpProtocol(dto.getIngressRules().get(i).get("protocol"))
                        .withFromPort(fromPort)
                        .withToPort(toPort);
            ingressRequest.withGroupId(dto.getGroupId()).withIpPermissions(ipPermission);
            AuthorizeSecurityGroupIngressResult ingressResult = ec2.authorizeSecurityGroupIngress(ingressRequest);
            if( ingressResult.getSdkHttpMetadata().getHttpStatusCode() == 200 ){
                flag = true;
            }else{
                flag = false;
                break;
            }
        }
        return flag;
      }
    
    /***************************************************
     * @project : AWS 인프라 관리 대시보드
     * @description : AWS Security Group 삭제 실제 메소드 호출
     * @title : deleteSecurityGroupInfoFromAws
     * @return : void
     ***************************************************/
     public Boolean deleteSecurityGroupInfoFromAws(IaasAccountMgntVO vo, AwsSecurityGroupMgntDTO dto, Region region) {
         Boolean flag = false;
         AmazonEC2Client ec2 =  getAmazonEC2Client(vo, region);
         DeleteSecurityGroupRequest request = new DeleteSecurityGroupRequest();
         request.setGroupId(dto.getGroupId());
         DeleteSecurityGroupResult result= ec2.deleteSecurityGroup(request);
         if( result.getSdkHttpMetadata().getHttpStatusCode() == 200 ){
             flag = !flag;
         }
         return flag;
     }
    
}