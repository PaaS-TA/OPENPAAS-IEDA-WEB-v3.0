package org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dto.HbCfDeploymentResourceConfigDTO;

public interface HbCfDeploymentResourceConfigDAO {
	/****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 리스소 목록 조회
     * @title : insertCfDeploymentResourceConfigInfo
     * @return : List<HbCfDeploymentResourceConfigVO>
    *****************************************************************/
    List<HbCfDeploymentResourceConfigVO> selectCfDeploymentResourceConfigInfoList();

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 리스소 정보 상세 조회
     * @title : selectCfDeploymentResourceConfigInfo
     * @return : HbCfDeploymentResourceConfigVO
    *****************************************************************/
    HbCfDeploymentResourceConfigVO selectCfDeploymentResourceConfigInfo(@Param("id")int id, @Param("iaas")String iaas);
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 리스소 정보 등록
     * @title : insertCfDeploymentResourceConfigInfo
     * @return : HbCfDeploymentResourceConfigVO
    *****************************************************************/
    void insertCfDeploymentResourceConfigInfo(@Param("resource")HbCfDeploymentResourceConfigVO vo);

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 리스소 정보 수정
     * @title : updateCfDeploymentResourceConfigInfo
     * @return : HbCfDeploymentCpiVO
    *****************************************************************/
    void updateCfDeploymentResourceConfigInfo(@Param("resource")HbCfDeploymentResourceConfigVO vo);
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 리스소 정보 삭제
     * @title : deleteCfDeploymentResourceConfigInfo
     * @return : HbCfDeploymentCpiVO
    *****************************************************************/
    void deleteCfDeploymentResourceConfigInfo(@Param("resource")HbCfDeploymentResourceConfigDTO dto);
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 리스소 정보 이름으로 조회
     * @title : selectCfDeploymentResourceConfigByName
     * @return : void
    *****************************************************************/
	int selectCfDeploymentResourceConfigByName(@Param("resourceConfigName")String resourceConfigName);
}
