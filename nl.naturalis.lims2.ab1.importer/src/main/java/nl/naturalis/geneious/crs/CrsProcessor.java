package nl.naturalis.geneious.crs;

import java.io.File;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

class CrsProcessor {

  private File crsFile;
  private CrsProcessingOptions options;
  private AnnotatedPluginDocument[] selectedDocuments;

  void initialize(File crsFile, CrsProcessingOptions options,
      AnnotatedPluginDocument[] selectedDocuments) {
    this.crsFile = crsFile;
    this.options = options;
    this.selectedDocuments = selectedDocuments;
  }

  void process() {
    System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX: " + selectedDocuments.length);
  }

}
