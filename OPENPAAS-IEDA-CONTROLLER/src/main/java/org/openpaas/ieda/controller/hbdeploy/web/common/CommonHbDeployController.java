package org.openpaas.ieda.controller.hbdeploy.web.common;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.openpaas.ieda.hbdeploy.web.common.service.CommonHbDeployService;
import org.openpaas.ieda.hbdeploy.web.config.setting.dao.HbDirectorConfigVO;
import org.openpaas.ieda.hbdeploy.web.config.setting.service.HbDirectorConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class CommonHbDeployController {

    @Autowired private HbDirectorConfigService hbDirectorService;
    @Autowired private CommonHbDeployService commonHbDeployService;
    private final static Logger LOGGER = LoggerFactory.getLogger(CommonHbDeployController.class);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 기본 설치 관리자 정보 조회
     * @title : getDefaultDirector
     * @return : ResponseEntity<DirectorConfigVO>
    *****************************************************************/
    @RequestMapping(value="/common/use/hbDirector", method=RequestMethod.GET)
    public ResponseEntity<List<HbDirectorConfigVO> > getHbDirector() {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> Hb 설치 관리자 정보 조회 요청"); }
        List<HbDirectorConfigVO> content = hbDirectorService.getDirectorList("all");
        return new ResponseEntity<List<HbDirectorConfigVO> >(content, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 배포파일 브라우저 다운로드
     * @title : downloadDeploymentFile
     * @return : void
    *****************************************************************/
    @RequestMapping(value = "/common/hbDeploy/download/manifest/{fileName}", method = RequestMethod.GET)
    public void downloadDeploymentFile( @PathVariable("fileName") String fileName, HttpServletResponse response){
        if(LOGGER.isInfoEnabled()){ LOGGER.debug("====================================> 배포파일 브라우저 다운로드 요청"); }
        commonHbDeployService.downloadHbDeploymentFile(fileName, response);
    }
    
}
