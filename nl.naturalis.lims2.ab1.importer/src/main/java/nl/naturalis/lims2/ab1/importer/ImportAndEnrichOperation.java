package nl.naturalis.lims2.ab1.importer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JFileChooser;
import org.apache.commons.lang3.StringUtils;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.Constraint;
import com.biomatters.geneious.publicapi.documents.DocumentNote;
import com.biomatters.geneious.publicapi.documents.DocumentNoteField;
import com.biomatters.geneious.publicapi.documents.DocumentNoteType;
import com.biomatters.geneious.publicapi.documents.DocumentNoteUtilities;
import com.biomatters.geneious.publicapi.plugin.DocumentImportException;
import com.biomatters.geneious.publicapi.plugin.DocumentOperation;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import com.biomatters.geneious.publicapi.plugin.Options;
import com.biomatters.geneious.publicapi.plugin.PluginUtilities;
import com.biomatters.geneious.publicapi.utilities.GuiUtilities;
import jebl.util.ProgressListener;
import nl.naturalis.geneious.util.RuntimeSettings;

public class ImportAndEnrichOperation extends DocumentOperation {

  public ImportAndEnrichOperation() {
    super();
  }

  @Override
  public GeneiousActionOptions getActionOptions() {
    return new GeneiousActionOptions("Ayco Rocks").setInMainToolbar(true);
  }

  public List<AnnotatedPluginDocument> performOperation(AnnotatedPluginDocument[] docs,
      ProgressListener progress, Options options) {
    JFileChooser fc = new JFileChooser(RuntimeSettings.INSTANCE.getLastSelectedFolder());
    fc.setMultiSelectionEnabled(true);
    List<AnnotatedPluginDocument> result = new ArrayList<>();
    if (fc.showOpenDialog(GuiUtilities.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
      RuntimeSettings.INSTANCE.setLastSelectedFolder(fc.getCurrentDirectory());
      File[] files = fc.getSelectedFiles();
      for (File f : files) {
        try {
          List<AnnotatedPluginDocument> apds = PluginUtilities.importDocuments(f, null);
          // In the world as we understand it:
          assert (apds.size() == 1);
          if (f.getName().endsWith(".ab1")) {
            if (f.getName().contains("_")) {
              String[] chunks = StringUtils.split(f.getName(), '_');

              DocumentNoteType noteType = DocumentNoteUtilities.getNoteType("naturalis-sequence-note");
              if (noteType == null) {
                List<DocumentNoteField> noteFields = new ArrayList<>();
                noteFields.add(DocumentNoteField.createTextNoteField("Extract ID",
                    "Extract ID", "ExtractIDCode_Seq", Collections.emptyList(), false));
                noteFields.add(DocumentNoteField.createTextNoteField("PCR Plate ID",
                    "PCR Plate ID", "PCRplateIDCode_Seq", Collections.emptyList(), false));
                
                noteType = DocumentNoteUtilities.createNewNoteType("Naturalis sequence annotation",
                    "naturalis-sequence-note", "Naturalis sequence annotation", noteFields, true);
                DocumentNoteUtilities.setNoteType(noteType);
              }
              else {
                noteType.setField(DocumentNoteField.createTextNoteField("Extract ID",
                    "Extract ID", "ExtractIDCode_Seq", Collections.emptyList(), false));
                noteType.setField(DocumentNoteField.createTextNoteField("PCR Plate ID",
                    "PCR Plate ID", "PCRplateIDCode_Seq", Collections.emptyList(), false));
               }

              String extractId = chunks[0];
              String plateId = chunks[3];
              
              DocumentNote note = noteType.createDocumentNote();
              note.setFieldValue("ExtractIDCode_Seq", extractId);
              note.setFieldValue("PCRplateIDCode_Seq", plateId);
              
              
              AnnotatedPluginDocument.DocumentNotes documentNotes = apds.get(0).getDocumentNotes(true);
              documentNotes.setNote(note);
              documentNotes.saveNotes();

//              String plateId = chunks[3];
//              String marker = StringUtils.EMPTY;
//              if (chunks[4].contains("-")) {
//                marker = chunks[4].substring(0, chunks[4].indexOf('-'));
//              }
            }
          }
          result.addAll(apds);
        } catch (IOException | DocumentImportException e) {
          throw new RuntimeException(e);
        }
      }
    }

    // List<DocumentNoteField> noteFields = new ArrayList<>();
    // String fieldCode = "Field1-Ayco";
    // noteFields.add(DocumentNoteField.createTextNoteField("Field no.1", "The first field",
    // fieldCode, Collections.emptyList(), false));
    //
    // String noteTypeCode = "DocumentNoteUtilities-AycoTestNote";
    // DocumentNoteType noteType = DocumentNoteUtilities.getNoteType(noteTypeCode);
    // if (noteType == null) { //create and add the note type if it hasn't already been added
    // noteType = DocumentNoteUtilities.createNewNoteType("Test Note", noteTypeCode, "A test note",
    // noteFields, true);
    // DocumentNoteUtilities.setNoteType(noteType);
    // }
    //
    // DocumentNote note = noteType.createDocumentNote();
    // note.setFieldValue(fieldCode, "Ayco rocks!");
    //
    // AnnotatedPluginDocument.DocumentNotes documentNotes = result.get(0).getDocumentNotes(true);
    // documentNotes.setNote(note);
    // documentNotes.saveNotes();
    //
    //
    // for(AnnotatedPluginDocument apd : result) {
    // //apd.
    // }
    return result;
  }

  @Override
  public String getHelp() {
    // TODO Auto-generated method stub
    return "Won't tell ya";
  }

  @Override
  public DocumentSelectionSignature[] getSelectionSignatures() {
    return new DocumentSelectionSignature[0];
  }

}
