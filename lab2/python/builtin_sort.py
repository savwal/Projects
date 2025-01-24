
from functools import cmp_to_key

from suffix_sorter import SuffixSorter


class BuiltinSort(SuffixSorter):
    def sortIndex(self):
        self.sa.index.sort(
            key = cmp_to_key(self.sa.compareSuffixes)
        )


