
package org.openpaas.ieda.deploy.web.common.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.web.common.dao.CommonDeployDAO;
import org.openpaas.ieda.deploy.web.common.dao.ManifestTemplateVO;
import org.openpaas.ieda.deploy.web.common.dto.KeyInfoDTO;
import org.openpaas.ieda.deploy.web.deploy.cf.dao.CfDAO;
import org.openpaas.ieda.deploy.web.deploy.cf.dao.CfVO;
import org.openpaas.ieda.deploy.web.information.iassConfig.dao.IaasConfigMgntDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.yaml.snakeyaml.Yaml;

@Service
public class CommonDeployService{
    
    @Autowired CfDAO cfDao;
    @Autowired CommonDeployDAO commonDao;
    @Autowired IaasConfigMgntDAO iaasConfigDao;
    @Autowired MessageSource message;
    
    final private static String SEPARATOR = System.getProperty("file.separator");
    final private static String SSH_DIR = LocalDirectoryConfiguration.getSshDir();
    final private static String KEY_DIR = LocalDirectoryConfiguration.getKeyDir();
    final private static String DEPLOYMENT_DIR = LocalDirectoryConfiguration.getDeploymentDir();
    final private static String HYBRID_CREDENTIAL_DIR = LocalDirectoryConfiguration.getGenerateHybridCredentialDir();
    final private static String LOCK_DIR = LocalDirectoryConfiguration.getLockDir() + SEPARATOR;
    final private static String CREDENTIAL_DIR = LocalDirectoryConfiguration.getGenerateCredentialDir();
    final private static Logger LOGGER = LoggerFactory.getLogger(CommonDeployService.class);
    final private static String CF_CREDENTIAL_DIR = LocalDirectoryConfiguration.getGenerateCfDeploymentCredentialDir();
    final private static String HYBRID_CF_CREDENTIAL_DIR = LocalDirectoryConfiguration.getGenerateHybridCfCredentialDir();
    final private static String MANIFEST_TEMPLATE_DIR = LocalDirectoryConfiguration.getManifastTemplateDir();
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Private Key 파일을 업로드하고 권한을 설정
     * @title : uploadKeyFile
     * @return : void
    *****************************************************************/
    public void uploadKeyFile(MultipartHttpServletRequest request) {
        Iterator<String> itr =  request.getFileNames();
        File keyPathFile = new File(SSH_DIR);
        if (!keyPathFile.isDirectory()){
            boolean result = keyPathFile.mkdir();
            LOGGER.debug("key path file directory create :: " + result);
        }
        if(itr.hasNext()) {
            BufferedOutputStream stream = null;
            MultipartFile mpf = request.getFile(itr.next());
            try {
                String keyFilePath = SSH_DIR + SEPARATOR + mpf.getOriginalFilename();
                byte[] bytes = mpf.getBytes();
                File isKeyFile = new File(keyFilePath);
                stream = new BufferedOutputStream(new FileOutputStream(isKeyFile));
                stream.write(bytes);
                
                boolean result = isKeyFile.setWritable(false, false);
                if(LOGGER.isDebugEnabled()){
                    LOGGER.debug("isKeyFile.setWritable : " + result);
                }
                isKeyFile.setExecutable(false, false);
                isKeyFile.setReadable(false, true);
                Set<PosixFilePermission> pfp = new HashSet<PosixFilePermission>();
                pfp.add(PosixFilePermission.OWNER_READ);
                Files.setPosixFilePermissions(Paths.get(keyFilePath), pfp);
                
            } catch (IOException e) {
            	e.printStackTrace();
                throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                        message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
            } finally {
                try {
                    if( stream != null ) {
                        stream.close();
                    }
                } catch (IOException e) {
                    throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                            message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 로컬에서 Private Key 파일(.pem)  정보 목록 조회
     * @title : getKeyFileList
     * @return : List<String>
    *****************************************************************/
    public List<String> getKeyFileList(String iaasType){
        File keyPathFile = new File(SSH_DIR);
        if ( !keyPathFile.isDirectory() ) {
            return null;
        }
        List<String> localFiles = null;
        File[] listFiles = keyPathFile.listFiles();
        if(listFiles != null){
            for (File file : listFiles) {
                if ( localFiles == null ){
                    localFiles = new ArrayList<String>();
                }
                localFiles.add(file.getName());
            }
        }
        return localFiles;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  Credential File 목록 조회
     * @title : getCredentialName
     * @return : List<String>
    *****************************************************************/
    public List<String> getCredentialName (){
        File credsKeyFile = new File(CREDENTIAL_DIR);
        if(!credsKeyFile.isDirectory()){
            return null;
        }
        List<String> localFiles = null;
        File[] credsListFiles = credsKeyFile.listFiles();
        if(credsKeyFile != null){
            for (File file : credsListFiles) {
                if ( localFiles == null ){
                    localFiles = new ArrayList<String>();
                }
                localFiles.add(file.getName());
            }
        }
        return localFiles;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  Credential File 목록 조회
     * @title : getCredentialName
     * @return : List<String>
    *****************************************************************/
    public List<String> getHybridCredentialName (){
        File credsKeyFile = new File(HYBRID_CREDENTIAL_DIR);
        if(!credsKeyFile.isDirectory()){
            return null;
        }
        List<String> localFiles = null;
        File[] credsListFiles = credsKeyFile.listFiles();
        if(credsKeyFile != null){
            for (File file : credsListFiles) {
                if ( localFiles == null ){
                    localFiles = new ArrayList<String>();
                }
                localFiles.add(file.getName());
            }
        }
        return localFiles;
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 배포파일 정보 조회
     * @title : getDeploymentInfo
     * @return : String
    *****************************************************************/
    public String getDeploymentInfo(String deploymentFile) {
        String contents = "";
        File settingFile = null;
        try {
            settingFile = new File(DEPLOYMENT_DIR + SEPARATOR + deploymentFile);
            if( settingFile.exists() ){
                contents = IOUtils.toString(new FileInputStream(settingFile));
            }
        } catch (FileNotFoundException e) {
            throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                    message.getMessage("common.file.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                    message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return contents;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Lock 파일 설정
     * @title : lockFileSet
     * @return : Boolean
    *****************************************************************/
    public Boolean lockFileSet(String lockFileName){
        File lockFile = new File(LOCK_DIR + lockFileName+".lock");
        Writer createLockFile =null;
        Boolean flag = null;
        try{
            if(!lockFile.exists()){
                flag= true;
                createLockFile = new OutputStreamWriter(new FileOutputStream(lockFile), "UTF-8");
            }else {
                flag= false;
            }
        }catch(IOException e){
            throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                    message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
        }finally {
            try {
                if(createLockFile != null) {
                    createLockFile.close();
                }
            } catch (IOException e) {
                throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                        message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return flag;
    }
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Key 생성
     * @title : createKeyInfo
     * @return : String
    *****************************************************************/
    public String createKeyInfo( KeyInfoDTO dto, Principal principal){
        String keyFileName = "";
        String commonCredentialManifestPath = MANIFEST_TEMPLATE_DIR + "/cf-deployment/"+dto.getVersion()+"/common/cf-credential.yml";
        File cfCredentialFile = new File(CF_CREDENTIAL_DIR + SEPARATOR + dto.getDomain()+ "-cred.yml");
        if(cfCredentialFile.exists()){
            cfCredentialFile.delete();
        }
        try {
            List<String> cmd = new ArrayList<String>();
            cmd.add("bosh");
            cmd.add("interpolate");
            cmd.add(commonCredentialManifestPath);
            cmd.add("-v");
            cmd.add("system_domain="+dto.getDomain()+"");
            cmd.add("--vars-store="+CF_CREDENTIAL_DIR + SEPARATOR + dto.getDomain()+ "-cred.yml");
            ProcessBuilder builder = new ProcessBuilder(cmd);
            builder.redirectErrorStream(true);
            builder.start();
            keyFileName = dto.getDomain()+"-cred.yml";
            Thread.sleep(10000);
            
            saveKeyFileName(dto, keyFileName, principal);
            
        } catch (IOException e) {
            throw new CommonException("conflict.credentialName.exception", "인증서 파일 생성 중 에러가 발생 하였습니다.", HttpStatus.BAD_REQUEST);
        } catch (InterruptedException e) {
            throw new CommonException("Thread.interruptedException", "서버 실행 중 에러가 발생 했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return keyFileName;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 키 파일명 저장
     * @title : saveKeyFileName
     * @return : void
    ***************************************************/
    public void saveKeyFileName( KeyInfoDTO dto, String keyFileName, Principal principal ){
        CfVO cfVo = cfDao.selectCfInfoById( Integer.parseInt(dto.getId()) );
        cfVo.setKeyFile(keyFileName);
        cfVo.setUpdateUserId(principal.getName());
        cfDao.updateCfInfo(cfVo);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 해당 key 파일에서 SSH 핑거프린트 조회
     * @title : getFingerprint
     * @return : String
    *****************************************************************/
    @SuppressWarnings("unchecked")
    public String getFingerprint(String keyFileName){
        FileInputStream fis = null;
        BufferedReader rd = null;
        String fingerprint = "";
        Yaml yaml = new Yaml();
        try{
            String keyFile = KEY_DIR + SEPARATOR + keyFileName;
            fis = new FileInputStream(new File(keyFile));
            rd = new BufferedReader(new InputStreamReader(fis,"UTF-8"));
            String line = null;
            String contents = "";
            while((line = rd.readLine()) != null) {
                contents += line + "\n";
            }
            Map<String, Object> object = (Map<String, Object>) yaml.load(contents);
            Map<String, String> certMap = (Map<String,String>)object.get("diego-certs");
            fingerprint = certMap.get("ssh-key-fingerprint");
            if( StringUtils.isEmpty(fingerprint)  || fingerprint == null){
                throw new CommonException(getMessageValue("common.badRequest.exception.code"), 
                        keyFileName  +" 파일에 SSH 핑거프린트가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
            }
        }catch(IOException e){
            throw new CommonException(getMessageValue("common.internalServerError.exception.code"), 
                    getMessageValue("common.file.internalServerError.message"), HttpStatus.INTERNAL_SERVER_ERROR);
        }catch(Exception e){
            throw new CommonException(getMessageValue("common.notFound.exception.code"), 
                    getMessageValue("common.notFound.template.message"), HttpStatus.NOT_FOUND);
        } finally {
            try {
                 if (rd != null) {
                     rd.close();
                 }
                 if (fis != null) {
                     fis.close();
                 }
            }catch (IOException e) {
                throw new CommonException(getMessageValue("common.internalServerError.exception.code"),
                        getMessageValue("common.internalServerError.message"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return fingerprint;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 플랫폼별 배포명 전체 조회
     * @title : listDeployment
     * @return : List<String>
    *****************************************************************/
    public List<String> listDeployment(String platform, String iaas){
        return commonDao.selectDeploymentNameByPlatform(platform, iaas);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 플랫폼 별 릴리즈 설치 지원 버전 목록 조회
     * @title : getReleaseInfoByPlatform
     * @return : List<ManifestTemplateVO>
    *****************************************************************/
    public List<ManifestTemplateVO> getReleaseInfoByPlatform(String deployType, String iaas){
        return  commonDao.selectReleaseInfoByPlatform(deployType, iaas);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인프라 환경 설정 목록 정보
     * @title : getIaasConfigInfo
     * @return : HashMap<String, Object> 
    *****************************************************************/
    public HashMap<String, Object> getIaasConfigInfo(String iaasType, int id, Principal principal){
        return commonDao.selectIaasConfigAndAccountById(iaasType, id, principal.getName());
    }
    
    /****************************************************************
     * @param string 
     * @project : Paas 플랫폼 설치 자동화
     * @description : 배포 유형에 따른 Manifest Input 템플릿 파일 내용
     * @title : getManifestInputTemplateStream
     * @return : String
    *****************************************************************/
    public String getManifestInputTemplateStream(String deployType, String templateVersion, String iaasType, String inputTemplate, String openstackVersion){
        String content ="";
        if(!openstackVersion.isEmpty()) {
            if(openstackVersion.equalsIgnoreCase("v3")){
                iaasType += "v3";
            }
        }
        try {
            InputStream inputs =  this.getClass().getClassLoader().getResourceAsStream("static/deploy_template/"+deployType+"/" + templateVersion + "/"+ iaasType.toLowerCase() + "/" +inputTemplate);
            if(inputs == null){
                throw new CommonException(getMessageValue("common.internalServerError.exception.code"),
                        "Template 파일이 존재하지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            content = IOUtils.toString(inputs, "UTF-8");
        } catch (IOException e) {
            throw new CommonException(getMessageValue("common.internalServerError.exception.code"),
                    "Template 파일이 존재하지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return content;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 배포파일 브라우저 다운로드
     * @title : downloadDeploymentFile
     * @return : void
    *****************************************************************/
    public void downloadDeploymentFile(String fileName, HttpServletResponse response){
        File file = new File(DEPLOYMENT_DIR + SEPARATOR +fileName +".yml");
        try {
            if( file.exists() ){ //파일이 있으면
                //파일 타입 확인
                String mimeType= URLConnection.guessContentTypeFromName(file.getName());
                if( StringUtils.isEmpty(mimeType) ){
                    mimeType = "application/octet-stream";
                }
                response.setContentType(mimeType);
                //웹에 다운로드
                response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".yml"); 
                response.setContentLength((int)file.length());
                InputStream inputStream = new BufferedInputStream(new FileInputStream(file)); 
                //파일복사
           
                FileCopyUtils.copy(inputStream, response.getOutputStream());
            }
        } catch (IOException e) {
            throw new CommonException(getMessageValue("common.internalServerError.exception.code"),
                    getMessageValue("common.internalServerError.message"), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e){
            throw new CommonException(getMessageValue("common.internalServerError.exception.code"),
                    getMessageValue("common.internalServerError.message"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Message 값 가져오기
     * @title : getMessageValue
     * @return : void
    ***************************************************/
    public String getMessageValue(String messageCode) {
        String messageValue = message.getMessage(messageCode, null, Locale.KOREA);
        return messageValue;
    }

	public void downloadCredentialFile(String fileName, HttpServletResponse response) {
        File file = new File(HYBRID_CF_CREDENTIAL_DIR + SEPARATOR +fileName +".yml");
        try {
            if( file.exists() ){ //파일이 있으면
                //파일 타입 확인
                String mimeType= URLConnection.guessContentTypeFromName(file.getName());
                if( StringUtils.isEmpty(mimeType) ){
                    mimeType = "application/octet-stream";
                }
                response.setContentType(mimeType);
                //웹에 다운로드
                response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".yml"); 
                response.setContentLength((int)file.length());
                InputStream inputStream = new BufferedInputStream(new FileInputStream(file)); 
                //파일복사
           
                FileCopyUtils.copy(inputStream, response.getOutputStream());
            }
        } catch (IOException e) {
            throw new CommonException(getMessageValue("common.internalServerError.exception.code"),
                    getMessageValue("common.internalServerError.message"), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e){
            throw new CommonException(getMessageValue("common.internalServerError.exception.code"),
                    getMessageValue("common.internalServerError.message"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
    
}
