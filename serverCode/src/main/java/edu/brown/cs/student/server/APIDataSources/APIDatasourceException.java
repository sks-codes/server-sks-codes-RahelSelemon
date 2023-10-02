package edu.brown.cs.student.server.APIDataSources;

/**
 * class for error handling related to API datasource get and reading exceptions
 */
public class APIDatasourceException extends Exception {
  private final Throwable cause;

  public APIDatasourceException(String message) {
    super(message);
    this.cause = null;
  }
  public APIDatasourceException(String message, Throwable cause) {
    super(message);
    this.cause = cause;
  }
}