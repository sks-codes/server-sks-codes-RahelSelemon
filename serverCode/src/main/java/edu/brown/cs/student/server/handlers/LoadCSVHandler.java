package edu.brown.cs.student.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.parser.CSVParser;
import edu.brown.cs.student.informationobjects.ListOfStringsCreator;
import edu.brown.cs.student.server.Server;
import java.io.FileNotFoundException;
import java.util.List;
import spark.Request;
import spark.Response;
import spark.Route;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


/**
 * Handler class for the loadcsv API endpoint.
 */
public class LoadCSVHandler implements Route {
  String filepath;
  String header;

  /**
   * loads a csv given a filepath and optionally a boolean indicating whether or not there
   * is a header on the csv.
   *
   * @param request  the request to handle
   * @param response use to modify properties of the response
   * @return response content
   * @throws Exception This is part of the interface; we don't have to throw anything.
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    filepath = request.queryParams("filepath");
    header = request.queryParams("header");

    Moshi moshi = new Moshi.Builder().build();
    Type mapStringString = Types.newParameterizedType(Map.class, String.class, String.class);
    JsonAdapter<Map<String, String>> adapter = moshi.adapter(mapStringString);
    Map<String, String> responseMap = new HashMap<>();

    //producing error response if no filepath is given
    if (filepath == null) {
      responseMap.put("result", "error");
      responseMap.put("error_type", "error_bad_request");
      responseMap.put("error_arg", "filepath");
      return adapter.toJson(responseMap);
    }
    boolean h = false;
    if (header != null){
      h = Boolean.parseBoolean(header);
    }
    CSVParser<List<String>> csvParser;
    try{
      csvParser = new CSVParser<List<String>>(filepath, h, new ListOfStringsCreator());
    }
    catch (FileNotFoundException e){ //If file doesn't exist
      responseMap.put("result", "error");
      responseMap.put("error_type", "error_datasource");
      responseMap.put("error_arg", "filepath");
      return adapter.toJson(responseMap);
    }

    Server.setCsvParser(csvParser);
    responseMap.put("result", "success");
    responseMap.put("filepath", filepath);
    return adapter.toJson(responseMap);
  }
}
