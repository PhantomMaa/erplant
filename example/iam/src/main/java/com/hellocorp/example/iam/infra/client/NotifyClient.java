package com.hellocorp.example.iam.infra.client;

public interface NotifyClient {
    void sendAccountAlreadyExistNotification(String email);

    void userSignUpVerifyTokenNotification(String email, String verifyToken);

    void userVerifyEmailTokenNotification(String email, String userSignUpVerifyToken);

    void sendNotifyEmail(String email, String subject, String content);
}
