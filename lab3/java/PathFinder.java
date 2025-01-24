import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * This is the main class for finding paths in graphs.
 *
 * Depending on the command line arguments,
 * it creates different graphs and runs different search algorithms.
 */
public class PathFinder {

    // Settings for showing the solution - you can change these if you want.
    static final boolean showGridGraph = true;
    static final int maxGridGraphWidth = 100;
    static final int maxGridGraphHeight = 25;
    static final boolean showPathWeights = true;


    static String[] searchAlgorithms = new String[] {
        "random",
        "ucs",
        "astar"
    };

    static String[] graphTypes = new String[] {
        "AdjacencyGraph",
        "GridGraph",
        "NPuzzle",
        "WordLadder"
    };

    public static <V> void main(String[] args) throws IOException {
        CommandParser parser = new CommandParser("PathFinder", "This is the main file for finding paths in graphs.");
        parser.addArgument("--algorithm", "-a", "search algorithm to test")
            .makeRequired().setChoices(searchAlgorithms);
        parser.addArgument("--graphtype", "-t", "sorting algorithm")
            .makeRequired().setChoices(graphTypes);
        parser.addArgument("--graph", "-g", "the graph itself")
            .makeRequired();
        parser.addArgument("--queries", "-q", "list of alternating start and goal states")
            .makeList();

        CommandParser.Namespace options = parser.parseArgs(args);

        Graph<?> graph;
        switch (options.getString("graphtype")) {
            case "AdjacencyGraph":
                graph = new AdjacencyGraph();
                break;
            case "GridGraph":
                graph = new GridGraph();
                break;
            case "NPuzzle":
                graph = new NPuzzle();
                break;
            case "WordLadder":
                graph = new WordLadder();
                break;
            default:
                throw new IllegalArgumentException("Unknown graph type");
        }
        graph.init(options.getString("graph"));
        main2(graph, options);
    }

    // We need to split the code into two methods, otherwise we get type errors like this:
    // "method searchInteractive in class PathFinder cannot be applied to given types"
    public static <V> void main2(Graph<V> graph, CommandParser.Namespace options) throws IOException{
        SearchAlgorithm<V> algorithm;
        switch (options.getString("algorithm")) {
            case "random":
                algorithm = new SearchRandom<>(graph);
                break;
            case "ucs":
                algorithm = new SearchUCS<>(graph);
                break;
            case "astar":
                algorithm = new SearchAstar<>(graph);
                break;
            default:
                throw new IllegalArgumentException("Unknown search algorithm");
        }

        List<String> queries = options.getStringList("queries");
        if (queries == null || queries.isEmpty()) {
            searchInteractive(graph, algorithm);
        } else {
            if (queries.size() % 2 != 0)
                throw new IllegalArgumentException("There must be an even number of query states");
            for (int i = 0; i < queries.size(); i += 2) {
                String start = queries.get(i);
                String goal = queries.get(i+1);
                searchOnce(graph, algorithm, start, goal);
            }
        }
    }

    public static <V> void searchInteractive(Graph<V> graph, SearchAlgorithm<V> algorithm) {
        System.out.println(graph);
        System.out.println();
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.println("(ENTER to quit)");
            System.out.print("Start: "); System.out.flush();
            String start = in.nextLine().strip();
            if (start.isEmpty()) 
                break;
            System.out.print("Goal: "); System.out.flush();
            String goal = in.nextLine().strip();
            System.out.println();
            searchOnce(graph, algorithm, start, goal);
        }
        System.out.println("Bye bye, hope to see you again soon!");
    }

    public static <V> void searchOnce(Graph<V> graph, SearchAlgorithm<V> algorithm, String start, String goal) {
        V startState, goalState;
        try {
            startState = graph.parseVertex(start);
            goalState = graph.parseVertex(goal);
        } catch (IllegalArgumentException e) {
            System.err.println("Parse error!");
            System.err.println(e);
            System.err.println();
            return;
        }
        System.out.format("Searching: %s --> %s\n", startState, goalState);
        Stopwatch stopwatch = new Stopwatch();
        SearchResult<V> result = algorithm.search(startState, goalState);
        stopwatch.finished("Searching the graph");
        System.out.println(result.toString(showGridGraph, maxGridGraphWidth, maxGridGraphHeight, showPathWeights));
        System.out.println();
    }

}
