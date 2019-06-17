package nl.naturalis.geneious.note;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;

/**
 * Interface bridging the plugin's concept of a set of annotations and Geneious's concept of it.
 *
 * @author Ayco Holleman
 */
public interface Note {

  /**
   * Copies annotations from the plugin-native {@code Note} instance to the Geneious-native {@code DocumentNotes}
   * instance. The {@code DocumentNotes} instance must have been retrieved using
   * {@code AnnotatedPluginDocument.getDocumentNotes(true}}.
   * 
   * @param notes
   */
  void copyTo(DocumentNotes notes);

}
