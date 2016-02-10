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
	private final String noteCode = "DocumentNoteUtilities-Registrationnumber (Samples)";
	private final String fieldName = "RegistrationnumberCode_Samples";
	private List<AnnotatedPluginDocument> docs;
	private LimsFileSelector fcd = new LimsFileSelector();
	private List<String> msgList = new ArrayList<String>();
	private List<String> msgUitvalList = new ArrayList<String>();
	private List<String> verwerkingListCnt = new ArrayList<String>();
	private List<String> verwerkList = new ArrayList<String>();
	private AnnotatedPluginDocument[] documents = null;
	private static final String contigFile = "DefaultAlignmentDocument";
	private boolean isContig = false;
	private DefaultAlignmentDocument alignmentDocument = null;

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
			String boldFileSelected = fcd.loadSelectedFile();
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

					if (docs.toString().contains(contigFile)) {
						alignmentDocument = (DefaultAlignmentDocument) docs
								.get(cnt).getDocument();
						isContig = true;
						logger.info("Selected document: "
								+ alignmentDocument.getName());
						setExtractIDfileName(alignmentDocument.getName());
						extractIDfileName = getExtractIDFromAB1FileName(alignmentDocument
								.getName());

						msgList.add(alignmentDocument.getName());
					} else {
						isContig = false;
						sequenceDocument = (SequenceDocument) docs.get(cnt)
								.getDocument();
						logger.info("Selected document: "
								+ sequenceDocument.getName());
						setExtractIDfileName(sequenceDocument.getName());
						extractIDfileName = getExtractIDFromAB1FileName(sequenceDocument
								.getName());

						msgList.add(sequenceDocument.getName());
					}

					readDataFromBold(annotatedPluginDocuments[cnt],
							boldFileSelected, cnt);

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
				if (!isContig) {
					msgUitvalList.add("Bold filename: "
							+ sequenceDocument.getName() + "\n");
				} else {
					msgUitvalList.add("Bold filename: "
							+ alignmentDocument.getName() + "\n");
				}

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
								record[7], record[8]);
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

	private void setNotesToBoldDocumentsRegistrationMarker(
			AnnotatedPluginDocument[] annotatedPluginDocuments, int cnt) {
		/** set note for TraceFile Presence */
		limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
				"TraceFilePresenceCode_Bold", "Trace file presence (Bold)",
				"Trace file presence (Bold)",
				limsBoldFields.getTraceFilePresence(), cnt);

		/** set note for Nucleotide Length */
		limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
				"NucleotideLengthCode_Bold", "Nucleotide length (Bold)",
				"Nucleotide length (Bold)",
				limsBoldFields.getNucleotideLength(), cnt);

		/** set note for GenBankID */
		limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
				"GenBankIDCode_Bold", "GenBank ID (Bold)", "GenBank ID (Bold)",
				limsBoldFields.getGenBankID(), cnt);
		logger.info("Done with adding notes to the document");
	}

	private void setNotesToBoldDocumentsRegistration(
			AnnotatedPluginDocument[] annotatedPluginDocuments, int cnt) {
		/** set note for BOLD-ID */
		limsNotes.setNoteToAB1FileName(annotatedPluginDocuments, "BOLDIDCode",
				"BOLD ID (Bold)", "BOLD ID (Bold)", limsBoldFields.getBoldID(),
				cnt);

		/** set note for BOLD-ID */
		limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
				"NumberOfImagesCode_Bold", "Number of images (Bold)",
				"Number of images (Bold)",
				limsBoldFields.getNumberOfImagesBold(), cnt);
		logger.info("Done with adding notes to the document");
	}

	private void setNotesThatMatchRegistrationNumber(String boldID,
			String numberOfImagesBold) {

		logger.info("Match Bold record only on registrationnumber.");

		limsBoldFields.setBoldID(boldID);
		limsBoldFields.setNumberOfImagesBold(numberOfImagesBold);

		logger.info("Bold-ID: " + limsBoldFields.getBoldID());
		logger.info("Number of Images Bold: "
				+ limsBoldFields.getNumberOfImagesBold());

	}

	private void setNotesThatMatchRegistrationNumberAndMarker(
			String nucleotideLength, String tracebestandPresence,
			String genBankID) {

		logger.info("Match Bold record on registrationnumber and marker.");

		limsBoldFields.setNucleotideLength(nucleotideLength);
		limsBoldFields.setTraceFilePresence(tracebestandPresence);
		limsBoldFields.setGenBankID(genBankID);

		logger.info("Nucleotide length: "
				+ limsBoldFields.getNucleotideLength());
		logger.info("TraceFile Presence: "
				+ limsBoldFields.getTraceFilePresence());
		logger.info("GenBankID: " + limsBoldFields.getGenBankID());

	}

}
