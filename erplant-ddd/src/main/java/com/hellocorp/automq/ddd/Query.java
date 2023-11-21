package com.hellocorp.automq.ddd;

import lombok.Data;

/**
 * Query
 */
@Data
public abstract class Query {

    protected int pageSize;

    public int getLimit() {
        return pageSize;
    }
}
