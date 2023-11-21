package com.hellocorp.example.iam.domain.entity;

import com.hellocorp.example.iam.domain.entity.User;
import lombok.Data;

@Data
public class ExtUser extends User {
    private String password;
}
