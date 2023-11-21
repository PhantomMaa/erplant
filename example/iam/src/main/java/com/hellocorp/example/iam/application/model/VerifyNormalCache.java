package com.hellocorp.example.iam.application.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class VerifyNormalCache {

    private String email;

    private String password;
}
