package org.openpaas.ieda.iaasDashboard.awsMgnt.api.internetgateway;

import java.util.ArrayList;
import java.util.List;

import org.openpaas.ieda.common.web.common.service.CommonApiService;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.internetGateway.dto.AwsInternetGatewayMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AttachInternetGatewayRequest;
import com.amazonaws.services.ec2.model.CreateInternetGatewayRequest;
import com.amazonaws.services.ec2.model.CreateInternetGatewayResult;
import com.amazonaws.services.ec2.model.CreateRouteRequest;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DeleteInternetGatewayRequest;
import com.amazonaws.services.ec2.model.DetachInternetGatewayRequest;
import com.amazonaws.services.ec2.model.InternetGateway;
import com.amazonaws.services.ec2.model.RouteTable;
import com.amazonaws.services.ec2.model.Tag;

@Service
public class AwsInternetGatewayMgntApiService {
    
    @Autowired
    CommonApiService commonApiService;
    
    /***************************************************
    * @param region 
     * @project : Paas 플랫폼 설치 자동화
    * @description : 공통 AmazonEC2Client 객체 생성 
    * @title : getAmazonEC2Client
    * @return : AmazonEC2Client
    ***************************************************/
    public AmazonEC2Client getAmazonEC2Client(IaasAccountMgntVO vo, Region region){
        AWSStaticCredentialsProvider provider = commonApiService.getAwsStaticCredentialsProvider(vo.getCommonAccessUser(), vo.getCommonAccessSecret());
        AmazonEC2Client ec2 =  (AmazonEC2Client)AmazonEC2ClientBuilder.standard().withRegion(region.getName()).withCredentials(provider).build();
        return ec2;
    }
    
    /***************************************************
    * @param region 
     * @project : AWS 인프라 관리 대시보드
    * @description : AWS 인터넷 게이트웨이 목록 조회 실제 AWS API 호출
    * @title : getAwsInternetGatewayInfoListFromAws
    * @return : List<InternetGateway>
    ***************************************************/
    public List<InternetGateway> getAwsInternetGatewayInfoListFromAws(IaasAccountMgntVO vo, Region region) {
        AmazonEC2Client ec2 =  getAmazonEC2Client(vo, region);
        List<InternetGateway> list = ec2.describeInternetGateways().getInternetGateways();
        return list;
    }
    
    /***************************************************
    * @param region 
     * @project : AWS 인프라 관리 대시보드
    * @description : AWS 인터넷 게이트웨이 생성 실제 AWS API 호출
    * @title : saveAwsInternetGatewayInfoFromAws
    * @return : void
    ***************************************************/
    public void saveAwsInternetGatewayInfoFromAws(AwsInternetGatewayMgntDTO dto, IaasAccountMgntVO vo, Region region) {
        AmazonEC2Client ec2 =  getAmazonEC2Client(vo, region);
        CreateInternetGatewayRequest req = new CreateInternetGatewayRequest();
        CreateInternetGatewayResult result= ec2.createInternetGateway(req);
        List<Tag> tags = new ArrayList<Tag>();
        Tag newTag = new Tag();
        newTag.setKey("Name");
        newTag.setValue(dto.getInternetGatewayName());
        tags.add(newTag);
        CreateTagsRequest createTagsRequest = new CreateTagsRequest();
        createTagsRequest.setTags(tags);
        createTagsRequest.withResources(result.getInternetGateway().getInternetGatewayId());
        ec2.createTags(createTagsRequest);
    }
    
    /***************************************************
    * @param region 
     * @project : AWS 인프라 관리 대시보드
    * @description : AWS 인터넷 게이트웨이 삭제 실제 AWS API 호출
    * @title : saveAwsInternetGatewayInfoFromAws
    * @return : void
    ***************************************************/
    public void deleteAwsInternetGatewayInfoFromAws(AwsInternetGatewayMgntDTO dto, IaasAccountMgntVO vo, Region region) {
        AmazonEC2Client ec2 =  getAmazonEC2Client(vo, region);
        DeleteInternetGatewayRequest req = new DeleteInternetGatewayRequest();
        req.setInternetGatewayId(dto.getInternetGatewayId());
        ec2.deleteInternetGateway(req);
    }
    
    /***************************************************
    * @param region 
     * @project : AWS 인프라 관리 대시보드
    * @description : AWS VPC 연결
    * @title : internetGatewayAttachVpcFromAws
    * @return : void
    ***************************************************/
    public void internetGatewayAttachVpcFromAws(AwsInternetGatewayMgntDTO dto, IaasAccountMgntVO vo, Region region) {
        AmazonEC2Client ec2 =  getAmazonEC2Client(vo, region);
        AttachInternetGatewayRequest req = new AttachInternetGatewayRequest();
        req.setInternetGatewayId(dto.getInternetGatewayId());
        req.setVpcId(dto.getVpcId());
        ec2.attachInternetGateway(req);
        String routeTableId = "";
        
        List<RouteTable> routeTableList = ec2.describeRouteTables().getRouteTables();
        
        for(RouteTable routeable : routeTableList ){
            if(routeable.getVpcId().equals(dto.getVpcId())){
                routeTableId = routeable.getRouteTableId();
                break;
            }
        }
        if(!routeTableId.isEmpty()){
            CreateRouteRequest routeReq = new CreateRouteRequest();
            routeReq.setGatewayId(dto.getInternetGatewayId());
            routeReq.setRouteTableId(routeTableId);
            routeReq.setDestinationCidrBlock("0.0.0.0/0");
            ec2.createRoute(routeReq);
        }
    }
    
    /***************************************************
    * @param region 
     * @project : AWS 인프라 관리 대시보드
    * @description : AWS VPC 연결 해제
    * @title : internetGatewayDetachVpcFromAws
    * @return : void
    ***************************************************/
    public void internetGatewayDetachVpcFromAws(AwsInternetGatewayMgntDTO dto, IaasAccountMgntVO vo, Region region) {
        AmazonEC2Client ec2 =  getAmazonEC2Client(vo, region);
        DetachInternetGatewayRequest req = new DetachInternetGatewayRequest();
        req.setInternetGatewayId(dto.getInternetGatewayId());
        req.setVpcId(dto.getVpcId());
        ec2.detachInternetGateway(req);
        
    }
    
}