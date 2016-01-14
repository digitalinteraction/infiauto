package com.infiauto.datastr.auto;

import java.io.*;
import java.util.*;
import java.util.HashMap;

/**
 * This is an implementation of the
 * <a href="http://www.itl.nist.gov/div897/sqg/dads/HTML/ahoCorasick.html">Aho-Corasick automaton</a>.
 * It is built atop a regular dictionary automaton.  The Aho-Corasick will
 * return all strings in its dictionary that contain the given sub-string.
 * @author Infinite Automata
 */
public final class AhoCorasickAutomaton
        extends DictionaryAutomaton
        implements Serializable {

    private HashMap<DictionaryAutomaton.State, DictionaryAutomaton.State> fail_transitions;
    private HashMap<DictionaryAutomaton.State, List<String>> mappings;

    public AhoCorasickAutomaton(String... words) {
        this(Arrays.asList(words));
    }

    public AhoCorasickAutomaton(List<String> word_list) {
        super(word_list);
        Automaton.State old_root = root_node;
        root_node = new Automaton.State() {

            @Override
            protected State getNextState(Object c) {
                State next_state = (State) next_states.get(c);
                return (next_state != null ? next_state : this);
            }
        };
        for (Object obj : old_root.next_states.keySet()) {
            try {
                root_node.addNextState(obj, old_root.getNextState(obj));
            } catch (Exception e) {
            }
        }

        // complete the Aho-Corasick-specific part of instantiation

        this.fail_transitions = new HashMap<DictionaryAutomaton.State, DictionaryAutomaton.State>();
        this.mappings = new HashMap<DictionaryAutomaton.State, List<String>>(word_list.size());

        DictionaryAutomaton.State root_state = getCurrentState();
        Queue<DictionaryAutomaton.State> queue = new LinkedList<DictionaryAutomaton.State>();
        for (Character c : getAlphabet()) {
            DictionaryAutomaton.State next_state = root_state.getNextState(c);
            if ((next_state != null) && (next_state != root_state)) {
                fail_transitions.put(next_state, root_state);
                queue.offer(next_state);
            }
        }

        while (!queue.isEmpty()) {
            DictionaryAutomaton.State r = queue.remove();
            for (Character a : getAlphabet()) {
                DictionaryAutomaton.State u = r.getNextState(a);
                if (u == null) {
                    continue;
                }

                // state u is accessible so enqueue it
                queue.offer(u);

                DictionaryAutomaton.State v0 = fail_transitions.get(r);
                DictionaryAutomaton.State v1 = null;
                boolean done = false;
                while (!done) {
                    v1 = v0.getNextState(a);
                    if (v1 == null) {
                        v1 = fail_transitions.get(v0);
                    } else {
                        done = true;
                    }
                    v0 = v1;
                }
                fail_transitions.put(u, v0);

                List<String> results = mappings.get(u);
                if (results == null) {
                    results = new LinkedList<String>();
                }
                if (v0.getElement() != null) {
                    results.add((String) v0.getElement());
                }
                mappings.put(u, results);
            }
        }
    }

    public List<String> matchAll(String word) {
        List<String> results = new LinkedList<String>();
        DictionaryAutomaton.State current_state = this.getCurrentState();
        DictionaryAutomaton.State next_state = null;

        for (char c : word.toCharArray()) {
            next_state = null;
            while (next_state == null) {
                next_state = current_state.getNextState(c);
                if (next_state == null) {
                    next_state = null;
                    current_state = fail_transitions.get(current_state);
                }
            }
            current_state = next_state;
            if (current_state.getElement() != null) {
                results.add((String) current_state.getElement());
            }
            if (mappings.get(current_state) != null) {
                results.addAll(mappings.get(current_state));
            }
        }

        return results;
    }

    /*
    public static void main(String[] args) {
    List<String> word_list = Arrays.asList(new String[] { "his", "hers", "he", "she" });
    AhoCorasickAutomaton automaton = new AhoCorasickAutomaton(word_list);
    List<String> matches = automaton.matchAll("she");
    }
     */
}