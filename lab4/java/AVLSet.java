
public class AVLSet<Elem extends Comparable<Elem>> extends SimpleSet<Elem> {
    AVLSet() {
        this.map = new AVLMap<>();
    }
}

