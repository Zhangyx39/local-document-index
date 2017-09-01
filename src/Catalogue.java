package hw2;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yixing Zhang on 5/31/17.
 */
public class Catalogue {
  private HashMap<String, TermInfo> data;
  private int lastID;

  public Catalogue() {
    this.data = new HashMap<>();
    this.lastID = 1;
    data.put("_vocabulary", new TermInfo(0, 0, 0));
    data.put("_docNum", new TermInfo(0, 0, 0));
    data.put("_totalTokens", new TermInfo(0, 0, 0));
  }

  public void reset() {
    data.clear();
    lastID = 1;
    data.put("_vocabulary", new TermInfo(0, 0, 0));
    data.put("_docNum", new TermInfo(0, 0, 0));
    data.put("_totalTokens", new TermInfo(0, 0, 0));
  }

  public int getLastID() {
    return lastID;
  }

  public int getTermID(String term) {
    if (data.containsKey(term)) {
      return data.get(term).termID;
    } else {
      return -1;
    }
  }

  public void setTerm(String term, int offset, int length) {
    if (!data.containsKey(term)) {
      throw new IllegalArgumentException(term + " doesn't exist.");
    }
    TermInfo termInfo = data.get(term);
    termInfo.setOffset(offset);
    termInfo.setLength(length);
  }

  public int addTerm(String term) {
    if (data.containsKey(term)) {
      throw new IllegalArgumentException(term + " already exists.");
    }
    data.put(term, new TermInfo(lastID, -1, -1));
    lastID++;
    return lastID - 1;
  }

  public void addTerm(String term, int offset, int length) {
    if (data.containsKey(term)) {
      throw new IllegalArgumentException(term + " already exists.");
    }
    data.put(term, new TermInfo(lastID, offset, length));
    lastID++;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, TermInfo> entry : data.entrySet()) {
      String str = entry.getValue().termID + " "
              + entry.getKey() + " "
              + entry.getValue().offset + " "
              + entry.getValue().length + "\n";
      sb.append(str);
    }
    return sb.toString().trim();
  }

  public void outputFile(String path, String name) {
    try {
      System.out.println("Output catalogue: " + name);
      FileUtils.writeStringToFile(
              new File(path + "/Catalogue/" + name + ".txt"),
              this.toString(), "UTF-8");
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  class TermInfo {
    int termID;
    int offset;
    int length;

    public TermInfo(int termID, int offset, int length) {
      this.termID = termID;
      this.offset = offset;
      this.length = length;
    }

    public void setOffset(int offset) {
      this.offset = offset;
    }

    public void setLength(int length) {
      this.length = length;
    }
  }

  public static void main(String[] args) {
    Catalogue c = new Catalogue();
    c.addTerm("the");
    c.setTerm("the", 10, 20);
    c.addTerm("car", 10, 20);
    c.addTerm("was", 10, 20);
    System.out.println(c.toString());
  }
}
