package com.hellocorp.example.iam.application.model.param;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import jakarta.validation.constraints.Email;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterUserParam {
    
    @Email(message = "邮箱不允许为空")
    private String email;

    @NotBlank
    private String password;

}
