package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.List;

/**
 * Main class for starting the RMI server.
 */
public class ServerApp {

  // Initialize the server logger
  private static final ServerLogger logger = new ServerLogger();

  /**
   * Main method to start the RMI server.
   *
   * @param args Command-line arguments (not used in this application).
   */
  public static void main(String[] args) {
    try {
      // Log that the server is starting
      logger.info("Starting the server");

      // Define participant hosts and ports
      List<String> participantHosts = Arrays.asList("localhost", "localhost", "localhost", "localhost", "localhost");
      List<Integer> participantPorts = Arrays.asList(5001, 5002, 5003, 5004, 5005);

      // Create and bind KeyValueService instances to registries
      for (int i = 0; i < 5; i++) {
        KeyValueServiceImpl participant = new KeyValueServiceImpl ();
        Registry participantRegistry = LocateRegistry.createRegistry(participantPorts.get(i));
        participantRegistry.bind("keyValueService", participant);
      }

      // Create and bind Coordinator instance
      CoordinatorImpl coordinator = new CoordinatorImpl();
      Registry coordinatorRegistry = LocateRegistry.createRegistry(1099);
      coordinatorRegistry.bind("Coordinator", coordinator);

      // Connect participants to the coordinator
      for (int i = 0; i < participantHosts.size(); i++) {
        // Log participant details
        logger.info("Participant: " + participantHosts.get(i) + " " + participantPorts.get(i));

        // Look up participant and coordinator
        Registry registry = LocateRegistry.getRegistry(participantHosts.get(i), participantPorts.get(i));
        KeyValueService participant = (KeyValueService) registry.lookup("keyValueService");
        coordinator.addParticipant(participant);

        // Set coordinator for participant
        Registry coordinatorReg = LocateRegistry.getRegistry("localhost", 1099);
        Coordinator c = (Coordinator) coordinatorReg.lookup("Coordinator");
        participant.setCoordinator(c);
      }

      // Log that servers are ready
      logger.info("Servers ready");
    } catch (Exception e) {
      // Log server exception
      logger.error("Server exception: " + e.toString());
      e.printStackTrace();
    }
  }
}
