package nl.naturalis.geneious.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.TraceFileType;
import nl.naturalis.geneious.note.NaturalisNote;

import static nl.naturalis.geneious.TraceFileType.*;
import static nl.naturalis.geneious.util.DocumentUtils.wasCreatedFromAb1File;

public class DocumentManager {

  private final Collection<AnnotatedPluginDocument> documents;

  private final EnumMap<TraceFileType, Map<String, List<AnnotatedPluginDocument>>> byTypeByExtractId;

  public DocumentManager(Collection<AnnotatedPluginDocument> documents) {
    this.documents = documents;
    byTypeByExtractId = new EnumMap<>(TraceFileType.class);
    HashMap<String, ArrayList<AnnotatedPluginDocument>> ab1s;
    HashMap<String, ArrayList<AnnotatedPluginDocument>> fastas;
    for (AnnotatedPluginDocument document : documents) {
      if (wasCreatedFromAb1File(document)) {
        addAb1(document);
      }
    }

  }

  private void addAb1(AnnotatedPluginDocument document) {
    NaturalisNote note = new NaturalisNote(document);
    byTypeByExtractId
        .computeIfAbsent(AB1, (k) -> new HashMap<>())
        .computeIfAbsent(note.getExtractId(), (k) -> new ArrayList<>())
        .add(document);
  }

  private void addFasta(AnnotatedPluginDocument document) {
    NaturalisNote note = new NaturalisNote(document);
    byTypeByExtractId
        .computeIfAbsent(FASTA, (k) -> new HashMap<>())
        .computeIfAbsent(note.getExtractId(), (k) -> new ArrayList<>())
        .add(document);
  }

  public AnnotatedPluginDocument findLatestVersion(String extractID, TraceFileType fileType) {
    // TODO Auto-generated method stub
    return null;
  }

}
