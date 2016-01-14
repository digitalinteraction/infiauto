package com.infiauto.datastr.auto;

import java.io.*;
import static java.lang.System.err;
import java.util.*;

/**
 * Automaton class used to represent a dictionary.
 * @author Infinite Automata
 */
public class DictionaryAutomaton
        extends Automaton<Character, String>
        implements Serializable {

    private Set<Character> alphabet;

    private static Map<Character[], String> buildInitialInput(List<String> word_list) {
        HashMap<Character[], String> results = new HashMap<Character[], String>();
        for (String word : word_list) {
            Character[] path = new Character[word.length()];
            int i = 0;
            for (char c : word.toCharArray()) {
                path[i++] = c;
            }
            results.put(path, word);
        }
        return results;
    }

    {
        this.alphabet = new TreeSet<Character>();
    }

    /**
     * Construct a DictionaryAutomaton instance based on a List of Strings.
     * @param word_list
     */
    public DictionaryAutomaton(List<String> word_list) {
        super(buildInitialInput(word_list));
        for (String word : word_list) {
            for (char c : word.toCharArray()) {
                this.alphabet.add(c);
            }
        }
    }

    /**
     * Construct a DictionaryAutomaton instance based on a variable argument list of Strings.
     * @param word_list Variable argument list of Strings
     */
    public DictionaryAutomaton(String... word_list) {
        super(buildInitialInput(Arrays.asList(word_list)));
        for (String word : word_list) {
            for (char c : word.toCharArray()) {
                this.alphabet.add(c);
            }
        }
    }

    /**
     * Gets the list of all unique characters in the dictionary, which
     * is also known as the alphabet.
     * @return alphabet as a Collection of Characters
     */
    public Collection<Character> getAlphabet() {
        return alphabet;
    }

    public boolean match(String word) {
        DictionaryAutomaton.State state = getCurrentState();
        for (char c : word.toCharArray()) {
            state = state.getNextState(c);
            if (state == null) {
                return false;
            }
        }
        return state.isAccept();
    }

    private static void usage() {
        System.out.println("Usage: DictionaryAutomaton -g in_file_name out_file_name");
        System.out.println("\t-g generates a dictionary from a file where each word is a new line");
    }

    /**
     * Command-line interface for generating instances of a DictionaryAutomaton.
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            usage();
            return;
        }

        if ((args.length == 3)
                && (args[0].compareTo("-g") == 0)) {
            String in_file_name = args[1];
            String dict_file_name = args[2];

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(in_file_name)));
                List<String> words = new ArrayList<String>();
                String word = null;
                while ((word = reader.readLine()) != null) {
                    words.add(word.trim());
                }
                reader.close();

                DictionaryAutomaton dictionary = new DictionaryAutomaton(words);

                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dict_file_name));
                try {
                    oos.writeObject(dictionary);
                } finally {
                    oos.close();
                }
            } catch (IOException e) {
                err.println(e);
                return;
            }
        }
    }
}
