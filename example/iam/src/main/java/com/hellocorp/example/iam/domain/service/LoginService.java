package com.hellocorp.example.iam.domain.service;

import com.hellocorp.example.iam.application.model.DecodeRoleVO;
import com.hellocorp.example.iam.application.model.UserLoginVO;
import com.hellocorp.example.iam.application.model.param.RegisterUserParam;
import com.hellocorp.example.iam.application.model.param.UserLoginParam;

public interface LoginService {

    /**
     * register new user
     *
     * @param param
     * @return Reply verify token for ut
     */
    String register(RegisterUserParam param);

    /**
     * verify and decode Token linked iam user
     */
    String decodeToken(String token);

    DecodeRoleVO decodeTokenRole(String token, String tenantCode);

    UserLoginVO verifyNewNormalUser(String verifyToken);

    UserLoginVO login(UserLoginParam userLoginParam);

    void logout(Long uid);

    UserLoginVO resetPassword(String token, String password);

    void sendResetPasswordLink(String email);

}
