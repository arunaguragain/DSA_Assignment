/* Question no: 1,a
 * Dynamic Programming is used for calculating the minimum required no. of measurements to find out 
 * the critical temperature of material using 'k' and 'n' identical sample and temperature level simultaneously.
 * Determining the critical temperature 'f' such that the material changes its properties above 'f' and 
 * remains unchanged below 'f' is the main idea of this algorithm.A recurrence relation is used for building up the solution 
 * and A DP table is used where the minimul no. of measurements required with respective to k samples and n temperature levels 
 * is represented by 'dp[k][n]'.It uses the formula dp[k][n] = 1 + min(t = 1 to n) { max(dp[k-1][t-1], dp[k][n-t]) } 
 * to check the temperature level at 't'. If the material does not react at temperature 't' then the problem is reduced 
 * to 'dp[k][t-1]' and if it does then it is reduced to 'dp[k-1][t-1]'
 * The time complexity is found to be  O(k * n^2)
 */ 

public class CriticalTemp{   // public class Critical Temperature declaration
    public static int minimumMeasurment(int k, int n){  // fuction to calculate the minimum no. of measurments
        int[][] dp = new int[k+1][n+1]; //DP table created in order to store the results for different values of k and n

        for(int i=1; i<=n; i++){ //for loop to initialize base case 
            dp[1][i]=i; //since we need to check all n temperature one by one it does it sequentially
        }

        for(int i=2; i<=k; i++){ // filling the DP table for all the values of k and n
            for(int j=1; j<=n; j++){
                dp[i][j] = Integer.MAX_VALUE; // Initializing with the large value

                for (int t=1; t<=j; t++){  // Testing at each temperature from 1 to j
                    int res = 1 + Math.max(dp[i-1][t-1], dp[i][j-t]);  // taking the maximum between the two outcomes and thus adding 1 
                    dp[i][j] = Math.min(dp[i][j], res);  // taking the minimum over all the temperatures 
                }
            }
        }

        return dp[k][n]; // returing the answer

    }

    public static void main(String[] args) { //calling main method
        System.out.println("Case 1: k =1, n=2"); //testing case 1
        System.out.println("Output is" + minimumMeasurment(1, 2));  // output must be 2
        System.out.println("Case 2: k =1, n=2"); //testing case 2
        System.out.println("Output is" + minimumMeasurment(2, 6));  // output must be 3
        System.out.println("Case 3: k =1, n=2"); //testing case 3
        System.out.println("Output is" + minimumMeasurment(3, 14));  // output must be 5
        
    }

}

/*
 * Testing Results 
    Case 1: k =1, n=2
    Output is2
    Case 2: k =1, n=2
    Output is3
    Case 3: k =1, n=2
    Output is4
 */