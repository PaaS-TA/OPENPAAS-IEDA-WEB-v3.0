package org.openpaas.ieda.deploy.web.config.credential.service;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.web.config.credential.dao.CredentialManagementDAO;
import org.openpaas.ieda.deploy.web.config.credential.dao.CredentialManagementVO;
import org.openpaas.ieda.deploy.web.config.credential.dto.CredentialManagementDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CredentialManagementService {
    
    @Autowired private CredentialManagementDAO credentailManagementDAO;
    
    final private static String SEPARATOR = System.getProperty("file.separator");
    final private static String CREDENTIAL_DIR = LocalDirectoryConfiguration.getGenerateCredentialDir();
    final private static String MANIFEST_TEMPLATE_DIR = LocalDirectoryConfiguration.getManifastTemplateDir();
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 로컬의 인증서 파일 명과 비교하여 존재 하지 않는 인증서 파일 정보를 삭제 하고 나머지 디렉터 인증서 정보를 조회 한다.
     * @title : getDirectorCredentialList
     * @return : List<CredentailManagementVO>
    ***************************************************/
    public List<CredentialManagementVO> getDirectorCredentialList() {
        List<CredentialManagementVO> list = credentailManagementDAO.selectDirectorCredentialList();
        if(list != null && list.size() != 0){
            for(int i = 0; i < list.size(); i++){
                File directorCredentialFile = new File(CREDENTIAL_DIR + SEPARATOR + list.get(i).getCredentialKeyName() );
                if(!directorCredentialFile.exists() || directorCredentialFile.length() == 0){
                    String id = list.get(i).getId().toString();
                    credentailManagementDAO.deleteCredentialInfoById(id);
                }
            }
            list = credentailManagementDAO.selectDirectorCredentialList();
        }
        return list;
    }
    
    /***************************************************
     * @param principal 
     * @project : Paas 플랫폼 설치 자동화
     * @description : 디렉터 인증서 정보 저장
     * @title : saveDirectorCredential
     * @return : void
    ***************************************************/
    public void saveDirectorCredential(CredentialManagementDTO dto, Principal principal) {
        CredentialManagementVO vo = new CredentialManagementVO();
        int count = credentailManagementDAO.selectdirectorCredentialInfoByName(dto.getCredentialName());
        if( count > 0 ){
            throw new CommonException("conflict.credentialName.exception", "디렉터 인증서 명이 중복 되었습니다.", HttpStatus.CONFLICT);
        }else {
            String credentialKeyName = dto.getCredentialName() + "-cred.yml";
            makeCredentialFile(dto, credentialKeyName);
            vo.setCreateUserId(principal.getName());
            vo.setUpdateUserId(principal.getName());
            vo.setDirectorPrivateIp(dto.getDirectorPrivateIp());
            vo.setCredentialName(dto.getCredentialName());
            vo.setCredentialKeyName(credentialKeyName);
            vo.setDirectorPublicIp(dto.getDirectorPublicIp());
            credentailManagementDAO.saveDirectorCredentialInfo(vo);
        }
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 디렉터 인증서 파일 명과 디렉터 공인 아이피를 통한 Key File 생성
     * @title : makeCredentialFile
     * @return : void
    ***************************************************/
    public void makeCredentialFile(CredentialManagementDTO dto, String credentialKeyName) {
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
            cmd.add("--vars-store="+CREDENTIAL_DIR + SEPARATOR + credentialKeyName+"");
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
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 디렉터 인증서 정보 삭제
     * @title : deleteDirectorCredentialInfo
     * @return : void
    ***************************************************/
    public void deleteDirectorCredentialInfo(CredentialManagementDTO dto) {
        if(dto.getId() == null || "".equals(dto.getId())){
            throw new CommonException("notfound.credentialInfo.exception", "디렉터 인증서 정보가 존재 하지 않습니다.", HttpStatus.NOT_FOUND);
        }
        File file = new File(CREDENTIAL_DIR + SEPARATOR + dto.getCredentialName() + "-cred.yml");
        if(file.exists()){  // 입력 받은 디렉터 인증서 파일이 존재 할 경우 삭제
            file.delete();
        }
        credentailManagementDAO.deleteCredentialInfoById(dto.getId()); // 디렉터 인증서 정보 삭제
    }
}
