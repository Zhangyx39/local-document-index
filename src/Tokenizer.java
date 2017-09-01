package hw2;

import java.util.Scanner;

/**
 * Created by Yixing Zhang on 5/31/17.
 */
public abstract class Tokenizer {

  int position;
  Scanner sc;


  public Tokenizer() {
  }

  public void setText(String text) {
    sc = new Scanner(analyze(text));
    position = 0;
  }

  public String analyze(String text) {
    return (text + "\n")
            .replaceAll("[.]+[^A-Za-z0-9]", " ")
            //.replaceAll("[',]", "")
            .replaceAll("[^A-Za-z0-9. ]", " ")
            .toLowerCase()
            .trim();
  }

  abstract Token nextToken();
}
