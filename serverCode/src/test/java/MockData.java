import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.server.APIDataSources.ACS_API;
import edu.brown.cs.student.server.APIDataSources.APIDatasourceException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MockData extends ACS_API {
  public MockData() throws APIDatasourceException {
    super();
  }
  public List<String> getData() throws APIDatasourceException {
    try {
      LocalTime time = LocalTime.now();
      LocalDate date = LocalDate.now();
      String state = "California";
      String county = "Orange County";
      URL requestURL = new URL("https","api.census.gov","/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:Orange%20County&in=state:California");
      List<List<String>> data = this.retrieveDataFromURL(requestURL);

      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<List> adapter = moshi.adapter(List.class).nonNull();
      List<String> result = new ArrayList<>();
      result.add("Bandwidth:" + data.get(1).get(1));
      result.add("Date:" + date);
      result.add("Time:" + time);
      result.add("State: California");
      result.add("County: Orange County");
      result.add("Data:" + data);
      return new ArrayList<>(result);
    } catch (Exception e) {
      throw new APIDatasourceException(e.getMessage());
    }
  }
}