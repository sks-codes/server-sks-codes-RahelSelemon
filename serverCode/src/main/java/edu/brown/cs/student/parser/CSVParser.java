package edu.brown.cs.student.parser;

import edu.brown.cs.student.informationobjects.CreatorFromRow;
import edu.brown.cs.student.informationobjects.FactoryFailureException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * CSVParser parses an input CSV into a list of a given type.
 *
 * @param <T>
 */
public class CSVParser<T> {
  static final Pattern regexSplitCSVRow =
      Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");
  BufferedReader reader;
  CreatorFromRow<T> gen_row;
  boolean header;
  List<List<String>> parsed_strings = new ArrayList<List<String>>();

  List<T> parsed_rows;

  /**
   * constructor that takes in a filename string, boolean indicating whether there is a header in
   * the CSV, and a CreatorFromRow object in order to parse the CSV into an indicated Object type.
   *
   * @param fn
   * @param header
   * @param gen_row
   */
  public CSVParser(String fn, Boolean header, CreatorFromRow<T> gen_row) {
    FileReader fr = null;
    try {
      fr = new FileReader(fn);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
    this.reader = new BufferedReader(fr);
    this.gen_row = gen_row;
    this.header = header;
  }

  /**
   * constructor that takes in a reader object, boolean indicating whether there is a header in the
   * CSV, and a CreatorFromRow object in order to parse the CSV into an indicated Object type.
   *
   * @param f
   * @param header
   * @param gen_row
   */
  public CSVParser(Reader f, Boolean header, CreatorFromRow<T> gen_row) {
    this.reader = new BufferedReader(f);
    this.gen_row = gen_row;
    this.header = header;
  }

  /**
   * @return boolean indicating whether the CSV contains a header
   */
  public boolean has_header() {
    return header;
  }

  /**
   * @return list of rows of the CSV parsed into strings
   */
  public List<List<String>> get_parsed_strings() {
    return parsed_strings;
  }

  /**
   * function parses a CSV into a list of object of type T
   *
   * @return the parsed CSV
   */
  public List<T> parse() {
    if (parsed_rows != null){
      return parsed_rows;
    }
    parsed_rows = new ArrayList<T>();
    try {
      String curr_line = this.reader.readLine(); // creating line reading stream
      if (header) {
        parsed_strings.add(
            List.of(regexSplitCSVRow.split(curr_line))); // storing header in parsed_strings CSV
        curr_line = this.reader.readLine(); // skipping header while parsing
      }
      while (curr_line != null) {
        try {
          List<String> l =
              List.of(regexSplitCSVRow.split(curr_line)); // list of parsed strings in current row
          parsed_strings.add(l);
          parsed_rows.add(this.gen_row.create(l)); // converting list of strings to object of type T and adding to output list
        } catch (FactoryFailureException err) {
          System.out.println("FactoryFailureException in converting String to type T!");
        }
        curr_line = this.reader.readLine();
      }
    } catch (IOException err) {
      System.out.println("IOException in streaming CSV rows!");
    }
    try {
      reader.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return parsed_rows;
  }
}

