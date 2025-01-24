
import gzip
import pickle
import random
from pathlib import Path
from typing import List, Union


class SuffixArray:
    text: str
    index: List[int]

    textFile : Path
    indexFile : Path

    # Internal constants.
    INDEX_SUFFIX = ".pix"
    ENCODING = "utf-8"

    def setText(self, text: str):
        self.text = text
        self.textFile = self.indexFile = self.index = None  # type: ignore

    def generateRandomText(self, size: int, alphabet: str):
        chars = random.choices(alphabet, k=size)
        self.setText("".join(chars))

    def loadText(self, textFile: Union[Path,str]):
        self.textFile = Path(textFile)
        self.indexFile = Path(str(self.textFile) + self.INDEX_SUFFIX)
        openFile = gzip.open if self.textFile.suffix == ".gz" else open
        with openFile(self.textFile, "rt", encoding=self.ENCODING) as IN:  # type: ignore
            self.text = IN.read()
        self.index = None  # type: ignore

    def loadIndex(self):
        with open(self.indexFile, "rb") as IN:
            self.index = pickle.load(IN)

    def size(self) -> int:
        return len(self.text)

    def compareSuffixes(self, suffix1: int, suffix2: int) -> int:
        if suffix1 == suffix2:
            return 0
        text = self.text
        end = len(text)
        while suffix1 < end and suffix2 < end:
            char1 = text[suffix1]
            char2 = text[suffix2]
            if char1 != char2:
                return -1 if char1 < char2 else 1
            suffix1 += 1
            suffix2 += 1
        return -1 if suffix1 > suffix2 else 1


    def print(self, header: str, breakpoints: List[int] = [], 
              indicators: str = "  ", context: int = 3, maxSuffix: int = 40):
        size, text, index = self.size(), self.text, self.index
        if not breakpoints:
            breakpoints = [0, size]
        digits = max(3, len(str(size)))

        print(f"--- {header}", "-" * (75-len(header)))
        print(f"{'index':>{digits+3}s}{'textpos':>{digits+6}s}      suffix")
        dotdotdot = f"{'...':>{digits+2}s}{'...':>{digits+6}s}"

        endRange = 0
        for k in breakpoints:
            startRange = k - context
            if endRange < startRange - 1:
                print(dotdotdot)
            else:
                startRange = endRange
            endRange = k + context
            for i in range(startRange, endRange):
                if 0 <= i < size:
                    ind = indicators[0]
                    for bp in range(len(breakpoints)):
                        if i >= breakpoints[bp]: 
                            ind = indicators[bp+1]
                    suffixPos = index[i]
                    suffixString = (
                        text[suffixPos : suffixPos + maxSuffix] + "..."
                        if suffixPos + maxSuffix <= size else
                        text[suffixPos :]
                    )
                    print(f"{ind} {i:{digits}d}  --> {suffixPos:{digits}d}  -->  {suffixString}")
        if endRange < size:
            print(dotdotdot)
        print("-" * 80)
        print()


