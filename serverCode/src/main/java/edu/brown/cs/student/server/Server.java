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
    try {
      ACS_API source = new ACS_API();
      Spark.get("broadband", new BroadbandHandler(source));
    } catch(APIDatasourceException e) {
      Spark.get("viewcsv", new ViewCSVHandler());
    }
    Spark.init();
    Spark.awaitInitialization();

    // Notice this link alone leads to a 404... Why is that?
    System.out.println("Server started at http://localhost:" + port);
  }
}