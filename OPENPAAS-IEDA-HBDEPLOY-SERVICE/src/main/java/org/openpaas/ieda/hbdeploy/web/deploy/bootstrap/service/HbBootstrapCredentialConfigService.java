package org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.service;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.transaction.Transactional;

import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapCredentialConfigDAO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao.HbBootstrapCredentialConfigVO;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto.HbBootstrapCredentialConfigDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class HbBootstrapCredentialConfigService {
    
    @Autowired private MessageSource message;
    @Autowired private HbBootstrapCredentialConfigDAO bootstrapCredentialConfigDao;
    
    final private static String SEPARATOR = System.getProperty("file.separator");
    final private static String HYBRID_CREDENTIAL_DIR = LocalDirectoryConfiguration.getGenerateHybridCredentialDir();
    final private static String MANIFEST_TEMPLATE_DIR = LocalDirectoryConfiguration.getManifastTemplateDir();
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 디렉터 인증서 정보 목록 조회
     * @title : getCredentialConfigInfoList
     * @return : List<HbBootstrapDefaultConfigVO> 
    *****************************************************************/
    public List<HbBootstrapCredentialConfigVO> getHbBootstrapCredentialConfigInfoList() {
        HbBootstrapCredentialConfigDTO dto = null;
        List<HbBootstrapCredentialConfigVO> list = bootstrapCredentialConfigDao.selectBootstrapCredentialConfigList();
        if(list != null && list.size() != 0){
            for(int i = 0; i < list.size(); i++){
                File directorCredentialFile = new File(HYBRID_CREDENTIAL_DIR + SEPARATOR + list.get(i).getCredentialKeyName() );
                if(!directorCredentialFile.exists() || directorCredentialFile.length() == 0){
                    String id = list.get(i).getId().toString();
                    dto = new HbBootstrapCredentialConfigDTO();
                    dto.setId(id);
                    bootstrapCredentialConfigDao.deleteBootstrapCredentialConfig(dto);
                }
            }
            list = bootstrapCredentialConfigDao.selectBootstrapCredentialConfigList();
        }
        return list;
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 디렉터 인증서 정보 등록/수정
     * @title : saveCredentialConfigInfo
     * @return : void
    *****************************************************************/
    @Transactional
    public void saveHbBootstrapCredentialConfigInfo(HbBootstrapCredentialConfigDTO dto, Principal principal) {
        File file = new File(HYBRID_CREDENTIAL_DIR + SEPARATOR + dto.getCredentialConfigName() + "-cred.yml");
        if(file.exists()){  // 입력 받은 디렉터 인증서 파일이 존재 할 경우 삭제
            file.delete();
        }
        HbBootstrapCredentialConfigVO vo = null;
        int count = bootstrapCredentialConfigDao.selectBootstrapCredentialConfigByName(dto.getCredentialConfigName());
        if( StringUtils.isEmpty(dto.getId())){
            vo = new HbBootstrapCredentialConfigVO();
            vo.setCreateUserId(principal.getName());
            if(count > 0){
                throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                        message.getMessage("hybrid.configMgnt.alias.conflict.message.exception", null, Locale.KOREA), HttpStatus.CONFLICT);
            }
        }else{
            vo = bootstrapCredentialConfigDao.selectBootstrapCredentialConfigInfo(Integer.parseInt(dto.getId()), dto.getIaasType().toLowerCase());
            if(!dto.getCredentialConfigName().equals(vo.getCredentialConfigName()) && count > 0){
                throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                        message.getMessage("hybrid.configMgnt.alias.conflict.message.exception", null, Locale.KOREA), HttpStatus.CONFLICT);
            }
        }
        if( vo != null ){
            String credentialKeyName = dto.getCredentialConfigName() + "-cred.yml";
            makeCredentialFile(dto, credentialKeyName);
            vo.setIaasType(dto.getIaasType());
            vo.setNetworkConfigName(dto.getNetworkConfigName());
            vo.setUpdateUserId(principal.getName());
            vo.setDirectorPrivateIp(dto.getDirectorPrivateIp());
            vo.setDirectorPublicIp(dto.getDirectorPublicIp());
            vo.setCredentialConfigName(dto.getCredentialConfigName());
            vo.setCredentialKeyName(credentialKeyName);
        }
        if( StringUtils.isEmpty(dto.getId()) ){
            bootstrapCredentialConfigDao.insertBootstrapCredentialConfigInfo(vo);
        }else{
            bootstrapCredentialConfigDao.updateBootstrapCredentialConfigInfo(vo);
        }
    }
    
    
    /***************************************************
     * @project : 이종 Paas 플랫폼 설치 자동화
     * @description : 디렉터 인증서 파일 명과 디렉터 공인 아이피를 통한 Key File 생성
     * @title : makeCredentialFile
     * @return : void
    ***************************************************/
    public void makeCredentialFile(HbBootstrapCredentialConfigDTO dto, String credentialKeyName) {
        String commonCredentialManifestPath = MANIFEST_TEMPLATE_DIR + "/bootstrap/credential/director-credential.yml";
        if(StringUtils.isEmpty(dto.getDirectorPublicIp()) || dto.getDirectorPublicIp() == null){
            dto.setDirectorPublicIp(dto.getDirectorPrivateIp());
        }
        try {
            List<String> cmd = new ArrayList<String>(); //bosh 명령어 실행 줄
            cmd.add("bosh");
            cmd.add("interpolate");
            cmd.add(commonCredentialManifestPath);
            cmd.add("-v");
            cmd.add("publicStaticIp="+dto.getDirectorPublicIp()+"");
            cmd.add("-v");
            cmd.add("privateStaticIp="+dto.getDirectorPrivateIp()+"");
            cmd.add("--vars-store="+HYBRID_CREDENTIAL_DIR + SEPARATOR + credentialKeyName+"");
            ProcessBuilder builder = new ProcessBuilder(cmd);
            builder.redirectErrorStream(true);
            builder.start();
            Thread.sleep(10000);
        } catch (IOException e) {
            throw new CommonException("conflict.credentialName.exception", "디렉터 인증서 파일 생성 중 에러가 발생 하였습니다.", HttpStatus.BAD_REQUEST);
        } catch (InterruptedException e) {
            throw new CommonException("Thread.interruptedException", "서버 실행 중 에러가 발생 했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 디렉터 인증서 정보 삭제
     * @title : deleteCredentialConfigInfo
     * @return : void
    *****************************************************************/
    public void deleteHbBootstrapCredentialConfigInfo(HbBootstrapCredentialConfigDTO dto, Principal principal) {
        File file = new File(HYBRID_CREDENTIAL_DIR + SEPARATOR + dto.getCredentialConfigName() + "-cred.yml");
        if(file.exists()){  // 입력 받은 디렉터 인증서 파일이 존재 할 경우 삭제
            file.delete();
        }
        if(dto.getId() == null || dto.getId().isEmpty()){
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        bootstrapCredentialConfigDao.deleteBootstrapCredentialConfig(dto);
        
    }
}
