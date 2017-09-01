package hw2;

import com.google.gson.JsonArray;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Yixing Zhang on 6/3/17.
 */
public class Merger {

  CatalogueReader c1;
  CatalogueReader c2;
  String path;
  String n1;
  String n2;
  RandomAccessFile tl1;
  RandomAccessFile tl2;
  RetrieverMY r1;
  RetrieverMY r2;

  public Merger(String path, String n1, String n2) throws Exception {
    this.c1 = new CatalogueReader(new File(path + "/Catalogue/" + n1));
    this.c2 = new CatalogueReader(new File(path + "/Catalogue/" + n2));
    this.path = path;
    this.n1 = n1;
    this.n2 = n2;
    this.tl1 = new RandomAccessFile(new File(path + "/TermList/" + n1), "r");
    this.tl2 = new RandomAccessFile(new File(path + "/TermList/" + n2), "r");
    this.r1 = new RetrieverMY(tl1, c1);
    this.r2 = new RetrieverMY(tl2, c2);
    System.out.println("Merging: " + n1 + ", " + n2);
  }

  public String getTerm(String term, RetrieverMY r) {
    return r.getTermInfo(term);
  }

  public void merge() throws Exception {

    int docNum1 = r1.getTotalDocNum();
    int docNum2 = r2.getTotalDocNum();
    int newDocNum = docNum1 + docNum2;

    int totalTokens1 = r1.getTotalTokens();
    int totalTokens2 = r2.getTotalTokens();
    int newTotalTokens = totalTokens1 + totalTokens2;

    Set<String> ks1 = c1.getKeySet();
    int termID = 1;
    int offset = 0;
    StringBuilder newCat = new StringBuilder();
    RandomAccessFile newTL = new RandomAccessFile(
            new File(path + "/TermList/m" + n1),"rw");

    String docN = newDocNum + "\n";
    int len = docN.length();
    newTL.seek(offset);
    newTL.write(docN.getBytes());
    newCat.append("0 _docNum " +  offset + " " + len + "\n");
    offset += len;


    String totT = newTotalTokens + "\n";
    len = totT.length();
    newTL.seek(offset);
    newTL.write(totT.getBytes());
    newCat.append("0 _totalTokens " +  offset + " " + len + "\n");
    offset += len;

    for (String s : ks1) {
      if (s.substring(0, 1).equals("_")) {
        continue;
      }
      String term1 = r1.getTermInfo(s);
      String term2 = r2.getTermInfo(s);
      String termMerged = mergeTerm(termID, term1, term2);
      //String head = "\"" + termID + "\": ";
      int length = termMerged.length();
      newCat.append(termID + " " + s + " " +  offset + " " + length + "\n");
      newTL.seek(offset);
      //newTL.write(head.getBytes());
      newTL.write(termMerged.getBytes());
      //offset += head.length();
      offset += length;
      termID++;
      c2.delete(s);
    }
    Set<String> ks2 = c2.getKeySet();
    for (String s : ks2) {
      if (s.substring(0, 1).equals("_")) {
        continue;
      }
      String term = r2.getTermInfo(s);
      term = mergeTerm(termID, term, null);
      //String head = "\"" + termID + "\": ";
      int length = term.length();
      newCat.append(termID + " " + s + " " +  offset + " " +
              length + "\n");
      newTL.seek(offset);
      //newTL.write(head.getBytes());
      newTL.write(term.getBytes());
      //offset += head.length();
      offset += length;
      termID++;
    }

    int vocSize = termID - 1;
    String voc = vocSize + "";
    len = voc.length();
    newTL.seek(offset);
    newTL.write(voc.getBytes());
    newCat.append("0 _vocabulary " +  offset + " " + len + "\n");
    offset += len;

    FileUtils.writeStringToFile(
            new File(path + "/Catalogue/m" + n1),
            newCat.toString().trim(), "UTF-8");

    this.delete();
  }

  private void delete() {
    try {
      Path path1 = FileSystems.getDefault().getPath(this.path + "/Catalogue/"
              + n1);
      Path path2 = FileSystems.getDefault().getPath(this.path + "/TermList/"
              + n1);
      Path path3 = FileSystems.getDefault().getPath(this.path + "/Catalogue/"
              + n2);
      Path path4 = FileSystems.getDefault().getPath(this.path + "/TermList/"
              + n2);
      Files.deleteIfExists(path1);
      Files.deleteIfExists(path2);
      Files.deleteIfExists(path3);
      Files.deleteIfExists(path4);
      System.out.println("Finish merging: " + n1 + ", " + n2);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String mergeTerm(int termID, String term1, String term2) {
    StringBuilder sb = new StringBuilder();
    if (term2 == null) {
      TermInfo t1 = stringToTermInfo(term1);
      t1.setTermID(termID);
      sb.append(t1.toString());
    } else {
      TermInfo t1 = stringToTermInfo(term1);
      TermInfo t2 = stringToTermInfo(term2);

      TermInfo newTerm = t1.merge(t2);
      newTerm.setTermID(termID);
      sb.append(newTerm.toString());
    }
    sb.append("\n");
    return sb.toString();
  }

  public TermInfo stringToTermInfo(String term) {
    String[] strings = term.split(";");
    int df = Integer.parseInt(strings[0].split(" ")[1]);
    int ttf = Integer.parseInt(strings[0].split(" ")[2]);
    List<DocInfo> docInfos = new ArrayList<>();
    JsonArray docs = new JsonArray();

    for (int i = 1; i < strings.length; i++) {
      String[] doc = strings[i].replaceAll("\n", "").split(" ");
      int docID = Integer.parseInt(doc[0]);
      int tf = Integer.parseInt(doc[1]);
      List<Integer> positions = new ArrayList<>();
      for (int j = 2; j < doc.length; j++) {
        positions.add(Integer.parseInt(doc[j]));
      }
      docInfos.add(new DocInfo(docID, tf, positions));
    }
    TermInfo newTerm = new TermInfo(df, ttf, docInfos);
    return newTerm;
  }

  class TermInfo {
    private int df;

    private int ttf;

    private List<DocInfo> docs;

    private int termID;

    public TermInfo(int df, int ttf, List<DocInfo> docs) {
      this.df = df;
      this.ttf = ttf;
      this.docs = docs;
      this.termID = 0;
    }

    public void setTermID(int termID) {
      this.termID = termID;
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(termID + " " + df + " " + ttf + ";");
      for (int i = 0; i < docs.size(); i++) {
        if (i != docs.size() - 1) {
          sb.append(docs.get(i).toString() + ";");
        } else {
          sb.append(docs.get(i).toString());
        }
      }
      //sb.append(";");
      return sb.toString();
    }

    TermInfo merge(TermInfo other) {
      int newDf = this.df + other.df;
      int newTtf = this.ttf + other.ttf;
      List<DocInfo> newDocs = new ArrayList<>();
      int p1 = 0;
      int p2 = 0;

      while (p1 < this.docs.size() || p2 < other.docs.size()) {
        int tf1 = p1 < this.docs.size()? this.docs.get(p1).tf : 0;
        int tf2 = p2 < other.docs.size()? other.docs.get(p2).tf : 0;
        if (tf1 >= tf2) {
          newDocs.add(this.docs.get(p1));
          p1++;
        } else {
          newDocs.add(other.docs.get(p2));
          p2++;
        }
      }
      return new TermInfo(newDf, newTtf, newDocs);
    }
  }

  class DocInfo {
    private int docID;

    private int tf;

    private List<Integer> positions;

    public DocInfo(int docID, int tf, List<Integer> positions) {
      this.docID = docID;
      this.tf = tf;
      this.positions = positions;
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder(docID + " " + tf);
      for (Integer position : positions) {
        sb.append(" " + position);
      }
      sb.append("");
      return sb.toString();
    }
  }


  public static void main(String[] args) throws Exception {
    Merger merger = new Merger
            ("/Users/tommy/Dropbox/CS6200_Yixing_Zhang/homework2",
                    "1.txt", "2.txt");
//    String term;
//    String t1 = merger.r1.getTermInfo("the");
//    String t2 = merger.r2.getTermInfo("the");
//    System.out.println(t1);
//    System.out.println(t2);
//    String merged = merger.mergeTerm(t1, t2);
      merger.merge();
//    Gson gson = new Gson();
////
//    String str2 = "3 4 ;168 2 15 468;122 1 291;111 1 227;\n";
//
//    String str1 = "3 4 ;168 2 15 468;\n";
//
//    String t = merger.mergeTerm(str1, str2);
//    TermInfo t1 = merger.stringToTermInfo(str1);
//    TermInfo t2 = merger.stringToTermInfo(str2);
//    TermInfo t3 = t1.merge(t2);
//    System.out.println(t);
//    System.out.println(t1);
//    System.out.println(t2);
//    System.out.println(t3);

  }
}
