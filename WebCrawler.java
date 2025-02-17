/* Question no: 6b
 * This program is designed to crawl web pages concurrently. The user enters a URL and clicks "Start Crawling".
 * Then the URL is added to a custom queue (URLQueue) and stored in a custom hash table (URLHashTable) to track visited URLs.
 * Three worker threads (CrawlerThread) continuously fetch URLs from the queue and each thread establishes a raw socket connection 
 * to the URL's host and manually sends an HTTP GET request.Then the response is processed, and valid links are extracted using 
 * manual string parsing (basic HTML scanning).After that extracted links are enqueued for further crawling (if not already visited).
 * Results are displayed in a Swing GUI.
 * 
 * Features:
 * - Uses a custom linked-list queue for URL management.
 * - Implements a custom hash table for visited URLs.
 * - Uses raw socket programming to fetch web pages.
 * - Multi-threaded crawling with three concurrent threads.
 * - Extracts new links manually for deeper crawling.
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    }

    // Method to dequeue URLs from the queue
    public synchronized String dequeue() {
        if (front == null) return null;
        String url = front.url;
        front = front.next;
        if (front == null) rear = null;
        return url;
    }

    // Check if the queue is empty
    public boolean isEmpty() { return front == null; }
}

// Custom hash table for visited URLs
class URLHashTable {
    private static class Entry {
        String url;
        Entry next;
        Entry(String url) { this.url = url; this.next = null; }
    }
    private Entry[] table;
    private int size = 1000;

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
    private URLQueue queue;
    private URLHashTable visited;
    private JTextArea resultArea;

    // Constructor for CrawlerTask
    public CrawlerTask(URLQueue queue, URLHashTable visited, JTextArea resultArea) {
        this.queue = queue;
        this.visited = visited;
        this.resultArea = resultArea;
    }

    @Override
    public void run() {
        String url = queue.dequeue();
        if (url != null) {
            fetchPage(url);
        }
    }

    // Method to fetch a page and extract links
    private void fetchPage(String url) {
        try {
            // Check if the URL is HTTPS, if yes, skip processing
            if (url.startsWith("https://")) {
                SwingUtilities.invokeLater(() -> resultArea.append("Skipping HTTPS link: " + url + "\n"));
                return; // Skip this URL as it uses HTTPS
            }

            // Extract host from URL
            String host = url.replace("http://", "").split("/")[0];

            // Establish socket connection for HTTP protocol
            Socket socket = new Socket(host, 80);
            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();

            // Send GET request to fetch the page
            os.write(("GET / HTTP/1.1\r\nHost: " + host + "\r\n\r\n").getBytes());
            os.flush();

            // Read response from the server
            byte[] buffer = new byte[4096];
            int read = is.read(buffer);
            if (read > 0) {
                String response = new String(buffer, 0, read);
                SwingUtilities.invokeLater(() -> resultArea.append("Crawled: " + url + "\n"));
                extractLinks(response, host); // Extract links from the page content
            }
            socket.close();
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> resultArea.append("Failed: " + url + "\n"));
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
            if (!link.startsWith("http")) {
                link = "http://" + host + link; // Prepend base URL if it's a relative link
            }

            // Filter out invalid links like mailto and javascript links
            if (link.startsWith("javascript:") || link.startsWith("mailto:")) {
                continue; // Skip these types of links
            }

            // If the link has not been visited, add it to the queue
            if (visited.add(link)) {
                queue.enqueue(link);
            }
        }
    }
}

// Swing GUI for web crawler
public class WebCrawler extends JFrame {
    private JTextField urlField;
    private JButton startButton;
    private JTextArea resultArea;
    private URLQueue queue;
    private URLHashTable visited;

    // Constructor for WebCrawlerSwing
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
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startCrawling(); // Start crawling when the button is clicked
            }
        });
    }

    // Method to start crawling from the provided URL
    private void startCrawling() {
        String startUrl = urlField.getText().trim();
        if (!startUrl.isEmpty() && visited.add(startUrl)) {
            queue.enqueue(startUrl); // Add the starting URL to the queue
            resultArea.append("Starting crawl from: " + startUrl + "\n");

            // Create and start the ExecutorService with 3 threads
            ExecutorService executor = Executors.newFixedThreadPool(3); // Use ExecutorService for thread pool
            for (int i = 0; i < 3; i++) {
                executor.submit(new CrawlerTask(queue, visited, resultArea)); // Submit crawling tasks
            }
            executor.shutdown(); // Shutdown the executor after all tasks are submitted
        }
    }

    // Main method to launch the GUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WebCrawler().setVisible(true)); // Launch the app on the event dispatch thread
    }
}


/* Testing Results
    Starting crawl from: www.google.com
    Crawled: www.google.com
    Starting crawl from: www.google.com/home
    Crawled: www.google.com/home
    Starting crawl from: www.google.com/home/aboutus
    Crawled: www.google.com/home/aboutus 
 */