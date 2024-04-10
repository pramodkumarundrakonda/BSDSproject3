package client;

import server.KeyValueService;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;
import java.util.Scanner;

/**
 * ClientApp class represents the main client application for interacting with the key-value store server.
 */
public class ClientApp {

  private static final ClientLogger logger = new ClientLogger();

  /**
   * Main method to start the client application.
   *
   * @param args Command-line arguments (expects client ID as argument).
   */
  public static void main(String[] args) throws RemoteException, NotBoundException {
    try{
      // Check if client ID is provided as a command-line argument
      if (args.length < 1) {
        logger.error("Usage: java ClientApp <clientID>");
        return;
      }

      // Extract client ID from command-line argument
      String clientID = args[0];
      System.setProperty("logFileName", "logs/client" + clientID + ".log");

      // Log client startup information
      logger.info("Client started with ID: " + clientID);

      // Create an instance of RMIClient with the specified client ID
      RMIClient client = new RMIClient(clientID);

      // Prompt user to populate and perform initial operations on the key-value store
      Scanner scanner = new Scanner(System.in);
      System.out.println("Do you want to populate and perform initial operations on the key-value store? (yes/no)");
      String choice = scanner.nextLine();

      // Perform operations based on user choice
      if (choice.equalsIgnoreCase("yes")) {
        client.populateKeyValueStore();
        client.performOperations();
      }

      // Start accepting user input for key-value store operations
      do {
        System.out.println("Waiting for user input");
        System.out.println("Please Enter \"PUT/GET/DELETE Key Value\" or enter \"quit\" to exit the application:");
        String input = scanner.nextLine();

        // Check if user wants to quit
        if (input.equalsIgnoreCase("quit")) {
          System.out.println("Exiting the client application.");
          break; // Exit the loop if "QUIT" is entered
        }

        // Process user input and send request to server
        String[] parts = input.split(" ");
        if (((parts[0].equalsIgnoreCase("DELETE") ||
                parts[0].equalsIgnoreCase("GET")) && parts.length == 2) || parts.length == 3) {
          client.sendRequest(input);
        } else {
          System.out.println("Invalid input format. Please follow \"PUT/GET/DELETE Key Value\" format.");
          logger.warn("Invalid input format received from user: " + input);
        }
      } while (true);
    }catch (Exception e){
      logger.error("Error in RMI client: " + e.getMessage());
    }


  }
}
