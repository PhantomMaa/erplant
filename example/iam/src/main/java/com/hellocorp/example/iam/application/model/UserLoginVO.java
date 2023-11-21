package com.hellocorp.example.iam.application.model;

import com.hellocorp.example.iam.infra.client.model.UserToken;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserLoginVO {

    private UserToken token;

    private UserVO user;
}
