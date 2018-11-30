package org.openpaas.ieda.controller.hbdeploy.web.information.deployment;

import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.controller.common.BaseController;
import org.openpaas.ieda.deploy.api.deployment.DeploymentInfoDTO;
import org.openpaas.ieda.hbdeploy.web.information.deployment.service.HbDeploymentService;
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
public class HbDeploymentsController extends BaseController { 
    
    @Autowired private HbDeploymentService deploymentService;
    
    final private static Logger LOGGER = LoggerFactory.getLogger(HbDeploymentsController.class);

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 배포 정보 화면 이동
     * @title : goListDeployment
     * @return : String
    *****************************************************************/
    @RequestMapping(value="/info/hbDeployment", method=RequestMethod.GET)
    public String goListDeployment() {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 배포 정보 화면 요청"); }
        return "/hbdeploy/information/listHbDeployment";
    }
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 설치 정보 목록을 조회 
     * @title : listDeployment
     * @return : ResponseEntity<HashMap<String,Object>>
    *****************************************************************/
    @RequestMapping(value="/info/hbDeployment/list/{directorId}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> listDeployment(@PathVariable String directorId){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 설치 정보 목록을 조회 요청"); }
        List<DeploymentInfoDTO> contents = deploymentService.listDeployment(directorId);
        HashMap<String, Object> result = new HashMap<String, Object>();
        int size = 0;
        
        if( contents!=null && contents.size() > 0 ) {
            result.put("records", contents);
            result.put("total", size); 
        }
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 설치 정보 목록을 조회 성공"); }
        return new ResponseEntity<HashMap<String, Object>>( result, HttpStatus.OK);
    }
}
