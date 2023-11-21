package com.hellocorp.example.kv.domain.converter;

import com.hellocorp.example.kv.domain.model.param.ConfigItemParam;
import com.hellocorp.example.kv.domain.entity.ConfigItem;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring"
)
public interface ExtConfigItemConverter extends ConfigItemConverter {

    ConfigItem toConfigItem(ConfigItemParam itemParam);
}
