import java.awt.*;
import java.util.*;
import javax.swing.*;

public class NetworkOptimizer {

    // Node class represents a network node with a name and coordinates for graphical representation
    static class Node {
        String name; // Name of the node
        int x, y;    // Coordinates for the graphical representation of the node

        // Constructor to initialize a Node with a name and coordinates
        Node(String name, int x, int y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }
    }

    // Connection class represents a connection between two nodes, with cost and bandwidth
    static class Connection {
        Node node1, node2; // The two nodes this connection links
        int cost, bandwidth; // The cost and bandwidth of this connection

        // Constructor to initialize a Connection with two nodes, cost, and bandwidth
        Connection(Node node1, Node node2, int cost, int bandwidth) {
            this.node1 = node1;
            this.node2 = node2;
            this.cost = cost;
            this.bandwidth = bandwidth;
        }
    }

    // NetworkGraph class represents the whole network, containing nodes and connections
    static class NetworkGraph {
        ArrayList<Node> nodes; // List of all nodes in the network
        ArrayList<Connection> connections; // List of all connections between nodes

        // Constructor to initialize the NetworkGraph with empty node and connection lists
        NetworkGraph() {
            nodes = new ArrayList<>();
            connections = new ArrayList<>();
        }

        // Method to add a new node to the network
        void addNode(String name, int x, int y) {
            nodes.add(new Node(name, x, y));
        }

        // Method to add a new connection between two nodes with a given cost and bandwidth
        void addConnection(Node node1, Node node2, int cost, int bandwidth) {
            connections.add(new Connection(node1, node2, cost, bandwidth));
        }

        // Method to get the Minimum Spanning Tree (MST) using Prim's algorithm (without PriorityQueue)
        public ArrayList<Connection> getMinimumSpanningTree() {
            ArrayList<Connection> mst = new ArrayList<>();
            boolean[] visited = new boolean[nodes.size()]; // Track visited nodes
            visited[0] = true; // Start from the first node

            // Loop until all nodes are included in the MST
            while (mst.size() < nodes.size() - 1) {
                int minCost = Integer.MAX_VALUE;
                Connection minConnection = null;

                // Find the minimum weight edge connecting a visited node to an unvisited node
                for (Connection conn : connections) {
                    boolean node1Visited = visited[nodes.indexOf(conn.node1)];
                    boolean node2Visited = visited[nodes.indexOf(conn.node2)];

                    // Ensure one node is visited and the other is unvisited
                    if (node1Visited != node2Visited) {
                        if (conn.cost < minCost) {
                            minCost = conn.cost;
                            minConnection = conn;
                        }
                    }
                }

                if (minConnection != null) {
                    mst.add(minConnection);
                    // Mark the unvisited node as visited
                    if (!visited[nodes.indexOf(minConnection.node1)]) {
                        visited[nodes.indexOf(minConnection.node1)] = true;
                    } else {
                        visited[nodes.indexOf(minConnection.node2)] = true;
                    }
                } else {
                    // No valid connection found (graph is disconnected)
                    break;
                }
            }

            return mst;
        }

        // Method to get the shortest path using Dijkstra's algorithm (without PriorityQueue)
        public ArrayList<Node> getShortestPath(Node start, Node end, double costWeight, double bandwidthWeight) {
            double[] dist = new double[nodes.size()]; // Stores shortest distance to each node
            Node[] prev = new Node[nodes.size()]; // Stores the previous node in the shortest path
            Arrays.fill(dist, Double.MAX_VALUE); // Initialize distances as infinity
            dist[nodes.indexOf(start)] = 0; // Start node's distance is 0
            prev[nodes.indexOf(start)] = null; // Start node has no previous node

            boolean[] visited = new boolean[nodes.size()]; // Array to track visited nodes

            // Main loop for Dijkstra's algorithm
            for (int i = 0; i < nodes.size(); i++) {
                int u = -1;
                double minDist = Double.MAX_VALUE;

                // Manually find the unvisited node with the smallest distance
                for (int j = 0; j < nodes.size(); j++) {
                    if (!visited[j] && dist[j] < minDist) {
                        minDist = dist[j];
                        u = j;
                    }
                }

                if (u == -1) break; // Exit if no more reachable nodes
                visited[u] = true; // Mark node as visited
                Node currentNode = nodes.get(u);

                // Update distances for neighboring nodes
                for (Connection conn : connections) {
                    if (conn.node1 == currentNode || conn.node2 == currentNode) {
                        Node neighbor = (conn.node1 == currentNode) ? conn.node2 : conn.node1;
                        int v = nodes.indexOf(neighbor);

                        // Adjusted distance formula
                        double alt = dist[u] + (costWeight * conn.cost) + (bandwidthWeight / conn.bandwidth);

                        // Update distance if a shorter path is found
                        if (alt < dist[v]) {
                            dist[v] = alt;
                            prev[v] = currentNode;
                        }
                    }
                }
            }

            // Reconstruct the shortest path from start to end
            ArrayList<Node> path = new ArrayList<>();
            for (Node at = end; at != null; at = prev[nodes.indexOf(at)]) {
                path.add(at); // Add each node in the path
            }

            // Check if the end node was reached
            if (path.size() == 1 && path.get(0) != start) {
                // No path exists
                return new ArrayList<>();
            }

            Collections.reverse(path); // Reverse to get path from start to end
            return path;
        }
    }

    // GUI application class to interact with the user
    static class NetworkOptimizerGUIApp {
        JFrame frame; // The main window of the application
        NetworkGraph graph; // The network graph
        JPanel panel; // Panel to display the network
        JTextArea infoArea; // Text area to display information about the shortest path
        JTextField nodeNameField; // Input field to add a new node
        JTextField costField, bandwidthField; // Input fields to specify the cost and bandwidth of a connection
        JComboBox<String> nodeSelectorStart, nodeSelectorEnd; // Dropdowns to select nodes for the start and end of a connection
        JTextArea descriptionArea; // Text area to display detailed descriptions

        // Constructor to initialize the GUI
        NetworkOptimizerGUIApp() {
            graph = new NetworkGraph(); // Create a new empty network graph
            frame = new JFrame("Network Optimizer"); // Create the frame for the GUI
            panel = new JPanel() { // Override the paintComponent method to draw the network graph
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    // Draw all connections between nodes
                    for (Connection conn : graph.connections) {
                        g.drawLine(conn.node1.x, conn.node1.y, conn.node2.x, conn.node2.y);
                        g.drawString("Cost: " + conn.cost + ", Bandwidth: " + conn.bandwidth,
                                (conn.node1.x + conn.node2.x) / 2, (conn.node1.y + conn.node2.y) / 2);
                    }
                    // Draw all nodes in the network
                    for (Node node : graph.nodes) {
                        g.setColor(Color.BLUE);
                        g.fillOval(node.x - 15, node.y - 15, 30, 30); // Draw node as a circle
                        g.setColor(Color.WHITE);
                        g.drawString(node.name, node.x - 10, node.y + 5); // Draw the name of the node inside the circle
                    }
                }
            };
            panel.setPreferredSize(new Dimension(800, 600)); // Set the panel's preferred size
            frame.add(panel, BorderLayout.CENTER); // Add the panel to the center of the frame

            infoArea = new JTextArea(5, 40); // Create a text area to display information about the shortest path
            infoArea.setEditable(false); // Make the text area read-only
            frame.add(new JScrollPane(infoArea), BorderLayout.SOUTH); // Add the info area to the bottom of the frame

            // Panel to input a new node
            JPanel nodeInputPanel = new JPanel();
            nodeInputPanel.add(new JLabel("Node Name:"));
            nodeNameField = new JTextField(10); // Input field for the node name
            nodeInputPanel.add(nodeNameField);
            JButton addNodeButton = new JButton("Add Node"); // Button to add a new node
            addNodeButton.addActionListener(e -> addNode()); // Add action listener to the button
            nodeInputPanel.add(addNodeButton);
            frame.add(nodeInputPanel, BorderLayout.NORTH); // Add the node input panel to the top of the frame

            // Panel to input a new connection
            JPanel connectionInputPanel = new JPanel();
            nodeSelectorStart = new JComboBox<>(); // Dropdown to select the start node
            nodeSelectorEnd = new JComboBox<>(); // Dropdown to select the end node
            costField = new JTextField(5); // Input field for the cost of the connection
            bandwidthField = new JTextField(5); // Input field for the bandwidth of the connection
            connectionInputPanel.add(new JLabel("Start Node:"));
            connectionInputPanel.add(nodeSelectorStart);
            connectionInputPanel.add(new JLabel("End Node:"));
            connectionInputPanel.add(nodeSelectorEnd);
            connectionInputPanel.add(new JLabel("Cost:"));
            connectionInputPanel.add(costField);
            connectionInputPanel.add(new JLabel("Bandwidth:"));
            connectionInputPanel.add(bandwidthField);
            JButton addConnectionButton = new JButton("Add Connection"); // Button to add a new connection
            addConnectionButton.addActionListener(e -> addConnection()); // Add action listener to the button
            connectionInputPanel.add(addConnectionButton);
            frame.add(connectionInputPanel, BorderLayout.EAST); // Add the connection input panel to the right of the frame

            // Panel to calculate and display the MST or shortest path
            JPanel calculationPanel = new JPanel();
            JButton mstButton = new JButton("Calculate MST"); // Button to calculate MST
            mstButton.addActionListener(e -> calculateMST());
            JButton shortestPathButton = new JButton("Calculate Shortest Path"); // Button to calculate shortest path
            shortestPathButton.addActionListener(e -> calculateShortestPath());
            calculationPanel.add(mstButton);
            calculationPanel.add(shortestPathButton);
            frame.add(calculationPanel, BorderLayout.WEST); // Add the calculation panel to the left of the frame

            // Add a description area in the bottom-left corner
            descriptionArea = new JTextArea(10, 20);
            descriptionArea.setEditable(false);
            descriptionArea.setText("Description:\n");
            JScrollPane scrollPane = new JScrollPane(descriptionArea);
            frame.add(scrollPane, BorderLayout.SOUTH); // Add the description area to the bottom of the frame

            frame.pack(); // Pack the components to fit within the frame
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit when the window is closed
            frame.setVisible(true); // Make the window visible
        }

        // Method to add a node to the network
        void addNode() {
            String nodeName = nodeNameField.getText(); // Get the node name from the input field
            int x = 100 + (int) (Math.random() * 600); // Random x-coordinate for the node
            int y = 100 + (int) (Math.random() * 400); // Random y-coordinate for the node
            graph.addNode(nodeName, x, y); // Add the new node to the graph
            nodeSelectorStart.addItem(nodeName); // Add the node to the start node dropdown
            nodeSelectorEnd.addItem(nodeName); // Add the node to the end node dropdown

            // Update the description area
            descriptionArea.append("Node Added: " + nodeName + " at (" + x + ", " + y + ")\n");
            panel.repaint(); // Repaint the panel to show the new node
        }

        // Method to add a connection between two nodes
        void addConnection() {
            String startNodeName = (String) nodeSelectorStart.getSelectedItem(); // Get the start node name
            String endNodeName = (String) nodeSelectorEnd.getSelectedItem(); // Get the end node name
            Node startNode = null, endNode = null;

            // Find the start and end nodes by name
            for (Node node : graph.nodes) {
                if (node.name.equals(startNodeName)) startNode = node;
                if (node.name.equals(endNodeName)) endNode = node;
            }

            // Get the cost and bandwidth for the connection
            int cost = Integer.parseInt(costField.getText());
            int bandwidth = Integer.parseInt(bandwidthField.getText());

            // Add the new connection to the graph
            graph.addConnection(startNode, endNode, cost, bandwidth);
            descriptionArea.append("Connection Added: " + startNodeName + " <-> " + endNodeName +
                    " with Cost: " + cost + ", Bandwidth: " + bandwidth + "\n");
            panel.repaint(); // Repaint the panel to show the new connection
        }

        // Method to calculate and display the Minimum Spanning Tree (MST)
        void calculateMST() {
            ArrayList<Connection> mst = graph.getMinimumSpanningTree(); // Get the MST

            if (mst.size() < graph.nodes.size() - 1) {
                infoArea.setText("The graph is disconnected. Cannot calculate MST.");
                descriptionArea.append("The graph is disconnected. Cannot calculate MST.\n");
            } else {
                StringBuilder sb = new StringBuilder();
                for (Connection conn : mst) {
                    sb.append("Connection between ").append(conn.node1.name).append(" and ")
                            .append(conn.node2.name).append(" with cost: ").append(conn.cost)
                            .append(" and bandwidth: ").append(conn.bandwidth).append("\n");
                }
                infoArea.setText(sb.toString()); // Display the MST in the info area
                descriptionArea.append("MST Calculated:\n" + sb.toString() + "\n"); // Update the description area
            }
        }

        // Method to calculate and display the shortest path
        void calculateShortestPath() {
            String startNodeName = (String) nodeSelectorStart.getSelectedItem(); // Get the start node
            String endNodeName = (String) nodeSelectorEnd.getSelectedItem(); // Get the end node
            Node startNode = null, endNode = null;

            // Find the start and end nodes by name
            for (Node node : graph.nodes) {
                if (node.name.equals(startNodeName)) startNode = node;
                if (node.name.equals(endNodeName)) endNode = node;
            }

            // Ask user for cost and bandwidth weights
            double costWeight = 1.0; // Example default weight for cost
            double bandwidthWeight = 1.0; // Example default weight for bandwidth

            ArrayList<Node> path = graph.getShortestPath(startNode, endNode, costWeight, bandwidthWeight); // Get the shortest path

            if (path.isEmpty()) {
                infoArea.setText("No path exists between " + startNodeName + " and " + endNodeName + ".");
                descriptionArea.append("No path exists between " + startNodeName + " and " + endNodeName + ".\n");
            } else {
                StringBuilder sb = new StringBuilder("Shortest Path:\n");
                for (Node node : path) {
                    sb.append(node.name).append("\n"); // Display each node in the shortest path
                }
                infoArea.setText(sb.toString()); // Display the shortest path in the info area
                descriptionArea.append("Shortest Path Calculated:\n" + sb.toString() + "\n"); // Update the description area
            }
        }

        // Main method to run the GUI application
        public static void main(String[] args) {
            SwingUtilities.invokeLater(NetworkOptimizerGUIApp::new); // Launch the GUI
        }
    }
}

/* Testing result 
    Node Added: a at (650, 291)
    Node Added: b at (190, 312)
    Node Added: c at (242, 171)
    Node Added: d at (137, 302)
    Node Added: e at (121, 138)
    Connection Added: a <-> e with Cost: 1, Bandwidth: 2
    Connection Added: c <-> e with Cost: 2, Bandwidth: 2
    Connection Added: a <-> b with Cost: 9, Bandwidth: 9
    Connection Added: e <-> b with Cost: 2, Bandwidth: 2
    Shortest Path Calculated:
    Shortest Path:
    e
    b

    Connection Added: e <-> a with Cost: 2, Bandwidth: 2
    Shortest Path Calculated:
    Shortest Path:
    e
    a

    Shortest Path Calculated:
    Shortest Path:
    a
    e
    b

    Connection Added: d <-> b with Cost: 2, Bandwidth: 2
    MST Calculated:
    Connection between a and e with cost: 1 and bandwidth: 2
    Connection between c and e with cost: 2 and bandwidth: 2
    Connection between e and b with cost: 2 and bandwidth: 2
    Connection between d and b with cost: 2 and bandwidth: 2

 */