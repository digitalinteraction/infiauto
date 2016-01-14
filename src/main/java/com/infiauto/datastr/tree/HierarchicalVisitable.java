package com.infiauto.datastr.tree;

/**
 *
 * @author Infinite Automata
 */
public interface HierarchicalVisitable<V> {
    public void accept(HierarchicalVisitor<V> visitor, HierarchicalTraversalOrder order);
}
