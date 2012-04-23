package com.infiauto;

import java.io.*;
import java.math.*;
import java.util.*;
import com.infiauto.datastr.*;

/**
 *
 * @author Courtney Falk
 */
public class NGramParser {

    /*
    public class NGram
        implements Comparable<NGram> {

        private String ngram;
        private BigInteger count;
        
        public NGram(String ngram) {
            this.ngram = ngram;
            this.count = BigInteger.ONE;
        } // NGram

        public String getString() {
            return ngram;
        } // getString

        public BigInteger getCount() {
            return count;
        } // getCount
        
        public void incrementCount() {
            count = count.add(BigInteger.ONE);
        } // incrementCount

        public int compareTo(NGram obj) {
            return ngram.compareTo(obj.ngram);
        } // compareTo
    } // class NGram

    public class NGramStringComparator
        implements Comparator<NGram> {

        public int compare(NGram a, NGram b) {
            return -a.getString().compareTo(b.getString());
        } // compare

        public boolean equals(Object obj) {
            return (obj instanceof NGramStringComparator);
        } // equals
    } // class NGramStringComparator

    public class NGramCountComparator
        implements Comparator<NGram> {

        public int compare(NGram a, NGram b) {
            return a.getCount().compareTo(b.getCount());
        } // compare

        public boolean equals(Object obj) {
            return (obj instanceof NGramCountComparator);
        } // equals
    } // class NGramCountComparator
     */

    public static class NGramCountDescendingComparator
            implements Comparator<Map.Entry<String,BigInteger>> {

        public int compare(Map.Entry<String,BigInteger> a,
                Map.Entry<String,BigInteger> b) {

//            return -a.getValue().compareTo(b.getValue());
            return a.getKey().compareTo(b.getKey());
        } // compare

        public boolean equals(Object obj) {
            return (obj instanceof NGramCountDescendingComparator);
        } // equals
    } // class NGramCountDescendingComparator

    //private static final int MAX_NGRAMS = 400;

    private int length;
    private CircularBuffer<Character> buffer;

    public NGramParser(int length) {
        this.length = length;
        this.buffer = new CircularBuffer<Character>(length);
    } // NGramParser

    public Map<String,BigInteger> parse(String corpus) {
        Map<String,BigInteger> ngrams = new Hashtable<String,BigInteger>();
//        CircularBuffer<Character> buffer = new CircularBuffer<Character>(length);
        buffer.reset();
        int index = 0;
        boolean add = false;
        char c = '\0';
        String s = null;
        BigInteger n = null;
        Character[] a = new Character[length];

        while(index < corpus.length()) {
            c = corpus.charAt(index);

            if(Character.isLetter(c)) {
                buffer.add(c);

                if(add) {
                    s = "";
                    buffer.toArray(a);
                    for(char x : a) s += x;

                    n = ngrams.get(s);
                    if(n == null) {
                        n = BigInteger.ONE;
                    }
                    else {
                        n = n.add(BigInteger.ONE);
                    }
                    ngrams.put(s, n);
                }
                else {
                    if(buffer.size() == (length - 1)) {
                        add = true;
                    }
                }

                index++;
            }
            else if(Character.isWhitespace(c)) {
                buffer.add(' ');

                if(add) {
                    s = "";
                    buffer.toArray(a);
                    for(char x : a) s += x;

                    n = ngrams.get(s);
                    if(n == null) {
                        n = BigInteger.ONE;
                    }
                    else {
                        n = n.add(BigInteger.ONE);
                    }
                    ngrams.put(s, n);
                }
                else {
                    if(buffer.size() == (length - 1)) {
                        add = true;
                    }
                }

                // condense whitespace
                while((index < corpus.length()) && Character.isWhitespace(corpus.charAt(++index)));
            }
            // ignore anything that isn't a letter or whitespace
            else index++;
        }

        return ngrams;
    } // parse(String)

    public Map<String,BigInteger> parse(String corpus, Map<String,BigInteger> ngrams) {
//        CircularBuffer<Character> buffer = new CircularBuffer<Character>(length);
        buffer.reset();
        int index = 0;
        boolean add = false;
        char c = '\0';
        String s = null;
        BigInteger n = null;
        Character[] a = new Character[length];

        while(index < corpus.length()) {
            c = corpus.charAt(index);

            if(Character.isLetter(c)) {
                buffer.add(c);

                if(add) {
                    s = "";
                    buffer.toArray(a);
                    for(char x : a) s += x;

                    n = ngrams.get(s);
                    if(n == null) {
                        n = BigInteger.ONE;
                    }
                    else {
                        n = n.add(BigInteger.ONE);
                    }
                    ngrams.put(s, n);
                }
                else {
                    if(buffer.size() == (length - 1)) {
                        add = true;
                    }
                }

                index++;
            }
            else if(Character.isWhitespace(c)) {
                buffer.add(' ');

                if(add) {
                    s = "";
                    buffer.toArray(a);
                    for(char x : a) s += x;

                    n = ngrams.get(s);
                    if(n == null) {
                        n = BigInteger.ONE;
                    }
                    else {
                        n = n.add(BigInteger.ONE);
                    }
                    ngrams.put(s, n);
                }
                else {
                    if(buffer.size() == (length - 1)) {
                        add = true;
                    }
                }

                // condense whitespace
                while((index < corpus.length()) && Character.isWhitespace(corpus.charAt(++index)));
            }
            // ignore anything that isn't a letter or whitespace
            else index++;
        }

        return ngrams;
    } // parse(String,Map<String,BigInteger>)

    private static void printUsage() {
        System.out.println("NGramParser -train <size> <outfile> <infile>+");
    } // printUsage

    public static void main(String[] args) {
        /*
        NGramParser parser = new NGramParser(3);
        BufferedReader in = new BufferedReader(new FileReader(args[0]));
        String s = "";
        String line = null;
        while((line = in.readLine()) != null) s += line;
        in.close();
        Map<String,BigInteger> results = parser.parse(s);
         */

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

        //    String out_file_name = args[2];

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

                        ngrams = parser.parse(line, ngrams);

                        last_line = line;
                    }

                    reader.close();
                }
                catch(IOException io_excep) {
                    io_excep.printStackTrace();
                }
            }

        //    int count = 0;
            List<Map.Entry<String,BigInteger>> sorted_entries
                    = new ArrayList<Map.Entry<String,BigInteger>>(ngrams.entrySet());
            Collections.sort(sorted_entries, new NGramCountDescendingComparator());
        }
    } // main

} // class NGramParser