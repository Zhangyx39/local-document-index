package hw2;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;

/**
 * Created by Yixing Zhang on 6/2/17.
 */
public class Indexer {
  TermList termList;
  Tokenizer tokenizer;
  StringBuilder sb;
  int docID;

  public Indexer(TermList termList, Tokenizer tokenizer) {
    this.termList = termList;
    this.tokenizer = tokenizer;
    this.sb = new StringBuilder();
    this.docID = 1;
  }

  int indexDoc(int docID, String text) {
    tokenizer.setText(text);
    termList.setDocID(docID);
    Token t;
    int count = 0;
    while ((t = tokenizer.nextToken()) != null) {
      termList.addTerm(t.getTerm(), t.getPosition());
      count++;
    }
    return count;
  }

  int readFile(File file) {
    int count = 0;
    try {
      Document doc = Jsoup.parse(file, "UTF-8");
      for (Element e : doc.getElementsByTag("doc")) {
        String docno = e.getElementsByTag("docno").text();
        String text = e.getElementsByTag("text").text();
        //System.out.println("Indexing doc: " + docno);
        int len = indexDoc(docID, text);
        sb.append(docno + " " + docID + " " + len + "\n");
        if (docID % 1000 == 0) {
          System.out.println("Finish reading " + docID + " docs.");

          termList.finish();
          termList.outputFile
                  ("/Users/tommy/Dropbox/CS6200_Yixing_Zhang/homework2",
          docID / 1000 + "");
          termList.reset();
        }
        docID++;
        count++;
      }
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
    return count;
  }

  void readAll(String path) {
    int countf = 0;
    int countd = 0;
    try {
      File[] files = new File(path).listFiles();
      System.out.println("Total number of files: " + files.length);
      for (int i = 1; i <= files.length; i++) {
        if (!files[i - 1].getName().substring(0, 2).equals("ap")) {
          continue;
        }
        //System.out.println("********************************************");
        System.out.println("Reading file: " + files[i - 1].getName());
        countd += readFile(files[i - 1]);
        countf++;
//        if (i % 5 == 0 || i == files.length) {
//          System.out.println("Finish reading " + i + " files.");
//          termList.finish();
//          termList.outputFile
//                  ("/Users/tommy/Dropbox/CS6200_Yixing_Zhang/homework2",
//                          i / 5 + "");
//          termList.reset();
//        }
      }
      System.out.println("Finish reading " + docID + " docs.");

      termList.finish();
      termList.outputFile
              ("/Users/tommy/Dropbox/CS6200_Yixing_Zhang/homework2",
                      docID / 1000 + 1 + "");
      termList.reset();
      outputDocInfo();
    } catch (Exception e) {
      System.out.println(e);
    }
    System.out.println("Total number of files read: " + countf);
    System.out.println("Total number of docs read: " + countd);
  }

  void finish() {
    System.out.println("Reading process finished.");
    termList.finish();
    termList.reset();
  }

  public void outputDocInfo() {
    try {
      FileUtils.writeStringToFile(new File(
              "/Users/tommy/Dropbox/CS6200_Yixing_Zhang/homework2/docInfo" +
                      ".txt"), sb.toString().trim(), "UTF-8");
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
    }

  }


  public static void main(String[] args) {
    Tokenizer tk1 = new BasicTokenizer();
    Tokenizer tk2 = new NoSWTokenizer();
    Tokenizer tk3 = new StemmedTokenizer();
    Tokenizer tk4 = new StemmedNoSWTokenizer();



    Catalogue c = new Catalogue();
    TermList tl = new TermList(c);
    Indexer indexer = new Indexer(tl, tk4);
    indexer.readAll("/Users/tommy/CS/CS6200/AP_DATA/ap89_collection");
  }
}
