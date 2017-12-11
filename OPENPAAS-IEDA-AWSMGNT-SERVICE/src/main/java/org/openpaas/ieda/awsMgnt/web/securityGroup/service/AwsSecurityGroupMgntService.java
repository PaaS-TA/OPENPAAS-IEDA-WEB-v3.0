package org.openpaas.ieda.awsMgnt.web.securityGroup.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.openpaas.ieda.awsMgnt.web.securityGroup.dao.AwsSecurityGroupMgntVO;
import org.openpaas.ieda.awsMgnt.web.securityGroup.dto.AwsSecurityGroupMgntDTO;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.dao.CommonCodeVO;
import org.openpaas.ieda.iaasDashboard.web.common.dao.CommonIaasDAO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.amazonaws.regions.Region;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.SecurityGroup;

@Service
public class AwsSecurityGroupMgntService {

    @Autowired AwsSecurityGroupMgntApiService awsSecurityGroupMgntApiService;
    @Autowired CommonIaasService commonIaasService;
    @Autowired CommonIaasDAO commonIaasDao;
    @Autowired MessageSource message;
    
    /***************************************************
    * @project : AWS 관리 대시보드
    * @description : Security Group 목록 조회
    * @title : getAwsSecurityGroupInfoList
    * @return : List<AwsSecurityGroupMgntVO>
    ***************************************************/
    public List<AwsSecurityGroupMgntVO> getAwsSecurityGroupInfoList(Principal principal, int accountId, String regionName) {
        IaasAccountMgntVO vo =  getAwsAccountInfo(principal, accountId);
        Region region = getAwsRegionInfo(regionName);
        List<SecurityGroup> awsSecurityGroupList = awsSecurityGroupMgntApiService.getAwsSecurityGroupInfoListApiFromAws(vo, region);
         
        List<AwsSecurityGroupMgntVO> list = new ArrayList<AwsSecurityGroupMgntVO>();
        for (int i=0; i<awsSecurityGroupList.size(); i++ ){
            SecurityGroup securityGroup = awsSecurityGroupList.get(i);
            AwsSecurityGroupMgntVO awsGroupVo = new AwsSecurityGroupMgntVO();
            awsGroupVo.setGroupId(securityGroup.getGroupId());
            awsGroupVo.setGroupName(securityGroup.getGroupName());
            awsGroupVo.setVpcId(securityGroup.getVpcId());
            awsGroupVo.setDescription(securityGroup.getDescription());
            awsGroupVo.setRecid(i);
            awsGroupVo.setAccountId(vo.getId());
            list.add(awsGroupVo);
        }
        return list;
    }
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : Security Group Inbound Rules 조회
     * @title : getAwsSecurityGroupRules
     * @return : List<AwsSecurityGroupMgntVO>
     ***************************************************/
    public List<HashMap<String, Object>> getAwsSecurityGroupRules(int accountId, String groupId, String regionName, Principal principal) {
        IaasAccountMgntVO vo =  getAwsAccountInfo(principal, accountId);//계정 정보 조회
        //Inbound Rules 조회
        Region region = getAwsRegionInfo(regionName);
        List<SecurityGroup> groupList = awsSecurityGroupMgntApiService.getAwsSecurityGroupRulesInfoFromAws(vo, groupId, region);
        List<HashMap<String, Object>> maps = setInboundRuleInfoList(groupId, groupList);
       return maps;
    }
    
    /****************************************************************
     * @project : AWS 관리 대시보드
     * @description : API에서 인바운드 규칙 조회
     * @title : setInboundRulesData
     * @return : List<HashMap<String, Object>>
    *****************************************************************/
    public List<HashMap<String, Object>> setInboundRuleInfoList(String groupId, List<SecurityGroup> groupList){
        List<HashMap<String, Object>> maps = new ArrayList<HashMap<String, Object>>();
        
        for(int i=0; i<groupList.size(); i++){
            if(groupList.get(i).getGroupId().equals(groupId)){
                List<IpPermission> ipPermission = (List<IpPermission>) groupList.get(i).getIpPermissions();
                for( int j=0; j < ipPermission.size(); j++ ){
                    HashMap<String, Object> map = setIpProtocolInfo(ipPermission.get(j));
                    map.put("groupId", groupId);
                    maps.add(map);
                }
            }
        }
        
        return maps;
    }
    
    /****************************************************************
     * @project : AWS 관리 대시보드
     * @description : 조회한 Ingress Rules 프로토콜 정보 설정
     * @title : setInboundRuleData
     * @return : HashMap<String,Object>
    *****************************************************************/
    public HashMap<String, Object> setIpProtocolInfo(IpPermission ipPermission){
        final Integer parentCode = Integer.parseInt(message.getMessage("common.code.ingress.rules.parent", null, Locale.KOREA));
        final String icmp = message.getMessage("common.code.ingress.rules.icmp", null, Locale.KOREA);
        final String customProtocolRule = message.getMessage("common.code.ingress.rules.customProtocolRule", null, Locale.KOREA);
        HashMap<String, Object> map = new HashMap<String, Object>();
        String trafficType = null;
        String protocol = null;
        String portRange = null;
        CommonCodeVO vo = null;
        
        String portRange2 = ipPermission.getFromPort() != null ? String.valueOf(ipPermission.getFromPort()) : null;
        if( ipPermission.getToPort() != null ){
            portRange2 += (int)ipPermission.getToPort() == (int)ipPermission.getFromPort() ? "" : "-"+ipPermission.getToPort();
        }else {
            portRange2 = "-1";
        }
        
        if( StringUtils.isNumeric(ipPermission.getIpProtocol()) ){ 
            CommonCodeVO customProtocol = commonIaasDao.selectIngressRulesInfoBySubGroupCode(
                    parentCode
                   ,ipPermission.getIpProtocol()
                   ,Integer.parseInt(customProtocolRule));
            trafficType = "Custom Protocol Rule";
            protocol = customProtocol.getCodeName();
            portRange = customProtocol.getCodeValue();
        }else{
            if( ipPermission.getIpProtocol().equals("icmp") && ipPermission.getFromPort() > -1){
                vo = new CommonCodeVO();
                vo = commonIaasDao.selectIngressRulesInfo(parentCode, icmp, ipPermission.getIpProtocol());
                trafficType = vo.getCodeName();
                protocol = vo.getCodeDescription();
                if( vo.getCodeNameKr().equals("icmp") && Integer.parseInt(vo.getCodeValue()) > 30000 ){
                    portRange2 = ipPermission.getToPort() + "";
                    String fromPort = String.valueOf(ipPermission.getFromPort());
                    String toPort = String.valueOf(ipPermission.getToPort());
                    CommonCodeVO icmpProtocol = commonIaasDao.selectIngressRulesInfoBySubGroupCode(parentCode, fromPort, Integer.parseInt(icmp));
                    portRange = icmpProtocol.getCodeName();
                    if( Integer.parseInt(icmpProtocol.getCodeValue()) > 31000  ){
                        CommonCodeVO icmpProtocolDetail = commonIaasDao.selectIngressRulesInfoBySubGroupCodeAndUsubGroupCode(parentCode, toPort,
                                Integer.parseInt(icmp), Integer.parseInt(icmpProtocol.getCodeValue()));
                        
                        portRange = icmpProtocolDetail.getCodeName();
                    }
                }
            }else{
                vo = commonIaasDao.selectIngressRulesInfo(parentCode, portRange2, ipPermission.getIpProtocol());
                if( vo == null ){
                    trafficType = "Custom " + ipPermission.getIpProtocol().toUpperCase() + " Rule";
                    protocol = ipPermission.getIpProtocol().toUpperCase();
                    portRange = portRange2;
                }else{
                    trafficType = vo.getCodeName();
                    protocol = vo.getCodeDescription();
                    portRange = vo.getCodeValue();
                }
                
            }
        }
        map.put("trafficType", trafficType);
        map.put("protocol", protocol);
        if( portRange != null  ) {
            map.put("portRange", (portRange.equals("-1") || portRange.equals("0-65535") )? "ALL" : portRange );
        }
        
        String source = setIpProtocolSourceInfo(ipPermission);
        map.put("source",source);
        return map;
    }
    
    /****************************************************************
     * @project : AWS 관리 대시보드
     * @description : 조회한 인바운드 Rules source 정보 설정
     * @title : setIpProtocolSourceInfo
     * @return : String
    *****************************************************************/
    public String setIpProtocolSourceInfo(IpPermission ipPermission){
        String source =  "";
        if( ipPermission.getIpv4Ranges().size() >0 ){
            for( int k = 0; k<ipPermission.getIpv4Ranges().size(); k++ ){
                Object cidrIp = ipPermission.getIpv4Ranges().get(k).getCidrIp();
                source = cidrIp != null ? cidrIp.toString() : "-";
            }
        }else if( ipPermission.getUserIdGroupPairs().size() >0 ){
            for( int k=0; k<ipPermission.getUserIdGroupPairs().size(); k++ ){
                Object sourceGroupId = ipPermission.getUserIdGroupPairs().get(k).getGroupId();
                source = sourceGroupId != null ? sourceGroupId.toString() : "-";
            }
        }else{
            source = "-";
        }
        return source;
    }
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : Security Group 생성
     * @title : saveAwsSecurityGroupInfo
     * @return : void
     ***************************************************/    
    public void saveAwsSecurityGroupInfo(AwsSecurityGroupMgntDTO dto, Principal principal){
        IaasAccountMgntVO vo =  getAwsAccountInfo( principal, dto.getAccountId());
        Region region = getAwsRegionInfo(dto.getRegion());
        String groupId = awsSecurityGroupMgntApiService.saveSecurityGroupFromAws(vo,dto, region);
        if( !StringUtils.isEmpty(groupId) && !dto.getIngressRuleType().equals("none") ){
            dto.setGroupId(groupId);
            saveAwsSecurityGroupRule(dto, principal, region);
        }
        
    }
    
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : Security Group  Ingress Rules 생성
     * @title : saveAwsSecurityGroupRule
     * @return : void
     ***************************************************/    
    public void saveAwsSecurityGroupRule(AwsSecurityGroupMgntDTO dto, Principal principal, Region region){
        IaasAccountMgntVO vo =  getAwsAccountInfo( principal, dto.getAccountId());
        Boolean flag = awsSecurityGroupMgntApiService.saveSecurityGroupIngressRuleFromAws(vo, dto, region);
        if( !flag ){
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
    }
    
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : AWS Security Group 삭제
     * @title :  deleteAwsSecurityGroupInfo
     * @return : void
     ***************************************************/
     public void deleteAwsSecurityGroupInfo(AwsSecurityGroupMgntDTO dto, Principal principal) {
         IaasAccountMgntVO vo =  getAwsAccountInfo(principal, dto.getAccountId());
         Region region = getAwsRegionInfo(dto.getRegion());
         Boolean flag = awsSecurityGroupMgntApiService.deleteSecurityGroupInfoFromAws(vo, dto, region);
         if( !flag ){
             throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                     message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
         }
     }
    
    
    /***************************************************
     * @project : AWS 관리 대시보드
     * @description : AWS 계정 정보가 실제 존재 하는지 확인 및 상세 조회
     * @title : getAwsAccountInfo
     * @return : IaasAccountMgntVO
     ***************************************************/
     public IaasAccountMgntVO getAwsAccountInfo(Principal principal, int accountId){
         return commonIaasService.getIaaSAccountInfo(principal, accountId, "AWS");
     }
     
     /***************************************************
      * @project : AWS 관리 대시보드
      * @description : AWS 리전 명 조회
      * @title : getAwsRegionInfo
      * @return : Region
      ***************************************************/
      public Region getAwsRegionInfo(String regionName) {
          return commonIaasService.getAwsRegionInfo(regionName);
      }
}