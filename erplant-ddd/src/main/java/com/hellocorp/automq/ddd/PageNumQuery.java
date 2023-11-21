package com.hellocorp.automq.ddd;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PageNumQuery extends Query {

    private int pageNum;

    /**
     * ASC or DESC
     */
    private OrderType orderType;

    private String orderColumn;

    public PageNumQuery(Integer pageSize, Integer pageNum, OrderType orderType, String orderColumn) {
        super.pageSize = pageSize == null ? 20 : pageSize;
        this.pageNum = pageNum == null ? 1 : pageNum;
        this.orderType = orderType;
        this.orderColumn = orderColumn;
    }

    public String getOrderColumn() {
        return orderColumn;
    }

    public int getStart() {
        return (pageNum - 1) * pageSize;
    }

    public String getOrderType() {
        return orderType == null ? OrderType.ASC.name() : orderType.name();
    }
}
