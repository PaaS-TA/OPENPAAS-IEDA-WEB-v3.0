package org.openpaas.ieda.common.web.common.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.common.web.common.dao.CommonDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AmazonEC2Exception;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Zone;
import com.google.api.services.compute.model.ZoneList;
@Service
public class CommonService {

    @Autowired CommonDAO commondao;
    @Autowired MessageSource message;
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 공통 인프라 계정 정보 조회
     * @title : getIaasAccountInfoList
     * @return : HashMap<String, Object>
     ***************************************************/
    public List<HashMap<String, Object>> getIaasAccountInfoList(String iaasType, Principal principal) {
        return commondao.selectAccountInfoList(iaasType.toLowerCase(), principal.getName());
    }

    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : AWS 리전 목록 조회
     * @title : getAWSRegionList
     * @return : List<Region>
     ***************************************************/
    public List<Region> getAWSRegionList() {
        List<Region> regions = RegionUtils.getRegions();
        return regions;
    }

    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : AWS 가용영역 조회
     * @title : getAWSAvailabilityZoneByRegion
     * @return : List<String>
     ***************************************************/
    public List<String> getAWSAvailabilityZoneByRegion(Principal principal, int id, String region){
        List<String> zones  = new ArrayList<String>();
        CommonApiService common = new CommonApiService();
        HashMap<String, Object> ant = commondao.selectAccountInfoById(id, principal.getName());

        AWSStaticCredentialsProvider provider = common.getAwsStaticCredentialsProvider(
                ant.get("commonAccessUser").toString(), ant.get("commonAccessSecret").toString());
        Region regionId = RegionUtils.getRegion(region);
        AmazonEC2Client ec2 = (AmazonEC2Client) AmazonEC2ClientBuilder.standard().withRegion(regionId.getName()).withCredentials(provider).build();
        
        try{
             zones = ec2.describeAvailabilityZones().getAvailabilityZones().stream()
                    .filter(zone -> "available".equalsIgnoreCase(zone.getState())).map(AvailabilityZone::getZoneName)
                    .collect(Collectors.toList());
        }catch(AmazonEC2Exception e){
            throw new CommonException(message.getMessage("common.unauthorized.exception.code", null, Locale.KOREA), 
                    "해당 계정(<strong>" +  ant.get("accountName")  +"</strong>)은 AWS에 존재하지 않는 계정 입니다. <br> 인프라 관리 대시보드의 계정 관리 메뉴에서 AWS 계정 정보를 수정하시길 바랍니다.", HttpStatus.UNAUTHORIZED);
        }
        return zones;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : google 클라우드 zone 목록 조회
     * @title : getGoogleZoneList
     * @return : List<String>
    *****************************************************************/
    public List<String> getGoogleZoneList( Principal principal, int id ){
        List<String> zones  = new ArrayList<String>();
        HashMap<String, Object> account = commondao.selectAccountInfoById(id, principal.getName());
        
        HttpTransport httpTransport = null;
        JsonFactory jsonFactory = null;
        try{
            CommonApiService api = new CommonApiService();
            GoogleCredential credential = api.getGoogleCredentialFromGoogle(account.get("googleJsonKey").toString());
            
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            jsonFactory = JacksonFactory.getDefaultInstance();
            
            Compute computeService = new Compute.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName("Google-ComputeSample/0.1")
                    .build();
            
            Compute.Zones.List request = computeService.zones().list(account.get("commonProject").toString());
            ZoneList response;
            do {
              response = request.execute();
              if (response.getItems() == null) {
                continue;
              }
              for (Zone zone : response.getItems()) {
                  zones.add(zone.getName());
              }
              request.setPageToken(response.getNextPageToken());
            } while (response.getNextPageToken() != null);
            
        }catch(IOException e){
            throw new CommonException(message.getMessage("common.unauthorized.exception.code", null, Locale.KOREA), 
                    message.getMessage("common.account.unauthorized.message", null, Locale.KOREA) , HttpStatus.UNAUTHORIZED);
        } catch (GeneralSecurityException e) {
            throw new CommonException(message.getMessage("common.unauthorized.exception.code", null, Locale.KOREA), 
                    message.getMessage("common.account.unauthorized.message", null, Locale.KOREA) , HttpStatus.UNAUTHORIZED);
        }
        return zones;
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure 리전 목록 조회
     * @title : getAzureRegionList
     * @return : List<Region>
     ***************************************************/
   public List<com.microsoft.azure.management.resources.fluentcore.arm.Region> getAzureRegionList(){
    	com.microsoft.azure.management.resources.fluentcore.arm.Region[] region = com.microsoft.azure.management.resources.fluentcore.arm.Region.values();
    	List<com.microsoft.azure.management.resources.fluentcore.arm.Region> regions = Arrays.stream(region).collect(Collectors.toList());
    	return regions;
    }
}
