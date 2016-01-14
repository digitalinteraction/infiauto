package com.infiauto.datastr.tree;

/**
 * The HierarchicalTraversalOrder enumeration allows one
 * of four possible orders:
 * <ul>
 *   <li>Depth-first, pre-order.</li>
 *   <li>Depth-first, in-order.</li>
 *   <li>Depth-first, post-order.</li>
 *   <li>Breadth-first (level-order).</li>
 * </ul>
 * @author Infinite Automata
 */
public enum HierarchicalTraversalOrder {
    DEPTH_FIRST_PREORDER,
    DEPTH_FIRST_INORDER,
    DEPTH_FIRST_POSTORDER,
    BREADTH_FIRST;
}
