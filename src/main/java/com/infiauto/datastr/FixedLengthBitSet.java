package com.infiauto.datastr;

import java.io.Serializable;
import java.util.BitSet;

/**
 * @author Infinite Automata
 */
public class FixedLengthBitSet
extends BitSet
implements Serializable {
	private static final long serialVersionUID = 3567697288265161400L;
	private int fixed_length;

    public FixedLengthBitSet(int length) {
        this.fixed_length = length;
    }

    public int fixedLength() {
        return fixed_length;
    }

    @Override
    public boolean equals(Object obj) {
/*        if(!(obj instanceof FixedLengthBitSet)) {
            return false;
        }*/
        FixedLengthBitSet a = (FixedLengthBitSet) obj;
        return super.equals(a) && (this.fixed_length == a.fixed_length);
/*        if ((compareTo (a) == 0) != super.equals(obj) ) {
        		System.out.println (this.toString() + "/" + super.toString() + "/" + a.toString());
        		
        		System.out.println (this.fixedLength());
        		System.out.println (a.fixedLength());
        		
        		throw new RuntimeException("FU!");
       		
        }
        return compareTo(a) == 0;*/
    }
    
    
    
    /*@Override
    public int hashCode() {
        return toString().hashCode();
    }*/

    @Override
	public void set(int bitIndex) {
    	if (bitIndex >= fixed_length)
    		throw new RuntimeException ("Низя!");   		

		super.set(bitIndex);
	}

/*	@Override
    public int compareTo(FixedLengthBitSet bits) {
        return toString().compareTo(bits.toString());
    }*/

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(fixed_length);
        
        for(int i = (fixed_length - 1); i >= 0; i--) {
            sb.append(get(i) ? '1' : '0');
        }

        return sb.toString();
    }
}
