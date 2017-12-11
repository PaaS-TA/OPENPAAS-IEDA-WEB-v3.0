package org.openpaas.ieda.controller.deploy.web.deploy.diego;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.openpaas.ieda.controller.common.BaseController;
import org.openpaas.ieda.deploy.web.deploy.cf.dto.CfListDTO;
import org.openpaas.ieda.deploy.web.deploy.cf.service.CfService;
import org.openpaas.ieda.deploy.web.deploy.common.dto.network.NetworkDTO;
import org.openpaas.ieda.deploy.web.deploy.common.dto.resource.ResourceDTO;
import org.openpaas.ieda.deploy.web.deploy.diego.dao.DiegoVO;
import org.openpaas.ieda.deploy.web.deploy.diego.dto.DiegoListDTO;
import org.openpaas.ieda.deploy.web.deploy.diego.dto.DiegoParamDTO;
import org.openpaas.ieda.deploy.web.deploy.diego.service.DiegoDeleteDeployAsyncService;
import org.openpaas.ieda.deploy.web.deploy.diego.service.DiegoDeployAsyncService;
import org.openpaas.ieda.deploy.web.deploy.diego.service.DiegoSaveService;
import org.openpaas.ieda.deploy.web.deploy.diego.service.DiegoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class DiegoController extends BaseController{

    @Autowired private DiegoService diegoService; 
    @Autowired private DiegoSaveService diegoSaveService;
    @Autowired private DiegoDeployAsyncService diegoDeployAsyncService;
    @Autowired private DiegoDeleteDeployAsyncService diegoDeleteDeployAsyncService;
    @Autowired private CfService cfService;
    private final static Logger LOGGER = LoggerFactory.getLogger(DiegoController.class);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Diego 플랫폼 설치 화면으로 이동
     * @title : goDiego
     * @return : String
    *****************************************************************/
    @RequestMapping(value = "/deploy/diego", method=RequestMethod.GET)
    public String goDiego() {
        if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> /deploy/diego"); }
        return "/deploy/deploy/diego/diego";
    }    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Diego 설치 팝업 화면 호출
     * @title : goDiegoPopup
     * @return : String
    *****************************************************************/
    @RequestMapping(value = "/deploy/diego/install/diegoPopup", method=RequestMethod.GET)
    public String goDiegoPopup() {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> /deploy/diego/install/diegoPopup"); }
        return "/deploy/deploy/diego/diegoPopup";
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Diego 목록 정보 조회
     * @title : getDiegoInfoList
     * @return : ResponseEntity<Map<String,Object>>
    *****************************************************************/
    @RequestMapping(value="/deploy/diego/list/{iaasType}", method=RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getDiegoInfoList(@PathVariable String iaasType) {
        if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> /deploy/diego/list/"+iaasType); }
        List<DiegoListDTO> content = diegoService.getDiegoInfoList(iaasType);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("total", (content == null) ? 0:content.size());
        result.put("records", content);

        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Diego 상세 조회 
     * @title : getDiegoDetailInfo
     * @return : ResponseEntity<Map<String,Object>>
    *****************************************************************/
    @RequestMapping(value="/deploy/diego/install/detail/{id}", method=RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getDiegoDetailInfo(@PathVariable int id){
        if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> deploy/diego/install/detail/"); }
        DiegoVO vo = diegoService.getDiegoDetailInfo(id);
        Map<String, Object> result =  new HashMap<>();
        result.put("content", vo);
        
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 정보 목록 조회 
     * @title : getCfLIst
     * @return : ResponseEntity<Map<String,Object>>
    *****************************************************************/
    @RequestMapping(value="/deploy/diego/list/cf/{iaas}", method=RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getCfLIst(@PathVariable String iaas) {
        
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> /deploy/diego/list/cf/"+iaas); }
        List<CfListDTO> content = cfService.getCfLIst(iaas.toLowerCase(), "diego");
        Map<String, Object> result = new HashMap<>();
        
        result.put("total", (content == null) ? 0:content.size());
        result.put("records", content);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Diego 기본정보 저장
     * @title : saveDefaultInfo
     * @return : ResponseEntity<Map<String,Object>>
    *****************************************************************/
    @RequestMapping(value="/deploy/diego/install/saveDefaultInfo", method=RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> saveDefaultInfo(@RequestBody @Valid DiegoParamDTO.Default dto, Principal principal){
        if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> /deploy/diego/install/saveDefaultInfo"); }
        DiegoVO vo = diegoSaveService.saveDefaultInfo(dto, principal);
        Map<String, Object> result  = new HashMap<>();
        result.put("content", vo);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.CREATED);
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Diego 네트워크 정보 저장  
     * @title : saveNetworkInfo
     * @return : ResponseEntity<DiegoVO>
    *****************************************************************/
    @RequestMapping(value="/deploy/diego/install/saveNetworkInfo", method=RequestMethod.PUT)
    public ResponseEntity<DiegoVO> saveNetworkInfo(@RequestBody @Valid List<NetworkDTO> dto, Principal principal){
        if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> /deploy/diego/install/saveNetworkInfo"); }
        DiegoVO vo = diegoSaveService.saveNetworkInfo(dto, principal);
        return new ResponseEntity<DiegoVO>(vo, HttpStatus.CREATED);
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Diego 리소스 정보 저장 
     * @title : saveResourceInfo
     * @return : ResponseEntity<Map<String,Object>>
    *****************************************************************/
    @RequestMapping(value="/deploy/diego/install/saveResourceInfo", method=RequestMethod.PUT)
    public ResponseEntity<Map<String, Object> > saveResourceInfo(@RequestBody @Valid ResourceDTO dto, Principal principal){
        if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> /deploy/diego/install/saveResourceInfo"); }
        Map<String, Object> map = diegoSaveService.saveResourceInfo(dto,principal);

        return new ResponseEntity<Map<String, Object> >(map, HttpStatus.CREATED);
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Diego 배포 파일 생성
     * @title : makeDeploymentFile
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value="/deploy/diego/install/createSettingFile", method=RequestMethod.POST)
    public ResponseEntity<?> makeDeploymentFile(@RequestBody DiegoParamDTO.Install dto){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> /deploy/diego/install/createSettingFile"); }
        //Manifest file Create
        DiegoVO vo = diegoService.getDiegoDetailInfo( Integer.parseInt(dto.getId()) );
        diegoService.createSettingFile(vo);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Diego 설치
     * @title : diegoInstall
     * @return : ResponseEntity<Object>
    *****************************************************************/
    @MessageMapping("/deploy/diego/install/diegoInstall")
    @SendTo("/deploy/diego/install/logs")
    public ResponseEntity<Object> diegoInstall(@RequestBody @Valid DiegoParamDTO.Install dto, Principal principal ){
        if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> /deploy/diego/install/diegoInstall"); }
        diegoDeployAsyncService.deployAsync(dto, principal, "diego");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Diego 단순 레코드 삭제 
     * @title : deleteJustOnlyDiegoRecord
     * @return : ResponseEntity<Object>
    *****************************************************************/
    @RequestMapping( value="/deploy/diego/delete/data", method=RequestMethod.DELETE)
    public ResponseEntity<Object> deleteJustOnlyDiegoRecord(@RequestBody @Valid DiegoParamDTO.Delete dto){
        if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> deploy/diego/delete/data"); }
        diegoService.deleteDiegoInfoRecord(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Diego 플랫폼 삭제 요청
     * @title : deleteDiego
     * @return : ResponseEntity<Object>
    *****************************************************************/
    @MessageMapping("/deploy/diego/delete/instance")
    @SendTo("/deploy/diego/delete/logs")
    public ResponseEntity<Object> deleteDiego(@RequestBody @Valid DiegoParamDTO.Delete dto, Principal principal ){
        if(LOGGER.isInfoEnabled()){ LOGGER.debug("========================================> deploy/diego/delete/instance"); }
        diegoDeleteDeployAsyncService.deleteDeployAsync(dto, "diego", principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : DIEGO 릴리즈 버전 및 인프라 별 JOb 목록 조회
     * @title : getCfJobList
     * @return : ResponseEntity<List<HashMap<String, String>>>
    *****************************************************************/
    @RequestMapping(value="/deploy/diego/install/save/job/list/{version}/{deployType}", method=RequestMethod.GET)
    public ResponseEntity<List<HashMap<String, String>>> getDiegoJobList(@PathVariable String version, @PathVariable String deployType){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> /deploy/cf/install/save/job/list/"+version); }
        List<HashMap<String, String>> list = diegoService.getJobTemplateList(deployType, version);
        return new ResponseEntity<List<HashMap<String, String>>>(list, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : DIEGO 고급설정 정보 저장
     * @title : saveDiegoJobsInfo
     * @return : ResponseEntity<List<HashMap<String,String>>>
    *****************************************************************/
    @RequestMapping(value="/deploy/diego/install/save/jobsInfo", method=RequestMethod.PUT)
    public ResponseEntity<List<HashMap<String, String>>> saveDiegoJobsInfo(@RequestBody List<HashMap<String, String>> maps, Principal principal){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("====================================> /deploy/cf/install/save/jobsInfo"); }
        diegoSaveService.saveDiegoJobsInfo(maps, principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
}
