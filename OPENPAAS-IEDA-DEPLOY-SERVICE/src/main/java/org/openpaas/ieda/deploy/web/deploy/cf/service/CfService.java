package org.openpaas.ieda.deploy.web.deploy.cf.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.openpaas.ieda.deploy.web.common.service.CommonDeployUtils;
import org.openpaas.ieda.deploy.web.deploy.cf.dao.CfDAO;
import org.openpaas.ieda.deploy.web.deploy.cf.dao.CfVO;
import org.openpaas.ieda.deploy.web.deploy.cf.dto.CfListDTO;
import org.openpaas.ieda.deploy.web.deploy.cf.dto.CfParamDTO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.network.NetworkDAO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.network.NetworkVO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.resource.ResourceDAO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.resource.ResourceVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class CfService {

    @Autowired private CfDAO cfDao;
    @Autowired private CommonDeployDAO commonDao;
    @Autowired private NetworkDAO networkDao;
    @Autowired private ResourceDAO resourceDao;
    @Autowired MessageSource message;
    
    final private static String SEPARATOR = System.getProperty("file.separator");
    final private static String MANIFEST_TEMPLATE_LOCATION = LocalDirectoryConfiguration.getManifastTemplateDir() + SEPARATOR +"cf" + SEPARATOR;
    final private static String TEMP_DIR = LocalDirectoryConfiguration.getTempDir();
    final private static Logger LOGGER = LoggerFactory.getLogger(CfService.class);
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :CF 정보 목록 전체 조회   
     * @title : getCfLIst
     * @return : List<CfListDTO>
    *****************************************************************/
    public List<CfListDTO> getCfLIst(String iaas, String platform) {
        List<CfListDTO> cfList = new ArrayList<CfListDTO>();
        String codeName= setMessageSourceValue("common.deploy.type.cf.name");
        List<CfVO> listCf  = cfDao.selectCfList(iaas, platform);
        if( !listCf.isEmpty()){
            int recid = 0;
            for( CfVO vo : listCf ){
                CfListDTO cfInfo = new CfListDTO();
                cfInfo.setRecid(recid++);
                cfInfo.setId(vo.getId());
                cfInfo.setIaas(vo.getIaasType());

                cfInfo.setCreateDate(vo.getCreateDate());
                cfInfo.setUpdateDate(vo.getUpdateDate());
                cfInfo.setDiegoYn(vo.getDiegoYn());
                cfInfo.setDeploymentName(vo.getDeploymentName());
                cfInfo.setDirectorUuid(vo.getDirectorUuid());
                cfInfo.setReleaseName(vo.getReleaseName());
                cfInfo.setReleaseVersion(vo.getReleaseVersion());
                cfInfo.setAppSshFingerprint(vo.getAppSshFingerprint());
                
                cfInfo.setDomain(vo.getDomain());
                cfInfo.setDescription(vo.getDescription());
                cfInfo.setDomainOrganization(vo.getDomainOrganization());
                cfInfo.setPaastaMonitoringUse(vo.getPaastaMonitoringUse());
                cfInfo.setIngestorIp(vo.getIngestorIp());
                cfInfo.setKeyFile(vo.getKeyFile());
                //NETWORK
                cfInfo = setNetworkInfoList(cfInfo, vo, codeName);
                
                //Resource
                cfInfo =setResourceListInfo(cfInfo, vo, codeName);
                
                cfInfo.setDeployStatus(vo.getDeployStatus());
                cfInfo.setDeploymentFile(vo.getDeploymentFile());
                if( !StringUtils.isEmpty(vo.getTaskId()) ) {
                    cfInfo.setTaskId(vo.getTaskId());
                }
                cfList.add(cfInfo);
            }
        }
        return cfList;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인프라 별 해당 CF의 네트워크 정보 조회
     * @title : setNetworkInfoList
     * @return : CfListDTO
    *****************************************************************/
    public CfListDTO setNetworkInfoList(CfListDTO cfListInfo, CfVO vo, String codeName){
        List<NetworkVO> netowrks = networkDao.selectNetworkList(vo.getId(), codeName);
        String br = "";
        int cnt = 0;
        String subnetRange , subnetGateway , subnetDns , subnetReservedIp;
        subnetRange = subnetGateway = subnetDns = subnetReservedIp = "";
        String subnetStaticIp ,subnetId , cloudSecurityGroups, availabilityZone;
        subnetStaticIp  = subnetId = cloudSecurityGroups = availabilityZone =  "";
        
        if(netowrks  != null){
            for(NetworkVO networkVO: netowrks){
                if( "internal".equalsIgnoreCase(networkVO.getNet() )){
                    cnt ++;
                    if( cnt > 1  && cnt < netowrks.size() ){
                        br = ""; 
                    }else {
                        br = "<br>";
                    }
                    subnetRange += networkVO.getSubnetRange()  + br;
                    subnetGateway += networkVO.getSubnetGateway() + br;
                    subnetDns += networkVO.getSubnetDns() + br;
                    subnetReservedIp += (networkVO.getSubnetReservedFrom() + " - " +  networkVO.getSubnetReservedTo() + br);
                    subnetStaticIp += networkVO.getSubnetStaticFrom() +" - " + networkVO.getSubnetStaticTo() + br;
                    subnetId += networkVO.getSubnetId() + br;
                    cloudSecurityGroups += networkVO.getCloudSecurityGroups() + br;
                    availabilityZone += networkVO.getAvailabilityZone() + br;
                }
            }
            cfListInfo.setSubnetRange(subnetRange);
            cfListInfo.setSubnetGateway(subnetGateway);
            cfListInfo.setSubnetDns(subnetDns);
            cfListInfo.setSubnetReservedIp(subnetReservedIp);
            cfListInfo.setSubnetStaticIp(subnetStaticIp);
            cfListInfo.setSubnetId(subnetId);
            cfListInfo.setCloudSecurityGroups(cloudSecurityGroups);
            cfListInfo.setAvailabilityZone(availabilityZone);
        }
        return cfListInfo;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인프라 별 해당 CF 리소스 목록 정보 조회
     * @title : setResourceListInfo
     * @return : CfListDTO
    *****************************************************************/
    public CfListDTO setResourceListInfo(CfListDTO cfListInfo, CfVO vo, String codeName){
        ResourceVO resource = resourceDao.selectResourceInfo(vo.getId(), codeName);
        if( resource != null ){
            cfListInfo.setStemcellName(resource.getStemcellName());
            cfListInfo.setStemcellVersion(resource.getStemcellVersion());
            cfListInfo.setBoshPassword(resource.getBoshPassword());
        }
        return cfListInfo;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 정보 상세 조회
     * @title : getCfInfo
     * @return : CfVO
    *****************************************************************/
    public CfVO getCfInfo(int id) {
        String codeName= setMessageSourceValue("common.deploy.type.cf.name");
        CfVO vo = cfDao.selectCfInfoById(id);
        if( vo == null ){
            throw new CommonException(setMessageSourceValue("common.badRequest.exception.code"), 
                    setMessageSourceValue("common.badRequest.message"), HttpStatus.BAD_REQUEST);
        }
        vo.setNetworks(networkDao.selectNetworkList(id, codeName) );
        vo.setResource(resourceDao.selectResourceInfo(id, codeName));
        vo.setJobs(cfDao.selectCfJobSettingInfoListBycfId(codeName, id));
        return vo;
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 네트워크 정보 목록 조회
     * @title : getNetowrkListInfo
     * @return : List<NetworkVO>
    *****************************************************************/
    public List<NetworkVO> getNetowrkListInfo(int id, String deployType){
        List<NetworkVO> list = networkDao.selectNetworkList(id, deployType);
        return list;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인프라 및 릴리즈 버전 별 job 목록 조회
     * @title : getJobTemplateList
     * @return : List<HashMap<String,String>>
    *****************************************************************/
    public List<HashMap<String, String>> getJobTemplateList(String deployType, String releaseVersion){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("releaseVersion", releaseVersion);
        map.put("deployType", deployType);
        List<HashMap<String, String>> list = cfDao.selectCfJobTemplatesByReleaseVersion(map);
        return list;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 입력 정보를 바탕으로 manifest 파일 생성
     * @title : createSettingFile
     * @return : void
    *****************************************************************/
    public void createSettingFile(CfVO vo) {
        String content = "";
        ManifestTemplateVO result = null;
        InputStream inputs  = null;
        try {
            //1. get Manifest Template info
            result = commonDao.selectManifetTemplate(vo.getIaasType(), vo.getReleaseVersion(), "CF", vo.getReleaseName());
            
            ManifestTemplateVO manifestTemplate = null;
            if(result != null){
                inputs =  this.getClass().getClassLoader().getResourceAsStream("static/deploy_template/cf/"+ result.getTemplateVersion()  + "/" + vo.getIaasType().toLowerCase() + "/" +result.getInputTemplate());
                content = IOUtils.toString(inputs, "UTF-8");
                
                manifestTemplate = new ManifestTemplateVO();
                manifestTemplate = setOptionManifestTemplateInfo(result, manifestTemplate, vo);
                manifestTemplate.setDeployType("CF");
                manifestTemplate.setIaasType(vo.getIaasType());
            }else {
                throw new CommonException(setMessageSourceValue("common.badRequest.exception.code"), 
                        setMessageSourceValue("common.badRequest.manifest_template.message"), HttpStatus.BAD_REQUEST);
            }
            List<ReplaceItemDTO> replaceItems = setReplaceItems(vo, vo.getIaasType());
            for (ReplaceItemDTO item : replaceItems) {
                content = content.replace(item.getTargetItem(), item.getSourceItem());
            }
            if( LOGGER.isDebugEnabled() ) {
                LOGGER.debug("content: " + content);
            }
            
            IOUtils.write(content, new FileOutputStream(TEMP_DIR + SEPARATOR + vo.getDeploymentFile()), "UTF-8");
            CommonDeployUtils.setSpiffMerge(vo.getKeyFile(),  vo.getDeploymentFile(),  manifestTemplate, vo.getPaastaMonitoringUse());
        } catch (IOException e) {
            throw new CommonException(setMessageSourceValue("common.internalServerError.exception.code"), 
                    setMessageSourceValue("common.file.internalServerError.message"), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NullPointerException e){
            throw new CommonException(setMessageSourceValue("common.badRequest.exception.code"), 
                    setMessageSourceValue("common.notFound.template.message"), HttpStatus.NOT_FOUND);
        } catch(Exception e){
            throw new CommonException(setMessageSourceValue("common.badRequest.exception.code"), 
                    setMessageSourceValue("common.badRequest.message"), HttpStatus.BAD_REQUEST);
        } 
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Option Manifest 템플릿 정보 설정
     * @title : setOptionManifestTemplateInfo
     * @return : ManifestTemplateVO
    *****************************************************************/
    public ManifestTemplateVO setOptionManifestTemplateInfo(ManifestTemplateVO result, ManifestTemplateVO  manifestTemplate, CfVO vo){
        //Base Template File
        if(result.getCommonBaseTemplate() != null && !(StringUtils.isEmpty( result.getCommonBaseTemplate()) )){
            manifestTemplate.setCommonBaseTemplate( MANIFEST_TEMPLATE_LOCATION + result.getTemplateVersion() + SEPARATOR + "common" + SEPARATOR  +  result.getCommonBaseTemplate());
        }else{
            manifestTemplate.setCommonBaseTemplate("");
        }
        //Job Template File
        if(result.getCommonJobTemplate() != null && !(StringUtils.isEmpty( result.getCommonJobTemplate()) )){
            manifestTemplate.setCommonJobTemplate(  MANIFEST_TEMPLATE_LOCATION + result.getTemplateVersion() + SEPARATOR + "common" + SEPARATOR  +  result.getCommonJobTemplate() );
        }else{
            manifestTemplate.setCommonJobTemplate("");
        }
        //meta Template File
        if(result.getMetaTemplate() != null  && !(StringUtils.isEmpty( result.getMetaTemplate()) )){
            manifestTemplate.setMetaTemplate(MANIFEST_TEMPLATE_LOCATION + result.getTemplateVersion() + SEPARATOR + vo.getIaasType().toLowerCase() + SEPARATOR  +  result.getMetaTemplate());
        }else{
            manifestTemplate.setMetaTemplate("");
        }
        //iaas Property Template File
        if(result.getIaasPropertyTemplate() != null  && !(StringUtils.isEmpty( result.getIaasPropertyTemplate()) )){
            manifestTemplate.setIaasPropertyTemplate(MANIFEST_TEMPLATE_LOCATION + result.getTemplateVersion() + SEPARATOR + vo.getIaasType().toLowerCase() + SEPARATOR + result.getIaasPropertyTemplate() );
        }else{
            manifestTemplate.setIaasPropertyTemplate("");
        }
        //네트워크를 추가할 경우(2개 이상)
        if( vo.getNetworks().size() >2 && result.getOptionNetworkTemplate() != null  && !(StringUtils.isEmpty( result.getOptionNetworkTemplate()) )){
            manifestTemplate.setOptionNetworkTemplate(MANIFEST_TEMPLATE_LOCATION + result.getTemplateVersion() + SEPARATOR + vo.getIaasType().toLowerCase() + SEPARATOR + result.getOptionNetworkTemplate() );
        }else{
            manifestTemplate.setOptionNetworkTemplate("");
        }
        //resource Template File 
        if( !vo.getJobs().isEmpty() && result.getOptionResourceTemplate() != null && !(StringUtils.isEmpty( result.getOptionResourceTemplate())) ){
            manifestTemplate.setOptionResourceTemplate(MANIFEST_TEMPLATE_LOCATION + result.getTemplateVersion() + SEPARATOR + vo.getIaasType().toLowerCase() + SEPARATOR + result.getOptionResourceTemplate() );
        }else{
            manifestTemplate.setOptionResourceTemplate("");
        }
        //option etc Template File
        if( "true".equals(vo.getPaastaMonitoringUse().toLowerCase()) && result.getCommonOptionTemplate() != null  && !(StringUtils.isEmpty( result.getCommonOptionTemplate()) )){
            manifestTemplate.setCommonOptionTemplate(MANIFEST_TEMPLATE_LOCATION + result.getTemplateVersion() + SEPARATOR + "common" + SEPARATOR + result.getCommonOptionTemplate() );
        }else{
            manifestTemplate.setCommonOptionTemplate("");
        }
        //diego use Template File
        if( "true".equals(vo.getDiegoYn().toLowerCase()) && result.getOptionEtc() != null  && !(StringUtils.isEmpty( result.getOptionEtc()) )){
            manifestTemplate.setOptionEtc(MANIFEST_TEMPLATE_LOCATION + result.getTemplateVersion() + SEPARATOR + "common" + SEPARATOR + result.getOptionEtc() );
        }else{
            manifestTemplate.setOptionEtc("");
        }
        
        return manifestTemplate;
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 입력한 정보를 ReplaceItemDTO에 설정
     * @title : setReplaceItems
     * @return : List<ReplaceItemDTO>
    *****************************************************************/
    public List<ReplaceItemDTO> setReplaceItems(CfVO vo, String iaas) {
        List<ReplaceItemDTO> items = new ArrayList<ReplaceItemDTO>();

        // 1 Deployment 정보
        items.add(new ReplaceItemDTO("[deploymentName]", vo.getDeploymentName()));
        items.add(new ReplaceItemDTO("[directorUuid]", vo.getDirectorUuid()));
        items.add(new ReplaceItemDTO("[releaseName]", vo.getReleaseName()));
        items.add(new ReplaceItemDTO("[releaseVersion]",  "\"" +vo.getReleaseVersion() + "\""));
        
        items.add(new ReplaceItemDTO("[loggregatorReleaseName]", vo.getLoggregatorReleaseName()));
        items.add(new ReplaceItemDTO("[loggregatorReleaseVersion]",  "\"" +vo.getLoggregatorReleaseVersion() + "\""));
        
        // 2 기본정보
        setReplaceItemsDefaultInfo(vo, items);
        
        // 3. 네트워크 정보
        setReplaceItemsFromNetworkInfo(vo, items);
        
        // 4. 리소스 정보
        setReplaceItemsFromResourceInfo(vo, items);
        
        //5. 고급 설정 정보
        setReplaceItmesFromJobsInfo(vo, items);
        return items;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 기본 정보 ReplaceItemDTO에 설정
     * @title : setReplaceItemsDefaultInfo
     * @return : List<ReplaceItemDTO>
    *****************************************************************/
    public List<ReplaceItemDTO> setReplaceItemsDefaultInfo(CfVO vo,List<ReplaceItemDTO> items){
        items.add(new ReplaceItemDTO("[domain]", vo.getDomain()));
        items.add(new ReplaceItemDTO("[description]", vo.getDescription()));
        items.add(new ReplaceItemDTO("[domainOrganization]", vo.getDomainOrganization()));
        items.add(new ReplaceItemDTO("[deaDiskMB]", String.valueOf(vo.getDeaDiskMB())));
        items.add(new ReplaceItemDTO("[deaMemoryMB]", String.valueOf(vo.getDeaMemoryMB())));
        items.add(new ReplaceItemDTO("[loginSecret]", vo.getLoginSecret()));
        //핑커프린트(diego 연동 유무)
        if("true".equalsIgnoreCase(vo.getDiegoYn())){
            items.add(new ReplaceItemDTO("[appSshFingerprint]", vo.getAppSshFingerprint()));
        }else{
            items.add(new ReplaceItemDTO("[appSshFingerprint]", ""));
        }
        items.add(new ReplaceItemDTO("[ingestorIp]", vo.getIngestorIp()));
        return items;
    }
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 네트워크 정보 ReplaceItemDTO에 Replace
     * @title : setReplaceItemsFromNetworkInfo
     * @return : List<ReplaceItemDTO>
    *****************************************************************/
    public List<ReplaceItemDTO> setReplaceItemsFromNetworkInfo(CfVO vo, List<ReplaceItemDTO> items){
        int internalCnt = 0;
        for( int i=0; i<vo.getNetworks().size(); i++ ){
            if( "INTERNAL".equalsIgnoreCase(vo.getNetworks().get(i).getNet()) ){
                internalCnt ++;
                if(internalCnt  == 1 ){
                    items.add(new ReplaceItemDTO("[subnetRange]", vo.getNetworks().get(i).getSubnetRange()));
                    items.add(new ReplaceItemDTO("[subnetGateway]", vo.getNetworks().get(i).getSubnetGateway()));
                    items.add(new ReplaceItemDTO("[subnetDns]", vo.getNetworks().get(i).getSubnetDns()));
                    items.add(new ReplaceItemDTO("[subnetReserved]", vo.getNetworks().get(i).getSubnetReservedFrom() + " - " + vo.getNetworks().get(i).getSubnetReservedTo()));
                    items.add(new ReplaceItemDTO("[subnetStatic]", vo.getNetworks().get(i).getSubnetStaticFrom() + " - " + vo.getNetworks().get(i).getSubnetStaticTo()));
                    items.add(new ReplaceItemDTO("[cloudNetId]", vo.getNetworks().get(i).getSubnetId()));            
                    if( !("VSPHERE".equalsIgnoreCase(vo.getIaasType())) ){
                        items.add(new ReplaceItemDTO("[cloudSecurityGroups]", vo.getNetworks().get(i).getCloudSecurityGroups()));
                        if("AWS".equalsIgnoreCase(vo.getIaasType())){
                            items.add(new ReplaceItemDTO("[availabilityZone]", vo.getNetworks().get(i).getAvailabilityZone()));
                        }else if( "GOOGLE".equalsIgnoreCase(vo.getIaasType()) ){
                            items.add(new ReplaceItemDTO("[zone]", vo.getNetworks().get(i).getAvailabilityZone()));
                            items.add(new ReplaceItemDTO("[networkName]", vo.getNetworks().get(i).getNetworkName()));
                        }
                    }
                }else if( internalCnt > 1){
                    items.add(new ReplaceItemDTO("[subnetRange1]", vo.getNetworks().get(i).getSubnetRange()));
                    items.add(new ReplaceItemDTO("[subnetGateway1]", vo.getNetworks().get(i).getSubnetGateway()));
                    items.add(new ReplaceItemDTO("[subnetDns1]", vo.getNetworks().get(i).getSubnetDns()));
                    items.add(new ReplaceItemDTO("[subnetReserved1]", vo.getNetworks().get(i).getSubnetReservedFrom() + " - " + vo.getNetworks().get(i).getSubnetReservedTo()));
                    items.add(new ReplaceItemDTO("[subnetStatic1]", vo.getNetworks().get(i).getSubnetStaticFrom() + " - " + vo.getNetworks().get(i).getSubnetStaticTo()));
                    items.add(new ReplaceItemDTO("[cloudNetId1]", vo.getNetworks().get(i).getSubnetId()));
                    if( !("VSPHERE".equalsIgnoreCase(vo.getIaasType())) ){
                        items.add(new ReplaceItemDTO("[cloudSecurityGroups1]", vo.getNetworks().get(i).getCloudSecurityGroups()));
                        if("AWS".equalsIgnoreCase(vo.getIaasType())){
                            items.add(new ReplaceItemDTO("[availabilityZone1]", vo.getNetworks().get(i).getAvailabilityZone()));
                        }else if( "GOOGLE".equalsIgnoreCase(vo.getIaasType()) ){
                            items.add(new ReplaceItemDTO("[zone]", vo.getNetworks().get(i).getAvailabilityZone()));
                            items.add(new ReplaceItemDTO("[networkName]", vo.getNetworks().get(i).getNetworkName()));
                        }
                    }
                }
            }else if( "EXTERNAL".equalsIgnoreCase(vo.getNetworks().get(i).getNet()) &&  "VSPHERE".equalsIgnoreCase(vo.getIaasType()) ){
                items.add(new ReplaceItemDTO("[publicSubnetRange]", vo.getNetworks().get(i).getSubnetRange()));
                items.add(new ReplaceItemDTO("[publicSubnetGateway]", vo.getNetworks().get(i).getSubnetGateway()));
                items.add(new ReplaceItemDTO("[publicSubnetDns]", vo.getNetworks().get(i).getSubnetDns()));
                items.add(new ReplaceItemDTO("[publicSubnetStatic]", vo.getNetworks().get(i).getSubnetStaticFrom() + " - " + vo.getNetworks().get(i).getSubnetStaticTo()));
                items.add(new ReplaceItemDTO("[publicCloudNetId]", vo.getNetworks().get(i).getSubnetId()));
                items.add(new ReplaceItemDTO("[proxyStaticIps]", vo.getNetworks().get(i).getSubnetStaticFrom()) );
            }else{
                items.add(new ReplaceItemDTO("[proxyStaticIps]", vo.getNetworks().get(i).getSubnetStaticFrom()) );
            }
        } 
        if( internalCnt < 2 ){
            items.add(new ReplaceItemDTO("[subnetRange1]", ""));
            items.add(new ReplaceItemDTO("[subnetGateway1]", ""));
            items.add(new ReplaceItemDTO("[subnetDns1]", ""));
            items.add(new ReplaceItemDTO("[subnetReserved1]", ""));
            items.add(new ReplaceItemDTO("[subnetStatic1]", ""));
            items.add(new ReplaceItemDTO("[networkName1]", ""));
            items.add(new ReplaceItemDTO("[cloudNetId1]", ""));
            items.add(new ReplaceItemDTO("[cloudSecurityGroups1]", ""));
            items.add(new ReplaceItemDTO("[availabilityZone1]", ""));
        }
        
        return items;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 리소스 정보 ReplaceItemDTO에 Replace
     * @title : setReplaceItemsFromResourceInfo
     * @return : List<ReplaceItemDTO>
    *****************************************************************/
    public List<ReplaceItemDTO> setReplaceItemsFromResourceInfo(CfVO vo, List<ReplaceItemDTO> items){
        items.add(new ReplaceItemDTO("[stemcellName]", vo.getResource().getStemcellName() ));
        items.add(new ReplaceItemDTO("[stemcellVersion]", "\"" + vo.getResource().getStemcellVersion() + "\"" ));
        items.add(new ReplaceItemDTO("[boshPassword]", Sha512Crypt.Sha512_crypt(vo.getResource().getBoshPassword(), RandomStringUtils.randomAlphabetic(10), 0)));
        if("VSPHERE".equalsIgnoreCase(vo.getIaasType())){
            //small Flavor
            items.add(new ReplaceItemDTO("[sInsTypeCPU]",  String.valueOf(vo.getResource().getSmallCpu())));
            items.add(new ReplaceItemDTO("[sInsTypeRAM]", String.valueOf(vo.getResource().getSmallRam())));
            items.add(new ReplaceItemDTO("[sInsTypeDISK]", String.valueOf(vo.getResource().getSmallDisk())));
            //medium Flavor
            items.add(new ReplaceItemDTO("[mInsTypeCPU]",  String.valueOf(vo.getResource().getMediumCpu())));
            items.add(new ReplaceItemDTO("[mInsTypeRAM]", String.valueOf(vo.getResource().getMediumRam())));
            items.add(new ReplaceItemDTO("[mInsTypeDISK]", String.valueOf(vo.getResource().getMediumDisk())));
            //large Flavor
            items.add(new ReplaceItemDTO("[lInsTypeCPU]",  String.valueOf(vo.getResource().getLargeCpu())));
            items.add(new ReplaceItemDTO("[lInsTypeRAM]", String.valueOf(vo.getResource().getLargeRam())));
            items.add(new ReplaceItemDTO("[lInsTypeDISK]", String.valueOf(vo.getResource().getLargeDisk())));
            //runner Flavor
            items.add(new ReplaceItemDTO("[rInsTypeCPU]",  String.valueOf(vo.getResource().getRunnerCpu())));
            items.add(new ReplaceItemDTO("[rInsTypeRAM]", String.valueOf(vo.getResource().getRunnerRam())));
            items.add(new ReplaceItemDTO("[rInsTypeDISK]", String.valueOf(vo.getResource().getRunnerDisk())));
        }else{
            items.add(new ReplaceItemDTO("[smallInstanceType]", vo.getResource().getSmallFlavor()));
            items.add(new ReplaceItemDTO("[mediumInstanceType]", vo.getResource().getMediumFlavor()));
            items.add(new ReplaceItemDTO("[largeInstanceType]", vo.getResource().getLargeFlavor()));
            items.add(new ReplaceItemDTO("[runnerInstanceType]", vo.getResource().getRunnerFlavor()));
        }
        return items;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 고급 설정 정보 ReplaceItemDTO에 설정
     * @title : setReplaceItmesFromJobsInfo
     * @return : List<ReplaceItemDTO>
    *****************************************************************/
    public List<ReplaceItemDTO> setReplaceItmesFromJobsInfo(CfVO vo, List<ReplaceItemDTO> items){
        //1. replaceItemDTO에 설정
        boolean flag = false;
        for( int j=0; j< vo.getJobs().size(); j++){
            HashMap<String, Object> map = vo.getJobs().get(j);
            //Internal Network는 1개인데 고급설정에 z2가 있을 경우 제외
            if( !(map.get("zone").toString().equalsIgnoreCase("z2") && vo.getNetworks().size() == 2) ) {
                items.add( new ReplaceItemDTO("["+map.get("job_name").toString()+map.get("zone").toString().toUpperCase()+"]"
                        , map.get("instances").toString()) );
                
            }
            if( map.get("zone").toString().equalsIgnoreCase("z2") ) {
                flag = true;
            }
        }
        if( vo.getNetworks().size() < 3 ) {
            for( HashMap<String, Object> map: vo.getJobs()){
                items.add( new ReplaceItemDTO("["+map.get("job_name").toString()+"Z2]", "0") );
            }
        }else if( vo.getNetworks().size() > 2 && !flag ) {
            for( HashMap<String, Object> map: vo.getJobs()){
                items.add( new ReplaceItemDTO("["+map.get("job_name").toString()+"Z2]", "1") );
            }
        }
        return items;
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 단순 레코드 삭제 
     * @title : deleteCfInfoRecord
     * @return : void
    *****************************************************************/
    @Transactional
    public void deleteCfInfoRecord(CfParamDTO.Delete dto) {
        String deployType= setMessageSourceValue("common.deploy.type.cf.name");
        cfDao.deleteCfInfoRecord(Integer.parseInt(dto.getId()));
        if( dto.getId() != null ){
            HashMap<String, String> map = new HashMap<String, String>();
            networkDao.deleteNetworkInfoRecord(Integer.parseInt( dto.getId()), deployType );
            resourceDao.deleteResourceInfo( Integer.parseInt(dto.getId()), deployType );
            map.put("id", dto.getId());
            map.put("deploy_type", deployType);
            cfDao.deleteCfJobSettingListById(map);
        }
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : message 값 가져오기
     * @title : setMessageSourceValue
     * @return : String
    *****************************************************************/
    public String setMessageSourceValue(String name){
        return message.getMessage(name, null, Locale.KOREA);
    }
}