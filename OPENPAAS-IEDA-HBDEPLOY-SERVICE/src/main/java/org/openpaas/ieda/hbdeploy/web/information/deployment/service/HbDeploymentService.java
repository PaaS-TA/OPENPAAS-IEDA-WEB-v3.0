package org.openpaas.ieda.hbdeploy.web.information.deployment.service;

import java.io.IOException;
import java.net.NoRouteToHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.api.deployment.DeploymentDTO;
import org.openpaas.ieda.deploy.api.deployment.DeploymentInfoDTO;
import org.openpaas.ieda.hbdeploy.api.director.utility.HbDirectorRestHelper;
import org.openpaas.ieda.hbdeploy.web.config.setting.dao.HbDirectorConfigDAO;
import org.openpaas.ieda.hbdeploy.web.config.setting.dao.HbDirectorConfigVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class HbDeploymentService {
    
    @Autowired private HbDirectorConfigDAO dao;
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 설치 목록 결과 정보 조회
     * @title : listDeployment
     * @return : List<DeploymentInfoDTO>
    ***************************************************/
    public List<DeploymentInfoDTO> listDeployment(String directorId){
        HbDirectorConfigVO directorInfo = dao.selectHbDirectorConfigBySeq(Integer.parseInt(directorId));
        if ( directorInfo == null ) {
            throw new CommonException("notfound.director.exception", "디렉터가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
        List<DeploymentInfoDTO> deploymentInfoList = null;
        try {
            HttpClient httpClient = HbDirectorRestHelper.getHttpClient(directorInfo.getDirectorPort());
            GetMethod get = new GetMethod(HbDirectorRestHelper.getDeploymentListURI(directorInfo.getDirectorUrl(), directorInfo.getDirectorPort()));
            get = (GetMethod)HbDirectorRestHelper.setAuthorization(directorInfo.getUserId(), directorInfo.getUserPassword(), (HttpMethodBase)get);
            httpClient.executeMethod(get);
            if ( !StringUtils.isEmpty(get.getResponseBodyAsString()) ) {
                
                ObjectMapper mapper = new ObjectMapper();
                DeploymentDTO[] deploymentList = mapper.readValue(get.getResponseBodyAsString(), DeploymentDTO[].class);
                
                int idx = 0;
                for ( DeploymentDTO deployment : deploymentList ) {
                    if ( deploymentInfoList == null ) {
                        deploymentInfoList = new ArrayList<DeploymentInfoDTO>();
                    }
                    
                    DeploymentInfoDTO deploymentInfo = new DeploymentInfoDTO();
                    
                    deploymentInfo.setRecid(idx++);
                    deploymentInfo.setName(deployment.getName());
                    
                    StringBuffer releaseInfo = new StringBuffer();
                    for ( HashMap<String, String> release : deployment.getReleases()) {
                        releaseInfo.append(release.get("name")).append(" (").append(release.get("version")).append(")<br>");
                    }
                    deploymentInfo.setReleaseInfo(releaseInfo.toString());
                    
                    StringBuffer stemcellInfo = new StringBuffer();
                    for ( HashMap<String, String> stemcell : deployment.getStemcells()) {
                        stemcellInfo.append(stemcell.get("name")).append(" (").append(stemcell.get("version")).append(")<br>");
                    }
                    deploymentInfo.setStemcellInfo(stemcellInfo.toString());
                    
                    deploymentInfoList.add(deploymentInfo);
                }
            }
        } catch (NoRouteToHostException e){
            throw new CommonException("noRouteToHost.deployment.exception", "네트워크 연결에 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            throw new CommonException("io.deployment.exception", " 배포 정보 조회중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        return deploymentInfoList;
    }
}
