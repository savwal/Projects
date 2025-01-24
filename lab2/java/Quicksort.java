import java.io.IOException;

class Quicksort extends SuffixSorter {
    ProgressBar<Void> progressBar;
    int progressBarSpanSize;

    Quicksort(SuffixArray sa) {
        super(sa);
    }

    Quicksort(SuffixArray sa, PivotSelector pivotSelector) {
        super(sa, pivotSelector);
    }

    public void sortIndex() {
        int size = this.sa.size();
        this.progressBar = new ProgressBar<>(size, "Quicksorting");
        this.progressBarSpanSize = size / 10_000;
        // Don't change this call, the second argument should be `size`:
        this.quicksort(0, size);
        this.progressBar.setValue(size);
        this.progressBar.close();
    }

    public void quicksort(int start, int end) {
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

        //---------- TASK 3b: Quicksort ---------------------------------------//
        int partitionPoint = partition(start, end);
        quicksort(start, partitionPoint);
        quicksort(partitionPoint + 1, end);
        //---------- END TASK 3b ----------------------------------------------//
    }

    public int partition(int start, int end) {
        // Hints when completing the code: 
        // - The variable `end` points to the element *after* the last one in the interval!
        // - You can use the following methods for convenience:
        //   this.sa.index, this.swap, this.sa.compareSuffixes

        // Select the pivot, and find the pivot suffix.
        int pivotIndex = pivotSelector.pivotIndex(this.sa, start, end);
        int pivotSuffix = this.sa.index[pivotIndex];

        // Swap the pivot so that it is the first element.
        if (start != pivotIndex) {
            this.swap(start, pivotIndex);
        }

        // This is the Hoare partition scheme, where the pointers move in opposite direction.
        int lo = start + 1;
        int hi = end - 1;
        
        // This is the value that will be returned in the end - the final position of the pivot.
        int newPivotIndex = -1;

        //---------- TASK 3b: Quicksort ---------------------------------------//
        while(lo <= hi) {
            this.swap(lo, hi);
            while (lo <= hi && this.sa.compareSuffixes(this.sa.index[lo], pivotSuffix) < 0) {
                lo += 1;
            }
            while (this.sa.compareSuffixes(this.sa.index[hi], pivotSuffix) > 0) {
                hi -= 1;
            }
        }

        this.swap(start, hi);
        newPivotIndex = hi;
        //---------- END TASK 3b ----------------------------------------------//

        if (debug) {
            // When debugging, print an excerpt of the suffix array.
            String pivotValue = (
                pivotSuffix + 20 <= sa.size()
                ? this.sa.text.substring(pivotSuffix, pivotSuffix + 20) + "..."
                : this.sa.text.substring(pivotSuffix)
            );
            String header = String.format("start: %d, end: %d, pivot: %s", start, end, pivotValue);
            this.sa.print(header, new int[] {start, newPivotIndex, newPivotIndex+1, end}, " <=> ");
        }

        return newPivotIndex;
    }


    public static void main(String[] args) throws IOException {
        SuffixArray sa = new SuffixArray();
        SuffixSorter sorter = new Quicksort(sa);

        // Run this for debugging.
        sorter.setDebugging(true);
        sa.setText("ABRACADABRA");
        sorter.buildIndex();
        sorter.checkIndex();
        sa.print("ABRACADABRA");

        // Some example performance tests.
        // Wait with these until you're pretty certain that your code works.
        sorter.setDebugging(false);
        String alphabet = "ABCD";
        for (int k = 1; k < 6; k++) {
            int size = k * 1_000_000;
            sa.generateRandomText(size, alphabet);
            sorter.buildIndex();
            sorter.checkIndex();
            sa.print(String.format("size: %,d, alphabet: '%s'", size, alphabet));
        }

        // What happens if you try different alphabet sizes?
        // (E.g., smaller ("AB") or larger ("ABC....XYZ"))

        // What happens if you use only "A" as alphabet?
        // (Hint: try much smaller test sizes)
    }
}

