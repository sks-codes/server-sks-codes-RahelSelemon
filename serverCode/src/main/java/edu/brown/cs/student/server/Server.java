package edu.brown.cs.student.server;

import static spark.Spark.after;

import edu.brown.cs.student.parser.CSVParser;
import edu.brown.cs.student.server.APIDataSources.ACS_API;
import edu.brown.cs.student.server.APIDataSources.APIDatasourceException;
import edu.brown.cs.student.server.handlers.BroadbandHandler;
import edu.brown.cs.student.server.handlers.LoadCSVHandler;
import edu.brown.cs.student.server.handlers.SearchCSVHandler;
import edu.brown.cs.student.server.handlers.ViewCSVHandler;
import spark.Spark;

/**
 * class that runs local server and handles API get request
 */
public class Server {
  public static CSVParser csvParser; //shared variable across handlers representing current loaded csv

  /**
   * get function for shared csvParser variable
   * @return CSVParser that is shared between handlers
   */
  public static CSVParser getCSVParser(){
    return csvParser;
  }

  /**
   * set function for shared csvParser variable
   */
  public static void setCsvParser(CSVParser newCsvParser){
    csvParser = newCsvParser;
  }

  public static void main(String[] args) {
    int port = 2323;
    Spark.port(port);

    after((request, response) -> {
      response.header("Access-Control-Allow-Origin", "*");
      response.header("Access-Control-Allow-Methods", "*");
    });

    //handling different endpoints
    Spark.get("loadcsv", new LoadCSVHandler());
    Spark.get("viewcsv", new ViewCSVHandler());
    Spark.get("searchcsv", new SearchCSVHandler());

    try {
      ACS_API source = new ACS_API();
      Spark.get("broadband", new BroadbandHandler(source));
    } catch(APIDatasourceException e) {
      Spark.get("viewcsv", new ViewCSVHandler()); //if broadband endpoint doesn't load
    }
    Spark.init();
    Spark.awaitInitialization();

    System.out.println("Server started at http://localhost:" + port);
  }
}