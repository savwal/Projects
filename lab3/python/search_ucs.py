from typing import Generic, List, Set, Optional
from dataclasses import dataclass
from functools import total_ordering

from edge import Edge, V
from priority_queue import PriorityQueue
from search_algorithm import SearchAlgorithm
from search_result import SearchResult


class SearchUCS(SearchAlgorithm[V]):
    """
    Run uniform-cost search for finding the shortest path.
    """

    def search(self, start: V, goal: V) -> SearchResult[V]:
        iterations = 0
        pqueue: PriorityQueue[UCSEntry[V]] = PriorityQueue()

        #---------- TASK 1a+c: Uniform-cost search ----------------------------------#
        # TODO: Replace these lines with your solution!
        # Note: Every time you remove a state from the priority queue, you should increment `iterations`.
        raise NotImplementedError()
        #---------- END TASK 1a+c ------------------------------------------------#

        # Return this if you didn't find a path
        return SearchResult(self.graph, False, start, goal, -1, None, iterations)


    def extractPath(self, entry: 'UCSEntry[V]') -> List[Edge[V]]:
        """
        Extracts the path from the start to the current priority queue entry.
        """
        #---------- TASK 1b: Extracting the path ---------------------------------#
        # TODO: Replace these lines with your solution!
        return NotImplemented
        #---------- END TASK 1b --------------------------------------------------#


@total_ordering         # This means that we only have to define __lt__
@dataclass(frozen=True) # 'frozen' means that we cannot modify elements after creation
class UCSEntry(Generic[V]):
    """
    Entries to put in the priority queues in `SearchUCS`.
    To create an instance: `UCSEntry(state, lastEdge, backPointer, costToHere)`
    """
    state: V
    lastEdge: Optional[Edge[V]]           # None for the starting entry
    backPointer: Optional['UCSEntry[V]']  # None for the starting entry
    costToHere: float

    def __lt__(self, other: 'UCSEntry[V]') -> bool:
        """The entry `self` is strictly smaller than `other`."""
        return self.costToHere < other.costToHere



