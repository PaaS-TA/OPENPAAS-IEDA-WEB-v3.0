package org.openpaas.ieda.iaasDashboard.openstackMgnt.api.securityGroup;

import java.util.List;

import org.openpaas.ieda.common.web.common.service.CommonApiService;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.web.securityGroup.dto.OpenstackSecurityGroupMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.IPProtocol;
import org.openstack4j.model.compute.SecGroupExtension;
import org.openstack4j.model.network.SecurityGroupRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OpenstackSecurityGroupMgntApiService {
    
    @Autowired
    CommonApiService commonApiService;
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : 받아온 Openstack 정보를 통해 OSClientV2 객체 생성
    * @title : getOpenstackClientV2
    * @return : OSClientV2
    ***************************************************/
    public OSClientV2 getOpenstackClientV2(IaasAccountMgntVO vo){
        OSClientV2 os= commonApiService.getOSClientFromOpenstackV2(vo.getCommonAccessEndpoint(), vo.getCommonTenant(), vo.getCommonAccessUser(), vo.getCommonAccessSecret());
        return os;
    }
    
    /***************************************************
     * @project : OPENSTACK 인프라 관리 대시보드
     * @description : 받아온 Openstack 정보를 통해 OSClientV3 객체 생성
     * @title : getOpenstackClientV3
     * @return : OSClientV3
     ***************************************************/
     public OSClientV3 getOpenstackClientV3(IaasAccountMgntVO vo){
         OSClientV3 os= commonApiService.getOSClientFromOpenstackV3(vo.getCommonAccessEndpoint(), vo.getOpenstackDomain(), vo.getCommonProject(), vo.getCommonAccessUser(), vo.getCommonAccessSecret());
         return os;
     }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 보안 그룹 목록 정보 조회 실제 API 호출
    * @title : getOpenstackSecrityGroupInfoListFromOpenstack
    * @return : List<? extends SecGroupExtension>
    ***************************************************/
    public List<? extends SecGroupExtension> getOpenstackSecrityGroupInfoListFromOpenstack(IaasAccountMgntVO vo) {
        List<? extends SecGroupExtension> securityGroupList = null;
        String version = vo.getOpenstackKeystoneVersion();
        if(version.equalsIgnoreCase("v2")){
            OSClientV2 os= getOpenstackClientV2(vo);
            securityGroupList = os.compute().securityGroups().list();
        }else if(version.equalsIgnoreCase("v3")){
            OSClientV3 os= getOpenstackClientV3(vo);
            securityGroupList = os.compute().securityGroups().list();
        }
        return securityGroupList;
    }

    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 보안 그룹 Ingress Rule 목록 정보 조회 실제 API 호출
    * @title : getOpenstackSecrityGroupIngressInfoFromOpenstack
    * @return : List<? extends Rule>
    ***************************************************/
    public List<? extends SecurityGroupRule> getOpenstackSecrityGroupIngressInfoFromOpenstack(IaasAccountMgntVO vo, String groupId) {
        List<? extends SecurityGroupRule> rules = null;
        String version = vo.getOpenstackKeystoneVersion();
        if(version.equalsIgnoreCase("v2")){
            OSClientV2 os= getOpenstackClientV2(vo);
            rules = os.networking().securitygroup().get(groupId).getRules();
        }else if(version.equalsIgnoreCase("v3")){
            OSClientV3 os= getOpenstackClientV3(vo);
            rules = os.networking().securitygroup().get(groupId).getRules();
        }
        return rules;
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 보안 그룹 생성 실제 API 호출
    * @title : saveOpenstackSecurityGroupInfo
    * @return : void
    ***************************************************/
    public String saveOpenstackSecurityGroupInfoFromOpenstack(IaasAccountMgntVO vo, OpenstackSecurityGroupMgntDTO dto) {
        String version = vo.getOpenstackKeystoneVersion();
        String groupId = "";
        if(version.equalsIgnoreCase("v2")){
            OSClientV2 os= getOpenstackClientV2(vo);
            groupId = os.compute().securityGroups().create(dto.getSecurityGroupName(), dto.getDescription()).getId();
        }else if(version.equalsIgnoreCase("v3")){
            OSClientV3 os= getOpenstackClientV3(vo);
            groupId = os.compute().securityGroups().create(dto.getSecurityGroupName(), dto.getDescription()).getId();
        }
        return groupId;
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 보안 그룹 Inbound Rule 생성 실제 API 호출
    * @title : saveOpenstackSecurityGroupInboundRuleFromOpenstack
    * @return : void
    ***************************************************/
    public void saveOpenstackSecurityGroupInboundRuleFromOpenstack(IaasAccountMgntVO vo,
            OpenstackSecurityGroupMgntDTO dto) {
        
        String version = vo.getOpenstackKeystoneVersion();
        if(version.equalsIgnoreCase("v2")){
            OSClientV2 os= getOpenstackClientV2(vo);
            
            IPProtocol protocol = null;
            for(int i =0; i< dto.getIngressRules().size(); i++){
                String[] ports = dto.getIngressRules().get(i).get("portRange").split("-");
                int fromPort = Integer.valueOf(ports[0]);
                int toPort = ports.length == 2 ? Integer.valueOf(ports[1]) : fromPort;
                if(dto.getIngressRules().get(i).get("protocol").equalsIgnoreCase("tcp")){
                        protocol = IPProtocol.TCP;
                }else{
                    protocol = IPProtocol.UDP;
                }
                
                if(dto.getIngressRules().get(i).get("protocol").equalsIgnoreCase("tcp") && dto.getIngressRules().get(i).get("portRange").equalsIgnoreCase("1-65535")){
                    os.compute().securityGroups()
                      .createRule(Builders.secGroupRule()
                      .parentGroupId(dto.getSecurityGroupId())
                      .protocol(protocol)
                      .cidr("0.0.0.0/0")
                      .range(fromPort, toPort).build());
                }else{
                    os.compute().securityGroups()
                        .createRule(Builders.secGroupRule()
                        .parentGroupId(dto.getSecurityGroupId())
                        .protocol(protocol)
                        .cidr("0.0.0.0/0")
                        .range(fromPort, toPort).build());
                }
            }
        }else if(version.equalsIgnoreCase("v3")){
            OSClientV3 os= getOpenstackClientV3(vo);
            IPProtocol protocol = null;
            for(int i =0; i< dto.getIngressRules().size(); i++){
                String[] ports = dto.getIngressRules().get(i).get("portRange").split("-");
                int fromPort = Integer.valueOf(ports[0]);
                int toPort = ports.length == 2 ? Integer.valueOf(ports[1]) : fromPort;
                if(dto.getIngressRules().get(i).get("protocol").equalsIgnoreCase("tcp")){
                        protocol = IPProtocol.TCP;
                }else{
                    protocol = IPProtocol.UDP;
                }
                
                if(dto.getIngressRules().get(i).get("protocol").equalsIgnoreCase("tcp") && dto.getIngressRules().get(i).get("portRange").equalsIgnoreCase("1-65535")){
                    os.compute().securityGroups()
                      .createRule(Builders.secGroupRule()
                      .parentGroupId(dto.getSecurityGroupId())
                      .protocol(protocol)
                      .cidr("0.0.0.0/0")
                      .range(fromPort, toPort).build());
                }else{
                    os.compute().securityGroups()
                        .createRule(Builders.secGroupRule()
                        .parentGroupId(dto.getSecurityGroupId())
                        .protocol(protocol)
                        .cidr("0.0.0.0/0")
                        .range(fromPort, toPort).build());
                }
            }
       }
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 보안 그룹 삭제 실제 API 호출
    * @title : deleteOpenstackSecurityGroupInfoFromOpenstack
    * @return : int
    ***************************************************/
    public int deleteOpenstackSecurityGroupInfoFromOpenstack(IaasAccountMgntVO vo, String securityGroupId) {
        String version = vo.getOpenstackKeystoneVersion();
        int code = 0;
        ActionResponse response = null;
        if(version.equalsIgnoreCase("v2")){
            OSClientV2 os= getOpenstackClientV2(vo);
            response = os.compute().securityGroups().delete(securityGroupId);
        }else{
            OSClientV3 os= getOpenstackClientV3(vo);
            response = os.compute().securityGroups().delete(securityGroupId);
        }
        if( response != null ) {
            code = response.getCode();
        }
        return code;
    }
}
