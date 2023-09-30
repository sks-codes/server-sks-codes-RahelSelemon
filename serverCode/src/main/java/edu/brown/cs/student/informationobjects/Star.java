package edu.brown.cs.student.informationobjects;

/** Star Class represents objects in the star data set */
public class Star {
  int StarID;
  String ProperName;
  double X;
  double Y;
  double Z;

  /**
   * Star constructor with types from star data header
   *
   * @param StarID
   * @param ProperName
   * @param X
   * @param Y
   * @param Z
   */
  public Star(Integer StarID, String ProperName, Double X, Double Y, Double Z) {
    this.StarID = StarID;
    this.ProperName = ProperName;
    this.X = X;
    this.Y = Y;
    this.Z = Z;
  }

  /**
   * @return StarID of Star object
   */
  public int getStarID() {
    return StarID;
  }

  /**
   * @return name of Star object
   */
  public String getProperName() {
    return ProperName;
  }

  /**
   * @return X value of Star object
   */
  public double getX() {
    return X;
  }

  /**
   * @return Y value of Star object
   */
  public double getY() {
    return Y;
  }

  /**
   * @return Z value of Star object
   */
  public double getZ() {
    return Z;
  }

  /** prints Star object */
  public void print_star() {
    System.out.print(
        this.StarID + " " + this.ProperName + " " + this.X + " " + this.Y + " " + this.Z);
  }
}
