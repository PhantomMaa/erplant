package com.hellocorp.example.kv.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BizGroupVO {

    /**
     * 模块名
     */
    private String moduleName;

    /**
     * 这组配置的名称
     */
    private String groupName;
}
