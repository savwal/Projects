import sys
import random
from typing import Tuple, List, FrozenSet, Iterator, Union, Optional
from dataclasses import dataclass

from edge import Edge
from graph import Graph
from point import Point, ORIGIN


SEPARATOR = '/'

# The characters '_', 'A', ..., 'Z', '0', ..., '9', 'a', ..., 'z'.
# A fixed NPuzzle uses only an initial prefix of these characters.
ALL_TILE_NAMES = (
    "_" + 
    "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
    "0123456789" + 
    "abcdefghijklmnopqrstuvwxyz"
)

MOVES = [
    Point(-1, 0),
    Point(1, 0),
    Point(0, -1),
    Point(0, 1)
]


@dataclass(frozen=True)
class NPuzzleState:
    """    
    A possible state of the N-puzzle.

    We represent the tiles as numbers from 0 to N * N.
    The empty tile is represented by 0.

    The array `positions` stores the position of each tile.

    Optional task: try out different representations of states:
     - coding the positions as indices (sending a point p to p.y * N + p.x)
     - using an array `tiles` that stores the tile at each point
     - combinations (more space usage, but better runtime?)
    """
    N: int
    positions: Tuple[Point, ...]

    @staticmethod
    def parse(s: str) -> 'NPuzzleState':
        """
        Parse a state from its string representation.
        For example, a valid string representation for N = 3 is "/FDA/CEH/GB_/".
        """
        rows = s.strip(SEPARATOR).split(SEPARATOR)
        N = len(rows)
        positions = [ORIGIN] * (N * N)
        for y, row in enumerate(rows):
            if len(row) != N:
                raise ValueError(f"Row {row} does not have {N} columns.")
            for x, tile_name in enumerate(row):
                i = ALL_TILE_NAMES.index(tile_name)
                if positions[i] != ORIGIN:
                    raise ValueError(f"Duplicate tiles: {tile_name}")
                positions[i] = Point(x, y)
        return NPuzzleState(N, tuple(positions))

    def swap(self, i: int, j: int) -> 'NPuzzleState':
        """Returns the state given by swapping the tiles `i` and `j`."""
        if i > j: 
            i, j = j, i
        assert 0 <= i < j < len(self.positions)
        return NPuzzleState(self.N,
            self.positions[:i] + 
            (self.positions[j],) + 
            self.positions[i+1:j] + 
            (self.positions[i],) + 
            self.positions[j+1:]
        )

    def shuffled(self) -> 'NPuzzleState':
        """Returns a randomly shuffled state."""
        return NPuzzleState(self.N,
            tuple(random.sample(self.positions, len(self.positions)))
        )

    def tiles(self) -> Tuple[Tuple[int, ...], ...]:
        """Returns the NxN-matrix of tiles of this state."""
        tiles = [[0] * self.N for _ in range(self.N)]
        for i, p in enumerate(self.positions):
            tiles[p.y][p.x] = i
        return tuple(tuple(t) for t in tiles)

    def __str__(self):
        return SEPARATOR + SEPARATOR.join(
            ''.join(ALL_TILE_NAMES[i] for i in tl)
            for tl in self.tiles()
        ) + SEPARATOR


class NPuzzle(Graph[NPuzzleState]):
    """
    NPuzzle is an encoding of the N-puzzle.

    The vertices are string encodings of an N x N matrix of tiles.
    The tiles are represented by characters starting from the letter A
    (A...H for N=3, and A...O for N=4).
    The empty tile is represented by "_", and
    to make it more readable for humans every row is separated by "/".
    """
    N : int

    def __init__(self, graph: Optional[Union[str, int]] = None):
        if graph:
            self.init(graph)

    def init(self, graph: Union[str, int]):
        """Creates a new n-puzzle of size N."""
        N = int(graph)
        assert 2 <= N <= 6, "We only support sizes of 2 <= N <= 6."
        self.N = N  # type: ignore
        # (Reason for type:ignore: Pylance complains that N is upper-case)

    def vertices(self) -> FrozenSet[NPuzzleState]:
        """
        All states are vertices of this graph.
        However, the set of such vertices is typically too large to enumerate.
        So we do not implement those operations.
        """
        raise NotImplementedError("too expensive!")

    def outgoingEdges(self, v: NPuzzleState) -> List[Edge[NPuzzleState]]:
        emptyPos = v.positions[0]
        edges: List[Edge[NPuzzleState]] = []
        for move in MOVES:
            p = emptyPos.subtract(move)
            if self.isValidPoint(p):
                i = v.positions.index(p)
                newState = v.swap(0, i)
                edges.append(Edge(v, newState))
        return edges

    def isWeighted(self) -> bool:
        return False

    def isValidPoint(self, p:Point) -> bool:
        """Checks if the point is valid (lies inside the matrix)."""
        return 0 <= p.x < self.N and 0 <= p.y < self.N

    def guessCost(self, v: NPuzzleState, w: NPuzzleState) -> float:
        """
        We guess the minimal cost for getting from one puzzle state to another,
        as the sum of the Manhattan displacement for each tile. 
        The Manhattan displacement is the Manhattan distance from where
        the tile is currently to its desired location.
        """
        cost = 0
        for i in range(1, self.N * self.N):
            cost += v.positions[i].subtract(w.positions[i]).manhattanNorm()
        return cost

    def goalState(self) -> NPuzzleState:
        """
        Return the traditional goal state.
        The empty tile is in the bottom right corner.
        """
        return NPuzzleState(self.N, tuple([
            Point(self.N-1, self.N-1)
        ] + [
            Point(i % self.N, i // self.N)
            for i in range(self.N * self.N - 1)
        ]))

    def parseVertex(self, s: str):
        return NPuzzleState.parse(s)

    def randomVertices(self) -> Iterator[NPuzzleState]:
        while True:
            yield self.goalState().shuffled()

    def __str__(self) -> str:
        N = self.N
        return (
            f"NPuzzle graph of size {N} x {N}.\n\n" +
            f"States are {N} x {N} matrices of unique characters in " +
            f"'{ALL_TILE_NAMES[1]}'...'{ALL_TILE_NAMES[N*N-1]}',\n" +
            f"and '{ALL_TILE_NAMES[0]}' (for the empty tile); " +
            f"rows are interspersed with '{SEPARATOR}'.\n" +
            f"The traditional goal state is: {self.goalState()}.\n" +
            f"\nRandom example states with outgoing edges:\n" +
            self.exampleOutgoingEdges(8)
        )


if __name__ == '__main__':
    _, size = sys.argv
    puzzle = NPuzzle(size)
    print(puzzle)

