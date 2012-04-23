package com.infiauto.datastr.tree;

import java.util.*;

/**
 *
 * @author Infinite Automata
 */
public abstract class Tree<K,V>
{
    public abstract class Node
    {
        private Node parent_node;
        private V value;

        protected Node(Node parent_node, V value) {
            this.parent_node = parent_node;
        }

        public Node getParentNode() {
            return parent_node;
        }

        public V getValue() {
            return value;
        }

        public abstract Collection<Node> getChildren();
    }

    protected Node root_node;
}
