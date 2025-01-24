"""
Command-line tool for detecting similar files.
"""

from pathlib import Path
from typing import Dict, Any, Tuple, Type

from simple_map import SimpleMap
from list_map import ListMap
from bst_map import BSTMap
from avl_map import AVLMap
from simple_set import SimpleSet
from list_set import ListSet
from bst_set import BSTSet
from avl_set import AVLSet
from simliarity_calculator import SimilarityCalculator
from faster_simliarity_calculator import FasterSimilarityCalculator
from command_parser import CommandParser
from stopwatch import Stopwatch


mapImplementations: Dict[str, Tuple[Type[SimpleMap[Any, Any]], Type[SimpleSet[Any]]]] = {
    "list": (ListMap, ListSet),
    "bst": (BSTMap, BSTSet),
    "avl": (AVLMap, AVLSet),
}

parser = CommandParser(description=__doc__.strip())
parser.add_argument("--documents", "-d", required=True, type=Path,
                    help="path to directory of documents")
parser.add_argument("--index", "-i", action="store_true",
                    help="use the optimised file index (default: use the slow version)")
parser.add_argument("--map", "-m", choices=list(mapImplementations), default="list",
                    help="map implementation (default: use key-value list)")
parser.add_argument("--ngram", "-n", type=int, default=5,
                    help="ngram size (default: 5)")
parser.add_argument("--limit", "-l", type=int, default=10,
                    help="limit the number of similar file pairs (default: 10)")
parser.add_argument("--similarity", "-s", choices=SimilarityCalculator.similarityMeasures, default='absolute',
                    help="similarity measure (default: 'absolute')")


def main():
    options = parser.parse_args()
    if not options.documents.is_dir():
        raise FileNotFoundError(f"The directory '{options.documents}' does not exist")

    calculator = SimilarityCalculator()
    if options.index: calculator = FasterSimilarityCalculator()

    # Initialise the maps.
    mapClass, setClass = mapImplementations[options.map]
    calculator.fileNgrams = mapClass(setClass)
    calculator.ngramIndex = mapClass(setClass)
    calculator.intersections = mapClass(setClass)

    # Find all .txt files in the directory and sort the filenames.
    paths = sorted(options.documents.glob('*.txt'))
    if not paths:
        raise FileNotFoundError("Empty directory")

    # Create stopwatches time the execution of each phase of the program.
    stopwatchTotal = Stopwatch()
    stopwatch = Stopwatch()

    # Phase 1: Read n-grams from all input files.
    calculator.readNgramsFromFiles(paths, options.ngram)
    calculator.fileNgrams.validate()
    stopwatch.finished("Reading all input files")

    # Phase 2: Build index of n-grams.
    calculator.buildNgramIndex()
    calculator.ngramIndex.validate()
    stopwatch.finished("Building n-gram index")

    # Phase 3: Compute the n-gram intersections of all file pairs.
    calculator.computeIntersections()
    calculator.intersections.validate()
    stopwatch.finished("Computing intersections")

    # Phase 4: Find the L most similar file pairs, arranged in decreasing order of similarity.
    mostSimilar = calculator.findMostSimilar(options.limit, measure=options.similarity)
    stopwatch.finished("Finding the most similar files")

    stopwatchTotal.finished("In total the program")

    # Print out some statistics.
    print()
    print("Balance statistics:")
    print(f"  fileNgrams: {calculator.fileNgrams}")
    print(f"  ngramIndex: {calculator.ngramIndex}")
    print(f"  intersections: {calculator.intersections}")

    # Print out the plagiarism report!
    print()
    print("Plagiarism report:")
    print("".join(f"{measure.rpartition('-')[0]:>10s}" for measure in calculator.similarityMeasures))
    print("".join(f"{measure.rpartition('-')[-1]:>10s}" for measure in calculator.similarityMeasures))
    maxFilenameSize = max(len(pair[0].name) for pair in mostSimilar)
    for pair in mostSimilar:
        for measure in calculator.similarityMeasures:
            decimals = 0 if measure == 'absolute' else 2 if 'weighted' in measure else 3
            print(f"{calculator.similarity(pair, measure):>10.{decimals}f}", end="")
        print(f"  {pair[0].name:{maxFilenameSize}s} {pair[1].name:s}")


if __name__ == '__main__':
    main()


