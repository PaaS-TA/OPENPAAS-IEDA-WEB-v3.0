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
    private final static String SUB_CODE_TYPE_3 = "3";
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 서브그룹 정보 목록을 조회 
     * @title : getSubGroupCodeList
     * @return : List<CommonCodeVO>
    *****************************************************************/
    public List<CommonCodeVO> getSubGroupCodeList(String parentCode, String subGroupCode, String type) {
        List<CommonCodeVO> list = dao.selectParentCodeAndSubGroupCode(parentCode, subGroupCode, type);
        
        if (SUB_CODE_TYPE_3.equals(type) && list.isEmpty() ) {
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null,Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        
        return list;
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 코드 그룹 등록
     * @title : createCode
     * @return : int
    *****************************************************************/
    public int createCode(CommonCodeDTO.Regist createCodeDto, Principal principal) {
        // 해당 코드가 존재하는지 확인한다
        int codeValCheck = dao.selectCodeValueCheck(createCodeDto);
        if( codeValCheck > 0 ){  
            throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                    message.getMessage("common.conflict.message", null,Locale.KOREA), HttpStatus.CONFLICT);
        }

        CommonCodeVO commonCode = new CommonCodeVO();
        if(createCodeDto.getCodeIdx() != null){
            commonCode.setCodeIdx(createCodeDto.getCodeIdx());
        }
        commonCode.setCodeName(createCodeDto.getCodeName());
        commonCode.setCodeValue(createCodeDto.getCodeValue());
        commonCode.setCodeDescription(createCodeDto.getCodeDescription());
        commonCode.setSortOrder(0);
        commonCode.setCreateUserId(principal.getName());
        commonCode.setUpdateUserId(principal.getName());

        // 입력된 코드 그룹 정보를 데이터베이스에 저장한다.
        return dao.insertCode(commonCode);
    }
    
    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 코드 등록
     * @title : createSubCode
     * @return : void
    *****************************************************************/
    public void createSubCode(CommonCodeDTO.Regist createCodeDto, Principal principal){
        // 해당 코드가 존재하는지 확인한다
        List<CommonCodeVO> codeList = dao.selectCodeName(createCodeDto.getCodeName());
        int codeValCheck = dao.selectCodeValueCheck(createCodeDto);
        if( !codeList.isEmpty() ){
            if ( codeList.get(0).getParentCode() == null || codeList.get(0).getParentCode().isEmpty() ) {
                throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                        message.getMessage("common.badRequest.message", null,Locale.KOREA), HttpStatus.BAD_REQUEST);
            }else if( codeValCheck > 0 ){  
                throw new CommonException(message.getMessage("common.conflict.exception.code", null, Locale.KOREA),
                        message.getMessage("common.conflict.message", null,Locale.KOREA), HttpStatus.CONFLICT);
            }
        }
        
        Integer maxSorderOrder = 0;
        if(!StringUtils.isEmpty(createCodeDto.getSubGroupCode()) || createCodeDto.getSubGroupCode() != null ){
            maxSorderOrder = dao.selectMaxSortOrder(createCodeDto.getParentCode(), createCodeDto.getSubGroupCode());
        }
        
        CommonCodeVO commonCode = new CommonCodeVO();
        commonCode.setParentCode(createCodeDto.getParentCode());
        commonCode.setSubGroupCode(createCodeDto.getSubGroupCode());
        commonCode.setCodeName(createCodeDto.getCodeName());
        commonCode.setCodeValue(createCodeDto.getCodeValue());
        commonCode.setCodeNameKR(createCodeDto.getCodeNameKR());
        commonCode.setSortOrder(maxSorderOrder);
        commonCode.setCodeDescription(createCodeDto.getCodeDescription());
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
        commonCode.setCodeName(updateCodeDto.getCodeName());
        commonCode.setCodeValue(updateCodeDto.getCodeValue());
        commonCode.setCodeNameKR(updateCodeDto.getCodeNameKR());
        commonCode.setCodeDescription(updateCodeDto.getCodeDescription());
        commonCode.setParentCode(updateCodeDto.getParentCode());
        commonCode.setSubGroupCode(updateCodeDto.getSubGroupCode());
        commonCode.setUpdateUserId( principal.getName() );
        
        return dao.updateCode(commonCode);
    }

    /****************************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 코드 삭제
     * @title : deleteCode
     * @return : void
    *****************************************************************/
    @Transactional
    public void deleteCode(int codeIdx) {
        // 해당 코드가 존재하는지 확인한다
        CommonCodeVO commonCode = dao.selectCodeIdx(codeIdx);
        if ( commonCode == null ) {
            throw new CommonException(message.getMessage("common.badRequest.exception.code", null, Locale.KOREA),
                    message.getMessage("common.badRequest.message", null,Locale.KOREA), HttpStatus.BAD_REQUEST);
        }
        dao.deleteCode(codeIdx);
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
