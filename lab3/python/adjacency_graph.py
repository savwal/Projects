import sys
from typing import Dict, List, FrozenSet, Optional

from edge import Edge
from graph import Graph


Vertex = str

class AdjacencyGraph(Graph[Vertex]):
    """
    This is a class for a generic finite graph, with string vertices.
     - The edges are stored as an adjacency list as described in the course book and the lectures.
     - The graphs can be anything, such as a road map or a web link graph.
     - The graph can be read from a simple text file with one edge per line.
    """
    adjacencyList: Dict[Vertex, List[Edge[Vertex]]]
    weighted: bool

    def __init__(self, graph: Optional[str] = None):
        self.adjacencyList = {}
        self.weighted = False
        if graph:
            self.init(graph)
        

    def init(self, graph: str):
        """
        Populates the graph with edges from a text file.
        The file should contain one edge per line, each on the form
        "from \\t to \\t weight" or "from \\t to" (where \\t == TAB).
        """
        if graph:
            with open(graph, encoding="utf-8") as IN:
                for line in IN:
                    line = line.strip()
                    if line and not line.startswith('#'):
                        start, end, *maybe_weight = line.split('\t')
                        if (not maybe_weight):
                            self.addEdge(Edge(start, end))
                        else:
                            weight = float(maybe_weight[0])
                            self.addEdge(Edge(start, end, weight))

    def addVertex(self, v: Vertex):
        """Adds a vertex to this graph."""
        self.adjacencyList.setdefault(v, [])

    def addEdge(self, e: Edge[Vertex]):
        """
        Adds a directed edge (and its source and target vertices) to this edge-weighted graph.
        Note: This does not test if the edge is already in the graph!
        """
        self.addVertex(e.start)
        self.addVertex(e.end)
        self.adjacencyList[e.start].append(e)
        if not self.weighted and e.weight != 1:
            self.weighted = True

    def vertices(self) -> FrozenSet[Vertex]:
        return frozenset(self.adjacencyList)

    def outgoingEdges(self, v: Vertex) -> List[Edge[Vertex]]:
        return self.adjacencyList.get(v, [])

    def isWeighted(self) -> bool:
        return self.weighted

    def parseVertex(self, s: str):
        if s not in self.adjacencyList:
            raise ValueError(f"Unknown vertex: {s}")
        return s

    def __str__(self) -> str:
        return (
            ("Weighted" if self.weighted else "Unweighted") +
            f" adjacency graph with {self.numVertices()} vertices and {self.numEdges()} edges.\n" +
            f"\nRandom vertices with outgoing edges:\n" +
            self.exampleOutgoingEdges(8)
        )


if __name__ == '__main__':
    _, file = sys.argv
    graph = AdjacencyGraph(file)
    print(graph)

