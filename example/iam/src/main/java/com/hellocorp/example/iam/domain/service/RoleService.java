package com.hellocorp.example.iam.domain.service;

import com.hellocorp.example.iam.application.model.UserVO;
import com.hellocorp.example.iam.infra.persist.bk.RoleBK;

import java.util.List;

public interface RoleService {

    void addUser(RoleBK roleBK, String bizId, Long uid);

    void removeUser(RoleBK roleBK, String bizId, Long uid);

    List<String> getRoleNames(String bizType, String bizId, Long uid);

    List<UserVO> getUsers(RoleBK roleBK, String bizId);

    List<String> getBizIds(Long uid, String bizType, String roleName);
}
