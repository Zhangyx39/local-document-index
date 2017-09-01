package hw2;

import opennlp.tools.stemmer.PorterStemmer;

import java.util.Scanner;

/**
 * Created by Yixing Zhang on 6/9/17.
 */
public class StemmedTokenizer extends Tokenizer {

  PorterStemmer stemmer;


  public StemmedTokenizer() {
    super();
    stemmer = new PorterStemmer();
  }

  @Override
  public Token nextToken() {
    if (sc.hasNext()) {
      position++;
      return new Token(stemmer.stem(sc.next()), position);
    }
    sc.close();
    return null;
  }

  public static void main(String[] args) {
    String str = "The celluloid torch has been passed to a new\n" +
            "generation: filmmakers who grew up in the 1960s.\n" +
            "   ``Platoon,'' ``Running on Empty,'' ``1969'' and ``Mississippi\n" +
            "Burning'' are among the movies released in the past two years from\n" +
            "writers and directors who brought their own experiences of that\n" +
            "turbulent decade to the screen.\n";

    StemmedTokenizer t = new StemmedTokenizer();
    t.setText(str);
    Token token;
    System.out.println(t.stemmer.stem("detat"));
  }
}
