import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The class for search results.
 * 
 * If you found a result: `SearchResult(graph, True, start, goal, cost, path, iterations)`
 * If no path was found: `SearchResult(graph, False, start, goal, -1, None, iterations)`
 */
public class SearchResult<V> {
    public final Graph<V> graph;
    public final boolean success;
    public final V start;
    public final V goal;
    public final double cost;
    public final List<Edge<V>> path;
    public final int iterations;

    SearchResult(Graph<V> graph, boolean success, V start, V goal, double cost, List<Edge<V>> path, int iterations) {
        this.graph = graph;
        this.success = success;
        this.start = start;
        this.goal = goal;
        this.cost = cost;
        this.path = path;
        this.iterations = iterations;
    }

    private String formatPathPart(boolean suffix, boolean withWeight, int i, int j) {
        return path.subList(i, j).stream()
                .map(e -> e.toString(!suffix, suffix, withWeight && this.graph.isWeighted()))
                .collect(Collectors.joining());
    }

    public void validate() {
        if (this.success) {
            if (this.path == null)
                throw new IllegalArgumentException("The path must be a list - did you forget to implement extractPath?");
            double actualCost = this.path.stream()
                .mapToDouble((e) -> e.weight)
                // We sum using left association order to mimic the algorithm.
                .reduce(0, Double::sum);
            if (this.cost != actualCost) 
                throw new IllegalArgumentException(String.format(
                    "The reported path cost (%f) differs from the calculated cost (%f)", this.cost, actualCost
                ));
        }
        if (this.iterations <= 0)
            throw new IllegalArgumentException("The number of iterations should be > 0");
    }

    public String toString(boolean drawGraph, int maxGraphWidth, int maxGraphHeight, boolean withWeight) {
        ArrayList<String> lines = new ArrayList<>();
        if (this.iterations <= 0)
            lines.add("ERROR: You have to iterate over at least the starting state!");
        lines.add("Loop iterations: " + this.iterations);
        if (drawGraph) {
            String graphStr = this.graph.drawGraph(maxGraphWidth, maxGraphHeight, this.start, this.goal, this.path);
            if (graphStr != null)
                lines.add(graphStr + "\n");
        }
        if (!this.success) {
            lines.add(String.format("No path found from %s to %s", start, goal));
        } else {
            lines.add(String.format("Cost of path from %s to %s: %.2f", start, goal, cost));
            if (path == null) {
                lines.add("WARNING: You have not implemented extractPath!");
            } else {
                // Print the path.
                int pathlen = this.path.size();
                lines.add("Number of edges: " + pathlen);
                if (pathlen <= 10) {
                    lines.add(
                        this.path.get(0).start + 
                        formatPathPart(true, withWeight, 0, pathlen)
                    );
                } else {
                    lines.add(
                        formatPathPart(false, withWeight, 0, 5) + "..." + 
                        formatPathPart(true, withWeight, pathlen-5, pathlen)
                    );
                }
                double actualCost = this.path.stream()
                    .mapToDouble((e) -> e.weight)
                    // We sum using left association order to mimic the algorithm.
                    .reduce(0, Double::sum);
                if (this.cost != actualCost) {
                    lines.add(String.format(
                        "WARNING: The actual path cost (%f) differs from the reported cost (%f)!", 
                        actualCost, this.cost
                    ));
                }
            }
        }
        return String.join("\n", lines);
    }

}

