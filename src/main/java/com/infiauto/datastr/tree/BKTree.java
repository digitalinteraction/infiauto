package com.infiauto.datastr.tree;

import static com.infiauto.DistanceFunctions.levenshteinDistance;
import com.infiauto.datastr.ErrorCorrectable;
import com.infiauto.datastr.MultiMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.TreeMap;

/**
 * Burkhard-Keller tree.
 * @author Infinite Automaton
 */
public abstract class BKTree<M extends Number, E>
        extends Tree<E>
        implements ErrorCorrectable<E>, MetricTree<M, E> {

    public static final BKTree<Integer, String> LEVENSHTEIN;

    static {
        LEVENSHTEIN = new BKTree<Integer, String>() {

            @Override
            public Integer metricFunction(String s1, String s2) {
                return levenshteinDistance(s1, s2);
            }
        };
    }

    public class BKTreeNode extends Node {

        private TreeMap<M, BKTreeNode> child_nodes;

        /**
         * Construct a new node for a Burkhard-Keller tree.
         * @param user_object
         * @param parent_node
         */
        public BKTreeNode(E user_object,
                BKTreeNode parent_node) {
            super(parent_node, user_object);
            this.child_nodes = new TreeMap<M, BKTreeNode>();
        }

        /**
         *
         * @param user_object
         * @return
         */
        public boolean add(E user_object) {
            M d = metricFunction(user_object, this.getValue());

            if (d.doubleValue() == 0.0) {
                // the two user objects match exactly so there is nothing to add
                return false;
            }

            BKTreeNode child_node = child_nodes.get(d);
            if (child_node == null) {
                child_nodes.put(d,
                        new BKTreeNode(user_object, this));
                return true;
            }

            return child_node.add(user_object);
        }

        /**
         *
         * @return
         */
        public boolean isLeaf() {
            return child_nodes.isEmpty();
        }

        /**
         * Get all children of the current node.
         * @return child nodes
         */
        public Collection<? extends Node> getChildren() {
            return child_nodes.values();
        }
    }

    private class BKTreeHierarchicalVisitor extends HierarchicalVisitor<E> {

        private int distance;
        private E value;
        private MultiMap<Integer, E> result;
        private int current_distance;

        public BKTreeHierarchicalVisitor(int distance, E value) {
            this.distance = distance;
            this.value = value;
            this.result = new MultiMap<Integer, E>();
            this.current_distance = -1;
        }

        public int getDistance() {
            return distance;
        }

        public void setCurrentDistance(int current_distance) {
            this.current_distance = current_distance;
        }

        @Override
        public void visit(Tree<E>.Node node) {
            result.put(current_distance, node.getValue());
        }
    }

    /**
     *
     * @param user_object
     * @return
     */
    public boolean add(E user_object) {
        if (root_node == null) {
            root_node = new BKTreeNode(user_object, null);
            size = 1;
            return true;
        }

        if (((BKTreeNode) root_node).add(user_object) == true) {
            size++;
            return true;
        }

        return false;
    }

    /**
     * 
     * @param input
     * @param distance
     * @return 
     */
    public MultiMap<Integer, E> correctError(int distance, E input) {
        BKTreeHierarchicalVisitor visitor = new BKTreeHierarchicalVisitor(distance, input);

        if (root_node != null) {
            accept(visitor, HierarchicalTraversalOrder.BREADTH_FIRST);
        }

        return (MultiMap<Integer, E>) visitor.result;
    }

    private M recast(M original, double value) {
        M result = null;

        if (original instanceof Integer) {
            result = (M) Integer.valueOf((int) Math.round(value));
        } else if (original instanceof Long) {
            result = (M) Long.valueOf(Math.round(value));
        } else if (original instanceof Double) {
            result = (M) Double.valueOf(value);
        } else if (original instanceof Float) {
            result = (M) Float.valueOf((float) value);
        }

        return result;
    }

    /**
     * 
     * @param visitor
     * @param order 
     */
    @Override
    public void accept(HierarchicalVisitor visitor, HierarchicalTraversalOrder order) {
        LinkedList<Node> node_queue = new LinkedList<Node>(Arrays.asList(root_node));
        BKTreeHierarchicalVisitor actual_visitor = (BKTreeHierarchicalVisitor) visitor;
        BKTreeNode current_node = null;
        M raw_distance = null;
        double distance = 0;
        double low = Double.MIN_VALUE;
        double high = Double.MAX_VALUE;

        while (!node_queue.isEmpty()) {
            current_node = (BKTreeNode) node_queue.poll();

            raw_distance = metricFunction(actual_visitor.value, current_node.getValue());
            distance = raw_distance.doubleValue();
            low = distance - actual_visitor.getDistance();
            high = distance + actual_visitor.getDistance();

            if (distance <= actual_visitor.getDistance()) {
                // TODO: this needs to be recast
                actual_visitor.setCurrentDistance((int) distance);
                current_node.accept(visitor, order);
            }

            M low_recast = recast(raw_distance, low);
            M high_recast = recast(raw_distance, high);
            node_queue.addAll(current_node.child_nodes.subMap(low_recast, true, high_recast, true).values());
        }
    }
}