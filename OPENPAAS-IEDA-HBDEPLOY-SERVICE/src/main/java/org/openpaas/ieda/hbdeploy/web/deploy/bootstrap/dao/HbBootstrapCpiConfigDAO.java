package org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto.HbBootstrapCpiConfigDTO;

public interface HbBootstrapCpiConfigDAO {
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CPI 정보 목록 조회
     * @title : selectBootstrapCpiConfigInfoList
     * @return : List<HbBootstrapCpiVO>
    *****************************************************************/
    List<HbBootstrapCpiConfigVO> selectBootstrapCpiConfigInfoList();
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CPI 정보 상세 조회
     * @title : saveCpiInfo
     * @return : HbBootstrapCpiVO
    *****************************************************************/
    HbBootstrapCpiConfigVO selectBootstrapCpiConfigInfo(@Param("id")int id, @Param("iaas")String iaas);
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CPI 정보 등록
     * @title : saveCpiInfo
     * @return : HbBootstrapCpiVO
    *****************************************************************/
    void insertBootStrapCpiConfigInfo(@Param("cpi")HbBootstrapCpiConfigVO vo);

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CPI 정보 수정
     * @title : saveCpiInfo
     * @return : HbBootstrapCpiVO
    *****************************************************************/
    void updateBootStrapCpiConfigInfo(@Param("cpi")HbBootstrapCpiConfigVO vo);
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CPI 정보 삭제
     * @title : saveCpiInfo
     * @return : HbBootstrapCpiVO
    *****************************************************************/
    void deleteBootStrapCpiConfigInfo(@Param("cpi")HbBootstrapCpiConfigDTO vo);
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : CPI 정보 조회
     * @title : selectbootstrapcpifconfigByName
     * @return : void
    *****************************************************************/
	int selectBootstrapCpiConfigByName(@Param("cpiName")String cpiName);
}
