package edu.brown.cs.student.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.server.Server;
import java.util.List;
import spark.Request;
import spark.Response;
import spark.Route;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler class for the viewcsv endpoint.
 */
public class ViewCSVHandler implements Route {
  /**
   * presents loaded csv on local server
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

    List<List<String>> parsedCSV = Server.getCSVParser().parse(); //getting loaded csv

    if (parsedCSV.isEmpty()){ //csv not properly loaded
      responseMap.put("result", "error");
      responseMap.put("error_type", "error_bad_request");
      responseMap.put("error_arg", "csv");
      return adapter1.toJson(responseMap);
    }

    responseMap.put("result", "success");
    responseMap.put("data", parsedCSV);

    return adapter1.toJson(responseMap);
  }
}