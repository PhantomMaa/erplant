package com.hellocorp.example.iam.infra.client.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserToken {

    private String accessToken;

    private Integer expiresIn;

    private String refreshToken;

    private Integer refreshExpiresIn;

    private String tokenType;

}
