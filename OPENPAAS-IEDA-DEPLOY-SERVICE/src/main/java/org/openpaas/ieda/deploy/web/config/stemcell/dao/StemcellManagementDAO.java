package org.openpaas.ieda.deploy.web.config.stemcell.dao;
 
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.openpaas.ieda.deploy.web.config.stemcell.dto.StemcellManagementDTO;
import org.openpaas.ieda.deploy.web.config.stemcell.dto.StemcellManagementDTO.Delete;
 
public interface StemcellManagementDAO {
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 전체 Public Stemcell 목록 조회
     * @title : selectPublicStemcellList
     * @return : List<StemcellManagementVO>
    *****************************************************************/
    List<StemcellManagementVO> selectPublicStemcellList();
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 파일 이름 기준 Stemcell 목록 조회
     * @title : selectPublicStemcell
     * @return : StemcellManagementVO
    *****************************************************************/
    StemcellManagementVO selectPublicStemcellInfoByFileName(@Param("fileName") String stemcellFileName);

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 정보 저장
     * @title : insertPublicStemcell
     * @return : void
    *****************************************************************/
    void insertPublicStemcell(@Param("dto") StemcellManagementDTO.Regist dto);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 정보 수정
     * @title : updatePublicStemcell
     * @return : void
    *****************************************************************/
    void updatePublicStemcell(@Param("dto") StemcellManagementDTO.Regist dto);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 아이디 기준 스템셀 목록 조회
     * @title : selectPublicStemcellById
     * @return : StemcellManagementVO
    *****************************************************************/
    StemcellManagementVO selectPublicStemcellById(@Param("id") Integer id);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 아이디 기준 스템셀 정보 수정
     * @title : updatePublicStemcellById
     * @return : void
    *****************************************************************/
    void updatePublicStemcellById(@Param("dto") StemcellManagementDTO.Regist dto);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 정보 삭제
     * @title : deletePublicStemcell
     * @return : void
    *****************************************************************/
    void deletePublicStemcell(@Param("dto") Delete dto);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인프라 유형 기준 다운로드된 스템셀 정보 조회
     * @title : selectLocalStemcellListByIaas
     * @return : List<StemcellManagementVO>
    *****************************************************************/
    List<StemcellManagementVO> selectLocalStemcellListByIaas(@Param("iaas") String iaas);
}