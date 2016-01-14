package com.infiauto.datastr.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import static java.lang.Math.ceil;
import static java.util.Collections.binarySearch;

/**
 *
 * @author Infinite Automata
 */
public class BTree<T extends Comparable<T>> extends Tree<T> {
    private int order;
    private int minimum_values;
    private int minimum_child_nodes;
    
    public class BTreeNode extends Node {
        private ArrayList<T> values;
        private ArrayList<BTreeNode> child_nodes;
        
        /**
         * 
         * @param parent_node 
         */
        public BTreeNode(BTreeNode parent_node) {
            super(parent_node, null);
            this.values = new ArrayList<T>(order - 1);
            this.child_nodes = new ArrayList<BTreeNode>(order);
        }
        
        /**
         * 
         * @param parent_node 
         * @param values
         */
        public BTreeNode(BTreeNode parent_node, List<T> existing_values) {
            this(parent_node);
            this.values.addAll(existing_values);
        }
        
        public int getValueSize() {
            return values.size();
        }
        
        public T getValue(int index) {
            return values.get(index);
        }
        
        /**
         * 
         * @return 
         */
        public Collection<? extends Node> getChildren() {
            return child_nodes;
        }
        
        /**
         * 
         * @param after
         * @param node 
         */
        public void insertNode(final BTreeNode after, final BTreeNode node) {
            int index = -1;
            while(++index < child_nodes.size()) {
                if(child_nodes.get(index) == after) {
                    index++;
                    break;
                }
            }
            child_nodes.add(index, node);
        }
        
        /**
         * 
         * @param node 
         */
        public void insertNode(final BTreeNode node) {
            child_nodes.add(node);
        }
        
        public void insertHere(int index, T value) {
            values.add(index, value);
            if(values.size() > (order - 1)) {
                // find the median and split the values on either side of it
                int median_temp_index = values.size() / 2;
                T median_value = values.get(median_temp_index);
                List<T> right_values = values.subList((median_temp_index + 1), values.size());

                // create the new sibling node
                BTreeNode sibling_node = new BTreeNode((BTreeNode) this.getParentNode(), right_values);
                
                // remove all of the rightmost values
                Iterator<T> right_iterator = right_values.iterator();
                while(right_iterator.hasNext()) {
                    right_iterator.next();
                    right_iterator.remove();
                }
                // remove the median value
                values.remove(values.size() - 1); 
                
                // split
                BTreeNode parent_node = null;
                if(getParentNode() != null) {
                    parent_node = (BTreeNode) getParentNode();
                    parent_node.insertNode(this, sibling_node);
                    parent_node.insert(median_value);
                }
                else {
                    // create new parent node
                    parent_node = new BTreeNode(null);
                    setParentNode(parent_node);
                    parent_node.insertNode(this);
                    parent_node.insertNode(sibling_node);
                    parent_node.insertHere(0, median_value);
                }
                sibling_node.setParentNode(parent_node);
            }
        }
        
        /**
         * 
         * @param value 
         */
        public void insert(T value) {
            int index = binarySearch(values, value);
            index = -index - 1;

            if(isLeafNode()) {
                insertHere(index, value);
            }
            else {
                child_nodes.get(index).insert(value);
            }
        }
    }
    
    public BTree(int order) {
        this.order = order;
        this.minimum_child_nodes = ((Double) ceil(order / 2)).intValue();
        this.minimum_values = minimum_child_nodes - 1;
        this.root_node = new BTreeNode(null);
    }
    
    public int getOrder() {
        return order;
    }
    
    public void insert(T value) {
        ((BTreeNode) root_node).insert(value);
        // recursively find a new root node if one exists
        while(root_node.getParentNode() != null) {
            root_node = root_node.getParentNode();
        }
    }
    
    public void delete(T value) {
        
    }
}