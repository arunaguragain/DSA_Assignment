import java.util.Scanner;
/* Question no: 6a
 * ThreadController class ensures three threads coordinate to print numbers in correct order.
 * It synchronizes threads using wait() and notifyAll().
 * A shared integer state is used to determine which thread should execute next.A `synchronized` method ensures 
 * that only one thread runs at a time. The `wait()` method pauses a thread if it's not its turn. 
 * The `notifyAll()` method wakes up waiting threads when the state changes.
 */
public class ThreadController {
    private int n; // Upper limit for printing numbers
    private NumberPrinter printer; // Instance of NumberPrinter
    private int state = 0; // Controls which thread should execute next

    public ThreadController(int n, NumberPrinter printer) {
        this.n = n;
        this.printer = printer;
    }

    // Method to be executed by ZeroThread
    public synchronized void printZero() throws InterruptedException {
        for (int i = 1; i <= n; i++) {
            while (state != 0) { // Wait until it's zero's turn
                wait();
            }
            printer.printZero(); // Print 0
            state = (i % 2 == 0) ? 2 : 1; // Determine next thread (odd/even)
            notifyAll(); // Wake up waiting threads
        }
    }

    // Method to be executed by OddThread
    public synchronized void printOdd() throws InterruptedException {
        for (int i = 1; i <= n; i += 2) {
            while (state != 1) { // Wait until it's odd number's turn
                wait();
            }
            printer.printOdd(i); // Print odd number
            state = 0; // Set state back to zero thread
            notifyAll();
        }
    }

    // Method to be executed by EvenThread
    public synchronized void printEven() throws InterruptedException {
        for (int i = 2; i <= n; i += 2) {
            while (state != 2) { // Wait until it's even number's turn
                wait();
            }
            printer.printEven(i); // Print even number
            state = 0; // Set state back to zero thread
            notifyAll();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the value of n: ");
        int n = scanner.nextInt();
        scanner.close();

        NumberPrinter printer = new NumberPrinter();
        ThreadController controller = new ThreadController(n, printer);

        // Create threads
        Thread zeroThread = new Thread(() -> {
            try {
                controller.printZero();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread evenThread = new Thread(() -> {
            try {
                controller.printEven();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread oddThread = new Thread(() -> {
            try {
                controller.printOdd();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Start threads
        zeroThread.start();
        evenThread.start();
        oddThread.start();

        // Join threads to wait for completion
        try {
            zeroThread.join();
            evenThread.join();
            oddThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

// NumberPrinter class with methods to print 0, even, and odd numbers.
class NumberPrinter {
    public void printZero() {
        System.out.print("0");
    }

    public void printEven(int number) {
        System.out.print(number);
    }

    public void printOdd(int number) {
        System.out.print(number);
    }
}

/* Testing Results
    Example 1
    Enter the value of n: 5
    0102030405

    Example 2
    Enter the value of n: 7
    01020304050607  
 */