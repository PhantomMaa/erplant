package com.hellocorp.example.iam.infra.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * jwt的payload部分存储的用户信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtUserInfo {

    private String username;

}
