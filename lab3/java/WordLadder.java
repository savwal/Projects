import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * A graph that encodes word ladders.
 *
 * The class does not store the full graph in memory, just a dictionary of words.
 * The edges are then computed on demand.
 */
public class WordLadder implements Graph<String> {

    private Set<String> dictionary;
    private Set<Character> alphabet;

    WordLadder() {
        this.dictionary = new HashSet<>();
        this.alphabet = new HashSet<>();
    }

    WordLadder(String graph) throws IOException {
        this();
        this.init(graph);
    }

    /**
     * Populates the word ladder graph from the given dictionary file.
     * The file should contain one word per line, except lines starting with "#".
     */
    public void init(String graph) throws IOException {
        Files.lines(Paths.get(graph))
            .filter(line -> !line.startsWith("#"))
            .map(String::trim)
            .forEach(this::addWord);
    }

    /**
     * Adds a word to the dictionary if it only contains letters.
     * The word is converted to lowercase.
     */
    public void addWord(String word) {
        if (word.matches("\\p{L}+")) {
            word = word.toLowerCase();
            this.dictionary.add(word);
            for (char c : word.toCharArray())
                this.alphabet.add(c);
        }
    }

    @Override
    public Set<String> vertices() {
        return Collections.unmodifiableSet(dictionary);
    }

    /**
     * Returns a list of the graph edges that originate from `word`.
     */
    @Override
    public List<Edge<String>> outgoingEdges(String w) {
        //---------- TASK 2: Outgoing edges, Wordladder -------------------//
        List<Edge<String>> edges = new LinkedList<Edge<String>>();
        
        for (int i = 0; i < w.length(); i++) {
            char[] wAsChar = w.toCharArray();
            for (Character ch : alphabet) {
                if (ch != w.charAt(i)) {
                    wAsChar[i] = ch;
                    if (dictionary.contains(String.valueOf(wAsChar))) {
                        edges.add(new Edge<String>(w, String.valueOf(wAsChar)));
                    }
                }
            }
        }
        return edges;
        //---------- END TASK 2 -------------------------------------------//
    }

    /**
     * Returns the guessed best cost for getting from a word to another.
     * (the number of differing character positions)
     */
    @Override
    public double guessCost(String w, String u) {
        //---------- TASK 4: Guessing the cost, Wordladder ----------------//
        // Don't forget to handle the case where the lengths differ.
        if (w.length() != u.length()) return 128;

        double guess = 0;
        for (int i = 0; i < w.length(); i++) {
            if (w.charAt(i) != u.charAt(i)) guess++;
        }
        return guess;
        //---------- END TASK 4 -------------------------------------------//
    }

    public boolean isWeighted() {
        return false;
    }

    @Override
    public String parseVertex(String s) {
        String word = s.toLowerCase();
        if (!this.dictionary.contains(word))
            throw new IllegalArgumentException("Unknown word: " + word);
        return s;
    }

    public String toString() {
        return (
            String.format("Word ladder graph with %d words.\n", this.numVertices()) +
            "Alphabet: " + 
            this.alphabet.stream().map(Object::toString)
                .sorted().collect(Collectors.joining()) +
            "\n\nRandom example words with ladder steps:\n" +
            this.exampleOutgoingEdges(8)
        );
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) throw new IllegalArgumentException();
        WordLadder graph = new WordLadder(args[0]);
        System.out.println(graph);
    }

}

