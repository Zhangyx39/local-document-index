package hw2;

import opennlp.tools.stemmer.PorterStemmer;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Scanner;

import hw1.Retriever;

/**
 * Created by Yixing Zhang on 6/9/17.
 */
public class Output {
  File intput;
  String path = "/Users/tommy/CS/CS6200/merged/";

  public Output(File intput) {
    this.intput = intput;
  }

  void noStopNoStemming() throws Exception {
    StringBuilder sb = new StringBuilder();
    CatalogueReader c = new CatalogueReader(new File(path + "catalogue1.txt"));
    RandomAccessFile tl = new RandomAccessFile(path + "termlist1.txt", "r");
    Retriever retriever = new RetrieverMY(tl, c);
    Scanner sc = new Scanner(intput);
    while (sc.hasNext()) {
      String term = sc.next();
      String t = term.replaceAll("[^A-Za-z0-9. ]", "").toLowerCase();
      int df = retriever.getDF(t);
      int ttf = retriever.getTTF(t);
      sb.append(term + " " + df + " " + ttf + "\n");
    }
    FileUtils.writeStringToFile(new File("out.no.stop.no.stem.txt"),
            sb.toString().trim(), "UTF-8");
  }

  void stopStemming() throws Exception {
    PorterStemmer stemmer = new PorterStemmer();
    StringBuilder sb = new StringBuilder();
    CatalogueReader c = new CatalogueReader(new File(path + "catalogue4.txt"));
    RandomAccessFile tl = new RandomAccessFile(path + "termlist4.txt", "r");
    Retriever retriever = new RetrieverMY(tl, c);
    Scanner sc = new Scanner(intput);
    while (sc.hasNext()) {
      String term = sc.next();
      String t = term.replaceAll("[^A-Za-z0-9. ]", "").toLowerCase();
      t = stemmer.stem(t);
      int df = retriever.getDF(t);
      int ttf = retriever.getTTF(t);
      sb.append(term + " " + df + " " + ttf + "\n");
    }
    FileUtils.writeStringToFile(new File("out.stop.stem.txt"),
            sb.toString().trim(), "UTF-8");
  }


  public static void main(String[] args) throws Exception {
    Output out = new Output(new File("in.0.50.txt"));
    //out.noStopNoStemming();
    out.stopStemming();
  }
}
