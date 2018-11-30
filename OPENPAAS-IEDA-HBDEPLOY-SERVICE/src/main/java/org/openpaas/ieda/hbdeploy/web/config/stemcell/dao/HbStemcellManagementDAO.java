package org.openpaas.ieda.hbdeploy.web.config.stemcell.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.openpaas.ieda.hbdeploy.web.config.stemcell.dao.HbStemcellManagementVO;
import org.openpaas.ieda.hbdeploy.web.config.stemcell.dto.HbStemcellManagementDTO;

public interface HbStemcellManagementDAO {
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 전체 Hybrid Stemcell 목록 조회
     * @title : selectHybridStemcellList
     * @return : List<StemcellManagementVO>
    *****************************************************************/
    List<HbStemcellManagementVO> selectHybridStemcellList(@Param("iaas") String iaas);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 파일 이름 기준 Stemcell 목록 조회
     * @title : selectHybridStemcell
     * @return : HybridStemcellManagementVO
    *****************************************************************/
    HbStemcellManagementVO selectHybridStemcellInfoByFileName(@Param("fileName") String stemcellFileName);

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 정보 저장
     * @title : insertHybridStemcell
     * @return : void
    *****************************************************************/
    void insertHybridStemcell(@Param("dto") HbStemcellManagementDTO.Regist dto);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 정보 수정
     * @title : updateHybridStemcell
     * @return : void
    *****************************************************************/
    void updateHybridStemcell(@Param("dto") HbStemcellManagementDTO.Regist dto);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 아이디 기준 스템셀 목록 조회
     * @title : selectHybridStemcellById
     * @return : HybridStemcellManagementVO
    *****************************************************************/
    HbStemcellManagementVO selectHybridStemcellById(@Param("id") Integer id);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 아이디 기준 스템셀 정보 수정
     * @title : updateHybridStemcellById
     * @return : void
    *****************************************************************/
    void updateHybridStemcellById(@Param("dto") HbStemcellManagementDTO.Regist dto);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 스템셀 정보 삭제
     * @title : deleteHybridStemcell
     * @return : void
    *****************************************************************/
    void deleteHybridStemcell(@Param("dto") HbStemcellManagementDTO.Delete dto);
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 인프라 유형 기준 다운로드된 스템셀 정보 조회
     * @title : selectLocalStemcellListByIaas
     * @return : List<HybridStemcellManagementVO>
    *****************************************************************/
    List<HbStemcellManagementVO> selectLocalStemcellListByIaas(@Param("iaas") String iaas);
}
