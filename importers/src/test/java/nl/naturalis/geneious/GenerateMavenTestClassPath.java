package nl.naturalis.geneious;

import java.io.File;
import java.util.Arrays;

/**
 * Utility class the print an XML snippet to be inserted into the pom's additionalClasspathElements element of the
 * maven-surefire-configuration.
 */
public class GenerateMavenTestClassPath {

  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("USAGE: java GenerateMavenTestClassPath /path/to/GeneiousFiles");
      System.exit(1);
    }
    File root = new File(args[0]);
    if (!root.isDirectory()) {
      System.err.println("no such directory: " + args[0]);
      System.exit(1);
    }
    File libdir = new File(root.getAbsolutePath() + "/lib");
    if (!libdir.isDirectory()) {
      System.err.println("no such directory: " + args[0]);
      System.exit(1);
    }
    File[] jars = libdir.listFiles(f -> f.getName().endsWith(".jar"));
    Arrays.sort(jars, (f1, f2) -> f1.getName().compareTo(f2.getName()));
    for (File jar : jars) {
      /*
       * Assumption: pom.xml or settings.xml defines a property named "GeneiousFiles.dir" which also points to the GeneiousFiles files
       * directory !! Otherwise we would have to write out the full path here, which becomes a bit verbose. 
       */
      System.out.print("<additionalClasspathElement>${GeneiousFiles.dir}/lib/");
      System.out.print(jar.getName());
      System.out.println("</additionalClasspathElement>");
    }
  }

}
