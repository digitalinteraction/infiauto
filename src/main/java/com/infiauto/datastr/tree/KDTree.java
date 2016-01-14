package com.infiauto.datastr.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import static java.util.Collections.binarySearch;
import static java.util.Map.Entry;

/**
 * K-dimensional tree.
 * @author Infinite Automata
 */
public final class KDTree<K extends Number & Comparable<K>>
extends BinaryTree<ComparableArrayList<K>> {
    private int dimensions;
    
    public KDTree(int dimensions, List<K[]> input) {
        this.dimensions = dimensions;
        if(!input.isEmpty()) {
            this.root_node = buildKDTree(input, 0, null);
        }
    }
    
    private BinaryTreeNode buildKDTree(List<K[]> input, int depth, BinaryTreeNode parent_node) {
        BinaryTreeNode result = null;
        int axis = depth % dimensions;
        
        // build two associated lists where the list for K is sorted in order to find the median
        ArrayList<K> axis_point = new ArrayList<K>(input.size());
        ArrayList<K[]> value = new ArrayList<K[]>(input.size());
        for(K[] entry : input) {
            // get the point at the dimension axis
            K point = entry[axis];
            // find where to inser the point
            int insert = binarySearch(axis_point, point);
            if(insert < 0) {
                insert = -insert - 1;
            }
            // map the point and its corresponding value to their lists
            axis_point.add(insert, point);
            value.add(insert, entry);
        }
        
        // get the median value
        int median_index = axis_point.size() / 2;
        K[] median = value.get(median_index);
        
        // build the resulting BinaryTreeNode
        result = new BinaryTreeNode(parent_node, new ComparableArrayList(median));
        List<K[]> sub_value = null;
        BinaryTreeNode left_child = null;
        BinaryTreeNode right_child = null;
        if(median_index > 0) {
            sub_value = value.subList(0, median_index);
            left_child = buildKDTree(sub_value, (depth + 1), result);
            if(left_child != null) {
                result.setLeftChild(left_child);
            }
        }
        if(median_index < (axis_point.size() - 1)) {
            sub_value = value.subList((median_index + 1), value.size());
            right_child = buildKDTree(sub_value, (depth + 1), result);
            if(right_child != null) {
                result.setRightChild(right_child);
            }
        }
        
        return result;
    }
    
    public int getDimensions() {
        return dimensions;
    }
}
