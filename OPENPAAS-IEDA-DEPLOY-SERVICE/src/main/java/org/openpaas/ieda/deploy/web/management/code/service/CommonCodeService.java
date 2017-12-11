package org.openpaas.ieda.deploy.web.management.code.service;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.web.management.code.dao.CommonCodeDAO;
import org.openpaas.ieda.deploy.web.management.code.dao.CommonCodeVO;
import org.openpaas.ieda.deploy.web.management.code.dto.CommonCodeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class CommonCodeService {
    
    @Autowired private CommonCodeDAO dao;
    @Autowired MessageSource message;
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 코드 그룹 목록 조회
     * @title : getSubGroupCodeList
     * @return : List<CommonCodeVO>
    ***************************************************/
    public List<CommonCodeVO> getSubGroupCodeList(){
        List<CommonCodeVO> list = dao.selectParentCodeIsNull();
        return list;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 코드 그룹 정보 목록 조회 
     * @title : getCodeList
     * @return : List<CommonCodeVO>
    *****************************************************************/
    public List<CommonCodeVO> getCodeList(String parentCode) {
        List<CommonCodeVO> list = dao.selectCodeList(parentCode);
        return list;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : subGroupCode가 존재하는 하위 코드 그룹 정보 목록 조회
     * @title : getCodeListByParentAndSubGroup
     * @return : List<CommonCodeVO>
    ***************************************************/
    public List<CommonCodeVO> getCodeListByParentAndSubGroup(String parentCode, String subGroupCode){
        List<CommonCodeVO> list = dao.selectParentCodeAndSubGroupCode(parentCode, subGroupCode);
        return list;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 하위 코드가 존재하지 않는 코드 목록 조회
     * @title : getSubCodeListBySubGroupCodeNull
     * @return : List<CommonCodeVO>
    ***************************************************/
    public List<CommonCodeVO> getSubCodeListBySubGroupCodeNull(String parentCode){
        List<CommonCodeVO> list = dao.selectSubCodeListBySubGroupCodeNull(parentCode);
        return list;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 코드 정보 상세 조회
     * @title : getCodeGroupList
     * @return : CommonCodeVO
    ***************************************************/
    public CommonCodeVO getCommonCodeList(int codeIdx) {
        CommonCodeVO vo = dao.selectCommonCodeInfo(codeIdx);
        return vo;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 코드 그룹 등록 및 수정
     * @title : saveCodeGroupInfo
     * @return : int
    *****************************************************************/
    public void saveCodeGroupInfo(CommonCodeDTO.Regist dto, Principal principal) {
        CommonCodeVO commonCode = null;
        // 해당 코드가 존재하는지 확인한다
        if( dto.getCodeIdx() == null ) {
            int codeValCheck = dao.selectCodeValueCheck(dto);
            if( codeValCheck > 0 ){  
                throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                        message.getMessage("common.conflict.message", null,Locale.KOREA), HttpStatus.CONFLICT);
            }
            
            commonCode = new CommonCodeVO();
            commonCode.setCodeValue(dto.getCodeValue());
            commonCode.setSortOrder(0);
            commonCode.setCreateUserId(principal.getName());
        }else {
            commonCode = dao.selectCodeIdx(dto.getCodeIdx());
        }
        commonCode.setCodeName(dto.getCodeName());
        commonCode.setCodeDescription(dto.getCodeDescription());
        commonCode.setUpdateUserId(principal.getName());
        
        // 입력된 코드 그룹 정보를 데이터베이스에 저장한다.
        if( dto.getCodeIdx() == null ) {
            dao.insertCode(commonCode);
        }else {
            dao.updateCode(commonCode);
        }
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 코드 등록
     * @title : createSubCode
     * @return : void
    *****************************************************************/
    public void createSubCode(CommonCodeDTO.Regist dto, Principal principal){
        // 해당 코드가 존재하는지 확인한다
        List<CommonCodeVO> codeList = dao.selectCodeName(dto.getCodeName());
        int codeValCheck = dao.selectCodeValueCheck(dto);
        if( codeValCheck > 0 ){
            if ( codeList.get(0).getParentCode() == null || codeList.get(0).getParentCode().isEmpty() ) {
                throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                        message.getMessage("common.badRequest.message", null,Locale.KOREA), HttpStatus.BAD_REQUEST);
            }else if( codeValCheck > 0 ){  
                throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                        message.getMessage("common.conflict.message", null,Locale.KOREA), HttpStatus.CONFLICT);
            }
        }
        
        Integer maxSorderOrder = 0;
        if(!StringUtils.isEmpty(dto.getSubGroupCode()) || dto.getSubGroupCode() != null ){
            maxSorderOrder = dao.selectMaxSortOrder(dto.getParentCode(), dto.getSubGroupCode());
        }
        
        CommonCodeVO commonCode = new CommonCodeVO();
        commonCode.setParentCode(dto.getParentCode().trim());
        commonCode.setSubGroupCode( StringUtils.isEmpty(dto.getSubGroupCode()) ? null : dto.getSubGroupCode() );
        commonCode.setCodeName(dto.getCodeName().trim());
        commonCode.setCodeValue(dto.getCodeValue().trim());
        commonCode.setCodeNameKR(dto.getCodeNameKR().trim());
        commonCode.setSortOrder(maxSorderOrder);
        commonCode.setCodeDescription(dto.getCodeDescription().trim());
        commonCode.setCreateUserId(principal.getName());
        commonCode.setUpdateUserId(principal.getName());
        
        dao.insertCode(commonCode);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 코드 수정
     * @title : updateCode
     * @return : int
    *****************************************************************/
    public int updateCode(CommonCodeDTO.Regist updateCodeDto, Principal principal) {
        
        // 해당 코드가 존재하는지 확인한다
        CommonCodeVO commonCode = dao.selectCodeIdx(updateCodeDto.getCodeIdx());
        if ( commonCode == null ){
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null,Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        
        // 입력된 코드 정보를 데이터베이스에 저장한다.
        commonCode.setCodeName(updateCodeDto.getCodeName().trim());
        commonCode.setCodeValue(updateCodeDto.getCodeValue().trim());
        commonCode.setCodeNameKR(updateCodeDto.getCodeNameKR().trim());
        commonCode.setCodeDescription(updateCodeDto.getCodeDescription().trim());
        commonCode.setParentCode(updateCodeDto.getParentCode().trim());
        commonCode.setSubGroupCode(updateCodeDto.getSubGroupCode().trim());
        commonCode.setUpdateUserId( principal.getName() );
        
        return dao.updateCode(commonCode);
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 코드 그룹 정보 삭제
     * @title : deleteCode
     * @return : void
    *****************************************************************/
    @Transactional
    public void deleteCodeGroupInfo(int codeIdx) {
        // 해당 코드가 존재하는지 확인한다
        CommonCodeVO commonCode = dao.selectCodeIdx(codeIdx);
        if ( commonCode == null ) {
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null,Locale.KOREA), HttpStatus.BAD_REQUEST);
        }else {
            dao.deleteCode(codeIdx);
        }
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 코드 정보 삭제
     * @title : deleteCodeInfo
     * @return : void
    ***************************************************/
    @Transactional
    public void deleteCodeInfo(int codeIdx) {
        // 해당 코드가 존재하는지 확인한다
        CommonCodeVO commonCode = dao.selectCodeIdx(codeIdx);
        if ( commonCode == null ) {
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.notFound.data.message", null,Locale.KOREA), HttpStatus.BAD_REQUEST);
        }else {
            List<CommonCodeVO> list = dao.selectParentCodeAndSubGroupCode(commonCode.getParentCode(), commonCode.getCodeValue());
            if( !list.isEmpty() ) {
                for( int i=0; i<list.size(); i++ ) {
                    dao.deleteCode(list.get(i).getCodeIdx());
                }
            }
            dao.deleteCode(codeIdx);
        }
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 상세 권한 목록 조회
     * @title : getCommonCodeList
     * @return : List<CommonCodeVO>
    *****************************************************************/
    public List<CommonCodeVO> getCommonCodeList() {
        String parentCode = message.getMessage("common.code.auth.admin.parent", null, Locale.KOREA);
        List<CommonCodeVO> list = dao.selectCommonCodeList(parentCode);
        if (list.isEmpty()) {
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null,Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        return list;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description :  국가 코드 조회(KR 기준 정렬)
     * @title : getCountryCodeList
     * @return : List<CommonCodeVO>
    *****************************************************************/
    public List<CommonCodeVO> getCountryCodeList(String parentCode){
        List<CommonCodeVO> list = dao.selectCountryCodeList(parentCode);
        if ( list.isEmpty() ) {
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null,Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        return list;
    }
}
