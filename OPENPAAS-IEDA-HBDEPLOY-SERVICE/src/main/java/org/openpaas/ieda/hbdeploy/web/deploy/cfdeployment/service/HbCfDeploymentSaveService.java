package org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service;

import java.io.File;
import java.security.Principal;
import java.util.Locale;

import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentVO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dto.HbCfDeploymentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class HbCfDeploymentSaveService {

    @Autowired private MessageSource message;
    @Autowired private HbCfDeploymentDAO hbCfDeploymentDao;
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CF Deployment 정보 등록/수정
     * @title : saveCfdeploymentConfigInfo
     * @return : void
    *****************************************************************/
    public void saveCfdeploymentConfigInfo(HbCfDeploymentDTO dto, Principal principal){
        HbCfDeploymentVO vo = null;
        int count = hbCfDeploymentDao.selectCfDeploymentConfigByName(dto.getCfDeploymentConfigName());
        if(StringUtils.isEmpty(dto.getId())){
            vo = new HbCfDeploymentVO();
            vo.setCreateUserId(principal.getName());
            if(count > 0){
                throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                        message.getMessage("hybrid.configMgnt.alias.conflict.message.exception", null, Locale.KOREA), HttpStatus.CONFLICT);
            }
        }else{
            vo = hbCfDeploymentDao.selectCfDeploymentConfigInfo(dto.getId());
            
            if(!dto.getCfDeploymentConfigName().equals(vo.getCfDeploymentConfigName()) && count > 0){
                throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                        message.getMessage("hybrid.configMgnt.alias.conflict.message.exception", null, Locale.KOREA), HttpStatus.CONFLICT);
            }
        }
        
        if(vo != null){
            vo.setIaasType(dto.getIaasType());
            vo.setCfDeploymentConfigName(dto.getCfDeploymentConfigName());
            vo.setDefaultConfigInfo(dto.getDefaultConfigInfo());
            vo.setCredentialConfigInfo(dto.getCredentialConfigInfo());
            vo.setNetworkConfigInfo(dto.getNetworkConfigInfo());
            vo.setResourceConfigInfo(dto.getResourceConfigInfo());
            vo.setInstanceConfigInfo(dto.getInstanceConfigInfo());
            vo.setCreateUserId(vo.getCreateUserId());
            vo.setUpdateUserId(principal.getName());
            vo.setCloudConfigFile(setCloudConifgFileName(vo));
        }
        
        if(StringUtils.isEmpty(dto.getId())){
            hbCfDeploymentDao.insertCfDeploymentConfigInfo(vo);
        }else {
            hbCfDeploymentDao.updateCfDeploymentConfigInfo(vo);
        }
        
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : cloud config 파일 명 추출
     * @title : setCloudConifgFileName
     * @return : void
    *****************************************************************/
   private String setCloudConifgFileName(HbCfDeploymentVO vo) {
      String settingFileName = "";
      if(vo.getIaasType() != null || vo.getId() != null){
          settingFileName = vo.getIaasType().toLowerCase() + "-hybrid-cf-"+ vo.getCfDeploymentConfigName() +"-cloud-config.yml";
          File file = new File(settingFileName);
          if(file.exists()){
              file.delete();
          }
      }else {
          throw new CommonException(setMessageSourceValue("common.badRequest.exception.code"), 
                  setMessageSourceValue("common.badRequest.message"), HttpStatus.BAD_REQUEST);
      }
      return settingFileName;
   }

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : message 값 가져오기
     * @title : setMessageSourceValue
     * @return : String
    *****************************************************************/
    public String setMessageSourceValue(String name){
        return message.getMessage(name, null, Locale.KOREA);
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Cf Deployment 단순 레코드 삭제
     * @title : deleteCfdeploymentConfigInfo
     * @return : void
    *****************************************************************/
    public void deleteCfdeploymentConfigInfo(HbCfDeploymentDTO dto, Principal principal) {
        if(dto.getId() != null){
        	hbCfDeploymentDao.deleteCfDeploymentConfigInfo(dto);
        }else {
            throw new CommonException(setMessageSourceValue("common.badRequest.exception.code"), 
                    setMessageSourceValue("common.badRequest.message"), HttpStatus.BAD_REQUEST);
        }
    }
}
