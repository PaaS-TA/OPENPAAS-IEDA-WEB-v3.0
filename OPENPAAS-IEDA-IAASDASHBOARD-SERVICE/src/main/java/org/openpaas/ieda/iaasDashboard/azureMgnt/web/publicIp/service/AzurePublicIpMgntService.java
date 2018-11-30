package org.openpaas.ieda.iaasDashboard.azureMgnt.web.publicIp.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.publicIp.dao.AzurePublicIpMgntVO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.publicIp.dto.AzurePublicIpMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.microsoft.azure.management.network.PublicIPAddress;

@Service
public class AzurePublicIpMgntService {
    @Autowired
    private AzurePublicIpMgntApiService azurePublicIpMgntApiService;
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
     * @description : Public IP 목록 조회
     * @title : getAzurePublicIpInfoList
     * @return : List<AzurePublicIpMgntVO>
     ***************************************************/
    public List<AzurePublicIpMgntVO> getAzurePublicIpInfoList(Principal principal, int accountId) {
         IaasAccountMgntVO vo = getAzureAccountInfo(principal, accountId);
         String subscriptionName = getAzureSubscriptionName(principal, accountId, vo.getAzureSubscriptionId());
         List<PublicIPAddress> results = azurePublicIpMgntApiService.getAzurePublicIpInfoListFromAzure(vo);
         List<AzurePublicIpMgntVO> list = new ArrayList<AzurePublicIpMgntVO>();
         for (int i=0; i< results.size(); i++){
        	 PublicIPAddress result = results.get(i);
             AzurePublicIpMgntVO azureVo = new AzurePublicIpMgntVO();
             azureVo.setPublicIpAddress(result.ipAddress());
             azureVo.setPublicIpName(result.name());
             azureVo.setSubscriptionName(subscriptionName);
             azureVo.setLocation(result.regionName());
             azureVo.setResourceGroupName(result.resourceGroupName());
             azureVo.setAccountId(accountId);
             azureVo.setRecid(i);
             list.add(azureVo);
         }
         return list;
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure Public IP 할당
     * @title : createPublicIp
     * @return : void
     ***************************************************/
    public void createPublicIp(AzurePublicIpMgntDTO dto, Principal principal) {
        IaasAccountMgntVO vo = getAzureAccountInfo(principal, dto.getAccountId());
        try{
            azurePublicIpMgntApiService.createAzurePublicIpFromAzure(vo, dto);
        }catch (Exception e) {
            String detailMessage = e.getMessage();
            if(!detailMessage.equals("") && detailMessage != "null"){
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
