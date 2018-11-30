package org.openpaas.ieda.iaasDashboard.awsMgnt.web.vpc.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.iaasDashboard.awsMgnt.api.vpc.AwsVpcMgntApiService;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.common.base.BaseAwsMgntControllerUnitTest;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.vpc.dao.AwsVpcMgntVO;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.vpc.dto.AwsVpcMgntDTO;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.vpc.service.AwsVpcMgntService;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.Vpc;
import com.amazonaws.services.ec2.model.VpcIpv6CidrBlockAssociation;
@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class AwsVpcMgntServiceUnitTest extends BaseAwsMgntControllerUnitTest{
    
    private Principal principal = null;
    
    @InjectMocks AwsVpcMgntService mockAwsVpcMgntService;
    @Mock AwsVpcMgntApiService mockAwsVpcMgntApiService;
    @Mock CommonIaasService mockCommonIaasService;
    @Mock MessageSource mockMessageSource;
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
    * @project : AWS 관리 대시보드
    * @description : 일반 AWS 목록 조회 TEST
    * @title : testDefaultGetAwsVpcInfoList
    * @return : void
    ***************************************************/
    @Test
    public void testDefaultGetAwsVpcInfoList(){
        getAwsAccountInfo();
        getAwsRegionInfo();
        List<Vpc> vpcList = getResultVpcListInfo("default");
        when(mockAwsVpcMgntApiService.getAwsVpcInfoListApiFromAws(any(), anyString())).thenReturn(vpcList);
        List<AwsVpcMgntVO> resultList = mockAwsVpcMgntService.getAwsVpcInfoList(1, "us-west-2", principal);
        assertEquals(resultList.size(), 1);
        assertEquals(resultList.get(0).getIpv4CidrBlock(), vpcList.get(0).getCidrBlock());
        assertEquals(resultList.get(0).getDhcpOptionSet(), vpcList.get(0).getDhcpOptionsId());
        assertEquals(resultList.get(0).getStatus(), vpcList.get(0).getState());
        assertEquals(resultList.get(0).getVpcId(), vpcList.get(0).getVpcId());
        assertEquals(resultList.get(0).getTenancy(), vpcList.get(0).getInstanceTenancy());
        assertEquals(resultList.get(0).isDefaultVpc(), vpcList.get(0).isDefault());
    }
    
    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : Ipv6CidrBlock 사이즈가 2개 이상 일 경우 AWS 목록 조회 TEST
    * @title : testIpv6CidrBlockListSize2GetAwsVpcInfoList
    * @return : void
    ***************************************************/
    @Test
    public void testIpv6CidrBlockListSize2GetAwsVpcInfoList(){
        getAwsAccountInfo();
        getAwsRegionInfo();
        List<Vpc> vpcList = getResultVpcListInfo("ipv6Cidr");
        when(mockAwsVpcMgntApiService.getAwsVpcInfoListApiFromAws(any(), anyString())).thenReturn(vpcList);
        List<AwsVpcMgntVO> resultList = mockAwsVpcMgntService.getAwsVpcInfoList(1, "us-west-2", principal);
        assertEquals(resultList.size(), 1);
        assertEquals(resultList.get(0).getIpv4CidrBlock(), vpcList.get(0).getCidrBlock());
        assertEquals(resultList.get(0).getDhcpOptionSet(), vpcList.get(0).getDhcpOptionsId());
        assertEquals(resultList.get(0).getStatus(), vpcList.get(0).getState());
        assertEquals(resultList.get(0).getVpcId(), vpcList.get(0).getVpcId());
        assertEquals(resultList.get(0).getTenancy(), vpcList.get(0).getInstanceTenancy());
        assertEquals(resultList.get(0).isDefaultVpc(), vpcList.get(0).isDefault());
        assertEquals(resultList.get(0).getIpv6CidrBlock(), vpcList.get(0).getIpv6CidrBlockAssociationSet().get(0).getIpv6CidrBlock()+ ", " + vpcList.get(0).getIpv6CidrBlockAssociationSet().get(1).getIpv6CidrBlock());
    }
    
    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : NameTagSize 사이즈가 2개 이상 일 경우 AWS 목록 조회 TEST
    * @title : testNameTagSize2GetAwsVpcInfoList
    * @return : void
    ***************************************************/
    @Test
    public void testNameTagSize2GetAwsVpcInfoList(){
        getAwsAccountInfo();
        getAwsRegionInfo();
        List<Vpc> vpcList = getResultVpcListInfo("nameTag");
        when(mockAwsVpcMgntApiService.getAwsVpcInfoListApiFromAws(any(), anyString())).thenReturn(vpcList);
        List<AwsVpcMgntVO> resultList = mockAwsVpcMgntService.getAwsVpcInfoList(1, "us-west-2", principal);
        assertEquals(resultList.size(), 1);
        assertEquals(resultList.get(0).getIpv4CidrBlock(), vpcList.get(0).getCidrBlock());
        assertEquals(resultList.get(0).getDhcpOptionSet(), vpcList.get(0).getDhcpOptionsId());
        assertEquals(resultList.get(0).getStatus(), vpcList.get(0).getState());
        assertEquals(resultList.get(0).getVpcId(), vpcList.get(0).getVpcId());
        assertEquals(resultList.get(0).getTenancy(), vpcList.get(0).getInstanceTenancy());
        assertEquals(resultList.get(0).isDefaultVpc(), vpcList.get(0).isDefault());
        assertEquals(resultList.get(0).getNameTag(), vpcList.get(0).getTags().get(0).getValue()+ ", " + vpcList.get(0).getTags().get(1).getValue());
    }
    
    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : 기본  AWS 상세 정보 조회 TEST
    * @title : testDefaultGetAwsVpcDetailInfo
    * @return : void
    ***************************************************/
    @Test
    public void testDefaultGetAwsVpcDetailInfo(){
        getAwsAccountInfo();
        getAwsRegionInfo();
        List<Vpc> vpcList = getResultVpcListInfo("default");
        when(mockAwsVpcMgntApiService.getAwsVpcDetailInfoFromAws(any(),anyString(), anyString())).thenReturn(vpcList);
        getVpcDetailInfo();
        AwsVpcMgntVO resultVO = mockAwsVpcMgntService.getAwsVpcDetailInfo(1, "vpcId" ,principal, "us-west-2");
        assertEquals(resultVO.getDhcpOptionSet(), vpcList.get(0).getDhcpOptionsId());
        assertEquals(resultVO.getIpv4CidrBlock(), vpcList.get(0).getCidrBlock());
        assertEquals(resultVO.isDefaultVpc(), vpcList.get(0).getIsDefault());
        assertEquals(resultVO.getStatus(), vpcList.get(0).getState());
        assertEquals(resultVO.getTenancy(), vpcList.get(0).getInstanceTenancy());
    }
    
    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : Ipv6Cidr의 사이즈가 2이상일 경우 AWS VPC 상세 조회 TEST
    * @title : testIpv6CidrBlockListSize2GetAwsVpcDetailInfo
    * @return : void
    ***************************************************/
    @Test
    public void testIpv6CidrBlockListSize2GetAwsVpcDetailInfo(){
        getAwsAccountInfo();
        getAwsRegionInfo();
        List<Vpc> vpcList = getResultVpcListInfo("ipv6Cidr");
        when(mockAwsVpcMgntApiService.getAwsVpcDetailInfoFromAws(any(),anyString(), anyString())).thenReturn(vpcList);
        getVpcDetailInfo();
        AwsVpcMgntVO resultVO = mockAwsVpcMgntService.getAwsVpcDetailInfo(1, "vpcId" ,principal, "us-west-2");
        assertEquals(resultVO.getDhcpOptionSet(), vpcList.get(0).getDhcpOptionsId());
        assertEquals(resultVO.getIpv4CidrBlock(), vpcList.get(0).getCidrBlock());
        assertEquals(resultVO.isDefaultVpc(), vpcList.get(0).getIsDefault());
        assertEquals(resultVO.getStatus(), vpcList.get(0).getState());
        assertEquals(resultVO.getTenancy(), vpcList.get(0).getInstanceTenancy());
        assertEquals(resultVO.getIpv6CidrBlock(), vpcList.get(0).getIpv6CidrBlockAssociationSet().get(0).getIpv6CidrBlock()+", "+ vpcList.get(0).getIpv6CidrBlockAssociationSet().get(1).getIpv6CidrBlock());
    }
    
    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : NameTag의 사이즈가 2이상일 경우 AWS VPC 상세 조회 TEST
    * @title : testNameTagSize2GetAwsVpcDetailInfo
    * @return : void
    ***************************************************/
    @Test
    public void testNameTagSize2GetAwsVpcDetailInfo(){
        getAwsAccountInfo();
        getAwsRegionInfo();
        List<Vpc> vpcList = getResultVpcListInfo("nameTag");
        when(mockAwsVpcMgntApiService.getAwsVpcDetailInfoFromAws(any(),anyString(), anyString())).thenReturn(vpcList);
        getVpcDetailInfo();
        AwsVpcMgntVO resultVO = mockAwsVpcMgntService.getAwsVpcDetailInfo(1, "vpcId" ,principal, "us-west-2");
        assertEquals(resultVO.getDhcpOptionSet(), vpcList.get(0).getDhcpOptionsId());
        assertEquals(resultVO.getIpv4CidrBlock(), vpcList.get(0).getCidrBlock());
        assertEquals(resultVO.isDefaultVpc(), vpcList.get(0).getIsDefault());
        assertEquals(resultVO.getStatus(), vpcList.get(0).getState());
        assertEquals(resultVO.getTenancy(), vpcList.get(0).getInstanceTenancy());
        assertEquals(resultVO.getNameTag(), vpcList.get(0).getTags().get(0).getValue()+ ", " + vpcList.get(0).getTags().get(1).getValue());
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : AWS VPC 생성 TEST
    * @title : testSaveAwsVpcInfo
    * @return : void
    ***************************************************/
    @Test
    public void testSaveAwsVpcInfo(){
        getAwsAccountInfo();
        getAwsRegionInfo();
        AwsVpcMgntDTO dto = setAwsVpcInfo();
        mockAwsVpcMgntService.saveAwsVpcInfo(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : AWS VPC 삭제 TEST
    * @title : AWS VPC 삭제 TEST
    * @return : void
    ***************************************************/
    @Test
    public void testDeleteAwsVpcInfo(){
        getAwsAccountInfo();
        getAwsRegionInfo();
        AwsVpcMgntDTO dto = setAwsVpcInfo();
        mockAwsVpcMgntService.deleteAwsVpcInfo(dto, principal);
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : AWS VPC 정보 설정
    * @title : setAwsVpcInfo
    * @return : AwsVpcMgntDTO
    ***************************************************/
    public AwsVpcMgntDTO setAwsVpcInfo() {
        AwsVpcMgntDTO dto = new AwsVpcMgntDTO();
        dto.setAccountId(1);
        dto.setIpv4CirdBlock("172.16.100.0/24");
        dto.setNameTag("vpcName");
        dto.setVpcId("vpcId");
        dto.setRegion("us-west-2");
        dto.setIpv6CirdBlock(false);
        dto.setTenancy("tenancy");
        dto.getAccountId();
        dto.getIpv4CirdBlock();
        dto.getNameTag();
        dto.getTenancy();
        dto.getVpcId();
        dto.getRegion();
        dto.isIpv6CirdBlock();
        return dto;
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
        List<Tag> tags = new ArrayList<Tag>();
        Tag newTag = new Tag();
        newTag.setKey("Name");
        newTag.setValue("VpcName");
        tags.add(newTag);
        if(type.equals("nameTag")){
            newTag = new Tag();
            newTag.setKey("Name");
            newTag.setValue("VpcName2");
            tags.add(newTag);
        }
        vpc.setTags(tags);
        List<VpcIpv6CidrBlockAssociation> vpcIpv6s = new ArrayList<VpcIpv6CidrBlockAssociation>();
        VpcIpv6CidrBlockAssociation vpcIpv6 = new VpcIpv6CidrBlockAssociation();
        vpcIpv6.setIpv6CidrBlock("testIpv6Cidr");
        vpcIpv6s.add(vpcIpv6);
        if(type.equals("ipv6Cidr")){
            vpcIpv6 = new VpcIpv6CidrBlockAssociation();
            vpcIpv6.setIpv6CidrBlock("testIpv6Cidr2");
            vpcIpv6s.add(vpcIpv6);
        }
        vpc.setIpv6CidrBlockAssociationSet(vpcIpv6s);
        vpcList.add(vpc);
        return vpcList;
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
        
        when(mockCommonIaasService.getAwsRegionInfo(any())).thenReturn(Region.getRegion(Regions.fromName("us-west-2")));
        return vo;
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
    
    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : AWS VPC API 호출이 아닌 다른 VPC 상세 정보 결과 값 설정 
    * @title : getVpcDetailInfo
    * @return : void
    ***************************************************/
    public void getVpcDetailInfo(){
        HashMap<String, Boolean> map = new HashMap<String, Boolean>();
        map.put("DNShostnames", false);
        map.put("DNSresolution", false);
        when(mockAwsVpcMgntApiService.getVpcDnsNameInfoFromAws(any(), anyString(), anyString())).thenReturn(map);
        when(mockAwsVpcMgntApiService.getRouteTableInfoFromAws(any(), anyString(), anyString())).thenReturn("route-1111");
        when(mockAwsVpcMgntApiService.getNetworkAcleInfoFromAws(any(), anyString(), anyString())).thenReturn("ncl-1111");
    }
    
}