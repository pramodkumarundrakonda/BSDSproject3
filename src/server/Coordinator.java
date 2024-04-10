package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Coordinator interface for managing transactions and participants.
 */
public interface Coordinator extends Remote {

    /**
     * Initiates the preparation phase of a transaction for the specified key.
     *
     * @param key The key involved in the transaction.
     * @throws RemoteException if a remote communication error occurs.
     */
    void prepareTransaction(String key) throws RemoteException;

    /**
     * Adds a participant to the transaction coordination.
     *
     * @param participant The participant to be added.
     * @throws RemoteException if a remote communication error occurs.
     */
    void addParticipant(KeyValueService participant) throws RemoteException;

    /**
     * Retrieves the list of participants in the transaction coordination.
     *
     * @return The list of KeyValueService participants.
     * @throws RemoteException if a remote communication error occurs.
     */
    List<KeyValueService> getParticipants() throws RemoteException;
}
