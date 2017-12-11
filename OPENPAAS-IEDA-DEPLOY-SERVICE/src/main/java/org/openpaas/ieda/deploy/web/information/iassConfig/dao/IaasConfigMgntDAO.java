package org.openpaas.ieda.deploy.web.information.iassConfig.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface IaasConfigMgntDAO {

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 전체 환경 설정 목록 정보 조회
     * @title : selectAllIaasConfigInfoList
     * @return : List<IaasConfigMgntVO>
    ***************************************************/
    List<IaasConfigMgntVO> selectAllIaasConfigInfoList(@Param("userId") String userId);

     /***************************************************
     * @project :  Paas 플랫폼 설치 자동화
     * @description : 인프라 별 환경 설정 개수 조회
     * @title : selectIaasConfigCount
     * @return : HashMap
    ***************************************************/
    HashMap<String, Integer> selectIaasConfigCount(@Param("userId") String userId);

     /***************************************************
     * @project :  Paas 플랫폼 설치 자동화
     * @description : 인프라 별 환경 설정 목록 정보 조회
     * @title : selectIaasConfigInfoList
     * @return : List<IaasConfigMgntVO>
    ***************************************************/
    List<IaasConfigMgntVO> selectIaasConfigInfoList(@Param("userId") String userId, @Param("iaasType") String iaasType);

    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 환경 설정 정보에 의한 중복 조회
     * @title : selectIaasAccountDuplicationByConfigInfo
     * @return : int
    ***************************************************/
    int selectIaasConfigDuplicationByConfigInfo(@Param("config") IaasConfigMgntVO vo);
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 환경 설정 별칭에 의한 중복 조회
     * @title : selectIaasAccountDuplicationByConfigName
     * @return : int
    ***************************************************/
    int selectIaasConfigDuplicationByConfigName(@Param("config") IaasConfigMgntVO vo);

    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 인프라 별 계정 정보 등록
     * @title : insertIaasConfigInfo
     * @return : int
    ***************************************************/
    int insertIaasConfigInfo(@Param("config") IaasConfigMgntVO vo);
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인프라 별 환경 설정 상세 정보 조회
     * @title : selectIaasConfigInfo
     * @return : IaasConfigMgntVO
    ***************************************************/
    IaasConfigMgntVO selectIaasConfigInfo(@Param("userId") String userId, @Param("iaasType") String iaasType, @Param("id") int id);
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인프라 별 환경 설정 정보 수정
     * @title : updateIaasConfigInfo
     * @return : void
    ***************************************************/
    int updateIaasConfigInfo(@Param("config") IaasConfigMgntVO vo);
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 환경 설정 정보 삭제
     * @title : deleteIaasConfigInfo
     * @return : int
    ***************************************************/
    int deleteIaasConfigInfo( @Param("createUserId") String createUserId, @Param("id") int id );
    
}