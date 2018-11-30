package org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service;

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

import org.apache.commons.httpclient.HttpClient;
import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.web.common.dao.CommonDeployDAO;
import org.openpaas.ieda.deploy.web.common.dao.ManifestTemplateVO;
import org.openpaas.ieda.hbdeploy.api.director.utility.HbDirectorRestHelper;
import org.openpaas.ieda.hbdeploy.web.config.setting.dao.HbDirectorConfigDAO;
import org.openpaas.ieda.hbdeploy.web.config.setting.dao.HbDirectorConfigVO;
import org.openpaas.ieda.hbdeploy.web.config.setting.service.HbDirectorConfigService;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentVO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dto.HbCfDeploymentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class HbCfDeploymentDeployAsyncService {
    
    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private HbDirectorConfigDAO directorConfigDao;
    @Autowired private HbDirectorConfigService directorConfigService;
    @Autowired private HbCfDeploymentDAO cfDeploymentDao;
    @Autowired private HbCfDeploymentService cfDeploymentService;
    @Autowired private MessageSource message;
    @Autowired private CommonDeployDAO commonDao;
    
    private final static String SEPARATOR = System.getProperty("file.separator");
    private final static String KEY_DIR = LocalDirectoryConfiguration.getLockDir()+SEPARATOR;
    private final static String MANIFEST_TEMPLATE_DIR = LocalDirectoryConfiguration.getManifastTemplateDir();
    final private static String HYBRID_CF_CREDENTIAL_DIR = LocalDirectoryConfiguration.getGenerateHybridCfCredentialDir();
    private final static String DEPLOYMENT_DIR = LocalDirectoryConfiguration.getDeploymentDir();
    
    public void deploy(HbCfDeploymentDTO dto, Principal principal, String platform) {
        
        String deploymentFileName = "";
        String messageEndpoint =  "/deploy/hbCfDeployment/install/logs";
        
        HbCfDeploymentVO vo = cfDeploymentService.getHbCfDeploymentInfo(dto.getId());
        
        String cfDeploymentType = vo.getHbCfDeploymentDefaultConfigVO().getCfDeploymentVersion().split("/")[0];
        String cfDeploymentVersion =  vo.getHbCfDeploymentDefaultConfigVO().getCfDeploymentVersion().split("/")[1];
        
        ManifestTemplateVO result = commonDao.selectManifetTemplate(vo.getIaasType(), cfDeploymentVersion, "CFDEPLOYMENT", cfDeploymentType);
        if(result == null){
            HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList("설치 가능한 CF Deployment 버전을 확인하세요."));
        }
        deploymentFileName = vo != null ? vo.getCloudConfigFile() : "";
        String status = "";
        try {
        if ( StringUtils.isEmpty(deploymentFileName) ) {
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), 
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        String cloudConfigFile = DEPLOYMENT_DIR + SEPARATOR + deploymentFileName; 
        
        cfDeploymentService.commonCreateCloudConfig(vo, result);
        
            BufferedReader bufferedReader = null;
            HbDirectorConfigVO directorInfo = directorConfigDao.selectHbDirectorConfigBySeq(Integer.parseInt(vo.getHbCfDeploymentResourceConfigVO().getDirectorInfo()));
            String httpStatus = directorConfigService.isExistBoshEnvLogin(directorInfo.getDirectorUrl(), directorInfo.getDirectorPort(), directorInfo.getUserId(), directorInfo.getUserPassword());
            if(!"200".equals(httpStatus)){
                HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList("디렉터 정보를 확인 하세요."));
                if(vo != null){
                    String deployStatus = message.getMessage("common.deploy.status.error", null, Locale.KOREA);
                    vo.setDeployStatus(deployStatus);
                    vo.setUpdateUserId(principal.getName());
                    saveDeployStatus(vo);
                }
            }
            // CF-Deployment 5.0.0 버전 이상 bosh runtime config required
            if("5.0.0".equals(cfDeploymentVersion) || "5.5.0".equals(cfDeploymentVersion) || "4.0".equals(cfDeploymentVersion)){
                status = settingRuntimeConfig(vo, directorInfo, principal, messageEndpoint, result);
            } else {
                deleteRuntimeConfig(vo, directorInfo, principal, messageEndpoint, result);
            }
            
            HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "started", Arrays.asList("Director Info Check Succeed...."));
            List<String> cmd = new ArrayList<String>(); //bosh cloud config 명령어 실행 줄 Cloud Config 관련 Rest API를 아직 지원 안하는 것 같음 2018.08.01
            cmd.add("bosh");
            cmd.add("-e");
            cmd.add(directorInfo.getDirectorName());
            cmd.add("update-cloud-config");
            cmd.add(cloudConfigFile);
            cmd.add("-n");
            ProcessBuilder builder = new ProcessBuilder(cmd);
            builder.redirectErrorStream(true);
            builder.start();
            if ( vo != null ) {
                String deployStatus = message.getMessage("common.deploy.status.processing", null, Locale.KOREA);
                vo.setDeployStatus(deployStatus);
                vo.setUpdateUserId(principal.getName());
                saveDeployStatus(vo);
            }
            //if 문을 통해 public IP 사용 유무/mysql/postgres 사용 유무에 따라 해당 커맨드 라인 변경
            cmd = new ArrayList<String>(); // bosh deploy 명령어 실행 줄 Rest API를 통해 deploy 시 -v/-o 옵션을 사용하지 못하고 통 Manifest 파일을 올려야 하는 것 같음 2018.08.01
            cmd.add("bosh");
            cmd.add("-e");
            cmd.add(directorInfo.getDirectorName());
            cmd.add("-d");
            cmd.add(vo.getHbCfDeploymentDefaultConfigVO().getDefaultConfigName());
            cmd.add("deploy");
            if(vo.getHbCfDeploymentNetworkConfigVO() != null){
                if(!vo.getHbCfDeploymentNetworkConfigVO().getSubnetId1().equals("")){
                    cmd.add(MANIFEST_TEMPLATE_DIR+"/cf-deployment/"+result.getTemplateVersion()+"/common/cf-deployment-multi-az.yml");
                }
            } else {
                cmd.add(MANIFEST_TEMPLATE_DIR+"/cf-deployment/"+result.getTemplateVersion()+"/common/"+result.getCommonBaseTemplate()+"");
            }
            setDefualtInfo(cmd, vo, result);
            setPublicNetworkIpUse(cmd, vo, result);
            if("postgres".equals(vo.getHbCfDeploymentDefaultConfigVO().getCfDbType())){
                postgresDbUse(cmd, result);
            }
            setJobSetting(cmd, vo, result);
            if("true".equalsIgnoreCase(vo.getHbCfDeploymentDefaultConfigVO().getPaastaMonitoringUse())){
                if(!"4.0".equalsIgnoreCase(cfDeploymentVersion) && !"paasta".equalsIgnoreCase(result.getReleaseType())){
                    HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList("PaaS-TA 모니터링은 paasta-4.0에서 사용 가능 합니다."));
                }
                settingPaasTaMonitoringInfo(vo, cmd, result);
            }
            cmd.add("--tty");
            cmd.add("-n");
            cmd.add("--no-redact");
            //cmd.add("--no-redact");
            builder = new ProcessBuilder(cmd);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            InputStream inputStream = process.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
            String info = null;
            StringBuffer accumulatedBuffer = new StringBuffer();
            while ((info = bufferedReader.readLine()) != null){
                accumulatedBuffer.append(info).append("\n");
                Thread.sleep(20);
                if(info.contains("invalid argument") || info.contains("error") || info.contains("fail") || info.contains("Error") || info.contains("Expected")){
                    status = "error";
                    HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList(info));
                }
                if(info.contains("Release")){
                    HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "started", Arrays.asList("Release Download Check:::"+info));
                }
                if(info.contains("cancelled")){
                    HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "cancelled", Arrays.asList("Cancel Task:::"+info));
                }
                
                if(info.contains("no such file or directory")){
                    status = "error";
                    HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList(info));
                }
                
                if(info.contains("Preparing deployment: Preparing deployment")){
                    String taskId = info.split(" ")[1];
                    HttpClient httpClient = HbDirectorRestHelper.getHttpClient(directorInfo.getDirectorPort());
                    status = HbDirectorRestHelper.trackToTask(directorInfo, messagingTemplate, messageEndpoint, httpClient, taskId, "event", principal.getName());
                }

            }
        }catch (RuntimeException e) {
            status = "error";
            HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList("CF-Deployment 설치 중 에러가 발생 했습니다.<br> 설정을 확인 해주세요."));
        }catch ( Exception e) {
            status = "error";
            HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList("CF-Deployment 설치 중 에러가 발생 했습니다.<br> 설정을 확인 해주세요."));
        }finally {
            //동시 설치 방지 lock 파일 삭제
            File lockFile = new File(KEY_DIR + "hybird_cfDeployment.lock");
            if(lockFile.exists()){
                lockFile.delete();
            }
        }
        
        if("".equals(status)){
            status = "error";
            HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList("Manifest 조합 중 에러가 발생했습니다.<br> 설정을 확인 해주세요."));
        }
        
        String deployStatus = message.getMessage("common.deploy.status."+status.toLowerCase(), null, Locale.KOREA);
        if ( vo != null ) {
            vo.setDeployStatus(deployStatus);
            vo.setUpdateUserId(principal.getName());
            saveDeployStatus(vo);
        }
    }
    
    /****************************************************************
     * @project : 이종 Paas 플랫폼 설치 자동화
     * @description : CF-Deploymnt 5.0.0/PaaS-TA 4.0 이상 BOSH Runtime Config 삭제 명령어 설정
     * @title : deleteRuntimeConfig
     * @return : void
    *****************************************************************/
    private void deleteRuntimeConfig(HbCfDeploymentVO vo, HbDirectorConfigVO directorInfo, Principal principal,
            String messageEndpoint, ManifestTemplateVO result) {
        String accumulatedLog= null;
        BufferedReader bufferedReader = null;
        try {
            List<String> cmd = new ArrayList<String>();
            cmd.add("bosh");
            cmd.add("-e");
            cmd.add(directorInfo.getDirectorName());
            cmd.add("delete-config");
            cmd.add("--type=runtime");
            cmd.add("--name=default");
            cmd.add("--tty");
            cmd.add("-n");
            ProcessBuilder builder = new ProcessBuilder(cmd);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            InputStream inputStream = process.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
            String info = null;
            StringBuffer accumulatedBuffer = new StringBuffer();
            while ((info = bufferedReader.readLine()) != null){
                accumulatedBuffer.append(info).append("\n");
            }
            if( accumulatedBuffer != null ) {
                accumulatedLog = accumulatedBuffer.toString();
            }
            if ( !accumulatedLog.contains("Succeeded") ) {
                String status = "error";
                vo.setDeployStatus(status);
                vo.setUpdateUserId(principal.getName());
                saveDeployStatus(vo);
                HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList("CF-Deployment 설치 중 에러가 발생 했습니다.<br> Runtime config를 확인 해주세요."));
            }
        } catch (IOException e) {
            HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList("CF-Deployment 설치 중 에러가 발생 했습니다.<br> Runtime config를 확인 해주세요."));
        }
        
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : BOSH Runtime Config 명령어 설정
     * @title : settingRuntimeConfig
     * @return : void
    *****************************************************************/
    private String settingRuntimeConfig(HbCfDeploymentVO vo, HbDirectorConfigVO directorInfo, Principal principal, String messageEndpoint, ManifestTemplateVO result) {
        String accumulatedLog= null;
        String status = "";
        BufferedReader bufferedReader = null;
        try {
            List<String> cmd = new ArrayList<String>();
            cmd.add("bosh");
            cmd.add("-e");
            cmd.add(directorInfo.getDirectorName());
            cmd.add("update-runtime-config");
            cmd.add(MANIFEST_TEMPLATE_DIR+"/cf-deployment/"+result.getTemplateVersion()+"/common/runtime-config-dns.yml");
            cmd.add("--vars-store");
            cmd.add(HYBRID_CF_CREDENTIAL_DIR+ SEPARATOR +vo.getHbCfDeploymentDefaultConfigVO().getDefaultConfigName()+"-runtime-cred.yml");
            cmd.add("-v");
            cmd.add("deployment_name="+vo.getHbCfDeploymentDefaultConfigVO().getDefaultConfigName()+"");
            cmd.add("--tty");
            cmd.add("-n");
            ProcessBuilder builder = new ProcessBuilder(cmd);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            InputStream inputStream = process.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
            String info = null;
            StringBuffer accumulatedBuffer = new StringBuffer();
            while ((info = bufferedReader.readLine()) != null){
                accumulatedBuffer.append(info).append("\n");
                if(info.contains("invalid argument") || info.contains("error") || info.contains("fail") || info.contains("Error") || info.contains("Expected")){
                    HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList(info));
                    status = "error";
                }
                
                if(info.contains("Downloading remote release")){
                    HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "started", Arrays.asList("Creating new packages:::"+info));
                }
                
                if(info.contains("Creating new packages")){
                    HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "started", Arrays.asList("Creating new packages:::"+info));
                }
                
                if(info.contains("Creating new jobs")){
                    HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "started", Arrays.asList("Creating new jobs:::"+info));
                }
                
                if(info.contains("Succeeded")){
                    HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "started", Arrays.asList("Bosh Runtime Config Succeeded:::"+info));
                }
            }
            if( accumulatedBuffer != null ) {
                accumulatedLog = accumulatedBuffer.toString();
            }
            if ( !accumulatedLog.contains("Succeeded") ) {
                status = "error";
                vo.setDeployStatus(status);
                vo.setUpdateUserId(principal.getName());
                saveDeployStatus(vo);
                HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList("CF-Deployment 설치 중 에러가 발생 했습니다.<br> Runtime config를 확인 해주세요."));
            }
        } catch (IOException e) {
            HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList("CF-Deployment 설치 중 에러가 발생 했습니다.<br> Runtime config를 확인 해주세요."));
        }
        return status;
    }
    
    public void saveDeployStatus(HbCfDeploymentVO vo) {
        cfDeploymentDao.updateCfDeploymentConfigInfo(vo);
    }

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CF 기본 정보 설정
     * @title : setDefualtInfo
     * @return : void
    *****************************************************************/
    public void setDefualtInfo(List<String> cmd, HbCfDeploymentVO vo, ManifestTemplateVO result) {
        cmd.add("--vars-store="+HYBRID_CF_CREDENTIAL_DIR+ SEPARATOR +vo.getHbCfDeploymentCredentialConfigVO().getCredentialConfigKeyFileName()+"");
        cmd.add("-v");
        cmd.add("deployment_name="+vo.getHbCfDeploymentDefaultConfigVO().getDefaultConfigName()+"");
        cmd.add("-v");
        cmd.add("system_domain="+vo.getHbCfDeploymentDefaultConfigVO().getDomain()+"");
        cmd.add("-v");
        cmd.add("system_domain_org="+vo.getHbCfDeploymentDefaultConfigVO().getDomainOrganization()+"");
        cmd.add("-v");
        cmd.add("stemcell_version="+vo.getHbCfDeploymentResourceConfigVO().getStemcellVersion()+"");
        cmd.add("-v");
        cmd.add("cf_admin_password="+vo.getHbCfDeploymentDefaultConfigVO().getCfAdminPassword()+"");
        if(!StringUtils.isEmpty(vo.getHbCfDeploymentDefaultConfigVO().getPortalDomain()) && vo.getHbCfDeploymentDefaultConfigVO().getPortalDomain() != null){
            cmd.add("-v");
            cmd.add("portal_domain="+vo.getHbCfDeploymentDefaultConfigVO().getPortalDomain()+"");
        }else{
            cmd.add("-v");
            cmd.add("portal_domain="+vo.getHbCfDeploymentDefaultConfigVO().getDomain()+"");
        }

        if(result.getReleaseType().equals("paasta")){
            cmd.add("-o");
            cmd.add(MANIFEST_TEMPLATE_DIR+"/cf-deployment/"+result.getTemplateVersion()+"/common/use-haproxy-compiled-release.yml");
            cmd.add("-v");
            cmd.add("inception_os_user_name="+vo.getHbCfDeploymentDefaultConfigVO().getInceptionOsUserName()+"");
            cmd.add("-o");
            cmd.add(MANIFEST_TEMPLATE_DIR+"/cf-deployment/"+result.getTemplateVersion()+"/common/"+result.getInputTemplate());
        } else {
            cmd.add("-o");
            cmd.add(MANIFEST_TEMPLATE_DIR+"/cf-deployment/"+result.getTemplateVersion()+"/common/"+result.getCommonJobTemplate());
            cmd.add("-o");
            cmd.add(MANIFEST_TEMPLATE_DIR+"/cf-deployment/"+result.getTemplateVersion()+"/common/"+result.getMetaTemplate());
        
        }
    }
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CF Deployment Job 인스턴스 수 설정
     * @title : setPublicNetworkIpUse
     * @return : void
    *****************************************************************/
    public void setJobSetting(List<String> cmd, HbCfDeploymentVO vo, ManifestTemplateVO result) {
        cmd.add("-v");
        cmd.add("adapter_instance="+vo.getHbCfDeploymentInstanceConfigVO().getAdapter()+"");
        cmd.add("-v");
        cmd.add("api_instance="+vo.getHbCfDeploymentInstanceConfigVO().getApi()+"");
        cmd.add("-v");
        cmd.add("cc-worker_instance="+vo.getHbCfDeploymentInstanceConfigVO().getCcWorker()+"");
        cmd.add("-v");
        cmd.add("consul_instance="+vo.getHbCfDeploymentInstanceConfigVO().getConsul()+"");
        cmd.add("-v");
        cmd.add("diego-api_instance="+vo.getHbCfDeploymentInstanceConfigVO().getDiegoApi()+"");
        cmd.add("-v");
        cmd.add("database_instance="+vo.getHbCfDeploymentInstanceConfigVO().getTheDatabase()+"");
        cmd.add("-v");
        cmd.add("diego-cell_instance="+vo.getHbCfDeploymentInstanceConfigVO().getDiegoCell()+"");
        cmd.add("-v");
        cmd.add("doppler_instance="+vo.getHbCfDeploymentInstanceConfigVO().getDoppler()+"");
        cmd.add("-v");
        cmd.add("haproxy_instance="+vo.getHbCfDeploymentInstanceConfigVO().getHaproxy()+"");
        cmd.add("-v");
        cmd.add("log-api_instance="+vo.getHbCfDeploymentInstanceConfigVO().getLogApi()+"");
        cmd.add("-v");
        cmd.add("nats_instance="+vo.getHbCfDeploymentInstanceConfigVO().getNats()+"");
        cmd.add("-v");
        cmd.add("router_instance="+vo.getHbCfDeploymentInstanceConfigVO().getRouter()+"");
        cmd.add("-v");
        cmd.add("singleton-blobstore_instance="+vo.getHbCfDeploymentInstanceConfigVO().getSingletonBlobstore()+"");
        cmd.add("-v");
        cmd.add("tcp-router_instance="+vo.getHbCfDeploymentInstanceConfigVO().getTcpRouter()+"");
        cmd.add("-v");
        cmd.add("uaa_instance="+vo.getHbCfDeploymentInstanceConfigVO().getUaa()+"");
        cmd.add("-v");
        cmd.add("scheduler_instance="+vo.getHbCfDeploymentInstanceConfigVO().getScheduler()+"");
        
        cmd.add("-o");
        cmd.add(MANIFEST_TEMPLATE_DIR+"/cf-deployment/"+result.getTemplateVersion()+"/common/"+result.getOptionResourceTemplate());
    }
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Public Network IP 사용 시 값
     * @title : setPublicNetworkIpUse
     * @return : void
    *****************************************************************/
    public void setPublicNetworkIpUse(List<String> cmd, HbCfDeploymentVO vo, ManifestTemplateVO result) {
        if(vo.getHbCfDeploymentNetworkConfigVO() != null){
            if(( vo.getHbCfDeploymentNetworkConfigVO().getPublicStaticIp() != null && !vo.getHbCfDeploymentNetworkConfigVO().getPublicStaticIp().isEmpty())){
                cmd.add("-v");
                cmd.add("haproxy_public_ip="+vo.getHbCfDeploymentNetworkConfigVO().getPublicStaticIp()+"");
                cmd.add("-o");
                cmd.add(MANIFEST_TEMPLATE_DIR+"/cf-deployment/"+result.getTemplateVersion()+"/common/"+result.getCommonOptionTemplate());
            }
//            if(vo.getHbCfDeploymentNetworkConfigVO().getSubnetId2() != null && !"".equals(vo.getHbCfDeploymentNetworkConfigVO().getSubnetId2())){
//                cmd.add("-o");
//                cmd.add(MANIFEST_TEMPLATE_DIR+"/cf-deployment/"+result.getTemplateVersion()+"/common/"+result.getOptionNetworkTemplate());
//            }
        }
    }
    
    /****************************************************************
     * @project : 이종 Paas 플랫폼 설치 자동화
     * @description : CF-Deploymnt PaaS-TA 4.0 이상 PaaS-TA 모니터링 사용
     * @title : settingPaasTaMonitoringInfo
     * @return : void
    *****************************************************************/
    private void settingPaasTaMonitoringInfo(HbCfDeploymentVO vo, List<String> cmd, ManifestTemplateVO result) {
        cmd.add("-o");
        cmd.add(MANIFEST_TEMPLATE_DIR+"/cf-deployment/"+result.getTemplateVersion()+"/common/enable-component-syslog.yml");
        cmd.add("-o");
        cmd.add(MANIFEST_TEMPLATE_DIR+"/cf-deployment/"+result.getTemplateVersion()+"/common/paasta-monitoring.yml");
        cmd.add("-v");
        cmd.add("metric_url="+vo.getHbCfDeploymentDefaultConfigVO().getMetricUrl()+"");
        cmd.add("-v");
        cmd.add("syslog_address="+vo.getHbCfDeploymentDefaultConfigVO().getSyslogAddress()+"");
        cmd.add("-v");
        cmd.add("syslog_port="+vo.getHbCfDeploymentDefaultConfigVO().getSyslogPort()+"");
        cmd.add("-v");
        cmd.add("syslog_custom_rule="+vo.getHbCfDeploymentDefaultConfigVO().getSyslogCustomRule()+"");
        cmd.add("-v");
        cmd.add("syslog_fallback_servers="+vo.getHbCfDeploymentDefaultConfigVO().getSyslogFallbackServers()+"");
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : postgres DB 사용 시 옵션 값 추가
     * @title : postgresDbUse
     * @return : void
    *****************************************************************/
    public void postgresDbUse(List<String> cmd, ManifestTemplateVO result) {
        if(result.getReleaseType().equals("paasta")){
            cmd.add("-o");
            cmd.add(MANIFEST_TEMPLATE_DIR+"/cf-deployment/"+result.getTemplateVersion()+"/common/"+result.getInputTemplateThird());
        } else {
            cmd.add("-o");
            cmd.add(MANIFEST_TEMPLATE_DIR+"/cf-deployment/"+result.getTemplateVersion()+"/common/"+result.getOptionEtc());
        }
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 비동기 호출
     * @title : deployAsync
     * @return : void
    *****************************************************************/
    @Async
    public void deployAsync(HbCfDeploymentDTO dto, Principal principal, String platform) {
        deploy(dto, principal, platform);
    }
    
}
