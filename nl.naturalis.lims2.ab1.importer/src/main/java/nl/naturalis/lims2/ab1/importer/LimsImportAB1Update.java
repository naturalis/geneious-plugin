/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.awt.EventQueue;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.naturalis.lims2.utils.LimsAB1Fields;
import nl.naturalis.lims2.utils.LimsImporterUtil;
import nl.naturalis.lims2.utils.LimsNotes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.components.Dialogs;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.documents.sequence.NucleotideSequenceDocument;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentOperationException;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsImportAB1Update extends DocumentAction {

	SequenceDocument seq;
	LimsNotes limsNotes = new LimsNotes();
	private LimsAB1Fields limsAB1Fields = new LimsAB1Fields();
	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	private List<AnnotatedPluginDocument> docs;
	// private List<AnnotatedPluginDocument> document;
	// private ImportCallback importCallback;
	private static final Logger logger = LoggerFactory
			.getLogger(LimsImportAB1Update.class);
	// private LimsReadGeneiousFieldsValues readVersionNumberValue = new
	// LimsReadGeneiousFieldsValues();
	// private Object versionNumber = "";

	// private final String noteCode = "DocumentNoteUtilities-Document version";
	// private final String fieldName = "DocumentversionCode";
	// private final String noteBOS =
	// "DocumentNoteUtilities-Registr-nmbr (Samples)";
	// private final String fieldBOS = "RegistrationnumberCode_Samples";

	private List<String> msgList = new ArrayList<String>();
	LimsFileSelector fcd = new LimsFileSelector();

	// private AnnotatedPluginDocument annotatedPluginDocument;

	@Override
	public void actionPerformed(
			AnnotatedPluginDocument[] annotatedPluginDocuments) {
		if (DocumentUtilities.getSelectedDocuments().isEmpty()) {
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					Dialogs.showMessageDialog("Select all documents");
					return;
				}
			});
		}

		if (!DocumentUtilities.getSelectedDocuments().isEmpty()) {

			logger.info("----------------------------S T A R T -------------------------------");
			try {
				docs = DocumentUtilities.getSelectedDocuments();
				// AnnotatedPluginDocument annotatedPluginDocument =
				// docs.iterator().next();

				for (int cnt = 0; cnt < docs.size(); cnt++) {
					seq = (SequenceDocument) docs.get(cnt).getDocument();

					if (seq.getName() != null && seq.getName().contains("_")) {
						logger.info("Start extracting value from file: "
								+ seq.getName());
						msgList.add(seq.getName());

						if (seq.getName().contains("ab1")) {
							limsAB1Fields.setFieldValuesFromAB1FileName(seq
									.getName());
						} else {
							try {
								limsAB1Fields.setFieldValuesFromAB1FileName(fcd
										.loadFastaFile(seq.getName()));
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}
						}

						logger.info("Extract ID: "
								+ limsAB1Fields.getExtractID());
						logger.info("PCR plaat ID: "
								+ limsAB1Fields.getPcrPlaatID());
						logger.info("Marker: " + limsAB1Fields.getMarker());
						logger.info("Versienummer: "
								+ limsAB1Fields.getVersieNummer());

						/** set note for Extract-ID */
						limsNotes.setNoteToAB1FileName(
								annotatedPluginDocuments, "ExtractIDCode_Seq",
								"Extract ID (Seq)", "Extract ID (Seq)",
								limsAB1Fields.getExtractID(), cnt);

						/** set note for PCR Plate ID */
						limsNotes.setNoteToAB1FileName(
								annotatedPluginDocuments, "PCRplateIDCode_Seq",
								"PCR plate ID (Seq)", "PCR plate ID (Seq)",
								limsAB1Fields.getPcrPlaatID(), cnt);

						/** set note for Marker */
						limsNotes.setNoteToAB1FileName(
								annotatedPluginDocuments, "MarkerCode_Seq",
								"Marker (Seq)", "Marker (Seq)",
								limsAB1Fields.getMarker(), cnt);

						/** set note for Document version */
						limsNotes.setNoteToAB1FileName(
								annotatedPluginDocuments,
								"DocumentVersionCode", "Document version",
								"Document version",
								limsAB1Fields.getVersieNummer(), cnt);
						/* set note for AmplicificationStaffCode_FixedValue */
						try {
							limsNotes
									.setNoteToAB1FileName(
											annotatedPluginDocuments,
											"AmplicificationStaffCode_FixedValue_Seq",
											"Ampl-staff (Seq)",
											"Ampl-staff (Seq)",
											limsImporterUtil
													.getPropValues("seqamplicification"),
											cnt);
						} catch (IOException e) {
							e.printStackTrace();
						}

						limsNotes.setNoteDropdownFieldToFileName(
								annotatedPluginDocuments,
								limsNotes.ConsensusSeqPass,
								"ConsensusSeqPass_Code_Seq", "Pass (Seq)",
								"Pass (Seq)", null, cnt);

						limsNotes.setNoteTrueFalseFieldToFileName(
								annotatedPluginDocuments, "CRSCode_CRS",
								"CRS (CRS)", "CRS (CRS)", true, cnt);
					}

					logger.info("Done with adding notes to the document");

					// versionNumber = readVersionNumberValue
					// .readValueFromAnnotatedPluginDocument(
					// annotatedPluginDocument, noteCode,
					// fieldName);
					//
					// if (seq.getName().contains("New Sequence")
					// && versionNumber.equals("0")) {
					//
					// String fileSelected = fcd.loadSelectedFile();
					// if (fileSelected == null) {
					// return;
					// }
					//
					// File file = new File(fileSelected);
					//
					// try {
					// document = PluginUtilities.importDocuments(file,
					// ProgressListener.EMPTY);
					// annotatedPluginDocument = document.iterator()
					// .next();
					// DocumentUtilities.addGeneratedDocument(
					// annotatedPluginDocument, true);
					// } catch (IOException e) {
					// e.printStackTrace();
					// } catch (DocumentImportException e) {
					// e.printStackTrace();
					// }
					//
					// String fileName = fileSelected.substring(fileSelected
					// .indexOf("\\e") + 1);
					//
					// for (int counter = 0; counter < document.size();
					// counter++) {
					//
					// logger.info("-------------------------- S T A R T --------------------------");
					// logger.info("Start Reading data from a excel file.");
					//
					// seq = (SequenceDocument) docs.get(cnt)
					// .getDocument();
					// limsAB1Fields
					// .setFieldValuesFromAB1FileName(fileName);
					//
					// msgList.add(fileSelected);
					//
					// /* set note for Extract-ID */
					// try {
					// limsNotes.setImportNotes(
					// annotatedPluginDocument,
					// "ExtractIdCode", "Extract ID",
					// "Extract-ID",
					// limsAB1Fields.getExtractID());
					// } catch (Exception ex) {
					// ex.printStackTrace();
					// }
					//
					// /* set note for PCR Plaat-ID */
					// try {
					// limsNotes.setImportNotes(
					// annotatedPluginDocument,
					// "PcrPlaatIdCode", "PCR plaat ID",
					// "PCR plaat ID",
					// limsAB1Fields.getPcrPlaatID());
					// } catch (Exception ex) {
					// ex.printStackTrace();
					// }
					//
					// /* set note for Marker */
					// try {
					// limsNotes.setImportNotes(
					// annotatedPluginDocument, "MarkerCode",
					// "Marker", "Marker",
					// limsAB1Fields.getMarker());
					// } catch (Exception ex) {
					// ex.printStackTrace();
					// }
					// /* set note for Marker */
					// try {
					// limsNotes.setImportNotes(
					// annotatedPluginDocument, "VersieCode",
					// "Version number", "Version number",
					// limsAB1Fields.getVersieNummer());
					// } catch (Exception ex) {
					// ex.printStackTrace();
					// }
					// logger.info("Done with adding notes to the document");
					// }
					//
					// }

				}
			} catch (DocumentOperationException e) {
				try {
					throw new Exception();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			logger.info("Total of document(s) updated: " + docs.size());
			logger.info("------------------------- E N D--------------------------------------");
			logger.info("Done with extracting Ab1 file name. ");
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					String filename = "";
					if (seq.getName().contains("ab1")) {
						filename = "AB1";
					} else {
						filename = "FAS";
					}
					Dialogs.showMessageDialog(filename + "-Update: "
							+ Integer.toString(msgList.size())
							+ " documents are update." + "\n"
							+ msgList.toString());
					logger.info(filename
							+ "-Update: Total imported document(s): "
							+ msgList.toString());

					msgList.clear();
				}
			});
		}
	}

	@Override
	public GeneiousActionOptions getActionOptions() {
		return new GeneiousActionOptions("5 Split name").setInPopupMenu(true)
				.setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools, 4.0)
				.setInMainToolbar(true).setInPopupMenu(true)
				.setAvailableToWorkflows(true);
	}

	@Override
	public String getHelp() {
		return null;
	}

	@Override
	public DocumentSelectionSignature[] getSelectionSignatures() {
		return new DocumentSelectionSignature[] { new DocumentSelectionSignature(
				NucleotideSequenceDocument.class, 0, Integer.MAX_VALUE) };
	}

}