package org.openpaas.ieda.deploy.web.common.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface CommonDeployDAO {

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Manifest 템플릿 정보 조회
     * @title : selectManifetTemplate
     * @return : ManifestTemplateVO
    *****************************************************************/
    ManifestTemplateVO selectManifetTemplate(@Param("iaas") String iaas, @Param("releaseVersion") String releaseVersion, 
            @Param("deployType") String deployType, @Param("releaseType") String releaseType);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 배포유형별 배포명 목록 조회
     * @title : selectDeploymentNameByPlatform
     * @return : List<String>
    *****************************************************************/
    List<String> selectDeploymentNameByPlatform(@Param("platform") String platform, @Param("iaas") String iaas);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 배포 유형별 릴리즈 최적화 정보 목록 조회
     * @title : selectReleaseInfoByPlatform
     * @return : List<ManifestTemplateVO>
    *****************************************************************/
    List<ManifestTemplateVO> selectReleaseInfoByPlatform(@Param("deployType") String deployType, @Param("iaas") String iaas);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인프라 환경 설정 정보 및 계정 정보 조회
     * @title : selectIaasConfigAndAccountById
     * @return : HashMap<String,Object>
    *****************************************************************/
    HashMap<String, Object> selectIaasConfigAndAccountById(@Param("iaasType") String iaasType, @Param("id")int id, @Param("createUserId") String createUserId);
}
