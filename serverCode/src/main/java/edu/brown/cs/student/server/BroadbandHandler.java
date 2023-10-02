package edu.brown.cs.student.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.searcher.*;
import edu.brown.cs.student.server.APIDataSources.ACS_API;
import edu.brown.cs.student.server.APIDataSources.APIDatasourceException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import spark.Request;
import spark.Response;
import spark.Route;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import spark.Spark;


/**
 * Handler class for the soup ordering API endpoint.
 *
 * This endpoint is similar to the endpoint(s) you'll need to create for Sprint 2. It takes a basic GET request with
 * no Json body, and returns a Json object in reply. The responses are more complex, but this should serve as a reference.
 *
 */
public class BroadbandHandler implements Route {
    private final ACS_API dataSource;
    public BroadbandHandler(ACS_API dataSource) {
      this.dataSource = dataSource;
    }
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

    String state = request.queryParams("state");
    String county = request.queryParams("county");

    if(state == null|| county == null) {
      responseMap.put("type", "error");
      responseMap.put("error_type", "missing_parameter");
      if (state == null)
        responseMap.put("error_arg", "state");
      else
        responseMap.put("error_arg", "county");
      return adapter1.toJson(responseMap);
    }
    state = state.replaceAll("_", " ");
    county = county.replaceAll("_", " ");

    try {
      List<String> bandwidthData = dataSource.getData(state,county);
      responseMap.put("type", "success");
      responseMap.put("Bandwidth", bandwidthData);
      return adapter1.toJson(responseMap);
    }
    catch (APIDatasourceException e) {
      responseMap.put("type", "error");
      responseMap.put("error_type", "datasource");
      responseMap.put("details", e.getMessage());
      return adapter1.toJson(responseMap);
    }
  }
}