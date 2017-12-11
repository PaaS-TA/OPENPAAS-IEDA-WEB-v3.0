package org.openpaas.ieda.deploy.web.information.manifest.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Principal;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.io.FileUtils;
import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.common.web.security.SessionInfoDTO;
import org.openpaas.ieda.deploy.web.common.service.CommonDeployUtils;
import org.openpaas.ieda.deploy.web.information.manifest.dao.ManifestDAO;
import org.openpaas.ieda.deploy.web.information.manifest.dao.ManifestVO;
import org.openpaas.ieda.deploy.web.information.manifest.dto.ManifestListDTO;
import org.openpaas.ieda.deploy.web.information.manifest.dto.ManifestParamDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.unitils.thirdparty.org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.parser.ParserException;

@Service
public class ManifestService {
    
    @Autowired ManifestDAO manifestDao;
    
    final private static String SEPARATOR = System.getProperty("file.separator");
    final private static String MANIFEST_DIRECTORY = LocalDirectoryConfiguration.getManifastDir() + SEPARATOR;
    final private static Logger LOGGER = LoggerFactory.getLogger(ManifestService.class);
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Manifest 정보 목록 조회
     * @title : getManifestList
     * @return : List<ManifestVO>
    ***************************************************/
    public List<ManifestVO> getManifestList(){
        return manifestDao.selectManifestList();
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 해당 Manifest 파일 내용 조회
     * @title : getManifestInfo
     * @return : String
    ***************************************************/
    public String getManifestInfo( int id ){
        
        String contents = "";
        File settingFile = null;
        ManifestVO result = manifestDao.selectManifestInfo(id);
        if( result != null ){
            try {
                settingFile = new File(MANIFEST_DIRECTORY + result.getFileName());
                contents = IOUtils.toString(new FileInputStream(settingFile), "UTF-8");
            } catch(FileNotFoundException e){
                throw new CommonException("notFound.manifest.exception",
                        "해당하는 Manifest 파일이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
            } catch (IOException e) {
                throw new CommonException("ioFileRead.manifest.exception",
                        "해당하는 Manifest 파일을 읽을 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
            } 
        }else{
            throw new CommonException("notFound.manifest.exception",
                    "Manifest 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }
        
        return contents;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Manifest 파일 업로드
     * @title : uploadManifestFile
     * @return : void
    ***************************************************/
    public void uploadManifestFile( MultipartHttpServletRequest request){
        File isManifestFile = null;
        BufferedOutputStream stream = null;
        InputStream instreamYaml = null;
        InputStream instreamFile = null;
        BufferedReader rd = null;
        Iterator<String> itr =  request.getFileNames();
        
        if(itr.hasNext()) {
            MultipartFile mpf = request.getFile(itr.next());
            try {
                byte[] tmp = new byte[8192];
                
                String manifestFilePath = MANIFEST_DIRECTORY + mpf.getOriginalFilename();
                isManifestFile = new File(manifestFilePath);
                if( isManifestFile.exists() ){
                    //1. if file exists, exception 
                    throw new CommonException("conflict.manifest.exception",
                            "이미 동일한 Manifest 파일이 존재합니다. 확인해주세요", HttpStatus.CONFLICT);
                }
                
                //1.1 파일 정보 읽어오기
                instreamFile = mpf.getInputStream();
                rd = new BufferedReader(new InputStreamReader(instreamFile,"UTF-8"));
                String line = null;
                StringBuffer contents = new StringBuffer("");
                while((line = rd.readLine()) != null) {
                    contents.append(line).append("\n");
                }
                
                //1.2 yaml parser
                Map<String, Object> object = CommonDeployUtils.yamlParser(contents.toString());
                if(object != null){
                    //1.3 배포명 중복 체크
                    String deploymentName = object.get("name").toString();
                    ManifestVO vo = manifestDao.selectManifestInfoByDeployName(deploymentName);
                    if( vo != null ) {
                        throw new CommonException("conflict.manifest.exception",
                                "해당 Manifest의 배포명은 이미 존재합니다. 확인해주세요.", HttpStatus.CONFLICT);
                    }
                    //1.4 출력
                    int i =0;
                    instreamYaml = mpf.getInputStream();
                    stream =     new BufferedOutputStream(new FileOutputStream(isManifestFile));
                     while ((i = instreamYaml.read(tmp)) >= 0) {
                         stream.write(tmp, 0, i);
                     }
                     
                    if(isManifestFile.exists()){
                        ManifestListDTO dto = new ManifestListDTO();
                        dto.setIaas(request.getParameter("iaas").toLowerCase());
                        dto.setFileName(mpf.getOriginalFilename());
                        dto.setDescription(request.getParameter("description"));
                        dto.setPath(MANIFEST_DIRECTORY);
                        dto.setDeploymentName(deploymentName);
                        saveManifestInfo(dto);
                    }
                }
            }catch(FileNotFoundException e){
                throw new CommonException("fileNotFound.manifest.exception",
                        "해당 Manifest 파일을 찾을수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
            }catch (IOException e) {
                throw new CommonException("ioFileRead.manifest.exception",
                        "해당 Manifest 파일 업로드 중 문제가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
            } catch( NullPointerException e) {
                throw new CommonException("null.manifest.exception",
                        "Yaml 형식에 맞지 않는 Manifest 입니다.", HttpStatus.NOT_FOUND);
            }catch( SQLException e){
                throw new CommonException("sql.manifest.exception",
                        "Manifest 정보 저장에 문제가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
            } finally {
                try {
                     if (rd != null) {
                         rd.close();
                         rd = null;
                     }
                }catch (IOException e) {
                    throw new CommonException("ioFileRead.manifest.exception",
                            "해당 Manifest 파일 업로드 중 문제가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
                }
                try{
                    if (stream != null) {
                        stream.close();
                        stream = null;
                    }
                }catch (IOException e) {
                    throw new CommonException("ioFileRead.manifest.exception",
                            "해당 Manifest 파일 업로드 중 문제가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Manifest 정보 저장
     * @title : saveManifestInfo
     * @return : void
    ***************************************************/
    public void saveManifestInfo(ManifestListDTO dto) throws SQLException{
        SessionInfoDTO session = new SessionInfoDTO();
        dto.setCreateUserId(session.getUserId());
        dto.setUpdateUserId(session.getUserId());
        
        manifestDao.insertManifestInfo(dto);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Manifest 파일 웹 브라우저에 다운로드
     * @title : downloadManifestFile
     * @return : void
    ***************************************************/
    public void downloadManifestFile( int id, HttpServletResponse response ){
        ManifestVO manifestInfo = manifestDao.selectManifestInfo(id);
        String fileName = "";
        if(  manifestInfo != null ){
            File manifest = new File( MANIFEST_DIRECTORY + manifestInfo.getFileName() );
            if(  manifest.exists()){
                try {
                    fileName = null;
                    //download web browser
                    byte[] content = FileUtils.readFileToByteArray(manifest);
                    fileName = manifestInfo.getFileName();
                    response.setContentType("application/octet-stream");
                    response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
                    IOUtils.write(content, response.getOutputStream());
                    
                } catch (IOException e) {
                    throw new CommonException("ioFileRead.manifest.exception",
                            "해당 Manifest 파일을 다운로드 할 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }else{
                throw new CommonException("notfound.manifest.exception",
                        "해당 Manifest 파일을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
            }
        }
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Manifest 정보 수정
     * @title : updateManifestContent
     * @return : void
    ***************************************************/
    public void updateManifestContent( ManifestParamDTO dto, Principal principal ){
        ManifestVO vo = manifestDao.selectManifestInfo( Integer.parseInt(dto.getId()) );
        try {
            if( vo != null ){
                //1.1 yaml file check
                Map<String, Object> object = CommonDeployUtils.yamlParser(dto.getContent());
                
                //1.2 old file delete
                File oldFile = new File( MANIFEST_DIRECTORY + vo.getFileName() );
                if(  oldFile.exists() ){
                    boolean deleteCheck = oldFile.delete();
                    if(LOGGER.isDebugEnabled()){
                        LOGGER.debug( "oldFile Delete : " + deleteCheck );
                    }
                }
                //1.3 update manifest
                IOUtils.write(dto.getContent(), new FileOutputStream( MANIFEST_DIRECTORY + vo.getFileName()), "UTF-8");
                //1.4 배포명 중복 체크
                String deploymentName = object.get("name").toString();
                ManifestVO result = manifestDao.selectManifestInfoByDeployNameANDId(deploymentName, Integer.parseInt(dto.getId()));
                if( result != null ) {
                    throw new CommonException("conflict.manifest.exception",
                            "해당 Manifest의 배포명은 이미 존재합니다. 확인해주세요.", HttpStatus.CONFLICT);
                }
                //1.5 save manifest info
                vo.setUpdateUserId(principal.getName());
                vo.setDeploymentName(deploymentName);
                manifestDao.updateManifestInfo(vo);
            }else{
                throw new CommonException("notfound.manifest.exception",
                        "해당 Manifest 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
            }
        } catch(NullPointerException e){
            throw new CommonException("null.manifest.exception",
                    "Yaml 형식에 맞지 않는 Manifest 입니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            throw new CommonException("ioFileRead.manifest.exception",
                    "Manifest 파일 저장 중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ParserException e){
            throw new CommonException("parse.yaml.exception",
                    e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Manifest 삭제
     * @title : deleteManifest
     * @return : void
    ***************************************************/
    @Transactional
    public void deleteManifest( int  id){
        ManifestVO info = manifestDao.selectManifestInfo(id);
        if(  info != null ){
            manifestDao.deleteManifestInfo(id);
            File manifest = new File(MANIFEST_DIRECTORY + info.getFileName());
            if( manifest.exists() ){
                boolean deleteCheck = manifest.delete();
                if(LOGGER.isDebugEnabled()){
                    LOGGER.debug( "Manifest Delete : " + deleteCheck );
                }
            }
        }else{
            throw new CommonException("notfound.manifest.exception",
                    "해당 Manifest 정보가 없습니다.", HttpStatus.NOT_FOUND);
        }
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Manifest 검색
     * @title : searchManifestList
     * @return : List<ManifestVO>
    ***************************************************/
    public List<ManifestVO> searchManifestList(String searchVal) {
        List<ManifestVO> list = manifestDao.selectManifestSearchList(searchVal);
        return list;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드한 매니페스트 정보 조회
     * @title : getManifestUploadInfo
     * @return : ManifestVO
    ***************************************************/
    public ManifestVO getManifestUploadInfo(int id) {
        return manifestDao.selectManifestInfo(id);
    }
}
