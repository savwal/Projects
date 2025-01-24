import sys
import re
from typing import List, Set, FrozenSet, Union, Optional

from edge import Edge
from graph import Graph
from point import Point


class GridGraph(Graph[Point]):
    """
    GridGraph is a 2D-map encoded as a bitmap, or an N x M matrix of characters.

    Some characters are passable, others denote obstacles.
    A verte is a point in the bitmap, consisting of an x- and a y-coordinate.
    This is defined by the helper class `Point`.
    You can move from each point to the eight point around it.
    The edge costs are 1.0 (for up/down/left/right) and sqrt(2) (for diagonal movement).
    The graph can be read from a simple ASCII art text file.
    """
    grid: List[str]

    # Characters from Moving AI Lab:
    #   . - passable terrain
    #   G - passable terrain
    #   @ - out of bounds
    #   O - out of bounds
    #   T - trees (unpassable)
    #   S - swamp (passable from regular terrain)
    #   W - water (traversable, but not passable from terrain)
    # Characters from http://www.delorie.com/game-room/mazes/genmaze.cgi
    #   | - +  walls
    #   space  passable
    allowedChars = ".G@OTSW +|-"
    passableChars = ".G "

    # The eight directions, as points.
    directions = [
        Point(x, y)
        for x in [-1, 0, 1] for y in [-1, 0, 1]
        if not (x == y == 0)
    ]

    def width(self) -> int:
        return len(self.grid[0])

    def height(self) -> int:
        return len(self.grid)

    def __init__(self, graph: Optional[Union[str, List[str]]] = None):
        if graph:
            self.init(graph)

    def init(self, graph: Union[str, List[str]]):
        """
        Initialises the graph with edges from a text file,
        or from a grid of characters.
        The file describes the graph as ASCII art,
        in the format of the graph files from the Moving AI Lab.
        """
        if isinstance(graph, str):
            with open(graph, encoding="utf-8") as IN:
                graph = [
                    line for line in IN.read().splitlines() 
                    if re.match("^[" + GridGraph.allowedChars + "]+$", line)
                ]
        self.grid = graph
        for row in self.grid:
            if len(row) != self.width():
                raise ValueError("Malformed grid, row widths doesn't match.");

    def passable(self, p: Point) -> bool:
        """Returns true if you're allowed to pass through the given point."""
        return (
            0 <= p.x < self.width() and
            0 <= p.y < self.height() and
            self.grid[p.y][p.x] in self.passableChars
        )

    def vertices(self) -> FrozenSet[Point]:
        # Note: this is inefficient because it calculates the set each time.
        return frozenset(
            p for y in range(self.height()) 
            for x in range(self.width())
            if self.passable(p := Point(x, y))
        )

    def outgoingEdges(self, v: Point) -> List[Edge[Point]]:
        return [
            edge
            # We consider all directions...
            for dir in self.directions
            # ...compute the edge in that direction...
            for edge in [Edge(v, v.add(dir), dir.euclideanNorm())]
            # ...and keep the ones with passable target.
            if self.passable(edge.end)
        ]

    def isWeighted(self) -> bool:
        return True

    def guessCost(self, v: Point, w: Point) -> float:
        """
        Returns the guessed best cost for getting from one point to another.
        (the Euclidean distance between the points)
        """
        #---------- TASK 4: Guessing the cost, GridGraph ---------------------#
        # TODO: Replace these lines with your solution!
        raise NotImplementedError()
        #---------- END TASK 4 -----------------------------------------------#

    def parseVertex(self, s: str) -> Point:
        """
        Parse a point from a string representation.
        For example, a valid string representation is "39:18".
        """
        return Point.parse(s)

    def drawGraph(self, 
                maxWidth: int,
                maxHeight: int,
                start: Optional[Point] = None, 
                goal: Optional[Point] = None, 
                solution: Optional[List[Edge[Point]]] = None,
            ) -> str:
        pathPoints: Set[Point] = set()
        if solution:
            for e in solution:
                pathPoints.add(e.start)
                pathPoints.add(e.end)

        lines: List[str] = []
        for y, row in enumerate(self.grid):
            if y >= maxHeight:
                lines.append("(truncated)")
                break
            line: List[str] = []
            for x, p in enumerate(row):
                if y == 0 and x >= maxWidth - 10:
                    line.append(" (truncated)")
                    break
                if x >= maxWidth:
                    break
                point = Point(x, y)
                line.append(
                    'S' if point == start else
                    'G' if point == goal else
                    '*' if point in pathPoints else p
                )
            lines.append("".join(line))
        return "\n".join(lines)

    def __str__(self) -> str:
        """
        Returns a string representation of this graph, including some random points and edges.
        """
        return (
            f"Bitmap graph of dimensions {self.width()} x {self.height()} pixels.\n" +
            self.drawGraph(100, 25) +
            "\n\nRandom example points with outgoing edges:\n" +
            self.exampleOutgoingEdges(8)
        )


if __name__ == '__main__':
    _, file = sys.argv
    graph = GridGraph(file)
    print(graph)

