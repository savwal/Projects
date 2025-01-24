import random
from typing import List

from edge import Edge, V
from search_algorithm import SearchAlgorithm
from search_result import SearchResult


class SearchRandom(SearchAlgorithm[V]):
    """
    Perform a random walk in the graph, hoping to reach the goal.
    Warning: this class will give up if the random walk
    reaches a dead end or after 10,000 iterations.
    So a negative result does not mean there is no path.
    """

    def search(self, start: V, goal: V) -> SearchResult[V]:
        iterations = 0
        cost = 0.0
        path: List[Edge[V]] = []

        current = start
        while iterations < 10_000:
            iterations += 1
            if current == goal:
                return SearchResult(self.graph, True, start, goal, cost, path, iterations)

            neighbours = self.graph.outgoingEdges(current)
            if len(neighbours) == 0:
                break

            edge = random.choice(neighbours)
            path.append(edge)
            cost += edge.weight
            current = edge.end

        return SearchResult(self.graph, False, start, goal, -1, None, iterations)


