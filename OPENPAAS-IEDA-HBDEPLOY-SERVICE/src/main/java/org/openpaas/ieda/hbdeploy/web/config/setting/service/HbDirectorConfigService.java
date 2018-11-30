package org.openpaas.ieda.hbdeploy.web.config.setting.service;

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
import org.openpaas.ieda.hbdeploy.api.director.dto.DirectorInfoDTO;
import org.openpaas.ieda.hbdeploy.api.director.utility.HbDirectorRestHelper;
import org.openpaas.ieda.hbdeploy.web.config.setting.dao.HbDirectorConfigDAO;
import org.openpaas.ieda.hbdeploy.web.config.setting.dao.HbDirectorConfigVO;
import org.openpaas.ieda.hbdeploy.web.config.setting.dto.HbDirectorConfigDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class HbDirectorConfigService  {
    
    @Autowired private HbDirectorConfigDAO dao;
    
    final private static String BASE_DIR = System.getProperty("user.home");
    final private static String SEPARATOR = System.getProperty("file.separator");
    final private static String HYBRID_CREDENTIAL_DIR = LocalDirectoryConfiguration.getGenerateHybridCredentialDir() + SEPARATOR;
    private final static Logger LOGGER = LoggerFactory.getLogger(HbDirectorConfigService.class);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Url을 통한 디렉터 정보 조회
     * @title : getSelectedDirectorByUrl
     * @return : HbDirectorConfigVO
    *****************************************************************/
    public HbDirectorConfigVO getSelectedDirectorByUrl(String directorUrl){
        List<HbDirectorConfigVO> listvo = dao.selectHbDirectorConfigByDirectorUrl(directorUrl);
        HbDirectorConfigVO selectedDirector = new HbDirectorConfigVO();
        if(listvo != null){
            selectedDirector = listvo.get(0);
        }else{
            throw new CommonException("notfound.director.exception",
                    "해당하는 디렉터가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }
        if( selectedDirector != null ) {
            boolean flag = checkDirectorConnect(selectedDirector.getDirectorUrl(),
                                                 selectedDirector.getDirectorPort(),
                                                 selectedDirector.getUserId(),
                                                 selectedDirector.getUserPassword());
            selectedDirector.setConnect(flag);
        }
        return selectedDirector;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : HttpClient에 요청하여 설치관리자 존재 유무 확인
     * @title : checkDirectorConnect
     * @return : boolean
    *****************************************************************/
    public boolean checkDirectorConnect(String directorUrl, int port, String userId, String password) {
        boolean flag = true;
        try {
            HttpClient client = HbDirectorRestHelper.getHttpClient(port);
            GetMethod get = new GetMethod(HbDirectorRestHelper.getInfoURI(directorUrl, port)); 
            get = (GetMethod)HbDirectorRestHelper.setAuthorization(userId, password, (HttpMethodBase)get); 
            client.executeMethod(get);
        } catch (RuntimeException e) {
            if( LOGGER.isErrorEnabled() ){ LOGGER.error( e.getMessage() );}
        } catch (Exception e) {
            return false;
        }
        return flag;
    }
    
    /***************************************************
     * @param directorType 
     * @project : Paas 플랫폼 설치 자동화
     * @description : 디렉터 정보 목록을 DefaultYn를 기준으로 역 정렬하여 전체 조회한 값을 응답
     * @title : listDirector
     * @return : List<DirectorConfigVO>
    ***************************************************/
    public List<HbDirectorConfigVO> getDirectorList(String directorType) {
        //디렉터 목록 전체조회
        List<HbDirectorConfigVO> resultList = dao.selectHbDirectorConfig(directorType);
        int recid = 0;
        for (HbDirectorConfigVO directionConfig : resultList) {
            directionConfig.setRecid(recid++);
            directionConfig.setIaasType(directionConfig.getDirectorCpi().split("_")[0]);
        }
        return resultList;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : HttpClient에 요청하여 디렉터 정보를 읽어옴
     * @title : getDirectorInfo
     * @return : DirectorInfoDTO
    ***************************************************/
    public DirectorInfoDTO getDirectorInfo(String directorUrl, int port, String userId, String password) {
        DirectorInfoDTO info = null;
        try {
            HttpClient client = HbDirectorRestHelper.getHttpClient(port);
            GetMethod get = new GetMethod(HbDirectorRestHelper.getInfoURI(directorUrl, port)); 
            get = (GetMethod)HbDirectorRestHelper.setAuthorization(userId, password, (HttpMethodBase)get); 
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
     * @param boshConfigFileName 
     * @project : OpenPaas 플랫폼 설치 자동
     * @description : 디렉터 추가 정보 확인
     * @title :  existCheckCreateDirectorInfo
     * @return : void
     ***************************************************/
    public void existCheckCreateDirectorInfo(HbDirectorConfigDTO directorDto, Principal principal, String boshConfigFileName) {
        //해당 디렉터가 존재하는지 확인한다
        List<HbDirectorConfigVO> resultList = dao.selectHbDirectorConfigByDirectorUrl(directorDto.getDirectorUrl());
        //세션 정보를 가져온다.
        
        if ( !resultList.isEmpty() ) {
            throw new CommonException("duplicated.director.exception",
                    "이미 등록되어 있는 디렉터 URL입니다.", HttpStatus.CONFLICT);
        }
        
        //디렉터 정보를 확인한다.
        DirectorInfoDTO info = getDirectorInfo(directorDto.getDirectorUrl()
                                , directorDto.getDirectorPort()
                                , directorDto.getUserId()
                                , directorDto.getUserPassword());
        if ( info == null || StringUtils.isEmpty(info.getUser())) {
            throw new CommonException("unauthenticated.director.exception",
                    "디렉터에 로그인 실패하였습니다.", HttpStatus.BAD_REQUEST);
        }
        insertDirectorInfo(directorDto, principal, info, boshConfigFileName);
    }
    
    /***************************************************
    * @param boshConfigFileName 
     * @project : Paas 플랫폼 설치 자동화
    * @description : 디렉터 정보 삽입
    * @title : insertDirectorInfo
    * @return : int
    ***************************************************/
    public void insertDirectorInfo(HbDirectorConfigDTO directorDto, Principal principal, DirectorInfoDTO info, String boshConfigFileName){
        SessionInfoDTO sessionInfo = new SessionInfoDTO(principal);
        HbDirectorConfigVO director = new HbDirectorConfigVO();
        director.setUserId(directorDto.getUserId());
        director.setUserPassword(directorDto.getUserPassword());
        director.setDirectorUrl(directorDto.getDirectorUrl());
        director.setDirectorPort(directorDto.getDirectorPort());
        director.setCredentialFile(directorDto.getCredentialFile());
        director.setDirectorName(info.getName());
        director.setDirectorUuid(info.getUuid());
        if(info.getCpi().indexOf("_cpi") == -1){
            info.setCpi(info.getCpi()+"_cpi");
        }
        
        if("public".equals(directorDto.getDirectorType())){
            if(info.getCpi().contains("openstack") || info.getCpi().contains("vsphere") ){
                throw new CommonException("unauthenticated.director.exception",
                        "Public/Private Cloud 환경 정보를 확인 하세요.", HttpStatus.BAD_REQUEST);
            }
        } else {
            if(info.getCpi().contains("aws") || info.getCpi().contains("google") || info.getCpi().contains("azure")){
                throw new CommonException("badrequest.director.exception",
                        "Public/Private Cloud 환경 정보를 확인 하세요.", HttpStatus.BAD_REQUEST);
            }
        }
        director.setDirectorCpi(info.getCpi());
        director.setDirectorVersion(info.getVersion());
        director.setCreateUserId(sessionInfo.getUserId());
        director.setUpdateUserId(sessionInfo.getUserId());
        director.setDirectorType(directorDto.getDirectorType());
        boshEnvAliasLoginSequence(director);
        isExistBoshEnvLogin(director.getDirectorUrl(),  director.getDirectorPort(),  director.getUserId(),  director.getUserPassword());
        dao.insertHbDirector(director);
    }
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  기본 디렉터 로그인
     * @title : boshEnvAliasLoginSequence
     * @return : void
    *****************************************************************/
    @SuppressWarnings("unchecked")
    public void boshEnvAliasLoginSequence(HbDirectorConfigVO directorConfig){
        OutputStreamWriter fileWriter = null;
        try {
            String boshCredentialFile = HYBRID_CREDENTIAL_DIR+directorConfig.getCredentialFile();
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
                        "디렉터로 설정 중 에러가 발생 했습니다. 정보를 확인 해주세요.", HttpStatus.NOT_FOUND);
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
     * @param boshConfigFileName 
     * @project : OpenPaas 플랫폼 설치 자동
     * @description : 디렉터 설정 삭제
     * @title :  deleteDirectorConfig
     * @return : void
     ***************************************************/
    public void deleteDirectorConfig(int seq, String boshConfigFileName) {
        //1. 해당 디렉터가 존재하는지 확인한다.
        HbDirectorConfigVO directorConfig = dao.selectHbDirectorConfigBySeq(seq);
        if (directorConfig == null) {
            throw new CommonException("notfound.director.exception",
                    "해당하는 디렉터는 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }
        dao.deleteHbDirector(seq);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : bosh-env 로그인 판별
     * @title : isExistBoshEnvLogin
     * @return : boolean
    *****************************************************************/
    public String isExistBoshEnvLogin(String directorUrl, int port, String userId, String password){
        int statusResult = 0;
        try {
            HttpClient client = HbDirectorRestHelper.getHttpClient(port);
            GetMethod get = new GetMethod(HbDirectorRestHelper.getStemcellsURI(directorUrl, port)); 
            get = (GetMethod)HbDirectorRestHelper.setAuthorization(userId, password, (HttpMethodBase)get); 
            statusResult = client.executeMethod(get);
        } catch (Exception e) {
            if( LOGGER.isErrorEnabled() ){ LOGGER.error( e.getMessage() );}
        }
        String httpStatus = String.valueOf(statusResult);
        // stemcell 조회 > httpStatus > 조건 200 이 아닐경우 Exception >> database update
        if(!httpStatus.equals("200")){
            throw new CommonException("unAuthorized.director.exception",
                    "실행 권한이 없습니다. 디렉터 정보를 확인하세요.", HttpStatus.UNAUTHORIZED);
        }
        
        return httpStatus;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 디렉터 인증서 파일 업로드
     * @title : uploadCredentialKeyFile
     * @return : void
    *****************************************************************/
    public void uploadCredentialKeyFile(MultipartHttpServletRequest request) {
        Iterator<String> itr =  request.getFileNames();
        File keyPathFile = new File(HYBRID_CREDENTIAL_DIR);
        if (!keyPathFile.isDirectory()){
            boolean result = keyPathFile.mkdir();
            LOGGER.debug("Credential key path file directory create :: " + result);
        }        
        if(itr.hasNext()) {
            BufferedOutputStream stream = null;
            MultipartFile mpf = request.getFile(itr.next());
            try { 
                String keyFilePath = HYBRID_CREDENTIAL_DIR + mpf.getOriginalFilename();
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
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : bosh-env 로그인 판별
     * @title : isExistBoshEnvLogin
     * @return : boolean
    *****************************************************************/
    public List<HbDirectorConfigVO> getDirectorListByIaas(String iaasType) {
        List<HbDirectorConfigVO> resultList = dao.selectHbDirectorConfigByIaas(iaasType);
        int recid = 0;
        for (HbDirectorConfigVO directionConfig : resultList) {
            directionConfig.setRecid(recid++);
            directionConfig.setIaasType(directionConfig.getDirectorCpi().split("_")[0]);
        }
        return resultList;
    }
 }
