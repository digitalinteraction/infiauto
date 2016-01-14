package com.infiauto.datastr.tree;

import java.util.Arrays;
import java.util.TreeMap;
import java.util.Collection;

/**
 * Radix tree for space-efficient string storage.
 * @author Infinite Automata
 */
public class RadixTree
    extends Tree<String> {

    private static String findPrefix(String a, String b) {
        int shortest_length = Math.min(a.length(), b.length());
        StringBuilder result = new StringBuilder();
        for(int i = 0;
            i < shortest_length
            && (a.charAt(i) == b.charAt(i));
            i++) {
            result.append(a.charAt(i));
        }
        return result.toString();
    }

    public class RadixNode extends Node {
        private TreeMap<String,RadixNode> child_nodes;

        public RadixNode(RadixNode parent_node, String value) {
            super(parent_node, value);
            this.child_nodes = new TreeMap<String,RadixNode>();
        }

        public void add(String key, RadixNode node) {
            if(child_nodes.isEmpty()) {
                node.setParentNode(this);
                child_nodes.put(key, node);
                return;
            }
            
            String[] keys_list = child_nodes.keySet().toArray(new String[0]);
            int insertion_point = Arrays.binarySearch(keys_list, key);
            if(insertion_point > 0) {
                // key already exists
                String matching_key = keys_list[insertion_point];
                if(key.length() == matching_key.length()) {
                    child_nodes.put(matching_key, node);
                }
                else if(key.length() < matching_key.length()) {
                    String sub_key = matching_key.substring(key.length());
                    RadixNode mid_node = child_nodes.get(key);
                    node.setParentNode(mid_node);
                    mid_node.add(sub_key, node);
                }
                else if(key.length() > matching_key.length()) {
                    String sub_key = key.substring(matching_key.length());
                    RadixNode mid_node = child_nodes.get(matching_key);
                    node.setParentNode(mid_node);
                    mid_node.add(sub_key, node);
                }
            }
            else {
                insertion_point = -insertion_point - 1;
                if(insertion_point < keys_list.length) {
                    String candidate = keys_list[insertion_point];
                    String prefix = findPrefix(candidate, key);
                    if(prefix.length() > 0) {
                        if(prefix.length() == candidate.length()) {
                            RadixNode mid_node = child_nodes.get(candidate);
                            mid_node.add(key.substring(prefix.length()), node);
                        }
                        else {
                            RadixNode mid_node = new RadixNode(this, prefix);
                            
                            RadixNode child_a = child_nodes.remove(candidate);
                            String child_a_key = candidate.substring(prefix.length());
                            child_a.setValue(child_a_key);
                            child_a.setParentNode(mid_node);
                            
                            mid_node.add(child_a_key, child_a);
                            mid_node.add(key.substring(prefix.length()), node);
                            child_nodes.put(prefix, mid_node);
                        }
                    }
                    else {
                        // insert the entire key
                        node.setParentNode(this);
                        child_nodes.put(key, node);
                    }
                }
                else if (insertion_point > 0) {
                    String candidate = keys_list[insertion_point - 1];
                    String prefix = findPrefix(candidate, key);
                    if(prefix.length() > 0) {
                        if(prefix.length() == candidate.length()) {
                            RadixNode mid_node = child_nodes.get(candidate);
                            mid_node.add(key.substring(prefix.length()), node);
                        }
                        else {
                            RadixNode mid_node = new RadixNode(this, prefix);
                            
                            RadixNode child_a = child_nodes.remove(candidate);
                            String child_a_key = candidate.substring(prefix.length());
                            child_a.setValue(child_a_key);
                            child_a.setParentNode(mid_node);
                            
                            mid_node.add(child_a_key, child_a);
                            mid_node.add(key.substring(prefix.length()), node);
                            child_nodes.put(prefix, mid_node);
                        }
                    }
                    else {
                        // insert the entire key
                        node.setParentNode(this);
                        child_nodes.put(key, node);
                    }
                }
                else {
                    // insert the entire key
                    node.setParentNode(this);
                    child_nodes.put(key, node);
                }
            }
        }

        public Collection<RadixNode> getChildren() {
            return child_nodes.values();
        }
    }

    public RadixTree() {
        this.root_node = new RadixNode(null, null);
    }

    public void add(String string) {
        ((RadixNode) root_node).add(string, new RadixNode(null, string));
    }
}
