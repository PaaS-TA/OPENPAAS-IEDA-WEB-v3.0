package org.openpaas.ieda.iaasDashboard.openstackMgnt.web.securityGroup.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.api.securityGroup.OpenstackSecurityGroupMgntApiService;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.web.securityGroup.dao.OpenstackSecurityGroupMgntVO;
import org.openpaas.ieda.iaasDashboard.openstackMgnt.web.securityGroup.dto.OpenstackSecurityGroupMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.openstack4j.model.compute.SecGroupExtension;
import org.openstack4j.model.network.SecurityGroupRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class OpenstackSecurityGroupMgntService {
    
    @Autowired OpenstackSecurityGroupMgntApiService openstackSecurityGroupMgntApiService;
    @Autowired CommonIaasService commonIaasService;
    @Autowired MessageSource message;
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 계정 정보가 실제 존재 하는지 확인 및 상세 조회
    * @title : getOpenstackAccountInfo
    * @return : IaasAccountMgntVO
    ***************************************************/
    public IaasAccountMgntVO getOpenstackAccountInfo(Principal principal, int accountId){
        return commonIaasService.getIaaSAccountInfo(principal, accountId, "openstack");
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 보안 그룹 목록 정보 조회
    * @title : getOpenstackSecrityGroupInfoList
    * @return : List<OpenstackNetworkMgntVO>
    ***************************************************/
    public List<OpenstackSecurityGroupMgntVO> getOpenstackSecrityGroupInfoList(Principal principal, int accountId) {
        IaasAccountMgntVO vo =  getOpenstackAccountInfo(principal, accountId);
        List<? extends SecGroupExtension> securityGroupList = openstackSecurityGroupMgntApiService.getOpenstackSecrityGroupInfoListFromOpenstack(vo);
        List<OpenstackSecurityGroupMgntVO> resultList = new ArrayList<OpenstackSecurityGroupMgntVO>();
        int recid = 1;
        if(securityGroupList != null && securityGroupList.size() != 0){
            for(int i=0; i<securityGroupList.size(); i++){
                OpenstackSecurityGroupMgntVO sgVo = new OpenstackSecurityGroupMgntVO();
                sgVo.setSecurityGroupId(securityGroupList.get(i).getId());
                sgVo.setSecurityGroupName(securityGroupList.get(i).getName());
                sgVo.setDescription(securityGroupList.get(i).getDescription());
                sgVo.setRecid(recid);
                sgVo.setAccountId(accountId);
                recid ++;
                resultList.add(sgVo);
            }
        }
        return resultList;
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 보안 그룹 Ingress Rules 목록 정보 조회
    * @title : getOpenstackSecrityGroupIngressInfo
    * @return : List<HashMap<String, Object>>
    ***************************************************/
    public List<HashMap<String, Object>> getOpenstackSecrityGroupIngressInfo(int accountId, String groupId,
            Principal principal) {
        IaasAccountMgntVO vo =  getOpenstackAccountInfo(principal, accountId);
        List<? extends SecurityGroupRule> rules;
        try{
          
             rules = openstackSecurityGroupMgntApiService.getOpenstackSecrityGroupIngressInfoFromOpenstack(vo, groupId);
        }catch (Exception e) {
            String detailMessage = e.getMessage();
            if(!detailMessage.equals("") && detailMessage != null){
                throw new CommonException(
                  detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
            }else{
                throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                        message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
            }
        }
        List<HashMap<String, Object>> resultRulesInfo = setOpenstackRulesList(rules, vo);
        return resultRulesInfo;
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 보안 그룹 Ingress Rules 목록 정보 출력 값 설정
    * @title : setOpenstackRulesList
    * @return : List<HashMap<String, Object>>
    ***************************************************/
    public List<HashMap<String, Object>> setOpenstackRulesList(List<? extends SecurityGroupRule> rules, IaasAccountMgntVO vo) {
        List<HashMap<String, Object>> maps = new ArrayList<HashMap<String, Object>>();
        if(rules != null && rules.size() != 0){
            for(int i=0; i<rules.size(); i++){
                HashMap<String, Object> map = new HashMap<String, Object>();
                //EtherType 갑 설정
                if(!rules.get(i).getDirection().equalsIgnoreCase("egress")){
                    setEtherType(map, rules.get(i));
                    //Remote 값 설정
                    setRemote(map, rules.get(i), vo);
                    //IP protocol 값 설정
                    setIpProtocol(map, rules.get(i));
                    //port Range 값 설정
                    setPortRange(map, rules.get(i));
                    maps.add(map);
                }
            }
        }
        return maps;
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 보안 그룹 Ingress Rules port Range 값 설정
    * @title : setPortRange
    * @return : void
    ***************************************************/
    public void setPortRange(HashMap<String, Object> map, SecurityGroupRule securityGroupRule) {
        if(securityGroupRule.getPortRangeMax() != null && securityGroupRule.getPortRangeMin() != null){
            if(String.valueOf(securityGroupRule.getPortRangeMin()).equalsIgnoreCase(String.valueOf(securityGroupRule.getPortRangeMax()))
                    && securityGroupRule.getPortRangeMin() != -1 && securityGroupRule.getPortRangeMin() != 0){
                map.put("portRange", securityGroupRule.getPortRangeMin() );
            }else if(securityGroupRule.getPortRangeMin() == -1 && securityGroupRule.getPortRangeMax() == -1){
                map.put("portRange", "-");
            }else if(securityGroupRule.getPortRangeMin() == 0 && securityGroupRule.getPortRangeMax() == 0){
                map.put("portRange", "-");
            }else{
                map.put("portRange", securityGroupRule.getPortRangeMin() + "-" + securityGroupRule.getPortRangeMax());
            }
        }
    }

    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 보안 그룹 Ingress Rules IP protocol 값 설정
    * @title : setIpPritocol
    * @return : void
    ***************************************************/
    public void setIpProtocol(HashMap<String, Object> map, SecurityGroupRule securityGroupRule) {
        if(securityGroupRule.getProtocol()!= null){
            map.put("IpProtocol", securityGroupRule.getProtocol());
        }else{
            map.put("IpProtocol", "any");
        }
    }

    /***************************************************
    * @param vo 
     * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 보안 그룹 Ingress Rules Remote 값 설정
    * @title : setRemote
    * @return : void
    ***************************************************/
    public void setRemote(HashMap<String, Object> map, SecurityGroupRule securityGroupRule, IaasAccountMgntVO vo) {
        if(securityGroupRule.getRemoteGroupId() != null){
            List<? extends SecGroupExtension> securityGroupList = openstackSecurityGroupMgntApiService.getOpenstackSecrityGroupInfoListFromOpenstack(vo);
            if(securityGroupList != null && securityGroupList.size() != 0){
                for(int i=0; i<securityGroupList.size(); i++){
                    if(securityGroupList.get(i).getId().equalsIgnoreCase(securityGroupRule.getRemoteGroupId())){
                        map.put("remote", securityGroupList.get(i).getName());
                    }
                }
            }
        }else{
            map.put("remote", securityGroupRule.getRemoteIpPrefix());
        }
    }

    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 보안 그룹 Ingress Rules Ether type 값 설정
    * @title : setEtherType
    * @return : void
    ***************************************************/
    public void setEtherType(HashMap<String, Object> map, SecurityGroupRule securityGroupRule) {
        map.put("etherType", securityGroupRule.getEtherType());
        map.put("direction", securityGroupRule.getDirection());
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 보안 그룹 생성
    * @title : saveOpenstackSecurityGroupInfo
    * @return : void
    ***************************************************/
    public void saveOpenstackSecurityGroupInfo(OpenstackSecurityGroupMgntDTO dto, Principal principal) {
        IaasAccountMgntVO vo =  getOpenstackAccountInfo(principal, dto.getAccountId());
        String groupId = "";
        try{
            groupId = openstackSecurityGroupMgntApiService.saveOpenstackSecurityGroupInfoFromOpenstack(vo,dto);
        }catch (Exception e) {
            String detailMessage = e.getMessage();
            if(!detailMessage.equals("") && detailMessage != null){
                throw new CommonException(
                  detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
            }else{
                throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                        message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
            }
        }
        
        if( !StringUtils.isEmpty(groupId) && !dto.getIngressRuleType().equalsIgnoreCase("none") ){
            dto.setSecurityGroupId(groupId);
            saveOpenstackSecurityGroupInboundRule(dto, principal);
        }
    }
    
    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 보안 그룹 Inbound Rule 생성
    * @title : saveOpenstackSecurityGroupInfo
    * @return : void
    ***************************************************/
    public void saveOpenstackSecurityGroupInboundRule(OpenstackSecurityGroupMgntDTO dto, Principal principal) {
        IaasAccountMgntVO vo =  getOpenstackAccountInfo(principal, dto.getAccountId());
        try{
            openstackSecurityGroupMgntApiService.saveOpenstackSecurityGroupInboundRuleFromOpenstack(vo,dto);
        }catch (Exception e) {
            String detailMessage = e.getMessage();
            if(!detailMessage.equals("") && detailMessage != null){
                throw new CommonException(
                  detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
            }else{
                throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                        message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
            }
        }
    }

    /***************************************************
    * @project : OPENSTACK 인프라 관리 대시보드
    * @description : OPENSTACK 보안 그룹 삭제
    * @title : deleteOpenstackSecurityGroupInfo
    * @return : void
    ***************************************************/
    public void deleteOpenstackSecurityGroupInfo(OpenstackSecurityGroupMgntDTO dto, Principal principal) {
        IaasAccountMgntVO vo =  getOpenstackAccountInfo(principal, dto.getAccountId());
        int statusCode =  openstackSecurityGroupMgntApiService.deleteOpenstackSecurityGroupInfoFromOpenstack(vo, dto.getSecurityGroupId());
        if( !(statusCode == HttpStatus.OK.value() || statusCode == HttpStatus.ACCEPTED.value()) ){
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
    }
}
