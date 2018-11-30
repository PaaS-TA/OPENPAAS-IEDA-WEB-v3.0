package org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.service;

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
import org.openpaas.ieda.deploy.web.common.dao.CommonDeployDAO;
import org.openpaas.ieda.deploy.web.common.dao.ManifestTemplateVO;
import org.openpaas.ieda.hbdeploy.api.director.dto.DirectorInfoDTO;
import org.openpaas.ieda.hbdeploy.api.director.utility.HbDirectorRestHelper;
import org.openpaas.ieda.hbdeploy.web.config.setting.service.HbDirectorConfigService;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapVO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto.HbBootStrapDeployDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class HbBootstrapDeployAsyncService {

    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private HbDirectorConfigService directorConfigService;
    @Autowired private HbBootstrapDAO bootstrapDao;
    @Autowired private MessageSource message;
    @Autowired private CommonDeployDAO commonDeployDao;
    

    final private static String SEPARATOR = System.getProperty("file.separator");
    final private static String MANIFEST_TEMPLATE_PATH = LocalDirectoryConfiguration.getManifastTemplateDir() + SEPARATOR +"bootstrap";
    final private static String PRIVATE_KEY_PATH = LocalDirectoryConfiguration.getSshDir()+SEPARATOR;
    final private static String KEY_DIR = LocalDirectoryConfiguration.getLockDir()+SEPARATOR;
    final private static String HYBRID_CREDENTIAL_DIR = LocalDirectoryConfiguration.getGenerateHybridCredentialDir() + SEPARATOR;
    final private static String MESSAGE_ENDPOINT = "/deploy/hbBootstrap/install/logs";
    final private static String DEPLOYMENT_DIR = LocalDirectoryConfiguration.getDeploymentDir() + SEPARATOR;
    final private static String RELEASE_DIR = LocalDirectoryConfiguration.getReleaseDir();
    final private static String STEMCELL_DIR = LocalDirectoryConfiguration.getStemcellDir();
    private final static Logger LOGGER = LoggerFactory.getLogger(HbBootstrapDeployAsyncService.class);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : bosh를 실행하여 Bootstrap 설치 실행
     * @title : deployBootstrap
     * @return : void
    *****************************************************************/
    public void deployBootstrap(HbBootStrapDeployDTO dto, Principal principal) {
        
        String status = "";
        String accumulatedLog= null;
        BufferedReader bufferedReader = null;
        HbBootstrapVO bootstrapInfo = null;
        try {
            bootstrapInfo = bootstrapDao.selectBootstrapConfigInfo(Integer.parseInt(dto.getId()), dto.getIaasType().toLowerCase());
            
            String boshRelease = bootstrapInfo.getDefaultConfigVo().getBoshRelease();
            if(  bootstrapInfo.getDefaultConfigVo().getBoshRelease().contains(".tgz") ){
                boshRelease= bootstrapInfo.getDefaultConfigVo().getBoshRelease().replace(".tgz", ""); 
            }
            String releaseVersion = boshRelease.replaceAll("[^0-9]", "");
            String releaseName = boshRelease.replaceAll("[^A-Za-z]", "");
            
            //해당 Bosh 릴리즈 버전의 Manifest Template 파일 조회
            ManifestTemplateVO result = commonDeployDao.selectManifetTemplate(bootstrapInfo.getIaasType(), releaseVersion, "BOOTSTRAP", releaseName );
            String deployFile = MANIFEST_TEMPLATE_PATH + SEPARATOR + result.getTemplateVersion()  + SEPARATOR  + "common" + SEPARATOR  + result.getCommonBaseTemplate();
            File deploymentFile = new File(deployFile);
            if( deploymentFile.exists() ) {
                  //1. 배포상태 설정
                bootstrapInfo.setUpdateUserId( principal.getName());
                String deployStatus = message.getMessage("common.deploy.status.processing", null, Locale.KOREA);
                bootstrapInfo.setDeployStatus( deployStatus );
                saveDeployStatus(bootstrapInfo, principal);
                List<String> cmd = new ArrayList<String>();
                cmd.add("bosh");
                cmd.add("create-env");
                cmd.add(deployFile);
                cmd.add("--state=" + DEPLOYMENT_DIR + bootstrapInfo.getDeploymentFile().replace(".yml", "")+"-state.json");
                cmd.add("--vars-store=" + HYBRID_CREDENTIAL_DIR + bootstrapInfo.getDefaultConfigVo().getCredentialKeyName()+"");
                
                settingBoshInfo(bootstrapInfo, cmd);
                settingIaasCpiInfo(bootstrapInfo, cmd, result);
                settingJumpBoxInfo(bootstrapInfo, cmd, result);
                
                if("true".equalsIgnoreCase(bootstrapInfo.getDefaultConfigVo().getPaastaMonitoringUse())){
                    if(!"268.2".equalsIgnoreCase(result.getTemplateVersion())){
                        HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("PaaS-TA 모니터링은 paasta-4.0(bosh-release v268.2)에서 사용 가능 합니다."));
                    }
                    settingPaasTaMonitoringInfo(bootstrapInfo, cmd, result);
                }
                
                if(!StringUtils.isEmpty(bootstrapInfo.getNetworkConfigVo().getPublicStaticIp()) && bootstrapInfo.getNetworkConfigVo().getPublicStaticIp() != null){
                    settingPublicIpInfo(bootstrapInfo, cmd, result);
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
                    HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "started", Arrays.asList(info));
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
                saveDeployStatus(bootstrapInfo, principal);
                HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList( "설치할 배포 파일(" + deployFile + ")이 존재하지 않습니다."));
            } else {
                if ( !accumulatedLog.contains("Succeeded")) {
                    status = "error";
                    bootstrapInfo.setDeployStatus(message.getMessage("common.deploy.status.failed", null, Locale.KOREA) );
                    saveDeployStatus(bootstrapInfo, principal);
                    HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("", "BOOTSTRAP 설치 중 오류가 발생하였습니다.<br> 배포 정보 및 로그를 확인 하세요."));
                }    else {
                    // 타겟 테스트
                    String bootstrapIp = "";
                    if(!StringUtils.isEmpty(bootstrapInfo.getNetworkConfigVo().getPublicStaticIp()) && bootstrapInfo.getNetworkConfigVo().getPublicStaticIp() != null){
                        bootstrapIp = bootstrapInfo.getNetworkConfigVo().getPublicStaticIp();
                    } else {
                        bootstrapIp = bootstrapInfo.getNetworkConfigVo().getPrivateStaticIp();
                    }
                    HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "started", Arrays.asList("","BOOTSTRAP 디렉터 정보 : https://" + bootstrapIp + ":25555"));
                    HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "started", Arrays.asList("BOOTSTRAP 디렉터 타겟 접속 테스트..."));
                    DirectorInfoDTO directorInfo = directorConfigService.getDirectorInfo(bootstrapIp, 25555, "admin", "admin");
                    
                    if ( directorInfo == null ) {
                        status = "error";
                        bootstrapInfo.setDeployStatus(message.getMessage("common.deploy.status.failed", null, Locale.KOREA));
                        saveDeployStatus(bootstrapInfo, principal);
                        HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("BOOTSTRAP 디렉터 타겟 접속 테스트 실패 <br> 인프라 정보를 확인 하세요."));
                    } else {
                        HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "started", Arrays.asList("BOOTSTRAP 디렉터 타겟 접속 테스트 성공"));
                        status = "done";
                        bootstrapInfo.setDeployStatus( message.getMessage("common.deploy.status.done", null,  Locale.KOREA ) );
                        saveDeployStatus(bootstrapInfo, principal);
                        HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "done", Arrays.asList("", "BOOTSTRAP 설치가 완료되었습니다."));
                    }
                }
            }
        }catch(RuntimeException e){
            e.printStackTrace();
            status = "error";
            HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("배포 중 Exception이 발생하였습니다."));
            if ( bootstrapInfo != null ) {
                bootstrapInfo.setDeployLog(accumulatedLog);
            }
            String deployStatus = message.getMessage("common.deploy.status.failed", null, Locale.KOREA);
            if( deployStatus != null ) {
                bootstrapInfo.setDeployStatus( deployStatus );
            }
            saveDeployStatus(bootstrapInfo, principal);
        }catch ( Exception e) {
            status = "error";
            e.printStackTrace();
            HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("배포 중 Exception이 발생하였습니다."));
            if ( bootstrapInfo != null ) {
                bootstrapInfo.setDeployLog(accumulatedLog);
            }
            String deployStatus =  message.getMessage("common.deploy.status.failed", null, Locale.KOREA) ;
            if( deployStatus != null ) {
                bootstrapInfo.setDeployStatus(message.getMessage("common.deploy.status.failed", null, Locale.KOREA) );
            }
            saveDeployStatus(bootstrapInfo, principal);
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
            File lockFile = new File(KEY_DIR + "hybird_bootstrap.lock");
            if(lockFile.exists()){
                Boolean check = lockFile.delete();
                if( LOGGER.isDebugEnabled() ) {
                    LOGGER.debug("check delete lock File  : "  + check); 
                }
            }
        }
    }
    
    private void settingPaasTaMonitoringInfo(HbBootstrapVO vo, List<String> cmd, ManifestTemplateVO result) {
        cmd.add("-o");
        cmd.add(MANIFEST_TEMPLATE_PATH + SEPARATOR + result.getMinReleaseVersion() + SEPARATOR + "common/" + "paasta-monitoring-agent.yml");
        cmd.add("-o");
        cmd.add(MANIFEST_TEMPLATE_PATH + SEPARATOR + result.getMinReleaseVersion() + SEPARATOR + "common/" + "syslog.yml");
        cmd.add("-v");
        cmd.add("paastaMoniteringRelease="+ RELEASE_DIR + SEPARATOR + vo.getDefaultConfigVo().getPaastaMonitoringRelease()+ "");
        cmd.add("-v");
        cmd.add("syslogRelease="+ RELEASE_DIR + SEPARATOR + vo.getDefaultConfigVo().getSyslogRelease()+ "");
        cmd.add("-v");
        cmd.add("metric_url="+ vo.getDefaultConfigVo().getMetricUrl()+ "");
        cmd.add("-v");
        cmd.add("syslog_address="+ vo.getDefaultConfigVo().getSyslogAddress()+ "");
        cmd.add("-v");
        cmd.add("syslog_port="+ vo.getDefaultConfigVo().getSyslogPort()+ "");
        cmd.add("-v");
        cmd.add("syslog_transport="+ vo.getDefaultConfigVo().getSyslogTransport()+ "");
    }

    /****************************************************************
     * @project : 이종 Paas 플랫폼 설치 자동화
     * @description : BOSH Release 관련 CMD 정의
     * @title : settingBoshInfo
     * @return : void
    ***************void*************************************************/
    private void settingBoshInfo(HbBootstrapVO vo, List<String> cmd) {
        cmd.add("-v");
        cmd.add("boshRelease="+ RELEASE_DIR + SEPARATOR + vo.getDefaultConfigVo().getBoshRelease()+ "");
        cmd.add("-v");
        cmd.add("bpmRelease="+ RELEASE_DIR + SEPARATOR + vo.getDefaultConfigVo().getBoshBpmRelease()+ "");
        cmd.add("-v");
        cmd.add("internal_cidr="+ vo.getNetworkConfigVo().getSubnetRange()+ "");
        cmd.add("-v");
        cmd.add("internal_gw="+ vo.getNetworkConfigVo().getSubnetGateway()+ "");
        cmd.add("-v");
        cmd.add("internal_dns="+ vo.getNetworkConfigVo().getSubnetDns()+ "");
        cmd.add("-v");
        cmd.add("ntp="+ vo.getDefaultConfigVo().getNtp()+ "");
        cmd.add("-v");
        cmd.add("director_name="+ vo.getDefaultConfigVo().getDirectorName()+ "");
    }

    /****************************************************************
     * @project : 이종 Paas 플랫폼 설치 자동화
     * @description : Public IP 사용 CMD 정의
     * @title : settingPublicIpInfo
     * @return : void
    ***************void*************************************************/
    private void settingPublicIpInfo(HbBootstrapVO vo, List<String> cmd, ManifestTemplateVO result) {
        cmd.add("-o");
        cmd.add(MANIFEST_TEMPLATE_PATH + SEPARATOR + result.getMinReleaseVersion() + SEPARATOR + "common/" + result.getInputTemplateSecond());
        cmd.add("-v");
        cmd.add("external_ip="+ vo.getNetworkConfigVo().getPublicStaticIp() + "");
    }

    /****************************************************************
     * @project : 이종 Paas 플랫폼 설치 자동화
     * @description : JumpBox CMD 정의
     * @title : settingJumpBoxInfo
     * @return : void
    ***************void*************************************************/
    private void settingJumpBoxInfo(HbBootstrapVO vo, List<String> cmd, ManifestTemplateVO result) {
        cmd.add("-o");
        cmd.add(MANIFEST_TEMPLATE_PATH + SEPARATOR + result.getMinReleaseVersion() + SEPARATOR + "common/" + result.getMetaTemplate());
        cmd.add("-v");
        cmd.add("osRelease="+ RELEASE_DIR + SEPARATOR + vo.getDefaultConfigVo().getOsConfRelease()+ "");
    }

    /****************************************************************
     * @project : 이종 Paas 플랫폼 설치 자동화
     * @description : CredHub CMD 정의
     * @title : settingCredhubInfo
     * @return : void
    *****************************************************************/
/*    private void settingCredhubInfo(HbBootstrapVO vo, List<String> cmd, ManifestTemplateVO result) {
        cmd.add("-o");
        cmd.add(MANIFEST_TEMPLATE_PATH + SEPARATOR + result.getMinReleaseVersion() + SEPARATOR + "common/" + result.getOptionEtc());
        cmd.add("-v");
        cmd.add("credhubRelease="+ RELEASE_DIR + SEPARATOR + vo.getDefaultConfigVo().getCredhubRelease()+ "");
    }*/

    /****************************************************************
     * @project : 이종 Paas 플랫폼 설치 자동화
     * @description : UAA CMD 정의
     * @title : settingUaaInfo
     * void : settingUaaInfo
    *****************************************************************/
/*    private void settingUaaInfo(HbBootstrapVO vo, List<String> cmd, ManifestTemplateVO result) {
        cmd.add("-o");
        cmd.add(MANIFEST_TEMPLATE_PATH + SEPARATOR + result.getMinReleaseVersion() + SEPARATOR + "common/" + result.getCommonOptionTemplate());
        cmd.add("-v");
        cmd.add("uaaRelease="+ RELEASE_DIR + SEPARATOR + vo.getDefaultConfigVo().getUaaRelease()+ "");
    }
*/
    /****************************************************************
     * @project : 이종 Paas 플랫폼 설치 자동화
     * @description : CPI CMD 정의
     * @title : settingIaasCpiInfo
     * @return : void
    *****************************************************************/
    private void settingIaasCpiInfo(HbBootstrapVO vo, List<String> cmd, ManifestTemplateVO result) {
        cmd.add("-o");
        cmd.add(MANIFEST_TEMPLATE_PATH + SEPARATOR + vo.getIaasType().toLowerCase() + SEPARATOR + result.getIaasPropertyTemplate());
        if("aws".equalsIgnoreCase(vo.getIaasType())){
            cmd.add("-v");
            cmd.add("az=" + vo.getIaasConfig().getCommonAvailabilityZone() + "");
            cmd.add("-v");
            cmd.add("subnet_id=" + vo.getNetworkConfigVo().getSubnetId() + "");
            cmd.add("-v");
            cmd.add("region=" + vo.getIaasAccount().get("commonRegion").toString());
            cmd.add("-v");
            cmd.add("access_key_id=" + vo.getIaasAccount().get("commonAccessUser").toString());
            cmd.add("-v");
            cmd.add("secret_access_key=" + vo.getIaasAccount().get("commonAccessSecret").toString());
        }else {
            cmd.add("-v");
            cmd.add("net_id=" + vo.getNetworkConfigVo().getSubnetId());
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
        }
        cmd.add("-v");
        cmd.add("boshCpiRelease=" + RELEASE_DIR + SEPARATOR + vo.getDefaultConfigVo().getBoshCpiRelease() + "");
        cmd.add("-v");
        cmd.add("stemcell=" + STEMCELL_DIR + SEPARATOR + vo.getResourceConfigVo().getStemcellName() + "");
        cmd.add("-v");
        cmd.add("cloudInstanceType=" + vo.getResourceConfigVo().getInstanceType());
        cmd.add("-v");
        cmd.add("internal_ip=" + vo.getNetworkConfigVo().getPrivateStaticIp());
        cmd.add("-v");
        cmd.add("default_key_name=" + vo.getIaasConfig().getCommonKeypairName());
        cmd.add("-v");
        cmd.add("default_security_groups=" + vo.getIaasConfig().getCommonSecurityGroup());
        cmd.add("-v");
        cmd.add("private_key=" + PRIVATE_KEY_PATH + vo.getIaasConfig().getCommonKeypairPath());
        
    }

    /****************************************************************
     * @param principal 
     * @project : 이종 Paas 플랫폼 설치 자동화
     * @description : 설치 상태를 설정하여 저장
     * @title : saveDeployStatus
     * @return : BootstrapVO
    *****************************************************************/
    public HbBootstrapVO saveDeployStatus(HbBootstrapVO bootstrapVo, Principal principal) {
        if ( bootstrapVo == null ) {
            return null;
        }
        bootstrapVo.setUpdateUserId(principal.getName());
        bootstrapDao.updateBootStrapConfigInfo(bootstrapVo);
        return bootstrapVo;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 비동기로 deployAsync 메소드 호출
     * @title : deployAsync
     * @return : void
    *****************************************************************/
    @Async
    public void deployAsync(HbBootStrapDeployDTO dto, Principal principal) {
            deployBootstrap(dto, principal);
    }
    
}
