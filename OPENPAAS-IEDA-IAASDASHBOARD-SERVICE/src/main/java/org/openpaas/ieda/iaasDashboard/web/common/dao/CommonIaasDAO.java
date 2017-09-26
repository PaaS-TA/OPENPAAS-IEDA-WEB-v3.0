package org.openpaas.ieda.iaasDashboard.web.common.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.openpaas.ieda.iaasDashboard.web.account.dao.IaasAccountMgntVO;


public interface CommonIaasDAO {
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 기본 인프라 계정 정보 조회
    * @title : selectdefaultIaasAccountInfo
    * @return : HashMap<String, String>
    ***************************************************/
    HashMap<String, String> selectdefaultIaasAccountInfo(@Param("defaultYn") String defaultYn, @Param("iaasType") String iaasType);
    
    /***************************************************
    * @project : Paas 플랫폼 설치 자동화
    * @description : 기본 인프라 계정 정보 저장
    * @title : updateDefaultIaasAccountInfo
    * @return : int
    ***************************************************/
    int updateDefaultIaasAccountInfo(@Param("account") IaasAccountMgntVO vo);
    
    /***************************************************
     * @project : 인프라 관리 대시보드
     * @description : AWS Ingress Rules Traffice Type 정보 목록을 조회
     * @title : selectParentCodeAndSubGroupCode
     * @return : List<CommonIaasVO>
    ***************************************************/
    List<CommonCodeVO> selectParentCodeAndSubGroupCode(@Param("parentCode")String parentCode);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인바운드 규칙 정보 조회
     * @title : selectIngressRulesInfo
     * @return : List<CommonIaasVO>
    *****************************************************************/
    CommonCodeVO selectIngressRulesInfo(@Param("parentCode") Integer parentCode,@Param("codeValue") String codeValue, @Param("codeNameKr") String codeNameKr);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인바운드 규칙 sub group code 정보 조회
     * @title : selectIngressRulesInfoBySubGroupCode
     * @return : CommonIaasVO
    *****************************************************************/
    CommonCodeVO selectIngressRulesInfoBySubGroupCode(@Param("parentCode") Integer parentCode,
            @Param("codeNameKr") String codeNameKr,@Param("subGroupCode") Integer subGroupCode);

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인바운드 규칙 under sub group code 정보 조회
     * @title : selectIngressRulesInfoBySubGroupCodeAndUsubGroupCode
     * @return : CommonIaasVO
    *****************************************************************/
    CommonCodeVO selectIngressRulesInfoBySubGroupCodeAndUsubGroupCode(@Param("parentCode") Integer parentCode,@Param("codeValue") String codeValue,
            @Param("subGroupCode") Integer subGroupCode, @Param("uSubGroupCode")Integer uSubGroupCode);
}
