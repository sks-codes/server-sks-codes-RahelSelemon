package edu.brown.cs.student.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.searcher.*;
import java.util.ArrayList;
import java.util.List;
import spark.Request;
import spark.Response;
import spark.Route;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


/**
 * Handler class for the soup ordering API endpoint.
 *
 * This endpoint is similar to the endpoint(s) you'll need to create for Sprint 2. It takes a basic GET request with
 * no Json body, and returns a Json object in reply. The responses are more complex, but this should serve as a reference.
 *
 */
public class SearchCSVHandler implements Route {

  /**
   * Pick a convenient soup and make it. the most "convenient" soup is the first recipe we find in
   * the unordered set of recipe cards.
   *
   * @param request  the request to handle
   * @param response use to modify properties of the response
   * @return response content
   * @throws Exception This is part of the interface; we don't have to throw anything.
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    // Prepare to send a reply
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter1 = moshi.adapter(mapStringObject);
    Map<String, Object> responseMap = new HashMap<>();

    String searchString = request.queryParams("find");
    String searchCol = request.queryParams("col");
    SearchCSV searcher;
    if (searchString == null){
      responseMap.put("type", "error");
      responseMap.put("error_type", "error_bad_request");
      responseMap.put("error_arg", "csv");
      return adapter1.toJson(responseMap);
    }
    if (searchCol == null){
      searcher = new SearchCSV(Server.getCSVParser(), searchString);
    }
    else{
      try {
        int col_num = Integer.parseInt(searchCol);
        searcher = new SearchCSV(Server.getCSVParser(), searchString, col_num);
      }
      catch (NumberFormatException e){
        searcher = new SearchCSV(Server.getCSVParser(), searchString, searchCol);
      }
    }
    List<Location> found = searcher.search();
    List<List<String>> foundRows = new ArrayList<List<String>>();
    List<List<String>> parsedCSV = Server.getCSVParser().get_parsed_strings();
    for (Location item : found){
      foundRows.add(parsedCSV.get(item.getRow_ind()));
    }

    if (foundRows.isEmpty()){
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