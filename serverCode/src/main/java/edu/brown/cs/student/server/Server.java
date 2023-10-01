package edu.brown.cs.student.server;

import static spark.Spark.after;

import edu.brown.cs.student.parser.CSVParser;
import spark.Spark;

/**
 * Top-level class for this demo. Contains the main() method which starts Spark and runs the various handlers.
 *
 * We have two endpoints in this demo. They need to share state (a menu).
 * This is a great chance to use dependency injection, as we do here with the menu set. If we needed more endpoints,
 * more functionality classes, etc. we could make sure they all had the same shared state.
 */
public class Server {
  public static CSVParser csvParser;
  //public static List<List<String>> parsedCSV = new ArrayList<List<String>>();
  public static CSVParser getCSVParser(){
    return csvParser;
  }
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

    Spark.get("loadcsv", new LoadCSVHandler());
    Spark.get("viewcsv", new ViewCSVHandler());
    Spark.get("searchcsv", new SearchCSVHandler());
    Spark.get("broadband", new SearchCSVHandler());
    Spark.init();
    Spark.awaitInitialization();

    // Notice this link alone leads to a 404... Why is that?
    System.out.println("Server started at http://localhost:" + port);
  }
}