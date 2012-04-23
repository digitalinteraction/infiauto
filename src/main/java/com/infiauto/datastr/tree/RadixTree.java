package com.infiauto.datastr.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.Collection;

/**
 *
 * @author Infinite Automata
 */
public class RadixTree
    extends Tree<String,String> {

    private static String prefix(String a, String b) {
        String result = "";
        for(int i = 0;
            (i < a.length())
            && (i < b.length())
            && (a.charAt(i) == b.charAt(i));
            i++) {
            result += a.charAt(i);
        }
        return result;
    }

    public class RadixNode extends Node {
        private TreeMap<String,RadixNode> child_nodes;

        public RadixNode(RadixNode parent_node, String value) {
            super(parent_node, value);
            this.child_nodes = new TreeMap<String,RadixNode>();
        }

        public void add(String key, RadixNode node) {
            String[] keys_list = child_nodes.keySet().toArray(new String[0]);
            int insertion_point = Arrays.binarySearch(keys_list, key);
            if(insertion_point > 0) {
                // TODO: key already exists
            }
            else {
                insertion_point = -insertion_point + 1;
                String candidate = keys_list[insertion_point];
                String prefix = prefix(candidate, key);
                if(prefix.length() == 0) {
                    // insert the entire key
                    child_nodes.put(key, node);
                }
                else {
                    if(candidate.equals(prefix)) {

                    }
                    else {

                    }
                }
                // TODO: find common substring
            }
        }

        public Collection<Node> getChildren() {
            return null;//child_nodes;
        }
    }

    public RadixTree() {
        this.root_node = new RadixNode(null, null);
    }

    public void add(String string) {
        
    }
}
