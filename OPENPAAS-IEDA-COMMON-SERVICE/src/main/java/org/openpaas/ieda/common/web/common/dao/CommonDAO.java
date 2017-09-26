package org.openpaas.ieda.common.web.common.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface CommonDAO {
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 공통 인프라 계정 정보 목록 조회
    * @title : selectAccountInfoList
    * @return : HashMap<String, Object>
    ***************************************************/
    List<HashMap<String, Object>> selectAccountInfoList(@Param("iaasType") String iaasType, @Param("userId")String userId);
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 계정id에 일치하는 인프라 계정 정보 조회
     * @title : selectAccountInfoById
     * @return : List<HashMap<String,Object>>
    ***************************************************/
    HashMap<String, Object> selectAccountInfoById(@Param("id") Integer id, @Param("userId")String userId);
}
