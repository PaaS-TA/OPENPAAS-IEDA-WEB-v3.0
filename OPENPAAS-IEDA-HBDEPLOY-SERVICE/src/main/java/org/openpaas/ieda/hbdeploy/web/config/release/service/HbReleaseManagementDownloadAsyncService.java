package org.openpaas.ieda.hbdeploy.web.config.release.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.web.common.service.CommonDeployUtils;
import org.openpaas.ieda.hbdeploy.web.config.release.dao.HbReleaseManagementDAO;
import org.openpaas.ieda.hbdeploy.web.config.release.dao.HbReleaseManagementVO;
import org.openpaas.ieda.hbdeploy.web.config.release.dto.HbReleaseManagementDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class HbReleaseManagementDownloadAsyncService {

    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private HbReleaseManagementDAO dao;
    @Autowired private MessageSource message;
    
    final private static String SEPARATOR = System.getProperty("file.separator");
    final private static String RELEASEDIRECTORY = LocalDirectoryConfiguration.getReleaseDir();
    final private static String LOCK_DIR = LocalDirectoryConfiguration.getLockDir();
    final private static String TMPDIRECTORY = LocalDirectoryConfiguration.getTmpDir();
    final private static String MESSAGE_ENDPOINT = "/config/hbRelease/regist/download/logs"; 
    final private static Logger LOGGER = LoggerFactory.getLogger(HbReleaseManagementDownloadAsyncService.class);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : wget을 이용하여 시스템 릴리즈 다운로드 파일 검사
     * @title : checkSystemReleaseDownloadFile
     * @return : void
    *****************************************************************/
    public void checkHybridReleaseDownloadFile(HbReleaseManagementDTO.Regist dto, HbReleaseManagementVO result, Principal principal, Boolean downloadFlag) {
        File tmpFile = new File(TMPDIRECTORY+ SEPARATOR + result.getReleaseFileName());
        File releseFile = new File(RELEASEDIRECTORY + SEPARATOR + result.getReleaseFileName());
        if(downloadFlag){
            dto.setDownloadStatus("DOWNLOADED");
        }else{
            if(tmpFile.exists()){
                CommonDeployUtils.deleteFile(TMPDIRECTORY, result.getReleaseFileName());
            }
            deleteLockFile(result.getReleaseFileName());
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        //1. 저장된 릴리즈 정보 조회
        if(releseFile.exists() && "true".equalsIgnoreCase(dto.getOverlayCheck())){
            CommonDeployUtils.deleteFile(RELEASEDIRECTORY, result.getReleaseFileName());
        }
        //덮어쓰기 불가능
        if(releseFile.exists() && "false".equalsIgnoreCase(dto.getOverlayCheck()) ){
            deleteLockFile( result.getReleaseFileName() );
            throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                    message.getMessage("common.conflict.file.message", null, Locale.KOREA), HttpStatus.CONFLICT);
        }else{//덮어쓰기 가능.
            try {
                FileUtils.moveFile(tmpFile,releseFile);
                saveHybridReleseInfo(dto, principal);
            } catch (IOException e) {
                CommonDeployUtils.deleteFile(RELEASEDIRECTORY, result.getReleaseFileName());
                throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                        message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
            }finally{
                deleteLockFile( result.getReleaseFileName() );
                messagingTemplate.convertAndSendToUser(principal.getName() ,MESSAGE_ENDPOINT, dto.getId()+"/done");
            }
        }
        
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 릴리즈 정보 조회
    * @title : getHybridReleaseInfo
    * @return : HbReleaseManagementVO
    ***************************************************/
    public HbReleaseManagementVO getHybridReleaseInfo(HbReleaseManagementDTO.Regist dto){
        HbReleaseManagementVO result = dao.selectHybridReleaseById(dto.getId());
        if(result == null || StringUtils.isEmpty(result.getReleaseFileName())){
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        return result;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : wget을 통한 릴리즈 다운로드
     * @title : doDownloadReleaseByWget
     * @return : void
    *****************************************************************/
    public void doDownloadReleaseByWget(HbReleaseManagementDTO.Regist dto, Principal principal){
        HbReleaseManagementVO result = getHybridReleaseInfo(dto);
        Boolean downloadFlag = false;
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        String info = null;
        try{
            //2. wget을 통해 릴리즈 다운로드
            ProcessBuilder builder = new ProcessBuilder("wget", "-d", "-P", TMPDIRECTORY, "--content-disposition", result.getDownloadLink());
            builder.redirectErrorStream(true);
            Process process = builder.start();
            
            inputStream = process.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
            
            //2.2 실행 출력하는 로그를 읽어온다.
            while ((info = bufferedReader.readLine()) != null){ 
                Pattern pattern = Pattern.compile("\\d+\\%");
                Matcher m = pattern.matcher(info);
                if(m.find()){
                    messagingTemplate.convertAndSendToUser(principal.getName() ,MESSAGE_ENDPOINT, dto.getId()+"/"+m.group());
                    if( m.group().equals("100%") ){
                        downloadFlag = true;
                        dto.setDownloadStatus("DOWNLOADED");
                        break;
                    }
                }
            }
        } catch(IOException e){
            throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                    message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            try {
                if(bufferedReader != null){
                    bufferedReader.close();
                }
            } catch (IOException e) {
                if(LOGGER.isErrorEnabled()) {
                    LOGGER.error( e.getMessage() );
                }
            }
        }
        checkHybridReleaseDownloadFile(dto, result,principal, downloadFlag);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 릴리즈 lock 파일 삭제 
     * @title : deleteLockFile
     * @return : Boolean
    *****************************************************************/
    public void deleteLockFile(String fileName){
        int index = fileName.indexOf(".tgz");
        String lockFile = fileName.substring(0,index) + "-download.lock";
        CommonDeployUtils.deleteFile(LOCK_DIR, lockFile);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 릴리즈 다운로드 정보 저장
     * @title : saveHybridReleseInfo
     * @return : void
    *****************************************************************/
    public void saveHybridReleseInfo(HbReleaseManagementDTO.Regist dto, Principal principal){
        HbReleaseManagementVO result = dao.selectHybridReleaseById(dto.getId());
        
        if(result != null){
            dto.setReleaseFileName(result.getReleaseFileName());
            dto.setReleaseSize(result.getReleaseSize());
            dto.setUpdateUserId(principal.getName());
            dao.updateHybridReleaseById(dto);
        }else{
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 비동기로 HybridReleaseDownload 메소드 호출
     * @title : releaseDownloadAsync
     * @return : void
    *****************************************************************/
    @Async
    public void releaseDownloadAsync(HbReleaseManagementDTO.Regist dto, Principal principal){
        doDownloadReleaseByWget(dto, principal);
    }
}
