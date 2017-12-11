package org.openpaas.ieda.deploy.web.information.vms.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.deploy.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.deploy.web.config.setting.service.DirectorConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.unitils.thirdparty.org.apache.commons.io.IOUtils;

@Service
public class VmsLogDownloadService {
    
    @Autowired private DirectorConfigService directorConfigService;
    @Autowired MessageSource message;
    final private static int THREAD_SLEEP_TIME = 2 * 1000;
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Agent 및 Job 로그 다운로드 요청
     * @title : doDownloadLog
     * @return : void
    ***************************************************/
    public void  doDownloadLog(String jobName, String index, String deploymentName, String type, HttpServletResponse response){
        int statusCode = 0;
        String taskId = "";
        String logFile = "";
        try{
            //설치 관리자 정보 조회
            DirectorConfigVO defaultDirector = directorConfigService.getDefaultDirector();
            if ( defaultDirector == null ) {
                throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                        message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
            }
            
            //Create Agent/Job Log
            HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
            GetMethod getLogMethod = new GetMethod(DirectorRestHelper.createLogURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(),  deploymentName, jobName, index, type));
            getLogMethod = (GetMethod) DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase) getLogMethod);
            statusCode = httpClient.executeMethod(getLogMethod);
            
            //get taskId
            String[] segments  = getLogMethod.getPath().split("/");
            taskId = segments[segments.length - 1];
            
            Thread.sleep(THREAD_SLEEP_TIME);
            
            //Task status Info
            GetMethod getTaskStaus = new GetMethod(DirectorRestHelper.getTaskStatusURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), taskId));
            getTaskStaus = (GetMethod) DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase) getTaskStaus);
            statusCode = httpClient.executeMethod(getTaskStaus);
            
            Thread.sleep(THREAD_SLEEP_TIME);
            
            if ( statusCode == HttpStatus.OK.value() ){
                JSONObject obj = new JSONObject(getTaskStaus.getResponseBodyAsString());
                String result = obj.get("result").toString();
                // download log by result
                GetMethod getResultOutput = new GetMethod(DirectorRestHelper.getResultOutputURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), result));
                getResultOutput = (GetMethod) DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase) getResultOutput);
                String range = "bytes=" + 0 + "-";
                getResultOutput.setRequestHeader("Range", range);
                
                statusCode = httpClient.executeMethod(getResultOutput);
                if ( statusCode == HttpStatus.OK.value() || statusCode == HttpStatus.PARTIAL_CONTENT.value()){
                    byte[] content = getResultOutput.getResponseBody();
                    Date now = new Date();
                    SimpleDateFormat dataformat = new SimpleDateFormat("yyyymmdd_HHmmss", Locale.KOREA);
                    logFile = jobName+"_"+index+"_"+dataformat.format(now)+"_"+type;
                    response.setContentType("application/octet-stream");
                    response.setHeader("Content-Disposition", "attachment; filename=" + logFile+".tgz");
                    IOUtils.write(content, response.getOutputStream());
                }else{
                    throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                            message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
                }
            }
        }catch(RuntimeException e){
            throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                    message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
                throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                        message.getMessage("common.file.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (JSONException e) {
            throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                    message.getMessage("common.file.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (InterruptedException e) {
            throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                    message.getMessage("common.file.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
