package edu.brown.cs.student.searcher;

public class Location {
  int row_ind;
  int col_ind;

  /**
   * Location class constructor that takes and sets a row and column number
   *
   * @param row
   * @param col
   */
  public Location(int row, int col) {
    row_ind = row;
    col_ind = col;
  }

  /**
   * @return row index of the location of an object in the CSV
   */
  public int getRow_ind() {
    return row_ind;
  }

  /**
   * @return column index of the location of an object in the CSV
   */
  public int getCol_ind() {
    return col_ind;
  }
}
