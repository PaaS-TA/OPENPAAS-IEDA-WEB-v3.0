package org.openpaas.ieda.deploy.web.management.auth.service;



import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.common.web.security.SessionInfoDTO;
import org.openpaas.ieda.deploy.web.management.auth.dao.AuthManagementDAO;
import org.openpaas.ieda.deploy.web.management.auth.dao.AuthManagementVO;
import org.openpaas.ieda.deploy.web.management.auth.dto.AuthManagementDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
@Service
public class AuthManagementService {
    
    @Autowired private AuthManagementDAO dao;
    @Autowired MessageSource message;
    
    final private static String AUTH_CODE_NUMBER = "100000";
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 권한 그룹 리스트 조회
     * @title : getRoleGroupList
     * @return : List<AuthManagementVO>
    ***************************************************/
    public List<AuthManagementVO> getRoleGroupList() {
        return dao.selectRoleGroupList(); 
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 권한 그룹의 하부 권한 코드 조회
     * @title : getRoleDetailList
     * @return : List<HashMap<String,Object>>
    ***************************************************/
    public List<HashMap<String,Object>> getRoleDetailList(int roleId) {
        return dao.selectRoleDetailListByRoleId(roleId, AUTH_CODE_NUMBER);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 저장 데이터(권한 그룹 이름, 설명, 권한 코드)를 VO에 저장 한 뒤 DB 테이블에 저장
     * @title : saveRoleInfo
     * @return : boolean
    ***************************************************/
    public boolean saveRoleInfo(AuthManagementDTO.Regist dto)  {
        
            AuthManagementVO auth = dao.selectRoleInfoByRoleName(dto.getRoleName());
            if ( auth != null) {
                throw new CommonException("conflict.auth.exception", "이미 등록되어 있는 권한 그룹 명입니다.", HttpStatus.CONFLICT);
            }            
            AuthManagementVO authVO = new AuthManagementVO();
            SessionInfoDTO sessionInfo = new SessionInfoDTO();
            if(dto.getRoleId()!=null){
                authVO.setRoleId(Integer.parseInt(dto.getRoleId()));
            }
            authVO.setRoleName(dto.getRoleName());
            authVO.setRoleDescription(dto.getRoleDescription());
            authVO.setCreateUserId(sessionInfo.getUserId());
            authVO.setUpdateUserId(sessionInfo.getUserId());
            try{
                if(dao.insertRoleGroupInfo(authVO)==1) {
                    return true;
                }
            }catch (Exception e) {
                throw new CommonException("sqlExcepion.auth.exception",
                        "권한 그룹 추가 중 에러가 발생했습니다.", HttpStatus.BAD_REQUEST);
            }
        return true;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 권한 그룹 하위 권한 코드 삭제 -> 권한 그룹 삭제
     * @title : deleteRole
     * @return : boolean
    ***************************************************/
    public boolean deleteRole(Integer roleId) {
        if(roleId == null || roleId.toString().isEmpty()){
            throw new CommonException("sqlException.auth.exception",
                    "권한 그룹 삭제 중 에러가 발생 했습니다.", HttpStatus.BAD_REQUEST);
        }
        
        AuthManagementVO authVO = dao.selectRoleInfoByRoleId(roleId);
        if ( authVO == null ){
            throw new CommonException("notfound_rold_id.auth_delete.exception",
                    "해당 권한 코드가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
        try{
            dao.deleteRoleDetailInfoByRoleId(roleId);
            dao.deleteRoleGroupInfoByRoleId(roleId);                    
        }catch(Exception e){
            throw new CommonException("sqlException.auth.exception",
                    "권한 그룹 삭제 중 에러가 발생 했습니다.", HttpStatus.BAD_REQUEST);
        }
        return true;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 권한 그룹 하위 권한 코드 삭제 -> 권한 그룹 수정 -> 권한 그룹 하위 권한 코드 재 등록
     * @title : updateRole
     * @return : boolean
    ***************************************************/
    public boolean updateRole(int roleId, AuthManagementDTO.Regist updateAuthDto) {
        AuthManagementVO authVO = dao.selectRoleInfoByRoleId(roleId);
        if ( authVO == null ) {
            throw new CommonException("notfound_rold_id.auth_update.exception",
                    "해당 권한 코드가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }
        AuthManagementVO auth = new AuthManagementVO();
        SessionInfoDTO sessionInfo = new SessionInfoDTO();
        auth.setRoleId(roleId);
        auth.setRoleName(updateAuthDto.getRoleName());
        auth.setRoleDescription(updateAuthDto.getRoleDescription());
        auth.setCreateUserId(authVO.getCreateUserId());
        auth.setCreateDate(authVO.getCreateDate());
        auth.setUpdateUserId(sessionInfo.getUserId());        
        try{        
            if(dao.updateRoleGroupInfoByRoleId(auth)==1){
                return true;
            }
        }catch(Exception e){
            throw new CommonException("sqlException.auth.exception", " 권한 그룹 수정 실패", HttpStatus.BAD_REQUEST);
        }
        return true;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 상세 권한 추가
     * @title : saveRoleDetail
     * @return : void
    ***************************************************/
    public void saveRoleDetail(int roleId, AuthManagementDTO.Regist dto){
        AuthManagementVO authVO = dao.selectRoleInfoByRoleId(roleId);
        AuthManagementVO auth = new AuthManagementVO();
        SessionInfoDTO sessionInfo = new SessionInfoDTO();
        if(dto.getRoleId()!=null){
            auth.setRoleId(roleId);
        }
        auth.setRoleId(roleId);
        dto.getActiveYn().get(0);
        auth.setActiveYn(dto.getActiveYn());
        auth.setCreateUserId(sessionInfo.getUserId());
        auth.setUpdateUserId(sessionInfo.getUserId());
        if(authVO==null){
            if(auth.getActiveYn().size()!=0){
            dao.insertRoleDetailInfoByRoleId(auth, auth.getActiveYn());
            }
        }else{
            dao.deleteRoleDetailInfoByRoleId(auth.getRoleId());
            if(auth.getActiveYn().size()!=0) {
                dao.insertRoleDetailInfoByRoleId(auth, auth.getActiveYn());
            }
        }
    }
    
}
