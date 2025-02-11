import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

/* Question no: 1,b
 * To find the k-th lowest combination by selecting a investment from each sorted array, at first, a min heap/priority queue is 
 * used to track the smallest products effectively then approach is started with the samllest possible product and then 
 * the smallest k times is extracted to get the k-th smallest product. A set of visited pairs is maintained to avoid the redundancy.
 * the time complexity is found to be O(k log k)
 */
public class LowestCombination {    //public class LowestCombination declaration
    public static int lowestCombination(int[]ret1, int[]ret2, int k){ 
        PriorityQueue<int[]> minHeap = new PriorityQueue<>(Comparator.comparing(a -> a[0])); //Min-heap to store the value

        Set<String> visited = new HashSet<>();  // tracking the visited index pairs(i, j) using  set

        minHeap.offer(new int[]{ret1[0]*ret2[0],0,0}); // inserting the smallest value
        visited.add("0,0");

        int value = 0; // storing the k-th smallest value

        for(int count=0; count<k; count++){ // loop to extract the k times from the min heap
            int[] smallest = minHeap.poll();  //getting the smallest value
            value = smallest[0];
            int i = smallest[1] , j = smallest[2];

            if(i+1 < ret1.length && !visited.contains((i+1) + ",")){  // if possible, adding the next element from ret1
                minHeap.offer(new int[]{ret1[i+1] * ret2[j], i+1, j});
                visited.add((i+1) + "," + j);
            }

            if(j+1 < ret2.length && !visited.contains((i + "," + (j+1)))){  // if possible, adding the nect element from ret2
                minHeap.offer(new int[]{ret1[i] * ret2[j+1], i , j+1});
                visited.add(i + "," + (j+1));
            }
        }
        return value;  // returning the answer
    }

    public static void main(String[] args) {  // calling main method
        int[] return1a = {2,5};  // Test case1
        int[] return2a = {3,4};
        int k1 = 2;
        System.out.println("The output is" + lowestCombination(return1a, return2a, k1)); //Expected Output: 8

        int[] return1b = {-4, -2, 0, 3};  // Test case2
        int[] return2b = {2, 4};
        int k2 = 6;
        System.out.println("The output is" + lowestCombination(return1b, return2b, k2));  //Expected Output: 0

    }

}

/*
 * Testing result
    The output is8
    The output is0
 */
