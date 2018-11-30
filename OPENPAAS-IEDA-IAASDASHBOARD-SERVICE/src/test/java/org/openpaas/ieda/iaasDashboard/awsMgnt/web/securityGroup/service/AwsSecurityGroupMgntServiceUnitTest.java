package org.openpaas.ieda.iaasDashboard.awsMgnt.web.securityGroup.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.common.base.BaseAwsMgntControllerUnitTest;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.securityGroup.dao.AwsSecurityGroupMgntVO;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.securityGroup.dto.AwsSecurityGroupMgntDTO;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.securityGroup.service.AwsSecurityGroupMgntApiService;
import org.openpaas.ieda.iaasDashboard.awsMgnt.web.securityGroup.service.AwsSecurityGroupMgntService;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.dao.CommonCodeVO;
import org.openpaas.ieda.iaasDashboard.web.common.dao.CommonIaasDAO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.appstream.model.Application;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.IpRange;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.UserIdGroupPair;
import com.amazonaws.services.ec2.model.Vpc;
import com.amazonaws.services.ec2.model.VpcIpv6CidrBlockAssociation;

@SpringApplicationConfiguration(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class AwsSecurityGroupMgntServiceUnitTest extends BaseAwsMgntControllerUnitTest {
    @InjectMocks AwsSecurityGroupMgntService mockAwsSecurityGroupMgntService;
    @Mock AwsSecurityGroupMgntApiService mockAwsSecurityGroupMgntApiService;
    @Mock CommonIaasService mockCommonIaasService;
    @Mock MessageSource mockMessageSource;
    @Mock CommonIaasDAO mockCommonIaasDao;
    
    private Principal principal = null;
    
    
    /****************************************************************
     * @project : AWS 관리 대시보드
     * @description : 하나의 메소드가 실행되기전 호출
     * @title : setUp
     * @return : void
    *****************************************************************/
    @Before
    public void setUp() throws Exception{
        MockitoAnnotations.initMocks(this);
        principal = getLoggined();
    }
    
    /****************************************************************
     * @project : AWS 관리 대시보드
     * @description : Security Group 목록 조회
     * @title : testGetAwsSecurityGroupInfoList
     * @return : void
    *****************************************************************/
    @Test
    public void testGetAwsSecurityGroupInfoList(){
        List<SecurityGroup> awsSecurityGroupList = getAwsSecurityGroupResultInfo();
        
        when( mockCommonIaasService.getIaaSAccountInfo(principal, 1, "AWS")).thenReturn(getAwsAccountInfo());
        when( mockCommonIaasService.getAwsRegionInfo(anyString()) ).thenReturn(Region.getRegion(Regions.US_WEST_2));
        when(mockAwsSecurityGroupMgntApiService.getAwsSecurityGroupInfoListApiFromAws(any(), any())).thenReturn(awsSecurityGroupList);
        List<AwsSecurityGroupMgntVO> resultList =  mockAwsSecurityGroupMgntService.getAwsSecurityGroupInfoList(principal, 1, "us-west-2");
        assertEquals(resultList.size(), 1);
        assertEquals(resultList.get(0).getGroupId(), awsSecurityGroupList.get(0).getGroupId());
        assertEquals(resultList.get(0).getGroupName(), awsSecurityGroupList.get(0).getGroupName());
        assertEquals(resultList.get(0).getVpcId(), awsSecurityGroupList.get(0).getVpcId());
        assertEquals(resultList.get(0).getDescription(), awsSecurityGroupList.get(0).getDescription());
    }
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : Security Group Ingress Rules 조회
     * @title : testGetAwsSecurityGroupRules
     * @return : void
     ***************************************************/
    @Test
    public void testGetAwsSecurityGroupRules(){
        List<SecurityGroup> groupList = getAwsSecurityGroupResultInfo();
        when(mockAwsSecurityGroupMgntApiService.getAwsSecurityGroupRulesInfoFromAws(any(), anyString(), any())).thenReturn(groupList);
        when(mockMessageSource.getMessage(any(), any(), any())).thenReturn("30000");
        when( mockCommonIaasDao.selectIngressRulesInfoBySubGroupCode(any(), anyString(), any())).thenReturn(setIngressCustomTcpRuleInfo());
        
        List<HashMap<String, Object>> resultLists =  mockAwsSecurityGroupMgntService.getAwsSecurityGroupRules(1, "sg-75aa370f", "us-west-2", principal);
        assertEquals(resultLists.size(), resultLists.size());
        assertEquals(resultLists.get(0).get("protocol"), groupList.get(0).getIpPermissions().get(0).getIpProtocol().toUpperCase());
        assertEquals(resultLists.get(0).get("portRange"), String.valueOf(groupList.get(0).getIpPermissions().get(0).getToPort()));
        assertEquals(resultLists.get(0).get("trafficType"),
                "Custom " + groupList.get(0).getIpPermissions().get(0).getIpProtocol().toUpperCase() + " Rule");
        assertEquals(resultLists.get(0).get("source"), groupList.get(0).getIpPermissions().get(0).getUserIdGroupPairs().get(0).getGroupId());
        
    }
    
    /****************************************************************
     * @project : AWS 관리 대시보드
     * @description : API에서 인바운드 규칙 조회
     * @title : testSetInboundRuleInfoList
     * @return : void
    *****************************************************************/
    @Test
    public void testSetInboundRuleInfoList(){
        List<SecurityGroup> groupList = getAwsSecurityGroupResultInfo();
        when(mockAwsSecurityGroupMgntApiService.getAwsSecurityGroupRulesInfoFromAws(any(), anyString(), any())).thenReturn(groupList);
        when(mockMessageSource.getMessage(any(), any(), any())).thenReturn("30000");
        when( mockCommonIaasDao.selectIngressRulesInfoBySubGroupCode(any(), anyString(), any())).thenReturn(setIngressCustomTcpRuleInfo());
        
        
        List<HashMap<String, Object>> resultLists =  mockAwsSecurityGroupMgntService.getAwsSecurityGroupRules(1, "sg-75aa370f", "us-west-2", principal);
        assertEquals(resultLists.size(), resultLists.size());
        assertEquals(resultLists.get(0).get("protocol"), groupList.get(0).getIpPermissions().get(0).getIpProtocol().toUpperCase());
        assertEquals(resultLists.get(0).get("portRange"), String.valueOf(groupList.get(0).getIpPermissions().get(0).getToPort()));
        assertEquals(resultLists.get(0).get("trafficType"),
                "Custom " + groupList.get(0).getIpPermissions().get(0).getIpProtocol().toUpperCase() + " Rule");
        assertEquals(resultLists.get(0).get("source"), groupList.get(0).getIpPermissions().get(0).getUserIdGroupPairs().get(0).getGroupId());
    }
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : Security Group 생성
     * @title : testSaveAwsSecurityGroupInfo
     * @return : void
     ***************************************************/
    @Test
    public void testSaveAwsSecurityGroupInfo(){
        IaasAccountMgntVO account = getAwsAccountInfo();
        when( mockCommonIaasService.getIaaSAccountInfo(principal, 1, "AWS")).thenReturn(account);
        when( mockCommonIaasService.getAwsRegionInfo("us-west-2") ).thenReturn(Region.getRegion(Regions.US_WEST_2));
        when( mockAwsSecurityGroupMgntApiService.saveSecurityGroupFromAws(any(), any(), any()) ).thenReturn("sg-12345");
        when( mockAwsSecurityGroupMgntApiService.saveSecurityGroupIngressRuleFromAws(any(), any(), any()) ).thenReturn(true);
        
        AwsSecurityGroupMgntDTO dto = setAwsSecurityGroupInfo();
        mockAwsSecurityGroupMgntService.saveAwsSecurityGroupInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : AWS 관리 대시보드
     * @description : Ingress Rules 프로토콜 정보 (Custom Protocol 경우) 
     * @title : testSetIpProtocolInfoFromCustomProtocol
     * @return : void
    *****************************************************************/
    @Test
    public void testSetIpProtocolInfoFromCustomProtocol(){
        when( mockMessageSource.getMessage(anyString(), any(), any()) ).thenReturn("30000");
        when( mockCommonIaasDao.selectIngressRulesInfoBySubGroupCode(
                any(), anyString(), any())).thenReturn(setIngressCustomTcpRuleNumberInfo());
        
        IpPermission ipPermission = new IpPermission();
        ipPermission.withIpProtocol("4")
                     .withFromPort(-1)
                     .withUserIdGroupPairs(new UserIdGroupPair().withGroupId("sg-1234"));
        
        mockAwsSecurityGroupMgntService.setIpProtocolInfo(ipPermission);
        
        
    }
    
    /****************************************************************
     * @project : AWS 관리 대시보드
     * @description : Ingress Rules 프로토콜 정보 (Icmp Protocol 경우) 
     * @title : testSetIpProtocolInfoFromIcmpProtocol
     * @return : void
    *****************************************************************/
    @Test
    public void testSetIpProtocolInfoFromIcmpProtocol(){
        when( mockMessageSource.getMessage(anyString(), any(), any()) ).thenReturn("30000");
        when( mockCommonIaasDao.selectIngressRulesInfo(
                any(), anyString(), any())).thenReturn(setIngressCustomIcmpRuleInfo());
        
        when( mockCommonIaasDao.selectIngressRulesInfoBySubGroupCode(
                any(), anyString(), any())).thenReturn(setIngressCustomIcmpRuleSubGroupInfo());
        
        when( mockCommonIaasDao.selectIngressRulesInfoBySubGroupCodeAndUsubGroupCode(
                any(), any(), any(), any()) ).thenReturn(setIngressCustomIcmpRuleUsubGroupInfo());
        
        IpPermission ipPermission = new IpPermission();
        ipPermission.withIpProtocol("icmp")
                     .withFromPort(3)
                     .withToPort(0)
                     .withUserIdGroupPairs(new UserIdGroupPair().withGroupId("sg-1234"));
        
        mockAwsSecurityGroupMgntService.setIpProtocolInfo(ipPermission);
    }
    
    /****************************************************************
     * @project : AWS 관리 대시보드
     * @description :  Ingress Rules 프로토콜 정보 (TCP 22번 포트 Protocol 경우) 
     * @title : testSetIpProtocolInfoFromTcpProtocol
     * @return : void
    *****************************************************************/
    @Test
    public void testSetIpProtocolInfoFromTcpProtocol(){
        when( mockMessageSource.getMessage(anyString(), any(), any()) ).thenReturn("30000");
        when( mockCommonIaasDao.selectIngressRulesInfo(
                any(), anyString(), any())).thenReturn(setIngressCustomTcpRuleInfo());
        
        IpPermission ipPermission = new IpPermission();
        ipPermission.withIpProtocol("tcp")
                     .withFromPort(22)
                     .withToPort(22)
                     .withUserIdGroupPairs(new UserIdGroupPair().withGroupId("sg-1234"));
        
        mockAwsSecurityGroupMgntService.setIpProtocolInfo(ipPermission);
    }
    
    /****************************************************************
     * @project : AWS 관리 대시보드
     * @description : Security Group  Ingress Rules 생성 Exception
     * @title : testSaveAwsSecurityGroupRuleFromException
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testSaveAwsSecurityGroupRuleFromException(){
        IaasAccountMgntVO account = getAwsAccountInfo();
        when( mockCommonIaasService.getIaaSAccountInfo(principal, 1, "AWS")).thenReturn(account);
        when( mockCommonIaasService.getAwsRegionInfo("us-west-2") ).thenReturn(Region.getRegion(Regions.US_WEST_2));
        when( mockAwsSecurityGroupMgntApiService.saveSecurityGroupIngressRuleFromAws(any(), any(), any()) ).thenReturn(false);
        
        AwsSecurityGroupMgntDTO dto = setAwsSecurityGroupInfo();
        mockAwsSecurityGroupMgntService.saveAwsSecurityGroupRule(dto, principal,Region.getRegion(Regions.US_WEST_2));
    }
    
    /****************************************************************
     * @project : AWS 관리 대시보드
     * @description : 조회한 인바운드 Rules source 정보 설정 (ipv4 Range 경우)
     * @title : testSetIpProtocolSourceInfoFromIpv4Ragnges
     * @return : void
    *****************************************************************/
    @Test
    public void testSetIpProtocolSourceInfoFromIpv4Ragnges(){
        IpPermission ipPermisson = new IpPermission();
        IpRange ipRange = new IpRange();
        ipRange.setCidrIp("10.10.0.0/24");
        ipPermisson.withIpv4Ranges(ipRange);
        
        mockAwsSecurityGroupMgntService.setIpProtocolSourceInfo(ipPermisson);
    }
    
    /****************************************************************
     * @project : AWS 관리 대시보드
     * @description : 조회한 인바운드 Rules source 정보 설정 (else 경우)
     * @title : testSetIpProtocolSourceInfoFromElse
     * @return : void
    *****************************************************************/
    @Test
    public void testSetIpProtocolSourceInfoFromElse(){
        IpPermission ipPermisson = new IpPermission();
        mockAwsSecurityGroupMgntService.setIpProtocolSourceInfo(ipPermisson);
    }
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : Security Group 삭제
     * @title : testDeleteAwsSecurityGroupInfo
     * @return : void
     ***************************************************/
    @Test
    public void testDeleteAwsSecurityGroupInfo(){
        when( mockCommonIaasService.getIaaSAccountInfo(principal, 1, "AWS")).thenReturn(getAwsAccountInfo());
        when( mockCommonIaasService.getAwsRegionInfo("us-west-2") ).thenReturn(Region.getRegion(Regions.US_WEST_2));
        when( mockAwsSecurityGroupMgntApiService.deleteSecurityGroupInfoFromAws(any(), any(), any()) ).thenReturn(true);
        AwsSecurityGroupMgntDTO dto = setAwsSecurityGroupInfo();
        mockAwsSecurityGroupMgntService.deleteAwsSecurityGroupInfo(dto, principal);
    }
    
    /****************************************************************
     * @project : AWS 관리 대시보드
     * @description : Security Group 삭제 
     * @title : testDeleteAwsSecurityGroupInfoFromExceptionCase
     * @return : void
    *****************************************************************/
    @Test(expected=CommonException.class)
    public void testDeleteAwsSecurityGroupInfoFromExceptionCase(){
        when( mockCommonIaasService.getIaaSAccountInfo(principal, 1, "AWS")).thenReturn(getAwsAccountInfo());
        when( mockCommonIaasService.getAwsRegionInfo("us-west-2") ).thenReturn(Region.getRegion(Regions.US_WEST_2));
        when( mockAwsSecurityGroupMgntApiService.deleteSecurityGroupInfoFromAws(any(), any(), any()) ).thenReturn(false);
        AwsSecurityGroupMgntDTO dto = setAwsSecurityGroupInfo();
        mockAwsSecurityGroupMgntService.deleteAwsSecurityGroupInfo(dto, principal);
    }
    
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : AWS Security Group 정보 설정
     * @title : setAwsSecurityGroupInfo
     * @return : AwsSecurityGroupMgntDTO
     ***************************************************/
     public AwsSecurityGroupMgntDTO setAwsSecurityGroupInfo() {
         AwsSecurityGroupMgntDTO dto = new AwsSecurityGroupMgntDTO();
         dto.setAccountId(1);
         dto.setNameTag("nameTag");
         dto.setGroupName("groupName");
         dto.setDescription("description");
         dto.setVpcId("vpcId");
         dto.setIngressRuleType("bosh-security");
         List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
         HashMap<String, String> ingress = new HashMap<>();
         ingress.put("protocol", "ssh");
         ingress.put("portRange", "22");
         list.add(ingress);
         dto.setIngressRules( list );
         
         dto.getNameTag();
         dto.getAccountId();
         dto.getGroupName();
         dto.getDescription();
         dto.getVpcId();
         dto.getIngressRules();
         dto.getIngressRuleType();
         return dto;
     }
     
     /****************************************************************
     * @project : AWS 관리 대시보드
     * @description : Ingress Rules SSH 프로토콜 정보 설정
     * @title : setIngressRulesInfoBYSubGroupCode
     * @return : CommonIaasVO
    *****************************************************************/
    public CommonCodeVO setIngressCustomTcpRuleInfo(){
        CommonCodeVO vo = new CommonCodeVO();
        vo.setParentCode("30000");
        vo.setCodeName("SSH (22)");
        vo.setCodeNameKr("SSH (22)");
        vo.setCodeValue("22");
        vo.setCodeDescription("Custom Protocol Rule_SSH");
        vo.setSubGroupCode(null);
        vo.setuSubGroupCode(null);
        
        return vo;
     }
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Ingress Rules ICMP 프로토콜 정보 설정
     * @title : setIngressCustomIcmpRuleInfo
     * @return : CommonIaasVO
    *****************************************************************/
    public CommonCodeVO setIngressCustomIcmpRuleInfo(){
        CommonCodeVO vo = new CommonCodeVO();
        vo.setParentCode("30000");
        vo.setCodeName("Custom ICMP Rule");
        vo.setCodeNameKr("icmp");
        vo.setCodeValue("31000");
        vo.setCodeDescription("ICMP (1)");
        vo.setSubGroupCode(null);
        vo.setuSubGroupCode(null);
        
        return vo;
     }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Ingress Rules ICMP - Destination Unreachable 프로토콜 정보 설정
     * @title : setIngressCustomIcmpRuleSubGroupInfo
     * @return : CommonIaasVO
    *****************************************************************/
    public CommonCodeVO setIngressCustomIcmpRuleSubGroupInfo(){
        CommonCodeVO vo = new CommonCodeVO();
        vo.setParentCode("30000");
        vo.setCodeName("Destination Unreachable");
        vo.setCodeNameKr("Destination Unreachable (3)");
        vo.setCodeValue("31100");
        vo.setCodeDescription("ICMP (1)");
        vo.setSubGroupCode("31000");
        vo.setuSubGroupCode(null);
        
        return vo;
     }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Ingress Rules 
     *      Destination Unreachable/Destination Network Unreachable 프로토콜 정보 설정
     * @title : setIngressCustomIcmpRuleUsubGroupInfo
     * @return : CommonIaasVO
    *****************************************************************/
    public CommonCodeVO setIngressCustomIcmpRuleUsubGroupInfo(){
        CommonCodeVO vo = new CommonCodeVO();
        vo.setParentCode("30000");
        vo.setCodeName("Destination Unreachable / Destination Network Unreachable");
        vo.setCodeNameKr("Destination Network Unreachable (0)");
        vo.setCodeValue("0");
        vo.setCodeDescription("ICMP (1)");
        vo.setSubGroupCode("31000");
        vo.setuSubGroupCode("31100");
        
        return vo;
     }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  Ingress Rules Ipv4 프로토콜 정보 설정1
     * @title : setIngressCustomTcpRuleNumberInfo
     * @return : CommonIaasVO
    *****************************************************************/
    public CommonCodeVO setIngressCustomTcpRuleNumberInfo(){
        CommonCodeVO vo = new CommonCodeVO();
        vo.setParentCode("30000");
        vo.setCodeName("IPv4 (4)");
        vo.setCodeNameKr("IPv4 (4)");
        vo.setCodeValue("-1");
        vo.setCodeDescription("Custom Protocol Rule_IPv4 (4)");
        vo.setSubGroupCode("32000");
        vo.setuSubGroupCode(null);
        
        return vo;
     }
     
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : AWS API Security Group 목록 조회 결과 값 설정
     * @title : getAwsSecurityGroupResultInfo
     * @return : List<SecurityGroup>
     ***************************************************/
    private List<SecurityGroup> getAwsSecurityGroupResultInfo(){
        List<SecurityGroup> securityGroupList = new ArrayList<SecurityGroup>();
        SecurityGroup group = new SecurityGroup();
        IpPermission ipPermission = new IpPermission();
        ipPermission.withUserIdGroupPairs(new UserIdGroupPair().withGroupId("sg-75aa370f"))
                    .withIpProtocol("tcp")
                    .withFromPort(22)
                    .withToPort(22);
        
        group.withGroupId("sg-75aa370f");
        group.withGroupName("group-name-test");
        group.withVpcId("vpcId-test");
        group.withDescription("decription-test");
        group.withIpPermissions(ipPermission);
        
        securityGroupList.add(group);
        return securityGroupList;
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
          return vo;
      }
    
}