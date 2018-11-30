package org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.service;

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
import org.openpaas.ieda.deploy.web.common.dao.CommonDeployDAO;
import org.openpaas.ieda.deploy.web.common.dao.ManifestTemplateVO;
import org.openpaas.ieda.deploy.web.common.service.CommonDeployUtils;
import org.openpaas.ieda.hbdeploy.api.director.utility.HbDirectorRestHelper;
import org.openpaas.ieda.hbdeploy.web.config.setting.dao.HbDirectorConfigDAO;
import org.openpaas.ieda.hbdeploy.web.config.setting.dao.HbDirectorConfigVO;
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

@Service
public class HbBootstrapDeleteDeployAsyncService{

    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private HbDirectorConfigDAO directorDao;
    @Autowired private HbBootstrapDAO bootstrapDao;
    @Autowired private CommonDeployDAO commonDeployDao;
    @Autowired MessageSource message;
    
    final private static String SEPARATOR = System.getProperty("file.separator");
    final private static String DEPLOYMENT_DIR = LocalDirectoryConfiguration.getDeploymentDir() + SEPARATOR;
    final private static String HYBRID_CREDENTIAL_FILE = LocalDirectoryConfiguration.getGenerateHybridCredentialDir() + SEPARATOR;
    final private static String LOCK_DIR=LocalDirectoryConfiguration.getLockDir();
    final private static String MESSAGE_ENDPOINT = "/deploy/hbBootstrap/delete/logs"; 
    final private static Logger LOGGER = LoggerFactory.getLogger(HbBootstrapDeleteDeployAsyncService.class);
    final private static String MANIFEST_TEMPLATE_PATH = LocalDirectoryConfiguration.getManifastTemplateDir() + SEPARATOR +"bootstrap";
    final private static String PRIVATE_KEY_PATH = LocalDirectoryConfiguration.getSshDir()+SEPARATOR;
    final private static String RELEASE_DIR = LocalDirectoryConfiguration.getReleaseDir();
    final private static String STEMCELL_DIR = LocalDirectoryConfiguration.getStemcellDir();
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : bosh를 실행하여 해당 플랫폼 삭제 요청
     * @title : deleteBootstrapDeploy
     * @return : void
    *****************************************************************/
    public void deleteBootstrapDeploy(HbBootStrapDeployDTO dto, Principal principal) {
        
        String accumulatedLog = "";
        HbBootstrapVO vo = bootstrapDao.selectBootstrapConfigInfo(Integer.parseInt(dto.getId()), dto.getIaasType().toLowerCase());
        if( vo == null ){
            bootstrapDao.deleteBootstrapInfo(dto);
            vo = new HbBootstrapVO();
        }
        String status = "";
        String resultMessage = "";
        BufferedReader bufferedReader = null;

        try {
            String deployStateFile = DEPLOYMENT_DIR +vo.getDeploymentFile().split(".yml")[0] + "-state.json";
            File stateFile = new File(deployStateFile);
            if ( !stateFile.exists() ) {
                status = "done";
                resultMessage = "BOOTSTRAP 삭제가 완료되었습니다.";
                bootstrapDao.deleteBootstrapInfo(dto);
                HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, status, Arrays.asList("BOOTSTRAP를 삭제했습니다."));
                
            }else{
                File credentialFile = new File(HYBRID_CREDENTIAL_FILE+vo.getDefaultConfigVo().getCredentialKeyName());
                String boshRelease = vo.getDefaultConfigVo().getBoshRelease();
                if(  vo.getDefaultConfigVo().getBoshRelease().contains(".tgz") ){
                    boshRelease= vo.getDefaultConfigVo().getBoshRelease().replace(".tgz", ""); 
                }
                String releaseVersion = boshRelease.replaceAll("[^0-9]", "");
                String releaseName = boshRelease.replaceAll("[^A-Za-z]", "");
                ManifestTemplateVO result = commonDeployDao.selectManifetTemplate(vo.getIaasType(), releaseVersion, "BOOTSTRAP", releaseName );
                
                if(!credentialFile.exists()){
                    status = "error";
                    HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, status, Arrays.asList("BOOTSTRAP 디렉터 인증서가 존재 하지 않습니다."));
                }
                String deployFile = MANIFEST_TEMPLATE_PATH + SEPARATOR + result.getTemplateVersion()  + SEPARATOR  + "common" + SEPARATOR  + result.getCommonBaseTemplate();
                File file = new File(deployFile);
                List<String> cmd = new ArrayList<String>();
                if( file.exists() ){
                    cmd.add("bosh");
                    cmd.add("delete-env");
                    cmd.add(deployFile);
                    cmd.add("--state="+ DEPLOYMENT_DIR + vo.getDeploymentFile().replace(".yml", "")+"-state.json");
                    cmd.add("--vars-store="+HYBRID_CREDENTIAL_FILE + vo.getDefaultConfigVo().getCredentialKeyName()+"");
                    settingBoshInfo(vo, cmd);
                    settingIaasCpiInfo(vo, cmd, result);
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
                        HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "started", Arrays.asList(info));
                    }
                    accumulatedLog = accumulatedBuffer.toString();
                } else {
                    status = "error";
                    resultMessage = "배포 파일(" + deployFile + ")이 존재하지 않습니다.";
                    vo.setDeployStatus( message.getMessage("common.deploy.status.failed", null, Locale.KOREA) );
                    saveDeployStatus(vo, principal);
                }
                
                if ( "error".equalsIgnoreCase(status) || accumulatedLog.contains("fail") || accumulatedLog.contains("error") || accumulatedLog.contains("No deployment") || accumulatedLog.contains("Error") || accumulatedLog.contains("Expected file path to be non-empty")) {
                    status = "error";
                    vo.setDeployStatus(message.getMessage("common.deploy.status.failed", null, Locale.KOREA));
                    saveDeployStatus(vo, principal);
                    if ( resultMessage.isEmpty() ) {
                        resultMessage = "BOOTSTRAP 삭제 중 오류가 발생하였습니다.<br> 로그를 확인하세요.";
                    }
                } else {
                    status = "done";
                    resultMessage = "BOOTSTRAP 삭제가 완료되었습니다.";
                    bootstrapDao.deleteBootstrapInfo(dto);
                    deleteDirectorConfigInfo(vo.getIaasType(), vo.getDefaultConfigVo().getDirectorName());
                }
                HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, status, Arrays.asList(resultMessage));
            }
        }catch(RuntimeException e){
            status = "error";
            e.printStackTrace();
            CommonDeployUtils.deleteFile(LOCK_DIR, "hybird_bootstrap.lock");
            HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, status, Arrays.asList("BOOTSTRAP 삭제 중 Exception이 발생하였습니다."));
        } catch ( Exception e) {
            status = "error";
            CommonDeployUtils.deleteFile(LOCK_DIR, "hybird_bootstrap.lock");
            HbDirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, status, Arrays.asList("BOOTSTRAP 삭제 중 Exception이 발생하였습니다."));
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
            CommonDeployUtils.deleteFile(LOCK_DIR, "hybird_bootstrap.lock");
        }
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
     * @project : Paas 플랫폼 설치 자동화
     * @description : 설치 상태 저장
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
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 설치관리자 정보가 존재 할 경우 삭제
     * @title : deleteDirectorConfigInfo
     * @return : void
    ***************************************************/
    public void deleteDirectorConfigInfo(String iaasType, String directorName) {
        String cpi = iaasType.toLowerCase()+"_cpi";
        HbDirectorConfigVO vo = directorDao.selectHbDirectorConfigInfoByDirectorNameAndCPI(cpi, directorName);
        if( vo != null ) {
          directorDao.deleteHbDirector(vo.getIedaDirectorConfigSeq());
        }
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 비동기로  deleteDeploy 호출
     * @title : deleteDeployAsync
     * @return : void
    *****************************************************************/
    @Async
    public void deleteDeployAsync(HbBootStrapDeployDTO dto, Principal principal) {
            deleteBootstrapDeploy(dto, principal);
    }    

}
