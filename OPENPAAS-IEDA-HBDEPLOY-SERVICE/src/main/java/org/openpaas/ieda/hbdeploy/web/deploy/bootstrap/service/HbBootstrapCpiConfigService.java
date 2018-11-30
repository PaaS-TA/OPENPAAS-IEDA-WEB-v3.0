package org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.service;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import javax.transaction.Transactional;

import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapCpiConfigDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapCpiConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto.HbBootstrapCpiConfigDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class HbBootstrapCpiConfigService {
    
    @Autowired private MessageSource message;
    @Autowired private HbBootstrapCpiConfigDAO bootstrapCpiDao;
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CPI 목록 정보 조회
     * @title : getCpiConfigInfoList
     * @return : List<HbBootstrapCpiConfigVO>
    *****************************************************************/
    public List<HbBootstrapCpiConfigVO> getCpiConfigInfoList() {
        List<HbBootstrapCpiConfigVO> list = bootstrapCpiDao.selectBootstrapCpiConfigInfoList();
        return list;
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CPI 정보 등록/수정
     * @title : saveCpiInfo
     * @return : HbBootstrapCpiVO
    *****************************************************************/
    @Transactional
    public HbBootstrapCpiConfigVO saveCpiConfigInfo(HbBootstrapCpiConfigDTO dto, Principal principal) {
        HbBootstrapCpiConfigVO vo = null;
        int count = bootstrapCpiDao.selectBootstrapCpiConfigByName(dto.getCpiName());
        if( StringUtils.isEmpty(dto.getCpiInfoId())){
            vo = new HbBootstrapCpiConfigVO();
            vo.setCreateUserId(principal.getName());
            if(count > 0){
                throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                        message.getMessage("hybrid.configMgnt.alias.conflict.message.exception", null, Locale.KOREA), HttpStatus.CONFLICT);
            }
        }else{
            vo = bootstrapCpiDao.selectBootstrapCpiConfigInfo(Integer.parseInt(dto.getCpiInfoId()), dto.getIaasType().toLowerCase());
            if(!dto.getCpiName().equals(vo.getCpiName()) && count > 0){
                throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                        message.getMessage("hybrid.configMgnt.alias.conflict.message.exception", null, Locale.KOREA), HttpStatus.CONFLICT);
            }
        }
        if( vo != null ){
            vo.setIaasType(dto.getIaasType());
            vo.setCpiName(dto.getCpiName());
            vo.setIaasConfigId(Integer.parseInt(dto.getIaasConfigId()));
            vo.setIaasType(dto.getIaasType());
            vo.setUpdateUserId(principal.getName());
        }
        if( StringUtils.isEmpty(dto.getCpiInfoId()) ){
            bootstrapCpiDao.insertBootStrapCpiConfigInfo(vo);
        }else{
            bootstrapCpiDao.updateBootStrapCpiConfigInfo(vo);
        }
        return vo;
    }

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CPI 정보 삭제
     * @title : deleteCpiConfigInfo
     * @return : void
    *****************************************************************/
    public void deleteCpiConfigInfo(HbBootstrapCpiConfigDTO dto, Principal principal) {
        if(dto.getCpiInfoId() == null || dto.getCpiInfoId().isEmpty()){
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        bootstrapCpiDao.deleteBootStrapCpiConfigInfo(dto);
        
    }
}
