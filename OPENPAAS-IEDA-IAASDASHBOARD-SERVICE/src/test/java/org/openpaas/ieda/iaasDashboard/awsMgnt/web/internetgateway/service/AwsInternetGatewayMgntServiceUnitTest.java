package org.openpaas.ieda.iaasDashboard.awsMgnt.web.internetgateway.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.iaasDashboard.awsMgnt.api.internetgateway.AwsInternetGatewayMgntApiService;
import org.openpaas.ieda.iaasDashboard.awsMgnt.api.vpc.AwsVpcMgntApiService;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.common.base.BaseAwsMgntControllerUnitTest;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.internetGateway.dao.AwsInternetGatewayMgntVO;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.internetGateway.dto.AwsInternetGatewayMgntDTO;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.internetGateway.service.AwsInternetGatewayMgntService;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.model.InternetGateway;
import com.amazonaws.services.ec2.model.InternetGatewayAttachment;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.Vpc;
import com.amazonaws.services.ec2.model.VpcIpv6CidrBlockAssociation;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class AwsInternetGatewayMgntServiceUnitTest extends BaseAwsMgntControllerUnitTest{
    
    @InjectMocks AwsInternetGatewayMgntService mockAwsInternetGatewayMgntService;
    @Mock AwsInternetGatewayMgntApiService mockAwsInternetGatewayMgntApiService;
    @Mock AwsVpcMgntApiService mockAwsVpcMgntApiService;
    @Mock MessageSource mockMessageSource;
    @Mock CommonIaasService mockCommonIaasService;
    
    private Principal principal = null;
    
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    ***************************************************/
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        principal = getLoggined();
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 일반 AWS 인터넷 게이트웨이 목록 조회 TEST
    * @title : testDefaultGetAwsInternetGatewayInfoList
    * @return : void
    ***************************************************/
    @Test
    public void testDefaultGetAwsInternetGatewayInfoList(){
        getAwsAccountInfo();
        getAwsRegionInfo();
        List<InternetGateway> expectList = getInternetGatewayResultInfoList("default");
        when(mockAwsInternetGatewayMgntApiService.getAwsInternetGatewayInfoListFromAws(any(),any())).thenReturn(expectList);
        List<AwsInternetGatewayMgntVO> resultList = mockAwsInternetGatewayMgntService.getAwsInternetGatewayInfoList(principal, 155555, "region");
        assertEquals(resultList.size(), 1 );
        assertEquals(expectList.get(0).getInternetGatewayId(), resultList.get(0).getInternetGatewayId());
        assertEquals(expectList.get(0).getTags().get(0).getValue(), resultList.get(0).getInternetGatewayName());
        assertEquals(expectList.get(0).getAttachments().get(0).getVpcId(), resultList.get(0).getVpcId());
        assertEquals(expectList.get(0).getAttachments().get(0).getState(), resultList.get(0).getStatus());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : AWS Attachments 사이즈가 2개 이상일 경우 인터넷 게이트웨이 정보 조회 TEST
    * @title : testAttachmentSize2GetAwsInternetGatewayInfoList
    * @return : void
    ***************************************************/
    @Test
    public void testAttachmentSize2GetAwsInternetGatewayInfoList(){
        getAwsAccountInfo();
        getAwsRegionInfo();
        List<InternetGateway> expectList = getInternetGatewayResultInfoList("vpc");
        when(mockAwsInternetGatewayMgntApiService.getAwsInternetGatewayInfoListFromAws(any(), any())).thenReturn(expectList);
        List<AwsInternetGatewayMgntVO> resultList = mockAwsInternetGatewayMgntService.getAwsInternetGatewayInfoList(principal, 155555, "region");
        assertEquals(resultList.size(), 1 );
        assertEquals(expectList.get(0).getInternetGatewayId(), resultList.get(0).getInternetGatewayId());
        assertEquals(expectList.get(0).getTags().get(0).getValue(), resultList.get(0).getInternetGatewayName());
        assertEquals(expectList.get(0).getAttachments().get(0).getVpcId() +", " + expectList.get(0).getAttachments().get(1).getVpcId(), resultList.get(0).getVpcId());
        assertEquals(expectList.get(0).getAttachments().get(0).getState() +", " + expectList.get(0).getAttachments().get(1).getState(), resultList.get(0).getStatus());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : AWS NameTag 사이즈가 2개 이상일 경우 인터넷 게이트웨이 정보 조회 TEST
    * @title : testNameTagSize2GetAwsInternetGatewayInfoList
    * @return : void
    ***************************************************/
    @Test
    public void testNameTagSize2GetAwsInternetGatewayInfoList(){
        getAwsAccountInfo();
        getAwsRegionInfo();
        List<InternetGateway> expectList = getInternetGatewayResultInfoList("nameTag");
        when(mockAwsInternetGatewayMgntApiService.getAwsInternetGatewayInfoListFromAws(any(), any())).thenReturn(expectList);
        List<AwsInternetGatewayMgntVO> resultList = mockAwsInternetGatewayMgntService.getAwsInternetGatewayInfoList(principal, 155555, "region");
        assertEquals(resultList.size(), 1 );
        assertEquals(expectList.get(0).getInternetGatewayId(), resultList.get(0).getInternetGatewayId());
        assertEquals(expectList.get(0).getAttachments().get(0).getVpcId(), resultList.get(0).getVpcId());
        assertEquals(expectList.get(0).getAttachments().get(0).getState(), resultList.get(0).getStatus());
        assertEquals(expectList.get(0).getTags().get(0).getValue()+ ", " +  expectList.get(0).getTags().get(1).getValue(), resultList.get(0).getInternetGatewayName());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : AWS 인터넷 게이트웨이 생성 TEST
    * @title : testSaveAwsInternetGatewayInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveAwsInternetGatewayInfo(){
        getAwsAccountInfo();
        getAwsRegionInfo();
        AwsInternetGatewayMgntDTO dto = setInternetGateInfo();
        mockAwsInternetGatewayMgntService.saveAwsInternetGatewayInfo(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : AWS 인터넷 게이트웨이 삭제 TEST
    * @title : testDeleteAwsInternetGatewayInfo
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteAwsInternetGatewayInfo(){
        getAwsAccountInfo();
        getAwsRegionInfo();
        AwsInternetGatewayMgntDTO dto = setInternetGateInfo();
        mockAwsInternetGatewayMgntService.deleteAwsInternetGatewayInfo(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : AWS VPC 연결 TEST
    * @title : testInternetGatewayAttachVpc
    * @return : void
    ***************************************************/
    @Test
    public void testInternetGatewayAttachVpc(){
        getAwsAccountInfo();
        getAwsRegionInfo();
        AwsInternetGatewayMgntDTO dto = setInternetGateInfo();
        mockAwsInternetGatewayMgntService.internetGatewayAttachVpc(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : AWS VPC 연결 해제 TEST
    * @title : testInternetGatewayAttachVpc
    * @return : void
    ***************************************************/
    @Test
    public void testInternetGatewayDetachVpc(){
        getAwsAccountInfo();
        getAwsRegionInfo();
        AwsInternetGatewayMgntDTO dto = setInternetGateInfo();
        mockAwsInternetGatewayMgntService.internetGatewayAttachVpc(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : AWS 인터넷 게이트웨이와 연결 할 VPC 목록 조회 TEST
    * @title : testGetAwsVpcInfoList
    * @return : void
    ***************************************************/
    @Test
    public void testGetAwsVpcInfoList(){
        getAwsAccountInfo();
        getAwsRegionInfo();
        List<InternetGateway> expectInternetGatewayList = getInternetGatewayResultInfoList("default");
        List<Vpc> expectVpcList = getResultVpcListInfo("default");
        when(mockAwsInternetGatewayMgntApiService.getAwsInternetGatewayInfoListFromAws(any(), any())).thenReturn(expectInternetGatewayList);
        when(mockAwsVpcMgntApiService.getAwsVpcInfoListApiFromAws(any(), anyString())).thenReturn(expectVpcList);
        mockAwsInternetGatewayMgntService.getAwsVpcInfoList(principal, 111115, "region");
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 사용중인 VPC 삭제 후 VPC 목록 조회 TEST
    * @title : testGetRemoveVpcAwsVpcInfoList
    * @return : void
    ***************************************************/
    @Test
    public void testGetRemoveVpcAwsVpcInfoList(){
        getAwsAccountInfo();
        getAwsRegionInfo();
        List<InternetGateway> expectInternetGatewayList = getInternetGatewayResultInfoList("default");
        List<Vpc> expectVpcList = getResultVpcListInfo("remove");
        when(mockAwsInternetGatewayMgntApiService.getAwsInternetGatewayInfoListFromAws(any(), any())).thenReturn(expectInternetGatewayList);
        when(mockAwsVpcMgntApiService.getAwsVpcInfoListApiFromAws(any(), anyString())).thenReturn(expectVpcList);
        mockAwsInternetGatewayMgntService.getAwsVpcInfoList(principal, 111115, "region");
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : AWS 인터넷 게이트웨이 정보 설정
    * @title : setInternetGateInfo
    * @return : AwsInternetGatewayMgntDTO
    ***************************************************/
    private AwsInternetGatewayMgntDTO setInternetGateInfo() {
        AwsInternetGatewayMgntDTO dto = new AwsInternetGatewayMgntDTO();
        dto.setInternetGatewayId("InternetGatewayId");
        dto.setInternetGatewayName("InternetGatewayName");
        dto.setVpcId("vpcId");
        dto.setAccountId(5555551);
        dto.getAccountId();
        dto.getInternetGatewayId();
        dto.getInternetGatewayName();
        dto.getVpcId();
        return dto;
    }

    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 인터넷 게이트웨이 정보 조회 결과 값 설정
    * @title : getInternetGatewayResultInfoList
    * @return : List<InternetGateway> 
    ***************************************************/
    private List<InternetGateway> getInternetGatewayResultInfoList(String type) {
        List<InternetGateway> list = new ArrayList<InternetGateway>();
        InternetGateway vo = new InternetGateway();
        List<Tag> tags = new ArrayList<Tag>();
        Tag newTag = new Tag();
        newTag.setKey("Name");
        newTag.setValue("InternetGateWayName");
        tags.add(newTag);
        if(type.equals("nameTag")){
            newTag = new Tag();
            newTag.setKey("Name");
            newTag.setValue("InternetGateWayName2");
            tags.add(newTag);
        }
        vo.setTags(tags);
        List<InternetGatewayAttachment> attachments = new ArrayList<InternetGatewayAttachment>();
        InternetGatewayAttachment attachment = new InternetGatewayAttachment();
        attachment.setVpcId("vpcId");
        attachment.setState("attach");
        attachments.add(attachment);
        if(type.equals("vpc")){
            attachment = new InternetGatewayAttachment();
            attachment.setVpcId("vpcId2");
            attachment.setState("attach2");
            attachments.add(attachment);
        }
        vo.setAttachments(attachments);
        vo.setInternetGatewayId("intId");
        list.add(vo);
        return list;
    }

    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : AWS Account 조회 정보 결과 값 설정
    * @title : getAwsAccountInfo
    * @return : IaasAccountMgntVO
    ***************************************************/
    public IaasAccountMgntVO getAwsAccountInfo() {
        IaasAccountMgntVO vo = new IaasAccountMgntVO();
        vo.setAccountName("testAccountName");
        vo.setCreateUserId("admin");
        vo.setIaasType("aws");
        vo.setCommonAccessSecret("commonSecret");
        vo.setCommonAccessUser("commonUser");
        when(mockCommonIaasService.getIaaSAccountInfo(any(), anyInt(), anyString())).thenReturn(vo);
        return vo;
    }
    
    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : AWS API VPC 목록 조회 결과 값 설정
    * @title : getResultVpcListInfo
    * @return : List<Vpc>
    ***************************************************/
    public List<Vpc> getResultVpcListInfo(String type) {
        List<Vpc> vpcList = new ArrayList<Vpc>();
        Vpc vpc = new Vpc();
        vpc.setCidrBlock("172.16.100.0/24");
        vpc.setDhcpOptionsId("testDhcpOption");
        vpc.setInstanceTenancy("testTenancy");
        vpc.setIsDefault(false);
        vpc.setState("avali");
        vpc.setVpcId("testVpcId");
        if(type.equals("remove")){
            vpc.setVpcId("vpcId");
        }
        List<Tag> tags = new ArrayList<Tag>();
        Tag newTag = new Tag();
        newTag.setKey("Name");
        newTag.setValue("VpcName");
        tags.add(newTag);
        vpc.setTags(tags);
        List<VpcIpv6CidrBlockAssociation> vpcIpv6s = new ArrayList<VpcIpv6CidrBlockAssociation>();
        VpcIpv6CidrBlockAssociation vpcIpv6 = new VpcIpv6CidrBlockAssociation();
        vpcIpv6.setIpv6CidrBlock("testIpv6Cidr");
        vpcIpv6s.add(vpcIpv6);
        vpc.setIpv6CidrBlockAssociationSet(vpcIpv6s);
        vpcList.add(vpc);
        return vpcList;
    }
    
    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : AWS Region 조회 정보 결과 값 설정
    * @title : getAwsAccountInfo
    * @return : IaasAccountMgntVO
    ***************************************************/
    public void getAwsRegionInfo(){
        when(mockCommonIaasService.getAwsRegionInfo(any())).thenReturn(Region.getRegion(Regions.fromName("us-west-2")));
    }
}
