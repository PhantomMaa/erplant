package com.hellocorp.example.common.model;

/**
 * service runtime exception
 */
public class ServiceException extends RuntimeException {

    public static final String ERROR_MESSAGE_FORMAT = "errCode: %d, errMsg: %s ";

    private final int errCode;

    public ServiceException(Result<?> result) {
        super(result.getMessage());
        this.errCode = result.getErrCode();
    }

    public ServiceException(IErrorCode iErrorCode) {
        super(iErrorCode.getPhraseReason());
        this.errCode = iErrorCode.getCode();
    }

    public ServiceException(int errCode, String errMsg) {
        super(String.format(ERROR_MESSAGE_FORMAT, errCode, errMsg));
        this.errCode = errCode;
    }

    public ServiceException(int errCode, Throwable throwable) {
        super(throwable);
        this.errCode = errCode;
    }

    public ServiceException(int errCode, String errMsg, Throwable throwable) {
        super(String.format(ERROR_MESSAGE_FORMAT, errCode, errMsg), throwable);
        this.errCode = errCode;
    }

    public int getErrCode() {
        return errCode;
    }

}
