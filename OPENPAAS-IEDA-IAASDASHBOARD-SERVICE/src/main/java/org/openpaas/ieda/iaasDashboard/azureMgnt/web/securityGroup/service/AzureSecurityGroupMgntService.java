package org.openpaas.ieda.iaasDashboard.azureMgnt.web.securityGroup.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.securityGroup.dao.AzureSecurityGroupMgntVO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.securityGroup.dto.AzureSecurityGroupMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.microsoft.azure.management.network.NetworkSecurityGroup;
import com.microsoft.azure.management.network.NetworkSecurityRule;
import com.microsoft.azure.management.network.SecurityRuleAccess;
import com.microsoft.azure.management.network.SecurityRuleDirection;
import com.microsoft.azure.management.network.SecurityRuleProtocol;
import com.microsoft.azure.management.network.implementation.SecurityRuleInner;

@Service
public class AzureSecurityGroupMgntService {
    @Autowired
    private AzureSecurityGroupMgntApiService azureSecurityGroupMgntApiService;
    @Autowired
    private CommonIaasService commonIaasService;
    @Autowired
    private MessageSource message;
    
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE 계정 정보가 실제 존재 하는지 확인 및 상세 조회
     * @title : getAzureAccountInfo
     * @return : IaasAccountMgntVO
     ***************************************************/
    public IaasAccountMgntVO getAzureAccountInfo(Principal principal, int accountId) {
        return commonIaasService.getIaaSAccountInfo(principal, accountId, "azure");
    }
    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : SecurityGroup 목록 조회
     * @title : getAzureSecurityGroupInfoList
     * @return : List<AzureSecurityGroupMgntVO>
     ***************************************************/
    public List<AzureSecurityGroupMgntVO> getAzureSecurityGroupInfoList(Principal principal, int accountId) {
         IaasAccountMgntVO vo = getAzureAccountInfo(principal, accountId);
         List<NetworkSecurityGroup> results = azureSecurityGroupMgntApiService.getAzureSecurityGroupInfoListFromAzure(vo);
         List<AzureSecurityGroupMgntVO> list = new ArrayList<AzureSecurityGroupMgntVO>();
         for (int i=0; i< results.size(); i++){
             NetworkSecurityGroup result = results.get(i);
             AzureSecurityGroupMgntVO azureVo = new AzureSecurityGroupMgntVO();
             azureVo.setSecurityGroupName(result.name());
             azureVo.setSecurityGroupId(result.id());
             azureVo.setResourceGroupName(result.resourceGroupName());
             azureVo.setLocation(result.regionName());
             azureVo.setSubscriptionName(getAzureSubscriptionName( principal, accountId, vo.getAzureSubscriptionId()));
             azureVo.setAzureSubscriptionId(vo.getAzureSubscriptionId());
             azureVo.setAccountId(accountId);
             azureVo.setRecid(i);
             list.add(azureVo);
         }
         return list;
    }
    
    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : Azure SecurityGroup Inbound Rules 목록 조회
     * @title : getAzureSecurityGroupInboundRules
     * @return : List<AzureSecurityGroupMgntVO>
     ***************************************************/
    public List<AzureSecurityGroupMgntVO> getAzureSecurityGroupInboundRules(Principal principal, int accountId, String secruityGroupName) {
         IaasAccountMgntVO vo = getAzureAccountInfo(principal, accountId);
         List<NetworkSecurityGroup> results = azureSecurityGroupMgntApiService.getAzureSecurityGroupInfoListFromAzure(vo);
         List<AzureSecurityGroupMgntVO> list = new ArrayList<AzureSecurityGroupMgntVO>();
         
         for (int i=0; i< results.size(); i++){
             ArrayList<NetworkSecurityRule> rules = new ArrayList<NetworkSecurityRule>();
             ArrayList<NetworkSecurityRule> rules2 = new ArrayList<NetworkSecurityRule>();
             Map<Integer, NetworkSecurityRule> map1 = new HashMap<Integer, NetworkSecurityRule>();
             Map<Integer, NetworkSecurityRule> map2 = new HashMap<Integer, NetworkSecurityRule>();
             NetworkSecurityGroup result = results.get(i);
             if(result.name().equals(secruityGroupName)){
                 Iterator<NetworkSecurityRule> itr = result.securityRules().values().iterator();
                 Iterator<NetworkSecurityRule> itr2 = result.defaultSecurityRules().values().iterator();
                 while(itr.hasNext()){
                     rules.add((NetworkSecurityRule) itr.next());
                 }
                 while(itr2.hasNext()){
                     rules2.add((NetworkSecurityRule) itr2.next());
                 }
                 ListIterator<NetworkSecurityRule> rules1list = null; 
                 ListIterator<NetworkSecurityRule> rules2list = null;
                 rules1list = rules.listIterator();
                 rules2list = rules2.listIterator();
                 while(rules1list.hasNext()){
                     map1.put(rules1list.nextIndex(), rules1list.next());
                 };
                 while(rules2list.hasNext()){
                     map2.put(rules2list.nextIndex(),rules2list.next());
                     System.out.print(rules2list.nextIndex()+"TEST INDEXT");
                 }
                 int m = 0;
                 if(map1.size()!=0){
                     for(int k=0; k< map1.size(); k++){
                        SecurityRuleDirection srDirection = map1.get(k).direction();
                        if(srDirection.toString().toLowerCase().equals("inbound")){
                         AzureSecurityGroupMgntVO azureVo = new AzureSecurityGroupMgntVO();
                         azureVo.setSecurityGroupName(result.name());
                         azureVo.setPriority(map1.get(k).priority());
                         azureVo.setInboundName(map1.get(k).name());
                         azureVo.setPort(map1.get(k).sourcePortRange());
                         SecurityRuleProtocol srProtocol =  map1.get(k).protocol();
                         azureVo.setProtocol(srProtocol.toString());
                         azureVo.setSource(map1.get(k).sourceAddressPrefix());
                         azureVo.setDestination(map1.get(k).destinationAddressPrefix());
                         SecurityRuleAccess srAccess = map1.get(k).access();
                         azureVo.setAction(srAccess.toString());
                         azureVo.setDirection(srDirection.toString());
                         azureVo.setAccountId(accountId);
                         azureVo.setRecid(k);
                         list.add(azureVo); 
                        };
                     }
                 }
                 if(map2.size()!=0){
                     m = list.size();
                     for(int h=0; h< map2.size(); h++){
                         SecurityRuleDirection srDirection = map2.get(h).direction();
                         if(srDirection.toString().toLowerCase().equals("inbound")){
                         AzureSecurityGroupMgntVO azureVo = new AzureSecurityGroupMgntVO();
                         azureVo.setSecurityGroupName(result.name());
                         azureVo.setPriority(map2.get(h).priority());
                         azureVo.setInboundName(map2.get(h).name());
                         azureVo.setPort(map2.get(h).sourcePortRange());
                         SecurityRuleProtocol srProtocol =  map2.get(h).protocol();
                         azureVo.setProtocol(srProtocol.toString());
                         azureVo.setSource(map2.get(h).sourceAddressPrefix());
                         azureVo.setDestination(map2.get(h).destinationAddressPrefix());
                         SecurityRuleAccess srAccess = map2.get(h).access();
                         azureVo.setAction(srAccess.toString());
                         azureVo.setDirection(srDirection.toString());
                         azureVo.setAccountId(accountId);
                         azureVo.setRecid(h+m);
                         list.add(azureVo); 
                         }   
                     }
                     
                 } 
             } 
         }
         return list;
         }
    
    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : Azure SecurityGroup Outbound Rules 목록 조회
     * @title : getAzureSecurityGroupOutboundRules
     * @return : List<AzureSecurityGroupMgntVO>
     ***************************************************/
    public List<AzureSecurityGroupMgntVO> getAzureSecurityGroupOutboundRules(Principal principal, int accountId, String secruityGroupName) {
         IaasAccountMgntVO vo = getAzureAccountInfo(principal, accountId);
         List<NetworkSecurityGroup> results = azureSecurityGroupMgntApiService.getAzureSecurityGroupInfoListFromAzure(vo);
         List<AzureSecurityGroupMgntVO> list = new ArrayList<AzureSecurityGroupMgntVO>();

         for (int i=0; i< results.size(); i++){
             ArrayList<NetworkSecurityRule> rules = new ArrayList<NetworkSecurityRule>();
             ArrayList<NetworkSecurityRule> rules2 = new ArrayList<NetworkSecurityRule>();
             Map<Integer, NetworkSecurityRule> map1 = new HashMap<Integer, NetworkSecurityRule>();
             Map<Integer, NetworkSecurityRule> map2 = new HashMap<Integer, NetworkSecurityRule>();
             NetworkSecurityGroup result = results.get(i);
             if(result.name().equals(secruityGroupName)){
                 Iterator<NetworkSecurityRule> itr = result.securityRules().values().iterator();
                 Iterator<NetworkSecurityRule> itr2 = result.defaultSecurityRules().values().iterator();
                 while(itr.hasNext()){
                     rules.add((NetworkSecurityRule) itr.next());
                 }
                 while(itr2.hasNext()){
                     rules2.add((NetworkSecurityRule) itr2.next());
                 }
                 ListIterator<NetworkSecurityRule> rules1list = null; 
                 ListIterator<NetworkSecurityRule> rules2list = null;
                 rules1list = rules.listIterator();
                 rules2list = rules2.listIterator();
                 while(rules1list.hasNext()){
                     map1.put(rules1list.nextIndex(), rules1list.next());
                 };
                 while(rules2list.hasNext()){
                     map2.put(rules2list.nextIndex(),rules2list.next());
                     System.out.print(rules2list.nextIndex()+"TEST INDEXT");
                 }
                 int m = 0;
                 if(map1.size()!=0){
                     for(int k=0; k< map1.size(); k++){
                        SecurityRuleDirection srDirection = map1.get(k).direction();
                        if(srDirection.toString().toLowerCase().equals("outbound")){
                         AzureSecurityGroupMgntVO azureVo = new AzureSecurityGroupMgntVO();
                         azureVo.setSecurityGroupName(result.name());
                         azureVo.setPriority(map1.get(k).priority());
                         azureVo.setOutboundName(map1.get(k).name());
                         azureVo.setPort(map1.get(k).destinationPortRange());
                         SecurityRuleProtocol srProtocol =  map1.get(k).protocol();
                         azureVo.setProtocol(srProtocol.toString());
                         azureVo.setSource(map1.get(k).sourceAddressPrefix());
                         azureVo.setDestination(map1.get(k).destinationAddressPrefix());
                         SecurityRuleAccess srAccess = map1.get(k).access();
                         azureVo.setAction(srAccess.toString());
                         azureVo.setDirection(srDirection.toString());
                         azureVo.setAccountId(accountId);
                         azureVo.setRecid(k);
                         list.add(azureVo); 
                        };
                     }
                 } 
                 if(map2.size()!=0){
                     m = list.size();
                     for(int h=0; h< map2.size(); h++){
                         SecurityRuleDirection srDirection = map2.get(h).direction();
                         if(srDirection.toString().toLowerCase().equals("outbound")){
                         AzureSecurityGroupMgntVO azureVo = new AzureSecurityGroupMgntVO();
                         azureVo.setSecurityGroupName(result.name());
                         azureVo.setPriority(map2.get(h).priority());
                         azureVo.setOutboundName(map2.get(h).name());
                         azureVo.setPort(map2.get(h).destinationPortRange());
                         SecurityRuleProtocol srProtocol =  map2.get(h).protocol();
                         azureVo.setProtocol(srProtocol.toString());
                         azureVo.setSource(map2.get(h).sourceAddressPrefix());
                         azureVo.setDestination(map2.get(h).destinationAddressPrefix());
                         SecurityRuleAccess srAccess = map2.get(h).access();
                         azureVo.setAction(srAccess.toString());
                         azureVo.setDirection(srDirection.toString());
                         azureVo.setAccountId(accountId);
                         azureVo.setRecid(h+m);
                         list.add(azureVo); 
                         }   
                     }
                    
                 } 
             } 
         }
         return list;
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure SecurityGroup 생성
     * @title : createAzureSecurityGroup
     * @return : void
     ***************************************************/
    public void createAzureSecurityGroup(AzureSecurityGroupMgntDTO dto, Principal principal) {
        IaasAccountMgntVO vo = getAzureAccountInfo(principal, dto.getAccountId());
        try{
            azureSecurityGroupMgntApiService.createAzureSecurityGroupFromAzure(vo, dto);
        }catch (Exception e) {
            String detailMessage = e.getMessage();
            if(!detailMessage.equals("") && detailMessage != null){
                throw new CommonException(
                  detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
            }else{
                throw new CommonException(
                  message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
            }
            
        }
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure SecurityGroup 삭제   
     * @title : deleteAzureSecurityGroup
     * @return : void
     ***************************************************/
    public void deleteAzureSecurityGroup(AzureSecurityGroupMgntDTO dto, Principal principal) {
        IaasAccountMgntVO vo = getAzureAccountInfo(principal, dto.getAccountId());
        try{
            azureSecurityGroupMgntApiService.deleteAzureSecurityGroupFromAzure(vo, dto);
        }catch (Exception e) {
            String detailMessage = e.getMessage();
            if(!detailMessage.equals("") && detailMessage != null){
                throw new CommonException(
                  detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
            }else{
                throw new CommonException(
                  message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
            }
            
        }
    }
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure InboundRules 생성
     * @title : createAzureInboundRules
     * @return : void
     ***************************************************/
    public void createAzureInboundRules(AzureSecurityGroupMgntDTO dto, Principal principal) {
        IaasAccountMgntVO vo = getAzureAccountInfo(principal, dto.getAccountId());
        List<String[]> list = new ArrayList<String[]>();
        List <SecurityRuleInner> srList = new ArrayList<SecurityRuleInner>();
             String[] stringArray0 = {"100", "inboundrules1", "22"};
             String[] stringArray1 = {"101", "inboundrules2", "6868"};
             String[] stringArray2 = {"102", "inboundrules3", "25555"};
             String[] stringArray3 = {"103", "inboundrules4", "80"};
             String[] stringArray4 = {"104", "inboundrules5", "443"};
             String[] stringArray5 = {"105", "inboundrules6", "4443"};
             String[] stringArray6 = {"106", "inboundrules7", "*"};
             
             list.add(0, stringArray0);
             list.add(1, stringArray1);
             list.add(2, stringArray2);
             list.add(3, stringArray3);
             list.add(4, stringArray4);
             list.add(5, stringArray5);
             list.add(6, stringArray6);
             
             int priority =0;
             String inboundName ="";
             String port ="";
             for (int i=0; i<list.size(); i++){
                 SecurityRuleInner securityRuleInner = new SecurityRuleInner();
                 priority = Integer.parseInt(list.get(i)[0]);
                 inboundName = list.get(i)[1];
                 port = list.get(i)[2];
                 SecurityRuleAccess access = SecurityRuleAccess.ALLOW;
                 SecurityRuleDirection direction = SecurityRuleDirection.INBOUND;
                 SecurityRuleProtocol protocol = null;
                 if(! "*".equals(port)){
                    protocol = SecurityRuleProtocol.TCP;
                 }else if("*".equals(port)){
                    protocol = SecurityRuleProtocol.ASTERISK;
                 }
                 securityRuleInner = securityRuleInner.withName(inboundName).withPriority(priority).withSourcePortRange(port).withDestinationPortRange("*").withProtocol(protocol).withSourceAddressPrefix("*").withDestinationAddressPrefix("*").withAccess(access).withDirection(direction);
                 srList.add(i,securityRuleInner );
             }
             for(int k=0; k< srList.size(); k++){
                SecurityRuleInner securityRuleInner = srList.get(k);
                try{
                	azureSecurityGroupMgntApiService.createAzureSecurityGroupInboundRulesFromAzure(vo, dto, securityRuleInner);
                }catch (Exception e) {
                    String detailMessage = e.getMessage();
                    if(!detailMessage.equals("") && detailMessage != null){
                        throw new CommonException(
                          detailMessage, detailMessage, HttpStatus.BAD_REQUEST);
                    }else{
                        throw new CommonException(
                          message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
                    }
                    
                }
            }
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure 구독 명 조회
     * @title : getAzureSubscriptionName
     * @return : String
     ***************************************************/
    public String getAzureSubscriptionName(Principal principal, int accountId, String subscriptionId) {
        IaasAccountMgntVO vo = getAzureAccountInfo(principal, accountId);
        String subscriptionName = commonIaasService.getSubscriptionNameFromAzure(vo, subscriptionId);
        return subscriptionName;
    }
}
