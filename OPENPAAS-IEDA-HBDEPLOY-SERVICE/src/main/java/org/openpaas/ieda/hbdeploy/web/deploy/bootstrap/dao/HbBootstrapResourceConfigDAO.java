package org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto.HbBootstrapResourceConfigDTO;

public interface HbBootstrapResourceConfigDAO {
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 리소스 목록 조회
     * @title : selectBootstrapResourceConfigInfoList
     * @return : List<HbBootstrapResourceConfigVO>
    *****************************************************************/
    List<HbBootstrapResourceConfigVO> selectBootstrapResourceConfigInfoList();

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CPI 정보 상세 조회
     * @title : selectBootstrapResourceConfigInfo
     * @return : HbBootstrapResourceConfigVO
    *****************************************************************/
    HbBootstrapResourceConfigVO selectBootstrapResourceConfigInfo(@Param("id")int id, @Param("iaas")String iaas);
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CPI 정보 등록
     * @title : insertBootStrapResourceConfigInfo
     * @return : HbBootstrapResourceConfigVO
    *****************************************************************/
    void insertBootStrapResourceConfigInfo(@Param("resource")HbBootstrapResourceConfigVO vo);

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 리스소 정보 수정
     * @title : updateBootStrapResourceConfigInfo
     * @return : HbBootstrapCpiVO
    *****************************************************************/
    void updateBootStrapResourceConfigInfo(@Param("resource")HbBootstrapResourceConfigVO vo);
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 리스소 정보 삭제
     * @title : deleteBootStrapResourceConfigInfo
     * @return : HbBootstrapCpiVO
    *****************************************************************/
    void deleteBootStrapResourceConfigInfo(@Param("resource")HbBootstrapResourceConfigDTO dto);
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 리스소 정보 조회
     * @title : selectBootstrapResourceConfigByName
     * @return : void
    *****************************************************************/
	int selectBootstrapResourceConfigByName(@Param("resourceConfigName")String networkConfigName);
}

