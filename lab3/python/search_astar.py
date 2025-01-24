from typing import Set
from dataclasses import dataclass
from functools import total_ordering

from priority_queue import PriorityQueue
from search_algorithm import SearchResult
from search_ucs import SearchUCS, UCSEntry, V


class SearchAstar(SearchUCS[V]):
    """
    Run the A* algorithm for finding the shortest path.
    """

    def search(self, start: V, goal: V) -> SearchResult[V]:
        iterations = 0
        pqueue: PriorityQueue[AstarEntry[V]] = PriorityQueue()

        #---------- TASK 3: A* search, the main search algorithm -----------------#
        # TODO: Replace these lines with your solution!
        # Note: Every time you remove a state from the priority queue, you should increment `iterations`.
        raise NotImplementedError()
        #---------- END TASK 3 ---------------------------------------------------#

        # Return this if you didn't find a path.
        return SearchResult(self.graph, False, start, goal, -1, None, iterations)


@total_ordering         # This means that we only have to define __lt__
@dataclass(frozen=True) # 'frozen' means that we cannot modify elements after creation
class AstarEntry(UCSEntry[V]):
    """
    Entries to put in the priority queues in `searchAstar`.
    This inherits all instance variables from `UCSEntry`, plus the ones you add.

    To create an instance: `AstarEntry(state, lastEdge, backPointer, costToHere, ...)`
    """
    # These are inherited from UCSEntry:
    # state: V
    # lastEdge: Optional[Edge[V]]             # None for the starting entry
    # backPointer: Optional['AstarEntry[V]']  # None for the starting entry
    # costToHere: float

    #---------- TASK 3: A* search, priority queue entries --------------------#
    # TODO: Add new fields here:
    ...

    # TODO: Complete the implementation of less-than:
    def __lt__(self, other: 'UCSEntry[V]'):
        '''The entry `self` is strictly smaller than `other`.'''
        assert isinstance(other, AstarEntry)
        raise NotImplementedError()
    #---------- END TASK 3 ---------------------------------------------------#



