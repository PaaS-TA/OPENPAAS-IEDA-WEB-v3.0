package org.openpaas.ieda.hbdeploy.web.information.stemcell.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.hbdeploy.api.director.utility.HbDirectorRestHelper;
import org.openpaas.ieda.hbdeploy.api.stemcell.StemcellListDTO;
import org.openpaas.ieda.hbdeploy.web.config.setting.dao.HbDirectorConfigDAO;
import org.openpaas.ieda.hbdeploy.web.config.setting.dao.HbDirectorConfigVO;
import org.openpaas.ieda.hbdeploy.web.config.setting.service.HbDirectorConfigService;
import org.openpaas.ieda.hbdeploy.web.config.stemcell.dao.HbStemcellManagementVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class HbStemcellService {
    
    @Autowired 
    private HbDirectorConfigService hbDirectorConfigService;
    @Autowired
    private HbDirectorConfigDAO hbDirectorConfigDao;
    @Autowired 
    private MessageSource message;
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드된 스템셀 조회 요청
     * @title : getStemcellList
     * @return : List<StemcellManagementVO>
    *****************************************************************/
    public List<HbStemcellManagementVO> getStemcellList(int directorId) {
        HbDirectorConfigVO selectedDirector = hbDirectorConfigDao.selectHbDirectorConfigBySeq(directorId);
        if ( selectedDirector == null ) {
            throw new CommonException("notfound.director.exception", "디렉터가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
        hbDirectorConfigService.isExistBoshEnvLogin(selectedDirector.getDirectorUrl(), selectedDirector.getDirectorPort(), selectedDirector.getUserId(), selectedDirector.getUserPassword());
        
        List<HbStemcellManagementVO> stemcellInfoList = new ArrayList<HbStemcellManagementVO>();
        try {
            HttpClient client = HbDirectorRestHelper.getHttpClient(selectedDirector.getDirectorPort().intValue());
            GetMethod get = new GetMethod(HbDirectorRestHelper.getStemcellsURI(selectedDirector.getDirectorUrl(), selectedDirector.getDirectorPort().intValue()));
            get = (GetMethod)HbDirectorRestHelper.setAuthorization(selectedDirector.getUserId(), selectedDirector.getUserPassword(), (HttpMethodBase)get);
            client.executeMethod(get);
            stemcellInfoList= setUploadedStemcellList(get.getResponseBodyAsString());
        } catch (HttpException e) {
            throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                    message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                    message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return stemcellInfoList;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드된 스템셀 목록 정보 설정
     * @title : setUploadedStemcellList
     * @return : List<StemcellManagementVO>
    *****************************************************************/
    public List<HbStemcellManagementVO> setUploadedStemcellList( String responseBody ){
        List<HbStemcellManagementVO> stemcellInfoList= new ArrayList<HbStemcellManagementVO>();
        if ( !StringUtils.isEmpty(responseBody) ) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                StemcellListDTO[] stemcells = mapper.readValue(responseBody, StemcellListDTO[].class);
                int idx = 0;
                for ( StemcellListDTO stemcell : stemcells ) {
                HbStemcellManagementVO stemcellInfo = new HbStemcellManagementVO();
                    stemcellInfo.setRecid(idx++);
                    stemcellInfo.setStemcellFileName(stemcell.getName());
                    stemcellInfo.setOs(stemcell.getOperatingSystem());
                    stemcellInfo.setStemcellVersion(stemcell.getVersion());
                    stemcellInfoList.add(stemcellInfo);
                }
            } catch (JsonParseException e) {
                throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                        message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (JsonMappingException e) {
            	e.printStackTrace();
            	throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                        message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (IOException e) {
                throw new CommonException(message.getMessage("common.internalServerError.exception.code", null, Locale.KOREA),
                        message.getMessage("common.internalServerError.message", null, Locale.KOREA), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return stemcellInfoList;
    }

}
