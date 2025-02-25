import java.util.Scanner;

/* Question no : 2a
 * This program calculates the minimum number of rewards to be distributed among employees 
 * based on their performance using a greedy approach.
 * 
 * The approach involves two passes over the array:
 * Left to Right Pass: If an employee has a higher rating than the previous one, 
 * they get more rewards than the previous employee.
 * Right to Left Pass: Ensures that employees with higher ratings than the next 
 * one also receive more rewards.
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(n)
 */
public class MinimumReward {

    public static int minimumRewards(int[] ratings) {
        int n = ratings.length;
        int[] rewards = new int[n];

        // Step 1: Initialize each employee with at least 1 reward
        for (int i = 0; i < n; i++) {
            rewards[i] = 1;
        }

        // Step 2: Left to Right Pass
        for (int i = 1; i < n; i++) {
            if (ratings[i] > ratings[i - 1]) {
                rewards[i] = rewards[i - 1] + 1;
            }
        }

        // Step 3: Right to Left Pass
        for (int i = n - 2; i >= 0; i--) {
            if (ratings[i] > ratings[i + 1]) {
                rewards[i] = Math.max(rewards[i], rewards[i + 1] + 1);
            }
        }

        // Step 4: Calculate Total Rewards
        int totalRewards = 0;
        for (int reward : rewards) {
            totalRewards += reward;
        }

        return totalRewards;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Taking input from the user
        System.out.print("Enter the number of employees: ");
        int n = scanner.nextInt();
        int[] ratings = new int[n];

        System.out.println("Enter the performance ratings of employees:");
        for (int i = 0; i < n; i++) {
            ratings[i] = scanner.nextInt();
        }

        // Calculating and displaying the minimum rewards
        System.out.println("The minimum reward required is: " + minimumRewards(ratings));

        scanner.close();
    }
}

/* Testing Results
    Example 1:
    Enter the number of employees: 3
    Enter the performance ratings of employees:
    1 0 2 
    The minimum reward required is: 5
    
    Example 2:
    Enter the number of employees: 5
    Enter the performance ratings of employees:
    1 3 2 2 1
    The minimum reward required is: 7
 
 */
