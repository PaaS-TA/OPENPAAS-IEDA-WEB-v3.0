package org.openpaas.ieda.controller.deploy.web.dashboard;

import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.controller.common.BaseController;
import org.openpaas.ieda.deploy.api.deployment.DeploymentInfoDTO;
import org.openpaas.ieda.deploy.api.release.ReleaseInfoDTO;
import org.openpaas.ieda.deploy.web.config.stemcell.dao.StemcellManagementVO;
import org.openpaas.ieda.deploy.web.information.deploy.service.DeploymentService;
import org.openpaas.ieda.deploy.web.information.release.service.ReleaseService;
import org.openpaas.ieda.deploy.web.information.stemcell.service.StemcellService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class DashboardController extends BaseController {

    @Autowired private DeploymentService deploymentService;
    @Autowired private ReleaseService releaseService;
    @Autowired private StemcellService stemcellService;
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : DASHBOARD 화면 호출
     * @title : goDashboard
     * @return : String
    ***************************************************/
    @RequestMapping(value="/main/dashboard", method=RequestMethod.GET)
    public String goDashboard() {
        return "/deploy/dashboard/dashboard";
    }

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 설치 정보 목록 조회
     * @title : listDeployment
     * @return : ResponseEntity<HashMap<String,Object>>
    ***************************************************/
    @RequestMapping(value="/main/dashboard/deployments", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getDploymentList(){
        List<DeploymentInfoDTO> contents = deploymentService.listDeployment();
        HashMap<String, Object> result = new HashMap<String, Object>();
        if( contents != null && contents.size() > 0 ){
            result.put("records", contents);
            result.put("total", contents.size());
        }
        return new ResponseEntity<HashMap<String, Object>>( result, HttpStatus.OK);
    }

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드 릴리즈 정보 목록 조회
     * @title : listRelease
     * @return : ResponseEntity<HashMap<String,Object>>
    ***************************************************/
    @RequestMapping( value="/main/dashboard/releases", method =RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getReleaseList(){
        List<ReleaseInfoDTO> contents = releaseService.getUploadedReleaseList();
        HashMap<String, Object> result = new HashMap<String, Object>();
        if ( contents.size() > 0 ) {
            result.put("records", contents);
            result.put("total", contents.size());
        } 
        return new ResponseEntity<HashMap<String, Object>>( result, HttpStatus.OK);
    }

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 업로드 스템셀 정보 목록 조회
     * @title : listStemcell
     * @return : ResponseEntity<HashMap<String,Object>>
    ***************************************************/
    @RequestMapping(value="/main/dashboard/stemcells", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getStemcellList(){
        List<StemcellManagementVO> contents = stemcellService.getStemcellList();
        HashMap<String, Object> result = new HashMap<String, Object>();
        if ( contents.size() > 0 ) {
            result.put("total", contents.size());
            result.put("records", contents);
        }
        return new ResponseEntity<HashMap<String, Object>>(result, HttpStatus.OK);
    }

}
