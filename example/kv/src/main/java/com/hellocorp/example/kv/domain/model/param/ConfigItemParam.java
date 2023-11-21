package com.hellocorp.example.kv.domain.model.param;

import com.hellocorp.example.kv.domain.model.BizGroupVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
public class ConfigItemParam {

    @NonNull
    private BizGroupVO bizGroup;

    @NonNull
    private String itemKey;

    @NonNull
    private String itemValue;

    private String valueType = "String";

    /**
     * 是否记录配置变更历史
     */
    private Boolean logHistory;
}
