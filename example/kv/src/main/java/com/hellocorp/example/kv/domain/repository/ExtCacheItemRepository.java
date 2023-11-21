package com.hellocorp.example.kv.domain.repository;

import com.hellocorp.example.kv.infra.persist.dao.ExtCacheItemDAO;
import com.hellocorp.example.kv.domain.entity.CacheItem;
import com.hellocorp.example.kv.infra.persist.bk.CacheItemBK;
import com.hellocorp.example.kv.infra.persist.dataobject.CacheItemDO;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExtCacheItemRepository extends CacheItemRepository {

    @Resource
    ExtCacheItemDAO extCacheItemDAO;

    public CacheItem getUnexpired(CacheItemBK bk) {
        CacheItemDO cacheItemDO = extCacheItemDAO.getUnexpired(bk, System.currentTimeMillis());
        return cacheItemConverter.toCacheItem(cacheItemDO);
    }

    public List<CacheItem> queryUnexpired(Long groupId) {
        List<CacheItemDO> cacheItemDOS = extCacheItemDAO.queryUnexpired(groupId, System.currentTimeMillis());
        return cacheItemDOS.stream().map(cacheItemConverter::toCacheItem).collect(Collectors.toList());
    }

    public int deleteExpired(long timestamp) {
        return extCacheItemDAO.deleteExpired(timestamp);
    }
}
