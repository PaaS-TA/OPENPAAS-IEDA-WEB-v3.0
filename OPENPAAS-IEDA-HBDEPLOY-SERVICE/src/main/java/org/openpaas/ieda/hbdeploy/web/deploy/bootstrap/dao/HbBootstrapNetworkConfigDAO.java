package org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto.HbBootstrapNetworkConfigDTO;

public interface HbBootstrapNetworkConfigDAO {
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 네트워크 정보 목록 조회
     * @title : selectBootstrapNetworkConfigInfoList
     * @return : List<HbBootstrapNetworkConfigVO>
    *****************************************************************/
    List<HbBootstrapNetworkConfigVO> selectBootstrapNetworkConfigInfoList();
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 네트워크 정보 상세 조회
     * @title : selectBootstrapNetworkConfigInfo
     * @return : HbBootstrapNetworkConfigVO
    *****************************************************************/
    HbBootstrapNetworkConfigVO selectBootstrapNetworkConfigInfo(@Param("id")int id, @Param("iaas")String iaas);
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 네트워크 정보 삽입
     * @title : insertBootStrapNetworkConfigInfo
     * @return : void
    *****************************************************************/
    void insertBootStrapNetworkConfigInfo(@Param("network")HbBootstrapNetworkConfigVO vo);

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 네트워크 정보 수정
     * @title : updateBootStrapNetworkConfigInfo
     * @return : void
    *****************************************************************/
    void updateBootStrapNetworkConfigInfo(@Param("network")HbBootstrapNetworkConfigVO vo);
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 네트워크 정보 삭제
     * @title : deleteBootStrapNetworkConfigInfo
     * @return : void
    *****************************************************************/
    void deleteBootStrapNetworkConfigInfo(@Param("network")HbBootstrapNetworkConfigDTO dto);
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 네트워크 정보 조회
     * @title : selectBootstrapNetworkConfigByName
     * @return : int
    *****************************************************************/
    int selectBootstrapNetworkConfigByName(@Param("networkConfigName")String networkConfigName);
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 인프라 환경 별 네트워크 정보 조회
     * @title : selectBootstrapNetworkConfigInfoListByIaasType
     * @return : List<HbBootstrapNetworkConfigVO>
    *****************************************************************/
    List<HbBootstrapNetworkConfigVO> selectBootstrapNetworkConfigInfoListByIaasType(@Param("iaasType") String iaasType);
}
