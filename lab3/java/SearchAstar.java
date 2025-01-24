import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * Run the A* algorithm for finding the shortest path.
 */
public class SearchAstar<V> extends SearchUCS<V> {

    SearchAstar(Graph<V> graph) {
        super(graph);
    }

    @Override
    public SearchResult<V> search(V start, V goal) {
        int iterations = 0;

        //---------- TASK 3: A* search, the main search algorithm ---------//
        // Note: Don't forget to initialise the priority queue with a comparator.
        // Note: Every time you remove a state from the priority queue, you should increment `iterations`.
        PriorityQueue<AstarEntry> pqueue = new PriorityQueue<>(Comparator.comparingDouble(entry -> entry.estimatedCost));

        //---------- TASK 1a+c: Uniform-cost search -----------------------//
        pqueue.add(new AstarEntry(start, null, null, 0, goal));
        Set<V> visited = new HashSet<>();

        while (!pqueue.isEmpty()) {
            AstarEntry nextEntry = pqueue.remove();
            iterations ++;
            if (visited.contains(nextEntry.state)) continue;

            visited.add(nextEntry.state);
            if (nextEntry.state.equals(goal)) {
                List<Edge<V>> pathToHere = extractPath(nextEntry);
                return new SearchResult<>(this.graph, true, start, goal, nextEntry.costToHere, pathToHere, iterations);
            }
            for (Edge<V> e : this.graph.outgoingEdges(nextEntry.state)) {
                pqueue.add(new AstarEntry(e.end, e, nextEntry, nextEntry.costToHere + e.weight, goal));
            }
        }
        //---------- END TASK 3 -------------------------------------------//

        // Return this if you didn't find a path.
        return new SearchResult<>(this.graph, false, start, goal, -1, null, iterations);
    }

    /**
     * Entries to put in the priority queues in `searchAstar`.
     * This inherits all instance variables from `UCSEntry`, plus the ones you add.
     */
    public class AstarEntry extends UCSEntry {
        // These are inherited from UCSEntry:
        // public final V state;
        // public final Edge<V> lastEdge;      // null for the starting entry
        // public final UCSEntry backPointer;  // null for the starting entry
        // public final double costToHere;

        //---------- TASK 3: A* search, priority queue entries ------------//
        public final double estimatedCost;

        AstarEntry(V state, Edge<V> lastEdge, AstarEntry backPointer, double costToHere, V goal) {
            super(state, lastEdge, backPointer, costToHere);
            this.estimatedCost = costToHere + graph.guessCost(this.state, goal);
        }
        //---------- END TASK 3 -------------------------------------------//
    }

}


