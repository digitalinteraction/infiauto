package com.infiauto.datastr.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;

/**
 *
 * @author InfiniteAutomaton
 */
public abstract class BKTree<M extends Number,E>
{
    private class BKTreeNode
    {
        private E user_object;
        private BKTreeNode parent_node;
        private HashMap<M,BKTreeNode> child_nodes;

        /**
         *
         * @param user_object
         * @param parent_node
         */
        public BKTreeNode(E user_object,
                BKTreeNode parent_node)
        {
            this.user_object = user_object;
            this.parent_node = parent_node;
            this.child_nodes = new HashMap<M,BKTreeNode>();
        }

        /**
         *
         * @param user_object
         * @param metric_distance
         * @return
         */
        public Collection<E> query(E user_object,
                M metric_distance)
        {
            ArrayList<E> result = new ArrayList<E>();
            M d = metric(user_object, this.user_object);
            double min = d.doubleValue() - metric_distance.doubleValue();
            double max = d.doubleValue() + metric_distance.doubleValue();

            if(d.doubleValue() <= metric_distance.doubleValue())
            {
                result.add(this.user_object);
            }

            for(M d2 : child_nodes.keySet())
            {
                if((min <= d2.doubleValue())
                        && (max >= d2.doubleValue()))
                {
                    result.addAll(child_nodes.get(d2).query(user_object, metric_distance));
                }
            }

            return result;
        }

        /**
         *
         * @param user_object
         * @return
         */
        public boolean add(E user_object)
        {
            M d = metric(user_object, this.user_object);

            if(d.doubleValue() == 0.0)
            {
                // the two user objects match exactly so there is nothing to add
                return false;
            }

            BKTreeNode child_node = child_nodes.get(d);
            if(child_node == null)
            {
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
        public boolean isLeaf()
        {
            return child_nodes.isEmpty();
        }

        /**
         * 
         * @return
         */
        public Iterator<BKTreeNode> children()
        {
            return child_nodes.values().iterator();
        }

        /**
         *
         * @return
         */
        @Override
        public String toString()
        {
            return user_object.toString();
        }

        /**
         *
         * @param depth
         */
        public void print(int depth)
        {
            for(int i = 0; i < depth; i++) System.out.print("   ");
            System.out.println(toString());
            for(BKTreeNode child_node : child_nodes.values())
                child_node.print(depth + 1);
        }
    }

    protected abstract M metric(E o1, E o2);

    private BKTreeNode root_node;
    private int node_count;

    private static int levenshteinDistance(String s1,
            String s2)
    {
        int[][] distance = new int[s1.length() + 1][s2.length() + 1];
        for(int i = 0; i < distance.length; i++) distance[i][0] = i;
        for(int j = 0; j < distance[0].length; j++) distance[0][j] = j;

        int cost = -1;
        for(int j = 1; j < distance[0].length; j++)
        {
            for(int i = 1; i < distance.length; i++)
            {
                cost = ((s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1);
                distance[i][j]
                        = Math.min(distance[i - 1][j] + 1,
                        Math.min(distance[i][j - 1] + 1,
                        distance[i - 1][j - 1] + cost));
            }
        }

        return distance[distance.length - 1][distance[0].length - 1];
    }

    /**
     *
     */
    public BKTree()
    {
        this.root_node = null;
        this.node_count = 0;
    }

    /**
     *
     * @param user_object
     * @return
     */
    public boolean add(E user_object)
    {
        if(root_node == null)
        {
            root_node = new BKTreeNode(user_object, null);
            node_count = 1;
            return true;
        }

        if(root_node.add(user_object) == true)
        {
            node_count++;
            return true;
        }

        return false;
    }

    /**
     *
     * @return
     */
    public int size()
    {
        return node_count;
    }

    /**
     *
     */
    public void print()
    {
        if(root_node != null) root_node.print(0);
    }

    /**
     *
     * @param user_object
     * @param metric_distance
     * @return
     */
    public Collection<E> query(E user_object,
            M metric_distance)
    {
        if(root_node == null) return new ArrayList<E>(0);
        return root_node.query(user_object, metric_distance);
    }

    public static void main(String[] args)
    {
        String[] words = {
            "alpha",
            "bravo",
            "charlie",
            "delta",
            "echo",
            "foxtrot",
            "golf",
            "hotel",
            "india",
            "juliet",
            "kilo",
            "lima",
            "mike",
            "november",
            "oscar",
            "papa",
            "quebec",
            "whiskey",
            "uncle",
            "victor",
            "x-ray",
            "yankee",
            "zulu",
        };
        BKTree<Integer,String> tree = new BKTree<Integer,String>() {
            protected Integer metric(String o1,
                    String o2)
            {
                int result = -1;
                result = levenshteinDistance(o1, o2);
                return new Integer(result);
            } // metric
        };

        for(String word : words)
        {
            tree.add(word);
        }
        System.out.println("Tree size: " + tree.size());
        tree.print();
        Collection<String> cs = tree.query("*l**a", new Integer(3));
        System.out.println(cs.size() + " matches");
        System.out.println(cs);
    }
}
