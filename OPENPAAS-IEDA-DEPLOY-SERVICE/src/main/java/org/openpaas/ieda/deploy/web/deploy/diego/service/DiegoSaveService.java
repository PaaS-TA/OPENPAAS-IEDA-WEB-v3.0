package org.openpaas.ieda.deploy.web.deploy.diego.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.transaction.Transactional;

import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.web.deploy.cf.dao.CfDAO;
import org.openpaas.ieda.deploy.web.deploy.cf.dao.CfVO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.network.NetworkDAO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.network.NetworkVO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.resource.ResourceDAO;
import org.openpaas.ieda.deploy.web.deploy.common.dao.resource.ResourceVO;
import org.openpaas.ieda.deploy.web.deploy.common.dto.network.NetworkDTO;
import org.openpaas.ieda.deploy.web.deploy.common.dto.resource.ResourceDTO;
import org.openpaas.ieda.deploy.web.deploy.diego.dao.DiegoDAO;
import org.openpaas.ieda.deploy.web.deploy.diego.dao.DiegoVO;
import org.openpaas.ieda.deploy.web.deploy.diego.dto.DiegoParamDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class DiegoSaveService {
    @Autowired private CfDAO cfDao;
    @Autowired private DiegoDAO diegoDao;
    @Autowired private NetworkDAO networkDao;
    @Autowired private ResourceDAO resourceDao;
    @Autowired MessageSource message;
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Diego 기본정보 저장
     * @title : saveDefaultInfo
     * @return : DiegoVO
    *****************************************************************/
    public DiegoVO saveDefaultInfo(DiegoParamDTO.Default dto, Principal principal) {
        DiegoVO vo = null;
        if( StringUtils.isEmpty(dto.getId()) ){
            vo = new DiegoVO();
            vo.setCreateUserId(principal.getName());
            vo.setIaasType(dto.getIaas());
        }else{
            vo = diegoDao.selectDiegoInfo(Integer.parseInt(dto.getId()));
        }
        // 1.1 기본정보
        vo.setDeploymentName(dto.getDeploymentName());
        vo.setDirectorUuid(dto.getDirectorUuid());
        vo.setDiegoReleaseName(dto.getDiegoReleaseName());
        vo.setDiegoReleaseVersion(dto.getDiegoReleaseVersion());
        vo.setCflinuxfs2rootfsreleaseName(dto.getCflinuxfs2rootfsreleaseName());
        vo.setCflinuxfs2rootfsreleaseVersion(dto.getCflinuxfs2rootfsreleaseVersion());
        vo.setCfId(dto.getCfId());
        
        if( !StringUtils.isEmpty(dto.getCfDeploymentFile()) ){
            vo.setCfDeployment(dto.getCfDeploymentFile());
        }
        vo.setGardenReleaseName(dto.getGardenReleaseName());
        vo.setGardenReleaseVersion(dto.getGardenReleaseVersion());
        vo.setEtcdReleaseName(dto.getEtcdReleaseName());
        vo.setEtcdReleaseVersion(dto.getEtcdReleaseVersion());
        vo.setPaastaMonitoringUse(dto.getPaastaMonitoringUse());
        vo.setCadvisorDriverIp(dto.getCadvisorDriverIp());
        vo.setUpdateUserId(principal.getName());
        
        //배포 명 중복 검사
        int count = diegoDao.selectDiegoDeploymentNameDuplication(vo.getIaasType(), vo.getDeploymentName(), vo.getId());
        if( count > 0 ){
            throw new CommonException(setMessageSourceValue("common.conflict.exception.code"), 
                    setMessageSourceValue("common.conflict.deployment.name.message"), HttpStatus.CONFLICT);
        }
        
        if( StringUtils.isEmpty(dto.getId()) ) { 
            diegoDao.insertDiegoDefaultInfo(vo);//저장
        }else{  
            diegoDao.updateDiegoDefaultInfo(vo);//수정 
        }
        return vo;
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  Diego 네트워크 정보 저장   
     * @title : saveNetworkInfo
     * @return : DiegoVO
    *****************************************************************/
    @Transactional
    public DiegoVO saveNetworkInfo(List<NetworkDTO> dto, Principal principal){
        List<NetworkVO> networkList = new ArrayList<NetworkVO>();
        String deployType = setMessageSourceValue("common.deploy.type.diego.name");
        if(!dto.isEmpty()){
            int diegoId = 0;
            for(NetworkDTO network: dto){
                NetworkVO vo = new NetworkVO();
                diegoId = Integer.parseInt(network.getDiegoId());
                vo.setId(diegoId);
                vo.setDeployType(deployType);
                vo.setNet(network.getNet());
                vo.setSubnetRange(network.getSubnetRange());
                vo.setSubnetGateway(network.getSubnetGateway());
                vo.setSubnetDns(network.getSubnetDns());
                vo.setSubnetReservedFrom(network.getSubnetReservedFrom());
                vo.setSubnetReservedTo(network.getSubnetReservedTo());
                vo.setNetworkName(network.getNetworkName());
                vo.setSubnetStaticFrom(network.getSubnetStaticFrom());
                vo.setSubnetStaticTo(network.getSubnetStaticTo());
                vo.setSubnetId(network.getSubnetId());
                vo.setCloudSecurityGroups(network.getCloudSecurityGroups());
                vo.setAvailabilityZone(network.getAvailabilityZone());
                vo.setCreateUserId(principal.getName());
                vo.setUpdateUserId(principal.getName());
                networkList.add(vo);
            }
            int cnt = networkDao.selectNetworkList(Integer.parseInt(dto.get(0).getDiegoId()), deployType).size();
            if(cnt > 0){
                networkDao.deleteNetworkInfoRecord(Integer.parseInt(dto.get(0).getDiegoId()), deployType);
            }
            networkDao.insertNetworkList(networkList);
            List<HashMap<String, Object>> jobs = diegoDao.selectDiegoJobSettingInfoListBycfId(setMessageSourceValue("common.deploy.type.diego.name"),diegoId);
            for( HashMap<String, Object> job : jobs ) {
                if( networkList.size() == 1 ) {
                    if( job.get("zone").toString().equalsIgnoreCase("z2") || job.get("zone").toString().equalsIgnoreCase("z3") ) {
                        diegoDao.deleteDiegoJobSettingRecordsByIdAndZone(diegoId, job.get("zone").toString());
                        break;
                    }
                } else if ( networkList.size() == 2  ){
                    if( job.get("zone").toString().equalsIgnoreCase("z3") ) {
                        diegoDao.deleteDiegoJobSettingRecordsByIdAndZone(diegoId, job.get("zone").toString());
                        break;
                    }
                }
            }
        }
        
        DiegoVO vo = new DiegoVO();
        vo.setNetworks(networkList);
        return vo;
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Diego 리소스 저장
     * @title : saveResourceInfo
     * @return : Map<String,Object>
    *****************************************************************/
    @Transactional
    public Map<String, Object> saveResourceInfo(ResourceDTO dto, Principal principal){
        String deploymentFile = null;
        Map<String, Object> map = new HashMap<String, Object>();
        ResourceVO resourceVo = new ResourceVO();
        String deployType  =setMessageSourceValue("common.deploy.type.diego.name");
        DiegoVO vo = diegoDao.selectResourceInfoById(Integer.parseInt(dto.getId()), deployType);
        
        if(vo.getDeploymentFile() == null  || StringUtils.isEmpty(vo.getDeploymentFile())) {
            deploymentFile = makeDeploymentName(vo);
        } else {
            deploymentFile = vo.getDeploymentFile();
        }
        
        //3. set resourceVo(insert/update)
        if( vo.getResource().getId() != null ){
            resourceVo = vo.getResource();
            resourceVo.setId(vo.getId());
        }else{
            resourceVo.setId(vo.getId());
            resourceVo.setDeployType(deployType);
            resourceVo.setCreateUserId(principal.getName());
        }
        
        resourceVo.setUpdateUserId(principal.getName());
        resourceVo.setStemcellName(dto.getStemcellName());
        resourceVo.setStemcellVersion(dto.getStemcellVersion());
        resourceVo.setBoshPassword(dto.getBoshPassword());
        
        //Flavor setting
        //vSphere Flavor setting 
        if( "vsphere".equalsIgnoreCase(vo.getIaasType()) ){
            resourceVo.setSmallCpu(Integer.parseInt(dto.getSmallCpu()));
            resourceVo.setSmallDisk(Integer.parseInt(dto.getSmallDisk()));
            resourceVo.setSmallRam(Integer.parseInt(dto.getSmallRam()));
            resourceVo.setMediumCpu(Integer.parseInt(dto.getMediumCpu()));
            resourceVo.setMediumDisk(Integer.parseInt(dto.getMediumDisk()));
            resourceVo.setMediumRam(Integer.parseInt(dto.getMediumRam()));
            resourceVo.setLargeCpu(Integer.parseInt(dto.getLargeCpu()));
            resourceVo.setLargeDisk(Integer.parseInt(dto.getLargeDisk()));
            resourceVo.setLargeRam(Integer.parseInt(dto.getLargeRam()));
            resourceVo.setRunnerCpu(Integer.parseInt(dto.getRunnerCpu()));
            resourceVo.setRunnerDisk(Integer.parseInt(dto.getRunnerDisk()));
            resourceVo.setRunnerRam(Integer.parseInt(dto.getRunnerRam()));
        }else{
            //openstack/aws Flavor setting
            resourceVo.setSmallFlavor(dto.getSmallFlavor());
            resourceVo.setMediumFlavor(dto.getMediumFlavor());
            resourceVo.setLargeFlavor(dto.getLargeFlavor());
            resourceVo.setRunnerFlavor(dto.getRunnerFlavor());
        }
        
        vo.setDeploymentFile(deploymentFile);
        vo.setUpdateUserId(principal.getName());
        map.put("deploymentFile", deploymentFile);
        map.put("id", vo.getId());
        
        //update Diego Info
        CfVO cfVo = cfDao.selectCfInfoByDeploymentName(vo.getIaasType(), vo.getCfDeployment());
        if( cfVo.getReleaseName().equalsIgnoreCase("cf") && Integer.parseInt(cfVo.getReleaseVersion()) > 271 || 
                cfVo.getReleaseName().equalsIgnoreCase("paasta-controller") && cfVo.getReleaseVersion().equals("3.0") || cfVo.getReleaseName().equalsIgnoreCase("paasta-controller") && cfVo.getReleaseVersion().equals("3.1") ) {
            vo.setKeyFile(cfVo.getKeyFile());
        }else {
            vo.setKeyFile(dto.getKeyFile());
        }
        
        diegoDao.updateDiegoDefaultInfo(vo);
        //Insert OR Update Diego Resource Info
        if( vo.getResource().getId() == null ){
            resourceDao.insertResourceInfo(resourceVo);
        }else{
            resourceDao.updateResourceInfo(resourceVo);
        }
        return map;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Diego 배포 파일 명 생성
     * @title : makeDeploymentName
     * @return : String
    *****************************************************************/
    public String makeDeploymentName(DiegoVO vo){
        String settingFileName = "";
        if(vo.getIaasType() != null || vo.getId() != null){
            settingFileName = vo.getIaasType().toLowerCase() + "-diego-"+ vo.getId() +".yml";
        } else{
            throw new CommonException(setMessageSourceValue("common.badRequest.exception.code"),
                    setMessageSourceValue("common.badRequest.message"), HttpStatus.BAD_REQUEST);
        }
        return settingFileName;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Diego jobs 정보 저장
     * @title : saveCfJobsInfo
     * @return : void
    *****************************************************************/
    public void saveDiegoJobsInfo(List<HashMap<String, String>> maps, Principal principal) {
        if(  maps.size() != 0){
            String deployType  =setMessageSourceValue("common.deploy.type.diego.name");
            int count= diegoDao.selectDiegoJobSettingInfoListBycfId( deployType, Integer.parseInt(maps.get(0).get("id"))).size();
            
            if( count > 0 ){
                diegoDao.deleteDiegoJobSettingInfo(maps.get(0));
            }
            for( HashMap<String, String> map : maps ){
                map.put("create_user_id", principal.getName());
                map.put("update_user_id", principal.getName());
            }
            diegoDao.insertDiegoJobSettingInfo(maps);
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
