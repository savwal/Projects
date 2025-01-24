import re
import math
from pathlib import Path
from typing import List, Tuple

from ngram import Ngram
from simple_map import SimpleMap
from simple_set import SimpleSet
from progress_bar import ProgressBar

PathPair = Tuple[Path, Path]


class SimilarityCalculator:
    fileNgrams: SimpleMap[Path, SimpleSet[Ngram]]
    ngramIndex: SimpleMap[Ngram, SimpleSet[Path]]
    intersections: SimpleMap[PathPair, SimpleSet[Ngram]]

    def readNgramsFromFiles(self, paths: List[Path], N: int):
        """
        Phase 1: Read in each file and chop it into n-grams.
        Stores the result in the instance variable `fileNgrams`.
        """
        for path in paths:
            with open(path, encoding="utf-8") as IN:
                # Split the input into lower-case words.
                # A "word" is here a sequence of letters, 
                # which is completely wrong but an ok approximation.
                contents = re.split(r"\W+", IN.read().lower())
            ngrams = self.fileNgrams.get(path)
            for ngram in Ngram.ngrams(contents, N):
                ngrams.add(ngram)


    def buildNgramIndex(self):
        """
        Phase 2: Build index of n-grams.
        This naive version doesn't do anything,
        you should implement it in `faster_similarity_calculation.py`.
        """


    def computeIntersections(self):
        """
        Phase 3: Count which n-grams each pair of files has in common.
        This is the naive, slow version,
        to be improved in `faster_similarity_calculation.py`.
        Uses the instance varable `fileNgrams`.
        Stores the result in the instance variable `intersections`.
        """
        for path1 in ProgressBar(self.fileNgrams, description="Computing intersections"):
            for path2 in self.fileNgrams:
                # Since intersection is a commutative operation (i.e., A ∩ B == B ∩ A),
                # it's enough to only compute the intersection for path pairs (p,q) where p < q:
                if path1 < path2:
                    for ngram1 in self.fileNgrams.get(path1):
                        for ngram2 in self.fileNgrams.get(path2):
                            if ngram1 == ngram2:
                                pair = (path1, path2)
                                intersection = self.intersections.get(pair)
                                intersection.add(ngram1)


    def findMostSimilar(self, M: int, measure: str) -> List[PathPair]:
        """
        Phase 4: find all pairs, sorted in descending order of similarity.
        Returns a list of the M path pairs that are most similar.
        Uses the instance variables `fileNgrams` and `intersections`.
        """
        return sorted(
            self.intersections, 
            key=lambda pair: self.similarity(pair, measure), 
            reverse=True
        ) [:M]


    similarityMeasures = [
        "absolute", "jaccard", "cosine", "average", 
        "weighted-jaccard", "weighted-cosine", "weighted-average",
    ]

    def similarity(self, pair: PathPair, measure: str) -> float:
        """
        Calculates a similarity score between the ngrams in A and B. The following are implemented:

        Absolute number of common ngrams:
            absolute(A, B)  =  |A ∩ B|

        Jaccard index: https://en.wikipedia.org/wiki/Jaccard_index
        This is the proportion of ngrams from A or B that are in both.
            jaccard(A, B)  =  |A ∩ B| / |A ∪ B|
                           =  |A ∩ B| / (|A| + |B| - |A ∩ B|)

        Cosine similarity, or Otsuka–Ochiai coefficient.
        This is the same as the geometric mean of p(B|A) and p(A|B)
        https://en.wikipedia.org/wiki/Cosine_similarity#Otsuka–Ochiai_coefficient
            cosine(A, B)  =  |A ∩ B| / √(|A|·|B|)
                          =  p(A,B) / √(p(A)·p(B))
                          =  √( |A∩B|/|A| · |A∩B|/|B| )
                          =  √( p(B|A) · p(A|B) )

        Arithmetic mean of p(B|A) and p(A|B):
            average(A, B)  =  ( |A∩B|/|A| + |A∩B|/|B| ) / 2
                           =  ( p(B|A) + p(A|B) ) / 2

        Weighted variants of all the above (except absolute):
            weighted-X(A, B)  =  |A∩B| · X(A, B)
        """
        if measure not in self.similarityMeasures:
            raise ValueError(f"Unknown simliarity measure: {measure}")

        nAB = len(self.intersections.get(pair) or ())

        score: float = nAB
        if measure == "absolute":
            return score

        nA = len(self.fileNgrams.get(pair[0]) or ())
        nB = len(self.fileNgrams.get(pair[1]) or ())

        if "jaccard" in measure:
            nAorB = nA + nB - nAB
            score = nAB / nAorB
        elif "cosine" in measure:
            score = math.sqrt(nAB/nA * nAB/nB)
        elif "average" in measure:
            score = (nAB/nA + nAB/nB)/2

        if "weighted" in measure:
            return nAB * score
        else:
            return score


