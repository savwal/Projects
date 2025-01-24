from dataclasses import dataclass
from typing import Generic, List, Optional

from edge import Edge, V
from graph import Graph


# Dataclasses provide a lot of methods for free, such as __init__ and comparison:
# https://docs.python.org/3/library/dataclasses.html
# 'frozen' means that we cannot modify elements after creation:
@dataclass(frozen=True)
class SearchResult(Generic[V]):
    """
    A class for search results.

    If you found a result: `SearchResult(graph, True, start, goal, cost, path, iterations)`
    If no path was found: `SearchResult(graph, False, start, goal, -1, None, iterations)`
    """
    graph: Graph[V]
    success: bool
    start: V
    goal: V
    cost: float
    path: Optional[List[Edge[V]]]
    iterations: int

    def formatPathPart(self, suffix: bool, withWeight: bool, i: int, j: int) -> str:
        assert self.path  # The path must not be None
        return "".join(
            e.toString(not suffix, suffix, withWeight and self.graph.isWeighted())
            for e in self.path[i:j]
        )

    def validate(self):
        if self.success:
            if not isinstance(self.path, list):
                raise ValueError("The path must be a list - did you forget to implement extractPath?")
            actualCost = sum(e.weight for e in self.path)
            if (self.cost != actualCost):
                raise ValueError("The reported path cost ({self.cost}) differs from the calculated cost ({actualCost})")
        if self.iterations <= 0:
            raise ValueError("The number of iterations should be > 0")

    def toString(self, 
                drawGraph: bool,
                maxGraphWidth: int,
                maxGraphHeight: int,
                withWeight: bool,
            ) -> str:
        lines: List[str] = []
        if self.iterations <= 0:
            lines.append("ERROR: You have to iterate over at least the starting state!")
        lines.append(f"Loop iterations: {self.iterations}")
        if drawGraph:
            graphStr = self.graph.drawGraph(maxGraphWidth, maxGraphHeight, self.start, self.goal, self.path)
            if graphStr:
                lines.append(graphStr + "\n")
        if not self.success:
            lines.append(f"No path found from {self.start} to {self.goal}")
        else:
            lines.append(f"Cost of path from {self.start} to {self.goal}: {self.cost:.2f}")
            if not isinstance(self.path, list):
                lines.append("WARNING: You have not implemented extractPath!")
            else:
                # Print the path.
                pathlen = len(self.path)
                lines.append(f"Number of edges: {pathlen}")
                if pathlen <= 10:
                    lines.append(
                        str(self.path[0].start) + 
                        self.formatPathPart(True, withWeight, 0, pathlen)
                    )
                else:
                    lines.append(
                        self.formatPathPart(False, withWeight, 0, 5) + "..." +
                        self.formatPathPart(True, withWeight, pathlen-5, pathlen)
                    )
                actualCost = sum(e.weight for e in self.path)
                if self.cost != actualCost:
                    lines.append(
                        f"WARNING: the actual path cost ({actualCost}) "
                        f"differs from the reported cost ({self.cost})!"
                    )
        return '\n'.join(lines)


