package org.openpaas.ieda.deploy.web.deploy.diego.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface DiegoDAO {
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Diego 목록 정보를 조회
     * @title : selectDiegoListInfo
     * @return : List<DiegoVO>
    *****************************************************************/
    List<DiegoVO> selectDiegoListInfo(@Param("iaasType")String iaasType);

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Diego 정보 상세 조회
     * @title : selectDiegoInfo
     * @return : DiegoVO
    *****************************************************************/
    DiegoVO selectDiegoInfo(@Param("id")int id);

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Diego 기본정보 저장
     * @title : insertDiegoDefaultInfo
     * @return : void
    *****************************************************************/
    void insertDiegoDefaultInfo(@Param("diego")DiegoVO vo);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Diego 정보 저장
     * @title : updateDiegoDefaultInfo
     * @return : void
    *****************************************************************/
    void updateDiegoDefaultInfo(@Param("diego")DiegoVO vo);

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Diego 단순 레코드 삭제
     * @title : deleteDiegoInfoRecord
     * @return : void
    *****************************************************************/
    void deleteDiegoInfoRecord(@Param("id")int id);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : DIEGO 리소스 정보 상세 조회
     * @title : selectResourceInfoById
     * @return : DiegoVO
    *****************************************************************/
    DiegoVO selectResourceInfoById(@Param("id")int id, @Param("deployType")String deployType);
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : Diego Job 정보 목록 조회
    * @title : selectDiegoJobTemplatesByReleaseVersion
    * @return : List<HashMap<String, String>>
    ***************************************************/
    List<HashMap<String, String>> selectDiegoJobTemplatesByReleaseVersion(@Param("map") HashMap<String, String> map);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Diego 아이디에 따른 job 정보 조회
     * @title : selectDiegoJobSettingInfoListBycfId
     * @return : List<HashMap<String,String>>
    *****************************************************************/
    List<HashMap<String, Object>> selectDiegoJobSettingInfoListBycfId(@Param("deployType")String deployType, @Param("diegoId")int diegoId);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 해당 Diego의 고급설정 정보 저장
     * @title : insertDiegoJobSettingInfo
     * @return : void
    *****************************************************************/
    void insertDiegoJobSettingInfo(@Param("maps")List<HashMap<String, String>> maps);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : Diego 고급 설정 정보 삭제
     * @title : deleteDiegoJobSettingInfo
     * @return : void
    *****************************************************************/
    void deleteDiegoJobSettingInfo(@Param("map")HashMap<String, String> hashMap);
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 배포 명 중복 조회
    * @title : selectDiegoDeploymentNameDuplication
    * @return : int
    ***************************************************/
    int selectDiegoDeploymentNameDuplication(@Param("iaasType") String iaasType, 
            @Param("deploymentName") String deploymentName, @Param("id")Integer id);

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : cf id 및 해당 zone에 일치하는 레코드 삭제
     * @title : deleteCfJobSettingRecords
     * @return : void
    ***************************************************/
    void deleteDiegoJobSettingRecordsByIdAndZone(@Param("id")int id, @Param("zone") String zone );
    
}
