import java.util.Random;
import java.util.List;
import java.util.LinkedList;

/**
 * Perform a random walk in the graph, hoping to reach the goal.
 * Warning: this class will give up of the random walk
 * reaches a dead end or after 10,000 iterations.
 * So a negative result does not mean there is no path.
 */
public class SearchRandom<V> extends SearchAlgorithm<V> {

    SearchRandom(Graph<V> graph) {
        super(graph);
    }

    @Override
    public SearchResult<V> search(V start, V goal) {
        Random random = new Random();
        int iterations = 0;
        double cost = 0;
        LinkedList<Edge<V>> path = new LinkedList<>();

        V current = start;
        while (iterations < 10_000) {
            iterations++;
            if (current.equals(goal))
                return new SearchResult<>(this.graph, true, start, current, cost, path, iterations);

            List<Edge<V>> neighbours = this.graph.outgoingEdges(current);
            if (neighbours.isEmpty())
                break;

            Edge<V> edge = neighbours.get(random.nextInt(neighbours.size()));
            path.add(edge);
            cost += edge.weight;
            current = edge.end;
        }
        return new SearchResult<>(this.graph, false, start, goal, -1, null, iterations);
    }

}


