#!/usr/bin/env python3

import sys
import io
from pathlib import Path
from typing import Iterable

from suffix_array import SuffixArray
from binary_search import binarySearchFirst
from command_parser import CommandParser, Namespace
from stopwatch import Stopwatch


def linearSearch(suffixArray: SuffixArray, value: str) -> Iterable[int]:
    """
    Generate all positions where the given value is found in the suffix array.
    This is done by naive linear search through the text.
    """
    for start in range(suffixArray.size() - len(value) + 1):
        end = start + len(value)
        if value == suffixArray.text[start : end]:
            yield start
    """
    # The following is too fast because str.find is implemented in C using a very good algorithm.
    # So if we use this one if will be unfair to the rest of our code which is implemented in Python.
    pos = suffixArray.text.find(value, 0)
    while pos >= 0:
        yield pos
        pos = suffixArray.text.find(value, pos+1)
    """


def binarySearch(suffixArray: SuffixArray, value: str) -> Iterable[int]:
    """
    Generate all positions where the given value is found in the suffix array.
    This is done by binary search in the suffix array index.
    """
    assert suffixArray.index, "Index is not initialised!"
    first = binarySearchFirst(suffixArray, value)
    if first < 0: 
        return
    for i in range(first, suffixArray.size()):
        start = suffixArray.index[i]
        end = start + len(value)
        if value == suffixArray.text[start : end]:
            yield start
        else:
            break


NUM_MATCHES = 10
CONTEXT = 40

parser = CommandParser(description="Search tool for text files.")
parser.add_argument("--textfile", "-f", type=Path, required=True,
                    help="text file (utf-8 encoded)")
parser.add_argument("--linear-search", "-l", action="store_true", 
                    help="use linear search (much slower than binary search)")
parser.add_argument("--num-matches", "-n", type=int, default=NUM_MATCHES,
                    help=f"number of matches to show (default: {NUM_MATCHES} matches)")
parser.add_argument("--context", "-c", type=int, default=CONTEXT,
                    help=f"context to show to the left and right (default: {CONTEXT} characters)")
parser.add_argument("--trim-lines", "-t", action="store_true",
                    help="trim each search result to the matching line")
parser.add_argument("--search-string", "-s", nargs="*",
                    help="string(s) to search for")


def main():
    options = parser.parse_args()

    # Create a stopwatch to time the execution of each phase of the program.
    stopwatch = Stopwatch()

    # Read the text file.
    suffixArray = SuffixArray()
    try:
        suffixArray.loadText(options.textfile)
    except FileNotFoundError:
        exit(f"\nERROR: I cannot find the text file '{options.textfile}'.\n"
             f"Make sure you specify both the directory and the filename correctly.\n")
    stopwatch.finished(f"Reading {suffixArray.size()} chars '{options.textfile}'")

    # Load the index if we're using it.
    if not options.linear_search:
        try:
            suffixArray.loadIndex()
        except FileNotFoundError:
            exit(f"\nERROR: I cannot find the index file '{suffixArray.indexFile}'.\n"
                 f"Make sure you build the index before using it.\n")
        stopwatch.finished("Loading the index")

    # Set up the search loop.
    print()
    if options.search_string:
        sys.stdin = io.StringIO("\n".join(options.search_string))
        prompt = ""
    else:
        prompt = "Search key (ENTER to quit): "

    # The main REPL (read-eval-print loop).
    # Read search key from input line, exit if there is no more input.
    while True:
        try:
            value = input(prompt)
            assert value
        except (AssertionError, EOFError, KeyboardInterrupt):
            break

        # Search for the first occurrence of the search string.
        print(f"Searching for {value!r}:")
        stopwatch.reset()
        results = (
            linearSearch(suffixArray, value)
            if options.linear_search else
            binarySearch(suffixArray, value)
        )

        # Iterate through the search results.
        ctr = 0
        plus = ""
        for start in results:
            end = start + len(value)
            printKeywordInContext(suffixArray.text, start, end, options)
            ctr += 1
            if ctr >= options.num_matches:
                plus = "+"
                break
        stopwatch.finished(f"Finding {ctr}{plus} matches")
        print()


def printKeywordInContext(text: str, start: int, end: int, args: Namespace):
    """
    Print one match (between positions [start...end-1]),
    together with `args.context` bytes of context before and after.
    """
    contextStart = max(0, start - args.context)
    contextEnd = min(len(text), end + args.context)

    prefix = text[contextStart : start]
    found  = text[start : end]
    suffix = text[end : contextEnd]

    if args.trim_lines:
        _, _, prefix = prefix.rpartition("\n")
        suffix, _, _ = suffix.partition("\n")

    found = found.replace("\n", " ").replace("\r", "")
    prefix = prefix.replace("\n", " ").replace("\r", "")
    suffix = suffix.replace("\n", " ").replace("\r", "")

    print(f"{start:8d}:  {prefix:>{args.context}s}|{found}|{suffix:<{args.context}s}")


if __name__ == "__main__":
    main()

