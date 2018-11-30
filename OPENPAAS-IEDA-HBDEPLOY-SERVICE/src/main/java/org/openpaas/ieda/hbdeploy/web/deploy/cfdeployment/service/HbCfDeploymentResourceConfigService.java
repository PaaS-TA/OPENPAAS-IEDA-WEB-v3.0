package org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import javax.transaction.Transactional;

import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentResourceConfigDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentResourceConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dto.HbCfDeploymentResourceConfigDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class HbCfDeploymentResourceConfigService {
    @Autowired private MessageSource message;
    @Autowired private  HbCfDeploymentResourceConfigDAO cfDeploymentResourceDao;
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Resource 정보 목록 조회
     * @title : getResourceConfigInfoList
     * @return : List< HbCfDeploymentResourceConfigVO>
    *****************************************************************/
    public List< HbCfDeploymentResourceConfigVO> getResourceConfigInfoList() {
        List< HbCfDeploymentResourceConfigVO> list = cfDeploymentResourceDao.selectCfDeploymentResourceConfigInfoList();
        return list;
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Resource 정보 등록/수정
     * @title : saveResourceConfigInfo
     * @return : void
    *****************************************************************/
    @Transactional
    public void saveResourceConfigInfo(HbCfDeploymentResourceConfigDTO dto, Principal principal) {
        HbCfDeploymentResourceConfigVO vo = null;
        int count = cfDeploymentResourceDao.selectCfDeploymentResourceConfigByName(dto.getResourceConfigName());
        if(dto.getId() == null){
            vo = new HbCfDeploymentResourceConfigVO();
            vo.setCreateUserId(principal.getName());
            if(count > 0){
                throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                        message.getMessage("hybrid.configMgnt.alias.conflict.message.exception", null, Locale.KOREA), HttpStatus.CONFLICT);
            }
        }else{
            vo = cfDeploymentResourceDao.selectCfDeploymentResourceConfigInfo(dto.getId(), dto.getIaasType().toLowerCase());
            if(!dto.getResourceConfigName().equals(vo.getResourceConfigName()) && count > 0){
                throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                        message.getMessage("hybrid.configMgnt.alias.conflict.message.exception", null, Locale.KOREA), HttpStatus.CONFLICT);
            }
        }
        if( vo != null ){
            vo.setResourceConfigName(dto.getResourceConfigName());
            vo.setIaasType(dto.getIaasType());
            vo.setStemcellName(dto.getStemcellName());
            vo.setStemcellVersion(dto.getStemcellVersion());
            vo.setInstanceTypeS(dto.getInstanceTypeS());
            vo.setInstanceTypeM(dto.getInstanceTypeM());
            vo.setInstanceTypeL(dto.getInstanceTypeL());
            vo.setDirectorInfo(dto.getDirectorInfo());
            vo.setUpdateUserId(principal.getName());
        }
        if( dto.getId() == null ){
            cfDeploymentResourceDao.insertCfDeploymentResourceConfigInfo(vo);
        }else{
            cfDeploymentResourceDao.updateCfDeploymentResourceConfigInfo(vo);
        }
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 리소스 정보 삭제
     * @title : deleteResourceConfigInfo
     * @return : void
    *****************************************************************/
    public void deleteResourceConfigInfo(HbCfDeploymentResourceConfigDTO dto, Principal principal) {
        if(dto.getId()  == null || dto.getId().toString().isEmpty()){
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        cfDeploymentResourceDao.deleteCfDeploymentResourceConfigInfo(dto);
    }
}