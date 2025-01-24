import java.io.IOException;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A simplistic interface for directed graphs.
 *
 * Note that this interface differs from the graph interface in the course API:
 * - it lacks several of the methods in the course API,
 * - it has an additional method `guessCost`.
 */

interface Graph<V> {

    /**
     * Initialises a graph from a file or a name or other description.
     */
    public void init(String graph) throws IOException;

    /**
     * Returns an unmodifiable set of vertices of this graph.
     */
    Set<V> vertices();

    /**
     * Returns a list of the graph edges that originate from the given vertex.
     */
    List<Edge<V>> outgoingEdges(V v);

    /**
     * Returns if the graph edges are weighted.
     */
    public boolean isWeighted();

    /**
     * Returns the number of vertices in this graph.
     */
    default int numVertices() {
        return this.vertices().size();
    }

    /**
     * Returns the number of edges in this graph.
     * (Warning: the default implementation is inefficient).
     */
    default int numEdges() {
        return this.vertices().stream()
            .mapToInt((v) -> this.outgoingEdges(v).size())
            .sum();
    }

    /**
     * Returns the guessed best cost for getting from v to w.
     * The default guessed cost is 0, which is always admissible.
     */
    default double guessCost(V v, V w) {
        return 0.0;
    }

    /**
     * Returns a vertex parsed from the given string.
     *
     * This is really an operation associated with the vertex type V, not Graph,
     * but there's no easy way to do that in Java.
     * So the result is not related to the vertices currently contained in the graph.
     */
    V parseVertex(String s);

    /**
     * Returns a graphical string representation of the graph, and if provided,
     * where the start and goal vertices, and the solution path are marked.
     */
    default String drawGraph(int maxWidth, int maxHeight, V start, V goal, List<Edge<V>> solution) {
        return null;
    }

    default String drawGraph(int maxWidth, int maxHeight) {
        return drawGraph(maxWidth, maxHeight, null, null, null);
    }

    /**
     * Generate an infinite stream of random vertices.
     */
    default Supplier<V> randomVertices() {
        ArrayList<V> vertexList = new ArrayList<>(this.vertices());
        Random random = new Random();
        return () -> vertexList.get(random.nextInt(vertexList.size()));
    }

    /**
     * A helper method for printing some graph information.
     */
    default String exampleOutgoingEdges(int limit) {
        ArrayList<String> lines = new ArrayList<>();
        return Stream.generate(this.randomVertices())
            .limit(limit)
            .map(start -> {
                List<Edge<V>> outgoing = this.outgoingEdges(start);
                if (outgoing.isEmpty()) {
                    return String.format(" * %s with no outgoing edges", start);
                } else {
                    ArrayList<String> ends = new ArrayList<>();
                    for (Edge<V> edge : outgoing) {
                        if (this.isWeighted()) {
                            double w = edge.weight;
                            int decimals = (w == Math.rint(w)) ? 0 : (w == Math.rint(10*w)/10) ? 1 : 2;
                            ends.add(String.format("%s [%."+ decimals + "f]", edge.end, w));                
                        } else {
                            ends.add(edge.end.toString());
                        }
                    }
                    return String.format(
                        " * %s --> %s", start, String.join(", ", ends)
                    );
                }
            }).collect(Collectors.joining("\n"));
    }

}

