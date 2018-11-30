package org.openpaas.ieda.deploy.web.config.stemcell.service;

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
import org.openpaas.ieda.common.web.security.SessionInfoDTO;
import org.openpaas.ieda.deploy.web.common.service.CommonDeployUtils;
import org.openpaas.ieda.deploy.web.config.stemcell.dao.StemcellManagementDAO;
import org.openpaas.ieda.deploy.web.config.stemcell.dao.StemcellManagementVO;
import org.openpaas.ieda.deploy.web.config.stemcell.dto.StemcellManagementDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class StemcellManagementDownloadAsyncService {
    
    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private StemcellManagementDAO dao;
    @Autowired private MessageSource message;
    
    final private static Logger LOGGER = LoggerFactory.getLogger(StemcellManagementDownloadAsyncService.class);
    final private static String SEPARATOR = System.getProperty("file.separator");
    final private static String LOCK_DIR=LocalDirectoryConfiguration.getLockDir();
    final private static String TMPDIRECTORY = LocalDirectoryConfiguration.getTmpDir();
    final private static String STEMCELL_DIR = LocalDirectoryConfiguration.getStemcellDir();
    final private static String MESSAGE_ENDPOINT = "/config/stemcell/regist/download/logs";
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : wget을 통해 다운로드된 스템셀 상태 저장
     * @title : saveStemcellDownLoadStatus
     * @return : void
    *****************************************************************/
    public void saveStemcellDownLoadStatus(StemcellManagementDTO.Regist dto, StemcellManagementVO result, Principal principal, Boolean downloadResult) {
        //1. 저장된 스템셀 정보 조회
        String status = "";
        File tmpFile = new File(TMPDIRECTORY+ SEPARATOR + result.getStemcellFileName());
        File stemcellFile = new File(STEMCELL_DIR + SEPARATOR + result.getStemcellFileName());
        
        if(downloadResult){
            dto.setDownloadStatus("DOWNLOADED");
        }else{
            status = "error";
            //tmp file 삭제
            CommonDeployUtils.deleteFile(TMPDIRECTORY, result.getStemcellFileName());
            deleteLockFile(status, result.getStemcellFileName());
            throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                    message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(stemcellFile.exists() && "true".equalsIgnoreCase(dto.getOverlayCheck())){
            CommonDeployUtils.deleteFile(STEMCELL_DIR, result.getStemcellFileName());//스템셀 파일 삭제
        }
        //덮어쓰기 불가능
        if(stemcellFile.exists() && "false".equalsIgnoreCase(dto.getOverlayCheck()) ){
            deleteLockFile(status, dto.getStemcellFileName());//lock 파일 삭제
            throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                    message.getMessage("common.conflict.file.message", null, Locale.KOREA), HttpStatus.CONFLICT);
        }else{//덮어쓰기 가능.
            try {
                FileUtils.moveFile(tmpFile,stemcellFile);
                saveDownloadStemcellInfo(dto, principal);
            } catch (IOException e) {
                deleteLockFile(status, dto.getStemcellFileName());//LOCK 파일 삭제
                CommonDeployUtils.deleteFile(STEMCELL_DIR, result.getStemcellFileName());//스템셀 파일 삭제
                
                LOGGER.error(e.getMessage());
                throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                        message.getMessage("common.conflict.file.message", null, Locale.KOREA), HttpStatus.CONFLICT);
            }finally{
                deleteLockFile("done",result.getStemcellFileName());
                messagingTemplate.convertAndSendToUser(principal.getName() ,MESSAGE_ENDPOINT, dto.getId()+"/done");
            }
        }
    }
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 스템셀 정보 조회
    * @title : getStemcellInfo
    * @return : StemcellManagementVO
    ***************************************************/
    public StemcellManagementVO getStemcellInfo(StemcellManagementDTO.Regist dto){
        StemcellManagementVO result = dao.selectPublicStemcellById(dto.getId());
        if(result == null || StringUtils.isEmpty(result.getStemcellFileName())){
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        return result;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : wget을 통한 시스템 릴리즈 다운로드
    * @title : doWgetStemcellDownload
    * @return : void
    ***************************************************/
    public void doWgetStemcellDownload(StemcellManagementDTO.Regist dto, Principal principal){
        Boolean downloadInfo = false;
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        String info = null;
        StemcellManagementVO result = getStemcellInfo(dto);
        try{
            //2. wget을 통해 스템셀 다운로드
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
                    messagingTemplate.convertAndSendToUser(principal.getName() ,MESSAGE_ENDPOINT, result.getId()+"/"+m.group());
                    if( m.group().equals("100%") ){
                        downloadInfo = true;
                        break;
                    }
                }
            }
        } catch(IOException e){
            downloadInfo = false;
        } finally {
            try {
                if(bufferedReader != null){
                    bufferedReader.close();
                }
            } catch (IOException e) {
                downloadInfo = false;
            }
        }
        saveStemcellDownLoadStatus(dto, result, principal,downloadInfo);
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 다운로드 정보 저장
     * @title : savePublicStemcell
     * @return : void
    *****************************************************************/
    public void saveDownloadStemcellInfo(StemcellManagementDTO.Regist dto, Principal principal) {
        SessionInfoDTO sessionInfo = new SessionInfoDTO(principal);
        StemcellManagementVO result = dao.selectPublicStemcellById(dto.getId());
        
        if(result != null){
            dto.setStemcellFileName(result.getStemcellFileName());
            dto.setStemcellSize(result.getSize());
            dto.setUpdateUserId(sessionInfo.getUserId());
            dao.updatePublicStemcellById(dto);
        }
    }
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 다운로드 method 비동기 호출
     * @title : stemcellDownloadAsync
     * @return : void
    *****************************************************************/
    @Async
    public void stemcellDownloadAsync(StemcellManagementDTO.Regist dto, Principal principal) {
        doWgetStemcellDownload(dto, principal);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Exception 발생 시 lock 파일 삭제 
     * @title : deleteLockFile
     * @return : void
    *****************************************************************/
    public void deleteLockFile(String status,String  fileName){
        if( status.equalsIgnoreCase("error") || status.equalsIgnoreCase("done")){
            //lock file delete
            if( !StringUtils.isEmpty(fileName) ){
                int index = fileName.lastIndexOf(".");
                String lockFileName = fileName.substring(0, index)+"-download.lock";
                CommonDeployUtils.deleteFile(LOCK_DIR, lockFileName);
            }
        }
    }
}
