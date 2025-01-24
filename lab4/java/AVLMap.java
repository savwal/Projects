import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * SimpleMap implemented as an AVL tree.
 * This inherits most functionality from BST, the only difference is 
 * that it rebalances the tree when adding new tree nodes.
 */
public class AVLMap<Key extends Comparable<Key>, Value> extends BSTMap<Key, Value> {

    AVLMap() {
        this(() -> null);
    }

    AVLMap(Supplier<Value> defaultValueSupplier) {
        this.root = null;
        this.defaultValueSupplier = defaultValueSupplier;
    }

    protected Node putHelper(Node node, Key key, Value value) {
        // Call `_put_helper` for normal BSTs, to insert the value into the tree,
        // and update the node to be the new parent.
        node = super.putHelper(node, key, value);

        // Calculate the balance factor for the node.
        int balanceFactor = this.heightHelper(node.left) - this.heightHelper(node.right);
        
        //---------- TASK 4a: Implement AVL rebalancing ---------------------//
        // Perform balance rotations if needed.
        // You do this by calling `leftRotate` and `rightRotate`.
        // Remember to return the new parent node if you balance the tree.

        if (balanceFactor > 1) {
            if (this.heightHelper(node.left.left) - this.heightHelper(node.left.right) < 0) {
                node.left = leftRotate(node.left);
            }
            node = rightRotate(node);
        } else if (balanceFactor < -1) {
            if (this.heightHelper(node.right.left) - this.heightHelper(node.right.right) > 0) {
                node.right = rightRotate(node.right);
            }
            node = leftRotate(node);
        }
        //---------- END TASK 4a -------------------------------------------//

        return node;
    }

    private Node rightRotate(Node parent) {
        // The left child will be the new parent.
        Node child = parent.left;
        // This must exist, otherwise we cannot rotate right.
        if (child == null) throw new NullPointerException("There must be a left child");

        //---------- TASK 4b: Implement right rotation ----------------------//
        // Don't forget to update the size and height for all nodes that need it.
        // You do this by calling the method `updateSizeAndHeight()`.
        parent.left = child.right;
        child.right = parent;
        parent.updateSizeAndHeight();
        child.updateSizeAndHeight();
        //---------- END TASK 4b --------------------------------------------//

        // Return the new parent.
        return child;
    }

    private Node leftRotate(Node parent) {
        // The left child will be the new parent.
        Node child = parent.right;
        // This must exist, otherwise we cannot rotate left.
        if (child == null) throw new NullPointerException("There must be a right child");

        //---------- TASK 4c: Implement left rotation -----------------------//
        // Don't forget to:
        //  1. Update the size and height for all nodes that need it.
        //     You do this by calling the method 'updateSizeAndHeight()`.
        //  2. Return the new parent node after rotation.
        parent.right = child.left;
        child.left = parent;
        parent.updateSizeAndHeight();
        child.updateSizeAndHeight();
        //---------- END TASK 4c --------------------------------------------//

        // Return the new parent.
        return child;
    }


    ///////////////////////////////////////////////////////////////////////////
    // Validation

    @Override
    public void validate() {
        super.validate();
        this.validateBalance(this.root);
    }

    private void validateBalance(Node node) {
        if (node == null) return;
        int balanceFactor = this.heightHelper(node.left) - this.heightHelper(node.right);
        if (! (-1 <= balanceFactor && balanceFactor <= 1))
            throw new IllegalArgumentException(String.format(
                "Node '%s:%s' not properly balanced: balance factor is %d, not -1, 0 or 1",
                node.key, node.value, balanceFactor
            ));
        this.validateBalance(node.left);
        this.validateBalance(node.right);
    }

    
    ///////////////////////////////////////////////////////////////////////////
    // Test the AVL tree: Change this code however you want.

    public static void main(String[] args) throws IOException {
        System.out.println("# A very simple tree.");
        AVLMap<Integer, String> tree = new AVLMap<>();
        tree.put(2, "dog"); tree.validate();
        tree.put(3, "sat"); tree.validate();
        tree.put(5, "the"); tree.validate();
        tree.put(6, "mat"); tree.validate();
        tree.put(4, "on");  tree.validate();
        tree.put(2, "cat"); tree.validate();
        tree.put(1, "the"); tree.validate();
        for (int k = 0; k < 8; k++) {
            System.out.format("%d --> %s\n", k, tree.get(k));
        }
        System.out.println(tree.show(5));
        System.out.println();

        System.out.println("# A tree where the values are mutable lists.");
        AVLMap<Integer, ArrayList<String>> tree2 = new AVLMap<>(() -> new ArrayList<>());
        tree2.get(1).add("the");
        tree2.get(3).add("sat");
        tree2.get(9).add("the");
        tree2.get(9).add("mat");
        tree2.get(3).add("on");
        tree2.get(1).add("cat");
        tree2.validate();
        for (int k : tree2) {
            System.out.format("%d --> %s\n", k, tree2.get(k));
        }
        System.out.println(tree2.show(5));
        if (tree2.size() != 3)
            throw new IllegalArgumentException(String.format("Wrong tree size: %d, but it should be 3", tree2.size()));
        System.out.println();

        
        // Wait with this until you're pretty certain that your code works.
        System.out.println("# A larger tree, testing performance.");
        AVLMap<Integer, Integer> tree3 = new AVLMap<>();
        List<Integer> numbers = IntStream.range(0, 5_000).boxed().collect(Collectors.toList());

        // Comment this to get an unbalanced tree:
        Collections.shuffle(numbers);

        for (int k : numbers) {
            tree3.put(k, k*k);  // Map a number to its square
        }
        tree3.validate();
        System.out.println(tree3.show(2));
        for (int k : numbers) {
            if (k*k != tree3.get(k))
                throw new IllegalArgumentException(String.format("%d --> %d", k, tree3.get(k)));
        }
        System.out.println();
       
    }

}

