package client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides logging functionality for the client application.
 */
public class ClientLogger {
  private static final Logger logger = LogManager.getLogger(ClientLogger.class);

  /**
   * Logs an informational message.
   * @param message The message to be logged.
   */
  public void info(String message) {
    logger.info(message);
  }

  /**
   * Logs an error message.
   * @param message The error message to be logged.
   */
  public void error(String message) {
    logger.error(message);
  }

  /**
   * Logs a warning message.
   * @param message The warning message to be logged.
   */
  public void warn(String message) {
    logger.warn(message);
  }
}
