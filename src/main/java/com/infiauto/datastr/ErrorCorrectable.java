package com.infiauto.datastr;

import java.util.Collection;

/**
 * @author Infinite Automata
 */
public interface ErrorCorrectable {
    Collection<String> correctError(String input, int errors);
}
