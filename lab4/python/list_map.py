from typing import List, Tuple, Optional, Iterator, Type

from simple_map import SimpleMap, Key, Value


class ListMap(SimpleMap[Key, Value]):
    """
    SimpleMap implemented as an unsorted list.
    This is a very stupid idea and should not be used in practice!
    """
    elements: List[Tuple[Key, Value]]

    def __init__(self, defaultValueSupplier: Optional[Type[Value]] = None):
        super().__init__(defaultValueSupplier)
        self.elements = []

    def size(self) -> int:
        return len(self.elements)
    
    def get(self, key: Key) -> Value:
        assert key is not None, "argument must not be None"
        for key_, value in self.elements:
            if key == key_:
                return value
        # If the key does not exist, create and return a default value:
        value = self.defaultValueSupplier()
        if value is not None:
            self.elements.append((key, value))
        return value
    
    def put(self, key: Key, value: Value):
        assert key is not None, "argument must not be None"
        for i, (key_, _) in enumerate(self.elements):
            if key == key_:
                self.elements[i] = (key, value)
                return
        self.elements.append((key, value))

    def clear(self):
        self.elements = []

    def __iter__(self) -> Iterator[Key]:
        for key, _ in self.elements:
            yield key

    def __str__(self) -> str:
        if self.isEmpty(): return "ListMap (empty)"
        return f"ListMap (size {self.size()})"

    ###########################################################################
    # Validation

    def validate(self):
        duplicateKeys = self.size() != len({key for (key, _) in self.elements})
        if duplicateKeys: 
            raise ValueError(f"List map contains duplicate keys: {self}")


