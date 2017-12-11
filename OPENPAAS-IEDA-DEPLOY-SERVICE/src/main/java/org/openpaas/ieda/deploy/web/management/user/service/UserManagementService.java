package org.openpaas.ieda.deploy.web.management.user.service;

import java.security.Principal;
import java.util.List;

import javax.transaction.Transactional;

import org.openpaas.ieda.common.exception.CommonException;
import org.openpaas.ieda.deploy.web.management.user.dao.UserManagementDAO;
import org.openpaas.ieda.deploy.web.management.user.dao.UserManagementVO;
import org.openpaas.ieda.deploy.web.management.user.dto.UserManagementDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
@Service
public class UserManagementService {

    @Autowired UserManagementDAO dao;
    @Autowired SessionRegistry sessionRegistry;

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 사용자 목록 정보 조회
     * @title : getUserInfoList
     * @return : List<UserManagementVO>
    ***************************************************/
    public List<UserManagementVO> getUserInfoList() {
        List<UserManagementVO> userList = dao.selectUserInfoList();
        for(int i=0;i<userList.size();i++){
            userList.get(i).setRecid(i);
        }
        return userList;
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 사용자 정보 저장
     * @title : savaUserInfo
     * @return : void
    ***************************************************/
    public void savaUserInfo(UserManagementDTO.Regist dto, Principal principal) {
        UserManagementVO findUserId = dao.selectUserIdInfoById(dto.getUserId());
        if(findUserId != null ){
            throw new CommonException("conflict.user.exception",
                    " 사용자 아이디 중복 입니다.", HttpStatus.CONFLICT);
        }
        UserManagementVO userVO = new UserManagementVO();
        userVO.setUserId(dto.getUserId());
        userVO.setUserName(dto.getUserName());
        userVO.setUserPassword(dto.getUserPassword());
        userVO.setEmail(dto.getEmail());
        userVO.setCreateUserId(principal.getName());
        userVO.setUpdateUserId(principal.getName());
        userVO.setRoleId(dto.getRoleId());
        userVO.setInitPassYn("N");
        dao.insertUserInfo(userVO);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 사용자 정보 수정
     * @title : updateUserInfo
     * @return : void
    ***************************************************/
    public void updateUserInfo(UserManagementDTO.Regist dto, String userId) {
        UserManagementVO findUserId = dao.selectUserIdInfoById(userId);
        if(findUserId == null ){
            throw new CommonException("badrequest.user.exception",
                    "해당 사용자 정보를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
        UserManagementVO userVO = new UserManagementVO();
        userVO.setUserId(userId);
        userVO.setUserName(dto.getUserName());
        userVO.setUserPassword(dto.getUserPassword());
        userVO.setUpdateUserId(findUserId.getCreateUserId());
        userVO.setUpdateDate(findUserId.getCreateDate());
        userVO.setRoleId(dto.getRoleId());
        userVO.setInitPassYn(dto.getInitPassYn());
        userVO.setEmail(dto.getEmail());
        dao.updateUserInfoByUid(userVO);
        
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 사용자 정보 삭제
     * @title : deleteUserInfo
     * @return : void
    ***************************************************/
    @Transactional
    public void deleteUserInfo(String userId) {
        UserManagementVO findUserInfo = dao.selectUserIdInfoById(userId);
        if(findUserInfo==null){
            throw new CommonException("badrequest.user.exception", "해당 사용자 정보를 찾을 수 없습니다 ", HttpStatus.BAD_REQUEST);
        }
        dao.deleteUserInfoByUid(userId);
        expireUserSessions(userId);
    }
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 삭제된 유저의 세션 강제 종료
     * @title : expireUserSessions
     * @return : void
    ***************************************************/
    public void expireUserSessions(String username) {
        List<SessionInformation> usersessions = sessionRegistry.getAllSessions(username, false);
        if(! usersessions.isEmpty()){
            for (int i = 0; i < usersessions.size(); i++){
                usersessions.get(i).expireNow();
            }
        } 
    }
}
