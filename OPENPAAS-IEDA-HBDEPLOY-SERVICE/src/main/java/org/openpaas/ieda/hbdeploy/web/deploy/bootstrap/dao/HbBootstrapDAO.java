package org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto.HbBootStrapDeployDTO;

public interface HbBootstrapDAO {
    
    /****************************************************************
     * @param installStatus 
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Bootstrap 전체 목록 조회
     * @title : selectBootstrapList
     * @return : List<BootstrapVO>
    *****************************************************************/
    List<HbBootstrapVO> selectBootstrapList(@Param("installStatus") String installStatus);
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : BootStrap 정보 삭제
     * @title : deleteBootstrapInfo
     * @return : void
    *****************************************************************/
    void deleteBootstrapInfo(@Param("dto")HbBootStrapDeployDTO dto);
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : hybridboostrap 설치 정보 중복 값 확인
     * @title : selectBootstrapConfigByName
     * @return : int
    ***************************************************/
    int selectBootstrapConfigByName(@Param("bootstrapConfigName") String bootstrapConfigName);
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : hybridboostrap 설치 정보 조회
     * @title : selectBootstrapConfigInfo
     * @return : HbBootstrapVO
    ***************************************************/
    HbBootstrapVO selectBootstrapConfigInfo(@Param("id")int id, @Param("iaasType")String iaasType);
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : hybridboostrap 설치 정보 삽입
     * @title : insertBootStrapConfigInfo
     * @return : void
    ***************************************************/
    void insertBootStrapConfigInfo(@Param("vo")HbBootstrapVO vo);
    
    /***************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : hybridboostrap 설치 정보 수정
     * @title : updateBootStrapConfigInfo
     * @return : void
    ***************************************************/
    void updateBootStrapConfigInfo(@Param("vo")HbBootstrapVO vo);
}
