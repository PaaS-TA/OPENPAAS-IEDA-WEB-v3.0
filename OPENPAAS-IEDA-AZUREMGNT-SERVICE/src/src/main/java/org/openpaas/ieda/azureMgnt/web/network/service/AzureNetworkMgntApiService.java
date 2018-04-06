package org.openpaas.ieda.azureMgnt.web.network.service;

import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.common.web.common.service.CommonApiService;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microsoft.azure.credentials.AzureTokenCredentials;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.network.Network;
import com.microsoft.rest.LogLevel;

@Service
public class AzureNetworkMgntApiService {

	@Autowired 
	CommonApiService commonApiService;
	/****************************************************************
     * @project : Azure 인프라 관리 대시보드
     * @description : Azure TokenCredentials 공통 빌드
     * @title : getAzureClient
     * @return : AzureTokenCredentials
    *****************************************************************/
   public AzureTokenCredentials getAzureClient(IaasAccountMgntVO vo){
       AzureTokenCredentials azure = commonApiService.getAzureCredentialsFromAzure(vo.getCommonAccessUser(),vo.getCommonTenant(), vo.getCommonAccessSecret(), vo.getAzureSubscriptionId());
       return azure;
    }
   
   /****************************************************************
    * @project : Azure 인프라 관리 대시보드
    * @description : Azure API를 통해 Virtural Network 목록 정보 조회
    * @title : getAzureNetworkInfoListApiFromAzure
    * @return : List<Network>
   *****************************************************************/
  public List<Network> getAzureNetworkInfoListApiFromAzure(IaasAccountMgntVO vo){
      AzureTokenCredentials azureClient = getAzureClient(vo);
      Azure azure  = Azure.configure()
              .withLogLevel(LogLevel.NONE)
              .authenticate(azureClient)
              .withSubscription(vo.getAzureSubscriptionId());
      List<Network> list = azure.networks().list();
      return list;
  }
  
  /****************************************************************
   * @project : Azure 인프라 관리 대시보드
   * @description : Azure API를 통해 Virtural Network 상세 정보 조회
   * @title : getAzureNetworkDetailInfoFromAzure
   * @return : HashMap<String, Object>
  *****************************************************************/
  public HashMap<String, Object> getAzureNetworkDetailInfoFromAzure(IaasAccountMgntVO vo) {
	  AzureTokenCredentials azureClient = getAzureClient(vo);
      Azure azure  = Azure.configure()
              .withLogLevel(LogLevel.NONE)
              .authenticate(azureClient)
              .withSubscription(vo.getAzureSubscriptionId());
      List<Network> networkList = azure.networks().list();
      HashMap<String, Object> map = new HashMap<String, Object>();
      map.put("networkList", networkList);
      return map;
  }   
}
