/** 
 * Different implementations of binary search.
 * 
 * Common description of the below functions:
 * - Precondition: `array` is sorted according to the natural order.
 * - Precondition: all arguments are non-null (no need to check).
 * - Required complexity: O(log(n)) comparisons where n is the length of `array`.
*/
public class BinarySearch {

    /**
     * Check if the array contains the given value.
     * Iterative solution.
     * @param array an array sorted according to the natural order of its elements.
     * @param value the value to search for.
     * @return true if `value` is in `array`.
     */
    public static<V extends Comparable<? super V>> boolean containsIterative(V[] array, V value) {
        //---------- TASK 1: Iterative version of binary search -------------------//
        // Hint: you probably need some auxiliary variables telling which part 
        // of the array you're looking at.
        int left = 0;
        int right = array.length - 1;

        while (left <= right){
            int mid = (right + left) / 2;
            int comp = array[mid].compareTo(value);

            if (comp > 0){
                right = mid - 1;
            }else if (comp < 0) {
                left = mid + 1;
            }else {
                return true;}
        }
        return false;
        //---------- END TASK 1 ---------------------------------------------------//
    }

    /**
     * Check if the array contains the given value. 
     * Recursive solution.
     * 
     * @param array an array sorted according to the natural order of its elements.
     * @param value the value to search for.
     * @return true if `value` is in `array`.
     */
    // helper function 
    public static <V extends Comparable<? super V>> boolean helperfunction(V[] array, V value, int start, int end) {
        if (start > end ) {return false;}

        int middle = (start + end) / 2;
        int comp = array[middle].compareTo(value);

        if (comp > 0){
            return helperfunction(array, value, start, middle-1);
        }else if (comp < 0) {
            return helperfunction(array, value, middle +1, end);
        }else {
            return true;}
    }        

    public static<V extends Comparable<? super V>> boolean containsRecursive(V[] array, V value) {
        //---------- TASK 2: Recursive version of binary search -------------------//
        // Hint: you need a recursive helper function with some extra
        // arguments telling which part of the array you're looking at.
        int n = array.length;
        int mid = (n-1) / 2;
        if(n == 0){
            return(false);}
        int comp = array[mid].compareTo(value);

        if (comp > 0){
            return(helperfunction(array, value, 0, mid-1));
        }else if (comp < 0){
            return(helperfunction(array, value, mid+1, n-1));
        }else{
            return(true);}
    }
         //---------- END TASK 2 ---------------------------------------------------//

    /**
     * Return the *first* position in the array whose element matches the given value.
     * 
     * @param array an array sorted according to the natural order of its elements.
     * @param value the value to search for.
     * @return the first position where `value` occurs in `array`, or -1 if it doesn't occur.
     */
    // Iterative helper function

    public static<V extends Comparable<? super V>> int firstIndexOf(V[] array, V value) {
        //---------- TASK 3: Binary search returning the first index --------------//
        // It's up to you if you want to use an iterative or recursive version.
        int n = array.length;
       
        int left = 0;
        int right = n-1;

        int index = -1;
        while(right >= left) {
            int mid = (right + left) / 2;
            int comp = array[mid].compareTo(value);
            if (comp > 0){
                right = mid-1;
            }else if (comp < 0){
                left = mid+1;
            }else{
                index = mid;
                right = mid-1;}
        };
        return(index);
    }
        //---------- END TASK 3 ---------------------------------------------------//

    // Put your own tests here.

    /**
     * @param args
     */
    public static void main(String[] args) {
        Integer[] integer_test_array = {1, 3, 5, 7, 9};

        assert containsIterative(integer_test_array, 4) == false;
        assert containsIterative(integer_test_array, 7) == true;

        assert containsRecursive(integer_test_array, 0) == false;
        assert containsRecursive(integer_test_array, 9) == true;

        String[] string_test_array = {"cat", "cat", "cat", "dog", "turtle", "turtle"};

        assert firstIndexOf(string_test_array, "cat") == 0;
        assert firstIndexOf(string_test_array, "dog") == 3;
        assert firstIndexOf(string_test_array, "turtle") == 4;
        assert firstIndexOf(string_test_array, "zebra") == -1;
    }
}
     

    

