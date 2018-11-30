package org.openpaas.ieda.deploy.web.deploy.bootstrap.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.deploy.api.director.dto.DirectorInfoDTO;
import org.openpaas.ieda.deploy.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.deploy.web.common.dao.CommonDeployDAO;
import org.openpaas.ieda.deploy.web.common.dao.ManifestTemplateVO;
import org.openpaas.ieda.deploy.web.config.setting.service.DirectorConfigService;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.dao.BootstrapDAO;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.dao.BootstrapVO;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.dto.BootStrapDeployDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class BootstrapDeployAsyncService {

    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private DirectorConfigService directorConfigService;
    @Autowired private BootstrapDAO bootstrapDao;
    @Autowired private MessageSource message;
    @Autowired private CommonDeployDAO commonDao;
    
    final private static String SEPARATOR = System.getProperty("file.separator");
    final private static String DEPLOYMENT_DIR = LocalDirectoryConfiguration.getDeploymentDir() + SEPARATOR;
    final private static String KEY_DIR = LocalDirectoryConfiguration.getLockDir()+SEPARATOR;
    final private static String CREDENTIAL_DIR = LocalDirectoryConfiguration.getGenerateCredentialDir() + SEPARATOR;
    final private static String MESSAGE_ENDPOINT = "/deploy/bootstrap/install/logs"; 
    final private static String RELEASE_DIR = LocalDirectoryConfiguration.getReleaseDir();
    final private static String STEMCELL_DIR = LocalDirectoryConfiguration.getStemcellDir();
    final private static String MANIFEST_TEMPLATE_PATH = LocalDirectoryConfiguration.getManifastTemplateDir() + SEPARATOR +"bootstrap";
    final private static String PRIVATE_KEY_PATH = LocalDirectoryConfiguration.getSshDir()+SEPARATOR;
    final private static String JSON_KEY_DIR = LocalDirectoryConfiguration.getKeyDir()+SEPARATOR;
    private final static Logger LOGGER = LoggerFactory.getLogger(BootstrapDeployAsyncService.class);
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : bosh를 실행하여 Bootstrap 설치 실행
     * @title : deployBootstrap
     * @return : void
    *****************************************************************/
    public void deployBootstrap(BootStrapDeployDTO.Install dto, Principal principal) {
        
        String status = "";
        String accumulatedLog= null;
        BufferedReader bufferedReader = null;
        BootstrapVO bootstrapInfo = new BootstrapVO();
        try {
            bootstrapInfo = bootstrapDao.selectBootstrapInfo(Integer.parseInt(dto.getId()));
            ManifestTemplateVO result = commonDao.selectManifetTemplate(bootstrapInfo.getIaasType(), "267.8", "BOOTSTRAP", "bosh");
            String deployFile = "";
            
            if( bootstrapInfo != null ) {
                deployFile = MANIFEST_TEMPLATE_PATH + SEPARATOR + result.getTemplateVersion()  + SEPARATOR  + "common" + SEPARATOR  + result.getCommonBaseTemplate();
            }
            
            File deploymentFile = new File(deployFile);
            
            if( deploymentFile.exists() ) {
                  //1. 배포상태 설정
                bootstrapInfo.setUpdateUserId( principal.getName());
                String deployStatus = message.getMessage("common.deploy.status.processing", null, Locale.KOREA);
                bootstrapInfo.setDeployStatus( deployStatus );
                saveDeployStatus(bootstrapInfo);
                //2. bosh 실행
                List<String> cmd = new ArrayList<String>();
                cmd.add("bosh");
                cmd.add("create-env");
                cmd.add(deployFile);
                cmd.add("--state="+ DEPLOYMENT_DIR + bootstrapInfo.getDeploymentFile().replace(".yml","")+"-state.json");
                cmd.add("--vars-store="+CREDENTIAL_DIR+ bootstrapInfo.getCredentialKeyName());
                
                settingBoshInfo(cmd, bootstrapInfo);
                settingIaasCpiInfo(cmd, bootstrapInfo, result);
                //settingUaaInfo(cmd, bootstrapInfo, result);
                //settingCredhubInfo(cmd, bootstrapInfo, result);
                settingJumpBoxInfo(cmd, bootstrapInfo, result);
                if(!StringUtils.isEmpty(bootstrapInfo.getPublicStaticIp()) && bootstrapInfo.getPublicStaticIp() != null){
                    settingPublicIpInfo(cmd, bootstrapInfo, result);
                }
                
                if(bootstrapInfo.getPaastaMonitoringUse().equals("true")){
                    settingPaastaMonitoring(cmd, bootstrapInfo, result);
                }
                
                cmd.add("--tty");
                
                ProcessBuilder builder = new ProcessBuilder(cmd);
                builder.redirectErrorStream(true);
                Process process = builder.start();

                //실행 출력하는 로그를 읽어온다.
                InputStream inputStream = process.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                String info = null;
                StringBuffer accumulatedBuffer = new StringBuffer();
                while ((info = bufferedReader.readLine()) != null){
                    accumulatedBuffer.append(info).append("\n");
                    DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "started", Arrays.asList(info));
                }
                if( accumulatedBuffer != null ) {
                    accumulatedLog = accumulatedBuffer.toString();
                }
            } else {
                status = "error";
            }
            if( accumulatedLog != null ) {
                bootstrapInfo.setDeployLog(accumulatedLog);
            }
            if ( status.equalsIgnoreCase("error") ) {
                bootstrapInfo.setDeployStatus( message.getMessage("common.deploy.status.failed", null, Locale.KOREA) );
                saveDeployStatus(bootstrapInfo);
                DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList( "설치할 배포 파일(" + deployFile + ")이 존재하지 않습니다."));
            } else {
                if (!accumulatedLog.contains("Succeeded")) {
                    status = "error";
                    bootstrapInfo.setDeployStatus(message.getMessage("common.deploy.status.failed", null, Locale.KOREA) );
                    saveDeployStatus(bootstrapInfo);
                    DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("", "BOOTSTRAP 설치 중 오류가 발생하였습니다.<br> 배포 정보 및 로그를 확인 하세요."));
                }    else {
                    // 타겟 테스트
                    DirectorInfoDTO directorInfo = null;
                    if(bootstrapInfo.getPublicStaticIp().isEmpty() || bootstrapInfo.getPublicStaticIp() == null){
                        DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "started", Arrays.asList("","BOOTSTRAP 디렉터 정보 : https://" + bootstrapInfo.getPrivateStaticIp() + ":25555"));
                        DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "started", Arrays.asList("BOOTSTRAP 디렉터 타겟 접속 테스트..."));
                        directorInfo = directorConfigService.getDirectorInfo(bootstrapInfo.getPrivateStaticIp(), 25555, "admin", "admin");
                    }else{
                        DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "started", Arrays.asList("","BOOTSTRAP 디렉터 정보 : https://" + bootstrapInfo.getPublicStaticIp() + ":25555"));
                        DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "started", Arrays.asList("BOOTSTRAP 디렉터 타겟 접속 테스트..."));
                        directorInfo = directorConfigService.getDirectorInfo(bootstrapInfo.getPublicStaticIp(), 25555, "admin", "admin");
                    }
                    
                    if ( directorInfo == null ) {
                        status = "error";
                        bootstrapInfo.setDeployStatus(message.getMessage("common.deploy.status.failed", null, Locale.KOREA));
                        saveDeployStatus(bootstrapInfo);
                        DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("BOOTSTRAP 디렉터 타겟 접속 테스트 실패 <br> 인프라 정보를 확인 하세요."));
                    } else {
                        DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "started", Arrays.asList("BOOTSTRAP 디렉터 타겟 접속 테스트 성공"));
                        status = "done";
                        bootstrapInfo.setDeployStatus( message.getMessage("common.deploy.status.done", null,  Locale.KOREA ) );
                        saveDeployStatus(bootstrapInfo);
                        DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "done", Arrays.asList("", "BOOTSTRAP 설치가 완료되었습니다."));
                    }
                }
            }
        }catch(RuntimeException e){
        
            status = "error";
            DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("배포 중 Exception이 발생하였습니다."));
            if ( bootstrapInfo != null ) {
                bootstrapInfo.setDeployLog(accumulatedLog);
            }
            String deployStatus = message.getMessage("common.deploy.status.failed", null, Locale.KOREA);
            if( deployStatus != null ) {
                bootstrapInfo.setDeployStatus( deployStatus );
            }
            saveDeployStatus(bootstrapInfo);
        }catch ( Exception e) {    
            status = "error";
            e.printStackTrace();
            DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("배포 중 Exception이 발생하였습니다."));
            if ( bootstrapInfo != null ) {
                bootstrapInfo.setDeployLog(accumulatedLog);
            }
            String deployStatus =  message.getMessage("common.deploy.status.failed", null, Locale.KOREA) ;
            if( deployStatus != null ) {
                bootstrapInfo.setDeployStatus(message.getMessage("common.deploy.status.failed", null, Locale.KOREA) );
            }
            saveDeployStatus(bootstrapInfo);
        }finally {
            try {
                if(bufferedReader!=null) {
                    bufferedReader.close();
                    
                }
            } catch (Exception e) {
                if( LOGGER.isErrorEnabled() ) {
                    LOGGER.error( e.getMessage() );
                }
            }
            //동시 설치 방지 lock 파일 삭제
            File lockFile = new File(KEY_DIR + "bootstrap.lock");
            if(lockFile.exists()){
                Boolean check = lockFile.delete();
                if( LOGGER.isDebugEnabled() ) {
                    LOGGER.debug("check delete lock File  : "  + check); 
                }
            }
        }
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  BOSH Release 관련 CMD 정의 
     * @title : settingBoshInfo
     * @return : void
    *****************************************************************/
    private void settingBoshInfo(List<String> cmd, BootstrapVO vo){
        cmd.add("-v");
        cmd.add("boshRelease="+ RELEASE_DIR + SEPARATOR + vo.getBoshRelease()+ "");
        cmd.add("-v");
        cmd.add("bpmRelease="+ RELEASE_DIR + SEPARATOR + vo.getBoshBpmRelease()+ "");
        cmd.add("-v");
        cmd.add("internal_cidr="+ vo.getSubnetRange()+ "");
        cmd.add("-v");
        cmd.add("internal_gw="+ vo.getSubnetGateway()+ "");
        cmd.add("-v");
        cmd.add("internal_dns="+ vo.getSubnetDns()+ "");
        cmd.add("-v");
        cmd.add("ntp="+ vo.getNtp()+ "");
        cmd.add("-v");
        cmd.add("director_name="+ vo.getDirectorName()+ "");
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  Public IP 사용 CMD 정의
     * @title : settingPublicIpInfo
     * @return : void
    *****************************************************************/
    private void settingPublicIpInfo(List<String> cmd, BootstrapVO vo, ManifestTemplateVO result){
        cmd.add("-o");
        cmd.add(MANIFEST_TEMPLATE_PATH + SEPARATOR + result.getMinReleaseVersion() + SEPARATOR + "common/" + result.getInputTemplateSecond());
        cmd.add("-v");
        cmd.add("external_ip="+ vo.getPublicStaticIp() + "");
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  JumpBox CMD 정의
     * @title : settingJumpBoxInfo
     * @return : void
    *****************************************************************/
    private void settingJumpBoxInfo(List<String> cmd, BootstrapVO vo, ManifestTemplateVO result){
        cmd.add("-o");
        cmd.add(MANIFEST_TEMPLATE_PATH + SEPARATOR + result.getMinReleaseVersion() + SEPARATOR + "common/" + result.getMetaTemplate());
        cmd.add("-v");
        cmd.add("osRelease="+ RELEASE_DIR + SEPARATOR + vo.getOsConfRelease()+ "");
    }
    
/*    *//****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  Credhub CMD 정의
     * @title : settingCredhubInfo
     * @return : void
    *****************************************************************//*
    private void settingCredhubInfo(List<String> cmd, BootstrapVO vo, ManifestTemplateVO result){
        cmd.add("-o");
        cmd.add(MANIFEST_TEMPLATE_PATH + SEPARATOR + result.getMinReleaseVersion() + SEPARATOR + "common/" + result.getOptionEtc());
        cmd.add("-v");
        cmd.add("credhubRelease="+ RELEASE_DIR + SEPARATOR + vo.getBoshCredhubRelease()+ "");
    }*/
    
/*    *//****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  Uaa CMD 정의
     * @title : settingUaaInfo
     * @return : void
    *****************************************************************//*
    private void settingUaaInfo(List<String> cmd, BootstrapVO vo, ManifestTemplateVO result){
        cmd.add("-o");
        cmd.add(MANIFEST_TEMPLATE_PATH + SEPARATOR + result.getMinReleaseVersion() + SEPARATOR + "common/" + result.getCommonOptionTemplate());
        cmd.add("-v");
        cmd.add("uaaRelease="+ RELEASE_DIR + SEPARATOR + vo.getBoshUaaRelease()+ "");
    }*/
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  CPI CMD 정의
     * @title : settingIaasCpiInfo
     * @return : void
    *****************************************************************/
    private void settingIaasCpiInfo(List<String> cmd, BootstrapVO vo, ManifestTemplateVO result){
        cmd.add("-o");
        cmd.add(MANIFEST_TEMPLATE_PATH + SEPARATOR + vo.getIaasType().toLowerCase() + SEPARATOR + result.getIaasPropertyTemplate());
        if("aws".equalsIgnoreCase(vo.getIaasType())){
            cmd.add("-v");
            cmd.add("az=" + vo.getIaasConfig().getCommonAvailabilityZone());
            cmd.add("-v");
            cmd.add("subnet_id=" + vo.getSubnetId());
            cmd.add("-v");
            cmd.add("region=" + vo.getIaasAccount().get("commonRegion").toString());
            cmd.add("-v");
            cmd.add("access_key_id=" + vo.getIaasAccount().get("commonAccessUser").toString());
            cmd.add("-v");
            cmd.add("secret_access_key=" + vo.getIaasAccount().get("commonAccessSecret").toString());
        }else if("openstack".equalsIgnoreCase(vo.getIaasType())){
            cmd.add("-v");
            cmd.add("net_id=" + vo.getSubnetId());
            cmd.add("-v");
            cmd.add("auth_url=" + vo.getIaasAccount().get("commonAccessEndpoint").toString());
            cmd.add("-v");
            cmd.add("openstack_username=" + vo.getIaasAccount().get("commonAccessUser").toString());
            cmd.add("-v");
            cmd.add("openstack_password=" + vo.getIaasAccount().get("commonAccessSecret").toString());
            if(vo.getIaasAccount().get("openstackVersion").toString().equalsIgnoreCase("v3")){
                cmd.add("-v");
                cmd.add("openstack_domain=" + vo.getIaasAccount().get("openstackDomain").toString());
                cmd.add("-v");
                cmd.add("openstack_project=" + vo.getIaasAccount().get("commonProject").toString());
                cmd.add("-v");
                cmd.add("region=" + vo.getIaasAccount().get("commonRegion").toString());
            }else {
                cmd.add("-o");
                cmd.add(MANIFEST_TEMPLATE_PATH + SEPARATOR + vo.getIaasType().toLowerCase() + SEPARATOR  + result.getInputTemplate());
                cmd.add("-v");
                cmd.add("openstack_tenant=" + vo.getIaasAccount().get("commonTenant").toString());
            }
        }else if("azure".equalsIgnoreCase(vo.getIaasType())){
            cmd.add("-v");
            cmd.add("vnet_name=" + vo.getNetworkName());
            cmd.add("-v");
            cmd.add("subnet_name=" + vo.getSubnetId());
            cmd.add("-v");
            cmd.add("subscription_id=" + vo.getIaasAccount().get("azureSubscriptionId").toString());
            cmd.add("-v");
            cmd.add("tenant_id=" + vo.getIaasAccount().get("commonTenant").toString());
            cmd.add("-v");
            cmd.add("client_id=" + vo.getIaasAccount().get("commonAccessUser").toString());
            cmd.add("-v");
            cmd.add("client_secret=" + vo.getIaasAccount().get("commonAccessSecret").toString());
            cmd.add("-v");
            cmd.add("resource_group_name=" + vo.getIaasConfig().getAzureResourceGroup());
            cmd.add("-v");
            cmd.add("storage_account_name=" + vo.getIaasConfig().getAzureStorageAccountName());
            cmd.add("-v");
            cmd.add("public_key=" + vo.getIaasConfig().getAzureSshPublicKey());
        }else if("google".equalsIgnoreCase(vo.getIaasType())){
            cmd.add("-v");
            cmd.add("network=" + vo.getNetworkName());
            cmd.add("-v");
            cmd.add("subnetwork=" + vo.getSubnetId());
            cmd.add("-v");
            cmd.add("tags=" + vo.getIaasConfig().getCommonSecurityGroup());
            cmd.add("-v");
            cmd.add("project_id=" + vo.getIaasAccount().get("commonProject").toString());
            cmd.add("-v");
            cmd.add("zone=" + vo.getIaasConfig().getCommonAvailabilityZone());
            cmd.add("--var-file");
            cmd.add("gcp_credentials_json=" + JSON_KEY_DIR + vo.getIaasAccount().get("googleJsonKey").toString());
        }else if("vsphere".equalsIgnoreCase(vo.getIaasType())){
//          cmd.add("-o");
//          cmd.add(MANIFEST_TEMPLATE_PATH + SEPARATOR + vo.getIaasType().toLowerCase() + SEPARATOR  + result.getOptionResourceTemplate());
            cmd.add("-v");
            cmd.add("network_name=" + vo.getNetworkName());
            cmd.add("-v");
            cmd.add("vcenter_dc=" + vo.getIaasConfig().getVsphereVcentDataCenterName());
            cmd.add("-v");
            cmd.add("vcenter_ds=" + vo.getIaasConfig().getVsphereVcenterDatastore());
            cmd.add("-v");
            cmd.add("vcenter_ip=" + vo.getIaasAccount().get("commonAccessEndpoint").toString());//
            cmd.add("-v");
            cmd.add("vcenter_user=" + vo.getIaasAccount().get("commonAccessUser").toString());//
            cmd.add("-v");
            cmd.add("vcenter_password=" + vo.getIaasAccount().get("commonAccessSecret").toString());//
            cmd.add("-v");
            cmd.add("vcenter_templates=" + vo.getIaasConfig().getVsphereVcenterTemplateFolder());
            cmd.add("-v");
            cmd.add("vcenter_vms=" + vo.getIaasConfig().getVsphereVcenterVmFolder());
            cmd.add("-v");
            cmd.add("vcenter_disks=" + vo.getIaasConfig().getVsphereVcenterDiskPath());
            cmd.add("-v");
            cmd.add("vcenter_cluster=" + vo.getIaasConfig().getVsphereVcenterCluster());
//          cmd.add("-v");
//          cmd.add("vcenter_rp=" + vo.getIaasConfig());//
            cmd.add("-v");
            cmd.add("resourcePoolCPU=" + vo.getResourcePoolCpu());
            cmd.add("-v");
            cmd.add("resourcePoolRAM=" + vo.getResourcePoolRam());
            cmd.add("-v");
            cmd.add("resourcePoolDisk=" + vo.getResourcePoolDisk());
        }
        cmd.add("-v");
        cmd.add("boshCpiRelease=" + RELEASE_DIR + SEPARATOR + vo.getBoshCpiRelease() + "");
        cmd.add("-v");
        cmd.add("stemcell=" + STEMCELL_DIR + SEPARATOR + vo.getStemcell() + "");
        cmd.add("-v");
        cmd.add("cloudInstanceType=" + vo.getCloudInstanceType());
        cmd.add("-v");
        cmd.add("internal_ip=" + vo.getPrivateStaticIp());
        cmd.add("-v");
        cmd.add("default_key_name=" + vo.getIaasConfig().getCommonKeypairName());
        cmd.add("-v");
        cmd.add("default_security_groups=" + vo.getIaasConfig().getCommonSecurityGroup());
        cmd.add("-v");
        cmd.add("private_key=" + PRIVATE_KEY_PATH + vo.getIaasConfig().getCommonKeypairPath());
        
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 모니터링 CMD 정의
     * @title : settingPaastaMonitoring
     * @return : void
    *****************************************************************/
    private void settingPaastaMonitoring(List<String> cmd, BootstrapVO vo, ManifestTemplateVO result){
        cmd.add("-o");
        cmd.add(MANIFEST_TEMPLATE_PATH + SEPARATOR + result.getMinReleaseVersion() + SEPARATOR + "common/" + "syslog.yml");
        cmd.add("-o");
        cmd.add(MANIFEST_TEMPLATE_PATH + SEPARATOR + result.getMinReleaseVersion() + SEPARATOR + "common/" + "paasta-monitoring-agent.yml");
        cmd.add("-v");
        cmd.add("metric_url="+ RELEASE_DIR + SEPARATOR + "");
        cmd.add("-v");
        cmd.add("syslog_address="+ RELEASE_DIR + SEPARATOR +  "");
        cmd.add("-v");
        cmd.add("syslog_port="+ RELEASE_DIR + SEPARATOR +  "");
        cmd.add("-v");
        cmd.add("syslog_transport="+ RELEASE_DIR + SEPARATOR + "");
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 설치 상태를 설정하여 저장
     * @title : saveDeployStatus
     * @return : BootstrapVO
    *****************************************************************/
    public BootstrapVO saveDeployStatus(BootstrapVO bootstrapVo) {
        if ( bootstrapVo == null ) {
            return null;
        }
        bootstrapDao.updateBootStrapInfo(bootstrapVo);
        
        return bootstrapVo;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 비동기로 deployAsync 메소드 호출
     * @title : deployAsync
     * @return : void
    *****************************************************************/
    @Async
    public void deployAsync(BootStrapDeployDTO.Install dto, Principal principal) {
            deployBootstrap(dto, principal);
    }
    
}
