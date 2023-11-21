package com.hellocorp.example.common.model;

public enum ErrorFamily {

    /**
     * {@code 4xxxx} client side error
     */
    CLIENT_ERROR,

    /**
     * {@code 5xxxx} server side error
     */
    SERVER_ERROR,

    @Deprecated
    UNKNOWN;

    public static ErrorFamily familyOf(final int statusCode) {
        return switch (statusCode / 10000) {
            case 4 -> ErrorFamily.CLIENT_ERROR;
            case 5 -> ErrorFamily.SERVER_ERROR;
            default -> ErrorFamily.UNKNOWN;
        };
    }
}