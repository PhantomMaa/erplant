package com.hellocorp.example.iam.infra.client.impl;

import com.hellocorp.example.iam.infra.client.LoginSessionClient;
import com.hellocorp.example.iam.infra.client.NotifyClient;
import com.hellocorp.example.iam.infra.client.model.JwtUserInfo;
import com.hellocorp.example.iam.infra.client.model.UserToken;
import org.springframework.stereotype.Component;

@Component
public class LoginSessionClientImpl implements LoginSessionClient {

    @Override
    public JwtUserInfo tokenDecode(String token) {
        return null;
    }

    @Override
    public UserToken login(String username, String password) {
        return null;
    }

    @Override
    public void logout(Long uid) {

    }
}
