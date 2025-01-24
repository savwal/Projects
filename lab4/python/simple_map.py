from abc import abstractmethod
from typing import TypeVar, Generic, Iterable, Optional, Type

###############################################################################
# Comparable types: only used for type checking.
# You don't have to understand this definition.

from abc import abstractmethod
from typing import Protocol, TypeVar

class ComparableProtocol(Protocol):
    """Protocol for annotating comparable types."""
    @abstractmethod
    def __lt__(self: 'Key', other: 'Key', /) -> bool: ...
    def __gt__(self: 'Key', other: 'Key', /) -> bool: ...
    def __le__(self: 'Key', other: 'Key', /) -> bool: ...
    def __ge__(self: 'Key', other: 'Key', /) -> bool: ...

Key = TypeVar('Key', bound=ComparableProtocol)
Value = TypeVar('Value')


###############################################################################
# Simple maps

class SimpleMap(Iterable[Key], Generic[Key, Value]):
    """
    An abstract class of simple maps (dictionaries).
    """
    defaultValueSupplier: Type[Value]

    def __init__(self, defaultValueSupplier: Optional[Type[Value]] = None):
        if defaultValueSupplier:
            self.defaultValueSupplier = defaultValueSupplier
        else:
            self.defaultValueSupplier = lambda: None  # type: ignore

    def isEmpty(self) -> bool:
        """Returns true if the map contains no key-value mappings."""
        return self.size() == 0

    def containsKey(self, key: Key) -> bool:
        """Returns true if the map contains a mapping for the specified key."""
        return self.get(key) is not None

    def __len__(self) -> int:
        """Returns the number of key-value mappings."""
        return self.size()

    @abstractmethod
    def size(self) -> int:
        """Returns the number of key-value mappings."""

    @abstractmethod
    def get(self, key: Key) -> Value:
        """
        Returns the value associated with the given key. If the key doesn't exist,
        a new default value is generated and associated with the key.
        """

    @abstractmethod
    def put(self, key: Key, value: Value):
        """Associates the specified value with the specified key."""

    @abstractmethod
    def clear(self):
        """Removes all keys and values."""

    def show(self, maxLevel: int) -> str:
        """Show the contents of the map, down to a certain level."""
        return str(self)

    @abstractmethod
    def validate(self):
        """
        Validates that the map is correctly implemented according to the specification.
        Raises a ValueError if there is anything wrong.
        """


