package com.hellocorp.example.kv.domain.service.impl;

import com.hellocorp.automq.ddd.OrderType;
import com.hellocorp.example.kv.domain.converter.ExtConfigItemConverter;
import com.hellocorp.example.kv.domain.converter.ExtConfigItemHistoryConverter;
import com.hellocorp.example.kv.domain.model.BizGroupVO;
import com.hellocorp.example.kv.domain.model.ItemKeyVO;
import com.hellocorp.example.kv.domain.model.ItemValueVO;
import com.hellocorp.example.kv.domain.model.param.ConfigItemParam;
import com.hellocorp.example.kv.domain.service.ConfigService;
import com.hellocorp.example.kv.domain.entity.ConfigItemHistory;
import com.hellocorp.example.kv.domain.entity.Group;
import com.hellocorp.example.kv.domain.entity.ConfigItem;
import com.hellocorp.example.kv.domain.repository.GroupRepository;
import com.hellocorp.example.kv.domain.repository.ConfigItemHistoryRepository;
import com.hellocorp.example.kv.domain.repository.ConfigItemRepository;
import com.hellocorp.example.kv.infra.persist.bk.ConfigItemBK;
import com.hellocorp.example.kv.infra.persist.bk.GroupBK;
import com.hellocorp.example.kv.infra.persist.query.ConfigItemHistoryPageNumQuery;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

@Slf4j
@Service
public class ConfigServiceImpl implements ConfigService {

    @Resource
    ExtConfigItemConverter extConfigItemConverter;

    @Resource
    ExtConfigItemHistoryConverter extConfigItemHistoryConverter;

    @Resource
    ConfigItemRepository configItemRepository;

    @Resource
    ConfigItemHistoryRepository configItemHistoryRepository;

    @Resource
    GroupRepository groupRepository;

    @Resource
    TransactionTemplate configTransactionTemplate;

    @Override
    public ItemValueVO get(ItemKeyVO itemKeyVO) {
        BizGroupVO bizGroupVO = itemKeyVO.getBizGroup();
        Group group = groupRepository.getByBk(new GroupBK(bizGroupVO.getModuleName(), bizGroupVO.getGroupName()));
        if (group == null) {
            return null;
        }

        ConfigItemBK configItemBk = new ConfigItemBK(group.getId(), itemKeyVO.getItemKey());
        ConfigItem configItem = configItemRepository.getByBk(configItemBk);
        if (configItem == null) {
            return null;
        }

        return ItemValueVO.builder().itemValue(configItem.getItemValue()).valueType(configItem.getValueType()).build();
    }

    @Override
    public void delete(ItemKeyVO itemKeyVO) {
        BizGroupVO bizGroupVO = itemKeyVO.getBizGroup();
        Group group = groupRepository.getByBk(new GroupBK(bizGroupVO.getModuleName(), bizGroupVO.getGroupName()));
        if (group == null) {
            throw new RuntimeException("group not found");
        }

        long groupId = group.getId();
        ConfigItemBK configItemBk = new ConfigItemBK(groupId, itemKeyVO.getItemKey());
        ConfigItem configItem = configItemRepository.getByBk(configItemBk);
        if (configItem == null) {
            return;
        }

        configTransactionTemplate.execute(status -> {
            boolean optSuccess = configItemRepository.removeByBk(configItemBk);
            if (!optSuccess) {
                log.info("delete configItemRepository.removeByBk fail");
                return false;
            }

            if (BooleanUtils.isTrue(configItem.getLogHistory())) {
                addConfigItemHistory(configItem);
            }
            return true;
        });
        log.info("delete done, configItemBk : {}", configItemBk);
    }

    @Override
    public void save(ConfigItemParam itemParam) {
        BizGroupVO bizGroupVO = itemParam.getBizGroup();
        Group group = getOrCreateGroup(bizGroupVO);

        long groupId = group.getId();
        saveConfigItemAndHistory(groupId, itemParam);
    }

    public Group getOrCreateGroup(BizGroupVO bizGroupVO) {
        Group pre = groupRepository.getByBk(new GroupBK(bizGroupVO.getModuleName(), bizGroupVO.getGroupName()));
        if (pre != null) {
            return pre;
        }

        String appName = bizGroupVO.getModuleName();
        String groupName = bizGroupVO.getGroupName();
        Group groupNew = new Group();
        groupNew.setAppName(appName);
        groupNew.setGroupName(groupName);
        boolean optSuccess = groupRepository.save(groupNew);
        if (!optSuccess) {
            log.warn("getOrCreateGroup groupRepository.save fail, groupNew : {}", groupNew);
            throw new IllegalStateException("save group fail");
        }

        return groupNew;
    }

    private void saveConfigItemAndHistory(long groupId, ConfigItemParam itemParam) {
        Assert.isTrue(groupId > 0, "groupId must be positive");

        ConfigItem configItem = extConfigItemConverter.toConfigItem(itemParam);
        configItem.setGroupId(groupId);

        configTransactionTemplate.execute(status -> {
            boolean optSuccess = configItemRepository.save(configItem);
            if (!optSuccess) {
                log.info("saveConfigItemAndHistory configItemRepository.save fail");
                throw new IllegalStateException("save config item fail");
            }

            if (BooleanUtils.isTrue(itemParam.getLogHistory())) {
                addConfigItemHistory(configItem);
            }
            return true;
        });
    }

    /**
     * add new record
     *
     * @param configItem
     */
    private void addConfigItemHistory(ConfigItem configItem) {
        ConfigItemHistory history = extConfigItemHistoryConverter.toConfigItemHistory(configItem);

        ConfigItemHistoryPageNumQuery query = ConfigItemHistoryPageNumQuery.builder()
            .itemId(configItem.getId())
            .orderType(OrderType.DESC)
            .pageSize(1)
            .build();
        List<ConfigItemHistory> historyList = configItemHistoryRepository.pageNumQuery(query);
        if (!historyList.isEmpty()) {
            ConfigItemHistory lastHistory = historyList.get(0);
            if (Objects.equals(history.getItemValue(), lastHistory.getItemValue())) {
                // if equal, do not record history
                return;
            }

            int itemVersion = lastHistory.getItemVersion() + 1;
            history.setItemVersion(itemVersion);
        } else {
            history.setItemVersion(0);
        }

        boolean optSuccess = configItemHistoryRepository.create(history);
        if (!optSuccess) {
            // unexpected exception, maybe the record was locked, or deleted by another thread
            log.warn("addConfigItemHistory configItemHistoryRepository.save fail, history : {}", history);
            throw new IllegalStateException("save config item history fail");
        }
    }

}
