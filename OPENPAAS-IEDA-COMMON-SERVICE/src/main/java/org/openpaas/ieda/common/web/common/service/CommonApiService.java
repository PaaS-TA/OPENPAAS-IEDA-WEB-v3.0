package org.openpaas.ieda.common.web.common.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.openstack.OSFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.compute.ComputeScopes;

@Service
public class CommonApiService {
    
    final private static String SEPARATOR = System.getProperty("file.separator");
    final private static String BASE_DIR  = System.getProperty("user.home") + SEPARATOR + ".bosh_plugin";
    final private static String KEY_DIR   = BASE_DIR + SEPARATOR + "key";
    final private static Logger LOGGER = LoggerFactory.getLogger(CommonApiService.class);
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : AWS 공통 credential API
     * @title : getAwsStaticCredentialsProvider
     * @return : AWSStaticCredentialsProvider
    ***************************************************/
    public AWSStaticCredentialsProvider getAwsStaticCredentialsProvider(String accessKey, String secret){
        BasicAWSCredentials credential = new BasicAWSCredentials(accessKey, secret);
        AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(credential);
        return provider;
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Openstack V2 접근 인증 토큰
     * @title : getAccountInfoFromOpenstackV2
     * @return : boolean
    ***************************************************/
    public OSClientV2 getOSClientFromOpenstackV2( String endpoint, String tenant, String user, String secret){
        OSClientV2 os = null;
        try{
            os = OSFactory.builderV2()
                    .endpoint(endpoint)
                    .credentials(user, secret)
                    .tenantName(tenant)
                    .authenticate();
        }catch(Exception e){
            if( LOGGER.isErrorEnabled() ){ LOGGER.error(e.getMessage()); }
        }
        return os;
   }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Openstack V3 접근 인증 토큰
     * @title : getAccountInfoFromOpenstackV3
     * @return : boolean
    ***************************************************/
    public OSClientV3 getOSClientFromOpenstackV3( String endpoint, String domain, String project, String user, String secret){
        OSClientV3 osV3 = null;
        try{
            Identifier domainIdentifier = Identifier.byName(domain);
            osV3 = OSFactory.builderV3()
                    .endpoint(endpoint )
                    .credentials(user, secret, domainIdentifier)
                    .scopeToProject(Identifier.byName(project), Identifier.byName(domain))
                    .authenticate();
            
        }catch(Exception e){
            if( LOGGER.isErrorEnabled() ){ LOGGER.error(e.getMessage()); }
        }
        return osV3;
   }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Google 계정 인증
     * @title : getGoogleCredentialFromGoogle
     * @return : Compute
    *****************************************************************/
    public GoogleCredential getGoogleCredentialFromGoogle( String jsonPath ){
        GoogleCredential credential= null;
        File file = new File(KEY_DIR + SEPARATOR + jsonPath);
        if( file.exists() ){
            try {
                InputStream inputStream = new BufferedInputStream(new FileInputStream(file)); 
                
                credential= GoogleCredential.fromStream(inputStream);
                List<String> computeScopes =Collections.singletonList(ComputeScopes.COMPUTE);
                if (credential.createScopedRequired()) {
                    credential = credential.createScoped(computeScopes);
                }
                
                credential.refreshToken();
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
        return credential;
    }

}
