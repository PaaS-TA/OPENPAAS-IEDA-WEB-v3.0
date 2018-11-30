package org.openpaas.ieda.deploy.web.common.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.web.common.dao.ManifestTemplateVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.parser.ParserException;

final public class CommonDeployUtils {

    final private static Logger LOGGER = LoggerFactory.getLogger(CommonDeployUtils.class);
    final private static String SEPARATOR = System.getProperty("file.separator");
    final private static String KEY_FILE = LocalDirectoryConfiguration.getKeyDir() + SEPARATOR;
    final private static String TEMP_FILE =  LocalDirectoryConfiguration.getTempDir() + SEPARATOR;
    final private static String DEPLOYMENT_FILE = LocalDirectoryConfiguration.getDeploymentDir() + SEPARATOR;

    private CommonDeployUtils() {
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 정규표현식 특수문자 검증 및 문자열 치환하여 문자열을 바꾼다.
     * @title : lineAddSpace
     * @return : String
    *****************************************************************/
    public static String lineAddSpace(String exc, int cnt) {
        String[] lines = exc.split(System.getProperty("line.separator"));
        StringBuffer emptyBuffer = new StringBuffer("");
        for (int i = 0; i < cnt; i++) {
            emptyBuffer.append(" ");
        }
        String empty = emptyBuffer.toString();
        
        StringBuffer resultBuffer = new StringBuffer(""); 
        if (lines.length > 0) {
            for (int i = 0; i < lines.length; i++) {
                String keyValue = lines[i].replace("/\r\n/g", "");
                if (!StringUtils.isEmpty(keyValue)) {
                    if (i == 0) {
                        resultBuffer.append(empty).append(keyValue);
                    } else {
                        resultBuffer.append("\n").append(empty).append(keyValue);
                    }
                }
            }
        }
        String returnString = resultBuffer.toString();
        return returnString;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Manifest 템플릿 파일과 merge하여 deployment 경로에 최종 Manifest 파일 생성
     * @title : setSpiffMerge
     * @return : void
    *****************************************************************/
    public static void setSpiffMerge(String keyFile, String settingFileName, ManifestTemplateVO manifestTemplate, String paastaMonitoringUse) {
        // temp
        String inputFile = TEMP_FILE + settingFileName;
        String deploymentPath = DEPLOYMENT_FILE + settingFileName;
        String keyPath = KEY_FILE + keyFile;

        File settingFile = null;
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        ProcessBuilder builder = new ProcessBuilder();
        if (manifestTemplate.getDeployType().equalsIgnoreCase("bootstrap")) {
            try {
                settingFile = new File(inputFile);
                if (settingFile.exists()) {
                    List<String> cmd = new ArrayList<String>();
                    cmd.add("cat");
                    cmd.add(inputFile);
                    builder.command(cmd);
                    builder.redirectErrorStream(true);
                    Process process = builder.start();
                    inputStream = process.getInputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                    String info = null;
                    StringBuffer deployBuffer = new StringBuffer("");
                    while ((info = bufferedReader.readLine()) != null) {
                        deployBuffer.append(info).append("\n");
                    }
                    String deloymentContent = deployBuffer.toString();
                    if( !deloymentContent.equalsIgnoreCase("") ){
                        IOUtils.write(deloymentContent, new FileOutputStream(deploymentPath), "UTF-8");
                    }
                } else {
                    throw new CommonException("notfound.manifest.exception", "Merge할 File이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
                }
            } catch (FileNotFoundException e) {
                throw new CommonException("ioFileRead.manifest.exception", "Manifest 생성 중 문제가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (IOException e) {
                throw new CommonException("ioFileRead.manifest.exception", "Manifest 생성 중 문제가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
            } finally {
                try {
                    if( bufferedReader != null) {
                        bufferedReader.close();
                    }
                } catch (IOException e) {
                    if( LOGGER.isErrorEnabled() ){
                        LOGGER.error( e.getMessage() );
                    }
                }
            }
        } else {
            try {
                settingFile = new File(inputFile);
                if (settingFile.exists()) {
                    List<String> cmd = new ArrayList<String>();
                    cmd.add("spiff");
                    cmd.add("merge");
                    // generic_manifest_mask.yml
                    if (!StringUtils.isEmpty(manifestTemplate.getCommonBaseTemplate())) {
                        cmd.add(manifestTemplate.getCommonBaseTemplate());
                    }
                    // cf.yml
                    if (!StringUtils.isEmpty(manifestTemplate.getCommonJobTemplate())) {
                        cmd.add(manifestTemplate.getCommonJobTemplate());
                    }
                    // cf_<iaas>_setting_<version>.yml
                    if (!StringUtils.isEmpty(manifestTemplate.getIaasPropertyTemplate())) {
                        cmd.add(manifestTemplate.getIaasPropertyTemplate());
                    }
                    // paasta_option.yml
                    if ( manifestTemplate.getDeployType().equals("BOOTSTRAP")  &&
                            !StringUtils.isEmpty(manifestTemplate.getCommonOptionTemplate())) {
                        if (paastaMonitoringUse.equals("true")) {
                            cmd.add(manifestTemplate.getCommonOptionTemplate());
                        }
                    }
                    // cf_<iaas>_stub_<version>.yml
                    if (!StringUtils.isEmpty(manifestTemplate.getMetaTemplate())) {
                        cmd.add(manifestTemplate.getMetaTemplate());
                    }
                    // cf_<iaas>_network_options.yml
                    if (!StringUtils.isEmpty(manifestTemplate.getOptionNetworkTemplate())) {
                        cmd.add(manifestTemplate.getOptionNetworkTemplate());
                    }
                    // cf_<iaas>_resouce_options.yml
                    if (!StringUtils.isEmpty(manifestTemplate.getOptionResourceTemplate())) {
                        cmd.add(manifestTemplate.getOptionResourceTemplate());
                    }
                    // cf_diego_option.yml
                    if (!StringUtils.isEmpty(manifestTemplate.getOptionEtc())) {
                        cmd.add(manifestTemplate.getOptionEtc());
                    }
                    // paasta_option.yml
                    if ( !manifestTemplate.getDeployType().equals("BOOTSTRAP") && 
                            !StringUtils.isEmpty(manifestTemplate.getCommonOptionTemplate())) {
                        if (paastaMonitoringUse.equals("true")) {
                            cmd.add(manifestTemplate.getCommonOptionTemplate());
                        }
                    }
                    
                    // <iaas>_<deploy_type>_key_<id>.yml
                    if (!(keyFile.equalsIgnoreCase("")) && !StringUtils.isEmpty(keyPath)) {
                        cmd.add(keyPath);// 생성한 key.yml파일 추가
                    }
                    cmd.add(inputFile);
                    builder.command(cmd);
                    builder.redirectErrorStream(true);
                    
                    Process process = builder.start();
                    inputStream = process.getInputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                    String info = null;
                    StringBuffer deployBuffer = new StringBuffer("");
                    while ((info = bufferedReader.readLine()) != null) {
                        deployBuffer.append(info).append("\n");
                    }
                    String deloymentContent = deployBuffer.toString();
                    if( !deloymentContent.equalsIgnoreCase("") ){
                        IOUtils.write(deloymentContent, new FileOutputStream(deploymentPath), "UTF-8");
                    }
                } else {
                    throw new CommonException("notfound.manifest.exception", "Merge할 File이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
                }
            } catch (FileNotFoundException e){
                throw new CommonException("ioFileRead.manifest.exception", "Manifest 생성 중 문제가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (IOException e) {
                throw new CommonException("ioFileRead.manifest.exception", "Manifest 생성 중 문제가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
            } finally {
                try {
                    if( bufferedReader != null) {
                        bufferedReader.close();
                    }
                } catch (IOException e) {
                    if( LOGGER.isErrorEnabled() ){
                        LOGGER.error( e.getMessage() );
                    }
                }
            }
        }
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Yaml Parser
     * @title : yamlParser
     * @return : Map<String,Object>
    *****************************************************************/
    @SuppressWarnings("unchecked")
    public static Map<String, Object> yamlParser(String contents){
        Map<String, Object> object  = null;
        try{
            Yaml yaml = new Yaml();
            object = (Map<String, Object>)yaml.load(contents);
            
        } catch(ParserException e ){
            String errorMessage = getPrintStackTrace(e);
            throw new CommonException("parser.yaml.exception", errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch(Exception e){
            String errorMessage = getPrintStackTrace(e);
            throw new CommonException("server.yaml.exception", errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return object;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : printStackTrace 정보 String 변환
     * @title : getPrintStackTrace
     * @return : String
    *****************************************************************/
    public static String getPrintStackTrace(Exception e){
        StringWriter errors = new StringWriter();
        String[]  split = e.getMessage().split("\n");
        for(  int i=0; i<split.length; i++){
            errors.append(split[i]).append("<br/>");
        }
        return errors.toString();
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 플랫폼 설치 자동화에 업로드된 파일 삭제
     * @title : deleteFile
     * @return : void
    *****************************************************************/
    public static void deleteFile(String path, String fileName){
        Boolean check = false;
        File file = new File(path +SEPARATOR +fileName);
        if( file.exists() ){
            check = file.delete();
        }
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Make sure the "+fileName + " is deleted : " + check);
        }
    }
    
}
