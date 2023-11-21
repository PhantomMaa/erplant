package com.hellocorp.example.kv.domain.service.impl;

import com.hellocorp.example.kv.domain.converter.ExtCacheItemConverter;
import com.hellocorp.example.kv.domain.model.BizGroupVO;
import com.hellocorp.example.kv.domain.model.ItemKeyVO;
import com.hellocorp.example.kv.domain.model.param.CacheItemParam;
import com.hellocorp.example.kv.domain.repository.ExtCacheItemRepository;
import com.hellocorp.example.kv.domain.service.CacheService;
import com.hellocorp.example.kv.domain.entity.CacheItem;
import com.hellocorp.example.kv.domain.entity.Group;
import com.hellocorp.example.kv.domain.repository.GroupRepository;
import com.hellocorp.example.kv.infra.persist.bk.CacheItemBK;
import com.hellocorp.example.kv.infra.persist.bk.GroupBK;
import jakarta.annotation.Resource;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@Service
public class CacheServiceImpl implements CacheService {

    @Resource
    GroupRepository groupRepository;

    @Resource
    ExtCacheItemConverter extCacheItemConverter;

    @Resource
    ExtCacheItemRepository extCacheItemRepository;

    @Resource
    ConfigServiceImpl configServiceImpl;

    /**
     * 01:00 very day
     */
    @Scheduled(cron = "0 0 1 * * ?")
    private void cleanExpiredCaches() {
        // every day, 01:00:00 clean expired datas before current time
        int count = extCacheItemRepository.deleteExpired(System.currentTimeMillis());
        log.info("cleanExpiredCaches deleteExpired count : {}", count);
    }

    @Override
    public boolean put(CacheItemParam itemParam) {
        BizGroupVO bizGroupVO = new BizGroupVO(itemParam.getAppName(), itemParam.getGroupName());
        Group group = configServiceImpl.getOrCreateGroup(bizGroupVO);

        saveCacheItem(itemParam, group.getId());
        return true;
    }

    @Override
    public byte[] get(ItemKeyVO itemKeyVO) {
        CacheItem cacheItem = getCacheItem(itemKeyVO);
        if (cacheItem == null) {
            return null;
        }

        return cacheItem.getItemValue();
    }

    private CacheItem getCacheItem(ItemKeyVO itemKeyVO) {
        BizGroupVO bizGroupVO = itemKeyVO.getBizGroup();
        Group group = groupRepository.getByBk(new GroupBK(bizGroupVO.getModuleName(), bizGroupVO.getGroupName()));
        if (group == null) {
            return null;
        }

        CacheItemBK cacheItemBk = new CacheItemBK(group.getId(), itemKeyVO.getItemKey());
        return extCacheItemRepository.getUnexpired(cacheItemBk);
    }

    @Override
    public boolean delete(ItemKeyVO itemKeyVO) {
        BizGroupVO bizGroupVO = itemKeyVO.getBizGroup();
        Group group = groupRepository.getByBk(new GroupBK(bizGroupVO.getModuleName(), bizGroupVO.getGroupName()));
        if (group == null) {
            return false;
        }

        CacheItemBK cacheItemBK = new CacheItemBK(group.getId(), itemKeyVO.getItemKey());
        deleteCacheItem(cacheItemBK);

        return true;
    }

    private void deleteCacheItem(CacheItemBK cacheItemBK) {
        boolean optSuccess;
        try {
            optSuccess = extCacheItemRepository.removeByBk(cacheItemBK);
        } catch (Exception e) {
            // unexpected exception
            log.warn("delete cacheItemRepository.removeByBk occur exception, configItemBk : {}", cacheItemBK, e);
            return;
        }

        if (!optSuccess) {
            // unexpected exception, maybe the record was locked, or deleted by another thread
            log.warn("delete cacheItemRepository.removeByBk fail, configItemBk : {}", cacheItemBK);
        }
    }

    @Override
    public byte[] getAndDelete(ItemKeyVO itemKeyVO) {
        CacheItem cacheItem = getCacheItem(itemKeyVO);
        if (cacheItem == null) {
            return null;
        }

        CacheItemBK cacheItemBK = cacheItem.getBk();
        deleteCacheItem(cacheItemBK);

        return cacheItem.getItemValue();
    }

    @Override
    public List<byte[]> getList(BizGroupVO bizGroupVO) {
        Group group = groupRepository.getByBk(new GroupBK(bizGroupVO.getModuleName(), bizGroupVO.getGroupName()));
        if (group == null) {
            return null;
        }
        List<CacheItem> cacheItems = extCacheItemRepository.queryUnexpired(group.getId());
        return cacheItems.stream().map(CacheItem::getItemValue).toList();
    }

    private void saveCacheItem(CacheItemParam param, Long groupId) {
        Assert.notNull(groupId, "groupId can not be null");

        CacheItem cacheItem = extCacheItemConverter.toCacheItem(param);
        cacheItem.setGroupId(groupId);
        boolean optSuccess = extCacheItemRepository.save(cacheItem);
        if (!optSuccess) {
            // unexpected exception, maybe the record was locked, or deleted by another thread
            log.warn("saveConfigItem configItemRepository.save fail");
            throw new IllegalStateException("save cache item fail");
        }
    }
}
