
from simple_set import SimpleSet, Key
from avl_map import AVLMap

class AVLSet(SimpleSet[Key]):
    def __init__(self):
        self._map = AVLMap()

