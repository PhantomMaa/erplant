package com.hellocorp.example.kv.test.service;

import com.hellocorp.example.kv.KvApplication;
import com.hellocorp.example.kv.domain.model.BizGroupVO;
import com.hellocorp.example.kv.domain.model.ItemKeyVO;
import com.hellocorp.example.kv.domain.model.param.CacheItemParam;
import com.hellocorp.example.kv.domain.service.CacheService;
import jakarta.annotation.Resource;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = KvApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CacheServiceTest {
    @Resource
    CacheService cacheService;

    private final String appName = "app1";

    private final String groupName = "test_group";

    private final String itemKey = "test";

    private final String itemValue = "abc";

    private final BizGroupVO bizGroup = new BizGroupVO(appName, groupName);

    @Test
    @Order(1)
    void testPut() {
        CacheItemParam itemParam = CacheItemParam.builder()
            .appName(appName)
            .groupName(groupName)
            .itemKey(itemKey)
            .itemValue(itemValue.getBytes())
            .build();
        boolean result = cacheService.put(itemParam);
        Assertions.assertTrue(result);
    }

    @Test
    @Order(2)
    void testGet() {
        ItemKeyVO itemVO = ItemKeyVO.builder().bizGroup(bizGroup).itemKey(itemKey).build();
        byte[] result = cacheService.get(itemVO);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(itemValue, new String(result));
    }

    @Test
    @Order(2)
    void testGetCacheKVPairs() {
        List<byte[]> cacheKVPairs = cacheService.getList(bizGroup);
        Assertions.assertNotNull(cacheKVPairs);
    }

    @Test
    @Order(3)
    void testDelete() {
        ItemKeyVO itemVO = ItemKeyVO.builder().bizGroup(bizGroup).itemKey(itemKey).build();
        boolean resultDelete = cacheService.delete(itemVO);
        Assertions.assertTrue(resultDelete);

        byte[] itemValue = cacheService.get(itemVO);
        Assertions.assertNull(itemValue);
    }

}