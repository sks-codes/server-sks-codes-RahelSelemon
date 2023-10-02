//package edu.brown.cs.student.tests;
//
//package edu.brown.cs.student.server;
//
//import com.squareup.moshi.JsonAdapter;
//import com.squareup.moshi.Moshi;
//import edu.brown.cs.student.server.APIDataSources.APIDatasourceException;
//import java.io.IOException;
//import java.net.URL;
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.ArrayList;
//import java.util.List;
//
//public class MockData {
//public MockData() {
//
//}
//public dfghj() {
//try {
//LocalTime time = LocalTime.now();
//LocalDate date = LocalDate.now();
//
//URL requestURL = new URL("https", "api.census.gov",
//          "/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:Orange%20County&in=state:California");
//List<List<String>> data = retrieveDataFromURL(requestURL);
//
//Moshi moshi = new Moshi.Builder().build();
//JsonAdapter<List> adapter = moshi.adapter(List.class).nonNull();
//List<String> result = new ArrayList<>();
//result.add("Bandwidth:" + data.get(1).get(1));
//result.add("Date:" + date);
//result.add("Time:" + time);
//result.add("State: California");
//result.add("County: Orange County");
//result.add("Data:" + data);
//
//return new ArrayList<>(result);
//} catch (IOException e) {
//throw new APIDatasourceException(e.getMessage());
//}
//}
//}