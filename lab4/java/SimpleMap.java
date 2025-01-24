import java.util.function.Supplier;

/**
 * An abstract class of simple maps (dictionaries).
 */
abstract class SimpleMap<Key, Value> implements Iterable<Key> {
    Supplier<Value> defaultValueSupplier;

    /**
     * Returns true if the map contains no key-value mappings.
     */
    public boolean isEmpty() {
        return this.size() == 0;
    }

    /**
     * Returns true if the map contains a mapping for the specified key.
     */
    public boolean containsKey(Key key) {
        return this.get(key) != null;
    }

    /**
     * Returns the number of key-value mappings.
     */
    public abstract int size();

    /**
     * Returns the value associated with the given key. If the key doesn't exist,
     * a new default value is generated and associated with the key.
     */
    public abstract Value get(Key key);

    /**
     * Associates the specified value with the specified key.
     */
    public abstract void put(Key key, Value value);

    /**
     * Removes all keys and values.
     */
    public abstract void clear();

    /**
     * Show the contents of the map, down to a certain level.
     */
    public String show(int maxLevel) {
        return this.toString();
    }

    /**
     * Validates that the map is correctly implemented according to the specification.
     * @throws IllegalArgumentException if there is anything wrong.
     */
    public abstract void validate();

}

