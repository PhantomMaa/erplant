package com.hellocorp.example.kv.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemKeyVO {

    private BizGroupVO bizGroup;

    private String itemKey;

}