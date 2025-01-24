import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * GridGraph is a 2D-map encoded as a bitmap, or an N x M matrix of characters.
 *
 * Some characters are passable, others denote obstacles.
 * A node is a point in the bitmap, consisting of an x- and a y-coordinate.
 * This is defined by the helper class `Point`.
 * You can move from each point to the eight point around it.
 * The edge costs are 1.0 (for up/down/left/right) and sqrt(2) (for diagonal movement).
 * The graph can be read from a simple ASCII art text file.
 */

public class GridGraph implements Graph<Point> {
    private char[][] grid;

    // Characters from Moving AI Lab:
    //   . - passable terrain
    //   G - passable terrain
    //   @ - out of bounds
    //   O - out of bounds
    //   T - trees (unpassable)
    //   S - swamp (passable from regular terrain)
    //   W - water (traversable, but not passable from terrain)
    // Characters from http://www.delorie.com/game-room/mazes/genmaze.cgi
    //   | - +  walls
    //   space  passable
    // Note: "-" must come last in allowedChars, because we use it unescaped in a regular expression.

    private static final String allowedChars = ".G@OTSW +|-";
    private static final String passableChars = ".G ";

    // The eight directions, as points.
    private static final Point[] directions =
        IntStream.rangeClosed(-1, 1).boxed().flatMap(x ->
            IntStream.rangeClosed(-1, 1).boxed().flatMap(y ->
                Stream.of(new Point(x, y)).filter(p -> !p.equals(Point.ORIGIN))
            )
        ).toArray(Point[]::new);

    public int width() {
        return this.grid[0].length;
    }

    public int height() {
        return this.grid.length;
    }

    GridGraph() {}
    GridGraph(String graph) throws IOException {
        this.init(graph);
    }
    GridGraph(char[][] graph) {
        this.init(graph);
    }

    /**
     * Initialises the graph with edges from a text file.
     * The file describes the graph as ASCII art,
     * in the format of the graph files from the Moving AI Lab.
     */
    @Override
    public void init(String graph) throws IOException {
        init(Files.lines(Paths.get(graph))
                .filter(line -> line.matches("^[" + allowedChars + "]+$"))
                .map(String::toCharArray)
                .toArray(char[][]::new)
        );
    }

    /**
     * Initialises the graph from a grid of characters.
     */
    public void init(char[][] graph) {
        this.grid = graph;
        for (char[] row : this.grid) {
            if (row.length != this.width())
                throw new IllegalArgumentException("Malformed grid, row widths doesn't match.");
        }
    }

    /**
     * Returns true if you're allowed to pass through the given point.
     */
    private boolean passable(Point p) {
        return (
            0 <= p.x && p.x < this.width() && 
            0 <= p.y && p.y < this.height() && 
            GridGraph.passableChars.indexOf(this.grid[p.y][p.x]) >= 0
        );
    }

    @Override
    public Set<Point> vertices() {
        // Note: this is inefficient because it calculates the set each time.
        HashSet<Point> vertices = new HashSet<>();
        for (int y = 0; y < this.height(); y++) {
            for (int x = 0; x < this.width(); x++) {
                Point p = new Point(x, y);
                if (this.passable(p))
                    vertices.add(p);
            }
        }
        return Collections.unmodifiableSet(vertices);
    }

    @Override
    public List<Edge<Point>> outgoingEdges(Point p) {
        return 
            // We consider all directions...
            Arrays.stream(GridGraph.directions)
            // ...compute the edge in that direction...
            .map(dir -> new Edge<>(p, p.add(dir), dir.euclideanNorm()))
            // ...keep the ones with passable target...
            .filter(edge -> this.passable(edge.end))
            // ...and return them as a list.
            .collect(Collectors.toList());
    }

    public boolean isWeighted() {
        return true;
    }

    /**
     * Returns the guessed best cost for getting from one point to another.
     * (the Euclidean distance between the points)
     */
    @Override
    public double guessCost(Point v, Point w) {
        //---------- TASK 4: Guessing the cost, GridGraph -----------------//
        return v.subtract(w).euclideanNorm();
        //---------- END TASK 4 -------------------------------------------//
    }

    /**
     * Parse a point from a string representation.
     * For example, a valid string representation is "39:18".
     */
    @Override
    public Point parseVertex(String s) {
        return Point.parse(s);
    }

    /**
     * Returns a graphical string representation of the grid, with the given path shown.
     */
    @Override
    public String drawGraph(int maxWidth, int maxHeight, Point start, Point goal, List<Edge<Point>> solution) {
        Set<Point> pathPoints = new HashSet<>();
        if (solution != null) {
            solution.stream().map(e -> e.start).forEach(pathPoints::add);
            solution.stream().map(e -> e.end).forEach(pathPoints::add);
        }

        ArrayList<String> lines = new ArrayList<>();
        for (int y = 0; y < this.height(); y++) {
            if (y >= maxHeight) {
                lines.add("(truncated)");
                break;
            }
            ArrayList<String> line = new ArrayList<>();
            for (int x = 0; x < this.width(); x++) {
                if (y == 0 && x >= maxWidth - 10) {
                    line.add(" (truncated)");
                    break;
                }
                if (x >= maxWidth) {
                    break;
                }
                char p = this.grid[y][x];
                Point point = new Point(x, y);
                line.add(
                    point.equals(start) ? "S" :
                    point.equals(goal) ? "G" :
                    pathPoints.contains(point) ? "*" : String.valueOf(p)
                );
            }
            lines.add(String.join("", line));
        }
        return String.join("\n", lines);
    }

    /**
     * Returns a string representation of this graph, including some random points and edges.
     */
    @Override
    public String toString() {
        return (
            String.format("Bitmap graph of dimensions %d x %d pixels.\n", this.width(), this.height()) +
            this.drawGraph(100, 25) +
            "\n\nRandom example points with outgoing edges:\n" + 
            this.exampleOutgoingEdges(8)
        );
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) throw new IllegalArgumentException();
        GridGraph graph = new GridGraph(args[0]);
        System.out.println(graph);
    }
}

