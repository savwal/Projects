
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

// This class is designed it be run.
// It is a search prompt for searching in the specified text file.
// We assume that the suffix array has been built and stored on disk before.
// (For this, see `BuildSuffixArray`.)
public class SearchIndex {

    public static Iterable<Integer> linearSearch(SuffixArray suffixArray, String value) {
        return new Iterable<>() {
            @Override
            public Iterator<Integer> iterator() {
                return new Iterator<>() {
                    int start = 0;
                    int end = value.length();
                    @Override
                    public boolean hasNext() {
                        while (true) {
                            if (end > suffixArray.size())
                                return false;
                            if (value.equals(suffixArray.text.substring(start, end)))
                                return true;
                            start++; end++;
                        }
                    }
                    @Override
                    public Integer next() {
                        if (hasNext()) {
                            end++;
                            return start++;
                        }
                        throw new NoSuchElementException();
                    }
                };
            }
        };
    }

    public static Iterable<Integer> binarySearch(SuffixArray suffixArray, String value) {
        if (suffixArray.index == null || suffixArray.index.length == 0)
            throw new AssertionError("Index is not initialised!");
        int first = BinarySearch.binarySearchFirst(suffixArray, value);
        return new Iterable<>() {
            @Override
            public Iterator<Integer> iterator() {
                return new Iterator<>() {
                    int i = first;
                    @Override
                    public boolean hasNext() {
                        if (i < 0 || i >= suffixArray.index.length)
                            return false;
                        int start = suffixArray.index[i];
                        int end = start + value.length();
                        return value.equals(suffixArray.text.substring(start, end));
                    }
                    @Override
                    public Integer next() {
                        if (hasNext())
                            return suffixArray.index[i++];
                        throw new NoSuchElementException();
                    }
                };
            }
        };
    }



    public static final int NUM_MATCHES = 10;
    public static final int CONTEXT = 40;

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        String[] printChoices = {"always", "ask", "never"};

        CommandParser parser = new CommandParser("SearchIndex", "Search tool for text files.");
        parser.addArgument("--textfile", "-f", "text file (utf-8 encoded)")
            .makeRequired();
        parser.addArgument("--linear-search", "-l", "use linear search (much slower than binary search)")
            .makeTrueOption();
        parser.addArgument("--num-matches", "-n", "number of matches to show (default: "+NUM_MATCHES+" matches)")
            .makeInteger().setDefault(NUM_MATCHES);
        parser.addArgument("--context", "-c", "context to show to the left and right (default: "+CONTEXT+" characters)")
            .makeInteger().setDefault(CONTEXT);
        parser.addArgument("--trim-lines", "-t", "trim each search result to the matching line")
            .makeTrueOption();
        parser.addArgument("--search-string", "-s", "string(s) to search for")
            .makeList();
        parser.addArgument("--print-matches", "-p", "whether to print the matches (default: always)")
            .setChoices(printChoices).setDefault("always");

        CommandParser.Namespace options = parser.parseArgs(args);

        String textFile = options.getString("textfile");
        boolean linearSearch = options.getBoolean("linear-search");
        int numMatches = options.getInteger("num-matches");
        int context = options.getInteger("context");
        boolean trimLines = options.getBoolean("trim-lines");
        List<String> searchStrings = options.getStringList("search-string");
        String printChoice = options.getString("print-matches");

        // Create a stopwatch to time the execution of each phase of the program.
        Stopwatch stopwatch = new Stopwatch();

        // Read the text file.
        SuffixArray suffixArray = new SuffixArray();
        try {
            suffixArray.loadText(textFile);
        } catch (NoSuchFileException e) {
            System.err.format("\nERROR: I cannot find the text file '%s'.\n" +
                "Make sure you specify both the directory and the filename correctly.\n\n", textFile);
            System.exit(1);
        }
        stopwatch.finished(String.format("Reading %s chars from '%s'", suffixArray.size(), textFile));

        // Load the index if we're using it.
        if (!linearSearch) {
            try {
                suffixArray.loadIndex();
            } catch (NoSuchFileException e) {
                System.err.format("\nERROR: I cannot find the index file '%s'.\n" +
                    "Make sure you build the index before using it.\n\n", suffixArray.indexFile);
                System.exit(1);
            }
            stopwatch.finished("Loading the index");
        }

        // Set up the search loop.
        System.out.println();
        Scanner input;
        String prompt;
        if (searchStrings != null && !searchStrings.isEmpty()) {
            input = new Scanner(String.join("\n", searchStrings));
            prompt = "";
        } else {
            input = new Scanner(System.in);
            prompt = "Search key (ENTER to quit): ";
        }

        boolean printMatches = true;
        Scanner inputPrintMatches = null;
        if (printChoice.compareTo("never") == 0) {
            printMatches = false;
        } else if (printChoice.compareTo("ask") == 0) {
            inputPrintMatches = new Scanner(System.in);
        }

        // The main REPL (read-eval-print loop).
        // Read search key from input line, exit if there is no more input.
        while (true) {
            System.out.print(prompt);
            System.out.flush();
            if (!input.hasNextLine())
                break;
            String value = input.nextLine();
            if (value.isEmpty())
                break;
            
            String valueToPrint = value.replaceAll("(\\n|\\r)+", " ");

            if (printChoice.compareTo("ask") == 0) {
                System.out.format("Print matches for '%s'? (yes/y to print, no/n/ENTER to continue): ", valueToPrint);                
                String inputDoPrint = inputPrintMatches.nextLine();
                if (inputDoPrint.isEmpty() || inputDoPrint.matches("no?")) {
                    printMatches = false;
                } else if (inputDoPrint.matches("y(es)?")) {
                    printMatches = true;
                } else {
                    System.out.println("ERROR: use yes/y to print or no/n/ENTER to continue\n");
                    break;
                }
            }

            // Search for the first occurrence of the search string.
            System.out.format("Searching for '%s':\n", valueToPrint);
            stopwatch.reset();
            Iterable<Integer> results = (
                linearSearch
                ? linearSearch(suffixArray, value)
                : binarySearch(suffixArray, value)
            );

            // Iterate through the search results.
            int ctr = 0;
            String plus = "";
            for (int start : results) {
                if (printMatches) {
                    int end = start + value.length();
                    printKeywordInContext(suffixArray.text, start, end, context, trimLines);
                }
                ctr++;
                if (ctr >= numMatches) {
                    plus = "+";
                    break;
                }
            }
            stopwatch.finished(String.format("Finding %d%s matches", ctr, plus));
            System.out.println();
        }
    }

    public static void printKeywordInContext(String text, int start, int end, int context, boolean trimLines) {
        // Print one match (between positions [start...end-1]),
        // together with `args.context` bytes of context before and after.

        int contextStart = Math.max(0, start - context);
        int contextEnd = Math.min(text.length(), end + context);

        String prefix = text.substring(contextStart, start);
        String found  = text.substring(start, end);
        String suffix = text.substring(end, contextEnd);

        if (trimLines) {
            int i;
            if ((i = prefix.lastIndexOf("\n")) >= 0) {
                prefix = prefix.substring(i+1);
            }
            if ((i = suffix.indexOf("\n")) >= 0) {
                suffix = suffix.substring(0, i);
            }
        }

        found = found.replaceAll("\\n", " ").replaceAll("\\r", "");
        prefix = prefix.replaceAll("\\n", " ").replaceAll("\\r", "");
        suffix = suffix.replaceAll("\\n", " ").replaceAll("\\r", "");

        System.out.format("%8d:  %" + context + "s|%s|%-" + context + "s\n", start, prefix, found, suffix);
    }
}
