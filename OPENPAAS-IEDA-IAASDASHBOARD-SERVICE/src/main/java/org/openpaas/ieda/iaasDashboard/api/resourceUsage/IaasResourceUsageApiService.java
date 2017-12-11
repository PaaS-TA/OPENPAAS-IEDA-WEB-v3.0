package org.openpaas.ieda.iaasDashboard.api.resourceUsage;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.openpaas.ieda.common.web.common.service.CommonApiService;
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
    
}
