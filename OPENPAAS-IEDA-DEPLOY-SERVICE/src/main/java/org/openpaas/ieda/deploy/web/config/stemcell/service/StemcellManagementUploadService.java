package org.openpaas.ieda.deploy.web.config.stemcell.service;

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
import org.openpaas.ieda.deploy.web.config.stemcell.dao.StemcellManagementDAO;
import org.openpaas.ieda.deploy.web.config.stemcell.dto.StemcellManagementDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Service
public class StemcellManagementUploadService {
    
    @Autowired private StemcellManagementDAO dao;
    @Autowired private MessageSource message;
    
    private final static String SEPARATOR = System.getProperty("file.separator");
    private final static String STEMCELL_DIR = LocalDirectoryConfiguration.getStemcellDir();
    private final static String LOCK_DIR = LocalDirectoryConfiguration.getLockDir();
    private final static Logger LOGGER = LoggerFactory.getLogger(StemcellManagementUploadService.class);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 파일 업로드
     * @title : uploadStemcellFile
     * @return : void
    *****************************************************************/
    public void uploadStemcellFile(MultipartHttpServletRequest request, Principal principal) {
        File isKeyFile = null;
        BufferedOutputStream stream = null;
        InputStream instream = null;
        Iterator<String> itr =  request.getFileNames();
        if(itr.hasNext()) {
            MultipartFile mpf = request.getFile(itr.next());
            try {
                byte[] tmp = new byte[8192];
                
                String stemcellFilePath = STEMCELL_DIR+ SEPARATOR + mpf.getOriginalFilename();
                isKeyFile = new File(stemcellFilePath);
                if(isKeyFile.exists() && "true".equalsIgnoreCase(request.getParameter("overlay"))){
                    //1. 파일이 존재하고 덮어쓰기가 체크 되어있을 때
                    boolean delete = isKeyFile.delete();//삭제
                    if(!delete){
                        throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                                message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);

                    }
                }else if(isKeyFile.exists() && "false".equalsIgnoreCase(request.getParameter("overlay"))){
                    //2. 파일이 존재하지만 덮어쓰기가 체크 되어있지 않을 때 
                    throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                            message.getMessage("common.conflict.file.messag", null, Locale.KOREA), HttpStatus.CONFLICT);
                }
                int i =0;
                stream =     new BufferedOutputStream(new FileOutputStream(isKeyFile));
                instream = mpf.getInputStream();
                 while ((i = instream.read(tmp)) >= 0) {
                     stream.write(tmp, 0, i);
                 }
                if(isKeyFile.exists()){
                    StemcellManagementDTO.Regist dto = new StemcellManagementDTO.Regist();
                    dto.setId(Integer.parseInt(request.getParameter("id")));
                    saveStemcellInfo(dto, principal);
                }
            }catch (IOException e) {
                if(isKeyFile.exists()){
                    Boolean check = isKeyFile.delete();
                    if( LOGGER.isDebugEnabled() ){
                        LOGGER.debug("check delete stemcellLock File  : "  + check);
                    }
                }
                throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                        message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
            }finally {
                String originalFileName = mpf.getOriginalFilename();
                String originalFileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                String fileResultName = originalFileName.replace(originalFileExtension, "");
                //lock 파일 삭제
                CommonDeployUtils.deleteFile(LOCK_DIR, fileResultName +"-download.lock");
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        if(LOGGER.isErrorEnabled()){ LOGGER.error(e.getMessage()); }
                    }
                }
            }
        }
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 정보 저장
     * @title : savePublicStemcell
     * @return : void
    *****************************************************************/
    public void saveStemcellInfo(StemcellManagementDTO.Regist dto, Principal principal) {
        SessionInfoDTO userInfo = new SessionInfoDTO(principal);
        if(  dto != null ){
            dto.setUpdateUserId(userInfo.getUserId());
            dto.setDownloadStatus("DOWNLOADED");
            dao.updatePublicStemcellById(dto);
        }else{
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
    }
}
