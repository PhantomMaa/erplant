package com.hellocorp.example.iam.domain.service.impl;

import com.hellocorp.example.iam.application.model.UserVO;
import com.hellocorp.example.iam.domain.converter.ExtUserConverter;
import com.hellocorp.example.iam.domain.entity.ExtUser;
import com.hellocorp.example.iam.domain.service.UserService;
import com.hellocorp.example.common.model.BaseErrorCode;
import com.hellocorp.example.common.model.ServiceException;
import com.hellocorp.example.iam.domain.entity.User;
import com.hellocorp.example.iam.domain.repository.UserRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    UserRepository userRepository;

    @Resource
    ExtUserConverter extUserConverter;

    @Override
    public UserVO getUser(Long uid) {
        User user = userRepository.get(uid);
        if (user == null) {
            return null;
        }

        return extUserConverter.toUserDTO(user);
    }

    @Override
    public UserVO getByEmail(String email) {
        User user = userRepository.getByEmail(email);
        if (user == null) {
            throw new ServiceException(BaseErrorCode.RESOURCE_NOT_FOUND);
        }
        return extUserConverter.toUserDTO(user);
    }

    @Override
    public UserVO getUser(String email) {
        User user = userRepository.getByEmail(email);
        if (user == null) {
            return null;
        }

        return extUserConverter.toUserVO(user);
    }

    /**
     * just for test, skip keycloak
     *
     * @param email
     */
    public Long createTestUser(String email) {
        ExtUser user = new ExtUser();
        user.setEmail(email);
        user.setNick(email);
        userRepository.save(user);
        return user.getId();
    }

    /**
     * just for test, skip keycloak
     *
     * @param uid
     */
    public void deleteTestUser(Long uid) {
        userRepository.remove(uid);
    }

    public void deleteUser(String email) {
        User user = userRepository.getByEmail(email);
        if (user == null) {
            return;
        }

        userRepository.remove(user.getId());
    }

}
