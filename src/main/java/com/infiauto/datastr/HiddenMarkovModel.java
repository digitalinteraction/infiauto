package com.infiauto.datastr;

import com.infiauto.*;
import java.io.*;
import java.math.*;
import java.util.*;

/**
 *
 * @author court
 */
public final class HiddenMarkovModel {

    private static class HiddenMarkovModelState {
        private Map<Character,Map.Entry<HiddenMarkovModelState,BigDecimal>> transitions;

        public HiddenMarkovModelState() {
            this.transitions = new Hashtable<Character,Map.Entry<HiddenMarkovModelState,BigDecimal>>();
        } // HiddenMarkovModelState

        public void addState(Character input,
                HiddenMarkovModelState next_state,
                BigDecimal output) {

            transitions.put(input, new AbstractMap.SimpleImmutableEntry<HiddenMarkovModelState,BigDecimal>(next_state, output));
        } // addState

        public Map.Entry<HiddenMarkovModelState,BigDecimal> getNextState(Character input) {
            return transitions.get(input);
        } // getNextState
    } // class HiddenMarkovModelState

    //private static final int ROUNDING_PRECISION = 8;
    //private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    private HiddenMarkovModelState start_state;
    private int length;

   /* private static BigDecimal divide(BigDecimal dividend,
            BigDecimal divisor) {

        return dividend.divide(divisor, ROUNDING_PRECISION, ROUNDING_MODE);
    }*/

    public HiddenMarkovModel(List<Map.Entry<String,BigInteger>> training_data) {
        this.start_state = new HiddenMarkovModelState();
        this.length = training_data.get(0).getKey().length();

        Map<Character,BigInteger> initial_counts = new Hashtable<Character,BigInteger>();
        BigInteger initial_count = BigInteger.ZERO;
        BigDecimal initial_count_final = BigDecimal.ZERO;
        List<Map<String,BigInteger>> transition_counts = new ArrayList<Map<String,BigInteger>>(length - 1);
        for(int i = 0; i < (length - 1); i++) transition_counts.add(new Hashtable<String,BigInteger>());
        BigInteger[] count_totals = new BigInteger[length - 1];
        for(int i = 0; i < count_totals.length; i++) count_totals[i] = BigInteger.ZERO;

        // add up all the transition counts
        for(Map.Entry<String,BigInteger> entry : training_data) {
            Character c = entry.getKey().charAt(0);
            BigInteger count = initial_counts.get(c);
            if(count == null) count = BigInteger.ZERO;
            count = count.add(BigInteger.ONE);
            initial_counts.put(c, count);

            for(int i = 0; i < (length - 1); i++) {
                String digraph = entry.getKey().substring(i, (i + 2));
                count = transition_counts.get(i).get(digraph);
                if(count == null) count = BigInteger.ZERO;
                count = count.add(BigInteger.ONE);
                transition_counts.get(i).put(digraph, count);
            }
        }

        // sum up all the counts for each index
        for(BigInteger count : initial_counts.values())
            initial_count = initial_count.add(count);
        initial_count_final = new BigDecimal(initial_count);
        for(int i = 0; i < (length - 1); i++)
            for(BigInteger count : transition_counts.get(i).values())
                count_totals[i] = count_totals[i].add(count);

        HiddenMarkovModelState current_state = null;
        Map.Entry<HiddenMarkovModelState,BigDecimal> next_state_temp = null;
        HiddenMarkovModelState next_state = null;
        List<Map<Character,HiddenMarkovModelState>> temp_states = new ArrayList<Map<Character,HiddenMarkovModelState>>(length - 1);
        Character c = null;

        for(int i = 0; i < (length - 1); i++)
            temp_states.add(new Hashtable<Character,HiddenMarkovModelState>());

        for(Map.Entry<String,BigInteger> entry : training_data) {
            // reset the current state to the start state
            current_state = start_state;

            for(int i = 0; i < length; i++) {
                c = entry.getKey().charAt(i);

                if(i == 0) {
                    next_state_temp = start_state.getNextState(c);
                    next_state = (next_state_temp == null
                            ? new HiddenMarkovModelState()
                            : next_state_temp.getKey());

                    current_state.addState(c,
                            next_state,
                            new BigDecimal(initial_counts.get(c)).divide(initial_count_final, 8, RoundingMode.HALF_UP));
                }
                else {
                    next_state = temp_states.get(i - 1).get(c);
                    if(next_state == null) {
                        next_state = new HiddenMarkovModelState();
                    }

                    String digraph = entry.getKey().substring((i - 1), (i + 1));
                    BigInteger count = transition_counts.get(i - 1).get(digraph);
                    current_state.addState(c,
                            next_state,
                            new BigDecimal(count).divide(new BigDecimal(count_totals[i - 1]), 8, RoundingMode.HALF_UP));

                    temp_states.get(i - 1).put(c, next_state);
                }

                current_state = next_state;
            }
        }
    } // HiddenMarkovModel(List<Map.Entry<String,BigInteger>>)

    private static void printUsage() {
        System.out.println("NGramParser -train <size> <outfile> <infile>+");
    } // printUsage

    public static void main(String[] args) {
        if(args.length == 0) {
            printUsage();
            System.exit(-1);
        }

        String flag = args[0];
        if(flag.compareToIgnoreCase("-train") == 0) {
            if(args.length < 4) {
                printUsage();
                System.exit(-1);
            }

            int size = -1;
            try {
                size = Integer.parseInt(args[1]);
            }
            catch(NumberFormatException nf_excep) {
                printUsage();
                System.exit(-1);
            }
            if(size <= 0) {
                printUsage();
                System.exit(-1);
            }

            //String out_file_name = args[2];

            String[] in_file_names = new String[args.length - 3];
            for(int i = 0; i < in_file_names.length; i++) {
                in_file_names[i] = args[i + 3];
            }

            Map<String,BigInteger> ngrams = new Hashtable<String,BigInteger>();
            NGramParser parser = new NGramParser(size);

            String last_line = null;
            // iterate through input file names
            for(String in_file_name : in_file_names) {
                try {
                    BufferedReader reader
                            = new BufferedReader(new FileReader(in_file_name));

                    String line = null;
                    while((line = reader.readLine()) != null) {
                        if(last_line != null) {
                            int start_index = last_line.length() - size - 1;
                            if(start_index >= 0) {
                                line = last_line.substring(start_index) + line;
                            }
                        }

                        ngrams = parser.parse(line.toLowerCase(), ngrams);

                        last_line = line;
                    }

                    reader.close();
                }
                catch(IOException io_excep) {
                    io_excep.printStackTrace();
                }
            }

            int MAX_NGRAMS = 400;
            int count = (MAX_NGRAMS < ngrams.size() ? MAX_NGRAMS : ngrams.size());
            List<Map.Entry<String,BigInteger>> sorted_entries
                    = new ArrayList<Map.Entry<String,BigInteger>>(ngrams.entrySet());
            Collections.sort(sorted_entries, new NGramParser.NGramCountDescendingComparator());
            sorted_entries = sorted_entries.subList(0, count);
            /*
            List<Map.Entry<String,BigInteger>> sorted_entries
                    = new ArrayList<Map.Entry<String,BigInteger>>(3);
            sorted_entries.add(new AbstractMap.SimpleImmutableEntry<String,BigInteger>("AB", BigInteger.ONE));
            sorted_entries.add(new AbstractMap.SimpleImmutableEntry<String,BigInteger>("CB", BigInteger.ONE));
            sorted_entries.add(new AbstractMap.SimpleImmutableEntry<String,BigInteger>("CC", BigInteger.ONE));
             */

            System.out.println(sorted_entries);

            //HiddenMarkovModel hmm = new HiddenMarkovModel(sorted_entries);
        }
    } // main

} // class HiddenMarkovModel
