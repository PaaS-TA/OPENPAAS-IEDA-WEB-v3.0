package org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.transaction.Transactional;

import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentInstanceConfigDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentInstanceConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dto.HbCfDeploymentInstanceConfigDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class HbCfDeploymentInstanceConfigService {
	
    @Autowired private MessageSource message;
    @Autowired private HbCfDeploymentInstanceConfigDAO hbCfDeploymentInstanceDao;

    
    public List< HbCfDeploymentInstanceConfigVO> getHbCfInstanceConfigInfoList() {
        List< HbCfDeploymentInstanceConfigVO> list = hbCfDeploymentInstanceDao.selectHbCfDeploymentInstanceConfigInfoList();
        return list;
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인프라 및 릴리즈 버전 별 job 목록 조회
     * @title : getHbCfJobTemplateList
     * @return : List<HashMap<String,String>>
    *****************************************************************/
    public List<HashMap<String, String>> getHbCfJobTemplateList(String releaseVersion, String deployType){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("releaseVersion", releaseVersion);
        map.put("deployType", deployType);
        List<HashMap<String, String>> list = hbCfDeploymentInstanceDao.selectHbCfJobTemplateByReleaseVersion(map);
        return list;
    }

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Instance 정보 등록/수정
     * @title : saveInstanceConfigInfo
     * @return : void
    *****************************************************************/
    @Transactional
    public void saveHbCfInstanceConfigInfo(HbCfDeploymentInstanceConfigDTO dto, Principal principal) {
        HbCfDeploymentInstanceConfigVO vo = new HbCfDeploymentInstanceConfigVO();
        int count = hbCfDeploymentInstanceDao.selectHbCfDeploymentInstanceConfigByName(dto.getInstanceConfigName());
        if(dto.getId() == null){
        //if( StringUtils.isEmpty(dto.getId().toString())){
            vo.setCreateUserId(principal.getName());
            if(count > 0){
                throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                        message.getMessage("hybrid.configMgnt.alias.conflict.message.exception", null, Locale.KOREA), HttpStatus.CONFLICT);
            }
        }else{
            vo = hbCfDeploymentInstanceDao.selectHbCfDeploymentInstanceConfigInfo(dto.getId());
            if(!dto.getInstanceConfigName().equals(vo.getInstanceConfigName()) && count > 0){
                throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                        message.getMessage("hybrid.configMgnt.alias.conflict.message.exception", null, Locale.KOREA), HttpStatus.CONFLICT);
            }
        }
        if( vo != null ){
            vo.setIaasType(dto.getIaasType());
            vo.setInstanceConfigName(dto.getInstanceConfigName());
            vo.setAdapter(dto.getAdapter());
            vo.setApi(dto.getApi());
            vo.setCcWorker(dto.getCcWorker());
            vo.setConsul(dto.getConsul());
            vo.setCfDeploymentName(dto.getCfDeploymentName());
            vo.setCfDeploymentVersion(dto.getCfDeploymentVersion());
            vo.setTheDatabase(dto.getTheDatabase());
            vo.setDiegoApi(dto.getDiegoApi());
            vo.setDiegoCell(dto.getDiegoCell());
            vo.setDoppler(dto.getDoppler());
            vo.setHaproxy(dto.getHaproxy());
            vo.setLogApi(dto.getLogApi());
            vo.setNats(dto.getNats());
            vo.setScheduler(dto.getScheduler());
            vo.setRouter(dto.getRouter());
            vo.setSingletonBlobstore(dto.getSingletonBlobstore());
            vo.setTcpRouter(dto.getTcpRouter());
            vo.setUaa(dto.getUaa());
            vo.setCreateDate(vo.getCreateDate());
            vo.setUpdateUserId(principal.getName());
        }
        if( dto.getId() == null ){
        //if( StringUtils.isEmpty(dto.getId().toString())){
        	hbCfDeploymentInstanceDao.insertHbCfDeploymentInstanceConfigInfo(vo);
        }else{
        	hbCfDeploymentInstanceDao.updateHbCfDeploymentInstanceConfigInfo(vo);
        }
    }

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Instance 정보 삭제
     * @title : deleteInstanceConfigInfo
     * @return : void
    *****************************************************************/
    public void deleteHbCfInstanceConfigInfo(HbCfDeploymentInstanceConfigDTO dto, Principal principal) {
        if(dto.getId()  == null || dto.getId().toString().isEmpty()){
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        hbCfDeploymentInstanceDao.deleteHbCfDeploymentInstanceConfigInfo(dto);
    }

}
