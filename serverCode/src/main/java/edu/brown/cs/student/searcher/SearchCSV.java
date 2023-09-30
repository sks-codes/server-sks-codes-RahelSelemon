package edu.brown.cs.student.searcher;

import java.util.ArrayList;
import java.util.List;
import edu.brown.cs.student.parser.CSVParser;
import edu.brown.cs.student.parser.CSVParser;

/**
 * SearchCSV class takes in CSVParser and item and has functionality for searching for the given
 * item in the CSV
 */
public class SearchCSV {
  CSVParser parser;
  boolean header;
  String search_val;
  String col_name;
  Integer col_number;
  List<List<String>> parsed_strings;

  /**
   * Constructor for SearchCSV that only takes search value
   *
   * @param parser
   * @param search_val
   */
  public SearchCSV(CSVParser parser, String search_val) {
    this.parser = parser;
    this.header = parser.has_header();
    this.search_val = search_val;
  }

  /**
   * Constructor for SearchCSV that takes search value and search column name
   *
   * @param parser
   * @param search_val
   * @param col
   */
  public SearchCSV(CSVParser parser, String search_val, String col) {
    this.parser = parser;
    this.header = parser.has_header();
    this.search_val = search_val;
    this.col_name = col;
  }

  /**
   * Constructor for SearchCSV that takes search value and search column index
   *
   * @param parser
   * @param search_val
   * @param col
   */
  public SearchCSV(CSVParser parser, String search_val, int col) {
    this.parser = parser;
    this.header = parser.has_header();
    this.search_val = search_val;
    this.col_number = col;
  }

  /**
   * Searches CSV for given item
   *
   * @return a list of Location objects indicating locations of matching items in CSV
   */
  public List<Location> search() {
    parser.parse();
    parsed_strings = parser.get_parsed_strings();
    List<Location> return_locs = new ArrayList<Location>();
    // Searching specific column by name
    if (col_name != null && header) {
      int col_ind = parsed_strings.indexOf(col_name);
      for (int i = 1; i < parsed_strings.size(); ++i) {
        if (parsed_strings.get(i).get(col_ind) == this.search_val) {
          return_locs.add(new Location(i - 1, col_ind));
        }
      }
    } else if (col_number != null) { // searching specific column by column index
      if (header) {
        for (int i = 1; i < parsed_strings.size(); ++i) {
          if (parsed_strings.get(i).get(col_number).equals(this.search_val)) {
            return_locs.add(new Location(i - 1, col_number));
          }
        }
      } else {
        for (int i = 0; i < parsed_strings.size(); ++i) {
          if (parsed_strings.get(i).get(col_number).equals(this.search_val)) {
            return_locs.add(new Location(i, col_number));
          }
        }
      }
    } else { // searching all columns for matches
      if (header) {
        for (int i = 1; i < parsed_strings.size(); ++i) {
          for (int j = 0; j < parsed_strings.get(0).size(); ++j) {
            if (parsed_strings.get(i).get(j).equals(this.search_val)) {
              return_locs.add(new Location(i - 1, j));
            }
          }
        }
      } else {
        for (int i = 0; i < parsed_strings.size(); ++i) {
          for (int j = 0; j < parsed_strings.get(0).size(); ++j) {
            if (parsed_strings.get(i).get(j).equals(this.search_val)) {
              return_locs.add(new Location(i, j));
            }
          }
        }
      }
    }
    return return_locs;
  }
}