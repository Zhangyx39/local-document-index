package hw2;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Yixing Zhang on 5/31/17.
 */
public class TermList {
  private HashMap<String, TermInfo> data;
  private Catalogue catalogue;
  private int docID;
  private Gson gson;
  private int vocSize;
  private int docNum;
  private int totalTokens;
  private String output;

  public TermList(Catalogue catalogue) {
    this.catalogue = catalogue;
    this.data = new HashMap<>();
    this.docID = -1;
    this.vocSize = 0;
    this.docNum = 0;
    this.totalTokens = 0;
    this.output = "";
  }

  public void reset() {
    catalogue.reset();
    data.clear();
    docID = -1;
    vocSize = 0;
    docNum = 0;
    totalTokens = 0;
    output = "";
  }

  public void setDocID(int docID) {
    this.docID = docID;
    this.docNum++;
  }

  public void addTerm(String term, int position) {
    if (!data.containsKey(term)) {
      int termID = catalogue.addTerm(term);
      data.put(term, new TermInfo(termID));
    }

    TermInfo current = data.get(term);
    if (!current.docInfos.containsKey(docID)) {
      current.createDoc();
    }
    current.addPosition(position);
    totalTokens++;
  }

  public void finish() {
    vocSize = data.size();
    for (TermInfo termInfo : data.values()) {
      termInfo.sortByTF();
    }
    output = toMyFormat();
  }


  public String toMyFormat() {
    StringBuilder sb = new StringBuilder();
    String voc = vocSize + "\n";
    String docN = docNum + "\n";
    String totT = totalTokens + "\n";
    int offset = sb.length();
    int len = voc.length();
    sb.append(voc);
    catalogue.setTerm("_vocabulary", offset, len);

    offset = sb.length();
    len = docN.length();
    sb.append(docN);
    catalogue.setTerm("_docNum", offset, len);

    offset = sb.length();
    len = totT.length();
    sb.append(totT);
    catalogue.setTerm("_totalTokens", offset, len);
    for (String s : data.keySet()) {
      offset = sb.length();
      sb.append(data.get(s) + "\n");
      len = sb.length() - offset;
      catalogue.setTerm(s, offset, len);
    }
    return sb.toString();
  }

  public void outputFile(String path, String name) {
    try {
      System.out.println("Output term list: " + name);
      FileUtils.writeStringToFile(
              new File(path + "/TermList/" + name + ".txt"),
              output, "UTF-8");
      catalogue.outputFile(path, name);
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  class TermInfo {
    private int termID;

    private int df;

    private int ttf;

    private HashMap<Integer, DocInfo> docInfos;

    private List<DocInfo> docs;


    public TermInfo(int termID) {
      this.termID = termID;
      this.df = 0;
      this.ttf = 0;
      this.docInfos = new HashMap<>();
      this.docs = new ArrayList<>();
    }


    void addPosition(int position) {
      DocInfo doc = docInfos.get(docID);
      doc.tf++;
      doc.positions.add(position);
      ttf++;
    }

    void createDoc() {
      DocInfo docInfo = new DocInfo(docID);
      docInfos.put(docID, docInfo);
      df++;
    }

    void sortByTF() {
      for (DocInfo docInfo : docInfos.values()) {
        docs.add(docInfo);
      }
      Collections.sort(docs,
              Collections.reverseOrder(Comparator.comparingInt(DocInfo::getTf)));
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
  }

  class DocInfo {

    private int docID;

    private int tf;

    private List<Integer> positions;

    @Expose
    private List<Object> output;

    DocInfo(int docID) {
      this.docID = docID;
      this.tf = 0;
      this.positions = new ArrayList<>();
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

    public int getTf() {
      return tf;
    }
  }

  public static void main(String[] args) throws Exception {
    Catalogue c = new Catalogue();
    TermList tl = new TermList(c);
    tl.setDocID(1234);
    tl.addTerm("the", 1);
    tl.addTerm("car", 2);
    tl.addTerm("was", 3);
    tl.addTerm("in", 4);
    tl.addTerm("the", 5);
    tl.addTerm("car", 6);
    tl.addTerm("wash", 7);

    tl.setDocID(9527);
    tl.addTerm("the", 1);
    tl.addTerm("car", 2);
    tl.addTerm("was", 3);
    tl.addTerm("in", 4);

    tl.setDocID(6666);
    tl.addTerm("the", 1);
    tl.addTerm("the", 1);
    tl.addTerm("the", 1);
    tl.addTerm("the", 1);
    tl.addTerm("the", 1);
    tl.addTerm("the", 1);
    tl.addTerm("car", 2);
    tl.addTerm("was", 3);
    tl.addTerm("in", 4);
    tl.finish();
    System.out.println(c);
    System.out.println("***");
    System.out.println(tl.toMyFormat());

    JsonParser jsonParser = new JsonParser();


    String str = "3 9 ;6666 6 1 1 1 1 1 1;1234 2 1 5;9527 1 1;\n";
    String[] s = str.split(";");
    Scanner sc = new Scanner(str);
    System.out.println(sc.nextInt());
    System.out.println(sc.nextInt());
    System.out.println(Integer.parseInt("123"));

//    String str = "[3,9,{\"6666\":[6,[1,1,1,1,1,1]],\"1234\":[2,[1,5]],\"9527\":[1,[1]]}]";
//    JsonArray ja = jsonParser.parse(str).getAsJsonArray();
//    System.out.println(ja.get(0));
//    System.out.println(ja.get(1));
//    System.out.println(ja.get(2));
//    ArrayList<Object> arrayList = new ArrayList<>();
//    arrayList.add(1);
//    arrayList.add("Ada");
//    arrayList.add(new ArrayList<Object>());
//    System.out.println(arrayList.get(0));
//    System.out.println(arrayList.get(1));
//    System.out.println(arrayList.get(2));
//    Gson gson = new GsonBuilder()
//            .excludeFieldsWithoutExposeAnnotation()
//            .enableComplexMapKeySerialization()
//            .disableHtmlEscaping()
//            .create();
//    System.out.println(gson.toJson(arrayList));
    //gson.fromJson(str, TermInfo.class);
  }
}
