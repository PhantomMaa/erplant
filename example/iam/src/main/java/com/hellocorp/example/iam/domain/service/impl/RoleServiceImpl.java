package com.hellocorp.example.iam.domain.service.impl;

import com.hellocorp.example.iam.application.model.UserVO;
import com.hellocorp.example.common.model.BaseErrorCode;
import com.hellocorp.example.common.model.ServiceException;
import com.hellocorp.example.iam.domain.converter.ExtUserConverter;
import com.hellocorp.example.iam.domain.entity.User;
import com.hellocorp.example.iam.domain.entity.UserRoleRel;
import com.hellocorp.example.iam.domain.service.RoleService;
import com.hellocorp.example.iam.infra.persist.bk.RoleBK;
import com.hellocorp.example.iam.domain.entity.Role;
import com.hellocorp.example.iam.domain.repository.RoleRepository;
import com.hellocorp.example.iam.domain.repository.UserRepository;
import com.hellocorp.example.iam.domain.repository.UserRoleRelRepository;
import com.hellocorp.example.iam.infra.persist.bk.UserRoleRelBK;
import com.hellocorp.example.iam.infra.persist.query.UserRoleRelPageNumQuery;
import jakarta.annotation.Resource;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    @Resource
    RoleRepository roleRepository;

    @Resource
    UserRoleRelRepository userRoleRelRepository;

    @Resource
    UserRepository userRepository;

    @Resource
    ExtUserConverter extUserConverter;

    private Role getOrCreateRole(RoleBK roleBK) {
        Role pre = roleRepository.getByBk(roleBK);
        if (pre != null) {
            return pre;
        }

        Role role = new Role();
        role.setBizType(roleBK.getBizType());
        role.setName(roleBK.getName());
        boolean optSuccess = roleRepository.save(role);
        if (!optSuccess) {
            log.warn("getOrCreateRole roleRepository.save fail");
            throw new ServiceException(BaseErrorCode.RETRY_LATER);
        }

        return role;
    }

    @Override
    public void addUser(RoleBK roleBK, String bizId, Long uid) {
        Role role = getOrCreateRole(roleBK);
        long roleId = role.getId();

        String bizType = roleBK.getBizType();
        UserRoleRelBK relBK = new UserRoleRelBK(bizType, bizId, roleId, uid);
        UserRoleRel pre = userRoleRelRepository.getByBk(relBK);
        if (pre != null) {
            log.info("addUser userRoleRelRepository.getByBk userRoleRel already exist");
            return;
        }

        UserRoleRel userRoleRel = new UserRoleRel();
        userRoleRel.setBizType(bizType);
        userRoleRel.setBizId(bizId);
        userRoleRel.setRoleId(roleId);
        userRoleRel.setUid(uid);
        boolean optSuccess = userRoleRelRepository.save(userRoleRel);
        if (!optSuccess) {
            log.warn("addUser userRoleRelRepository.save fail");
            throw new ServiceException(BaseErrorCode.RETRY_LATER);
        }
    }

    @Override
    public void removeUser(RoleBK roleBK, String bizId, Long uid) {
        Role pre = roleRepository.getByBk(roleBK);
        if (pre == null) {
            throw new ServiceException(BaseErrorCode.RESOURCE_NOT_FOUND);
        }
        userRoleRelRepository.removeByBk(new UserRoleRelBK(roleBK.getBizType(), bizId, pre.getId(), uid));
    }

    @Override
    public List<String> getRoleNames(String bizType, String bizId, Long uid) {
        UserRoleRelPageNumQuery userRoleRelQuery = UserRoleRelPageNumQuery.builder().uid(uid).bizId(bizId).bizType(bizType).build();
        List<UserRoleRel> userRoleRelList = userRoleRelRepository.pageNumQuery(userRoleRelQuery);
        List<Long> roleIds = userRoleRelList.stream().map(UserRoleRel::getRoleId).toList();
        return roleRepository.batchGet(roleIds).stream().map(Role::getName).toList();
    }

    @Override
    public List<UserVO> getUsers(RoleBK roleBK, String bizId) {
        Role role = getOrCreateRole(roleBK);
        long roleId = role.getId();
        String bizType = roleBK.getBizType();

        UserRoleRelPageNumQuery query = UserRoleRelPageNumQuery.builder().bizType(bizType).bizId(bizId).roleId(roleId).build();
        List<UserRoleRel> relList = userRoleRelRepository.pageNumQuery(query);
        List<Long> uids = relList.stream().map(UserRoleRel::getUid).toList();
        List<User> userList = userRepository.batchGet(uids);
        return userList.stream().map(it -> extUserConverter.toUserDTO(it)).toList();
    }

    @Override
    public List<String> getBizIds(Long uid, String bizType, String roleName) {
        Long roleId = null;
        if (roleName != null) {
            Role role = getOrCreateRole(new RoleBK(bizType, roleName));
            roleId = role.getId();
        }

        UserRoleRelPageNumQuery query = UserRoleRelPageNumQuery.builder().uid(uid).bizType(bizType).roleId(roleId).build();
        List<UserRoleRel> relList = userRoleRelRepository.pageNumQuery(query);
        return relList.stream().map(UserRoleRel::getBizId).toList();
    }

    public void cleanUsers(RoleBK roleBK, String bizId) {
        Role role = roleRepository.getByBk(roleBK);
        long roleId = role.getId();
        String bizType = roleBK.getBizType();
        UserRoleRelPageNumQuery query = UserRoleRelPageNumQuery.builder().bizType(bizType).bizId(bizId).roleId(roleId).build();
        List<UserRoleRel> relList = userRoleRelRepository.pageNumQuery(query);
        List<Long> uids = relList.stream().map(UserRoleRel::getUid).toList();
        uids.forEach(it -> userRoleRelRepository.removeByBk(new UserRoleRelBK(bizType, bizId, roleId, it)));
    }
}
