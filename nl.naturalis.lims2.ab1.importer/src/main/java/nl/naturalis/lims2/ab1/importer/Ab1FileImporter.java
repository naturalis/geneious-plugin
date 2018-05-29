package nl.naturalis.lims2.ab1.importer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentFileImporter;
import com.biomatters.geneious.publicapi.plugin.DocumentImportException;
import com.biomatters.geneious.publicapi.plugin.PluginUtilities;
import jebl.util.ProgressListener;

public class Ab1FileImporter extends DocumentFileImporter {

  public Ab1FileImporter() {
    super();
  }

  @Override
  public String getFileTypeDescription() {
    return "AB1 files (enriched)";
  }

  @Override
  public String[] getPermissibleExtensions() {
    return new String[] {""};
  }

  @Override
  public void importDocuments(File f, ImportCallback callback, ProgressListener progress)
      throws DocumentImportException, IOException {
    List<AnnotatedPluginDocument> docs = PluginUtilities.importDocuments(f, null);
    AnnotatedPluginDocument apd = callback.addDocument(docs.get(0));
    // callback.
  }

  @Override
  public AutoDetectStatus tentativeAutoDetect(File file, String fileContentsStart) {
    if (fileContentsStart.startsWith(">")) {
      return AutoDetectStatus.MAYBE;
    }
    return AutoDetectStatus.ACCEPT_FILE;
  }

}
