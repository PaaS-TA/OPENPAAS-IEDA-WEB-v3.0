package org.openpaas.ieda.deploy.web.config.credential.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface CredentialManagementDAO {
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 디렉터 인증서 목록 정보 조회
     * @title : selectDirectorCredentialList
     * @return : List<CredentailManagementVO> 
    ***************************************************/
    public List<CredentialManagementVO> selectDirectorCredentialList();
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 아이디를 기준으로 디렉터 인증서 정보를 삭제 한다.
     * @title : deleteCredentialInfoById
     * @return : void
    ***************************************************/
    public void deleteCredentialInfoById(@Param("id")String id);
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 디렉터 인증서 파일 명을 기준으로 count 조회
     * @title : selectdirectorCredentialInfoByName
     * @return : void
    ***************************************************/
    public int selectdirectorCredentialInfoByName(@Param("credentialName")String credentialName);
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 디렉터 인증서 정보 저장
     * @title : saveDirectorCredentialInfo
     * @return : void
    ***************************************************/
	public void saveDirectorCredentialInfo(@Param("vo") CredentialManagementVO vo);
}
