package org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dto.HbCfDeploymentDefaultConfigDTO;

public interface HbCfDeploymentDefaultConfigDAO {
	
	/****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 기본 정보 목록 조회
     * @title : selectHbCfDeploymentDefaultConfigInfoList
     * @return : List<HbCfDeploymentDefaultConfigVO>
    *****************************************************************/
    List<HbCfDeploymentDefaultConfigVO> selectHbCfDeploymentDefaultConfigInfoList();

	/****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 기본 정보 상세 조회
     * @title : selectHbCfDeploymentDefaultConfigInfo
     * @return : HbCfDeploymentDefaultConfigVO
    *****************************************************************/
    HbCfDeploymentDefaultConfigVO selectHbCfDeploymentDefaultConfigInfo(@Param("id")int id, @Param("iaas")String iaas);

	/****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 기본 정보 등록 
     * @title : insertHbCfDeploymentDefaultConfigInfo
     * @return : void
    *****************************************************************/   
    void insertHbCfDeploymentDefaultConfigInfo(@Param("default")HbCfDeploymentDefaultConfigVO vo);

	/****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 기본 정보 수정 
     * @title : updateHbCfDeploymentDefaultConfigInfo
     * @return : void
    *****************************************************************/ 
    void updateHbCfDeploymentDefaultConfigInfo(@Param("default")HbCfDeploymentDefaultConfigVO vo);

	/****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 기본 정보 삭제  
     * @title : deleteHbCfDeploymentDefaultConfigInfo
     * @return : void
    *****************************************************************/ 
    void deleteHbCfDeploymentDefaultConfigInfo(@Param("default")HbCfDeploymentDefaultConfigDTO dto);

	/****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 기본 정보 개수 이름으로 조회
     * @title : selectHbCfDeploymentDefaultConfigByName
     * @return : int
    *****************************************************************/ 
    int selectHbCfDeploymentDefaultConfigByName(@Param("defaultConfigName")String defaultConfigName);

}