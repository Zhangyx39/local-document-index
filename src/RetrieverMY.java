package hw2;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.sun.tools.classfile.Annotation;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import hw1.Retriever;

/**
 * Created by Yixing Zhang on 6/2/17.
 */
public class RetrieverMY implements Retriever {
  private RandomAccessFile termList;
  private CatalogueReader catReader;
  private HashMap<Integer, String> docIDtoDocNum;
  private HashMap<String, Integer> docNumtoDocLen;

  public RetrieverMY(RandomAccessFile termList, CatalogueReader catReader) {
    this.termList = termList;
    this.catReader = catReader;
    this.docIDtoDocNum = new HashMap<>();
    this.docNumtoDocLen = new HashMap<>();
  }

  public void loadDocs(File f) {
    try {
      Scanner sc = new Scanner(f);
      while (sc.hasNextLine()) {
        String docNum = sc.next();
        int docID = sc.nextInt();
        int len = sc.nextInt();
        docIDtoDocNum.put(docID, docNum);
        docNumtoDocLen.put(docNum, len);
      }
      sc.close();
    }
    catch (Exception e) {
      System.out.println(e);
    }
  }

  public String getTermInfo(String term) {
    try {
      int offset = catReader.getOffset(term);
      int len = catReader.getLength(term);
      if (offset == -1) {
        return null;
      }
      termList.seek(offset);
      byte[] arr = new byte[len];
      termList.readFully(arr, 0, len);
      return new String(arr);
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return null;
  }

  public int getDF(String term) {
    try {
      String str = getTermInfo(term);
      Scanner sc = new Scanner(str);
      sc.next();
      int df = sc.nextInt();
      sc.close();
      return df;
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
      return 0;
    }
  }

  public int getTTF(String term) {
    try {
      String str = getTermInfo(term);
      Scanner sc = new Scanner(str);
      sc.next();
      sc.next();
      String[] strs = sc.next().split(";");

      int ttf = Integer.parseInt(strs[0]);
      sc.close();
      return ttf;
    }
    catch (Exception e) {
      return 0;
    }
  }

  public int getVocSize() {
    String term = "_vocabulary";
    try {
      return Integer.parseInt(getTermInfo(term).replaceAll("\n", ""));
    }
    catch (Exception e) {
      return 0;
    }
  }

  public int getTotalDocNum() {
    String term = "_docNum";
    try {
      return Integer.parseInt(getTermInfo(term).replaceAll("\n", ""));
    }
    catch (Exception e) {
      return 0;
    }
  }

  @Override
  public JsonArray getTermInfos(String term) {
    String termInfo = getTermInfo(term);
    if (termInfo == null) {
      termInfo = "";
    }
    String[] strings = termInfo.split(";");

    JsonArray toReturn = new JsonArray();
    for (int i = 1; i < strings.length; i++) {
      String[] str = strings[i].replaceAll("\n", "").split(" ");
      //System.out.println(strings[i]);
      int docID =  Integer.parseInt(str[0]);
      String docNum = getDocNum(docID);
      int tf =  Integer.parseInt(str[1]);
      JsonObject current = new JsonObject();
      current.addProperty("_id", docNum);
      current.addProperty("tf", tf);
      JsonArray positions = new JsonArray();
      for (int j = 2; j < str.length; j++) {
        positions.add(Integer.parseInt(str[j]));
      }
      current.add("p", positions);
      toReturn.add(current);
    }
    return toReturn;
  }

  @Override
  public void close() {

  }

  @Override
  public int getDocLen(String docNum) {
    return docNumtoDocLen.getOrDefault(docNum, 0);
  }

  public String getDocNum(int docID) {
    return docIDtoDocNum.getOrDefault(docID, null);
  }

  @Override
  public double getAvgLen() {
    double totalLen = 0;
    for (Integer len : docNumtoDocLen.values()) {
      totalLen += len;
    }
    return totalLen / getTotalDocNum();
  }


  @Override
  public JsonArray getAllDoc(String sentence) {
    HashSet<String> docs = new HashSet<>();
    JsonArray toReturn = new JsonArray();
    Scanner sc = new Scanner(sentence);
    while (sc.hasNext()) {
      String term = sc.next();
      JsonArray ja = getTermInfos(term);
      for (JsonElement jsonElement : ja) {
        String docNum = jsonElement.getAsJsonObject().get("_id").getAsString();
        docs.add(docNum);
      }
    }
    for (String docNum : docs) {
      JsonObject jo = new JsonObject();
      int len = getDocLen(docNum);
      jo.addProperty("_id", docNum);
      jo.addProperty("docLen", len);
      toReturn.add(jo);
    }
    sc.close();
    return toReturn;
  }

  public int getTotalTokens() {
    String term = "_totalTokens";
    try {
      return Integer.parseInt(getTermInfo(term).replaceAll("\n", ""));
    }
    catch (Exception e) {
      return 0;
    }
  }

  public static void main(String[] args) throws Exception {
    String path = "/Users/tommy/CS/CS6200/merged/";
    RandomAccessFile tl = new RandomAccessFile(path + "termlist4.txt", "r");
    CatalogueReader c = new CatalogueReader(new File(path + "catalogue4.txt"));
    File docs = new File(path + "docinfo4.txt");

    RetrieverMY r = new RetrieverMY(tl, c);
    r.loadDocs(docs);

    String term = "appprais";
    System.out.println(r.getDF(term));
    System.out.println(r.getTTF(term));
    System.out.println(r.getTermInfo(term));

    System.out.println(r.getAvgLen());
    //System.out.println(r.getTermInfos(term));
    //System.out.println(r.getTotalDocNum());
    //System.out.println(r.getAvgLen());
    //System.out.println(r.getDocLen(r.getDocNum(1)));
    //System.out.println(r.getDocLen(r.getDocNum(84678)));

//    System.out.println(r.getTermInfos(term));
//
//    System.out.println(r.getAvgLen());

//    System.out.println(r.getAllDoc("appl algorithm").size());
//    System.out.println();
  }
}
