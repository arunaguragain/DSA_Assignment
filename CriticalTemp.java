import java.util.Scanner;  
/* Question no : 1a
 * This program uses Dynamic Programming to determine the minimum number of measurements
 * required to find the critical temperature of a material using 'k' samples and 'n' temperature levels.
 * The DP table dp[k][n] stores the minimum measurements required for 'k' samples and 'n' temperature levels.
 * The formula used: dp[k][n] = 1 + min(t = 1 to n) { max(dp[k-1][t-1], dp[k][n-t]) }
 * The time complexity is O(k * n^2).
 */
public class CriticalTemp {

    public static int minimumMeasurement(int k, int n) {
        int[][] dp = new int[k + 1][n + 1]; // DP table to store results

        // Base case: If only one sample, check all n temperatures sequentially
        for (int i = 1; i <= n; i++) {
            dp[1][i] = i;
        }

        // Filling the DP table for different values of k and n
        for (int i = 2; i <= k; i++) { // Loop for different sample sizes
            for (int j = 1; j <= n; j++) { // Loop for different temperature levels
                dp[i][j] = Integer.MAX_VALUE; // Initialize with a large value

                // Testing at each temperature level t
                for (int t = 1; t <= j; t++) {
                    int res = 1 + Math.max(dp[i - 1][t - 1], dp[i][j - t]); // Worst case scenario
                    dp[i][j] = Math.min(dp[i][j], res); // Store the minimum value
                }
            }
        }
        return dp[k][n]; // Return the computed minimum measurements
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); // Create Scanner object for input

        System.out.print("Enter the number of samples (k): ");
        int k = scanner.nextInt(); // Read user input for samples

        System.out.print("Enter the number of temperature levels (n): ");
        int n = scanner.nextInt(); // Read user input for temperature levels

        int result = minimumMeasurement(k, n); // Compute the result
        System.out.println("Minimum number of measurements required: " + result); // Print the result

        scanner.close(); // Close the scanner
    }
}

/* Testing Results
    Example 1:
    Enter the number of samples (k): 2
    Enter the number of temperature levels (n): 6
    Minimum number of measurements required: 3

    Example 2:
    Enter the number of samples (k): 2
    Enter the number of temperature levels (n): 10
    Minimum number of measurements required: 4
 */