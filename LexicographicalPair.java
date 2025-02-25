import java.util.Arrays;
import java.util.Scanner;

/* Question no : 2b
 * This program finds the lexicographically smallest pair of points that are closest to each other
 * in a 2D plane using Manhattan distance.
 * 
 * The algorithm iterates over all pairs (i, j) where i < j, calculates the Manhattan distance, 
 * and keeps track of the minimum distance and lexicographically smallest pair.
 * 
 * Time Complexity: O(n^2)
 * Space Complexity: O(1)
 */
public class LexicographicalPair {

    public static int[] findClosestPair(int[] x_coordinate, int[] y_coordinate) {
        int n = x_coordinate.length; // Number of points
        int minimumDistance = Integer.MAX_VALUE; // Variable to store the minimum distance found
        int[] closestPair = {-1, -1}; // Storing the closest pair

        for (int i = 0; i < n; i++) { // Loop to iterate over all pairs (i, j) where i < j
            for (int j = i + 1; j < n; j++) {
                int distance = Math.abs(x_coordinate[i] - x_coordinate[j]) 
                             + Math.abs(y_coordinate[i] - y_coordinate[j]);
                
                if (distance < minimumDistance) { // If a smaller distance is found, update closest pair
                    minimumDistance = distance;
                    closestPair[0] = i;
                    closestPair[1] = j;
                } else if (distance == minimumDistance) { // If the same distance, update only if (i, j) is lexicographically smaller
                    if (i < closestPair[0] || (i == closestPair[0] && j < closestPair[1])) {
                        closestPair[0] = i;
                        closestPair[1] = j;
                    }
                }
            }
        }
        return closestPair; // Return the closest pair
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Taking user input for number of points
        System.out.print("Enter the number of points: ");
        int n = scanner.nextInt();

        int[] x_coordinate = new int[n];
        int[] y_coordinate = new int[n];

        // Taking user input for coordinates
        System.out.println("Enter the x and y coordinates for each point:");
        for (int i = 0; i < n; i++) {
            System.out.print("Point " + (i + 1) + " (x y): ");
            x_coordinate[i] = scanner.nextInt();
            y_coordinate[i] = scanner.nextInt();
        }

        // Finding and displaying the closest pair
        int[] result = findClosestPair(x_coordinate, y_coordinate);
        System.out.println("The closest pair of points is: " + Arrays.toString(result));

        scanner.close();
    }
}

/* Testing Result
    Example 1:
    Enter the number of points: 5
    Enter the x and y coordinates for each point:
    Point 1 (x y): 1 2
    Point 2 (x y): 2 3
    Point 3 (x y): 3 1
    Point 4 (x y): 2 2 
    Point 5 (x y): 4 3
    The closest pair of points is: [0, 3]

    Example 2:
    Enter the x and y coordinates for each point:
    Point 1 (x y): 5 1
    Point 2 (x y): 3 4
    Point 3 (x y): 1 1 
    Point 4 (x y): 2 2
    Point 5 (x y): 3 3
    Point 6 (x y): 6 1
    The closest pair of points is: [0, 5]

 */
