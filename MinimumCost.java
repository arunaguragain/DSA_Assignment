import java.util.*;

/* Question no : 3a
 * This program calculates the minimum total cost to connect all devices in a network
 * using Kruskal's Minimum Spanning Tree (MST) algorithm.
 *
 * Each device is treated as a node, and a dummy node represents the communication module cost.
 * The algorithm selects the lowest-cost edges while avoiding cycles.
 *
 * Time Complexity: O(E log E)
 * Space Complexity: O(n + E)
 */
public class MinimumCost {
    
    static class FindUnion {
        int[] parent, rank;

        // Constructor to initialize parent and rank arrays
        public FindUnion(int size) {
            parent = new int[size];
            rank = new int[size];
            for (int i = 0; i < size; i++) {
                parent[i] = i;
            }
        }

        // Find function with path compression
        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        // Union function to merge two sets
        public boolean union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);
            if (rootX == rootY) {
                return false;
            }
            if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;
            } else if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;
            } else {
                parent[rootY] = rootX;
                rank[rootX]++;
            }
            return true;
        }
    }

    public static int minimumTotalCost(int n, int[] modules, int[][] connections) {
        List<int[]> edges = new ArrayList<>();

        // Adding connection module cost
        for (int i = 0; i < n; i++) {
            edges.add(new int[]{0, i + 1, modules[i]});
        }

        // Adding direct connection costs
        for (int[] c : connections) {
            edges.add(new int[]{c[0], c[1], c[2]});
        }

        // Sorting edges by cost
        edges.sort(Comparator.comparingInt(a -> a[2]));

        // Applying Kruskal's Algorithm
        FindUnion fu = new FindUnion(n + 1);
        int totalCost = 0, edgesUsed = 0;

        for (int[] edge : edges) {
            if (fu.union(edge[0], edge[1])) {
                totalCost += edge[2];
                edgesUsed++;
                if (edgesUsed == n) {
                    break;
                }
            }
        }

        return totalCost;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Taking user input for number of devices
        System.out.print("Enter the number of devices: ");
        int n = scanner.nextInt();

        int[] modules = new int[n];
        System.out.println("Enter the installation cost for each device:");
        for (int i = 0; i < n; i++) {
            modules[i] = scanner.nextInt();
        }

        System.out.print("Enter the number of direct connections: ");
        int m = scanner.nextInt();
        int[][] connections = new int[m][3];

        System.out.println("Enter the direct connections in the format (device1 device2 cost):");
        for (int i = 0; i < m; i++) {
            connections[i][0] = scanner.nextInt();
            connections[i][1] = scanner.nextInt();
            connections[i][2] = scanner.nextInt();
        }

        // Calculating and displaying the minimum total cost
        int result = minimumTotalCost(n, modules, connections);
        System.out.println("The minimum total cost to connect all devices is: " + result);

        scanner.close();
    }
}

/* Testing Results
    Example 1 
    Enter the number of devices: 3
    Enter the installation cost for each device:
    1 2 2
    Enter the number of direct connections: 2
    Enter the direct connections in the format (device1 device2 cost):
    1 2 1     2 3 1
    The minimum total cost to connect all devices is: 3

    Example 2
    Enter the number of devices: 4
    Enter the installation cost for each device:
    3 1 4 2 
    Enter the number of direct connections: 3
    Enter the direct connections in the format (device1 device2 cost):
    1 2 2  2 3 3  3 4 1
    The minimum total cost to connect all devices is: 6
 */
