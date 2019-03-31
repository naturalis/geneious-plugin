package nl.naturalis.geneious.crs;

import java.util.EnumMap;

import nl.naturalis.geneious.csv.InvalidRowException;
import nl.naturalis.geneious.csv.NoteFactory;
import nl.naturalis.geneious.note.NaturalisNote;

import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.trimToNull;

import static nl.naturalis.geneious.crs.CrsColumn.FULL_SCIENTIFIC_NAME;
import static nl.naturalis.geneious.crs.CrsColumn.GENUS_OR_MONOMIAL;
import static nl.naturalis.geneious.crs.CrsColumn.HIGHER_NAMES;
import static nl.naturalis.geneious.crs.CrsColumn.HIGHER_RANKS;
import static nl.naturalis.geneious.crs.CrsColumn.IDENTIFIED_BY;
import static nl.naturalis.geneious.crs.CrsColumn.SEX;
import static nl.naturalis.geneious.note.NaturalisField.CRS_CLASS;
import static nl.naturalis.geneious.note.NaturalisField.CRS_FAMILY;
import static nl.naturalis.geneious.note.NaturalisField.CRS_FLAG;
import static nl.naturalis.geneious.note.NaturalisField.CRS_GENUS;
import static nl.naturalis.geneious.note.NaturalisField.CRS_IDENTIFIER;
import static nl.naturalis.geneious.note.NaturalisField.CRS_ORDER;
import static nl.naturalis.geneious.note.NaturalisField.CRS_PHYLUM;
import static nl.naturalis.geneious.note.NaturalisField.CRS_SCIENTIFIC_NAME;
import static nl.naturalis.geneious.note.NaturalisField.CRS_SEX;
import static nl.naturalis.geneious.note.NaturalisField.CRS_SUBFAMILY;

/**
 * Represents a single row within a sample sheet and functions as a producer of {@link NaturalisNote} instances.
 */
class CrsNoteFactory extends NoteFactory<CrsColumn> {

  CrsNoteFactory(int rownum, CrsRow cells) {
    super(rownum, cells);
  }

  @Override
  protected void populate(NaturalisNote note) throws InvalidRowException {
    note.castAndSet(CRS_FLAG, Boolean.TRUE);
    setOptionalValue(note, CRS_IDENTIFIER, IDENTIFIED_BY);
    setOptionalValue(note, CRS_SCIENTIFIC_NAME, FULL_SCIENTIFIC_NAME);
    setOptionalValue(note, CRS_GENUS, GENUS_OR_MONOMIAL);
    setOptionalValue(note, CRS_SEX, SEX);
    addClassification(note);
  }

  private void addClassification(NaturalisNote note) throws InvalidRowException {
    String[] ranks = split(get(HIGHER_RANKS), '/');
    String[] names = split(get(HIGHER_NAMES), '/');
    if (ranks.length != names.length) {
      throw InvalidRowException.custom(this, "Number of ranks (%s) does not match number of names (%s)", ranks.length, names.length);
    }
    for (int i = 0; i < ranks.length; ++i) {
      String rank = trimToNull(ranks[i]);
      if (rank == null) {
        throw InvalidRowException.custom(this, "Empty rank at position %s", i + 1);
      }
      String name = trimToNull(names[i]);
      if (name == null) {
        throw InvalidRowException.custom(this, "No name provided for %s", rank);
      }
      switch (rank) {
        case "phylum":
          note.parseAndSet(CRS_PHYLUM, name);
          break;
        case "order":
          note.parseAndSet(CRS_ORDER, name);
          break;
        case "class":
          note.parseAndSet(CRS_CLASS, name);
          break;
        case "family":
          note.parseAndSet(CRS_FAMILY, name);
          break;
        case "subfamily":
          note.parseAndSet(CRS_SUBFAMILY, name);
          break;
        default:
          throw InvalidRowException.custom(this, "Unknown rank: %s", rank);
      }
    }
  }

}
