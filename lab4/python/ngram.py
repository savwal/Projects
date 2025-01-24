from typing import Tuple, List

class Ngram(Tuple[str, ...]):
    """
    An n-gram is just a tuple of tokens (strings).
    """

    def __str__(self) -> str:
        return "/".join(self)

    @staticmethod
    def ngrams(input: List[str], n: int) -> List['Ngram']:
        """
        Return all n-grams of a given list of tokens.
        """
        count = len(input) - n + 1
        return [Ngram(input[i : i+n]) for i in range(count)]


