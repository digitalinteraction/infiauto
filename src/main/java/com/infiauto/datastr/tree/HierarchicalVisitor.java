package com.infiauto.datastr.tree;

/**
 *
 * @author Infinite Automata
 */
public abstract class HierarchicalVisitor<T> {
    public abstract void visit(Tree<T>.Node node);
}
