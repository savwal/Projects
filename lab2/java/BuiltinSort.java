
import java.util.Arrays;
import java.util.Comparator;

class BuiltinSort extends SuffixSorter {

    BuiltinSort(SuffixArray sa) {
        super(sa);
    }

    public void sortIndex() {
        Comparator<Integer> compare = (a,b) -> this.sa.compareSuffixes(a,b);
        // Java cannot sort an int[] using a custom comparator,
        // so we have to box the elements to Integer instead. 
        // We use streams for that:
        int[] sorted = Arrays
            .stream(this.sa.index)       // now we have an IntStream
            .boxed()                // now it's a Stream<Integer>
            .sorted(compare)        // now it's sorted
            .mapToInt(i -> i)       // now back to an IntStream
            .toArray();             // and finally an int[]
        // And finally we copy the result back into the original array:
        System.arraycopy(sorted, 0, this.sa.index, 0, this.sa.index.length);
    }

}

