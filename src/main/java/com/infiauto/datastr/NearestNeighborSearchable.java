package com.infiauto.datastr;

/**
 * Find the nearest neighbor point.
 * @author Infinite Automata
 */
public interface NearestNeighborSearchable<T> {
    /**
     * 
     * @param point
     * @return 
     */
    public T[] findNearestNeighbor(T[] point);
}