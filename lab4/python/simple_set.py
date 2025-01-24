from typing import Iterable, Iterator, Any

from simple_map import SimpleMap, Key


class SimpleSet(Iterable[Key]):
    """
    A set implemented as a SimpleMap from keys to nothing at all.
    (It's only the keys that are used.)
    """
    _map: SimpleMap[Key, Any]

    def isEmpty(self) -> bool:
        """Returns true if there are no elements."""
        return self._map.isEmpty()

    def contains(self, key: Key) -> bool:
        """Returns true if the element is in the set."""
        return self._map.containsKey(key)

    def __len__(self) -> int:
        return self.size()

    def size(self) -> int:
        """Returns the number of elements."""
        return self._map.size()

    def add(self, key: Key):
        """Adds the given element, does nothing if it already exists."""
        self._map.put(key, ...)

    def clear(self):
        """Removes all elements."""
        self._map.clear()

    def __iter__(self) -> Iterator[Key]:
        """Returns an iterator over the elements in the set."""
        return iter(self._map)

    def __str__(self) -> str:
        """Returns a string representation of the set."""
        return f"SimpleSet({self._map})"

    def validate(self):
        """
        Validates that the set is correctly implemented according to the specification.
        Raises a ValueError if there is anything wrong.
        """
        self._map.validate()


