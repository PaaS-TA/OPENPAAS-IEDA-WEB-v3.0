package org.openpaas.ieda.deploy.web.config.setting.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File
;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.security.Principal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.common.web.security.SessionInfoDTO;
import org.openpaas.ieda.deploy.api.director.dto.DirectorInfoDTO;
import org.openpaas.ieda.deploy.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.deploy.web.config.setting.dao.DirectorConfigDAO;
import org.openpaas.ieda.deploy.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.deploy.web.config.setting.dto.DirectorConfigDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DirectorConfigService  {
    
    @Autowired private DirectorConfigDAO dao;
    
    final private static String BASE_DIR = System.getProperty("user.home");
    final private static String SEPARATOR = System.getProperty("file.separator");
    final private static String CREDENTIAL_DIR = LocalDirectoryConfiguration.getGenerateCredentialDir() + SEPARATOR;
    private final static Logger LOGGER = LoggerFactory.getLogger(DirectorConfigService.class);
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 기본 설치 관리자 정보 조회
     * @title : getDefaultDirector
     * @return : DirectorConfigVO
    ***************************************************/
    public DirectorConfigVO getDefaultDirector() {
        //기본 설치 관리자 존재 여부 조회
        DirectorConfigVO directorConfig = dao.selectDirectorConfigByDefaultYn("Y");
        if( directorConfig != null ){
            boolean flag = checkDirectorConnect(directorConfig.getDirectorUrl(), directorConfig.getDirectorPort(), directorConfig.getUserId(), directorConfig.getUserPassword());
            directorConfig.setConnect(flag);
        }
        return directorConfig;
    }
    
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 설치 관리자 정보 목록을 DefaultYn를 기준으로 역 정렬하여 전체 조회한 값을 응답
     * @title : listDirector
     * @return : List<DirectorConfigVO>
    ***************************************************/
    public List<DirectorConfigVO> getDirectorList() {
        //설치관리자 목록 전체조회
        List<DirectorConfigVO> resultList = dao.selectDirectorConfig();
        int recid = 0;
        for (DirectorConfigVO directionConfig : resultList) {
            directionConfig.setRecid(recid++);
        }
        return resultList;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : HttpClient에 요청하여 설치관리자 정보를 읽어옴
     * @title : getDirectorInfo
     * @return : DirectorInfoDTO
    ***************************************************/
    public DirectorInfoDTO getDirectorInfo(String directorUrl, int port, String userId, String password) {
        DirectorInfoDTO info = null;
        try {
            HttpClient client = DirectorRestHelper.getHttpClient(port);
            GetMethod get = new GetMethod(DirectorRestHelper.getInfoURI(directorUrl, port)); 
            get = (GetMethod)DirectorRestHelper.setAuthorization(userId, password, (HttpMethodBase)get); 
            client.executeMethod(get);
        
            ObjectMapper mapper = new ObjectMapper();
            info = mapper.readValue(get.getResponseBodyAsString(), DirectorInfoDTO.class);
        } catch (Exception e) {
            if( LOGGER.isErrorEnabled() ){
                LOGGER.error( e.getMessage() );
            }
        }
        
        return info;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 설치 관리자 조회
     * @title : checkDirectorConnect
     * @return : boolean
    ***************************************************/
    public boolean checkDirectorConnect(String directorUrl, int port, String userId, String password) {
        boolean flag = true;
        try {
            HttpClient client = DirectorRestHelper.getHttpClient(port);
            GetMethod get = new GetMethod(DirectorRestHelper.getInfoURI(directorUrl, port)); 
            get = (GetMethod)DirectorRestHelper.setAuthorization(userId, password, (HttpMethodBase)get); 
            client.executeMethod(get);
        } catch (RuntimeException e) {
            if( LOGGER.isErrorEnabled() ){ LOGGER.error( e.getMessage() );}
        } catch (Exception e) {
            return false;
        }
        return flag;
    }

    /***************************************************
     * @param boshConfigFileName 
     * @project : OpenPaas 플랫폼 설치 자동
     * @description : 설치관리자 추가 정보 확인
     * @title :  existCheckCreateDirectorInfo
     * @return : void
     ***************************************************/
    public void existCheckCreateDirectorInfo(DirectorConfigDTO.Create createDto, Principal principal, String boshConfigFileName) {
        //해당 설치관리자가 존재하는지 확인한다
        List<DirectorConfigVO> resultList = dao.selectDirectorConfigByDirectorUrl(createDto.getDirectorUrl());
        //세션 정보를 가져온다.
        
        if ( !resultList.isEmpty() ) {
            throw new CommonException("duplicated.director.exception",
                    "이미 등록되어 있는 디렉터 URL입니다.", HttpStatus.CONFLICT);
        }
        
        //설치관리자 정보를 확인한다.
        DirectorInfoDTO info = getDirectorInfo(createDto.getDirectorUrl()
                                , createDto.getDirectorPort()
                                , createDto.getUserId()
                                , createDto.getUserPassword());
        if ( info == null || StringUtils.isEmpty(info.getUser())) {
            throw new CommonException("unauthenticated.director.exception",
                    "디렉터에 로그인 실패하였습니다.", HttpStatus.BAD_REQUEST);
        }
        insertDirectorInfo(createDto, principal, info, boshConfigFileName);
    }
    
    /***************************************************
    * @param boshConfigFileName 
     * @project : Paas 플랫폼 설치 자동화
    * @description : 설치 관리자 정보 삽입
    * @title : insertDirectorInfo
    * @return : int
    ***************************************************/
    public void insertDirectorInfo(DirectorConfigDTO.Create createDto, Principal principal, DirectorInfoDTO info, String boshConfigFileName){
        SessionInfoDTO sessionInfo = new SessionInfoDTO(principal);
        DirectorConfigVO director = new DirectorConfigVO();
        director.setUserId(createDto.getUserId());
        director.setUserPassword(createDto.getUserPassword());
        director.setDirectorUrl(createDto.getDirectorUrl());
        director.setDirectorPort(createDto.getDirectorPort());
        director.setCredentialFile(createDto.getCredentialFile());
        director.setDirectorName(info.getName());
        director.setDirectorUuid(info.getUuid());
        if(info.getCpi().indexOf("_cpi") == -1){
            info.setCpi(info.getCpi()+"_cpi");
        }
        director.setDirectorCpi(info.getCpi());
        director.setDirectorVersion(info.getVersion());
        director.setCreateUserId(sessionInfo.getUserId());
        director.setUpdateUserId(sessionInfo.getUserId());
        

        //기존에 기본 설치관리자가 존재한다면 N/ 존재하지않는다면 기본 설치관리자로 설정
        DirectorConfigVO directorConfig = dao.selectDirectorConfigByDefaultYn("Y");
        director.setDefaultYn((directorConfig == null ) ? "Y":"N");
        if( director.getDefaultYn().equalsIgnoreCase("Y") ) {
            boshEnvAliasSequence(director);
        }else{        
            dao.insertDirector(director);
        }
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 기본 설치관리자 설정
     * @title : boshEnvAliasSequence
     * @return : void
    *****************************************************************/
    public void boshEnvAliasSequence(DirectorConfigVO directorConfig){
        try{
            // bosh-env에 로그인
            boshEnvAliasLoginSequence(directorConfig);
             // 로그인 판별
            int statusResult = isExistBoshEnvLogin(directorConfig.getDirectorUrl(), 
                    directorConfig.getDirectorPort(), 
                    directorConfig.getUserId(), 
                    directorConfig.getUserPassword());
            String httpStatus = String.valueOf(statusResult);
            // stemcell 조회 > httpStatus > 조건 200 이 아닐경우 Exception >> database update
            if(httpStatus.equals("200")){
                dao.insertDirector(directorConfig);
            }else{
                throw new CommonException("unAuthorized.director.exception",
                        "실행 권한이 없습니다.", HttpStatus.UNAUTHORIZED);
            }
        } catch (NullPointerException e){
            e.printStackTrace();
            throw new CommonException("notfound.directorFile.exception",
                    "설치관리자 입력 정보를 확인해 주세요.", HttpStatus.NOT_FOUND);
        } catch (ClassCastException e){
            e.printStackTrace();
            throw new CommonException("classCastException.directorFile.exception",
                    "설치관리자 입력 정보를 확인해 주세요.", HttpStatus.NOT_FOUND);
        }
    }
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  기본 설치관리자 로그인
     * @title : boshEnvAliasLoginSequence
     * @return : void
    *****************************************************************/
    @SuppressWarnings("unchecked")
    public void boshEnvAliasLoginSequence(DirectorConfigVO directorConfig){
        OutputStreamWriter fileWriter = null;
        try {
            String boshCredentialFile = CREDENTIAL_DIR+directorConfig.getCredentialFile();
            InputStream input = new FileInputStream(new File( boshCredentialFile));
            Yaml yaml = new Yaml();
            // 파일을 로드하여 Map<String, Object>에 parse한다.
            Map<String, Object> object = (Map<String, Object>)yaml.load(input);
            Map<String, String> certMap = (Map<String,String>)object.get("director_ssl");
            // bosh alias-env를 실행한다.
            ProcessBuilder builder = new ProcessBuilder("bosh", "alias-env", directorConfig.getDirectorName(),
                                                         "-e", directorConfig.getDirectorUrl(), "--ca-cert="+certMap.get("ca"), "--tty");
            Process process = builder.start();
            BufferedReader bufferedReader = null;
            InputStream inputStream = process.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
            String info = null;
            
            String accumulatedLog= null;
            
            StringBuffer accumulatedBuffer = new StringBuffer();
            
            while ((info = bufferedReader.readLine()) != null){
                accumulatedBuffer.append(info).append("\n");
            }
            if( accumulatedBuffer != null ) {
                accumulatedLog = accumulatedBuffer.toString();
            }
            if(!accumulatedLog.contains("Succeeded")){
                throw new CommonException("notfound.directorFile.exception",
                        "기본 디렉터로 설정 중 에러가 발생 했습니다. 정보를 확인 해주세요.", HttpStatus.NOT_FOUND);
            }
            Thread.sleep(10000);
            
            String boshConfigFile = BASE_DIR+SEPARATOR+".bosh"+SEPARATOR+"config";
            input = new FileInputStream(new File(boshConfigFile));
            yaml = new Yaml();
            Map<String, Object> boshEnv = (Map<String, Object>)yaml.load(input);
            List<Map<String, Object>> envMap = (List<Map<String, Object>>) boshEnv.get("environments");
            for(int i=0;i<envMap.size();i++){
                if(envMap.get(i).get("url").equals(directorConfig.getDirectorUrl())){
                    envMap.get(i).put("username",directorConfig.getUserId());
                    envMap.get(i).put("password", directorConfig.getUserPassword());
                }
            }
            // bosh config 파일을 출력하기 위한  FileWriter 객체 생성
            fileWriter = new OutputStreamWriter(new FileOutputStream(boshConfigFile),"UTF-8");
            // StringWriter 객체 생성
            StringWriter stringWriter = new StringWriter();
            yaml.dump(boshEnv, stringWriter);
            fileWriter.write(stringWriter.toString());
        } catch (IOException e) {
            throw new CommonException("taretDirector.director.exception",
                    "입력 정보를 확인해 주세요.", HttpStatus.NOT_FOUND);
        } catch (NullPointerException e){
            throw new CommonException("notfound.directorFile.exception",
                    "입력 정보를 확인해 주세요.", HttpStatus.NOT_FOUND);
        } catch (InterruptedException e) {
            throw new CommonException("notfound.directorFile.exception",
                    "입력 정보를 확인해 주세요.", HttpStatus.NOT_FOUND);
		} finally {
            try {
                if(fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
                    throw new CommonException("taretDirector.director.exception",
                            "읽어오는 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
                }
        }
    }
    
    /***************************************************
     * @project : OpenPaas 플랫폼 설치 자동
     * @description : 설치관리자 설정 조회
     * @title :  getDirectorConfig
     * @return : DirectorConfigVO
     ***************************************************/
    public DirectorConfigVO getDirectorConfig(int seq) {
        DirectorConfigVO directorConfig = dao.selectDirectorConfigBySeq(seq);
        
        if (directorConfig == null) {
            throw new CommonException("notfonud.director.exception",
                    "해당하는 설치관리자는 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }
        
        return directorConfig;
    }
    
    /***************************************************
     * @param boshConfigFileName 
     * @project : OpenPaas 플랫폼 설치 자동
     * @description : 설치관리자 설정 삭제
     * @title :  deleteDirectorConfig
     * @return : void
     ***************************************************/
    public void deleteDirectorConfig(int seq, String boshConfigFileName) {
        //1. 해당 설치관리자가 존재하는지 확인한다.
        DirectorConfigVO directorConfig = dao.selectDirectorConfigBySeq(seq);
        if (directorConfig == null) {
            throw new CommonException("notfound.director.exception",
                    "해당하는 설치관리자는 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }
        dao.deleteDirector(seq);
    }

    
    /***************************************************
     * @param principal 
     * @project : OpenPaas 플랫폼 설치 자동
     * @description : 기본 설치 관리자 정보 확인
     * @title :  existSetDefaultDirectorInfo
     * @return : DirectorConfigVO
     ***************************************************/
    public DirectorConfigVO  existCheckSetDefaultDirectorInfo(int seq, Principal principal, String boshConfigFileName) {
        //1. 설치관리자가 존재하는지 확인한다.
        DirectorConfigVO directorConfig = dao.selectDirectorConfigBySeq(seq);
        if (directorConfig == null) {
            throw new CommonException("notfound.director.exception",
                    "해당하는 설치관리자는 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }
        
        //2.    설치관리자 정보를 확인한다
        DirectorInfoDTO info = getDirectorInfo(directorConfig.getDirectorUrl(), directorConfig.getDirectorPort(), directorConfig.getUserId(), directorConfig.getUserPassword());
        if ( info == null || StringUtils.isEmpty(info.getUser()) ) {
            throw new CommonException("unauthenticated.director.exception",
                    "해당 설치 관리자 로그인 실패하였습니다.", HttpStatus.BAD_REQUEST);
        }
        return setDefaultDirectorInfo(directorConfig, info, principal, boshConfigFileName);
    }
    
    /***************************************************
    * @param principal 
    * @param boshConfigFileName 
    * @project : Paas 플랫폼 설치 자동화
    * @description : 기본 설치 관리자 변경
    * @title : setDefaultDirectorInfo
    * @return : DirectorConfigVO
    ***************************************************/
    @SuppressWarnings("unchecked")
    public DirectorConfigVO setDefaultDirectorInfo(DirectorConfigVO directorConfig, DirectorInfoDTO info, Principal principal, String boshConfigFileName){
        // 기존 기본관리자의 정보를 불러온다.
        DirectorConfigVO oldDefaultDiretor = dao.selectDirectorConfigByDefaultYn("Y");
        // 세션 정보를 가져온다.
        SessionInfoDTO sessionInfo = new SessionInfoDTO(principal);
        if (oldDefaultDiretor != null) {
            oldDefaultDiretor.setDefaultYn("N");
            oldDefaultDiretor.setUpdateUserId(sessionInfo.getUserId());
        }
        // 새로운 기본관리자의 정보를 셋팅한다.
        directorConfig.setDefaultYn("Y");
        directorConfig.setDirectorName(info.getName());
        directorConfig.setDirectorUuid(info.getUuid());
        if(info.getCpi().indexOf("_cpi") == -1){
            info.setCpi(info.getCpi()+"_cpi");
        }
        directorConfig.setDirectorCpi(info.getCpi());
        directorConfig.setDirectorVersion(info.getVersion());
        directorConfig.setUpdateUserId(sessionInfo.getUserId());
        // bosh-env 환경설정 정보를 업데이트
        OutputStreamWriter fileWriter = null;
        try{
            boshEnvAliasLoginSequence(directorConfig);
            int statusResult = isExistBoshEnvLogin(directorConfig.getDirectorUrl(), 
                                                   directorConfig.getDirectorPort(), 
                                                   directorConfig.getUserId(), 
                                                   directorConfig.getUserPassword());
            String httpStatus = String.valueOf(statusResult);
            // stemcell 조회 > httpStatus > 조건 200 이 아닐경우 Exception >> database update
            if(httpStatus.equals("200")){
                dao.updateDirector(oldDefaultDiretor);
                dao.updateDirector(directorConfig);
            }else{
                oldDefaultDiretor.setDefaultYn("Y");
                oldDefaultDiretor.setUpdateUserId(sessionInfo.getUserId());
                dao.updateDirector(oldDefaultDiretor);
                throw new CommonException("unAuthorized.director.exception",
                        "로그인 되지 않아 실행 권한이 없습니다.", HttpStatus.UNAUTHORIZED);
            }
        } catch (NullPointerException e){
            e.printStackTrace();
            throw new CommonException("notfound.directorFile.exception",
                    "설치관리자 관리 파일을 읽어오는 중 오류가 발생했습니다.", HttpStatus.NOT_FOUND);
        } catch (ClassCastException e){
            e.printStackTrace();
            throw new CommonException("classCastException.directorFile.exception",
                    "설치관리자 관리 파일을 읽어오는 중 오류가 발생했습니다.", HttpStatus.NOT_FOUND);
        } catch(HttpStatusCodeException e) {
            e.printStackTrace();
            throw new CommonException("unAuthorized.director.exception",
                    "실행 권한이 없습니다.", HttpStatus.UNAUTHORIZED);
        } finally {
            try {
                if(fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
                throw new CommonException("taretDirector.director.exception",
                        "읽어오는중 오류가 발생했습니다!", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return directorConfig;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : bosh-env 로그인 판별
     * @title : isExistBoshEnvLogin
     * @return : boolean
    *****************************************************************/
    public int isExistBoshEnvLogin(String directorUrl, int port, String userId, String password){
        int statusResult = 0;
        try {
            HttpClient client = DirectorRestHelper.getHttpClient(port);
            GetMethod get = new GetMethod(DirectorRestHelper.getStemcellsURI(directorUrl, port)); 
            get = (GetMethod)DirectorRestHelper.setAuthorization(userId, password, (HttpMethodBase)get); 
            statusResult = client.executeMethod(get);
        } catch (Exception e) {
            if( LOGGER.isErrorEnabled() ){ LOGGER.error( e.getMessage() );}
        } 
        return statusResult;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 
     * @title : uploadCredentialKeyFile
     * @return : void
    *****************************************************************/
    public void uploadCredentialKeyFile(MultipartHttpServletRequest request) {
        Iterator<String> itr =  request.getFileNames();
        File keyPathFile = new File(CREDENTIAL_DIR);
        if (!keyPathFile.isDirectory()){
            boolean result = keyPathFile.mkdir();
            LOGGER.debug("Credential key path file directory create :: " + result);
        }        
        if(itr.hasNext()) {
            BufferedOutputStream stream = null;
            MultipartFile mpf = request.getFile(itr.next());
            try { 
                String keyFilePath = CREDENTIAL_DIR + mpf.getOriginalFilename();
                byte[] bytes = mpf.getBytes();
                File isKeyFile = new File(keyFilePath);
                stream = new BufferedOutputStream(new FileOutputStream(isKeyFile));
                stream.write(bytes); 
                        
            } catch (IOException e) {
                e.printStackTrace();
                throw new CommonException("notfound.keyFile.exception",
                        "Key 입력 정보를 확인해 주세요.", HttpStatus.INTERNAL_SERVER_ERROR);
            } finally {
                try {
                    if( stream != null ) {
                        stream.close();
                    }
                } catch (IOException e) {
                    throw new CommonException("notfound.keyFile.exception",
                            "Key 입력 정보를 확인해 주세요.", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }
    }
 }
