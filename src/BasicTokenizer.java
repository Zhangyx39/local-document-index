package hw2;

import java.util.Scanner;

/**
 * Created by Yixing Zhang on 6/2/17.
 */
public class BasicTokenizer extends Tokenizer {

  public BasicTokenizer() {
    super();
  }

  @Override
  public Token nextToken() {
    if (sc.hasNext()) {
      position++;
      return new Token(sc.next(), position);
    }
    sc.close();
    return null;
  }

  public static void main(String[] args) {
    String str =
            "13.''take" + " ... " + " U.S. " + " 12.32 " + " d'etat " + " 'a "+
            " 123,456 " + " abc, cde " +
            "   _ Regulations implementing a law passed by Congress last summer\n" +
            "forbidding landlords from discriminating against the handicapped or\n" +
            "families with children take effect March 12. Violators can be fined\n" +
            "up to $10,000 for a first offense, $25,000 for a second violation in\n" +
            "a five-year period and $50,000 for two or more violations within a\n" +
            "   ``Platoon,'' ``Running on Empty,'' ``1969'and ``Mississippi\n" +
            "Burning'' are among the movies released in the past two years from\n" +
            "writers and directors who brought their own experiences of that\n" +
            "turbulent decade to the screen " +
            "seven-year period.";

    str = (str+"\n")
            .replaceAll("[',.]+[^A-Za-z0-9]", " ")
            .replaceAll("[',]", "")
            .replaceAll("[^A-Za-z0-9. ]", " ");
    System.out.println(str);
  }
}
