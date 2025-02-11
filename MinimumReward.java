/* Question no: 2,a
 * This program is for calculating the minimum number of rewards that needed to be distrubuted among employees based on their performance. 
 * This is done using a greedy approach with two passes over the array. At first, each employee is given reward amd then in first pass(left to right), 
 * if an  employee's ratings is greater than the previous employee's rating is checked and if it is so one more reward is assigned to that employee.
 * In second pass (right to left), the same thing is done but ratings is compared with the next employee and it is ensured that the reward 
 * is higher than the next employee's reward. At last, the sum of all individual rewards is the total no. of rewards.
 * The time complexity and space complexity is found to be O(n).
 */
public class MinimumReward {
    public static int minimumRewards(int[] ratings){
        int n = ratings.length;

        int[] rewards = new int[n];  //initializing the reward array where every employee is getting at least a reward
        for(int i=0; i<n; i++){
            rewards[i]=1;  // each employee getting a reward initially
        }

        for(int i=1; i<n; i++){   // At first - left to right If an employee has a higher rating than the one before them,
            if(ratings[i] > ratings[i-1]){  //they should get more rewards than the employee to their left.
                rewards[i] = rewards[i-1] + 1;
            }
        }

        for(int i=n-2; i>=0; i--){ //then right to left If an employee has a higher rating than the one after them, 
            if(ratings[i]>ratings[i+1]){    //they should get more rewards than the employee to their right.
                rewards[i] = Math.max(rewards[i], rewards[i+1]+1);
            }
        }

        int totalRewards =0;
        for(int reward: rewards){  //calculating the total no of rewards by summing up the 'rewards' array
            totalRewards += reward;
        }

        return totalRewards;  //returning the total no. of rewards 
    }

    public static void main(String[] args) {  //main methpd
        int[] ratings1 = {1,0,2}; //Test case 1
        int[] ratings2 = {1,2,2}; //Test case 2

        System.out.println("The minimum reward is" + minimumRewards(ratings1)); //expected output = 5 
        System.out.println("The minimum reward is" + minimumRewards(ratings2)); //expected output = 4
    }

}


/*
 * Testing result
    The minimum reward is5
    The minimum reward is4
 */