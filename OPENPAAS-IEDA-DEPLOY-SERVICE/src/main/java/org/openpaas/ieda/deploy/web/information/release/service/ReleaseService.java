package org.openpaas.ieda.deploy.web.information.release.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.deploy.api.release.ReleaseDTO;
import org.openpaas.ieda.deploy.api.release.ReleaseInfoDTO;
import org.openpaas.ieda.deploy.api.release.ReleaseVersionDTO;
import org.openpaas.ieda.deploy.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.deploy.web.config.setting.service.DirectorConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ReleaseService {
    
    @Autowired private DirectorConfigService directorConfigService;
    @Autowired private MessageSource message;
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 기본 설치관리자에 업로드된 릴리즈 목록 조회 요청
     * @title : getUploadedReleaseList
     * @return : List<ReleaseInfoDTO>
    ***************************************************/
    public List<ReleaseInfoDTO> getUploadedReleaseList() {
        DirectorConfigVO defaultDirector = directorConfigService.getDefaultDirector();
        List<ReleaseInfoDTO> releaseInfoList =  new ArrayList<ReleaseInfoDTO>();
        HttpClient client = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
        try {
            GetMethod get = new GetMethod(DirectorRestHelper.getReleaseListURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort()));
            get = (GetMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)get);
            client.executeMethod(get);
            if ( !StringUtils.isEmpty(get.getResponseBodyAsString())) {
                releaseInfoList= setUploadedReleaseList(get.getResponseBodyAsString());
            }
        } catch (HttpException e) {
            throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                    message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                    message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return releaseInfoList; 
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 조회한 릴리즈 목록 정보 설정
     * @title : setUploadedReleaseList
     * @return : List<ReleaseInfoDTO>
    *****************************************************************/
    public List<ReleaseInfoDTO> setUploadedReleaseList(String responseBody){
        List<ReleaseInfoDTO> releaseInfoList =  new ArrayList<ReleaseInfoDTO>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            ReleaseDTO[] releases = mapper.readValue(responseBody, ReleaseDTO[].class);
            int idx = 0;
            List<ReleaseDTO> releaseList = Arrays.asList(releases);
            for ( ReleaseDTO release : releaseList ) {
                List<ReleaseVersionDTO> versionList = release.getReleaseVersions();
                for (ReleaseVersionDTO releaseVersion : versionList) {
                    ReleaseInfoDTO releaseInfo = new ReleaseInfoDTO();
                    releaseInfo.setRecid(idx++);
                    releaseInfo.setName(release.getName());
                    releaseInfo.setVersion(releaseVersion.getVersion());
                    releaseInfo.setCurrentDeployed(releaseVersion.getCurrentlyDeployed().toString());
                    releaseInfo.setJobNames(releaseVersion.getJobNames().toString());
                    releaseInfoList.add(releaseInfo);
                }
            }
            if ( releaseInfoList != null ) {// 릴리즈 버전 역순으로 정렬
                Comparator<ReleaseInfoDTO> byReleaseVersion = Collections.reverseOrder(Comparator.comparing(ReleaseInfoDTO::getVersion));
                releaseInfoList = releaseInfoList.stream()
                        .sorted(byReleaseVersion).collect(Collectors.toList());
            }
        } catch (JsonParseException e) {
            throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                    message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (JsonMappingException e) {
            throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                    message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                    message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return releaseInfoList;
    }

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF, DIEGO, Garden-Linux, ETCD 등의 공통 릴리즈 정보 조회
     * @title : getFilteredReleseList
     * @return : List<ReleaseInfoDTO>
    ***************************************************/
    public List<ReleaseInfoDTO> getFilteredReleseList(String type) {
        DirectorConfigVO defaultDirector = directorConfigService.getDefaultDirector();
        List<ReleaseInfoDTO> releaseInfoList = null;
        try {
            HttpClient client = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
            GetMethod get = new GetMethod(DirectorRestHelper.getReleaseListURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort()));
            get = (GetMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)get);
            client.executeMethod(get);
            if ( !StringUtils.isEmpty(get.getResponseBodyAsString()) ) {
                releaseInfoList=setFilteredReleseInfo(get.getResponseBodyAsString(), type); 
            }
        } catch (HttpException e) {
            throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                    message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                    message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
        }
                
        return releaseInfoList; 
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 공통 릴리즈 목록 정보 설정
     * @title : setFilteredReleseInfo
     * @return : List<ReleaseInfoDTO>
    *****************************************************************/
    public List<ReleaseInfoDTO> setFilteredReleseInfo(String responseBody, String type){
        List<ReleaseInfoDTO> releaseInfoList = new ArrayList<ReleaseInfoDTO>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            ReleaseDTO[] releases = mapper.readValue(responseBody, ReleaseDTO[].class);
            int idx = 0;
            List<ReleaseDTO> releaseList = Arrays.asList(releases);
            for ( ReleaseDTO release : releaseList ) {
                String releaseName = release.getName();
                if( release.getName().indexOf("paasta-") > -1){
                    releaseName = release.getName().split("paasta-")[1];
                    if("controller".equalsIgnoreCase(releaseName)){
                        releaseName = "cf";
                    }else if("container".equalsIgnoreCase(releaseName)){
                        releaseName="diego";
                    }else if("garden-runc".equalsIgnoreCase(releaseName)){
                        releaseName="garden-linux";
                    }else if( "loggregator".equalsIgnoreCase(releaseName) ){
                        releaseName="loggregator";
                    }
                }
                if("garden-runc".equalsIgnoreCase(releaseName)){
                    releaseName="garden-linux";
                }
                if("cflinuxfs2".equalsIgnoreCase(releaseName)){
                    releaseName= "cflinuxfs2-rootfs";
                }
                if( type.equalsIgnoreCase(releaseName)){
                    List<ReleaseVersionDTO> versionList = release.getReleaseVersions();
                    for (ReleaseVersionDTO releaseVersion : versionList) {
                        ReleaseInfoDTO releaseInfo = new ReleaseInfoDTO();
                        releaseInfo.setRecid(idx++);
                        releaseInfo.setName(release.getName());
                        releaseInfo.setVersion(releaseVersion.getVersion());
                        releaseInfoList.add(releaseInfo);
                    }
                }
            }
            // 릴리즈 버전 역순으로 정렬
            Comparator<ReleaseInfoDTO> byReleaseVersion = Collections.reverseOrder(Comparator.comparing(ReleaseInfoDTO::getVersion));
            releaseInfoList = releaseInfoList.stream()
                    .sorted(byReleaseVersion)
                    .collect(Collectors.toList());
        } catch (JsonParseException e) {
            throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                    message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (JsonMappingException e) {
            throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                    message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                    message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
        } 
        
        return releaseInfoList;
    }
       

}