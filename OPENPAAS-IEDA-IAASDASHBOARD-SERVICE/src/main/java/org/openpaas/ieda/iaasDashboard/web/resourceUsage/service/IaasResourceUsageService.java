package org.openpaas.ieda.iaasDashboard.web.resourceUsage.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.common.web.common.dao.CommonDAO;
import org.openpaas.ieda.iaasDashboard.api.resourceUsage.IaasResourceUsageApiService;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.openpaas.ieda.iaasDashboard.web.resourceUsage.dao.IaasResourceUsageVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.amazonaws.regions.Region;


@Service
public class IaasResourceUsageService {
    
    @Autowired CommonDAO commonDao; 
    @Autowired CommonIaasService commonIaasService;
    @Autowired IaasResourceUsageApiService apiService;
    @Autowired MessageSource message;
    final static Logger LOGGER = LoggerFactory.getLogger(IaasResourceUsageService.class);
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 인프라 전체 리소스 사용량 정보 조회
     * @title : getIaasResourceUsageTotalInfo
     * @return : List<IaasResourceUsageVO>
    ***************************************************/
    public List<IaasResourceUsageVO> getIaasResourceUsageTotalInfo( Principal principal  ){
        List<IaasResourceUsageVO> resourceList = new ArrayList<IaasResourceUsageVO>();
        //aws
        String region = message.getMessage("common.aws.region.default", null, Locale.KOREA);
        List<IaasResourceUsageVO> awsResources = getAwsResourceUsageInfoList(region, principal);
        for( IaasResourceUsageVO resource : awsResources ){
            resourceList.add(resource);
        }
        //openstack
        List<IaasResourceUsageVO> openstackResources =  getOpenstackResourceUsageInfoList(principal);
        for( IaasResourceUsageVO resource2 : openstackResources ){
            resourceList.add(resource2);
        }
        //azure
        List<IaasResourceUsageVO> azureResources =  getAzureResourceUsageInfoList(principal);
        for( IaasResourceUsageVO resource3 : azureResources ){
            resourceList.add(resource3);
        }
        return resourceList; 
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : AWS 리소스 사용량 조회
     * @title : getAwsResourceUsageInfoList
     * @return : List<IaasResourceUsageVO>
    ***************************************************/
    public List<IaasResourceUsageVO> getAwsResourceUsageInfoList( String regionName, Principal principal ){
        List<IaasResourceUsageVO> resources = new ArrayList<IaasResourceUsageVO>();
        Region region = getAwsRegionInfo(regionName);
        try{
            List<HashMap<String, Object>> accounts = commonDao.selectAccountInfoList("AWS", principal.getName());
            for( HashMap<String, Object> at : accounts ){ 
                IaasResourceUsageVO resource = new IaasResourceUsageVO();
                HashMap<String, Object> result = apiService.getResourceInfoFromAWS(at.get("commonAccessUser").toString(), at.get("commonAccessSecret").toString(), region);
                resource.setAccountName( at.get("accountName").toString() );
                resource.setInstance( Long.parseLong(result.get("instance").toString()) );
                resource.setNetwork( Long.parseLong(result.get("network").toString()) );
                resource.setVolume( Long.parseLong(result.get("volume").toString()) );
                resource.setBilling( Double.parseDouble(result.get("billing").toString()));
                resource.setIaasType("AWS");
                resources.add(resource);
            }
        }catch(Exception e){
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("iaas.aws.account.exist.message.exception", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        return resources;
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Openstack 리소스 사용량 조회
     * @title : getOpenstackResourceUsageInfoList
     * @return : List<IaasResourceUsageVO>
    ***************************************************/
    public List<IaasResourceUsageVO> getOpenstackResourceUsageInfoList( Principal principal ){
        List<IaasResourceUsageVO> resources = new ArrayList<IaasResourceUsageVO>();
        String iaasType = message.getMessage("iaas.openstack", null, Locale.KOREA);
        try{
            List<HashMap<String, Object>> accounts = commonDao.selectAccountInfoList(iaasType, principal.getName());
            for( HashMap<String, Object> at : accounts ){
                IaasResourceUsageVO resource = new IaasResourceUsageVO();
                HashMap<String, Object> result = null;
                //keystone 버전 v2
                if( at.get("openstackKeystoneVersion").toString().equalsIgnoreCase("v2") ) {
                    result = apiService.getResourceInfoFromOpenstackV2( at.get("commonAccessEndpoint").toString()
                                                                       ,at.get("commonTenant").toString()
                                                                       ,at.get("commonAccessUser").toString()
                                                                       ,at.get("commonAccessSecret").toString());
                }else { //keystone 버전 v3
                    result = apiService.getResourceInfoFromOpenstackV3( at.get("commonAccessEndpoint").toString()
                                                                       ,at.get("openstackDomain").toString()
                                                                       ,at.get("commonProject").toString()
                                                                       ,at.get("commonAccessUser").toString()
                                                                       ,at.get("commonAccessSecret").toString());
                }
                resource.setAccountName( at.get("accountName").toString() );
                resource.setInstance( Long.parseLong(result.get("instance").toString()) );
                resource.setNetwork( Long.parseLong(result.get("network").toString()) );
                resource.setVolume( Long.parseLong(result.get("volume").toString()) );
                resource.setIaasType("Openstack");
                
                resources.add(resource);
            }
        }catch(Exception e){
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("iaas.openstack.account.exist.message.exception", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        return resources;
    }
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure 리소스 사용량 조회
     * @title : getAzureResourceUsageInfoList
     * @return : List<IaasResourceUsageVO>
    ***************************************************/
    public List<IaasResourceUsageVO> getAzureResourceUsageInfoList( Principal principal ){
        List<IaasResourceUsageVO> resources = new ArrayList<IaasResourceUsageVO>();
        int cost = 0;
        try{ //Azure
            List<HashMap<String, Object>> accounts = commonDao.selectAccountInfoList("azure", principal.getName());
            for( HashMap<String, Object> at : accounts ){
                IaasResourceUsageVO resource = new IaasResourceUsageVO();
                Double usageCost = apiService.setAzureBillingInfo(at.get("commonAccessUser").toString(), at.get("commonTenant").toString(), at.get("commonAccessSecret").toString(), at.get("azureSubscriptionId").toString());
                cost = Math.round(usageCost.intValue());
                HashMap<String, Object> result = apiService.getResourceInfoFromAzure( at.get("commonAccessUser").toString(), at.get("commonTenant").toString(), at.get("commonAccessSecret").toString(), at.get("azureSubscriptionId").toString());
                resource.setAccountName( at.get("accountName").toString() );
                resource.setInstance( Long.parseLong(result.get("instance").toString()) );
                resource.setNetwork( Long.parseLong(result.get("network").toString()) );
                resource.setVolume(  Long.parseLong(result.get("volume").toString()) );
                resource.setBilling(Double.parseDouble(String.valueOf(cost)));
                resource.setIaasType("Azure");
                resources.add(resource);
            }
        }catch(Exception e){
            if( LOGGER.isErrorEnabled() ){
                LOGGER.error(e.getMessage());
            }
        }
        return resources;
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
