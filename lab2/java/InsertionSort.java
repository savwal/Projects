import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

class InsertionSort extends SuffixSorter {

    InsertionSort(SuffixArray sa) {
        super(sa);
    }

    public void sortIndex() {
        // Use the following for convenience:
        // * this.sa.compareSuffixes
        // * this.sa.index
        // * this.swap

        for (int i : ProgressBar.range(0, this.sa.index.length, "Insertion sorting")) {
            //---------- TASK 3a: Insertion sort ------------------------------//
            int hi = i;
            int lo = 0;

            while (lo < hi) {
                int mid = (lo + hi - 1)/2;
                if (this.sa.compareSuffixes(this.sa.index[i], this.sa.index[mid]) > 0) {
                    lo = mid + 1;
                } else {
                    hi = mid;
                }
            }

            int tmp = this.sa.index[i];
            for (int j = i; j > hi; j--) {
                this.sa.index[j] = this.sa.index[j-1];
            }
            this.sa.index[hi] = tmp;
            //---------- END TASK 3a ------------------------------------------//

            if (this.debug) {
                // When debugging, print an excerpt of the suffix array.
                this.sa.print("i = " + i, new int[] {0, i+1}, " * ");
            }
        }
    }


    public static void main(String[] args) {
        SuffixArray sa = new SuffixArray();
        SuffixSorter sorter = new InsertionSort(sa);

        // Run this for debugging.
        sorter.setDebugging(true);
        sa.setText("ABRACADABRA");
        sorter.buildIndex();
        sorter.checkIndex();
        sa.print("ABRACADABRA");

        // Some example performance tests.
        // Wait with these until you're pretty certain that your code works.
        sorter.setDebugging(false);
        String alpabet = "ABCD";
        for (int k = 1; k < 6; k++) {
            int size = k * 10_000;
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

