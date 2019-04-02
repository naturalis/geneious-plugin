package nl.naturalis.geneious.crs;

import nl.naturalis.geneious.csv.InvalidRowException;
import nl.naturalis.geneious.csv.NoteFactory;
import nl.naturalis.geneious.note.NaturalisNote;

import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.trimToNull;

import static nl.naturalis.geneious.crs.CrsColumn.AGENT;
import static nl.naturalis.geneious.crs.CrsColumn.ALTITUDE;
import static nl.naturalis.geneious.crs.CrsColumn.COLLECTING_START_DATE;
import static nl.naturalis.geneious.crs.CrsColumn.COUNTRY;
import static nl.naturalis.geneious.crs.CrsColumn.FULL_SCIENTIFIC_NAME;
import static nl.naturalis.geneious.crs.CrsColumn.GENUS_OR_MONOMIAL;
import static nl.naturalis.geneious.crs.CrsColumn.HIGHER_NAMES;
import static nl.naturalis.geneious.crs.CrsColumn.HIGHER_RANKS;
import static nl.naturalis.geneious.crs.CrsColumn.IDENTIFIED_BY;
import static nl.naturalis.geneious.crs.CrsColumn.LATTITUDE;
import static nl.naturalis.geneious.crs.CrsColumn.LOCALITY;
import static nl.naturalis.geneious.crs.CrsColumn.LONGITUDE;
import static nl.naturalis.geneious.crs.CrsColumn.PHASE_OR_STAGE;
import static nl.naturalis.geneious.crs.CrsColumn.REGISTRATION_NUMBER;
import static nl.naturalis.geneious.crs.CrsColumn.SEX;
import static nl.naturalis.geneious.crs.CrsColumn.STATE_OR_PROVINCE;
import static nl.naturalis.geneious.note.NaturalisField.CRS_ALTITUDE;
import static nl.naturalis.geneious.note.NaturalisField.CRS_CLASS;
import static nl.naturalis.geneious.note.NaturalisField.CRS_COLLECTOR;
import static nl.naturalis.geneious.note.NaturalisField.CRS_COUNTRY;
import static nl.naturalis.geneious.note.NaturalisField.CRS_DATE;
import static nl.naturalis.geneious.note.NaturalisField.CRS_FAMILY;
import static nl.naturalis.geneious.note.NaturalisField.CRS_FLAG;
import static nl.naturalis.geneious.note.NaturalisField.CRS_GENUS;
import static nl.naturalis.geneious.note.NaturalisField.CRS_IDENTIFIER;
import static nl.naturalis.geneious.note.NaturalisField.CRS_LATITUDE;
import static nl.naturalis.geneious.note.NaturalisField.CRS_LOCALITY;
import static nl.naturalis.geneious.note.NaturalisField.CRS_LONGITUDE;
import static nl.naturalis.geneious.note.NaturalisField.CRS_ORDER;
import static nl.naturalis.geneious.note.NaturalisField.CRS_PHYLUM;
import static nl.naturalis.geneious.note.NaturalisField.CRS_REGION;
import static nl.naturalis.geneious.note.NaturalisField.CRS_SCIENTIFIC_NAME;
import static nl.naturalis.geneious.note.NaturalisField.CRS_SEX;
import static nl.naturalis.geneious.note.NaturalisField.CRS_STAGE;
import static nl.naturalis.geneious.note.NaturalisField.CRS_SUBFAMILY;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_REGISTRATION_NUMBER;

/**
 * Represents a single row within a sample sheet and functions as a producer of {@link NaturalisNote} instances.
 */
class CrsNoteFactory extends NoteFactory<CrsColumn> {

  CrsNoteFactory(int rownum, CrsRow row) {
    super(rownum, row);
  }

  @Override
  protected void populate(NaturalisNote note) throws InvalidRowException {
    note.castAndSet(CRS_FLAG, Boolean.TRUE);
    setRequiredValue(note, SMPL_REGISTRATION_NUMBER, REGISTRATION_NUMBER);
    setRequiredValue(note, CRS_SCIENTIFIC_NAME, FULL_SCIENTIFIC_NAME);
    setValue(note, CRS_IDENTIFIER, IDENTIFIED_BY);
    setValue(note, CRS_GENUS, GENUS_OR_MONOMIAL);
    setValue(note, CRS_SEX, SEX);
    setValue(note, CRS_COLLECTOR, AGENT);
    setValue(note, CRS_DATE, COLLECTING_START_DATE);
    setValue(note, CRS_STAGE, PHASE_OR_STAGE);
    setValue(note, CRS_LATITUDE, LATTITUDE);
    setValue(note, CRS_LONGITUDE, LONGITUDE);
    setValue(note, CRS_ALTITUDE, ALTITUDE);
    setValue(note, CRS_COUNTRY, COUNTRY);
    setValue(note, CRS_REGION, STATE_OR_PROVINCE);
    setValue(note, CRS_LOCALITY, LOCALITY);
    addClassification(note);
  }

  private void addClassification(NaturalisNote note) throws InvalidRowException {
    String r = (r = get(HIGHER_RANKS)) == null ? "" : r;
    String n = (n = get(HIGHER_NAMES)) == null ? "" : n;
    String[] ranks = split(r, '/');
    String[] names = split(n, '/');
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
