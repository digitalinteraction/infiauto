package com.infiauto.datastr.tree;

import static java.lang.Math.max;

/**
 * Adelson-Velski and Landis (AVL) self-balancing binary tree.
 * @author Infinite Automata
 */
public class AVLTree<V extends Comparable> extends BinaryTree<V> {

    public class AVLTreeNode extends BinaryTreeNode {

        public AVLTreeNode(AVLTreeNode parent_node, V value) {
            super(parent_node, value);
        }

        /**
         * 
         * @param value
         * @param depth
         * @return 
         */
        @Override
        protected int add(V value, int depth) {
            int result = depth;
            int comparison = value.compareTo(getValue());

            if (comparison < 0) {
                if (getLeftChild() != null) {
                    result = getLeftChild().add(value, (depth + 1));
                } else {
                    AVLTreeNode child_node = new AVLTreeNode(this, value);
                    setLeftChild(child_node);
                }
            } else if (comparison > 0) {
                if (getRightChild() != null) {
                    result = getRightChild().add(value, (depth + 1));
                } else {
                    AVLTreeNode child_node = new AVLTreeNode(this, value);
                    setRightChild(child_node);
                }
            }
            setDepth(max((getLeftChild() != null ? getLeftChild().getDepth() : 0),
                    (getRightChild() != null ? getRightChild().getDepth() : 0)) + 1);

            int balance_factor = -(getLeftChild() != null ? getLeftChild().getDepth() + 1 : 0);
            balance_factor += (getRightChild() != null ? getRightChild().getDepth() + 1 : 0);
            if (balance_factor < -1) {
                if (value.compareTo(getLeftChild().getValue()) < 0) {
                    singleRotateLeft(this);
                } else {
                    doubleRotateLeft(this);
                }
            } else if (balance_factor > 1) {
                if (value.compareTo(getRightChild().getValue()) > 0) {
                    singleRotateRight(this);
                } else {
                    doubleRotateRight(this);
                }
            }

            return result;
        }

        private AVLTreeNode singleRotateLeft(AVLTreeNode node) {
            AVLTreeNode parent_node = (AVLTreeNode) node.getParentNode();
            AVLTreeNode swap_node = (AVLTreeNode) node.getLeftChild();
            node.setLeftChild(swap_node.getRightChild());
            swap_node.setRightChild(node);
            node.setDepth(max(node.getLeftChildDepth(), (node.getRightChildDepth() + 1)));
            swap_node.setDepth(max(swap_node.getLeftChildDepth(), (node.getDepth() + 1)));

            if (parent_node == null) {
                swap_node.setParentNode(parent_node);
            } else {
                if (parent_node.getLeftChild() == node) {
                    parent_node.setLeftChild(swap_node);
                } else {
                    parent_node.setRightChild(swap_node);
                }
            }

            return swap_node;
        }

        private AVLTreeNode doubleRotateLeft(AVLTreeNode node) {
            node.setLeftChild(singleRotateRight((AVLTreeNode) node.getLeftChild()));
            return singleRotateLeft(node);
        }

        private AVLTreeNode singleRotateRight(AVLTreeNode node) {
            AVLTreeNode parent_node = (AVLTreeNode) node.getParentNode();
            AVLTreeNode swap_node = (AVLTreeNode) node.getRightChild();
            node.setRightChild(swap_node.getLeftChild());
            swap_node.setLeftChild(node);
            node.setDepth(max(node.getLeftChildDepth(), (node.getRightChildDepth() + 1)));
            swap_node.setDepth(max(swap_node.getRightChildDepth(), (node.getDepth() + 1)));

            if (parent_node == null) {
                swap_node.setParentNode(parent_node);
            } else {
                if (parent_node.getLeftChild() == node) {
                    parent_node.setLeftChild(swap_node);
                } else {
                    parent_node.setRightChild(swap_node);
                }
            }

            return swap_node;
        }

        private AVLTreeNode doubleRotateRight(AVLTreeNode node) {
            node.setRightChild(singleRotateLeft((AVLTreeNode) node.getRightChild()));
            return singleRotateRight(node);
        }
    }

    /**
     * 
     * @param value 
     */
    @Override
    public void add(V value) {
        if (root_node == null) {
            root_node = new AVLTreeNode(null, value);
        } else {
            ((AVLTreeNode) root_node).add(value);
            // make sure its still root
            while (root_node.getParentNode() != null) {
                root_node = root_node.getParentNode();
            }
        }
    }
}