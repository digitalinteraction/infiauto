package com.infiauto;

import java.util.BitSet;
import java.util.LinkedList;

/**
 * Collection of functions used for generating lists of prime numbers.
 * @author Infinite Automata
 */
public class Primes {
    private static final int FIRST_PRIME = 2;
    
    /**
     * Generates a list of primes using the sieve of Eratosthenes method.
     * @param max highest possible prime
     * @return list of primes in ascending order from 2 to the specified maximum inclusive
     */
    public static Integer[] eratosthenesSieve(int max) {
        assert(max <= 10000000);
        
        BitSet not_prime = new BitSet(max);
        LinkedList<Integer> temp = new LinkedList<Integer>();
        
        int root_max = ((Double) Math.sqrt((double) max)).intValue();
        for(int i = FIRST_PRIME; i <= root_max; i++) {
            int index = i - FIRST_PRIME;
            if(!not_prime.get(index)) {
                // found a prime
                temp.add(i);
                
                // mark products as not prime
                for(int j = index + i; j <= max; j += i) {
                    not_prime.set(j);
                }
            }
        }
        
        // now examine the primality of all numbers above the square root of the maximum
        int current = -1;
        int next = root_max - 1;
        while(current <= max) {
            current = next + 1;
            
            // find the next unset bit
            next = not_prime.nextClearBit(current);
            if(next < max) {
                temp.add(next + FIRST_PRIME);
            }
        }
        
        return temp.toArray(new Integer[0]);
    }
}
