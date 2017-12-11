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
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.web.common.dao.CommonDeployDAO;
import org.openpaas.ieda.deploy.web.common.dao.ManifestTemplateVO;
import org.openpaas.ieda.deploy.web.common.dto.KeyInfoDTO;
import org.openpaas.ieda.deploy.web.deploy.cf.dao.CfDAO;
import org.openpaas.ieda.deploy.web.deploy.cf.dao.CfVO;
import org.openpaas.ieda.deploy.web.deploy.diego.dao.DiegoDAO;
import org.openpaas.ieda.deploy.web.deploy.diego.dao.DiegoVO;
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
    @Autowired DiegoDAO diegoDao;
    @Autowired CommonDeployDAO commonDao;
    @Autowired IaasConfigMgntDAO iaasConfigDao;
    @Autowired MessageSource message;
    
    final private static String SEPARATOR = System.getProperty("file.separator");
    final private static String GENERATE_CERTS_DIR = LocalDirectoryConfiguration.getGenerateCertsDir();
    final private static String SSH_DIR = LocalDirectoryConfiguration.getSshDir();
    final private static String KEY_DIR = LocalDirectoryConfiguration.getKeyDir();
    final private static String DEPLOYMENT_DIR = LocalDirectoryConfiguration.getDeploymentDir();
    final private static String LOCK_DIR = LocalDirectoryConfiguration.getLockDir() + SEPARATOR;
    final private static Logger LOGGER = LoggerFactory.getLogger(CommonDeployService.class);
    
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
                if( iaasType != null && iaasType.equals("google") ){
                    if(file.getName().toLowerCase().endsWith(".pub") ||  file.getName().toLowerCase().endsWith(".pem")){
                        continue;
                    }
                }else{
                    if(!file.getName().toLowerCase().endsWith(".pem")) {
                        continue;
                    }
                }
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
        File generateCertsFile = null;
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        String keyFileName = "";
        
        DefaultArtifactVersion maxVersion = new DefaultArtifactVersion("1.25.1");
        DefaultArtifactVersion version = new DefaultArtifactVersion(dto.getVersion());
        String generateCerts = "";
        String platform = dto.getPlatform().toLowerCase();
        Integer releaseVersion = 0;
        if(!platform.equals("diego") && (!dto.getVersion().equals("2.0") && !dto.getVersion().equals("3.0"))){
            releaseVersion = Integer.parseInt(dto.getVersion());
        }
        if( (platform.equals("cf") && releaseVersion < 272 && !dto.getVersion().equals("3.0"))  
                || ( platform.equals("diego") && maxVersion.compareTo(version) > 0 ) ) {
            generateCerts = GENERATE_CERTS_DIR + SEPARATOR + "generate-certs_v1" + SEPARATOR + "generate-certs";
        } else {
            generateCerts = GENERATE_CERTS_DIR + SEPARATOR + "generate-certs_v2" + SEPARATOR + "generate-certs";
        }
        try {
            generateCertsFile = new File( generateCerts );
            if( generateCertsFile.exists() ){
                //key를 생성할 코드를 설정(cf: 1, diego: 2, cf-diego: 3)
                  //272 버전 이상일 경우 platform이 cf 일지라도 cf-diego 키 생성
                String code = setCreateKeyCodeNumber( dto.getPlatform(),dto.getVersion() );
                if(code.equals("3")) {
                    dto.setPlatform("cf-diego");
                }
                
                //key 파일명
                keyFileName = dto.getIaas().toLowerCase() + "-" + dto.getPlatform()+"-key-" + dto.getId()+".yml";
                
                ProcessBuilder builder = new ProcessBuilder();
                List<String> cmd = new ArrayList<String>();
                cmd.add(generateCerts);
                if( (platform.equals("cf") && releaseVersion < 272 && !dto.getVersion().equals("3.0")) || ( platform.equals("diego") && maxVersion.compareTo(version) > 0 ) ) {
                    cmd.add(GENERATE_CERTS_DIR + SEPARATOR + "generate-certs_v1");
                }else {
                    cmd.add(GENERATE_CERTS_DIR + SEPARATOR + "generate-certs_v2");
                }
                cmd.add(code); //1:cf, 2: diego, 3: cf-diego
                cmd.add( keyFileName.split(".yml")[0] ); // make key name(<iaas>-cf-key-<id>);
                if( !platform.equals("diego")){
                    cmd.add(dto.getDomain());//domain
                    cmd.add(dto.getCountryCode());//국가 코드
                    cmd.add(dto.getStateName());//시//도
                    cmd.add(dto.getLocalityName());//시/구/군
                    cmd.add(dto.getOrganizationName());//회사명
                    cmd.add(dto.getUnitName());//부서명
                    cmd.add(dto.getEmail());//email
                }
                builder.command(cmd);
                builder.redirectErrorStream(true);
                Process process = builder.start();//start script
                
                inputStream = process.getInputStream();//get script log
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                String info = null;
                while ((info = bufferedReader.readLine()) != null) {
                    if( info.indexOf("ERROR") > -1 ){
                        throw new CommonException(getMessageValue("common.internalServerError.exception.code"),
                                keyFileName + getMessageValue("common.file.create.internalServerError.message"), HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }
                File keyFile = new File( KEY_DIR + SEPARATOR + keyFileName );
                if( !keyFile.exists() ){
                    throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                            keyFileName +" 파일을 찾을 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
                }
                saveKeyFileName(dto, keyFileName, principal);
            }else{
                throw new CommonException(getMessageValue("common.notFound.exception.code"),
                        getMessageValue("common.notFound.template.message"), HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            throw new CommonException(getMessageValue("common.internalServerError.exception.code"),
                    getMessageValue("common.internalServerError.message"), HttpStatus.INTERNAL_SERVER_ERROR);
        }finally{
            try {
                if( bufferedReader != null ){
                    bufferedReader.close();
                }
            } catch (IOException e) {
                throw new CommonException(getMessageValue("common.internalServerError.exception.code"),
                        getMessageValue("common.internalServerError.message"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return keyFileName;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : key 생성 코드 설정(cf: 1; diego: 2; cf-diego: 3)
     * @title : setCreateKeyCodeNumber
     * @return : String
    ***************************************************/
    public String setCreateKeyCodeNumber( String platform, String releaseVersion ) {
        String code ="";
        int cfReleaseVersion = 0;
        if(!platform.equalsIgnoreCase("diego") && (!releaseVersion.equals("2.0") && !releaseVersion.equals("3.0"))){
            cfReleaseVersion = Integer.parseInt(releaseVersion);
            if( (platform.equalsIgnoreCase("cf") && cfReleaseVersion >= 272) || platform.equalsIgnoreCase("cfdiego")) {
                code = "3";
            }else {
                code="1";
            }
        } else if(platform.equalsIgnoreCase("diego")){
            code = "2";
        } else if( platform.equalsIgnoreCase("cfdiego") ){
            code = "3";
        } else {
            code = "1";
        }
        return code;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 키 파일명 저장
     * @title : saveKeyFileName
     * @return : void
    ***************************************************/
    public void saveKeyFileName( KeyInfoDTO dto, String keyFileName, Principal principal ){
        if( !("diego".equalsIgnoreCase(dto.getPlatform())) ){
            CfVO cfVo = cfDao.selectCfInfoById( Integer.parseInt(dto.getId()) );
            cfVo.setKeyFile(keyFileName);
            cfVo.setUpdateUserId(principal.getName());
            cfDao.updateCfInfo(cfVo);
        }else{
            DiegoVO diegoVo = diegoDao.selectDiegoInfo( Integer.parseInt(dto.getId()) );
            diegoVo.setKeyFile(keyFileName);
            diegoVo.setUpdateUserId(principal.getName());
            diegoDao.updateDiegoDefaultInfo(diegoVo);
        }
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
                iaasType = "v3";
            }
        }
        try {
            InputStream inputs =  this.getClass().getClassLoader().getResourceAsStream("static/deploy_template/"+deployType+"/" + templateVersion + "/"+ iaasType.toLowerCase() + "/" +inputTemplate);
            content = IOUtils.toString(inputs, "UTF-8");
        } catch (IOException e) {
            throw new CommonException(getMessageValue("common.internalServerError.exception.code"),
                    getMessageValue("common.internalServerError.message"), HttpStatus.INTERNAL_SERVER_ERROR);
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
    
}
