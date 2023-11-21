package com.hellocorp.automq.ddd;

import java.util.List;

public interface Repository<T extends AggregateRoot<BK>, Q extends PageNumQuery, O extends OffsetQuery, BK extends Identifier> {

    /**
     * Get an aggregate through its id
     */
    T get(Long id);

    /**
     * Get an aggregate through its business key
     */
    T getByBk(BK bk);

    /**
     * Get and lock an aggregate through its business key
     */
    T getAndLockByBk(BK bk);

    /**
     * Get aggregates through them ids
     */
    List<T> batchGet(List<Long> ids);

    /**
     * Save an entity, first try to update with entity's id or bk, else to insert
     */
    boolean save(T aggregate);

    /**
     * Create new record
     *
     * @param aggregate
     * @return
     */
    boolean create(T aggregate);

    /**
     * Update fields partly as needed
     */
    boolean update(T aggregate);

    /**
     * Remove an entity from repository
     */
    boolean remove(Long id);

    /**
     * Remove an entity from repository through its business key
     */
    boolean removeByBk(BK bk);

    /**
     * Query aggregates by page num
     */
    List<T> pageNumQuery(Q query);

    /**
     * Query aggregates by offset
     */
    List<T> offsetQuery(O query);

    /**
     * Count aggregates
     */
    int count(Query query);

}
