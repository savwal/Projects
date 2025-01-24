
class MultikeyQuicksort extends SuffixSorter {
    ProgressBar<Void> progressBar;
    int progressBarSpanSize;

    MultikeyQuicksort(SuffixArray sa) {
        super(sa);
    }

    MultikeyQuicksort(SuffixArray sa, PivotSelector pivotSelector) {
        super(sa, pivotSelector);
    }

    public void sortIndex() {
        int size = this.sa.size();
        this.progressBarSpanSize = size / 10_000;
        this.progressBar = new ProgressBar<>(size, "Multikey quicksorting");
        // Don't change this call, the second argument should be `size`:
        this.multikeyQuicksort(0, size, 0);
        this.progressBar.setValue(size);
        this.progressBar.close();
    }

        public void multikeyQuicksort(int start, int end, int offset) {
        // Hint when completing the code: 
        // The variable `end` points to the element *after* the last one in the interval!

        int size = end - start;

        // Base case: the list to sort has at most one element.
        if (size <= 1) {
            return;
        }

        // Don't update the progress bar unnecessarily often.
        if (size >= progressBarSpanSize) {
            this.progressBar.setValue(start);
        }

        //---------- TASK 5: Multikey quicksort -------------------------------//
        IndexPair middle = partition(start, end, offset);
        multikeyQuicksort(start, middle.start, offset);
        multikeyQuicksort(middle.start, middle.end, offset + 1);
        multikeyQuicksort(middle.end, end, offset);
        //---------- END TASK 5 -----------------------------------------------//
    }

    private class IndexPair {
        int start;
        int end;
        IndexPair(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    private char getCharAtOffset(int i, int offset) {
        int pos = this.sa.index[i] + offset;
        return pos < this.sa.text.length() ? this.sa.text.charAt(pos) : '\0';
    };

    public IndexPair partition(int start, int end, int offset) {
        // Hints when completing the code: 
        // - The variable `end` points to the element *after* the last one in the interval!
        // - You can use the following methods for convenience:
        //   getCharAtOffset, this.swap

        // Select the pivot, and find the pivot character.
        int pivotIndex = pivotSelector.pivotIndex(this.sa, start, end);
        char pivotChar = getCharAtOffset(pivotIndex, offset);

        // Swap the pivot so that it is the first element.
        if (start != pivotIndex) {
            this.swap(start, pivotIndex);
        }

        // Initialise the middle pointers.
        int middleStart = start;
        int middleEnd = end;

        //---------- TASK 5: Multikey quicksort -------------------------------//
        int processIndex = middleStart + 1;
        char charAtIndexOffset;
        while (processIndex < middleEnd) {
            charAtIndexOffset = getCharAtOffset(processIndex, offset);
            if (pivotChar < charAtIndexOffset) {
                this.swap(processIndex, middleEnd - 1);
                middleEnd -= 1;
            } else if (pivotChar > charAtIndexOffset) {
                this.swap(processIndex, middleStart);
                middleStart += 1;
                processIndex += 1;
            } else {
                processIndex += 1;
            }
        }
        //---------- END TASK 5 -----------------------------------------------//

        if (debug) {
            // When debugging, print an excerpt of the suffix array.
            String pivotValue = ".".repeat(offset) + String.valueOf(pivotChar);
            String header = String.format("start: %d, end: %d, pivot: %s", start, end, pivotValue);
            sa.print(header, new int[] {start, middleStart, middleEnd, end}, " <=> ");
        }

        // Return the new interval containing all elements selected by the pivot char.
        // Note that `middleEnd` should point to the element *after* the last in the interval.
        return new IndexPair(middleStart, middleEnd);
    }


    public static void main(String[] args) {
        SuffixArray sa = new SuffixArray();
        SuffixSorter sorter = new MultikeyQuicksort(sa);

        // Run this for debugging.
        sorter.setDebugging(true);
        sa.setText("ABRACADABRA");
        sorter.buildIndex();
        sorter.checkIndex();
        sa.print("ABRACADABRA");

        // Some example performance tests.
        // Wait with these until you're pretty certain that your code works.
        sorter.setDebugging(false);
        String alpabet = "ABCDEFGHIJKLMNOP";
        for (int k = 1; k < 6; k++) {
            int size = k * 2_000_000;
            sa.generateRandomText(size, alpabet);
            sorter.buildIndex();
            sorter.checkIndex();
            sa.print(String.format("size: %,d, alphabet: '%s'", size, alpabet));
        }

        // What happens if you try different alphabet sizes?
        // (E.g., smaller ("AB") or larger ("ABC....XYZ"))

        // What happens if you use only "A" as alphabet?
        // (Hint: try much smaller test sizes)
    }
}

