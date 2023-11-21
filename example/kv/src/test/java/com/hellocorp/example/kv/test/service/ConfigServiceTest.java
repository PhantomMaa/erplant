package com.hellocorp.example.kv.test.service;

import com.hellocorp.example.kv.KvApplication;
import com.hellocorp.example.kv.domain.model.BizGroupVO;
import com.hellocorp.example.kv.domain.model.ItemKeyVO;
import com.hellocorp.example.kv.domain.model.ItemValueVO;
import com.hellocorp.example.kv.domain.model.param.ConfigItemParam;
import com.hellocorp.example.kv.domain.service.ConfigService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = KvApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ConfigServiceTest {
    @Resource
    ConfigService configService;

    private final String appName = "app1";

    private final String groupName = "test_group";

    private final String itemKey = "test";

    private final String itemValue = "abc";

    private final BizGroupVO bizGroup = new BizGroupVO(appName, groupName);

    @Test
    @Order(1)
    void testSave() {
        ConfigItemParam itemParam = ConfigItemParam.builder()
            .bizGroup(new BizGroupVO(appName, groupName))
            .itemKey(itemKey)
            .itemValue(itemValue)
            .valueType("String")
            .build();
        configService.save(itemParam);
        Assertions.assertTrue(true);
    }

    @Test
    @Order(2)
    void testGet() {
        ItemKeyVO itemVO = ItemKeyVO.builder().bizGroup(bizGroup).itemKey(itemKey).build();
        ItemValueVO itemValueVO = configService.get(itemVO);
        Assertions.assertNotNull(itemValueVO);
        Assertions.assertEquals(itemValue, itemValueVO.getItemValue());
        Assertions.assertEquals("String", itemValueVO.getValueType());
    }

    @Test
    @Order(2)
    void testDelete() {
        ItemKeyVO itemVO = ItemKeyVO.builder().bizGroup(bizGroup).itemKey(itemKey).build();
        configService.delete(itemVO);
        Assertions.assertTrue(true);

        ItemValueVO itemValueVO = configService.get(itemVO);
        Assertions.assertNull(itemValueVO);
    }
}