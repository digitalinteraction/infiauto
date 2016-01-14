package com.infiauto.datastr.tree;

/**
 * 
 * @author Infinite Automata
 * @param <M> class extending java.lang.Number
 * @param <T> type of object stored at each tree node
 * @since 1.0.0
 */
public interface MetricTree<M extends Number,T> {
    /**
     * Function that defines the metric space for the MetricTree.
     * @param t1 first operand
     * @param t2 second operand
     * @return numeric result of the metric calculation
     */
    public M metricFunction(T t1, T t2);
}