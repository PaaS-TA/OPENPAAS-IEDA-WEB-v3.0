package org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dto.HbCfDeploymentDTO;

public interface HbCfDeploymentDAO {
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CF Deployment 전체 정보 목록 조회
     * @title : selectCfDeploymentList
     * @return : List<HbCfDeploymentVO> 
    *****************************************************************/
    List<HbCfDeploymentVO> selectCfDeploymentList(@Param("installStatus")String installStatus);
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description :  CF Deployment 설치 중복 값 확인
     * @title : selectCfDeploymentConfigByName
     * @return : int
    *****************************************************************/
    int selectCfDeploymentConfigByName(@Param("cfDeploymentConfigName")String cfDeploymentConfigName);
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description :  CF Deployment 설치 정보 삽입
     * @title : insertCfDeploymentConfigInfo
     * @return : void
    *****************************************************************/
    void insertCfDeploymentConfigInfo(@Param("vo") HbCfDeploymentVO vo);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  CF Deployment 설치된 정보 조회
     * @title : selectCfDeploymentConfigInfo
     * @return : HbCfDeploymentVO
    *****************************************************************/
    HbCfDeploymentVO selectCfDeploymentConfigInfo(@Param("id")int id);
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description :  CF Deployment 설치 정보 수정
     * @title : updateCfDeploymentConfigInfo
     * @return : void
    *****************************************************************/
    void updateCfDeploymentConfigInfo(@Param("vo") HbCfDeploymentVO vo);
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description :  CF Deployment 설치 정보 삭제
     * @title : deleteCfDeploymentConfigInfo
     * @return : void
    *****************************************************************/
    void deleteCfDeploymentConfigInfo(@Param("dto") HbCfDeploymentDTO dto);

}
