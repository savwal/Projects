
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Map;
import java.util.function.Function;

public class BuildIndex {

    static Map<String, Function<SuffixArray, SuffixSorter>> suffixSorters = Map.of(
        "insertion", InsertionSort::new,
        "quicksort", Quicksort::new,
        "multikey", MultikeyQuicksort::new,
        "builtin", BuiltinSort::new
    );

    static Map<String, PivotSelector> pivotSelectors = Map.of(
        "first", PivotSelector.TakeFirstPivot,
        "middle", PivotSelector.TakeMiddlePivot,
        "random", PivotSelector.RandomPivot,
        "median", PivotSelector.MedianOfThreePivot,
        "adaptive", PivotSelector.AdaptivePivot
    );

    public static void main(String[] args) throws IOException {
        CommandParser parser = new CommandParser("BuildIndex", "Build an inverted search index.");
        parser.addArgument("--textfile", "-f", "text file (utf-8 encoded)")
            .makeRequired();
        parser.addArgument("--algorithm", "-a", "sorting algorithm")
            .makeRequired().setChoices(suffixSorters.keySet());
        parser.addArgument("--pivot", "-p", "pivot selectors (only for quicksort algorithms)")
            .setChoices(pivotSelectors.keySet());

        CommandParser.Namespace options = parser.parseArgs(args);

        // Create stopwatches to time the execution of each phase of the program.
        Stopwatch stopwatchTotal = new Stopwatch();
        Stopwatch stopwatch = new Stopwatch();

        // Read the text file.
        String textFile = options.getString("textfile");
        SuffixArray suffixArray = new SuffixArray();
        try {
            suffixArray.loadText(textFile);
        } catch (NoSuchFileException e) {
            System.err.format("\nERROR: I cannot find the text file '%s'.\n" +
                "Make sure you specify both the directory and the filename correctly.\n\n", textFile);
            System.exit(1);
        }
        stopwatch.finished(String.format("Reading %s chars from '%s'", suffixArray.size(), textFile));

        // Select sorting algorithm.
        Function<SuffixArray, SuffixSorter> sortingAlgorithm = suffixSorters.get(options.getString("algorithm"));
        SuffixSorter sorter = sortingAlgorithm.apply(suffixArray);
        if (options.getString("pivot") != null) {
            sorter.setPivotSelector(pivotSelectors.get(options.getString("pivot")));
        }

        // Build the index using the selected sorting algorithm.
        sorter.buildIndex();
        stopwatch.finished("Building index");
 
        // Check that it's sorted.
        sorter.checkIndex();
        stopwatch.finished("Checking index");

        // Save it to an index file.
        sorter.saveIndex();
        stopwatch.finished(String.format("Saving index to '%s'", suffixArray.indexFile));

        stopwatchTotal.finished("In total the program");
    }

}

