package org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto.HbBootstrapDefaultConfigDTO;

public interface HbBootstrapDefaultConfigDAO {
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 기본 정보 목록 조회
     * @title : selectBootstrapDefaultConfigInfoList
     * @return : List<HbBootstrapDefaultVO>
    *****************************************************************/
    List<HbBootstrapDefaultConfigVO> selectBootstrapDefaultConfigInfoList();
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 기본 정보 상세 조회
     * @title : saveCpiInfo
     * @return : HbBootstrapDefaultVO
    *****************************************************************/
    HbBootstrapDefaultConfigVO selectBootstrapDefaultConfigInfo(@Param("id")int id, @Param("iaas")String iaas);
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 기본 정보 등록
     * @title : insertBootstrapDefaultConfigInfo
     * @return : void
    *****************************************************************/
    void insertBootstrapDefaultConfigInfo(@Param("default")HbBootstrapDefaultConfigVO vo);

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 기본 정보 수정
     * @title : updateBootstrapDefaultConfigInfo
     * @return : void
    *****************************************************************/
    void updateBootstrapDefaultConfigInfo(@Param("default")HbBootstrapDefaultConfigVO vo);
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 기본 정보 삭제
     * @title : deleteBootStrapDefaultConfigInfo
     * @return : void
    *****************************************************************/
    void deleteBootstrapDefaultConfigInfo(@Param("default")HbBootstrapDefaultConfigDTO dto);
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 기본 정보 조회
     * @title : selectBootstrapDefaultfconfigByName
     * @return : void
    *****************************************************************/
	int selectBootstrapDefaultConfigByName(@Param("defaultConfigName")String defaultConfigName);
}
