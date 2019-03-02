package nl.naturalis.geneious.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import static nl.naturalis.geneious.DocumentType.*;

import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.note.NaturalisNote;

import static nl.naturalis.geneious.DocumentType.*;
import static nl.naturalis.geneious.util.DocumentUtils.wasCreatedFromAb1File;

public class DocumentManager {

  private final Collection<AnnotatedPluginDocument> documents;

  private final EnumMap<DocumentType, Map<String, List<AnnotatedPluginDocument>>> byTypeByExtractId;

  public DocumentManager(Collection<AnnotatedPluginDocument> documents) {
    this.documents = documents;
    byTypeByExtractId = new EnumMap<>(DocumentType.class);
    HashMap<String, ArrayList<AnnotatedPluginDocument>> ab1s;
    HashMap<String, ArrayList<AnnotatedPluginDocument>> fastas;
    for (AnnotatedPluginDocument document : documents) {
      NaturalisNote note = new NaturalisNote(document);
      switch(getType(document,note)) {
        
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

  private static DocumentType getType(AnnotatedPluginDocument doc, NaturalisNote note) {
    if (note.getMarker() != null && note.getMarker().equals("Dum")) {
      return DUMMY;
    }
    if (doc.getDocumentClass() == AB1.getGeneiousType()) {
      return AB1;
    }
    if (doc.getDocumentClass() == FASTA.getGeneiousType()) {
      return FASTA;
    }
    return null;
  }

  public AnnotatedPluginDocument findLatestVersion(String extractID, DocumentType fileType) {
    // TODO Auto-generated method stub
    return null;
  }

}
