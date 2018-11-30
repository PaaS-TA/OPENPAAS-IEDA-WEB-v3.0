package org.openpaas.ieda.iaasDashboard.azureMgnt.web.network.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.network.dao.AzureNetworkMgntVO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.network.dto.AzureNetworkMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.microsoft.azure.management.network.Network;
import com.microsoft.azure.management.network.Subnet;
import com.microsoft.azure.management.resources.ResourceGroup;

@Service
public class AzureNetworkMgntService {


    @Autowired
    private AzureNetworkMgntApiService azureNetworkMgntApiService;
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
     * @description : NETWORK 목록 조회
     * @title : getAzureNetworkInfoList
     * @return : List<AzureNetworkMgntVO>
     ***************************************************/
    public List<AzureNetworkMgntVO> getAzureNetworkInfoList(Principal principal, int accountId) {

        IaasAccountMgntVO vo = getAzureAccountInfo(principal, accountId);
        String subName = getAzureSubscriptionName(principal, accountId, vo.getAzureSubscriptionId());
        List<Network> azureNetworkList = azureNetworkMgntApiService.getAzureNetworkInfoListApiFromAzure(vo);
        List<AzureNetworkMgntVO> list = new ArrayList<AzureNetworkMgntVO>();
        for (int i = 0; i < azureNetworkList.size(); i++) {
            Network network = azureNetworkList.get(i);
            AzureNetworkMgntVO azureRgVo = new AzureNetworkMgntVO();
            azureRgVo.setNetworkId(network.id());
            azureRgVo.setNetworkName(network.name());
            azureRgVo.setLocation(network.regionName());
            azureRgVo.setResourceGroupName(network.resourceGroupName());
            azureRgVo.setResourceType(network.type());
            if (network.addressSpaces().size() != 0) {
                azureRgVo.setNetworkAddressSpaceCidr(network.addressSpaces().get(0).toString());
            }
            azureRgVo.setSubscriptionName(subName);
            azureRgVo.setAzureSubscriptionId(vo.getAzureSubscriptionId());
            azureRgVo.setAccountId(vo.getId());
            azureRgVo.setRecid(i);
            list.add(azureRgVo);
        }
        return list;
    }

    /***************************************************
     * @project : Azure 관리 대시보드
     * @description : 해당 NETWORK 에 대한 Subnets 정보 목록 조회
     * @title : getAzureNetworkSubnetsInfo
     * @return : List<AzureNetworkMgntVO>
     ***************************************************/
    public List<AzureNetworkMgntVO> getAzureNetworkSubnetsInfoList(Principal principal, int accountId, String networkName) {
        IaasAccountMgntVO vo = getAzureAccountInfo(principal, accountId);
        List<Network> results = azureNetworkMgntApiService.getAzureNetworkInfoListApiFromAzure(vo);
        List<AzureNetworkMgntVO> list = new ArrayList<AzureNetworkMgntVO>();
        for (int i = 0; i < results.size(); i++) {
            if (!results.get(i).subnets().isEmpty()) {
                Network network = results.get(i);
                String thename = network.name();
                if (thename.equals(networkName)) {
                    int subSize = network.subnets().size();
                    for (int k = 0; k < subSize; k++) {
                        Set<String> subnetInfo = network.subnets().keySet();
                        String[] subnets = subnetInfo.toArray(new String[subnetInfo.size()]);
                        AzureNetworkMgntVO azureRgVo = new AzureNetworkMgntVO();
                        azureRgVo.setSubnetName(subnets[k].toString());
                        Subnet subnetList = results.get(i).subnets().get(subnets[k]);
                        azureRgVo.setSubnetAddressRangeCidr(subnetList.addressPrefix());
                        if (subnetList.getNetworkSecurityGroup() != null) {
                            azureRgVo.setSecurityGroupName(subnetList.getNetworkSecurityGroup().name().toString());
                        } else {
                            azureRgVo.setSecurityGroupName(" - ");
                        }
                        // Available IP Address Count
                        int azureReservedIPs = 5;
                        int configCnt = subnetList.networkInterfaceIPConfigurationCount();
                        int netmaskLength = Integer.parseInt(subnetList.addressPrefix().split("/")[1]);
                        int countOfAvailableIPs = (int)(Math.pow(2, 32-netmaskLength)-azureReservedIPs-configCnt); 
                        azureRgVo.setSubnetAddressesCnt(countOfAvailableIPs);
                        azureRgVo.setResourceGroupName(network.resourceGroupName());
                        azureRgVo.setNetworkName(networkName);
                        azureRgVo.setRecid(k);
                        azureRgVo.setAccountId(accountId);
                        list.add(azureRgVo);
                    }
                }
            }
        }
        return list;
    }

    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure ResourceGroup 정보 조회
     * @title : getResourceGroupInfoList
     * @return : List<AzureNetworkMgntVO>
     ***************************************************/
    public List<AzureNetworkMgntVO> getResourceGroupInfoList(Principal principal, int accountId) {
        IaasAccountMgntVO vo = getAzureAccountInfo(principal, accountId);
        List<ResourceGroup> azureResourceGroupList = azureNetworkMgntApiService
                .getResourceGroupInfoListApiFromAzure(vo);
        List<AzureNetworkMgntVO> list = new ArrayList<AzureNetworkMgntVO>();
        for (int i = 0; i < azureResourceGroupList.size(); i++) {
            ResourceGroup resourceGroup = azureResourceGroupList.get(i);
            AzureNetworkMgntVO azureRgVo = new AzureNetworkMgntVO();
            azureRgVo.setResourceGroupName(resourceGroup.name());
            azureRgVo.setLocation(resourceGroup.inner().location());
            azureRgVo.setResourceGroupId(resourceGroup.id());
            azureRgVo.setRecid(i);
            azureRgVo.setAccountId(vo.getId());
            list.add(azureRgVo);
        }
        return list;
    }

    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure Network 생성
     * @title : saveNetworkInfo
     * @return : void
     ***************************************************/
    public void saveNetworkInfo(AzureNetworkMgntDTO dto, Principal principal) {
        IaasAccountMgntVO vo = getAzureAccountInfo(principal, dto.getAccountId());
        try{
            azureNetworkMgntApiService.createAzureNetworkFromAzure(vo, dto);
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
     * @description : Azure Network 삭제
     * @title : deleteNetworkInfo
     * @return : void
     ***************************************************/
    public void deleteNetworkInfo(AzureNetworkMgntDTO dto, Principal principal) {
        IaasAccountMgntVO vo = getAzureAccountInfo(principal, dto.getAccountId());
        try{
          azureNetworkMgntApiService.deleteAzureNetworkFromAzure(vo, dto);
        }catch (Exception e) {
            throw new CommonException(
                    message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
    }

    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure Network Subnet 생성
     * @title : addSubnet
     * @return : void
     ***************************************************/
    public void addSubnet(AzureNetworkMgntDTO dto, Principal principal) {
    	IaasAccountMgntVO vo = getAzureAccountInfo(principal, dto.getAccountId());
        try{
          azureNetworkMgntApiService.addSubnetFromAzure(vo, dto);
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
     * @description : Azure Network Subnet 삭제
     * @title : deleteSubnet
     * @return : void
     ***************************************************/    
    public void deleteSubnet(AzureNetworkMgntDTO dto, Principal principal) {
        IaasAccountMgntVO vo = getAzureAccountInfo(principal, dto.getAccountId());
        try{
          azureNetworkMgntApiService.deleteSubnetFromAzure(vo, dto);
        }catch (Exception e) {
            String detailMessage = e.getMessage();
            
            if(!detailMessage.equals("") && detailMessage != null && !detailMessage.equals("No message available.")){
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
     * @description : Azure 구독 정보 조회
     * @title : getAzureSubscriptionInfo
     * @return : AzureNetworkMgntVO
     ***************************************************/
    public AzureNetworkMgntVO getAzureSubscriptionInfo(Principal principal, int accountId) {
        IaasAccountMgntVO vo = getAzureAccountInfo(principal, accountId);
        String subscriptionId = vo.getAzureSubscriptionId().toString();
        String subscriptionName = getAzureSubscriptionName(principal, accountId, subscriptionId);
        AzureNetworkMgntVO networkVO = new AzureNetworkMgntVO();
        networkVO.setAzureSubscriptionId(subscriptionId);
        networkVO.setSubscriptionName(subscriptionName);
        return networkVO;
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
