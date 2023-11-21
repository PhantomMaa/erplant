package com.hellocorp.example.kv.domain.converter;

import com.hellocorp.example.kv.domain.converter.ConfigItemHistoryConverter;
import com.hellocorp.example.kv.domain.entity.ConfigItem;
import com.hellocorp.example.kv.domain.entity.ConfigItemHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring"
)
public interface ExtConfigItemHistoryConverter extends ConfigItemHistoryConverter {

    @Mapping(source = "id", target = "id", ignore = true)
    @Mapping(source = "id", target = "itemId")
    ConfigItemHistory toConfigItemHistory(ConfigItem item);
}
