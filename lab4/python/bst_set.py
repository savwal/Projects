
from simple_set import SimpleSet, Key
from bst_map import BSTMap

class BSTSet(SimpleSet[Key]):
    def __init__(self):
        self._map = BSTMap()

