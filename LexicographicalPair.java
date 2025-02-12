/* Question no: 2,b
 * To find the lexicographically smallest pair of points closest to each other in a 2D place at first we iterate over all pairs (i,j) where i <j. 
 * then the Manhattan distance is computed. then a track of the minimum distance and lexicographically smallest pair is kept. and
 * if we find a smaller distance, the pair is updated and if the same distance is found, the new pair (i, j) is updated if it is lexicographically smaller
 * the time complexity is found to be o(n2) and space complexity is found to be O(1)
 */

import java.util.Arrays;

public class LexicographicalPair {
    public static int [] findClosestPair(int[] x_coordinate, int[] y_coordinate){
        int n = x_coordinate.length;   //no. of points
        int minimumDistance = Integer.MAX_VALUE;   //variable to store the minimum distance found
        int[] closestPair = {-1, -1};      // storing the closest pair 

        for (int i=0; i<n; i++){  // for loop to iterate over all pairs(i,j) where i < j
            for (int j=i+1; j<n; j++) {
                int distance = Math.abs(x_coordinate[i]-x_coordinate[j]) + Math.abs((y_coordinate[i]-y_coordinate[j]));
                if (distance < minimumDistance){    // closest pair is updated if a smaller distance is found
                    minimumDistance = distance;
                    closestPair[0] = i;
                    closestPair[1] = j;
                }else if (distance == minimumDistance){   // update the distance(if it is same) only if (i, j) is lexicographically smaller
                    if (i < closestPair[0] || (i == closestPair[0] && j < closestPair[1])){
                        closestPair[0] = i;
                        closestPair[1] = j;
                    }
                }
                
            }
        }
        return closestPair;  // returns the closest pair
    }

    public static void main(String[] args) {
        int[] x_coordinate = {1, 2, 3, 2, 4};  // sample input
        int[] y_coordinate = {2, 3, 1, 2, 3};

        int[] result = findClosestPair(x_coordinate, y_coordinate);  //calling the function and storing the result
        System.out.println("The closest pair of points is: " + Arrays.toString(result));  // Expected Output: [0, 3]

    }

    
}

/* 
 * Testing Result
    The closest pair of points is: [0, 3]
 */
