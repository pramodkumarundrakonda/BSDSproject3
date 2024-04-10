package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the KeyValueService interface using ConcurrentHashMap and Locks for synchronization.
 */
public class KeyValueServiceImpl extends UnicastRemoteObject implements KeyValueService {
    private static final Logger LOGGER = Logger.getLogger(KeyValueServiceImpl.class.getName());
    private ConcurrentHashMap<String, String> keyValueStore;
    private Lock lock;

    private boolean isReplicationNeeded = false;
    private Coordinator coordinator;
    private ExecutorService executor;
    private boolean proceed;
    private int isDeleteFlag;



    /**
     * Constructs a KeyValueServiceImpl object.
     *
     * @throws RemoteException if an RMI-related exception occurs.
     */
    public KeyValueServiceImpl() throws RemoteException {
        keyValueStore = new ConcurrentHashMap<>();
        lock = new ReentrantLock();
    }


    @Override
    public String getValue(String key, String requestId, String clientID) throws Exception {
        coordinator.prepareTransaction(key);
        if (proceed) {
            lock.lock(); // Acquire the lock
            try {
                LOGGER.log(Level.INFO, "Request to get value for key: " + key +
                        " (Request ID: " + requestId + ")" + "(Client ID: " + clientID + ")");
                String value = keyValueStore.get(key);
                if(value == null){
                    return "null";
                }
                LOGGER.log(Level.INFO, "Value retrieved successfully: Key - " + key +
                        ", Value - " + value + " (Request ID: " + requestId + ")" + "(Client ID: " + clientID + ")");
                return value;
            } finally {
                lock.unlock(); // Release the lock in a final block
            }
        }
        return "abort";

    }

    @Override
    public Boolean putValue(String key, String value, String requestId, String clientID) throws RemoteException, Exception {
        if (isReplicationNeeded) {
            coordinator.prepareTransaction(key);
        }
        if (proceed) {
            lock.lock(); // Acquire the lock
            try {
                LOGGER.log(Level.INFO, "Request to put value: Key - " + key + ", Value - " + value +
                        " (Request ID: " + requestId + ")" + "(Client ID: " + clientID + ")");
                keyValueStore.put(key, value);
                LOGGER.log(Level.INFO, "Value inserted successfully: Key - " + key + ", Value - " + value +
                        " (Request ID: " + requestId + ")" + "(Client ID: " + clientID + ")");
                if (isReplicationNeeded) {
                    // Send put request to all participants except this one
                    for (KeyValueService participant : coordinator.getParticipants()) {
                        if (!participant.equals(this)) {
                            participant.go(key, value);
                        }
                    }
                }
                return true;
            } finally {
                lock.unlock(); // Release the lock in a final block
            }
        }
        return false;
    }

    @Override
    public Boolean deleteValue(String key, String requestId, String clientID) throws RemoteException, Exception {
        if (isReplicationNeeded) {
            coordinator.prepareTransaction(key);
        }
        if (proceed) {
            lock.lock(); // Acquire the lock
            try {
                LOGGER.log(Level.INFO, "Request to delete value for key: " + key +
                        " (Request ID: " + requestId + ")" + "(Client ID: " + clientID + ")");
                boolean deleted = keyValueStore.remove(key) != null;
                LOGGER.log(Level.INFO, "Value deletion status: Key - " + key + ", Deleted - " + deleted +
                        " (Request ID: " + requestId + ")" + "(Client ID: " + clientID + ")");

                if (isReplicationNeeded) {
                    // Send put request to all participants except this one
                    for (KeyValueService participant : coordinator.getParticipants()) {
                        if (!participant.equals(this)) {
                            participant.remove(key);
                        }
                    }
                }
                return deleted;
            } finally {
                lock.unlock(); // Release the lock in a final block
            }
        }
        return false;
    }

    @Override
    public String prepare(String key) throws RemoteException {
        if(isDeleteFlag == 1){
            if (!keyValueStore.containsKey(key)){
                return "NACK";
            }
        }else{
            return "ACK";
        }
        return "ACK";
    }

    @Override
    public void commit() throws RemoteException {
        proceed = true;
    }

    @Override
    public void abort() throws RemoteException {
        proceed = false;
    }

    @Override
    public void setCoordinator(Coordinator c) throws RemoteException{
        this.coordinator = c;
    }

    @Override
    public void requestTxn(int isDeleteFlag) throws RemoteException {
        this.isDeleteFlag = isDeleteFlag;

    }

    public void replicateOtherParticipants(){
        this.isReplicationNeeded = true;
    }

    public String go(String key, String value) throws RemoteException {
        // Update the local store
        synchronized (keyValueStore) {
            keyValueStore.put(key, value);
        }
        return "ACK";
    }

    public String remove(String key) throws RemoteException {
        // Update the local store
        synchronized (keyValueStore) {
            keyValueStore.remove(key);
        }
        return "ACK";
    }

}