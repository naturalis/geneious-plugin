package nl.naturalis.geneious.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;

import static nl.naturalis.geneious.DocumentType.AB1;
import static nl.naturalis.geneious.DocumentType.DUMMY;
import static nl.naturalis.geneious.DocumentType.FASTA;

/**
 * Provides various types of lookups on a collection of Geneious documents (presumably fetched from the database).
 */
public class DocumentResultSetInspector {

  public static class CacheValue {
    private final AnnotatedPluginDocument doc;
    private final NaturalisNote note;

    private CacheValue(AnnotatedPluginDocument doc, NaturalisNote note) {
      this.doc = doc;
      this.note = note;
    }

    public AnnotatedPluginDocument getDoc() {
      return doc;
    }

    public NaturalisNote getNote() {
      return note;
    }
  }

  // Sorts descending on document version
  private static Comparator<CacheValue> versionComparator = (v1, v2) -> {
    if (v1.note.getDocumentVersion() == null) {
      if (v2.note.getDocumentVersion() == null) {
        return 0;
      }
      return -1;
    }
    if (v2.note.getDocumentVersion() == null) {
      return 1;
    }
    return v2.note.getDocumentVersion() - v1.note.getDocumentVersion();
  };

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(DocumentResultSetInspector.class);

  private final EnumMap<DocumentType, HashMap<String, TreeSet<CacheValue>>> byTypeByExtractId;

  public DocumentResultSetInspector(Collection<AnnotatedPluginDocument> documents) {
    this.byTypeByExtractId = new EnumMap<>(DocumentType.class);
    cacheDocuments(documents);
  }

  public CacheValue findPreviousVersion(String extractID, DocumentType type) {
    Map<String, TreeSet<CacheValue>> subcache = byTypeByExtractId.get(type);
    if (subcache != null) {
      TreeSet<CacheValue> values = subcache.get(extractID);
      if (values != null) {
        return values.first();
      }
    }
    return null;
  }

  public CacheValue findDummy(String extractID) {
    Map<String, TreeSet<CacheValue>> subcache = byTypeByExtractId.get(DUMMY);
    if (subcache != null) {
      TreeSet<CacheValue> values = subcache.get(extractID);
      if (values != null) {
        return values.first();
      }
    }
    return null;
  }

  private void cacheDocuments(Collection<AnnotatedPluginDocument> documents) {
    for (AnnotatedPluginDocument document : documents) {
      NaturalisNote note = new NaturalisNote(document);
      DocumentType type = getType(document, note);
      switch (type) {
        case AB1:
        case FASTA:
        case DUMMY:
          cacheDocument(document, note, type);
          break;
        case UNKNOWN:
        default:
          guiLogger.warn("Unexpected document type: %s (document ignored)", document.getDocumentClass());
      }
    }
  }

  private void cacheDocument(AnnotatedPluginDocument doc, NaturalisNote note, DocumentType type) {
    byTypeByExtractId
        .computeIfAbsent(type, (k) -> new HashMap<>())
        .computeIfAbsent(note.getExtractId(), (k) -> new TreeSet<>(versionComparator))
        .add(new CacheValue(doc, note));
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

}
