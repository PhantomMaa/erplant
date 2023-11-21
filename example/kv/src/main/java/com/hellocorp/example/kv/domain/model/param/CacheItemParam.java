package com.hellocorp.example.kv.domain.model.param;

import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
public class CacheItemParam {

    @NonNull
    private String appName;

    @NonNull
    private String groupName;

    @NonNull
    private String itemKey;

    private byte[] itemValue;

    private Long ttl;

    private TimeUnit unit = TimeUnit.SECONDS;
}
