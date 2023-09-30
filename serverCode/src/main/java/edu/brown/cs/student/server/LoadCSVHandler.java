package edu.brown.cs.student.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.parser.CSVParser;
import edu.brown.cs.student.informationobjects.ListOfStringsCreator;
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
public class LoadCSVHandler implements Route {
  Server currServer;
  CSVParser csv;
  String filepath;

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
    filepath = request.queryParams("filepath");
    csv = new CSVParser(filepath, false, new ListOfStringsCreator());
    Server.setParsedCSV(csv.parse());
    // Prepare to send a reply
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringString = Types.newParameterizedType(Map.class, String.class, String.class);
    JsonAdapter<Map<String, String>> adapter = moshi.adapter(mapStringString);
    Map<String, String> responseMap = new HashMap<>();

    if (filepath == null) {
      responseMap.put("type", "error");
      responseMap.put("error_type", "missing_parameter");
      responseMap.put("error_arg", "filepath");
      return adapter.toJson(responseMap);
    }

    // Generate the reply
    responseMap.put("type", "success");

    responseMap.put("filepath", filepath);

    return adapter.toJson(responseMap);
  }
}
