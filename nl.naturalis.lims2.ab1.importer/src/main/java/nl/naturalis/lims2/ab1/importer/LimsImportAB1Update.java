/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.naturalis.lims2.utils.LimsAB1Fields;
import nl.naturalis.lims2.utils.LimsFrameProgress;
import nl.naturalis.lims2.utils.LimsImporterUtil;
import nl.naturalis.lims2.utils.LimsNotes;
import nl.naturalis.lims2.utils.LimsReadGeneiousFieldsValues;

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
	private static final Logger logger = LoggerFactory
			.getLogger(LimsImportAB1Update.class);

	private List<String> msgList = new ArrayList<String>();
	private int versienummer = 0;
	private boolean isVersionnumberOne = false;
	LimsFileSelector fcd = new LimsFileSelector();
	LimsFrameProgress limsFrameProgress = new LimsFrameProgress();
	private LimsReadGeneiousFieldsValues ReadGeneiousFieldsValues = new LimsReadGeneiousFieldsValues();
	private AnnotatedPluginDocument documents = null;
	private int version = 0;
	private String documentName = "";
	private String recordDocumentName = "";
	private String extractAb1FastaFileName = "";
	private boolean fastaFileExists = false;
	private boolean fileExists = false;
	private LimsFileSelector fileselector = new LimsFileSelector();
	private File file = null;
	private Boolean extractValue = false;

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

			/* Get Databasename */
			ReadGeneiousFieldsValues.resultDB = ReadGeneiousFieldsValues
					.getServerDatabaseServiceName();
			limsFrameProgress.createProgressBar();
			logger.info("----------------------------S T A R T -------------------------------");
			try {
				docs = DocumentUtilities.getSelectedDocuments();
				documents = docs.iterator().next();

				for (int cnt = 0; cnt < docs.size(); cnt++) {
					seq = (SequenceDocument) docs.get(cnt).getDocument();

					versienummer = 0;
					ReadGeneiousFieldsValues.recordcount = 0;
					fileExists = ReadGeneiousFieldsValues
							.fileNameExistsInGeneiousDatabase(seq.getName());

					if (fileExists && ReadGeneiousFieldsValues.recordcount >= 1) {
						isVersionnumberOne = true;
					} else {
						isVersionnumberOne = docs.get(cnt).toString()
								.contains("DocumentVersionCode_Seq");
					}

					extractValue = ReadGeneiousFieldsValues
							.getValueFromAnnotatedPluginDocument(
									annotatedPluginDocuments[cnt],
									"DocumentNoteUtilities-Extract ID (Seq)",
									"ExtractIDCode_Seq");

					System.out.println(extractValue);

					if (!isVersionnumberOne && !fileExists) {
						versienummer = 1;
					}

					if (fileExists) {

						if (!docs.get(cnt).toString().contains("ab1")) {
							try {
								file = new File(
										fcd.loadFastaFile(seq.getName()));
							} catch (FileNotFoundException e1) {
								e1.printStackTrace();
							}
							// }
							//
							// if (file.getName() != "" && fastaFileExists) {
							extractAb1FastaFileName = null;
							extractAb1FastaFileName = file.getName();

							fastaFileExists = ReadGeneiousFieldsValues
									.checkOfFastaOrAB1Exists(
											extractAb1FastaFileName,
											"plugin_document_xml",
											"//XMLSerialisableRootElement/name");

							versienummer = ReadGeneiousFieldsValues
									.getLastVersion_For_AB1_Fasta(extractAb1FastaFileName);

						} else {
							extractAb1FastaFileName = seq.getName();
							versienummer = ReadGeneiousFieldsValues
									.getLastVersion_For_AB1_Fasta(extractAb1FastaFileName);
						}
					}

					if (seq.getName() != null && seq.getName().contains("_")) {
						logger.info("Start extracting value from file: "
								+ seq.getName());
						msgList.add(seq.getName());

						documentName = (String) docs.get(cnt).getFieldValue(
								"cache_name");

						recordDocumentName = docs.get(cnt).getName();

						if (seq.getName().contains("ab1")) {
							limsAB1Fields.setFieldValuesFromAB1FileName(seq
									.getName());
							if (fileExists && !extractValue) {
								versienummer++;
							}

						} else {
							limsAB1Fields.setFieldValuesFromAB1FileName(file
									.getName());
							if (fastaFileExists && !extractValue) {
								versienummer++;
							}
						}

						if (fastaFileExists && !extractValue) {
							limsAB1Fields.setVersieNummer(versienummer);
						} else if (fileExists && !extractValue) {
							limsAB1Fields.setVersieNummer(versienummer);
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
						if (fileExists && !extractValue) {
							limsNotes.setNoteToAB1FileName(
									annotatedPluginDocuments,
									"DocumentVersionCode_Seq",
									"Document version", "Document version",
									Integer.toString(versienummer), cnt);
						}
						// else {
						// limsNotes.setNoteToAB1FileName(
						// annotatedPluginDocuments,
						// "DocumentVersionCode_Seq",
						// "Document version", "Document version",
						// Integer.toString(versienummer), cnt);
						// }
						/* set note for SequencingStaffCode_FixedValue_Seq */
						try {
							limsNotes.setNoteToAB1FileName(
									annotatedPluginDocuments,
									"SequencingStaffCode_FixedValue_Seq",
									"Seq-staff (Seq)", "Seq-staff (Seq)",
									limsImporterUtil
											.getPropValues("seqsequencestaff"),
									cnt);
						} catch (IOException e) {
							e.printStackTrace();
						}

						limsNotes.setNoteDropdownFieldToFileName(
								annotatedPluginDocuments,
								limsNotes.ConsensusSeqPass,
								"ConsensusSeqPassCode_Seq", "Pass (Seq)",
								"Pass (Seq)", null, cnt);

						limsFrameProgress.showProgress(docs.get(cnt).getName());
					}

					logger.info("Done with adding notes to the document");

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
					limsFrameProgress.hideFrame();
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