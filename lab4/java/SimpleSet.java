import java.util.Iterator;

/**
 * A set implemented as a SimpleMap from keys to nothing at all.
 * (It's only the keys that are used.)
 */
abstract class SimpleSet<Key> implements Iterable<Key> {
    // There are no good builtin singleton classes in Java, 
    // so we use a boolean instead:
    SimpleMap<Key, Boolean> map;

    /**
     * Returns true if there are no elements.
     */
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    /**
     * Returns true if the element is in the set.
     */
    public boolean contains(Key key) {
        return this.map.containsKey(key);
    }

    /**
     * Returns the number of elements.
     */
    public int size() {
        return this.map.size();
    }

    /**
     * Adds the given element, does nothing if it already exists.
     */
    public void add(Key key) {
        this.map.put(key, true);
    }

    /**
     * Removes all elements.
     */
    public void clear() {
        this.map.clear();
    }

    /**
     * Returns an iterator over the elements in the set.
     */
    public Iterator<Key> iterator() {
        return this.map.iterator();
    }

    /** 
     * Returns a string representation of the set.
     */
    public String toString() {
        return "SimpleSet(" + this.map + ")";
    }

    /**
     * Validates that the set is correctly implemented according to the specification.
     * @throws IllegalArgumentException if there is anything wrong.
     */
    public void validate() {
        this.map.validate();
    };

}

