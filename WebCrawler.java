import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;
import javax.swing.*;

// Thread-safe queue for URLs to be crawled
class URLQueue {
    private final ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

    // Add a URL to the queue
    public void enqueue(String url) {
        queue.offer(url);
    }

    // Remove a URL from the queue
    public String dequeue() {
        return queue.poll();
    }

    // Check if the queue is empty
    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

// Thread-safe hash table to track visited URLs
class URLHashTable {
    private final ConcurrentHashMap<String, Boolean> visited = new ConcurrentHashMap<>();

    // Add URL to visited list, return true if it's new
    public boolean add(String url) {
        return visited.putIfAbsent(url, true) == null;
    }
}

// Worker task to fetch and process web pages
class CrawlerTask implements Runnable {
    private final URLQueue queue;       // URL queue
    private final URLHashTable visited; // Visited URLs table
    private final JTextArea resultArea; // UI output
    private final File file;            // File for storing results

    public CrawlerTask(URLQueue queue, URLHashTable visited, JTextArea resultArea, File file) {
        this.queue = queue;
        this.visited = visited;
        this.resultArea = resultArea;
        this.file = file;
    }

    @Override
    public void run() {
        while (true) {
            String url = queue.dequeue(); // Get next URL
            if (url == null) break;       // Stop if queue is empty
            fetchPage(url);
        }
    }

    // Fetches a web page
    private void fetchPage(String url) {
        if (url.startsWith("https://")) {
            SwingUtilities.invokeLater(() -> resultArea.append("Skipping HTTPS link: " + url + "\n"));
            return;
        }

        try {
            // Extract host, port, and path
            String host;
            int port = 80; // Default HTTP port
            String path = "/";
            String processedUrl = url.replace("http://", "");
            int slashIndex = processedUrl.indexOf('/');
            int colonIndex = processedUrl.indexOf(':');

            if (colonIndex != -1) {
                host = processedUrl.substring(0, colonIndex);
                port = Integer.parseInt(processedUrl.substring(colonIndex + 1, slashIndex != -1 ? slashIndex : processedUrl.length()));
            } else if (slashIndex != -1) {
                host = processedUrl.substring(0, slashIndex);
                path = processedUrl.substring(slashIndex);
            } else {
                host = processedUrl;
            }

            // Connect to server
            try (Socket socket = new Socket(host, port);
                 OutputStream os = socket.getOutputStream();
                 InputStream is = socket.getInputStream()) {

                // Send HTTP GET request
                String request = "GET " + path + " HTTP/1.1\r\n" +
                                 "Host: " + host + "\r\n" +
                                 "User-Agent: Mozilla/5.0\r\n" +
                                 "Accept: text/html\r\n" +
                                 "Connection: close\r\n\r\n";
                os.write(request.getBytes());
                os.flush();

                // Read response
                byte[] buffer = new byte[4096];
                int read = is.read(buffer);
                if (read > 0) {
                    SwingUtilities.invokeLater(() -> resultArea.append("Crawled: " + url + "\n"));
                    saveToFile(url);
                }
            }
        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> resultArea.append("Failed: " + url + " - " + e.getMessage() + "\n"));
        }
    }

    // Saves crawled URL to file (thread-safe)
    private synchronized void saveToFile(String url) {
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write(url + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// Swing GUI for web crawler
public class WebCrawler extends JFrame {
    private final JTextField urlField;
    private final JButton startButton;
    private final JTextArea resultArea;
    private final URLQueue queue;
    private final URLHashTable visited;
    private ExecutorService executor;
    private final File file;

    // Constructor
    public WebCrawler() {
        setTitle("Multithreaded Web Crawler");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // UI Panel
        JPanel panel = new JPanel(new FlowLayout());
        urlField = new JTextField(40);
        startButton = new JButton("Start Crawling");
        panel.add(urlField);
        panel.add(startButton);
        add(panel, BorderLayout.NORTH);

        // Results display
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        // Initialize data structures
        queue = new URLQueue();
        visited = new URLHashTable();
        file = new File("crawled_data.txt");

        // Start button action listener
        startButton.addActionListener(_ -> startCrawling());
    }

    // Start the crawling process
    private void startCrawling() {
        String input = urlField.getText().trim();
        if (input.isEmpty()) return;

        // Process multiple URLs
        String[] urls = input.split(",");
        for (String url : urls) {
            url = url.trim();
            if (!url.isEmpty() && visited.add(url)) {
                queue.enqueue(url);
                resultArea.append("Starting crawl from: " + url + "\n");
            }
        }

        // Create thread pool
        int numThreads = 3;
        executor = Executors.newFixedThreadPool(numThreads);

        // Submit tasks
        for (int i = 0; i < numThreads; i++) {
            executor.submit(new CrawlerTask(queue, visited, resultArea, file));
        }

        // Shutdown executor when done
        executor.shutdown();
        new Thread(() -> {
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                SwingUtilities.invokeLater(() -> resultArea.append("Crawling completed.\n"));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    // Main method to launch GUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WebCrawler().setVisible(true));
    }
}

/*Testing result
    input: http://example.com, http://example.org
    output: Starting crawl from: http://example.com
    Starting crawl from: http://example.org
    Crawled: http://example.org
    Crawled: http://example.com
    Crawling completed.
 */

