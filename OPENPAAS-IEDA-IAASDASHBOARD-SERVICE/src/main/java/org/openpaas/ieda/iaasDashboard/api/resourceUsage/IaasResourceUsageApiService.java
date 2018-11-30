package org.openpaas.ieda.iaasDashboard.api.resourceUsage;


import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang3.StringUtils;
import org.openpaas.ieda.common.web.common.service.CommonApiService;
import org.openpaas.ieda.deploy.api.director.utility.DirectorRestHelper;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.Subnet;
import org.openstack4j.model.storage.block.Volume;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeVolumesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Vpc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.credentials.AzureTokenCredentials;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.VirtualMachine;
import com.microsoft.azure.management.storage.StorageAccount;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.ListBlobItem;
import com.microsoft.rest.LogLevel;

/*import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.VirtualMachine;
import com.microsoft.azure.management.network.Network;
import com.microsoft.azure.management.storage.StorageAccount;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.ListBlobItem;*/
  
@Service
public class IaasResourceUsageApiService {
    
    final static Logger LOGGER = LoggerFactory.getLogger(IaasResourceUsageApiService.class);

    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : AWS 리소스 사용량(인스턴스, 네트워크, 볼륨)
     * @title : getResourceInfoFromAWS
     * @return : HashMap<String,Object>
    ***************************************************/
    public HashMap<String, Object> getResourceInfoFromAWS( String accessKey, String secret, Region region ){
        HashMap<String, Object> map = new HashMap<String, Object>();
      
        AWSStaticCredentialsProvider provider  = new CommonApiService().getAwsStaticCredentialsProvider(accessKey, secret);
        AmazonEC2Client ec2 =  (AmazonEC2Client)AmazonEC2ClientBuilder.standard().withRegion(region.getName()).withCredentials(provider).build();
        AmazonCloudWatchClient cloudWatch = (AmazonCloudWatchClient) AmazonCloudWatchClientBuilder.standard().withRegion(region.getName()).withCredentials(provider).build();
        
        map.put("iaasType", "AWS");
        //Instance count
        DescribeInstancesResult instanceReq =  ec2.describeInstances();
        List<Reservation> resList = instanceReq.getReservations();
        if( resList.size() > 0  ){
            List<Instance> instances = resList.get(0).getInstances();
            map.put("instance", instances.size());
        }else{
            map.put("instance", 0);
        }
        
        //Network count
        List<Vpc> vpcs = ec2.describeVpcs().getVpcs();
        if( vpcs.size() > 0 ){
            map.put("network", vpcs.size());
        }else{
            map.put("network", 0);
        }
        
        //volume count
        DescribeVolumesResult volumeReq= ec2.describeVolumes();
        List<com.amazonaws.services.ec2.model.Volume> volumes = volumeReq.getVolumes();
        if( volumes.size() > 0 ){
            map.put("volume", volumes.size());
        }else{
            map.put("volume", 0);
        }
        
        //cloudwatch billing
        GetMetricStatisticsRequest request = cloudwatchRequest(); 
        GetMetricStatisticsResult result = cloudWatch.getMetricStatistics(request);
        if( result.getDatapoints().size() > 0 ) {
            map.put("billing", result.getDatapoints().get(0).getMaximum());
        }else {
            map.put("billing", 0);
        }
        return map;
        
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Cloudwatch 과금 요청
     * @title : cloudwatchRequest
     * @return : GetMetricStatisticsRequest
    ***************************************************/
    private static GetMetricStatisticsRequest cloudwatchRequest() {
        Calendar cal = Calendar.getInstance(Locale.KOREA);
        cal.add(Calendar.MONTH, -1);
        final int oneHours = 60 * 60 * 24;
        return new GetMetricStatisticsRequest()
            .withStartTime(cal.getTime())
            .withNamespace("AWS/Billing")
            .withPeriod(oneHours)
            .withDimensions(new Dimension().withName("Currency").withValue("USD"))
            .withMetricName("EstimatedCharges")
            .withStatistics("Maximum")
            .withEndTime(new Date());
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Openstack 리소스 사용량 조회 (Keysone Version: v2)
     * @title : getResourceInfoFromOpenstackV2
     * @return : HashMap<String,Object>
    ***************************************************/
    public HashMap<String, Object> getResourceInfoFromOpenstackV2( String endpoint, String tenant, String user, String secret ){
        HashMap<String, Object> map = new HashMap<String, Object>();
        try{
            OSClientV2 os  = new CommonApiService().getOSClientFromOpenstackV2(endpoint, tenant, user, secret);
            List<? extends Server> servers = os.compute().servers().list();
            map.put("instance", servers.size());
            
            List<? extends Subnet> subnets = os.networking().subnet().list();
            map.put("network", subnets.size());
            
            List<? extends Volume> volumes = os.blockStorage().volumes().list();
            int total = 0;
            for( int i=0; i < volumes.size(); i++ ){
                total += volumes.get(i).getSize();
            }
            map.put("volume", total);
            map.put("iaasType", "OPENSTACK");
        }catch(Exception e){
            if( LOGGER.isErrorEnabled() ){ LOGGER.error(e.getMessage()); }
        }
        return map;
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Openstack 리소스 사용량 조회(Keystone version : v3)
     * @title : getResourceInfoFromOpenstackV3
     * @return : HashMap<String,Object>
    ***************************************************/
    public HashMap<String, Object> getResourceInfoFromOpenstackV3( String endpoint, String domain, String project,  String user, String secret ){
        HashMap<String, Object> map = new HashMap<String, Object>();
        try{
            OSClientV3 os  = new CommonApiService().getOSClientFromOpenstackV3(endpoint, domain, project, user, secret);
            List<? extends Server> servers = os.compute().servers().list();
            map.put("instance", servers.size());
            
            List<? extends Network> networks = os.networking().network().list();
            map.put("network", networks.size());

            List<? extends Volume> volumes = os.blockStorage().volumes().list();
            int total = 0;
            for( int i=0; i < volumes.size(); i++ ){
                total += volumes.get(i).getSize();
            }
            map.put("volume", total);
            map.put("iaasType", "OPENSTACK");
        }catch(Exception e){
            if( LOGGER.isErrorEnabled() ){ 
                LOGGER.error(e.getMessage()); 
            }
        }
        return map;
    }
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Azure 리소스 사용량 조회
     * @title : getResourceInfoFromAzure
     * @return : boolean
    ***************************************************/
    public HashMap<String, Object> getResourceInfoFromAzure( String commonAccessUser, String commonTenant, String commonAccessSecret,  String azureSubscriptionId ){
        HashMap<String, Object> map = new HashMap<String, Object>();
       try {
        long totalSize =0;
        AzureTokenCredentials azureCredentials = new CommonApiService().getAzureCredentialsFromAzure(commonAccessUser,  commonTenant,commonAccessSecret, azureSubscriptionId);
        Azure azure  = Azure.configure()
                .withLogLevel(LogLevel.BASIC)
                .authenticate(azureCredentials)
                .withSubscription(azureSubscriptionId);
        
        PagedList<com.microsoft.azure.management.network.Network> networks = azure.networks().list();
        if( networks.size() > 0 ){
            map.put("network", networks.size());
        }else{
            map.put("network", 0);
        }
        
        
        PagedList<VirtualMachine> vms = azure.virtualMachines().list();
        map.put("instance", vms.size());
        if( azure.getCurrentSubscription().state().toString().equals("Disabled") ){
            map.put("volume", totalSize);
            return map;
        }else{
          PagedList<StorageAccount> storageAccounts = azure.storageAccounts().list();
            long size = 0L;
            for( int i=0; i < storageAccounts.size(); i++ ){
                String storageConnectionString ="DefaultEndpointsProtocol=http;";
                storageConnectionString += "AccountName="+ storageAccounts.get(i).name()+";";
                storageConnectionString += "AccountKey="+storageAccounts.get(i).getKeys().get(0).value();
                CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

                CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
                Iterator<CloudBlobContainer> containers=  blobClient.listContainers().iterator();
                while( containers.hasNext()){
                    String containerName = containers.next().getName();
                    CloudBlobContainer container = blobClient.getContainerReference(containerName);
                    Iterable<ListBlobItem> blobItems=  container.listBlobs();
                    for (ListBlobItem blobItem : blobItems) {
                        if (blobItem instanceof CloudBlob) {
                            CloudBlob blob = (CloudBlob) blobItem;
                            size += blob.getProperties().getLength();
                        }
                    }
                }
            }
        	
            map.put("volume", size);
         }
        
        int totalcost = 0;
        int vmtoatalcost = 0;
        int netcost =0;
        int storagetoatalcost = 0;
        //int resourcecost = 0;
        int vmSize = azure.virtualMachines().manager().usages().listByRegion("centralus").size();
            for (int i=0;i<vmSize; i++){
                
                 vmtoatalcost =+ azure.virtualMachines().manager().usages().listByRegion("centralus").get(i).currentValue();
            }
        int netSize = azure.networks().manager().usages().listByRegion("centralus").size();
            for (int i=0;i<netSize; i++){
                Long networktoatalcost =+ azure.networks().manager().usages().listByRegion("centralus").get(i).currentValue();
                netcost = networktoatalcost.intValue();
            }
        int volumeSize = azure.storageAccounts().manager().usages().list().size();
            for (int i=0; i< volumeSize; i++){
                storagetoatalcost  =+ azure.storageAccounts().manager().usages().list().get(i).currentValue();
                //resourcecost =+ azure.storageUsages().list().get(i).currentValue();
               
            }
        totalcost = vmtoatalcost + netcost + storagetoatalcost;
        if( totalcost != 0 ){
            map.put("billing", totalcost);
        }else{
            map.put("billing", 00.00);
        }
        
        map.put("iaasType", "AZURE");
        
       }
        catch (InvalidKeyException e) {
            if( LOGGER.isErrorEnabled() ){ LOGGER.error(e.getMessage()); }
        } catch (URISyntaxException e) {
            if( LOGGER.isErrorEnabled() ){ LOGGER.error(e.getMessage()); }
        } catch (StorageException e) {
            if( LOGGER.isErrorEnabled() ){ LOGGER.error(e.getMessage()); }
        }
        
        return map;
   }

    final private static String AZURE_TOKEN_URL = "https://login.microsoftonline.com/";
    final private static String AZURE_ACQUIRE_TOKEN_URL = "https://management.azure.com/";
    /***************************************************
     * @return 
     * @project : 인프라 관리 대시보드
     * @description : Azure 과금 조회
     * @title : getResourceInfoFromAzure
     * @return : boolean
    ***************************************************/
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Double setAzureBillingInfo(String commonAccessUser, String commonTenant, String commonAccessSecret,  String azureSubscriptionId) {
        AuthenticationContext authContext = null;
        AuthenticationResult authResult = null;
        ExecutorService service = null;
        Double costSum = 0.0;
        try {
            service = Executors.newFixedThreadPool(1);
            String url = AZURE_TOKEN_URL + commonTenant + "/oauth2/token";
            authContext = new AuthenticationContext(url, false, service);
            ClientCredential clientCred = new ClientCredential(commonAccessUser, commonAccessSecret);
            Future<AuthenticationResult> future = authContext.acquireToken(AZURE_ACQUIRE_TOKEN_URL, clientCred, null);
            authResult = future.get();
            if( !StringUtils.isEmpty(authResult.getAccessToken())){
                String accessToken = authResult.getAccessToken();
                Calendar cal = Calendar.getInstance();
                int year = cal.get ( cal.YEAR );
                int month = cal.get ( cal.MONTH );
                String sMonth = "";
                if(month < 10){
                    sMonth = "0"+String.valueOf(month);
                } else {
                    sMonth = String.valueOf(month);
                }
                String setDateInfo = String.valueOf(year) + sMonth + "-1";
                HttpClient httpClient = DirectorRestHelper.getHttpClient(443);
                GetMethod get = new GetMethod(DirectorRestHelper.getAzureBillingInfoUri("management.azure.com","443", azureSubscriptionId, setDateInfo));
                get = (GetMethod)DirectorRestHelper.setAuthorization(accessToken, (HttpMethodBase)get);
                get.setRequestHeader("Authorization", "Bearer " + accessToken);
                httpClient.executeMethod(get);
                if ( !StringUtils.isEmpty(get.getResponseBodyAsString()) ) {
                    ObjectMapper mapper = new ObjectMapper();
                    HashMap<String, Object> usageMaps = mapper.readValue(get.getResponseBodyAsString(), HashMap.class);
                    if(usageMaps != null && usageMaps.size() != 0) {
                        List<LinkedHashMap> usageList =  (List<LinkedHashMap>) usageMaps.get("value");
                        if(usageList != null && usageList.size() != 0){
                            for(int i=0; i<usageList.size(); i++){
                                HashMap<String, Object> costs = (HashMap<String, Object>) usageList.get(i).get("properties");
                                if(costs != null && costs.size()!=0 && !"0".equalsIgnoreCase(costs.get("pretaxCost").toString()))
                                costSum += (Double)costs.get("pretaxCost");
                            }
                        }
                    }
                }
                
            }
        } catch (Exception e) {
            if( LOGGER.isErrorEnabled() ){ LOGGER.error(e.getMessage()); }
            e.printStackTrace();
        }
        return costSum;
    }
 
    
}
