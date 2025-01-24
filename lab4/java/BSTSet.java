
public class BSTSet<Elem extends Comparable<Elem>> extends SimpleSet<Elem> {
    BSTSet() {
        this.map = new BSTMap<>();
    }
}

