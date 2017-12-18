package org.openpaas.ieda.deploy.web.deploy.cf.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface CfDAO {
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 설치 정보 목록 조회
     * @title : selectCfList
     * @return : List<CfVO>
    *****************************************************************/
    List<CfVO> selectCfList(@Param("iaas")String iaas, @Param("platform")String platform);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 배포명 중복 조회
     * @title : selectCfDeploymentNameDuplication
     * @return : int
    *****************************************************************/
    int selectCfDeploymentNameDuplication(@Param("iaasType") String iaasType, 
            @Param("deploymentName") String deploymentName, @Param("id")Integer id );
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 설치 정보 상세 조회
     * @title : selectCfInfoById
     * @return : CfVO
    *****************************************************************/
    CfVO selectCfInfoById(@Param("id")int id);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 리소스 정보 상세 조회
     * @title : selectCfResourceInfoById
     * @return : CfVO
    *****************************************************************/
    CfVO selectCfResourceInfoById(@Param("id")Integer id, @Param("deployType")String deployType);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 설치 정보 저장
     * @title : insertCfInfo
     * @return : void
    *****************************************************************/
    void insertCfInfo(@Param("cf")CfVO vo);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 설치 정보 수정
     * @title : updateCfInfo
     * @return : void
    *****************************************************************/
    void updateCfInfo(@Param("cf")CfVO vo);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 단순 레코드 삭제 
     * @title : deleteCfInfoRecord
     * @return : void
    *****************************************************************/
    void deleteCfInfoRecord(@Param("id")int id);

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF Manifest Template 조회
     * @title : selectDeploymentFilebyDeploymentName
     * @return : CfVO
    *****************************************************************/
    CfVO selectDeploymentFilebyDeploymentName(@Param("cfDeploymentName") String cfDeployName);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 릴리즈 버전 및 배포 유형 별 Job 목록 조회
     * @title : selectCfJobTemplatesByReleaseVersion
     * @return : List<HashMap<String,String>>
    *****************************************************************/
    List<HashMap<String, String>> selectCfJobTemplatesByReleaseVersion(@Param("map") HashMap<String, String> map);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 해당 CF의 고급설정 정보 저장
     * @title : insertCfJobSettingInfo
     * @return : void
    *****************************************************************/
    void insertCfJobSettingInfo(@Param("maps") List<HashMap<String, String>> maps);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Cf 아이디에 따른 job 정보 조회
     * @title : selectCfJobSettingInfoListBycfId
     * @return : List<HashMap<String,String>>
    *****************************************************************/
    List<HashMap<String, Object>> selectCfJobSettingInfoListBycfId(@Param("deployType")String deployType, @Param("cfId")int cfId);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : CF 고급 설정 정보 목록 삭제
     * @title : deleteCfJobSettingInfo
     * @return : void
    *****************************************************************/
    void deleteCfJobSettingListById(@Param("map")HashMap<String, String> map);
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : cf id 및 해당 zone에 일치하는 레코드 삭제
     * @title : deleteCfJobSettingRecords
     * @return : void
    ***************************************************/
    void deleteCfJobSettingRecordsByIdAndZone(@Param("id")int id, @Param("zone") String zone );
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 
     * @title : selectCfInfoByDeploymentName
     * @return : CfVO
    ***************************************************/
    CfVO selectCfInfoByDeploymentName(@Param("iaasType") String iaasType,@Param("deploymentFile") String deploymentFile);
}
