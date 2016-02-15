/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.naturalis.lims2.utils.LimsImporterUtil;
import nl.naturalis.lims2.utils.LimsLogger;
import nl.naturalis.lims2.utils.LimsNotes;
import nl.naturalis.lims2.utils.LimsReadGeneiousFieldsValues;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.components.Dialogs;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.documents.PluginDocument;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceDocument;
import com.biomatters.geneious.publicapi.implementations.DefaultAlignmentDocument;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideSequence;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentOperationException;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import com.opencsv.CSVReader;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsReadDataFromBold extends DocumentAction {

	private LimsNotes limsNotes = new LimsNotes();
	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	private LimsBoldFields limsBoldFields = new LimsBoldFields();
	private LimsReadGeneiousFieldsValues readGeneiousFieldsValues = new LimsReadGeneiousFieldsValues();
	private SequenceDocument sequenceDocument;
	private static final Logger logger = LoggerFactory
			.getLogger(LimsImportAB1Update.class);

	/*
	 * String logFileName = limsImporterUtil.getLogPath() + File.separator +
	 * limsImporterUtil.getLogFilename();
	 * 
	 * LimsLogger limsLogger = new LimsLogger(logFileName);
	 */

	private String boldFilePath;
	private String boldFile;
	private String extractIDfileName;
	private final String noteCode = "DocumentNoteUtilities-Registr-nmbr (Samples)";
	private final String fieldName = "RegistrationNumberCode_Samples";
	private List<AnnotatedPluginDocument> docs;
	private LimsFileSelector fcd = new LimsFileSelector();
	private List<String> msgList = new ArrayList<String>();
	private List<String> msgUitvalList = new ArrayList<String>();
	private List<String> verwerkingListCnt = new ArrayList<String>();
	private List<String> verwerkList = new ArrayList<String>();
	private AnnotatedPluginDocument[] documents = null;
	private DefaultAlignmentDocument defaultAlignmentDocument = null;
	private DefaultNucleotideSequence defaultNucleotideSequence = null;
	private Object documentFileName = "";
	private String boldFileSelected = "";
	private boolean result = false;

	public int importCounter;
	private int importTotal;
	private String[] record = null;

	String logFileName = limsImporterUtil.getLogPath() + File.separator
			+ "Bold-Uitvallijst-" + limsImporterUtil.getLogFilename();
	LimsLogger limsLogger = new LimsLogger(logFileName);

	@Override
	public void actionPerformed(
			AnnotatedPluginDocument[] annotatedPluginDocuments) {

		if (DocumentUtilities.getSelectedDocuments().isEmpty()) {
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					Dialogs.showMessageDialog("Select all documents for BOLD");
					return;
				}
			});
		}

		if (!DocumentUtilities.getSelectedDocuments().isEmpty()) {
			msgList.clear();
			boldFileSelected = fcd.loadSelectedFile();
			if (boldFileSelected == null) {
				return;
			}

			documents = annotatedPluginDocuments;
			logger.info("------------------------------S T A R T -----------------------------------");
			logger.info("Start adding Bold metadata to AB1 File(s)");

			try {
				docs = DocumentUtilities.getSelectedDocuments();
				msgUitvalList.add("Filename: " + boldFileSelected + "\n");
				for (int cnt = 0; cnt < docs.size(); cnt++) {
					documentFileName = annotatedPluginDocuments[cnt]
							.getFieldValue("cache_name");

					/* Add sequence name for the dialog screen */
					if (DocumentUtilities.getSelectedDocuments().listIterator()
							.hasNext()) {
						msgList.add(documentFileName + "\n");
					}

					result = false;

					/* Reads Assembly Contig 1 file */
					try {
						if (readGeneiousFieldsValues
								.getCacheNameFromGeneiousDatabase(
										documentFileName,
										"//document/hiddenFields/override_cache_name")
								.equals(documentFileName)) {
							defaultAlignmentDocument = (DefaultAlignmentDocument) docs
									.get(cnt).getDocument();

							logger.info("Selected Contig document: "
									+ defaultAlignmentDocument.getName());
							setExtractIDfileName(defaultAlignmentDocument
									.getName());
							extractIDfileName = getExtractIDFromAB1FileName(defaultAlignmentDocument
									.getName());
							result = true;
						}
					} catch (IOException e2) {
						e2.printStackTrace();
					}

					/* Reads Assembly Contig 1 consensus sequence */
					try {
						if (readGeneiousFieldsValues
								.getCacheNameFromGeneiousDatabase(
										documentFileName,
										"//document/hiddenFields/cache_name")
								.equals(documentFileName)
								&& !documentFileName.toString().contains("ab1")) {

							defaultNucleotideSequence = (DefaultNucleotideSequence) docs
									.get(cnt).getDocument();

							logger.info("Selected Contig consensus sequence document: "
									+ defaultNucleotideSequence.getName());

							setExtractIDfileName(defaultNucleotideSequence
									.getName());
							extractIDfileName = getExtractIDFromAB1FileName(defaultNucleotideSequence
									.getName());
							result = true;
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					/* AB1 file */
					try {
						if (readGeneiousFieldsValues
								.getFileNameFromGeneiousDatabase(
										(String) documentFileName).equals(
										documentFileName)) {

							sequenceDocument = (SequenceDocument) docs.get(cnt)
									.getDocument();
							logger.info("Selected AB1 document: "
									+ sequenceDocument.getName());
							setExtractIDfileName(sequenceDocument.getName());
							extractIDfileName = getExtractIDFromAB1FileName(sequenceDocument
									.getName());
							result = true;

						}
					} catch (IOException e) {
						e.printStackTrace();
					}

					if (result) {
						readDataFromBold(annotatedPluginDocuments[cnt],
								boldFileSelected, cnt);
					}

				}
			} catch (DocumentOperationException e) {
				e.printStackTrace();
			}
			logger.info("Total of document(s) updated: " + docs.size());
			logger.info("------------------------------E N D -----------------------------------");
			logger.info("Done with reading bold file. ");
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					Dialogs.showMessageDialog("Bold: "
							+ Integer.toString(docs.size()) + " out of "
							+ Integer.toString(importTotal)
							+ " documents are imported." + "\n"
							+ msgList.toString());
					logger.info("Bold: Total imported document(s): "
							+ msgList.toString());

					limsLogger.logToFile(logFileName, msgUitvalList.toString());

					msgList.clear();
					msgUitvalList.clear();
					verwerkingListCnt.clear();
					verwerkList.clear();
				}
			});
		}
	}

	@Override
	public GeneiousActionOptions getActionOptions() {
		return new GeneiousActionOptions("4 Bold").setInPopupMenu(true)
				.setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools, 3.0)
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
				PluginDocument.class, 0, Integer.MAX_VALUE) };
	}

	private void readDataFromBold(
			AnnotatedPluginDocument annotatedPluginDocument, String fileName,
			int cnt) {

		String[] headerCOI = null;
		logger.info("CSV Bold file: " + fileName);
		logger.info("Start with adding notes to the document");

		try {
			CSVReader csvReader = new CSVReader(new FileReader(fileName), '\t',
					'\'', 0);

			int counter = 0;
			int cntVerwerkt = 0;

			headerCOI = csvReader.readNext();

			try {
				msgUitvalList
						.add("-----------------------------------------------"
								+ "\n");

				msgUitvalList.add("Bold filename: " + documentFileName + "\n");

				while ((record = csvReader.readNext()) != null) {
					if (record.length == 0) {
						continue;
					}

					/** DocumentNoteUtilities-Registration number */
					/** Get value from "RegistrationnumberCode_Samples" */
					Object fieldValue = readGeneiousFieldsValues
							.readValueFromAnnotatedPluginDocument(
									annotatedPluginDocument, noteCode,
									fieldName);

					/** Match only on registration number */
					if (record[2].equals(fieldValue)) {

						setNotesThatMatchRegistrationNumber(record[1],
								record[9]);
						setNotesToBoldDocumentsRegistration(documents, cnt);

					}

					/** Match only on registration number and Marker */
					if (record[2].equals(fieldValue)
							&& headerCOI[6].equals("COI-5P Seq. Length")) {
						setNotesThatMatchRegistrationNumberAndMarker(record[6],
								record[7], record[8], record[0], record[3],
								record[4],
								limsImporterUtil.getPropValues("bolduri"));
						setNotesToBoldDocumentsRegistrationMarker(documents,
								cnt);
					}

					cntVerwerkt++;
					verwerkingListCnt.add(Integer.toString(cntVerwerkt));
					verwerkList.add(record[2]);

					if (!verwerkList.contains(record[5])) {
						msgUitvalList.add("Catalognumber: " + record[5] + "\n");
					}

					counter++;

				} // end While
				importTotal = counter;
				counter = importTotal - verwerkingListCnt.size();
				msgUitvalList.add("Total records: " + Integer.toString(counter)
						+ "\n");

			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				csvReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getBoldFilePath() {
		return boldFilePath;
	}

	public void setBoldFilePath(String boldFilePath) {
		this.boldFilePath = boldFilePath;
	}

	public String getBoldFile() {
		return boldFile;
	}

	public void setBoldFile(String boldFile) {
		this.boldFile = boldFile;
	}

	public String getExtractIDfileName() {
		return extractIDfileName;
	}

	public void setExtractIDfileName(String extractIDfileName) {
		this.extractIDfileName = extractIDfileName;
	}

	/**
	 * Extract the ID from the filename
	 * 
	 * @param annotatedPluginDocuments
	 *            set the param
	 * @return
	 */
	private String getExtractIDFromAB1FileName(String fileName) {
		/* for example: e4010125015_Sil_tri_MJ243_COI-A01_M13F_A01_008.ab1 */
		String[] underscore = StringUtils.split(fileName, "_");
		return underscore[0];
	}

	/* Set value to Notes */
	private void setNotesToBoldDocumentsRegistrationMarker(
			AnnotatedPluginDocument[] annotatedPluginDocuments, int cnt) {
		/** set note for TraceFile Presence */
		limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
				"TraceFilePresenceCode_Bold", "N traces (Bold)",
				"N traces (Bold)", limsBoldFields.getTraceFilePresence(), cnt);

		/** set note for Nucleotide Length */
		limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
				"NucleotideLengthCode_Bold", "Nucleotide length (Bold)",
				"Nucleotide length (Bold)",
				limsBoldFields.getNucleotideLength(), cnt);

		/** set note for GenBankID */
		limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
				"GenBankIDCode_Bold", "GenBank ID (Bold)", "GenBank ID (Bold)",
				limsBoldFields.getGenBankID(), cnt);

		/** set note for BoldProjectID */
		limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
				"BOLDprojIDCode_Bold", "BOLD proj-ID (Bold)",
				"BOLD proj-ID (Bold)", limsBoldFields.getBoldProjectID(), cnt);

		/** set note for FieldID */
		limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
				"FieldIDCode_Bold", "Field ID (Bold)", "Field ID (Bold)",
				limsBoldFields.getFieldID(), cnt);

		/** set note for BOLD BIN Code */
		limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
				"BOLDBINCode_Bold", "BOLD BIN (Bold)", "BOLD BIN (Bold)",
				limsBoldFields.getBoldBIN(), cnt);

		/** set note for BOLD URI */
		limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
				"BOLDURICode_FixedValue_Bold", "BOLD URI (Bold)",
				"BOLD URI (Bold)", limsBoldFields.getBoldURI(), cnt);

		/** set note for GenBank URI */
		try {
			limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
					"GenBankURICode_FixedValue", "GenBank URI (Bold)",
					"GenBank URI (Bold)",
					limsImporterUtil.getPropValues("boldurigenbank")
							+ limsBoldFields.getGenBankID(), cnt);
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.info("Done with adding notes to the document");
	}

	/* Set value to Notes */
	private void setNotesToBoldDocumentsRegistration(
			AnnotatedPluginDocument[] annotatedPluginDocuments, int cnt) {
		/** set note for BOLD-ID */
		limsNotes.setNoteToAB1FileName(annotatedPluginDocuments, "BOLDIDCode",
				"BOLD ID (Bold)", "BOLD ID (Bold)", limsBoldFields.getBoldID(),
				cnt);

		/** set note for Number of Images */
		limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
				"NumberOfImagesCode_Bold", "N images (Bold)",
				"N images (Bold)", limsBoldFields.getNumberOfImagesBold(), cnt);
		logger.info("Done with adding notes to the document");
	}

	/* Set value to variable */
	private void setNotesThatMatchRegistrationNumber(String boldID,
			String numberOfImagesBold) {

		logger.info("Match Bold record only on registrationnumber.");

		limsBoldFields.setBoldID(boldID);
		limsBoldFields.setNumberOfImagesBold(numberOfImagesBold);

		logger.info("Bold-ID: " + limsBoldFields.getBoldID());
		logger.info("Number of Images Bold: "
				+ limsBoldFields.getNumberOfImagesBold());

	}

	/* Set value to variable */
	private void setNotesThatMatchRegistrationNumberAndMarker(
			String nucleotideLength, String tracebestandPresence,
			String genBankID, String boldProjectID, String fieldID,
			String boldBIN, String boldURI) {

		logger.info("Match Bold record on registrationnumber and marker.");

		limsBoldFields.setNucleotideLength(nucleotideLength);
		limsBoldFields.setTraceFilePresence(tracebestandPresence);
		limsBoldFields.setGenBankID(genBankID);
		limsBoldFields.setBoldProjectID(boldProjectID);
		limsBoldFields.setFieldID(fieldID);
		limsBoldFields.setBoldBIN(boldBIN);
		limsBoldFields.setBoldURI(boldURI);

		logger.info("Nucleotide length: "
				+ limsBoldFields.getNucleotideLength());
		logger.info("TraceFile Presence: "
				+ limsBoldFields.getTraceFilePresence());
		logger.info("GenBankID: " + limsBoldFields.getGenBankID());
		logger.info("BoldProjectID: " + limsBoldFields.getBoldProjectID());
		logger.info("FieldID: " + limsBoldFields.getFieldID());
		logger.info("BoldBIN: " + limsBoldFields.getBoldBIN());
		logger.info("BoldURI: " + limsBoldFields.getBoldURI());

	}

}
