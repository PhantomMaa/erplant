package com.hellocorp.example.iam.application.controller;

import com.hellocorp.example.iam.application.model.UserVO;
import com.hellocorp.example.iam.domain.service.UserService;
import com.hellocorp.example.common.model.Result;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/inner")
@Slf4j
@Validated
public class UserController {

    @Resource
    UserService userService;

    @GetMapping("/get")
    public Result<UserVO> get(@RequestParam Long uid) {
        UserVO user = userService.getUser(uid);
        return Result.success(user);
    }

    @GetMapping("/getByEmail")
    public Result<UserVO> getByEmail(@RequestParam @Email String email) {
        UserVO user = userService.getByEmail(email);
        return Result.success(user);
    }
}
