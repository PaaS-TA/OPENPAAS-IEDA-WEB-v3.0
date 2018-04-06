package org.openpaas.ieda.deploy.web.config.stemcell.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.common.service.CommonUtils;
import org.openpaas.ieda.deploy.web.common.service.CommonDeployService;
import org.openpaas.ieda.deploy.web.common.service.CommonDeployUtils;
import org.openpaas.ieda.deploy.web.config.stemcell.dao.StemcellManagementDAO;
import org.openpaas.ieda.deploy.web.config.stemcell.dao.StemcellManagementVO;
import org.openpaas.ieda.deploy.web.config.stemcell.dto.StemcellManagementDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class StemcellManagementService {
    
    @Autowired private CommonDeployService commonService;
    @Autowired private StemcellManagementDAO dao;
    @Autowired private MessageSource message;
    
    final private static String TMPDIRECTORY = LocalDirectoryConfiguration.getTmpDir();
    final private static String STEMCELLDIR = LocalDirectoryConfiguration.getStemcellDir();
    final private static String LOCK_DIR=LocalDirectoryConfiguration.getLockDir();
    final private static String SEPARATOR = System.getProperty("file.separator");
    final static private String PUBLIC_STEMCELLS_NEWEST_URL = "https://s3.amazonaws.com"; 
    final static private String PUBLIC_STEMCELLS_OLDEST_URL = "https://bosh-jenkins-artifacts.s3.amazonaws.com";
    final static private String PUBLIC_STEMCELLS_WINDOWS_URL = "https://bosh-windows-stemcells-production.";
    private final static Logger LOGGER = LoggerFactory.getLogger(StemcellManagementService.class);
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 로컬 스템셀과 비교 후 스템셀 목록 조회
     * @title : getPublicStemcellList
     * @return : List<StemcellManagementVO>
    ***************************************************/
    public List<StemcellManagementVO> getPublicStemcellList() {
        List<StemcellManagementVO> list = dao.selectPublicStemcellList();
        if( list != null ){
            for( StemcellManagementVO stemcell : list ){
                if( stemcell.getDownloadStatus() != null ){
                    if( stemcell.getDownloadStatus().toUpperCase().equalsIgnoreCase("DOWNLOADED") ){
                        File stemcellFile = new File(STEMCELLDIR+SEPARATOR +stemcell.getStemcellFileName());
                        if(!stemcellFile.exists() || stemcellFile.length() == 0){
                            StemcellManagementDTO.Delete dto = new StemcellManagementDTO.Delete();
                            dto.setId(stemcell.getId());
                            dto.setStemcellFileName(stemcell.getStemcellFileName());
                            dao.deletePublicStemcell(dto);
                        }
                    }
                }
            }
            list = dao.selectPublicStemcellList();
        }
        return list;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 정보 검사
     * @title : saveStemcellInfoByFilePath
     * @return : StemcellManagementVO
    ***************************************************/
    public StemcellManagementVO saveStemcellInfoByFilePath(StemcellManagementDTO.Regist dto, String testFlag, Principal principal) {
        String fileName = dto.getStemcellFileName();
        String status = "";
        int index = fileName.lastIndexOf(".");
        String lockFile = fileName.substring(0, index);
        Boolean checkLock = commonService.lockFileSet(lockFile +"-download");
        if(!checkLock ){
            throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA), 
                    message.getMessage("common.lock.conflict.message", null, Locale.KOREA), HttpStatus.CONFLICT);
        }
        StemcellManagementVO vo = null;
        if( fileName.indexOf(".tgz") < 0 && fileName.indexOf(".zip") < 0 ){
            status = "error";
            deleteStemcellLockFile(status, dto.getStemcellFileName());
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.extension.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        
        if(Long.parseLong(dto.getStemcellSize()) < 1 || StringUtils.isEmpty(dto.getStemcellFileName())){
            status = "error";
            deleteStemcellLockFile(status, dto.getStemcellFileName());
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        //스템셀 path
        File stemcell = new File(STEMCELLDIR + SEPARATOR + dto.getStemcellFileName());
        
        //스템셀 파일이 존재하고 덮어쓰기 체크가 안되어 있을 경우
        if(stemcell.exists() && "false".equalsIgnoreCase(dto.getOverlayCheck())) {
            status = "conflict";
            throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                    message.getMessage("common.conflict.file.message", null, Locale.KOREA), HttpStatus.CONFLICT);
        }else{
            status = "done";
            // 스템셀 사이즈 format
            long stemcellSize = Long.parseLong(dto.getStemcellSize());
            dto.setStemcellSize(CommonUtils.formatSizeUnit(stemcellSize));
            dto.setDownloadStatus("DOWNLOADING");
            vo = saveDownloadingStemcellInfoByFile(dto, testFlag, principal);
        }
        return vo;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드 시 스템셀 정보 저장
     * @title : saveDownloadingStemcellInfoByFile
     * @return : StemcellManagementVO
    ***************************************************/
    public StemcellManagementVO saveDownloadingStemcellInfoByFile(StemcellManagementDTO.Regist dto, String testFlag, Principal principal) {
        StemcellManagementVO vo = null;
        StemcellManagementVO duplicationCheck = null;
        String fileVersion = "";
        try{
            //중복
            duplicationCheck = dao.selectPublicStemcellInfoByFileName(dto.getStemcellFileName());
            if(dto.getLight().toLowerCase().equalsIgnoreCase("true") || dto.getStemcellFileName().indexOf("light")!=-1){
                fileVersion = dto.getStemcellFileName().split("-")[3];
            }else{
                fileVersion = dto.getStemcellFileName().split("-")[2];
            }
            
            if( duplicationCheck == null || "Y".equalsIgnoreCase(testFlag)){
                dto.setStemcellVersion(fileVersion);
                dto.setCreateUserId(principal.getName());
                dto.setUpdateUserId(principal.getName());
                dao.insertPublicStemcell(dto);
            } else{
                dto.setStemcellVersion(fileVersion);
                dto.setUpdateUserId(principal.getName());
                dto.setId(duplicationCheck.getId());
                dao.updatePublicStemcell(dto);
            }
            vo = dao.selectPublicStemcellById(dto.getId());
        } catch(RuntimeException e){
            throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                    message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch(Exception e){
            throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                    message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return vo;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 다운로드 시 정보 상태 확인 후 정보 저장
     * @title : savePublicStemcellDownLoadInfo
     * @return : StemcellManagementVO
    ***************************************************/
    public StemcellManagementVO checkDownloadInfoOfStemcellByURL(StemcellManagementDTO.Regist dto, String testFlag, Principal principal) {
        StemcellManagementVO vo = null;
        if( dto == null ){
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        File stemcllFile = new File(STEMCELLDIR + SEPARATOR + dto.getStemcellFileName());
        //스템셀 파일이 존재하고 덮어쓰기 체크가 안되어 있을 경우
        if(stemcllFile.exists() && "false".equalsIgnoreCase(dto.getOverlayCheck())) {
            throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                    message.getMessage("common.conflict.file.message", null, Locale.KOREA), HttpStatus.CONFLICT);
        }else{
            if("".equalsIgnoreCase(dto.getStemcellSize()) ||  dto.getStemcellSize().isEmpty()){
                throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                        message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
            }
            
            //create lock file 
            int index = dto.getStemcellFileName().lastIndexOf(".");
            String lockFile = dto.getStemcellFileName().substring(0, index);
            Boolean checkLock = commonService.lockFileSet(lockFile +"-download");
            if( !checkLock ){
                throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA), 
                        message.getMessage("common.lock.conflict.message", null, Locale.KOREA), HttpStatus.CONFLICT);
            
            }
            dto.setStemcellSize(CommonUtils.formatSizeUnit(Long.parseLong(dto.getStemcellSize())));
            dto.setStemcellVersion(dto.getStemcellVersion());
            dto.setStemcellFileName(dto.getStemcellFileName());
            dto.setDownloadStatus("DOWNLOADING");
            vo = saveDownloadingStemcellInfoByFile(dto, testFlag, principal);
        }
        return vo;
    }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : wget을 통해 스템셀 정보 저장
    * @title : getStemcellFileInfoByWget
    * @return : StemcellManagementVO
    ***************************************************/
    public StemcellManagementVO saveStemcellInfoByURL(StemcellManagementDTO.Regist dto, String testFlag, Principal principal){
        if( dto != null ){
            if(StringUtils.isEmpty(dto.getStemcellVersion()) && StringUtils.isEmpty(dto.getStemcellUrl())){
                throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                        message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
            }
        }
        if( dto == null ) {
            dto = new StemcellManagementDTO.Regist();
        }
        String downloadUrlInfo = setStemcellUrlForWget(dto);
        dto.setDownloadLink(downloadUrlInfo);
        return doWgetToGetPublicStemcellInfo(dto, testFlag, principal);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 정보 추출을 위한 wget 실행
     * @title : doWgetToGetPublicStemcellInfo
     * @return : StemcellManagementVO
     ***************************************************/
     public StemcellManagementVO doWgetToGetPublicStemcellInfo(StemcellManagementDTO.Regist dto, String testFlag, Principal principal){
         if( StringUtils.isEmpty(dto.getDownloadLink()) ){
             throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                     message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
         }
         InputStream inputStream = null;
         BufferedReader bufferedReader = null;
         Process process = null;
         String[] search = null;
         StringBuffer accumulatedBuffer = new StringBuffer("");
         String info = null;
         boolean flag = false;
         try{
             //wget 실행
             ProcessBuilder builder = new ProcessBuilder("wget", "--spider","-d", "-P", TMPDIRECTORY, "--content-disposition", dto.getDownloadLink());
             builder.redirectErrorStream(true);
             process = builder.start();
             inputStream = process.getInputStream();
             bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
             String contains = "";
             while ((info = bufferedReader.readLine()) != null){ 
                 accumulatedBuffer.append(info).append("\n");
                 if(dto.getFileType().toLowerCase().equalsIgnoreCase("url") && dto.getStemcellUrl().contains("bosh.io")){
                     contains = "Location:";
                 }else{
                     contains ="https:";
                 }
                 if(info.contains(contains) && !flag){
                     search = info.split("/");
                     dto.setStemcellFileName(search[search.length-1]);
                     dto.setStemcellVersion(setStemcellVersionWithWget(dto));
                     flag = true;
                 }
                 if(info.contains("Content-Length:")){
                     search = info.split(" ");
                     dto.setStemcellSize(search[search.length-1]);
                 }
             }
         }catch(IOException e){
             throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                     message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
         }finally {
             try {
                 if(bufferedReader!= null){
                     bufferedReader.close();
                 }
             } catch (IOException e) {
                 LOGGER.error(e.getMessage());
             }
         }
         return checkDownloadInfoOfStemcellByURL(dto, testFlag, principal);
     }
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : wget을 통해 스템셀 정보를 추출 할 URL 설정
    * @title : getStemcellDownLoadUrlCombination
    * @return : String
    ***************************************************/
    public String setStemcellUrlForWget(StemcellManagementDTO.Regist dto){
        String downloadUrl = "";
        if(dto.getFileType().toLowerCase().equalsIgnoreCase("version")){
            String iaas = setIaasHypervisor(dto);
            String baseUrl = setStemcellDownLoadBaseUrlByVersionType(dto);
            String subUrl = setStemcellDownLoadSubUrlByVersionType(dto);
            if(("centos").equalsIgnoreCase(dto.getOsName().toLowerCase())){
                downloadUrl = baseUrl+SEPARATOR+subUrl+"-"+dto.getStemcellVersion().toLowerCase()+"-"+iaas+"-"+dto.getOsName().toLowerCase()+"-"+dto.getOsVersion().toLowerCase().replace("7.x", "7")+"-go_agent.tgz";
            }else if(("windows").equalsIgnoreCase(dto.getOsName().toLowerCase())){
                downloadUrl = baseUrl+subUrl+"-"+dto.getStemcellVersion().toLowerCase()+"-"+iaas+"-"+dto.getOsName().toLowerCase()+dto.getOsVersion()+"-go_agent.tgz";
            }else{
                downloadUrl = baseUrl+SEPARATOR+subUrl+"-"+dto.getStemcellVersion().toLowerCase()+"-"+iaas+"-"+dto.getOsName().toLowerCase()+"-"+dto.getOsVersion().toLowerCase()+"-go_agent.tgz";
            }
            
        }else if(dto.getFileType().toLowerCase().equalsIgnoreCase("url")){
            downloadUrl = dto.getStemcellUrl();
        }
        return downloadUrl;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : wget 실행 중 조건에 맞는 스템셀 버전 추출
     * @title : doingWgetgetStemcellVersion
     * @return : String
    ***************************************************/
    public String setStemcellVersionWithWget(StemcellManagementDTO.Regist dto){
        String stemcellVersion ="";
        if(dto.getFileType().toLowerCase().equalsIgnoreCase("url") && dto.getStemcellUrl().contains("bosh.io")){
            if(dto.getStemcellFileName().contains("light")){
                stemcellVersion = dto.getStemcellFileName().split("-")[3];
            }else{
                stemcellVersion = dto.getStemcellFileName().split("-")[2];
            }
        }else{
            if(dto.getLight().toLowerCase().equalsIgnoreCase("true") || dto.getStemcellFileName().contains("light")){
                stemcellVersion = dto.getStemcellFileName().split("-")[3];
            }else if(dto.getLight().toLowerCase().equalsIgnoreCase("false")){
                stemcellVersion = dto.getStemcellFileName().split("-")[2];
            }
        }
        return stemcellVersion;
    }
    
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 버전 별 서브 Url 조합
     * @title : setStemcellDownLoadSubUrlByVersionType
     * @return : String
    ***************************************************/
    public String setStemcellDownLoadSubUrlByVersionType(StemcellManagementDTO.Regist dto) {
        String subUrl = "";
            if(dto.getLight().toLowerCase().equalsIgnoreCase("true")){
                subUrl = "light-bosh-stemcell";
            }else{
                subUrl = "bosh-stemcell";
            }
        return subUrl;
    }

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : IaaS 별 하이퍼바이저 설정
     * @title : getIaasHypervisor
     * @return : String
    ***************************************************/
    public String setIaasHypervisor(StemcellManagementDTO.Regist dto) {
        String iaasHypervisor = "";
        String iaas=dto.getIaasType().toLowerCase();
        if( iaas.equalsIgnoreCase("openstack") || iaas.equalsIgnoreCase("google") ){
            iaasHypervisor = iaas +"-kvm";
        } else if( iaas.equalsIgnoreCase("vsphere") ){
            iaasHypervisor = iaas+"-esxi";
        } else if(iaas.equalsIgnoreCase("aws")){
            if(dto.getLight().toLowerCase().equalsIgnoreCase("true")) {
                iaasHypervisor = iaas+"-xen-hvm";
            }else {
                if( Float.parseFloat(dto.getStemcellVersion()) >= 3363 ) {
                    iaasHypervisor = iaas+"-xen-hvm";
                }else {
                    iaasHypervisor = iaas+"-xen";
                }
            }
        } else if(iaas.equalsIgnoreCase("azure")){
            iaasHypervisor = iaas + "-hyperv";
        }
        return iaasHypervisor;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 버전 별 Base Url 조합
     * @title : stemcellVersionTypeDownLoadBaseUrl
     * @return : String
    ***************************************************/
    public String setStemcellDownLoadBaseUrlByVersionType(StemcellManagementDTO.Regist dto) {
        String baseUrl = "";
        try{
            if(Float.parseFloat(dto.getStemcellVersion())>3264){
                if(dto.getLight().toLowerCase().equalsIgnoreCase("true")){//light stemcell
                    if( dto.getIaasType().toLowerCase().equalsIgnoreCase("aws") ){
                        baseUrl = PUBLIC_STEMCELLS_NEWEST_URL+SEPARATOR+"bosh-aws-light-stemcells";
                    }else if( dto.getIaasType().toLowerCase().equalsIgnoreCase("google") ){
                        baseUrl = PUBLIC_STEMCELLS_NEWEST_URL+SEPARATOR+"bosh-gce-light-stemcells";
                    }
                }else{
                    baseUrl = PUBLIC_STEMCELLS_NEWEST_URL+SEPARATOR+"bosh-core-stemcells"+SEPARATOR+dto.getIaasType().toLowerCase();
                }
            }else if(dto.getOsName().equalsIgnoreCase("windows")){
                baseUrl = PUBLIC_STEMCELLS_WINDOWS_URL+PUBLIC_STEMCELLS_NEWEST_URL.substring(8)+SEPARATOR;
            }else{
                baseUrl = PUBLIC_STEMCELLS_OLDEST_URL+SEPARATOR+"bosh-stemcell"+SEPARATOR+dto.getIaasType().toLowerCase();
            }
        }catch(NumberFormatException e){
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        return baseUrl;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Exception 발생 시 lock 파일 삭제 
     * @title : deleteLockFile
     * @return : Boolean
    ***************************************************/
    public void deleteStemcellLockFile(String status,String  fileName){
        if( status.equalsIgnoreCase("error")){
            //lock file delete
            if( !StringUtils.isEmpty(fileName) ){
                int index = fileName.lastIndexOf(".");
                String lockFileName = fileName.substring(0, index)+"-download.lock";
                CommonDeployUtils.deleteFile(LOCK_DIR, lockFileName);
            }
        }
    }

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 삭제
     * @title : deletePublicStemcell
     * @return : Boolean
    ***************************************************/
    public void deletePublicStemcell(StemcellManagementDTO.Delete dto) {
        //1. 스템셀 데이터 삭제
        dao.deletePublicStemcell(dto);
        //2. lock 파일 삭제
        int index = dto.getStemcellFileName().indexOf(".tgz");
        String lockFileName = dto.getStemcellFileName().substring(0, index) + "-download.lock";
        CommonDeployUtils.deleteFile(LOCK_DIR, lockFileName); //lock 파일 삭제
        
        //스템셀 파일 삭제
        File file = new File(STEMCELLDIR + SEPARATOR + dto.getStemcellFileName());
        if(file.exists()){ 
            CommonDeployUtils.deleteFile(STEMCELLDIR, dto.getStemcellFileName());
            dao.deletePublicStemcell(dto);
        }
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 공통 로컬 스템셀 콤보
     * @title : getLocalStemcellList
     * @return : List<StemcellManagementVO>
    ***************************************************/
    public List<StemcellManagementVO> getLocalStemcellList(String iaas){
        List<StemcellManagementVO> list = dao.selectLocalStemcellListByIaas(iaas);
        if( list != null ){
            for( StemcellManagementVO stemcell : list ){
                if( stemcell.getDownloadStatus() != null && stemcell.getDownloadStatus().equalsIgnoreCase("DOWNLOADED")  ){
                    File stemcellFile = new File(STEMCELLDIR + SEPARATOR + stemcell.getStemcellFileName());
                    if(!stemcellFile.exists() || stemcellFile.length() == 0){
                        StemcellManagementDTO.Delete dto = new StemcellManagementDTO.Delete();
                        dto.setId(stemcell.getId());
                        dto.setStemcellFileName(stemcell.getStemcellFileName());
                        dao.deletePublicStemcell(dto);
                    }
                }
            }
            list = dao.selectLocalStemcellListByIaas(iaas);
        }
        return list;
    }
}
