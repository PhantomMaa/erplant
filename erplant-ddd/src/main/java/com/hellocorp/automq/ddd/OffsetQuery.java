package com.hellocorp.automq.ddd;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * OffsetPageQuery
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OffsetQuery extends Query {

    /**
     * 支持按照id顺序或者逆序查询
     */
    private Long offset;

    /**
     * ASC or DESC
     */
    private OrderType orderType;

    public OffsetQuery(Integer pageSize, Long offset, OrderType orderType) {
        super.pageSize = pageSize == null ? 20 : pageSize;
        this.offset = offset;
        this.orderType = orderType;
    }

    public long getOffset() {
        return offset == null ? 0 : offset;
    }

    public String getOrderType() {
        return orderType == null ? OrderType.ASC.name() : orderType.name();
    }
}
