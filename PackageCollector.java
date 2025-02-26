import java.util.*;

/* Question no: 4b
 * This program finds the minimum number of roads that need to be traversed to collect all packages
 * within a distance of 2 and return to the starting location.
 * The number of locations, package availability, and road connections is parsed to read input
 * Any node with a package is a valid start to find a starting location.
 * BFS is used to traverse roads:
 * At first moved to adjacent nodes.
 * Then collected packages in reachable nodes.
 * Then tracked roads traveled (counting multiple passes).
 * After that returned to the starting location:
 * Then ensured the shortest route back using BFS.
 * Finally Output is the total number of roads traversed.
 */
public class PackageCollector {

    //This method uses Breadth-First Search (BFS) to find the shortest path from `start` to all nodes.
    // It returns a map with the minimum distance from `start` to each node.
    public static Map<Integer, Integer> bfs(int start, Map<Integer, List<Integer>> graph) {
        Queue<Integer> queue = new LinkedList<>();
        Map<Integer, Integer> distance = new HashMap<>();
        queue.add(start);
        distance.put(start, 0); // Start node has distance 0

        while (!queue.isEmpty()) {
            int node = queue.poll();
            int currentDist = distance.get(node);

            for (int neighbor : graph.getOrDefault(node, new ArrayList<>())) {
                if (!distance.containsKey(neighbor)) { // If not visited
                    queue.add(neighbor);
                    distance.put(neighbor, currentDist + 1); // Increase distance by 1
                }
            }
        }
        return distance;
    }

    public static int minRoadsToCollectPackages(int[] packages, int[][] roads) {
        int n = packages.length; // Number of locations

        // Build graph as an adjacency list
        Map<Integer, List<Integer>> graph = new HashMap<>();
        for (int[] road : roads) {
            graph.putIfAbsent(road[0], new ArrayList<>());
            graph.putIfAbsent(road[1], new ArrayList<>());
            graph.get(road[0]).add(road[1]);
            graph.get(road[1]).add(road[0]); // Undirected graph
        }

        // Find a starting node (any node with a package)
        int start = -1;
        for (int i = 0; i < n; i++) {
            if (packages[i] == 1) {
                start = i;
                break;
            }
        }

        // If no packages are present, no need to travel
        if (start == -1) return 0;

        // Use BFS to get distances from start
        Map<Integer, Integer> distances = bfs(start, graph);

        int totalRoads = 0;
        Set<Integer> visited = new HashSet<>();

        // Collect packages from reachable locations
        for (int i = 0; i < n; i++) {
            if (packages[i] == 1 && distances.containsKey(i) && distances.get(i) <= 2) {
                totalRoads += distances.get(i); // Count roads traveled
                visited.add(i); // Mark node as visited
            }
        }

        // Find shortest path back to start using BFS
        Map<Integer, Integer> returnPath = bfs(start, graph);
        int returnRoads = Integer.MAX_VALUE;

        // Find the closest package location to return from
        for (int location : visited) {
            if (returnPath.containsKey(location)) {
                returnRoads = Math.min(returnRoads, returnPath.get(location));
            }
        }

        return totalRoads + returnRoads;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Read number of locations
        System.out.print("Enter number of locations: ");
        int n = scanner.nextInt();

        // Read package availability
        int[] packages = new int[n];
        System.out.println("Enter package array (0 for no package, 1 for package): ");
        for (int i = 0; i < n; i++) {
            packages[i] = scanner.nextInt();
        }

        // Read number of roads
        System.out.print("Enter number of roads: ");
        int m = scanner.nextInt();

        int[][] roads = new int[m][2];
        System.out.println("Enter roads (pairs of connected locations): ");
        for (int i = 0; i < m; i++) {
            roads[i][0] = scanner.nextInt();
            roads[i][1] = scanner.nextInt();
        }

        scanner.close();

        // Compute and print the minimum roads to traverse
        int result = minRoadsToCollectPackages(packages, roads);
        System.out.println("Minimum number of roads to traverse: " + result);
    }
}

/* Testing Results
    Example 1:
    Enter number of locations: 8
    Enter package array (0 for no package, 1 for package): 
    0 0 0 1 1 0 0 1
    Enter number of roads: 7
    Enter roads (pairs of connected locations): 
    0 1
    0 2
    1 3
    1 4
    2 5
    6 5
    7 7
    Minimum number of roads to traverse: 2

    Example 2:
    Enter number of locations: 6
    Enter package array (0 for no package, 1 for package): 
    1 0 0 0 0 1
    Enter number of roads: 5
    Enter roads (pairs of connected locations): 
    0 1
    2 3
    3 4
    4 5
    5 1
    Minimum number of roads to traverse: 2
 */