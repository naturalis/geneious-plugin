package nl.naturalis.geneious;

public class Test {

  public static void main(String[] args) {
    String s = "Registrationnumber  Rank or classification  Name  Genus or monomial Full scientific name  Identifier  Sex Phase or stage  Agent Collecting start date Country State/province  Locality  Lattitude Longitude Altitude";
    System.out.println(s.replace("  ","\n").replace('/', '_').replace(' ', '_').toUpperCase());


  }

}
