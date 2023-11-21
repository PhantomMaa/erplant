package com.hellocorp.example.iam.infra.client;

import com.hellocorp.example.iam.infra.client.model.CacheInputParam;
import com.hellocorp.example.common.model.Result;

public interface CacheClient {
    void put(CacheInputParam itemParam);

    Result<byte[]> get(String itemKey);

    void delete(String itemKey);
}
