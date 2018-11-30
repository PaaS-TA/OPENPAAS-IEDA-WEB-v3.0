package org.openpaas.ieda.iaasDashboard.azureMgnt.web.resourceGroup.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.resourceGroup.dao.AzureResourceGroupMgntVO;
import org.openpaas.ieda.iaasDashboard.azureMgnt.web.resourceGroup.dto.AzureResourceGroupMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.resources.GenericResource;
import com.microsoft.azure.management.resources.ResourceGroup;

@Service
public class AzureResourceGroupMgntService {
    
    @Autowired AzureResourceGroupMgntVO azureResourceGroupMgntVO;
    @Autowired AzureResourceGroupMgntDTO azureResourceGroupMgntDTO;
    @Autowired AzureResourceGroupMgntApiService azureResourceGroupMgntApiService;
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
    public List<AzureResourceGroupMgntVO> getAzureResourceGroupInfoList(Principal principal, int accountId) {
      
        IaasAccountMgntVO vo = getAzureAccountInfo(principal, accountId);
        List<ResourceGroup> azureResourceGroupList = azureResourceGroupMgntApiService.getAzureResourceGroupInfoListApiFromAzure(vo);
        List<AzureResourceGroupMgntVO> list = new ArrayList<AzureResourceGroupMgntVO>();
        if(azureResourceGroupList !=null && azureResourceGroupList.size() != 0){
        for (int i=0; i<azureResourceGroupList.size(); i++ ){
            ResourceGroup resourceGroup = azureResourceGroupList.get(i);
            AzureResourceGroupMgntVO azureRgVo = new AzureResourceGroupMgntVO();
            
            azureRgVo.setName(resourceGroup.name());
            azureRgVo.setLocation(resourceGroup.inner().location());
            azureRgVo.setResourceGroupId(resourceGroup.id());
            azureRgVo.setStatus(resourceGroup.provisioningState());
            azureRgVo.setRecid(i);
            azureRgVo.setAzureSubscriptionId(vo.getAzureSubscriptionId());
            azureRgVo.setAccountId(vo.getId());
            list.add(azureRgVo);
        }}
        return list;
    }
    
    /***************************************************
     * @project : Azure 인프라 관리 대시보드
     * @description : Azure ResourceGroup 정보 상세 조회 
     * @title : getAzureResourceGroupDetailInfo
     * @return : HashMap<String, Object> 
     ***************************************************/
     @SuppressWarnings("unchecked")
    public HashMap<String, Object> getAzureResourceGroupDetailInfo(Principal principal, int accountId, String resourceGroupName) {
         IaasAccountMgntVO vo =  getAzureAccountInfo(principal, accountId);
         HashMap<String, Object> result = azureResourceGroupMgntApiService.getAzureResourceGroupDetailInfoFromAzure(vo);
         String subName = getAzureSubscriptionName(principal, accountId, vo.getAzureSubscriptionId());
         List<ResourceGroup> azureResourceGroupList = (List<ResourceGroup>) result.get("resourceGroupList");
         HashMap<String, Object> apiAzureRgInfo = new HashMap<String, Object>();
         if(azureResourceGroupList !=null && azureResourceGroupList.size() != 0){
         for( int i=0; i<azureResourceGroupList.size(); i++ ){
             if( azureResourceGroupList.get(i).inner().name().toString().equals( resourceGroupName )){
                 ResourceGroup resourceGroup = azureResourceGroupList.get(i);
                 AzureResourceGroupMgntVO azureRgVo = new AzureResourceGroupMgntVO();
                 azureRgVo.setAccountId(accountId);
                 apiAzureRgInfo.put("name",resourceGroup.name());
                 apiAzureRgInfo.put("subscriptionName", subName);
                 String deployments = getDepolymentInfo(principal, accountId, resourceGroup.name());
                 apiAzureRgInfo.put("deployments",deployments);
             }
         }
         }
         return apiAzureRgInfo;
     }
     
     /***************************************************
      * @project : Azure 인프라 관리 대시보드
      * @description : Azure 해당 Resource Group에 대한 Resource 목록 조회 
      * @title : getAzureResourceList
      * @return :  List<AzureResourceGroupMgntVO> 
      ***************************************************/
    public  List<AzureResourceGroupMgntVO> getAzureResourceList (Principal principal, int accountId, String resourceGroupName){
         IaasAccountMgntVO vo =  getAzureAccountInfo(principal, accountId);
         PagedList<GenericResource> results = azureResourceGroupMgntApiService.getAzureResouceListFromAzure(vo, resourceGroupName);
        
         List<AzureResourceGroupMgntVO> list = new ArrayList<AzureResourceGroupMgntVO>();
        if(results!=null && results.size() != 0){
         for (int i=0; i<results.size(); i++ ){
             GenericResource resource = results.get(i);
             AzureResourceGroupMgntVO azureRgVo = new AzureResourceGroupMgntVO();
             azureRgVo.setResourceName(resource.name().toString());
             azureRgVo.setResourceLocation(resource.regionName().toString());
             azureRgVo.setResourceType(resource.resourceType());
             azureRgVo.setRecid(i);
             azureRgVo.setAccountId(vo.getId());
             list.add(azureRgVo);
         }
         }
         return list;
     }
    
     /***************************************************
      * @project : Azure 인프라 관리 대시보드
      * @description : Azure ResourceGroup 생성 
      * @title : saveResourceGroupInfo
      * @return : AzureResourceGroupMgntVO
      ***************************************************/
     public AzureResourceGroupMgntVO saveResourceGroupInfo(AzureResourceGroupMgntDTO dto,Principal principal){
         
         IaasAccountMgntVO vo =  getAzureAccountInfo(principal, dto.getAccountId());
         String regionName = getAzureLocationInfo(dto.getRglocation());
             ResourceGroup apiResourceGroup = azureResourceGroupMgntApiService.createResourceGroupromAzure(vo, regionName, dto.getName(),dto.getAzureSubscriptionId());
             AzureResourceGroupMgntVO azureRGVo = new AzureResourceGroupMgntVO();
               azureRGVo.setLocation(apiResourceGroup.region().name());
               azureRGVo.setName(apiResourceGroup.name());
               azureRGVo.setAzureSubscriptionId(dto.getAzureSubscriptionId());
             return azureRGVo;
     }

     /***************************************************
      * @project : Azure 인프라 관리 대시보드
      * @description : Azure 리전 조회
      * @title : getAzureLocationInfo
      * @return : Region
      ***************************************************/
      public String getAzureLocationInfo(String rglocation) {
          return commonIaasService.getAzureLocationInfo(rglocation);
      }
      
      /***************************************************
       * @project : 인프라 관리 대시보드
       * @description : Azure 구독 명 조회
       * @title : getAzureSubNameInfo
       * @return : String
       ***************************************************/
      public String getAzureSubscriptionName(Principal principal, int accountId, String subscriptionId){
          IaasAccountMgntVO vo =  getAzureAccountInfo(principal, accountId);
          //String subName = azureResourceGroupMgntApiService.getSubscriptionInfoFromAzure(vo, subscriptionId);
          String subName = commonIaasService.getSubscriptionNameFromAzure(vo, subscriptionId);
          return  subName;
      }
      /***************************************************
       * @project : 인프라 관리 대시보드
       * @description : Azure 구독 정보 조회
       * @title : getAzureSubscription
       * @return : AzureResourceGroupMgntVO
       ***************************************************/
      public AzureResourceGroupMgntVO getAzureSubscription(Principal principal, int accountId){
          IaasAccountMgntVO vo =  getAzureAccountInfo(principal, accountId);
          String subId = vo.getAzureSubscriptionId().toString();
          String subName = getAzureSubscriptionName(principal, accountId, subId);
          AzureResourceGroupMgntVO rgVO = new AzureResourceGroupMgntVO();
          rgVO.setAzureSubscriptionId(subId);
          rgVO.setSubscriptionName(subName);
          return  rgVO;
      }
      
      /***************************************************
       * @project : 인프라 관리 대시보드
       * @description : Azure Deployments 정보 조회
       * @title : getDepolymentInfoFromAzure
       * @return : String
       ***************************************************/
      public String getDepolymentInfo (Principal principal, int accountId, String resourceGroupName){
          IaasAccountMgntVO vo =  getAzureAccountInfo(principal, accountId);
          String deployments =  azureResourceGroupMgntApiService.getDepolymentInfoFromAzure(vo, resourceGroupName);
          return deployments;
      }
      
      /***************************************************
       * @project : 인프라 관리 대시보드
       * @description : Azure 리전 목록 조회
       * @title : getAzureRegionList
       * @return : List<Region>
       ***************************************************/
      public ArrayList<String> getAzureRegionList(){
          com.microsoft.azure.management.resources.fluentcore.arm.Region[] region = com.microsoft.azure.management.resources.fluentcore.arm.Region.values();
          List<com.microsoft.azure.management.resources.fluentcore.arm.Region> regions = Arrays.stream(region).collect(Collectors.toList());
          Long num = regions.stream().count();
          ArrayList<String> regionlist = new ArrayList<>();
          for(int i=0; i<num; i++){
              regionlist.add(i, regions.get(i).name().toString());
          }
          return regionlist;
      }
      
      /***************************************************
       * @project : Azure 인프라 관리 대시보드
       * @description : Azure ResourceGroup 삭제 
       * @title : deleteAzureResourceGroupInfo
       * @return : void
       ***************************************************/
      public void deleteAzureResourceGroupInfo(Principal principal, AzureResourceGroupMgntDTO dto) {
          IaasAccountMgntVO vo =  getAzureAccountInfo(principal, dto.getAccountId());
          try{ 
              azureResourceGroupMgntApiService.deleteResourceGroupInfoApiFromAzure(vo, dto.getName());
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
