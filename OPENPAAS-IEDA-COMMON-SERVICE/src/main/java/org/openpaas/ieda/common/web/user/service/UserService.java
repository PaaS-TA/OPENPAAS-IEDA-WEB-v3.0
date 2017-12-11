package org.openpaas.ieda.common.web.user.service;

import java.security.Principal;

import org.openpaas.ieda.common.web.user.dao.UserDAO;
import org.openpaas.ieda.common.web.user.dao.UserVO;
import org.openpaas.ieda.common.web.user.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
        
    @Autowired private UserDAO dao;

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 패스워드를 재설정 한다.
     * @title : savePassword
     * @return : int
    ***************************************************/
    public int savePassword(UserDTO.SavePassword savePasswordDto, Principal princiapl) {
        UserVO user = new UserVO();
        
        user.setUserId(princiapl.getName());
        user.setUpdateUserId(princiapl.getName());
        user.setPassword(savePasswordDto.getPassword());

        // 입력된 패스워드 변경 정보를 데이터베이스에 저장한다.
        return dao.savePassword(user);
    }
}
