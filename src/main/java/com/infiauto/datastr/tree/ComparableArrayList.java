package com.infiauto.datastr.tree;

import java.util.ArrayList;
import static java.util.Arrays.asList;

/**
 * ArrayList used for comparing all list elements.  Only used by other
 * classes in the same package.
 * @author Infinite Automata
 */
final class ComparableArrayList<T extends Comparable<T>> extends ArrayList<T> implements Comparable<ComparableArrayList<T>> {

    ComparableArrayList(T... var_args) {
        super(asList(var_args));
    }

    @Override
    public int compareTo(ComparableArrayList<T> list) {
        int comparison = 0;
        for (int i = 0; i < Math.min(size(), list.size()); i++) {
            comparison = ((Comparable) get(i)).compareTo(list.get(i));
            if (comparison != 0) {
                break;
            }
        }

        return comparison;
    }
}