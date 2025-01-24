from abc import ABC, abstractmethod
from typing import Generic

from graph import Graph, V
from search_result import SearchResult

class SearchAlgorithm(ABC, Generic[V]):
    """
    This is an abstract class for finding paths in a given graph.
    The main method is `search` which returns a `SearchResult` object.
    """

    graph: Graph[V]

    def __init__(self, graph: Graph[V]):
        self.graph = graph

    @abstractmethod
    def search(self, start: V, goal: V) -> SearchResult[V]:
        """The main search method, taking the start and goal vertices as input."""


