package com.hellocorp.example.iam.infra.client;

import com.hellocorp.example.iam.infra.client.model.JwtUserInfo;
import com.hellocorp.example.iam.infra.client.model.UserToken;

public interface LoginSessionClient {
    JwtUserInfo tokenDecode(String token);

    UserToken login(String username, String password);

    void logout(Long uid);
}
