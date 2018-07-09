package nl.naturalis.geneious;

import java.io.File;
import java.util.List;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

public interface SampleSheetProcessor {

  void process(File sampleSheet, List<AnnotatedPluginDocument> documentsToEnrich,
      boolean createDummies);

}
