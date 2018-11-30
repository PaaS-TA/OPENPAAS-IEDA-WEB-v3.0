package org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.service;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import javax.transaction.Transactional;

import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapDefaultConfigDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapDefaultConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto.HbBootstrapDefaultConfigDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class HbBootstrapDefaultConfigService {
    
    @Autowired private MessageSource message;
    @Autowired private HbBootstrapDefaultConfigDAO bootstrapDefaultDao;
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 기본 정보 목록 조회
     * @title : getDefaultConfigInfoList
     * @return : List<HbBootstrapDefaultConfigVO> 
    *****************************************************************/
    public List<HbBootstrapDefaultConfigVO> getDefaultConfigInfoList() {
        List<HbBootstrapDefaultConfigVO> list = bootstrapDefaultDao.selectBootstrapDefaultConfigInfoList();
        return list;
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 기본 정보 등록/수정
     * @title : saveDefaultConfigInfo
     * @return : void
    *****************************************************************/
    @Transactional
    public void saveDefaultConfigInfo(HbBootstrapDefaultConfigDTO dto, Principal principal) {
        HbBootstrapDefaultConfigVO vo = null;
        int count = bootstrapDefaultDao.selectBootstrapDefaultConfigByName(dto.getDefaultConfigName());
        if( StringUtils.isEmpty(dto.getId())){
            vo = new HbBootstrapDefaultConfigVO();
            vo.setCreateUserId(principal.getName());
            if(count > 0){
                throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                        message.getMessage("hybrid.configMgnt.alias.conflict.message.exception", null, Locale.KOREA), HttpStatus.CONFLICT);
            }
        }else{
            vo = bootstrapDefaultDao.selectBootstrapDefaultConfigInfo(Integer.parseInt(dto.getId()), dto.getIaasType().toLowerCase());
            if(!dto.getDefaultConfigName().equals(vo.getDefaultConfigName()) && count > 0){
                throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                        message.getMessage("hybrid.configMgnt.alias.conflict.message.exception", null, Locale.KOREA), HttpStatus.CONFLICT);
            }
        }
        if( vo != null ){
            vo.setIaasType(dto.getIaasType());
            vo.setDefaultConfigName(dto.getDefaultConfigName());
            vo.setDeploymentName(dto.getDeploymentName().trim());
            vo.setDirectorName(dto.getDirectorName().trim());
            vo.setBoshRelease(dto.getBoshRelease().trim());
            vo.setCredentialKeyName(dto.getCredentialKeyName());
            vo.setNtp(dto.getNtp());
            vo.setBoshCpiRelease(dto.getBoshCpiRelease());
            vo.setBoshBpmRelease(dto.getBoshBpmRelease());
            vo.setEnableSnapshots(dto.getEnableSnapshots());
            vo.setSnapshotSchedule(dto.getSnapshotSchedule());
            vo.setUpdateUserId(principal.getName());
            vo.setPaastaMonitoringUse(dto.getPaastaMonitoringUse());
            vo.setPaastaMonitoringRelease(dto.getPaastaMonitoringRelease());
            vo.setUaaRelease(dto.getUaaRelease());
            vo.setCredhubRelease(dto.getCredhubRelease());
            vo.setOsConfRelease(dto.getOsConfRelease());
            vo.setSyslogRelease(dto.getSyslogRelease());
            vo.setSyslogAddress(dto.getSyslogAddress());
            vo.setSyslogPort(dto.getSyslogPort());
            vo.setSyslogTransport(dto.getSyslogTransport());
            vo.setMetricUrl(dto.getMetrscUrl());
        }
        if( StringUtils.isEmpty(dto.getId()) ){
            bootstrapDefaultDao.insertBootstrapDefaultConfigInfo(vo);
        }else{
            bootstrapDefaultDao.updateBootstrapDefaultConfigInfo(vo);
        }
    }

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 기본 정보 삭제
     * @title : deleteDefaultConfigInfo
     * @return : void
    *****************************************************************/
    public void deleteDefaultConfigInfo(HbBootstrapDefaultConfigDTO dto, Principal principal) {
        if(dto.getId() == null || dto.getId().isEmpty()){
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        bootstrapDefaultDao.deleteBootstrapDefaultConfigInfo(dto);
        
    }
}
