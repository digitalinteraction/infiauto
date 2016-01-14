package com.infiauto.datastr;

import java.util.BitSet;

/**
 * Extension of the JDK BitSet class that provides for specifying a fixed length
 * along with the ability to de/serialized.
 * @author Infinite Automata
 */
public class FixedLengthBitSet
        extends BitSet {

    private int fixed_length;

    public FixedLengthBitSet(int length) {
        this.fixed_length = length;
    }

    public int fixedLength() {
        return fixed_length;
    }
    
    public FixedLengthBitSet substring(int start, int length) {
        assert(start + length <= fixed_length);
        
        FixedLengthBitSet result = new FixedLengthBitSet(length);
        for(int i = 0; i < length; i++) {
            result.set(i, this.get(start + i));
        }
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FixedLengthBitSet)) {
            return false;
        }
        FixedLengthBitSet a = (FixedLengthBitSet) obj;
        if(fixedLength() != a.fixedLength()) {
            return false;
        }
        return super.equals(obj);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() ^ fixed_length * 4703;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(fixed_length);

        for (int i = (fixed_length - 1); i >= 0; i--) {
            sb.append(get(i) ? '1' : '0');
        }

        return sb.toString();
    }
}
