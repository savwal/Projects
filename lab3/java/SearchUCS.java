import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * Run uniform-cost search for finding the shortest path.
 */
public class SearchUCS<V> extends SearchAlgorithm<V> {

    SearchUCS(Graph<V> graph) {
        super(graph);
    }

    @Override
    public SearchResult<V> search(V start, V goal) {
        int iterations = 0;
        PriorityQueue<UCSEntry> pqueue = new PriorityQueue<>(Comparator.comparingDouble(entry -> entry.costToHere));

        //---------- TASK 1a+c: Uniform-cost search -----------------------//
        pqueue.add(new UCSEntry(start, null, null, 0));
        Set<V> visited = new HashSet<>();

        while (!pqueue.isEmpty()) {
            UCSEntry nextEntry = pqueue.remove();
            iterations ++;
            if (visited.contains(nextEntry.state)) continue;

            visited.add(nextEntry.state);
            if (nextEntry.state.equals(goal)) {
                List<Edge<V>> pathToHere = extractPath(nextEntry);
                return new SearchResult<>(this.graph, true, start, goal, nextEntry.costToHere, pathToHere, iterations);
            }
            for (Edge<V> e : this.graph.outgoingEdges(nextEntry.state)) {
                pqueue.add(new UCSEntry(e.end, e, nextEntry, nextEntry.costToHere + e.weight));
            }
        }
        //---------- END TASK 1a+c ----------------------------------------//

        // Return this if you didn't find a path.
        return new SearchResult<>(this.graph, false, start, goal, -1, null, iterations);
    }

    /**
     * Extracts the path from the start to the current priority queue entry.
     */
    public List<Edge<V>> extractPath(UCSEntry entry) {
        //---------- TASK 1b: Extracting the path -------------------------//
        LinkedList<Edge<V>> path = new LinkedList<Edge<V>>();
        while (entry.lastEdge != null) {
            path.addFirst(entry.lastEdge);
            entry = entry.backPointer;
        }
        if (path.isEmpty()) {
            Edge<V> backToSelf = new Edge<V>(entry.state, entry.state, 0);
            path.add(backToSelf);
        }
        return path;
        //---------- END TASK 1b ------------------------------------------//
    }

    /**
     * Entries to put in the priority queues in `SearchUCS`.
     */
    public class UCSEntry {
        public final V state;
        public final Edge<V> lastEdge;      // null for the starting entry
        public final UCSEntry backPointer;  // null for the starting entry
        public final double costToHere;

        UCSEntry(V state, Edge<V> lastEdge, UCSEntry backPointer, double costToHere) {
            this.state = state;
            this.lastEdge = lastEdge;
            this.backPointer = backPointer;
            this.costToHere = costToHere;
        }
    }

}

