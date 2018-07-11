package nl.naturalis.geneious;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.documents.URN;
import com.biomatters.geneious.publicapi.documents.sequence.NucleotideSequenceDocument;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideSequence;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import nl.naturalis.geneious.gui.SampleSheetSelector;
import nl.naturalis.geneious.notes.NaturalisNote;

public class SampleSheetImporter extends DocumentAction implements SampleSheetProcessor {

  private static final String DUMMY_NUCLEOTIDE_SEQUENCE = "NNNNNNNNNN";
  private static final String DUMMY_PLATE_ID = "AA000";
  private static final String DUMMY_MARKER = "Dum";

  public SampleSheetImporter() {
    super();
  }

  @Override
  public void actionPerformed(AnnotatedPluginDocument[] docs) {
    SampleSheetSelector sss = new SampleSheetSelector(this);
    sss.show();
  }

  @Override
  public GeneiousActionOptions getActionOptions() {
    return new GeneiousActionOptions("Samples [V2]")
        .setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools).setInMainToolbar(true)
        .setInPopupMenu(true).setAvailableToWorkflows(true);
  }

  @Override
  public String getHelp() {
    return null;
  }

  @Override
  public DocumentSelectionSignature[] getSelectionSignatures() {
    return new DocumentSelectionSignature[0];
  }

  @Override
  public void process(File sampleSheet, List<AnnotatedPluginDocument> documentsToEnrich,
      boolean createDummies) {
    TsvParserSettings settings = new TsvParserSettings();
    settings.getFormat().setLineSeparator("\n");
    TsvParser parser = new TsvParser(settings);
    List<String[]> rows = parser.parseAll(sampleSheet);
    List<AnnotatedPluginDocument> apds = new ArrayList<AnnotatedPluginDocument>(rows.size());
    for (int i = 1; i < rows.size(); i++) {
      SampleSheetRow sampleSheetRow = new SampleSheetRow(i, rows.get(i));
      if (sampleSheetRow.isEmpty()) {
        continue;
      }
      NaturalisNote note;
      try {
        note = sampleSheetRow.extractNote();
      } catch (SampleSheetRowException e) {
        // TODO log error message
        System.err.println(e.getMessage());
        continue;
      }
      note.setPcrPlateId(DUMMY_PLATE_ID);
      note.setMarker(DUMMY_MARKER);
      NucleotideSequenceDocument nsd = createDummyDocument(note);
      AnnotatedPluginDocument apd = DocumentUtilities.createAnnotatedPluginDocument(nsd);
      note.attach(apd);
      apds.add(apd);
    }
    DocumentUtilities.addGeneratedDocuments(apds, false);
  }

  private static NucleotideSequenceDocument createDummyDocument(NaturalisNote note) {
    String seqName = note.getExtractId() + ".dum";
    String descr = "Dummy sequence";
    String sequence = DUMMY_NUCLEOTIDE_SEQUENCE;
    Date timestamp = new Date();
    URN urn = URN.generateUniqueLocalURN("Dummy");
    return new DefaultNucleotideSequence(seqName, descr, sequence, timestamp, urn);
  }

}
