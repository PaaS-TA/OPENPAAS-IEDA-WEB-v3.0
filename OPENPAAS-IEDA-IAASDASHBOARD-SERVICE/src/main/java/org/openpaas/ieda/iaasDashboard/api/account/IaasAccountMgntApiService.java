package org.openpaas.ieda.iaasDashboard.api.account;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;

import org.apache.commons.lang3.StringUtils;
import org.openpaas.ieda.common.web.common.service.CommonApiService;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.api.OSClient.OSClientV3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Project;
import com.vmware.vim25.mo.ServiceInstance;

@Service
public class IaasAccountMgntApiService {
    
    final private static Logger LOGGER = LoggerFactory.getLogger(IaasAccountMgntApiService.class);
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : AWS 계정 정보 조회
     * @title : getAccountInfoFromAWS
     * @return : boolean
    ***************************************************/
    public boolean getAccountInfoFromAWS( String accessKey, String secret ){
        boolean flag = false;
        try{
            
            CommonApiService credential = new CommonApiService();
            AWSStaticCredentialsProvider provider =credential.getAwsStaticCredentialsProvider(accessKey, secret);
            AmazonIdentityManagement  identity =  AmazonIdentityManagementClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).withCredentials(provider).build();
            String user =  identity.getUser().toString();
            
            if( !StringUtils.isEmpty(user) ){
                flag = true;
            }
        }catch(Exception e){
            if( LOGGER.isErrorEnabled() ){ LOGGER.error(e.getMessage()); }
        }
        return flag;
   }
    
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Openstack v2 버전 계정 정보 조회
     * @title : getAccountInfoFromOpenstackV2
     * @return : boolean
    ***************************************************/
    public boolean getAccountInfoFromOpenstackV2( String endpoint, String tenant, String user, String secret){
        boolean flag = false;
        try{
            CommonApiService credentials = new CommonApiService();
            OSClientV2 os = credentials.getOSClientFromOpenstackV2(endpoint, tenant, user, secret);
            
            if( !StringUtils.isEmpty(os.getAccess().getToken().toString()) ){
                flag = true;
            }
        }catch(Exception e){
            if( LOGGER.isErrorEnabled() ){ LOGGER.error(e.getMessage()); }
        }
        return flag;
   }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Openstack v3 버전 계정 정보 조회
     * @title : getAccountInfoFromOpenstackV3
     * @return : boolean
    ***************************************************/
    public boolean getAccountInfoFromOpenstackV3( String endpoint, String domain, String project, String user, String secret){
        boolean flag = false;
        try{
            CommonApiService credentials = new CommonApiService();
            OSClientV3 osV3 = credentials.getOSClientFromOpenstackV3(endpoint, domain, project, user, secret);
            
            if( StringUtils.isEmpty(osV3.getToken().toString()) ){
                flag = false;
            }
        }catch(Exception e){
            if( LOGGER.isErrorEnabled() ){ LOGGER.error(e.getMessage()); }
        }
        return flag;
   }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : vSPhere 계정 조회
     * @title : getAccountInfoFromVsphere
     * @return : boolean
    ***************************************************/
    public boolean getAccountInfoFromVsphere( String endpoint, String user, String secret){
        boolean flag = false;
        String url = "https://"+endpoint +"/sdk/vimService";
        try {
            ServiceInstance serviceInstance = new ServiceInstance(new URL(url), user, secret, true);
            if( !StringUtils.isEmpty(serviceInstance.getAboutInfo().getName()) ){
                flag = true;
            }
        } catch (RuntimeException ex) {
            if( LOGGER.isErrorEnabled() ){ LOGGER.error(ex.getMessage()); }
        } catch (Exception ex) {
            if( LOGGER.isErrorEnabled() ){ LOGGER.error(ex.getMessage()); }
        }
        return flag;
   }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Google 계정 확인
     * @title : getAccountInfoFromGoogle
     * @return : boolean
    *****************************************************************/
    public boolean getAccountInfoFromGoogle( String jsonPath, String projectId){
        boolean flag = false;
        HttpTransport httpTransport = null;
        JsonFactory jsonFactory = null;
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            jsonFactory = JacksonFactory.getDefaultInstance();
            
            GoogleCredential credential = new CommonApiService().getGoogleCredentialFromGoogle(jsonPath);
            
            Compute computeService = new Compute.Builder(httpTransport, jsonFactory, credential)
            .setApplicationName("Google-ComputeSample/0.1")
            .build();
            
            Compute.Projects.Get request = computeService.projects().get(projectId);
            Project project = request.execute();
            
            if( project != null ){
                flag = true;
            }
        }catch(IOException e){
            LOGGER.error(e.getMessage());
        } catch (GeneralSecurityException e) {
            LOGGER.error(e.getMessage());
        }
        return flag;
   }
}
