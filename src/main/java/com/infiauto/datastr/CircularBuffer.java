package com.infiauto.datastr;

/**
 *
 * @author Courtney Falk
 */
public class CircularBuffer<T> {
    private T[] buffer;
    private int count;
    private int index;

    @SuppressWarnings({"unchecked"})
    public CircularBuffer(int max_size) {
        this.buffer = (T[]) new Object[max_size];
        this.count = 0;
        this.index = 0;
    } // CircularBuffer

    /**
     * Current number of elements in the buffer.
     *
     * @return element count
     */
    public int size() {
        return count;
    } // size

    /**
     * Add a single element to the buffer.
     *
     * @param element element to add
     */
    public void add(T element) {
        buffer[(index + count) % buffer.length] = element;
        if(count < buffer.length) count++;
        else index = (index + 1) % buffer.length;
    } // add

    /**
     * Retrieve a specific item from the buffer.
     *
     * @param i index for the specified item
     * @return the item at index i
     */
    public T get(int i) {
        if((i < 0) || (i >= count)) throw new IndexOutOfBoundsException();
        int position = (index + i) % buffer.length;
        return buffer[position];
    } // get

    /**
     * Copies the CircularBuffer's contents in order to the provided array.
     *
     * @param result array to contain the CircularBuffer contents
     */
    public void toArray(T[] result) {
        if(result.length != buffer.length)
            throw new RuntimeException("Length of array ("
                    + result.length
                    + ") does not match the "
                    + "length of the CircularBuffer ("
                    + buffer.length
                    + ")");
        for(int i = 0; i < count; i++)
            result[i] = buffer[(index + i) % buffer.length];
    } // toArray

    /**
     * Gives the overall buffer size.
     *
     * @return total buffer size
     */
    public int maxSize() {
        return buffer.length;
    } // maxSize

    /**
     * Resets the buffer and associated variables.
     */
    public void reset() {
        for(int i = 0; i < buffer.length; i++)
            buffer[i] = null;
        count = 0;
        index = 0;
    } // reset
} // class CircularBuffer