
import java.io.IOException;

public class BinarySearch {

    public static int binarySearchFirst(SuffixArray sa, String value) {
        int[] index = sa.index;
        
        int result = -1;
        //---------- TASK 4: Binary search returning the first index ----------//
        int lo = 0;
        int hi = index.length - 1;
        
        while (lo < hi) {
            int mid = (lo + hi)/2;
            if (sa.compareSuffixToValue(index[mid], value) < 0) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        if (sa.compareSuffixToValue(index[lo], value) == 0) result = lo;
        //---------- END TASK 4 -----------------------------------------------//

        return result;
    }

    public static void main(String[] args) throws IOException {
        SuffixArray sa = new SuffixArray();
        SuffixSorter sorter = new Quicksort(sa);

        sa.setText("ABRACADABRA");
        sorter.buildIndex();
        sorter.checkIndex();
        sa.print("Suffix array", new int[]{0, sa.size()}, "   ");

        // Search for some strings, e.g.: "ABRA", "RAC", "RAD", "AA"
        String value = "AB";
        System.out.format("Searching for: '%s'%n", value);
        int i = binarySearchFirst(sa, value);
        if (i < 0) {
            System.out.format("--> String not found%n");
        } else {
            int pos = sa.index[i];
            System.out.format("--> String found at index: %d --> text position: %d%n", i, pos);
        }

        
        // Next step is to search in a slightly larger text file, such as:
        sa.loadText("texts/bnc-tiny.txt");
        sorter.buildIndex();

        value = "zzz";
        System.out.format("Searching for: '%s'%n", value);
        i = binarySearchFirst(sa, value);
        if (i < 0) {
            System.out.format("--> String not found%n");
        } else {
            int pos = sa.index[i];
            System.out.format("--> String found at index: %d --> text position: %d%n", i, pos);
        }

        // Try, e.g., to search for the following strings:
        // "and", "ands", "\n\n", "zz", "zzzzz"
       
    }
}

