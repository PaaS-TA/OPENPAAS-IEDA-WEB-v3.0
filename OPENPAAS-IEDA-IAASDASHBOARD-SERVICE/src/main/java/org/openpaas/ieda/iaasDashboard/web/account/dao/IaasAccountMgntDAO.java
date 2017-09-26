package org.openpaas.ieda.iaasDashboard.web.account.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface IaasAccountMgntDAO {
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 전체 Iaas Account 목록 정보 조회
     * @title : selectAllIaasAccountInfoList
     * @return : List<IaasAccountMgntVO>
    ***************************************************/
    List<IaasAccountMgntVO> selectAllIaasAccountInfoList(@Param("userId") String userId);
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 인프라 별 계정 개수 조회
     * @title : selectIaasAccountCount
     * @return : HashMap
    ***************************************************/
    HashMap<String, Integer> selectIaasAccountCount(@Param("userId") String userId);
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : Iaas Account 목록 정보 조회
     * @title : selectIaasAccountInfoList
     * @return : List<IaasAccountMgntVO>
    ***************************************************/
    List<IaasAccountMgntVO> selectIaasAccountInfoList(@Param("userId") String userId, @Param("iaasType") String iaasType);
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 계정 정보에 의한 중복 조회
     * @title : selectIaasAccountDuplication
     * @return : int
    ***************************************************/
    int selectIaasAccountDuplicationByInfraAccount(@Param("account") IaasAccountMgntVO vo);
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 계정 별칭에 의한 중복 조회
     * @title : selectIaasAccountDuplicationByAccountName
     * @return : int
    ***************************************************/
    int selectIaasAccountDuplicationByAccountName(@Param("account") IaasAccountMgntVO vo);
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 인프라 별 계정 정보 등록
     * @title : insertIaasAccountInfo
     * @return : int
    ***************************************************/
    int insertIaasAccountInfo(@Param("account") IaasAccountMgntVO vo);
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 인프라 별 계정 상세 정보 조회
     * @title : selectIaasAccountInfo
     * @return : IaasAccountMgntVO
    ***************************************************/
    IaasAccountMgntVO selectIaasAccountInfo(@Param("createUserId") String userId, @Param("iaasType") String iaasType, @Param("id") int id);
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 인프라 별 계정 정보 수정 
     * @title : updateIaasAccountInfo
     * @return : int
    ***************************************************/
    int updateIaasAccountInfo(@Param("account") IaasAccountMgntVO vo);
    
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : 인프라 계정 정보 삭제
     * @title : deleteIaasAccountInfo
     * @return : int
    ***************************************************/
    int deleteIaasAccountInfo(@Param("createUserId") String userId, @Param("id") int id);
}
