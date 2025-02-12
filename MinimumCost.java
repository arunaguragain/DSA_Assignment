/* Question no: 3,a
 * To find the minimum total cost to connect all devices in a network Kruskal's Minimum Spanning Tree algorithm is used Each device is 
 * treated as a node and dummy node is used to represent the communication model cost. The selction of lowest cost edges is done
 * while avoiding the cycles.
 * The time complexity was found to be O(E log E) and space complexity was found to be O(n + E)
 */
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MinimumCost {
    static class FindUnion{
        int [] parent, rank;

        public FindUnion(int size){ // Constructor to initialize the parent and rank arrays
            parent = new int [size];
            rank = new int [size];
            for(int i=0; i<size; i++){  // initializing the parent array where each node is its own parent initiall
                parent[i] = i;
            }
        }

        public int find(int x){   // method to return the node root
            if(parent[x] != x){
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        public boolean union(int x, int y){   // method to combine two components into one
            int rootx = find(x);
            int rooty = find(y);
            if(rootx == rooty){  // no unification needed if both nodes are already connected
                return false;
            }
            if(rank[rootx] > rank[rooty]){ //union of two sets based on their rank
                parent[rooty] = rootx;
            }else if (rank[rootx] < rank[rooty]){
                parent[rootx] = rooty;
            }else{
                parent[rooty] = rootx; // if ranks are equal then make one root the parent of the other
                rank[rootx]++;  // increasing the rank of the new root
            }
            return true;  //Successfully united
        }
    }

    public static int minimumTotalCost(int n, int[] modules, int[][] connections){ //method to calculate the minimum total cost
        List<int[]> edges = new ArrayList<>();
 
        for(int i=0; i<n; i++){      // adding connection module cost
            edges.add(new int[]{0, i+1, modules[i]});
        }

        for(int[] c : connections){  //adding direct connection costd from the connection array
            edges.add(new int[]{c[0], c[1], c[2]}); 
        }

        edges.sort(Comparator.comparingInt(a -> a[2]));  //Sorting all edges based on the cost

        FindUnion fu = new FindUnion(n+1);  // Kruskal's Algorithm to construct the MST
        int totalCost = 0, edgesUsed= 0;

        for(int[] edge : edges){   //loop to iterate through the sorted edges and adding them to the MST if they don't form a cycle
            if(fu.union(edge[0], edge[1])){  
                totalCost += edge[2];   // adding the cost of total edge to the total cost
                edgesUsed ++;          // increasing the count of used edges
                if(edgesUsed == n){
                    break;             //stopping if all devices are connected
                }
            }
        }

        return totalCost;  //returning the minimum total cost
    }

    public static void main(String[] args) {  //main method
        int n =3;  //no. of devices
        int[] modules = {1, 2, 2};   //installation cost
        int[][] connections = {{1, 2, 1}, {2, 3, 1}};   // direct connection cost

        int result = minimumTotalCost(n, modules, connections);  //calling the method to get result
        System.out.println("The minimum total cost to connect all devices is: " + result); //Expected Output: 3
    }
    
}

/* Testing Result
    The minimum total cost to connect all devices is: 3
 */