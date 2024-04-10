package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The KeyValueService interface defines the remote methods for interacting with the key-value store server.
 */
public interface KeyValueService extends Remote {

    /**
     * Retrieves the value associated with the specified key.
     *
     * @param key       The key to retrieve the value.
     * @param requestId The unique identifier for the request.
     * @param clientID  The ID of the client making the request.
     * @return The value associated with the key, or null if the key does not exist.
     * @throws RemoteException if a remote communication error occurs.
     * @throws Exception       if an error occurs during the retrieval process.
     */
    String getValue(String key, String requestId, String clientID) throws RemoteException, Exception;

    /**
     * Inserts or updates the value associated with the specified key.
     *
     * @param key       The key to be inserted or updated.
     * @param value     The value to be associated with the key.
     * @param requestId The unique identifier for the request.
     * @param clientID  The ID of the client making the request.
     * @return true if the operation is successful, false otherwise.
     * @throws RemoteException if a remote communication error occurs.
     * @throws Exception       if an error occurs during the insertion or update process.
     */
    Boolean putValue(String key, String value, String requestId, String clientID) throws RemoteException, Exception;

    /**
     * Deletes the value associated with the specified key.
     *
     * @param key       The key to be deleted.
     * @param requestId The unique identifier for the request.
     * @param clientID  The ID of the client making the request.
     * @return true if the key is found and deleted, false otherwise.
     * @throws RemoteException if a remote communication error occurs.
     * @throws Exception       if an error occurs during the deletion process.
     */
    Boolean deleteValue(String key, String requestId, String clientID) throws RemoteException, Exception;

    /**
     * Prepares the transaction for the specified key.
     *
     * @param key The key involved in the transaction.
     * @return "ACK" if the prepare is successful, "NACK" otherwise.
     * @throws RemoteException if a remote communication error occurs.
     */
    String prepare(String key) throws RemoteException;

    /**
     * Commits the transaction.
     *
     * @throws RemoteException if a remote communication error occurs.
     */
    void commit() throws RemoteException;

    /**
     * Aborts the transaction.
     *
     * @throws RemoteException if a remote communication error occurs.
     */
    void abort() throws RemoteException;

    /**
     * Sets the coordinator for transaction management.
     *
     * @param coord The coordinator instance.
     * @throws RemoteException if a remote communication error occurs.
     */
    void setCoordinator(Coordinator coord) throws RemoteException;

    /**
     * Requests a transaction with an optional delete flag.
     *
     * @param isDeleteFlag Flag indicating delete operation (1 for delete, 0 for other operations).
     * @throws RemoteException if a remote communication error occurs.
     */
    void requestTxn(int isDeleteFlag) throws RemoteException;

    /**
     * Updates the key-value store with the given key-value pair.
     *
     * @param key   The key to update.
     * @param value The value to update.
     * @return "ACK" upon successful update.
     * @throws RemoteException if a remote communication error occurs.
     */
    String go(String key, String value) throws RemoteException;

    /**
     * Removes the key-value pair from the key-value store.
     *
     * @param key The key to remove.
     * @return "ACK" upon successful removal.
     * @throws RemoteException if a remote communication error occurs.
     */
    String remove(String key) throws RemoteException;

    void replicateOtherParticipants() throws RemoteException;
}
