package org.openpaas.ieda.hbdeploy.web.config.release.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.Iterator;
import java.util.Locale;

import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.common.web.security.SessionInfoDTO;
import org.openpaas.ieda.deploy.web.common.service.CommonDeployUtils;
import org.openpaas.ieda.hbdeploy.web.config.release.dao.HbReleaseManagementDAO;
import org.openpaas.ieda.hbdeploy.web.config.release.dto.HbReleaseManagementDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Service
public class HbReleaseManagementUploadService {

    @Autowired private HbReleaseManagementDAO dao;
    @Autowired private MessageSource message;
    
    final private static String SEPARATOR = System.getProperty("file.separator");
    final private static String RELEASE_DIR =LocalDirectoryConfiguration.getReleaseDir();
    final private static String LOCK_DIR=LocalDirectoryConfiguration.getLockDir();
    final private static Logger LOGGER = LoggerFactory.getLogger(HbReleaseManagementUploadService.class);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : hybrid 릴리즈 파일 업로드
     * @title : uploadHybridReleaseFile
     * @return : void
    *****************************************************************/
    public void uploadHybridReleaseFile(MultipartHttpServletRequest request, Principal principal){
        File isKeyFile = null;
        BufferedOutputStream stream = null;
        InputStream instream = null;
        Iterator<String> itr =  request.getFileNames();
        
        if(itr.hasNext()) {
            MultipartFile mpf = request.getFile(itr.next());
            try {
                byte[] tmp = new byte[8192];
                
                String releaseFilePath = RELEASE_DIR + SEPARATOR + mpf.getOriginalFilename();
                isKeyFile = new File(releaseFilePath);
                if(isKeyFile.exists() && "true".equalsIgnoreCase(request.getParameter("overlay"))){
                    //1. 파일이 존재하고 덮어쓰기가 체크 되어있을 때
                    CommonDeployUtils.deleteFile(RELEASE_DIR, mpf.getOriginalFilename());
                }else if(isKeyFile.exists() && "false".equalsIgnoreCase(request.getParameter("overlay"))){
                    //2. 파일이 존재하지만 덮어쓰기가 체크 되어있지 않을 때 
                    throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                            message.getMessage("common.conflict.file.message", null, Locale.KOREA), HttpStatus.CONFLICT);
                }
                
                int i=0;
                stream= new BufferedOutputStream(new FileOutputStream(isKeyFile));
                instream= mpf.getInputStream();
                 while ((i = instream.read(tmp)) >= 0) {
                     stream.write(tmp, 0, i);
                 }
                 
                if(isKeyFile.exists()){
                    HbReleaseManagementDTO.Regist dto = new HbReleaseManagementDTO.Regist();
                    dto.setId(Integer.parseInt(request.getParameter("id")));
                    saveHybridRelease(dto, principal);
                }
            }catch (IOException e) {
                if(isKeyFile.exists()){
                    CommonDeployUtils.deleteFile(RELEASE_DIR, mpf.getOriginalFilename());
                }
                throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                        message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
            }finally {
                String originalFileExtension = mpf.getOriginalFilename().substring(mpf.getOriginalFilename().lastIndexOf("."));
                String fileResultName = mpf.getOriginalFilename().replace(originalFileExtension, "") + "-download.lock";
                //lock 파일 삭제
                CommonDeployUtils.deleteFile(LOCK_DIR, fileResultName);
                
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        if( LOGGER.isErrorEnabled() ){
                            LOGGER.error( e.getMessage() );
                        }
                    }
                }
            }
        }
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : hybrid 릴리즈 다운로드 정보 저장
     * @title : saveHybridRelese
     * @return : void
    *****************************************************************/
    public void saveHybridRelease(HbReleaseManagementDTO.Regist dto, Principal principal) {
        SessionInfoDTO userInfo = new SessionInfoDTO(principal);
        dto.setUpdateUserId(userInfo.getUserId());
        dto.setDownloadStatus("DOWNLOADED");
        dao.updateHybridReleaseById(dto);
    }
}
