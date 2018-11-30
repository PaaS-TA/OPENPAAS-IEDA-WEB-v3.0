package org.openpaas.ieda.hbdeploy.web.config.release.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.openpaas.ieda.hbdeploy.web.config.release.dto.HbReleaseManagementDTO;

public interface HbReleaseManagementDAO {
    
    /****************************************************************
     * @project : 이종 플랫폼 설치 자동화
     * @description : 릴리즈 유형을 기준으로 정렬하여 시스템 릴리즈 정보 목록 조회
     * @title : selectHybridReleaseList
     * @return : List<HbReleaseManagementVO>
    *****************************************************************/
    List<HbReleaseManagementVO> selectHybridReleaseList(@Param("iaasType")String iaas);
    
    /****************************************************************
     * @project : 이종 플랫폼 설치 자동화
     * @description : 파일명에 따른 시스템 릴리즈 정보 상세 조회
     * @title : selectHybridRelease
     * @return : HbReleaseManagementVO
    *****************************************************************/
    HbReleaseManagementVO selectHybridRelease(@Param("fileName")String fileName);
    
    /****************************************************************
     * @project : 이종 플랫폼 설치 자동화
     * @description : Id에 따른 시스템 릴리즈 정보 상세 조회
     * @title : selectHybridReleaseById
     * @return : HbReleaseManagementVO
    *****************************************************************/
    HbReleaseManagementVO selectHybridReleaseById(@Param("id")Integer id);
    
    /****************************************************************
     * @project : 이종 플랫폼 설치 자동화
     * @description : 시스템 릴리즈 정보 저장
     * @title : insertHybridRelease
     * @return : void
    *****************************************************************/
    void insertHybridRelease(@Param("release")HbReleaseManagementDTO.Regist dto);
    
    /****************************************************************
     * @project : 이종 플랫폼 설치 자동화
     * @description : Id에 따른 시스템 릴리즈 파일명에 따른 정보 수정
     * @title : updateHybridRelease
     * @return : void
    *****************************************************************/
    void updateHybridRelease(@Param("release")HbReleaseManagementDTO.Regist dto);
    
    /****************************************************************
     * @project : 이종 플랫폼 설치 자동화
     * @description : 시스템 릴리즈 id에 따른 정보 수정
     * @title : updateHybridReleaseById
     * @return : int
    *****************************************************************/
    int updateHybridReleaseById(@Param("release")HbReleaseManagementDTO.Regist dto);
    
    /****************************************************************
     * @project : 이종 플랫폼 설치 자동화
     * @description : 시스템 릴리즈 정보 삭제
     * @title : deleteHybridRelase
     * @return : void
    *****************************************************************/
    void deleteHybridRelease(@Param("release")HbReleaseManagementDTO.Delete dto);
    
    /****************************************************************
     * @project : 이종 플랫폼 설치 자동화
     * @description : 공통 릴리즈 콤보
     * @title : selectLocalReleaseList
     * @return : List<String>
    *****************************************************************/
    List<String> selectLocalReleaseList(@Param("type")String type, @Param("iaas")String iaas);
    
}
