package com.infiauto.datastr;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;

/**
 * The MultiMap is a collection that provides for mapping between
 * a single key and multiple corresponding values.  This differs
 * from the java.util.Map class that allows only a one-to-one
 * mapping.  MultiMap allows for one-to-many mappings.
 * @author Infinite Automata
 */
public class MultiMap<K, V> {

    private TreeMap<K, LinkedList<V>> map;

    public MultiMap(Comparator<K> comparator) {
        this.map = new TreeMap<K, LinkedList<V>>(comparator);
    }

    public MultiMap() {
        this.map = new TreeMap<K, LinkedList<V>>();
    }

    public boolean contains(K key, V value) {
        LinkedList<V> list = map.get(key);
        if (list == null) {
            return false;
        }
        return list.contains(value);
    }

    public void put(K key, V value) {
        LinkedList<V> list = map.get(key);
        if (list == null) {
            list = new LinkedList<V>();
        }
        list.add(value);
        map.put(key, list);
    }

    public Set<K> keySet() {
        return map.keySet();
    }

    public Collection<V> getAll(K key) {
        return map.get(key);
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public int hashCode() {
        int result = 0;
        int[] primes = new int[]{1, 3, 5, 7, 11, 13, 17};
        int i = 0;
        for (K key : map.keySet()) {
            result ^= map.get(key).hashCode() * key.hashCode() * primes[i++];
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MultiMap)) {
            return false;
        }
        MultiMap<K, V> mm = (MultiMap<K, V>) obj;
        HashSet<K> mm_keys = new HashSet<K>(mm.keySet());
        for (K key : mm.keySet()) {
            Collection<V> c = mm.getAll(key);
            if (c == null) {
                // no matching key
                return false;
            }
            if (!c.equals(map.get(key))) {
                // mapped collections don't match
                return false;
            }
            mm_keys.remove(key);
        }
        return mm_keys.isEmpty();
    }
}