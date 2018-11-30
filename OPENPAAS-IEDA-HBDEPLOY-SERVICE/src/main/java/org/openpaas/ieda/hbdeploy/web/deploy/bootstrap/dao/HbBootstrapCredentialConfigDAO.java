package org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.openpaas.ieda.hbdeploy.web.deploy.bootstrap.dto.HbBootstrapCredentialConfigDTO;

public interface HbBootstrapCredentialConfigDAO {
    
    /***************************************************
     * @project : 이종 Paas 플랫폼 설치 자동화
     * @description : 디렉터 인증서 목록 정보 조회
     * @title : selectBootstrapCredentialConfigList
     * @return : List<CredentailManagementVO> 
    ***************************************************/
    public List<HbBootstrapCredentialConfigVO> selectBootstrapCredentialConfigList();
    
    /***************************************************
     * @project : 이종 Paas 플랫폼 설치 자동화
     * @description : 아이디를 기준으로 디렉터 인증서 정보를 삭제 한다.
     * @title : deleteBootstrapCredentialConfigById
     * @return : void
    ***************************************************/
    public void deleteBootstrapCredentialConfig(@Param("dto") HbBootstrapCredentialConfigDTO dto);
    
    /***************************************************
     * @project : 이종 Paas 플랫폼 설치 자동화
     * @description : 디렉터 인증서 파일 명을 기준으로 count 조회
     * @title : selectBootstrapCredentialConfigByName
     * @return : void
    ***************************************************/
    public int selectBootstrapCredentialConfigByName(@Param("credentialName")String credentialName);
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 디렉터 인증서 정보 상세 조회
     * @title : selectBootstrapCredentialConfigInfo
     * @return : HbBootstrapDefaultVO
    *****************************************************************/
    HbBootstrapCredentialConfigVO selectBootstrapCredentialConfigInfo(@Param("id")int id, @Param("iaas")String iaas);
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 디렉터 인증서 정보 등록
     * @title : insertBootstrapCredentialConfigInfo
     * @return : void
    *****************************************************************/
    void insertBootstrapCredentialConfigInfo(@Param("vo")HbBootstrapCredentialConfigVO vo);

    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : 디렉터 인증서 정보 수정
     * @title : updateBootstrapCredentialConfigInfo
     * @return : void
    *****************************************************************/
    void updateBootstrapCredentialConfigInfo(@Param("vo")HbBootstrapCredentialConfigVO vo);
    
    
}
