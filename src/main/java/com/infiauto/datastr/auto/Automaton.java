package com.infiauto.datastr.auto;

import java.io.Serializable;
import java.util.*;

/**
 * Generalized class for representing automata.
 * @author Infinite Automata
 */
public abstract class Automaton<INTYPE, ELEMTYPE>
        implements Serializable {

    /**
     * Atomic state that forms the basic building block of the automaton.
     * @param <TP> Type of key for state transitions.
     */
    protected class State<TP>
            implements Serializable {

        private ELEMTYPE element;
        protected Map<TP, State> next_states;
        private String to_string;

        protected State() {
            this(null);
        }

        protected State(ELEMTYPE element) {
            this.element = element;
            this.next_states = new HashMap<TP, State>();
        }

        protected State(ELEMTYPE element, String to_string) {
            this.element = element;
            this.next_states = new HashMap<TP, State>();
            this.to_string = to_string;
        }

        public State<TP> addNextState(TP t, State<TP> next_state) {
            if (next_states.containsKey(t)) {
                return next_states.get(t);
            }
            next_states.put(t, next_state);
            return next_state;
        }

        public int getNextStateCount() {
            return next_states.size();
        }

        /**
         * Add a next state with the specified transition.
         * @param t Transition to the next state.
         * @return New state associated with the specified transition.
         */
        public State<TP> addNextState(TP t) {
            return addNextState(t, new State<TP>());
        }

        protected State<TP> getNextState(TP t) {
            return next_states.get(t);
        }

        /**
         * Determine if the state is an output accepting state.
         * @return True if the state is an accepting state,
         * false otherwise.
         */
        public boolean isAccept() {
            return element != null;
        }

        /**
         * Get the element stored in this state.
         * @return Element stored in this state.
         */
        public ELEMTYPE getElement() {
            return element;
        }

        protected void setElement(ELEMTYPE element) {
            this.element = element;
        }
        
        @Override
        public String toString() {
            if(to_string == null) {
                return super.toString();
            }
            return to_string;
        }
    }

    /**
     * Get the current state of the automaton.
     * @return Automaton's current state.
     */
    public State<INTYPE> getCurrentState() {
        return root_node;
    }
    protected State root_node;
    private int state_count;

    /**
     * Initialize an empty Automaton.
     */
    protected Automaton() {
        this.root_node = new State();
        this.state_count = 0;
    }

    /**
     * Initialize an Automaton with an initial set of input.
     * @param initial_input mapping of paths to output elements
     * @see Map
     */
    public Automaton(Map<INTYPE[], ELEMTYPE> initial_input) {
        this();

        for (INTYPE[] input_string : initial_input.keySet()) {
            State current_node = root_node;
            for (INTYPE input_symbol : input_string) {
                State next_node = null;
                next_node = current_node.getNextState(input_symbol);

                if (next_node == null) {
                    next_node = current_node.addNextState(input_symbol);
                    this.state_count++;
                }
                current_node = next_node;
            }
            current_node.setElement(initial_input.get(input_string));
        }
    }

    /**
     * Get the number of states in the Automaton.
     * @return number of states in the Automaton
     */
    public int getStateCount() {
        return state_count;
    }
}