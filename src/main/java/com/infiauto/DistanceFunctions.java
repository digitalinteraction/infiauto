package com.infiauto;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.util.Arrays.fill;

/**
 * Collection of distance functions.
 * @author Infinite Automata
 */
public abstract class DistanceFunctions {
    /**
     * Calculates the Cartesian distance between two arrays made up of numeric values.
     * @param <K> the class extending Number comprising the input arrays
     * @param a first number array for comparison
     * @param b second number array for comparison
     * @return the Cartesian distance between two arrays of numbers
     */
    public static <K extends Number> double cartesianDistance(K[] a, K[] b) {
        assert(a.length == b.length): "Arrays must be of equal length";
        
        double distance = 0.0;
        for(int i = 0; i < a.length; i++) {
            distance += pow((b[i].doubleValue() - a[i].doubleValue()), 2.0);
        }
        return sqrt(distance);
    }
    
    /**
     * Calculates the Hamming distance between two strings of equal length.
     * @param s1 first string for comparison
     * @param s2 second string for comparison
     * @return the Hamming distance between the two provided strings
     */
    public static int hammingDistance(String s1, String s2) {
        assert(s1.length() == s2.length()) : "Input string lengths are not equal";
        
        int distance = 0;
        for(int i = 0; i < s1.length(); i++)
            if(s1.charAt(i) != s2.charAt(i))
                distance++;

        return distance;
    }
    
    /**
     * Calculates the Levenshtein distance between two arrays of objects.
     * @param <T> type of the objects that make up the input arrays
     * @param o1 first input array for comparison
     * @param o2 second input array for comparison
     * @return the Levenshtein distance between the two provided arrays
     */
    public static <T> int levenshteinDistance(T[] o1, T[] o2) {
        int[][] distance = new int[o1.length + 1][o2.length + 1];
        for(int i = 0; i < distance.length; i++) distance[i][0] = i;
        for(int j = 0; j < distance[0].length; j++) distance[0][j] = j;

        int cost = -1;
        for(int j = 1; j < distance[0].length; j++)
        {
            for(int i = 1; i < distance.length; i++)
            {
                cost = (o1[i - 1].equals(o2[j - 1]) ? 0 : 1);
                distance[i][j]
                        = min(distance[i - 1][j] + 1,
                        min(distance[i][j - 1] + 1,
                        distance[i - 1][j - 1] + cost));
            }
        }

        return distance[distance.length - 1][distance[0].length - 1];
    }

    /**
     * Calculates the Levenshtein distance between two strings.
     * @param s1 first string for comparison
     * @param s2 second string for comparison
     * @return the Levenshtein distance between the two provided strings
     */
    public static int levenshteinDistance(String s1,
            String s2) {
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
                        = min(distance[i - 1][j] + 1,
                        min(distance[i][j - 1] + 1,
                        distance[i - 1][j - 1] + cost));
            }
        }

        return distance[distance.length - 1][distance[0].length - 1];
    }

    /**
     * Calculate the Jaro distance score for the similarity of two strings.
     * @param s1 first string for comparison
     * @param s2 second string for comparison
     * @return score between 0.0 and 1.0
     */
    public static double jaroDistance(String s1, String s2) {
        double sum = 0.0;
        int matches = 0;
        int transpositions = 0;
        int furthest = max(s1.length(), s2.length()) / 2 - 1;
        boolean[] matched1 = new boolean[s1.length()];
        boolean[] matched2 = new boolean[s2.length()];
        fill(matched1, false);
        fill(matched2, false);

        // count up the matches
        for(int i = 0; i < s1.length(); i++) {
            int start = (i - furthest < 0 ? 0 : i - furthest);
            int end = (i + furthest > s2.length() ? s2.length() : i + furthest);
            for(int j = start; j < end; j++) {
                if(!matched2[j] && (s1.charAt(i) == s2.charAt(j))) {
                    matched1[i] = matched2[j] = true;
                    matches++;
                    break;
                }
            }
        }

        // count up the transpositions
        int j = 0;
        for(int i = 0; i < s1.length(); i++) {
            if(!matched1[i]) {
                continue;
            }

            while(!matched2[j]) {
                j++;
            }
            if(s1.charAt(i) != s2.charAt(j)) {
                transpositions++;
            }
            j++;
        }
        transpositions /= 2;

        // produce the final score
        sum = (double) matches / (double) s1.length()
                + (double) matches / (double) s2.length()
                + (double) (matches - transpositions) / (double) matches;
        return sum / 3.0;
    }

    /**
     * Calculate the Jaro-Winkler distance score for the similarity of two strings.
     * Jaro-Winkler differs from the Jaro distance in that it favorably considers
     * strings that have a common prefix.
     * @param prefix_length length of prefix
     * @param weight prefix weighting factor between 0.0 and 1.0
     * @param s1 first string for comparison
     * @param s2 second string for comparison
     * @return score between 0.0 and 1.0
     */
    public static double jaroWinklerDistance(int prefix_length,
            double weight,
            String s1,
            String s2) {
        // calculate the Jaro distance
        double jaro_distance = jaroDistance(s1, s2);
        // calculate the Jaro-Winkler distance
        return jaro_distance + ((double) prefix_length * weight * (1.0 - jaro_distance));
    }
}