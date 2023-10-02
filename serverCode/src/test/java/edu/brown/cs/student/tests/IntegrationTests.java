package edu.brown.cs.student.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.server.APIDataSources.ACS_API;
import edu.brown.cs.student.server.APIDataSources.APIDatasourceException;
import edu.brown.cs.student.server.handlers.BroadbandHandler;
import edu.brown.cs.student.server.handlers.LoadCSVHandler;
import edu.brown.cs.student.server.handlers.SearchCSVHandler;
import edu.brown.cs.student.server.handlers.ViewCSVHandler;
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
        Logger.getLogger("").setLevel(Level.WARNING); // empty name = root
    }

    // Helping Moshi serialize Json responses
    private final Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    private JsonAdapter<Map<String, Object>> adapter;

    public void setup() throws APIDatasourceException {
        // Re-initialize parser, state, etc. for every test method
        // Use *MOCKED* data when in this test environment.
        // Notice that the WeatherHandler code doesn't need to care whether it has
        // "real" data or "fake" data. Good separation of concerns enables better testing.
        ACS_API mockedSource = new MockData();

        Spark.get("broadband", new BroadbandHandler(mockedSource));

        Spark.awaitInitialization(); // don't continue until the server is listening

        // New Moshi adapter for responses (and requests, too; see a few lines below)
        //For more on this, see the Server gearup.
        Moshi moshi = new Moshi.Builder().build();
        adapter = moshi.adapter(mapStringObject);
    }

    public void tearDown() {
        Spark.unmap("broadband");
        Spark.awaitStop();
    }

    /**
    * Helper to start a connection to a specific API endpoint/params
    * @param apiCall the call string, including endpoint
    *(Note: this would be better if it had more structure!)
    * @return the connection for the given URL, just after connecting
    * @throws IOException if the connection fails for some reason
     */
    private HttpURLConnection tryRequest(String apiCall) throws IOException {
        URL requestURL = new URL("http://localhost:"+ Spark.port()+"/"+apiCall);
        HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
        clientConnection.setRequestProperty("Content-Type", "application/json");
        clientConnection.setRequestProperty("Accept", "application/json");

        clientConnection.connect();
        return clientConnection;
    }
    public void testRequestSuccessMock() throws IOException {
        /////////// LOAD DATASOURCE ///////////
        // Set up the request, make the request
        HttpURLConnection loadConnection = tryRequest("https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:Orange%20County&in=state:California");
        // Get an OK response (the *connection* worked, the *API* provides an error response)
        assertEquals(200, loadConnection.getResponseCode());
        // Get the expected response: a success
        Map<String, Object> body = adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
        showDetailsIfError(body);
        assertEquals("success", body.get("type"));

        // Mocked data: correct temp? We know what it is, because we mocked.
        List<String> list = (List<String>) body.get("Bandwidth");
        List<String> expected = new ArrayList<>();

        //expected.add("Bandwidth:" + );
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