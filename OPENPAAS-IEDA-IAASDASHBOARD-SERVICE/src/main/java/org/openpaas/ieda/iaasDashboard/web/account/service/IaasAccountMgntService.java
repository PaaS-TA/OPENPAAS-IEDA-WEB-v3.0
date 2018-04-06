
package org.openpaas.ieda.iaasDashboard.web.account.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.security.Principal;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.common.web.security.SessionInfoDTO;
import org.openpaas.ieda.iaasDashboard.api.account.IaasAccountMgntApiService;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntDAO;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;
import org.openpaas.ieda.iaasDashboard.web.account.dto.IaasAccountMgntDTO;
import org.openpaas.ieda.iaasDashboard.web.common.service.CommonIaasService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Service
public class IaasAccountMgntService {
    @Autowired IaasAccountMgntDAO dao;
    @Autowired MessageSource message;
    @Autowired IaasAccountMgntApiService api;
    
    final private static String IAAS_AWS = "AWS";
    final private static String IAAS_OPENSTACK = "OPENSTACK";
    final private static String IAAS_GOOGLE = "GOOGLE";
    final private static String IAAS_VSPHERE = "VSPHERE";
    final private static String IAAS_AZURE = "AZURE";
    
    final private static String SEPARATOR = System.getProperty("file.separator");
    final private static String JSON_KEY_DIR=LocalDirectoryConfiguration.getKeyDir();
    final private static Logger LOGGER = LoggerFactory.getLogger(IaasAccountMgntService.class);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 전체 Iaas Account 목록 정보 조회
     * @title : getAllIaasAccountInfoList
     * @return : List<IaasAccountMgntVO>
    *****************************************************************/
    public List<IaasAccountMgntVO> getAllIaasAccountInfoList( Principal principal){
        return dao.selectAllIaasAccountInfoList(principal.getName());
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 인프라 별 계정 개수 조회
     * @title : getIaasAccountCount
     * @return : HashMap<String, Object>
    ***************************************************/
    public HashMap<String, Integer> getIaasAccountCount(Principal principal){
        return dao.selectIaasAccountCount(principal.getName());
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 해당 인프라 계정 정보 목록 조회
     * @title : getIaasAccountMgntInfoList
     * @return : List<IaaSAccountMgntVO>
    ***************************************************/
    public List<IaasAccountMgntVO> getIaasAccountInfoList(String iaasType, Principal principal){
        return dao.selectIaasAccountInfoList(principal.getName(), iaasType);
    }
  
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 해당 인프라 계정 상세 정보 조회
     * @title : getIaasAccontMgntInfo
     * @return : IaasAccountMgntVO
    ***************************************************/
    public IaasAccountMgntVO getIaasAccountInfo(String iaasType, int id, Principal principal){
       SessionInfoDTO session = new SessionInfoDTO(principal);
       return dao.selectIaasAccountInfo(session.getUserId(), iaasType, id);
    }
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 인프라 계정 정보 등록 및 수정
     * @title : saveIaasAccountInfo
     * @return : void
    ***************************************************/
    public void saveIaasAccountInfo(String iaasType, IaasAccountMgntDTO dto, HttpServletRequest request , Principal principal){
        HttpSession session = request.getSession();
        PrivateKey privateKey = (PrivateKey) session.getAttribute("__rsaPrivateKey__");
        CommonIaasService common = new CommonIaasService();
        IaasAccountMgntVO vo =null;
        
        if (privateKey == null) {
            throw new CommonException(message.getMessage("common.rsa.privateKey.exception.code", null, Locale.KOREA), 
                    message.getMessage("common.rsa.privateKey.exception.message",null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        String commonAccessUser = null;
        String commonAccessSecret =null;
        if( !StringUtils.isEmpty(dto.getCommonAccessUser()) ){
            commonAccessUser = common.decryptRsa(privateKey, dto.getCommonAccessUser());
        }
        if( !StringUtils.isEmpty(dto.getCommonAccessSecret()) ){
            commonAccessSecret = common.decryptRsa(privateKey, dto.getCommonAccessSecret());
        }
        
        //등록
        if( StringUtils.isEmpty(dto.getId())){
            vo =  new IaasAccountMgntVO();
            vo.setIaasType(dto.getIaasType());
            vo.setAccountName( dto.getAccountName() );
            vo.setCommonProject(dto.getCommonProject());
            vo.setGoogleJsonKeyPath(dto.getGoogleJsonKeyPath());
            vo.setCommonAccessEndpoint(dto.getCommonAccessEndpoint());
            vo.setCommonAccessUser( commonAccessUser );
            vo.setCommonAccessSecret( commonAccessSecret );
            vo.setOpenstackKeystoneVersion(dto.getOpenstackKeystoneVersion());
            vo.setCommonTenant(dto.getCommonTenant());
            vo.setAzureSubscriptionId(dto.getAzureSubscriptionId());
            vo.setCreateUserId(principal.getName());
            vo.setDefaultYn("N");
            vo.setTestFlag(dto.getTestFlag());
            //계정 중복체크
            int checkAccountCnt = dao.selectIaasAccountDuplicationByInfraAccount(vo);
            if(checkAccountCnt >  0){
                throw new CommonException(message.getMessage("iaas.accountManagement.conflict.code.exception", null, Locale.KOREA), 
                        message.getMessage("iaas.accountManagement.conflict.message.exception",null, Locale.KOREA), HttpStatus.CONFLICT);
            }
            
            //계정 name 중복체크
            int checkAccountNameCnt = dao.selectIaasAccountDuplicationByAccountName(vo);
            if(checkAccountNameCnt >  0){
                throw new CommonException(message.getMessage("iaas.accountManagement.conflict.code.exception", null, Locale.KOREA), 
                        message.getMessage("iaas.accountManagement.accountName.conflict.message.exception",null, Locale.KOREA), HttpStatus.CONFLICT);
            }
        }else{
            vo =  dao.selectIaasAccountInfo(principal.getName(), iaasType, Integer.parseInt(dto.getId()));
        }
        vo.setOpenstackDomain(dto.getOpenstackDomain());
        vo.setCommonTenant(dto.getCommonTenant());
        vo.setCommonProject(dto.getCommonProject());
        vo.setUpdateUserId(principal.getName());
        vo.setCommonAccessSecret( commonAccessSecret );
        vo.setGoogleJsonKeyPath(dto.getGoogleJsonKeyPath());
        vo.setAzureSubscriptionId(dto.getAzureSubscriptionId());
        
        //API를 통해 계정 존재 확인
        boolean flag = false;
        if( iaasType.toUpperCase().trim().equalsIgnoreCase(IAAS_AWS) ){
            flag = api.getAccountInfoFromAWS(commonAccessUser, commonAccessSecret );
        } else  if( iaasType.toUpperCase().trim().equalsIgnoreCase(IAAS_OPENSTACK) ){
            if( vo.getOpenstackKeystoneVersion().equalsIgnoreCase("v2") ){
                flag = api.getAccountInfoFromOpenstackV2(vo.getCommonAccessEndpoint(), vo.getCommonTenant(), commonAccessUser, commonAccessSecret);
            }else{
                flag = api.getAccountInfoFromOpenstackV3(vo.getCommonAccessEndpoint(), vo.getOpenstackDomain(), vo.getCommonProject(),commonAccessUser, commonAccessSecret);
            }
        } else if( iaasType.toUpperCase().trim().equalsIgnoreCase(IAAS_VSPHERE) ){
            flag = api.getAccountInfoFromVsphere(vo.getCommonAccessEndpoint(), commonAccessUser, commonAccessSecret);
        }else if( iaasType.toUpperCase().trim().equalsIgnoreCase(IAAS_GOOGLE) ){
            flag = api.getAccountInfoFromGoogle(vo.getGoogleJsonKeyPath(), vo.getCommonProject());
        }else if( iaasType.toUpperCase().trim().equalsIgnoreCase(IAAS_AZURE) ){
            flag = api.getAccountInfoFromAzure(commonAccessUser,  vo.getCommonTenant(),commonAccessSecret, vo.getAzureSubscriptionId());
        }
       
        if( flag ){
            if( StringUtils.isEmpty(dto.getId())){
                dao.insertIaasAccountInfo(vo);
            }else{
                dao.updateIaasAccountInfo(vo);
            }
            session.removeAttribute("__rsaPrivateKey__"); //항상 새로운 키를 받도록 키의 재사용을 막는다. 
        }else{
            throw new CommonException(
                    message.getMessage("iaas.accountManagement.connect.code.exception", null, Locale.KOREA), message.getMessage("iaas.accountManagement.connect.message.exception", null, Locale.KOREA), HttpStatus.NOT_FOUND);
        }
    }

    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 인프라 계정 정보 삭제
     * @title : deleteIaasAccountInfo
     * @return : void
    ***************************************************/
    public void deleteIaasAccountInfo(IaasAccountMgntDTO dto, Principal principal){
       SessionInfoDTO session = new SessionInfoDTO(principal);
       if( StringUtils.isEmpty(dto.getId())  ){
           throw new CommonException(
                   message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA), message.getMessage("common.database.internalServerError.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
       }
       dao.deleteIaasAccountInfo(session.getUserId(),Integer.parseInt(dto.getId()));
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Google Json 키 파일 목록 조회
     * @title : getJsonKeyFileList
     * @return : List<String>
    *****************************************************************/
    public List<String> getJsonKeyFileList(){
        File keyPathFile = new File(JSON_KEY_DIR);
        if( !keyPathFile.isDirectory() ) {
            return null;
        }
        
        List<String> localFiles = null;
        File[] listFiles = keyPathFile.listFiles();
        if(listFiles != null){
            for (File file : listFiles) {
                if(!file.getName().toLowerCase().endsWith(".json")) {
                    continue;
                }

                if ( localFiles == null ) {
                    localFiles = new ArrayList<String>();
                }

                localFiles.add(file.getName());
            }
        }
        return localFiles;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : google Json 키 파일 업로드
     * @title : uploadJsonKeyFile
     * @return : void
    *****************************************************************/
    public void uploadJsonKeyFile( MultipartHttpServletRequest request ){
        Iterator<String> itr =  request.getFileNames();
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("request.getFileName : " + request.getFileNames().toString());
        }
        if(itr.hasNext()) {
            BufferedOutputStream stream = null;
            MultipartFile mpf = request.getFile(itr.next());
            try {
                String keyFilePath = JSON_KEY_DIR + SEPARATOR + mpf.getOriginalFilename();
                byte[] bytes = mpf.getBytes();
                File isKeyFile = new File(keyFilePath);
                stream = new BufferedOutputStream(new FileOutputStream(isKeyFile));
                stream.write(bytes);
                
                boolean result = isKeyFile.setWritable(false, false);
                LOGGER.debug("isKeyFile.setWritable : " + result);
                isKeyFile.setExecutable(false, false);
                isKeyFile.setReadable(false, true);
                Set<PosixFilePermission> pfp = new HashSet<PosixFilePermission>();
                pfp.add(PosixFilePermission.OWNER_READ);
                Files.setPosixFilePermissions(Paths.get(keyFilePath), pfp);
                
            } catch (IOException e) {
                if(LOGGER.isErrorEnabled()){ LOGGER.error(e.getMessage()); }
            } finally{
                try {
                    if( stream != null ) {
                        stream.close();
                    }
                } catch (IOException e) {
                    if( LOGGER.isErrorEnabled() ){ LOGGER.error( e.getMessage() ); }
                }
            }
        }
    }
}
