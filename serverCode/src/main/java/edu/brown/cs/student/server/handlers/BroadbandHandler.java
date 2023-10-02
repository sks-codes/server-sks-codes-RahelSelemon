package edu.brown.cs.student.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.server.APIDataSources.ACS_API;
import edu.brown.cs.student.server.APIDataSources.APIDatasourceException;
import java.util.List;
import spark.Request;
import spark.Response;
import spark.Route;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


/**
 * handler for broadband endpoint
 */
public class BroadbandHandler implements Route {
    private final ACS_API dataSource;
    public BroadbandHandler(ACS_API dataSource) {
      this.dataSource = dataSource;
    }
  /**
   * shows broadband access data for specified state and county, obtained from the ACS API data
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

    String state = request.queryParams("state");
    String county = request.queryParams("county");

    if(state == null|| county == null) { //if missing required state or county param
      responseMap.put("type", "error");
      responseMap.put("error_type", "missing_parameter");
      if (state == null)
        responseMap.put("error_arg", "state");
      else
        responseMap.put("error_arg", "county");
      return adapter1.toJson(responseMap);
    }

    //converting param from link to real state or county name by removing underscores
    state = state.replaceAll("_", " ");
    county = county.replaceAll("_", " ");

    try {
      List<String> broadbandData = dataSource.getData(state,county);
      responseMap.put("type", "success");
      responseMap.put("Bandwidth", broadbandData);
      return adapter1.toJson(responseMap);
    }
    catch (APIDatasourceException e) { //error with retrieving desired ACS data
      responseMap.put("type", "error");
      responseMap.put("error_type", "datasource");
      responseMap.put("details", e.getMessage());
      return adapter1.toJson(responseMap);
    }
  }
}