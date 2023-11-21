package com.hellocorp.example.kv.domain.repository;

import com.hellocorp.example.kv.domain.entity.ConfigItemHistory;
import com.hellocorp.example.kv.domain.repository.ConfigItemHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExtConfigItemHistoryRepository extends ConfigItemHistoryRepository {

    @Override
    public boolean update(ConfigItemHistory configItemHistory) {
        throw new UnsupportedOperationException("this table type does not support update");
    }
}
