import java.util.*;
/* Question no : 1b
 * This program finds the k-th lowest combination by selecting an investment 
 * from each sorted array using a min-heap (priority queue).
 * It efficiently tracks the smallest products and extracts the smallest k times 
 * to find the k-th smallest product.
 * A set of visited pairs is maintained to avoid redundancy.
 * The time complexity is O(k log k).
 */
public class LowestCombination {

    public static int lowestCombination(int[] ret1, int[] ret2, int k) {
        PriorityQueue<int[]> minHeap = new PriorityQueue<>(Comparator.comparingInt(a -> a[0])); // Min-heap to store values
        Set<String> visited = new HashSet<>();  // Tracking visited index pairs (i, j)

        minHeap.offer(new int[]{ret1[0] * ret2[0], 0, 0}); // Insert the smallest value
        visited.add("0,0");

        int value = 0; // Store the k-th smallest value

        for (int count = 0; count < k; count++) { // Extract k times from the min heap
            int[] smallest = minHeap.poll();  // Get the smallest value
            value = smallest[0];
            int i = smallest[1], j = smallest[2];

            if (i + 1 < ret1.length && !visited.contains((i + 1) + "," + j)) { // Adding the next element from ret1
                minHeap.offer(new int[]{ret1[i + 1] * ret2[j], i + 1, j});
                visited.add((i + 1) + "," + j);
            }

            if (j + 1 < ret2.length && !visited.contains(i + "," + (j + 1))) { // Adding the next element from ret2
                minHeap.offer(new int[]{ret1[i] * ret2[j + 1], i, j + 1});
                visited.add(i + "," + (j + 1));
            }
        }
        return value;  // Return the k-th smallest product
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); // Create Scanner object for user input

        // Reading first array
        System.out.print("Enter the size of the first sorted array: ");
        int size1 = scanner.nextInt();
        int[] ret1 = new int[size1];

        System.out.println("Enter elements of the first sorted array: ");
        for (int i = 0; i < size1; i++) {
            ret1[i] = scanner.nextInt();
        }

        // Reading second array
        System.out.print("Enter the size of the second sorted array: ");
        int size2 = scanner.nextInt();
        int[] ret2 = new int[size2];

        System.out.println("Enter elements of the second sorted array: ");
        for (int i = 0; i < size2; i++) {
            ret2[i] = scanner.nextInt();
        }

        // Reading value of k
        System.out.print("Enter the value of k (for k-th smallest combination): ");
        int k = scanner.nextInt();

        // Compute the result and print output
        int result = lowestCombination(ret1, ret2, k);
        System.out.println("The " + k + "-th lowest combination is: " + result);

        scanner.close(); // Close the scanner
    }
}

/* Testing Results
    Example 1:
    Enter the size of the first sorted array: 2
    Enter elements of the first sorted array: 
    2 5
    Enter the size of the second sorted array: 2
    Enter elements of the second sorted array: 
    3 4
    Enter the value of k (for k-th smallest combination): 2
    The 2-th lowest combination is: 8

    Example 2:
    Enter the size of the first sorted array: 3
    Enter elements of the first sorted array: 
    1 3 7
    Enter the size of the second sorted array: 3
    Enter elements of the second sorted array: 
    2 6 8
    Enter the value of k (for k-th smallest combination): 4
    The 4-th lowest combination is: 8
 */
