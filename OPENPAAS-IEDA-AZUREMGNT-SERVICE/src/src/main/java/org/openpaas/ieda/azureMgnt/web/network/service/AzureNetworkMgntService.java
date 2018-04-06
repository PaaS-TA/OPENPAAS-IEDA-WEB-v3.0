package org.openpaas.ieda.azureMgnt.web.network.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.openpaas.ieda.azureMgnt.web.network.dao.AzureNetworkMgntVO;
import org.openpaas.ieda.azureMgnt.web.network.dto.AzureNetworkMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.microsoft.azure.management.network.Network;

@Service
public class AzureNetworkMgntService {
	
	@Autowired AzureNetworkMgntVO azureNetworkMgntVO;
	@Autowired AzureNetworkMgntDTO azureNetworkMgntDTO;
	@Autowired AzureNetworkMgntApiService azureNetworkMgntApiService;
	@Autowired CommonIaasService commonIaasService;
    @Autowired MessageSource message;
    /***************************************************
     * @project : AZURE 인프라 관리 대시보드
     * @description : AZURE 계정 정보가 실제 존재 하는지 확인 및 상세 조회
     * @title : getAzureAccountInfo
     * @return : IaasAccountMgntVO
     ***************************************************/
     public IaasAccountMgntVO getAzureAccountInfo(Principal principal, int accountId){
         return commonIaasService.getIaaSAccountInfo(principal, accountId, "azure");
     }
     
     /***************************************************
      * @project : Azure 관리 대시보드
      * @description : Resource Group 목록 조회
      * @title : getAzureResourceGroupInfoList
      * @return : List<AzureResourceGroupMgntVO>
      ***************************************************/
      public List<AzureNetworkMgntVO> getAzureNetworkInfoList(Principal principal, int accountId) {
        
          IaasAccountMgntVO vo = getAzureAccountInfo(principal, accountId);
          List<Network> azureNetworkList = azureNetworkMgntApiService.getAzureNetworkInfoListApiFromAzure(vo);
          List<AzureNetworkMgntVO> list = new ArrayList<AzureNetworkMgntVO>();
          for (int i=0; i<azureNetworkList.size(); i++ ){
              Network network = azureNetworkList.get(i);
              AzureNetworkMgntVO azureRgVo = new AzureNetworkMgntVO();
              azureRgVo.setNetworkName(network.name());
              azureRgVo.setLocation(network.regionName());
              azureRgVo.setResourceGroupName(network.resourceGroupName());
              azureRgVo.setRecid(i);
              azureRgVo.setAzureSubscriptionId(vo.getAzureSubscriptionId());
              azureRgVo.setAccountId(vo.getId());
              list.add(azureRgVo);
          }
          return list;
      }
    
}
