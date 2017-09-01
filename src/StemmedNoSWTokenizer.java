package hw2;

import opennlp.tools.stemmer.PorterStemmer;

import java.io.File;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Created by Yixing Zhang on 6/9/17.
 */
public class StemmedNoSWTokenizer extends Tokenizer {

  HashSet<String> stopWords;
  PorterStemmer stemmer;


  public StemmedNoSWTokenizer() {
    super();
    stemmer = new PorterStemmer();
    stopWords = new HashSet<>();
    try {
      Scanner sw = new Scanner(new File("stopwords.txt"));
      while (sw.hasNext()) {
        stopWords.add(sw.next());
      }
      sw.close();
    }
    catch (Exception e) {
      System.out.println(e);
    }

  }

  @Override
  public Token nextToken() {
    if (sc.hasNext()) {
      String term = sc.next();
      if (stopWords.contains(term)) {
        return nextToken();
      }
      position++;
      return new Token(stemmer.stem(term), position);
    }
    sc.close();
    return null;
  }

  public static void main(String[] args) {
//    String str = "The celluloid torch has been passed to a new\n" +
//            "generation: filmmakers who grew up in the 1960s.\n" +
//            "   ``Platoon,'' ``Running on Empty,'' ``1969'' and ``Mississippi\n" +
//            "Burning'' are among the movies released in the past two years from\n" +
//            "writers and directors who brought their own experiences of that\n" +
//            "turbulent decade to U.S. the screen.\n";
    StemmedNoSWTokenizer t = new StemmedNoSWTokenizer();
//    t.setText(str);
//    Token token;
//    while ((token = t.nextToken()) != null) {
//      System.out.println(token.getPosition() + " " + token.getTerm());
//    }
    System.out.println(t.stemmer.stem(""));
  }
}
