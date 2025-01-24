
// Abstract class for Suffix sorting algorithms.

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;

public abstract class SuffixSorter {
    public SuffixArray sa;
    public PivotSelector pivotSelector;

    SuffixSorter(SuffixArray sa) {
        this(sa, PivotSelector.TakeMiddlePivot);
    }

    SuffixSorter(SuffixArray sa, PivotSelector pivotSelector) {
        this.sa = sa;
        this.setPivotSelector(pivotSelector);
    }

    public void setPivotSelector(PivotSelector pivotSelector) {
        this.pivotSelector = pivotSelector;
    }    

    public void buildIndex() {
        this.initIndex();
        this.sortIndex();
    }

    public void initIndex(){
        this.sa.index = new int[this.sa.text.length()];
        for (int i = 0; i < this.sa.index.length; i++) {
            this.sa.index[i] = i;
        }
    }

    abstract void sortIndex();

    public void swap(int i, int j) {
        int tmp = this.sa.index[i];
        this.sa.index[i] = this.sa.index[j];
        this.sa.index[j] = tmp;
    }

    public void saveIndex() throws IOException {
        try (ObjectOutputStream stream = new ObjectOutputStream(
                Files.newOutputStream(this.sa.indexFile)
            ))
        {
            stream.writeObject(this.sa.index);
        }
    }

    public void checkIndex() {
        int left = this.sa.index[0];
        int size = this.sa.size();
        ProgressBar<?> progressBar = new ProgressBar<>(this.sa.size(), "Checking index");
        int progressBarInterval = size / 10_000 + 1;
        for (int i = 1; i < size; i++) {
            if (i % progressBarInterval == 0) progressBar.setValue(i);
            int right = this.sa.index[i];
            if (this.sa.compareSuffixes(left, right) >= 0) {
                throw new AssertionError(String.format(
                    "Ordering error in positions %d-%d:'%s...' > %d'%s...'", 
                    i, left, this.sa.text.substring(left, Math.min(left+10, size)), 
                    right, this.sa.text.substring(right, Math.min(right+10, size))
                ));                    
            }
            left = right;
        }
        progressBar.setValue(size);
        progressBar.close();
    }


    boolean debug;
    public void setDebugging(boolean debug) {
        this.debug = debug;
        ProgressBar.visible = !debug;
    }
}

