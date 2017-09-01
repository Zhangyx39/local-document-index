package hw2;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by Yixing Zhang on 6/2/17.
 */
public class CatalogueReader {
  HashMap<String, TermInfo> data;

  public CatalogueReader(File catalogue) {
    this.data = new HashMap<>();
    readFile(catalogue);
  }

  private void readFile(File f) {
    try {
      Scanner sc = new Scanner(f);
      while (sc.hasNextLine()) {
        int termID = sc.nextInt();
        String term = sc.next();
        int offset = sc.nextInt();
        int len = sc.nextInt();
        data.put(term, new TermInfo(termID, offset, len));
      }
      sc.close();
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  public Set<String> getKeySet() {
    return data.keySet();
  }

  public void delete(String term) {
    data.remove(term);
  }

  public int getOffset(String term) {
    if (data.containsKey(term)) {
      return data.get(term).getOffset();
    }
    return -1;
  }

  public int getLength(String term) {
    if (data.containsKey(term)) {
      return data.get(term).getLength();
    }
    return -1;
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

    public int getOffset() {
      return offset;
    }

    public int getLength() {
      return length;
    }
  }

  public static void main(String[] args) {
    HashMap<String, Integer> hm = new HashMap<>();
    hm.put("1", 1);
    hm.remove("2");
  }
}
