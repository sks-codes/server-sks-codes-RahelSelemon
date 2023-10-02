package edu.brown.cs.student.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.searcher.*;
import edu.brown.cs.student.server.Server;
import java.util.ArrayList;
import java.util.List;
import spark.Request;
import spark.Response;
import spark.Route;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


/**
 * Handler class for the searchcsv endpoint.
 */
public class SearchCSVHandler implements Route {

  /**
   * searches loaded csv for certain search term in optionally specified search colomn and displays
   * rows containing the desired terms
   *
   * @param request  the request to handle
   * @param response use to modify properties of the response
   * @return response content
   * @throws Exception This is part of the interface; we don't have to throw anything.
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {

    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter1 = moshi.adapter(mapStringObject);
    Map<String, Object> responseMap = new HashMap<>();

    String searchString = request.queryParams("find");
    String searchCol = request.queryParams("col");
    SearchCSV searcher;

    if (searchString == null){ //no input search term
      responseMap.put("type", "error");
      responseMap.put("error_type", "error_bad_request");
      responseMap.put("error_arg", "csv");
      return adapter1.toJson(responseMap);
    }
    if (searchCol == null){
      searcher = new SearchCSV(Server.getCSVParser(), searchString); //run search without specified column
    }
    else{
      try {
        int col_num = Integer.parseInt(searchCol); // if specified column can be parsed into int, run with int parameter
        searcher = new SearchCSV(Server.getCSVParser(), searchString, col_num);
      }
      catch (NumberFormatException e){
        searcher = new SearchCSV(Server.getCSVParser(), searchString, searchCol); //otherwise run with string parameter
      }
    }
    List<Location> found = searcher.search(); //running searcher
    List<List<String>> foundRows = new ArrayList<List<String>>();
    List<List<String>> parsedCSV = Server.getCSVParser().get_parsed_strings();
    for (Location item : found){
      foundRows.add(parsedCSV.get(item.getRow_ind())); //creating list of rows containing objects whose locations were found
    }

    if (foundRows.isEmpty()){ //nothing found by searcher
      responseMap.put("result", "error");
      responseMap.put("error_type", "error_bad_json");
      responseMap.put("error_arg", "csv");
      return adapter1.toJson(responseMap);
    }

    // Generate the reply
    responseMap.put("result", "success");
    responseMap.put("data", foundRows);

    return adapter1.toJson(responseMap);
  }
}