package org.openpaas.ieda.iaasDashboard.awsMgnt.api.vpc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openpaas.ieda.common.web.common.service.CommonApiService;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.vpc.dto.AwsVpcMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.CreateVpcRequest;
import com.amazonaws.services.ec2.model.CreateVpcResult;
import com.amazonaws.services.ec2.model.DeleteVpcRequest;
import com.amazonaws.services.ec2.model.DescribeVpcAttributeRequest;
import com.amazonaws.services.ec2.model.DescribeVpcsRequest;
import com.amazonaws.services.ec2.model.NetworkAcl;
import com.amazonaws.services.ec2.model.RouteTable;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.Tenancy;
import com.amazonaws.services.ec2.model.Vpc;
@Service
public class AwsVpcMgntApiService {
    
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
    * @param string 
     * @project : AWS 인프라 관리 대시보드
    * @description : AWS VPC 상세 정보 조회 실제 API 호출
    * @title : getAwsVpcDetailInfoFromAws
    * @return : List<Vpc>
    ***************************************************/
    public List<Vpc> getAwsVpcDetailInfoFromAws(IaasAccountMgntVO vo, String vpcId, String regionName) {
        AmazonEC2Client ec2 =  getAmazonEC2Client(vo, regionName);
        DescribeVpcsRequest vpcsRequest = new DescribeVpcsRequest();
        vpcsRequest.withVpcIds(vpcId);
        List<Vpc> vpcs = ec2.describeVpcs(vpcsRequest).getVpcs();
        return vpcs;
    }
    
    /***************************************************
    * @param string 
     * @project : AWS 인프라 관리 대시보드
    * @description : VPC에서 실행 된  DNS 호스트 이름을 가져올 지 여부 조회 실제 API 호출
    * @title : getVpcDnsNameInfoFromAws
    * @return : HashMap<String, Boolean>
    ***************************************************/
    public HashMap<String, Boolean> getVpcDnsNameInfoFromAws(IaasAccountMgntVO vo, String vpcId, String regionName) {
        AmazonEC2Client ec2 =  getAmazonEC2Client(vo, regionName);
        HashMap<String, Boolean> dnsMap = new HashMap<String, Boolean>();
        DescribeVpcAttributeRequest request = new DescribeVpcAttributeRequest();
        request.withVpcId(vpcId);
        request.setAttribute("enableDnsHostnames");
        dnsMap.put("DNShostnames", ec2.describeVpcAttribute(request).isEnableDnsHostnames());
        request.setAttribute("enableDnsSupport");
        dnsMap.put("DNSresolution", ec2.describeVpcAttribute(request).isEnableDnsSupport());
        return dnsMap;
    }
    
    /***************************************************
    * @param string 
     * @project : AWS 인프라 관리 대시보드
    * @description : 해당 VPC 아이디에 연결 된 라우터 테이블 조회 실제 AWS API 호출
    * @title : getRouteTableInfoFromAws
    * @return : String
    ***************************************************/
    public String getRouteTableInfoFromAws(IaasAccountMgntVO vo, String vpcId, String regionName) {
        AmazonEC2Client ec2 =  getAmazonEC2Client(vo, regionName);List<RouteTable> list = ec2.describeRouteTables().getRouteTables();
        String routeTableId = "";
        if(!list.isEmpty()){
            for(int i = 0; i<list.size(); i++){
                if(list.get(i).getVpcId().equals(vpcId)){
                    routeTableId = list.get(i).getRouteTableId();
                }
            }
        }
        return routeTableId;
    }
    
    /***************************************************
    * @param string 
     * @project : AWS 인프라 관리 대시보드
    * @description : 해당 VPC에 연결 된 notworkAcle 아이디 조회 실제 AWS API 호출
    * @title : getNetworkAcleInfoFromAws
    * @return : String
    ***************************************************/
    public String getNetworkAcleInfoFromAws(IaasAccountMgntVO vo, String vpcId, String regionName) {
        AmazonEC2Client ec2 =  getAmazonEC2Client(vo, regionName);
        String networkAcleId = "";
        List<NetworkAcl> list = ec2.describeNetworkAcls().getNetworkAcls();
        if(!list.isEmpty()){
            for(int i=0; i<list.size(); i++){
                if(list.get(i).getVpcId().equals(vpcId)){
                    networkAcleId = list.get(i).getNetworkAclId();
                }
            }
        }
        return networkAcleId;
    }
    
    /***************************************************
    * @project : AWS 인프라 관리 대시보드
    * @description : AWS VPC 생성 실제 API 호출
    * @title : saveAwsVpcInfoApiFromAws
    * @return : void
    ***************************************************/
    public void saveAwsVpcInfoApiFromAws(IaasAccountMgntVO vo, AwsVpcMgntDTO dto, String regionName) {
        AmazonEC2Client ec2 =  getAmazonEC2Client(vo, regionName);
        
        CreateVpcRequest request = new CreateVpcRequest();
        request.setCidrBlock(dto.getIpv4CirdBlock());
        Tenancy instanceTenancy = null;
        if("default".equals(dto.getTenancy().toLowerCase())){
            instanceTenancy = Tenancy.Default;
        }else{
            instanceTenancy = Tenancy.Dedicated;
        }
        request.setInstanceTenancy(instanceTenancy);
        request.setAmazonProvidedIpv6CidrBlock(dto.isIpv6CirdBlock());
        CreateVpcResult result = ec2.createVpc(request);
        
        if( !StringUtils.isEmpty(dto.getNameTag()) ){
            List<Tag> tags = new ArrayList<Tag>();
            Tag newTag = new Tag();
            newTag.setKey("Name");
            newTag.setValue(dto.getNameTag());
            tags.add(newTag);
            CreateTagsRequest createTagsRequest = new CreateTagsRequest();
            createTagsRequest.setTags(tags);
            createTagsRequest.withResources(result.getVpc().getVpcId());
            ec2.createTags(createTagsRequest);
        }
    }
    
    /***************************************************
    * @project : AWS 인프라 관리 대시보드
    * @description : AWS VPC 실제 메소드 호출
    * @title : deleteAwsVpcInfoApiFromAws
    * @return : void
    ***************************************************/
    public void deleteAwsVpcInfoApiFromAws(IaasAccountMgntVO vo, AwsVpcMgntDTO dto, String regionName) {
        AWSStaticCredentialsProvider provider = commonApiService.getAwsStaticCredentialsProvider(vo.getCommonAccessUser(), vo.getCommonAccessSecret());
        AmazonEC2Client ec2 =  (AmazonEC2Client)AmazonEC2ClientBuilder.standard().withRegion(regionName).withCredentials(provider).build();
        DeleteVpcRequest request = new DeleteVpcRequest();
        request.setVpcId(dto.getVpcId());
        ec2.deleteVpc(request);
    }
}