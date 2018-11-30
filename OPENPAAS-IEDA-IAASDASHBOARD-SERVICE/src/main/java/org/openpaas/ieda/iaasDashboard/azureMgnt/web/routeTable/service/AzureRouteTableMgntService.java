package org.openpaas.ieda.iaasDashboard.azureMgnt.web.routeTable.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.routeTable.dao.AzureRouteTableMgntVO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.routeTable.dto.AzureRouteTableMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.microsoft.azure.management.network.NetworkSecurityGroup;
import com.microsoft.azure.management.network.RouteTable;
import com.microsoft.azure.management.network.Subnet;

@Service
public class AzureRouteTableMgntService {
    @Autowired
    private AzureRouteTableMgntApiService azureRouteTableMgntApiService;
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
     * @description : RouteTable 목록 조회
     * @title : getAzureRouteTableInfoList
     * @return : List<AzureSecurityGroupMgntVO>
     ***************************************************/
    public List<AzureRouteTableMgntVO> getAzureRouteTableInfoList(Principal principal, int accountId) {
         IaasAccountMgntVO vo = getAzureAccountInfo(principal, accountId);
         List<RouteTable> results = azureRouteTableMgntApiService.getAzureRouteTableInfoListFromAzure(vo);
         List<AzureRouteTableMgntVO> list = new ArrayList<AzureRouteTableMgntVO>();
         for (int i=0; i< results.size(); i++){
             RouteTable result = results.get(i);
             AzureRouteTableMgntVO azureVo = new AzureRouteTableMgntVO();
             azureVo.setRouteTableName(result.name());
             azureVo.setRouteTableId(result.id());
             azureVo.setResourceGroupName(result.resourceGroupName());
             azureVo.setLocation(result.regionName());
             azureVo.setSubscriptionName(getAzureSubscriptionName( principal, accountId, vo.getAzureSubscriptionId()));
             azureVo.setAzureSubscriptionId(vo.getAzureSubscriptionId());
             azureVo.setAssociations(result.listAssociatedSubnets().size());
             azureVo.setAccountId(accountId);
             azureVo.setRecid(i);
             list.add(azureVo);
         }
         return list;
    }
    
    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : RouteTable의 Subnet 목록 조회
     * @title : getAzureRouteTableSubnetInfoList
     * @return : List<AzureSecurityGroupMgntVO>
     ***************************************************/
    public List<AzureRouteTableMgntVO> getAzureRouteTableSubnetInfoList(Principal principal, int accountId, String routeTableName) {
        IaasAccountMgntVO vo = getAzureAccountInfo(principal, accountId);
        List<RouteTable> results = azureRouteTableMgntApiService.getAzureRouteTableInfoListFromAzure(vo);
        List<AzureRouteTableMgntVO> list = new ArrayList<AzureRouteTableMgntVO>();
        for (int i=0; i< results.size(); i++){
            RouteTable result = results.get(i);
            if(result.name().equals(routeTableName)){
                List<Subnet> subnetList = result.listAssociatedSubnets();
                for(int k=0; k< subnetList.size(); k++){
                    Subnet subnet = subnetList.get(k);
                    AzureRouteTableMgntVO azureVo = new AzureRouteTableMgntVO();
                    azureVo.setRouteTableName(routeTableName);
                    azureVo.setSubnetName(subnet.name());
                    azureVo.setSubnetAddressRange(subnet.addressPrefix());
                    NetworkSecurityGroup securityGroup = subnet.getNetworkSecurityGroup();
                    String sgName ="";
                    if(securityGroup != null){
                    	sgName = securityGroup.name();
                    }else {
                    	sgName = " - ";
                    }
                    azureVo.setSecurityGroupName(sgName);
                    azureVo.setNetworkName(subnet.parent().name());
                    azureVo.setAccountId(accountId);
                    azureVo.setRecid(k);
                    list.add(azureVo);
                }
            }
        }
        return list;
    }

    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure RouteTable 생성
     * @title : createAzureRouteTable
     * @return : void
     ***************************************************/
    public void createAzureRouteTable(AzureRouteTableMgntDTO dto, Principal principal) {
        IaasAccountMgntVO vo = getAzureAccountInfo(principal, dto.getAccountId());
        try{
            azureRouteTableMgntApiService.createAzureRouteTableFromAzure(vo, dto);
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
     * @description : Azure RouteTable 삭제
     * @title : deleteAzureRouteTable
     * @return : void
     ***************************************************/
    public void deleteAzureRouteTable(AzureRouteTableMgntDTO dto, Principal principal) {
        IaasAccountMgntVO vo = getAzureAccountInfo(principal, dto.getAccountId());
        try{
            azureRouteTableMgntApiService.deleteAzureRouteTableFromAzure(vo, dto);
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
     * @description : Azure Subnet associate
     * @title : associateAzureSubnet
     * @return : void
     ***************************************************/
    public void associateAzureSubnet(AzureRouteTableMgntDTO dto, Principal principal){
    	IaasAccountMgntVO vo = getAzureAccountInfo(principal, dto.getAccountId());
        try{
            azureRouteTableMgntApiService.associateAzureSubnetFromAzure(vo, dto);
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
     * @description : Azure Subnet disassociate
     * @title : disassociateAzureSubnet
     * @return : void
     ***************************************************/
    public void disassociateAzureSubnet(AzureRouteTableMgntDTO dto, Principal principal){
    	IaasAccountMgntVO vo = getAzureAccountInfo(principal, dto.getAccountId());
        try{
            azureRouteTableMgntApiService.disassociateAzureSubnetFromAzure(vo, dto);
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
     * @description : Azure Network 명 조회
     * @title : getAzureNetworkName
     * @return : List<String>
     ***************************************************/
    public List<String> getAzureNetworkName(Principal principal, int accountId, String resourceGroupName) {
        IaasAccountMgntVO vo = getAzureAccountInfo(principal, accountId);
        List<String> list = azureRouteTableMgntApiService.getAzureNetworkNameListFromAzure(vo, resourceGroupName);
        return list;
    }

    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure Subnet 명 조회
     * @title : getAzureSubnetName
     * @return : List<String>
     ***************************************************/
    public List<String> getAzureSubnetName(Principal principal, int accountId, String resourceGroupName, String networkName) {
        IaasAccountMgntVO vo = getAzureAccountInfo(principal, accountId);
        List<String> list = azureRouteTableMgntApiService.getAzureSubnetNameListFromAzure(vo, resourceGroupName, networkName);
        return list;
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
