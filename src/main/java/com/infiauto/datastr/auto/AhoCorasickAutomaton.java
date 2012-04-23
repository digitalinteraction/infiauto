package com.infiauto.datastr.auto;

import java.io.*;
import java.util.*;
import java.util.HashMap;

/**
 * This is an implementation of the <a
 * href="http://www.itl.nist.gov/div897/sqg/dads/HTML/ahoCorasick.html"
 * >Aho-Corasick automaton</a>. It is built atop a regular dictionary automaton.
 * The Aho-Corasick will return all strings in its dictionary that contain the
 * given sub-string.
 * 
 * @author Infinite Automata
 */
public final class AhoCorasickAutomaton extends DictionaryAutomaton implements
		Serializable {
	private static final long serialVersionUID = 5391864836014666019L;
	private HashMap<State<Character>, State<Character>> fail_transitions;
	private HashMap<State<Character>, List<String>> mappings;

	public AhoCorasickAutomaton(String... words) {
		this(Arrays.asList(words));
	}

	public AhoCorasickAutomaton(List<String> word_list) {
		super(word_list);
		State<Character> old_root = root_node;
		root_node = new State<Character>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected State<Character> getNextState(Character c) {
				State<Character> next_state = next_states.get(c);
				return (next_state != null ? next_state : this);
			}
		};

		for (Character c : old_root.next_states.keySet()) {
			root_node.addNextState(c, old_root.getNextState(c));
		}

		// complete the Aho-Corasick-specific part of instantiation
		this.fail_transitions = new HashMap<State<Character>, State<Character>>();
		this.mappings = new HashMap<State<Character>, List<String>>(
				word_list.size());

		State<Character> root_state = getCurrentState();
		Queue<State<Character>> queue = new LinkedList<State<Character>>();
		for (Character c : getAlphabet()) {

			State<Character> next_state = root_state.getNextState(c);
			if (next_state != null && next_state != root_state) {
				fail_transitions.put(next_state, root_state);
				queue.offer(next_state);
			}
		}

		while (!queue.isEmpty()) {
			State<Character> r = queue.remove();
			for (Character a : getAlphabet()) {
				State<Character> u = r.getNextState(a);
				if (u == null)
					continue;

				// state u is accessible so enqueue it
				queue.offer(u);

				State<Character> v = fail_transitions.get(r);
				boolean done = false;
				while (!done) {
					v = v.getNextState(a);
					if (v != null)
						done = true;
					else
						v = fail_transitions.get(v);
				}
				fail_transitions.put(u, v);

				List<String> results = mappings.get(u);
				if (results == null)
					results = new LinkedList<String>();
				if (v.getElement() != null)
					results.add((String) v.getElement());
				mappings.put(u, results);
			}
		}
	}

	public List<String> matchAll(String word) {
		List<String> results = new LinkedList<String>();
		State<Character> current_state = this.getCurrentState();
		State<Character> next_state = null;

		for (char c : word.toCharArray()) {
			next_state = null;
			while (next_state == null) {
					next_state = current_state.getNextState(c);
					if (next_state == null)
						current_state = fail_transitions.get(current_state);
				}
			
			current_state = next_state;
			if (current_state.getElement() != null)
				results.add((String) current_state.getElement());
			if (mappings.get(current_state) != null)
				results.addAll(mappings.get(current_state));
		}

		return results;
	}

	/*
	 * public static void main(String[] args) { List<String> word_list =
	 * Arrays.asList(new String[] { "his", "hers", "he", "she" });
	 * AhoCorasickAutomaton automaton = new AhoCorasickAutomaton(word_list);
	 * List<String> matches = automaton.matchAll("she"); }
	 */
}