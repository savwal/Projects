import sys
from typing import Iterator, Generic, Optional, List, Any

from simple_map import SimpleMap, Key, Value


# The standard recursion limit is 1000, but then we won't be able to even analyse 
# the `small` directory, so we increase the limit to 5000:
sys.setrecursionlimit(5000)


class Node(Generic[Key, Value]):
    """
    A generic class for BST nodes (and subclasses).
    We include the instance variables `size` and `height` for efficiency reasons
    (`height` is needed to calculate the balance in AVL trees).
    """
    key: Key
    value: Value
    left: Optional['Node[Key, Value]']
    right: Optional['Node[Key, Value]']
    size: int
    height: int

    # Setting the __slots__ reduces memory footprint and increases efficiency:
    # https://wiki.python.org/moin/UsingSlots
    __slots__ = "key", "value", "left", "right", "size", "height"

    def __init__(self, key: Key, value: Value):
        self.key = key
        self.value = value
        self.left = self.right = None
        self.updateSizeAndHeight()

    def updateSizeAndHeight(self):
        """
        Makes sure that the `size` and `height` instance variables are correct.
        This assumes that the size and height for the children are already correct,
        so if you're changing several nodes you have to update them bottom-up
        (first the children, then the parents).
        """
        self.size = (1
            + (0 if self.left is None else self.left.size)
            + (0 if self.right is None else self.right.size)
        )
        self.height = 1 + max(
            (0 if self.left is None else self.left.height),
            (0 if self.right is None else self.right.height),
        )


class BSTMap(SimpleMap[Key, Value]):
    """
    SimpleMap implemented as a Binary Search Tree.
    """
    root: Optional[Node[Key, Value]] = None


    def size(self) -> int:
        return self._sizeHelper(self.root)

    def _sizeHelper(self, node: Optional[Node[Key, Value]]) -> int:
        if node is None: return 0
        return node.size


    def height(self) -> int:
        return self._heightHelper(self.root)

    def _heightHelper(self, node: Optional[Node[Key, Value]]) -> int:
        if node is None: return 0
        return node.height


    def get(self, key: Key) -> Value:
        assert key is not None, "argument must not be None"
        value = self._getHelper(self.root, key)
        if value is None:
            # If the key does not exist, generate a default value and associate with the key:
            value = self.defaultValueSupplier()
            if value is not None:
                self.put(key, value)
        return value

    def _getHelper(self, node: Optional[Node[Key, Value]], key: Key) -> Optional[Value]:
        #---------- TASK 3a: Implement BST get -------------------------------#
        # Note 1: you can implement this method using iteration or recursion
        # Note 2: if the key does not exist, you should return None

        # TODO: Replace these lines with your solution! 
        raise NotImplementedError()
        #---------- END TASK 3a ----------------------------------------------#


    def put(self, key: Key, value: Value):
        assert key is not None, "argument must not be None"
        self.root = self._putHelper(self.root, key, value)

    def _putHelper(self, node: Optional[Node[Key, Value]], key: Key, value: Value) -> Node[Key, Value]:
        #---------- TASK 3b: Implement BST put -------------------------------#
        # Note 1: you should implement this method using recursion
        # Note 2: if the node is None you should return a new Node

        # TODO: Replace these lines with your solution!
        raise NotImplementedError()
        #---------- END TASK 3b ----------------------------------------------#
 
        # We need to make sure that the node `size` and `height` are up-to-date.
        node.updateSizeAndHeight()
        return node


    def clear(self):
        self.root = None


    def __iter__(self) -> Iterator[Key]:
        yield from self._iterHelper(self.root)

    def _iterHelper(self, node: Optional[Node[Key, Value]]) -> Iterator[Key]:
        if node is not None:
            yield from self._iterHelper(node.left)
            yield node.key
            yield from self._iterHelper(node.right)


    def __str__(self) -> str:
        classname = type(self).__name__
        if self.isEmpty(): return f"{classname}(empty)"
        return f"{classname}(size {self.size()}, height {self.height()})"

    def show(self, maxLevel: int) -> str:
        return f"{self}: {self._showNode(self.root, maxLevel)}"
    
    def _showNode(self, node: Optional[Node[Key, Value]], maxLevel: int) -> str:
        if node is None: return "-"
        if maxLevel <= 0: return f"(...{node.size} nodes...)"
        left = self._showNode(node.left, maxLevel-1)
        right = self._showNode(node.right, maxLevel-1)
        return f"({left} {node.key}:{node.value} {right})"


    ###########################################################################
    # Validation

    def validate(self):
        self._validateBST(self.root, None, None)
        self._validateSize(self.root)
        self._validateHeight(self.root)

    def _validateBST(self, node: Optional[Node[Key, Value]], min: Optional[Key], max: Optional[Key]):
        if node is None: return
        if min is not None and node.key <= min:
            raise ValueError(f"Node '{node.key}:{node.value}' not in BST order: "
                             f"rightmost left child ({min}) >= node ({node.key})")
        if max is not None and max <= node.key:
            raise ValueError(f"Node '{node.key}:{node.value}' not in BST order: "
                             f"node ({node.key}) >= leftmost right child ({max})")
        self._validateBST(node.left, min, node.key)
        self._validateBST(node.right, node.key, max)

    def _validateSize(self, node: Optional[Node[Key, Value]]):
        if node is None: return
        calculated = 1 + self._sizeHelper(node.left) + self._sizeHelper(node.right)
        if node.size != calculated:
            raise ValueError(f"Subtree size for node '{node.key}:{node.value}' not consistent: "
                             f"stored ({node.size}) != calculated ({calculated})")
        self._validateSize(node.left)
        self._validateSize(node.right)
    
    def _validateHeight(self, node: Optional[Node[Key, Value]]):
        if node is None: return
        calculated = 1 + max(self._heightHelper(node.left), self._heightHelper(node.right))
        if node.height != calculated:
            raise ValueError(f"Subtree height for node '{node.key}:{node.value}' not consistent: "
                             f"stored ({node.height}) != calculated ({calculated})")
        self._validateHeight(node.left)
        self._validateHeight(node.right)


###############################################################################
# Test the BST: Change this code however you want.

def main():
    print("# A very simple tree.")
    tree: BSTMap[int, str] = BSTMap()
    tree.put(2, "dog"); tree.validate()
    tree.put(3, "sat"); tree.validate()
    tree.put(5, "the"); tree.validate()
    tree.put(6, "mat"); tree.validate()
    tree.put(4, "on");  tree.validate()
    tree.put(2, "cat"); tree.validate()
    tree.put(1, "the"); tree.validate()
    for k in range(0, 8):
        print(k, "-->", tree.get(k))
    print(tree.show(5))
    print()

    print("# A tree where the values are mutable lists.")
    tree2: BSTMap[int, List[Any]] = BSTMap(list)
    tree2.get(1).append("the")
    tree2.get(3).append("sat")
    tree2.get(9).append("the")
    tree2.get(9).append("mat")
    tree2.get(3).append("on")
    tree2.get(1).append("cat")
    tree2.validate()
    for k in tree2:
        print(k, "-->", tree2.get(k))
    print(tree2.show(5))
    if (tree2.size() != 3):
        raise ValueError(f"Wrong tree size: {tree2.size()}, but it should be 3.")
    print()

    """
    # Wait with this until you're pretty certain that your code works.
    print("# A larger tree, testing performance.")
    tree3: BSTMap[int, int] = BSTMap()
    numbers = list(range(0, 2000))

    # Comment this to get an unbalanced tree:
    import random
    random.shuffle(numbers)

    for k in numbers:
        tree3.put(k, k*k)  # Map a number to its square
    tree3.validate()
    print(tree3.show(2))
    for k in numbers:
        assert k*k == tree3.get(k)
    print()
    """


if __name__ == "__main__":
    main()


