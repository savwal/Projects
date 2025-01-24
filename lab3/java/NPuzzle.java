import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * NPuzzle is an encoding of the N-puzzle.
 *
 * The vertices are string encodings of an N x N matrix of tiles.
 * The tiles are represented by characters starting from the letter A
 * (A...H for N=3, and A...O for N=4).
 * The empty tile is represented by "_", and
 * to make it more readable for humans every row is separated by "/".
 */
public class NPuzzle implements Graph<NPuzzle.State> {
    private int N;

    private static final String SEPARATOR = "/";

    // The characters '_', 'A', ..., 'Z', '0', ..., '9', 'a', ..., 'z'.
    // A fixed NPuzzle uses only an initial prefix of these characters.
    private static final String ALL_TILE_NAMES =
        "_" + 
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
        "0123456789" + 
        "abcdefghijklmnopqrstuvwxyz";

    private static final Point[] MOVES = {
        new Point(-1, 0),
        new Point(1, 0),
        new Point(0, -1),
        new Point(0, 1)
    };

    NPuzzle() {}
    NPuzzle(int N) {
        init(N);
    }
    NPuzzle(String graph) {
        init(graph);
    }
    
    /**
     * Creates a new n-puzzle of size `N`.
     */
    public void init(int N) {
        if (!(N >= 2 && N <= 6))
            throw new IllegalArgumentException("We only support sizes of 2 <= N <= 6.");
        this.N = N;
    }

    @Override
    public void init(String graph) {
        init(Integer.parseInt(graph));
    }

    /**
     * A possible state of the N-puzzle.
     *
     * We represent the tiles as numbers from 0 to N * N.
     * The empty tile is represented by 0.
     *
     * The array `positions` stores the position of each tile.
     *
     * Optional task: try out different representations of states:
     *  - coding the positions as indices (sending a point p to p.y * N + p.x)
     *  - using an array `tiles` that stores the tile at each point
     *  - combinations (more space usage, but better runtime?)
     */
    public class State {
        public final Point[] positions;

        State(Point[] positions) {
            this.positions = positions;
        }

        /**
         * Parse a state from its string representation.
         * For example, a valid string representation for N = 3 is "/FDA/CEH/GB_/".
         */
        public State(String s) {
            String[] rows = Arrays.stream(s.split(Pattern.quote(SEPARATOR)))
                .filter(row -> !row.isEmpty())
                .toArray(String[]::new);
            int N = rows.length;
            this.positions = new Point[N * N];
            for (int y = 0; y < N; y++) {
                String row = rows[y];
                if (row.length() != N)
                    throw new IllegalArgumentException("Row " + row + " does not have " + N + " columns.");
                for (int x = 0; x < N; x++) {
                    char tileName = row.charAt(x);
                    int i = ALL_TILE_NAMES.indexOf(tileName);
                    if (this.positions[i] != null)
                        throw new IllegalArgumentException("Duplicate tiles: " + tileName);
                    this.positions[i] = new Point(x, y);
                }
            }
        }

        /**
         * Returns the state given by swapping the tiles `i` and `j`.
         */
        public State swap(int i, int j) {
            Point[] positionsNew = this.positions.clone();
            positionsNew[i] = this.positions[j];
            positionsNew[j] = this.positions[i];
            return new State(positionsNew);
        }

        /**
         * Returns a randomly shuffled state.
         */
        public State shuffled() {
            Point[] positionsNew = this.positions.clone();
            Collections.shuffle(Arrays.asList(positionsNew));
            return new State(positionsNew);
        }

        /**
         * Returns the NxN-matrix of tiles of this state.
         */
        public int[][] tiles() {
            int[][] tiles = new int[N][N];
            for (int i = 0; i != this.positions.length; i++) {
                Point p = this.positions[i];
                tiles[p.y][p.x] = i;
            }
            return tiles;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) // equality of references
                return true;
            if (!(o instanceof State))
                return false;
            return Arrays.deepEquals(this.positions, ((State) o).positions);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(this.positions);
        }

        @Override
        public String toString() {
            return Arrays.stream(this.tiles())
                .map(rowTiles -> Arrays.stream(rowTiles)
                    .mapToObj(ALL_TILE_NAMES::charAt)
                    .map(String::valueOf)
                    .collect(Collectors.joining())
                ).collect(Collectors.joining(SEPARATOR, SEPARATOR, SEPARATOR));
        }
    }

    // Helper methods for formatting and parsing tiles.

    public char formatTile(int tile) {
        return ALL_TILE_NAMES.charAt(tile);
    }

    /**
     * All states are vertices of this graph.
     * However, the set of such vertices is typically too large to enumerate.
     * So we do not implement those operations.
     */
    @Override
    public Set<State> vertices() {
        throw new UnsupportedOperationException("too expensive!");
    }

    public List<Edge<State>> outgoingEdges(State v) {
        Point emptyPos = v.positions[0];
        ArrayList<Edge<State>> edges = new ArrayList<>();
        for (Point move : MOVES) {
            Point p = emptyPos.subtract(move);
            if (isValidPoint(p)) {
                int i = Arrays.asList(v.positions).indexOf(p);
                State newState = v.swap(0, i);
                edges.add(new Edge<>(v, newState));
            }
        }
        return edges;
    }

    public boolean isWeighted() {
        return false;
    }

    /**
     * Checks if the point is valid (lies inside the matrix).
     */
    public boolean isValidPoint(Point p) {
        return p.x >= 0 && p.y >= 0 && p.x < N && p.y < N;
    }

    /**
     * We guess the minimal cost for getting from one puzzle state to another,
     * as the sum of the Manhattan displacement for each tile. 
     * The Manhattan displacement is the Manhattan distance from where
     * the tile is currently to its desired location.
     */
    @Override
    public double guessCost(State v, State w) {
        int cost = 0;
        for (int i = 1; i < N * N; i++) {
            cost += v.positions[i].subtract(w.positions[i]).manhattanNorm();
        }
        return cost;
    }

    /**
     * Return the traditional goal state.
     * The empty tile is in the bottom right corner.
     */
    public State goalState() {
        return new State(IntStream.range(0, N * N)
                .map(i -> Math.floorMod(i - 1, N * N))
                .mapToObj(i -> new Point(i % N, i / N))
                .toArray(Point[]::new));
    }

    public State parseVertex(String str) {
        return new State(str);
    }

    @Override public Supplier<State> randomVertices() {
        return () -> this.goalState().shuffled();
    }

    @Override
    public String toString() {
        return String.format(
            "NPuzzle graph of size %d x %d.\n\n" +
            "States are %d x %d matrices of unique characters in '%c'...'%c',\n" +
            "and '%c' (for the empty tile); rows are interspersed with '%s'.\n" +
            "The traditional goal state is: %s.\n\n" +
            "Random example points with outgoing edges:\n" +
            "%s",
            N, N, N, N, 
            ALL_TILE_NAMES.charAt(1), ALL_TILE_NAMES.charAt(N*N-1), ALL_TILE_NAMES.charAt(0),
            SEPARATOR, this.goalState(),
            this.exampleOutgoingEdges(8)
        );
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) throw new IllegalArgumentException();
        NPuzzle graph = new NPuzzle(args[0]);
        System.out.println(graph);
    }

}

