import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

/**
 * This is a class for a generic finite graph, with string vertices.
 *  - The edges are stored as an adjacency list as described in the course book and the lectures.
 *  - The graphs can be anything, such as a road map or a web link graph.
 *  - The graph can be read from a simple text file with one edge per line.
 */

public class AdjacencyGraph implements Graph<String> {
    private final Map<String, List<Edge<String>>> adjacencyList;
    private boolean weighted;

    AdjacencyGraph() {
        this.adjacencyList = new HashMap<>();
        this.weighted = false;
    }

    AdjacencyGraph(String graph) throws IOException {
        this();
        this.init(graph);
    }

    /**
     * Populates the graph with edges from a text file.
     * The file should contain one edge per line, each on the form
     * "from \\t to \\t weight" or "from \\t to"(where \\t == TAB).
     */
    @Override
    public void init(String graph) throws IOException {
        Files.lines(Paths.get(graph))
            .map(String::strip)
            .filter(line -> !line.startsWith("#"))
            .map(line -> line.split("\t"))
            .map(parts -> ( parts.length == 2
                          ? new Edge<>(parts[0], parts[1])
                          : new Edge<>(parts[0], parts[1], Double.parseDouble(parts[2]))
                          ))
            .forEach(this::addEdge);
    }

    /**
     * Adds a vertex to this graph.
     */
    public void addVertex(String v) {
        this.adjacencyList.putIfAbsent(v, new LinkedList<>());
    }

    /**
     * Adds a directed edge (and its source and target vertices) to this edge-weighted graph.
     * Note: This does not test if the edge is already in the graph!
     */
    public void addEdge(Edge<String> e) {
        this.addVertex(e.start);
        this.addVertex(e.end);
        this.adjacencyList.get(e.start).add(e);
        if (!this.weighted && e.weight != 1)
            this.weighted = true;
    }

    @Override
    public Set<String> vertices() {
        return Collections.unmodifiableSet(this.adjacencyList.keySet());
    }

    @Override
    public List<Edge<String>> outgoingEdges(String v) {
        return Collections.unmodifiableList(this.adjacencyList.get(v));
    }

    public boolean isWeighted() {
        return this.weighted;
    }

    @Override
    public String parseVertex(String s) {
        if (!this.adjacencyList.containsKey(s)) 
            throw new IllegalArgumentException("Unknown vertex: " + s);
        return s;
    }

    @Override
    public String toString() {
        return (
            (this.weighted ? "Weighted" : "Unweighted") +
            String.format(" adjacency graph of dimensions %d vertices and %d edges\n", this.numVertices(), this.numEdges()) +
            "\nRandom example points with outgoing edges:\n" + 
            this.exampleOutgoingEdges(8)
        );
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) throw new IllegalArgumentException();
        AdjacencyGraph graph = new AdjacencyGraph(args[0]);
        System.out.println(graph);
    }

}

