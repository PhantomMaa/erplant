package com.hellocorp.example.iam.application.controller;

import com.hellocorp.example.iam.application.model.UserVO;
import com.hellocorp.example.iam.domain.service.RoleService;
import com.hellocorp.example.common.model.Result;
import com.hellocorp.example.iam.infra.persist.bk.RoleBK;
import jakarta.annotation.Resource;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/role/inner")
@Slf4j
@Validated
public class RoleController {

    @Resource
    RoleService roleService;

    @PostMapping("/addUser")
    public Result<Void> addUser(@RequestParam String bizType, @RequestParam String roleName, @RequestParam String bizId, @RequestParam Long uid) {
        roleService.addUser(new RoleBK(bizType, roleName), bizId, uid);
        return Result.success();
    }

    // remove user tag
    @PutMapping("/removeUser")
    public Result<Void> removeUser(@RequestParam String bizType, @RequestParam String roleName, @RequestParam String bizId, @RequestParam Long uid) {
        roleService.removeUser(new RoleBK(bizType, roleName), bizId, uid);
        return Result.success();
    }

    @GetMapping("/getRoleNames")
    public Result<List<String>> getRoleNames(@RequestParam String bizType, @RequestParam String bizId, @RequestParam Long uid) {
        return Result.success(roleService.getRoleNames(bizType, bizId, uid));
    }

    @GetMapping("/users")
    public Result<List<UserVO>> users(@RequestParam String bizType, @RequestParam String roleName, @RequestParam String bizId) {
        List<UserVO> users = roleService.getUsers(new RoleBK(bizType, roleName), bizId);
        return Result.success(users);
    }

    @GetMapping("/bizIds")
    public Result<List<String>> bizIds(@RequestParam String bizType, @RequestParam Long uid, @RequestParam(required = false) String roleName) {
        List<String> bizIds = roleService.getBizIds(uid, bizType, roleName);
        return Result.success(bizIds);
    }

}
