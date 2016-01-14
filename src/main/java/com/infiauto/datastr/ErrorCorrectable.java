package com.infiauto.datastr;

/**
 * A class implementing the ErrorCorrectable interface takes
 * as input a possible string along with a maximum possible
 * distance and returns a map of candidate corrected strings
 * along with the number of corrections required to reach the
 * corresponding candidate string.
 * @author Infinite Automata
 */
public interface ErrorCorrectable<T> {
    /**
     * Suggests a list of corrected strings within a range of error corrections.
     * @param distance maximum distance in which to search for corrections
     * @param input candidate string for correction
     * @return mapping between error distances and corrected strings
     */
    public MultiMap<Integer,T> correctError(int distance, T input);
}