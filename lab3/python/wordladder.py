
import sys
import math
from typing import Set, FrozenSet, List, Optional

from edge import Edge
from graph import Graph


# These types are just aliases for plain strings:
Word = str
Char = str

class WordLadder(Graph[Word]):
    """
    A graph that encodes word ladders.

    The class does not store the full graph in memory, just a dictionary of words.
    The edges are then computed on demand.
    """
    dictionary: Set[Word]
    alphabet: Set[Char]

    def __init__(self, graph: Optional[str] = None):
        self.dictionary = set()
        self.alphabet = set()
        if graph:
            self.init(graph)

    def init(self, graph: str):
        """
        Creates a new word ladder graph from the given dictionary file.
        The file should contain one word per line, except lines starting with "#".
        """
        with open(graph, encoding="utf-8") as IN:
            for line in IN:
                line = line.strip()
                if line and not line.startswith('#'):
                    self.addWord(line)

    def addWord(self, word: Word):
        """
        Adds a word to the dictionary if it only contains letters.
        The word is converted to lowercase.
        """
        if word.isalpha():
            word = word.lower()
            self.dictionary.add(word)
            self.alphabet.update(word)

    def vertices(self) -> FrozenSet[Word]:
        return frozenset(self.dictionary)

    def outgoingEdges(self, v: Word) -> List[Edge[Word]]:
        """        
        Returns a list of the graph edges that originate from `word`.
        """
        #---------- TASK 2: Outgoing edges, Wordladder -----------------------#
        # TODO: Replace these lines with your solution!
        raise NotImplementedError()
        #---------- END TASK 2 -----------------------------------------------#

    def isWeighted(self) -> bool:
        return False

    def guessCost(self, v: Word, w: Word) -> float:
        """        
        Returns the guessed best cost for getting from a word to another.
        (the number of differing character positions)
        """
        #---------- TASK 4: Guessing the cost, Wordladder --------------------#
        # TODO: Replace these lines with your solution!
        # Don't forget to handle the case where the lengths differ.
        raise NotImplementedError()
        #---------- END TASK 4 -----------------------------------------------#

    def parseVertex(self, s: str) -> Word:
        word = s.lower()
        if word not in self.dictionary:
            raise ValueError(f"Unknown word: {word}")
        return word

    def __str__(self) -> str:
        return (
            f"Word ladder graph with {self.numVertices()} words.\n" +
            f"Alphabet: " +
            "".join(sorted(self.alphabet)) +
            f"\n\nRandom example words with ladder steps:\n" + 
            self.exampleOutgoingEdges(8)
        )


if __name__ == '__main__':
    _, dictionary = sys.argv
    ladder = WordLadder(dictionary)
    print(ladder)

