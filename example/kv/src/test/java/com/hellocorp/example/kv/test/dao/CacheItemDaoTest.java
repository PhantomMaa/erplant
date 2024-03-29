package com.hellocorp.example.kv.test.dao;

import com.hellocorp.example.kv.KvApplication;
import com.hellocorp.example.kv.infra.persist.bk.CacheItemBK;
import com.hellocorp.example.kv.infra.persist.dao.CacheItemDAO;
import com.hellocorp.example.kv.infra.persist.dataobject.CacheItemDO;
import com.hellocorp.example.kv.infra.persist.query.CacheItemOffsetQuery;
import com.hellocorp.example.kv.infra.persist.query.CacheItemPageNumQuery;
import jakarta.annotation.Resource;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * auto generated by erplant.
 */
@SpringBootTest(
        classes = KvApplication.class
)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CacheItemDaoTest {
    @Resource
    private CacheItemDAO cacheItemDAO;

    private final Long groupId = 18L;

    private final String itemKey = "ghg";

    private final Long ttl = 80L;

    @Test
    @Order(1)
    public void testInsert() {
        CacheItemDO cacheItemDO = new CacheItemDO();
        cacheItemDO.setGroupId(groupId);
        cacheItemDO.setItemKey(itemKey);
        cacheItemDO.setItemValue("hello".getBytes());
        int affectRow = cacheItemDAO.insert(cacheItemDO);
        Assertions.assertTrue(affectRow > 0);
    }

    @Test
    @Order(2)
    public void testGet() {
        CacheItemDO cacheItemDO = cacheItemDAO.getByBk(new CacheItemBK(groupId, itemKey));
        Assertions.assertNotNull(cacheItemDO);
    }

    @Test
    @Order(2)
    public void testGetByBk() {
        CacheItemDO cacheItemDO = cacheItemDAO.getByBk(new CacheItemBK(groupId, itemKey));
        Assertions.assertNotNull(cacheItemDO);
    }

    @Test
    @Order(2)
    public void testPageNumQuery() {
        CacheItemPageNumQuery query = CacheItemPageNumQuery.builder().groupId(groupId).build();
        List<CacheItemDO> list = cacheItemDAO.pageNumQuery(query);
        Assertions.assertFalse(list.isEmpty());
    }

    @Test
    @Order(2)
    public void testOffsetQuery() {
        CacheItemOffsetQuery query = CacheItemOffsetQuery.builder().groupId(groupId).build();
        List<CacheItemDO> list = cacheItemDAO.offsetQuery(query);
        Assertions.assertFalse(list.isEmpty());
    }

    @Test
    @Order(2)
    public void testCount() {
        CacheItemPageNumQuery query = CacheItemPageNumQuery.builder().groupId(groupId).build();
        int count = cacheItemDAO.count(query);
        Assertions.assertTrue(count > 0);
    }

    @Test
    @Order(3)
    public void testUpdate() {
        CacheItemDO cacheItemDO = cacheItemDAO.getByBk(new CacheItemBK(groupId, itemKey));
        cacheItemDO.setGroupId(groupId);
        cacheItemDO.setItemKey(itemKey);
        cacheItemDO.setItemValue("hello".getBytes());
        int affectRow = cacheItemDAO.update(cacheItemDO);
        Assertions.assertTrue(affectRow > 0);
    }

    @Test
    @Order(4)
    public void testDelete() {
        CacheItemDO cacheItemDO = cacheItemDAO.getByBk(new CacheItemBK(groupId, itemKey));
        int affectRow = cacheItemDAO.delete(cacheItemDO.getId());
        Assertions.assertTrue(affectRow > 0);
    }

    @Test
    @Order(2)
    public void testBatchGet() {
        CacheItemDO cacheItemDO = cacheItemDAO.getByBk(new CacheItemBK(groupId, itemKey));
        List<CacheItemDO> list = cacheItemDAO.batchGet(List.of(cacheItemDO.getId()));
        Assertions.assertFalse(list.isEmpty());
    }
}
