package org.openpaas.ieda.deploy.web.deploy.cf.service;

import java.io.BufferedReader;
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
import org.openpaas.ieda.deploy.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.deploy.web.common.dao.CommonDeployDAO;
import org.openpaas.ieda.deploy.web.common.dao.ManifestTemplateVO;
import org.openpaas.ieda.deploy.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.deploy.web.config.setting.service.DirectorConfigService;
import org.openpaas.ieda.deploy.web.deploy.cf.dao.CfDAO;
import org.openpaas.ieda.deploy.web.deploy.cf.dao.CfVO;
import org.openpaas.ieda.deploy.web.deploy.cf.dto.CfParamDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CfDeployAsyncService {
    
    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private DirectorConfigService directorConfigService;
    @Autowired private CfDAO cfDao;
    @Autowired private CfService cfService;
    @Autowired private MessageSource message;
    @Autowired private CommonDeployDAO commonDao;
    
    private final static String SEPARATOR = System.getProperty("file.separator");
    private final static String MANIFEST_TEMPLATE_DIR = LocalDirectoryConfiguration.getManifastTemplateDir();
    final private static String CF_CREDENTIAL_DIR = LocalDirectoryConfiguration.getGenerateCfDeploymentCredentialDir();
    private final static String DEPLOYMENT_DIR = LocalDirectoryConfiguration.getDeploymentDir();
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 플랫폼 설치 요청
     * @title : deploy
     * @return : void
    *****************************************************************/
    public void deploy(CfParamDTO.Install dto, Principal principal, String platform) {
        
        String deploymentFileName = "";
        String messageEndpoint =  "/deploy/cf/install/logs"; 
        CfVO vo = cfService.getCfInfo(Integer.parseInt(dto.getId()));
        ManifestTemplateVO result = commonDao.selectManifetTemplate(vo.getIaasType(), vo.getReleaseVersion(), "CFDEPLOYMENT", vo.getReleaseName());
        deploymentFileName = vo != null ? vo.getDeploymentFile() : "";
        
        if ( StringUtils.isEmpty(deploymentFileName) ) {
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), 
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        String cloudConfigFile = DEPLOYMENT_DIR + SEPARATOR + deploymentFileName; 
        String errorMessage = message.getMessage("common.internalServerError.message", null, Locale.KOREA);
        String status = "";

        try {
            BufferedReader bufferedReader = null;
            DirectorConfigVO directorInfo = directorConfigService.getDefaultDirector();
            
            if("5.0.0".equals(vo.getReleaseVersion()) || "5.5.0".equals(vo.getReleaseVersion()) || "4.0".equals(vo.getReleaseVersion())){
                status = settingRuntimeConfig(vo, directorInfo, principal, messageEndpoint, result);
            } else {
                deleteRuntimeConfig(vo, directorInfo, principal, messageEndpoint, result);
            }
            
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
            cmd.add(vo.getDeploymentName());
            cmd.add("deploy");
            if(vo.getNetworks() != null && vo.getNetworks().size() ==3){
                if(vo.getNetworks().size() == 3){
                    cmd.add(MANIFEST_TEMPLATE_DIR+"/cf-deployment/"+result.getTemplateVersion()+"/common/cf-deployment-multi-az.yml");
                }
            } else {
                cmd.add(MANIFEST_TEMPLATE_DIR+"/cf-deployment/"+result.getTemplateVersion()+"/common/"+result.getCommonBaseTemplate()+"");
            }
            setDefualtInfo(cmd, vo, result);
            
            if("true".equalsIgnoreCase(vo.getPaastaMonitoringUse())){
                if(!"4.0".equalsIgnoreCase(vo.getReleaseVersion()) && !"paasta".equalsIgnoreCase(result.getReleaseType())){
                    DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList("PaaS-TA 모니터링은 paasta-4.0에서 사용 가능 합니다."));
                }
                settingPaasTaMonitoringInfo(vo, cmd, result);
            }
            
            setPublicNetworkIpUse(cmd, vo, result);
            if("postgres".equals(vo.getCfDbType().toLowerCase())){
                postgresDbUse(cmd, result);
            }
            setJobSetting(cmd, vo, result);
            if("azure".equals(vo.getIaasType().toLowerCase())){
                if(!(vo.getResource().getWindowsStemcellVersion().isEmpty())) {
                    setWindowsCellUse(cmd, vo, result);
                }
            }
            cmd.add("--tty");
            cmd.add("-n");
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
                    DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList(info));
                }
                
                if(info.contains("Release")){
                    DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "started", Arrays.asList("Release Download Check:::"+info));
                }
                
                if(info.contains("cancelled")){
                    DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "cancelled", Arrays.asList("Cancel Task:::"+info));
                }
                
                if(info.contains("no such file or directory")){
                    status = "error";
                    DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList(info));
                }
                
                if(info.contains("Preparing deployment: Preparing deployment")){
                    String taskId = info.split(" ")[1];
                    HttpClient httpClient = DirectorRestHelper.getHttpClient(directorInfo.getDirectorPort());
                    status = DirectorRestHelper.trackToTask(directorInfo, messagingTemplate, messageEndpoint, httpClient, taskId, "event", principal.getName());
                }
            }
        }catch (RuntimeException e) {
            status = "error";
            DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList("CF-Deployment 설치 중 에러가 발생 했습니다.<br> 설정을 확인 해주세요."));
        }catch ( Exception e) {
            status = "error";
            DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList(errorMessage));
        } 
        if("".equals(status)){
            status = "error";
            DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList("Manifest 조합 중 에러가 발생했습니다.<br> 설정을 확인 해주세요."));
        }
        String deployStatus = message.getMessage("common.deploy.status."+status.toLowerCase(), null, Locale.KOREA);
        if ( vo != null ) {
            vo.setDeployStatus(deployStatus);
            vo.setUpdateUserId(principal.getName());
            saveDeployStatus(vo);
        }
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF-Deploymnt 5.0.0/PaaS-TA 4.0 이상 BOSH Runtime Config 삭제 명령어 설정
     * @title : settingPaasTaMonitoringInfo
     * @return : void
    *****************************************************************/
    private void settingPaasTaMonitoringInfo(CfVO vo, List<String> cmd, ManifestTemplateVO result) {
        cmd.add("-o");
        cmd.add(MANIFEST_TEMPLATE_DIR+"/cf-deployment/"+result.getTemplateVersion()+"/common/enable-component-syslog.yml");
        cmd.add("-o");
        cmd.add(MANIFEST_TEMPLATE_DIR+"/cf-deployment/"+result.getTemplateVersion()+"/common/paasta-monitoring.yml");
        cmd.add("-v");
        cmd.add("metric_url="+vo.getMetricUrl()+"");
        cmd.add("-v");
        cmd.add("syslog_address="+vo.getSyslogAddress()+"");
        cmd.add("-v");
        cmd.add("syslog_port="+vo.getSyslogPort()+"");
        cmd.add("-v");
        cmd.add("syslog_custom_rule="+vo.getSyslogCustomRule()+"");
        cmd.add("-v");
        cmd.add("syslog_fallback_servers="+vo.getSyslogFallbackServers()+"");
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF-Deploymnt 5.0.0/PaaS-TA 4.0 이상 BOSH Runtime Config 삭제 명령어 설정
     * @title : deleteRuntimeConfig
     * @return : void
    *****************************************************************/
    private void deleteRuntimeConfig(CfVO vo, DirectorConfigVO directorInfo, Principal principal, String messageEndpoint, ManifestTemplateVO result) {
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
                DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "started", Arrays.asList(info));
            }
            if( accumulatedBuffer != null ) {
                accumulatedLog = accumulatedBuffer.toString();
            }
            if ( !accumulatedLog.contains("Succeeded") ) {
                String status = "error";
                vo.setDeployStatus(status);
                vo.setUpdateUserId(principal.getName());
                saveDeployStatus(vo);
                DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList("CF-Deployment 설치 중 에러가 발생 했습니다.<br> Runtime config를 확인 해주세요."));
            }
        } catch (IOException e) {
            DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList("CF-Deployment 설치 중 에러가 발생 했습니다.<br> Runtime config를 확인 해주세요."));
        }
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF-Deploymnt 5.0.0/PaaS-TA 4.0 이상 BOSH Runtime Config Update 명령어 설정
     * @title : settingRuntimeConfig
     * @return : void
    *****************************************************************/
    private String settingRuntimeConfig(CfVO vo, DirectorConfigVO directorInfo, Principal principal, String messageEndpoint, ManifestTemplateVO result) {
        String accumulatedLog= null;
        BufferedReader bufferedReader = null;
        String status = "";
        try {
            List<String> cmd = new ArrayList<String>();
            cmd.add("bosh");
            cmd.add("-e");
            cmd.add(directorInfo.getDirectorName());
            cmd.add("update-runtime-config");
            cmd.add(MANIFEST_TEMPLATE_DIR+"/cf-deployment/"+result.getTemplateVersion()+"/common/runtime-config-dns.yml");
            cmd.add("--vars-store");
            cmd.add(CF_CREDENTIAL_DIR+ SEPARATOR +vo.getDeploymentName()+"-runtime-cred.yml");
            cmd.add("-v");
            cmd.add("deployment_name="+vo.getDeploymentName()+"");
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
                    status = "error";
                    DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList(info));
                }
                
                if(info.contains("Downloading remote release")){
                    DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "started", Arrays.asList("Creating new packages:::"+info));
                }
                
                if(info.contains("Creating new packages")){
                    DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "started", Arrays.asList("Creating new packages:::"+info));
                }
                
                if(info.contains("Creating new jobs")){
                    DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "started", Arrays.asList("Creating new jobs:::"+info));
                }
                if(info.contains("Succeeded")){
                    DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "started", Arrays.asList("Bosh Runtime Config Succeeded:::"+info));
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
                DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList("CF-Deployment 설치 중 에러가 발생 했습니다.<br> Runtime config를 확인 해주세요."));
            }
        } catch (IOException e) {
            DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, messageEndpoint, "error", Arrays.asList("CF-Deployment 설치 중 에러가 발생 했습니다.<br> Runtime config를 확인 해주세요."));
        }
        return status;
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 기본 정보 설정
     * @title : setDefualtInfo
     * @return : void
    *****************************************************************/
    public void setDefualtInfo(List<String> cmd, CfVO vo, ManifestTemplateVO result) {
        cmd.add("--vars-store="+CF_CREDENTIAL_DIR+ SEPARATOR +vo.getKeyFile()+"");
        cmd.add("-v");
        cmd.add("deployment_name="+vo.getDeploymentName()+"");
        cmd.add("-v");
        cmd.add("system_domain="+vo.getDomain()+"");
        cmd.add("-v");
        cmd.add("system_domain_org="+vo.getDomainOrganization()+"");
        cmd.add("-v");
        cmd.add("stemcell_version="+vo.getResource().getStemcellVersion()+"");
        cmd.add("-v");
        cmd.add("cf_admin_password="+vo.getCfAdminPassword()+"");
        if(!StringUtils.isEmpty(vo.getPortalDomain()) && vo.getPortalDomain() != null){
            cmd.add("-v");
            cmd.add("portal_domain="+vo.getPortalDomain()+"");
        }else{
            cmd.add("-v");
            cmd.add("portal_domain="+vo.getDomain()+"");
        }
        if(result.getReleaseType().equals("paasta")){
            cmd.add("-o");
            cmd.add(MANIFEST_TEMPLATE_DIR+"/cf-deployment/"+result.getTemplateVersion()+"/common/use-haproxy-compiled-release.yml");
            cmd.add("-v");
            cmd.add("inception_os_user_name="+vo.getInceptionOsUserName()+"");
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
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF Job 인스턴스 수 설정
     * @title : setJobSetting
     * @return : void
    *****************************************************************/
    public void setJobSetting(List<String> cmd, CfVO vo, ManifestTemplateVO result) {
        if (vo.getJobs()!=null && vo.getJobs().size()!=0 ){
            for(int i=0; i<vo.getJobs().size(); i++){
                cmd.add("-v");
                cmd.add(vo.getJobs().get(i).get("job_name")+"_instance="+String.valueOf(vo.getJobs().get(i).get("instances"))+"");
            }
            cmd.add("-o");
            cmd.add(MANIFEST_TEMPLATE_DIR+"/cf-deployment/"+result.getTemplateVersion()+"/common/"+result.getOptionResourceTemplate());
        }
    }
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Public Network IP 사용 시 값
     * @title : setPublicNetworkIpUse
     * @return : void
    *****************************************************************/
    public void setPublicNetworkIpUse(List<String> cmd, CfVO vo, ManifestTemplateVO result) {
        if(vo.getNetworks() != null && vo.getNetworks().size() != 0){
            for( int i=0; i<vo.getNetworks().size(); i++ ){
                if("external".equals(vo.getNetworks().get(i).getNet().toLowerCase()) 
                    && ( vo.getNetworks().get(i).getPublicStaticIp() != null && !vo.getNetworks().get(i).getPublicStaticIp().isEmpty())){
                    cmd.add("-v");
                    cmd.add("haproxy_public_ip="+vo.getNetworks().get(i).getPublicStaticIp()+"");
                    cmd.add("-o");
                    cmd.add(MANIFEST_TEMPLATE_DIR+"/cf-deployment/"+result.getTemplateVersion()+"/common/"+result.getCommonOptionTemplate());
                }
            }
        }
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  Windows Cell 사용시 설정 값
     * @title : setWindowsCellUse
     * @return : void
    *****************************************************************/
    public void setWindowsCellUse(List<String> cmd, CfVO vo, ManifestTemplateVO result) {
        cmd.add("-v");
        cmd.add("windows_stemcell_version="+"\""+vo.getResource().getWindowsStemcellVersion()+"\""+"");
        cmd.add("-v");
        cmd.add("windows_cell_instance="+vo.getResource().getWindowsCellInstance()+"");
        cmd.add("-o");
        cmd.add(MANIFEST_TEMPLATE_DIR+"/cf-deployment/"+result.getTemplateVersion()+"/common/"+result.getInputTemplateSecond());
    }
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : postgres DB 사용 시 옵션 값 추가
     * @title : postgresDbUse
     * @return : CfVO
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
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 설치 상태 저장
     * @title : saveDeployStatus
     * @return : CfVO
    *****************************************************************/
    public CfVO saveDeployStatus(CfVO cfVo) {
        cfDao.updateCfInfo(cfVo);
        return cfVo;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 비동기로 deploy 메소드 호출
     * @title : deployAsync
     * @return : void
    *****************************************************************/
    @Async
    public void deployAsync(CfParamDTO.Install dto, Principal principal, String platform) {
        deploy(dto, principal, platform);
    }

}
