package com.infiauto.datastr.tree;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Binary tree.
 * @author Infinite Automata
 */
public class BinaryTree<V extends Comparable> extends Tree<V> {
    public class BinaryTreeNode extends Node {
        private BinaryTreeNode left_child;
        private BinaryTreeNode right_child;
        
        public BinaryTreeNode(BinaryTreeNode parent_node, V value) {
            super(parent_node, value);
            
            this.left_child = null;
            this.right_child = null;
        }
        
        public void setLeftChild(BinaryTreeNode child) {
            this.left_child = child;
            if(child != null) {
                child.setParentNode(this);
            }
        }
        
        public void setRightChild(BinaryTreeNode child) {
            this.right_child = child;
            if(child != null) {
                child.setParentNode(this);
            }
        }
        
        /**
         * Return the lesser child, or the child to the left of the current node.
         * @return the left child
         */
        public BinaryTreeNode getLeftChild() {
            return left_child;
        }
        
        /**
         * Return the greater child, or the child to the right of the current node.
         * @return the right child
         */
        public BinaryTreeNode getRightChild() {
            return right_child;
        }
        
        protected int getLeftChildDepth() {
            return left_child != null ? left_child.getDepth() : 0;
        }
        
        protected int getRightChildDepth() {
            return right_child != null ? right_child.getDepth() : 0;
        }
        
        /**
         * 
         * @return 
         */
        public Collection<? extends Node> getChildren() {
            ArrayList<Node> result = new ArrayList<Node>(2);
            if(left_child != null) {
                result.add(left_child);
            }
            if(right_child != null) {
                result.add(right_child);
            }
            return result;
        }
        
        /**
         * 
         * @param value 
         */
        public void add(V value) {
            //setDepth(add(value, 0));
            add(value, 0);
        }
        
        /**
         * 
         * @param value
         * @param depth
         * @return 
         */
        protected int add(V value, int depth) {
            int result = depth;
            int comparison = value.compareTo(getValue());
            
            if(comparison < 0) {
                if(getLeftChild() != null) {
                    result = getLeftChild().add(value, (depth + 1));
                }
                else {
                    setLeftChild(new BinaryTreeNode(this, value));
                    result++;
                }
            }
            else if(comparison > 0) {
                if(getRightChild() != null) {
                    result = getRightChild().add(value, (depth + 1));
                }
                else {
                    setRightChild(new BinaryTreeNode(this, value));
                    result++;
                }
            }
            
            setDepth(result);
            return result;
        }
    }
    
    /**
     * 
     * @param value 
     */
    public void add(V value) {
        if(root_node == null) {
            root_node = new BinaryTreeNode(null, value);
        }
        else {
            ((BinaryTreeNode) root_node).add(value);
        }
    }
}