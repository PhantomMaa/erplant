package com.hellocorp.automq.ddd;

/**
 * Identifiable
 *
 * @param <BK>
 */
public interface Identifiable<BK extends Identifier> {

    BK getBk();

}
