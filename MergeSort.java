/*************************************************************************
 *  Compilation:  javac Merge.java
 *  Execution:    java Merge N
 *
 *  A more generic version of mergesort using Comparable.
 *
 *************************************************************************/

public class MergeSort {

    public static void sort(Page[] theArray) {
        sort(0, theArray.length, theArray);
    } 

    // Sort theArray[lo, hi). 
    public static void sort(int lo, int hi, Page[] theArray) {
        int N = hi - lo;        // number of elements to sort

        // 0- or 1-element file, so we're done
        if (N <= 1) return; 

        // recursively sort left and right halves
        int mid = lo + N/2; 
        sort(lo, mid, theArray); 
        sort(mid, hi, theArray); 

        // merge two sorted subarrays
        Page[] aux = new Page[N];
        int i = lo, j = mid;

        for (int k = 0; k < N; k++) {
            if      (i == mid)  
		aux[k] = theArray[j++];
            else if (j == hi)   
		aux[k] = theArray[i++];
            else if (theArray[j].compareTo(theArray[i]) < 0) 
		aux[k] = theArray[j++];
            else                               
		aux[k] = theArray[i++];
        }

        // copy back
        for (int k = 0; k < N; k++) {
            theArray[lo + k] = aux[k]; 
        }
    } 
}
