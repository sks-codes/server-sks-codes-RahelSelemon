package edu.brown.cs.student.server.APIDataSources;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import okio.Buffer;

/**
 * ACS_API loads data from the ACS data set to specified state and county
 */
public class ACS_API {
  private List<List<String>> stateCodes; //stores the state to code mapping table

  /**
   * constructor that initializes the state to code table by calling the readInStateCode function
   * @throws APIDatasourceException if read fails
   */
  public ACS_API() throws APIDatasourceException {
    readInStateCodes();
  }

  /**
   * loads state to code table into public variable
   * @throws APIDatasourceException if the states to codes table is not properly read
   */
  private void readInStateCodes() throws APIDatasourceException {
    try {
      URL requestURL = new URL("https", "api.census.gov", "/data/2010/dec/sf1?get=NAME&for=state:*");
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

  /**
   * returns HttpURLConnection for specified url.
   * @param requestURL specified URL to connect
   * @return HttpURLConnection for requestURL
   * @throws APIDatasourceException if there is a failure to connect to a url
   * @throws IOException
   */
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

  /**
   * Converts state name into state code as specified by the ACS state to code table
   * @param state String representing state name
   * @return string representing state code
   * @throws APIDatasourceException if there is no matching state in the state to code table
   */
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
      throw new APIDatasourceException("State not found."); //state not found in table
    }
  }

  /**
   * Converts county name to county code from the ACS data set
   * @param countyList List of county data
   * @param county target county name
   * @return code corresponding to target county
   * @throws APIDatasourceException if there is no code corresponding to the county
   */
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
      throw new APIDatasourceException("County not found!"); //county not found
    }
  }

  /**
   * reads ACS table filtered by desired state
   * @param state desired state code
   * @return returns list of counties in the requested state
   * @throws APIDatasourceException if no counties are found for a given state or if there is a read exception
   */
  private List<List<String>> readInCounties(String state) throws APIDatasourceException {
    try {
      URL requestURL = new URL("https", "api.census.gov",
          "/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:*&in=state:" + state);
      HttpURLConnection clientConnection = connect(requestURL);
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<List> adapter = moshi.adapter(List.class).nonNull();
      List<List<String>> counties = adapter.fromJson(
          new Buffer().readFrom(clientConnection.getInputStream()));
      clientConnection.disconnect();
      if (counties.isEmpty()) { //no counties found for given state
        throw new APIDatasourceException("Malformed response from ACS.");
      }
      return counties;
    } catch (IOException e) {
      throw new APIDatasourceException(e.getMessage());
    }
  }

  /**
   * loads data from a given url
   * @param requestURL
   * @return a list of list of strings representing the data at the given URL
   * @throws APIDatasourceException if there is a read write exception from streaming the URL content
   * @throws IOException
   */
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

  /**
   * Loads broadband data from ACS API for given state and country
   * @param state given state name
   * @param county given county code
   * @returns a list of Strings representing the response
   * @throws APIDatasourceException if there is a data loading issue
   */
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
      result.add("Data:" + data);

      return new ArrayList<>(result);
    } catch (IOException e) {
      throw new APIDatasourceException(e.getMessage());
    }
  }
}


