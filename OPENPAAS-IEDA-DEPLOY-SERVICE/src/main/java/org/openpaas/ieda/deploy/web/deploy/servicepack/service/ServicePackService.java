package org.openpaas.ieda.deploy.web.deploy.servicepack.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.List;
import java.util.Locale;

import javax.transaction.Transactional;

import org.apache.commons.io.IOUtils;
import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.web.deploy.servicepack.dao.ServicePackDAO;
import org.openpaas.ieda.deploy.web.deploy.servicepack.dao.ServicePackVO;
import org.openpaas.ieda.deploy.web.deploy.servicepack.dto.ServicePackParamDTO;
import org.openpaas.ieda.deploy.web.information.manifest.dao.ManifestDAO;
import org.openpaas.ieda.deploy.web.information.manifest.dao.ManifestVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ServicePackService {
    @Autowired ServicePackDAO dao;
    @Autowired ManifestDAO manifestDao;
    @Autowired MessageSource message;
    
    final private static String SEPARATOR = System.getProperty("file.separator");
    final private static String MANIFEST_DIRECTORY = LocalDirectoryConfiguration.getManifastDir() + SEPARATOR;
    final private static String DEPLOYMENT_DIR = LocalDirectoryConfiguration.getDeploymentDir() + SEPARATOR;

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스팩 전체 목록 조회
     * @title : getServicePackList
     * @return : List<ServicePackVO>
    ***************************************************/
    public List<ServicePackVO> getServicePackList(String iaas) {
        List<ServicePackVO> vo = dao.selectServicePackInfo(iaas.toUpperCase());
        return vo;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스팩 정보 저장
     * @title : saveServicePackInfo
     * @return : ServicePackVO
    ***************************************************/
    @Transactional
    public ServicePackVO saveServicePackInfo(ServicePackParamDTO dto, Principal principal) {
        ServicePackVO vo = null;
        ManifestVO manifestVo =  manifestDao.selectManifestInfoByDeployName(dto.getDeploymentName());
        try{
            if(dto.getId() == 0 ) {
                vo = new ServicePackVO();
                vo.setCreateUserId(principal.getName());
                vo.setUpdateUserId(principal.getName());
                vo.setDeploymentFile(dto.getDeploymentFile());
                vo.setDeploymentName(dto.getDeploymentName());
                vo.setIaas(dto.getIaas());
                dao.insertServicePackInfo(vo);
                if(manifestVo != null){
                    String deployStatus = message.getMessage("common.deploy.status.processing", null, Locale.KOREA);
                    manifestVo.setDeployStatus(deployStatus);
                    manifestDao.updateManifestInfo(manifestVo);
                }
            }else{
                vo = dao.selectServicePackDetailInfo(dto.getId());
                vo.setDeploymentFile(manifestVo.getFileName());
                vo.setDeploymentName(dto.getDeploymentName());
                dao.updateServicePackInfo(vo);
            }
        } catch(DuplicateKeyException e){
            throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                    message.getMessage("common.conflict.file.message", null, Locale.KOREA), HttpStatus.CONFLICT);
        } catch (Exception e) {
            throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                    message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return vo;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스팩 manifest 파일 생성
     * @title : makeDeploymentFile
     * @return : void
    ***************************************************/
    public void makeDeploymentFile(int id) {
        String settingDeploymentFileName = "";
        String settingDeploymentName = "";
        StringBuffer content = new StringBuffer();
        String manifestFilePath  = null;
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        ServicePackVO vo  = dao.selectServicePackDetailInfo(id);
        
        if(vo == null){
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), 
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        ManifestVO manifestvo = manifestDao.selectManifestInfoByDeployName(vo.getDeploymentName());
        //서비스팩 배포 명 셋팅
        settingDeploymentName = vo.getDeploymentName();
        //서비스팩 배포 파일 명 셋팅
        settingDeploymentFileName = manifestvo.getFileName();
        //ManifestFile 경로 셋팅
        manifestFilePath =  MANIFEST_DIRECTORY+vo.getDeploymentFile();
        
        //셋팅 된 배포 명, 파일 명으로 테이블 업데이트
        vo.setDeploymentFile(settingDeploymentFileName);
        vo.setDeploymentName(settingDeploymentName);
        dao.updateServicePackInfo(vo);
        
        try {
            //Manifestl temp file 생성
            File settingFile = new File(manifestFilePath);
            if( settingFile.exists() ) {
                inputStream = new FileInputStream(settingFile);
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                String info = null;
                while ((info = bufferedReader.readLine()) != null) {
                    content.append(info).append("\n");
                }
                IOUtils.write(content.toString(), new FileOutputStream(DEPLOYMENT_DIR + vo.getDeploymentFile()), "UTF-8");
            }else {
                throw new CommonException(message.getMessage("common.notFound.exception.code", null, Locale.KOREA),
                        message.getMessage("common.notFound.template.message", null, Locale.KOREA), HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                    message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
        }  finally {
            try {
                if( bufferedReader != null ) {
                    bufferedReader.close();
                }
                if( inputStream != null ) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서비스팩 단순 레코드 삭제
     * @title : deleteServicePackInfoRecord
     * @return : void
    ***************************************************/
    @Transactional
    public void deleteServicePackInfoRecord(ServicePackParamDTO dto){
        ServicePackVO vo  = dao.selectServicePackDetailInfo(dto.getId());    
        if(vo != null){
            ManifestVO manifestvo = manifestDao.selectManifestInfoByDeployName(vo.getDeploymentName());
            dao.deleteServicePackInfoRecord(dto.getId());
            if(manifestvo != null){
                manifestvo.setDeployStatus(null);
                manifestDao.updateManifestInfo(manifestvo);
            }
        }else {
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA), 
                    message.getMessage("common.badRequest.message", null, Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
    }
}
