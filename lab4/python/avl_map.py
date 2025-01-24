from typing import Optional, List, Any

from bst_map import Node, BSTMap, Key, Value


class AVLMap(BSTMap[Key, Value]):
    """
    SimpleMap implemented as an AVL tree.
    This inherits most functionality from BST, the only difference is 
    that it rebalances the tree when adding new tree nodes.
    """

    def _putHelper(self, node: Optional[Node[Key, Value]], key: Key, value: Value) -> Node[Key, Value]:
        # Call `_putHelper` for normal BSTs, to insert the value into the tree,
        # and update the node to be the new parent.
        node = super()._putHelper(node, key, value)

        # Calculate the balance factor for the node.
        balanceFactor = self._heightHelper(node.left) - self._heightHelper(node.right)

        #---------- TASK 4a: Implement AVL rebalancing -------------------#
        # Perform balance rotations if needed.
        # You do this by calling `_left_rotate` and `_right_rotate`.
        # Remember to return the new parent node if you balance the tree.

        # TODO: Replace these lines with your solution!
        raise NotImplementedError()
        #---------- END TASK 4a ------------------------------------------#

        # If the tree doesn't need rebalancing, return the node as it is.
        return node


    def _rightRotate(self, parent: Node[Key, Value]) -> Node[Key, Value]:
        # The left child will be the new parent.
        child = parent.left
        # This must exist, otherwise we cannot rotate right.
        assert child, "There must be a left child"

        # Perform right rotation.
        #---------- TASK 4b: Implement right rotation --------------------#
        # Don't forget to update the size and height for all nodes that need it.
        # You do this by calling the method `updateSizeAndHeight()`.

        # TODO: Replace these lines with your solution! 
        raise NotImplementedError()
        #---------- END TASK 4b -------------------------------------------#

        # Return the new parent.
        return child


    def _leftRotate(self, parent: Node[Key, Value]) -> Node[Key, Value]:
        #---------- TASK 4c: Implement left rotation ---------------------#
        # Don't forget to:
        #  1. Update the size and height for all nodes that need it.
        #     You do this by calling the method 'update_size_and_height()'.
        #  2. Return the new parent node after rotation.

        # TODO: Replace these lines with your solution! 
        raise NotImplementedError()
        #---------- END TASK 4c ------------------------------------------#


    ###########################################################################
    # Validation

    def validate(self):
        super().validate()
        self._validateBalance(self.root)

    def _validateBalance(self, node: Optional[Node[Key, Value]]):
        if node is None: return
        balanceFactor = self._heightHelper(node.left) - self._heightHelper(node.right)
        if not (-1 <= balanceFactor <= 1):
            raise ValueError(f"Node '{node.key}:{node.value}' not properly balanced: "
                             f"balance factor is {balanceFactor}, not -1, 0 or 1")
        self._validateBalance(node.left)
        self._validateBalance(node.right)


###############################################################################
# Test the AVL tree: Change this code however you want.

def main():
    print("# A very simple tree.")
    tree: AVLMap[int, str] = AVLMap()
    tree.put(2, "dog"); tree.validate()
    tree.put(3, "sat"); tree.validate()
    tree.put(5, "the"); tree.validate()
    tree.put(6, "mat"); tree.validate()
    tree.put(4, "on");  tree.validate()
    tree.put(2, "cat"); tree.validate()
    tree.put(1, "the"); tree.validate()
    for k in range(1, 7):
        print(k, "-->", tree.get(k))
    print(tree.show(5))
    print()

    print("# A tree where the values are mutable lists.")
    tree2: AVLMap[int, List[Any]] = AVLMap(list)
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
    tree3: AVLMap[int, int] = AVLMap()
    numbers = list(range(0, 2000))

    # Comment this to see an that the AVL tree doesn't become unbalanced:
    import random
    random.shuffle(numbers)

    for k in numbers:
        tree3.put(k, k*k)  # map a number to its square
    tree3.validate()
    print(tree3.show(2))
    for k in numbers:
        assert k*k == tree3.get(k)
    print()
    """


if __name__ == "__main__":
    main()
    

