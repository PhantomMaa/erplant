package com.hellocorp.example.common.model;



public interface IErrorCode {

    int getCode();

    String getPhraseReason();

    ErrorFamily getFamily();
}
