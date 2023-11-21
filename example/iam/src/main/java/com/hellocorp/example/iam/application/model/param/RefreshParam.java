package com.hellocorp.example.iam.application.model.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshParam {

    @NotBlank
    private String refreshToken;
}
