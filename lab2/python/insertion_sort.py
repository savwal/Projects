
from suffix_array import SuffixArray
from suffix_sorter import SuffixSorter
from progress_bar import ProgressBar


class InsertionSort(SuffixSorter):

    def sortIndex(self):
        index = self.sa.index
        compareSuffixes = self.sa.compareSuffixes
        swap = self.swap

        for i in ProgressBar(range(len(index)), description="Insertion sorting"):
            #---------- TASK 3a: Insertion sort --------------------------#
            # TODO: Replace these lines with your solution!
            raise NotImplementedError()
            #---------- END TASK 3 -------------------------------------------#

            if self.debug:
                # When debugging, print an excerpt of the suffix array.
                self.sa.print(f"i = {i}", [0, i+1], " * ")


def main():
    sa: SuffixArray = SuffixArray()
    sorter: SuffixSorter = InsertionSort(sa)

    # Run this for debugging.
    sorter.setDebugging(True)
    sa.setText("ABRACADABRA")
    sorter.buildIndex()
    sorter.checkIndex()
    sa.print("ABRACADABRA")

    """
    # Some example performance tests.
    # Wait with these until you're pretty certain that your code works.
    sorter.setDebugging(False)
    alphabet = "ABCD"
    for k in range(1, 6):
        size = k * 1_000
        sa.generateRandomText(size, alphabet)
        sorter.buildIndex()
        sorter.checkIndex()
        sa.print(f"size: {size:,d}, alphabet: '{alphabet}'")
    """

    # What happens if you try different alphabet sizes?
    # (E.g., smaller ("AB") or larger ("ABC....XYZ")

    # What happens if you use only "A" as alphabet?
    # (Hint: try much smaller test sizes)


if __name__ == "__main__":
    main()

