"""
This is the main file for finding paths in graphs.
Depending on the command line arguments,
it creates different graphs and runs different search algorithms.
"""

import sys
from typing import Any

from command_parser import CommandParser
from stopwatch import Stopwatch
from graph import Graph, V
from adjacency_graph import AdjacencyGraph
from npuzzle import NPuzzle
from gridgraph import GridGraph
from wordladder import WordLadder
from search_algorithm import SearchAlgorithm
from search_random import SearchRandom
from search_ucs import SearchUCS
from search_astar import SearchAstar


# Settings for showing the solution - you can change these if you want.
showPathWeights = True
showGridGraph = True
maxGridGraphWidth = 100
maxGridGraphHeight = 25


searchAlgorithms = [
    "random",
    "ucs",
    "astar",
]

graphTypes = [
    "AdjacencyGraph",
    "GridGraph",
    "NPuzzle",
    "WordLadder",
]

parser = CommandParser(description=__doc__.strip())
parser.add_argument("--algorithm", "-a", required=True, choices=searchAlgorithms,
                    help="search algorithm to test")
parser.add_argument("--graphtype", "-t", required=True, choices=graphTypes,
                    help="type of graph")
parser.add_argument("--graph", "-g", required=True, 
                    help="the graph itself")
parser.add_argument("--queries", "-q", nargs="*", 
                    help="list of alternating start and goal states")


def main():
    options = parser.parse_args()

    graph: Graph[Any]
    if options.graphtype == "AdjacencyGraph":
        graph = AdjacencyGraph(options.graph)
    elif options.graphtype == "GridGraph":
        graph = GridGraph(options.graph)
    elif options.graphtype == "NPuzzle":
        graph = NPuzzle(options.graph)
    elif options.graphtype == "WordLadder":
        graph = WordLadder(options.graph)
    else:
        raise ValueError("Unknown graph type")

    algorithm: SearchAlgorithm[Any]
    if options.algorithm == "random":
        algorithm = SearchRandom(graph)
    elif options.algorithm == "ucs":
        algorithm = SearchUCS(graph)
    elif options.algorithm == "astar":
        algorithm = SearchAstar(graph)
    else:
        raise

    if not options.queries:
        searchInteractive(algorithm, graph)
    else:
        if len(options.queries) % 2 != 0:
            raise ValueError("There must be an even number of query states")
        for i in range(0, len(options.queries), 2):
            start = options.queries[i]
            goal = options.queries[i+1]
            searchOnce(algorithm, graph, start, goal)


def searchInteractive(algorithm: SearchAlgorithm[V], graph: Graph[V]):
    print(graph)
    print()
    while True:
        print("(ENTER to quit)")
        start = input("Start: ")
        if not start.strip():
            break
        goal = input("Goal: ")
        print()
        searchOnce(algorithm, graph, start, goal)
    print("Bye bye, hope to see you again soon!")


def searchOnce(algorithm: SearchAlgorithm[V], graph: Graph[V], start: str, goal: str):
    try:
        startVertex = graph.parseVertex(start.strip())
        goalVertex = graph.parseVertex(goal.strip())
    except ValueError as e:
        print("Parse error!", file=sys.stderr)
        print(e, file=sys.stderr)
        print(file=sys.stderr)
        return

    print(f"Searching: {startVertex} --> {goalVertex}")
    stopwatch = Stopwatch()
    result = algorithm.search(startVertex, goalVertex)
    stopwatch.finished("Searching the graph")
    print(result.toString(showGridGraph, maxGridGraphWidth, maxGridGraphHeight, showPathWeights))
    print()



if __name__ == '__main__':
    main()


