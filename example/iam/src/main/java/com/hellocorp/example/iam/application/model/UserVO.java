package com.hellocorp.example.iam.application.model;

import lombok.Data;

@Data
public class UserVO {

    private long id;

    private String email;

    private String outerId;

    private int userType;

    private String nick;

    private String avatar;

    private Boolean emailVerified;

}
