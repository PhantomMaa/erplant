package com.hellocorp.example.common.model;

/**
 * exception constant code
 */
public enum BaseErrorCode implements IErrorCode {
    INVALID_PARAM(400, "Invalid param"),
    DUPLICATE_KEY(401, "Duplicate key exception, Please try another one"),

    UNAUTHENTICATED(401, "auth token invalid"),
    RESOURCE_NOT_FOUND(404, "Resource not found"),
    SERVER_ERROR(500, "Server error"),
    RETRY_LATER(501, "System busy, Please retry later"),
    JSON_SERIALIZE_ROOR(502, "Json serialize/deserialize error"),
    GRPC_SERIALIZE_ERROR(503, "gRpc serialize/deserialize error"),
    DEPENDENCY_HTTP_SERVICE_UNAVAILABLE(504, "Dependency http service unavailable, Please retry later"),
    DEPENDENCY_HTTP_SERVICE_RESPONSE_ERROR(505, "Dependency http service response error"),
    ACCESS_EXCEED_PERMIT(506, "You don't have permission to access the resource"),
    SYSTEM_INIT(507, "System init exception"),

    EXPIRED_CODE(508, "code expired code"),

    EXCHANGE_GATEWAY_ERROR(509, "exgw error"),

    STATE_EXCEPTION(555, "IllegalStateException");

    private final int code;

    /**
     * phrase reason
     */
    private final String phraseReason;

    private final ErrorFamily family;

    BaseErrorCode(final int statusCode, final String reasonPhrase) {
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
