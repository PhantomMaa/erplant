package com.hellocorp.example.iam.infra.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CacheInputParam {

    @NonNull
    private String itemKey;

    @NonNull
    private byte[] itemValue;

    private Long ttl;

}
