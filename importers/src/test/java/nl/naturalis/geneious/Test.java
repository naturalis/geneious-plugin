package nl.naturalis.geneious;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Test {

  public static void main(String[] args) {
    List<String> l1 = Arrays.asList("1","2","3","4");
    List<String> l2 = l1.subList(2, 2);
    System.out.println("XXXXX"+l2.size());
  }

}
