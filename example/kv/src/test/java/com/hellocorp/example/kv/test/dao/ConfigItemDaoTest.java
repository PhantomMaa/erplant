package com.hellocorp.example.kv.test.dao;

import com.hellocorp.example.kv.KvApplication;
import com.hellocorp.example.kv.infra.persist.bk.ConfigItemBK;
import com.hellocorp.example.kv.infra.persist.dao.ConfigItemDAO;
import com.hellocorp.example.kv.infra.persist.dataobject.ConfigItemDO;
import com.hellocorp.example.kv.infra.persist.query.ConfigItemOffsetQuery;
import com.hellocorp.example.kv.infra.persist.query.ConfigItemPageNumQuery;
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
public class ConfigItemDaoTest {
    @Resource
    private ConfigItemDAO configItemDAO;

    private final Long groupId = 18L;

    private final String itemKey = "ghg";

    @Test
    @Order(1)
    public void testInsert() {
        ConfigItemDO configItemDO = new ConfigItemDO();
        configItemDO.setGroupId(groupId);
        configItemDO.setItemKey(itemKey);
        int affectRow = configItemDAO.insert(configItemDO);
        Assertions.assertTrue(affectRow > 0);
    }

    @Test
    @Order(2)
    public void testGet() {
        ConfigItemDO configItemDO = configItemDAO.getByBk(new ConfigItemBK(groupId, itemKey));
        Assertions.assertNotNull(configItemDO);
    }

    @Test
    @Order(2)
    public void testGetByBk() {
        ConfigItemDO configItemDO = configItemDAO.getByBk(new ConfigItemBK(groupId, itemKey));
        Assertions.assertNotNull(configItemDO);
    }

    @Test
    @Order(2)
    public void testPageNumQuery() {
        ConfigItemPageNumQuery query = ConfigItemPageNumQuery.builder().groupId(groupId).build();
        List<ConfigItemDO> list = configItemDAO.pageNumQuery(query);
        Assertions.assertFalse(list.isEmpty());
    }

    @Test
    @Order(2)
    public void testOffsetQuery() {
        ConfigItemOffsetQuery query = ConfigItemOffsetQuery.builder().groupId(groupId).build();
        List<ConfigItemDO> list = configItemDAO.offsetQuery(query);
        Assertions.assertFalse(list.isEmpty());
    }

    @Test
    @Order(2)
    public void testCount() {
        ConfigItemPageNumQuery query = ConfigItemPageNumQuery.builder().groupId(groupId).build();
        int count = configItemDAO.count(query);
        Assertions.assertTrue(count > 0);
    }

    @Test
    @Order(3)
    public void testUpdate() {
        ConfigItemDO configItemDO = configItemDAO.getByBk(new ConfigItemBK(groupId, itemKey));
        configItemDO.setGroupId(groupId);
        configItemDO.setItemKey(itemKey);
        int affectRow = configItemDAO.update(configItemDO);
        Assertions.assertTrue(affectRow > 0);
    }

    @Test
    @Order(4)
    public void testDelete() {
        ConfigItemDO configItemDO = configItemDAO.getByBk(new ConfigItemBK(groupId, itemKey));
        int affectRow = configItemDAO.delete(configItemDO.getId());
        Assertions.assertTrue(affectRow > 0);
    }

    @Test
    @Order(2)
    public void testBatchGet() {
        ConfigItemDO configItemDO = configItemDAO.getByBk(new ConfigItemBK(groupId, itemKey));
        List<ConfigItemDO> list = configItemDAO.batchGet(List.of(configItemDO.getId()));
        Assertions.assertFalse(list.isEmpty());
    }
}
