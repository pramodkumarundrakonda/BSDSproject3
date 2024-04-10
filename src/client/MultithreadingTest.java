package client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultithreadingTest {

    private static final ClientLogger logger = new ClientLogger();

    public static void main(String[] args) throws Exception {
        // Number of threads to run concurrently
        int numThreads = 5;

        // Create a thread pool with a fixed number of threads
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // Create an RMIClient instance for each thread
        for (int i = 1; i <= numThreads; i++) {
            RMIClient client = new RMIClient("Client" + i);

            // Execute concurrent operations for each client
            executor.execute(() -> {
                // Perform PUT operation
                client.put("key1", "value1");

                // Perform GET operation
                client.get("key1");

                // Perform DELETE operation
                client.delete("key1");
            });
        }

        // Shutdown the executor after all tasks are completed
        executor.shutdown();
    }
}
