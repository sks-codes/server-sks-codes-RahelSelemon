package edu.brown.cs.student.informationobjects;

import java.util.List;

public class ListOfStringsCreator implements CreatorFromRow<List<String>> {
  @Override
  public List<String> create(List<String> row) throws FactoryFailureException {
    return row;
  }
}
