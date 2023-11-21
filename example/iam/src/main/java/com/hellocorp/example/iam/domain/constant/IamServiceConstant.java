package com.hellocorp.example.iam.domain.constant;

public class IamServiceConstant {

    public static final String IAM = "iam";

    public static final String IAM_USER = "iam_user";

    /**
     * sign up business related constant
     */
    public static final String USER_VERIFY_TOKEN_KEY_NOTATION_WITH_PASSWORD = "user:verify:pw:";

    public static final String SIGN_UP_VERIFY_TOKEN_INFO_SUBJECT = "Verify your AutoMQ Cloud account";

    public static final String SIGN_UP_VERIFY_TOKEN_MAIL_TEMPLATE_URI = "mail-templates/register/register-token-verify-mail.html.vm";

    public static final String SIGN_UP_ACCOUNT_EXIST_ALERT_SUBJECT = "Security Alert: Someone tried to create an account with your email address";

    public static final String SIGN_UP_ACCOUNT_EXIST_ALERT_TEMPLATE_URI = "mail-templates/register/register-account-already-exist.html.vm";

    public static final String WECHAT_USER_INFO_KEY_NOTATION = "wechat:userInfo:";

    /**
     * reset password business related constant
     */
    public static final String USER_RESET_PASSWORD_KEY_NOTATION = "user:resetPW:";

    public static final String RESET_PASSWORD_SUBJECT = "Reset password for AutoMQ Cloud";

    public static final String RESET_PASSWORD_ACCOUNT_NOT_EXIST_SUBJECT = "Security Alert: Password reset on your account with your email address";

    public static final String RESET_PASSWORD_ACCOUNT_NOT_NORMAL_SUBJECT = "Security Alert: Password reset on your account";

    public static final String RESET_PASSWORD_MAIL_TEMPLATE_URI = "mail-templates/reset-password/reset-password.html.vm";

    public static final String RESET_PASSWORD_ACCOUNT_NOT_EXIST_MAIL_TEMPLATE_URI = "mail-templates/reset-password/reset-password-alert-account-not-exist.html.vm";

    public static final String RESET_PASSWORD_ACCOUNT_NOT_NORMAL_MAIL_TEMPLATE_URI = "mail-templates/reset-password/reset-password-alert-account-not-normal.html.vm";

}
