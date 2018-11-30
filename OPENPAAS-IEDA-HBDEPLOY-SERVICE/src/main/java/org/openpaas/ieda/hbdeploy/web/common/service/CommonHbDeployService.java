package org.openpaas.ieda.hbdeploy.web.common.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.common.exception.CommonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

@Service
public class CommonHbDeployService {
    @Autowired private MessageSource message;
    
    
    private final static Logger LOGGER = LoggerFactory.getLogger(CommonHbDeployService.class);
    final private static String DEPLOYMENT_DIR = LocalDirectoryConfiguration.getDeploymentDir();
    final private static String SEPARATOR = System.getProperty("file.separator");
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 디렉터 유무 확인
     * @title : isExistBoshEnvLogin
     * @return : void
    *****************************************************************/
    public void isExistBoshEnvLogin(String directoruuid){
/*        int statusResult = 0;
        try {
            HttpClient client = DirectorRestHelper.getHttpClient(port);
            GetMethod get = new GetMethod(DirectorRestHelper.getStemcellsURI(directorUrl, port)); 
            get = (GetMethod)DirectorRestHelper.setAuthorization(userId, password, (HttpMethodBase)get); 
            statusResult = client.executeMethod(get);
        } catch (Exception e) {
            if( LOGGER.isErrorEnabled() ){ LOGGER.error( e.getMessage() );}
        } 
        return statusResult;*/
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 이종 클라우드 Deployment File 다운로드
     * @title : downloadDeploymentFile
     * @return : void
    ***************************************************/
    public void downloadHbDeploymentFile(String fileName, HttpServletResponse response) {
        String privateDeploymentFilename = fileName.split("<br>")[1];
        String publicDeploymentFilename = fileName.split("<br>")[0];
        
        File privateFile = new File(DEPLOYMENT_DIR + SEPARATOR +privateDeploymentFilename +".yml");
        File publicFile = new File(DEPLOYMENT_DIR + SEPARATOR +publicDeploymentFilename );
        
        try {
            if( privateFile.exists() ){ //파일이 있으면
                //파일 타입 확인
                String mimeType= URLConnection.guessContentTypeFromName(privateFile.getName());
                if( StringUtils.isEmpty(mimeType) ){
                    mimeType = "application/octet-stream";
                }
                response.setContentType(mimeType);
                //웹에 다운로드
                response.setHeader("Content-Disposition", "attachment; filename=" + privateDeploymentFilename + ".yml"); 
                response.setContentLength((int)privateFile.length());
                InputStream inputStream = new BufferedInputStream(new FileInputStream(privateFile)); 
                //파일복사
                
                
                if( publicFile.exists() ){ //파일이 있으면
                    //파일 타입 확인
                    mimeType= URLConnection.guessContentTypeFromName(publicFile.getName());
                    if( StringUtils.isEmpty(mimeType) ){
                        mimeType = "application/octet-stream";
                    }
                    response.setContentType(mimeType);
                    //웹에 다운로드
                    response.setHeader("Content-Disposition", "attachment; filename=" + publicDeploymentFilename); 
                    response.setContentLength((int)publicFile.length());
                    InputStream inputStream2 = new BufferedInputStream(new FileInputStream(publicFile)); 
                    //파일복사
                    FileCopyUtils.copy(inputStream2, response.getOutputStream());
                    FileCopyUtils.copy(inputStream, response.getOutputStream());
                }
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
