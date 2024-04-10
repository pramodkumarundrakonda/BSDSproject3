package client;

import server.KeyValueService;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

/**
 * RMIClient class represents a client using RMI protocol to interact with a KeyValueService.
 */
public class RMIClient {
    private String clientId;
    private static final ClientLogger logger = new ClientLogger();
    private KeyValueService keyValueService;

    /**
     * Constructs an RMIClient with the specified client ID.
     *
     * @param clientId The client ID for this RMIClient instance.
     */
    public RMIClient(String clientId) throws Exception {
        this.clientId = clientId;
        try {
            Random rand = new Random();
            int randomNum = 5001 + rand.nextInt(5005 - 5001);

            Registry registry = LocateRegistry.getRegistry("localhost", randomNum);

            keyValueService = (KeyValueService) registry.lookup("keyValueService");
            logger.info("Client started on port: ");
            logger.info(String.valueOf(randomNum));

        } catch (Exception e) {
            logger.error("Error connecting to RMI server: " + e.getMessage());
            throw new Exception(e);
        }
    }

    /**
     * Sends a PUT request to the server.
     *
     * @param key   The key to be inserted.
     * @param value The value associated with the key.
     */
    public void put(String key, String value) {
        try {
            keyValueService.requestTxn(0);
            keyValueService.replicateOtherParticipants();
            String packetId = String.valueOf(System.currentTimeMillis());
            boolean result = keyValueService.putValue(key, value, packetId, clientId);
            if(result){
                logger.info("PUT: Key=" + key + ", Value=" + value + ", PacketID=" + packetId + ", Result=" + " Insertion Successful");
            }else{
                logger.info("PUT: Key=" + key + ", Value=" + value + ", PacketID=" + packetId + ", Result=" + " Insertion Unsuccessful");
            }

        } catch (Exception e) {
            logger.error("Error sending PUT request: " + e.getMessage());
        }
    }

    /**
     * Sends a GET request to the server.
     *
     * @param key The key to retrieve the value.
     */
    public void get(String key) {
        try {
            keyValueService.requestTxn(0);
            keyValueService.replicateOtherParticipants();
            String packetId = String.valueOf(System.currentTimeMillis());
            String result = keyValueService.getValue(key, packetId, clientId);
            if (result.equals("null")) {
                result = "Key not found";
                logger.info("Key not found");
            }else if (result.equals("abort")){
                result = "Get operation is aborted";
            }
            logger.info("GET: Key=" + key + ", PacketID=" + packetId + ", Retrieved value = " + result);
        } catch (Exception e) {
            logger.error("Error sending GET request: " + e.getMessage());
        }
    }

    /**
     * Sends a DELETE request to the server.
     *
     * @param key The key to be deleted.
     */
    public void delete(String key) {
        try {
            keyValueService.requestTxn(1);
            keyValueService.replicateOtherParticipants();
            String packetId = String.valueOf(System.currentTimeMillis());
            boolean result = keyValueService.deleteValue(key, packetId, clientId);
            String response = result ? "Deletion Successful" : "Key not found to delete";
            logger.info("DELETE: Key=" + key + ", PacketID=" + packetId + ", Result=" + response);
        } catch (Exception e) {
            logger.error("Error sending DELETE request: " + e.getMessage());
        }
    }

    /**
     * Populates the key-value store with initial data.
     */
    public void populateKeyValueStore() {
        logger.info("-------------------Initializing the key-value store with 10 pairs---------------");

        for (int i = 1; i <= 10; i++) {
            String key = "key" + i;
            put(key, String.valueOf(i));
        }
        logger.info("---------------Initialization Done !!! -------------------------------------------");
    }

    /**
     * Performs a set of PUT, GET, and DELETE operations on the server.
     */
    public void performOperations() {
        logger.info("---------------Performing 5 PUT/GET/DELETE Operations ------------------------");
        for (int i = 11; i <= 15; i++) {
            String key = "key" + i;

            // Perform PUT operation
            put(key, String.valueOf(i));

            // Perform GET operation
            get(key);

            // Perform DELETE operation
            delete(key);
        }
    }

    /**
     * Sends a request to the server based on the input data.
     *
     * @param requestData The request data containing the operation (PUT/GET/DELETE) and key-value pair.
     */
    void sendRequest(String requestData) {
        // Parse the request and extract the operation (PUT, GET, DELETE) and data (key, value)

        String requestString = requestData.trim();
        String[] parts = requestString.split(" ");
        String operation = parts[0];
        String key = parts[1];
        String value = parts.length > 2 ? parts[2] : null;

        try {

            switch (operation.toUpperCase()) {
                case "PUT":
                    logger.info("Sending Request to Insert Key & value");
                    put(key, value);
                    break;
                case "GET":
                    logger.info("Sending Request to Retrieve Key and its value");
                    get(key);
                    break;
                case "DELETE":
                    logger.info("Sending Request to Delete the Key");
                    delete(key);
                    break;
                default:
                    logger.error("Invalid operation. Must be one of (PUT, GET, DELETE)");
                    break;
            }
        } catch (Exception e) {
            // Handle exceptions
            logger.error("Error processing request: " + e.getMessage());
        }
    }
}
