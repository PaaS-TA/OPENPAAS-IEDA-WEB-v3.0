package org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.service;

import java.security.Principal;
import java.util.Locale;

import org.hsqldb.lib.StringUtil;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapVO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto.HbBootStrapDeployDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class HbBootstrapSaveService {

    @Autowired private MessageSource message;
    @Autowired private HbBootstrapDAO bootstrapDao;
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 배포 파일명 생성
     * @title : saveBootstrapInfo
     * @return : void
    *****************************************************************/
    public void saveBootstrapInfo(HbBootStrapDeployDTO dto, Principal principal) {
        int count = bootstrapDao.selectBootstrapConfigByName(dto.getBootstrapConfigName());
        HbBootstrapVO vo = null;
        if( StringUtils.isEmpty(dto.getId())){
            vo = new HbBootstrapVO();
            vo.setIaasType(dto.getIaasType());
            vo.setCreateUserId(principal.getName());
            if(count > 0){
                throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                        message.getMessage("hybrid.configMgnt.alias.conflict.message.exception", null, Locale.KOREA), HttpStatus.CONFLICT);
            }
        }else{
            vo = bootstrapDao.selectBootstrapConfigInfo(Integer.parseInt(dto.getId()), dto.getIaasType().toLowerCase());
            if(!dto.getBootstrapConfigName().equals(vo.getBootstrapConfigName()) && count > 0){
                throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                        message.getMessage("hybrid.configMgnt.alias.conflict.message.exception", null, Locale.KOREA), HttpStatus.CONFLICT);
            }
        }
        if( vo != null ){
            if( StringUtil.isEmpty(vo.getDeploymentFile()) ){
                vo.setDeploymentFile(makeDeploymentName(dto));
            } else {
                vo.setBootstrapConfigName(dto.getDeploymentFile());
            }
            vo.setBootstrapConfigName(dto.getBootstrapConfigName());
            vo.setNetworkConfigInfo(dto.getNetworkConfigInfo());
            vo.setCpiConfigInfo(dto.getCpiConfigInfo());
            vo.setDefaultConfigInfo(dto.getDefaultConfigInfo());
            vo.setResourceConfigInfo(dto.getResourceConfigInfo());
            vo.setIaasType(dto.getIaasType());
            vo.setUpdateUserId(principal.getName());
        }
        if( StringUtils.isEmpty(dto.getId()) ){
            bootstrapDao.insertBootStrapConfigInfo(vo);
        }else{
            bootstrapDao.updateBootStrapConfigInfo(vo);
        }
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 배포 파일명 생성
     * @title : makeDeploymentName
     * @return : String
    *****************************************************************/
    public String makeDeploymentName(HbBootStrapDeployDTO dto ){
        String settingFileName = "";
        if( !StringUtils.isEmpty(dto.getIaasType()) || !StringUtils.isEmpty(dto.getId()) ){
            settingFileName = dto.getIaasType().toLowerCase() + "-hybrid-microbosh-"+ dto.getBootstrapConfigName() +".yml";
        }else{
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        return settingFileName;
    }
}