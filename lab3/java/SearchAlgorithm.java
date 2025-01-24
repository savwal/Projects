
/**
 * This is an abstract class for finding paths in a given graph.
 * The main method is `search` which returns a `SearchResult` object.
 */
abstract class SearchAlgorithm<V> {
    public final Graph<V> graph;

    public SearchAlgorithm(Graph<V> graph) {
        this.graph = graph;
    }

    /**
     * The main search method, taking the start and goal vertices as input.
     */
    public abstract SearchResult<V> search(V start, V goal);

}

