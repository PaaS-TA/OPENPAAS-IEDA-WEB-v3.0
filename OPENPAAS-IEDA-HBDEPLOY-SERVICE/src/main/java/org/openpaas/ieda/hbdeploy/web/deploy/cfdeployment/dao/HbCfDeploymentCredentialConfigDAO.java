package org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.openpaas.ieda.hbdeploy.web.deploy.cfdeployment.dto.HbCfDeploymentCredentialConfigDTO;

public interface HbCfDeploymentCredentialConfigDAO {
	
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybird CF Deployment 인증서 전체 목록 조회
     * @title : selectHbCfDeploymentCredentialConfigInfoList
     * @return : List<HbCfDeploymentCredentialConfigVO>
    *****************************************************************/
    List<HbCfDeploymentCredentialConfigVO> selectHbCfDeploymentCredentialConfigInfoList();
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybird CF Deployment 인증서 상세 조회
     * @title : selectHbCfDeploymentCredentialConfigInfo
     * @return : HbCfDeploymentCredentialConfigVO
    *****************************************************************/
    HbCfDeploymentCredentialConfigVO selectHbCfDeploymentCredentialConfigInfo(@Param("id")int id);
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybird CF Deployment 인증서 정보 삽입
     * @title : insertHbCfDeploymentCredentialConfigInfo
     * @return : void
    *****************************************************************/
    void insertHbCfDeploymentCredentialConfigInfo(@Param("credential")HbCfDeploymentCredentialConfigVO vo);
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybird CF Deployment 인증서 정보 수정
     * @title : updateHbCfDeploymentCredentialConfigInfo
     * @return : void
    *****************************************************************/
    void updateHbCfDeploymentCredentialConfigInfo(@Param("credential")HbCfDeploymentCredentialConfigVO vo);
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybird CF Deployment 인증서 정보 삭제
     * @title : deleteHbCfDeploymentCredentialConfigInfo
     * @return : void
    *****************************************************************/
    void deleteHbCfDeploymentCredentialConfigInfo(@Param("credential")HbCfDeploymentCredentialConfigDTO dto);
    
    /****************************************************************
     * @project : Paas 이종 플랫폼 설치 자동화
     * @description : Hybird CF Deployment 명칭 중복 확인
     * @title : selectHbCfDeploymentCredentialConfigByName
     * @return : void
    *****************************************************************/
    int selectHbCfDeploymentCredentialConfigByName(@Param("credentialConfigName")String credentialConfigName);

}
