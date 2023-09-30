package edu.brown.cs.student.informationobjects;

import java.util.List;

/** StarCreator Implements CreatorFromRow for Star Class */
public class StarCreator implements CreatorFromRow<Star> {

  @Override
  /** implements create function for Star object */
  public Star create(List<String> row) throws FactoryFailureException {
    Integer starID;
    try{
      starID = Integer.parseInt(row.get(0));
    }
    catch(NumberFormatException e){
      starID = -1;
    }

    Double X;
    try{
      X = Double.parseDouble(row.get(2));
    }
    catch(NumberFormatException e){
      X = -1.0;
    }
    Double Y;
    try{
      Y = Double.parseDouble(row.get(3));
    }
    catch(NumberFormatException e){
      Y = -1.0;
    }
    Double Z;
    try{
      Z = Double.parseDouble(row.get(4));
    }
    catch(NumberFormatException e){
      Z = -1.0;
    }

    return new Star(starID, row.get(1), X, Y, Z);
  }
}
