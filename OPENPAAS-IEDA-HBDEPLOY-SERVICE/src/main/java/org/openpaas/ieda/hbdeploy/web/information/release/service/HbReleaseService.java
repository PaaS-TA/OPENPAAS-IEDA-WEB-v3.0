package org.openpaas.ieda.hbdeploy.web.information.release.service;

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
import org.openpaas.ieda.deploy.api.release.ReleaseDTO;
import org.openpaas.ieda.deploy.api.release.ReleaseInfoDTO;
import org.openpaas.ieda.deploy.api.release.ReleaseVersionDTO;
import org.openpaas.ieda.hbdeploy.api.director.utility.HbDirectorRestHelper;
import org.openpaas.ieda.hbdeploy.web.config.setting.dao.HbDirectorConfigDAO;
import org.openpaas.ieda.hbdeploy.web.config.setting.dao.HbDirectorConfigVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class HbReleaseService {
    
    @Autowired private MessageSource message;
    @Autowired private HbDirectorConfigDAO hbDirectorConfigDao;
    /***************************************************
     * @param directorId 
     * @project : Paas 플랫폼 설치 자동화
     * @description : 기본 설치관리자에 업로드된 릴리즈 목록 조회 요청
     * @title : getUploadedReleaseList
     * @return : List<ReleaseInfoDTO>
    ***************************************************/
    public List<ReleaseInfoDTO> getUploadedReleaseList(String directorId) {
        HbDirectorConfigVO directorInfo = hbDirectorConfigDao.selectHbDirectorConfigBySeq(Integer.parseInt(directorId));
        if ( directorInfo == null ) {
            throw new CommonException("notfound.director.exception", "디렉터가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
        List<ReleaseInfoDTO> releaseInfoList =  new ArrayList<ReleaseInfoDTO>();
        HttpClient client = HbDirectorRestHelper.getHttpClient(directorInfo.getDirectorPort());
        try {
            GetMethod get = new GetMethod(HbDirectorRestHelper.getReleaseListURI(directorInfo.getDirectorUrl(), directorInfo.getDirectorPort()));
            get = (GetMethod)HbDirectorRestHelper.setAuthorization(directorInfo.getUserId(), directorInfo.getUserPassword(), (HttpMethodBase)get);
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
}