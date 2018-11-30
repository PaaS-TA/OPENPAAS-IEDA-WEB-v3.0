package org.openpaas.ieda.deploy.web.deploy.bootstrap.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.deploy.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.deploy.web.common.dao.CommonDeployDAO;
import org.openpaas.ieda.deploy.web.common.dao.ManifestTemplateVO;
import org.openpaas.ieda.deploy.web.common.service.CommonDeployUtils;
import org.openpaas.ieda.deploy.web.config.setting.dao.DirectorConfigDAO;
import org.openpaas.ieda.deploy.web.config.setting.dao.DirectorConfigVO;
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
public class BootstrapDeleteDeployAsyncService{

    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private DirectorConfigDAO directorDao;
    @Autowired private BootstrapDAO bootstrapDao;
    @Autowired private CommonDeployDAO commonDao;
    @Autowired MessageSource message;
    
    final private static String SEPARATOR = System.getProperty("file.separator");
    final private static String DEPLOYMENT_DIR = LocalDirectoryConfiguration.getDeploymentDir() + SEPARATOR;
    final private static String LOCK_DIR=LocalDirectoryConfiguration.getLockDir();
    final private static String MESSAGE_ENDPOINT = "/deploy/bootstrap/delete/logs";
    final private static String CREDENTIAL_DIR = LocalDirectoryConfiguration.getGenerateCredentialDir() + SEPARATOR;
    final private static String RELEASE_DIR = LocalDirectoryConfiguration.getReleaseDir();
    final private static String STEMCELL_DIR = LocalDirectoryConfiguration.getStemcellDir();
    final private static String MANIFEST_TEMPLATE_PATH = LocalDirectoryConfiguration.getManifastTemplateDir() + SEPARATOR +"bootstrap";
    final private static String PRIVATE_KEY_PATH = LocalDirectoryConfiguration.getSshDir()+SEPARATOR;
    final private static String JSON_KEY_DIR = LocalDirectoryConfiguration.getKeyDir()+SEPARATOR;
    private final static Logger LOGGER = LoggerFactory.getLogger(BootstrapDeleteDeployAsyncService.class);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : bosh를 실행하여 해당 플랫폼 삭제 요청
     * @title : deleteBootstrapDeploy
     * @return : void
    *****************************************************************/
    public void deleteBootstrapDeploy(BootStrapDeployDTO.Delete dto, Principal principal) {
        
        String accumulatedLog = "";
        BootstrapVO vo = bootstrapDao.selectBootstrapInfo(Integer.parseInt(dto.getId()));
        if( vo == null ){
            bootstrapDao.deleteBootstrapInfo(Integer.parseInt(dto.getId()));
            vo = new BootstrapVO();
        }
        String status = "";
        String resultMessage = "";
        BufferedReader bufferedReader = null;
        ManifestTemplateVO result = commonDao.selectManifetTemplate(vo.getIaasType(), "267.8", "BOOTSTRAP", "bosh");

        try {
            String deployStateFile = DEPLOYMENT_DIR +vo.getDeploymentFile().split(".yml")[0] + "-state.json";
            File stateFile = new File(deployStateFile);
            if ( !stateFile.exists() ) {
                status = "done";
                resultMessage = "BOOTSTRAP 삭제가 완료되었습니다.";
                bootstrapDao.deleteBootstrapInfo(vo.getId());
                DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, status, Arrays.asList("BOOTSTRAP를 삭제했습니다."));
                
            }else{
                File credentialFile = new File(CREDENTIAL_DIR+vo.getCredentialKeyName());
                
                if(!credentialFile.exists()){
                    status = "error";
                    DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, status, Arrays.asList("BOOTSTRAP 디렉터 인증서가 존재 하지 않습니다."));
                }
                String deployFile = MANIFEST_TEMPLATE_PATH + SEPARATOR + result.getTemplateVersion()  + SEPARATOR  + "common" + SEPARATOR  + result.getCommonBaseTemplate();
                File file = new File(deployFile);
                if( file.exists() ){
                    List<String> cmd = new ArrayList<String>();
                    cmd.add("bosh");
                    cmd.add("delete-env");
                    cmd.add(deployFile);
                    cmd.add("--state="+ DEPLOYMENT_DIR + vo.getDeploymentFile().replace(".yml","")+"-state.json");
                    cmd.add("--vars-store="+CREDENTIAL_DIR+ vo.getCredentialKeyName());
                    
                    settingBoshInfo(cmd, vo);
                    settingIaasCpiInfo(cmd, vo, result);
                    //settingUaaInfo(cmd, bootstrapInfo, result);
                    //settingCredhubInfo(cmd, bootstrapInfo, result);
                    settingJumpBoxInfo(cmd, vo, result);
                    
                    if(!StringUtils.isEmpty(vo.getPublicStaticIp()) && vo.getPublicStaticIp() != null){
                        settingPublicIpInfo(cmd, vo, result);
                    }
                    
                    if(vo.getPaastaMonitoringUse().equals("true")){
                        settingPaastaMonitoring(cmd, vo, result);
                    }
                    
                    cmd.add("--tty");
                    
                    ProcessBuilder builder = new ProcessBuilder(cmd);
                    builder.redirectErrorStream(true);
                    Process process = builder.start();
                    
                       //배포 상태
                    vo.setDeployStatus( message.getMessage("common.deploy.status.deleting", null, Locale.KOREA) );
                    saveDeployStatus(vo, principal);
                    
                    //Delete log...
                    InputStream inputStream = process.getInputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                    StringBuffer accumulatedBuffer = new StringBuffer("");
                    String info = null;
                    while ((info = bufferedReader.readLine()) != null){
                        accumulatedBuffer.append(info).append("\n");
                        DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "started", Arrays.asList(info));
                    }
                    accumulatedLog = accumulatedBuffer.toString();
                } else {
                    status = "error";
                    resultMessage = "배포 파일(" + deployFile + ")이 존재하지 않습니다.";
                    vo.setDeployStatus( message.getMessage("common.deploy.status.failed", null, Locale.KOREA) );
                    saveDeployStatus(vo, principal);
                }
                
                if ( "error".equalsIgnoreCase(status) || accumulatedLog.contains("fail") || accumulatedLog.contains("error") || accumulatedLog.contains("No deployment") || accumulatedLog.contains("Error")) {
                    status = "error";
                    vo.setDeployStatus(message.getMessage("common.deploy.status.failed", null, Locale.KOREA));
                    saveDeployStatus(vo, principal);
                    if ( resultMessage.isEmpty() ) {
                        resultMessage = "BOOTSTRAP 삭제 중 오류가 발생하였습니다.<br> 로그를 확인하세요.";
                    }
                } else {
                    status = "done";
                    resultMessage = "BOOTSTRAP 삭제가 완료되었습니다.";
                    bootstrapDao.deleteBootstrapInfo(vo.getId());
                    deleteDirectorConfigInfo(vo.getIaasType(), vo.getDirectorName());
                }
                DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, status, Arrays.asList(resultMessage));
            }
        }catch(RuntimeException e){
            status = "error";
            DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, status, Arrays.asList("BOOTSTRAP 삭제 중 Exception이 발생하였습니다."));
        } catch ( Exception e) {
            status = "error";
            DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, status, Arrays.asList("BOOTSTRAP 삭제 중 Exception이 발생하였습니다."));
        }finally {
            if(status.toLowerCase().equalsIgnoreCase("error")){
                vo.setDeployStatus(message.getMessage("common.deploy.status.failed", null, Locale.KOREA));
                saveDeployStatus(vo, principal);
            }
            if(bufferedReader!=null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    if( LOGGER.isErrorEnabled() ){
                        LOGGER.error( e.getMessage() );
                    }
                }
            }
            CommonDeployUtils.deleteFile(LOCK_DIR, "bootstrap.lock");
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
        if(!(vo.getCloudInstanceType().equals("") || vo.getCloudInstanceType() == null)){
            cmd.add("-v");
            cmd.add("cloudInstanceType=" + vo.getCloudInstanceType());
        }
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
     * @description : 설치 상태 저장
     * @title : saveDeployStatus
     * @return : BootstrapVO
    *****************************************************************/
    public BootstrapVO saveDeployStatus(BootstrapVO bootstrapVo, Principal principal) {
        if ( bootstrapVo == null ) {
            return null;
        }
        bootstrapVo.setUpdateUserId(principal.getName());
        bootstrapDao.updateBootStrapInfo(bootstrapVo);
        return bootstrapVo;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 설치관리자 정보가 존재 할 경우 삭제
     * @title : deleteDirectorConfigInfo
     * @return : void
    ***************************************************/
    public void deleteDirectorConfigInfo(String iaasType, String directorName) {
        String cpi = iaasType.toLowerCase()+"_cpi";
        DirectorConfigVO vo = directorDao.selectDirectorConfigInfoByDirectorNameAndCPI(cpi, directorName);
        if( vo != null ) {
            directorDao.deleteDirector(vo.getIedaDirectorConfigSeq());
        }
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 비동기로  deleteDeploy 호출
     * @title : deleteDeployAsync
     * @return : void
    *****************************************************************/
    @Async
    public void deleteDeployAsync(BootStrapDeployDTO.Delete dto, Principal principal) {
            deleteBootstrapDeploy(dto, principal);
    }    

}
