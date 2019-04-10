package nl.naturalis.geneious.seq;

import java.util.Objects;

import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.util.StoredDocument;

class CacheKey {
  private final DocumentType dt;
  private final String id;
  private final int hash;

  CacheKey(DocumentType dt, String id) {
    Objects.requireNonNull(this.dt = dt, "Document type must not be null");
    Objects.requireNonNull(this.id = id, "ID must not be null");
    hash = (dt.ordinal() * 31) + id.hashCode();
  }

  CacheKey(StoredDocument doc) {
    this(doc.getType(), doc.getNaturalisNote().getExtractId());
  }

  CacheKey(ImportableDocument doc) {
    this(doc.getSequenceInfo().getDocumentType(), doc.getSequenceInfo().getNaturalisNote().getExtractId());
  }

  @Override
  public boolean equals(Object obj) {
    CacheKey other = (CacheKey) obj;
    return dt == other.dt && id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return hash;
  }
}