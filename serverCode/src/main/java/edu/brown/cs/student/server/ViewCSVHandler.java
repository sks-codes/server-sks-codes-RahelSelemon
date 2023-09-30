package edu.brown.cs.student.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.parser.CSVParser;
import edu.brown.cs.student.informationobjects.ListOfStringsCreator;
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
public class ViewCSVHandler implements Route {

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
    Type mapStringString = Types.newParameterizedType(Map.class, String.class, String.class);
    JsonAdapter<Map<String, String>> adapter1 = moshi.adapter(mapStringString);
    Type listString = Types.newParameterizedType(List.class, String.class, String.class);
    JsonAdapter<List<String>> adapter2 = moshi.adapter(listString);
    Map<String, String> responseMap = new HashMap<>();


    if (Server.getParsedCSV().isEmpty()){
      responseMap.put("type", "error");
      responseMap.put("error_type", "missing_parameter");
      responseMap.put("error_arg", "csv");
      return adapter1.toJson(responseMap);
    }

    // Generate the reply
    responseMap.put("type", "success");
    List<String> jsonList = new ArrayList<String>();
    for (List<String> strings : Server.getParsedCSV()) {
      jsonList.add(adapter2.toJson(strings));
    }
    responseMap.put("data", adapter2.toJson(jsonList));

    return adapter1.toJson(responseMap);
  }
}