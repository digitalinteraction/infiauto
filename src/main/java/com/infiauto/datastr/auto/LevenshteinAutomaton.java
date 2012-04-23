package com.infiauto.datastr.auto;

import com.infiauto.datastr.FixedLengthBitSet;
import java.io.*;
import java.util.*;

/**
 * Class for calculating a Levenshtein distance by using automata.
 * @author Infinite Automata
 */
public final class LevenshteinAutomaton
        extends Automaton<FixedLengthBitSet, Boolean>
        implements Serializable {

	private static final long serialVersionUID = 2278879493224424765L;

	private enum Parameter {
        I, M;
    }

    private enum Type {
        USUAL, T, MS;
    }

    private static class Position
            implements Comparable<Position> {

        private Parameter parameter;
        private Type type;
        private int index;
        private int error;

        public Position(Parameter parameter,
                Type type,
                int index,
                int error) {
            this.parameter = parameter;
            this.type = type;
            this.index = index;
            this.error = error;
        } // Position

        public Parameter getParameter() {
            return parameter;
        } // getParameter

        public Type getType() {
            return type;
        } // getType

        public int getIndex() {
            return index;
        } // getIndex

        public int getError() {
            return error;
        } // getError

        public int compareTo(Position p) {
            if ((p.getParameter() == parameter)
                    && (p.getType() == type)
                    && (p.getIndex() == index)
                    && (p.getError() == error)) {
                return 0;
            }
            if (parameter != p.getParameter()) {
                return (parameter == Parameter.I ? -1 : 1);
            }
            if (type != p.getType()) {
                return (type == Type.USUAL ? -1 : 1);
            }
            if (index < p.getIndex()) {
                return -1;
            } else if (index > p.getIndex()) {
                return 1;
            }
            if (error < p.getError()) {
                return -1;
            } else if (error > p.getError()) {
                return 1;
            }
            return 0;
        } // compareTo

        @Override
        public boolean equals(Object o) {
            if (o instanceof Position) {
                return compareTo((Position) o) == 0;
            }
            return false;
        } // equals

        @Override
        public int hashCode() {
            return getParameter().hashCode()
                    ^ getType().hashCode()
                    ^ (getIndex() * 0x00010000)
                    ^ getError();
        } // hashCode
    } // class Position

    private static class PositionState
            extends TreeSet<Position>
            implements Comparable<PositionState> {

		private static final long serialVersionUID = -2766442001215571923L;

		public PositionState() {
            super();
        } // PositionState()

        public PositionState(Collection<? extends Position> c) {
            super(c);
        } // PositionState(Collection<? extends Position>)

        public boolean contains(Position p) {
            for (Position t : this) {
                if (t.equals(p)) {
                    return true;
                }
            }
            return false;
        } // contains

        @Override
        public boolean equals(Object o) {
            if (o instanceof PositionState) {
                return equals((PositionState) o);
            }
            return false;
        } // equals(Object)

        @Override
        public int hashCode() {
            int hash = 0;
            for (Position p : this) {
                hash ^= p.hashCode();
            }
            return hash;
        } // hashCode

        public boolean equals(PositionState s) {
            if (s.size() != size()) {
                return false;
            }
            for (Position p : s) {
                if (!this.contains(p)) {
                    return false;
                }
            }
            return true;
        } // equals(PositionState)

        public int compareTo(PositionState s) {
            if (size() < s.size()) {
                return -1;
            } else if (size() > s.size()) {
                return 1;
            }
            if (equals(s)) {
                return 0;
            }
            return -1;
        } // compareTo
    } // class PositionState

    private static class Point {

        private Type type;
        private int x;
        private int y;

        public Point(Type type,
                int x,
                int y) {
            this.type = type;
            this.x = x;
            this.y = y;
        } // Point

        public Type getType() {
            return type;
        } // getType

        public int getX() {
            return x;
        } // getX

        public int getY() {
            return y;
        } // getY

        public boolean equals(Point p) {
            return (p.getType() == type)
                    && (p.getX() == x)
                    && (p.getY() == y);
        } // equals
    } // class Point

    private static class SetOfPoints
            extends HashSet<Point> {

		private static final long serialVersionUID = -7999018882937107435L;

		SetOfPoints() {
            super();
        } // SetOfPoints()

        SetOfPoints(Collection<? extends Point> c) {
            super(c);
        } // SetOfPoints(Collection<? extends Point>)
    } // class SetOfPoints

    private class Transition {

        private PositionState from;
        private FixedLengthBitSet b;
        private PositionState to;

        public Transition(PositionState from,
                FixedLengthBitSet b,
                PositionState to) {
            this.from = from;
            this.b = b;
            this.to = to;
        }

        public PositionState getFrom() {
            return from;
        }

        public FixedLengthBitSet getString() {
            return b;
        }

        public PositionState getTo() {
            return to;
        }
    }

    private enum ChiType {

        EPSILON, T, MS;
    } // enum ChiType
    private static final char NONALPHABET_CHARACTER = '$';
    private static final PositionState START_STATE = new PositionState(Arrays.asList(new Position[]{new Position(Parameter.I, Type.USUAL, 0, 0)}));
    private static final ChiType DEFAULT_CHI = ChiType.EPSILON;
    private int edit_distance;
    private ChiType chi;

    /**
     * Generate a power set of binary set of the specified length.
     * @param len length of the longest binary set
     * @return List of boolean arrays representing binary sets
     */
    private static ArrayList<FixedLengthBitSet> buildPowerSet(int len) {
        int max = new Double(Math.pow(2.0, (double) len)).intValue();
        ArrayList<FixedLengthBitSet> result = new ArrayList<FixedLengthBitSet>(max);
        FixedLengthBitSet temp = new FixedLengthBitSet(len);

        // Add the all-zero bit set
        result.add(temp);

        // Build the rest of the similar length bit sets
        for (int i = 0; i < max - 1; i++) {
            temp = new FixedLengthBitSet(len);
            for (int j = 0; j < len; j++) {
                temp.set(j, result.get(i).get(j));
            }
            int j = len - 1;
            while (j >= 0) {
                temp.flip(j);
                if (!temp.get(j)) {
                    j--;
                } else {
                    break;
                }
            }
            result.add(temp);
        }

        // Recursively call this method with successively shorter lengths
        if (len > 1) {
            result.addAll(buildPowerSet(len - 1));
        }

        return result;
    }

    private static Position functionRM(PositionState state) {
        Position rm = null;
        for (Position pi : state) {
            if (pi.getType() == Type.USUAL) {
                rm = pi;
            }
        }
        for (Position pi : state) {
            if ((pi.getType() == Type.USUAL) && ((pi.getIndex() - pi.getError()) > (rm.getIndex() - rm.getError()))) {
                rm = pi;
            }
        }
        return rm;
    }

    private static PositionState functionM(int edit_distance, PositionState state, int string_length) {
        PositionState m = new PositionState();
        for (Position pi : state) {
            if (pi.getParameter() == Parameter.I) {
                m.add(new Position(Parameter.M, pi.getType(), pi.getIndex() + edit_distance + 1 - string_length, pi.getError()));
            } else {
                m.add(new Position(Parameter.I, pi.getType(), pi.getIndex() - edit_distance - 1 + string_length, pi.getError()));
            }
        }
        return m;
    }

    private static boolean functionF(int edit_distance, Position pos, int string_length) {
        if (pos == null) {
            return false;
        }
        if (pos.getParameter() == Parameter.I) {
            return ((string_length <= (2 * edit_distance + 1)) && (pos.getError() <= (pos.getIndex() + 2 * edit_distance + 1 - string_length)));
        }
        return (pos.getError() > (pos.getIndex() + edit_distance));
    }

    private static SetOfPoints deltaED(ChiType chi,
            int edit_distance,
            Point point,
            FixedLengthBitSet b) {
        int index = point.getX();
        int error = point.getY();

        switch (chi) {
            case EPSILON:

                // zero-length binary string
                if (b.fixedLength() == 0) {
                    if (error < edit_distance) {
                        return new SetOfPoints(Arrays.asList(new Point[]{new Point(Type.USUAL, index, error + 1)}));
                    }
                    return new SetOfPoints();
                }

                // first bit in the binary string is true
                if (b.get(0)) {
                    return new SetOfPoints(Arrays.asList(new Point[]{new Point(Type.USUAL, index + 1, error)}));
                }

                // binary string of length one
                if (b.fixedLength() == 1) {
                    if (error < edit_distance) {
                        return new SetOfPoints(Arrays.asList(new Point[]{new Point(Type.USUAL, index, error + 1),
                                    new Point(Type.USUAL, index + 1, error + 1)}));
                    }
                    return new SetOfPoints();
                }

                // find the first true bit in the binary string
                int first_one = 0;
                for (int i = 1; i < b.fixedLength(); i++) {
                    if (b.get(i)) {
                        first_one = i;
                        break;
                    }
                }

                if (first_one == 0) {
                    // no true bit was found
                    return new SetOfPoints(Arrays.asList(new Point[]{new Point(Type.USUAL, index, error + 1),
                                new Point(Type.USUAL, index + 1, error + 1)}));
                }

                // found at least one true bit
                first_one++;
                return new SetOfPoints(Arrays.asList(new Point[]{new Point(Type.USUAL, index, error + 1),
                            new Point(Type.USUAL, index + 1, error + 1),
                            new Point(Type.USUAL, index + first_one, error + first_one - 1)}));

            case T:

                if (point.getType() == Type.T) {
                    if (b.get(0)) {
                        return new SetOfPoints(Arrays.asList(new Point[]{new Point(Type.USUAL, index + 2, error)}));
                    }
                    return new SetOfPoints();
                }

                if (b.fixedLength() == 0) {
                    if (error < edit_distance) {
                        return new SetOfPoints(Arrays.asList(new Point[]{new Point(Type.USUAL, index, error + 1)}));
                    }
                    return new SetOfPoints();
                }

                if (b.get(0)) {
                    return new SetOfPoints(Arrays.asList(new Point[]{new Point(Type.USUAL, index + 1, error)}));
                }

                if (b.fixedLength() == 1) {
                    if (error < edit_distance) {
                        return new SetOfPoints(Arrays.asList(new Point[]{new Point(Type.USUAL, index, error + 1),
                                    new Point(Type.USUAL, index + 1, error + 1)}));
                    }
                    return new SetOfPoints();
                }

                if (b.get(1)) {
                    return new SetOfPoints(Arrays.asList(new Point[]{new Point(Type.USUAL, index, error + 1),
                                new Point(Type.USUAL, index + 1, error + 1),
                                new Point(Type.USUAL, index + 2, error + 1),
                                new Point(Type.T, index, error + 1)}));
                }

                // find the first true bit in the binary string
                first_one = 0;
                for (int i = 1; i < b.fixedLength(); i++) {
                    if (b.get(i)) {
                        first_one = i;
                        break;
                    }
                }

                if (first_one == 0) {
                    return new SetOfPoints(Arrays.asList(new Point[]{new Point(Type.USUAL, index, error + 1),
                                new Point(Type.USUAL, index + 1, error + 1)}));
                }

                return new SetOfPoints(Arrays.asList(new Point[]{new Point(Type.USUAL, index, error + 1),
                            new Point(Type.USUAL, index + 1, error + 1),
                            new Point(Type.USUAL, index + first_one, error + first_one - 1)}));

            case MS:

                if (point.getType() == Type.MS) {
                    return new SetOfPoints(Arrays.asList(new Point[]{new Point(Type.USUAL, index + 1, error)}));
                }

                if (b.fixedLength() == 0) {
                    if (error < edit_distance) {
                        return new SetOfPoints(Arrays.asList(new Point[]{new Point(Type.USUAL, index, error + 1)}));
                    }
                    return new SetOfPoints();
                }

                if (b.fixedLength() == 1) {
                    if (error < edit_distance) {
                        return new SetOfPoints(Arrays.asList(new Point[]{new Point(Type.USUAL, index, error + 1),
                                    new Point(Type.USUAL, index + 1, error + 1),
                                    new Point(Type.MS, index, error + 1)}));
                    }
                }

                return new SetOfPoints(Arrays.asList(new Point[]{new Point(Type.USUAL, index, error + 1),
                            new Point(Type.USUAL, index + 1, error + 1),
                            new Point(Type.USUAL, index + 2, error + 1),
                            new Point(Type.MS, index, error + 1)}));
        }

        return null;
    }

    private static FixedLengthBitSet functionR(int edit_distance,
            Position pos,
            FixedLengthBitSet b) {
        int length = -1;

        if (pos.getParameter() == Parameter.I) {
            if ((edit_distance - pos.getError() + 1) < (b.fixedLength() - edit_distance - pos.getIndex())) {
                length = edit_distance - pos.getError() + 1;
            } else {
                length = b.fixedLength() - edit_distance - pos.getIndex();
            }

            FixedLengthBitSet result = new FixedLengthBitSet(length);
            int start = edit_distance + pos.getIndex();
            for (int i = 0; i < length; i++) {
                result.set(i, b.get(start + i));
            }
            return result;
        }

        if ((edit_distance - pos.getError() + 1) < -pos.getIndex()) {
            length = edit_distance - pos.getError() + 1;
        } else {
            length = -pos.getIndex();
        }

        FixedLengthBitSet result = new FixedLengthBitSet(length);
        int start = b.fixedLength() + pos.getIndex();
        for (int i = 0; i < length; i++) {
            result.set(i, b.get(start + i));
        }
        return result;
    }

    private static PositionState deltaE(ChiType chi,
            int edit_distance,
            Position q,
            FixedLengthBitSet b) {
        FixedLengthBitSet h = functionR(edit_distance, q, b);
        SetOfPoints delta_ed = deltaED(chi, edit_distance, new Point(q.getType(), q.getIndex(), q.getError()), h);

        if (delta_ed.isEmpty()) {
            return new PositionState();
        }

        PositionState state = new PositionState();

        if (q.getParameter() == Parameter.I) {
            for (Point pi : delta_ed) {
                state.add(new Position(Parameter.I, pi.getType(), pi.getX() - 1, pi.getY()));
            }
        } else {
            for (Point pi : delta_ed) {
                state.add(new Position(Parameter.M, pi.getType(), pi.getX(), pi.getY()));
            }
        }

        return state;
    }

    private static PositionState delta(ChiType chi, int edit_distance, PositionState state, FixedLengthBitSet b) {
        PositionState next_state = new PositionState();
        boolean add = false;

        for (Position q : state) {
            PositionState delta_e = deltaE(chi, edit_distance, q, b);

            if (!delta_e.isEmpty()) {
                for (Position pi : delta_e) {
                    // initialize the add flag to true
                    add = true;

                    Iterator<Position> iter = next_state.iterator();
                    while (iter.hasNext()) {
                        Position p = iter.next();

                        if (lessThanSubsume(pi, p)) {
                            iter.remove();
                        } else {
                            if (p.equals(pi) || lessThanSubsume(p, pi)) {
                                add = false;
                                break;
                            }
                        }
                    }

                    if (add == true) {
                        next_state.add(pi);
                    }
                }
            }
        }

        if (functionF(edit_distance, functionRM(next_state), b.fixedLength())) {
            next_state = functionM(edit_distance, next_state, b.fixedLength());
        }

        return next_state;
    }

    private static boolean lessThanSubsume(Position q1, Position q2) {
        if ((q1.getType() != Type.USUAL) || (q2.getError() <= q1.getError())) {
            return false;
        }

        int m = -1;
        if (q2.getType() == Type.T) {
            m = q2.getIndex() + 1 - q1.getIndex();
        } else {
            m = q2.getIndex() - q1.getIndex();
        }
        // make sure that m is positive
        if (m < 0) {
            m = -m;
        }

        return (m <= (q2.getError() - q1.getError()));
    }

    private static boolean coversAllPositions(int edit_distance, int string_length, PositionState state) {
        Position pos = state.first();

        if (pos.getParameter() == Parameter.I) {
            if (state.equals(START_STATE)) {
                return (string_length >= (pos.getIndex() + edit_distance));
            } else {
                for (Position pi : state) {
                    if (string_length < (2 * edit_distance + pi.getIndex() - pi.getError() + 1)) {
                        return false;
                    }
                }
            }
        } else {
            Position q = null;
            if (string_length < edit_distance) {
                q = new Position(Parameter.M, Type.USUAL, 0, edit_distance - string_length);
            } else {
                q = new Position(Parameter.M, Type.USUAL, edit_distance - string_length, 0);
            }

            for (Position pi : state) {
                if (!pi.equals(q) && !lessThanSubsume(q, pi)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Constructs a LevenshteinAutomaton.
     * @param edit_distance maximum number of edits detected by this automaton
     */
    public LevenshteinAutomaton(int edit_distance) {
        super();

        this.chi = DEFAULT_CHI;
        this.edit_distance = edit_distance;
        List<Transition> transitions = new LinkedList<Transition>();
        HashMap<PositionState, State<FixedLengthBitSet>> state_mappings = new HashMap<PositionState, State<FixedLengthBitSet>>();

        Queue<PositionState> queue = new LinkedList<PositionState>();
        List<PositionState> added_states = new LinkedList<PositionState>();
        ArrayList<FixedLengthBitSet> power_set = buildPowerSet(2 * edit_distance + 2);

        added_states.add(START_STATE);
        state_mappings.put(START_STATE, getCurrentState());
        TreeSet<Integer> temp_set = new TreeSet<Integer>();
        temp_set.add(new Integer(0));

        HashSet<State<FixedLengthBitSet>> finished_state_list
                = new HashSet<State<FixedLengthBitSet>>();

        // build the universal Levenshtein automaton
        queue.add(START_STATE);
        while (!queue.isEmpty()) {
            PositionState state = queue.remove();

            for (FixedLengthBitSet b : power_set) {
                if (coversAllPositions(edit_distance, b.fixedLength(), state)) {
                    // generate the next state
                    PositionState next_state = delta(chi, edit_distance, state, b);

                    if (!next_state.isEmpty()) {
                        int index = added_states.indexOf(next_state);
                        if (index == -1) {
                            // state hasn't been added before now
                            queue.add(next_state);
                            added_states.add(next_state);

                            State<FixedLengthBitSet> s = new State<FixedLengthBitSet>(null);//next_state.first().getParameter() == Parameter.M);
                            state_mappings.put(next_state, s);
                        } else {
                            next_state = added_states.get(index);
                        }

                        transitions.add(new Transition(state, b, next_state));

                        State<FixedLengthBitSet> first_state = state_mappings.get(state);
                        State<FixedLengthBitSet> second_state = state_mappings.get(next_state);
                        if ((first_state != null) && (second_state != null)) {
                            finished_state_list.add(first_state);
                            finished_state_list.add(second_state);
                            first_state.addNextState(b, second_state);
                        }
                    }
                }
            }
        }

        // find the only state without any outbound edges
        for(State<FixedLengthBitSet> current_state : finished_state_list) {
            if(current_state.getNextStateCount() == 0) {
                current_state.setElement(Boolean.TRUE);
                break;
            }
        }
    }

    /**
     * Creates a characteristic vector of a character against a string.
     * @param c Character from which the characteristic vector is created
     * @param s String from which the characteristic vector is created
     * @param edit_distance Distance greater than or equal to one of the
     * desired LevenshteinAutomaton
     * @return FixedLengthBitSet representing the characteristic bit vector
     */
    private static FixedLengthBitSet buildCharacteristicVector(char c, String s, int edit_distance) {
        int len = 2 * edit_distance + 2;
        if (s.length() < len) {
            len = s.length();
        }
        FixedLengthBitSet result = new FixedLengthBitSet(len);
        for (int i = 0; i < len; i++) {
            result.set(i, (c == s.charAt(i)));
        }
        return result;
    }

    private class RecognizeMapping {

        public String working_string;
        public DictionaryAutomaton.State<Character> dictionary_state;
        public LevenshteinAutomaton.State<FixedLengthBitSet> levenshtein_state;
        public int index;

        public RecognizeMapping(String working_string,
                DictionaryAutomaton.State<Character> dictionary_state,
                LevenshteinAutomaton.State<FixedLengthBitSet> levenshtein_state,
                int index) {
            this.working_string = working_string;
            this.dictionary_state = dictionary_state;
            this.levenshtein_state = levenshtein_state;
            this.index = index;
        }
    }

    /**
     * Simultaneously traverses a DictionaryAutomaton and the provided
     * LevenshteinAutomaton to find all words within the specified Levenshtein
     * distance.
     * @param input_string used to search for other string within an edit distance
     * @param dictionary_automaton automaton representing all the words to search
     * @return Collection containing all the words within the edit distance
     * matching the input word
     */
    public Collection<String> recognize(String input_string, DictionaryAutomaton dictionary_automaton) {
        Collection<String> result = new TreeSet<String>();
        String s = input_string;
        for (int i = 0; i < edit_distance; i++) {
            s = LevenshteinAutomaton.NONALPHABET_CHARACTER + s;
        }
        RecognizeMapping mapping = null;
        Stack<RecognizeMapping> stack = new Stack<RecognizeMapping>();
        stack.push(new RecognizeMapping("", dictionary_automaton.getCurrentState(), this.getCurrentState(), 0));

        while (!stack.isEmpty()) {
            mapping = stack.pop();
            for (Character c : dictionary_automaton.getAlphabet()) {
                    String ss = s.substring(mapping.index);
                    DictionaryAutomaton.State<Character> dictionary_next = mapping.dictionary_state.getNextState(c);
                    if (dictionary_next == null)
                    	continue;
                    FixedLengthBitSet characteristic_vector = buildCharacteristicVector(c.charValue(), ss, edit_distance);
                    LevenshteinAutomaton.State<FixedLengthBitSet> levenshtein_next = mapping.levenshtein_state.getNextState(characteristic_vector);
                    if (levenshtein_next == null)
                    	continue;

                    if (dictionary_next.isAccept() && levenshtein_next.isAccept()) {
                        result.add(mapping.working_string + c);
                    }

                    stack.push(new RecognizeMapping(mapping.working_string + c, dictionary_next, levenshtein_next, mapping.index + 1));
                }
            }
       

        return result;
    }

    /**
     * Generate a new LevenshteinAutomaton instance from scratch.
     * @param edit_distance Distance greater than or equal to one of the
     * desired LevenshteinAutomaton
     * @return New LevenshteinAutomaton instance of the given edit distance
     */
    public static LevenshteinAutomaton generateLevenshtein(int edit_distance) {
        LevenshteinAutomaton levenshtein = new LevenshteinAutomaton(edit_distance);
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(getLevenshteinFileName(edit_distance)));
            try {
                oos.writeObject(levenshtein);
            } finally {
                oos.close();
            }
        } catch (IOException io_excep) {
            io_excep.printStackTrace();
        }
        return levenshtein;
    }

    /**
     * Builds a formatted file name based on the edit distance.
     * @param edit_distance Edit distance of desired file name
     * @return A valid file name for a LevenshteinAutomaton instance
     */
    private static String getLevenshteinFileName(int edit_distance) {
        return String.format("dist%03d.lev", edit_distance);
    }

    /**
     * Load an existing serialized instance of LevenshteinAutomaton.
     * Will search the JAR file first, and then check on the local disk.
     * @param edit_distance Distance greater than or equal to one of the
     * desired LevenshteinAutomaton
     * @return LevenshteinAutomaton instance if one exists, null otherwise
     */
    public static LevenshteinAutomaton loadLevenshteinAutomaton(int edit_distance) {
        String file_name = getLevenshteinFileName(edit_distance);
        InputStream in_stream = null;
        LevenshteinAutomaton automaton = null;

        // first, try to load the LevenshteinAutomaton from the JAR file
        in_stream = LevenshteinAutomaton.class.getClassLoader().getResourceAsStream(file_name);
        if (in_stream == null) {
            System.err.println("Couldn't find /" + file_name + " in the JAR file");
            // second, look for the LevenshteinAutomaton on the disk
            try {
                in_stream = new FileInputStream(file_name);
            } catch (FileNotFoundException fnf_excep) {
                System.err.println("Couldn't find " + file_name + " on disk");
                return null;
            }
        }

        try {
            ObjectInputStream ois = new ObjectInputStream(in_stream);
            try {
                automaton = (LevenshteinAutomaton) ois.readObject();
            } catch (ClassNotFoundException cnf_excep) {
                cnf_excep.printStackTrace();
            } finally {
                ois.close();
            }
        } catch (IOException io_excep) {
            io_excep.printStackTrace();
        }

        return automaton;
    }

    /**
     * Describe the command line usage of the LevenshteinAutomaton class.
     */
    private static void usage() {
        System.out.println("LevenshteinAutomaton (-g distance) | (-l distance -d dictionary_file (word)+)");
        System.out.println("\t-g generates a new Levenshtein automaton");
        System.out.println("\t-l matches based on an existing Levenshtein automaton");
    }

    /**
     * Command-line interface for generating instances of Levenshtein automata
     * and using them in conjunction with dictionary automata to find matching
     * strings within a metric space.
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            usage();
            return;
        }

        int edit_distance = -1;
        if ((args.length == 2)
                && (args[0].compareTo("-g") == 0)) {
            try {
                edit_distance = new Integer(args[1]).intValue();
                if (edit_distance < 1) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException nf_excep) {
                System.err.println(args[1] + " is not a positive integer value");
                return;
            }
            generateLevenshtein(edit_distance);
            return;
        }

        if ((args.length < 5)
                || (args[0].compareTo("-l") != 0)
                || (args[2].compareTo("-d") != 0)) {
            usage();
            return;
        }

        try {
            edit_distance = new Integer(args[1]).intValue();
            if (edit_distance < 1) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException nf_excep) {
            System.err.println(args[1] + " is not a positive integer value");
            return;
        }

        String dict_file_name = args[3];
        ObjectInputStream ois = null;
        DictionaryAutomaton dictionary = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(dict_file_name));
            int ver_length = ois.readInt();
            byte[] ver_magic_bytes = new byte[ver_length];
            ois.read(ver_magic_bytes);
            dictionary = (DictionaryAutomaton) ois.readObject();
            ois.close();
        } catch (FileNotFoundException fnf_excep) {
            System.err.println("Couldn't find dictionary file " + dict_file_name);
            return;
        } catch (IOException io_excep) {
            io_excep.printStackTrace();
            return;
        } catch (ClassNotFoundException cnf_excep) {
            System.err.println("Error while reading in the Dictionary object");
            return;
        }

        Collection<String> words = new ArrayList<String>(args.length - 4);
        for (int i = 4; i < args.length; i++) {
            words.add(args[i]);
        }

        LevenshteinAutomaton automaton = loadLevenshteinAutomaton(edit_distance);
        if (automaton != null) {
            Set<String> result = new TreeSet<String>();
            for (String word : words) {
                Collection<String> results = automaton.recognize(word, dictionary);
                result.addAll(results);
            }
            for (String s : result) {
                System.out.println(s);
            }
        }
    }
}
