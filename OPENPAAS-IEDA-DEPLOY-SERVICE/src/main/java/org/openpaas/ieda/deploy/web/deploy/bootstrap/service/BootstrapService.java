package org.openpaas.ieda.deploy.web.deploy.bootstrap.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.common.api.Sha512Crypt;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.web.common.dao.CommonDeployDAO;
import org.openpaas.ieda.deploy.web.common.dao.ManifestTemplateVO;
import org.openpaas.ieda.deploy.web.common.dto.ReplaceItemDTO;
import org.openpaas.ieda.deploy.web.common.service.CommonDeployService;
import org.openpaas.ieda.deploy.web.common.service.CommonDeployUtils;
import org.openpaas.ieda.deploy.web.config.setting.dao.DirectorConfigDAO;
import org.openpaas.ieda.deploy.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.dao.BootstrapDAO;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.dao.BootstrapVO;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.dto.BootStrapDeployDTO;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.dto.BootstrapListDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service
public class BootstrapService {

    @Autowired private BootstrapDAO bootStrapDao;
    @Autowired private CommonDeployDAO commonDeployDao;
    @Autowired private CommonDeployService commonDeployService;
    @Autowired private DirectorConfigDAO directorDao;
    @Autowired MessageSource message;
    
    final private static String SEPARATOR = System.getProperty("file.separator");
    final private static String LOCK_DIR=LocalDirectoryConfiguration.getLockDir();
    final private static String STEMCELL_DIR=LocalDirectoryConfiguration.getStemcellDir();
    final private static String TEMP_DIR=LocalDirectoryConfiguration.getTempDir();
    final private static String RELEASE_DIR=LocalDirectoryConfiguration.getReleaseDir();
    final private static String PRIVATE_KEY_PATH = LocalDirectoryConfiguration.getSshDir()+SEPARATOR;
    final private static String JSON_KEY_PATH = LocalDirectoryConfiguration.getKeyDir() + SEPARATOR;
    final private static String MANIFEST_TEMPLATE_PATH = LocalDirectoryConfiguration.getManifastTemplateDir() + SEPARATOR +"bootstrap";
    final static Logger LOGGER = LoggerFactory.getLogger(BootstrapService.class);
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 목록 조회
     * @title : bootstrapList
     * @return : List<BootstrapListDTO>
    ***************************************************/
    public List<BootstrapListDTO> getBootstrapList() {
        List<BootstrapVO> bootstrapConfigsList = bootStrapDao.selectBootstrapList();
        List<BootstrapListDTO> listDtos = new ArrayList<>();
        int recid =0;
        
        if(!bootstrapConfigsList.isEmpty()){
            for(BootstrapVO vo :bootstrapConfigsList){
                BootstrapListDTO dto = new BootstrapListDTO();
                dto.setRecid(recid++);
                dto.setId(vo.getId());
                dto.setIaasConfigAlias(vo.getIaasConfig().getIaasConfigAlias());
                dto.setDeployStatus(vo.getDeployStatus());
                dto.setDeploymentName(vo.getDeploymentName());
                dto.setDirectorName(vo.getDirectorName());
                dto.setIaas(vo.getIaasType());
                dto.setBoshRelease(vo.getBoshRelease());
                dto.setBoshCpiRelease(vo.getBoshCpiRelease());
                dto.setBoshBpmRelease(vo.getBoshBpmRelease());
                dto.setSubnetId(vo.getSubnetId());
                dto.setSubnetRange(vo.getSubnetRange());
                dto.setPublicStaticIp(vo.getPublicStaticIp());
                dto.setPrivateStaticIp(vo.getPrivateStaticIp());
                dto.setSubnetGateway(vo.getSubnetGateway());
                dto.setSubnetDns(vo.getSubnetDns());
                dto.setNtp(vo.getNtp());
                dto.setStemcell(vo.getStemcell());
                dto.setInstanceType(vo.getCloudInstanceType());
                dto.setBoshPassword(vo.getBoshPassword());
                dto.setDeploymentFile(vo.getDeploymentFile());
                dto.setDeployLog(vo.getDeployLog());
                dto.setCreateDate(vo.getCreateDate());
                dto.setUpdateDate(vo.getUpdateDate());
                
                listDtos.add(dto);
            }
        }
        return listDtos;
    }

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Bootstrap 정보 상세 조회
     * @title : getBootstrapInfo
     * @return : BootstrapVO
    ***************************************************/
    public BootstrapVO getBootstrapInfo(int id) {
        BootstrapVO vo = bootStrapDao.selectBootstrapInfo(id);
        if( vo == null ){
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.notFound.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        return vo;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스냅샷 사용 여부
     * @title : getSnapshotInfo
     * @return : int
    ***************************************************/
    public int getSnapshotInfo(){
        DirectorConfigVO vo = directorDao.selectDirectorConfigByDefaultYn("Y");
        if ( vo == null ) {
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.notFound.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }        
        return bootStrapDao.selectSnapshotInfo(vo);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 입력 정보를 바탕으로 manifest 생성
     * @title : createSettingFile
     * @return : void
    ***************************************************/
    public void createSettingFile(int id) {
        String content = "";
        try {
            //data 조회
            BootstrapVO vo = bootStrapDao.selectBootstrapInfo(id);
            if( vo == null){
                throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                        message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
            }
            //릴리즈명/버전 추출
            String boshRelease = vo.getBoshRelease();
            if(  vo.getBoshRelease().contains(".tgz") ){
                boshRelease= vo.getBoshRelease().replace(".tgz", ""); 
            }
            String releaseVersion = boshRelease.replaceAll("[^0-9]", "");
            String releaseName = boshRelease.replaceAll("[^A-Za-z]", "");
            
            //해당 Bosh 릴리즈 버전의 Manifest Template 파일 조회
            ManifestTemplateVO result = commonDeployDao.selectManifetTemplate(vo.getIaasType(), releaseVersion, "BOOTSTRAP", releaseName );
            if(result != null){
                if(vo.getPaastaMonitoringUse().equals("true")) {
                    String paastaMoniteringDeploymentFile = result.getCommonJobTemplate().split("\\.")[0] + "-paasta-monitering.yml";
                    result.setCommonJobTemplate(paastaMoniteringDeploymentFile);
                }
                content = commonDeployService.getManifestInputTemplateStream("bootstrap", result.getTemplateVersion(), vo.getIaasType(), result.getCommonJobTemplate(), vo.getIaasAccount().get("openstackVersion").toString());
            }else {
                throw new CommonException("null.boshTemplate.exception", "설치 가능한 BOSH 릴리즈 버전을 확인 하세요.", HttpStatus.NOT_FOUND);
            }
            //필요한 Manifest Template 파일의 디렉토리 정보 설정
            ManifestTemplateVO manifestTemplate = setOptionManifestTemplateInfo(result, vo.getIaasType().toLowerCase() , vo.getIaasAccount().get("openstackVersion").toString());
            manifestTemplate.setDeployType(result.getDeployType());
            manifestTemplate.setIaasType(result.getIaasType());
            
            //입력한 정보를 바탕으로 Input Template의 항목과 데이터 치환할 항목들 설정
            List<ReplaceItemDTO> replaceItems = makeReplaceItems(vo);
            for (ReplaceItemDTO item : replaceItems) {
                content = content.replace(item.getTargetItem(), item.getSourceItem() == null ? "":item.getSourceItem());
            }
            LOGGER.debug(content);
            //플랫폼 설치 자동화(.bosh_plugin)의 temp 디렉토리에 치환한 Input Template 파일 출력
            IOUtils.write(content, new FileOutputStream(TEMP_DIR + SEPARATOR + vo.getDeploymentFile()), "UTF-8");
            //spiff 프로그램을 통해 배포파일 생성
            CommonDeployUtils.setSpiffMerge("", vo.getDeploymentFile(),  manifestTemplate, vo.getPaastaMonitoringUse());
        } catch (IOException e) {
            throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                    message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /***************************************************
     * @param string 
     * @project : Paas 플랫폼 설치 자동화
     * @description : Manifest Template 디렉토리 정보
     * @title : setOptionManifestTemplateInfo
     * @return : ManifestTemplateVO
    ***************************************************/
    public ManifestTemplateVO setOptionManifestTemplateInfo(ManifestTemplateVO result, String iaasType, String openstackVerison){
        ManifestTemplateVO  manifestTemplate = new ManifestTemplateVO();
        if(!openstackVerison.isEmpty()) {
            if(openstackVerison.equalsIgnoreCase("v3")){
                iaasType += "v3";
            }
        }
        //base
        if(result.getCommonBaseTemplate() != null  && !(StringUtils.isEmpty( result.getCommonBaseTemplate()) )){
            manifestTemplate.setCommonBaseTemplate( MANIFEST_TEMPLATE_PATH + SEPARATOR + result.getTemplateVersion()  + SEPARATOR  + "common" + SEPARATOR  + result.getCommonBaseTemplate());
        }else{
            manifestTemplate.setCommonBaseTemplate("");
        }
        //job
        if(result.getCommonJobTemplate() != null && !(StringUtils.isEmpty( result.getCommonJobTemplate()) )){
            manifestTemplate.setCommonJobTemplate(  MANIFEST_TEMPLATE_PATH + SEPARATOR + result.getTemplateVersion()  + SEPARATOR + iaasType+ SEPARATOR  +  result.getCommonJobTemplate() );
        }else{
            manifestTemplate.setCommonJobTemplate("");
        }
        
        //option etc Template File
        if( result.getCommonOptionTemplate() != null && !(StringUtils.isEmpty( result.getCommonOptionTemplate())  )){
            if( result.getDeployType().equalsIgnoreCase("bootstrap") || result.getDeployType().equals("cf")){
                manifestTemplate.setCommonOptionTemplate(MANIFEST_TEMPLATE_PATH + SEPARATOR + result.getTemplateVersion() + SEPARATOR + "common" + SEPARATOR + result.getCommonOptionTemplate() );
            }else{
                manifestTemplate.setCommonOptionTemplate("");
            }
        }else{
            manifestTemplate.setCommonOptionTemplate("");
        }
        
        //iaasProperty
        if(result.getIaasPropertyTemplate() != null && !(StringUtils.isEmpty( result.getIaasPropertyTemplate()) )){
            manifestTemplate.setIaasPropertyTemplate(  MANIFEST_TEMPLATE_PATH + SEPARATOR + result.getTemplateVersion()  + SEPARATOR + iaasType + SEPARATOR  +  result.getIaasPropertyTemplate() );
        }else{
            manifestTemplate.setIaasPropertyTemplate("");
        }
        //meta
        if(result.getMetaTemplate() != null && !(StringUtils.isEmpty( result.getMetaTemplate()) )){
            manifestTemplate.setMetaTemplate(MANIFEST_TEMPLATE_PATH + SEPARATOR + result.getTemplateVersion()  + SEPARATOR + iaasType + SEPARATOR  +  result.getMetaTemplate());
        }else{
            manifestTemplate.setMetaTemplate("");
        }
        return manifestTemplate;
    }

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 화면에 입력한 값을 Input Template 파일의 항목과 치환 하기위해 데이터 설정
     * @title : makeReplaceItems
     * @return : List<ReplaceItemDTO>
    ***************************************************/
    public List<ReplaceItemDTO> makeReplaceItems(BootstrapVO vo) {
        List<ReplaceItemDTO> items = new ArrayList<ReplaceItemDTO>();
        //인프라 환경 설정 정보
        if(vo.getIaasAccount().get("openstackVersion") != null) {
            if(vo.getIaasAccount().get("openstackVersion").toString().equalsIgnoreCase("v3")){
                items.add(new ReplaceItemDTO("[project]", vo.getIaasAccount().get("commonProject").toString()));
                items.add(new ReplaceItemDTO("[domain]", vo.getIaasAccount().get("openstackDomain").toString()));
                items.add(new ReplaceItemDTO("[region]", vo.getIaasAccount().get("commonRegion").toString()));
            }else {
                items.add(new ReplaceItemDTO("[tenant]", vo.getIaasAccount().get("commonTenant").toString()));
            }
        }
        if("AZURE".equalsIgnoreCase(vo.getIaasType())){
            items.add(new ReplaceItemDTO("[commonTenant]", vo.getIaasAccount().get("commonTenant").toString()));
            items.add(new ReplaceItemDTO("[azureSubscriptionId]", vo.getIaasAccount().get("azureSubscriptionId").toString()));
        }
        
        items.add(new ReplaceItemDTO("[accessEndpoint]", vo.getIaasAccount().get("commonAccessEndpoint").toString()));
        items.add(new ReplaceItemDTO("[accessUser]", vo.getIaasAccount().get("commonAccessUser").toString()));
        items.add(new ReplaceItemDTO("[accessSecret]", vo.getIaasAccount().get("commonAccessSecret").toString()));
        items.add(new ReplaceItemDTO("[region]", vo.getIaasConfig().getCommonRegion()));
        items.add(new ReplaceItemDTO("[defaultSecurityGroups]", vo.getIaasConfig().getCommonSecurityGroup()));
        
        items.add(new ReplaceItemDTO("[availabilityZone]", vo.getIaasConfig().getCommonAvailabilityZone()));
        items.add(new ReplaceItemDTO("[privateKeyName]", vo.getIaasConfig().getCommonKeypairName()));
        items.add(new ReplaceItemDTO("[privateKeyPath]", PRIVATE_KEY_PATH + vo.getIaasConfig().getCommonKeypairPath()));
        
        if( vo.getIaasType().equalsIgnoreCase("Google") ){
            String googleJsonKey=setGoogleJosnKeyReplaceEnter(JSON_KEY_PATH + vo.getIaasAccount().get("googleJsonKey").toString());
            items.add(new ReplaceItemDTO("[jsonKey]", googleJsonKey));
            items.add(new ReplaceItemDTO("[projectId]", vo.getIaasAccount().get("commonProject").toString()));
            items.add(new ReplaceItemDTO("[sshKeyFile]", vo.getIaasConfig().getGooglePublicKey()));
            items.add(new ReplaceItemDTO("[boshOsConfRelease]", RELEASE_DIR + SEPARATOR + vo.getOsConfRelease()));
        }
        
        items.add(new ReplaceItemDTO("[vCenterName]", vo.getIaasConfig().getVsphereVcentDataCenterName()));
        items.add(new ReplaceItemDTO("[vCenterVMFolder]", vo.getIaasConfig().getVsphereVcenterVmFolder()));
        items.add(new ReplaceItemDTO("[vCenterTemplateFolder]", vo.getIaasConfig().getVsphereVcenterTemplateFolder()));
        items.add(new ReplaceItemDTO("[vCenterDatastore]", vo.getIaasConfig().getVsphereVcenterDatastore()));
        items.add(new ReplaceItemDTO("[vCenterPersistentDatastore]", vo.getIaasConfig().getVsphereVcenterPersistentDatastore()));
        items.add(new ReplaceItemDTO("[vCenterDiskPath]", vo.getIaasConfig().getVsphereVcenterDiskPath()));
        items.add(new ReplaceItemDTO("[vCenterCluster]", vo.getIaasConfig().getVsphereVcenterCluster()));
        
        items.add(new ReplaceItemDTO("[azureResourceGroup]", vo.getIaasConfig().getAzureResourceGroup()));
        items.add(new ReplaceItemDTO("[azureStorageAccountName]", vo.getIaasConfig().getAzureStorageAccountName()));
        items.add(new ReplaceItemDTO("[azureSshPublicKey]", vo.getIaasConfig().getAzureSshPublicKey()));
        
        //기본 정보
        items.add(new ReplaceItemDTO("[deploymentName]", vo.getDeploymentName()));
        items.add(new ReplaceItemDTO("[directorName]", vo.getDirectorName()));
        items.add(new ReplaceItemDTO("[boshRelease]", RELEASE_DIR + SEPARATOR + vo.getBoshRelease()));
        items.add(new ReplaceItemDTO("[boshCpiRelease]", RELEASE_DIR + SEPARATOR + vo.getBoshCpiRelease()));
        items.add(new ReplaceItemDTO("[boshBpmRelease]", RELEASE_DIR + SEPARATOR + vo.getBoshBpmRelease()));
        items.add(new ReplaceItemDTO("[enableSnapshot]", vo.getEnableSnapshots()));
        items.add(new ReplaceItemDTO("[snapshotSchedule]", vo.getSnapshotSchedule()));
        items.add(new ReplaceItemDTO("[ntp]", vo.getNtp()));
        items.add(new ReplaceItemDTO("[cpiName]", vo.getIaasType().toLowerCase()+"_cpi"));
        items.add(new ReplaceItemDTO("[cpiReleaseName]", "bosh-"+vo.getIaasType().toLowerCase()+"-cpi"));
        
        if( vo.getPaastaMonitoringUse().equalsIgnoreCase("true") ) {
            //items.add(new ReplaceItemDTO("[paastaMonitoringIp]", vo.getPaastaMonitoringIp()));
            items.add(new ReplaceItemDTO("[paastaMonitoringReleaseName]", "bosh-monitoring-agent"));
            //items.add(new ReplaceItemDTO("[paastaMonitoringRelease]", RELEASE_DIR + SEPARATOR + vo.getPaastaMonitoringRelease()));
            //items.add(new ReplaceItemDTO("[influxdbIp]", vo.getInfluxdbIp()+":8059"));
        }else {
            items.add(new ReplaceItemDTO("[paastaMonitoringIp]", ""));
            items.add(new ReplaceItemDTO("[paastaMonitoringReleaseName]", ""));
            items.add(new ReplaceItemDTO("[paastaMonitoringRelease]", ""));
            items.add(new ReplaceItemDTO("[influxdbIp]", ""));
        }
        
        //Internal 네트워크 정보
        items.add(new ReplaceItemDTO("[privateStaticIp]", vo.getPrivateStaticIp()));
        items.add(new ReplaceItemDTO("[subnetId]", vo.getSubnetId()));
        
        if( vo.getIaasType().equalsIgnoreCase("vSphere") ){
            items.add(new ReplaceItemDTO("[networkName]", vo.getSubnetId()));
        }else{
            items.add(new ReplaceItemDTO("[networkName]", vo.getNetworkName()));
        }
        items.add(new ReplaceItemDTO("[subnetRange]", vo.getSubnetRange() ));
        items.add(new ReplaceItemDTO("[subnetGateway]", vo.getSubnetGateway()));
        items.add(new ReplaceItemDTO("[subnetDns]", vo.getSubnetDns()));
        //External 네트워크 정보
        items.add(new ReplaceItemDTO("[publicStaticIp]", vo.getPublicStaticIp()));
        items.add(new ReplaceItemDTO("[publicNetworkName]", vo.getPublicSubnetId()));
        items.add(new ReplaceItemDTO("[publicSubnetRange]", vo.getPublicSubnetRange() ));
        items.add(new ReplaceItemDTO("[publicSubnetGateway]", vo.getPublicSubnetGateway()));
        items.add(new ReplaceItemDTO("[publicSubnetDns]", vo.getPublicSubnetDns()));
        
        //리소스 정보
        items.add(new ReplaceItemDTO("[cloudInstanceType]", vo.getCloudInstanceType()));
        items.add(new ReplaceItemDTO("[resourcePoolCPU]", vo.getResourcePoolCpu()));
        items.add(new ReplaceItemDTO("[resourcePoolRAM]", vo.getResourcePoolRam()));
        items.add(new ReplaceItemDTO("[resourcePoolDisk]", vo.getResourcePoolDisk()));
        items.add(new ReplaceItemDTO("[stemcell]", STEMCELL_DIR + SEPARATOR + vo.getStemcell()));
        items.add(new ReplaceItemDTO("[boshPassword]", Sha512Crypt.Sha512_crypt(vo.getBoshPassword(), RandomStringUtils.randomAlphabetic(10), 0)));
        
        return items;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 구글 Json key 엔터 제거
     * @title : setGoogleJosnKeyReplaceEnter
     * @return : String
    *****************************************************************/
    public String setGoogleJosnKeyReplaceEnter(String jsonKeyPath){
        StringBuffer jsonKey = new StringBuffer();
        FileInputStream fis = null;
        BufferedReader rd = null;
        try{
            fis = new FileInputStream(new File(jsonKeyPath));
            rd = new BufferedReader(new InputStreamReader(fis,"UTF-8"));
            String line = null;
            while((line = rd.readLine()) != null) {
                jsonKey.append(line);
            }
        }catch(IOException e){
            throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA), 
                    message.getMessage("common.file.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
        }catch(Exception e){
            throw new CommonException("notfound.createKey.exception", e.getMessage(), HttpStatus.NOT_FOUND);
        } finally {
            try {
                if( rd != null ) {
                    rd.close();
                }
            } catch (IOException e) {
                throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA), 
                        message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            try {
                if( fis != null ) {
                    fis.close();
                }
            } catch (IOException e) {
                throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA), 
                        message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return jsonKey.toString();
    }

    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 단순 레코드 삭제
     * @title : deleteBootstrapInfoRecord
     * @return : Boolean
    ***************************************************/
    @Transactional
    public void deleteBootstrapInfo(BootStrapDeployDTO.Delete dto ) {
        if( StringUtils.isEmpty(dto.getId()) ){
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        bootStrapDao.deleteBootstrapInfo(Integer.parseInt(dto.getId()));
        CommonDeployUtils.deleteFile(LOCK_DIR, "bootstrap.lock");
    }
}