package com.hellocorp.example.iam.infra.client.impl;

import com.hellocorp.example.common.model.Result;
import com.hellocorp.example.iam.infra.client.CacheClient;
import com.hellocorp.example.iam.infra.client.NotifyClient;
import com.hellocorp.example.iam.infra.client.model.CacheInputParam;
import org.springframework.stereotype.Component;

@Component
public class NotifyClientImpl implements NotifyClient {

    @Override
    public void sendAccountAlreadyExistNotification(String email) {

    }

    @Override
    public void userSignUpVerifyTokenNotification(String email, String verifyToken) {

    }

    @Override
    public void userVerifyEmailTokenNotification(String email, String userSignUpVerifyToken) {

    }

    @Override
    public void sendNotifyEmail(String email, String subject, String content) {

    }
}
