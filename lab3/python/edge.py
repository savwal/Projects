from dataclasses import dataclass
from typing import TypeVar, Generic, Optional


V = TypeVar('V')

# Dataclasses provide a lot of methods for free, such as __init__ and comparison:
# https://docs.python.org/3/library/dataclasses.html
# 'frozen' means that we cannot modify elements after creation:
@dataclass(frozen=True)
class Edge(Generic[V]):
    """
    A class for weighted directed edges.
    """
    start: V
    end: V
    weight: float = 1

    def reverse(self) -> 'Edge[V]':
        """Returns a new edge with the direction reversed."""
        return Edge(self.end, self.start, self.weight)

    # Different ways of formatting edges.

    def toString(self, includeStart: bool, includeEnd: bool, withWeight: Optional[bool] = None):
        if withWeight is None:
            withWeight = (self.weight != 1)
        startStr = str(self.start) if includeStart else ""
        endStr = str(self.end) if includeEnd else ""
        if withWeight:
            w = self.weight
            decimals = 0 if w == round(w, 0) else 1 if w == round(w, 1) else 2
            return f"{startStr} --[{w:.{decimals}f}]--> {endStr}"
        else:
            return f"{startStr} --> {endStr}"

    def __str__(self):
        return self.toString(True, True)

