package org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import javax.transaction.Transactional;

import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentDefaultConfigDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentDefaultConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dto.HbCfDeploymentDefaultConfigDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class HbCfDeploymentDefaultConfigService {
	@Autowired private MessageSource message;
    @Autowired private  HbCfDeploymentDefaultConfigDAO cfDeploymentResourceDao;

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Default 정보 조회 
     * @title : getDefaultConfigInfoList
     * @return : List< HbCfDeploymentDefaultConfigVO>
    *****************************************************************/
    public List< HbCfDeploymentDefaultConfigVO> getDefaultConfigInfoList() {
        List< HbCfDeploymentDefaultConfigVO> list = cfDeploymentResourceDao.selectHbCfDeploymentDefaultConfigInfoList();
        return list;
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Default 정보 등록/수정
     * @title : saveDefaultConfigInfo
     * @return : void
    *****************************************************************/
    @Transactional
    public void saveDefaultConfigInfo(HbCfDeploymentDefaultConfigDTO dto, Principal principal) {
        HbCfDeploymentDefaultConfigVO vo = null;
        int count = cfDeploymentResourceDao.selectHbCfDeploymentDefaultConfigByName(dto.getDefaultConfigName());
        if(dto.getId() == null){
        //if( StringUtils.isEmpty(dto.getId().toString())){
            vo = new HbCfDeploymentDefaultConfigVO();
            vo.setCreateUserId(principal.getName());
            if(count > 0){
                throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                        message.getMessage("hybrid.configMgnt.alias.conflict.message.exception", null, Locale.KOREA), HttpStatus.CONFLICT);
            }
        }else{
            vo = cfDeploymentResourceDao.selectHbCfDeploymentDefaultConfigInfo(dto.getId(), dto.getIaasType().toLowerCase());
            if(!dto.getDefaultConfigName().equals(vo.getDefaultConfigName()) && count > 0){
                throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                        message.getMessage("hybrid.configMgnt.alias.conflict.message.exception", null, Locale.KOREA), HttpStatus.CONFLICT);
            }
        }
        if( vo != null ){
            vo.setIaasType(dto.getIaasType());
            vo.setDefaultConfigName(dto.getDefaultConfigName());
            vo.setDomain(dto.getDomain());
            vo.setDomainOrganization(dto.getDomainOrganization());
            vo.setCfDeploymentVersion(dto.getCfDeploymentVersion());
            vo.setCfDbType(dto.getCfDbType());
            vo.setInceptionOsUserName(dto.getInceptionOsUserName());
            vo.setCfAdminPassword(dto.getCfAdminPassword());
            vo.setPortalDomain(dto.getPortalDomain());
            vo.setMetricUrl(dto.getMetricUrl());
            vo.setSyslogAddress(dto.getSyslogAddress());
            vo.setSyslogPort(dto.getSyslogPort());
            vo.setSyslogCustomRule(dto.getSyslogCustomRule());
            vo.setSyslogFallbackServers(dto.getSyslogFallbackServers());
            vo.setPaastaMonitoringUse(dto.getPaastaMonitoringUse());
            vo.setCreateDate(vo.getCreateDate());
            vo.setUpdateUserId(principal.getName());
        }
        if( dto.getId() == null ){
        //if( StringUtils.isEmpty(dto.getId().toString())){
            cfDeploymentResourceDao.insertHbCfDeploymentDefaultConfigInfo(vo);
        }else{
        	cfDeploymentResourceDao.updateHbCfDeploymentDefaultConfigInfo(vo);
        }
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Default 정보 삭제
     * @title : deleteDefaultConfigInfo
     * @return : void
    *****************************************************************/
    public void deleteDefaultConfigInfo(HbCfDeploymentDefaultConfigDTO dto, Principal principal) {
        if(dto.getId()  == null || dto.getId().toString().isEmpty()){
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        cfDeploymentResourceDao.deleteHbCfDeploymentDefaultConfigInfo(dto);
    }
}
