package com.hellocorp.example.kv.domain.converter;

import com.hellocorp.example.kv.domain.model.param.CacheItemParam;
import com.hellocorp.example.kv.domain.entity.CacheItem;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring"
)
public interface ExtCacheItemConverter extends CacheItemConverter {

    CacheItem toCacheItem(CacheItemParam param);
}
