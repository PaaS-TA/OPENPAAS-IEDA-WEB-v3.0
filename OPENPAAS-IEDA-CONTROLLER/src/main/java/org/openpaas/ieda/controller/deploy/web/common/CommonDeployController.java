package org.openpaas.ieda.controller.deploy.web.common;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.openpaas.ieda.controller.deploy.web.management.code.CommonCodeController;
import org.openpaas.ieda.deploy.api.deployment.DeploymentInfoDTO;
import org.openpaas.ieda.deploy.api.release.ReleaseInfoDTO;
import org.openpaas.ieda.deploy.web.common.dao.ManifestTemplateVO;
import org.openpaas.ieda.deploy.web.common.dto.KeyInfoDTO;
import org.openpaas.ieda.deploy.web.common.service.CommonDeployService;
import org.openpaas.ieda.deploy.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.deploy.web.config.setting.service.DirectorConfigService;
import org.openpaas.ieda.deploy.web.config.stemcell.dao.StemcellManagementVO;
import org.openpaas.ieda.deploy.web.config.stemcell.service.StemcellManagementService;
import org.openpaas.ieda.deploy.web.config.systemRelease.service.ReleaseManagementService;
import org.openpaas.ieda.deploy.web.information.deployment.service.DeploymentService;
import org.openpaas.ieda.deploy.web.information.iassConfig.dao.IaasConfigMgntVO;
import org.openpaas.ieda.deploy.web.information.iassConfig.service.IaasConfigMgntService;
import org.openpaas.ieda.deploy.web.information.release.service.ReleaseService;
import org.openpaas.ieda.deploy.web.information.stemcell.service.StemcellService;
import org.openpaas.ieda.deploy.web.management.code.dao.CommonCodeVO;
import org.openpaas.ieda.deploy.web.management.code.service.CommonCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Controller
public class CommonDeployController {

    @Autowired private CommonDeployService commonService;
    @Autowired private ReleaseManagementService systemReleaseService;
    @Autowired private ReleaseService releaseService;
    @Autowired private StemcellService stemcellService;
    @Autowired private StemcellManagementService stemcellManageService;
    @Autowired private CommonCodeService codeService;
    @Autowired private DirectorConfigService directorService;
    @Autowired private DeploymentService deploymentService;
    @Autowired private IaasConfigMgntService iaasConfigMgntService;
    
    private final static Logger LOGGER = LoggerFactory.getLogger(CommonCodeController.class);
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 기본 설치 관리자 정보 조회
     * @title : getDefaultDirector
     * @return : ResponseEntity<DirectorConfigVO>
    *****************************************************************/
    @RequestMapping(value="/common/use/director", method=RequestMethod.GET)
    public ResponseEntity<DirectorConfigVO> getDefaultDirector() {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> 기본 설치 관리자 정보 조회 요청"); }
        DirectorConfigVO content = directorService.getDefaultDirector();
        return new ResponseEntity<DirectorConfigVO>(content, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 기본 설치관리자에 배포명 조회 요청
     * @title : getDeploymentList
     * @return : ResponseEntity<HashMap<String,Object>>
    *****************************************************************/
    @RequestMapping(value="/common/use/deployments", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getDeploymentList(){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 배포명 조회 요청");  }
        List<DeploymentInfoDTO> contents = deploymentService.listDeployment();
        HashMap<String, Object> result = new HashMap<String, Object>();
        int size =0;
        if (contents != null) {
            size = contents.size();
        }
        result.put("total", size);
        result.put("contents", contents);
        
        return new ResponseEntity<HashMap<String, Object>>( result, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 플랫폼 별 배포 목록 조회
     * @title : getDeploymentListByPlatform
     * @return : ResponseEntity<List<String>>
    *****************************************************************/
    @RequestMapping(value="/common/deploy/deployments/{platform}/{iaas}", method=RequestMethod.GET)
    public ResponseEntity<List<String>> getDeploymentListByPlatform(@PathVariable String platform, @PathVariable String iaas){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 플랫폼 별 배포명 조회 요청");  }
        List<String> contents = commonService.listDeployment(platform, iaas);
        return new ResponseEntity<List<String>>( contents, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Private Key 파일업로드
     * @title : doBootstrapKeyPathFileUpload
     * @return : ResponseEntity<?>
    *****************************************************************/
    @RequestMapping(value="/common/deploy/key/upload", method=RequestMethod.POST)
    public ResponseEntity<?> doBootstrapKeyPathFileUpload( MultipartHttpServletRequest request){
        if(LOGGER.isInfoEnabled()){ LOGGER.debug("====================================> Private key 파일 업로드 조회 요청"); }
        commonService.uploadKeyFile(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Private Key 파일 정보 목록  조회(AWS/Openstack/google)
     * @title : getKeyPathFileList
     * @return : ResponseEntity<List<String>>
    *****************************************************************/
    @RequestMapping(value="/common/deploy/key/list/{iaasType}" , method=RequestMethod.GET)
    public ResponseEntity<List<String>> getKeyPathFileList(@PathVariable String iaasType){
        if(LOGGER.isInfoEnabled()){ LOGGER.debug("====================================> Private Key 파일  정보 목록 조회 요청"); }
        List<String> keyPathFileList = commonService.getKeyFileList(iaasType);
        
        return new ResponseEntity<List<String>>(keyPathFileList, HttpStatus.OK);
    }
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  Credential File 목록 조회(공통)
     * @title : getCredentialKeyPathFileList
     * @return : ResponseEntity<List<String>>
    *****************************************************************/
    @RequestMapping(value="/common/deploy/creds/list", method=RequestMethod.GET)
    public ResponseEntity<List<String>> getCredentialKeyPathFileList(){
        if(LOGGER.isInfoEnabled()){ LOGGER.debug("====================================> Credential Key 파일  정보 목록 조회 요청"); }
        List<String> credsKeyPathFileList = commonService.getCredentialName();
        return new ResponseEntity<List<String>>(credsKeyPathFileList, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  Hybrid Credential Key 파일  정보 목록 조회 
     * @title : getCredentialKeyPathFileList
     * @return : ResponseEntity<List<String>>
    *****************************************************************/
    @RequestMapping(value="/common/deploy/hybridCreds/list", method=RequestMethod.GET)
    public ResponseEntity<List<String>> getHybridCredentialKeyPathFileList(){
        if(LOGGER.isInfoEnabled()){ LOGGER.debug("====================================> Hybrid Credential Key 파일  정보 목록 조회 요청"); }
        List<String> credsKeyPathFileList = commonService.getHybridCredentialName();
        return new ResponseEntity<List<String>>(credsKeyPathFileList, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 배포파일 정보 조회
     * @title : getBoshAwsDeployInfo
     * @return : ResponseEntity<String>
    *****************************************************************/
    @RequestMapping(value="/common/use/deployment/{deploymentFile:.+}", method=RequestMethod.GET)
    public ResponseEntity<String> getBoshAwsDeployInfo(@PathVariable @Valid String deploymentFile){
        if(LOGGER.isInfoEnabled()){ LOGGER.debug("====================================> 배포파일 정보 조회 요청"); }
        String content = commonService.getDeploymentInfo(deploymentFile);
        return new ResponseEntity<String>(content, HttpStatus.OK);
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 배포파일 브라우저 다운로드
     * @title : downloadDeploymentFile
     * @return : void
    *****************************************************************/
    @RequestMapping(value = "/common/deploy/download/manifest/{fileName}", method = RequestMethod.GET)
    public void downloadDeploymentFile( @PathVariable("fileName") String fileName, HttpServletResponse response){
        if(LOGGER.isInfoEnabled()){ LOGGER.debug("====================================> 배포파일 브라우저 다운로드 요청"); }
        commonService.downloadDeploymentFile(fileName, response);
    }
    
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 배포파일 브라우저 다운로드
     * @title : downloadDeploymentFile
     * @return : void
    *****************************************************************/
    @RequestMapping(value = "/common/deploy/download/credential/{fileName}", method = RequestMethod.GET)
    public void downloadCredentialFile( @PathVariable("fileName") String fileName, HttpServletResponse response){
        if(LOGGER.isInfoEnabled()){ LOGGER.debug("====================================> 배포파일 브라우저 다운로드 요청"); }
        commonService.downloadCredentialFile(fileName, response);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 로컬 릴리즈 목록 조회
     * @title : localReleaseList
     * @return : ResponseEntity<List<String>>
    *****************************************************************/
    @RequestMapping(value = "/common/deploy/systemRelease/list/{type}/{iaas}", method = RequestMethod.GET)
    public ResponseEntity<List<String>> getlocalReleaseList(@PathVariable String type, @PathVariable String iaas){
        if(LOGGER.isInfoEnabled()){ LOGGER.debug("====================================> 공통 시스템 릴리즈 콤보 요청"); }
        String iaasType = iaas;
        iaasType = iaas.equalsIgnoreCase("''") ? "" : iaas;
        List<String> contents = systemReleaseService.getLocalReleaseList(type, iaasType);
        return new ResponseEntity<List<String>>( contents, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 공통 릴리즈 콤보(cf/diego/garden/etcd)
     * @title : listLocalFilterReleaseList
     * @return : ResponseEntity<Map<String,Object>>
    *****************************************************************/
    @RequestMapping( value="/common/deploy/release/list/{type}", method =RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getLocalFilterReleaseList(@PathVariable  String type){
        if(LOGGER.isInfoEnabled()){ LOGGER.debug("====================================> 공통 릴리즈 콤보 요청"); }
        List<ReleaseInfoDTO> contents = releaseService.getFilteredReleseList(type);
        Map<String, Object> result = new HashMap<>();
        result.put("records", contents);
        result.put("total", (contents == null) ? 0:contents.size());
        return new ResponseEntity<Map<String, Object>>( result, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 공통 스템셀 콤보
     * @title : listStemcell
     * @return : ResponseEntity<HashMap<String,Object>>
    *****************************************************************/
    @RequestMapping( value="/common/deploy/stemcell/list/{type}/{iaas}", method= RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getListStemcell(@PathVariable String type, @PathVariable String iaas) {
        
        if(LOGGER.isInfoEnabled()){ LOGGER.info("=======================> 업로드 된 공통 스템셀 콤보 조회 요청!"); }
        HashMap<String, Object> result = new HashMap<String, Object>();
        List<StemcellManagementVO> contents = null;
        
        if("bootstrap".equalsIgnoreCase(type)){ 
            contents = stemcellManageService.getLocalStemcellList(iaas.toLowerCase()); 
        }else{ 
            contents = stemcellService.getStemcellList(); 
        }
        result.put("total", contents.size());
        result.put("records", contents);
        return new ResponseEntity<HashMap<String, Object>>(result, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 공통 코드 조회 (하위 코드 목록)
     * @title : getSubCode
     * @return : ResponseEntity<List<CommonCodeVO>>
    *****************************************************************/
    @RequestMapping(value="/common/deploy/codes/parent/{parentCode}", method=RequestMethod.GET)
    public ResponseEntity<List<CommonCodeVO>> getSubCode(@PathVariable String parentCode) {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================================> 서브 그룹 조회 요청");  }
        List<CommonCodeVO> content = codeService.getSubCodeListBySubGroupCodeNull(parentCode);
        return new ResponseEntity<List<CommonCodeVO>>(content, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 공통 코드 조회 (하위 그룹의 코드 목록)
     * @title : getComplexCode
     * @return : ResponseEntity<List<CommonCodeVO>>
    *****************************************************************/
    @RequestMapping(value="/common/deploy/codes/parent/{parentCode}/subcode/{subGroupCode}", method=RequestMethod.GET)
    public ResponseEntity<List<CommonCodeVO>> getComplexCode(@PathVariable String parentCode, @PathVariable String subGroupCode) {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================================> 공통 코드 조회 요청");  }
        List<CommonCodeVO> content = codeService.getCodeListByParentAndSubGroup(parentCode, subGroupCode);
        return new ResponseEntity<List<CommonCodeVO>>(content, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : lock 파일 생성
     * @title : setLockFile
     * @return : ResponseEntity<Boolean>
    *****************************************************************/
    @RequestMapping(value="/common/deploy/lockFile/{fileName:.*}", method=RequestMethod.GET)
    public ResponseEntity<Boolean> setLockFile(@PathVariable @Valid String fileName){
        if(LOGGER.isInfoEnabled()){ LOGGER.debug("====================================> 락 파일 요청"); }
        Boolean lock = commonService.lockFileSet(fileName);
        return new ResponseEntity<Boolean>(lock, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 국가 코드 조회(KR 우선 정렬 조건)
     * @title : getCountryCodeList
     * @return : ResponseEntity<List<CommonCodeVO>>
    *****************************************************************/
    @RequestMapping(value="/common/deploy/codes/countryCode/{parentCode}", method=RequestMethod.GET)
    public ResponseEntity<List<CommonCodeVO>> getCountryCodeList(@PathVariable String parentCode) {
        if(LOGGER.isInfoEnabled()){ LOGGER.info("================================================> 서브 그룹 조회 요청");  }
        List<CommonCodeVO> content = codeService.getCountryCodeList(parentCode);
        return new ResponseEntity<List<CommonCodeVO>>(content, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Key 생성
     * @title : createKeyInfo
     * @return : ResponseEntity<HashMap<String,Object>>
    *****************************************************************/
    @RequestMapping( value="/common/deploy/key/createKey", method=RequestMethod.POST)
    public ResponseEntity<HashMap<String, Object>> createKeyInfo( @RequestBody KeyInfoDTO dto, Principal principal ){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> Key 생성 요청"); }
        String keyFile = commonService.createKeyInfo(dto, principal);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("keyFile", keyFile);
        return new ResponseEntity<HashMap<String, Object>>(map,HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 플랫폼 별 릴리즈 설치 지원 버전 목록 조회
     * @title : getReleaseInfoByPlatform
     * @return : ResponseEntity<List<ManifestTemplateVO>>
    *****************************************************************/
    @RequestMapping( value="/common/deploy/list/releaseInfo/{deployType}/{iaas}", method=RequestMethod.GET)
    public ResponseEntity<List<ManifestTemplateVO>> getReleaseInfoByPlatform( @PathVariable String deployType, @PathVariable String iaas ){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> 플랫폼 별 릴리즈 버전의 최적화 정보 조회 요청"); }
        List<ManifestTemplateVO> vo = commonService.getReleaseInfoByPlatform(deployType, iaas);
        return new ResponseEntity<List<ManifestTemplateVO>>(vo, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :인프라 환경 설정 목록 정보 조회 
     * @title : getIaasConfigList
     * @return : ResponseEntity<List<IaasConfigMgntVO>>
    *****************************************************************/
    @RequestMapping( value="/common/deploy/list/iaasConfig/{iaasType}", method=RequestMethod.GET)
    public ResponseEntity<List<IaasConfigMgntVO>> getIaasConfigList( @PathVariable String iaasType, Principal principal ){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> 인프라 환경 설정 목록 정보 조회 요청"); }
        List<IaasConfigMgntVO> list = iaasConfigMgntService.getIaasConfigInfoList(iaasType, principal);
        return new ResponseEntity<List<IaasConfigMgntVO>>(list, HttpStatus.OK);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인프라 환경 설정 정보 상세 조회 
     * @title : getIaasConfigInfo
     * @return : ResponseEntity<IaasConfigMgntVO>
    *****************************************************************/
    @RequestMapping( value="/common/deploy/list/iaasConfig/{iaasType}/{id}", method=RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object> > getIaasConfigInfo( @PathVariable String iaasType, @PathVariable int id, Principal principal ){
        if(LOGGER.isInfoEnabled()){ LOGGER.info("==================================> 인프라 환경 설정 정보 상세 조회 요청"); }
        HashMap<String, Object>  configInfo = commonService.getIaasConfigInfo(iaasType, id, principal);
        return new ResponseEntity<HashMap<String, Object> >(configInfo, HttpStatus.OK);
    }
    
}
