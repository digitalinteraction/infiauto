package com.infiauto.datastr.tree;

/**
 *
 * @author Infinite Automata
 */
public final class RedBlackTree<V extends Comparable>
extends BinaryTree<V> {
    public enum Color { RED, BLACK; }
    
    public class RedBlackTreeNode extends BinaryTreeNode {
        private Color color;
        
        public RedBlackTreeNode(RedBlackTreeNode parent_node, V value, Color color) {
            super(parent_node, value);
            this.color = color;
        }
        
        public Color getColor() {
            return color;
        }
        
        public void flipColor() {
            color = (color == Color.BLACK ? Color.RED : Color.BLACK);
        }
        
        private RedBlackTreeNode getUncleNode() {
            RedBlackTreeNode parent_node = (RedBlackTreeNode) getParentNode();
            if(parent_node == null) {
                return null;
            }
            return (RedBlackTreeNode) (parent_node.getLeftChild() == this ? parent_node.getRightChild() : parent_node.getLeftChild());
        }
        
        public void add(V value) {
            //int comparison = ((Comparable) getValue()).compareTo(value);
            int comparison = value.compareTo(getValue());
            if(comparison < 0) {
                if(getLeftChild() == null) {
                    setLeftChild(new RedBlackTreeNode(this, value, Color.RED));
                    // TODO: check about rebalancing tree
                    if(getColor() == Color.RED) {
                        
                    }
                }
                else {
                    ((RedBlackTreeNode) getLeftChild()).add(value);
                }
            }
            else if(comparison > 0) {
                if(getRightChild() == null) {
                    setRightChild(new RedBlackTreeNode(this, value, Color.RED));
                    // TODO: check about rebalancing tree
                }
                else {
                    ((RedBlackTreeNode) getRightChild()).add(value);
                }
            }
        }
    }
    
    @Override public void add(V value) {
        if(root_node == null) {
            root_node = new RedBlackTreeNode(null, value, Color.BLACK);
        }
        else {
            ((RedBlackTreeNode) root_node).add(value);
        }
    }
}
