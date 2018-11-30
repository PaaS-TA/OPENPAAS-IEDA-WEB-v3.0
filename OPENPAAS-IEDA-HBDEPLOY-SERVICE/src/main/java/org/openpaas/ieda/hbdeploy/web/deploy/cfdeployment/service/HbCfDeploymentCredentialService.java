package org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.service;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.transaction.Transactional;

import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.web.common.dao.CommonDeployDAO;
import org.openpaas.ieda.deploy.web.common.dao.ManifestTemplateVO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentCredentialConfigDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao.HbCfDeploymentCredentialConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dto.HbCfDeploymentCredentialConfigDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class HbCfDeploymentCredentialService {
    
    @Autowired private MessageSource message;
    @Autowired private  HbCfDeploymentCredentialConfigDAO hbCfDeploymentCredentialConfigDao;
    @Autowired private CommonDeployDAO commonDao;
    
    final private static String HYBIRD_CREDENTIAL_DIR = LocalDirectoryConfiguration.getGenerateHybridCfCredentialDir();
    final private static String MANIFEST_TEMPLATE_DIR = LocalDirectoryConfiguration.getManifastTemplateDir();
    final private static String SEPARATOR = System.getProperty("file.separator");
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 인증서 정보 조회 
     * @title : getCredentialConfigInfoList
     * @return : List< HbCfDeploymentCredentialConfigVO>
    *****************************************************************/
    public List< HbCfDeploymentCredentialConfigVO> getCredentialConfigInfoList() {
        List< HbCfDeploymentCredentialConfigVO> list = hbCfDeploymentCredentialConfigDao.selectHbCfDeploymentCredentialConfigInfoList();
        return list;
    }

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 인증서 정보 등록/수정
     * @title : saveCredentialConfigInfo
     * @return : void
    *****************************************************************/
    @Transactional
    public void saveCredentialConfigInfo(HbCfDeploymentCredentialConfigDTO dto, Principal principal) {
        HbCfDeploymentCredentialConfigVO vo = null;
        int count = hbCfDeploymentCredentialConfigDao.selectHbCfDeploymentCredentialConfigByName(dto.getCredentialConfigName());
        if( StringUtils.isEmpty(dto.getId())){
        //if( StringUtils.isEmpty(dto.getId().toString())){
            vo = new HbCfDeploymentCredentialConfigVO();
            vo.setCreateUserId(principal.getName());
            if(count > 0){
                throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                        message.getMessage("hybrid.configMgnt.alias.conflict.message.exception", null, Locale.KOREA), HttpStatus.CONFLICT);
            }
        }else{
            vo = hbCfDeploymentCredentialConfigDao.selectHbCfDeploymentCredentialConfigInfo(dto.getId());
            if(!dto.getCredentialConfigName().equals(vo.getCredentialConfigName()) && count > 0){
                throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                        message.getMessage("hybrid.configMgnt.alias.conflict.message.exception", null, Locale.KOREA), HttpStatus.CONFLICT);
            }
        }
        ManifestTemplateVO mvo = null;
        if(dto != null){
            mvo = commonDao.selectManifetTemplate(dto.getIaasType(), dto.getReleaseVersion(), "CFDEPLOYMENT", dto.getReleaseName());
        }
        if( vo != null ){
            vo.setCredentialConfigName(dto.getCredentialConfigName());
            vo.setIaasType(dto.getIaasType());
            vo.setCompany(dto.getCompany());
            vo.setCountryCode(dto.getCountryCode());
            vo.setDomain(dto.getDomain());
            vo.setCity(dto.getCity());
            vo.setEmailAddress(dto.getEmailAddress());
            vo.setJobTitle(dto.getJobTitle());
            vo.setUpdateUserId(principal.getName());
            if(dto.getReleaseName().equalsIgnoreCase("paasta")){
                vo.setReleaseVersion(mvo.getTemplateVersion());
            }else{
                vo.setReleaseVersion(dto.getReleaseVersion());
            }
            vo.setReleaseName(dto.getReleaseName());
        }
        createKeyInfo(vo);
        if( dto.getId() == null ){
        //if( StringUtils.isEmpty(dto.getId().toString())){
            hbCfDeploymentCredentialConfigDao.insertHbCfDeploymentCredentialConfigInfo(vo);
        }else{
            hbCfDeploymentCredentialConfigDao.updateHbCfDeploymentCredentialConfigInfo(vo);
        }
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Key 생성
     * @title : createKeyInfo
     * @return : String
    *****************************************************************/
    public String createKeyInfo( HbCfDeploymentCredentialConfigVO vo){
        String keyFileName = "";
        String commonCredentialManifestPath = MANIFEST_TEMPLATE_DIR + "/cf-deployment/"+vo.getReleaseVersion()+"/common/cf-credential.yml";
        File cfCredentialFile = new File(HYBIRD_CREDENTIAL_DIR + SEPARATOR + vo.getCredentialConfigName()+ "-cred.yml");
        
        if(cfCredentialFile.exists()){
            cfCredentialFile.delete();
        }
        
        try {
            List<String> cmd = new ArrayList<String>();
            cmd.add("bosh");
            cmd.add("interpolate");
            cmd.add(commonCredentialManifestPath);
            cmd.add("-v");
            cmd.add("system_domain="+vo.getDomain()+"");
            cmd.add("--vars-store="+HYBIRD_CREDENTIAL_DIR + SEPARATOR + vo.getCredentialConfigName()+ "-cred.yml");
            ProcessBuilder builder = new ProcessBuilder(cmd);
            builder.redirectErrorStream(true);
            builder.start();
            keyFileName = vo.getCredentialConfigName()+"-cred.yml";
            vo.setCredentialConfigKeyFileName(keyFileName);
            Thread.sleep(10000);
        } catch (IOException e) {
            throw new CommonException("conflict.credentialName.exception", "인증서 파일 생성 중 에러가 발생 하였습니다.", HttpStatus.BAD_REQUEST);
        } catch (InterruptedException e) {
            throw new CommonException("Thread.interruptedException", "서버 실행 중 에러가 발생 했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return keyFileName;
    }
    
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 인증서 정보 삭제
     * @title : deleteCredentialConfigInfo
     * @return : void
    *****************************************************************/
    public void deleteCredentialConfigInfo(HbCfDeploymentCredentialConfigDTO dto, Principal principal){
        File file = new File(HYBIRD_CREDENTIAL_DIR + SEPARATOR + dto.getCredentialConfigName() + "-cred.yml");
        if(file.exists()){  // 입력 받은 디렉터 인증서 파일이 존재 할 경우 삭제
            file.delete();
        }
        if(dto.getId() == null){
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        hbCfDeploymentCredentialConfigDao.deleteHbCfDeploymentCredentialConfigInfo(dto);
    }
}
