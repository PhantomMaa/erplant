package com.hellocorp.example.iam.application.controller;

import com.hellocorp.example.iam.application.model.DecodeRoleVO;
import com.hellocorp.example.iam.application.model.UserLoginVO;
import com.hellocorp.example.iam.application.model.param.RegisterUserParam;
import com.hellocorp.example.iam.application.model.param.UserLoginParam;
import com.hellocorp.example.iam.domain.service.LoginService;
import com.hellocorp.example.common.model.Result;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
@Validated
public class LoginController {

    @Resource
    LoginService loginService;

    @PostMapping("/decode/token")
    public Result<String> decodeToken(@RequestParam @NotBlank String token) {
        return Result.success(loginService.decodeToken(token));
    }

    @PostMapping("/decode/role")
    public Result<DecodeRoleVO> decodeTokenRole(@RequestParam @NotBlank String token,
                                                @RequestParam String tenantCode) {
        return Result.success(loginService.decodeTokenRole(token, tenantCode));
    }

    @PostMapping("/normal/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginParam loginParam) {
        return Result.success(loginService.login(loginParam));
    }

    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader Long uid) {
        loginService.logout(uid);
        return Result.success();
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterUserParam param) {
        loginService.register(param);
        return Result.success();
    }

    @PostMapping("/verifyNewNormalUser")
    public Result<UserLoginVO> verifyNewNormalUser(@RequestParam @NotBlank String signUpVerifyToken) {
        return Result.success(loginService.verifyNewNormalUser(signUpVerifyToken));
    }

    // reset password
    @PostMapping("/sendResetPasswordLink")
    public Result<Void> sendResetPasswordLink(@RequestParam @Email(message = "邮箱不允许为空") String email) {
        loginService.sendResetPasswordLink(email);
        return Result.success();
    }

    @PutMapping("/resetPassword")
    public Result<UserLoginVO> resetPassword(@RequestParam @NotBlank String token,
        @RequestParam @NotBlank String password) {
        return Result.success(loginService.resetPassword(token, password));
    }

}
