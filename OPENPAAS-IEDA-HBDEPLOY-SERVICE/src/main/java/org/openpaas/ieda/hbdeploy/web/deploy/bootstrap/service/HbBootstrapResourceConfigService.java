package org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.service;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import javax.transaction.Transactional;

import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapResourceConfigDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapResourceConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto.HbBootstrapResourceConfigDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
@Service
public class HbBootstrapResourceConfigService {
	
    @Autowired private MessageSource message;
    @Autowired private HbBootstrapResourceConfigDAO bootstrapResourceDao;
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CPI 목록 정보 조회
     * @title : getResourceConfigInfoList
     * @return : List<HbBootstrapResourceConfigVO>
    *****************************************************************/
    public List<HbBootstrapResourceConfigVO> getResourceConfigInfoList() {
        List<HbBootstrapResourceConfigVO> list = bootstrapResourceDao.selectBootstrapResourceConfigInfoList();
        return list;
    }

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Resource 정보 등록/수정
     * @title : saveResourceConfigInfo
     * @return : void
    *****************************************************************/
    @Transactional
    public void saveResourceConfigInfo(HbBootstrapResourceConfigDTO dto, Principal principal) {
        HbBootstrapResourceConfigVO vo = null;
        int count = bootstrapResourceDao.selectBootstrapResourceConfigByName(dto.getResourceConfigName());
        if(dto.getId() == null){
        //if( StringUtils.isEmpty(dto.getId().toString())){
            vo = new HbBootstrapResourceConfigVO();
            vo.setCreateUserId(principal.getName());
            if(count > 0){
                throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                        message.getMessage("hybrid.configMgnt.alias.conflict.message.exception", null, Locale.KOREA), HttpStatus.CONFLICT);
            }
        }else{
            vo = bootstrapResourceDao.selectBootstrapResourceConfigInfo(dto.getId(), dto.getIaasType().toLowerCase());
            if(!dto.getResourceConfigName().equals(vo.getResourceConfigName()) && count > 0){
                throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                        message.getMessage("hybrid.configMgnt.alias.conflict.message.exception", null, Locale.KOREA), HttpStatus.CONFLICT);
            }
        }
        if( vo != null ){
            vo.setResourceConfigName(dto.getResourceConfigName());
            vo.setIaasType(dto.getIaasType());
            vo.setStemcellName(dto.getStemcellName());
            vo.setInstanceType(dto.getInstanceType());
            vo.setVmPassword(dto.getVmPassword());
            vo.setCreateDate(vo.getCreateDate());
            vo.setUpdateUserId(principal.getName());
        }
        if( dto.getId() == null ){
        //if( StringUtils.isEmpty(dto.getId().toString())){
        	bootstrapResourceDao.insertBootStrapResourceConfigInfo(vo);
        }else{
        	bootstrapResourceDao.updateBootStrapResourceConfigInfo(vo);
        }
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 리소스 정보 삭제
     * @title : deleteResourceConfigInfo
     * @return : void
    *****************************************************************/
    public void deleteResourceConfigInfo(HbBootstrapResourceConfigDTO dto, Principal principal) {
        if(dto.getId()  == null || dto.getId().toString().isEmpty()){
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        bootstrapResourceDao.deleteBootStrapResourceConfigInfo(dto);
        
    }
}
