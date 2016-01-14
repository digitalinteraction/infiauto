package com.infiauto.datastr;

/**
 * A class implementing the SubstringsMatchable interface
 * provides the ability to search an input text and identify
 * any substrings contained within.
 * @author Infinite Automata
 */
public interface SubstringsMatchable {
    /**
     * Searches an input text for matching substrings.
     * @param text input text to search
     * @return MultiMap of found substrings and the indices
     * of their positions within the input text
     */
    public MultiMap<String,Integer> matchSubstrings(String text);
}