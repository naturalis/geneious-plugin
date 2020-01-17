package nl.naturalis.geneious;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SuppressWarnings("unused")
public class Test {

  public static void main(String[] args) {
//    Date d = new Date(1496858267470L);
//    SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm");
//    System.out.println("The date is: "+ df.format(d));
    //1570085047432
    Instant i = Instant.ofEpochMilli(1470208622364L);
    System.out.println(DateTimeFormatter.ISO_INSTANT.format(i));
  }

}
