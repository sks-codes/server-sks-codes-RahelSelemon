//package edu.brown.cs.student.test.HandlerTests;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.server.APIDataSources.ACS_API;
import edu.brown.cs.student.server.APIDataSources.APIDatasourceException;
import edu.brown.cs.student.server.handlers.BroadbandHandler;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import spark.Spark;
public class IntegrationTests {
  public static void setupOnce() {
    // Pick an arbitrary free port
    Spark.port(0);
    // Eliminate logger spam in console for test suite
    Logger.getLogger("").setLevel(Level.WARNING); //empty name = root
  }
    // Helping Moshi serialize Json responses
  private final Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String, Object>> adapter;    public void setup() throws APIDatasourceException {
    ACS_API mockedSource = new MockData();
    Spark.get("broadband", new BroadbandHandler(mockedSource));
    Spark.awaitInitialization(); // don't continue until the server is listening
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);
  }
  public void tearDown() {
    Spark.unmap("broadband");
    Spark.awaitStop();
  }
  private HttpURLConnection tryRequest(String apiCall) throws IOException {
    URL requestURL = new URL("http://localhost:"+ Spark.port()+"/"+apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    clientConnection.setRequestProperty("Content-Type", "application/json");
    clientConnection.setRequestProperty("Accept", "application/json");
    clientConnection.connect();
    return clientConnection;
  }
  public void testRequestSuccessMock() throws IOException {
    HttpURLConnection loadConnection = tryRequest("https://http://localhost:2323/broadband?state=California&county=Orange_County");
    assertEquals(200, loadConnection.getResponseCode());
    Map<String, Object> body = adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    showDetailsIfError(body);
    assertEquals("success", body.get("type"));
    List<String> list = (List<String>) body.get("Bandwidth");
    List<String> expected = new ArrayList<>();
    expected.add("Bandwidth:"+93.0);
    expected.add("Date:" + LocalDate.now());
    expected.add("Time:" + LocalTime.now());
    expected.add("State: California");
    expected.add("County: Orange County");
    assertEquals(list.get(0), expected.get(0));
    loadConnection.disconnect();
  }
  private void showDetailsIfError(Map<String, Object> body) {
    if(body.containsKey("type") && "error".equals(body.get("type"))) {
      System.out.println(body.toString());
    }
  }
}
