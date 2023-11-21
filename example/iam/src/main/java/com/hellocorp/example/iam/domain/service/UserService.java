package com.hellocorp.example.iam.domain.service;

import com.hellocorp.example.iam.application.model.UserVO;

public interface UserService {

    /**
     * get user info
     *
     * @param uid
     * @return Reply
     */
    UserVO getUser(Long uid);

    UserVO getByEmail(String email);

    /**
     * get user info
     *
     * @param email
     * @return Reply
     */
    UserVO getUser(String email);

}
