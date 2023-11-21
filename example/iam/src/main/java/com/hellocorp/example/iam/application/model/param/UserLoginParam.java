package com.hellocorp.example.iam.application.model.param;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginParam {

    @Email(message = "邮箱不允许为空")
    private String email;

    @NotBlank
    private String password;
}
