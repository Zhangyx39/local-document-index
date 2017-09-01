package hw2;

import java.io.File;

/**
 * Created by Yixing Zhang on 6/3/17.
 */
public class MergeAll {

  public static void main(String[] args) throws Exception {
    String path = "/Users/tommy/Dropbox/CS6200_Yixing_Zhang/homework2";
    File tl = new File(path + "/TermLIst");
    String[] list = tl.list();
    while (list.length > 1) {
      for (int i = 0; i < list.length - list.length % 2; i += 2) {
        String n1 = list[i];
        String n2 = list[i + 1];
        Merger merger = new Merger(path, n1, n2);
        merger.merge();
      }
      list = tl.list();
    }
  }
}
