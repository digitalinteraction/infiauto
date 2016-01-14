package com.infiauto.datastr.tree;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Base tree class and node sub-class for building extended tree types.
 * @author Infinite Automata
 */
public abstract class Tree<V> implements HierarchicalVisitable, Serializable {

    public abstract class Node implements HierarchicalVisitable<V> {

        private Node parent_node;
        private V value;
        private int depth;

        protected Node(Node parent_node, V value) {
            this.parent_node = parent_node;
            this.value = value;
            this.depth = 0;
        }
        
        public void setParentNode(Node parent_node) {
            this.parent_node = parent_node;
        }

        public Node getParentNode() {
            return parent_node;
        }

        public V getValue() {
            return value;
        }
        
        public int getDepth() {
            return depth;
        }
        
        public void setValue(V value) {
            this.value = value;
        }
        
        public void setDepth(int depth) {
            this.depth = depth;
        }
        
        public boolean isLeafNode() {
            return getChildren().isEmpty();
        }
        
        public void accept(HierarchicalVisitor<V> visitor, HierarchicalTraversalOrder order) {
            visitor.visit(this);
        }
        
        public abstract Collection<? extends Node> getChildren();
    }
    
    {
        this.root_node = null;
        this.height = 0;
        this.size = 0;
    }
    
    protected Node root_node;
    protected int height;
    protected int size;

    /**
     * 
     * @return 
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * 
     * @return 
     */
    public int size() {
        return size;
    }
    
    /**
     * 
     * @return 
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * Implements the accept method of the HierarchicalVisitable interface.
     */
    public void accept(HierarchicalVisitor visitor, HierarchicalTraversalOrder order) {
        if(order == HierarchicalTraversalOrder.BREADTH_FIRST) {
            breadthFirst(visitor);
        }
        else {
            depthFirst(visitor, order, root_node, 0);
        }
    }
    
    private void breadthFirst(HierarchicalVisitor visitor) {
        // build up the queue of nodes to visit in breadth-first order
        LinkedList<Node> node_queue = new LinkedList<Node>(Arrays.asList(root_node));
        Node current_node = null;
        Collection<? extends Node> child_nodes = null;
        while(!node_queue.isEmpty()) {
            // get the next node
            current_node = node_queue.poll();
            
            // add all the current node's children to the queue
            child_nodes = current_node.getChildren();
            node_queue.addAll(child_nodes);
            
            // visit the current node
            current_node.accept(visitor, HierarchicalTraversalOrder.BREADTH_FIRST);
        }
    }
    
    protected void depthFirst(HierarchicalVisitor visitor, HierarchicalTraversalOrder order, Node node, int depth) {
        Collection<? extends Node> child_nodes = node.getChildren();
        
        if(order == HierarchicalTraversalOrder.DEPTH_FIRST_PREORDER) {
            node.accept(visitor, order);
            
            for(Node child_node : child_nodes) {
                depthFirst(visitor, order, child_node, (depth + 1));
            }
        }
        else if(order == HierarchicalTraversalOrder.DEPTH_FIRST_INORDER) {
            if(child_nodes.isEmpty()) {
                node.accept(visitor, order);
                return;
            }
            
            int midpoint = child_nodes.size() / 2;
            int index = 0;
            for(Node child_node : child_nodes) {
                if(index++ == midpoint) {
                    node.accept(visitor, order);
                }
                
                depthFirst(visitor, order, child_node, (depth + 1));
            }
        }
        else if(order == HierarchicalTraversalOrder.DEPTH_FIRST_POSTORDER) {
            for(Node child_node : child_nodes) {
                depthFirst(visitor, order, child_node, (depth + 1));
            }
            
            node.accept(visitor, order);
        }
    }
}