import java.nio.file.Path;
import java.security.Policy;
import java.util.ArrayList;


public class FasterSimilarityCalculator extends SimilarityCalculator {

    /** 
     * Phase 2: Build index of n-grams.
     * Uses the instance varable `fileNgrams`.
     * Stores the result in the instance variable `ngramIndex`.
     */
    @Override
    public void buildNgramIndex() {
        //---------- TASK 2a: Build n-gram index ----------------------------//
        // Note: You can use a ProgressBar in your outermost loop if you want.
        // See in `similarityCalculator.java` how it is used.
        for (Path path : new ProgressBar<>(this.fileNgrams, this.fileNgrams.size(), "Building n-gram index")) {
            SimpleSet<Ngram> ngrams = fileNgrams.get(path);
            for (Ngram ngram : ngrams) {
                ngramIndex.get(ngram).add(path);
            }
        }
        //---------- END TASK 2a --------------------------------------------//
    }

    /**
     * Phase 3: Count how many n-grams each pair of files has in common.
     * This version should use the `ngramIndex` to make this function much more efficient.
     * Stores the result in the instance variable `intersections`.
     */
    @Override
    public void computeIntersections() {
        //---------- TASK 2b: Compute n-gram intersections ------------------//
        // Note 1: Intersection is a commutative operation, i.e., A ∩ B == B ∩ A.
        // This means that you restrict yourself to only compute the intersection 
        // for path pairs (p,q) where p < q (in Java: p.compareTo(q) < 0).

        // Note 2: You can use a ProgressBar in your outermost loop if you want.
        // See in `similarityCalculator.java` how it is used.

        for (Ngram ngram : new ProgressBar<>(this.ngramIndex, this.ngramIndex.size(), "Computing intersections")) {
            SimpleSet<Path> paths = ngramIndex.get(ngram);
            for (Path path1 : paths) {
                for (Path path2 : paths) {
                    if (path1.compareTo(path2) < 0) {
                        PathPair pair = new PathPair(path1, path2);
                        this.intersections.get(pair).add(ngram);
                    }
                }
            }
        }
        //---------- END TASK 2b --------------------------------------------//
    }

}

