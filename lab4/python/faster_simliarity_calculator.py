
from progress_bar import ProgressBar
from simliarity_calculator import SimilarityCalculator


class FasterSimilarityCalculator(SimilarityCalculator):

    def buildNgramIndex(self):
        """
        Phase 2: Build index of n-grams.
        Uses the instance varable `fileNgrams`.
        Stores the result in the instance variable `ngramIndex`.
        """

        #---------- TASK 2a: Build n-gram index ------------------------------#
        # Note: You can use a ProgressBar in your outermost loop if you want.
        # See in `similarity_calculator.py` how it is used.

        # TODO: Replace these lines with your solution! 
        raise NotImplementedError()
        #---------- END TASK 2a ----------------------------------------------#


    def computeIntersections(self):
        """
        Phase 3: Count how many n-grams each pair of files has in common.
        This version should use the `ngramIndex` to make this function much more efficient.
        Stores the result in the instance variable `intersections`.
        """

        #---------- TASK 2b: Compute n-gram intersections --------------------#
        # Note 1: Intersection is a commutative operation, i.e., A ∩ B == B ∩ A.
        # This means that you can restrict yourself to only compute the intersection 
        # for path pairs (p,q) where p < q.

        # Note 2: You can use a ProgressBar in your outermost loop if you want.
        # See in `similarity_calculator.py` how it is used.

        # TODO: Replace these lines with your solution! 
        raise NotImplementedError()
        #---------- END TASK 2b ----------------------------------------------#


