package edu.brown.cs.student.main;

import java.util.List;
import edu.brown.cs.student.informationobjects.*;
import edu.brown.cs.student.parser.*;
import edu.brown.cs.student.searcher.*;

/** The Main class of our project. This is where execution begins. */
public final class Main {
  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private Main(String[] args) {}

  private void run() {
    // Making Star CSV parser
    StarCreator s = new StarCreator();
    CSVParser<Star> p1 =
        new CSVParser<Star>(
            "/Users/siddarthsitaraman/Documents/CSCI0320/csv-sks-codes/data/stars/stardata.csv",
            true,
            s);
    List<Star> par_rows = p1.parse();
    par_rows.get(4).print_star();

    CSVParser<Star> p2 =
        new CSVParser<Star>(
            "/Users/siddarthsitaraman/Documents/CSCI0320/csv-sks-codes/data/stars/test_stardata1.csv",
            true,
            s);
    SearchCSV searcher = new SearchCSV(p2, "52.95794", 2);
    List<Location> results = searcher.search();
    System.out.println("\n Number of matches: " + results.size());
    System.out.println(results.get(0).getRow_ind() + ", " + results.get(0).getCol_ind());
    System.out.println(results.get(1).getRow_ind() + ", " + results.get(1).getCol_ind());
  }
}
