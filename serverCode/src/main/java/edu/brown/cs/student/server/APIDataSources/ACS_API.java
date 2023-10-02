package edu.brown.cs.student.server.APIDataSources;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import okio.Buffer;

public class ACS_API {

  private List<List<String>> stateCodes;

  public ACS_API() throws APIDatasourceException {
    readInStateCodes();
  }

  private void readInStateCodes() throws APIDatasourceException {
    try {
      URL requestURL = new URL("https", "api.census.gov", "data/2010/dec/sf1?get=NAME&for=state:*");
      HttpURLConnection clientConnection = connect(requestURL);
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<List> adapter = moshi.adapter(List.class).nonNull();
      this.stateCodes = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      clientConnection.disconnect();
      if (this.stateCodes.isEmpty()) {
        throw new APIDatasourceException("Malformed response from ACS.");
      }
    } catch (IOException e) {
      throw new APIDatasourceException(e.getMessage());
    }
  }

  private static HttpURLConnection connect(URL requestURL) throws APIDatasourceException, IOException {
    URLConnection urlConnection = requestURL.openConnection();
    if (!(urlConnection instanceof HttpURLConnection))
      throw new APIDatasourceException("unexpected: result of connection wasn't HTTP");
    HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
    clientConnection.connect(); // GET
    if (clientConnection.getResponseCode() != 200)
      throw new APIDatasourceException(
          "unexpected: API connection not success status " + clientConnection.getResponseMessage());
    return clientConnection;
  }

  private String stateToCode(String state) throws APIDatasourceException {
    String code = "";
    for (List<String> str : this.stateCodes) {
      if (str.get(0).equals(state)) {
        code = str.get(1);
      }
    }
    if (!code.isEmpty()) {
      return code;
    } else {
      throw new APIDatasourceException("State not found.");
    }
  }

  private String countyToCode(List<List<String>> countyList, String county) throws APIDatasourceException {
    String code = "";
    for (List<String> str : countyList) {
      List<String> countyState = Arrays.asList(str.get(0).split(","));
      if (countyState.get(0).equals(county)) {
        code = str.get(3);
      }
    }
    if (!code.isEmpty()) {
      return code;
    } else {
      throw new APIDatasourceException("County not found.");
    }
  }

  private List<List<String>> readInCounties(String state) throws APIDatasourceException {
    try {
      URL requestURL = new URL("https", "api.census.gov",
          "/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:*&in=state:*");
      HttpURLConnection clientConnection = connect(requestURL);
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<List> adapter = moshi.adapter(List.class).nonNull();
      List<List<String>> counties = adapter.fromJson(
          new Buffer().readFrom(clientConnection.getInputStream()));
      clientConnection.disconnect();
      if (counties.isEmpty()) {
        throw new APIDatasourceException("Malformed response from ACS.");
      }
      return counties;
    } catch (IOException e) {
      throw new APIDatasourceException(e.getMessage());
    }
  }

  public List<List<String>> retrieveDataFromURL(URL requestURL) throws APIDatasourceException, IOException {
    List<List<String>> data = new ArrayList<>();
    try {
      String url = requestURL.toString();
      HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
      connection.setRequestMethod("GET");
      BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      StringBuilder jsonResponse = new StringBuilder();
      String line = "";
      while ((line = reader.readLine()) != null) {
        jsonResponse.append(line);
      }
      reader.close();

      Gson gson = new Gson();
      TypeToken<List<List<String>>> token = new TypeToken<List<List<String>>>() {};
      data = gson.fromJson(jsonResponse.toString(), token.getType());


    } catch (IOException e) {
      throw new APIDatasourceException(e.getMessage());
    }
    return data;
  }

  public List<String> getData(String state, String county) throws APIDatasourceException {
    try {
      LocalTime time = LocalTime.now();
      LocalDate date = LocalDate.now();
      String stateCode = stateToCode(state);
      String countyCode = countyToCode(this.readInCounties(stateCode), county);
      URL requestURL = new URL("https", "api.census.gov",
          "/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:" + countyCode + "&in=state:" + stateCode);
      List<List<String>> data = retrieveDataFromURL(requestURL);

      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<List> adapter = moshi.adapter(List.class).nonNull();
      List<String> result = new ArrayList<>();
      result.add("Bandwidth:" + data.get(1).get(1));
      result.add("Date:" + date);
      result.add("Time:" + time);
      result.add("State:" + state);
      result.add("County:" + county);
      result.add("Data:" + adapter.toJson(data));

      return new ArrayList<>(result);
    } catch (IOException e) {
      throw new APIDatasourceException(e.getMessage());
    }
  }
}


