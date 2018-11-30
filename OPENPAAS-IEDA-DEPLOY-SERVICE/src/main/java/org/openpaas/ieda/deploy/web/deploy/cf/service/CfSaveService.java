package org.openpaas.ieda.deploy.web.deploy.cf.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.transaction.Transactional;

import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.web.common.dto.KeyInfoDTO;
import org.openpaas.ieda.deploy.web.deploy.cf.dao.CfDAO;
import org.openpaas.ieda.deploy.web.deploy.cf.dao.CfVO;
import org.openpaas.ieda.deploy.web.deploy.cf.dto.CfParamDTO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.network.NetworkDAO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.network.NetworkVO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.resource.ResourceDAO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.resource.ResourceVO;
import org.openpaas.ieda.deploy.web.deploy.common.dto.network.NetworkDTO;
import org.openpaas.ieda.deploy.web.deploy.common.dto.resource.ResourceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CfSaveService {
    
    @Autowired private CfDAO cfDao;
    @Autowired private CfService cfService;
    @Autowired private NetworkDAO networkDao;
    @Autowired private ResourceDAO resourceDao;
    @Autowired MessageSource message;
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 기본 정보 저장
     * @title : saveDefaultInfo
     * @return : CfVO
    *****************************************************************/
    @Transactional
    public CfVO saveDefaultInfo(CfParamDTO.Default dto, Principal principal) {
        CfVO vo = null;
        if( StringUtils.isEmpty(dto.getId())){
            vo = new CfVO();
            vo.setIaasType(dto.getIaas());
            vo.setCreateUserId(principal.getName());
        }else{
            vo = cfDao.selectCfInfoById(Integer.parseInt(dto.getId()));
        }
        
        // 1.1 Deployment 정보
        vo.setDeploymentName(dto.getDeploymentName());
        vo.setDirectorUuid(dto.getDirectorUuid());
        vo.setReleaseName(dto.getReleaseName());
        vo.setReleaseVersion(dto.getReleaseVersion());
        vo.setUserAddSsh(dto.getUserAddSsh());
        vo.setCfDbtype(dto.getCfDbType());
        vo.setInceptionOsUserName(dto.getInceptionOsUserName());
        vo.setCfAdminPassword(dto.getCfAdminPassword());
        vo.setPortalDomain(dto.getPortalDomain());
        // 1.2 기본정보
        vo.setDomain(dto.getDomain());
        vo.setDomainOrganization(dto.getDomainOrganization());
        
        //1.3 PaaS-TA 모니터링 
        vo.setPaastaMonitoringUse(dto.getPaastaMonitoringUse());
        vo.setSyslogAddress(dto.getSyslogAddress());
        vo.setSyslogCustomRule(dto.getSyslogCustomRule());
        vo.setSyslogFallbackServers(dto.getSyslogFallbackServers());
        vo.setSyslogPort(dto.getSyslogPort());
        vo.setMetricUrl(dto.getMetricUrl());
        vo.setUpdateUserId(principal.getName());
        //배포 명 중복 검사
        int count = cfDao.selectCfDeploymentNameDuplication(vo.getIaasType(), vo.getDeploymentName(), vo.getId());
        if( count > 0 ){
            throw new CommonException(setMessageSourceValue("common.conflict.exception.code"), 
                    setMessageSourceValue("common.conflict.deployment.name.message"), HttpStatus.CONFLICT);
        }
        if( StringUtils.isEmpty(dto.getId()) ){
            cfDao.insertCfInfo(vo);
        }else{
            cfDao.updateCfInfo(vo);
        }
        return vo;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 네트워크 정보 저장 
     * @title : saveNetworkInfo
     * @return : void
    *****************************************************************/
    @Transactional
    public void saveNetworkInfo(List<NetworkDTO> dto, Principal principal ){
        List<NetworkVO> networkList = new ArrayList<NetworkVO>();
        String codeName= setMessageSourceValue("common.deploy.type.cf.name");
        if(!dto.isEmpty()){
            int cfId = 0;
            for(NetworkDTO network: dto){
                NetworkVO vo = new NetworkVO();
                cfId = Integer.parseInt(network.getCfId());
                vo.setId(Integer.parseInt(network.getCfId()));
                vo.setDeployType(network.getDeployType());
                vo.setPublicStaticIp(network.getPublicStaticIp());
                vo.setNet(network.getNet());
                vo.setSubnetRange(network.getSubnetRange());
                vo.setSubnetGateway(network.getSubnetGateway());
                vo.setSubnetDns(network.getSubnetDns());
                vo.setSubnetReservedFrom(network.getSubnetReservedFrom());
                vo.setSubnetReservedTo(network.getSubnetReservedTo());
                vo.setSubnetStaticFrom(network.getSubnetStaticFrom());
                vo.setSubnetStaticTo(network.getSubnetStaticTo());
                vo.setNetworkName(network.getNetworkName());
                vo.setSubnetId(network.getSubnetId());
                vo.setCloudSecurityGroups(network.getCloudSecurityGroups());
                vo.setAvailabilityZone(network.getAvailabilityZone());
                vo.setCreateUserId(principal.getName());
                vo.setUpdateUserId(principal.getName());
                
                networkList.add(vo);
            }
            int cnt = networkDao.selectNetworkList(Integer.parseInt(dto.get(0).getCfId()), codeName).size();
            if(cnt > 0 ){
                networkDao.deleteNetworkInfoRecord(Integer.parseInt(dto.get(0).getCfId()), codeName);
            }
            networkDao.insertNetworkList(networkList);
            
            //Internal Network는 1개인데 cf 고급 설정 z2가 존재 할 경우
            if( networkList.size() < 2 ) {
                List<HashMap<String, Object>> jobs = cfDao.selectCfJobSettingInfoListBycfId(setMessageSourceValue("common.deploy.type.cf.name"),cfId);
                for( HashMap<String, Object> job : jobs ) {
                    if( job.get("zone").toString().equalsIgnoreCase("z2") ) {
                        cfDao.deleteCfJobSettingRecordsByIdAndZone(cfId, job.get("zone").toString());
                        break;
                    }
                }
            }
        }
    }
    

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Key 생성 정보 저장
     * @title : saveKeyInfo
     * @return : void
    *****************************************************************/
    @Transactional
    public void saveKeyInfo(KeyInfoDTO dto, Principal principal){
        CfVO vo = cfDao.selectCfInfoById(Integer.parseInt(dto.getId()));
        if( vo != null ){
            vo.setCountryCode(dto.getCountryCode());
            vo.setStateName(dto.getStateName());
            vo.setLocalityName(dto.getLocalityName());
            vo.setOrganizationName(dto.getOrganizationName());
            vo.setUnitName(dto.getUnitName());
            vo.setEmail(dto.getEmail());
            vo.setUpdateUserId(principal.getName());
            cfDao.updateCfInfo(vo);
        }else{
            throw new CommonException(setMessageSourceValue("common.badRequest.exception.code"), 
                    setMessageSourceValue("common.badRequest.message"), HttpStatus.BAD_REQUEST);
        }
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : cf jobs 정보 저장
     * @title : saveCfJobsInfo
     * @return : void
    *****************************************************************/
    @Transactional
    public void saveCfJobsInfo(List<HashMap<String, String>> maps, Principal principal){
        if(  maps.size() != 0){
            String deployType = setMessageSourceValue("common.deploy.type.cf.name");
            int cfId = Integer.parseInt(maps.get(0).get("id"));
            int count= cfDao.selectCfJobSettingInfoListBycfId( deployType, cfId).size();
            
            if( count > 0 ){
                cfDao.deleteCfJobSettingListById(maps.get(0));
            }
            for( HashMap<String, String> map : maps ){
                map.put("create_user_id", principal.getName());
                map.put("update_user_id", principal.getName());
            }
            
            CfVO createCloudConfigVo = cfService.getCfInfo(cfId);
            if(createCloudConfigVo == null){
                throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), 
                        message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
            }
            cfService.commonCreateCloudConfig(createCloudConfigVo);
            
            cfDao.insertCfJobSettingInfo(maps);
            
        }
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 리소스 정보 저장 및 배포 파일명 설정 
     * @title : saveResourceInfo
     * @return : Map<String,Object>
    *****************************************************************/
    @Transactional
    public HashMap<String, Object> saveResourceInfo(ResourceDTO dto, Principal principal){
        String codeName= setMessageSourceValue("common.deploy.type.cf.name");
        HashMap<String, Object> map  = new HashMap<String, Object>();
        ResourceVO resourceVo = new ResourceVO();
        String deploymentFile = "";
        CfVO vo = cfDao.selectCfResourceInfoById(Integer.parseInt(dto.getId()), codeName);
        
        if(StringUtils.isEmpty(vo.getDeploymentFile())) {
            deploymentFile = makeDeploymentName(vo);
        }else {
            deploymentFile = vo.getDeploymentFile();
        }
        
        if( vo.getResource().getId() != null ){
            resourceVo = vo.getResource();
        }else{
            resourceVo.setId(vo.getId());
            resourceVo.setDeployType(codeName);
            resourceVo.setCreateUserId(principal.getName());
        }
        resourceVo.setUpdateUserId(principal.getName());
        resourceVo.setStemcellName(dto.getStemcellName());
        resourceVo.setStemcellVersion(dto.getStemcellVersion());
        resourceVo.setBoshPassword(dto.getBoshPassword());
        
        //vSphere Flavor setting 
        if( "vsphere".equals(vo.getIaasType().toLowerCase()) ){
            resourceVo.setSmallCpu(Integer.parseInt(dto.getSmallCpu()));
            resourceVo.setSmallDisk(Integer.parseInt(dto.getSmallDisk()));
            resourceVo.setSmallRam(Integer.parseInt(dto.getSmallRam()));
            resourceVo.setMediumCpu(Integer.parseInt(dto.getMediumCpu()));
            resourceVo.setMediumDisk(Integer.parseInt(dto.getMediumDisk()));
            resourceVo.setMediumRam(Integer.parseInt(dto.getMediumRam()));
            resourceVo.setLargeCpu(Integer.parseInt(dto.getLargeCpu()));
            resourceVo.setLargeDisk(Integer.parseInt(dto.getLargeDisk()));
            resourceVo.setLargeRam(Integer.parseInt(dto.getLargeRam()));
        }else{
            //openstack/aws Flavor setting
            resourceVo.setSmallFlavor(dto.getSmallFlavor());
            resourceVo.setMediumFlavor(dto.getMediumFlavor());
            resourceVo.setLargeFlavor(dto.getLargeFlavor());
        }
        //azure windows stemcell setting
        if("azure".equals(vo.getIaasType().toLowerCase())){
            resourceVo.setEnableWindowsStemcell(dto.getEnableWindowsStemcell());
            resourceVo.setWindowsStemcellName(dto.getWindowsStemcellName());
            resourceVo.setWindowsStemcellVersion(dto.getWindowsStemcellVersion());
            resourceVo.setWindowsCellInstance(dto.getWindowsCellInstance());
        }
        
        vo.setDeploymentFile(deploymentFile);
        vo.setUpdateUserId(principal.getName());
        
        //4. update Cf Info
        cfDao.updateCfInfo(vo);
        
        //5. Insert OR Update Cf Resource Info
        if( vo.getResource().getId() == null ){
            resourceDao.insertResourceInfo(resourceVo);
        }else{
            resourceDao.updateResourceInfo(resourceVo);
        }
        return map;
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
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 배포 파일명 설정
     * @title : makeDeploymentName
     * @return : String
    *****************************************************************/
    public String makeDeploymentName(CfVO vo ){
        String settingFileName = "";
        if(vo.getIaasType() != null || vo.getId() != null){
            settingFileName = vo.getIaasType().toLowerCase() + "-cf-"+ vo.getId() +"-cloud-config.yml";
        }else{
            throw new CommonException(setMessageSourceValue("common.badRequest.exception.code"), 
                    setMessageSourceValue("common.badRequest.message"), HttpStatus.BAD_REQUEST);
        }
        return settingFileName;
    }
}
