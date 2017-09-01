package hw2;

/**
 * Created by Yixing Zhang on 6/2/17.
 */
public class Token {
  private String term;
  private int position;

  public Token(String term, int position) {
    this.term = term;
    this.position = position;
  }

  public String getTerm() {
    return term;
  }

  public int getPosition() {
    return position;
  }
}
