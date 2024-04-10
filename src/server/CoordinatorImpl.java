package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Implementation of the Coordinator interface for managing transactions and participants.
 */
public class CoordinatorImpl extends UnicastRemoteObject implements Coordinator {
  private static final Logger LOGGER = Logger.getLogger(CoordinatorImpl.class.getName());

  private boolean commit = true; // Flag to track transaction commit status

  private List<KeyValueService> participants; // List of participants in the transaction

  /**
   * Constructs a CoordinatorImpl object.
   *
   * @throws RemoteException if an RMI-related exception occurs.
   */
  public CoordinatorImpl() throws RemoteException {
    super();
    participants = new ArrayList<>(); // Initialize the list of participants
  }

  @Override
  public void prepareTransaction(String key) throws RemoteException {
    for (KeyValueService participant : participants) {
      // Check if all participants respond with "ACK" for prepare
      if (!participant.prepare(key).equals("ACK")) {
        commit = false; // Set commit flag to false if any participant responds with "NACK"
        return;
      }else{
        commit = true;
      }

    }
    if (commit) {
      for (KeyValueService participant : participants) {
        participant.commit(); // Commit the transaction for all participants
      }
    } else {
      for (KeyValueService participant : participants) {
        participant.abort(); // Abort the transaction for all participants if commit is false
      }
    }
  }

  /**
   * Adds a participant to the transaction coordination.
   *
   * @param participant The participant to be added.
   * @throws RemoteException if a remote communication error occurs.
   */
  public void addParticipant(KeyValueService participant) throws RemoteException {
    this.participants.add(participant); // Add the participant to the list
  }

  /**
   * Retrieves the list of participants in the transaction coordination.
   *
   * @return The list of KeyValueService participants.
   * @throws RemoteException if a remote communication error occurs.
   */
  public List<KeyValueService> getParticipants() throws RemoteException {
    return this.participants; // Return the list of participants
  }
}
