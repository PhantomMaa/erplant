package com.hellocorp.example.iam.infra.client.impl;

import com.hellocorp.example.common.model.Result;
import com.hellocorp.example.iam.infra.client.CacheClient;
import com.hellocorp.example.iam.infra.client.model.CacheInputParam;
import org.springframework.stereotype.Component;

@Component
public class CacheClientImpl implements CacheClient {
    @Override
    public void put(CacheInputParam itemParam) {

    }

    @Override
    public Result<byte[]> get(String itemKey) {
        return null;
    }

    @Override
    public void delete(String itemKey) {

    }
}
