import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import javax.swing.*;

// Custom linked list queue for managing URLs
class URLQueue {
    private static class Node {
        String url;
        Node next;
        Node(String url) { this.url = url; this.next = null; }
    }
    private Node front, rear;

    // Constructor for URLQueue
    public URLQueue() { front = rear = null; }

    // Method to enqueue URLs to the queue
    public synchronized void enqueue(String url) {
        Node newNode = new Node(url);
        if (rear == null) front = rear = newNode;
        else { rear.next = newNode; rear = newNode; }
        notifyAll(); // Notify waiting threads that a new URL is available
    }

    // Method to dequeue URLs from the queue
    public synchronized String dequeue() {
        while (front == null) {
            try {
                wait(); // Wait until a URL is available
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        String url = front.url;
        front = front.next;
        if (front == null) rear = null;
        return url;
    }

    // Check if the queue is empty
    public synchronized boolean isEmpty() { return front == null; }
}

// Custom hash table for visited URLs
class URLHashTable {
    private static class Entry {
        String url;
        Entry next;
        Entry(String url) { this.url = url; this.next = null; }
    }
    private final Entry[] table;
    private final int size = 1000;

    // Constructor for URLHashTable
    public URLHashTable() { table = new Entry[size]; }

    // Simple hash function to map URLs to hash table slots
    private int hash(String url) {
        int hash = 0;
        for (char c : url.toCharArray()) hash = (hash * 31 + c) % size;
        return hash;
    }

    // Add a URL to the hash table, return true if added, false if already exists
    public synchronized boolean add(String url) {
        int index = hash(url);
        Entry curr = table[index];
        while (curr != null) {
            if (curr.url.equals(url)) return false;
            curr = curr.next;
        }
        Entry newEntry = new Entry(url);
        newEntry.next = table[index];
        table[index] = newEntry;
        return true;
    }
}

// Worker thread for crawling web pages
class CrawlerTask implements Runnable {
    private final URLQueue queue;
    private final URLHashTable visited;
    private final JTextArea resultArea;
    private volatile boolean running = true;

    // Constructor for CrawlerTask
    public CrawlerTask(URLQueue queue, URLHashTable visited, JTextArea resultArea) {
        this.queue = queue;
        this.visited = visited;
        this.resultArea = resultArea;
    }

    @Override
    public void run() {
        while (running) {
            String url = queue.dequeue();
            if (url != null) {
                fetchPage(url);
            }
        }
    }

    // Method to fetch a page and extract links
    private void fetchPage(String url) {
        String finalUrl = url; // Create an effectively final variable
        try {
            // Check if the URL is HTTPS, if yes, skip processing
            if (finalUrl.startsWith("https://")) {
                SwingUtilities.invokeLater(() -> resultArea.append("Skipping HTTPS link: " + finalUrl + "\n"));
                return; // Skip this URL as it uses HTTPS
            }

            // Extract host, port, and path from URL
            String host;
            int port = 80; // Default HTTP port
            String path = "/";
            String processedUrl = finalUrl.replace("http://", ""); // Remove "http://"
            int colonIndex = processedUrl.indexOf(':');
            int slashIndex = processedUrl.indexOf('/');
            if (colonIndex != -1) {
                host = processedUrl.substring(0, colonIndex);
                String portString = processedUrl.substring(colonIndex + 1, slashIndex != -1 ? slashIndex : processedUrl.length());
                if (!portString.isEmpty()) { // Check if port is not empty
                    port = Integer.parseInt(portString);
                }
            } else if (slashIndex != -1) {
                host = processedUrl.substring(0, slashIndex);
                path = processedUrl.substring(slashIndex);
            } else {
                host = processedUrl;
            }

            // Debug: Print extracted host, port, and path
            System.out.println("Host: " + host);
            System.out.println("Port: " + port);
            System.out.println("Path: " + path);

            // Establish socket connection for HTTP protocol
            try (Socket socket = new Socket(host, port);
                 OutputStream os = socket.getOutputStream();
                 InputStream is = socket.getInputStream()) {
                // Send GET request with headers
                String request = "GET " + path + " HTTP/1.1\r\n" +
                                 "Host: " + host + "\r\n" +
                                 "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64)\r\n" +
                                 "Accept: text/html\r\n" +
                                 "Connection: close\r\n\r\n";
                os.write(request.getBytes());
                os.flush();

                // Read response from the server
                byte[] buffer = new byte[4096];
                int read = is.read(buffer);
                if (read > 0) {
                    String response = new String(buffer, 0, read);
                    SwingUtilities.invokeLater(() -> resultArea.append("Crawled: " + finalUrl + "\n"));
                    extractLinks(response, host); // Extract links from the page content
                }
            }
        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> resultArea.append("Failed: " + finalUrl + " - " + e.getMessage() + "\n"));
            e.printStackTrace(); // Print stack trace for debugging
        }
    }

    // Extract links from the page content
    private void extractLinks(String content, String host) {
        int index = 0;
        while ((index = content.indexOf("href=\"", index)) != -1) {
            index += 6;
            int endIndex = content.indexOf("\"", index);
            if (endIndex == -1) break;
            String link = content.substring(index, endIndex);

            // If the link does not start with "http", make it absolute
            String absoluteLink = link; // Create a new variable for the absolute link
            if (!absoluteLink.startsWith("http")) {
                absoluteLink = "http://" + host + absoluteLink; // Modify the new variable
            }

            // Filter out invalid links like mailto and javascript links
            if (absoluteLink.startsWith("javascript:") || absoluteLink.startsWith("mailto:")) {
                continue; // Skip these types of links
            }

            // If the link has not been visited, add it to the queue
            String finalLink = absoluteLink; // Create an effectively final variable
            if (visited.add(finalLink)) {
                queue.enqueue(finalLink);
                SwingUtilities.invokeLater(() -> resultArea.append("Extracted link: " + finalLink + "\n"));
            }
        }
    }

    // Stop the crawler task
    public void stop() {
        running = false;
    }
}

// Swing GUI for web crawler
public class WebCrawler extends JFrame {
    private final JTextField urlField;
    private final JButton startButton;
    private final JTextArea resultArea;
    private final URLQueue queue;
    private final URLHashTable visited;
    private Thread[] threads;

    // Constructor for WebCrawler
    public WebCrawler() {
        setTitle("Multithreaded Web Crawler");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel for URL input and start button
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        urlField = new JTextField(40);
        startButton = new JButton("Start Crawling");
        panel.add(urlField);
        panel.add(startButton);
        add(panel, BorderLayout.NORTH);

        // Text area for displaying crawl results
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        // Initialize the queue and visited hash table
        queue = new URLQueue();
        visited = new URLHashTable();

        // Start button action listener
        startButton.addActionListener(_ -> startCrawling());
    }

    // Method to start crawling from the provided URL
    private void startCrawling() {
        String input = urlField.getText().trim();
        if (input.isEmpty()) {
            return; // Do nothing if the input is empty
        }

        // Split the input by commas to get individual URLs
        String[] urls = input.split(",");
        for (String url : urls) {
            url = url.trim(); // Remove leading/trailing spaces
            if (!url.isEmpty() && visited.add(url)) {
                queue.enqueue(url); // Add the URL to the queue
                resultArea.append("Starting crawl from: " + url + "\n");
            }
        }

        // Create and start worker threads for crawling
        int numThreads = 3; // Number of worker threads
        threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(new CrawlerTask(queue, visited, resultArea));
            threads[i].start();
        }
    }

    // Main method to launch the GUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WebCrawler().setVisible(true)); // Launch the app on the event dispatch thread
    }
}

/* Testing */