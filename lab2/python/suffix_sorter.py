
import pickle
from abc import abstractmethod

from progress_bar import ProgressBar
from suffix_array import SuffixArray
from pivot_selector import PivotSelector, TakeMiddlePivot


class SuffixSorter:
    """Abstract class for Suffix sorting algorithms."""
    sa: SuffixArray
    pivotSelector: PivotSelector  # This is only used by quicksort algorithms

    def __init__(self, sa: SuffixArray, pivotSelector: PivotSelector = TakeMiddlePivot):
        self.sa = sa
        self.setPivotSelector(pivotSelector)

    def setPivotSelector(self, pivotSelector: PivotSelector):
        self.pivotSelector = pivotSelector

    def buildIndex(self):
        self.initIndex()
        self.sortIndex()

    def initIndex(self):
        self.sa.index = list(range(len(self.sa.text)))

    @abstractmethod
    def sortIndex(self): ...

    def swap(self, i: int, j: int):
        self.sa.index[i], self.sa.index[j] = self.sa.index[j], self.sa.index[i]

    def saveIndex(self):
        with open(self.sa.indexFile, "wb") as OUT:
            pickle.dump(self.sa.index, OUT)

    def checkIndex(self):
        left = self.sa.index[0]
        for i in ProgressBar(range(1, self.sa.size()), description="Checking index"):
            right = self.sa.index[i]
            assert self.sa.compareSuffixes(left, right) < 0, (
                f'Ordering error in position {i}:'
                f' {left}"{self.sa.text[left : left+10]}..."'
                f' > {right}"{self.sa.text[right : right+10]}..."'
            )
            left = right

    debug: bool = False
    def setDebugging(self, debug: bool):
        self.debug = debug
        ProgressBar.visible = not debug

