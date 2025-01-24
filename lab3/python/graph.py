import random
from abc import ABC, abstractmethod
from typing import Generic, List, FrozenSet, Optional, Iterator

from edge import Edge, V


class Graph(ABC, Generic[V]):
    """
    An interface for directed graphs.
    
    Note that this interface differs from the graph interface in the course API:
    - it lacks several of the methods in the course API,
    - it has an additional method `guessCost`.
    """

    @abstractmethod
    def init(self, graph: str):
        """Initialises a graph from a file or a name or other description."""

    @abstractmethod
    def vertices(self) -> FrozenSet[V]:
        """
        Returns an unmodifiable set of vertices of this graph.
        """

    @abstractmethod
    def outgoingEdges(self, v: V) -> List[Edge[V]]:
        """Returns a collection of the graph edges that originate from the given vertex."""

    @abstractmethod
    def isWeighted(self) -> bool:
        """Returns if the graph edges are weighted."""

    def numVertices(self) -> int:
        """Returns the number of vertices in this graph."""
        return len(self.vertices())

    def numEdges(self) -> int:
        """
        Returns the number of edges in this graph.
        (Warning: the default implementation is inefficient).
        """
        return sum(len(self.outgoingEdges(v)) for v in self.vertices())

    def guessCost(self, v: V, w: V) -> float:
        """
        Returns the guessed best cost for getting from v to w.
        The default guessed cost is 0, which is always admissible.
        """
        return 0.0

    @abstractmethod
    def parseVertex(self, s: str) -> V:
        """
        Returns a vertex parsed from the given string.
        
        This is really an operation associated with the vertex type V, not Graph,
        but there's no easy way to do that in Python.
        So the result is not related to the vertices currently contained in the graph.
        """

    def drawGraph(self, 
                maxWidth: int, 
                maxHeight: int,
                start: Optional[V], 
                goal: Optional[V], 
                solution: Optional[List[Edge[V]]],
            ) -> Optional[str]:
        """
        Returns a graphical string representation of the graph, and if provided,
        where the start and goal vertices, and the solution path are marked.
        """
        return None

    def randomVertices(self) -> Iterator[V]:
        """Generate an infinite stream of random vertices."""
        vertexList = list(self.vertices())
        while True:
            yield random.choice(vertexList)

    def exampleOutgoingEdges(self, limit: int) -> str:
        """A helper method for printing some graph information."""
        lines: List[str] = []
        for k, start in enumerate(self.randomVertices()):
            if k >= limit: 
                break
            outgoing = self.outgoingEdges(start)
            if not outgoing:
                lines.append(f" * {start} with no outgoing edges")
            else:
                ends: List[str] = []
                for edge in outgoing:
                    if self.isWeighted():
                        w = edge.weight
                        decimals = 0 if w == round(w, 0) else 1 if w == round(w, 1) else 2
                        ends.append(f"{edge.end} [{w:.{decimals}f}]")
                    else:
                        ends.append(str(edge.end))
                lines.append(f" * {start} --> " + ", ".join(ends))
        return "\n".join(lines)



