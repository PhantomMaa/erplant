package com.hellocorp.example.iam.domain.constant;


import com.hellocorp.example.common.model.ErrorFamily;
import com.hellocorp.example.common.model.IErrorCode;

/**
 * iam module
 * inner error code scope: 0 ～ 99
 * custom business error code, standalone with HTTP RESPONSE CODE
 * used for international document in the future
 */
public enum ErrorCode implements IErrorCode {

    NORMAL_ACCOUNT_NOT_VERIFIED(40000, "normal account not verified"),

    FAIL_TO_NOTIFY(40001, "fail to notify, resend or check the email"),

    LOGIN_WAY_WRONG(40002, "please use origin way to login"),

    WECHAT_NEED_EMAIL(40003, "need provide wechat info"),

    EMAIL_HAS_BEEN_USED(40004, "email has been used"),

    INVALID_LOGIN_INPUT(40005, "Incorrect email or password"),

    INCORRECT_LOGIN_TYPE(40007, "Incorrect login type, please origin Oauth login");

    private final int code;

    /**
     * phrase reason
     */
    private final String phraseReason;

    private final ErrorFamily family;

    ErrorCode(final int statusCode, final String reasonPhrase) {
        this.code = statusCode;
        this.phraseReason = reasonPhrase;
        this.family = ErrorFamily.familyOf(statusCode);
    }

    public int getCode() {
        return code;
    }

    public String getPhraseReason() {
        return phraseReason;
    }

    public ErrorFamily getFamily() {
        return family;
    }
}