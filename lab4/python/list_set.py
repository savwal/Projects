
from simple_set import SimpleSet, Key
from list_map import ListMap

class ListSet(SimpleSet[Key]):
    def __init__(self):
        self._map = ListMap()

