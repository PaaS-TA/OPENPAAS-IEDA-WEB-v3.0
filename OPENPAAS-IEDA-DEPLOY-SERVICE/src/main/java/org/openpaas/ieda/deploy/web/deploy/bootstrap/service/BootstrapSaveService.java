package org.openpaas.ieda.deploy.web.deploy.bootstrap.service;

import java.security.Principal;
import java.util.Locale;

import javax.transaction.Transactional;

import org.hsqldb.lib.StringUtil;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.dao.BootstrapDAO;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.dao.BootstrapVO;
import org.openpaas.ieda.deploy.web.deploy.bootstrap.dto.BootStrapDeployDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class BootstrapSaveService {

    @Autowired MessageSource message;
    @Autowired private BootstrapDAO bootstrapDao;
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인프라 환경 설정 정보 등록/수정
     * @title : saveIaasConfigInfo
     * @return : BootstrapVO
    *****************************************************************/
    @Transactional
    public BootstrapVO saveIaasConfigInfo(BootStrapDeployDTO.IaasConfig dto, Principal principal) {
        BootstrapVO vo = null;
        if( StringUtils.isEmpty(dto.getId()) ){
            vo = new BootstrapVO();
            vo.setIaasType(dto.getIaasType());
            vo.setCreateUserId(principal.getName());
            vo.setTestFlag(dto.getTestFlag());//unit test 사용여부
        }else{
            vo = bootstrapDao.selectBootstrapInfo(Integer.parseInt(dto.getId()));
        }
        
        if( vo != null ){
            vo.setIaasConfigId(Integer.parseInt(dto.getIaasConfigId()));
            vo.setUpdateUserId(principal.getName());
        }else{
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        
        if( StringUtils.isEmpty(dto.getId()) ){
            bootstrapDao.insertBootStrapInfo(vo);
        }else{
            bootstrapDao.updateBootStrapInfo(vo);
        }
        
        return vo;
    }
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 기본 정보 저장
     * @title : saveDefaultInfo
     * @return : BootstrapVO
    ***************************************************/
    @Transactional
    public BootstrapVO saveDefaultInfo(BootStrapDeployDTO.Default dto, Principal principal) {
        if( StringUtils.isEmpty(dto.getId()) ){
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        BootstrapVO vo = bootstrapDao.selectBootstrapInfo(Integer.parseInt(dto.getId()));
        if( vo != null ){
            vo.setDeploymentName(dto.getDeploymentName().trim());
            vo.setDirectorName(dto.getDirectorName().trim());
            vo.setBoshRelease(dto.getBoshRelease().trim());
            vo.setCredentialKeyName(dto.getCredentialKeyName());
            vo.setNtp(dto.getNtp());
            vo.setBoshCpiRelease(dto.getBoshCpiRelease().trim());
            vo.setBoshBpmRelease(dto.getBoshBpmRelease().trim());
            vo.setEnableSnapshots(dto.getEnableSnapshots().trim());
            vo.setSnapshotSchedule(dto.getSnapshotSchedule().trim());
            vo.setUpdateUserId(principal.getName());
            vo.setPaastaMonitoringUse(dto.getPaastaMonitoringUse());
            vo.setPaastaMonitoringAgentRelease(dto.getPaastaMonitoringAgentRelease());
            vo.setPaastaMonitoringSyslogRelease(dto.getPaastaMonitoringSyslogRelease());
            vo.setMetricUrl(dto.getMetricUrl());
            vo.setSyslogAddress(dto.getSyslogAddress());
            vo.setSyslogPort(dto.getSyslogPort());
            vo.setSyslogTransport(dto.getSyslogTransport());
            vo.setOsConfRelease(dto.getOsConfRelease());
            vo.setBoshUaaRelease(dto.getBoshUaaRelease());
            vo.setBoshCredhubRelease(dto.getBoshCredhubRelease());
        }else{
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        bootstrapDao.updateBootStrapInfo(vo);
        return vo;
    }

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 네트워크 정보 저장
     * @title : saveNetworkInfo
     * @return : BootstrapVO
    ***************************************************/
    @Transactional
    public BootstrapVO saveNetworkInfo(BootStrapDeployDTO.Network dto, Principal principal) {
        if( StringUtils.isEmpty(dto.getId()) ){
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        
        BootstrapVO vo = bootstrapDao.selectBootstrapInfo(Integer.parseInt(dto.getId()));
        
        if( vo != null ){
            vo.setSubnetId(dto.getSubnetId());
            vo.setPrivateStaticIp(dto.getPrivateStaticIp());
            vo.setPublicStaticIp(dto.getPublicStaticIp());
            vo.setSubnetRange(dto.getSubnetRange());
            vo.setSubnetGateway(dto.getSubnetGateway());
            vo.setSubnetDns(dto.getSubnetDns());
            vo.setUpdateUserId(principal.getName());
            vo.setPublicSubnetId(dto.getPublicSubnetId()); //vSphere
            vo.setPublicSubnetRange(dto.getPublicSubnetRange()); //vSphere
            vo.setPublicSubnetGateway(dto.getPublicSubnetGateway()); //vSphere
            vo.setPublicSubnetDns(dto.getPublicSubnetDns()); //vSphere
            vo.setNetworkName(dto.getNetworkName());
        }else{
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        
        bootstrapDao.updateBootStrapInfo(vo);
        
        return vo;
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 리소스 정보 저장
     * @title : saveResourcesInfo
     * @return : BootstrapVO
    *****************************************************************/
    @Transactional
    public BootstrapVO saveResourceInfo(BootStrapDeployDTO.Resource dto, Principal principal) {
        if( StringUtils.isEmpty(dto.getId()) ){
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        
        BootstrapVO bootstrapVo = bootstrapDao.selectBootstrapInfo(Integer.parseInt(dto.getId()));
        //Result Check
        if(bootstrapVo != null){
            if( StringUtil.isEmpty(bootstrapVo.getDeploymentFile()) ){
                bootstrapVo.setDeploymentFile(makeDeploymentName(bootstrapVo));
            }
            bootstrapVo.setStemcell(dto.getStemcell());
            bootstrapVo.setCloudInstanceType(dto.getCloudInstanceType());
            bootstrapVo.setBoshPassword(dto.getBoshPassword());
            bootstrapVo.setResourcePoolCpu(dto.getResourcePoolCpu());
            bootstrapVo.setResourcePoolRam(dto.getResourcePoolRam());
            bootstrapVo.setResourcePoolDisk(dto.getResourcePoolDisk());
            bootstrapVo.setUpdateUserId(principal.getName());
        }else{
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        bootstrapDao.updateBootStrapInfo(bootstrapVo);
        return bootstrapVo;
    }    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 배포 파일명 생성
     * @title : makeDeploymentName
     * @return : String
    *****************************************************************/
    public String makeDeploymentName(BootstrapVO bootstrapVo ){
        String settingFileName = "";
        if( !StringUtils.isEmpty(bootstrapVo.getIaasType()) || !StringUtils.isEmpty(bootstrapVo.getId()) ){
            settingFileName = bootstrapVo.getIaasType().toLowerCase() + "-microbosh-"+ bootstrapVo.getId() +".yml";
        }else{
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        return settingFileName;
    }
}