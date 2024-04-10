package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Custom logger class for the server application.
 */
public class ServerLogger {
  private static final Logger logger = LogManager.getLogger(ServerLogger.class);

  /**
   * Logs an informational message.
   *
   * @param message The message to be logged.
   */
  public void info(String message) {
    logger.info(message);
  }

  /**
   * Logs an error message.
   *
   * @param message The error message to be logged.
   */
  public void error(String message) {
    logger.error(message);
  }

  /**
   * Logs a severe message.
   *
   * @param message The severe message to be logged.
   */
  public void severe(String message) {
    logger.fatal(message);
  }
}
