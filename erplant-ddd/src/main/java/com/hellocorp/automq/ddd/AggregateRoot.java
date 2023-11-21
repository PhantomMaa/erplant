package com.hellocorp.automq.ddd;

/**
 * Marker interface for AggregateRoot, Only an AggregateRoot can be persisted by
 * the Repository
 */
public interface AggregateRoot<D extends Identifier> extends Entity<D> {

}
